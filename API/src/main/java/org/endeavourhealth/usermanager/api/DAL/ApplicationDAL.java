package org.endeavourhealth.usermanager.api.DAL;

import org.endeavourhealth.common.security.usermanagermodel.models.ConnectionManager;
import org.endeavourhealth.common.security.usermanagermodel.models.DAL.SecurityAuditDAL;
import org.endeavourhealth.common.security.usermanagermodel.models.caching.ApplicationCache;
import org.endeavourhealth.common.security.usermanagermodel.models.database.ApplicationEntity;
import org.endeavourhealth.common.security.usermanagermodel.models.enums.AuditAction;
import org.endeavourhealth.common.security.usermanagermodel.models.enums.ItemType;
import org.endeavourhealth.common.security.usermanagermodel.models.json.JsonApplication;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.UUID;

public class ApplicationDAL {

    public List<ApplicationEntity> getAllApplications() throws Exception {
        EntityManager entityManager = ConnectionManager.getUmEntityManager();

        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<ApplicationEntity> cq = cb.createQuery(ApplicationEntity.class);
            Root<ApplicationEntity> rootEntry = cq.from(ApplicationEntity.class);

            Predicate predicate = cb.equal(rootEntry.get("isDeleted"), 0);

            cq.where(predicate);

            TypedQuery<ApplicationEntity> query = entityManager.createQuery(cq);
            List<ApplicationEntity> ret = query.getResultList();

            return ret;

        } finally {
            entityManager.close();
        }
    }

    public String saveApplication(JsonApplication application, String userRoleId) throws Exception {

        boolean added = false;
        String originalUuid = application.getId();
        if (application.getId() == null) {
            application.setId(UUID.randomUUID().toString());
            added = true;
        }

        saveApplicationInDatabase(application);

        if (!added && !application.getIsDeleted()) {
            // editing so set store a copy with a new uuid and set to deleted
            application.setId(UUID.randomUUID().toString());
            application.setDeleted(true);
            saveApplicationInDatabase(application);
            application.setDeleted(false);
        }

        if (application.getIsDeleted()) {
            new SecurityAuditDAL().addToAuditTrail(userRoleId,
                    AuditAction.DELETE, ItemType.APPLICATION, application.getId(), null, null);
        } else if (added) {
            new SecurityAuditDAL().addToAuditTrail(userRoleId,
                    AuditAction.ADD, ItemType.APPLICATION, null, application.getId(), null);
        } else {
            new SecurityAuditDAL().addToAuditTrail(userRoleId,
                    AuditAction.EDIT, ItemType.APPLICATION, application.getId(), originalUuid, null);
        }

        return application.getId();

    }

    public void saveApplicationInDatabase(JsonApplication application) throws Exception {
        EntityManager entityManager = ConnectionManager.getUmEntityManager();

        try {
            ApplicationEntity applicationEntity = new ApplicationEntity();
            applicationEntity.setId(application.getId());
            applicationEntity.setName(application.getName());
            applicationEntity.setDescription(application.getDescription());
            applicationEntity.setApplicationTree(application.getApplicationTree());
            applicationEntity.setIsDeleted(application.getIsDeleted() ? (byte) 1 : (byte) 0);
            entityManager.getTransaction().begin();
            entityManager.merge(applicationEntity);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            throw e;
        } finally {
            entityManager.close();
        }

        ApplicationCache.clearApplicationCache(application.getId());

    }

    public void setExistingApplicationToDeleted(String applicationId) throws Exception {
        EntityManager entityManager = ConnectionManager.getUmEntityManager();

        try {
            entityManager.getTransaction().begin();
            String sql = "update ApplicationEntity a" +
                    " set a.isDeleted = 1 " +
                    " where a.id = :appId";

            Query query = entityManager.createQuery(sql)
                    .setParameter("appId", applicationId);

            query.executeUpdate();
            entityManager.getTransaction().commit();

            ApplicationCache.clearApplicationCache(applicationId);

        } finally {
            entityManager.close();
        }
    }

    public void deleteApplication(String applicationId, String userRoleId) throws Exception {

        // DelegationRelationshipEntity.deleteAllDelegationRelationships(applicationId, userRoleId);

        JsonApplication application = new JsonApplication(ApplicationCache.getApplicationDetails(applicationId));

        application.setDeleted(true);

        saveApplication(application, userRoleId);

    }
}
