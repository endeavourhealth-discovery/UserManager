package org.endeavourhealth.usermanagermodel.models.enums;

public enum ItemType {
    ROLE((short)0);

    private Short itemType;

    ItemType(short itemType) { this.itemType = itemType; }

    public Short getItemType() { return itemType; }
}
