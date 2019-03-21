package org.endeavourhealth.usermanager.api.logic;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.endeavourhealth.common.security.datasharingmanagermodel.models.database.OrganisationEntity;
import org.endeavourhealth.common.security.datasharingmanagermodel.models.database.ProjectEntity;
import org.endeavourhealth.common.security.usermanagermodel.models.DAL.SecurityUserProjectDAL;
import org.endeavourhealth.common.security.usermanagermodel.models.caching.*;
import org.endeavourhealth.common.security.usermanagermodel.models.database.*;
import org.endeavourhealth.common.security.usermanagermodel.models.json.JsonAuditSummary;
import org.endeavourhealth.usermanager.api.DAL.*;
import org.keycloak.representations.idm.UserRepresentation;

import javax.ws.rs.core.Response;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.zone.ZoneRules;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class AuditLogic {

    public Response getAuditEntries(String userOrganisationId, Integer pageNumber, Integer pageSize,
                                     String organisationId, String userId,
                                     Timestamp startDate, Timestamp endDate) throws Exception {

        List<Object[]> queryResults = new AuditDAL().getAudit(userOrganisationId, pageNumber, pageSize, organisationId, userId, startDate, endDate);

        List<JsonAuditSummary> auditDetails = new ArrayList<>();

        for (Object[] obj : queryResults) {
            JsonAuditSummary detail = new JsonAuditSummary();
            detail.setId(obj[0].toString());
            Date auditTime = (Date)obj[2];

            ZoneId zone = ZoneId.systemDefault();
            ZoneRules rules = zone.getRules();

            LocalDateTime date = auditTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            ZonedDateTime inDST = ZonedDateTime.of(date, zone);

            if (rules.isDaylightSavings(inDST.toInstant())) {
                date = date.plusHours(1);
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/YYYY HH:mm:ss");
            detail.setTimestamp(date.format(formatter));
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



    public Response getAuditDetails(String auditId) throws Exception {
        AuditEntity auditEntity = new AuditDAL().getAuditDetail(auditId);

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
            title = "Project added";
            userProject = new SecurityUserProjectDAL().getUserProject(audit.getItemAfter());
            afterJson = generateProjectAuditJson(userProject);
        } else {
            title = "Project deleted";
            userProject = new SecurityUserProjectDAL().getUserProject(audit.getItemBefore());
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
            relationshipAfter = new DelegationRelationshipDAL().getDelegationRelationship(audit.getItemAfter());
            afterJson = generateDelegationRelationshipAuditJson(relationshipAfter);
        } else if (audit.getAuditType() == 1) {
            title = "Delegation relationship edited";
            relationshipBefore = new DelegationRelationshipDAL().getDelegationRelationship(audit.getItemBefore());
            beforeJson = generateDelegationRelationshipAuditJson(relationshipBefore);
            relationshipAfter = new DelegationRelationshipDAL().getDelegationRelationship(audit.getItemAfter());
            afterJson = generateDelegationRelationshipAuditJson(relationshipAfter);
        } else {
            title = "Delegation relationship deleted";
            relationshipBefore = new DelegationRelationshipDAL().getDelegationRelationship(audit.getItemBefore());
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
            applicationAfter = ApplicationCache.getApplicationDetails(audit.getItemAfter());
            afterJson = generateApplicationAuditJson(applicationAfter);
        } else if (audit.getAuditType() == 1) {
            title = "Application edited";
            applicationBefore = ApplicationCache.getApplicationDetails(audit.getItemBefore());
            beforeJson = generateApplicationAuditJson(applicationBefore);
            applicationAfter = ApplicationCache.getApplicationDetails(audit.getItemAfter());
            afterJson = generateApplicationAuditJson(applicationAfter);
        } else {
            title = "Application deleted";
            applicationBefore = ApplicationCache.getApplicationDetails(audit.getItemBefore());
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
            applicationProfileAfter = ApplicationProfileCache.getApplicationProfileDetails(audit.getItemAfter());
            afterJson = generateApplicationProfileAuditJson(applicationProfileAfter);
        } else if (audit.getAuditType() == 1) {
            title = "Application profile edited";
            applicationProfileBefore = ApplicationProfileCache.getApplicationProfileDetails(audit.getItemBefore());
            beforeJson = generateApplicationProfileAuditJson(applicationProfileBefore);
            applicationProfileAfter = ApplicationProfileCache.getApplicationProfileDetails(audit.getItemAfter());
            afterJson = generateApplicationProfileAuditJson(applicationProfileAfter);
        } else {
            title = "Application profile deleted";
            applicationProfileBefore = ApplicationProfileCache.getApplicationProfileDetails(audit.getItemBefore());
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
            accessProfileAfter = new ApplicationPolicyAttributeDAL().getRoleTypeAccessProfile(audit.getItemAfter());
            afterJson = generateApplicationPolicyAttributeAuditJson(accessProfileAfter);
        } else if (audit.getAuditType() == 1) {
            title = "Application policy attribute edited";
            accessProfileBefore = new ApplicationPolicyAttributeDAL().getRoleTypeAccessProfile(audit.getItemBefore());
            beforeJson = generateApplicationPolicyAttributeAuditJson(accessProfileBefore);
            accessProfileAfter = new ApplicationPolicyAttributeDAL().getRoleTypeAccessProfile(audit.getItemAfter());
            afterJson = generateApplicationPolicyAttributeAuditJson(accessProfileAfter);
        } else {
            title = "Application policy attribute deleted";
            accessProfileBefore = new ApplicationPolicyAttributeDAL().getRoleTypeAccessProfile(audit.getItemBefore());
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

        ApplicationPolicyEntity applicationPolicyEntity = ApplicationPolicyCache.getApplicationPolicyDetails(accessProfileEntity.getApplicationPolicyId());
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
            accessPolicyAfter = ApplicationPolicyCache.getApplicationPolicyDetails(audit.getItemAfter());
            afterJson = generateApplicationPolicyAuditJson(accessPolicyAfter);
        } else if (audit.getAuditType() == 1) {
            title = "Application policy edited";
            accessPolicyBefore = ApplicationPolicyCache.getApplicationPolicyDetails(audit.getItemBefore());
            beforeJson = generateApplicationPolicyAuditJson(accessPolicyBefore);
            accessPolicyAfter = ApplicationPolicyCache.getApplicationPolicyDetails(audit.getItemAfter());
            afterJson = generateApplicationPolicyAuditJson(accessPolicyAfter);
        } else {
            title = "Application policy deleted";
            accessPolicyBefore = ApplicationPolicyCache.getApplicationPolicyDetails(audit.getItemBefore());
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
            delegationAfter = DelegationCache.getDelegationDetails(audit.getItemAfter());
            afterJson = generateDelegationAuditJson(delegationAfter);
        } else {
            title = "Delegation deleted";
            delegationBefore = DelegationCache.getDelegationDetails(audit.getItemBefore());
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
