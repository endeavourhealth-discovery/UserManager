package org.endeavourhealth.usermanagermodel.models.json;

import org.endeavourhealth.usermanagermodel.models.database.ApplicationPolicyEntity;

public class JsonApplicationPolicy {
    private String id = null;
    private String name = null;
    private String description = null;
    private String jobCategoryId = null;
    private boolean isDeleted;

    public JsonApplicationPolicy() {
    }

    public JsonApplicationPolicy(ApplicationPolicyEntity applicationPolicyEntity) {
        this.id = applicationPolicyEntity.getId();
        this.name = applicationPolicyEntity.getName();
        this.description = applicationPolicyEntity.getDescription();
        this.jobCategoryId = applicationPolicyEntity.getJobCategoryId();
        this.isDeleted = applicationPolicyEntity.getIsDeleted() == 1;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getJobCategoryId() {
        return jobCategoryId;
    }

    public void setJobCategoryId(String jobCategoryId) {
        this.jobCategoryId = jobCategoryId;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
