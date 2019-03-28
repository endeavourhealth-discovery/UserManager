package org.endeavourhealth.usermanager.api.logic;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.endeavourhealth.common.config.ConfigManager;
import org.endeavourhealth.common.security.keycloak.client.KeycloakAdminClient;
import org.endeavourhealth.common.security.usermanagermodel.models.DAL.SecurityAuditDAL;
import org.endeavourhealth.common.security.usermanagermodel.models.caching.UserCache;
import org.endeavourhealth.common.security.usermanagermodel.models.database.UserProjectEntity;
import org.endeavourhealth.common.security.usermanagermodel.models.enums.AuditAction;
import org.endeavourhealth.common.security.usermanagermodel.models.enums.ItemType;
import org.endeavourhealth.common.security.usermanagermodel.models.json.JsonUser;
import org.endeavourhealth.common.security.usermanagermodel.models.json.JsonUserProject;
import org.endeavourhealth.usermanager.api.DAL.UserProjectDAL;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import javax.ws.rs.NotAllowedException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.endeavourhealth.common.security.SecurityUtils.getCurrentUserId;
import static org.endeavourhealth.common.security.SecurityUtils.hasRole;
import static org.endeavourhealth.coreui.endpoints.AbstractEndpoint.clearLogbackMarkers;

public class UserLogic {

    public Response getUsers(String organisationId, String searchData, boolean machineUsers) throws Exception {


        List<String> usersAtOrg = new ArrayList<>();

        if (organisationId != null) {

            List<UserProjectEntity> userRoleEntities = new UserProjectDAL().getUsersAtOrganisation(organisationId);
            usersAtOrg = userRoleEntities.stream()
                    .map(UserProjectEntity::getUserId)
                    .collect(Collectors.toList());
        }

        List<JsonUser> userList = new ArrayList<>();
        List<UserRepresentation> users;

        KeycloakAdminClient keycloakClient = new KeycloakAdminClient();

        if (searchData == null) {
            if (machineUsers) {
                users = keycloakClient.realms().users().getUsers(ConfigManager.getConfiguration("machine_user_realm"), "", 0, 100);
            } else {
                users = keycloakClient.realms().users().getUsers("", 0, 100);
            }
        } else {
            if (machineUsers) {
                users = keycloakClient.realms().users().getUsers(ConfigManager.getConfiguration("machine_user_realm"), searchData, 0, 100);
            } else {
                users = keycloakClient.realms().users().getUsers(searchData, 0, 100);
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

        boolean editModeb = editMode.equalsIgnoreCase("1") ? true:false;

        UserRepresentation oldUser = null;

        //If editing and the user IDs don't match, then throw an error if user does not have correct role
        //This prevents security vunerability when an authenticated user could execute API outside of app without role
        if (editModeb){
            //get current authenticated user id and check for user editing role (currently eds_superuser for v1.1)  //TODO: v2 roles
            UUID currentUserUuid = getCurrentUserId(sc);
            boolean superUser = hasRole(sc, "eds_superuser");

            //can only update other users with eds_superuser role
            if (!currentUserUuid.toString().equalsIgnoreCase(user.getUuid().toString()) && !superUser)
            {
                throw new NotAllowedException("Save User not allowed with UserId mismatch");
            }
        }

        //Set the basic user profile info
        UserRepresentation userRep = new UserRepresentation();
        userRep.setEnabled(true);
        if (!editModeb) {userRep.setUsername(user.getUsername());}  //cannot edit username, only add
        userRep.setLastName(user.getSurname());
        userRep.setFirstName(user.getForename());
        userRep.setEmail(user.getEmail());

        //Set the user attributes such as mobile and photo and v1 organisation-id
        userRep.singleAttribute("Mobile", user.getMobile());
        userRep.singleAttribute("Photo", user.getPhoto());
        //Preserve v1 organisation-id as the keycloak API has a bug which deletes all attributes
        String defaultOrgId = user.getDefaultOrgId();
        if (defaultOrgId != null && defaultOrgId.trim()!="") {
            userRep.singleAttribute("organisation-id", defaultOrgId);
        }

        //Create the keycloak admin client and file the user
        KeycloakAdminClient keycloakClient = new KeycloakAdminClient();

        String userId;
        if (!editModeb) {
            try {
                userRep = keycloakClient.realms().users().postUser(userRep);
            }
            catch (Exception e) {
                // TODO REMOVE ONCE DEV IS COMPLETE OR FIND OUT WHY THIS DOESN'T WORK LOCALLY
            }
            //This is the newly created userId
            userId = userRep.getId();
            if (userId == null) {
                userId = UUID.randomUUID().toString();
            }
            user.setUuid(UUID.fromString(userId));  //new uuid to return to client
        } else {
            //This is the existing userId, so we set for update
            userId = user.getUuid().toString();

            oldUser = UserCache.getUserDetails(userId);
            userRep.setId(userId);
            try {
                userRep = keycloakClient.realms().users().putUser(userRep);
            } catch (Exception e) {
                // TODO REMOVE ONCE DEV IS COMPLETE OR FIND OUT WHY THIS DOESN'T WORK LOCALLY
            }
        }

        //Now, file the new temporary password if it is not blank (edit mode password may be blank)
        if (!editModeb || user.getPassword().trim()!=""){
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(user.getPassword());
            credential.setTemporary(true);
            try {
                keycloakClient.realms().users().setUserPassword(userId, credential);
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

        clearLogbackMarkers();

        return Response
                .ok()
                .entity(user)
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

        return Response
                .ok()
                .entity(success)
                .build();
    }

    public Response getUser(SecurityContext sc, String userId, boolean machineUser) throws Exception {

        UUID currentUserUuid = getCurrentUserId(sc);
        if (!currentUserUuid.toString().equalsIgnoreCase(userId) )
        {
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

        JsonNode beforeJson = generateUserAuditJson(oldUser);
        JsonNode afterJson = generateUserAuditJson(newUser);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.createObjectNode();

        ((ObjectNode)rootNode).put("title", "User edited");

        if (afterJson != null) {
            ((ObjectNode) rootNode).set("after", afterJson);
        }

        if (beforeJson != null) {
            ((ObjectNode) rootNode).set("before", beforeJson);
        }

        new SecurityAuditDAL().addToAuditTrail(userRoleId,
                AuditAction.EDIT,
                ItemType.USER, null, null, prettyPrintJsonString(rootNode));

    }

    private void auditUserAdd(UserRepresentation newUser, String userRoleId) throws Exception {

        JsonNode afterJson = generateUserAuditJson(newUser);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.createObjectNode();

        ((ObjectNode)rootNode).put("title", "User added");

        if (afterJson != null) {
            ((ObjectNode) rootNode).set("after", afterJson);
        }

        new SecurityAuditDAL().addToAuditTrail(userRoleId,
                AuditAction.ADD,
                ItemType.USER, null, null, prettyPrintJsonString(rootNode));

    }

    private void auditUserDelete(UserRepresentation deletedUser, String userRoleId) throws Exception {

        JsonNode afterJson = generateUserAuditJson(deletedUser);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.createObjectNode();

        ((ObjectNode)rootNode).put("title", "User deleted");

        if (afterJson != null) {
            ((ObjectNode) rootNode).set("after", afterJson);
        }

        new SecurityAuditDAL().addToAuditTrail(userRoleId,
                AuditAction.DELETE,
                ItemType.USER, null, null, prettyPrintJsonString(rootNode));

    }

    private JsonNode generateUserAuditJson(UserRepresentation user) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode auditJson = mapper.createObjectNode();

        ((ObjectNode)auditJson).put("id", user.getId());
        ((ObjectNode)auditJson).put("username", user.getUsername());
        ((ObjectNode)auditJson).put("forename", user.getFirstName());
        ((ObjectNode)auditJson).put("surname", user.getLastName());
        ((ObjectNode)auditJson).put("email", user.getEmail());

        if (user.getAttributes() != null) {
            Map<String, List<String>> userAttributes = user.getAttributes();
            if (userAttributes.get("Photo") != null) {
                ((ObjectNode) auditJson).put("photo", userAttributes.get("Photo").get(0));
            }
            if (userAttributes.get("Mobile") != null) {
                ((ObjectNode) auditJson).put("mobile", userAttributes.get("Mobile").get(0));
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
            throw new Exception("Converting Json to String failed : " + e.getMessage() );
        }
    }
}
