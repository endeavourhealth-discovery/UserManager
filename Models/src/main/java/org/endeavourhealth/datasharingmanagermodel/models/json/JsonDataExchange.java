package org.endeavourhealth.datasharingmanagermodel.models.json;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;
import java.util.UUID;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonDataExchange {
    private String uuid = null;
    private String name = null;
    private boolean publisher = false;
    private String systemName = null;
    private Short directionId = null;
    private Short flowScheduleId = null;
    private Integer approximateVolume = null;
    private Short dataExchangeMethodId = null;
    private Short securityInfrastructureId = null;
    private Short securityArchitectureId = null;
    private Short flowStatusId = null;
    private String endpoint = null;
    private Map<UUID, String> dataFlows = null;

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

    public boolean isPublisher() {
        return publisher;
    }

    public void setPublisher(boolean publisher) {
        this.publisher = publisher;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public Short getDirectionId() {
        return directionId;
    }

    public void setDirectionId(Short directionId) {
        this.directionId = directionId;
    }

    public Short getFlowScheduleId() {
        return flowScheduleId;
    }

    public void setFlowScheduleId(Short flowScheduleId) {
        this.flowScheduleId = flowScheduleId;
    }

    public Integer getApproximateVolume() {
        return approximateVolume;
    }

    public void setApproximateVolume(Integer approximateVolume) {
        this.approximateVolume = approximateVolume;
    }

    public Short getDataExchangeMethodId() {
        return dataExchangeMethodId;
    }

    public void setDataExchangeMethodId(Short dataExchangeMethodId) {
        this.dataExchangeMethodId = dataExchangeMethodId;
    }

    public Short getSecurityInfrastructureId() {
        return securityInfrastructureId;
    }

    public void setSecurityInfrastructureId(Short securityInfrastructureId) {
        this.securityInfrastructureId = securityInfrastructureId;
    }

    public Short getSecurityArchitectureId() {
        return securityArchitectureId;
    }

    public void setSecurityArchitectureId(Short securityArchitectureId) {
        this.securityArchitectureId = securityArchitectureId;
    }

    public Short getFlowStatusId() {
        return flowStatusId;
    }

    public void setFlowStatusId(Short flowStatusId) {
        this.flowStatusId = flowStatusId;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public Map<UUID, String> getDataFlows() {
        return dataFlows;
    }

    public void setDataFlows(Map<UUID, String> dataFlows) {
        this.dataFlows = dataFlows;
    }
}
