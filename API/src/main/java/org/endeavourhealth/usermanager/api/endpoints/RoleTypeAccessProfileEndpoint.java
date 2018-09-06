package org.endeavourhealth.usermanager.api.endpoints;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.annotation.Timed;
import io.astefanutti.metrics.aspectj.Metrics;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.endeavourhealth.common.security.SecurityUtils;
import org.endeavourhealth.common.security.annotations.RequiresAdmin;
import org.endeavourhealth.core.data.audit.UserAuditRepository;
import org.endeavourhealth.core.data.audit.models.AuditAction;
import org.endeavourhealth.core.data.audit.models.AuditModule;
import org.endeavourhealth.coreui.endpoints.AbstractEndpoint;
import org.endeavourhealth.usermanager.api.metrics.UserManagerMetricListener;
import org.endeavourhealth.usermanagermodel.models.database.RoleTypeAccessProfileEntity;
import org.endeavourhealth.usermanagermodel.models.json.JsonRoleTypeAccessProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;

import static org.endeavourhealth.common.security.SecurityUtils.getCurrentUserId;

@Path("/roleTypeAccessProfile")
@Metrics(registry = "UserManagerRegistry")
@Api(value = "Role type access profile", description = "API endpoint related to the role type access profiles.")
public class RoleTypeAccessProfileEndpoint extends AbstractEndpoint {
    private static final Logger LOG = LoggerFactory.getLogger(RoleTypeAccessProfileEndpoint.class);

    private static final UserAuditRepository userAudit = new UserAuditRepository(AuditModule.EdsUiModule.User);
    private static final MetricRegistry metricRegistry = UserManagerMetricListener.userManagerMetricRegistry;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="UserManager.RoleTypeAccessProfileEndpoint.getRoleTypeAccessProfiles")
    @Path("/getRoleTypeAccessProfiles")
    @ApiOperation(value = "Returns a list of role type access profiles")
    public Response getRoleTypeAccessProfiles(@Context SecurityContext sc,
                                    @ApiParam(value = "Role type id") @QueryParam("roleTypeId") String roleTypeId) throws Exception {

        super.setLogbackMarkers(sc);
        userAudit.save(SecurityUtils.getCurrentUserId(sc), getOrganisationUuidFromToken(sc), AuditAction.Load,
                "application(s)");

        return getRoleTypeAccessProfiles(roleTypeId);

    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="UserManager.RoleTypeAccessProfileEndpoint.saveRoleTypeAccessProfiles")
    @Path("/saveRoleTypeAccessProfiles")
    @ApiOperation(value = "Save a new role type access profile or update an existing one.  Accepts a JSON representation " +
            "of an role type access profile.")
    @RequiresAdmin
    public Response saveRoleTypeAccessProfiles(@Context SecurityContext sc,
                                            @ApiParam(value = "Json representation of the role type access profiles to save or update") List<JsonRoleTypeAccessProfile> roleTypeProfiles,
                                            @ApiParam(value = "User Role Id who is making the change") @QueryParam("userRoleId") String userRoleId) throws Exception {
        super.setLogbackMarkers(sc);
        userAudit.save(SecurityUtils.getCurrentUserId(sc), getOrganisationUuidFromToken(sc), AuditAction.Save,
                "save application",
                "roleTypeProfiles", roleTypeProfiles);

        return saveRoleTypeAccessProfiles(roleTypeProfiles, userRoleId);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="UserManager.RoleTypeAccessProfileEndpoint.deleteRoleTypeAccessProfile")
    @Path("/deleteRoleTypeAccessProfile")
    @RequiresAdmin
    @ApiOperation(value = "Deletes a role type access profile")
    public Response deleteApplication(@Context SecurityContext sc,
                                      @ApiParam(value = "role type access profile id to be deleted") @QueryParam("roleTypeProfileId") String roleTypeProfileId,
                                      @ApiParam(value = "User Role Id who is making the change") @QueryParam("userRoleId") String userRoleId) throws Exception {
        super.setLogbackMarkers(sc);

        userAudit.save(getCurrentUserId(sc), getOrganisationUuidFromToken(sc), AuditAction.Delete,
                "User", "roleTypeProfileId", roleTypeProfileId, "userRoleId", userRoleId);

        deleteRoleTypeAccessProfile(roleTypeProfileId, userRoleId);

        return Response
                .ok()
                .build();
    }

    private Response getRoleTypeAccessProfiles(String roleTypeId) throws Exception {
        List<JsonRoleTypeAccessProfile> profiles = RoleTypeAccessProfileEntity.getRoleAccessProfiles(roleTypeId);

        clearLogbackMarkers();
        return Response
                .ok()
                .entity(profiles)
                .build();
    }

    private Response saveRoleTypeAccessProfiles(List<JsonRoleTypeAccessProfile> roleTypeProfiles, String userRoleId) throws Exception {

        for (JsonRoleTypeAccessProfile roleProfile : roleTypeProfiles) {
            RoleTypeAccessProfileEntity.saveRoleAccessProfile(roleProfile, userRoleId);
        }

        clearLogbackMarkers();
        return Response
                .ok()
                .build();
    }

    private Response deleteRoleTypeAccessProfile(String roleTypeProfileId, String userRoleId) throws Exception {

        RoleTypeAccessProfileEntity.deleteRoleAccessProfile(roleTypeProfileId, userRoleId);

        clearLogbackMarkers();
        return Response
                .ok()
                .build();
    }
}
