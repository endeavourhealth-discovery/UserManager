package org.endeavourhealth.datasharingmanager.api.database.models;

import org.endeavourhealth.datasharingmanager.api.database.MapType;
import org.endeavourhealth.datasharingmanager.api.database.PersistenceManager;
import org.endeavourhealth.datasharingmanager.api.json.JsonDocumentation;

import javax.persistence.*;
import javax.persistence.criteria.*;
import java.util.List;

@Entity
@Table(name = "Documentation", schema = "OrganisationManager")
public class DocumentationEntity {
    private String uuid;
    private String title;
    private String filename;
    private String fileData;

    @Id
    @Column(name = "Uuid", nullable = false, length = 36)
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Basic
    @Column(name = "Title", nullable = false, length = 50)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Basic
    @Column(name = "Filename", nullable = false, length = 50)
    public String getFilename() {
        return filename;
    }

    public void setFilename(String fileName) {
        this.filename = fileName;
    }

    @Basic
    @Column(name = "FileData", nullable = false)
    public String getFileData() {
        return fileData;
    }

    public void setFileData(String fileData) {
        this.fileData = fileData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        org.endeavourhealth.datasharingmanager.api.database.models.DocumentationEntity that = (org.endeavourhealth.datasharingmanager.api.database.models.DocumentationEntity) o;

        if (uuid != null ? !uuid.equals(that.uuid) : that.uuid != null) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (filename != null ? !filename.equals(that.filename) : that.filename != null) return false;
        if (fileData != null ? !fileData.equals(that.fileData) : that.fileData != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = uuid != null ? uuid.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (filename != null ? filename.hashCode() : 0);
        result = 31 * result + (fileData != null ? fileData.hashCode() : 0);
        return result;
    }

    public static org.endeavourhealth.core.mySQLDatabase.models.DocumentationEntity getDocument(String uuid) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        org.endeavourhealth.core.mySQLDatabase.models.DocumentationEntity ret = entityManager.find(org.endeavourhealth.core.mySQLDatabase.models.DocumentationEntity.class, uuid);
        entityManager.close();

        return ret;
    }

    public static void saveDocument(JsonDocumentation document) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        org.endeavourhealth.core.mySQLDatabase.models.DocumentationEntity documentationEntity = new org.endeavourhealth.core.mySQLDatabase.models.DocumentationEntity();
        documentationEntity.setUuid(document.getUuid());
        documentationEntity.setFilename(document.getFilename());
        documentationEntity.setTitle(document.getTitle());
        documentationEntity.setFileData(document.getFileData());
        entityManager.getTransaction().begin();
        entityManager.persist(documentationEntity);
        entityManager.getTransaction().commit();

        entityManager.close();
    }

    public static void updateDocument(JsonDocumentation document) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        org.endeavourhealth.core.mySQLDatabase.models.DocumentationEntity documentationEntity = entityManager.find(org.endeavourhealth.core.mySQLDatabase.models.DocumentationEntity.class, document.getUuid());
        entityManager.getTransaction().begin();
        documentationEntity.setTitle(document.getTitle());
        documentationEntity.setFilename(document.getFilename());
        entityManager.getTransaction().commit();

        entityManager.close();
    }

    public static void deleteDocument(String uuid) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        org.endeavourhealth.core.mySQLDatabase.models.DocumentationEntity documentationEntity = entityManager.find(org.endeavourhealth.core.mySQLDatabase.models.DocumentationEntity.class, uuid);
        entityManager.getTransaction().begin();
        entityManager.remove(documentationEntity);
        entityManager.getTransaction().commit();

        entityManager.close();
    }

    public static void deleteAllAssociatedDocuments(String parentUuid, Short parentMapType) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        List<String> documents = MasterMappingEntity.getChildMappings(parentUuid, parentMapType, MapType.DOCUMENT.getMapType());

        if (documents.size() == 0)
            return;

        entityManager.getTransaction().begin();
        CriteriaBuilder criteriaBuilder  = entityManager.getCriteriaBuilder();
        CriteriaDelete<org.endeavourhealth.core.mySQLDatabase.models.DocumentationEntity> query = criteriaBuilder.createCriteriaDelete(org.endeavourhealth.core.mySQLDatabase.models.DocumentationEntity.class);
        Root<org.endeavourhealth.core.mySQLDatabase.models.DocumentationEntity> root = query.from(org.endeavourhealth.core.mySQLDatabase.models.DocumentationEntity.class);
        query.where(root.get("uuid").in(documents));

        entityManager.createQuery(query).executeUpdate();
        entityManager.getTransaction().commit();

        entityManager.close();
    }

    public static List<org.endeavourhealth.core.mySQLDatabase.models.DocumentationEntity> getDocumentsFromList(List<String> documents) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<org.endeavourhealth.core.mySQLDatabase.models.DocumentationEntity> cq = cb.createQuery(org.endeavourhealth.core.mySQLDatabase.models.DocumentationEntity.class);
        Root<org.endeavourhealth.core.mySQLDatabase.models.DocumentationEntity> rootEntry = cq.from(org.endeavourhealth.core.mySQLDatabase.models.DocumentationEntity.class);

        Predicate predicate = rootEntry.get("uuid").in(documents);

        cq.where(predicate);
        TypedQuery<org.endeavourhealth.core.mySQLDatabase.models.DocumentationEntity> query = entityManager.createQuery(cq);

        List<org.endeavourhealth.core.mySQLDatabase.models.DocumentationEntity> ret = query.getResultList();

        entityManager.close();

        return ret;
    }
}
