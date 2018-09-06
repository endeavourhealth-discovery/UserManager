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
import org.endeavourhealth.usermanagermodel.models.database.ApplicationAccessProfileEntity;
import org.endeavourhealth.usermanagermodel.models.json.JsonApplicationAccessProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;

import static org.endeavourhealth.common.security.SecurityUtils.getCurrentUserId;

@Path("/applicationProfile")
@Metrics(registry = "UserManagerRegistry")
@Api(value = "Application profile", description = "API endpoint related to the application profiles.")
public class ApplicationProfileEndpoint extends AbstractEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationProfileEndpoint.class);

    private static final UserAuditRepository userAudit = new UserAuditRepository(AuditModule.EdsUiModule.User);
    private static final MetricRegistry metricRegistry = UserManagerMetricListener.userManagerMetricRegistry;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="UserManager.ApplicationProfileEndpoint.getApplicationProfiles")
    @Path("/getApplicationProfiles")
    @ApiOperation(value = "Returns a list of application profiles")
    public Response getApplications(@Context SecurityContext sc,
                                    @ApiParam(value = "Application id") @QueryParam("applicationId") String applicationId) throws Exception {

        super.setLogbackMarkers(sc);
        userAudit.save(SecurityUtils.getCurrentUserId(sc), getOrganisationUuidFromToken(sc), AuditAction.Load,
                "application(s)");

        return getApplicationProfiles(applicationId);

    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="UserManager.ApplicationProfileEndpoint.saveApplicationProfiles")
    @Path("/saveApplicationProfiles")
    @ApiOperation(value = "Save a new application or update an existing one.  Accepts a JSON representation " +
            "of an application.")
    @RequiresAdmin
    public Response saveApplicationProfiles(@Context SecurityContext sc,
                                    @ApiParam(value = "Json representation of the application profile to save or update") List<JsonApplicationAccessProfile> applicationProfile,
                                    @ApiParam(value = "User Role Id who is making the change") @QueryParam("userRoleId") String userRoleId) throws Exception {
        super.setLogbackMarkers(sc);
        userAudit.save(SecurityUtils.getCurrentUserId(sc), getOrganisationUuidFromToken(sc), AuditAction.Save,
                "save application",
                "application", applicationProfile);

        return saveApplicationProfiles(applicationProfile, userRoleId);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="UserManager.ApplicationProfileEndpoint.deleteApplicationProfile")
    @Path("/deleteApplicationProfile")
    @RequiresAdmin
    @ApiOperation(value = "Deletes an application")
    public Response deleteApplication(@Context SecurityContext sc,
                                      @ApiParam(value = "Application profile id to be deleted") @QueryParam("applicationProfileId") String applicationProfileId,
                                      @ApiParam(value = "User Role Id who is making the change") @QueryParam("userRoleId") String userRoleId) throws Exception {
        super.setLogbackMarkers(sc);

        userAudit.save(getCurrentUserId(sc), getOrganisationUuidFromToken(sc), AuditAction.Delete,
                "User", "applicationProfileId", applicationProfileId, "userRoleId", userRoleId);

        deleteApplicationProfile(applicationProfileId, userRoleId);

        return Response
                .ok()
                .build();
    }

    private Response getApplicationProfiles(String applicationProfileId) throws Exception {
        List<ApplicationAccessProfileEntity> applications = ApplicationAccessProfileEntity.getApplicationProfiles(applicationProfileId);

        clearLogbackMarkers();
        return Response
                .ok()
                .entity(applications)
                .build();
    }

    private Response saveApplicationProfiles(List<JsonApplicationAccessProfile> applicationProfiles, String userRoleId) throws Exception {

        for (JsonApplicationAccessProfile applicationProfile : applicationProfiles) {
            ApplicationAccessProfileEntity.saveApplicationProfile(applicationProfile, userRoleId);
        }

        clearLogbackMarkers();
        return Response
                .ok()
                .build();
    }

    private Response deleteApplicationProfile(String applicationId, String userRoleId) throws Exception {

        ApplicationAccessProfileEntity.deleteApplicationProfile(applicationId, userRoleId);

        clearLogbackMarkers();
        return Response
                .ok()
                .build();
    }
}
