package org.endeavourhealth.datasharingmanagermodel.models.database;

import org.endeavourhealth.datasharingmanagermodel.PersistenceManager;
import org.endeavourhealth.datasharingmanagermodel.models.json.JsonDataSet;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "dataset", schema = "data_sharing_manager")
public class DatasetEntity {
    private String uuid;
    private String name;
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
        DatasetEntity that = (DatasetEntity) o;
        return Objects.equals(uuid, that.uuid) &&
                Objects.equals(name, that.name) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {

        return Objects.hash(uuid, name, description);
    }

    public static List<DatasetEntity> getAllDataSets() throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<DatasetEntity> cq = cb.createQuery(DatasetEntity.class);
        Root<DatasetEntity> rootEntry = cq.from(DatasetEntity.class);
        CriteriaQuery<DatasetEntity> all = cq.select(rootEntry);
        TypedQuery<DatasetEntity> allQuery = entityManager.createQuery(all);
        List<DatasetEntity> ret = allQuery.getResultList();

        entityManager.close();

        return ret;
    }

    public static List<DatasetEntity> getDataSetsFromList(List<String> datasets) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<DatasetEntity> cq = cb.createQuery(DatasetEntity.class);
        Root<DatasetEntity> rootEntry = cq.from(DatasetEntity.class);

        Predicate predicate = rootEntry.get("uuid").in(datasets);

        cq.where(predicate);
        TypedQuery<DatasetEntity> query = entityManager.createQuery(cq);

        List<DatasetEntity> ret = query.getResultList();

        entityManager.close();

        return ret;
    }

    public static DatasetEntity getDataSet(String uuid) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        DatasetEntity ret = entityManager.find(DatasetEntity.class, uuid);
        entityManager.close();

        return ret;
    }

    public static void updateDataSet(JsonDataSet dataset) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        DatasetEntity dataSetEntity = entityManager.find(DatasetEntity.class, dataset.getUuid());
        entityManager.getTransaction().begin();
        dataSetEntity.setName(dataset.getName());
        dataSetEntity.setDescription(dataset.getDescription());
        entityManager.getTransaction().commit();

        entityManager.close();
    }

    public static void saveDataSet(JsonDataSet dataset) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        DatasetEntity dataSetEntity = new DatasetEntity();
        entityManager.getTransaction().begin();
        dataSetEntity.setUuid(dataset.getUuid());
        dataSetEntity.setName(dataset.getName());
        dataSetEntity.setDescription(dataset.getDescription());
        entityManager.persist(dataSetEntity);
        entityManager.getTransaction().commit();

        entityManager.close();
    }

    public static void deleteDataSet(String uuid) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        DatasetEntity dataSetEntity = entityManager.find(DatasetEntity.class, uuid);
        entityManager.getTransaction().begin();
        entityManager.remove(dataSetEntity);
        entityManager.getTransaction().commit();

        entityManager.close();
    }

    public static List<DatasetEntity> search(String expression) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<DatasetEntity> cq = cb.createQuery(DatasetEntity.class);
        Root<DatasetEntity> rootEntry = cq.from(DatasetEntity.class);

        Predicate predicate = cb.like(cb.upper(rootEntry.get("name")), "%" + expression.toUpperCase() + "%");

        cq.where(predicate);
        TypedQuery<DatasetEntity> query = entityManager.createQuery(cq);
        List<DatasetEntity> ret = query.getResultList();

        entityManager.close();

        return ret;
    }
}
