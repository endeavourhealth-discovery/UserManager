package org.endeavourhealth.usermanager.api.DAL;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.endeavourhealth.core.database.dal.usermanager.caching.RegionCache;
import org.endeavourhealth.core.database.dal.usermanager.caching.UserCache;
import org.endeavourhealth.core.database.dal.usermanager.models.JsonUserRegion;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.datasharingmanager.models.RegionEntity;
import org.endeavourhealth.core.database.rdbms.usermanager.models.UserRegionEntity;
import org.endeavourhealth.uiaudit.dal.UIAuditJDBCDAL;
import org.endeavourhealth.uiaudit.enums.AuditAction;
import org.endeavourhealth.uiaudit.enums.ItemType;
import org.keycloak.representations.idm.UserRepresentation;

import javax.persistence.EntityManager;

public class UserRegionDAL {

    public static void saveUserRegion(JsonUserRegion userRegion, String userProjectId) throws Exception {
        UserRegionEntity oldRegion = UserCache.getUserRegion(userRegion.getUserId());

        if (oldRegion != null) {
            if (oldRegion.getRegionId().equals(userRegion.getRegionId())) {
                // region hasnt changed so don't save
                return;
            }
        }

        EntityManager entityManager = ConnectionManager.getUmEntityManager();

        try {
            entityManager.getTransaction().begin();
            UserRegionEntity userRegionEntity = new UserRegionEntity();
            userRegionEntity.setUserId(userRegion.getUserId());
            userRegionEntity.setRegionId(userRegion.getRegionId());
            entityManager.merge(userRegionEntity);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            throw e;
        } finally {
            entityManager.close();
        }

        UserCache.clearUserCache(userRegion.getUserId());

        String auditJson = getAuditJsonForRegionChange(oldRegion, new UserRegionEntity(userRegion));

        new UIAuditJDBCDAL().addToAuditTrail(userProjectId,
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

        ((ObjectNode)rootNode).put("title", "User region changed");

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
