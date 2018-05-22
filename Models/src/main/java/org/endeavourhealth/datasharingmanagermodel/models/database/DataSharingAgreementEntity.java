package org.endeavourhealth.datasharingmanagermodel.models.database;


import org.endeavourhealth.datasharingmanagermodel.PersistenceManager;
import org.endeavourhealth.datasharingmanagermodel.models.enums.MapType;
import org.endeavourhealth.datasharingmanagermodel.models.json.JsonDSA;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.sql.Date;
import java.util.List;

@Entity
@Table(name = "data_sharing_agreement", schema = "data_sharing_manager")
public class DataSharingAgreementEntity {
    private String uuid;
    private String name;
    private String description;
    private String derivation;
    private short dsaStatusId;
    private short consentModelId;
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
    @Column(name = "description", nullable = true, length = 10000)
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
    @Column(name = "dsa_status_id", nullable = false)
    public short getDsaStatusId() {
        return dsaStatusId;
    }

    public void setDsaStatusId(short dsaStatusId) {
        this.dsaStatusId = dsaStatusId;
    }

    @Basic
    @Column(name = "consent_model_id", nullable = false)
    public short getConsentModelId() {
        return consentModelId;
    }

    public void setConsentModelId(short consentModelId) {
        this.consentModelId = consentModelId;
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

        DataSharingAgreementEntity that = (DataSharingAgreementEntity) o;

        if (dsaStatusId != that.dsaStatusId) return false;
        if (consentModelId != that.consentModelId) return false;
        if (uuid != null ? !uuid.equals(that.uuid) : that.uuid != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (derivation != null ? !derivation.equals(that.derivation) : that.derivation != null) return false;
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
        result = 31 * result + (int) dsaStatusId;
        result = 31 * result + (int) consentModelId;
        result = 31 * result + (startDate != null ? startDate.hashCode() : 0);
        result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
        return result;
    }

    public static List<DataSharingAgreementEntity> getAllDSAs() throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<DataSharingAgreementEntity> cq = cb.createQuery(DataSharingAgreementEntity.class);
        Root<DataSharingAgreementEntity> rootEntry = cq.from(DataSharingAgreementEntity.class);
        CriteriaQuery<DataSharingAgreementEntity> all = cq.select(rootEntry);
        TypedQuery<DataSharingAgreementEntity> allQuery = entityManager.createQuery(all);
        List<DataSharingAgreementEntity> ret = allQuery.getResultList();

        entityManager.close();

        return ret;
    }

    public static DataSharingAgreementEntity getDSA(String uuid) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        DataSharingAgreementEntity ret = entityManager.find(DataSharingAgreementEntity.class, uuid);

        entityManager.close();

        return ret;
    }

    public static void updateDSA(JsonDSA dsa) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        DataSharingAgreementEntity dsaEntity = entityManager.find(DataSharingAgreementEntity.class, dsa.getUuid());
        entityManager.getTransaction().begin();
        dsaEntity.setName(dsa.getName());
        dsaEntity.setDescription(dsa.getDescription());
        dsaEntity.setDerivation(dsa.getDerivation());
        dsaEntity.setDsaStatusId(dsa.getDsaStatusId());
        dsaEntity.setConsentModelId(dsa.getConsentModelId());
        if (dsa.getStartDate() != null) {
            dsaEntity.setStartDate(Date.valueOf(dsa.getStartDate()));
        }
        if (dsa.getEndDate() != null) {
            dsaEntity.setEndDate(Date.valueOf(dsa.getEndDate()));
        }
        entityManager.getTransaction().commit();

        entityManager.close();
    }

    public static void saveDSA(JsonDSA dsa) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        DataSharingAgreementEntity dsaEntity = new DataSharingAgreementEntity();
        entityManager.getTransaction().begin();
        dsaEntity.setName(dsa.getName());
        dsaEntity.setDescription(dsa.getDescription());
        dsaEntity.setDerivation(dsa.getDerivation());
        dsaEntity.setDsaStatusId(dsa.getDsaStatusId());
        dsaEntity.setConsentModelId(dsa.getConsentModelId());
        if (dsa.getStartDate() != null) {
            dsaEntity.setStartDate(Date.valueOf(dsa.getStartDate()));
        }
        if (dsa.getEndDate() != null) {
            dsaEntity.setEndDate(Date.valueOf(dsa.getEndDate()));
        }
        dsaEntity.setUuid(dsa.getUuid());
        entityManager.persist(dsaEntity);
        entityManager.getTransaction().commit();

        entityManager.close();
    }

    public static void deleteDSA(String uuid) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        DataSharingAgreementEntity dsaEntity = entityManager.find(DataSharingAgreementEntity.class, uuid);
        entityManager.getTransaction().begin();
        entityManager.remove(dsaEntity);
        entityManager.getTransaction().commit();

        entityManager.close();
    }

    public static List<DataSharingAgreementEntity> search(String expression) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<DataSharingAgreementEntity> cq = cb.createQuery(DataSharingAgreementEntity.class);
        Root<DataSharingAgreementEntity> rootEntry = cq.from(DataSharingAgreementEntity.class);

        Predicate predicate = cb.or(cb.like(cb.upper(rootEntry.get("name")), "%" + expression.toUpperCase() + "%"),
                cb.like(cb.upper(rootEntry.get("description")), "%" + expression.toUpperCase() + "%"));

        cq.where(predicate);
        TypedQuery<DataSharingAgreementEntity> query = entityManager.createQuery(cq);
        List<DataSharingAgreementEntity> ret = query.getResultList();

        entityManager.close();

        return ret;
    }

    public static List<DataSharingAgreementEntity> getDSAsFromList(List<String> dsas) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<DataSharingAgreementEntity> cq = cb.createQuery(DataSharingAgreementEntity.class);
        Root<DataSharingAgreementEntity> rootEntry = cq.from(DataSharingAgreementEntity.class);

        Predicate predicate = rootEntry.get("uuid").in(dsas);

        cq.where(predicate);
        TypedQuery<DataSharingAgreementEntity> query = entityManager.createQuery(cq);

        List<DataSharingAgreementEntity> ret = query.getResultList();

        entityManager.close();

        return ret;
    }

    public static List<String> checkDataSharingAgreementsForOrganisation(String odsCode) throws Exception {

        EntityManager entityManager = PersistenceManager.getEntityManager();

        Query query = entityManager.createQuery(
                "select de.endpoint from DataSharingAgreementEntity dsa " +
                        "inner join MasterMappingEntity mm on mm.parentUuid = dsa.uuid and mm.parentMapTypeId = :dpaType " +
                        "inner join OrganisationEntity o on o.uuid = mm.childUuid and mm.childMapTypeId = :subscriberType " +
                        "inner join MasterMappingEntity mdf on mdf.parentUuid = dsa.uuid and mdf.parentMapTypeId = :dpaType " +
                        "inner join DataFlowEntity df on df.uuid = mdf.childUuid and mm.childMapTypeId = :dataFlowType " +
                        "inner join MasterMappingEntity mde on mde.parentUuid = df.uuid and mde.parentMapTypeId = :dataFlowType " +
                        "inner join DataExchangeEntity de on de.uuid = mde.childUuid and mde.childMapTypeId = :exchangeType " +
                        "where o.odsCode = :ods " +
                        "and (dsa.startDate is not null and dsa.startDate <= current_date) " +
                        "and (dsa.endDate is null or dsa.endDate >= current_date) " +
                        "and dsa.dsaStatusId = 0 ");
        query.setParameter("dpaType", MapType.DATAPROCESSINGAGREEMENT.getMapType());
        query.setParameter("ods", odsCode);
        query.setParameter("subscriberType", MapType.SUBSCRIBER.getMapType());
        query.setParameter("dataFlowType", MapType.DATAFLOW.getMapType());
        query.setParameter("exchangeType", MapType.DATAEXCHANGE.getMapType());

        List<String> result = query.getResultList();

        entityManager.close();

        return result;
    }
}
