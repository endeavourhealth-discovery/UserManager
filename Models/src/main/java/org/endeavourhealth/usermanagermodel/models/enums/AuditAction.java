package org.endeavourhealth.usermanagermodel.models.enums;

public enum AuditAction {
    ADD((short)0),
    EDIT((short)1),
    DELETE((short)2);

    private Short auditAction;

    AuditAction(short auditAction) { this.auditAction = auditAction; }

    public Short getAuditAction() { return auditAction; }
}
