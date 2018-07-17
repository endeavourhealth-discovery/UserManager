package org.endeavourhealth.usermanager.api.endpoints;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.annotation.Timed;
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
import org.endeavourhealth.coreui.json.JsonEndUser;
import org.endeavourhealth.usermanager.api.metrics.UserManagerMetricListener;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
                             @ApiParam(value = "Optional search term") @QueryParam("searchData") String searchData) throws Exception {
        super.setLogbackMarkers(sc);

        userAudit.save(getCurrentUserId(sc), getOrganisationUuidFromToken(sc), AuditAction.Load,
                "Users", "Search Data", searchData);

        LOG.trace("getUsers");

        List<JsonEndUser> userList = new ArrayList<>();
        List<UserRepresentation> users;

        KeycloakAdminClient keycloakClient = new KeycloakAdminClient();

        if (searchData == null) {
            users = keycloakClient.realms().users().getUsers("", 0, 100);
        } else {
            users = keycloakClient.realms().users().getUsers(searchData, 0, 100);
        }

        //Add as Json
        for (UserRepresentation user : users) {
            userList.add(new JsonEndUser(user));
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
    public Response saveUser(@Context SecurityContext sc, JsonEndUser user, @QueryParam("editMode") String editMode) throws Exception {
        super.setLogbackMarkers(sc);

        boolean editModeb = editMode.equalsIgnoreCase("1") ? true:false;

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
            userRep = keycloakClient.realms().users().postUser(userRep);
            //This is the newly created userId
            userId = userRep.getId();
            user.setUuid(UUID.fromString(userId));  //new uuid to return to client
        } else {
            //This is the existing userId, so we set for update
            userId = user.getUuid().toString();
            userRep.setId(userId);
            userRep = keycloakClient.realms().users().putUser(userRep);
        }

        //Now, file the new temporary password if it is not blank (edit mode password may be blank)
        if (!editModeb || user.getPassword().trim()!=""){
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(user.getPassword());
            credential.setTemporary(true);
            keycloakClient.realms().users().setUserPassword (userId, credential);
        }

        //Blank out password for audit object
        user.setPassword("*********");
        userAudit.save(getCurrentUserId(sc), getOrganisationUuidFromToken(sc), AuditAction.Save,
                "User", "User", user);

        clearLogbackMarkers();

        return Response
                .ok()
                .entity(user)
                .build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="UserManager.UserEndpoint.deleteUser")
    @Path("/users/delete")
    @RequiresAdmin
    @ApiOperation(value = "Deletes a user")
    public Response deleteUser(@Context SecurityContext sc, @QueryParam("userId") String userId) throws Exception {
        super.setLogbackMarkers(sc);

        userAudit.save(getCurrentUserId(sc), getOrganisationUuidFromToken(sc), AuditAction.Delete,
                "User", "User Id", userId);

        //Create the keycloak admin client and delete the user
        KeycloakAdminClient keycloakClient = new KeycloakAdminClient();
        boolean success = keycloakClient.realms().users().deleteUser(userId);

        return Response
                .ok()
                .entity(success)
                .build();
    }

}