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
import org.endeavourhealth.usermanagermodel.models.database.ApplicationPolicyEntity;
import org.endeavourhealth.usermanagermodel.models.json.JsonApplicationPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;
import java.util.UUID;

@Path("/roleType")
@Metrics(registry = "UserManagerRegistry")
@Api(value = "Role type", description = "API endpoint related to the role types.")
public class RoleTypeEndpoint extends AbstractEndpoint {
    private static final Logger LOG = LoggerFactory.getLogger(RoleTypeEndpoint.class);

    private static final UserAuditRepository userAudit = new UserAuditRepository(AuditModule.EdsUiModule.User);
    private static final MetricRegistry metricRegistry = UserManagerMetricListener.userManagerMetricRegistry;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="UserManager.RoleTypeEndpoint.getRoles")
    @Path("/getRoles")
    @ApiOperation(value = "Returns a list of delegations")
    public Response getRoles(@Context SecurityContext sc) throws Exception {

        super.setLogbackMarkers(sc);
        userAudit.save(SecurityUtils.getCurrentUserId(sc), getOrganisationUuidFromToken(sc), AuditAction.Load,
                "Organisation(s)");

        return getAllRoleTypes();

    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="UserManager.RoleTypeEndpoint.saveRoleType")
    @Path("/saveRoleType")
    @ApiOperation(value = "Save a new role type or update an existing one.  Accepts a JSON representation " +
            "of a role type.")
    @RequiresAdmin
    public Response saveRoleType(@Context SecurityContext sc,
                            @ApiParam(value = "Json representation of role type to save or update") JsonApplicationPolicy roleType) throws Exception {
        super.setLogbackMarkers(sc);
        userAudit.save(SecurityUtils.getCurrentUserId(sc), getOrganisationUuidFromToken(sc), AuditAction.Save,
                "Role Type",
                "roleType", roleType);

        return saveRoleType(roleType);
    }

    private Response getAllRoleTypes() throws Exception {
        List<ApplicationPolicyEntity> roleTypes = ApplicationPolicyEntity.getAllRoleTypes();

        clearLogbackMarkers();
        return Response
                .ok()
                .entity(roleTypes)
                .build();
    }

    private Response saveRoleType(JsonApplicationPolicy roleType) throws Exception {

        String roleId = roleType.getId();
        if (roleId == null) {
            roleId = UUID.randomUUID().toString();
            roleType.setId(roleId);
        }

        ApplicationPolicyEntity.saveRoleType(roleType);

        clearLogbackMarkers();
        return Response
                .ok()
                .entity(roleId)
                .build();
    }

}
