package org.endeavourhealth.datasharingmanagermodel.models.json;


import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonDataFlow {
    private String uuid = null;
    private String name = null;
    private String purpose = null;
    private Short storageProtocolId = null;
    private Short deidentificationLevel = null;
    private Short consentModelId = null;
    private Map<UUID, String> dsas = null;
    private Map<UUID, String> dpas = null;
    private Map<UUID, String> exchanges = null;
    private Map<UUID, String> publishers = null;
    private Map<UUID, String> subscribers = null;
    private List<JsonDocumentation> documentations = new ArrayList<>();

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

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public Short getStorageProtocolId() {
        return storageProtocolId;
    }

    public void setStorageProtocolId(Short storageProtocolId) {
        this.storageProtocolId = storageProtocolId;
    }

    public Short getDeidentificationLevel() {
        return deidentificationLevel;
    }

    public void setDeidentificationLevel(Short deidentificationLevel) {
        this.deidentificationLevel = deidentificationLevel;
    }

    public Short getConsentModelId() {
        return consentModelId;
    }

    public void setConsentModelId(Short consentModelId) {
        this.consentModelId = consentModelId;
    }

    public Map<UUID, String> getDsas() {
        return dsas;
    }

    public void setDsas(Map<UUID, String> dsas) {
        this.dsas = dsas;
    }

    public Map<UUID, String> getDpas() {
        return dpas;
    }

    public void setDpas(Map<UUID, String> dpas) {
        this.dpas = dpas;
    }

    public Map<UUID, String> getExchanges() {
        return exchanges;
    }

    public void setExchanges(Map<UUID, String> exchanges) {
        this.exchanges = exchanges;
    }

    public Map<UUID, String> getPublishers() {
        return publishers;
    }

    public void setPublishers(Map<UUID, String> publishers) {
        this.publishers = publishers;
    }

    public Map<UUID, String> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(Map<UUID, String> subscribers) {
        this.subscribers = subscribers;
    }

    public List<JsonDocumentation> getDocumentations() {
        return documentations;
    }

    public void setDocumentations(List<JsonDocumentation> documentations) {
        this.documentations = documentations;
    }
}
