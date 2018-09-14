package org.endeavourhealth.usermanager.api.endpoints;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.annotation.Timed;
import io.astefanutti.metrics.aspectj.Metrics;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.endeavourhealth.common.security.SecurityUtils;
import org.endeavourhealth.core.data.audit.UserAuditRepository;
import org.endeavourhealth.core.data.audit.models.AuditAction;
import org.endeavourhealth.core.data.audit.models.AuditModule;
import org.endeavourhealth.coreui.endpoints.AbstractEndpoint;
import org.endeavourhealth.datasharingmanagermodel.models.database.OrganisationEntity;
import org.endeavourhealth.datasharingmanagermodel.models.json.JsonProject;
import org.endeavourhealth.usermanager.api.metrics.UserManagerMetricListener;
import org.endeavourhealth.usermanagermodel.models.caching.ApplicationPolicyCache;
import org.endeavourhealth.usermanagermodel.models.caching.OrganisationCache;
import org.endeavourhealth.usermanagermodel.models.caching.ProjectCache;
import org.endeavourhealth.usermanagermodel.models.database.ApplicationPolicyAttributeEntity;
import org.endeavourhealth.usermanagermodel.models.database.ApplicationPolicyEntity;
import org.endeavourhealth.usermanagermodel.models.database.UserApplicationPolicyEntity;
import org.endeavourhealth.usermanagermodel.models.database.UserProjectEntity;
import org.endeavourhealth.usermanagermodel.models.json.JsonApplicationPolicyAttribute;
import org.endeavourhealth.usermanagermodel.models.json.JsonUserProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import java.util.List;

@Path("/userProfile")
@Metrics(registry = "UserManagerRegistry")
@Api(value = "User profile", description = "API endpoint related to the user profile.")
public class UserProfileEndpoint extends AbstractEndpoint {
    private static final Logger LOG = LoggerFactory.getLogger(UserProfileEndpoint.class);

    private static final UserAuditRepository userAudit = new UserAuditRepository(AuditModule.EdsUiModule.User);
    private static final MetricRegistry metricRegistry = UserManagerMetricListener.userManagerMetricRegistry;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="UserManager.UserBioEndpoint.getUserProfile")
    @Path("/getUserProfile")
    @ApiOperation(value = "Returns a representation of the access rights for a user role")
    public Response getAccessProfile(@Context SecurityContext sc,
                                     @ApiParam(value = "User id") @QueryParam("userId") String userId) throws Exception {

        super.setLogbackMarkers(sc);
        userAudit.save(SecurityUtils.getCurrentUserId(sc), getOrganisationUuidFromToken(sc), AuditAction.Load,
                "application(s)");

        return getUserProfile(userId);

    }

    private Response getUserProfile(String userId) throws Exception {
        UserApplicationPolicyEntity userApplicationPolicyEntity = UserApplicationPolicyEntity.getUserApplicationPolicyId(userId);
        ApplicationPolicyEntity userAppPolicyEntity = ApplicationPolicyCache.getApplicationPolicyDetails(userApplicationPolicyEntity.getApplicationPolicyId());

        List<UserProjectEntity> projectEntities = UserProjectEntity.getUserProjectEntities(userId);

        List<JsonUserProfile> userProfiles = new ArrayList<>();

        for (UserProjectEntity profile : projectEntities) {
            JsonUserProfile userProfile = userProfiles.stream().filter(app -> app.getOrganisation().equals(profile.getOrganisationId())).findFirst().orElse(new JsonUserProfile());
            if (userProfile.getOrganisation() == null) {
                OrganisationEntity organisation = OrganisationCache.getOrganisationDetails(profile.getOrganisationId());

                userProfile.setOrganisation(organisation);
                userProfiles.add(userProfile);
            }

            JsonProject project = ProjectCache.getJsonProjectDetails(profile.getProjectId());
            ApplicationPolicyEntity projectAppPolicyEntity = ApplicationPolicyCache.getApplicationPolicyDetails(project.getApplicationPolicy());


            List<JsonApplicationPolicyAttribute> projectPolicyAttributes = processProjectPolicyAttributes(userAppPolicyEntity.getId(), projectAppPolicyEntity.getId());
            project.setApplicationPolicyAttributes(projectPolicyAttributes);

            userProfile.addProject(project);
        }

        return Response
                .ok()
                .entity(userProfiles)
                .build();
    }

    private List<JsonApplicationPolicyAttribute> processProjectPolicyAttributes(String userPolicyId, String projectPolicyId) throws Exception {
        List<JsonApplicationPolicyAttribute> mergedAttributes = new ArrayList<>();

        List<JsonApplicationPolicyAttribute> projectPolicyAttributes = ApplicationPolicyAttributeEntity.getApplicationPolicyAttributes(projectPolicyId);

        if (userPolicyId.equals(projectPolicyId)) {
            // both user and project have the same policy so just return the project attributes
            return projectPolicyAttributes;
        }

        List<JsonApplicationPolicyAttribute> userPolicyAttributes = ApplicationPolicyAttributeEntity.getApplicationPolicyAttributes(userPolicyId);

        for (JsonApplicationPolicyAttribute attribute : projectPolicyAttributes) {
            if (userPolicyAttributes.stream().filter(a -> a.getId().equals(attribute.getId())).findFirst().isPresent()) {
                //user policy has the project attribute so add it to the list
                mergedAttributes.add(attribute);
            }
        }

        return mergedAttributes;
    }

}
