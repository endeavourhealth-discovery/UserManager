package org.endeavourhealth.usermanagermodel.models.json;

import org.endeavourhealth.datasharingmanagermodel.models.database.OrganisationEntity;

import java.util.ArrayList;
import java.util.List;

public class JsonOrganisationDelegation {
    private String uuid = null;
    private String displayName = null;
    private String odsCode = null;
    private List<JsonOrganisationDelegation> children = null;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getOdsCode() {
        return odsCode;
    }

    public void setOdsCode(String odsCode) {
        this.odsCode = odsCode;
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
        delegation.setDisplayName(organisation.getName());
        delegation.setOdsCode(organisation.getOdsCode());*/
        this.children.add(organisation);
    }
}
