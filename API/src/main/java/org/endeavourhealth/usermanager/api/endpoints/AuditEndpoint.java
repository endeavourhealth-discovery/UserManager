package org.endeavourhealth.usermanager.api.endpoints;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.astefanutti.metrics.aspectj.Metrics;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.endeavourhealth.common.security.SecurityUtils;
import org.endeavourhealth.core.data.audit.UserAuditRepository;
import org.endeavourhealth.core.data.audit.models.AuditAction;
import org.endeavourhealth.core.data.audit.models.AuditModule;
import org.endeavourhealth.coreui.endpoints.AbstractEndpoint;
import org.endeavourhealth.usermanager.api.DAL.AuditDAL;
import org.endeavourhealth.usermanager.api.logic.AuditLogic;
import org.endeavourhealth.usermanager.api.metrics.UserManagerMetricListener;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@Path("/audit")
@Metrics(registry = "UserManagerRegistry")
@Api(value = "Audit", description = "API endpoint related to the audits.")
public class AuditEndpoint extends AbstractEndpoint {
    private static final Logger LOG = LoggerFactory.getLogger(AuditEndpoint.class);

    private static final UserAuditRepository userAudit = new UserAuditRepository(AuditModule.EdsUiModule.User);
    private static final MetricRegistry metricRegistry = UserManagerMetricListener.userManagerMetricRegistry;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="UserManager.AuditEndpoint.getAudit")
    @Path("/getAudit")
    @ApiOperation(value = "Returns a list of audit entries")
    public Response getAudit(@Context SecurityContext sc,
                             @ApiParam(value = "Organisation id of user role (used to limit the audit results)") @QueryParam("userOrganisationId") String userOrganisationId,
                             @ApiParam(value = "Optional page number (defaults to 1 if not provided)") @QueryParam("pageNumber") Integer pageNumber,
                             @ApiParam(value = "Optional page size (defaults to 20 if not provided)")@QueryParam("pageSize") Integer pageSize,
                             @ApiParam(value = "Optional organisation id")@QueryParam("organisationId") String organisationId,
                             @ApiParam(value = "Optional user id ")@QueryParam("userId") String userId,
                             @ApiParam(value = "Optional date from ")@QueryParam("dateFrom") String dateFrom,
                             @ApiParam(value = "Optional date to ")@QueryParam("dateTo") String dateTo) throws Exception {

        super.setLogbackMarkers(sc);
        userAudit.save(SecurityUtils.getCurrentUserId(sc), getOrganisationUuidFromToken(sc), AuditAction.Load,
                "Organisation(s)");

        if (pageNumber == null) {
            pageNumber = 1;
        }
        if (pageSize == null) {
            pageSize = 20;
        }

        Timestamp timestampFrom = null;
        Timestamp timestampTo = null;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        if (dateFrom != null) {
            /*Date startDate = new Date(dateFrom);
            timestampFrom = new Timestamp(startDate.getTime());*/
            Date parsedDateFrom = dateFormat.parse(dateFrom);
            timestampFrom = new Timestamp(parsedDateFrom.getTime());
        }

        if (dateTo != null) {
            Date parsedDateTo = dateFormat.parse(dateTo);
            timestampTo = new Timestamp(parsedDateTo.getTime());
        }

        return new AuditLogic().getAuditEntries(userOrganisationId, pageNumber, pageSize, organisationId, userId, timestampFrom, timestampTo);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="UserManager.AuditEndpoint.getAuditDetail")
    @Path("/getAuditDetail")
    @ApiOperation(value = "Returns a list of audit entries")
    public Response getAuditDetail(@Context SecurityContext sc,
                                   @ApiParam(value = "Audit Id") @QueryParam("auditId") String auditId) throws Exception {

        super.setLogbackMarkers(sc);
        userAudit.save(SecurityUtils.getCurrentUserId(sc), getOrganisationUuidFromToken(sc), AuditAction.Load,
                "Organisation(s)");

        return new AuditLogic().getAuditDetails(auditId);

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="UserManager.AuditEndpoint.auditCount")
    @Path("/auditCount")
    @ApiOperation(value = "When using server side pagination, this returns the total count of the results of the query")
    public Response getAuditCount(@Context SecurityContext sc,
                                  @ApiParam(value = "Organisation id of user role (used to limit the audit results)") @QueryParam("userOrganisationId") String userOrganisationId,
                                  @ApiParam(value = "Optional organisation id")@QueryParam("organisationId") String organisationId,
                                  @ApiParam(value = "Optional user id ")@QueryParam("userId") String userId) throws Exception {

        Long count = new AuditDAL().getAuditCount(userOrganisationId, organisationId, userId);

        return Response
                .ok()
                .entity(count)
                .build();
    }
}
