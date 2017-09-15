package org.endeavourhealth.datasharingmanagermodel.models.database;

import org.endeavourhealth.datasharingmanagermodel.PersistenceManager;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Entity
@Table(name = "organisation_type", schema = "data_sharing_manager")
public class OrganisationTypeEntity {
    private byte id;
    private String organisationType;

    @Id
    @Column(name = "id", nullable = false)
    public byte getId() {
        return id;
    }

    public void setId(byte id) {
        this.id = id;
    }

    @Basic
    @Column(name = "organisation_type", nullable = false, length = 100)
    public String getOrganisationType() {
        return organisationType;
    }

    public void setOrganisationType(String organisationType) {
        this.organisationType = organisationType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrganisationTypeEntity that = (OrganisationTypeEntity) o;

        if (id != that.id) return false;
        if (organisationType != null ? !organisationType.equals(that.organisationType) : that.organisationType != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) id;
        result = 31 * result + (organisationType != null ? organisationType.hashCode() : 0);
        return result;
    }

    public static List<OrganisationTypeEntity> getAllOrganisationTypes() throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<OrganisationTypeEntity> cq = cb.createQuery(OrganisationTypeEntity.class);
        Root<OrganisationTypeEntity> rootEntry = cq.from(OrganisationTypeEntity.class);
        CriteriaQuery<OrganisationTypeEntity> all = cq.select(rootEntry);
        TypedQuery<OrganisationTypeEntity> allQuery = entityManager.createQuery(all);
        List<OrganisationTypeEntity> ret =  allQuery.getResultList();

        entityManager.close();

        return ret;
    }
}
