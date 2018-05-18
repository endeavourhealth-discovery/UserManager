package org.endeavourhealth.datasharingmanagermodel.models.database;

import org.endeavourhealth.datasharingmanagermodel.PersistenceManager;
import org.endeavourhealth.datasharingmanagermodel.models.json.JsonDataExchange;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "data_exchange", schema = "data_sharing_manager")
public class DataExchangeEntity {
    private String uuid;
    private String name;
    private byte publisher;
    private Short directionId;
    private String systemName;
    private short flowScheduleId;
    private int approximateVolume;
    private short dataExchangeMethodId;
    private short flowStatusId;
    private short securityInfrastructureId;
    private short securityArchitectureId;
    private String endpoint;

    @Id
    @Column(name = "uuid")
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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
    @Column(name = "publisher")
    public byte getPublisher() {
        return publisher;
    }

    public void setPublisher(byte publisher) {
        this.publisher = publisher;
    }

    @Basic
    @Column(name = "direction_id")
    public Short getDirectionId() {
        return directionId;
    }

    public void setDirectionId(Short directionId) {
        this.directionId = directionId;
    }

    @Basic
    @Column(name = "system_name")
    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    @Basic
    @Column(name = "flow_schedule_id")
    public short getFlowScheduleId() {
        return flowScheduleId;
    }

    public void setFlowScheduleId(short flowScheduleId) {
        this.flowScheduleId = flowScheduleId;
    }

    @Basic
    @Column(name = "approximate_volume")
    public int getApproximateVolume() {
        return approximateVolume;
    }

    public void setApproximateVolume(int approximateVolume) {
        this.approximateVolume = approximateVolume;
    }

    @Basic
    @Column(name = "data_exchange_method_id")
    public short getDataExchangeMethodId() {
        return dataExchangeMethodId;
    }

    public void setDataExchangeMethodId(short dataExchangeMethodId) {
        this.dataExchangeMethodId = dataExchangeMethodId;
    }

    @Basic
    @Column(name = "flow_status_id")
    public short getFlowStatusId() {
        return flowStatusId;
    }

    public void setFlowStatusId(short flowStatusId) {
        this.flowStatusId = flowStatusId;
    }

    @Basic
    @Column(name = "security_infrastructure_id")
    public short getSecurityInfrastructureId() {
        return securityInfrastructureId;
    }

    public void setSecurityInfrastructureId(short securityInfrastructureId) {
        this.securityInfrastructureId = securityInfrastructureId;
    }

    @Basic
    @Column(name = "security_architecture_id")
    public short getSecurityArchitectureId() {
        return securityArchitectureId;
    }

    public void setSecurityArchitectureId(short securityArchitectureId) {
        this.securityArchitectureId = securityArchitectureId;
    }

    @Basic
    @Column(name = "endpoint")
    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataExchangeEntity that = (DataExchangeEntity) o;
        return publisher == that.publisher &&
                flowScheduleId == that.flowScheduleId &&
                approximateVolume == that.approximateVolume &&
                dataExchangeMethodId == that.dataExchangeMethodId &&
                flowStatusId == that.flowStatusId &&
                securityInfrastructureId == that.securityInfrastructureId &&
                securityArchitectureId == that.securityArchitectureId &&
                Objects.equals(uuid, that.uuid) &&
                Objects.equals(name, that.name) &&
                Objects.equals(directionId, that.directionId) &&
                Objects.equals(systemName, that.systemName) &&
                Objects.equals(endpoint, that.endpoint);
    }

    @Override
    public int hashCode() {

        return Objects.hash(uuid, name, publisher, directionId, systemName, flowScheduleId, approximateVolume, dataExchangeMethodId, flowStatusId, securityInfrastructureId, securityArchitectureId, endpoint);
    }

    public static List<DataExchangeEntity> getAllDataExchanges() throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<DataExchangeEntity> cq = cb.createQuery(DataExchangeEntity.class);
        Root<DataExchangeEntity> rootEntry = cq.from(DataExchangeEntity.class);
        CriteriaQuery<DataExchangeEntity> all = cq.select(rootEntry);
        TypedQuery<DataExchangeEntity> allQuery = entityManager.createQuery(all);
        List<DataExchangeEntity> ret =  allQuery.getResultList();

        entityManager.close();

        return ret;
    }

    public static DataExchangeEntity getDataExchange(String uuid) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        DataExchangeEntity ret = entityManager.find(DataExchangeEntity.class, uuid);

        entityManager.close();

        return ret;
    }

    public static void updateDataExchange(JsonDataExchange dataExchange) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        DataExchangeEntity dataExchangeEntity = entityManager.find(DataExchangeEntity.class, dataExchange.getUuid());
        entityManager.getTransaction().begin();
        dataExchangeEntity.setName(dataExchange.getName());
        dataExchangeEntity.setPublisher(dataExchange.isPublisher() ? (byte)1 : (byte)0);
        dataExchangeEntity.setSystemName(dataExchange.getSystemName());
        dataExchangeEntity.setDirectionId(dataExchange.getDirectionId());
        dataExchangeEntity.setFlowScheduleId(dataExchange.getFlowScheduleId());
        dataExchangeEntity.setApproximateVolume(dataExchange.getApproximateVolume());
        dataExchangeEntity.setDataExchangeMethodId(dataExchange.getDataExchangeMethodId());
        dataExchangeEntity.setSecurityInfrastructureId(dataExchange.getSecurityInfrastructureId());
        dataExchangeEntity.setSecurityArchitectureId(dataExchange.getSecurityArchitectureId());
        dataExchangeEntity.setEndpoint(dataExchange.getEndpoint());
        dataExchangeEntity.setFlowStatusId(dataExchange.getFlowStatusId());

        entityManager.getTransaction().commit();

        entityManager.close();
    }

    public static void saveDataExchange(JsonDataExchange dataExchange) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        DataExchangeEntity dataExchangeEntity = new DataExchangeEntity();
        entityManager.getTransaction().begin();
        dataExchangeEntity.setName(dataExchange.getName());
        dataExchangeEntity.setPublisher(dataExchange.isPublisher() ? (byte)1 : (byte)0);
        dataExchangeEntity.setSystemName(dataExchange.getSystemName());
        dataExchangeEntity.setDirectionId(dataExchange.getDirectionId());
        dataExchangeEntity.setFlowScheduleId(dataExchange.getFlowScheduleId());
        dataExchangeEntity.setApproximateVolume(dataExchange.getApproximateVolume());
        dataExchangeEntity.setDataExchangeMethodId(dataExchange.getDataExchangeMethodId());
        dataExchangeEntity.setSecurityInfrastructureId(dataExchange.getSecurityInfrastructureId());
        dataExchangeEntity.setSecurityArchitectureId(dataExchange.getSecurityArchitectureId());
        dataExchangeEntity.setEndpoint(dataExchange.getEndpoint());
        dataExchangeEntity.setFlowStatusId(dataExchange.getFlowStatusId());
        dataExchangeEntity.setUuid(dataExchange.getUuid());
        entityManager.persist(dataExchangeEntity);
        entityManager.getTransaction().commit();

        entityManager.close();
    }

    public static void deleteDataExchange(String uuid) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        DataExchangeEntity DataExchangeEntity = entityManager.find(DataExchangeEntity.class, uuid);
        entityManager.getTransaction().begin();
        entityManager.remove(DataExchangeEntity);
        entityManager.getTransaction().commit();

        entityManager.close();
    }

    public static List<DataExchangeEntity> search(String expression) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<DataExchangeEntity> cq = cb.createQuery(DataExchangeEntity.class);
        Root<DataExchangeEntity> rootEntry = cq.from(DataExchangeEntity.class);

        Predicate predicate = cb.like(cb.upper(rootEntry.get("name")), "%" + expression.toUpperCase() + "%");

        cq.where(predicate);
        TypedQuery<DataExchangeEntity> query = entityManager.createQuery(cq);
        List<DataExchangeEntity> ret = query.getResultList();

        entityManager.close();

        return ret;
    }

    public static List<DataExchangeEntity> getDataExchangesFromList(List<String> dataFlows) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<DataExchangeEntity> cq = cb.createQuery(DataExchangeEntity.class);
        Root<DataExchangeEntity> rootEntry = cq.from(DataExchangeEntity.class);

        Predicate predicate = rootEntry.get("uuid").in(dataFlows);

        cq.where(predicate);
        TypedQuery<DataExchangeEntity> query = entityManager.createQuery(cq);

        List<DataExchangeEntity> ret = query.getResultList();

        entityManager.close();

        return ret;
    }
}
