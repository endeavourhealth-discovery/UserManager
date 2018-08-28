package org.endeavourhealth.usermanagermodel.models.json;

public class JsonApplication {
    private String id = null;
    private String name = null;
    private String description = null;
    private String applicationTree = null;
    private boolean isDeleted;

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

    public String getApplicationTree() {
        return applicationTree;
    }

    public void setApplicationTree(String applicationTree) {
        this.applicationTree = applicationTree;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
