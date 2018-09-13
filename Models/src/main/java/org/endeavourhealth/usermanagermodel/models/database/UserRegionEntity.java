package org.endeavourhealth.usermanagermodel.models.database;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.endeavourhealth.datasharingmanagermodel.models.database.RegionEntity;
import org.endeavourhealth.usermanagermodel.PersistenceManager;
import org.endeavourhealth.usermanagermodel.models.caching.RegionCache;
import org.endeavourhealth.usermanagermodel.models.caching.UserCache;
import org.endeavourhealth.usermanagermodel.models.enums.AuditAction;
import org.endeavourhealth.usermanagermodel.models.enums.ItemType;
import org.endeavourhealth.usermanagermodel.models.json.JsonUserRegion;
import org.keycloak.representations.idm.UserRepresentation;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "user_region", schema = "user_manager")
public class UserRegionEntity {
    private String userId;
    private String regionId;

    @Id
    @Column(name = "user_id")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Basic
    @Column(name = "region_id")
    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserRegionEntity that = (UserRegionEntity) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(regionId, that.regionId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(userId, regionId);
    }

    public UserRegionEntity() {
    }

    public UserRegionEntity(JsonUserRegion jsonUserRegion) {
        this.userId = jsonUserRegion.getUserId();
        this.regionId = jsonUserRegion.getRegionId();
    }

    public static UserRegionEntity getUserRegion(String userId) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        UserRegionEntity ret = entityManager.find(UserRegionEntity.class, userId);

        entityManager.close();

        return ret;
    }

    public static void saveUserRegion(JsonUserRegion userRegion, String userProjectId) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        UserRegionEntity oldRegion = UserRegionEntity.getUserRegion(userRegion.getUserId());

        if (oldRegion != null) {
            if (oldRegion.regionId.equals(userRegion.getRegionId())) {
                // region hasnt changed so don't save
                return;
            }
        }

        entityManager.getTransaction().begin();
        UserRegionEntity userRegionEntity = new UserRegionEntity();
        userRegionEntity.setUserId(userRegion.getUserId());
        userRegionEntity.setRegionId(userRegion.getRegionId());
        entityManager.merge(userRegionEntity);
        entityManager.getTransaction().commit();

        entityManager.close();

        String auditJson = getAuditJsonForRegionChange(oldRegion, new UserRegionEntity(userRegion));

        AuditEntity.addToAuditTrail(userProjectId,
                AuditAction.EDIT, ItemType.USER_REGION, null, null, auditJson);

    }

    private static String getAuditJsonForRegionChange(UserRegionEntity oldRegion, UserRegionEntity newRegion) throws Exception {
        JsonNode beforeJson = null;
        if (oldRegion != null) {
            beforeJson = generateRegionChangeJson(oldRegion);
        }
        JsonNode afterJson = generateRegionChangeJson(newRegion);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.createObjectNode();

        ((ObjectNode)rootNode).put("title", "Default project changed");

        if (afterJson != null) {
            ((ObjectNode) rootNode).set("after", afterJson);
        }

        if (beforeJson != null) {
            ((ObjectNode) rootNode).set("before", beforeJson);
        }

        return prettyPrintJsonString(rootNode);
    }

    private static JsonNode generateRegionChangeJson(UserRegionEntity userRegionEntity) throws Exception {
        UserRepresentation user = UserCache.getUserDetails(userRegionEntity.getUserId());
        RegionEntity region = RegionCache.getRegionDetails(userRegionEntity.getRegionId());

        ObjectMapper mapper = new ObjectMapper();
        JsonNode auditJson = mapper.createObjectNode();

        ((ObjectNode)auditJson).put("user", user.getUsername());
        ((ObjectNode)auditJson).put("region", region.getName());

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
