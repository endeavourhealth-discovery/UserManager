package org.endeavourhealth.usermanager.api.DAL;

import org.endeavourhealth.core.database.dal.usermanager.caching.DelegationCache;
import org.endeavourhealth.core.database.dal.usermanager.models.JsonDelegation;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.usermanager.models.DelegationEntity;
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

public class DelegationDAL {

    public List<DelegationEntity> getDelegations(String organisationId) throws Exception {
        if (organisationId == null) {
            return getAllDelegations();
        } else return getSelectedDelegations(organisationId);

    }

    public List<DelegationEntity> getAllDelegations() throws Exception {
        EntityManager entityManager = ConnectionManager.getUmEntityManager();

        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<DelegationEntity> cq = cb.createQuery(DelegationEntity.class);
            Root<DelegationEntity> rootEntry = cq.from(DelegationEntity.class);

            Predicate predicate = cb.equal(rootEntry.get("isDeleted"), 0);

            cq.where(predicate);

            TypedQuery<DelegationEntity> query = entityManager.createQuery(cq);
            List<DelegationEntity> ret = query.getResultList();

            return ret;
        } finally {
            entityManager.close();
        }
    }

    public List<DelegationEntity> getSelectedDelegations(String organisationId) throws Exception {

        EntityManager entityManager = ConnectionManager.getUmEntityManager();

        try {
            String orderby = " order by a.timestamp desc ";
            String sql = "select distinct" +
                    " d" +
                    " from DelegationEntity d" +
                    " join DelegationRelationshipEntity rel on rel.delegation = d.uuid" +
                    " where rel.childUuid = :org or rel.parentUuid = :org" +
                    " and d.isDeleted = 0";


            Query query = entityManager.createQuery(sql, DelegationEntity.class)
                    .setParameter("org", organisationId);

            List<DelegationEntity> results = query.getResultList();

            return results;

        } finally {
            entityManager.close();
        }

    }

    public String getRootOrganisation(String delegationId) throws Exception {
        EntityManager entityManager = ConnectionManager.getUmEntityManager();

        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<DelegationEntity> cq = cb.createQuery(DelegationEntity.class);
            Root<DelegationEntity> rootEntry = cq.from(DelegationEntity.class);

            Predicate predicate = cb.equal(rootEntry.get("uuid"), delegationId);

            cq.where(predicate);
            TypedQuery<DelegationEntity> query = entityManager.createQuery(cq);
            List<DelegationEntity> ret = query.getResultList();

            return ret.get(0).getRootOrganisation();
        } finally {
            entityManager.close();
        }
    }

    public void saveDelegation(JsonDelegation delegation, String userRoleId) throws Exception {
        EntityManager entityManager = ConnectionManager.getUmEntityManager();


        try {
            DelegationEntity delegationEntity = new DelegationEntity();
            delegationEntity.setUuid(delegation.getUuid());
            delegationEntity.setName(delegation.getName());
            delegationEntity.setRootOrganisation(delegation.getRootOrganisation());
            delegationEntity.setIsDeleted(delegation.isDeleted() ? (byte) 1 : (byte) 0);
            entityManager.getTransaction().begin();
            entityManager.merge(delegationEntity);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            throw e;
        } finally {
            entityManager.close();
        }

        DelegationCache.clearDelegationCache(delegation.getUuid());

        if (delegation.isDeleted()) {
            new UIAuditJDBCDAL().addToAuditTrail(userRoleId,
                    AuditAction.DELETE, ItemType.DELEGATION, delegation.getUuid(), null);
        } else {
            new UIAuditJDBCDAL().addToAuditTrail(userRoleId,
                    AuditAction.ADD, ItemType.DELEGATION, null, delegation.getUuid());
        }
    }

    public void deleteDelegation(String delegationId, String userRoleId) throws Exception {

        new DelegationRelationshipDAL().deleteAllDelegationRelationships(delegationId, userRoleId);

        JsonDelegation delegation = new JsonDelegation(DelegationCache.getDelegationDetails(delegationId));

        delegation.setDeleted(true);

        saveDelegation(delegation, userRoleId);

    }
}
