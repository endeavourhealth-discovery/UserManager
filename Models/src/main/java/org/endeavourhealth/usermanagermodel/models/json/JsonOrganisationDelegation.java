package org.endeavourhealth.usermanagermodel.models.json;

import java.util.ArrayList;
import java.util.List;

public class JsonOrganisationDelegation {
    private String uuid = null;
    private String name = null;
    private boolean createSuperUsers;
    private boolean createUsers;
    private List<JsonOrganisationDelegation> children = null;

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

    public List<JsonOrganisationDelegation> getChildren() {
        return children;
    }

    public void setChildren(List<JsonOrganisationDelegation> children) {
        this.children = children;
    }

    public void addChild(JsonOrganisationDelegation organisation) {
        if (this.children == null) {
            this.children = new ArrayList<JsonOrganisationDelegation>();
        }

        /*JsonOrganisationDelegation delegation = new JsonOrganisationDelegation();
        delegation.setUuid(organisation.getUuid());
        delegation.setName(organisation.getName());
        delegation.setOdsCode(organisation.getOdsCode());*/
        this.children.add(organisation);
    }
}
