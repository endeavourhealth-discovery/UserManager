package org.endeavourhealth.usermanager.api.endpoints;


import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.annotation.Timed;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.endeavourhealth.common.security.SecurityUtils;
import org.endeavourhealth.common.security.annotations.RequiresAdmin;
import org.endeavourhealth.common.security.usermanagermodel.models.database.DelegationRelationshipEntity;
import org.endeavourhealth.common.security.usermanagermodel.models.json.JsonDelegationRelationship;
import org.endeavourhealth.common.security.usermanagermodel.models.json.JsonOrganisationDelegation;
import org.endeavourhealth.core.data.audit.UserAuditRepository;
import org.endeavourhealth.core.data.audit.models.AuditAction;
import org.endeavourhealth.core.data.audit.models.AuditModule;
import org.endeavourhealth.coreui.endpoints.AbstractEndpoint;
import org.endeavourhealth.usermanager.api.DAL.DelegationRelationshipDAL;
import org.endeavourhealth.usermanager.api.logic.DelegationRelationshipLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.*;

@Path("/delegationRelationship")
@Api(value = "Delegation relationship", description = "API endpoint related to the delegation relationships.")
public class DelegationRelationshipEndpoint extends AbstractEndpoint {
    private static final Logger LOG = LoggerFactory.getLogger(DelegationRelationshipEndpoint.class);

    private static final UserAuditRepository userAudit = new UserAuditRepository(AuditModule.EdsUiModule.User);
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

        List<DelegationRelationshipEntity> delegations = new DelegationRelationshipDAL().getAllRelationshipsForDelegation(delegationId);

        clearLogbackMarkers();
        return Response
                .ok()
                .entity(delegations)
                .build();
    }

    @GET
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

        return new DelegationRelationshipLogic().getDelegationTreeData(delegationId);

    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="UserManager.DelegationRelationshipEndpoint.saveRelationship")
    @Path("/saveRelationship")
    @ApiOperation(value = "Save a new delegation relationship or update an existing one.  Accepts a JSON representation " +
            "of a delegation relationship.")
    @RequiresAdmin
    public Response saveRelationship(@Context SecurityContext sc,
                                     @ApiParam(value = "Json representation of delegation relationship to save or update") JsonDelegationRelationship delegationRelationship,
                                     @ApiParam(value = "User Role Id who is making the change") @QueryParam("userRoleId") String userRoleId) throws Exception {
        super.setLogbackMarkers(sc);
        userAudit.save(SecurityUtils.getCurrentUserId(sc), getOrganisationUuidFromToken(sc), AuditAction.Save,
                "Role Type",
                "roleType", delegationRelationship);

        new DelegationRelationshipDAL().saveDelegationRelationship(delegationRelationship, userRoleId);

        clearLogbackMarkers();
        return Response
                .ok()
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="UserManager.DelegationRelationshipEndpoint.getGodModeOrganisations")
    @Path("/getGodModeOrganisations")
    @ApiOperation(value = "Returns a list of all organisation that are part of a delegation")
    public Response getGodModeOrganisations(@Context SecurityContext sc) throws Exception {

        super.setLogbackMarkers(sc);
        userAudit.save(SecurityUtils.getCurrentUserId(sc), getOrganisationUuidFromToken(sc), AuditAction.Load,
                "Organisation(s)");

        return new DelegationRelationshipLogic().getGodModeOrganisations();

    }


}
