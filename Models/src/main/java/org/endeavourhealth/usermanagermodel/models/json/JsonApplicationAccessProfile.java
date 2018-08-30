package org.endeavourhealth.usermanagermodel.models.json;

import org.endeavourhealth.usermanagermodel.models.database.ApplicationAccessProfileEntity;

public class JsonApplicationAccessProfile {
    private String id = null;
    private String name = null;
    private String applicationId = null;
    private String description = null;
    private String profileTree = null;
    private boolean isDeleted;

    public JsonApplicationAccessProfile() {
    }

    public JsonApplicationAccessProfile(ApplicationAccessProfileEntity applicationAccessProfileEntity) {
        this.id = applicationAccessProfileEntity.getId();
        this.name = applicationAccessProfileEntity.getName();
        this.description = applicationAccessProfileEntity.getDescription();
        this.profileTree = applicationAccessProfileEntity.getProfileTree();
        this.isDeleted = applicationAccessProfileEntity.getIsDeleted() == 1;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
