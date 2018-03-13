package org.endeavourhealth.datasharingmanagermodel.models.json;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonCohort {
    private String uuid = null;
    private String name = null;
    private Short ConsentModelId = null;
    private String description = null;
    private Map<UUID, String> dpas = null;

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

    public Short getConsentModelId() {
        return ConsentModelId;
    }

    public void setConsentModelId(Short consentModelId) {
        ConsentModelId = consentModelId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<UUID, String> getDpas() {
        return dpas;
    }

    public void setDpas(Map<UUID, String> dpas) {
        this.dpas = dpas;
    }
}
