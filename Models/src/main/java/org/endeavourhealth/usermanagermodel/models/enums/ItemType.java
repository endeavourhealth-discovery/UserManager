package org.endeavourhealth.usermanagermodel.models.enums;

public enum ItemType {
    ROLE((short)0),
    USER((short)1);

    private Short itemType;

    ItemType(short itemType) { this.itemType = itemType; }

    public Short getItemType() { return itemType; }
}
