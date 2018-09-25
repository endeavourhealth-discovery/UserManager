package org.endeavourhealth.usermanagermodel.models.database;

import org.endeavourhealth.usermanagermodel.PersistenceManager;
import org.endeavourhealth.usermanagermodel.models.enums.AuditAction;
import org.endeavourhealth.usermanagermodel.models.enums.ItemType;
import org.endeavourhealth.usermanagermodel.models.json.JsonApplicationPolicy;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "application_policy", schema = "user_manager")
public class ApplicationPolicyEntity {
    private String id;
    private String name;
    private String description;
    private String jobCategoryId;
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
    @Column(name = "job_category_id")
    public String getJobCategoryId() {
        return jobCategoryId;
    }

    public void setJobCategoryId(String jobCategoryId) {
        this.jobCategoryId = jobCategoryId;
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
        ApplicationPolicyEntity that = (ApplicationPolicyEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(description, that.description) &&
                Objects.equals(jobCategoryId, that.jobCategoryId) &&
                Objects.equals(isDeleted, that.isDeleted);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name, description, jobCategoryId, isDeleted);
    }

    public static List<ApplicationPolicyEntity> getAllApplicationPolicies() throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ApplicationPolicyEntity> cq = cb.createQuery(ApplicationPolicyEntity.class);
        Root<ApplicationPolicyEntity> rootEntry = cq.from(ApplicationPolicyEntity.class);

        Predicate predicate = cb.equal(rootEntry.get("isDeleted"), 0);

        cq.where(predicate);

        TypedQuery<ApplicationPolicyEntity> query = entityManager.createQuery(cq);
        List<ApplicationPolicyEntity> ret = query.getResultList();

        entityManager.close();

        return ret;
    }

    public static ApplicationPolicyEntity getApplicationPolicy(String roleId) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        ApplicationPolicyEntity ret = entityManager.find(ApplicationPolicyEntity.class, roleId);

        entityManager.close();

        return ret;
    }

    public static void saveApplicationPolicy(JsonApplicationPolicy applicationPolicy, String userRoleId) throws Exception {

        boolean added = false;
        String originalUuid = applicationPolicy.getId();
        if (applicationPolicy.getId() == null) {
            applicationPolicy.setId(UUID.randomUUID().toString());
            added = true;
        }

        // store the profile in the DB
        saveApplicationPolicyInDatabase(applicationPolicy);

        if (!added && !applicationPolicy.getIsDeleted()) {
            // editing so set store a copy with a new uuid and set to deleted
            applicationPolicy.setId(UUID.randomUUID().toString());
            applicationPolicy.setDeleted(true);
            saveApplicationPolicyInDatabase(applicationPolicy);
        }

        if (applicationPolicy.getIsDeleted()) {
            AuditEntity.addToAuditTrail(userRoleId,
                    AuditAction.DELETE, ItemType.APPLICATION_POLICY, applicationPolicy.getId(), null, null);
        } else if (added) {
            AuditEntity.addToAuditTrail(userRoleId,
                    AuditAction.ADD, ItemType.APPLICATION_POLICY, null, applicationPolicy.getId(), null);
        } else {
            AuditEntity.addToAuditTrail(userRoleId,
                    AuditAction.EDIT, ItemType.APPLICATION_POLICY, applicationPolicy.getId(), originalUuid, null);
        }

    }

    public static void saveApplicationPolicyInDatabase(JsonApplicationPolicy roleType) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        ApplicationPolicyEntity applicationPolicyEntity = new ApplicationPolicyEntity();
        applicationPolicyEntity.setId(roleType.getId());
        applicationPolicyEntity.setName(roleType.getName());
        applicationPolicyEntity.setDescription(roleType.getDescription());
        applicationPolicyEntity.setJobCategoryId(roleType.getJobCategoryId());
        applicationPolicyEntity.setIsDeleted(roleType.getIsDeleted() ? (byte)1 : (byte)0);
        entityManager.getTransaction().begin();
        entityManager.merge(applicationPolicyEntity);
        entityManager.getTransaction().commit();

        entityManager.close();
    }

    public static void deleteApplicationPolicy(String applicationPolicyId, String userRoleId) throws Exception {

        JsonApplicationPolicy applicationPolicy = new JsonApplicationPolicy(getApplicationPolicy(applicationPolicyId));

        applicationPolicy.setDeleted(true);

        saveApplicationPolicy(applicationPolicy, userRoleId);

    }

}
