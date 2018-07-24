package org.endeavourhealth.usermanagermodel.models.json;

import org.endeavourhealth.usermanagermodel.models.database.UserRoleEntity;

public class JsonUserRole {
    private String id = null;
    private String userId = null;
    private String roleTypeId = null;
    private String organisationId = null;
    private String organisationName = null;
    private String userAccessProfileId = null;
    private boolean isDeleted;

    public JsonUserRole(UserRoleEntity roleEntity) {
        this.id = roleEntity.getId();
        this.userId = roleEntity.getUserId();
        this.roleTypeId = roleEntity.getRoleTypeId();
        this.organisationId = roleEntity.getOrganisationId();
        this.userAccessProfileId = roleEntity.getUserAccessProfileId();
        this.isDeleted = roleEntity.getIsDeleted().equals((byte)1) ? true : false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRoleTypeId() {
        return roleTypeId;
    }

    public void setRoleTypeId(String roleTypeId) {
        this.roleTypeId = roleTypeId;
    }

    public String getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(String organisationId) {
        this.organisationId = organisationId;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    public String getUserAccessProfileId() {
        return userAccessProfileId;
    }

    public void setUserAccessProfileId(String userAccessProfileId) {
        this.userAccessProfileId = userAccessProfileId;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
