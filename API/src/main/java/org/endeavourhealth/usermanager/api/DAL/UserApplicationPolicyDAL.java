package org.endeavourhealth.usermanager.api.DAL;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.endeavourhealth.common.security.usermanagermodel.models.ConnectionManager;
import org.endeavourhealth.common.security.usermanagermodel.models.caching.ApplicationPolicyCache;
import org.endeavourhealth.common.security.usermanagermodel.models.caching.UserCache;
import org.endeavourhealth.common.security.usermanagermodel.models.database.ApplicationPolicyEntity;
import org.endeavourhealth.common.security.usermanagermodel.models.database.UserApplicationPolicyEntity;
import org.endeavourhealth.common.security.usermanagermodel.models.json.JsonUserApplicationPolicy;
import org.endeavourhealth.uiaudit.dal.UIAuditJDBCDAL;
import org.endeavourhealth.uiaudit.enums.AuditAction;
import org.endeavourhealth.uiaudit.enums.ItemType;
import org.keycloak.representations.idm.UserRepresentation;

import javax.persistence.EntityManager;

public class UserApplicationPolicyDAL {

    public static void saveUserApplicationPolicyId(JsonUserApplicationPolicy userApplicationPolicy, String userProjectId) throws Exception {

        UserApplicationPolicyEntity oldPolicy = UserCache.getUserApplicationPolicy(userApplicationPolicy.getUserId());

        if (oldPolicy != null) {
            if (oldPolicy.getApplicationPolicyId().equals(userApplicationPolicy.getApplicationPolicyId())) {
                // application policy hasnt changed so don't save
                return;
            }
        }

        EntityManager entityManager = ConnectionManager.getUmEntityManager();

        try {
            entityManager.getTransaction().begin();
            UserApplicationPolicyEntity userApplicationPolicyEntity = new UserApplicationPolicyEntity();
            userApplicationPolicyEntity.setUserId(userApplicationPolicy.getUserId());
            userApplicationPolicyEntity.setApplicationPolicyId(userApplicationPolicy.getApplicationPolicyId());
            entityManager.merge(userApplicationPolicyEntity);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            throw e;
        } finally {
            entityManager.close();
        }

        UserCache.clearUserCache(userApplicationPolicy.getUserId());

        String auditJson = getAuditJsonForApplicationPolicyChange(oldPolicy, new UserApplicationPolicyEntity(userApplicationPolicy));

        new UIAuditJDBCDAL().addToAuditTrail(userProjectId,
                AuditAction.EDIT, ItemType.USER_APPLICATION_POLICY, null, null, auditJson);

    }

    private static String getAuditJsonForApplicationPolicyChange(UserApplicationPolicyEntity oldPolicy, UserApplicationPolicyEntity newPolicy) throws Exception {
        JsonNode beforeJson = null;
        if (oldPolicy != null) {
            beforeJson = generateAppplicationPolicyChangeJson(oldPolicy);
        }
        JsonNode afterJson = generateAppplicationPolicyChangeJson(newPolicy);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.createObjectNode();

        ((ObjectNode)rootNode).put("title", "Application policy changed");

        if (afterJson != null) {
            ((ObjectNode) rootNode).set("after", afterJson);
        }

        if (beforeJson != null) {
            ((ObjectNode) rootNode).set("before", beforeJson);
        }

        return prettyPrintJsonString(rootNode);
    }

    private static JsonNode generateAppplicationPolicyChangeJson(UserApplicationPolicyEntity userApplicationPolicyEntity) throws Exception {
        UserRepresentation user = UserCache.getUserDetails(userApplicationPolicyEntity.getUserId());
        ApplicationPolicyEntity policy = ApplicationPolicyCache.getApplicationPolicyDetails(userApplicationPolicyEntity.getApplicationPolicyId());

        ObjectMapper mapper = new ObjectMapper();
        JsonNode auditJson = mapper.createObjectNode();

        ((ObjectNode)auditJson).put("user", user.getUsername());
        ((ObjectNode)auditJson).put("applicationPolicy", policy.getName());

        return auditJson;
    }

    private static String prettyPrintJsonString(JsonNode jsonNode) throws Exception {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Object json = mapper.readValue(jsonNode.toString(), Object.class);
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
        } catch (Exception e) {
            throw new Exception("Converting Json to String failed : " + e.getMessage() );
        }
    }
}
