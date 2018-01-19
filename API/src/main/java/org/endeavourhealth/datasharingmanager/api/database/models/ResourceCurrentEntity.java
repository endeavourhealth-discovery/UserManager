package org.endeavourhealth.datasharingmanager.api.database.models;

import org.endeavourhealth.datasharingmanager.api.database.PersistenceManager;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "resource_current", schema = "ehr", catalog = "")
@IdClass(ResourceCurrentEntityPK.class)
public class ResourceCurrentEntity {
    private String serviceId;
    private String systemId;
    private String resourceType;
    private String resourceId;
    private Timestamp updatedAt;
    private String patientId;
    private String resourceData;
    private Long resourceChecksum;
    private String resourceMetadata;
    private String resourceDataNew;

    @Id
    @Column(name = "service_id")
    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    @Id
    @Column(name = "system_id")
    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    @Id
    @Column(name = "resource_type")
    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    @Id
    @Column(name = "resource_id")
    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    @Basic
    @Column(name = "updated_at")
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Id
    @Column(name = "patient_id")
    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    @Basic
    @Column(name = "resource_data")
    public String getResourceData() {
        return resourceData;
    }

    public void setResourceData(String resourceData) {
        this.resourceData = resourceData;
    }

    @Basic
    @Column(name = "resource_checksum")
    public Long getResourceChecksum() {
        return resourceChecksum;
    }

    public void setResourceChecksum(Long resourceChecksum) {
        this.resourceChecksum = resourceChecksum;
    }

    @Basic
    @Column(name = "resource_metadata")
    public String getResourceMetadata() {
        return resourceMetadata;
    }

    public void setResourceMetadata(String resourceMetadata) {
        this.resourceMetadata = resourceMetadata;
    }

    @Basic
    @Column(name = "resource_data_new")
    public String getResourceDataNew() {
        return resourceDataNew;
    }

    public void setResourceDataNew(String resourceDataNew) {
        this.resourceDataNew = resourceDataNew;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResourceCurrentEntity that = (ResourceCurrentEntity) o;
        return Objects.equals(serviceId, that.serviceId) &&
                Objects.equals(systemId, that.systemId) &&
                Objects.equals(resourceType, that.resourceType) &&
                Objects.equals(resourceId, that.resourceId) &&
                Objects.equals(updatedAt, that.updatedAt) &&
                Objects.equals(patientId, that.patientId) &&
                Objects.equals(resourceData, that.resourceData) &&
                Objects.equals(resourceChecksum, that.resourceChecksum) &&
                Objects.equals(resourceMetadata, that.resourceMetadata) &&
                Objects.equals(resourceDataNew, that.resourceDataNew);
    }

    @Override
    public int hashCode() {

        return Objects.hash(serviceId, systemId, resourceType, resourceId, updatedAt, patientId, resourceData, resourceChecksum, resourceMetadata, resourceDataNew);
    }

    public static List<String> getResourceData(List<PseudoIdMapEntity> patientList) throws Exception {
        EntityManager entityManager = PersistenceManager.getEhrEntityManager();

        List<String> patientIds = new ArrayList<>();
        for (PseudoIdMapEntity entity : patientList) {
            patientIds.add(entity.getPatientId());
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ResourceCurrentEntity> cq = cb.createQuery(ResourceCurrentEntity.class);
        Root<ResourceCurrentEntity> rootEntry = cq.from(ResourceCurrentEntity.class);

        Predicate predicate = cb.and(rootEntry.get("patientId").in(patientIds), cb.equal(rootEntry.get("resourceType"), "Patient"));
        cq.where(predicate);

        TypedQuery<ResourceCurrentEntity> query = entityManager.createQuery(cq);
        List<ResourceCurrentEntity> ret = query.getResultList();

        List<String> resources = new ArrayList<>();

        for (ResourceCurrentEntity currentEntity : ret) {
            resources.add(currentEntity.resourceData);
        }

        entityManager.close();

        return resources;
    }
}
