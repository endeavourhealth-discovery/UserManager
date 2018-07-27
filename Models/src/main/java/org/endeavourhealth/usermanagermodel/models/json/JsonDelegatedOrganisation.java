package org.endeavourhealth.usermanagermodel.models.json;

import org.endeavourhealth.usermanagermodel.models.database.DelegationRelationshipEntity;

public class JsonDelegatedOrganisation {
    private String uuid = null;
    private String name = null;
    private String odsCode = null;
    private boolean createSuperUsers;
    private boolean createUsers;

    public JsonDelegatedOrganisation() {
    }

    public JsonDelegatedOrganisation(DelegationRelationshipEntity delgationEntity) {
        this.uuid = delgationEntity.getChildUuid();
        this.createSuperUsers = delgationEntity.getCreateSuperUsers() == 0 ? false : true;
        this.createUsers = delgationEntity.getCreateUsers() == 0 ? false : true;
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

    public String getOdsCode() {
        return odsCode;
    }

    public void setOdsCode(String odsCode) {
        this.odsCode = odsCode;
    }

    public boolean isCreateSuperUsers() {
        return createSuperUsers;
    }

    public void setCreateSuperUsers(boolean createSuperUsers) {
        this.createSuperUsers = createSuperUsers;
    }

    public boolean isCreateUsers() {
        return createUsers;
    }

    public void setCreateUsers(boolean createUsers) {
        this.createUsers = createUsers;
    }
}
