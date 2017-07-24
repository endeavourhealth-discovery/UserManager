package org.endeavourhealth.datasharingmanager.api.json;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonDataSet {
    private String uuid = null;
    private String name = null;
    private String description = null;
    private String attributes = null;
    private String queryDefinition = null;
    private List<Object> composition = null;
    private Map<UUID, String> dpas = null;

    public JsonDataSet() {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAttributes() {
        return attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    public String getQueryDefinition() {
        return queryDefinition;
    }

    public void setQueryDefinition(String queryDefinition) {
        this.queryDefinition = queryDefinition;
    }

    public Map<UUID, String> getDpas() { return dpas; }

    public void setDpas(Map<UUID, String> dpas) {
        this.dpas = dpas;
    }

    public List<Object> getComposition() { return composition; }

    public void setComposition (List<Object> composition) {this.composition = composition; }
}
