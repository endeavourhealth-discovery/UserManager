package org.endeavourhealth.usermanager.api.DAL;

import org.endeavourhealth.common.security.usermanagermodel.models.ConnectionManager;
import org.endeavourhealth.common.security.usermanagermodel.models.DAL.SecurityAuditDAL;
import org.endeavourhealth.common.security.usermanagermodel.models.database.DelegationRelationshipEntity;
import org.endeavourhealth.common.security.usermanagermodel.models.enums.AuditAction;
import org.endeavourhealth.common.security.usermanagermodel.models.enums.ItemType;
import org.endeavourhealth.common.security.usermanagermodel.models.json.JsonDelegationRelationship;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.UUID;

public class DelegationRelationshipDAL {

    public List<DelegationRelationshipEntity> getAllRelationshipsForDelegation(String delegationId) throws Exception {
        EntityManager entityManager = ConnectionManager.getUmEntityManager();

        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<DelegationRelationshipEntity> cq = cb.createQuery(DelegationRelationshipEntity.class);
            Root<DelegationRelationshipEntity> rootEntry = cq.from(DelegationRelationshipEntity.class);

            Predicate predicate = cb.and(cb.equal(rootEntry.get("delegation"), delegationId),
                    (cb.equal(rootEntry.get("isDeleted"), 0)));

            cq.where(predicate);
            TypedQuery<DelegationRelationshipEntity> query = entityManager.createQuery(cq);
            List<DelegationRelationshipEntity> ret = query.getResultList();

            return ret;
        } finally {
            entityManager.close();
        }
    }

    public List<DelegationRelationshipEntity> getDelegatedOrganisations(String organisationId) throws Exception {
        EntityManager entityManager = ConnectionManager.getUmEntityManager();

        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<DelegationRelationshipEntity> cq = cb.createQuery(DelegationRelationshipEntity.class);
            Root<DelegationRelationshipEntity> rootEntry = cq.from(DelegationRelationshipEntity.class);

            Predicate predicate = cb.equal(rootEntry.get("parentUuid"), organisationId);

            cq.where(predicate);
            TypedQuery<DelegationRelationshipEntity> query = entityManager.createQuery(cq);
            List<DelegationRelationshipEntity> ret = query.getResultList();

            return ret;
        } finally {
            entityManager.close();
        }
    }

    public void saveDelegationRelationship(JsonDelegationRelationship delegationRelationship,
                                                  String userRoleId) throws Exception {


        boolean added = false;
        String originalUuid = delegationRelationship.getUuid();
        if (delegationRelationship.getUuid() == null) {
            delegationRelationship.setUuid(UUID.randomUUID().toString());
            added = true;
        }

        if (!added && !delegationRelationship.getIsDeleted()) {
            // editing so set original to deleted and save new one
            setExistingRelationshipToDeleted(delegationRelationship.getUuid());
            delegationRelationship.setUuid(UUID.randomUUID().toString());

        }

        EntityManager entityManager = ConnectionManager.getUmEntityManager();

        try {
            DelegationRelationshipEntity relationshipEntity = new DelegationRelationshipEntity();
            relationshipEntity.setUuid(delegationRelationship.getUuid());
            relationshipEntity.setDelegation(delegationRelationship.getDelegation());
            relationshipEntity.setParentUuid(delegationRelationship.getParentUuid());
            relationshipEntity.setParentType(delegationRelationship.getParentType());
            relationshipEntity.setChildUuid(delegationRelationship.getChildUuid());
            relationshipEntity.setChildType(delegationRelationship.getChildType());
            relationshipEntity.setIncludeAllChildren(delegationRelationship.isIncludeAllChildren() ? (byte) 1 : (byte) 0);
            relationshipEntity.setCreateSuperUsers(delegationRelationship.isCreateSuperUsers() ? (byte) 1 : (byte) 0);
            relationshipEntity.setCreateUsers(delegationRelationship.isCreateUsers() ? (byte) 1 : (byte) 0);
            relationshipEntity.setIsDeleted(delegationRelationship.getIsDeleted() ? (byte) 1 : (byte) 0);
            entityManager.getTransaction().begin();
            entityManager.merge(relationshipEntity);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            throw e;
        } finally {
            entityManager.close();
        }

        if (delegationRelationship.getIsDeleted()) {
            new SecurityAuditDAL().addToAuditTrail(userRoleId,
                    AuditAction.DELETE, ItemType.DELEGATION_RELATIONSHIP, delegationRelationship.getUuid(), null, null);
        } else if (added) {
            new SecurityAuditDAL().addToAuditTrail(userRoleId,
                    AuditAction.ADD, ItemType.DELEGATION_RELATIONSHIP, null, delegationRelationship.getUuid(), null);
        } else {
            new SecurityAuditDAL().addToAuditTrail(userRoleId,
                    AuditAction.EDIT, ItemType.DELEGATION_RELATIONSHIP, originalUuid, delegationRelationship.getUuid(), null);
        }
    }

    public void setExistingRelationshipToDeleted(String relationshipUuid) throws Exception {
        EntityManager entityManager = ConnectionManager.getUmEntityManager();

        try {
            entityManager.getTransaction().begin();
            String sql = "update DelegationRelationshipEntity d" +
                    " set d.isDeleted = 1 " +
                    " where d.uuid = :relId";

            Query query = entityManager.createQuery(sql)
                    .setParameter("relId", relationshipUuid);

            query.executeUpdate();
            entityManager.getTransaction().commit();


        } finally {
            entityManager.close();
        }
    }

    public DelegationRelationshipEntity getDelegationRelationship(String relationshipId) throws Exception {
        EntityManager entityManager = ConnectionManager.getUmEntityManager();

        try {
            DelegationRelationshipEntity ret = entityManager.find(DelegationRelationshipEntity.class, relationshipId);

            return ret;

        } finally {
            entityManager.close();
        }
    }

    public void deleteAllDelegationRelationships(String delegationId, String userRoleId) throws Exception {
        EntityManager entityManager = ConnectionManager.getUmEntityManager();

        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<DelegationRelationshipEntity> cq = cb.createQuery(DelegationRelationshipEntity.class);
            Root<DelegationRelationshipEntity> rootEntry = cq.from(DelegationRelationshipEntity.class);

            Predicate predicate = cb.equal(rootEntry.get("delegation"), delegationId);

            cq.where(predicate);
            TypedQuery<DelegationRelationshipEntity> query = entityManager.createQuery(cq);
            List<DelegationRelationshipEntity> results = query.getResultList();


            for (DelegationRelationshipEntity result : results) {
                JsonDelegationRelationship rel = new JsonDelegationRelationship(result);
                rel.setIsDeleted(true);
                saveDelegationRelationship(rel, userRoleId);

            }

        } finally {
            entityManager.close();
        }
    }

    public List<DelegationRelationshipEntity> getAllRelationshipsOrganisationsForGodMode() throws Exception {
        EntityManager entityManager = ConnectionManager.getUmEntityManager();

        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<DelegationRelationshipEntity> cq = cb.createQuery(DelegationRelationshipEntity.class);
            Root<DelegationRelationshipEntity> rootEntry = cq.from(DelegationRelationshipEntity.class);

            Predicate predicate = cb.equal(rootEntry.get("isDeleted"), 0);

            cq.where(predicate);
            TypedQuery<DelegationRelationshipEntity> query = entityManager.createQuery(cq);
            List<DelegationRelationshipEntity> ret = query.getResultList();

            return ret;
        } finally {
            entityManager.close();
        }
    }
}
