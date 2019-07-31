package org.endeavourhealth.usermanager.api.DAL;

import org.endeavourhealth.common.security.usermanagermodel.models.ConnectionManager;
import org.endeavourhealth.common.security.usermanagermodel.models.DAL.SecurityAuditDAL;
import org.endeavourhealth.common.security.usermanagermodel.models.database.ApplicationPolicyAttributeEntity;
import org.endeavourhealth.common.security.usermanagermodel.models.enums.AuditAction;
import org.endeavourhealth.common.security.usermanagermodel.models.enums.ItemType;
import org.endeavourhealth.common.security.usermanagermodel.models.json.JsonApplicationPolicyAttribute;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.UUID;

public class ApplicationPolicyAttributeDAL {


    public List<ApplicationPolicyAttributeEntity> getAllRoleAccessProfiles() throws Exception {
        EntityManager entityManager = ConnectionManager.getUmEntityManager();

        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<ApplicationPolicyAttributeEntity> cq = cb.createQuery(ApplicationPolicyAttributeEntity.class);
            Root<ApplicationPolicyAttributeEntity> rootEntry = cq.from(ApplicationPolicyAttributeEntity.class);

            Predicate predicate = cb.equal(rootEntry.get("isDeleted"), 0);

            cq.where(predicate);

            TypedQuery<ApplicationPolicyAttributeEntity> query = entityManager.createQuery(cq);
            List<ApplicationPolicyAttributeEntity> ret = query.getResultList();

            return ret;

        } finally {
            entityManager.close();
        }
    }

    public String saveRoleAccessProfile(JsonApplicationPolicyAttribute roleAccessProfile, String userRoleId) throws Exception {

        boolean added = false;
        String originalUuid = roleAccessProfile.getId();
        if (roleAccessProfile.getId() == null) {
            roleAccessProfile.setId(UUID.randomUUID().toString());
            added = true;
        }

        saveRoleAccessProfileInDatabase(roleAccessProfile);

        if (!added && !roleAccessProfile.getIsDeleted()) {
            // editing so set store a copy with a new uuid and set to deleted
            roleAccessProfile.setId(UUID.randomUUID().toString());
            roleAccessProfile.setDeleted(true);
            saveRoleAccessProfileInDatabase(roleAccessProfile);
            roleAccessProfile.setDeleted(false);
        }

        if (roleAccessProfile.getIsDeleted()) {
            new SecurityAuditDAL().addToAuditTrail(userRoleId,
                    AuditAction.DELETE, ItemType.APPLICATION_POLICY_ATTRIBUTE, roleAccessProfile.getId(), null, null);
        } else if (added) {
            new SecurityAuditDAL().addToAuditTrail(userRoleId,
                    AuditAction.ADD, ItemType.APPLICATION_POLICY_ATTRIBUTE, null, roleAccessProfile.getId(), null);
        } else {
            new SecurityAuditDAL().addToAuditTrail(userRoleId,
                    AuditAction.EDIT, ItemType.APPLICATION_POLICY_ATTRIBUTE, roleAccessProfile.getId(), originalUuid, null);
        }

        return roleAccessProfile.getId();

    }

    public void saveRoleAccessProfileInDatabase(JsonApplicationPolicyAttribute jsonApplicationPolicyAttribute) throws Exception {
        EntityManager entityManager = ConnectionManager.getUmEntityManager();

        try {
            ApplicationPolicyAttributeEntity roleAccessEntity = new ApplicationPolicyAttributeEntity();
            roleAccessEntity.setId(jsonApplicationPolicyAttribute.getId());
            roleAccessEntity.setApplicationPolicyId(jsonApplicationPolicyAttribute.getApplicationPolicyId());
            roleAccessEntity.setApplicationAccessProfileId(jsonApplicationPolicyAttribute.getApplicationAccessProfileId());
            roleAccessEntity.setProfileTree(jsonApplicationPolicyAttribute.getProfileTree());
            roleAccessEntity.setIsDeleted(jsonApplicationPolicyAttribute.getIsDeleted() ? (byte) 1 : (byte) 0);
            entityManager.getTransaction().begin();
            entityManager.merge(roleAccessEntity);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            throw e;
        } finally {
            entityManager.close();
        }

    }

    public void setExistingRoleAccessProfileToDeleted(String roleAccessProfileId) throws Exception {
        EntityManager entityManager = ConnectionManager.getUmEntityManager();

        try {
            entityManager.getTransaction().begin();
            String sql = "update ApplicationPolicyAttributeEntity a" +
                    " set a.isDeleted = 1 " +
                    " where a.id = :accessId";

            Query query = entityManager.createQuery(sql)
                    .setParameter("accessId", roleAccessProfileId);

            query.executeUpdate();
            entityManager.getTransaction().commit();


        } finally {
            entityManager.close();
        }
    }

    public ApplicationPolicyAttributeEntity getRoleTypeAccessProfile(String profileId) throws Exception {
        EntityManager entityManager = ConnectionManager.getUmEntityManager();

        try {
            ApplicationPolicyAttributeEntity ret = entityManager.find(ApplicationPolicyAttributeEntity.class, profileId);

            return ret;
        } finally {
            entityManager.close();
        }
    }

    public void deleteRoleAccessProfile(String profileId, String userRoleId) throws Exception {

        JsonApplicationPolicyAttribute accessProfile = new JsonApplicationPolicyAttribute(getRoleTypeAccessProfile(profileId));

        accessProfile.setDeleted(true);

        saveRoleAccessProfile(accessProfile, userRoleId);

    }
}
