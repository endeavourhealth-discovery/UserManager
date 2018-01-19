package org.endeavourhealth.datasharingmanager.api.database;

import com.fasterxml.jackson.databind.JsonNode;
import org.endeavourhealth.common.config.ConfigManager;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

public class PersistenceManager {
    private static EntityManagerFactory subscriberEntityManagerFactory;
    private static EntityManagerFactory ehrEntityManagerFactory;

    public static EntityManager getEntityManager() throws Exception {

        if (subscriberEntityManagerFactory == null
                || !subscriberEntityManagerFactory.isOpen()) {
            createEntityManager();
        }

        return subscriberEntityManagerFactory.createEntityManager();
    }

    public static EntityManager getEhrEntityManager() throws Exception {

        if (ehrEntityManagerFactory == null
                || !ehrEntityManagerFactory.isOpen()) {
            createEhrEntityManager();
        }

        return ehrEntityManagerFactory.createEntityManager();
    }

    private static synchronized void createEntityManager() throws Exception {

        if (subscriberEntityManagerFactory != null
                && subscriberEntityManagerFactory.isOpen()) {
            return;
        }

        JsonNode json = ConfigManager.getConfigurationAsJson("ceg_enterprise", "db_subscriber");
        String url = json.get("url").asText();
        String user = json.get("username").asText();
        String pass = json.get("password").asText();

        Map<String, Object> properties = new HashMap<>();
        //properties.put("hibernate.temp.use_jdbc_metadata_defaults", "false");
        properties.put("hibernate.connection.url", url);
        properties.put("hibernate.connection.username", user);
        properties.put("hibernate.connection.password", pass);

        subscriberEntityManagerFactory = Persistence.createEntityManagerFactory("Subscriber", properties);
    }

    private static synchronized void createEhrEntityManager() throws Exception {

        if (ehrEntityManagerFactory != null
                && ehrEntityManagerFactory.isOpen()) {
            return;
        }

        JsonNode json = ConfigManager.getConfigurationAsJson("db_ehr", "global");
        String url = json.get("url").asText();
        String user = json.get("username").asText();
        String pass = json.get("password").asText();

        Map<String, Object> properties = new HashMap<>();
        //properties.put("hibernate.temp.use_jdbc_metadata_defaults", "false");
        properties.put("hibernate.connection.url", url);
        properties.put("hibernate.connection.username", user);
        properties.put("hibernate.connection.password", pass);

        ehrEntityManagerFactory = Persistence.createEntityManagerFactory("ehr", properties);
    }
}
