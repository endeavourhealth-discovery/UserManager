package org.endeavourhealth.usermanagermodel.models.caching;

import org.endeavourhealth.usermanagermodel.models.database.RoleTypeEntity;

import java.util.HashMap;
import java.util.Map;

public class RoleTypeCache {

    private static Map<String, RoleTypeEntity> roleTypeMap = new HashMap<>();

    public static RoleTypeEntity getRoleDetails(String roleId) throws Exception {
        RoleTypeEntity foundRole = null;

        if (roleTypeMap.containsKey(roleId)) {
            foundRole = roleTypeMap.get(roleId);
        } else {
            foundRole = RoleTypeEntity.getRoleType(roleId);
            roleTypeMap.put(foundRole.getId(), foundRole);

        }

        return foundRole;

    }
}
