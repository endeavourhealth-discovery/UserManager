package org.endeavourhealth.datasharingmanagermodel.models.database;

import org.endeavourhealth.datasharingmanagermodel.PersistenceManager;
import org.endeavourhealth.datasharingmanagermodel.models.json.JsonDataFlow;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "data_flow", schema = "data_sharing_manager")
public class DataFlowEntity {
    private String uuid;
    private String purpose;
    private String name;
    private short storageProtocolId;
    private Short consentModelId;
    private Short deidentificationLevel;

    @Id
    @Column(name = "uuid")
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Basic
    @Column(name = "purpose")
    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    @Basic
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "storage_protocol_id")
    public short getStorageProtocolId() {
        return storageProtocolId;
    }

    public void setStorageProtocolId(short storageProtocolId) {
        this.storageProtocolId = storageProtocolId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataFlowEntity that = (DataFlowEntity) o;
        return storageProtocolId == that.storageProtocolId &&
                Objects.equals(uuid, that.uuid) &&
                Objects.equals(purpose, that.purpose) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(uuid, purpose, name, storageProtocolId);
    }

    @Basic
    @Column(name = "consent_model_id")
    public Short getConsentModelId() {
        return consentModelId;
    }

    public void setConsentModelId(Short consentModelId) {
        this.consentModelId = consentModelId;
    }

    @Basic
    @Column(name = "deidentification_level")
    public Short getDeidentificationLevel() {
        return deidentificationLevel;
    }

    public void setDeidentificationLevel(Short deidentificationLevel) {
        this.deidentificationLevel = deidentificationLevel;
    }

    public static List<DataFlowEntity> getAllDataFlows() throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<DataFlowEntity> cq = cb.createQuery(DataFlowEntity.class);
        Root<DataFlowEntity> rootEntry = cq.from(DataFlowEntity.class);
        CriteriaQuery<DataFlowEntity> all = cq.select(rootEntry);
        TypedQuery<DataFlowEntity> allQuery = entityManager.createQuery(all);
        List<DataFlowEntity> ret =  allQuery.getResultList();

        entityManager.close();

        return ret;
    }

    public static DataFlowEntity getDataFlow(String uuid) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        DataFlowEntity ret = entityManager.find(DataFlowEntity.class, uuid);

        entityManager.close();

        return ret;
    }

    public static void updateDataFlow(JsonDataFlow dataFlow) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        DataFlowEntity dataFlowEntity = entityManager.find(DataFlowEntity.class, dataFlow.getUuid());
        entityManager.getTransaction().begin();
        dataFlowEntity.setName(dataFlow.getName());
        dataFlowEntity.setStorageProtocolId(dataFlow.getStorageProtocolId());
        dataFlowEntity.setDeidentificationLevel(dataFlow.getDeidentificationLevel());
        dataFlowEntity.setConsentModelId(dataFlow.getConsentModelId());
        dataFlowEntity.setPurpose(dataFlow.getPurpose());
        entityManager.getTransaction().commit();

        entityManager.close();
    }

    public static void saveDataFlow(JsonDataFlow dataFlow) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        DataFlowEntity dataFlowEntity = new DataFlowEntity();
        entityManager.getTransaction().begin();
        dataFlowEntity.setName(dataFlow.getName());
        dataFlowEntity.setStorageProtocolId(dataFlow.getStorageProtocolId());
        dataFlowEntity.setDeidentificationLevel(dataFlow.getDeidentificationLevel());
        dataFlowEntity.setConsentModelId(dataFlow.getConsentModelId());
        dataFlowEntity.setPurpose(dataFlow.getPurpose());
        dataFlowEntity.setUuid(dataFlow.getUuid());
        entityManager.persist(dataFlowEntity);
        entityManager.getTransaction().commit();

        entityManager.close();
    }

    public static void deleteDataFlow(String uuid) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        DataFlowEntity dataFlowEntity = entityManager.find(DataFlowEntity.class, uuid);
        entityManager.getTransaction().begin();
        entityManager.remove(dataFlowEntity);
        entityManager.getTransaction().commit();

        entityManager.close();
    }

    public static List<DataFlowEntity> search(String expression) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<DataFlowEntity> cq = cb.createQuery(DataFlowEntity.class);
        Root<DataFlowEntity> rootEntry = cq.from(DataFlowEntity.class);

        Predicate predicate = cb.like(cb.upper(rootEntry.get("name")), "%" + expression.toUpperCase() + "%");

        cq.where(predicate);
        TypedQuery<DataFlowEntity> query = entityManager.createQuery(cq);
        List<DataFlowEntity> ret = query.getResultList();

        entityManager.close();

        return ret;
    }

    public static List<DataFlowEntity> getDataFlowsFromList(List<String> dataFlows) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<DataFlowEntity> cq = cb.createQuery(DataFlowEntity.class);
        Root<DataFlowEntity> rootEntry = cq.from(DataFlowEntity.class);

        Predicate predicate = rootEntry.get("uuid").in(dataFlows);

        cq.where(predicate);
        TypedQuery<DataFlowEntity> query = entityManager.createQuery(cq);

        List<DataFlowEntity> ret = query.getResultList();

        entityManager.close();

        return ret;
    }
}
