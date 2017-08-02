package org.endeavourhealth.datasharingmanager.api.database.models;

import org.endeavourhealth.datasharingmanager.api.database.MapType;
import org.endeavourhealth.datasharingmanager.api.database.PersistenceManager;
import org.endeavourhealth.datasharingmanager.api.json.JsonDPA;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.sql.Date;
import java.util.List;

@Entity
@Table(name = "data_processing_agreement", schema = "data_sharing_manager")
public class DataProcessingAgreementEntity {
    private String uuid;
    private String name;
    private String description;
    private String derivation;
    private String publisherInformation;
    private String publisherContractInformation;
    private String publisherDataset;
    private short dsaStatusId;
    private String returnToSenderPolicy;
    private Date startDate;
    private Date endDate;

    @Id
    @Column(name = "uuid", nullable = false, length = 36)
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Basic
    @Column(name = "name", nullable = false, length = 100)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "description", nullable = true, length = 100)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Basic
    @Column(name = "derivation", nullable = true, length = 100)
    public String getDerivation() {
        return derivation;
    }

    public void setDerivation(String derivation) {
        this.derivation = derivation;
    }

    @Basic
    @Column(name = "publisher_information", nullable = true, length = 100)
    public String getPublisherInformation() {
        return publisherInformation;
    }

    public void setPublisherInformation(String publisherInformation) {
        this.publisherInformation = publisherInformation;
    }

    @Basic
    @Column(name = "publisher_contract_information", nullable = true, length = 100)
    public String getPublisherContractInformation() {
        return publisherContractInformation;
    }

    public void setPublisherContractInformation(String publisherContractInformation) {
        this.publisherContractInformation = publisherContractInformation;
    }

    @Basic
    @Column(name = "publisher_dataset", nullable = true, length = 36)
    public String getPublisherDataset() {
        return publisherDataset;
    }

    public void setPublisherDataset(String publisherDataset) {
        this.publisherDataset = publisherDataset;
    }

    @Basic
    @Column(name = "dsa_status_id", nullable = false)
    public short getDsaStatusId() {
        return dsaStatusId;
    }

    public void setDsaStatusId(short dsaStatusId) {
        this.dsaStatusId = dsaStatusId;
    }

    @Basic
    @Column(name = "return_to_sender_policy", nullable = true, length = 100)
    public String getReturnToSenderPolicy() {
        return returnToSenderPolicy;
    }

    public void setReturnToSenderPolicy(String returnToSenderPolicy) {
        this.returnToSenderPolicy = returnToSenderPolicy;
    }

    @Basic
    @Column(name = "start_date", nullable = true)
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Basic
    @Column(name = "end_date", nullable = true)
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataProcessingAgreementEntity that = (DataProcessingAgreementEntity) o;

        if (dsaStatusId != that.dsaStatusId) return false;
        if (uuid != null ? !uuid.equals(that.uuid) : that.uuid != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (derivation != null ? !derivation.equals(that.derivation) : that.derivation != null) return false;
        if (publisherInformation != null ? !publisherInformation.equals(that.publisherInformation) : that.publisherInformation != null)
            return false;
        if (publisherContractInformation != null ? !publisherContractInformation.equals(that.publisherContractInformation) : that.publisherContractInformation != null)
            return false;
        if (publisherDataset != null ? !publisherDataset.equals(that.publisherDataset) : that.publisherDataset != null)
            return false;
        if (returnToSenderPolicy != null ? !returnToSenderPolicy.equals(that.returnToSenderPolicy) : that.returnToSenderPolicy != null)
            return false;
        if (startDate != null ? !startDate.equals(that.startDate) : that.startDate != null) return false;
        if (endDate != null ? !endDate.equals(that.endDate) : that.endDate != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = uuid != null ? uuid.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (derivation != null ? derivation.hashCode() : 0);
        result = 31 * result + (publisherInformation != null ? publisherInformation.hashCode() : 0);
        result = 31 * result + (publisherContractInformation != null ? publisherContractInformation.hashCode() : 0);
        result = 31 * result + (publisherDataset != null ? publisherDataset.hashCode() : 0);
        result = 31 * result + (int) dsaStatusId;
        result = 31 * result + (returnToSenderPolicy != null ? returnToSenderPolicy.hashCode() : 0);
        result = 31 * result + (startDate != null ? startDate.hashCode() : 0);
        result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
        return result;
    }

    public static List<DataProcessingAgreementEntity> getAllDPAs() throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<DataProcessingAgreementEntity> cq = cb.createQuery(DataProcessingAgreementEntity.class);
        Root<DataProcessingAgreementEntity> rootEntry = cq.from(DataProcessingAgreementEntity.class);
        CriteriaQuery<DataProcessingAgreementEntity> all = cq.select(rootEntry);
        TypedQuery<DataProcessingAgreementEntity> allQuery = entityManager.createQuery(all);
        List<DataProcessingAgreementEntity> ret =  allQuery.getResultList();

        entityManager.close();

        return ret;
    }

    public static DataProcessingAgreementEntity getDPA(String uuid) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        DataProcessingAgreementEntity ret = entityManager.find(DataProcessingAgreementEntity.class, uuid);

        entityManager.close();

        return ret;
    }

    public static void updateDPA(JsonDPA dpa) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        DataProcessingAgreementEntity dpaEntity = entityManager.find(DataProcessingAgreementEntity.class, dpa.getUuid());
        entityManager.getTransaction().begin();
        dpaEntity.setName(dpa.getName());
        dpaEntity.setDescription(dpa.getDescription());
        dpaEntity.setDerivation(dpa.getDerivation());
        dpaEntity.setPublisherInformation(dpa.getPublisherInformation());
        dpaEntity.setPublisherContractInformation(dpa.getPublisherContractInformation());
        dpaEntity.setPublisherDataset(dpa.getPublisherDataset());
        dpaEntity.setDsaStatusId(dpa.getDsaStatusId());
        dpaEntity.setReturnToSenderPolicy(dpa.getReturnToSenderPolicy());
        if (dpa.getStartDate() != null) {
            dpaEntity.setStartDate(Date.valueOf(dpa.getStartDate()));
        }
        if (dpa.getEndDate() != null) {
            dpaEntity.setEndDate(Date.valueOf(dpa.getEndDate()));
        }
        entityManager.getTransaction().commit();

        entityManager.close();
    }

    public static void saveDPA(JsonDPA dpa) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        DataProcessingAgreementEntity dpaEntity = new DataProcessingAgreementEntity();
        entityManager.getTransaction().begin();
        dpaEntity.setName(dpa.getName());
        dpaEntity.setName(dpa.getName());
        dpaEntity.setDescription(dpa.getDescription());
        dpaEntity.setDerivation(dpa.getDerivation());
        dpaEntity.setPublisherInformation(dpa.getPublisherInformation());
        dpaEntity.setPublisherContractInformation(dpa.getPublisherContractInformation());
        dpaEntity.setPublisherDataset(dpa.getPublisherDataset());
        dpaEntity.setDsaStatusId(dpa.getDsaStatusId());
        dpaEntity.setReturnToSenderPolicy(dpa.getReturnToSenderPolicy());
        if (dpa.getStartDate() != null) {
            dpaEntity.setStartDate(Date.valueOf(dpa.getStartDate()));
        }
        if (dpa.getEndDate() != null) {
            dpaEntity.setEndDate(Date.valueOf(dpa.getEndDate()));
        }
        dpaEntity.setUuid(dpa.getUuid());
        entityManager.persist(dpaEntity);
        entityManager.getTransaction().commit();

        entityManager.close();
    }

    public static void deleteDPA(String uuid) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        DataProcessingAgreementEntity dpaEntity = entityManager.find(DataProcessingAgreementEntity.class, uuid);
        entityManager.getTransaction().begin();
        entityManager.remove(dpaEntity);
        entityManager.getTransaction().commit();

        entityManager.close();
    }

    public static List<DataProcessingAgreementEntity> search(String expression) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<DataProcessingAgreementEntity> cq = cb.createQuery(DataProcessingAgreementEntity.class);
        Root<DataProcessingAgreementEntity> rootEntry = cq.from(DataProcessingAgreementEntity.class);

        Predicate predicate = cb.or(cb.like(cb.upper(rootEntry.get("name")), "%" + expression.toUpperCase() + "%"),
                cb.like(cb.upper(rootEntry.get("description")), "%" + expression.toUpperCase() + "%"));

        cq.where(predicate);
        TypedQuery<DataProcessingAgreementEntity> query = entityManager.createQuery(cq);
        List<DataProcessingAgreementEntity> ret = query.getResultList();

        entityManager.close();

        return ret;
    }

    public static List<DataProcessingAgreementEntity> getDPAsFromList(List<String> dpas) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<DataProcessingAgreementEntity> cq = cb.createQuery(DataProcessingAgreementEntity.class);
        Root<DataProcessingAgreementEntity> rootEntry = cq.from(DataProcessingAgreementEntity.class);

        Predicate predicate = rootEntry.get("uuid").in(dpas);

        cq.where(predicate);
        TypedQuery<DataProcessingAgreementEntity> query = entityManager.createQuery(cq);

        List<DataProcessingAgreementEntity> ret = query.getResultList();

        entityManager.close();

        return ret;
    }

    public static List<DataProcessingAgreementEntity> getDataProcessingAgreementsForOrganisation(String odsCode) throws Exception {

        EntityManager entityManager = PersistenceManager.getEntityManager();

        Query query = entityManager.createQuery(
                "select dpa from DataProcessingAgreementEntity dpa " +
                        "inner join MasterMappingEntity mm on mm.parentUuid = dpa.uuid and mm.parentMapTypeId = :dpaType " +
                        "inner join OrganisationEntity o on o.uuid = mm.childUuid " +
                        "where o.odsCode = :ods " +
                        "and mm.childMapTypeId = :publisherType");
        query.setParameter("dpaType", MapType.DATAPROCESSINGAGREEMENT.getMapType());
        query.setParameter("ods", odsCode);
        query.setParameter("publisherType", MapType.PUBLISHER.getMapType());

        List<DataProcessingAgreementEntity> result = query.getResultList();

        entityManager.close();

        return result;
    }
}
