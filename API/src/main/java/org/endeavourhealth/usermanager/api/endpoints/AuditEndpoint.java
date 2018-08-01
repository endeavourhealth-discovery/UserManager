package org.endeavourhealth.usermanager.api.endpoints;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.annotation.Timed;
import io.astefanutti.metrics.aspectj.Metrics;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.endeavourhealth.common.security.SecurityUtils;
import org.endeavourhealth.core.data.audit.UserAuditRepository;
import org.endeavourhealth.core.data.audit.models.AuditAction;
import org.endeavourhealth.core.data.audit.models.AuditModule;
import org.endeavourhealth.coreui.endpoints.AbstractEndpoint;
import org.endeavourhealth.usermanager.api.metrics.UserManagerMetricListener;
import org.endeavourhealth.usermanagermodel.models.database.AuditEntity;
import org.endeavourhealth.usermanagermodel.models.json.JsonAuditDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Path("/audit")
@Metrics(registry = "UserManagerRegistry")
@Api(description = "API endpoint related to the audits.")
public class AuditEndpoint extends AbstractEndpoint {
    private static final Logger LOG = LoggerFactory.getLogger(UserEndpoint.class);

    private static final UserAuditRepository userAudit = new UserAuditRepository(AuditModule.EdsUiModule.User);
    private static final MetricRegistry metricRegistry = UserManagerMetricListener.userManagerMetricRegistry;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="UserManager.AuditEndpoint.getAudit")
    @Path("/getAudit")
    @ApiOperation(value = "Returns a list of audit entries")
    public Response getAudit(@Context SecurityContext sc) throws Exception {

        super.setLogbackMarkers(sc);
        userAudit.save(SecurityUtils.getCurrentUserId(sc), getOrganisationUuidFromToken(sc), AuditAction.Load,
                "Organisation(s)");

        return getAuditEntries();

    }

    private Response getAuditEntries() throws Exception {
        List<Object[]> queryResults = AuditEntity.getAudit();

        List<JsonAuditDetail> auditDetails = new ArrayList<>();

        for (Object[] obj : queryResults) {
            JsonAuditDetail detail = new JsonAuditDetail();
            detail.setId(obj[0].toString());
            detail.setTimestamp((Date)obj[2]);
            detail.setUserRole(obj[1].toString());
            detail.setUserName(obj[5].toString());
            detail.setOrganisation(obj[4].toString());

            auditDetails.add(detail);
        }

        return Response
                .ok()
                .entity(auditDetails)
                .build();

    }


}
