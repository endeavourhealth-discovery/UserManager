package org.endeavourhealth.usermanagermodel.models.database;

import org.endeavourhealth.usermanagermodel.PersistenceManager;
import org.endeavourhealth.usermanagermodel.models.enums.AuditAction;
import org.endeavourhealth.usermanagermodel.models.enums.ItemType;
import org.endeavourhealth.usermanagermodel.models.json.JsonDelegationRelationship;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "delegation_relationship", schema = "user_manager")
public class DelegationRelationshipEntity {
    private String uuid;
    private String delegation;
    private String parentUuid;
    private short parentType;
    private String childUuid;
    private short childType;
    private byte includeAllChildren;
    private byte createSuperUsers;
    private byte createUsers;
    private Byte isDeleted;

    @Id
    @Column(name = "uuid")
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Basic
    @Column(name = "delegation")
    public String getDelegation() {
        return delegation;
    }

    public void setDelegation(String delegation) {
        this.delegation = delegation;
    }

    @Basic
    @Column(name = "parent_uuid")
    public String getParentUuid() {
        return parentUuid;
    }

    public void setParentUuid(String parentUuid) {
        this.parentUuid = parentUuid;
    }

    @Basic
    @Column(name = "parent_type")
    public short getParentType() {
        return parentType;
    }

    public void setParentType(short parentType) {
        this.parentType = parentType;
    }

    @Basic
    @Column(name = "child_uuid")
    public String getChildUuid() {
        return childUuid;
    }

    public void setChildUuid(String childUuid) {
        this.childUuid = childUuid;
    }

    @Basic
    @Column(name = "child_type")
    public short getChildType() {
        return childType;
    }

    public void setChildType(short childType) {
        this.childType = childType;
    }

    @Basic
    @Column(name = "include_all_children")
    public byte getIncludeAllChildren() {
        return includeAllChildren;
    }

    public void setIncludeAllChildren(byte includeAllChildren) {
        this.includeAllChildren = includeAllChildren;
    }

    @Basic
    @Column(name = "create_super_users")
    public byte getCreateSuperUsers() {
        return createSuperUsers;
    }

    public void setCreateSuperUsers(byte createSuperUsers) {
        this.createSuperUsers = createSuperUsers;
    }

    @Basic
    @Column(name = "create_users")
    public byte getCreateUsers() {
        return createUsers;
    }

    public void setCreateUsers(byte createUsers) {
        this.createUsers = createUsers;
    }

    @Basic
    @Column(name = "is_deleted")
    public Byte getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Byte isDeleted) {
        this.isDeleted = isDeleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DelegationRelationshipEntity that = (DelegationRelationshipEntity) o;
        return parentType == that.parentType &&
                childType == that.childType &&
                includeAllChildren == that.includeAllChildren &&
                createSuperUsers == that.createSuperUsers &&
                createUsers == that.createUsers &&
                Objects.equals(uuid, that.uuid) &&
                Objects.equals(delegation, that.delegation) &&
                Objects.equals(parentUuid, that.parentUuid) &&
                Objects.equals(childUuid, that.childUuid) &&
                Objects.equals(isDeleted, that.isDeleted);
    }

    @Override
    public int hashCode() {

        return Objects.hash(uuid, delegation, parentUuid, parentType, childUuid, childType, includeAllChildren, createSuperUsers, createUsers, isDeleted);
    }

    public static List<DelegationRelationshipEntity> getAllRelationshipsForDelegation(String delegationId) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<DelegationRelationshipEntity> cq = cb.createQuery(DelegationRelationshipEntity.class);
        Root<DelegationRelationshipEntity> rootEntry = cq.from(DelegationRelationshipEntity.class);

        Predicate predicate = cb.and(cb.equal(rootEntry.get("delegation"), delegationId),
                (cb.equal(rootEntry.get("isDeleted"), 0)));

        cq.where(predicate);
        TypedQuery<DelegationRelationshipEntity> query = entityManager.createQuery(cq);
        List<DelegationRelationshipEntity> ret = query.getResultList();

        entityManager.close();

        return ret;
    }

    public static List<DelegationRelationshipEntity> getDelegatedOrganisations(String organisationId) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<DelegationRelationshipEntity> cq = cb.createQuery(DelegationRelationshipEntity.class);
        Root<DelegationRelationshipEntity> rootEntry = cq.from(DelegationRelationshipEntity.class);

        Predicate predicate = cb.equal(rootEntry.get("parentUuid"), organisationId);

        cq.where(predicate);
        TypedQuery<DelegationRelationshipEntity> query = entityManager.createQuery(cq);
        List<DelegationRelationshipEntity> ret = query.getResultList();

        entityManager.close();

        return ret;
    }

    public static void saveDelegationRelationship(JsonDelegationRelationship delegationRelationship,
                                                  String userRoleId) throws Exception {

        EntityManager entityManager = PersistenceManager.getEntityManager();

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

        DelegationRelationshipEntity relationshipEntity = new DelegationRelationshipEntity();
        relationshipEntity.setUuid(delegationRelationship.getUuid());
        relationshipEntity.setDelegation(delegationRelationship.getDelegation());
        relationshipEntity.setParentUuid(delegationRelationship.getParentUuid());
        relationshipEntity.setParentType(delegationRelationship.getParentType());
        relationshipEntity.setChildUuid(delegationRelationship.getChildUuid());
        relationshipEntity.setChildType(delegationRelationship.getChildType());
        relationshipEntity.setIncludeAllChildren(delegationRelationship.isIncludeAllChildren() ? (byte)1 : (byte)0);
        relationshipEntity.setCreateSuperUsers(delegationRelationship.isCreateSuperUsers() ? (byte)1 : (byte)0);
        relationshipEntity.setCreateUsers(delegationRelationship.isCreateUsers() ? (byte)1 : (byte)0);
        relationshipEntity.setIsDeleted(delegationRelationship.getIsDeleted() ? (byte)1 : (byte)0);
        entityManager.getTransaction().begin();
        entityManager.merge(relationshipEntity);
        entityManager.getTransaction().commit();

        entityManager.close();


        if (delegationRelationship.getIsDeleted()) {
            AuditEntity.addToAuditTrail(userRoleId,
                    AuditAction.DELETE, ItemType.DELEGATION_RELATIONSHIP, delegationRelationship.getUuid(), null, null);
        } else if (added) {
            AuditEntity.addToAuditTrail(userRoleId,
                    AuditAction.ADD, ItemType.DELEGATION_RELATIONSHIP, null, delegationRelationship.getUuid(), null);
        } else {
            AuditEntity.addToAuditTrail(userRoleId,
                    AuditAction.EDIT, ItemType.DELEGATION_RELATIONSHIP, originalUuid, delegationRelationship.getUuid(), null);
        }
    }

    public static void setExistingRelationshipToDeleted(String relationshipUuid) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

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

    public static DelegationRelationshipEntity getDelegationRelationship(String relationshipId) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        DelegationRelationshipEntity ret = entityManager.find(DelegationRelationshipEntity.class, relationshipId);

        entityManager.close();

        return ret;
    }

    public static void deleteAllDelegationRelationships(String delegationId, String userRoleId) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

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

        entityManager.close();
    }

    public static List<DelegationRelationshipEntity> getAllRelationshipsOrganisationsForGodMode() throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<DelegationRelationshipEntity> cq = cb.createQuery(DelegationRelationshipEntity.class);
        Root<DelegationRelationshipEntity> rootEntry = cq.from(DelegationRelationshipEntity.class);

        Predicate predicate = cb.equal(rootEntry.get("isDeleted"), 0);

        cq.where(predicate);
        TypedQuery<DelegationRelationshipEntity> query = entityManager.createQuery(cq);
        List<DelegationRelationshipEntity> ret = query.getResultList();

        entityManager.close();

        return ret;
    }
}
