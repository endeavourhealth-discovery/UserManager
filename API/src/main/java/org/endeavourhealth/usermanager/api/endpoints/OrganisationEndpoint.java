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
import org.endeavourhealth.usermanager.api.metrics.UserManagerMetricListener;
import org.endeavourhealth.usermanagermodel.models.database.UserRoleEntity;
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
import java.util.stream.Collectors;

@Path("/organisation")
@Metrics(registry = "UserManagerRegistry")
@Api(value = "Organisation", description = "API endpoint related to the organisations.")
public class OrganisationEndpoint extends AbstractEndpoint {
    private static final Logger LOG = LoggerFactory.getLogger(OrganisationEndpoint.class);

    private static final UserAuditRepository userAudit = new UserAuditRepository(AuditModule.EdsUiModule.User);
    private static final MetricRegistry metricRegistry = UserManagerMetricListener.userManagerMetricRegistry;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="UserManager.OrganisationEndpoint.getOrganisation")
    @Path("/search")
    @ApiOperation(value = "Returns a list of organisations based on a search term")
    public Response getOrganisation(@Context SecurityContext sc,
                                    @ApiParam(value = "Optional search term") @QueryParam("searchData") String searchData) throws Exception {

        super.setLogbackMarkers(sc);
        userAudit.save(SecurityUtils.getCurrentUserId(sc), getOrganisationUuidFromToken(sc), AuditAction.Load,
                "Organisation(s)",
                "SearchData", searchData);

        return searchOrganisations(searchData, SecurityUtils.getCurrentUserId(sc));

    }

   /* @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="UserManager.OrganisationEndpoint.organisationsForUser")
    @Path("/organisationsForUser")
    @ApiOperation(value = "Returns a list of organisations a user is assigned to")
    public Response organisationsForUser(@Context SecurityContext sc,
                                    @ApiParam(value = "User Id") @QueryParam("userId") String userId) throws Exception {

        super.setLogbackMarkers(sc);
        userAudit.save(SecurityUtils.getCurrentUserId(sc), getOrganisationUuidFromToken(sc), AuditAction.Load,
                "Organisation(s)",
                "UserId", userId);

        return getOrganisationsForUser(userId);

    }*/

    private Response searchOrganisations(String searchData, UUID userId) throws Exception {

        List<OrganisationEntity> organisations = OrganisationEntity.searchOrganisations(searchData, false,
                (byte)0, 1, 50, "name", false, userId);

        clearLogbackMarkers();
        return Response
                .ok()
                .entity(organisations)
                .build();
    }



   /* private static Response getOrganisationsForUser(String userId) throws Exception {
        List<OrganisationEntity> orgList = new ArrayList<>();

        List<UserRoleEntity> userRoles = UserRoleEntity.getUserRoles(userId);

        if (userRoles.size() > 0) {

            List<String> organisations = userRoles.stream()
                    .map(UserRoleEntity::getOrganisationId)
                    .collect(Collectors.toList());

            orgList = OrganisationEntity.getOrganisationsFromList(organisations);

        }

        clearLogbackMarkers();
        return Response
                .ok()
                .entity(orgList)
                .build();
    }*/
}
