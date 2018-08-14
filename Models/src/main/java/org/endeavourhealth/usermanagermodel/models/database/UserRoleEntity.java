package org.endeavourhealth.usermanagermodel.models.database;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.endeavourhealth.datasharingmanagermodel.models.database.OrganisationEntity;
import org.endeavourhealth.usermanagermodel.PersistenceManager;
import org.endeavourhealth.usermanagermodel.models.caching.OrganisationCache;
import org.endeavourhealth.usermanagermodel.models.caching.RoleTypeCache;
import org.endeavourhealth.usermanagermodel.models.caching.UserCache;
import org.endeavourhealth.usermanagermodel.models.enums.AuditAction;
import org.endeavourhealth.usermanagermodel.models.enums.ItemType;
import org.endeavourhealth.usermanagermodel.models.json.JsonUserRole;
import org.keycloak.representations.idm.UserRepresentation;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Map;
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
    private Byte isDefault;

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

    @Basic
    @Column(name = "is_default")
    public Byte getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Byte isDefault) {
        this.isDefault = isDefault;
    }


    public static List<Object[]> getUserRoles(String userId) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        try {
            String sql = "select " +
                    " ur.id," +
                    " ur.userId," +
                    " ur.roleTypeId," +
                    " rt.name," +
                    " ur.organisationId," +
                    " ur.userAccessProfileId," +
                    " ur.isDeleted," +
                    " ur.isDefault" +
                    " from UserRoleEntity ur" +
                    " join RoleTypeEntity rt on ur.roleTypeId = rt.id" +
                    " where ur.userId = :userId" +
                    " and ur.isDeleted = 0";

            Query query = entityManager.createQuery(sql);

            query.setParameter("userId", userId);

            List<Object[]> results = query.getResultList();

            return results;
        } finally {
            entityManager.close();
        }

    }

    public static UserRoleEntity getUserRole(String roleId) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<UserRoleEntity> cq = cb.createQuery(UserRoleEntity.class);
        Root<UserRoleEntity> rootEntry = cq.from(UserRoleEntity.class);

        Predicate predicate = cb.equal(rootEntry.get("id"), roleId);

        cq.where(predicate);
        TypedQuery<UserRoleEntity> query = entityManager.createQuery(cq);
        UserRoleEntity ret = query.getSingleResult();

        entityManager.close();

        return ret;
    }

    public static List<UserRoleEntity> getUsersAtOrganisation(String organisationId) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<UserRoleEntity> cq = cb.createQuery(UserRoleEntity.class);
        Root<UserRoleEntity> rootEntry = cq.from(UserRoleEntity.class);

        Predicate predicate = cb.and(cb.equal(rootEntry.get("organisationId"), organisationId),
                cb.equal(rootEntry.get("isDeleted"), (byte)0));

        cq.where(predicate);
        TypedQuery<UserRoleEntity> query = entityManager.createQuery(cq);
        List<UserRoleEntity> ret = query.getResultList();

        entityManager.close();

        return ret;
    }

    public static List<UserRoleEntity> getSuperUserOrganisations(String userId, String superUserRoleTypeId) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<UserRoleEntity> cq = cb.createQuery(UserRoleEntity.class);
        Root<UserRoleEntity> rootEntry = cq.from(UserRoleEntity.class);

        Predicate predicate = cb.and(cb.equal(rootEntry.get("userId"), userId),
                cb.equal(rootEntry.get("roleTypeId"), superUserRoleTypeId));

        cq.where(predicate);
        TypedQuery<UserRoleEntity> query = entityManager.createQuery(cq);
        List<UserRoleEntity> ret = query.getResultList();

        entityManager.close();

        return ret;
    }

    public static void saveUserRole(JsonUserRole userRole, String userRoleId) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        entityManager.getTransaction().begin();
        UserRoleEntity userRoleEntity = new UserRoleEntity();
        userRoleEntity.setId(userRole.getId());
        userRoleEntity.setUserId(userRole.getUserId());
        userRoleEntity.setOrganisationId(userRole.getOrganisationId());
        userRoleEntity.setRoleTypeId(userRole.getRoleTypeId());
        userRoleEntity.setUserAccessProfileId(userRole.getUserAccessProfileId());
        userRoleEntity.setIsDeleted(userRole.isDeleted() ? (byte)1 : (byte)0);
        userRoleEntity.setIsDefault(userRole.isDefault() ? (byte)1 : (byte)0);
        entityManager.merge(userRoleEntity);
        entityManager.getTransaction().commit();

        entityManager.close();

        if (userRole.isDeleted()) {
            AuditEntity.addToAuditTrail(userRoleId,
                    AuditAction.DELETE, ItemType.ROLE, userRole.getId(), null, null);
        } else {
            AuditEntity.addToAuditTrail(userRoleId,
                    AuditAction.ADD, ItemType.ROLE, null, userRole.getId(), null);
        }
    }

    public static void removeCurrentDefaultRole(String userId) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        try {
            entityManager.getTransaction().begin();
            String sql = "update UserRoleEntity ur" +
                    " set ur.isDefault = 0 " +
                    " where ur.userId = :user";

            Query query = entityManager.createQuery(sql)
                    .setParameter("user", userId);

            query.executeUpdate();
            entityManager.getTransaction().commit();


        } finally {
            entityManager.close();
        }
    }

    public static void setCurrentDefaultRole(String userId, String roleId) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        try {
            entityManager.getTransaction().begin();
            String sql = "update UserRoleEntity ur" +
                    " set ur.isDefault = 1 " +
                    " where ur.userId = :user" +
                    " and ur.id = :role";

            Query query = entityManager.createQuery(sql)
                    .setParameter("user", userId)
                    .setParameter("role", roleId);

            query.executeUpdate();
            entityManager.getTransaction().commit();


        } finally {
            entityManager.close();
        }
    }

    public static UserRoleEntity getDefaultRole(String userId) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<UserRoleEntity> cq = cb.createQuery(UserRoleEntity.class);
        Root<UserRoleEntity> rootEntry = cq.from(UserRoleEntity.class);

        Predicate predicate = cb.and(cb.equal(rootEntry.get("userId"), userId),
                (cb.equal(rootEntry.get("isDefault"), 1)));

        cq.where(predicate);
        TypedQuery<UserRoleEntity> query = entityManager.createQuery(cq);
        UserRoleEntity ret = query.getSingleResult();

        entityManager.close();

        return ret;
    }

    public static void changeDefaultRole(String userId, String defaultRoleId, String userRoleId) throws Exception {
        UserRoleEntity oldDefaultRole = getDefaultRole(userId);
        UserRoleEntity newDefaultRole = getUserRole(defaultRoleId);

        removeCurrentDefaultRole(userId);

        setCurrentDefaultRole(userId, defaultRoleId);

        String auditJson = getAuditJsonForDefaultRoleChange(oldDefaultRole, newDefaultRole);

        AuditEntity.addToAuditTrail(userRoleId,
                AuditAction.EDIT, ItemType.DEFAULT_ROLE, null, null, auditJson);

    }

    private static String getAuditJsonForDefaultRoleChange(UserRoleEntity oldDefault, UserRoleEntity newDefault) throws Exception {

        JsonNode beforeJson = generateDefaultRoleJson(oldDefault);
        JsonNode afterJson = generateDefaultRoleJson(newDefault);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.createObjectNode();

        ((ObjectNode)rootNode).put("title", "Default role changed");

        if (afterJson != null) {
            ((ObjectNode) rootNode).set("after", afterJson);
        }

        if (beforeJson != null) {
            ((ObjectNode) rootNode).set("before", beforeJson);
        }

        return prettyPrintJsonString(rootNode);
    }

    private static JsonNode generateDefaultRoleJson(UserRoleEntity role) throws Exception {
        UserRepresentation user = UserCache.getUserDetails(role.getUserId());
        OrganisationEntity organisation = OrganisationCache.getOrganisationDetails(role.getOrganisationId());
        RoleTypeEntity roleType = RoleTypeCache.getRoleDetails(role.getRoleTypeId());

        ObjectMapper mapper = new ObjectMapper();
        JsonNode auditJson = mapper.createObjectNode();

        ((ObjectNode)auditJson).put("id", role.getId());
        ((ObjectNode)auditJson).put("user", user.getUsername());
        ((ObjectNode)auditJson).put("roleType", roleType.getName());
        ((ObjectNode)auditJson).put("organisation", organisation.getName() + " (" + organisation.getOdsCode() + ")");

        return auditJson;
    }

    private static String prettyPrintJsonString(JsonNode jsonNode) throws Exception {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Object json = mapper.readValue(jsonNode.toString(), Object.class);
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
        } catch (Exception e) {
            throw new Exception("Converting Json to String failed : " + e.getMessage() );
        }
    }
}
