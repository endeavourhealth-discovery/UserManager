package org.endeavourhealth.usermanagermodel.models.database;

import org.endeavourhealth.usermanagermodel.PersistenceManager;
import org.endeavourhealth.usermanagermodel.models.enums.AuditAction;
import org.endeavourhealth.usermanagermodel.models.enums.ItemType;
import org.endeavourhealth.usermanagermodel.models.json.JsonRoleTypeAccessProfile;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "role_type_access_profile", schema = "user_manager")
public class RoleTypeAccessProfileEntity {
    private String id;
    private String roleTypeId;
    private String applicationAccessProfileId;
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
    @Column(name = "role_type_id")
    public String getRoleTypeId() {
        return roleTypeId;
    }

    public void setRoleTypeId(String roleTypeId) {
        this.roleTypeId = roleTypeId;
    }

    @Basic
    @Column(name = "application_access_profile_id")
    public String getApplicationAccessProfileId() {
        return applicationAccessProfileId;
    }

    public void setApplicationAccessProfileId(String applicationAccessProfileId) {
        this.applicationAccessProfileId = applicationAccessProfileId;
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
        RoleTypeAccessProfileEntity that = (RoleTypeAccessProfileEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(roleTypeId, that.roleTypeId) &&
                Objects.equals(applicationAccessProfileId, that.applicationAccessProfileId) &&
                Objects.equals(profileTree, that.profileTree) &&
                Objects.equals(isDeleted, that.isDeleted);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, roleTypeId, applicationAccessProfileId, profileTree, isDeleted);
    }

    public static List<RoleTypeAccessProfileEntity> getAllRoleAccessProfiles() throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<RoleTypeAccessProfileEntity> cq = cb.createQuery(RoleTypeAccessProfileEntity.class);
        Root<RoleTypeAccessProfileEntity> rootEntry = cq.from(RoleTypeAccessProfileEntity.class);

        Predicate predicate = cb.equal(rootEntry.get("isDeleted"), 0);

        cq.where(predicate);

        TypedQuery<RoleTypeAccessProfileEntity> query = entityManager.createQuery(cq);
        List<RoleTypeAccessProfileEntity> ret = query.getResultList();

        entityManager.close();

        return ret;
    }

    public static List<JsonRoleTypeAccessProfile> getRoleAccessProfiles(String roleTypeId) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        try {
            String sql = "select " +
                    " rp.id," +
                    " rp.roleTypeId," +
                    " rt.name as roleName," +
                    " a.name as applicationName," +
                    " a.id as applicationId," +
                    " aap.profileTree," +
                    " rp.applicationAccessProfileId," +
                    " aap.name as profileName," +
                    " aap.description as profileDescription," +
                    " rp.isDeleted" +
                    " from RoleTypeAccessProfileEntity rp" +
                    " join RoleTypeEntity rt on rp.roleTypeId = rt.id" +
                    " join ApplicationAccessProfileEntity aap on aap.id = rp.applicationAccessProfileId" +
                    " join ApplicationEntity a on a.id = aap.applicationId" +
                    " where rp.roleTypeId = :roleTypeId" +
                    " and rp.isDeleted = 0";

            Query query = entityManager.createQuery(sql);

            query.setParameter("roleTypeId", roleTypeId);

            List<Object[]> results = query.getResultList();

            return convertRoleProfilesToJson(results);
        } finally {
            entityManager.close();
        }
    }

    private static List<JsonRoleTypeAccessProfile> convertRoleProfilesToJson(List<Object[]> results) throws Exception {
        List<JsonRoleTypeAccessProfile> profiles = new ArrayList<>();

        for (Object[] obj : results) {
            JsonRoleTypeAccessProfile profile = new JsonRoleTypeAccessProfile();
            profile.setId(obj[0].toString());
            profile.setRoleTypeId(obj[1].toString());
            profile.setName(obj[2].toString());
            profile.setApplication(obj[3].toString());
            profile.setApplicationId(obj[4].toString());
            profile.setProfileTree(obj[5].toString());
            profile.setApplicationAccessProfileId(obj[6].toString());
            profile.setApplicationAccessProfileName(obj[7].toString());
            profile.setApplicationAccessProfileDescription(obj[8].toString());
            profile.setDeleted(obj[9].toString().equals("1"));

            profiles.add(profile);
        }

        return profiles;
    }

    public static String saveRoleAccessProfile(JsonRoleTypeAccessProfile roleAccessProfile, String userRoleId) throws Exception {

        boolean added = false;
        String originalUuid = roleAccessProfile.getId();
        if (roleAccessProfile.getId() == null) {
            roleAccessProfile.setId(UUID.randomUUID().toString());
            added = true;
        }

        saveRoleAccessProfileInDatabase(roleAccessProfile);

        if (!added && !roleAccessProfile.getIsDeleted()) {
            // editing so set store a copy with a new uuid and set to deleted
            roleAccessProfile.setId(UUID.randomUUID().toString());
            roleAccessProfile.setDeleted(true);
            saveRoleAccessProfileInDatabase(roleAccessProfile);
        }

        if (roleAccessProfile.getIsDeleted()) {
            AuditEntity.addToAuditTrail(userRoleId,
                    AuditAction.DELETE, ItemType.ROLE_TYPE_APPLICATION_PROFILE, roleAccessProfile.getId(), null, null);
        } else if (added) {
            AuditEntity.addToAuditTrail(userRoleId,
                    AuditAction.ADD, ItemType.ROLE_TYPE_APPLICATION_PROFILE, null, roleAccessProfile.getId(), null);
        } else {
            AuditEntity.addToAuditTrail(userRoleId,
                    AuditAction.EDIT, ItemType.ROLE_TYPE_APPLICATION_PROFILE, roleAccessProfile.getId(), originalUuid, null);
        }

        return roleAccessProfile.getId();

    }

    public static void saveRoleAccessProfileInDatabase(JsonRoleTypeAccessProfile jsonRoleTypeAccessProfile) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        RoleTypeAccessProfileEntity roleAccessEntity = new RoleTypeAccessProfileEntity();
        roleAccessEntity.setId(jsonRoleTypeAccessProfile.getId());
        roleAccessEntity.setRoleTypeId(jsonRoleTypeAccessProfile.getRoleTypeId());
        roleAccessEntity.setApplicationAccessProfileId(jsonRoleTypeAccessProfile.getApplicationAccessProfileId());
        roleAccessEntity.setProfileTree(jsonRoleTypeAccessProfile.getProfileTree());
        roleAccessEntity.setIsDeleted(jsonRoleTypeAccessProfile.getIsDeleted() ? (byte)1 : (byte)0);
        entityManager.getTransaction().begin();
        entityManager.merge(roleAccessEntity);
        entityManager.getTransaction().commit();

        entityManager.close();

    }

    public static void setExistingRoleAccessProfileToDeleted(String roleAccessProfileId) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        try {
            entityManager.getTransaction().begin();
            String sql = "update RoleTypeAccessProfileEntity a" +
                    " set a.isDeleted = 1 " +
                    " where a.id = :accessId";

            Query query = entityManager.createQuery(sql)
                    .setParameter("accessId", roleAccessProfileId);

            query.executeUpdate();
            entityManager.getTransaction().commit();


        } finally {
            entityManager.close();
        }
    }

    public static RoleTypeAccessProfileEntity getRoleTypeAccessProfile(String profileId) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        RoleTypeAccessProfileEntity ret = entityManager.find(RoleTypeAccessProfileEntity.class, profileId);

        entityManager.close();

        return ret;
    }

    public static void deleteRoleAccessProfile(String profileId, String userRoleId) throws Exception {

        JsonRoleTypeAccessProfile accessProfile = new JsonRoleTypeAccessProfile(getRoleTypeAccessProfile(profileId));

        accessProfile.setDeleted(true);

        saveRoleAccessProfile(accessProfile, userRoleId);

    }
}
