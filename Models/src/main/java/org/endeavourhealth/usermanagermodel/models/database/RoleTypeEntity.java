package org.endeavourhealth.usermanagermodel.models.database;

import org.endeavourhealth.usermanagermodel.PersistenceManager;
import org.endeavourhealth.usermanagermodel.models.json.JsonRoleType;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "role_type", schema = "user_manager")
public class RoleTypeEntity {
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
        RoleTypeEntity that = (RoleTypeEntity) o;
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

    public static List<RoleTypeEntity> getAllRoleTypes() throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<RoleTypeEntity> cq = cb.createQuery(RoleTypeEntity.class);
        Root<RoleTypeEntity> rootEntry = cq.from(RoleTypeEntity.class);

        TypedQuery<RoleTypeEntity> query = entityManager.createQuery(cq);
        List<RoleTypeEntity> ret = query.getResultList();

        entityManager.close();

        return ret;
    }

    public static RoleTypeEntity getRoleType(String roleId) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        RoleTypeEntity ret = entityManager.find(RoleTypeEntity.class, roleId);

        entityManager.close();

        return ret;
    }

    public static void saveRoleType(JsonRoleType roleType) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        RoleTypeEntity roleTypeEntity = new RoleTypeEntity();
        roleTypeEntity.setId(roleType.getId());
        roleTypeEntity.setName(roleType.getName());
        roleTypeEntity.setDescription(roleType.getDescription());
        roleTypeEntity.setJobCategoryId(roleType.getJobCategoryId());
        roleTypeEntity.setIsDeleted(roleType.isDeleted() ? (byte)1 : (byte)0);
        entityManager.getTransaction().begin();
        entityManager.merge(roleTypeEntity);
        entityManager.getTransaction().commit();

        entityManager.close();
    }

}
