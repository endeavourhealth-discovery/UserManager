package org.endeavourhealth.usermanagermodel.models.enums;

public enum ItemType {
    USER_PROJECT((short)0),
    USER((short)1),
    DELEGATION((short)2),
    DELEGATION_RELATIONSHIP((short)3),
    DEFAULT_PROJECT((short)4),
    APPLICATION((short)5),
    APPLICATION_PROFILE((short)6),
    ROLE_TYPE_APPLICATION_PROFILE((short)7),
    USER_REGION((short)8);

    private Short itemType;

    ItemType(short itemType) { this.itemType = itemType; }

    public Short getItemType() { return itemType; }
}
