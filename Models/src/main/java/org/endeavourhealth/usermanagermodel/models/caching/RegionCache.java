package org.endeavourhealth.usermanagermodel.models.caching;

import org.endeavourhealth.datasharingmanagermodel.models.database.RegionEntity;

import java.util.HashMap;
import java.util.Map;

public class RegionCache {
    private static Map<String, RegionEntity> regionMap = new HashMap<>();

    public static RegionEntity getRegionDetails(String regionId) throws Exception {
        RegionEntity foundRegion = null;

        if (regionMap.containsKey(regionId)) {
            foundRegion = regionMap.get(regionId);
        } else {
            foundRegion = RegionEntity.getSingleRegion(regionId);
            regionMap.put(foundRegion.getUuid(), foundRegion);

        }

        CacheManager.startScheduler();

        return foundRegion;

    }

    public static void flushCache() throws Exception {
        regionMap.clear();
    }
}
