package org.endeavourhealth.usermanagermodel.models.caching;

import org.endeavourhealth.usermanagermodel.models.database.ApplicationEntity;

import java.util.HashMap;
import java.util.Map;

public class ApplicationCache {
    private static Map<String, ApplicationEntity> applicationMap = new HashMap<>();

    public static ApplicationEntity getApplicationDetails(String applicationId) throws Exception {
        ApplicationEntity foundRole = null;

        if (applicationMap.containsKey(applicationId)) {
            foundRole = applicationMap.get(applicationId);
        } else {
            foundRole = ApplicationEntity.getApplication(applicationId);
            applicationMap.put(foundRole.getId(), foundRole);

        }

        return foundRole;

    }

    public static void flushCache() throws Exception {
        applicationMap.clear();
    }
}
