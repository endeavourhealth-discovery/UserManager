package org.endeavourhealth.usermanagermodel.models.database;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "item_type", schema = "user_manager")
public class ItemTypeEntity {
    private byte id;
    private String itemType;

    @Id
    @Column(name = "id")
    public byte getId() {
        return id;
    }

    public void setId(byte id) {
        this.id = id;
    }

    @Basic
    @Column(name = "item_type")
    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemTypeEntity that = (ItemTypeEntity) o;
        return id == that.id &&
                Objects.equals(itemType, that.itemType);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, itemType);
    }
}
