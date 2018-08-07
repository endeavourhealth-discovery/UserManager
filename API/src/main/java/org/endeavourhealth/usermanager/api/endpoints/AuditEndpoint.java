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
import org.endeavourhealth.common.security.keycloak.client.KeycloakAdminClient;
import org.endeavourhealth.core.data.audit.UserAuditRepository;
import org.endeavourhealth.core.data.audit.models.AuditAction;
import org.endeavourhealth.core.data.audit.models.AuditModule;
import org.endeavourhealth.coreui.endpoints.AbstractEndpoint;
import org.endeavourhealth.datasharingmanagermodel.models.database.OrganisationEntity;
import org.endeavourhealth.usermanager.api.metrics.UserManagerMetricListener;
import org.endeavourhealth.usermanagermodel.models.database.AuditEntity;
import org.endeavourhealth.usermanagermodel.models.database.UserRoleEntity;
import org.endeavourhealth.usermanagermodel.models.json.JsonAuditSummary;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

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
    public Response getAudit(@Context SecurityContext sc,
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

        /*System.out.println(timestampFrom);
        System.out.println(timestampTo);*/
        return getAuditEntries(pageNumber, pageSize, organisationId, userId, timestampFrom, timestampTo);

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

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="UserManager.AuditEndpoint.auditCount")
    @Path("/auditCount")
    @ApiOperation(value = "When using server side pagination, this returns the total count of the results of the query")
    public Response getAuditCount(@Context SecurityContext sc,
                                  @ApiParam(value = "Optional organisation id")@QueryParam("organisationId") String organisationId,
                                  @ApiParam(value = "Optional user id ")@QueryParam("userId") String userId) throws Exception {

        return getAuditCount(organisationId, userId);
    }

    private Response getAuditCount(String organisationId, String userId) throws Exception {
        Long count = AuditEntity.getAuditCount(organisationId, userId);

        return Response
                .ok()
                .entity(count)
                .build();
    }

    private Response getAuditEntries(Integer pageNumber, Integer pageSize,
                                     String organisationId, String userId,
                                     Timestamp startDate, Timestamp endDate) throws Exception {
        List<Object[]> queryResults = AuditEntity.getAudit(pageNumber, pageSize, organisationId, userId, startDate, endDate);

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

        if (!queryResults.isEmpty()) {

            List<String> orgs = auditDetails.stream()
                    .map(JsonAuditSummary::getOrganisation)
                    .collect(Collectors.toList());

            List<OrganisationEntity> orgList = OrganisationEntity.getOrganisationsFromList(orgs);

            Map<String, String> userNameMap = new HashMap<>();


            KeycloakAdminClient keycloakClient = new KeycloakAdminClient();

            for (JsonAuditSummary sum : auditDetails) {
                OrganisationEntity org = orgList.stream().filter(o -> o.getUuid().equals(sum.getOrganisation())).findFirst().orElse(null);
                if (org != null) {
                    sum.setOrganisation(org.getName() + " (" + org.getOdsCode() + ")");
                }

                if (userNameMap.containsKey(sum.getUserName())) {
                    sum.setUserName(userNameMap.get(sum.getUserName()));
                } else {
                    try {
                        UserRepresentation user = keycloakClient.realms().users().getUser(sum.getUserName());

                        if (user != null) {
                            userNameMap.put(user.getId(), user.getUsername());
                            sum.setUserName(user.getUsername());
                        }
                    } catch (Exception e) {
                        userNameMap.put(sum.getUserName(), "Unknown User");
                        sum.setUserName("Unknown User");
                    }
                }
            }
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
