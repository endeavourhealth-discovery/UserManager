package org.endeavourhealth.usermanagermodel.models.database;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

public class DelegationRelationshipEntityPK implements Serializable {
    private String parentUuid;
    private short parentType;
    private String childUuid;
    private short childType;
    private String delegation;

    @Column(name = "parent_uuid")
    @Id
    public String getParentUuid() {
        return parentUuid;
    }

    public void setParentUuid(String parentUuid) {
        this.parentUuid = parentUuid;
    }

    @Column(name = "parent_type")
    @Id
    public short getParentType() {
        return parentType;
    }

    public void setParentType(short parentType) {
        this.parentType = parentType;
    }

    @Column(name = "child_uuid")
    @Id
    public String getChildUuid() {
        return childUuid;
    }

    public void setChildUuid(String childUuid) {
        this.childUuid = childUuid;
    }

    @Column(name = "child_type")
    @Id
    public short getChildType() {
        return childType;
    }

    public void setChildType(short childType) {
        this.childType = childType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DelegationRelationshipEntityPK that = (DelegationRelationshipEntityPK) o;
        return parentType == that.parentType &&
                childType == that.childType &&
                Objects.equals(parentUuid, that.parentUuid) &&
                Objects.equals(childUuid, that.childUuid);
    }

    @Override
    public int hashCode() {

        return Objects.hash(parentUuid, parentType, childUuid, childType);
    }

    @Column(name = "delegation")
    @Id
    public String getDelegation() {
        return delegation;
    }

    public void setDelegation(String delegation) {
        this.delegation = delegation;
    }
}
