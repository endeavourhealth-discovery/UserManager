package org.endeavourhealth.datasharingmanager.api.database.models;

import org.endeavourhealth.datasharingmanager.api.database.PersistenceManager;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "pseudo_id_map", schema = "subscriber_transform_mysql_pseudo", catalog = "")
public class PseudoIdMapEntity {
    private String patientId;
    private String pseudoId;

    @Id
    @Column(name = "patient_id")
    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    @Basic
    @Column(name = "pseudo_id")
    public String getPseudoId() {
        return pseudoId;
    }

    public void setPseudoId(String pseudoId) {
        this.pseudoId = pseudoId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PseudoIdMapEntity that = (PseudoIdMapEntity) o;
        return Objects.equals(patientId, that.patientId) &&
                Objects.equals(pseudoId, that.pseudoId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(patientId, pseudoId);
    }

    public static List<PseudoIdMapEntity> getPatientIdListFromPseudoList(List<String> pseudoIds) throws Exception {

        EntityManager entityManager = PersistenceManager.getEntityManager();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<PseudoIdMapEntity> cq = cb.createQuery(PseudoIdMapEntity.class);
        Root<PseudoIdMapEntity> rootEntry = cq.from(PseudoIdMapEntity.class);

        Predicate predicate = rootEntry.get("pseudoId").in(pseudoIds);
        cq.where(predicate);

        TypedQuery<PseudoIdMapEntity> query = entityManager.createQuery(cq);
        List<PseudoIdMapEntity> ret = query.getResultList();
        entityManager.close();

        return ret;
    }
}
