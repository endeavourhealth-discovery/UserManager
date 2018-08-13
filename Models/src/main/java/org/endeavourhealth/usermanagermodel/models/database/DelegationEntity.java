package org.endeavourhealth.usermanagermodel.models.database;

import org.endeavourhealth.usermanagermodel.PersistenceManager;
import org.endeavourhealth.usermanagermodel.models.enums.AuditAction;
import org.endeavourhealth.usermanagermodel.models.enums.ItemType;
import org.endeavourhealth.usermanagermodel.models.json.JsonDelegation;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "delegation", schema = "user_manager")
public class DelegationEntity {
    private String uuid;
    private String name;
    private String rootOrganisation;
    private Byte isDeleted;

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
    @Column(name = "root_organisation")
    public String getRootOrganisation() {
        return rootOrganisation;
    }

    public void setRootOrganisation(String rootOrganisation) {
        this.rootOrganisation = rootOrganisation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DelegationEntity that = (DelegationEntity) o;
        return Objects.equals(uuid, that.uuid) &&
                Objects.equals(name, that.name) &&
                Objects.equals(rootOrganisation, that.rootOrganisation);
    }

    @Override
    public int hashCode() {

        return Objects.hash(uuid, name, rootOrganisation);
    }

    @Basic
    @Column(name = "is_deleted")
    public Byte getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Byte isDeleted) {
        this.isDeleted = isDeleted;
    }

    public static List<DelegationEntity> getDelegations(String organisationId) throws Exception {
        if (organisationId == null) {
            return getAllDelegations();
        } else return getSelectedDelegations(organisationId);

    }

    public static List<DelegationEntity> getAllDelegations() throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<DelegationEntity> cq = cb.createQuery(DelegationEntity.class);
        Root<DelegationEntity> rootEntry = cq.from(DelegationEntity.class);

        Predicate predicate = cb.equal(rootEntry.get("isDeleted"), 0);

        cq.where(predicate);

        TypedQuery<DelegationEntity> query = entityManager.createQuery(cq);
        List<DelegationEntity> ret = query.getResultList();

        entityManager.close();

        return ret;
    }

    public static List<DelegationEntity> getSelectedDelegations(String organisationId) throws Exception {

        EntityManager entityManager = PersistenceManager.getEntityManager();

        try {
            String orderby = " order by a.timestamp desc ";
            String sql = "select distinct" +
                    " d" +
                    " from DelegationEntity d" +
                    " join DelegationRelationshipEntity rel on rel.delegation = d.uuid" +
                    " where rel.childUuid = :org or rel.parentUuid = :org" +
                    " and d.isDeleted = 0";


            Query query = entityManager.createQuery(sql, DelegationEntity.class)
                    .setParameter("org", organisationId);

            List<DelegationEntity> results = query.getResultList();

            return results;

        } finally {
            entityManager.close();
        }

    }

    public static String getRootOrganisation(String delegationId) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<DelegationEntity> cq = cb.createQuery(DelegationEntity.class);
        Root<DelegationEntity> rootEntry = cq.from(DelegationEntity.class);

        Predicate predicate = cb.equal(rootEntry.get("uuid"), delegationId);

        cq.where(predicate);
        TypedQuery<DelegationEntity> query = entityManager.createQuery(cq);
        List<DelegationEntity> ret = query.getResultList();

        entityManager.close();

        return ret.get(0).rootOrganisation;
    }

    public static void saveDelegation(JsonDelegation delegation, String userRoleId) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        DelegationEntity delegationEntity = new DelegationEntity();
        delegationEntity.setUuid(delegation.getUuid());
        delegationEntity.setName(delegation.getName());
        delegationEntity.setRootOrganisation(delegation.getRootOrganisation());
        delegationEntity.setIsDeleted(delegation.isDeleted() ? (byte)1 : (byte)0);
        entityManager.getTransaction().begin();
        entityManager.merge(delegationEntity);
        entityManager.getTransaction().commit();

        entityManager.close();

        if (delegation.isDeleted()) {
            AuditEntity.addToAuditTrail(userRoleId,
                    AuditAction.DELETE, ItemType.DELEGATION, delegation.getUuid(), null, null);
        } else {
            AuditEntity.addToAuditTrail(userRoleId,
                    AuditAction.ADD, ItemType.DELEGATION, null, delegation.getUuid(), null);
        }
    }

    public static DelegationEntity getDelegation(String delegationId) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        DelegationEntity ret = entityManager.find(DelegationEntity.class, delegationId);

        entityManager.close();

        return ret;
    }

    public static void deleteDelegation(String delegationId, String userRoleId) throws Exception {

        DelegationRelationshipEntity.deleteAllDelegationRelationships(delegationId, userRoleId);

        JsonDelegation delegation = new JsonDelegation(getDelegation(delegationId));

        delegation.setDeleted(true);

        saveDelegation(delegation, userRoleId);

    }
}
