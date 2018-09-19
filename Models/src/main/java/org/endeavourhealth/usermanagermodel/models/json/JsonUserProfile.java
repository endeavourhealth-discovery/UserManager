package org.endeavourhealth.usermanagermodel.models.json;

import org.endeavourhealth.datasharingmanagermodel.models.database.OrganisationEntity;
import org.endeavourhealth.datasharingmanagermodel.models.database.RegionEntity;
import org.endeavourhealth.datasharingmanagermodel.models.json.JsonProject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JsonUserProfile {
    private String uuid = null;
    private String username = null;
    private String forename = null;
    private String surname = null;
    private String email = null;
    private String mobile = null;
    private String photo = null;
    private RegionEntity region = null;
    private List<JsonUserOrganisationProject> organisationProjects = null;


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getForename() {
        return forename;
    }

    public void setForename(String forename) {
        this.forename = forename;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public RegionEntity getRegion() {
        return region;
    }

    public void setRegion(RegionEntity region) {
        this.region = region;
    }

    public List<JsonUserOrganisationProject> getOrganisationProjects() {
        return organisationProjects;
    }

    public void setOrganisationProjects(List<JsonUserOrganisationProject> organisationProjects) {
        this.organisationProjects = organisationProjects;
    }
}
