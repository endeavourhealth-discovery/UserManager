package org.endeavourhealth.usermanager.api.endpoints;


import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.annotation.Timed;
import io.astefanutti.metrics.aspectj.Metrics;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.endeavourhealth.common.security.SecurityUtils;
import org.endeavourhealth.core.data.audit.UserAuditRepository;
import org.endeavourhealth.core.data.audit.models.AuditAction;
import org.endeavourhealth.core.data.audit.models.AuditModule;
import org.endeavourhealth.coreui.endpoints.AbstractEndpoint;
import org.endeavourhealth.datasharingmanagermodel.models.database.OrganisationEntity;
import org.endeavourhealth.usermanager.api.metrics.UserManagerMetricListener;
import org.endeavourhealth.usermanagermodel.models.database.DelegationEntity;
import org.endeavourhealth.usermanagermodel.models.database.DelegationRelationshipEntity;
import org.endeavourhealth.usermanagermodel.models.json.JsonOrganisationDelegation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.*;
import java.util.stream.Collectors;

@Path("/delegationRelationship")
@Metrics(registry = "UserManagerRegistry")
@Api(description = "API endpoint related to the delegation relationships.")
public class DelegationRelationshipEndpoint extends AbstractEndpoint {
    private static final Logger LOG = LoggerFactory.getLogger(UserEndpoint.class);

    private static final UserAuditRepository userAudit = new UserAuditRepository(AuditModule.EdsUiModule.User);
    private static final MetricRegistry metricRegistry = UserManagerMetricListener.userManagerMetricRegistry;
    private static Map<String, JsonOrganisationDelegation> delegationMap = new HashMap<>();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="UserManager.DelegationRelationshipEndpoint.getOrganisation")
    @Path("/get")
    @ApiOperation(value = "Returns a list of delegation relationships")
    public Response getDelegationTree(@Context SecurityContext sc,
                                      @ApiParam(value = "Delegation Id") @QueryParam("delegationId") String delegationId) throws Exception {

        super.setLogbackMarkers(sc);
        userAudit.save(SecurityUtils.getCurrentUserId(sc), getOrganisationUuidFromToken(sc), AuditAction.Load,
                "Organisation(s)",
                "delegationId", delegationId);

        return getDelegations(delegationId);

    }@GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="UserManager.DelegationRelationshipEndpoint.getOrganisation")
    @Path("/getd3")
    @ApiOperation(value = "Returns a list of delegation relationships")
    public Response getDelegationTreed3(@Context SecurityContext sc,
                                      @ApiParam(value = "Delegation Id") @QueryParam("delegationId") String delegationId) throws Exception {

        super.setLogbackMarkers(sc);
        userAudit.save(SecurityUtils.getCurrentUserId(sc), getOrganisationUuidFromToken(sc), AuditAction.Load,
                "Organisation(s)",
                "delegationId", delegationId);

        return getDelegations(delegationId);

    }

    private Response getDelegations(String delegationId) throws Exception {

        List<DelegationRelationshipEntity> delegations = DelegationRelationshipEntity.getDelegations(delegationId);

        JsonOrganisationDelegation orgDelegations = processDelegationOrganisations(delegations);

        clearLogbackMarkers();
        return Response
                .ok()
                .entity(orgDelegations)
                .build();
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
                List<OrganisationEntity> orgList = OrganisationEntity.getOrganisationsFromList(organisations);

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
                parentOrgDelegation.setUuid(parentOrg.getUuid());
                parentOrgDelegation.setName(parentOrg.getName());
                parentOrgDelegation.setOdsCode(parentOrg.getOdsCode());
                delegationMap.put(parentOrg.getUuid(), parentOrgDelegation);
            }

            if (childOrgDelegation == null) {
                childOrgDelegation = new JsonOrganisationDelegation();
                childOrgDelegation.setUuid(childOrg.getUuid());
                childOrgDelegation.setName(childOrg.getName());
                childOrgDelegation.setOdsCode(childOrg.getOdsCode());
                delegationMap.put(childOrg.getUuid(), childOrgDelegation);
            }
        }

        orgDelegation = delegationMap.get(DelegationEntity.getRootOrganisation(delegations.get(0).getDelegation()));

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

}
