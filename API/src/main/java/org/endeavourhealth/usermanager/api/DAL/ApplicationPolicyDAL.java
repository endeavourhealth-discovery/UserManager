package org.endeavourhealth.usermanager.api.DAL;

import org.endeavourhealth.common.security.usermanagermodel.models.ConnectionManager;
import org.endeavourhealth.common.security.usermanagermodel.models.DAL.SecurityAuditDAL;
import org.endeavourhealth.common.security.usermanagermodel.models.caching.ApplicationPolicyCache;
import org.endeavourhealth.common.security.usermanagermodel.models.database.ApplicationPolicyEntity;
import org.endeavourhealth.common.security.usermanagermodel.models.enums.AuditAction;
import org.endeavourhealth.common.security.usermanagermodel.models.enums.ItemType;
import org.endeavourhealth.common.security.usermanagermodel.models.json.JsonApplicationPolicy;

import javax.persistence.EntityManager;
import java.util.UUID;

public class ApplicationPolicyDAL {

    public void saveApplicationPolicy(JsonApplicationPolicy applicationPolicy, String userRoleId) throws Exception {

        boolean added = false;
        String originalUuid = applicationPolicy.getId();
        if (applicationPolicy.getId() == null) {
            applicationPolicy.setId(UUID.randomUUID().toString());
            added = true;
        }

        // store the profile in the DB
        saveApplicationPolicyInDatabase(applicationPolicy);

        if (!added && !applicationPolicy.getIsDeleted()) {
            // editing so set store a copy with a new uuid and set to deleted
            applicationPolicy.setId(UUID.randomUUID().toString());
            applicationPolicy.setDeleted(true);
            saveApplicationPolicyInDatabase(applicationPolicy);
            applicationPolicy.setDeleted(false);
        }

        if (applicationPolicy.getIsDeleted()) {
            new SecurityAuditDAL().addToAuditTrail(userRoleId,
                    AuditAction.DELETE, ItemType.APPLICATION_POLICY, applicationPolicy.getId(), null, null);
        } else if (added) {
            new SecurityAuditDAL().addToAuditTrail(userRoleId,
                    AuditAction.ADD, ItemType.APPLICATION_POLICY, null, applicationPolicy.getId(), null);
        } else {
            new SecurityAuditDAL().addToAuditTrail(userRoleId,
                    AuditAction.EDIT, ItemType.APPLICATION_POLICY, applicationPolicy.getId(), originalUuid, null);
        }

    }

    public void saveApplicationPolicyInDatabase(JsonApplicationPolicy roleType) throws Exception {
        EntityManager entityManager = ConnectionManager.getUmEntityManager();

        ApplicationPolicyEntity applicationPolicyEntity = new ApplicationPolicyEntity();
        applicationPolicyEntity.setId(roleType.getId());
        applicationPolicyEntity.setName(roleType.getName());
        applicationPolicyEntity.setDescription(roleType.getDescription());
        applicationPolicyEntity.setJobCategoryId(roleType.getJobCategoryId());
        applicationPolicyEntity.setIsDeleted(roleType.getIsDeleted() ? (byte)1 : (byte)0);
        entityManager.getTransaction().begin();
        entityManager.merge(applicationPolicyEntity);
        entityManager.getTransaction().commit();

        entityManager.close();

        ApplicationPolicyCache.clearApplicationPolicyCache(roleType.getId());
    }

    public void deleteApplicationPolicy(String applicationPolicyId, String userRoleId) throws Exception {

        JsonApplicationPolicy applicationPolicy = new JsonApplicationPolicy(ApplicationPolicyCache.getApplicationPolicyDetails(applicationPolicyId));

        applicationPolicy.setDeleted(true);

        saveApplicationPolicy(applicationPolicy, userRoleId);

    }
}
