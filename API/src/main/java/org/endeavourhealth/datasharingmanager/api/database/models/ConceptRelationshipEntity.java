package org.endeavourhealth.datasharingmanager.api.database.models;

import org.endeavourhealth.datasharingmanager.api.database.PersistenceManager;
import org.endeavourhealth.datasharingmanager.api.json.JsonConceptRelationship;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

@Entity
@Table(name = "concept_relationship", schema = "information_model")
public class ConceptRelationshipEntity {
    private int id;
    private long sourceConcept;
    private long targetConcept;
    private String targetLabel;
    private Integer relationshipOrder;
    private Long relationshipType;
    private Integer contextId;
    private Long count;

    @Id
    @Column(name = "id", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "source_concept", nullable = false)
    public long getSourceConcept() {
        return sourceConcept;
    }

    public void setSourceConcept(long sourceConcept) {
        this.sourceConcept = sourceConcept;
    }

    @Basic
    @Column(name = "target_concept", nullable = false)
    public long getTargetConcept() {
        return targetConcept;
    }

    public void setTargetConcept(long targetConcept) {
        this.targetConcept = targetConcept;
    }

    @Basic
    @Column(name = "target_label", nullable = true, length = 45)
    public String getTargetLabel() {
        return targetLabel;
    }

    public void setTargetLabel(String targetLabel) {
        this.targetLabel = targetLabel;
    }

    @Basic
    @Column(name = "relationship_order", nullable = true)
    public Integer getRelationshipOrder() {
        return relationshipOrder;
    }

    public void setRelationshipOrder(Integer relationshipOrder) {
        this.relationshipOrder = relationshipOrder;
    }

    @Basic
    @Column(name = "relationship_type", nullable = true)
    public Long getRelationshipType() {
        return relationshipType;
    }

    public void setRelationshipType(Long relationshipType) {
        this.relationshipType = relationshipType;
    }

    @Basic
    @Column(name = "context_id", nullable = true)
    public Integer getContextId() {
        return contextId;
    }

    public void setContextId(Integer contextId) {
        this.contextId = contextId;
    }

    @Basic
    @Column(name = "count", nullable = false)
    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConceptRelationshipEntity that = (ConceptRelationshipEntity) o;

        if (id != that.id) return false;
        if (sourceConcept != that.sourceConcept) return false;
        if (targetConcept != that.targetConcept) return false;
        if (targetLabel != null ? !targetLabel.equals(that.targetLabel) : that.targetLabel != null) return false;
        if (relationshipOrder != null ? !relationshipOrder.equals(that.relationshipOrder) : that.relationshipOrder != null)
            return false;
        if (relationshipType != null ? !relationshipType.equals(that.relationshipType) : that.relationshipType != null)
            return false;
        if (contextId != null ? !contextId.equals(that.contextId) : that.contextId != null) return false;
        if (count != null ? !count.equals(that.count) : that.count != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (int) (sourceConcept ^ (sourceConcept >>> 32));
        result = 31 * result + (int) (targetConcept ^ (targetConcept >>> 32));
        result = 31 * result + (targetLabel != null ? targetLabel.hashCode() : 0);
        result = 31 * result + (relationshipOrder != null ? relationshipOrder.hashCode() : 0);
        result = 31 * result + (relationshipType != null ? relationshipType.hashCode() : 0);
        result = 31 * result + (contextId != null ? contextId.hashCode() : 0);
        result = 31 * result + (count != null ? count.hashCode() : 0);
        return result;
    }

    public static List<ConceptRelationshipEntity> getConceptsRelationships(Integer conceptId) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ConceptRelationshipEntity> cq = cb.createQuery(ConceptRelationshipEntity.class);
        Root<ConceptRelationshipEntity> rootEntry = cq.from(ConceptRelationshipEntity.class);

        Predicate predicate = cb.or(cb.equal(rootEntry.get("sourceConcept"),conceptId),
                cb.equal(rootEntry.get("targetLabel"), conceptId));

        cq.where(predicate);
        TypedQuery<ConceptRelationshipEntity> query = entityManager.createQuery(cq);
        List<ConceptRelationshipEntity> ret = query.getResultList();

        entityManager.close();

        return ret;
    }

    public static void deleteConceptRelationship(Integer conceptRelationshipId) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        ConceptRelationshipEntity conceptRelationshipEntity = entityManager.find(ConceptRelationshipEntity.class, conceptRelationshipId);
        entityManager.getTransaction().begin();
        entityManager.remove(conceptRelationshipEntity);
        entityManager.getTransaction().commit();

        entityManager.close();
    }

    public static void saveConceptRelationship(JsonConceptRelationship conceptRelationship) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        ConceptRelationshipEntity conceptRelationshipEntity = new ConceptRelationshipEntity();
        entityManager.getTransaction().begin();
        //conceptRelationshipEntity.setId(conceptRelationship.getId());
        conceptRelationshipEntity.setSourceConcept(conceptRelationship.getSourceConcept());
        conceptRelationshipEntity.setTargetConcept(conceptRelationship.getTargetConcept());
        conceptRelationshipEntity.setTargetLabel(conceptRelationship.getTargetLabel());
        conceptRelationshipEntity.setRelationshipOrder(conceptRelationship.getRelationship_order());
        //conceptRelationshipEntity.setRelationshipType(conceptRelationship.getRelationship_type());
        conceptRelationshipEntity.setCount((long)(1));
        entityManager.persist(conceptRelationshipEntity);
        entityManager.getTransaction().commit();

        entityManager.close();
    }



}
