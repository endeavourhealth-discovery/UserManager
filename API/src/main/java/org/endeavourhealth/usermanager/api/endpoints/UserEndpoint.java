package org.endeavourhealth.usermanager.api.endpoints;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.astefanutti.metrics.aspectj.Metrics;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.endeavourhealth.common.security.annotations.RequiresAdmin;
import org.endeavourhealth.common.security.keycloak.client.KeycloakAdminClient;
import org.endeavourhealth.core.data.audit.UserAuditRepository;
import org.endeavourhealth.core.data.audit.models.AuditAction;
import org.endeavourhealth.core.data.audit.models.AuditModule;
import org.endeavourhealth.coreui.endpoints.AbstractEndpoint;
import org.endeavourhealth.usermanager.api.metrics.UserManagerMetricListener;
import org.endeavourhealth.usermanagermodel.models.caching.UserCache;
import org.endeavourhealth.usermanagermodel.models.database.AuditEntity;
import org.endeavourhealth.usermanagermodel.models.database.UserRoleEntity;
import org.endeavourhealth.usermanagermodel.models.enums.ItemType;
import org.endeavourhealth.usermanagermodel.models.json.JsonUser;
import org.endeavourhealth.usermanagermodel.models.json.JsonUserRole;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.*;
import java.util.stream.Collectors;

import static org.endeavourhealth.common.security.SecurityUtils.getCurrentUserId;
import static org.endeavourhealth.common.security.SecurityUtils.hasRole;

@Path("/user")
@Metrics(registry = "UserManagerRegistry")
@Api(description = "API endpoint related to the users.")
public final class UserEndpoint extends AbstractEndpoint {
    private static final Logger LOG = LoggerFactory.getLogger(UserEndpoint.class);

    private static final UserAuditRepository userAudit = new UserAuditRepository(AuditModule.EdsUiModule.User);
    private static final MetricRegistry metricRegistry = UserManagerMetricListener.userManagerMetricRegistry;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="UserManager.UserEndpoint.Get")
    @Path("/users")
    @ApiOperation(value = "Returns a list of all users")
    public Response getUsers(@Context SecurityContext sc,
                             @ApiParam(value = "Organisation Id") @QueryParam("organisationId") String organisationId,
                             @ApiParam(value = "Optional search term") @QueryParam("searchData") String searchData) throws Exception {
        super.setLogbackMarkers(sc);

        userAudit.save(getCurrentUserId(sc), getOrganisationUuidFromToken(sc), AuditAction.Load,
                "Users", "Search Data", searchData, "Organisation Id", organisationId);

        LOG.trace("getUsers");

        List<String> usersAtOrg = new ArrayList<>();

        if (organisationId != null) {

            List<UserRoleEntity> userRoleEntities = UserRoleEntity.getUsersAtOrganisation(organisationId);
            usersAtOrg = userRoleEntities.stream()
                    .map(UserRoleEntity::getUserId)
                    .collect(Collectors.toList());
        }

        List<JsonUser> userList = new ArrayList<>();
        List<UserRepresentation> users;

        KeycloakAdminClient keycloakClient = new KeycloakAdminClient();

        if (searchData == null) {
            users = keycloakClient.realms().users().getUsers("", 0, 100);
        } else {
            users = keycloakClient.realms().users().getUsers(searchData, 0, 100);
        }

        //Add as Json
        for (UserRepresentation user : users) {
            if (searchData != null || usersAtOrg.contains(user.getId())) {
                userList.add(new JsonUser(user));
            }
        }

        AbstractEndpoint.clearLogbackMarkers();
        return Response
                .ok()
                .entity(userList)
                .build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="UserManager.UserEndpoint.saveUser")
    @Path("/users/save")
    @RequiresAdmin
    @ApiOperation(value = "Saves a user or updates an existing user")
    public Response saveUser(@Context SecurityContext sc, JsonUser user,
                             @ApiParam(value = "edit mode") @QueryParam("editMode") String editMode,
                             @ApiParam(value = "User Role Id") @QueryParam("userRoleId") String userRoleId) throws Exception {
        super.setLogbackMarkers(sc);

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
        userAudit.save(getCurrentUserId(sc), getOrganisationUuidFromToken(sc), AuditAction.Save,
                "User", "User", user);

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

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="UserManager.UserEndpoint.saveRoles")
    @Path("/users/saveRoles")
    @RequiresAdmin
    @ApiOperation(value = "Saves roles associated with a user")
    public Response saveRoles(@Context SecurityContext sc, List<JsonUserRole> userRoles,
                             @ApiParam(value = "User Role Id") @QueryParam("userRoleId") String userRoleId) throws Exception {
        super.setLogbackMarkers(sc);

        if (userRoles.size() > 0) {
            for (JsonUserRole role : userRoles) {
                if (role.getId() == null && role.isDeleted()) {
                    continue;  // role was never saved in the DB so no need to add it
                }

                if (role.getId() == null) {
                    role.setId(UUID.randomUUID().toString());
                }
                UserRoleEntity.saveUserRole(role, userRoleId);
            }
        }

        return Response
                .ok()
                .build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="UserManager.UserEndpoint.deleteUser")
    @Path("/users/delete")
    @RequiresAdmin
    @ApiOperation(value = "Deletes a user")
    public Response deleteUser(@Context SecurityContext sc,
                               @ApiParam(value = "User id to be deleted") @QueryParam("userId") String userId,
                               @ApiParam(value = "User Role Id who is making the change") @QueryParam("userRoleId") String userRoleId) throws Exception {
        super.setLogbackMarkers(sc);

        userAudit.save(getCurrentUserId(sc), getOrganisationUuidFromToken(sc), AuditAction.Delete,
                "User", "User Id", userId);

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

        AuditEntity.addToAuditTrail(userRoleId,
                org.endeavourhealth.usermanagermodel.models.enums.AuditAction.EDIT,
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

        AuditEntity.addToAuditTrail(userRoleId,
                org.endeavourhealth.usermanagermodel.models.enums.AuditAction.ADD,
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

        AuditEntity.addToAuditTrail(userRoleId,
                org.endeavourhealth.usermanagermodel.models.enums.AuditAction.DELETE,
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
