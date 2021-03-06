package org.endeavourhealth.usermanager.api.DAL;

import org.endeavourhealth.core.database.dal.usermanager.caching.UserCache;
import org.endeavourhealth.core.database.dal.usermanager.models.JsonUserProject;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.usermanager.models.UserProjectEntity;
import org.endeavourhealth.uiaudit.dal.UIAuditJDBCDAL;
import org.endeavourhealth.uiaudit.enums.AuditAction;
import org.endeavourhealth.uiaudit.enums.ItemType;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

public class UserProjectDAL {

    public void saveUserProject(JsonUserProject userProject, String userProjectId) throws Exception {
        EntityManager entityManager = ConnectionManager.getUmEntityManager();

        try {
            entityManager.getTransaction().begin();
            UserProjectEntity userProjectEntity = new UserProjectEntity();
            userProjectEntity.setId(userProject.getId());
            userProjectEntity.setUserId(userProject.getUserId());
            userProjectEntity.setOrganisationId(userProject.getOrganisationId());
            userProjectEntity.setProjectId(userProject.getProjectId());
            userProjectEntity.setIsDeleted(userProject.isDeleted() ? (byte) 1 : (byte) 0);
            userProjectEntity.setIsDefault(userProject.isDefault() ? (byte) 1 : (byte) 0);
            entityManager.merge(userProjectEntity);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            throw e;
        } finally {
            entityManager.close();
        }

        UserCache.clearUserCache(userProject.getUserId());

        if (userProject.isDeleted()) {
            new UIAuditJDBCDAL().addToAuditTrail(userProjectId,
                    AuditAction.DELETE, ItemType.USER_PROJECT, userProject.getId(), null);
        } else {
            new UIAuditJDBCDAL().addToAuditTrail(userProjectId,
                    AuditAction.ADD, ItemType.USER_PROJECT, null, userProject.getId());
        }
    }

    public List<UserProjectEntity> getUsersAtOrganisation(String organisationId) throws Exception {
        EntityManager entityManager = ConnectionManager.getUmEntityManager();

        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<UserProjectEntity> cq = cb.createQuery(UserProjectEntity.class);
            Root<UserProjectEntity> rootEntry = cq.from(UserProjectEntity.class);

            Predicate predicate = cb.and(cb.equal(rootEntry.get("organisationId"), organisationId),
                    cb.equal(rootEntry.get("isDeleted"), (byte) 0));

            cq.where(predicate);
            TypedQuery<UserProjectEntity> query = entityManager.createQuery(cq);
            List<UserProjectEntity> ret = query.getResultList();

            return ret;
        } finally {
            entityManager.close();
        }
    }
}
