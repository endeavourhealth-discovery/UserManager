package org.endeavourhealth.usermanager.api.DAL;

import org.endeavourhealth.common.security.usermanagermodel.models.ConnectionManager;
import org.endeavourhealth.common.security.usermanagermodel.models.DAL.SecurityAuditDAL;
import org.endeavourhealth.common.security.usermanagermodel.models.caching.DelegationCache;
import org.endeavourhealth.common.security.usermanagermodel.models.database.DelegationEntity;
import org.endeavourhealth.common.security.usermanagermodel.models.enums.AuditAction;
import org.endeavourhealth.common.security.usermanagermodel.models.enums.ItemType;
import org.endeavourhealth.common.security.usermanagermodel.models.json.JsonDelegation;

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

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<DelegationEntity> cq = cb.createQuery(DelegationEntity.class);
        Root<DelegationEntity> rootEntry = cq.from(DelegationEntity.class);

        Predicate predicate = cb.equal(rootEntry.get("isDeleted"), 0);

        cq.where(predicate);

        TypedQuery<DelegationEntity> query = entityManager.createQuery(cq);
        List<DelegationEntity> ret = query.getResultList();

        entityManager.close();

        return ret;
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

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<DelegationEntity> cq = cb.createQuery(DelegationEntity.class);
        Root<DelegationEntity> rootEntry = cq.from(DelegationEntity.class);

        Predicate predicate = cb.equal(rootEntry.get("uuid"), delegationId);

        cq.where(predicate);
        TypedQuery<DelegationEntity> query = entityManager.createQuery(cq);
        List<DelegationEntity> ret = query.getResultList();

        entityManager.close();

        return ret.get(0).getRootOrganisation();
    }

    public void saveDelegation(JsonDelegation delegation, String userRoleId) throws Exception {
        EntityManager entityManager = ConnectionManager.getUmEntityManager();

        DelegationEntity delegationEntity = new DelegationEntity();
        delegationEntity.setUuid(delegation.getUuid());
        delegationEntity.setName(delegation.getName());
        delegationEntity.setRootOrganisation(delegation.getRootOrganisation());
        delegationEntity.setIsDeleted(delegation.isDeleted() ? (byte)1 : (byte)0);
        entityManager.getTransaction().begin();
        entityManager.merge(delegationEntity);
        entityManager.getTransaction().commit();

        entityManager.close();

        if (delegation.isDeleted()) {
            new SecurityAuditDAL().addToAuditTrail(userRoleId,
                    AuditAction.DELETE, ItemType.DELEGATION, delegation.getUuid(), null, null);
        } else {
            new SecurityAuditDAL().addToAuditTrail(userRoleId,
                    AuditAction.ADD, ItemType.DELEGATION, null, delegation.getUuid(), null);
        }
    }

    public void deleteDelegation(String delegationId, String userRoleId) throws Exception {

        new DelegationRelationshipDAL().deleteAllDelegationRelationships(delegationId, userRoleId);

        JsonDelegation delegation = new JsonDelegation(DelegationCache.getDelegationDetails(delegationId));

        delegation.setDeleted(true);

        saveDelegation(delegation, userRoleId);

    }
}
