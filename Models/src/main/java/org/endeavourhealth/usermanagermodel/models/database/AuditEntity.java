package org.endeavourhealth.usermanagermodel.models.database;

import org.endeavourhealth.usermanagermodel.PersistenceManager;
import org.endeavourhealth.usermanagermodel.models.enums.AuditAction;
import org.endeavourhealth.usermanagermodel.models.enums.ItemType;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "audit", schema = "user_manager")
@IdClass(AuditEntityPK.class)
public class AuditEntity {
    private String id;
    private String userProjectId;
    private Timestamp timestamp;
    private Byte auditType;
    private String itemBefore;
    private String itemAfter;
    private Byte itemType;
    private String auditJson;

    @Id
    @Column(name = "id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Id
    @Column(name = "user_project_id")
    public String getUserProjectId() {
        return userProjectId;
    }

    public void setUserProjectId(String userProjectId) {
        this.userProjectId = userProjectId;
    }

    @Id
    @Column(name = "timestamp")
    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Basic
    @Column(name = "audit_type")
    public Byte getAuditType() {
        return auditType;
    }

    public void setAuditType(Byte auditType) {
        this.auditType = auditType;
    }

    @Basic
    @Column(name = "item_before")
    public String getItemBefore() {
        return itemBefore;
    }

    public void setItemBefore(String itemBefore) {
        this.itemBefore = itemBefore;
    }

    @Basic
    @Column(name = "item_after")
    public String getItemAfter() {
        return itemAfter;
    }

    public void setItemAfter(String itemAfter) {
        this.itemAfter = itemAfter;
    }

    @Basic
    @Column(name = "item_type")
    public Byte getItemType() {
        return itemType;
    }

    public void setItemType(Byte itemType) {
        this.itemType = itemType;
    }

    @Basic
    @Column(name = "audit_json")
    public String getAuditJson() {
        return auditJson;
    }

    public void setAuditJson(String auditJson) {
        this.auditJson = auditJson;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuditEntity that = (AuditEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(userProjectId, that.userProjectId) &&
                Objects.equals(timestamp, that.timestamp) &&
                Objects.equals(auditType, that.auditType) &&
                Objects.equals(itemBefore, that.itemBefore) &&
                Objects.equals(itemAfter, that.itemAfter) &&
                Objects.equals(itemType, that.itemType) &&
                Objects.equals(auditJson, that.auditJson);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, userProjectId, timestamp, auditType, itemBefore, itemAfter, itemType, auditJson);
    }

    public static List<Object[]> getAudit(String userOrganisationId, Integer pageNumber, Integer pageSize,
                                          String organisationId, String userId,
                                          Timestamp startDate, Timestamp endDate) throws Exception {

        EntityManager entityManager = PersistenceManager.getEntityManager();

        List<String> filterOrgs = new ArrayList<>();

        // get a list of all delegated orgs that this user has access to view audit trail for
        // if userOrganisationId is null, the user must be in god mode so don't limit by organisations
        if (userOrganisationId != null) {
            List<DelegationRelationshipEntity> relationships = DelegationRelationshipEntity.getDelegatedOrganisations(userOrganisationId);

            filterOrgs = relationships.stream()
                    .map(DelegationRelationshipEntity::getChildUuid)
                    .collect(Collectors.toList());

            filterOrgs.add(userOrganisationId);
        }

        try {
            String orderby = " order by a.timestamp desc ";
            String whereAnd = " where ";
            String sql = "select distinct" +
                    " a.id," +
                    " up.projectId," +
                    " a.timestamp," +
                    " a.auditType," +
                    " up.organisationId," +
                    " up.userId," +
                    " aa.actionType," +
                    " it.itemType" +
                    " from AuditEntity a" +
                    " join UserProjectEntity up on up.id = a.userProjectId" +
                    " join AuditActionEntity aa on aa.id = a.auditType" +
                    " join ItemTypeEntity it on it.id = a.itemType ";

            if (userOrganisationId != null) {
                sql += " where up.organisationId in :filterOrgIds";
                whereAnd = " and ";
            }

            if (organisationId != null) {
                sql +=  whereAnd + " up.organisationId = :orgId";
                whereAnd = " and ";

                if (userId != null) {
                    sql += " and up.userId = :userId";
                }
            }

            if (startDate != null) {
                sql += whereAnd +  " a.timestamp >= :fromDate";
                whereAnd = " and ";
            }

            if (endDate != null) {
                sql += whereAnd + " a.timestamp <= :toDate";
                whereAnd = " and ";
            }

            sql += orderby;

            Query query = entityManager.createQuery(sql);

            if (userOrganisationId != null) {
                query.setParameter("filterOrgIds", filterOrgs);
            }

            if (organisationId != null) {
                query.setParameter("orgId", organisationId);

                if (userId != null) {
                    query.setParameter("userId", userId);
                }
            }

            if (startDate != null) {
                query.setParameter("fromDate", startDate);
            }
            if (endDate != null) {
                query.setParameter("toDate", endDate);
            }

            query.setFirstResult((pageNumber - 1) * pageSize);
            query.setMaxResults(pageSize);

            List<Object[]> results = query.getResultList();


            return results;

        } finally {
            entityManager.close();
        }
    }

    public static long getAuditCount(String organisationId, String userId) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        try {
            String sql = "select count (a.id)" +
                    " from AuditEntity a";

            if (organisationId != null) {
                sql = "select count (a.id)" +
                        " from AuditEntity a " +
                        " join UserProjectEntity up on up.id = a.userProjectId" +
                        " where up.organisationId = :orgId";

                if (userId != null) {
                    sql += " and up.userId = :userId";
                }
            }

            Query query = entityManager.createQuery(sql);

            if (organisationId != null) {
                query.setParameter("orgId", organisationId);

                if (userId != null) {
                    query.setParameter("userId", userId);
                }
            }

            long count = (long)query.getSingleResult();


            return count;

        } finally {
            entityManager.close();
        }
    }

    public static AuditEntity getAuditDetail(String auditId) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        try {
            String sql = "select a" +
                    " from AuditEntity a " +
                    " where a.id = :auditId";

            Query query = entityManager.createQuery(sql, AuditEntity.class)
                    .setParameter("auditId", auditId);

            AuditEntity result = (AuditEntity)query.getSingleResult();

            return result;

        } finally {
            entityManager.close();
        }
    }

    public static void addToAuditTrail(String userRoleId, AuditAction auditAction, ItemType itemType,
                                       String itemBefore, String itemAfter, String auditJson) throws Exception {

        EntityManager entityManager = PersistenceManager.getEntityManager();

        AuditEntity auditEntity = new AuditEntity();
        auditEntity.setId(UUID.randomUUID().toString());
        auditEntity.setTimestamp(new Timestamp(System.currentTimeMillis()));
        auditEntity.setAuditType(auditAction.getAuditAction().byteValue());
        auditEntity.setItemType(itemType.getItemType().byteValue());
        auditEntity.setUserProjectId(userRoleId);
        if (itemBefore != null) {
            auditEntity.setItemBefore(itemBefore);
        }
        if (itemAfter != null) {
            auditEntity.setItemAfter(itemAfter);
        }
        if (auditJson != null) {
            auditEntity.setAuditJson(auditJson);
        }


        entityManager.getTransaction().begin();
        entityManager.merge(auditEntity);
        entityManager.getTransaction().commit();

        entityManager.close();

    }

}
