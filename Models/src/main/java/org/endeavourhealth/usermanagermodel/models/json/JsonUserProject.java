package org.endeavourhealth.usermanagermodel.models.json;

import org.endeavourhealth.usermanagermodel.models.database.UserProjectEntity;

public class JsonUserProject {
    private String id = null;
    private String userId = null;
    private String projectId = null;
    private String projectName = null;
    private String organisationId = null;
    private String organisationName = null;
    private boolean isDeleted;
    private boolean isDefault;

    public JsonUserProject() {
    }

    public JsonUserProject(UserProjectEntity projectEntity) {
        this.id = projectEntity.getId();
        this.userId = projectEntity.getUserId();
        this.projectId = projectEntity.getProjectId();
        this.organisationId = projectEntity.getOrganisationId();
        this.isDeleted = projectEntity.getIsDeleted().equals((byte)1) ? true : false;
        this.isDefault = projectEntity.getIsDefault().equals((byte)1) ? true : false;
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

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
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

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }
}
