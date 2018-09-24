package org.endeavourhealth.usermanagermodel.models.enums;

public enum ItemType {
    USER_PROJECT((short)0),
    USER((short)1),
    DELEGATION((short)2),
    DELEGATION_RELATIONSHIP((short)3),
    DEFAULT_PROJECT((short)4),
    APPLICATION((short)5),
    APPLICATION_PROFILE((short)6),
    APPLICATION_POLICY_ATTRIBUTE((short)7),
    USER_REGION((short)8),
    USER_APPLICATION_POLICY((short)9),
    APPLICATION_POLICY((short)10);

    private Short itemType;

    ItemType(short itemType) { this.itemType = itemType; }

    public Short getItemType() { return itemType; }
}
