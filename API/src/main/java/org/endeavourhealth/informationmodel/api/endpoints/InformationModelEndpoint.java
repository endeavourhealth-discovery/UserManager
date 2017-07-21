package org.endeavourhealth.informationmodel.api.endpoints;
import com.codahale.metrics.annotation.Timed;
import io.astefanutti.metrics.aspectj.Metrics;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.endeavourhealth.common.security.annotations.RequiresAdmin;
import org.endeavourhealth.informationmodel.api.database.models.ConceptEntity;
import org.endeavourhealth.informationmodel.api.database.models.ConceptRelationshipEntity;
import org.endeavourhealth.informationmodel.api.json.JsonConcept;
import org.endeavourhealth.informationmodel.api.json.JsonConceptRelationship;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.*;
import java.util.stream.Collectors;

@Path("/informationModel")
@Metrics(registry = "informationManagerMetricRegistry")
@Api(description = "Initial api for all calls relating to the information model")
public class InformationModelEndpoint {

    private static List<ConceptEntity> snomedConceptEntities = new ArrayList<>();
    private static List<ConceptRelationshipEntity> snomedRelationshipEntities = new ArrayList<>();
    private static Date bulkUploadStarted = null;
    private static boolean activeOnly = true;
    private static boolean delta = false;
    private static List<Integer> inactiveRelationships = new ArrayList<>();
    private static List<Integer> inactiveSnomedConcepts = new ArrayList<>();
    private static Integer conceptId = 1;
    private static HashMap<Long, Integer> snomedIdMap = new HashMap<>();
    //private static List<SnomedConceptMapEntity> mapsToSave = new ArrayList<>();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="InformationManager.ConceptEndpoint.Get")
    @Path("/")
    @ApiOperation(value = "Returns a list of all concepts")
    public Response get(@Context SecurityContext sc,
                        @ApiParam(value = "Optional Concept Id") @QueryParam("conceptId") Integer conceptId,
                        @ApiParam(value = "Optional Name of concept") @QueryParam("conceptName") String conceptName,
                        @ApiParam(value = "Optional Array of concept Ids") @QueryParam("conceptIdList") List<Integer> conceptIdList,
                        @ApiParam(value = "Optional page number parameter (default is 1)") @QueryParam("pageNumber") Integer pageNumber,
                        @ApiParam(value = "Optional page size paramater (default is 10)") @QueryParam("pageSize") Integer pageSize
    ) throws Exception {

        if (conceptId == null && conceptName == null && conceptIdList.size() == 0) {
            return getCommonConcepts(20);
        }
        else if (conceptId != null) {
            return getConceptById(conceptId);
        }
        else if (conceptIdList.size() > 0) {
            return getConceptsByIdList(conceptIdList);
        }
        else {
            if (pageNumber == null)
                pageNumber = 1;
            if (pageSize == null)
                pageSize = 10;
            return getConceptsByName(conceptName, pageNumber, pageSize);
        }
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="InformationManager.ConceptEndpoint.Delete")
    @Path("/")
    @ApiOperation(value = "Deletes a concepts based on ConceptId")
    @RequiresAdmin
    public Response deleteConcept(@Context SecurityContext sc,
                                  @ApiParam(value = "Concept Id to Delete") @QueryParam("conceptId") Integer conceptId
    ) throws Exception {
        ConceptEntity.deleteConcept(conceptId);

        return Response
                .ok()
                .build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="InformationManager.ConceptEndpoint.Post")
    @Path("/")
    @ApiOperation(value = "Adds a new concept to the Database")
    @RequiresAdmin
    public Response post(@Context SecurityContext sc,
                         @ApiParam(value = "Concept Entity to post") JsonConcept concept
    ) throws Exception {

        System.out.println("saving concept " + concept.getName());
        concept = ConceptEntity.saveConcept(concept);

        return Response
                .ok()
                .entity(concept)
                .build();

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="InformationManager.ConceptEndpoint.GetRelationships")
    @Path("/relationships")
    @ApiOperation(value = "Returns a list of all concept relationships")
    public Response getRelationships(@Context SecurityContext sc,
                                     @ApiParam(value = "Concept Id") @QueryParam("conceptId") Integer conceptId
    ) throws Exception {

        return getConceptRelationships(conceptId);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="InformationManager.ConceptEndpoint.PostRelationship")
    @Path("/relationships")
    @ApiOperation(value = "Adds a new concept relationship between two concepts")
    @RequiresAdmin
    public Response postRelationship(@Context SecurityContext sc,
                                     @ApiParam(value = "Concept Relationship Entity to post") JsonConceptRelationship conceptRelationship
    ) throws Exception {

        ConceptRelationshipEntity.saveConceptRelationship(conceptRelationship);

        return Response
                .ok()
                .build();

    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="InformationManager.ConceptEndpoint.DeleteRelationship")
    @Path("/relationships")
    @ApiOperation(value = "Deletes a relationship between two concepts")
    @RequiresAdmin
    public Response deleteConceptRelationship(@Context SecurityContext sc,
                                              @ApiParam(value = "Concept Relationship Id to Delete") @QueryParam("conceptRelationshipId") Long conceptRelationshipId
    ) throws Exception {
        //ConceptRelationshipEntity.deleteConceptRelationship(conceptRelationshipId);

        return Response
                .ok()
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="InformationManager.ConceptEndpoint.GetCommonConcepts")
    @Path("/common")
    @ApiOperation(value = "Returns a list of common concept relationships restricted by limit passed into API")
    public Response getCommonConcepts(@Context SecurityContext sc,
                                      @ApiParam(value = "limit of number of common concepts to return") @QueryParam("limit") Integer limit
    ) throws Exception {

        return getCommonConcepts(limit);
    }
/*

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="InformationManager.ConceptEndpoint.startUpload")
    @Path("/startUpload")
    @ApiOperation(value = "Prepares the API for a bulk upload of snomed codes")
    public Response startUpload(@Context SecurityContext sc,
                                @ApiParam(value = "json configuration options") JsonSnomedConfig config) throws Exception {

        return startUpload(config);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    @Timed(absolute = true, name="InformationManager.ConceptEndpoint.SnomedUpload")
    @Path("/snomedUpload")
    @ApiOperation(value = "Bulk Upload snomed concepts")
    @RequiresAdmin
    public Response uploadSnomed(@Context SecurityContext sc,
                                 @ApiParam(value = "Bulk CSV file to upload") String csvFile
    ) throws Exception {

        return bulkUploadSnomed(csvFile);

    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    @Timed(absolute = true, name="InformationManager.ConceptEndpoint.uploadSnomedRelationships")
    @Path("/snomedRelationshipUpload")
    @ApiOperation(value = "Bulk Upload snomed concept relationships")
    @RequiresAdmin
    public Response uploadSnomedRelationships(@Context SecurityContext sc,
                                              @ApiParam(value = "Bulk CSV file to upload") String csvFile
    ) throws Exception {

        return bulkUploadSnomedRelationships(csvFile);

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="InformationManager.ConceptEndpoint.saveConcepts")
    @Path("/saveConcepts")
    @ApiOperation(value = "Save concepts to the database.  Part of the bulk upload process")
    public Response saveConcepts(@Context SecurityContext sc,
                                 @ApiParam(value = "Number of concepts to save in this batch") @QueryParam("limit") Integer limit) throws Exception {

        return saveConcepts(limit);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="InformationManager.ConceptEndpoint.saveRelationships")
    @Path("/saveRelationships")
    @ApiOperation(value = "Save relationships to the database.  Part of the bulk upload process")
    public Response saveRelationships(@Context SecurityContext sc,
                                      @ApiParam(value = "Number of concepts to save in this batch") @QueryParam("limit") Integer limit) throws Exception {

        return saveRelationships(limit);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="InformationManager.ConceptEndpoint.setInactiveSnomed")
    @Path("/setInactiveSnomed")
    @ApiOperation(value = "As part of a delta upload, update all the now inactive snomed concepts to be inactive")
    public Response setInactiveSnomed(@Context SecurityContext sc) throws Exception {

        return setInactiveSnomedConcepts();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="InformationManager.ConceptEndpoint.deleteInactiveRelationships")
    @Path("/deleteInactiveRelationships")
    @ApiOperation(value = "As part of a delta upload, delete all the now inactive snomed relationships")
    public Response deleteInactiveRelationships(@Context SecurityContext sc) throws Exception {

        return deleteInactiveSnomedRelationships();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    @Timed(absolute = true, name="InformationManager.ConceptEndpoint.completeUpload")
    @Path("/completeUpload")
    @ApiOperation(value = "Finalises the bulk upload of snomed codes by saving concepts to the database")
    public Response completeUpload(@Context SecurityContext sc) throws Exception {

        return completeUpload();
    }
*/

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="InformationManager.ConceptEndpoint.GetRelationshipConcepts")
    @Path("/relationshipConcepts")
    @ApiOperation(value = "Returns a list of the concepts that define relationships between two concepts")
    public Response getRelationshipConcepts(@Context SecurityContext sc) throws Exception {

        return getRelationshipConcepts();
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="InformationManager.ConceptEndpoint.GetClassConcepts")
    @Path("/classConcepts")
    @ApiOperation(value = "Returns a list of the concepts that define classes")
    public Response getClassConcepts(@Context SecurityContext sc) throws Exception {

        return getClassConcepts();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="InformationManager.ConceptEndpoint.searchCount")
    @Path("/searchCount")
    @ApiOperation(value = "Returns the number of search results for a particular concept search")
    public Response getConceptSearchCount(@Context SecurityContext sc,
                                          @ApiParam(value = "Concept Name") @QueryParam("conceptName") String conceptName
    ) throws Exception {

        return getConceptsSearchCount(conceptName);
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="InformationManager.ConceptEndpoint.nextId")
    @Path("/nextId")
    @ApiOperation(value = "Returns the next primary key identifier for the given table")
    public Response getNextId(@Context SecurityContext sc, @ApiParam(value = "Table Name") @QueryParam("tableName") String tableName) throws Exception {
        //long id =  TableIdentityEntity.getNextId(tableName);

        return Response
                .ok()
                .build();
    }

    private Response getAllConcepts() throws Exception {
        List<ConceptEntity> concepts = ConceptEntity.getAllConcepts();

        return Response
                .ok()
                .entity(concepts)
                .build();
    }

    private Response getConceptById(Integer conceptId) throws Exception {
        ConceptEntity concept = ConceptEntity.getConceptById(conceptId);

        return Response
                .ok()
                .entity(concept)
                .build();
    }

    private Response getConceptsByName(String conceptName, Integer pageNumber, Integer pageSize) throws Exception {
        List<ConceptEntity> concepts = ConceptEntity.getConceptsByName(conceptName, pageNumber, pageSize);

        return Response
                .ok()
                .entity(concepts)
                .build();
    }

    private Response getConceptsSearchCount(String conceptName) throws Exception {
        Long count = ConceptEntity.getCountOfConceptSearch(conceptName);

        return Response
                .ok()
                .entity(count)
                .build();
    }

    private Response getConceptsByIdList(List<Integer> conceptIdList) throws Exception {
        List<ConceptEntity> concepts = ConceptEntity.getConceptsFromList(conceptIdList);

        return Response
                .ok()
                .entity(concepts)
                .build();
    }

    private Response getConceptRelationships(Integer conceptId) throws Exception {

        List<ConceptRelationshipEntity> concepts = ConceptRelationshipEntity.getConceptsRelationships(conceptId);

        //List<JsonConceptRelationship> relationships = convertRelationshipToJson(concepts);

        return Response
                .ok()
                .build();
    }

    private Response getCommonConcepts(Integer limit) throws Exception {
        List<ConceptEntity> concepts = ConceptEntity.getCommonConcepts(limit);

        return Response
                .ok()
                .entity(concepts)
                .build();
    }
/*

    private Response startUpload(JsonSnomedConfig config) throws Exception {
        activeOnly = config.isActiveOnly();
        delta = config.isDelta();
        snomedRelationshipEntities.clear();
        snomedIdMap.clear();
        snomedConceptEntities.clear();

        if (bulkUploadStarted != null) {
            long uploadTimeDifference = new Date().getTime() - bulkUploadStarted.getTime();

            if (uploadTimeDifference < 20*60*1000) {
                return Response
                        .ok()
                        .entity("Upload currently in progress.  Please try again in " + Long.toString(20 - (uploadTimeDifference / 60000 )) + " minutes")
                        .build();
            }
        }

        bulkUploadStarted = new Date();

        return Response
                .ok()
                .entity("OK")
                .build();
    }

    private Response saveConcepts(Integer limit) throws Exception {
        List<ConceptEntity> conceptSubset = snomedConceptEntities.stream().limit(limit).collect(Collectors.toList());

        ConceptEntity.bulkSaveConcepts(conceptSubset);

        if (snomedConceptEntities.size() < limit)
            limit = snomedConceptEntities.size();

        snomedConceptEntities.subList(0, limit).clear();


        saveSnomedMaps(limit);

        return Response
                .ok()
                .entity(snomedConceptEntities.size())
                .build();
    }

    private void populateSnomedConceptIdMap() throws Exception {
        List<SnomedConceptMapEntity> snomedMap = SnomedConceptMapEntity.getAllSnomedMappings();

        for (SnomedConceptMapEntity map : snomedMap) {
            snomedIdMap.put(map.getSnomedId(), map.getConceptId());
        }
    }

    private Integer getIdentityId(String tableName, Integer blockSize) throws Exception {

        return TableIdentityEntity.getNextIdBlock(tableName, blockSize);
    }

    private Response saveRelationships(Integer limit) throws Exception {
        List<ConceptRelationshipEntity> relationshipSubset = snomedRelationshipEntities.stream().limit(limit).collect(Collectors.toList());

        ConceptRelationshipEntity.bulkSaveConceptRelationships(relationshipSubset);

        if (snomedRelationshipEntities.size() < limit)
            limit = snomedRelationshipEntities.size();
        snomedRelationshipEntities.subList(0, limit).clear();

        return Response
                .ok()
                .entity(snomedRelationshipEntities.size())
                .build();
    }

    private Response setInactiveSnomedConcepts() throws Exception {

        Integer deleted = ConceptEntity.setInactiveSnomedCodes(inactiveSnomedConcepts);

        return Response
                .ok()
                .entity(deleted)
                .build();
    }

    private Response deleteInactiveSnomedRelationships() throws Exception {

        Integer deleted = ConceptRelationshipEntity.deleteInactiveRelationships(inactiveRelationships);

        return Response
                .ok()
                .entity(deleted)
                .build();
    }

    private void saveSnomedMaps(Integer limit) throws Exception {
        List<SnomedConceptMapEntity> mapSubset = mapsToSave.stream().limit(limit).collect(Collectors.toList());

        SnomedConceptMapEntity.bulkSaveSnomedMap(mapSubset);

        if (mapsToSave.size() < limit)
            limit = mapsToSave.size();

        mapsToSave.subList(0, limit).clear();

    }

    private Response completeUpload() throws Exception {

        snomedRelationshipEntities.clear();
        snomedIdMap.clear();
        snomedConceptEntities.clear();
        bulkUploadStarted = null;

        return Response
                .ok()
                .build();
    }

    private Integer getConceptIdFromSnomed(String snomed) throws Exception {
        Long snomedId = Long.parseLong(snomed);

        Integer foundConceptId = snomedIdMap.get(snomedId);

        if (foundConceptId != null) {
            return foundConceptId;
        } else {
            snomedIdMap.put(snomedId, conceptId);
            SnomedConceptMapEntity newSnomedConcept = new SnomedConceptMapEntity();
            newSnomedConcept.setConceptId(conceptId);
            newSnomedConcept.setSnomedId(snomedId);
            mapsToSave.add(newSnomedConcept);
            return conceptId++;
        }
    }

    private Response bulkUploadSnomed(String csvFile) throws Exception {

        Scanner scanner = new Scanner(csvFile);
        boolean skippedHeaders = false;
        boolean itemActive = false;
        populateSnomedConceptIdMap();

        conceptId = getIdentityId("snomed", countLinesInCsv(csvFile));

        while (scanner.hasNext()) {
            List<String> snomed = CsvHelper.parseLine(scanner.nextLine(), '\t');
            if (!skippedHeaders){
                skippedHeaders = true;
                continue;
            }

            ConceptEntity conceptEntity = createConcept(snomed);

            if (conceptEntity != null) {
                snomedConceptEntities.add(conceptEntity);
            }
        }

        return Response
                .ok()
                .entity(snomedConceptEntities.size())
                .build();
    }

    private Integer countLinesInCsv(String csvFile) throws Exception {
        Integer count = 1;
        Scanner scanner = new Scanner(csvFile);

        while (scanner.hasNext()) {
            count++;
            scanner.nextLine();
        }

        return count;
    }


    private Response bulkUploadSnomedRelationships(String csvFile) throws Exception {

        Scanner scanner = new Scanner(csvFile);
        boolean skippedHeaders = false;

        while (scanner.hasNext()) {
            List<String> relationship = CsvHelper.parseLine(scanner.nextLine(), '\t');
            if (!skippedHeaders){
                skippedHeaders = true;
                continue;
            }

            ConceptRelationshipEntity conceptRelationship = createConceptRelationship(relationship);
            if (conceptRelationship != null) {
                snomedRelationshipEntities.add(conceptRelationship);
            }
        }

        return Response
                .ok()
                .entity(snomedRelationshipEntities.size())
                .build();
    }

    private ConceptEntity createConcept(List<String> snomed) throws Exception {

        //ignore non NHS specified codes descriptions
        if (!snomed.get(6).equals("900000000000003001"))
            return null;

        boolean itemActive = snomed.get(2).equals("1");

        if (activeOnly && delta && !itemActive) {
            //If an inactive item is already in our DB then process it for future updates. Set it to true to pass the next validation
            if (snomedIdMap.get(Long.parseLong(snomed.get(4))) != null)
                itemActive = true;
        }

        if (activeOnly && !itemActive)
            return null;


        ConceptEntity concept = new ConceptEntity();
        String snomedName = snomed.get(7);
        Integer snomedId = getConceptIdFromSnomed(snomed.get(4));
        concept.setName(snomedName.substring(0, Math.min(249, snomedName.length())));
        concept.setId(snomedId);
        concept.setShortName(snomedName.substring(0, Math.min(124, snomedName.length())));
        concept.setDescription(snomedName);
        concept.setStatus(Byte.parseByte(snomed.get(2)));
        concept.setClazz(13);

        return concept;
    }

    private ConceptRelationshipEntity createConceptRelationship(List<String> relationship) throws Exception {

        boolean itemActive = relationship.get(2).equals("1");

        if (activeOnly && delta && !itemActive) {
            inactiveRelationships.add(Integer.parseInt(relationship.get(0)));
        }

        if (activeOnly && !itemActive)
            return null;

        if (!relationship.get(7).equals("116680003"))
            return null;

        Integer source = snomedIdMap.get(Long.parseLong(relationship.get(4)));
        Integer target = snomedIdMap.get(Long.parseLong(relationship.get(5)));

        if (source == null || target == null)
            return null;

        ConceptRelationshipEntity conceptRelationship = new ConceptRelationshipEntity();
        conceptRelationship.setId(Long.parseLong(relationship.get(0)));
        conceptRelationship.setSourceConcept(source);
        conceptRelationship.setTargetConcept(target);
        conceptRelationship.setRelationshipType(105);

        return conceptRelationship;
    }

    private List<JsonConceptRelationship> convertRelationshipToJson(List<Object[]> results) throws Exception {

        List<JsonConceptRelationship> relationships = new ArrayList<>();

        for (Object[] rel : results) {
            String Id = rel[0]==null?"":rel[0].toString();
            String sourceId = rel[1]==null?"":rel[1].toString();
            String sourceName = rel[2]==null?"":rel[2].toString();
            String sourceDescription = rel[3]==null?"":rel[3].toString();
            String sourceShortName = rel[4]==null?"":rel[4].toString();
            String relationshipId = rel[5]==null?"":rel[5].toString();
            String relationshipName = rel[6]==null?"":rel[6].toString();
            String relationshipDescription = rel[7]==null?"":rel[7].toString();
            String relationshipShortName = rel[8]==null?"":rel[8].toString();
            String targetId = rel[9]==null?"":rel[9].toString();
            String targetName = rel[10]==null?"":rel[10].toString();
            String targetDescription = rel[11]==null?"":rel[11].toString();
            String targetShortName = rel[12]==null?"":rel[12].toString();

            JsonConceptRelationship relationship = new JsonConceptRelationship();
            relationship.setId(Long.parseLong(Id));
            relationship.setSourceConcept(Integer.parseInt(sourceId));
            relationship.setSourceConceptName(sourceName);
            relationship.setSourceConceptDescription(sourceDescription);
            relationship.setSourceConceptShortName(sourceShortName);
            relationship.setRelationship_type(Integer.parseInt(relationshipId));
            relationship.setRelationshipTypeName(relationshipName);
            relationship.setRelationshipTypeDescription(relationshipDescription);
            relationship.setRelationshipTypeShortName(relationshipShortName);
            relationship.setTargetConcept(Integer.parseInt(targetId));
            relationship.setTargetConceptName(targetName);
            relationship.setTargetConceptDescription(targetDescription);
            relationship.setTargetConceptShortName(targetShortName);

            relationships.add(relationship);
        }

        return relationships;
    }
*/

    private Response getRelationshipConcepts() throws Exception {
        List<ConceptEntity> concepts = ConceptEntity.getRelationshipConcepts();

        return Response
                .ok()
                .entity(concepts)
                .build();
    }

    private Response getClassConcepts() throws Exception {
        List<ConceptEntity> concepts = ConceptEntity.getClassConcepts();

        return Response
                .ok()
                .entity(concepts)
                .build();
    }

}
