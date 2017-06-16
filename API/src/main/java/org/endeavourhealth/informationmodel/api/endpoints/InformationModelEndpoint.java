package org.endeavourhealth.informationmodel.api.endpoints;
import com.codahale.metrics.annotation.Timed;
import io.astefanutti.metrics.aspectj.Metrics;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
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
import java.util.List;

@Path("/informationModel")
@Metrics(registry = "informationManagerMetricRegistry")
@Api(description = "Initial api for all calls relating to the information model")
public class InformationModelEndpoint {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="InformationManager.ConceptEndpoint.Get")
    @Path("/")
    @ApiOperation(value = "Returns a list of all concepts")
    public Response get(@Context SecurityContext sc,
                        @ApiParam(value = "Optional Concept Id") @QueryParam("conceptId") Integer conceptId,
                        @ApiParam(value = "Optional Name of concept") @QueryParam("conceptName") String conceptName,
                        @ApiParam(value = "Optional Array of concept Ids") @QueryParam("conceptIdList") List<Integer> conceptIdList
    ) throws Exception {
        System.out.println("Get Called");

        if (conceptId == null && conceptName == null && conceptIdList.size() == 0) {
            return getAllConcepts();
        }
        else if (conceptId != null) {
            return getConceptById(conceptId);
        }
        else if (conceptIdList.size() > 0) {
            return getConceptsByIdList(conceptIdList);
        }
        else {
            return getConceptsByName(conceptName);
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
        ConceptEntity.saveConcept(concept);

        return Response
                .ok()
                .build();

    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="InformationManager.ConceptEndpoint.Put")
    @Path("/")
    @ApiOperation(value = "Updates an existing concept")
    @RequiresAdmin
    public Response put(@Context SecurityContext sc,
                         @ApiParam(value = "Concept Entity to post") JsonConcept concept
    ) throws Exception {

        ConceptEntity.updateConcept(concept);

        return Response
                .ok()
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
                                  @ApiParam(value = "Concept Relationship Id to Delete") @QueryParam("conceptRelationshipId") Integer conceptRelationshipId
    ) throws Exception {
        ConceptRelationshipEntity.deleteConceptRelationship(conceptRelationshipId);

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

    private Response getAllConcepts() throws Exception {
        List<ConceptEntity> concepts = ConceptEntity.getAllConcepts();

        for (ConceptEntity concept : concepts) {
            System.out.println(concept.getName() + " " + concept.getId());
        }

        return Response
                .ok()
                .entity(concepts)
                .build();
    }

    private Response getConceptById(Integer conceptId) throws Exception {
        ConceptEntity concept = ConceptEntity.getConceptById(conceptId);

        System.out.println(concept.getName() + " " + concept.getId());

        return Response
                .ok()
                .entity(concept)
                .build();
    }

    private Response getConceptsByName(String conceptName) throws Exception {
        List<ConceptEntity> concepts = ConceptEntity.getConceptsByName(conceptName);

        for (ConceptEntity concept : concepts) {
            System.out.println(concept.getName() + " " + concept.getId());
        }

        return Response
                .ok()
                .entity(concepts)
                .build();
    }

    private Response getConceptsByIdList(List<Integer> conceptIdList) throws Exception {
        List<ConceptEntity> concepts = ConceptEntity.getConceptsFromList(conceptIdList);

        for (ConceptEntity concept : concepts) {
            System.out.println(concept.getName() + " " + concept.getId());
        }

        return Response
                .ok()
                .entity(concepts)
                .build();
    }

    private Response getConceptRelationships(Integer conceptId) throws Exception {
        List<ConceptRelationshipEntity> concepts = ConceptRelationshipEntity.getConceptsRelationships(conceptId);

        for (ConceptRelationshipEntity concept : concepts) {
            System.out.println(concept.getSourceConcept() + " " + concept.getTargetConcept());
        }

        return Response
                .ok()
                .entity(concepts)
                .build();
    }

    private Response getCommonConcepts(Integer limit) throws Exception {
        List<ConceptEntity> concepts = ConceptEntity.getCommonConcepts(limit);

        for (ConceptEntity concept : concepts) {
            System.out.println(concept.getName() + " " + concept.getId());
        }

        return Response
                .ok()
                .entity(concepts)
                .build();
    }

}
