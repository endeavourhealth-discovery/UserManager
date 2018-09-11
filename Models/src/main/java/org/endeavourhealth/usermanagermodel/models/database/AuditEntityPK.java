package org.endeavourhealth.usermanagermodel.models.database;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

public class AuditEntityPK implements Serializable {
    private String id;
    private String userRoleId;
    private Timestamp timestamp;

    @Column(name = "id")
    @Id
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "user_role_id")
    @Id
    public String getUserProjectId() {
        return userRoleId;
    }

    public void setUserProjectId(String userRoleId) {
        this.userRoleId = userRoleId;
    }

    @Column(name = "timestamp")
    @Id
    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuditEntityPK that = (AuditEntityPK) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(userRoleId, that.userRoleId) &&
                Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, userRoleId, timestamp);
    }
}
