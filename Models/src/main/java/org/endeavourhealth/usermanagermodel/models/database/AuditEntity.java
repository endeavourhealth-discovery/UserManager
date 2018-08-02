package org.endeavourhealth.usermanagermodel.models.database;

import org.endeavourhealth.usermanagermodel.PersistenceManager;
import org.endeavourhealth.usermanagermodel.models.enums.AuditAction;
import org.endeavourhealth.usermanagermodel.models.enums.ItemType;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.*;

@Entity
@Table(name = "audit", schema = "user_manager")
@IdClass(AuditEntityPK.class)
public class AuditEntity {
    private String id;
    private String userRoleId;
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
    @Column(name = "user_role_id")
    public String getUserRoleId() {
        return userRoleId;
    }

    public void setUserRoleId(String userRoleId) {
        this.userRoleId = userRoleId;
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
                Objects.equals(userRoleId, that.userRoleId) &&
                Objects.equals(timestamp, that.timestamp) &&
                Objects.equals(auditType, that.auditType) &&
                Objects.equals(itemBefore, that.itemBefore) &&
                Objects.equals(itemAfter, that.itemAfter) &&
                Objects.equals(itemType, that.itemType) &&
                Objects.equals(auditJson, that.auditJson);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, userRoleId, timestamp, auditType, itemBefore, itemAfter, itemType, auditJson);
    }

    public static List<Object[]> getAudit(Integer pageNumber, Integer pageSize,
                                          String organisationId, String userId) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        try {
            String orderby = " order by a.timestamp desc ";
            String sql = "select " +
                    " a.id," +
                    " rt.name," +
                    " a.timestamp," +
                    " a.auditType," +
                    " ur.organisationId," +
                    " ur.userId," +
                    " aa.actionType," +
                    " it.itemType" +
                    " from AuditEntity a" +
                    " join UserRoleEntity ur on ur.id = a.userRoleId" +
                    " join RoleTypeEntity rt on rt.id = ur.roleTypeId" +
                    " join AuditActionEntity aa on aa.id = a.auditType" +
                    " join ItemTypeEntity it on it.id = a.itemType";

            if (organisationId != null) {
                sql += " where ur.organisationId = :orgId";

                if (userId != null) {
                    sql += " and ur.userId = :userId";
                }
            }

            sql += orderby;

            Query query = entityManager.createQuery(sql);
            if (organisationId != null) {
                query.setParameter("orgId", organisationId);

                if (userId != null) {
                    query.setParameter("userId", userId);
                }
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
                        " join UserRoleEntity ur on ur.id = a.userRoleId" +
                        " where ur.organisationId = :orgId";

                if (userId != null) {
                    sql += " and ur.userId = :userId";
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
        auditEntity.setUserRoleId(userRoleId);
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
