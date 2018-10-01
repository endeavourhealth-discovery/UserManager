package org.endeavourhealth.usermanagermodel.models.caching;

import org.endeavourhealth.usermanagermodel.models.database.DelegationEntity;

import java.util.HashMap;
import java.util.Map;

public class DelegationCache {

    private static Map<String, DelegationEntity> delegationMap = new HashMap<>();

    public static DelegationEntity getDelegationDetails(String delgationId) throws Exception {
        DelegationEntity foundDelegation = null;

        if (delegationMap.containsKey(delgationId)) {
            foundDelegation = delegationMap.get(delgationId);
        } else {
            foundDelegation = DelegationEntity.getDelegation(delgationId);
            delegationMap.put(foundDelegation.getUuid(), foundDelegation);

        }

        CacheManager.startScheduler();

        return foundDelegation;

    }

    public static void flushCache() throws Exception {
        delegationMap.clear();
    }
}
