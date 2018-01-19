package org.endeavourhealth.datasharingmanager.api.endpoints;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.databind.JsonNode;
import io.astefanutti.metrics.aspectj.Metrics;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.endeavourhealth.common.config.ConfigManager;
import org.endeavourhealth.common.security.SecurityUtils;
import org.endeavourhealth.core.data.audit.UserAuditRepository;
import org.endeavourhealth.core.data.audit.models.AuditAction;
import org.endeavourhealth.core.data.audit.models.AuditModule;
import org.endeavourhealth.coreui.endpoints.AbstractEndpoint;
import org.endeavourhealth.datasharingmanager.api.database.models.PseudoIdMapEntity;
import org.endeavourhealth.datasharingmanager.api.database.models.ResourceCurrentEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;

import static org.endeavourhealth.datasharingmanager.api.database.models.PseudoIdMapEntity.getPatientIdListFromPseudoList;

@Path("/subscriber")
@Metrics(registry = "EdsRegistry")
@Api(description = "API endpoint related to the subscriber utilities")
public class SubscriberUtilityEndpoint extends AbstractEndpoint {
    private static final Logger LOG = LoggerFactory.getLogger(SubscriberUtilityEndpoint.class);

    private static final UserAuditRepository userAudit = new UserAuditRepository(AuditModule.EdsUiModule.Organisation);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="DataSharingManager.RegionEndpoint.GetOrganisations")
    @Path("/reverseLookupPatients")
    @ApiOperation(value = "Returns a list of Json representations of re-identified patients that have been pseudonymised " +
            "Accepts a List of pseudo_ids.")
    public Response reverseLookupPatients(@Context SecurityContext sc,
                                         @ApiParam(value = "List of Pseudo Ids") @QueryParam("pseudoIds") List<String> pseudoIds) throws Exception {
        super.setLogbackMarkers(sc);
        userAudit.save(SecurityUtils.getCurrentUserId(sc), getOrganisationUuidFromToken(sc), AuditAction.Load,
                "Organisation(s)",
                "pseudo Ids", pseudoIds);

        return reverseLookupPatients(pseudoIds);
    }

    private Response reverseLookupPatients(List<String> pseudoIds) throws Exception {


        List<PseudoIdMapEntity> patients = PseudoIdMapEntity.getPatientIdListFromPseudoList(pseudoIds);

        List<String> resources = ResourceCurrentEntity.getResourceData(patients);

        return Response
                .ok()
                .entity(resources)
                .build();
    }
}
