package org.endeavourhealth.usermanager.api.endpoints;

import com.amazonaws.util.StringUtils;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.databind.JsonNode;
import io.astefanutti.metrics.aspectj.Metrics;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.endeavourhealth.common.cache.ObjectMapperPool;
import org.endeavourhealth.common.security.SecurityUtils;
import org.endeavourhealth.core.data.audit.UserAuditRepository;
import org.endeavourhealth.core.data.audit.models.AuditAction;
import org.endeavourhealth.core.data.audit.models.AuditModule;
import org.endeavourhealth.coreui.endpoints.AbstractEndpoint;
import org.endeavourhealth.usermanager.api.metrics.UserManagerMetricListener;
import org.endeavourhealth.usermanagermodel.models.database.RoleTypeAccessProfileEntity;
import org.endeavourhealth.usermanagermodel.models.json.JsonRoleTypeAccessProfile;
import org.endeavourhealth.usermanagermodel.models.json.JsonUserAccessProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import java.util.List;

@Path("/userBio")
@Metrics(registry = "UserManagerRegistry")
@Api(description = "API endpoint related to the user bio.")
public class UserBioEndpoint extends AbstractEndpoint {
    private static final Logger LOG = LoggerFactory.getLogger(UserBioEndpoint.class);

    private static final UserAuditRepository userAudit = new UserAuditRepository(AuditModule.EdsUiModule.User);
    private static final MetricRegistry metricRegistry = UserManagerMetricListener.userManagerMetricRegistry;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="UserManager.UserBioEndpoint.getAccessProfile")
    @Path("/getAccessProfile")
    @ApiOperation(value = "Returns a representation of the access rights for a user role")
    public Response getAccessProfile(@Context SecurityContext sc,
                                     @ApiParam(value = "Role type id") @QueryParam("roleTypeId") String roleTypeId) throws Exception {

        super.setLogbackMarkers(sc);
        userAudit.save(SecurityUtils.getCurrentUserId(sc), getOrganisationUuidFromToken(sc), AuditAction.Load,
                "application(s)");

        return getAccessProfile(roleTypeId);

    }

    private Response getAccessProfile(String roleTypeId) throws Exception {

        List<JsonUserAccessProfile> userProfiles = new ArrayList<>();
        List<JsonRoleTypeAccessProfile> roleProfiles = RoleTypeAccessProfileEntity.getRoleAccessProfiles(roleTypeId);

        for (JsonRoleTypeAccessProfile profile : roleProfiles) {
            JsonUserAccessProfile applicationProfile = userProfiles.stream().filter(app -> app.getApplicationId().equals(profile.getApplicationId())).findFirst().orElse(new JsonUserAccessProfile());
            if (applicationProfile.getApplicationId() == null) {
                applicationProfile.setApplicationId(profile.getApplicationId());
                applicationProfile.setApplicationName(profile.getApplication());
                if (!applicationProfile.isCanAccessData()) {  //Only check if false...once we get a positive, move on
                    applicationProfile.setCanAccessData(checkForDataAccess(profile.getProfileTree()));
                }
                userProfiles.add(applicationProfile);
            }
            applicationProfile.addRoleTypeAccessProfile(processAccessProfile(profile));

        }

        return Response
                .ok()
                .entity(userProfiles)
                .build();
    }

    private boolean checkForDataAccess(String profileTree) throws Exception {
        JsonNode profileNode = null;
        if (!StringUtils.isNullOrEmpty(profileTree)) {
            profileNode = ObjectMapperPool.getInstance().readTree(profileTree);
            boolean accessToData = profileNode.get("accessToData").asBoolean();

            return accessToData;

        } else {
            return false;
        }
    }

    private JsonRoleTypeAccessProfile processAccessProfile(JsonRoleTypeAccessProfile profile) throws Exception {
        if (!StringUtils.isNullOrEmpty(profile.getProfileTree())) {
            JsonNode profileTreeNode = ObjectMapperPool.getInstance().readTree(profile.getProfileTree());
            if (profileTreeNode.get("accessToData").asBoolean()) {
                //get sharing agreement and insert it into the json
            }
            return profile;
        } else
            return profile;
    }
}
