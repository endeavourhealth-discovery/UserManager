package org.endeavourhealth.usermanagermodel.models.json;

public class JsonUserApplicationPolicy {
    private String userId = null;
    private String applicationPolicyId = null;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getApplicationPolicyId() {
        return applicationPolicyId;
    }

    public void setApplicationPolicyId(String applicationPolicyId) {
        this.applicationPolicyId = applicationPolicyId;
    }
}
