package org.endeavourhealth.usermanager.api.endpoints;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.annotation.Timed;
import io.astefanutti.metrics.aspectj.Metrics;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.endeavourhealth.common.security.SecurityUtils;
import org.endeavourhealth.common.security.annotations.RequiresAdmin;
import org.endeavourhealth.core.data.audit.UserAuditRepository;
import org.endeavourhealth.core.data.audit.models.AuditAction;
import org.endeavourhealth.core.data.audit.models.AuditModule;
import org.endeavourhealth.coreui.endpoints.AbstractEndpoint;
import org.endeavourhealth.datasharingmanagermodel.models.database.OrganisationEntity;
import org.endeavourhealth.usermanager.api.metrics.UserManagerMetricListener;
import org.endeavourhealth.usermanagermodel.models.caching.OrganisationCache;
import org.endeavourhealth.usermanagermodel.models.database.DelegationEntity;
import org.endeavourhealth.usermanagermodel.models.database.DelegationRelationshipEntity;
import org.endeavourhealth.usermanagermodel.models.json.JsonDelegatedOrganisation;
import org.endeavourhealth.usermanagermodel.models.json.JsonDelegation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.endeavourhealth.common.security.SecurityUtils.getCurrentUserId;

@Path("/delegation")
@Metrics(registry = "UserManagerRegistry")
@Api(description = "API endpoint related to the delegation.")
public class DelegationEndpoint extends AbstractEndpoint {
    private static final Logger LOG = LoggerFactory.getLogger(DelegationEndpoint.class);

    private static final UserAuditRepository userAudit = new UserAuditRepository(AuditModule.EdsUiModule.User);
    private static final MetricRegistry metricRegistry = UserManagerMetricListener.userManagerMetricRegistry;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="UserManager.DelegationEndpoint.getAllRelationshipsForDelegation")
    @Path("/get")
    @ApiOperation(value = "Returns a list of delegations")
    public Response getDelegations(@Context SecurityContext sc,
                                   @ApiParam(value = "organisation Id") @QueryParam("organisationId") String organisationId) throws Exception {

        super.setLogbackMarkers(sc);
        userAudit.save(SecurityUtils.getCurrentUserId(sc), getOrganisationUuidFromToken(sc), AuditAction.Load,
                "Organisation(s)");

        return getDelegations(organisationId);

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="UserManager.DelegationEndpoint.getDelegatedOrganisations")
    @Path("/getDelegatedOrganisations")
    @ApiOperation(value = "Returns a list of roles available at delegated organisations")
    public Response getDelegatedOrganisations(@Context SecurityContext sc,
                                              @ApiParam(value = "organisation Id") @QueryParam("organisationId") String organisationId) throws Exception {

        // TODO remove the hack and select organisation based on userId
        super.setLogbackMarkers(sc);
        userAudit.save(SecurityUtils.getCurrentUserId(sc), getOrganisationUuidFromToken(sc), AuditAction.Load,
                "Organisation(s)");

        return getDelegatedOrganisations(organisationId);

    }

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="UserManager.DelegationEndpoint.saveDelegation")
    @Path("/saveDelegation")
    @ApiOperation(value = "Save a new delegation or update an existing one.  Accepts a JSON representation " +
            "of a delegation.")
    @RequiresAdmin
    public Response saveDelegation(@Context SecurityContext sc,
                                   @ApiParam(value = "Json representation of delegation to save or update") JsonDelegation delegation,
                                   @ApiParam(value = "User Role Id who is making the change") @QueryParam("userRoleId") String userRoleId) throws Exception {
        super.setLogbackMarkers(sc);
        userAudit.save(SecurityUtils.getCurrentUserId(sc), getOrganisationUuidFromToken(sc), AuditAction.Save,
                "Role Type",
                "roleType", delegation);

        return saveDelegation(delegation, userRoleId);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="UserManager.DelegationEndpoint.deleteDelegation")
    @Path("/deleteDelegation")
    @RequiresAdmin
    @ApiOperation(value = "Deletes a delegation")
    public Response deleteDelegation(@Context SecurityContext sc,
                               @ApiParam(value = "Delegation id to be deleted") @QueryParam("delegationId") String delegationId,
                               @ApiParam(value = "User Role Id who is making the change") @QueryParam("userRoleId") String userRoleId) throws Exception {
        super.setLogbackMarkers(sc);

        userAudit.save(getCurrentUserId(sc), getOrganisationUuidFromToken(sc), AuditAction.Delete,
                "User", "DelegationId", delegationId, "userRoleId", userRoleId);

        deleteDelegation(delegationId, userRoleId);

        return Response
                .ok()
                .build();
    }

    private Response getDelegations(String organisationId) throws Exception {
        List<DelegationEntity> delegations = DelegationEntity.getDelegations(organisationId);

        clearLogbackMarkers();
        return Response
                .ok()
                .entity(delegations)
                .build();
    }

    private Response getDelegatedOrganisations(String organisationId) throws Exception {

        List<DelegationRelationshipEntity> relationships = DelegationRelationshipEntity.getDelegatedOrganisations(organisationId);

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

    private Response saveDelegation(JsonDelegation delegation, String userRoleId) throws Exception {

        if (delegation.getUuid() == null) {
            delegation.setUuid(UUID.randomUUID().toString());
        }

        DelegationEntity.saveDelegation(delegation, userRoleId);

        clearLogbackMarkers();
        return Response
                .ok()
                .build();
    }

    private void deleteDelegation(String delegationId, String userRoleId) throws Exception {

        DelegationEntity.deleteDelegation(delegationId, userRoleId);

    }
}
