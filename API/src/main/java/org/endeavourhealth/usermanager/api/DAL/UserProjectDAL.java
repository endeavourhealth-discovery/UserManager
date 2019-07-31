package org.endeavourhealth.usermanager.api.DAL;

import org.endeavourhealth.common.security.usermanagermodel.models.ConnectionManager;
import org.endeavourhealth.common.security.usermanagermodel.models.DAL.SecurityAuditDAL;
import org.endeavourhealth.common.security.usermanagermodel.models.caching.UserCache;
import org.endeavourhealth.common.security.usermanagermodel.models.database.UserProjectEntity;
import org.endeavourhealth.common.security.usermanagermodel.models.enums.AuditAction;
import org.endeavourhealth.common.security.usermanagermodel.models.enums.ItemType;
import org.endeavourhealth.common.security.usermanagermodel.models.json.JsonUserProject;

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
            new SecurityAuditDAL().addToAuditTrail(userProjectId,
                    AuditAction.DELETE, ItemType.USER_PROJECT, userProject.getId(), null, null);
        } else {
            new SecurityAuditDAL().addToAuditTrail(userProjectId,
                    AuditAction.ADD, ItemType.USER_PROJECT, null, userProject.getId(), null);
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
