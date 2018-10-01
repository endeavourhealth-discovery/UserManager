package org.endeavourhealth.usermanagermodel.models.caching;

import org.endeavourhealth.datasharingmanagermodel.models.database.OrganisationEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrganisationCache {

    private static Map<String, OrganisationEntity> organisationMap = new HashMap<>();

    public static List<OrganisationEntity> getOrganisationDetails(List<String> organisations) throws Exception {
        List<OrganisationEntity> organisationEntities = new ArrayList<>();
        List<String> missingOrgs = new ArrayList<>();

        for (String org : organisations) {
            if (organisationMap.containsKey(org)) {
                organisationEntities.add(organisationMap.get(org));
            } else {
                missingOrgs.add(org);
            }
        }

        if (missingOrgs.size() > 0) {
            List<OrganisationEntity> entities = OrganisationEntity.getOrganisationsFromList(missingOrgs);

            for (OrganisationEntity org : entities) {
                organisationMap.put(org.getUuid(), org);
                organisationEntities.add(org);
            }
        }

        CacheManager.startScheduler();

        return organisationEntities;

    }

    public static OrganisationEntity getOrganisationDetails(String organisationId) throws Exception {
        OrganisationEntity organisationEntity = null;

        if (organisationMap.containsKey(organisationId)) {
            organisationEntity = organisationMap.get(organisationId);
        } else {
            organisationEntity = OrganisationEntity.getOrganisation(organisationId);
            organisationMap.put(organisationEntity.getUuid(), organisationEntity);
        }

        CacheManager.startScheduler();

        return organisationEntity;

    }

    public static void flushCache() throws Exception {
        organisationMap.clear();
    }
}
