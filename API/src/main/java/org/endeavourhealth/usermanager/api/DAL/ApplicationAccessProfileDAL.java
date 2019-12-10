package org.endeavourhealth.usermanager.api.DAL;

import org.endeavourhealth.common.security.usermanagermodel.models.ConnectionManager;
import org.endeavourhealth.common.security.usermanagermodel.models.caching.ApplicationProfileCache;
import org.endeavourhealth.common.security.usermanagermodel.models.database.ApplicationAccessProfileEntity;
import org.endeavourhealth.common.security.usermanagermodel.models.json.JsonApplicationAccessProfile;
import org.endeavourhealth.uiaudit.dal.UIAuditJDBCDAL;
import org.endeavourhealth.uiaudit.enums.AuditAction;
import org.endeavourhealth.uiaudit.enums.ItemType;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.UUID;

public class ApplicationAccessProfileDAL {

    public List<ApplicationAccessProfileEntity> getAllApplicationProfiles() throws Exception {
        EntityManager entityManager = ConnectionManager.getUmEntityManager();

        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<ApplicationAccessProfileEntity> cq = cb.createQuery(ApplicationAccessProfileEntity.class);
            Root<ApplicationAccessProfileEntity> rootEntry = cq.from(ApplicationAccessProfileEntity.class);

            Predicate predicate = cb.equal(rootEntry.get("isDeleted"), 0);

            cq.where(predicate);

            TypedQuery<ApplicationAccessProfileEntity> query = entityManager.createQuery(cq);
            List<ApplicationAccessProfileEntity> ret = query.getResultList();

            return ret;
        } finally {
            entityManager.close();
        }
    }

    public List<ApplicationAccessProfileEntity> getApplicationProfiles(String applicationId) throws Exception {
        EntityManager entityManager = ConnectionManager.getUmEntityManager();

        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<ApplicationAccessProfileEntity> cq = cb.createQuery(ApplicationAccessProfileEntity.class);
            Root<ApplicationAccessProfileEntity> rootEntry = cq.from(ApplicationAccessProfileEntity.class);

            Predicate predicate = cb.and(cb.equal(rootEntry.get("isDeleted"), 0),
                    (cb.equal(rootEntry.get("applicationId"), applicationId)));

            cq.where(predicate);

            TypedQuery<ApplicationAccessProfileEntity> query = entityManager.createQuery(cq);
            List<ApplicationAccessProfileEntity> ret = query.getResultList();

            return ret;
        } finally {
            entityManager.close();
        }
    }

    public void saveApplicationProfile(JsonApplicationAccessProfile applicationProfile, String userRoleId) throws Exception {

        boolean added = false;
        String originalUuid = applicationProfile.getId();
        if (applicationProfile.getId() == null) {
            applicationProfile.setId(UUID.randomUUID().toString());
            added = true;
        }

        // store the profile in the DB
        saveApplicationProfileInDatabase(applicationProfile);

        if (!added && !applicationProfile.getIsDeleted()) {
            // editing so set store a copy with a new uuid and set to deleted
            applicationProfile.setId(UUID.randomUUID().toString());
            applicationProfile.setDeleted(true);
            saveApplicationProfileInDatabase(applicationProfile);
            applicationProfile.setDeleted(false);
        }

        if (applicationProfile.getIsDeleted()) {
            new UIAuditJDBCDAL().addToAuditTrail(userRoleId,
                    AuditAction.DELETE, ItemType.APPLICATION_PROFILE, applicationProfile.getId(), null);
        } else if (added) {
            new UIAuditJDBCDAL().addToAuditTrail(userRoleId,
                    AuditAction.ADD, ItemType.APPLICATION_PROFILE, null, applicationProfile.getId());
        } else {
            new UIAuditJDBCDAL().addToAuditTrail(userRoleId,
                    AuditAction.EDIT, ItemType.APPLICATION_PROFILE, applicationProfile.getId(), originalUuid);
        }

    }

    public void saveApplicationProfileInDatabase(JsonApplicationAccessProfile applicationProfile) throws Exception {
        EntityManager entityManager = ConnectionManager.getUmEntityManager();

        try {
            ApplicationAccessProfileEntity applicationEntity = new ApplicationAccessProfileEntity();
            applicationEntity.setId(applicationProfile.getId());
            applicationEntity.setName(applicationProfile.getName());
            applicationEntity.setApplicationId(applicationProfile.getApplicationId());
            applicationEntity.setDescription(applicationProfile.getDescription());
            applicationEntity.setSuperUser(applicationProfile.getIsSuperUser() ? (byte)1 : (byte)0);
            applicationEntity.setIsDeleted(applicationProfile.getIsDeleted() ? (byte) 1 : (byte) 0);
            entityManager.getTransaction().begin();
            entityManager.merge(applicationEntity);
            entityManager.getTransaction().commit();

        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            throw e;
        } finally {
            entityManager.close();
        }

        ApplicationProfileCache.clearApplicationProfileCache(applicationProfile.getId());

    }

    public void setExistingApplicationProfileToDeleted(String applicationProfileId) throws Exception {
        EntityManager entityManager = ConnectionManager.getUmEntityManager();

        try {
            entityManager.getTransaction().begin();
            String sql = "update ApplicationAccessProfileEntity a" +
                    " set a.isDeleted = 1 " +
                    " where a.id = :profileId";

            Query query = entityManager.createQuery(sql)
                    .setParameter("profileId", applicationProfileId);

            query.executeUpdate();
            entityManager.getTransaction().commit();


            ApplicationProfileCache.clearApplicationProfileCache(applicationProfileId);


        } finally {
            entityManager.close();
        }
    }

    public void deleteApplicationProfile(String applicationProfileId, String userRoleId) throws Exception {

        // DelegationRelationshipEntity.deleteAllDelegationRelationships(applicationId, userRoleId);

        JsonApplicationAccessProfile applicationProfile = new JsonApplicationAccessProfile(ApplicationProfileCache.getApplicationProfileDetails(applicationProfileId));

        applicationProfile.setDeleted(true);

        saveApplicationProfile(applicationProfile, userRoleId);

    }
}
