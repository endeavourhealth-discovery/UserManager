package org.endeavourhealth.usermanagermodel.models.caching;

import org.endeavourhealth.common.security.keycloak.client.KeycloakAdminClient;
import org.endeavourhealth.usermanagermodel.models.json.JsonUser;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserCache {

    private static Map<String, UserRepresentation> userMap = new HashMap<>();

    public static UserRepresentation getUserDetails(String userId) throws Exception {
        UserRepresentation foundUser = null;

        if (userMap.containsKey(userId)) {
            foundUser = userMap.get(userId);
        } else {

            KeycloakAdminClient keycloakClient = new KeycloakAdminClient();

            try {
                UserRepresentation user = keycloakClient.realms().users().getUser(userId);

                if (user != null) {
                    userMap.put(user.getId(), user);
                    foundUser = user;
                }
            } catch (Exception e) {

            }
        }

        CacheManager.startScheduler();

        return foundUser;

    }

    public static List<JsonUser> getAllUsers() throws Exception {
        UserRepresentation foundUser = null;

        List<JsonUser> userList = new ArrayList<>();
        List<UserRepresentation> users;
        KeycloakAdminClient keycloakClient = new KeycloakAdminClient();

        try {
            users = keycloakClient.realms().users().getUsers("", 0, 100);

            for (UserRepresentation user : users) {
                userMap.put(user.getId(), user);
                userList.add(new JsonUser(user));
            }
        } catch (Exception e) {

        }

        CacheManager.startScheduler();

        return userList;

    }

    public static void flushCache() throws Exception {
        userMap.clear();
    }
}
