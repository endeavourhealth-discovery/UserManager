package org.endeavourhealth.usermanagermodel.models.database;

import org.endeavourhealth.usermanagermodel.PersistenceManager;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Objects;


@Entity
@Table(name = "user_role", schema = "user_manager")
public class UserRoleEntity {
    private String id;
    private String userId;
    private String roleTypeId;
    private String organisationId;
    private String userAccessProfileId;
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
    @Column(name = "user_id")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Basic
    @Column(name = "role_type_id")
    public String getRoleTypeId() {
        return roleTypeId;
    }

    public void setRoleTypeId(String roleTypeId) {
        this.roleTypeId = roleTypeId;
    }

    @Basic
    @Column(name = "organisation_id")
    public String getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(String organisationId) {
        this.organisationId = organisationId;
    }

    @Basic
    @Column(name = "user_access_profile_id")
    public String getUserAccessProfileId() {
        return userAccessProfileId;
    }

    public void setUserAccessProfileId(String userAccessProfileId) {
        this.userAccessProfileId = userAccessProfileId;
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
        UserRoleEntity that = (UserRoleEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(roleTypeId, that.roleTypeId) &&
                Objects.equals(organisationId, that.organisationId) &&
                Objects.equals(userAccessProfileId, that.userAccessProfileId) &&
                Objects.equals(isDeleted, that.isDeleted);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, userId, roleTypeId, organisationId, userAccessProfileId, isDeleted);
    }

    public static List<UserRoleEntity> getUserRoles(String userId) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<UserRoleEntity> cq = cb.createQuery(UserRoleEntity.class);
        Root<UserRoleEntity> rootEntry = cq.from(UserRoleEntity.class);

        Predicate predicate = cb.equal(rootEntry.get("user_id"), userId);

        cq.where(predicate);
        TypedQuery<UserRoleEntity> query = entityManager.createQuery(cq);
        List<UserRoleEntity> ret = query.getResultList();

        entityManager.close();

        return ret;
    }


}