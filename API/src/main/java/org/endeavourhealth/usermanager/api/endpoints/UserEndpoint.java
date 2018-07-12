package org.endeavourhealth.usermanager.api.endpoints;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.annotation.Timed;
import io.astefanutti.metrics.aspectj.Metrics;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.endeavourhealth.common.security.keycloak.client.KeycloakAdminClient;
import org.endeavourhealth.core.data.audit.UserAuditRepository;
import org.endeavourhealth.core.data.audit.models.AuditAction;
import org.endeavourhealth.core.data.audit.models.AuditModule;
import org.endeavourhealth.coreui.endpoints.AbstractEndpoint;
import org.endeavourhealth.coreui.json.JsonEndUser;
import org.endeavourhealth.usermanager.api.metrics.UserManagerMetricListener;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import java.util.List;

import static org.endeavourhealth.common.security.SecurityUtils.getCurrentUserId;

@Path("/user")
@Metrics(registry = "UserManagerRegistry")
@Api(description = "API endpoint related to the users.")
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
                             @ApiParam(value = "Optional search term") @QueryParam("searchData") String searchData) throws Exception {
        super.setLogbackMarkers(sc);

        userAudit.save(getCurrentUserId(sc), getOrganisationUuidFromToken(sc), AuditAction.Load,
                "Users", "Search Data", searchData);

        LOG.trace("getUsers");

        List<JsonEndUser> userList = new ArrayList<>();
        List<UserRepresentation> users;

        KeycloakAdminClient keycloakClient = new KeycloakAdminClient();

        if (searchData == null) {
            users = keycloakClient.realms().users().getUsers("", 0, 100);
        } else {
            users = keycloakClient.realms().users().getUsers(searchData, 0, 100);
        }

        //Add as Json
        for (UserRepresentation user : users) {
            userList.add(new JsonEndUser(user));
        }

        AbstractEndpoint.clearLogbackMarkers();
        return Response
                .ok()
                .entity(userList)
                .build();
    }

}
