package org.endeavourhealth.usermanagermodel.models.database;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "delegation", schema = "user_manager")
public class DelegationEntity {
    private String uuid;
    private String name;
    private String rootOrganisation;

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
}
