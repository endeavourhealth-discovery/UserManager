package org.endeavourhealth.usermanager.api.endpoints;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.annotation.Timed;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.endeavourhealth.common.security.SecurityUtils;
import org.endeavourhealth.common.security.annotations.RequiresAdmin;
import org.endeavourhealth.common.security.usermanagermodel.models.database.DelegationEntity;
import org.endeavourhealth.common.security.usermanagermodel.models.json.JsonDelegation;
import org.endeavourhealth.core.data.audit.UserAuditRepository;
import org.endeavourhealth.core.data.audit.models.AuditAction;
import org.endeavourhealth.core.data.audit.models.AuditModule;
import org.endeavourhealth.coreui.endpoints.AbstractEndpoint;
import org.endeavourhealth.usermanager.api.DAL.DelegationDAL;
import org.endeavourhealth.usermanager.api.logic.DelegationLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;

import static org.endeavourhealth.common.security.SecurityUtils.getCurrentUserId;

@Path("/delegation")
@Api(value = "Delegation", description = "API endpoint related to the delegation.")
public class DelegationEndpoint extends AbstractEndpoint {
    private static final Logger LOG = LoggerFactory.getLogger(DelegationEndpoint.class);

    private static final UserAuditRepository userAudit = new UserAuditRepository(AuditModule.EdsUiModule.User);

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

        List<DelegationEntity> delegations = new DelegationDAL().getDelegations(organisationId);

        clearLogbackMarkers();
        return Response
                .ok()
                .entity(delegations)
                .build();
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

        return new DelegationLogic().getDelegatedOrganisations(organisationId);

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

        return new DelegationLogic().saveDelegation(delegation, userRoleId);
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

        new DelegationDAL().deleteDelegation(delegationId, userRoleId);

        return Response
                .ok()
                .build();
    }
}
