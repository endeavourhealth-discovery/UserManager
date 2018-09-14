package org.endeavourhealth.usermanagermodel.models.caching;

import org.endeavourhealth.usermanagermodel.models.database.ApplicationPolicyEntity;

import java.util.HashMap;
import java.util.Map;

public class ApplicationPolicyCache {
    private static Map<String, ApplicationPolicyEntity> applicationPolicyMap = new HashMap<>();

    public static ApplicationPolicyEntity getApplicationPolicyDetails(String applicationPolicyId) throws Exception {
        ApplicationPolicyEntity foundPolicy = null;

        if (applicationPolicyMap.containsKey(applicationPolicyId)) {
            foundPolicy = applicationPolicyMap.get(applicationPolicyId);
        } else {
            foundPolicy = ApplicationPolicyEntity.getApplicationPolicy(applicationPolicyId);
            applicationPolicyMap.put(foundPolicy.getId(), foundPolicy);

        }

        return foundPolicy;

    }
}
