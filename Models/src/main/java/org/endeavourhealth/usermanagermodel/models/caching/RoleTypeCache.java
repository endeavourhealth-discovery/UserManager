package org.endeavourhealth.usermanagermodel.models.caching;

import org.endeavourhealth.usermanagermodel.models.database.ApplicationPolicyEntity;

import java.util.HashMap;
import java.util.Map;

public class RoleTypeCache {

    private static Map<String, ApplicationPolicyEntity> roleTypeMap = new HashMap<>();

    public static ApplicationPolicyEntity getRoleDetails(String roleId) throws Exception {
        ApplicationPolicyEntity foundRole = null;

        if (roleTypeMap.containsKey(roleId)) {
            foundRole = roleTypeMap.get(roleId);
        } else {
            foundRole = ApplicationPolicyEntity.getApplicationPolicy(roleId);
            roleTypeMap.put(foundRole.getId(), foundRole);

        }

        return foundRole;

    }
}
