package org.endeavourhealth.datasharingmanager.api.endpoints;

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
import org.endeavourhealth.datasharingmanagermodel.models.database.DataProcessingAgreementEntity;
import org.endeavourhealth.datasharingmanagermodel.models.database.DatasetEntity;
import org.endeavourhealth.datasharingmanagermodel.models.database.MasterMappingEntity;
import org.endeavourhealth.datasharingmanagermodel.models.enums.MapType;
import org.endeavourhealth.datasharingmanagermodel.models.json.JsonDataSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Path("/dataSet")
@Metrics(registry = "EdsRegistry")
@Api(description = "API endpoint related to the Cohorts")
public final class DataSetEndpoint extends AbstractEndpoint {
    private static final Logger LOG = LoggerFactory.getLogger(DataSetEndpoint.class);

    private static final UserAuditRepository userAudit = new UserAuditRepository(AuditModule.EdsUiModule.Organisation);


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="DataSharingManager.DataSetEndpoint.Get")
    @Path("/")
    @ApiOperation(value = "Return either all data sets if no parameter is provided or search for " +
            "data sets using a UUID or a search term. Search matches on name of data sets. " +
            "Returns a JSON representation of the matching set of data sets")
    public Response getDataSet(@Context SecurityContext sc,
                              @ApiParam(value = "Optional uuid") @QueryParam("uuid") String uuid,
                              @ApiParam(value = "Optional search term") @QueryParam("searchData") String searchData
    ) throws Exception {
        super.setLogbackMarkers(sc);
        userAudit.save(SecurityUtils.getCurrentUserId(sc), getOrganisationUuidFromToken(sc), AuditAction.Load,
                "Data Set(s)",
                "Data Set Id", uuid,
                "SearchData", searchData);


        if (uuid == null && searchData == null) {
            LOG.trace("getDataSet - list");

            return getDataSetList();
        } else if (uuid != null){
            LOG.trace("getDataSet - single - " + uuid);
            return getSingleDataSet(uuid);
        } else {
            LOG.trace("Search Data Set - " + searchData);
            return search(searchData);
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="DataSharingManager.DataSetEndpoint.Post")
    @Path("/")
    @ApiOperation(value = "Save a new data set or update an existing one.  Accepts a JSON representation " +
            "of a cohort.")
    @RequiresAdmin
    public Response postDataSet(@Context SecurityContext sc,
                               @ApiParam(value = "Json representation of data set to save or update") JsonDataSet dataSet
    ) throws Exception {
        super.setLogbackMarkers(sc);
        userAudit.save(SecurityUtils.getCurrentUserId(sc), getOrganisationUuidFromToken(sc), AuditAction.Save,
                "Data set",
                "Data set", dataSet);

        if (dataSet.getUuid() != null) {
            MasterMappingEntity.deleteAllMappings(dataSet.getUuid());
            DatasetEntity.updateDataSet(dataSet);
        } else {
            dataSet.setUuid(UUID.randomUUID().toString());
            DatasetEntity.saveDataSet(dataSet);
        }

        MasterMappingEntity.saveDataSetMappings(dataSet);

        clearLogbackMarkers();

        return Response
                .ok()
                .build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="DataSharingManager.DataSetEndpoint.Delete")
    @Path("/")
    @ApiOperation(value = "Delete a data set based on UUID that is passed to the API.  Warning! This is permanent.")
    @RequiresAdmin
    public Response deleteDataSet(@Context SecurityContext sc,
                                 @ApiParam(value = "UUID of the data set to be deleted") @QueryParam("uuid") String uuid
    ) throws Exception {
        super.setLogbackMarkers(sc);
        userAudit.save(SecurityUtils.getCurrentUserId(sc), getOrganisationUuidFromToken(sc), AuditAction.Delete,
                "data set",
                "data set Id", uuid);

        DatasetEntity.deleteDataSet(uuid);

        clearLogbackMarkers();
        return Response
                .ok()
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="DataSharingManager.DataSetEndpoint.GetDataProcessingAgreements")
    @Path("/dpas")
    @ApiOperation(value = "Returns a list of Json representations of Data Processing Agreements that are linked " +
            "to the data set.  Accepts a UUID of a data set.")
    public Response getDpaForCohort(@Context SecurityContext sc,
                                    @ApiParam(value = "UUID of cohort") @QueryParam("uuid") String uuid
    ) throws Exception {
        super.setLogbackMarkers(sc);
        userAudit.save(SecurityUtils.getCurrentUserId(sc), getOrganisationUuidFromToken(sc), AuditAction.Load,
                "DPA(s)",
                "data set Id", uuid);

        return getLinkedDpas(uuid);
    }

    private Response getDataSetList() throws Exception {

        List<DatasetEntity> dataSets = DatasetEntity.getAllDataSets();

        clearLogbackMarkers();
        return Response
                .ok()
                .entity(dataSets)
                .build();
    }

    private Response getSingleDataSet(String uuid) throws Exception {
        DatasetEntity dataSet = DatasetEntity.getDataSet(uuid);

        return Response
                .ok()
                .entity(dataSet)
                .build();

    }

    private Response search(String searchData) throws Exception {
        Iterable<DatasetEntity> dataSets = DatasetEntity.search(searchData);

        clearLogbackMarkers();
        return Response
                .ok()
                .entity(dataSets)
                .build();
    }

    private Response getLinkedDpas(String cohortUuid) throws Exception {

        List<String> dpaUuids = MasterMappingEntity.getParentMappings(cohortUuid, MapType.DATASET.getMapType(), MapType.DATAPROCESSINGAGREEMENT.getMapType());

        List<DataProcessingAgreementEntity> ret = new ArrayList<>();

        if (dpaUuids.size() > 0)
            ret = DataProcessingAgreementEntity.getDPAsFromList(dpaUuids);

        clearLogbackMarkers();
        return Response
                .ok()
                .entity(ret)
                .build();
    }

}
