package org.endeavourhealth.usermanagermodel.models.database;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.endeavourhealth.datasharingmanagermodel.models.database.OrganisationEntity;
import org.endeavourhealth.datasharingmanagermodel.models.database.ProjectEntity;
import org.endeavourhealth.usermanagermodel.PersistenceManager;
import org.endeavourhealth.usermanagermodel.models.caching.OrganisationCache;
import org.endeavourhealth.usermanagermodel.models.caching.ProjectCache;
import org.endeavourhealth.usermanagermodel.models.caching.UserCache;
import org.endeavourhealth.usermanagermodel.models.enums.AuditAction;
import org.endeavourhealth.usermanagermodel.models.enums.ItemType;
import org.endeavourhealth.usermanagermodel.models.json.JsonUserProject;
import org.keycloak.representations.idm.UserRepresentation;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "user_project", schema = "user_manager")
public class UserProjectEntity {
    private String id;
    private String userId;
    private String organisationId;
    private String projectId;
    private Byte isDefault;
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
    @Column(name = "organisation_id")
    public String getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(String organisationId) {
        this.organisationId = organisationId;
    }

    @Basic
    @Column(name = "project_id")
    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    @Basic
    @Column(name = "is_default")
    public Byte getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Byte isDefault) {
        this.isDefault = isDefault;
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
        UserProjectEntity that = (UserProjectEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(organisationId, that.organisationId) &&
                Objects.equals(projectId, that.projectId) &&
                Objects.equals(isDefault, that.isDefault) &&
                Objects.equals(isDeleted, that.isDeleted);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, userId, organisationId, projectId, isDefault, isDeleted);
    }

    public static List<UserProjectEntity> getUserProjectEntities(String userId) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        try {
            String sql = "select up" +
                    " from UserProjectEntity up" +
                    " where up.userId = :userId" +
                    " and up.isDeleted = 0";

            Query query = entityManager.createQuery(sql);

            query.setParameter("userId", userId);

            List<UserProjectEntity> results = query.getResultList();

            return results;
        } finally {
            entityManager.close();
        }

    }

    public static List<UserProjectEntity> getUserProjectEntitiesForProject(String projectId) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        try {
            String sql = "select up" +
                    " from UserProjectEntity up" +
                    " where up.projectId = :projectId" +
                    " and up.isDeleted = 0";

            Query query = entityManager.createQuery(sql);

            query.setParameter("projectId", projectId);

            List<UserProjectEntity> results = query.getResultList();

            return results;
        } finally {
            entityManager.close();
        }

    }

    public static List<Object[]> getUserProjects(String userId) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        try {
            String sql = "select " +
                    " up.id," +
                    " up.userId," +
                    " up.projectId," +
                    " up.organisationId," +
                    " up.isDeleted," +
                    " up.isDefault" +
                    " from UserProjectEntity up" +
                    " where up.userId = :userId" +
                    " and up.isDeleted = 0";

            Query query = entityManager.createQuery(sql);

            query.setParameter("userId", userId);

            List<Object[]> results = query.getResultList();

            return results;
        } finally {
            entityManager.close();
        }

    }

    public static UserProjectEntity getUserProject(String userProjectId) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<UserProjectEntity> cq = cb.createQuery(UserProjectEntity.class);
        Root<UserProjectEntity> rootEntry = cq.from(UserProjectEntity.class);

        Predicate predicate = cb.equal(rootEntry.get("id"), userProjectId);

        cq.where(predicate);
        TypedQuery<UserProjectEntity> query = entityManager.createQuery(cq);
        UserProjectEntity ret = query.getSingleResult();

        entityManager.close();

        return ret;
    }

    public static void saveUserProject(JsonUserProject userProject, String userProjectId) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        entityManager.getTransaction().begin();
        UserProjectEntity userProjectEntity = new UserProjectEntity();
        userProjectEntity.setId(userProject.getId());
        userProjectEntity.setUserId(userProject.getUserId());
        userProjectEntity.setOrganisationId(userProject.getOrganisationId());
        userProjectEntity.setProjectId(userProject.getProjectId());
        userProjectEntity.setIsDeleted(userProject.isDeleted() ? (byte)1 : (byte)0);
        userProjectEntity.setIsDefault(userProject.isDefault() ? (byte)1 : (byte)0);
        entityManager.merge(userProjectEntity);
        entityManager.getTransaction().commit();

        entityManager.close();

        if (userProject.isDeleted()) {
            AuditEntity.addToAuditTrail(userProjectId,
                    AuditAction.DELETE, ItemType.USER_PROJECT, userProject.getId(), null, null);
        } else {
            AuditEntity.addToAuditTrail(userProjectId,
                    AuditAction.ADD, ItemType.USER_PROJECT, null, userProject.getId(), null);
        }
    }

    public static void removeCurrentDefaultProject(String userId) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        try {
            entityManager.getTransaction().begin();
            String sql = "update UserProjectEntity up" +
                    " set up.isDefault = 0 " +
                    " where up.userId = :user";

            Query query = entityManager.createQuery(sql)
                    .setParameter("user", userId);

            query.executeUpdate();
            entityManager.getTransaction().commit();


        } finally {
            entityManager.close();
        }
    }

    public static void setCurrentDefaultProject(String userId, String userProjectId) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        try {
            entityManager.getTransaction().begin();
            String sql = "update UserProjectEntity up" +
                    " set up.isDefault = 1 " +
                    " where up.userId = :user" +
                    " and up.id = :projectId";

            Query query = entityManager.createQuery(sql)
                    .setParameter("user", userId)
                    .setParameter("projectId", userProjectId);

            query.executeUpdate();
            entityManager.getTransaction().commit();


        } finally {
            entityManager.close();
        }
    }

    public static UserProjectEntity getDefaultProject(String userId) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<UserProjectEntity> cq = cb.createQuery(UserProjectEntity.class);
        Root<UserProjectEntity> rootEntry = cq.from(UserProjectEntity.class);

        Predicate predicate = cb.and(cb.equal(rootEntry.get("userId"), userId),
                (cb.equal(rootEntry.get("isDefault"), 1)),
                (cb.equal(rootEntry.get("isDeleted"), 0)));

        cq.where(predicate);
        TypedQuery<UserProjectEntity> query = entityManager.createQuery(cq);
        UserProjectEntity ret = query.getSingleResult();

        entityManager.close();

        return ret;
    }

    public static void changeDefaultProject(String userId, String defaultRoleId, String userProjectId) throws Exception {
        UserProjectEntity oldDefaultProject = getDefaultProject(userId);
        UserProjectEntity newDefaultRole = getUserProject(defaultRoleId);

        removeCurrentDefaultProject(userId);

        setCurrentDefaultProject(userId, defaultRoleId);

        String auditJson = getAuditJsonForDefaultRoleChange(oldDefaultProject, newDefaultRole);

        AuditEntity.addToAuditTrail(userProjectId,
                AuditAction.EDIT, ItemType.DEFAULT_PROJECT, null, null, auditJson);

    }

    private static String getAuditJsonForDefaultRoleChange(UserProjectEntity oldDefault, UserProjectEntity newDefault) throws Exception {

        JsonNode beforeJson = generateDefaultRoleJson(oldDefault);
        JsonNode afterJson = generateDefaultRoleJson(newDefault);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.createObjectNode();

        ((ObjectNode)rootNode).put("title", "Default project changed");

        if (afterJson != null) {
            ((ObjectNode) rootNode).set("after", afterJson);
        }

        if (beforeJson != null) {
            ((ObjectNode) rootNode).set("before", beforeJson);
        }

        return prettyPrintJsonString(rootNode);
    }

    private static JsonNode generateDefaultRoleJson(UserProjectEntity userProject) throws Exception {
        UserRepresentation user = UserCache.getUserDetails(userProject.getUserId());
        OrganisationEntity organisation = OrganisationCache.getOrganisationDetails(userProject.getOrganisationId());
        ProjectEntity projectEntity = ProjectCache.getProjectDetails(userProject.getProjectId());

        ObjectMapper mapper = new ObjectMapper();
        JsonNode auditJson = mapper.createObjectNode();

        ((ObjectNode)auditJson).put("id", userProject.getId());
        ((ObjectNode)auditJson).put("user", user.getUsername());
        ((ObjectNode)auditJson).put("project", projectEntity.getName());
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

    public static List<UserProjectEntity> getUsersAtOrganisation(String organisationId) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<UserProjectEntity> cq = cb.createQuery(UserProjectEntity.class);
        Root<UserProjectEntity> rootEntry = cq.from(UserProjectEntity.class);

        Predicate predicate = cb.and(cb.equal(rootEntry.get("organisationId"), organisationId),
                cb.equal(rootEntry.get("isDeleted"), (byte)0));

        cq.where(predicate);
        TypedQuery<UserProjectEntity> query = entityManager.createQuery(cq);
        List<UserProjectEntity> ret = query.getResultList();

        entityManager.close();

        return ret;
    }
}
