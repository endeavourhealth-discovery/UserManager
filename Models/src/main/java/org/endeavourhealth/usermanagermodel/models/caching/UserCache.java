package org.endeavourhealth.usermanagermodel.models.caching;

import org.endeavourhealth.common.security.keycloak.client.KeycloakAdminClient;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.HashMap;
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

        return foundUser;

    }

    public static void flushCache() throws Exception {
        userMap.clear();
    }
}
