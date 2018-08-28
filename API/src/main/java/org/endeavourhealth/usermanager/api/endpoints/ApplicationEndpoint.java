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
import org.endeavourhealth.usermanagermodel.models.database.ApplicationEntity;
import org.endeavourhealth.usermanagermodel.models.json.JsonApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;

@Path("/application")
@Metrics(registry = "UserManagerRegistry")
@Api(description = "API endpoint related to the applications.")
public class ApplicationEndpoint extends AbstractEndpoint {
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationEndpoint.class);

    private static final UserAuditRepository userAudit = new UserAuditRepository(AuditModule.EdsUiModule.User);
    private static final MetricRegistry metricRegistry = UserManagerMetricListener.userManagerMetricRegistry;
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="UserManager.RoleTypeEndpoint.getApplications")
    @Path("/getApplications")
    @ApiOperation(value = "Returns a list of applications")

    public Response getApplications(@Context SecurityContext sc) throws Exception {

        super.setLogbackMarkers(sc);
        userAudit.save(SecurityUtils.getCurrentUserId(sc), getOrganisationUuidFromToken(sc), AuditAction.Load,
                "application(s)");

        return getAllApplications();

    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="UserManager.RoleTypeEndpoint.saveApplication")
    @Path("/saveApplication")
    @ApiOperation(value = "Save a new application or update an existing one.  Accepts a JSON representation " +
            "of an application.")
    @RequiresAdmin
    public Response saveApplication(@Context SecurityContext sc,
                                    @ApiParam(value = "Json representation of the application to save or update") JsonApplication application,
                                    @ApiParam(value = "User Role Id who is making the change") @QueryParam("userRoleId") String userRoleId) throws Exception {
        super.setLogbackMarkers(sc);
        userAudit.save(SecurityUtils.getCurrentUserId(sc), getOrganisationUuidFromToken(sc), AuditAction.Save,
                "Role Type",
                "application", application);

        return saveApplication(application, userRoleId);
    }

    private Response getAllApplications() throws Exception {
        List<ApplicationEntity> applications = ApplicationEntity.getAllApplications();

        clearLogbackMarkers();
        return Response
                .ok()
                .entity(applications)
                .build();
    }

    private Response saveApplication(JsonApplication application, String userRoleId) throws Exception {

        ApplicationEntity.saveApplication(application, userRoleId);

        clearLogbackMarkers();
        return Response
                .ok()
                .build();
    }

}
