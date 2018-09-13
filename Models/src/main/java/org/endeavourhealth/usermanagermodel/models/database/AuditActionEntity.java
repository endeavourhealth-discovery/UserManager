package org.endeavourhealth.usermanagermodel.models.database;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "audit_action", schema = "user_manager")
public class AuditActionEntity {
    private byte id;
    private String actionType;

    @Id
    @Column(name = "id")
    public byte getId() {
        return id;
    }

    public void setId(byte id) {
        this.id = id;
    }

    @Basic
    @Column(name = "action_type")
    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuditActionEntity that = (AuditActionEntity) o;
        return id == that.id &&
                Objects.equals(actionType, that.actionType);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, actionType);
    }
}
