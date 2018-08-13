package org.endeavourhealth.usermanagermodel.models.json;

import org.endeavourhealth.usermanagermodel.models.database.DelegationEntity;

public class JsonDelegation {
    private String uuid = null;
    private String name = null;
    private String rootOrganisation = null;
    private boolean isDeleted;

    public JsonDelegation() {
    }

    public JsonDelegation(DelegationEntity delegationEntity) {
        this.uuid = delegationEntity.getUuid();
        this.name = delegationEntity.getName();
        this.rootOrganisation = delegationEntity.getRootOrganisation();
        this.isDeleted = delegationEntity.getIsDeleted() == 1;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRootOrganisation() {
        return rootOrganisation;
    }

    public void setRootOrganisation(String rootOrganisation) {
        this.rootOrganisation = rootOrganisation;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
