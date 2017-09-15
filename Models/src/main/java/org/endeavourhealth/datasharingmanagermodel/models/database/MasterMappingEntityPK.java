package org.endeavourhealth.datasharingmanagermodel.models.database;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

public class MasterMappingEntityPK implements Serializable {
    private String childUuid;
    private short childMapTypeId;
    private String parentUuid;
    private short parentMapTypeId;

    @Column(name = "child_uuid", nullable = false, length = 36)
    @Id
    public String getChildUuid() {
        return childUuid;
    }

    public void setChildUuid(String childUuid) {
        this.childUuid = childUuid;
    }

    @Column(name = "child_map_type_id", nullable = false)
    @Id
    public short getChildMapTypeId() {
        return childMapTypeId;
    }

    public void setChildMapTypeId(short childMapTypeId) {
        this.childMapTypeId = childMapTypeId;
    }

    @Column(name = "parent_uuid", nullable = false, length = 36)
    @Id
    public String getParentUuid() {
        return parentUuid;
    }

    public void setParentUuid(String parentUuid) {
        this.parentUuid = parentUuid;
    }

    @Column(name = "parent_map_type_id", nullable = false)
    @Id
    public short getParentMapTypeId() {
        return parentMapTypeId;
    }

    public void setParentMapTypeId(short parentMapTypeId) {
        this.parentMapTypeId = parentMapTypeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MasterMappingEntityPK that = (MasterMappingEntityPK) o;

        if (childMapTypeId != that.childMapTypeId) return false;
        if (parentMapTypeId != that.parentMapTypeId) return false;
        if (childUuid != null ? !childUuid.equals(that.childUuid) : that.childUuid != null) return false;
        if (parentUuid != null ? !parentUuid.equals(that.parentUuid) : that.parentUuid != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = childUuid != null ? childUuid.hashCode() : 0;
        result = 31 * result + (int) childMapTypeId;
        result = 31 * result + (parentUuid != null ? parentUuid.hashCode() : 0);
        result = 31 * result + (int) parentMapTypeId;
        return result;
    }
}
