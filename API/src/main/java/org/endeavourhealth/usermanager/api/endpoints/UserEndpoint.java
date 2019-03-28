package org.endeavourhealth.usermanager.api.endpoints;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.annotation.Timed;
import io.astefanutti.metrics.aspectj.Metrics;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.endeavourhealth.common.security.annotations.RequiresAdmin;
import org.endeavourhealth.common.security.datasharingmanagermodel.models.database.RegionEntity;
import org.endeavourhealth.common.security.usermanagermodel.models.DAL.SecurityUserRegionDAL;
import org.endeavourhealth.common.security.usermanagermodel.models.caching.RegionCache;
import org.endeavourhealth.common.security.usermanagermodel.models.caching.UserCache;
import org.endeavourhealth.common.security.usermanagermodel.models.database.UserApplicationPolicyEntity;
import org.endeavourhealth.common.security.usermanagermodel.models.database.UserRegionEntity;
import org.endeavourhealth.common.security.usermanagermodel.models.json.JsonUser;
import org.endeavourhealth.common.security.usermanagermodel.models.json.JsonUserApplicationPolicy;
import org.endeavourhealth.common.security.usermanagermodel.models.json.JsonUserProject;
import org.endeavourhealth.common.security.usermanagermodel.models.json.JsonUserRegion;
import org.endeavourhealth.core.data.audit.UserAuditRepository;
import org.endeavourhealth.core.data.audit.models.AuditAction;
import org.endeavourhealth.core.data.audit.models.AuditModule;
import org.endeavourhealth.coreui.endpoints.AbstractEndpoint;
import org.endeavourhealth.usermanager.api.DAL.UserApplicationPolicyDAL;
import org.endeavourhealth.usermanager.api.DAL.UserRegionDAL;
import org.endeavourhealth.usermanager.api.logic.UserLogic;
import org.endeavourhealth.usermanager.api.metrics.UserManagerMetricListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.*;

import static org.endeavourhealth.common.security.SecurityUtils.getCurrentUserId;

@Path("/user")
@Metrics(registry = "UserManagerRegistry")
@Api(value = "User", description = "API endpoint related to the users.")
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

        return new UserLogic().getUsers(organisationId, searchData, false);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="UserManager.UserEndpoint.machineUsers")
    @Path("/machineUsers")
    @ApiOperation(value = "Returns a list of all machine users")
    public Response getMachineUsers(@Context SecurityContext sc,
                             @ApiParam(value = "Organisation Id") @QueryParam("organisationId") String organisationId,
                             @ApiParam(value = "Optional search term") @QueryParam("searchData") String searchData) throws Exception {
        super.setLogbackMarkers(sc);

        userAudit.save(getCurrentUserId(sc), getOrganisationUuidFromToken(sc), AuditAction.Load,
                "Users", "Search Data", searchData, "Organisation Id", organisationId);

        LOG.trace("getUsers");

        return new UserLogic().getUsers(organisationId, searchData, true);
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

        userAudit.save(getCurrentUserId(sc), getOrganisationUuidFromToken(sc), AuditAction.Save,
                "User", "User", user);

        return new UserLogic().saveUser(user, editMode, userRoleId, sc);

    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="UserManager.UserEndpoint.saveProjects")
    @Path("/users/saveProjects")
    @RequiresAdmin
    @ApiOperation(value = "Saves projects associated with a user")
    public Response saveProjects(@Context SecurityContext sc, List<JsonUserProject> userProjects,
                             @ApiParam(value = "User Role Id") @QueryParam("userRoleId") String userRoleId) throws Exception {
        super.setLogbackMarkers(sc);

        return new UserLogic().saveProjects(userProjects, userRoleId);
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

        return new UserLogic().deleteUser(userId, userRoleId);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="UserManager.UserEndpoint.getUser")
    @Path("/users/user")
    @RequiresAdmin
    @ApiOperation(value = "Gets the details for a user")
    public Response getUser(@Context SecurityContext sc,
                            @ApiParam(value = "User id to be retrieved") @QueryParam("userId") String userId) throws Exception {
        super.setLogbackMarkers(sc);

        userAudit.save(getCurrentUserId(sc), getOrganisationUuidFromToken(sc), AuditAction.Load,
                "User", "User Id", userId);


        AbstractEndpoint.clearLogbackMarkers();

        return new UserLogic().getUser(sc, userId, false);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="UserManager.UserEndpoint.getMachineUser")
    @Path("/users/machineUser")
    @RequiresAdmin
    @ApiOperation(value = "Gets the details for a machine user")
    public Response getMachineUser(@Context SecurityContext sc,
                            @ApiParam(value = "User id to be retrieved") @QueryParam("userId") String userId) throws Exception {
        super.setLogbackMarkers(sc);

        userAudit.save(getCurrentUserId(sc), getOrganisationUuidFromToken(sc), AuditAction.Load,
                "User", "User Id", userId);


        AbstractEndpoint.clearLogbackMarkers();

        return new UserLogic().getUser(sc, userId, true);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="UserManager.UserEndpoint.getUserRegion")
    @Path("/userRegion")
    @ApiOperation(value = "Returns the data sharing manager region associated with the user")
    public Response getUserRegion(@Context SecurityContext sc,
                            @ApiParam(value = "User id to get the region for") @QueryParam("userId") String userId) throws Exception {
        super.setLogbackMarkers(sc);

        userAudit.save(getCurrentUserId(sc), getOrganisationUuidFromToken(sc), AuditAction.Load,
                "User Region", "User Id", userId);

        UserRegionEntity userRegion = new SecurityUserRegionDAL().getUserRegion(userId);
        if (userRegion == null) {
            userRegion = new UserRegionEntity();
        }

        AbstractEndpoint.clearLogbackMarkers();
        return Response
                .ok()
                .entity(userRegion)
                .build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="UserManager.UserEndpoint.setUserRegion")
    @Path("/setUserRegion")
    @RequiresAdmin
    @ApiOperation(value = "Saves region associated with a user")
    public Response setUserRegion(@Context SecurityContext sc, JsonUserRegion userRegion,
                                  @ApiParam(value = "userProjectId of the user making the change") @QueryParam("userProjectId") String userProjectId) throws Exception {
        super.setLogbackMarkers(sc);

        userAudit.save(getCurrentUserId(sc), getOrganisationUuidFromToken(sc), AuditAction.Load,
                "User Region", "User region", userRegion);

        LOG.trace("getUser");

        new UserRegionDAL().saveUserRegion(userRegion, userProjectId);

        AbstractEndpoint.clearLogbackMarkers();
        return Response
                .ok()
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="UserManager.UserEndpoint.getAvailableRegions")
    @Path("/availableRegions")
    @ApiOperation(value = "Returns the available regions")
    public Response getAvailableRegions(@Context SecurityContext sc) throws Exception {
        super.setLogbackMarkers(sc);

        userAudit.save(getCurrentUserId(sc), getOrganisationUuidFromToken(sc), AuditAction.Load,
                "Get available regions");

        LOG.trace("getUser");

        return getAvailableRegions();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="UserManager.UserEndpoint.getUserApplicationPolicy")
    @Path("/userApplicationPolicy")
    @ApiOperation(value = "Returns the application policy associated with the user")
    public Response getUserApplicationPolicy(@Context SecurityContext sc,
                                  @ApiParam(value = "User id to get the application policy for") @QueryParam("userId") String userId) throws Exception {
        super.setLogbackMarkers(sc);

        userAudit.save(getCurrentUserId(sc), getOrganisationUuidFromToken(sc), AuditAction.Load,
                "User application policy", "User Id", userId);

        LOG.trace("getUser");

        UserApplicationPolicyEntity userPolicy = UserCache.getUserApplicationPolicy(userId);
        if (userPolicy == null) {
            userPolicy = new UserApplicationPolicyEntity();
        }

        AbstractEndpoint.clearLogbackMarkers();
        return Response
                .ok()
                .entity(userPolicy)
                .build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="UserManager.UserEndpoint.setUserApplicationPolicy")
    @Path("/setUserApplicationPolicy")
    @RequiresAdmin
    @ApiOperation(value = "Saves application policy associated with a user")
    public Response setUserApplicationPolicy(@Context SecurityContext sc, JsonUserApplicationPolicy userApplicationPolicy,
                                  @ApiParam(value = "userProjectId of the user making the change") @QueryParam("userProjectId") String userProjectId) throws Exception {
        super.setLogbackMarkers(sc);

        userAudit.save(getCurrentUserId(sc), getOrganisationUuidFromToken(sc), AuditAction.Load,
                "User application policy", "User application policy", userApplicationPolicy);

        LOG.trace("getUser");

        new UserApplicationPolicyDAL().saveUserApplicationPolicyId(userApplicationPolicy, userProjectId);

        AbstractEndpoint.clearLogbackMarkers();
        return Response
                .ok()
                .build();
    }

    private Response getAvailableRegions() throws Exception {

        List<RegionEntity> regions = RegionCache.getAllRegions();

        return Response
                .ok()
                .entity(regions)
                .build();
    }

}
