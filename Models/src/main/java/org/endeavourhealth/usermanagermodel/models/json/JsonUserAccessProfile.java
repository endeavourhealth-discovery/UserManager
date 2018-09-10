package org.endeavourhealth.usermanagermodel.models.json;

import java.util.ArrayList;
import java.util.List;

public class JsonUserAccessProfile {
    private String applicationName = null;
    private String applicationId = null;
    private boolean canAccessData;
    private List<JsonApplicationPolicyAttribute> roleTypeAccessProfiles = null;

    public List<JsonApplicationPolicyAttribute> getRoleTypeAccessProfiles() {
        return roleTypeAccessProfiles;
    }

    public void setRoleTypeAccessProfiles(List<JsonApplicationPolicyAttribute> roleTypeAccessProfiles) {
        this.roleTypeAccessProfiles = roleTypeAccessProfiles;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public void addRoleTypeAccessProfile(JsonApplicationPolicyAttribute profile) {
        if (this.roleTypeAccessProfiles == null) {
            this.roleTypeAccessProfiles = new ArrayList<>();
        }

        this.roleTypeAccessProfiles.add(profile);
    }

    public boolean isCanAccessData() {
        return canAccessData;
    }

    public void setCanAccessData(boolean canAccessData) {
        this.canAccessData = canAccessData;
    }
}
