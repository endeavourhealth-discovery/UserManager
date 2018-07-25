package org.endeavourhealth.usermanagermodel.models.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonUser {
    private UUID uuid = null;
    private String username = null;
    private String password = null;
    private String title = null;
    private String forename = null;
    private String surname = null;
    private String email = null;
    private String mobile = null;
    private String photo = null;
    private String defaultOrgId = null;
    private Boolean superUser = null;
    private Boolean admin = null;
    private Integer permissions = null;
    private Boolean mustChangePassword = null;
    private List<JsonUserRole> userRoles = null;

    public JsonUser() {
    }

    public JsonUser(UserRepresentation keycloakUser) {
        this.uuid = UUID.fromString(keycloakUser.getId());
        this.username = keycloakUser.getUsername();
        this.forename = keycloakUser.getFirstName();
        this.surname = keycloakUser.getLastName();
        this.email = keycloakUser.getEmail();
        Map<String, List<String>> userAttributes = keycloakUser.getAttributes();
        if (userAttributes != null) {
            Iterator var3 = userAttributes.keySet().iterator();

            while(var3.hasNext()) {
                String attributeKey = (String)var3.next();
                Object obj;
                if (attributeKey.equalsIgnoreCase("mobile")) {
                    obj = userAttributes.get(attributeKey);
                    this.mobile = obj.toString().substring(1, obj.toString().length() - 1);
                } else if (attributeKey.equalsIgnoreCase("photo")) {
                    obj = userAttributes.get(attributeKey);
                    this.photo = obj.toString().substring(1, obj.toString().length() - 1);
                } else if (attributeKey.equalsIgnoreCase("organisation-id")) {
                    obj = userAttributes.get(attributeKey);
                    this.defaultOrgId = obj.toString().substring(1, obj.toString().length() - 1);
                }
            }
        }

    }

    public UUID getUuid() {
        return this.uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getForename() {
        return this.forename;
    }

    public void setForename(String forename) {
        this.forename = forename;
    }

    public String getSurname() {
        return this.surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail() {
        this.email = this.email;
    }

    public Boolean getAdmin() {
        return this.admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public Boolean getSuperUser() {
        return this.superUser;
    }

    public void setSuperUser(Boolean superUser) {
        this.superUser = superUser;
    }

    public Integer getPermissions() {
        return this.permissions;
    }

    public void setPermissions(Integer permissions) {
        this.permissions = permissions;
    }

    public Boolean getMustChangePassword() {
        return this.mustChangePassword;
    }

    public void setMustChangePassword(Boolean mustChangePassword) {
        this.mustChangePassword = mustChangePassword;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<JsonUserRole> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(List<JsonUserRole> userRoles) {
        this.userRoles = userRoles;
    }

    public String getMobile() {
        return this.mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPhoto() {
        return this.photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getDefaultOrgId() {
        return this.defaultOrgId;
    }

    public void setDefaultOrgId(String defaultOrgId) {
        this.defaultOrgId = defaultOrgId;
    }
}
