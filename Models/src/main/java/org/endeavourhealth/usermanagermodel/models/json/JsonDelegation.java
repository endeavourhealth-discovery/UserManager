package org.endeavourhealth.usermanagermodel.models.json;

public class JsonDelegation {
    private String uuid = null;
    private String name = null;
    private String rootOrganisation = null;

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
}
