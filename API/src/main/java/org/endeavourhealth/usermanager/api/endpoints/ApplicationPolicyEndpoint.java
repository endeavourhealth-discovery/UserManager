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

import static org.endeavourhealth.common.security.SecurityUtils.getCurrentUserId;

@Path("/applicationPolicy")
@Metrics(registry = "UserManagerRegistry")
@Api(value = "Application policy", description = "API endpoint related to the application policies.")
public class ApplicationPolicyEndpoint extends AbstractEndpoint {
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationPolicyEndpoint.class);

    private static final UserAuditRepository userAudit = new UserAuditRepository(AuditModule.EdsUiModule.User);
    private static final MetricRegistry metricRegistry = UserManagerMetricListener.userManagerMetricRegistry;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="UserManager.ApplicationPolicyEndpoint.getApplicationPolicies")
    @Path("/getApplicationPolicies")
    @ApiOperation(value = "Returns a list of application policies")
    public Response getApplicationPolicies(@Context SecurityContext sc) throws Exception {

        super.setLogbackMarkers(sc);
        userAudit.save(SecurityUtils.getCurrentUserId(sc), getOrganisationUuidFromToken(sc), AuditAction.Load,
                "application policy(s)");

        return getAllApplicationPolicies();

    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="UserManager.ApplicationPolicyEndpoint.saveApplicationPolicy")
    @Path("/saveApplicationPolicy")
    @ApiOperation(value = "Save a new application policy or update an existing one.  Accepts a JSON representation " +
            "of a application policy.")
    @RequiresAdmin
    public Response saveApplicationPolicy(@Context SecurityContext sc,
                            @ApiParam(value = "Json representation of application policy to save or update") JsonApplicationPolicy applicationPolicy,
                                          @ApiParam(value = "User Role Id who is making the change") @QueryParam("userRoleId") String userRoleId) throws Exception {
        super.setLogbackMarkers(sc);
        userAudit.save(SecurityUtils.getCurrentUserId(sc), getOrganisationUuidFromToken(sc), AuditAction.Save,
                "Role Type",
                "roleType", applicationPolicy);

        return saveApplicationPolicy(applicationPolicy, userRoleId);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="UserManager.ApplicationPolicyEndpoint.deleteApplicationPolicy")
    @Path("/deleteApplicationPolicy")
    @RequiresAdmin
    @ApiOperation(value = "Deletes an application")
    public Response deleteApplicationPolicy(@Context SecurityContext sc,
                                      @ApiParam(value = "Application policy id to be deleted") @QueryParam("applicationPolicyId") String applicationPolicyId,
                                      @ApiParam(value = "User Role Id who is making the change") @QueryParam("userRoleId") String userRoleId) throws Exception {
        super.setLogbackMarkers(sc);

        userAudit.save(getCurrentUserId(sc), getOrganisationUuidFromToken(sc), AuditAction.Delete,
                "User", "applicationId", applicationPolicyId, "userRoleId", userRoleId);

        deleteApplicationPolicy(applicationPolicyId, userRoleId);

        return Response
                .ok()
                .build();
    }

    private Response getAllApplicationPolicies() throws Exception {
        List<ApplicationPolicyEntity> applicationPolicies = ApplicationPolicyEntity.getAllApplicationPolicies();

        clearLogbackMarkers();
        return Response
                .ok()
                .entity(applicationPolicies)
                .build();
    }

    private Response saveApplicationPolicy(JsonApplicationPolicy applicationPolicy, String userInRoleId) throws Exception {

        ApplicationPolicyEntity.saveApplicationPolicy(applicationPolicy, userInRoleId);

        clearLogbackMarkers();
        return Response
                .ok()
                .build();
    }

    private Response deleteApplicationPolicy(String applicationPolicyId, String userRoleId) throws Exception {

        ApplicationPolicyEntity.deleteApplicationPolicy(applicationPolicyId, userRoleId);

        clearLogbackMarkers();
        return Response
                .ok()
                .build();
    }

}
