package org.endeavourhealth.usermanagermodel.models.database;

import org.endeavourhealth.usermanagermodel.PersistenceManager;
import org.endeavourhealth.usermanagermodel.models.enums.AuditAction;
import org.endeavourhealth.usermanagermodel.models.enums.ItemType;
import org.endeavourhealth.usermanagermodel.models.json.JsonApplication;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "application", schema = "user_manager")
public class ApplicationEntity {
    private String id;
    private String name;
    private String applicationTree;
    private Byte isDeleted;
    private String description;

    @Id
    @Column(name = "id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
    @Column(name = "application_tree")
    public String getApplicationTree() {
        return applicationTree;
    }

    public void setApplicationTree(String applicationTree) {
        this.applicationTree = applicationTree;
    }

    @Basic
    @Column(name = "is_deleted")
    public Byte getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Byte isDeleted) {
        this.isDeleted = isDeleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApplicationEntity that = (ApplicationEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(applicationTree, that.applicationTree) &&
                Objects.equals(isDeleted, that.isDeleted);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name, applicationTree, isDeleted);
    }

    @Basic
    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static List<ApplicationEntity> getAllApplications() throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ApplicationEntity> cq = cb.createQuery(ApplicationEntity.class);
        Root<ApplicationEntity> rootEntry = cq.from(ApplicationEntity.class);

        TypedQuery<ApplicationEntity> query = entityManager.createQuery(cq);
        List<ApplicationEntity> ret = query.getResultList();

        entityManager.close();

        return ret;
    }

    public static void saveApplication(JsonApplication application, String userRoleId) throws Exception {

        boolean added = false;
        String originalUuid = application.getId();
        if (application.getId() == null) {
            application.setId(UUID.randomUUID().toString());
            added = true;
        }

        if (!added && !application.getIsDeleted()) {
            // editing so set original to deleted and save new one
            setExistingApplicationToDeleted(application.getId());
            application.setId(UUID.randomUUID().toString());
        }

        EntityManager entityManager = PersistenceManager.getEntityManager();

        ApplicationEntity applicationEntity = new ApplicationEntity();
        applicationEntity.setId(application.getId());
        applicationEntity.setName(application.getName());
        applicationEntity.setApplicationTree(application.getApplicationTree());
        applicationEntity.setIsDeleted(application.getIsDeleted() ? (byte)1 : (byte)0);
        entityManager.getTransaction().begin();
        entityManager.merge(applicationEntity);
        entityManager.getTransaction().commit();

        entityManager.close();

        if (application.getIsDeleted()) {
            AuditEntity.addToAuditTrail(userRoleId,
                    AuditAction.DELETE, ItemType.APPLICATION, application.getId(), null, null);
        } else if (added) {
            AuditEntity.addToAuditTrail(userRoleId,
                    AuditAction.ADD, ItemType.APPLICATION, null, application.getId(), null);
        } else {
            AuditEntity.addToAuditTrail(userRoleId,
                    AuditAction.EDIT, ItemType.APPLICATION, originalUuid, application.getId(), null);
        }

    }

    public static void setExistingApplicationToDeleted(String applicationId) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        try {
            entityManager.getTransaction().begin();
            String sql = "update ApplicationEntity a" +
                    " set a.isDeleted = 1 " +
                    " where a.id = :appId";

            Query query = entityManager.createQuery(sql)
                    .setParameter("appId", applicationId);

            query.executeUpdate();
            entityManager.getTransaction().commit();


        } finally {
            entityManager.close();
        }
    }
}
