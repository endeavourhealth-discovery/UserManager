package org.endeavourhealth.datasharingmanagermodel.models.database;

import org.endeavourhealth.datasharingmanagermodel.PersistenceManager;
import org.endeavourhealth.datasharingmanagermodel.models.json.JsonCohort;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "cohort", schema = "data_sharing_manager")
public class CohortEntity {
    private String uuid;
    private String name;
    private Short consentModelId;
    private String description;

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
    @Column(name = "consent_model_id")
    public Short getConsentModelId() {
        return consentModelId;
    }

    public void setConsentModelId(Short consentModelId) {
        this.consentModelId = consentModelId;
    }

    @Basic
    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CohortEntity that = (CohortEntity) o;
        return Objects.equals(uuid, that.uuid) &&
                Objects.equals(name, that.name) &&
                Objects.equals(consentModelId, that.consentModelId) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {

        return Objects.hash(uuid, name, consentModelId, description);
    }



    public static List<CohortEntity> getAllCohorts() throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<CohortEntity> cq = cb.createQuery(CohortEntity.class);
        Root<CohortEntity> rootEntry = cq.from(CohortEntity.class);
        CriteriaQuery<CohortEntity> all = cq.select(rootEntry);
        TypedQuery<CohortEntity> allQuery = entityManager.createQuery(all);
        List<CohortEntity> ret = allQuery.getResultList();

        entityManager.close();

        return ret;
    }

    public static List<CohortEntity> getCohortsFromList(List<String> cohorts) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<CohortEntity> cq = cb.createQuery(CohortEntity.class);
        Root<CohortEntity> rootEntry = cq.from(CohortEntity.class);

        Predicate predicate = rootEntry.get("uuid").in(cohorts);

        cq.where(predicate);
        TypedQuery<CohortEntity> query = entityManager.createQuery(cq);

        List<CohortEntity> ret = query.getResultList();

        entityManager.close();

        return ret;
    }

    public static CohortEntity getCohort(String uuid) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        CohortEntity ret = entityManager.find(CohortEntity.class, uuid);
        entityManager.close();

        return ret;
    }

    public static void updateCohort(JsonCohort cohort) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        CohortEntity cohortEntity = entityManager.find(CohortEntity.class, cohort.getUuid());
        entityManager.getTransaction().begin();
        cohortEntity.setName(cohort.getName());
        cohortEntity.setConsentModelId(cohort.getConsentModelId());
        cohortEntity.setDescription(cohort.getDescription());
        entityManager.getTransaction().commit();

        entityManager.close();
    }

    public static void saveCohort(JsonCohort cohort) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        CohortEntity cohortEntity = new CohortEntity();
        entityManager.getTransaction().begin();
        cohortEntity.setName(cohort.getName());
        cohortEntity.setConsentModelId(cohort.getConsentModelId());
        cohortEntity.setDescription(cohort.getDescription());
        cohortEntity.setUuid(cohort.getUuid());
        entityManager.persist(cohortEntity);
        entityManager.getTransaction().commit();

        entityManager.close();
    }

    public static void deleteCohort(String uuid) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        CohortEntity cohortEntity = entityManager.find(CohortEntity.class, uuid);
        entityManager.getTransaction().begin();
        entityManager.remove(cohortEntity);
        entityManager.getTransaction().commit();

        entityManager.close();
    }

    public static List<CohortEntity> search(String expression) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<CohortEntity> cq = cb.createQuery(CohortEntity.class);
        Root<CohortEntity> rootEntry = cq.from(CohortEntity.class);

        Predicate predicate = cb.or(cb.like(cb.upper(rootEntry.get("name")), "%" + expression.toUpperCase() + "%"),
                cb.like(cb.upper(rootEntry.get("description")), "%" + expression.toUpperCase() + "%"));

        cq.where(predicate);
        TypedQuery<CohortEntity> query = entityManager.createQuery(cq);
        List<CohortEntity> ret = query.getResultList();

        entityManager.close();

        return ret;
    }
}
