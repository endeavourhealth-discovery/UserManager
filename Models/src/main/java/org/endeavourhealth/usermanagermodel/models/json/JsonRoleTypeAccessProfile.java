package org.endeavourhealth.usermanagermodel.models.json;

import org.endeavourhealth.usermanagermodel.models.database.RoleTypeAccessProfileEntity;

public class JsonRoleTypeAccessProfile {
    private String id = null;
    private String roleTypeId = null;
    private String name = null;
    private String application = null;
    private String applicationAccessProfileId = null;
    private String profileTree = null;
    private boolean isDeleted;

    public JsonRoleTypeAccessProfile() {
    }

    public JsonRoleTypeAccessProfile(RoleTypeAccessProfileEntity roleTypeAccessProfileEntity) {
        this.id = roleTypeAccessProfileEntity.getId();
        this.roleTypeId = roleTypeAccessProfileEntity.getRoleTypeId();
        this.applicationAccessProfileId = roleTypeAccessProfileEntity.getApplicationAccessProfileId();
        this.profileTree = roleTypeAccessProfileEntity.getProfileTree();
        this.isDeleted = roleTypeAccessProfileEntity.getIsDeleted() == 1;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoleTypeId() {
        return roleTypeId;
    }

    public void setRoleTypeId(String roleTypeId) {
        this.roleTypeId = roleTypeId;
    }

    public String getApplicationAccessProfileId() {
        return applicationAccessProfileId;
    }

    public void setApplicationAccessProfileId(String applicationAccessProfileId) {
        this.applicationAccessProfileId = applicationAccessProfileId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getProfileTree() {
        return profileTree;
    }

    public void setProfileTree(String profileTree) {
        this.profileTree = profileTree;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
