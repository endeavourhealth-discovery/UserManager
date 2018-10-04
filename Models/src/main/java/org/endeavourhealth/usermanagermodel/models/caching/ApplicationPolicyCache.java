package org.endeavourhealth.usermanagermodel.models.caching;

import org.endeavourhealth.usermanagermodel.models.database.ApplicationPolicyAttributeEntity;
import org.endeavourhealth.usermanagermodel.models.database.ApplicationPolicyEntity;
import org.endeavourhealth.usermanagermodel.models.json.JsonApplicationPolicyAttribute;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicationPolicyCache {
    private static Map<String, ApplicationPolicyEntity> applicationPolicyMap = new HashMap<>();
    private static Map<String, List<JsonApplicationPolicyAttribute>> policyAttributeMap = new HashMap<>();

    public static ApplicationPolicyEntity getApplicationPolicyDetails(String applicationPolicyId) throws Exception {
        ApplicationPolicyEntity foundPolicy = null;

        if (applicationPolicyMap.containsKey(applicationPolicyId)) {
            foundPolicy = applicationPolicyMap.get(applicationPolicyId);
        } else {
            foundPolicy = ApplicationPolicyEntity.getApplicationPolicy(applicationPolicyId);
            applicationPolicyMap.put(foundPolicy.getId(), foundPolicy);

        }

        CacheManager.startScheduler();
        return foundPolicy;

    }

    public static List<JsonApplicationPolicyAttribute> getApplicationPolicyAttributes(String applicationPolicyId) throws Exception {
        List<JsonApplicationPolicyAttribute> foundAttributes = null;

        if (policyAttributeMap.containsKey(applicationPolicyId)) {
            foundAttributes = policyAttributeMap.get(applicationPolicyId);
        } else {
            foundAttributes = ApplicationPolicyAttributeEntity.getApplicationPolicyAttributes(applicationPolicyId);
            policyAttributeMap.put(applicationPolicyId, foundAttributes);
        }

        CacheManager.startScheduler();
        return foundAttributes;

    }

    public static void flushCache() throws Exception {
        applicationPolicyMap.clear();
        policyAttributeMap.clear();
    }
}
