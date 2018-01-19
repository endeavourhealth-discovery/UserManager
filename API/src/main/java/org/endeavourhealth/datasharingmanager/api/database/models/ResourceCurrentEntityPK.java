package org.endeavourhealth.datasharingmanager.api.database.models;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

public class ResourceCurrentEntityPK implements Serializable {
    private String serviceId;
    private String systemId;
    private String resourceType;
    private String resourceId;
    private String patientId;

    @Column(name = "service_id")
    @Id
    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    @Column(name = "system_id")
    @Id
    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    @Column(name = "resource_type")
    @Id
    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    @Column(name = "resource_id")
    @Id
    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    @Column(name = "patient_id")
    @Id
    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResourceCurrentEntityPK that = (ResourceCurrentEntityPK) o;
        return Objects.equals(serviceId, that.serviceId) &&
                Objects.equals(systemId, that.systemId) &&
                Objects.equals(resourceType, that.resourceType) &&
                Objects.equals(resourceId, that.resourceId) &&
                Objects.equals(patientId, that.patientId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(serviceId, systemId, resourceType, resourceId, patientId);
    }
}
