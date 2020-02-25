package org.endeavourhealth.usermanager.api.logic;

import org.endeavourhealth.core.database.dal.DalProvider;
import org.endeavourhealth.core.database.dal.usermanager.DelegationRelationshipDalI;
import org.endeavourhealth.core.database.dal.usermanager.caching.OrganisationCache;
import org.endeavourhealth.core.database.dal.usermanager.models.JsonDelegatedOrganisation;
import org.endeavourhealth.core.database.dal.usermanager.models.JsonDelegation;
import org.endeavourhealth.core.database.rdbms.datasharingmanager.models.OrganisationEntity;
import org.endeavourhealth.core.database.rdbms.usermanager.models.DelegationRelationshipEntity;
import org.endeavourhealth.usermanager.api.DAL.DelegationDAL;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.endeavourhealth.coreui.endpoints.AbstractEndpoint.clearLogbackMarkers;

public class DelegationLogic {
    private static DelegationRelationshipDalI delegationRelationshipRepository = DalProvider.factoryUMDelegationRelationshipDal();

    public Response getDelegatedOrganisations(String organisationId) throws Exception {

        List<DelegationRelationshipEntity> relationships = delegationRelationshipRepository.getDelegatedOrganisations(organisationId);

        List<String> orgs = relationships.stream()
                .map(DelegationRelationshipEntity::getChildUuid)
                .collect(Collectors.toList());

        orgs.add(organisationId);

        List<OrganisationEntity> orgList = OrganisationCache.getOrganisationDetails(orgs);

        List<JsonDelegatedOrganisation> delegated = new ArrayList<>();

        // Add the parent first as that is the org the user belongs to
        JsonDelegatedOrganisation parentDel = new JsonDelegatedOrganisation();
        OrganisationEntity parentOrg = orgList.stream().filter(o -> o.getUuid().equals(organisationId)).findFirst().orElse(null);
        if (parentOrg != null) {
            parentDel.setUuid(parentOrg.getUuid());
            parentDel.setName(parentOrg.getName());
            parentDel.setOdsCode(parentOrg.getOdsCode());
        }
        delegated.add(parentDel);

        for (DelegationRelationshipEntity rel : relationships) {
            JsonDelegatedOrganisation del = new JsonDelegatedOrganisation(rel);
            OrganisationEntity org = orgList.stream().filter(o -> o.getUuid().equals(rel.getChildUuid())).findFirst().orElse(null);

            if (org != null) {
                del.setName(org.getName());
                del.setOdsCode(org.getOdsCode());
            }

            delegated.add(del);
        }

        clearLogbackMarkers();
        return Response
                .ok()
                .entity(delegated)
                .build();
    }

    public Response saveDelegation(JsonDelegation delegation, String userRoleId) throws Exception {

        if (delegation.getUuid() == null) {
            delegation.setUuid(UUID.randomUUID().toString());
        }

        new DelegationDAL().saveDelegation(delegation, userRoleId);

        clearLogbackMarkers();
        return Response
                .ok()
                .build();
    }
}
