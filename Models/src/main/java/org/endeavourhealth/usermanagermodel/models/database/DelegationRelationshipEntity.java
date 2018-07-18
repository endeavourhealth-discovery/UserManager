package org.endeavourhealth.usermanagermodel.models.database;

import org.endeavourhealth.usermanagermodel.PersistenceManager;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "delegation_relationship", schema = "user_manager")
@IdClass(DelegationRelationshipEntityPK.class)
public class DelegationRelationshipEntity {
    private String parentUuid;
    private short parentType;
    private String childUuid;
    private short childType;
    private byte includeAllChildren;
    private byte createSuperUsers;
    private byte createUsers;
    private String delegation;

    @Id
    @Column(name = "parent_uuid")
    public String getParentUuid() {
        return parentUuid;
    }

    public void setParentUuid(String parentUuid) {
        this.parentUuid = parentUuid;
    }

    @Id
    @Column(name = "parent_type")
    public short getParentType() {
        return parentType;
    }

    public void setParentType(short parentType) {
        this.parentType = parentType;
    }

    @Id
    @Column(name = "child_uuid")
    public String getChildUuid() {
        return childUuid;
    }

    public void setChildUuid(String childUuid) {
        this.childUuid = childUuid;
    }

    @Id
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
                Objects.equals(parentUuid, that.parentUuid) &&
                Objects.equals(childUuid, that.childUuid);
    }

    @Override
    public int hashCode() {

        return Objects.hash(parentUuid, parentType, childUuid, childType, includeAllChildren, createSuperUsers, createUsers);
    }

    @Id
    @Column(name = "delegation")
    public String getDelegation() {
        return delegation;
    }

    public void setDelegation(String delegation) {
        this.delegation = delegation;
    }

    public static List<DelegationRelationshipEntity> getDelegations(String delegationId) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<DelegationRelationshipEntity> cq = cb.createQuery(DelegationRelationshipEntity.class);
        Root<DelegationRelationshipEntity> rootEntry = cq.from(DelegationRelationshipEntity.class);

        Predicate predicate = cb.equal(rootEntry.get("delegation"), delegationId);

        cq.where(predicate);
        TypedQuery<DelegationRelationshipEntity> query = entityManager.createQuery(cq);
        List<DelegationRelationshipEntity> ret = query.getResultList();

        entityManager.close();

        return ret;
    }

}
