package org.endeavourhealth.usermanager.api.logic;

import org.endeavourhealth.common.security.datasharingmanagermodel.models.database.OrganisationEntity;
import org.endeavourhealth.common.security.usermanagermodel.models.caching.OrganisationCache;
import org.endeavourhealth.common.security.usermanagermodel.models.database.DelegationRelationshipEntity;
import org.endeavourhealth.common.security.usermanagermodel.models.json.JsonOrganisationDelegation;
import org.endeavourhealth.usermanager.api.DAL.DelegationDAL;
import org.endeavourhealth.usermanager.api.DAL.DelegationRelationshipDAL;

import javax.ws.rs.core.Response;
import java.util.*;
import java.util.stream.Collectors;

import static org.endeavourhealth.coreui.endpoints.AbstractEndpoint.clearLogbackMarkers;

public class DelegationRelationshipLogic {
    private static Map<String, JsonOrganisationDelegation> delegationMap = new HashMap<>();

    public Response getDelegationTreeData(String delegationId) throws Exception {

        List<DelegationRelationshipEntity> delegations = new DelegationRelationshipDAL().getAllRelationshipsForDelegation(delegationId);

        JsonOrganisationDelegation organisationDelegation = null;

        if (delegations.isEmpty()) {
            organisationDelegation = processEmptyDelegation(delegationId);
        } else {
            organisationDelegation = processDelegationOrganisations(delegations);
        }

        clearLogbackMarkers();
        return Response
                .ok()
                .entity(organisationDelegation)
                .build();
    }

    private JsonOrganisationDelegation processEmptyDelegation(String delegationId) throws Exception {
        String rootOrganisation = new DelegationDAL().getRootOrganisation(delegationId);

        OrganisationEntity organisation = OrganisationCache.getOrganisationDetails(rootOrganisation);

        if (organisation != null) {
            JsonOrganisationDelegation orgDelegation = new JsonOrganisationDelegation();
            orgDelegation.setUuid(organisation.getUuid());
            orgDelegation.setName(organisation.getName() + "(" + organisation.getOdsCode() + ")");
            orgDelegation.setCreateSuperUsers(false);
            orgDelegation.setCreateUsers(false);
            orgDelegation.setChildren(new ArrayList<>());

            return orgDelegation;
        }

        return null;
    }

    private JsonOrganisationDelegation processDelegationOrganisations(List<DelegationRelationshipEntity> delegations) throws Exception {

        JsonOrganisationDelegation delegation = new JsonOrganisationDelegation();

        if (delegations.size() > 0) {
            List<String> organisations = delegations.stream()
                    .map(DelegationRelationshipEntity::getParentUuid)
                    .collect(Collectors.toList());

            delegations.stream()
                    .map(DelegationRelationshipEntity::getChildUuid)
                    .forEachOrdered(organisations::add);

            if (organisations.size() > 0) {
                List<OrganisationEntity> orgList = OrganisationCache.getOrganisationDetails(organisations);

                return replaceUuidsWithOrganisation(delegations, orgList);
            }
        }

        return delegation;
    }

    private JsonOrganisationDelegation replaceUuidsWithOrganisation(List<DelegationRelationshipEntity> delegations,
                                                                    List<OrganisationEntity> organisations) throws Exception {
        delegationMap.clear();

        JsonOrganisationDelegation orgDelegation = new JsonOrganisationDelegation();


        for (DelegationRelationshipEntity delegation : delegations) {

            OrganisationEntity parentOrg = organisations.stream().filter(org -> org.getUuid().equals(delegation.getParentUuid())).findFirst().orElse(null);

            OrganisationEntity childOrg = organisations.stream().filter(org -> org.getUuid().equals(delegation.getChildUuid())).findFirst().orElse(null);

            JsonOrganisationDelegation parentOrgDelegation = delegationMap.get(delegation.getParentUuid());

            JsonOrganisationDelegation childOrgDelegation = delegationMap.get(delegation.getChildUuid());

            if (parentOrgDelegation == null) {
                parentOrgDelegation = new JsonOrganisationDelegation();
                if (parentOrg != null) {
                    parentOrgDelegation.setUuid(parentOrg.getUuid());
                    parentOrgDelegation.setName(parentOrg.getName() + "(" + parentOrg.getOdsCode() + ")");
                    delegationMap.put(parentOrg.getUuid(), parentOrgDelegation);
                }
            }

            if (childOrgDelegation == null) {
                childOrgDelegation = new JsonOrganisationDelegation();
                if (childOrg != null) {
                    childOrgDelegation.setUuid(childOrg.getUuid());
                    childOrgDelegation.setName(childOrg.getName() + "(" + childOrg.getOdsCode() + ")");
                    childOrgDelegation.setCreateSuperUsers(delegation.getCreateSuperUsers() == (byte) 0 ? false : true);
                    childOrgDelegation.setCreateUsers(delegation.getCreateUsers() == (byte) 0 ? false : true);
                    delegationMap.put(childOrg.getUuid(), childOrgDelegation);
                }
            } else { // If previously added as a parent...add the options here
                childOrgDelegation.setCreateSuperUsers(delegation.getCreateSuperUsers() == (byte)0 ? false : true);
                childOrgDelegation.setCreateUsers(delegation.getCreateUsers() == (byte)0 ? false : true);
            }
        }

        orgDelegation = delegationMap.get(new DelegationDAL().getRootOrganisation(delegations.get(0).getDelegation()));

        addChildren(orgDelegation, delegations, orgDelegation.getUuid());

        return orgDelegation;
    }

    private void addChildren(JsonOrganisationDelegation delegation, List<DelegationRelationshipEntity> relationships, String parentUuid) throws Exception {
        List<DelegationRelationshipEntity> parents = relationships.stream().filter(rel -> rel.getParentUuid().equals(parentUuid)).collect(Collectors.toList());
        for (DelegationRelationshipEntity parent : parents) {
            delegation.addChild(delegationMap.get(parent.getChildUuid()));
            addChildren(delegationMap.get(parent.getChildUuid()), relationships, parent.getChildUuid());
        }
    }

    public Response getGodModeOrganisations() throws Exception {
        List<DelegationRelationshipEntity> relationships = new DelegationRelationshipDAL().getAllRelationshipsOrganisationsForGodMode();

        List<OrganisationEntity> orgList = new ArrayList<>();
        if (relationships.size() > 0) {
            List<String> organisations = relationships.stream()
                    .map(DelegationRelationshipEntity::getParentUuid)
                    .collect(Collectors.toList());

            relationships.stream()
                    .map(DelegationRelationshipEntity::getChildUuid)
                    .forEachOrdered(organisations::add);

            organisations.add("439e9f06-d54c-3eb6-b800-010863bf1399");

            organisations = organisations.stream().distinct().collect(Collectors.toList());

            if (organisations.size() > 0) {
                orgList = OrganisationCache.getOrganisationDetails(organisations);
                orgList.sort(Comparator.comparing(OrganisationEntity::getName));
            }
        }

        return Response
                .ok()
                .entity(orgList)
                .build();

    }
}
