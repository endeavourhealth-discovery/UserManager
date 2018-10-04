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
import org.endeavourhealth.datasharingmanagermodel.models.database.ProjectEntity;
import org.endeavourhealth.usermanager.api.metrics.UserManagerMetricListener;
import org.endeavourhealth.usermanagermodel.models.caching.*;
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
                                  @ApiParam(value = "Organisation id of user role (used to limit the audit results)") @QueryParam("userOrganisationId") String userOrganisationId,
                                  @ApiParam(value = "Optional organisation id")@QueryParam("organisationId") String organisationId,
                                  @ApiParam(value = "Optional user id ")@QueryParam("userId") String userId) throws Exception {

        return getAuditCount(userOrganisationId, organisationId, userId);
    }

    private Response getAuditCount(String userOrganisationId, String organisationId, String userId) throws Exception {
        Long count = AuditEntity.getAuditCount(userOrganisationId, organisationId, userId);

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
            detail.setUserProject(obj[1].toString());
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

            List<String> projects = auditDetails.stream()
                    .map(JsonAuditSummary::getUserProject)
                    .collect(Collectors.toList());

            List<OrganisationEntity> orgList = OrganisationCache.getOrganisationDetails(orgs);

            List<ProjectEntity> projectList = ProjectCache.getProjectDetails(projects);

            for (JsonAuditSummary sum : auditDetails) {
                OrganisationEntity org = orgList.stream().filter(o -> o.getUuid().equals(sum.getOrganisation())).findFirst().orElse(null);
                if (org != null) {
                    sum.setOrganisation(org.getName() + " (" + org.getOdsCode() + ")");
                }

                ProjectEntity proj = projectList.stream().filter(p -> p.getUuid().equals(sum.getUserProject())).findFirst().orElse(null);
                if (proj != null) {
                    sum.setUserProject(proj.getName());
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
            case 0: return getJsonForUserProjectAudit(auditEntity); // Role
            case 1: return getJsonForUserAudit(auditEntity);
            case 2: return getJsonForDelegationAudit(auditEntity);
            case 3: return getJsonForDelegationRelationshipAudit(auditEntity);
            case 4: return getJsonForDefaltRoleChangeAudit(auditEntity);
            case 5: return getJsonForApplicationAudit(auditEntity);
            case 6: return getJsonForApplicationProfileAudit(auditEntity);
            case 7: return getJsonForApplicationPolicyAttributeAudit(auditEntity);
            case 8: return getJsonForUserRegionAudit(auditEntity);
            case 9: return getJsonForUserApplicationPolicyAudit(auditEntity);
            case 10: return getJsonForApplicationPolicyAudit(auditEntity);
            default: throw new Exception("Unknown audit type");
        }

    }

    private Response getJsonForUserAudit(AuditEntity audit) throws Exception {

        return Response
                .ok()
                .entity(audit.getAuditJson())
                .build();
    }

    private Response getJsonForDefaltRoleChangeAudit(AuditEntity audit) throws Exception {

        return Response
                .ok()
                .entity(audit.getAuditJson())
                .build();
    }

    private Response getJsonForUserRegionAudit(AuditEntity audit) throws Exception {

        return Response
                .ok()
                .entity(audit.getAuditJson())
                .build();
    }

    private Response getJsonForUserApplicationPolicyAudit(AuditEntity audit) throws Exception {

        return Response
                .ok()
                .entity(audit.getAuditJson())
                .build();
    }

    private Response getJsonForUserProjectAudit(AuditEntity audit) throws Exception {
        String title = "";
        UserProjectEntity userProject;
        JsonNode beforeJson = null;
        JsonNode afterJson = null;
        if (audit.getAuditType() == 0) {
            title = "Role added";
            userProject = UserProjectEntity.getUserProject(audit.getItemAfter());
            afterJson = generateProjectAuditJson(userProject);
        } else {
            title = "Role deleted";
            userProject = UserProjectEntity.getUserProject(audit.getItemBefore());
            beforeJson = generateProjectAuditJson(userProject);
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

    private JsonNode generateProjectAuditJson(UserProjectEntity userProject) throws Exception {
        UserRepresentation user = UserCache.getUserDetails(userProject.getUserId());
        OrganisationEntity org = OrganisationCache.getOrganisationDetails(userProject.getOrganisationId());
        ProjectEntity project = ProjectCache.getProjectDetails(userProject.getProjectId());

        ObjectMapper mapper = new ObjectMapper();
        JsonNode auditJson = mapper.createObjectNode();

        ((ObjectNode)auditJson).put("id", userProject.getId());
        if (user != null) {
            ((ObjectNode)auditJson).put("userId", user.getUsername());
        } else {
            ((ObjectNode)auditJson).put("userId","Unknown user");
        }
        ((ObjectNode)auditJson).put("project", project.getName());
        ((ObjectNode)auditJson).put("organisation", org.getName() + " (" + org.getOdsCode() + ")");

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

    private JsonNode generateDelegationAuditJson(DelegationEntity delegation) throws Exception {
        OrganisationEntity rootOrg = OrganisationCache.getOrganisationDetails(delegation.getRootOrganisation());

        ObjectMapper mapper = new ObjectMapper();
        JsonNode auditJson = mapper.createObjectNode();
        // https://stackoverflow.com/questions/40967921/create-json-object-using-jackson-in-java
        ((ObjectNode)auditJson).put("id", delegation.getUuid());
        ((ObjectNode)auditJson).put("name", delegation.getName());
        ((ObjectNode)auditJson).put("rootOrganisation", rootOrg.getName() + " (" + rootOrg.getOdsCode() + ")");

        return auditJson;
    }

    private Response getJsonForApplicationAudit(AuditEntity audit) throws Exception {
        String title = "";
        ApplicationEntity applicationBefore;
        ApplicationEntity applicationAfter;
        JsonNode beforeJson = null;
        JsonNode afterJson = null;
        if (audit.getAuditType() == 0) {
            title = "Application added";
            applicationAfter = ApplicationEntity.getApplication(audit.getItemAfter());
            afterJson = generateApplicationAuditJson(applicationAfter);
        } else if (audit.getAuditType() == 1) {
            title = "Application edited";
            applicationBefore = ApplicationEntity.getApplication(audit.getItemBefore());
            beforeJson = generateApplicationAuditJson(applicationBefore);
            applicationAfter = ApplicationEntity.getApplication(audit.getItemAfter());
            afterJson = generateApplicationAuditJson(applicationAfter);
        } else {
            title = "Application deleted";
            applicationBefore = ApplicationEntity.getApplication(audit.getItemBefore());
            beforeJson = generateApplicationAuditJson(applicationBefore);
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

    private JsonNode generateApplicationAuditJson(ApplicationEntity application) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode auditJson = mapper.createObjectNode();
        ((ObjectNode)auditJson).put("id", application.getId());
        ((ObjectNode)auditJson).put("name", application.getName());
        ((ObjectNode)auditJson).put("description", application.getDescription());
        ((ObjectNode)auditJson).put("applicationTree", application.getApplicationTree());

        return auditJson;
    }

    private Response getJsonForApplicationProfileAudit(AuditEntity audit) throws Exception {
        String title = "";
        ApplicationAccessProfileEntity applicationProfileBefore;
        ApplicationAccessProfileEntity applicationProfileAfter;
        JsonNode beforeJson = null;
        JsonNode afterJson = null;
        if (audit.getAuditType() == 0) {
            title = "Application profile added";
            applicationProfileAfter = ApplicationAccessProfileEntity.getApplicationProfile(audit.getItemAfter());
            afterJson = generateApplicationProfileAuditJson(applicationProfileAfter);
        } else if (audit.getAuditType() == 1) {
            title = "Application profile edited";
            applicationProfileBefore = ApplicationAccessProfileEntity.getApplicationProfile(audit.getItemBefore());
            beforeJson = generateApplicationProfileAuditJson(applicationProfileBefore);
            applicationProfileAfter = ApplicationAccessProfileEntity.getApplicationProfile(audit.getItemAfter());
            afterJson = generateApplicationProfileAuditJson(applicationProfileAfter);
        } else {
            title = "Application profile deleted";
            applicationProfileBefore = ApplicationAccessProfileEntity.getApplicationProfile(audit.getItemBefore());
            beforeJson = generateApplicationProfileAuditJson(applicationProfileBefore);
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

    private JsonNode generateApplicationProfileAuditJson(ApplicationAccessProfileEntity applicationProfile) throws Exception {

        ApplicationEntity applicationEntity = ApplicationCache.getApplicationDetails(applicationProfile.getApplicationId());
        ObjectMapper mapper = new ObjectMapper();
        JsonNode auditJson = mapper.createObjectNode();
        ((ObjectNode)auditJson).put("id", applicationProfile.getId());
        ((ObjectNode)auditJson).put("name", applicationProfile.getName());
        ((ObjectNode)auditJson).put("description", applicationProfile.getDescription());
        ((ObjectNode)auditJson).put("applicationName", applicationEntity.getName());
        ((ObjectNode)auditJson).put("profileTree", applicationProfile.getProfileTree());

        return auditJson;
    }

    private Response getJsonForApplicationPolicyAttributeAudit(AuditEntity audit) throws Exception {
        String title = "";
        ApplicationPolicyAttributeEntity accessProfileBefore;
        ApplicationPolicyAttributeEntity accessProfileAfter;
        JsonNode beforeJson = null;
        JsonNode afterJson = null;
        if (audit.getAuditType() == 0) {
            title = "Application policy attribute added";
            accessProfileAfter = ApplicationPolicyAttributeEntity.getRoleTypeAccessProfile(audit.getItemAfter());
            afterJson = generateApplicationPolicyAttributeAuditJson(accessProfileAfter);
        } else if (audit.getAuditType() == 1) {
            title = "Application policy attribute edited";
            accessProfileBefore = ApplicationPolicyAttributeEntity.getRoleTypeAccessProfile(audit.getItemBefore());
            beforeJson = generateApplicationPolicyAttributeAuditJson(accessProfileBefore);
            accessProfileAfter = ApplicationPolicyAttributeEntity.getRoleTypeAccessProfile(audit.getItemAfter());
            afterJson = generateApplicationPolicyAttributeAuditJson(accessProfileAfter);
        } else {
            title = "Application policy attribute deleted";
            accessProfileBefore = ApplicationPolicyAttributeEntity.getRoleTypeAccessProfile(audit.getItemBefore());
            beforeJson = generateApplicationPolicyAttributeAuditJson(accessProfileBefore);
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

    private JsonNode generateApplicationPolicyAttributeAuditJson(ApplicationPolicyAttributeEntity accessProfileEntity) throws Exception {

        ApplicationPolicyEntity applicationPolicyEntity = RoleTypeCache.getRoleDetails(accessProfileEntity.getApplicationPolicyId());
        ApplicationAccessProfileEntity profileEntity = ApplicationProfileCache.getApplicationProfileDetails(accessProfileEntity.getApplicationAccessProfileId());

        ObjectMapper mapper = new ObjectMapper();
        JsonNode auditJson = mapper.createObjectNode();
        ((ObjectNode)auditJson).put("id", accessProfileEntity.getId());
        ((ObjectNode)auditJson).put("applicationPolicyName", applicationPolicyEntity.getName());
        ((ObjectNode)auditJson).put("attributeName", profileEntity.getName());

        return auditJson;
    }

    private Response getJsonForApplicationPolicyAudit(AuditEntity audit) throws Exception {
        String title = "";
        ApplicationPolicyEntity accessPolicyBefore;
        ApplicationPolicyEntity accessPolicyAfter;
        JsonNode beforeJson = null;
        JsonNode afterJson = null;
        if (audit.getAuditType() == 0) {
            title = "Application policy added";
            accessPolicyAfter = ApplicationPolicyEntity.getApplicationPolicy(audit.getItemAfter());
            afterJson = generateApplicationPolicyAuditJson(accessPolicyAfter);
        } else if (audit.getAuditType() == 1) {
            title = "Application policy edited";
            accessPolicyBefore = ApplicationPolicyEntity.getApplicationPolicy(audit.getItemBefore());
            beforeJson = generateApplicationPolicyAuditJson(accessPolicyBefore);
            accessPolicyAfter = ApplicationPolicyEntity.getApplicationPolicy(audit.getItemAfter());
            afterJson = generateApplicationPolicyAuditJson(accessPolicyAfter);
        } else {
            title = "Application policy deleted";
            accessPolicyBefore = ApplicationPolicyEntity.getApplicationPolicy(audit.getItemBefore());
            beforeJson = generateApplicationPolicyAuditJson(accessPolicyBefore);
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

    private JsonNode generateApplicationPolicyAuditJson(ApplicationPolicyEntity policyEntity) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode auditJson = mapper.createObjectNode();
        ((ObjectNode)auditJson).put("id", policyEntity.getId());
        ((ObjectNode)auditJson).put("applicationPolicyName", policyEntity.getName());
        ((ObjectNode)auditJson).put("applicationPolicyDescription", policyEntity.getDescription());

        return auditJson;
    }

    private Response getJsonForDelegationAudit(AuditEntity audit) throws Exception {
        String title = "";
        DelegationEntity delegationBefore;
        DelegationEntity delegationAfter;
        JsonNode beforeJson = null;
        JsonNode afterJson = null;
        if (audit.getAuditType() == 0) {
            title = "Delegation added";
            delegationAfter = DelegationEntity.getDelegation(audit.getItemAfter());
            afterJson = generateDelegationAuditJson(delegationAfter);
        } else {
            title = "Delegation deleted";
            delegationBefore = DelegationEntity.getDelegation(audit.getItemBefore());
            beforeJson = generateDelegationAuditJson(delegationBefore);
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
