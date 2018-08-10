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
import org.endeavourhealth.datasharingmanagermodel.models.database.OrganisationEntity;
import org.endeavourhealth.usermanager.api.metrics.UserManagerMetricListener;
import org.endeavourhealth.usermanagermodel.models.caching.DelegationCache;
import org.endeavourhealth.usermanagermodel.models.caching.OrganisationCache;
import org.endeavourhealth.usermanagermodel.models.caching.RoleTypeCache;
import org.endeavourhealth.usermanagermodel.models.caching.UserCache;
import org.endeavourhealth.usermanagermodel.models.database.*;
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

        /*System.out.println(timestampFrom);
        System.out.println(timestampTo);*/
        return getAuditEntries(userOrganisationId, pageNumber, pageSize, organisationId, userId, timestampFrom, timestampTo);

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

    private Response getAuditEntries(String userOrganisationId, Integer pageNumber, Integer pageSize,
                                     String organisationId, String userId,
                                     Timestamp startDate, Timestamp endDate) throws Exception {

        List<Object[]> queryResults = AuditEntity.getAudit(userOrganisationId, pageNumber, pageSize, organisationId, userId, startDate, endDate);

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

            List<OrganisationEntity> orgList = OrganisationCache.getOrganisationDetails(orgs);

            for (JsonAuditSummary sum : auditDetails) {
                OrganisationEntity org = orgList.stream().filter(o -> o.getUuid().equals(sum.getOrganisation())).findFirst().orElse(null);
                if (org != null) {
                    sum.setOrganisation(org.getName() + " (" + org.getOdsCode() + ")");
                }

                UserRepresentation user = UserCache.getUserDetails(sum.getUserName());
                if (user != null) {
                    sum.setUserName(user.getUsername());
                } else {
                    sum.setUserName("Unknown user");
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
            case 1: return getJsonForUserAudit(auditEntity);
            case 3: return getJsonForDelegationRelationshipAudit(auditEntity);
            default: throw new Exception("Unknown audit type");
        }

    }

    private Response getJsonForUserAudit(AuditEntity audit) throws Exception {

        return Response
                .ok()
                .entity(audit.getAuditJson())
                .build();
    }

    private Response getJsonForRoleAudit(AuditEntity audit) throws Exception {
        String title = "";
        UserRoleEntity role;
        JsonNode beforeJson = null;
        JsonNode afterJson = null;
        if (audit.getAuditType() == 0) {
            title = "Role added";
            role = UserRoleEntity.getUserRole(audit.getItemAfter());
            afterJson = generateRoleAuditJson(role);
        } else {
            title = "Role deleted";
            role = UserRoleEntity.getUserRole(audit.getItemBefore());
            beforeJson = generateRoleAuditJson(role);
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.createObjectNode();

        ((ObjectNode)rootNode).put("title", title);

        if (afterJson != null) {
            ((ObjectNode) rootNode).set("after", afterJson);
        }

        if (beforeJson != null) {
            ((ObjectNode) rootNode).set("before", beforeJson);
        }


        return Response
                .ok()
                .entity(rootNode)
                .build();
    }

    private JsonNode generateRoleAuditJson(UserRoleEntity role) throws Exception {
        UserRepresentation user = UserCache.getUserDetails(role.getUserId());
        OrganisationEntity org = OrganisationCache.getOrganisationDetails(role.getOrganisationId());
        RoleTypeEntity roleEntity = RoleTypeCache.getRoleDetails(role.getRoleTypeId());

        ObjectMapper mapper = new ObjectMapper();
        JsonNode auditJson = mapper.createObjectNode();
        // https://stackoverflow.com/questions/40967921/create-json-object-using-jackson-in-java
        ((ObjectNode)auditJson).put("id", role.getId());
        if (user != null) {
            ((ObjectNode)auditJson).put("userId", user.getUsername());
        } else {
            ((ObjectNode)auditJson).put("userId","Unknown user");
        }
        ((ObjectNode)auditJson).put("roleType", roleEntity.getName());
        ((ObjectNode)auditJson).put("organisation", org.getName() + " (" + org.getOdsCode() + ")");
        ((ObjectNode)auditJson).put("accessProfile", role.getUserAccessProfileId());

        return auditJson;
    }

    private Response getJsonForDelegationRelationshipAudit(AuditEntity audit) throws Exception {
        String title = "";
        DelegationRelationshipEntity relationshipBefore;
        DelegationRelationshipEntity relationshipAfter;
        JsonNode beforeJson = null;
        JsonNode afterJson = null;
        if (audit.getAuditType() == 0) {
            title = "Delegation relationship added";
            relationshipAfter = DelegationRelationshipEntity.getDelegationRelationship(audit.getItemAfter());
            afterJson = generateDelegationRelationshipAuditJson(relationshipAfter);
        } else if (audit.getAuditType() == 1) {
            title = "Delegation relationship edited";
            relationshipBefore = DelegationRelationshipEntity.getDelegationRelationship(audit.getItemBefore());
            beforeJson = generateDelegationRelationshipAuditJson(relationshipBefore);
            relationshipAfter = DelegationRelationshipEntity.getDelegationRelationship(audit.getItemAfter());
            afterJson = generateDelegationRelationshipAuditJson(relationshipAfter);
        } else {
            title = "Delegation relationship deleted";
            relationshipBefore = DelegationRelationshipEntity.getDelegationRelationship(audit.getItemBefore());
            beforeJson = generateDelegationRelationshipAuditJson(relationshipBefore);
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.createObjectNode();

        ((ObjectNode)rootNode).put("title", title);

        if (afterJson != null) {
            ((ObjectNode) rootNode).set("after", afterJson);
        }

        if (beforeJson != null) {
            ((ObjectNode) rootNode).set("before", beforeJson);
        }


        return Response
                .ok()
                .entity(rootNode)
                .build();
    }

    private JsonNode generateDelegationRelationshipAuditJson(DelegationRelationshipEntity relationship) throws Exception {
        OrganisationEntity childOrg = OrganisationCache.getOrganisationDetails(relationship.getChildUuid());
        OrganisationEntity parentOrg = OrganisationCache.getOrganisationDetails(relationship.getParentUuid());
        DelegationEntity delegation = DelegationCache.getDelegationDetails(relationship.getDelegation());

        ObjectMapper mapper = new ObjectMapper();
        JsonNode auditJson = mapper.createObjectNode();
        // https://stackoverflow.com/questions/40967921/create-json-object-using-jackson-in-java
        ((ObjectNode)auditJson).put("id", relationship.getUuid());
        ((ObjectNode)auditJson).put("delegation", delegation.getName());
        ((ObjectNode)auditJson).put("parentOrg", parentOrg.getName() + " (" + parentOrg.getOdsCode() + ")");
        ((ObjectNode)auditJson).put("childOrg", childOrg.getName() + " (" + childOrg.getOdsCode() + ")");
        ((ObjectNode)auditJson).put("includeAllChildren", relationship.getIncludeAllChildren() == 1);
        ((ObjectNode)auditJson).put("createSuperUsers", relationship.getCreateSuperUsers() == 1);
        ((ObjectNode)auditJson).put("createUsers", relationship.getCreateUsers() == 1);

        return auditJson;
    }

}
