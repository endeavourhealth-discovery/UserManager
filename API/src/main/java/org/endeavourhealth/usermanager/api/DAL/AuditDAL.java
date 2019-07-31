package org.endeavourhealth.usermanager.api.DAL;

import org.endeavourhealth.common.security.usermanagermodel.models.ConnectionManager;
import org.endeavourhealth.common.security.usermanagermodel.models.database.AuditEntity;
import org.endeavourhealth.common.security.usermanagermodel.models.database.DelegationRelationshipEntity;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AuditDAL {

    public List<Object[]> getAudit(String userOrganisationId, Integer pageNumber, Integer pageSize,
                                          String organisationId, String userId,
                                          Timestamp startDate, Timestamp endDate) throws Exception {


        List<String> filterOrgs = new ArrayList<>();

        // get a list of all delegated orgs that this user has access to view audit trail for
        // if userOrganisationId is null, the user must be in god mode so don't limit by organisations
        if (userOrganisationId != null) {
            List<DelegationRelationshipEntity> relationships = new DelegationRelationshipDAL().getDelegatedOrganisations(userOrganisationId);

            filterOrgs = relationships.stream()
                    .map(DelegationRelationshipEntity::getChildUuid)
                    .collect(Collectors.toList());

            filterOrgs.add(userOrganisationId);
        }

        EntityManager entityManager = ConnectionManager.getUmEntityManager();

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

    public long getAuditCount(String userOrganisationId, String organisationId, String userId) throws Exception {


        List<String> filterOrgs = new ArrayList<>();

        // get a list of all delegated orgs that this user has access to view audit trail for
        // if userOrganisationId is null, the user must be in god mode so don't limit by organisations
        if (userOrganisationId != null) {
            List<DelegationRelationshipEntity> relationships = new DelegationRelationshipDAL().getDelegatedOrganisations(userOrganisationId);

            filterOrgs = relationships.stream()
                    .map(DelegationRelationshipEntity::getChildUuid)
                    .collect(Collectors.toList());

            filterOrgs.add(userOrganisationId);
        }

        EntityManager entityManager = ConnectionManager.getUmEntityManager();
        try {
            String whereAnd = " where ";
            String sql = "select count (a.id)" +
                    " from AuditEntity a";

            if (organisationId != null || userOrganisationId != null || userId != null) {
                sql = "select count (a.id)" +
                        " from AuditEntity a " +
                        " join UserProjectEntity up on up.id = a.userProjectId";

                if (userId != null) {
                    sql += whereAnd + " up.userId = :userId";
                    whereAnd = " and ";
                }

                if (userOrganisationId != null) {
                    sql += whereAnd + " up.organisationId in :filterOrgIds";
                    whereAnd = " and ";
                }

                if (organisationId != null) {
                    sql += whereAnd + " up.organisationId = :orgId";
                }
            }



            Query query = entityManager.createQuery(sql);

            if (organisationId != null) {
                query.setParameter("orgId", organisationId);
            }

            if (userId != null) {
                query.setParameter("userId", userId);
            }

            if (userOrganisationId != null) {
                query.setParameter("filterOrgIds", filterOrgs);
            }

            long count = (long)query.getSingleResult();


            return count;

        } finally {
            entityManager.close();
        }
    }

    public AuditEntity getAuditDetail(String auditId) throws Exception {
        EntityManager entityManager = ConnectionManager.getUmEntityManager();

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
}
