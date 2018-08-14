package org.endeavourhealth.usermanagermodel.models.enums;

public enum ItemType {
    ROLE((short)0),
    USER((short)1),
    DELEGATION((short)2),
    DELEGATION_RELATIONSHIP((short)3),
    DEFAULT_ROLE((short)4);

    private Short itemType;

    ItemType(short itemType) { this.itemType = itemType; }

    public Short getItemType() { return itemType; }
}
