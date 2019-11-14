package org.endeavourhealth.usermanager.api.logic;

import org.endeavourhealth.common.security.datasharingmanagermodel.models.database.OrganisationEntity;
import org.endeavourhealth.common.security.usermanagermodel.models.DAL.SecurityDelegationRelationshipDAL;
import org.endeavourhealth.common.security.usermanagermodel.models.caching.OrganisationCache;
import org.endeavourhealth.common.security.usermanagermodel.models.database.DelegationRelationshipEntity;
import org.endeavourhealth.common.security.usermanagermodel.models.json.JsonDelegatedOrganisation;
import org.endeavourhealth.common.security.usermanagermodel.models.json.JsonDelegation;
import org.endeavourhealth.usermanager.api.DAL.DelegationDAL;
import org.endeavourhealth.usermanager.api.DAL.DelegationRelationshipDAL;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.endeavourhealth.coreui.endpoints.AbstractEndpoint.clearLogbackMarkers;

public class DelegationLogic {

    public Response getDelegatedOrganisations(String organisationId) throws Exception {

        List<DelegationRelationshipEntity> relationships = new SecurityDelegationRelationshipDAL().getDelegatedOrganisations(organisationId);

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
