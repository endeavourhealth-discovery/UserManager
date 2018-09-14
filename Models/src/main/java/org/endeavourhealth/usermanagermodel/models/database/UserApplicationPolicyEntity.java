package org.endeavourhealth.usermanagermodel.models.database;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.endeavourhealth.usermanagermodel.PersistenceManager;
import org.endeavourhealth.usermanagermodel.models.caching.ApplicationPolicyCache;
import org.endeavourhealth.usermanagermodel.models.caching.UserCache;
import org.endeavourhealth.usermanagermodel.models.enums.AuditAction;
import org.endeavourhealth.usermanagermodel.models.enums.ItemType;
import org.endeavourhealth.usermanagermodel.models.json.JsonUserApplicationPolicy;
import org.keycloak.representations.idm.UserRepresentation;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "user_application_policy", schema = "user_manager")
public class UserApplicationPolicyEntity {
    private String userId;
    private String applicationPolicyId;

    @Id
    @Column(name = "user_id")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Basic
    @Column(name = "application_policy_id")
    public String getApplicationPolicyId() {
        return applicationPolicyId;
    }

    public void setApplicationPolicyId(String applicationPolicyId) {
        this.applicationPolicyId = applicationPolicyId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserApplicationPolicyEntity that = (UserApplicationPolicyEntity) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(applicationPolicyId, that.applicationPolicyId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(userId, applicationPolicyId);
    }

    public UserApplicationPolicyEntity() {
    }

    public UserApplicationPolicyEntity(JsonUserApplicationPolicy jsonUserApplicationPolicy) {
        this.userId = jsonUserApplicationPolicy.getUserId();
        this.applicationPolicyId = jsonUserApplicationPolicy.getApplicationPolicyId();
    }

    public static UserApplicationPolicyEntity getUserApplicationPolicyId(String userId) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        UserApplicationPolicyEntity ret = entityManager.find(UserApplicationPolicyEntity.class, userId);

        entityManager.close();

        return ret;
    }

    public static void saveUserApplicationPolicyId(JsonUserApplicationPolicy userApplicationPolicy, String userProjectId) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        UserApplicationPolicyEntity oldPolicy = UserApplicationPolicyEntity.getUserApplicationPolicyId(userApplicationPolicy.getUserId());

        if (oldPolicy != null) {
            if (oldPolicy.applicationPolicyId.equals(userApplicationPolicy.getApplicationPolicyId())) {
                // application policy hasnt changed so don't save
                return;
            }
        }

        entityManager.getTransaction().begin();
        UserApplicationPolicyEntity userApplicationPolicyEntity = new UserApplicationPolicyEntity();
        userApplicationPolicyEntity.setUserId(userApplicationPolicy.getUserId());
        userApplicationPolicyEntity.setApplicationPolicyId(userApplicationPolicy.getApplicationPolicyId());
        entityManager.merge(userApplicationPolicyEntity);
        entityManager.getTransaction().commit();

        entityManager.close();

        String auditJson = getAuditJsonForApplicationPolicyChange(oldPolicy, new UserApplicationPolicyEntity(userApplicationPolicy));

        AuditEntity.addToAuditTrail(userProjectId,
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
