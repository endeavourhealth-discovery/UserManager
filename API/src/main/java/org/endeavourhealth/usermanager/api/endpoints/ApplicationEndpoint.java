package org.endeavourhealth.usermanager.api.endpoints;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.annotation.Timed;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.endeavourhealth.common.security.SecurityUtils;
import org.endeavourhealth.common.security.annotations.RequiresAdmin;
import org.endeavourhealth.core.data.audit.UserAuditRepository;
import org.endeavourhealth.core.data.audit.models.AuditAction;
import org.endeavourhealth.core.data.audit.models.AuditModule;
import org.endeavourhealth.core.database.dal.usermanager.caching.*;
import org.endeavourhealth.core.database.dal.usermanager.models.JsonApplication;
import org.endeavourhealth.core.database.rdbms.usermanager.models.ApplicationEntity;
import org.endeavourhealth.coreui.endpoints.AbstractEndpoint;
import org.endeavourhealth.usermanager.api.DAL.ApplicationDAL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;

import static org.endeavourhealth.common.security.SecurityUtils.getCurrentUserId;

@Path("/application")
@Api(value = "Application", description = "API endpoint related to the applications.")
public class ApplicationEndpoint extends AbstractEndpoint {
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationEndpoint.class);

    private static final UserAuditRepository userAudit = new UserAuditRepository(AuditModule.EdsUiModule.User);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="UserManager.ApplicationEndpoint.getApplications")
    @Path("/getApplications")
    @ApiOperation(value = "Returns a list of applications")
    public Response getApplications(@Context SecurityContext sc) throws Exception {

        super.setLogbackMarkers(sc);
        userAudit.save(SecurityUtils.getCurrentUserId(sc), getOrganisationUuidFromToken(sc), AuditAction.Load,
                "application(s)");

        List<ApplicationEntity> applications = new ApplicationDAL().getAllApplications();

        clearLogbackMarkers();
        return Response
                .ok()
                .entity(applications)
                .build();

    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="UserManager.ApplicationEndpoint.saveApplication")
    @Path("/saveApplication")
    @ApiOperation(value = "Save a new application or update an existing one.  Accepts a JSON representation " +
            "of an application.")
    @RequiresAdmin
    public Response saveApplication(@Context SecurityContext sc,
                                    @ApiParam(value = "Json representation of the application to save or update") JsonApplication application,
                                    @ApiParam(value = "User Role Id who is making the change") @QueryParam("userRoleId") String userRoleId) throws Exception {
        super.setLogbackMarkers(sc);
        userAudit.save(SecurityUtils.getCurrentUserId(sc), getOrganisationUuidFromToken(sc), AuditAction.Save,
                "save application",
                "application", application);

        String newId = new ApplicationDAL().saveApplication(application, userRoleId);

        clearLogbackMarkers();
        return Response
                .ok()
                .entity(newId)
                .build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="UserManager.ApplicationEndpoint.deleteApplication")
    @Path("/deleteApplication")
    @RequiresAdmin
    @ApiOperation(value = "Deletes an application")
    public Response deleteApplication(@Context SecurityContext sc,
                                     @ApiParam(value = "Application id to be deleted") @QueryParam("applicationId") String applicationId,
                                     @ApiParam(value = "User Role Id who is making the change") @QueryParam("userRoleId") String userRoleId) throws Exception {
        super.setLogbackMarkers(sc);

        userAudit.save(getCurrentUserId(sc), getOrganisationUuidFromToken(sc), AuditAction.Delete,
                "User", "applicationId", applicationId, "userRoleId", userRoleId);

        new ApplicationDAL().deleteApplication(applicationId, userRoleId);

        clearLogbackMarkers();

        return Response
                .ok()
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="UserManager.ApplicationEndpoint.flushCache")
    @Path("/flushCache")
    @ApiOperation(value = "Returns a list of applications")
    public Response flushCache(@Context SecurityContext sc) throws Exception {

        super.setLogbackMarkers(sc);
        userAudit.save(SecurityUtils.getCurrentUserId(sc), getOrganisationUuidFromToken(sc), AuditAction.Load,
                "application(s)");

        return flushCache();

    }

    private Response flushCache() throws Exception {
        ApplicationCache.flushCache();
        ApplicationPolicyCache.flushCache();
        ApplicationProfileCache.flushCache();
        DelegationCache.flushCache();
        OrganisationCache.flushCache();
        ProjectCache.flushCache();
        RegionCache.flushCache();
        UserCache.flushCache();

        clearLogbackMarkers();
        return Response
                .ok()
                .build();
    }

}
