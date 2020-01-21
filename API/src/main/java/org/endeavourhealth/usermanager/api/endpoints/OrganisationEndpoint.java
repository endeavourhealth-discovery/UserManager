package org.endeavourhealth.usermanager.api.endpoints;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.annotation.Timed;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.endeavourhealth.common.security.SecurityUtils;
import org.endeavourhealth.common.security.datasharingmanagermodel.models.DAL.SecurityOrganisationDAL;
import org.endeavourhealth.common.security.datasharingmanagermodel.models.DAL.SecurityProjectDAL;
import org.endeavourhealth.common.security.datasharingmanagermodel.models.database.OrganisationEntity;
import org.endeavourhealth.common.security.datasharingmanagermodel.models.database.ProjectEntity;
import org.endeavourhealth.core.data.audit.UserAuditRepository;
import org.endeavourhealth.core.data.audit.models.AuditAction;
import org.endeavourhealth.core.data.audit.models.AuditModule;
import org.endeavourhealth.coreui.endpoints.AbstractEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;
import java.util.UUID;

@Path("/organisation")
@Api(value = "Organisation", description = "API endpoint related to the organisations.")
public class OrganisationEndpoint extends AbstractEndpoint {
    private static final Logger LOG = LoggerFactory.getLogger(OrganisationEndpoint.class);

    private static final UserAuditRepository userAudit = new UserAuditRepository(AuditModule.EdsUiModule.User);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="UserManager.OrganisationEndpoint.getOrganisation")
    @Path("/search")
    @ApiOperation(value = "Returns a list of organisations based on a search term")
    public Response getOrganisation(@Context SecurityContext sc,
                                    @ApiParam(value = "Optional search term") @QueryParam("searchData") String searchData) throws Exception {

        super.setLogbackMarkers(sc);
        userAudit.save(SecurityUtils.getCurrentUserId(sc), getOrganisationUuidFromToken(sc), AuditAction.Load,
                "Organisation(s)",
                "SearchData", searchData);

        return searchOrganisations(searchData, SecurityUtils.getCurrentUserId(sc));

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="UserManager.OrganisationEndpoint.organisationProjects")
    @Path("/organisationProjects")
    @ApiOperation(value = "Returns a list of projects assigned to an organisation")
    public Response organisationsForUser(@Context SecurityContext sc,
                                    @ApiParam(value = "Organisation id") @QueryParam("organisationId") String organisationId) throws Exception {

        super.setLogbackMarkers(sc);
        userAudit.save(SecurityUtils.getCurrentUserId(sc), getOrganisationUuidFromToken(sc), AuditAction.Load,
                "Projects(s)",
                "organisationId", organisationId);

        return getProjectsForOrganisation(organisationId);

    }

    private Response searchOrganisations(String searchData, UUID userId) throws Exception {

        List<OrganisationEntity> organisations = new SecurityOrganisationDAL().searchOrganisations(searchData, false,
                (byte)0, 1, 50, "name", false, userId);

        clearLogbackMarkers();
        return Response
                .ok()
                .entity(organisations)
                .build();
    }

    private Response getProjectsForOrganisation(String organisationId) throws Exception {

        List<ProjectEntity> projects = new SecurityProjectDAL().getProjectsForOrganisation(organisationId);

        clearLogbackMarkers();
        return Response
                .ok()
                .entity(projects)
                .build();
    }
}
