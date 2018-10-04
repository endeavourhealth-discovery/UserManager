package org.endeavourhealth.usermanagermodel.models.database;

import org.endeavourhealth.usermanagermodel.PersistenceManager;
import org.endeavourhealth.usermanagermodel.models.enums.AuditAction;
import org.endeavourhealth.usermanagermodel.models.enums.ItemType;
import org.endeavourhealth.usermanagermodel.models.json.JsonApplicationAccessProfile;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "application_access_profile", schema = "user_manager")
public class ApplicationAccessProfileEntity {
    private String id;
    private String name;
    private String description;
    private String applicationId;
    private String profileTree;
    private Byte isDeleted;

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
    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Basic
    @Column(name = "application_id")
    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    @Basic
    @Column(name = "profile_tree")
    public String getProfileTree() {
        return profileTree;
    }

    public void setProfileTree(String profileTree) {
        this.profileTree = profileTree;
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
        ApplicationAccessProfileEntity that = (ApplicationAccessProfileEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(description, that.description) &&
                Objects.equals(applicationId, that.applicationId) &&
                Objects.equals(profileTree, that.profileTree) &&
                Objects.equals(isDeleted, that.isDeleted);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name, description, applicationId, profileTree, isDeleted);
    }

    public static List<ApplicationAccessProfileEntity> getAllApplicationProfiles() throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ApplicationAccessProfileEntity> cq = cb.createQuery(ApplicationAccessProfileEntity.class);
        Root<ApplicationAccessProfileEntity> rootEntry = cq.from(ApplicationAccessProfileEntity.class);

        Predicate predicate = cb.equal(rootEntry.get("isDeleted"), 0);

        cq.where(predicate);

        TypedQuery<ApplicationAccessProfileEntity> query = entityManager.createQuery(cq);
        List<ApplicationAccessProfileEntity> ret = query.getResultList();

        entityManager.close();

        return ret;
    }

    public static List<ApplicationAccessProfileEntity> getApplicationProfiles(String applicationId) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ApplicationAccessProfileEntity> cq = cb.createQuery(ApplicationAccessProfileEntity.class);
        Root<ApplicationAccessProfileEntity> rootEntry = cq.from(ApplicationAccessProfileEntity.class);

        Predicate predicate = cb.and(cb.equal(rootEntry.get("isDeleted"), 0),
                (cb.equal(rootEntry.get("applicationId"), applicationId)));

        cq.where(predicate);

        TypedQuery<ApplicationAccessProfileEntity> query = entityManager.createQuery(cq);
        List<ApplicationAccessProfileEntity> ret = query.getResultList();

        entityManager.close();

        return ret;
    }

    public static void saveApplicationProfile(JsonApplicationAccessProfile applicationProfile, String userRoleId) throws Exception {

        boolean added = false;
        String originalUuid = applicationProfile.getId();
        if (applicationProfile.getId() == null) {
            applicationProfile.setId(UUID.randomUUID().toString());
            added = true;
        }

        // store the profile in the DB
        saveApplicationProfileInDatabase(applicationProfile);

        if (!added && !applicationProfile.getIsDeleted()) {
            // editing so set store a copy with a new uuid and set to deleted
            applicationProfile.setId(UUID.randomUUID().toString());
            applicationProfile.setDeleted(true);
            saveApplicationProfileInDatabase(applicationProfile);
            applicationProfile.setDeleted(false);
        }

        if (applicationProfile.getIsDeleted()) {
            AuditEntity.addToAuditTrail(userRoleId,
                    AuditAction.DELETE, ItemType.APPLICATION_PROFILE, applicationProfile.getId(), null, null);
        } else if (added) {
            AuditEntity.addToAuditTrail(userRoleId,
                    AuditAction.ADD, ItemType.APPLICATION_PROFILE, null, applicationProfile.getId(), null);
        } else {
            AuditEntity.addToAuditTrail(userRoleId,
                    AuditAction.EDIT, ItemType.APPLICATION_PROFILE, applicationProfile.getId(), originalUuid, null);
        }

    }

    public static void saveApplicationProfileInDatabase(JsonApplicationAccessProfile applicationProfile) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        ApplicationAccessProfileEntity applicationEntity = new ApplicationAccessProfileEntity();
        applicationEntity.setId(applicationProfile.getId());
        applicationEntity.setName(applicationProfile.getName());
        applicationEntity.setApplicationId(applicationProfile.getApplicationId());
        applicationEntity.setDescription(applicationProfile.getDescription());
        applicationEntity.setProfileTree(applicationProfile.getProfileTree());
        applicationEntity.setIsDeleted(applicationProfile.getIsDeleted() ? (byte)1 : (byte)0);
        entityManager.getTransaction().begin();
        entityManager.merge(applicationEntity);
        entityManager.getTransaction().commit();

        entityManager.close();

    }

    public static void setExistingApplicationProfileToDeleted(String applicationProfileId) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        try {
            entityManager.getTransaction().begin();
            String sql = "update ApplicationAccessProfileEntity a" +
                    " set a.isDeleted = 1 " +
                    " where a.id = :profileId";

            Query query = entityManager.createQuery(sql)
                    .setParameter("profileId", applicationProfileId);

            query.executeUpdate();
            entityManager.getTransaction().commit();


        } finally {
            entityManager.close();
        }
    }

    public static ApplicationAccessProfileEntity getApplicationProfile(String applicationProfileId) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        ApplicationAccessProfileEntity ret = entityManager.find(ApplicationAccessProfileEntity.class, applicationProfileId);

        entityManager.close();

        return ret;
    }

    public static void deleteApplicationProfile(String applicationProfileId, String userRoleId) throws Exception {

        // DelegationRelationshipEntity.deleteAllDelegationRelationships(applicationId, userRoleId);

        JsonApplicationAccessProfile applicationProfile = new JsonApplicationAccessProfile(getApplicationProfile(applicationProfileId));

        applicationProfile.setDeleted(true);

        saveApplicationProfile(applicationProfile, userRoleId);

    }
}
