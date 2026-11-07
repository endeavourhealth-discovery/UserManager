package org.endeavourhealth.usermanager.api.logic;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.util.Json;
import org.endeavourhealth.common.config.ConfigManager;
import org.endeavourhealth.common.security.keycloak.client.KeycloakAdminClient;
import org.endeavourhealth.core.database.dal.datasharingmanager.models.JsonProject;
import org.endeavourhealth.core.database.dal.usermanager.UserProjectDalI;
import org.endeavourhealth.core.database.dal.usermanager.caching.*;
import org.endeavourhealth.core.database.dal.usermanager.models.*;
import org.endeavourhealth.core.database.rdbms.datasharingmanager.models.OrganisationEntity;
import org.endeavourhealth.core.database.rdbms.datasharingmanager.models.RegionEntity;
import org.endeavourhealth.core.database.rdbms.usermanager.models.ApplicationPolicyEntity;
import org.endeavourhealth.core.database.rdbms.usermanager.models.UserApplicationPolicyEntity;
import org.endeavourhealth.core.database.rdbms.usermanager.models.UserProjectEntity;
import org.endeavourhealth.core.database.rdbms.usermanager.models.UserRegionEntity;
import org.endeavourhealth.uiaudit.dal.UIAuditJDBCDAL;
import org.endeavourhealth.uiaudit.enums.AuditAction;
import org.endeavourhealth.uiaudit.enums.ItemType;
import org.endeavourhealth.usermanager.api.DAL.UserProjectDAL;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.NotAllowedException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.*;
import java.util.stream.Collectors;

import static org.endeavourhealth.common.security.SecurityUtils.getCurrentUserId;
import static org.endeavourhealth.coreui.endpoints.AbstractEndpoint.clearLogbackMarkers;

public class UserLogic {
    private static final Logger LOG = LoggerFactory.getLogger(UserLogic.class);
    private static UserProjectDalI userProjectRepository;
    public Response getUsers(String organisationId, String searchData, boolean machineUsers) throws Exception {


        List<String> usersAtOrg = new ArrayList<>();

        if (organisationId != null) {
            List<String> userIDsInOrg = new ArrayList<>();
            List<UserProjectEntity> userRoleEntities = new UserProjectDAL().getUsersAtOrganisation(organisationId);
            for (UserProjectEntity userRole : userRoleEntities) {
                if (userRole.getOrganisationId().equals(organisationId)) {
                    userIDsInOrg.add(userRole.getUserId());
                }
            }
            usersAtOrg = userRoleEntities.stream()
                    .map(UserProjectEntity::getUserId)
                    .collect(Collectors.toList());
        }

        List<JsonUser> userList = new ArrayList<>();
        List<UserRepresentation> users;

        KeycloakAdminClient keycloakClient = new KeycloakAdminClient();

        if (searchData == null || searchData.equals("")) {
            if (machineUsers) {
                users = keycloakClient.realms().users().getUsers(ConfigManager.getConfiguration("machine_user_realm"), "", 0, 1000);
            } else {
                users = keycloakClient.realms().users().getUsers("", 0, 1000);
            }
        } else {
            if (machineUsers) {
                users = keycloakClient.realms().users().getUsers(ConfigManager.getConfiguration("machine_user_realm"), searchData, 0, 1000);
            } else {
                users = keycloakClient.realms().users().getUsers(searchData, 0, 1000);
            }
        }
        //Add as Json
        for (UserRepresentation user : users) {
            if (searchData != null || usersAtOrg.contains(user.getId()) || machineUsers) {
                userList.add(new JsonUser(user));
            }
        }

        clearLogbackMarkers();

        return Response
                .ok()
                .entity(userList)
                .build();
    }

    public Response saveUser(JsonUser user, String editMode, String userRoleId, SecurityContext sc) throws Exception {

        LOG.trace("saving user " + user.getUsername());

        boolean editModeb = editMode.equalsIgnoreCase("1") ? true : false;

        UserRepresentation oldUser = null;

        //If editing and the user IDs don't match, then throw an error if user does not have correct role
        //This prevents security vunerability when an authenticated user could execute API outside of app without role
        if (editModeb) {
            //get current authenticated user id and check for user editing role (currently eds_superuser for v1.1)  //TODO: v2 roles
            /*UUID currentUserUuid = getCurrentUserId(sc);
            boolean superUser = hasRole(sc, "eds_superuser");

            //can only update other users with eds_superuser role
            if (!currentUserUuid.toString().equalsIgnoreCase(user.getUuid().toString()) && !superUser)
            {
                throw new NotAllowedException("Save User not allowed with UserId mismatch");
            }*/
        }

        //Set the basic user profile info
        UserRepresentation userRep = new UserRepresentation();
        userRep.setEnabled(true);
        if (!editModeb) {
            userRep.setUsername(user.getUsername());
        }  //cannot edit username, only add
        userRep.setLastName(user.getSurname());
        userRep.setFirstName(user.getForename());
        userRep.setEmail(user.getEmail());

        //Set the user attributes such as mobile and photo and v1 organisation-id
        userRep.singleAttribute("Mobile", user.getMobile());
        userRep.singleAttribute("Photo", user.getPhoto());
        //Preserve v1 organisation-id as the keycloak API has a bug which deletes all attributes
        String defaultOrgId = user.getDefaultOrgId();
        if (defaultOrgId != null && defaultOrgId.trim() != "") {
            userRep.singleAttribute("organisation-id", defaultOrgId);
        }

        //Create the keycloak admin client and file the user
        KeycloakAdminClient keycloakClient = new KeycloakAdminClient();

        String userId = "";
        //This is the newly created userId
        if (!editModeb) {
            try {
                userRep = keycloakClient.realms().users().postUser(userRep);

                keycloakClient.realms().users().putUserUpdatePasswordEmail(userRep);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("user failed after posting to keycloak\n" + e.getMessage());
                // TODO REMOVE ONCE DEV IS COMPLETE OR FIND OUT WHY THIS DOESN'T WORK LOCALLY
            }

        } else {
            //This is the existing userId, so we set for update
            userId = user.getUuid().toString();

            LOG.trace("Retrieving old version of user " + userId);
            oldUser = UserCache.getUserDetails(userId);
            LOG.trace("Got old version of user " + (oldUser != null));
            userRep.setId(userId);
            try {
                userRep = keycloakClient.realms().users().putUser(userRep);
            } catch (Exception e) {
                // TODO REMOVE ONCE DEV IS COMPLETE OR FIND OUT WHY THIS DOESN'T WORK LOCALLY
            }
        }
        userId = userRep.getId();
        if (userId == null) {
            userId = UUID.randomUUID().toString();
            userRep.setId(userId);
        }
        user.setUuid(UUID.fromString(userId));  //new uuid to return to client
        //Now, file the new temporary password if it is not blank (edit mode password may be blank)
        if (!editModeb || user.getPassword().trim() != "") {
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(user.getPassword());
            credential.setTemporary(true);
            try {
                keycloakClient.realms().users().setUserPassword(userRep.getId(), credential);
            } catch (Exception e) {
                // TODO REMOVE ONCE DEV IS COMPLETE OR FIND OUT WHY THIS DOESN'T WORK LOCALLY
            }
        }

        //Blank out password for audit object
        user.setPassword("*********");

        // populate the username for audit purposes
        userRep.setUsername(user.getUsername());
        if (editModeb) {
            auditUserEdit(userRep, oldUser, userRoleId);
        } else {
            auditUserAdd(userRep, userRoleId);
        }

        UserCache.clearUserCache(userId);

        clearLogbackMarkers();

        return Response
                .ok()
                .entity(user)
                .build();
    }

    public Response saveUsersProjects(List<JsonUser> users, String userRoleId, SecurityContext sc) throws Exception {

        for (JsonUser user : users) {
            saveProjects(user.getUserProjects(), userRoleId);
        }
        return Response
                .ok()
                .entity(users)
                .build();
    }

    public Response saveProjects(List<JsonUserProject> userProjects, String userRoleId) throws Exception {

        if (userProjects.size() > 0) {
            for (JsonUserProject project : userProjects) {
                if (project.getId() == null && project.isDeleted()) {
                    continue;  // role was never saved in the DB so no need to add it
                }

                if (project.getId() == null) {
                    project.setId(UUID.randomUUID().toString());
                }
                new UserProjectDAL().saveUserProject(project, userRoleId);
            }
        }

        return Response
                .ok()
                .build();
    }

    public Response deleteUser(String userId, String userRoleId) throws Exception {

        UserRepresentation deletedUser = UserCache.getUserDetails(userId);
        //Create the keycloak admin client and delete the user
        KeycloakAdminClient keycloakClient = new KeycloakAdminClient();
        boolean success = keycloakClient.realms().users().deleteUser(userId);

        auditUserDelete(deletedUser, userRoleId);

        UserCache.clearUserCache(userId);

        return Response
                .ok()
                .entity(success)
                .build();
    }

    public Response getUser(SecurityContext sc, String userId, boolean machineUser) throws Exception {

        UUID currentUserUuid = getCurrentUserId(sc);
        if (!currentUserUuid.toString().equalsIgnoreCase(userId)) {
            throw new NotAllowedException("Get User not allowed with UserId mismatch");
        }

        //First up, get the user account representation
        KeycloakAdminClient keycloakClient = new KeycloakAdminClient();
        UserRepresentation userRep = null;
        if (machineUser) {
            userRep = keycloakClient.realms().users().getUser(ConfigManager.getConfiguration("machine_user_realm"), userId);
        } else {
            userRep = keycloakClient.realms().users().getUser(userId);
        }

        JsonUser user = new JsonUser(userRep);

        return Response
                .ok()
                .entity(user)
                .build();
    }

    private void auditUserEdit(UserRepresentation newUser, UserRepresentation oldUser, String userRoleId) throws Exception {

        //SD-518 it fails to retrieve the old version of users if they're in the machine realm, so
        //as last attempt to get something working, I'm changing this to just let it through if the old version wasn't retrieved
        JsonNode beforeJson = null;
        if (oldUser != null) {
            beforeJson = generateUserAuditJson(oldUser);
        }
        //JsonNode beforeJson = generateUserAuditJson(oldUser);

        JsonNode afterJson = generateUserAuditJson(newUser);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.createObjectNode();

        ((ObjectNode) rootNode).put("title", "User edited");

        if (afterJson != null) {
            ((ObjectNode) rootNode).set("after", afterJson);
        }

        if (beforeJson != null) {
            ((ObjectNode) rootNode).set("before", beforeJson);
        }

        new UIAuditJDBCDAL().addToAuditTrail(userRoleId,
                AuditAction.EDIT,
                ItemType.USER, null, null, prettyPrintJsonString(rootNode));

    }

    private void auditUserAdd(UserRepresentation newUser, String userRoleId) throws Exception {

        JsonNode afterJson = generateUserAuditJson(newUser);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.createObjectNode();

        ((ObjectNode) rootNode).put("title", "User added");

        if (afterJson != null) {
            ((ObjectNode) rootNode).set("after", afterJson);
        }

        new UIAuditJDBCDAL().addToAuditTrail(userRoleId,
                AuditAction.ADD,
                ItemType.USER, null, null, prettyPrintJsonString(rootNode));

    }

    private void auditUserDelete(UserRepresentation deletedUser, String userRoleId) throws Exception {

        JsonNode afterJson = generateUserAuditJson(deletedUser);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.createObjectNode();

        ((ObjectNode) rootNode).put("title", "User deleted");

        if (afterJson != null) {
            ((ObjectNode) rootNode).set("after", afterJson);
        }

        new UIAuditJDBCDAL().addToAuditTrail(userRoleId,
                AuditAction.DELETE,
                ItemType.USER, null, null, prettyPrintJsonString(rootNode));

    }

    private void auditUserPasswordSend(UserRepresentation userRep, String userRoleId) throws Exception {

        JsonNode afterJson = generateUserAuditJson(userRep);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.createObjectNode();

        ((ObjectNode) rootNode).put("title", "User password reset email sent");

        if (afterJson != null) {
            ((ObjectNode) rootNode).set("after", afterJson);
        }

        new UIAuditJDBCDAL().addToAuditTrail(userRoleId,
                AuditAction.ADD,
                ItemType.USER_PASSWORD_EMAIL, null, null, prettyPrintJsonString(rootNode));

    }

    private JsonNode generateUserAuditJson(UserRepresentation user) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode auditJson = mapper.createObjectNode();

        ((ObjectNode) auditJson).put("id", user.getId());
        ((ObjectNode) auditJson).put("username", user.getUsername());
        ((ObjectNode) auditJson).put("forename", user.getFirstName());
        ((ObjectNode) auditJson).put("surname", user.getLastName());
        ((ObjectNode) auditJson).put("email", user.getEmail());

        if (user.getAttributes() != null) {
            Map<String, List<String>> userAttributes = user.getAttributes();

            List<String> l = userAttributes.get("Photo");
            if (l != null && !l.isEmpty()) {
                ((ObjectNode) auditJson).put("photo", l.get(0));
            }

            l = userAttributes.get("Mobile");
            if (l != null && !l.isEmpty()) {
                ((ObjectNode) auditJson).put("mobile", l.get(0));
            }
        }

        return auditJson;
    }

    public String prettyPrintJsonString(JsonNode jsonNode) throws Exception {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Object json = mapper.readValue(jsonNode.toString(), Object.class);
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
        } catch (Exception e) {
            throw new Exception("Converting Json to String failed : " + e.getMessage());
        }
    }

    public Response sendUserPasswordEmail(SecurityContext sc, String userId, String userProjectId) throws Exception {

        UserRepresentation userRep = UserCache.getUserDetails(userId);


        KeycloakAdminClient keycloakClient = new KeycloakAdminClient();
        keycloakClient.realms().users().putUserUpdatePasswordEmail(userRep);

        auditUserPasswordSend(userRep, userProjectId);

        return Response
                .ok()
                .build();
    }

    public Response getUserProfile(String userId) throws Exception {
        UserRepresentation userDetails = UserCache.getUserDetails(userId);
        if (userDetails == null) {
            System.out.println("user details could not be obtained");
        }

        UserApplicationPolicyEntity userApplicationPolicyEntity = UserCache.getUserApplicationPolicy(userId);
        ApplicationPolicyEntity userAppPolicyEntity = ApplicationPolicyCache.getApplicationPolicyDetails(userApplicationPolicyEntity.getApplicationPolicyId());
        List<UserProjectEntity> projectEntities = userProjectRepository.getUserProjectEntities(userId);
        JsonUserProfile userProfile = new JsonUserProfile();
        userProfile.setUuid(userDetails.getId());
        userProfile.setUsername(userDetails.getUsername());
        userProfile.setForename(userDetails.getFirstName());
        userProfile.setSurname(userDetails.getLastName());
        userProfile.setEmail(userDetails.getEmail());
        UserRegionEntity userRegion = UserCache.getUserRegion(userId);
        if (userRegion != null) {
            RegionEntity region = RegionCache.getRegionDetails(userRegion.getRegionId());
            if (region != null) {
                userProfile.setRegion(region);
            }
        }

        Map<String, List<String>> userAttributes = userDetails.getAttributes();
        if (userAttributes != null) {
            Iterator var3 = userAttributes.keySet().iterator();

            while(var3.hasNext()) {
                String attributeKey = (String)var3.next();
                Object obj;
                if (attributeKey.equalsIgnoreCase("mobile")) {
                    obj = userAttributes.get(attributeKey);
                    userProfile.setMobile(obj.toString().substring(1, obj.toString().length() - 1));
                } else if (attributeKey.equalsIgnoreCase("photo")) {
                    obj = userAttributes.get(attributeKey);
                    userProfile.setPhoto(obj.toString().substring(1, obj.toString().length() - 1));
                }
            }
        }

        List<JsonUserOrganisationProject> organisationProjects = new ArrayList();
        Iterator var18 = projectEntities.iterator();

        while(var18.hasNext()) {
            UserProjectEntity profile = (UserProjectEntity)var18.next();
            JsonUserOrganisationProject orgProject = (JsonUserOrganisationProject)organisationProjects.stream().filter((app) -> {
                return app.getOrganisation().getUuid().equals(profile.getOrganisationId());
            }).findFirst().orElse(new JsonUserOrganisationProject());
            if (orgProject.getOrganisation() == null) {
                OrganisationEntity organisation = OrganisationCache.getOrganisationDetails(profile.getOrganisationId());
                orgProject.setOrganisation(organisation);
                organisationProjects.add(orgProject);
            }

            JsonProject project = ProjectCache.getJsonProjectDetails(profile.getProjectId());
            ApplicationPolicyEntity projectAppPolicyEntity = ApplicationPolicyCache.getApplicationPolicyDetails(project.getApplicationPolicy());
            List<JsonApplicationPolicyAttribute> projectPolicyAttributes = this.processProjectPolicyAttributes(userAppPolicyEntity.getId(), projectAppPolicyEntity.getId());
            project.setApplicationPolicyAttributes(projectPolicyAttributes);
            orgProject.addProject(project);
        }

        userProfile.setOrganisationProjects(organisationProjects);
        return Response.ok().entity(userProfile).build();
    }
    private List<JsonApplicationPolicyAttribute> processProjectPolicyAttributes(String userPolicyId, String projectPolicyId) throws Exception {
        List<JsonApplicationPolicyAttribute> mergedAttributes = new ArrayList();
        List<JsonApplicationPolicyAttribute> projectPolicyAttributes = ApplicationPolicyCache.getApplicationPolicyAttributes(projectPolicyId);
        if (userPolicyId.equals(projectPolicyId)) {
            return projectPolicyAttributes;
        } else {
            List<JsonApplicationPolicyAttribute> userPolicyAttributes = ApplicationPolicyCache.getApplicationPolicyAttributes(userPolicyId);
            Iterator var6 = projectPolicyAttributes.iterator();

            while(var6.hasNext()) {
                JsonApplicationPolicyAttribute attribute = (JsonApplicationPolicyAttribute)var6.next();
                if (userPolicyAttributes.stream().filter((a) -> {
                    return a.getApplicationAccessProfileId().equals(attribute.getApplicationAccessProfileId());
                }).findFirst().isPresent()) {
                    mergedAttributes.add(attribute);
                }
            }

            return mergedAttributes;
        }
    }
}
