package org.endeavourhealth.usermanager.api.endpoints;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.core.ObjectCodec;
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
import org.endeavourhealth.usermanager.api.metrics.UserManagerMetricListener;
import org.endeavourhealth.usermanagermodel.models.database.AuditEntity;
import org.endeavourhealth.usermanagermodel.models.database.UserRoleEntity;
import org.endeavourhealth.usermanagermodel.models.json.JsonAuditSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.keycloak.util.JsonSerialization.mapper;

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

        return getAuditDetails(auditId);

    }

    private Response getAuditEntries() throws Exception {
        List<Object[]> queryResults = AuditEntity.getAudit();

        List<JsonAuditSummary> auditDetails = new ArrayList<>();

        for (Object[] obj : queryResults) {
            JsonAuditSummary detail = new JsonAuditSummary();
            detail.setId(obj[0].toString());
            Date auditTime = (Date)obj[2];
            DateFormat df = new SimpleDateFormat("dd/MM/YYYY hh:mm:ss");
            detail.setTimestamp(df.format(auditTime));
            detail.setUserRole(obj[1].toString());
            detail.setUserName(obj[5].toString());
            detail.setOrganisation(obj[4].toString());
            detail.setAuditAction(obj[6].toString());
            detail.setItemType(obj[7].toString());

            auditDetails.add(detail);
        }

        return Response
                .ok()
                .entity(auditDetails)
                .build();

    }

    private Response getAuditDetails(String auditId) throws Exception {
        AuditEntity auditEntity = AuditEntity.getAuditDetail(auditId);

        switch (auditEntity.getItemType()) {
            case 0: return getJsonForRoleAudit(auditEntity); // Role
            default: throw new Exception("Unknown audit type");
        }

    }

    private Response getJsonForRoleAudit(AuditEntity audit) throws Exception {
        String title = "";
        UserRoleEntity role;
        if (audit.getAuditType() == 0) {
            title = "Role Added";
            role = UserRoleEntity.getUserRole(audit.getItemAfter());
        } else {
            title = "Role Deleted";
            role = UserRoleEntity.getUserRole(audit.getItemBefore());
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode auditJson = mapper.createObjectNode();
        // https://stackoverflow.com/questions/40967921/create-json-object-using-jackson-in-java
        ((ObjectNode)auditJson).put("title", title);
        ((ObjectNode)auditJson).put("id", role.getId());
        ((ObjectNode)auditJson).put("userId", role.getUserId());
        ((ObjectNode)auditJson).put("roleType", role.getRoleTypeId());
        ((ObjectNode)auditJson).put("organisation", role.getOrganisationId());
        ((ObjectNode)auditJson).put("accessProfile", role.getUserAccessProfileId());

        return Response
                .ok()
                .entity(auditJson)
                .build();
    }


}
