package org.endeavourhealth.usermanager.api.endpoints;

import com.amazonaws.util.StringUtils;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.astefanutti.metrics.aspectj.Metrics;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.endeavourhealth.common.cache.ObjectMapperPool;
import org.endeavourhealth.common.security.SecurityUtils;
import org.endeavourhealth.core.data.audit.UserAuditRepository;
import org.endeavourhealth.core.data.audit.models.AuditAction;
import org.endeavourhealth.core.data.audit.models.AuditModule;
import org.endeavourhealth.coreui.endpoints.AbstractEndpoint;
import org.endeavourhealth.datasharingmanagermodel.models.database.DataSharingAgreementEntity;
import org.endeavourhealth.datasharingmanagermodel.models.database.MasterMappingEntity;
import org.endeavourhealth.datasharingmanagermodel.models.database.OrganisationEntity;
import org.endeavourhealth.datasharingmanagermodel.models.enums.MapType;
import org.endeavourhealth.usermanager.api.metrics.UserManagerMetricListener;
import org.endeavourhealth.usermanagermodel.models.database.ApplicationPolicyAttributeEntity;
import org.endeavourhealth.usermanagermodel.models.json.JsonApplicationPolicyAttribute;
import org.endeavourhealth.usermanagermodel.models.json.JsonUserAccessProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import java.util.List;

@Path("/userBio")
@Metrics(registry = "UserManagerRegistry")
@Api(value = "User bio", description = "API endpoint related to the user bio.")
public class UserBioEndpoint extends AbstractEndpoint {
    private static final Logger LOG = LoggerFactory.getLogger(UserBioEndpoint.class);

    private static final UserAuditRepository userAudit = new UserAuditRepository(AuditModule.EdsUiModule.User);
    private static final MetricRegistry metricRegistry = UserManagerMetricListener.userManagerMetricRegistry;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(absolute = true, name="UserManager.UserBioEndpoint.getAccessProfile")
    @Path("/getAccessProfile")
    @ApiOperation(value = "Returns a representation of the access rights for a user role")
    public Response getAccessProfile(@Context SecurityContext sc,
                                     @ApiParam(value = "Role type id") @QueryParam("roleTypeId") String roleTypeId,
                                     @ApiParam(value = "organisation id") @QueryParam("organisationId") String organisationId) throws Exception {

        super.setLogbackMarkers(sc);
        userAudit.save(SecurityUtils.getCurrentUserId(sc), getOrganisationUuidFromToken(sc), AuditAction.Load,
                "application(s)");

        return getAccessProfile(roleTypeId, organisationId);

    }

    private Response getAccessProfile(String roleTypeId, String organisationId) throws Exception {

        List<JsonUserAccessProfile> userProfiles = new ArrayList<>();
        List<JsonApplicationPolicyAttribute> roleProfiles = ApplicationPolicyAttributeEntity.getApplicationPolicyAttributes(roleTypeId);

        for (JsonApplicationPolicyAttribute profile : roleProfiles) {
            JsonUserAccessProfile applicationProfile = userProfiles.stream().filter(app -> app.getApplicationId().equals(profile.getApplicationId())).findFirst().orElse(new JsonUserAccessProfile());
            if (applicationProfile.getApplicationId() == null) {
                applicationProfile.setApplicationId(profile.getApplicationId());
                applicationProfile.setApplicationName(profile.getApplication());
                userProfiles.add(applicationProfile);
            }
            if (!applicationProfile.isCanAccessData()) {  //Only check if false...once we get a positive, move on
                applicationProfile.setCanAccessData(checkForDataAccess(profile.getProfileTree()));
            }
            applicationProfile.addRoleTypeAccessProfile(processAccessProfile(profile, organisationId));

        }

        return Response
                .ok()
                .entity(userProfiles)
                .build();
    }

    private boolean checkForDataAccess(String profileTree) throws Exception {
        JsonNode profileNode = null;
        if (!StringUtils.isNullOrEmpty(profileTree)) {
            profileNode = ObjectMapperPool.getInstance().readTree(profileTree);
            boolean accessToData = profileNode.get("accessToData").asBoolean();

            return accessToData;

        } else {
            return false;
        }
    }

    private JsonApplicationPolicyAttribute processAccessProfile(JsonApplicationPolicyAttribute profile, String organisationId) throws Exception {
        if (!StringUtils.isNullOrEmpty(profile.getProfileTree())) {
            JsonNode profileTreeNode = ObjectMapperPool.getInstance().readTree(profile.getProfileTree());
            if (profileTreeNode.get("accessToData").asBoolean()) {
                //get sharing agreement and insert it into the json
                String sharingAgreementLevel = profileTreeNode.get("sharingAgreementLevel").asText("organisation");
                if (sharingAgreementLevel.equals("organisation")) {
                    JsonNode sharingAgreements = getSharingAgreementsForOrganisationLevel(profileTreeNode, organisationId);
                    if (sharingAgreements != null) {
                        ((ObjectNode) profileTreeNode).set("sharingAgreementsHasAccess", sharingAgreements);
                        profile.setProfileTree(prettyPrintJsonString(profileTreeNode));
                    }
                } else if (sharingAgreementLevel.equals("agreement")) {
                    //sharing agreement specific
                    JsonNode sharingAgreements = getSpecificSharingAgreement(profileTreeNode, organisationId);
                    if (sharingAgreements != null) {
                        ((ObjectNode) profileTreeNode).set("sharingAgreementsHasAccess", sharingAgreements);
                        profile.setProfileTree(prettyPrintJsonString(profileTreeNode));
                    }
                }
            }
            return profile;
        } else
            return profile;
    }

    private JsonNode getSpecificSharingAgreement(JsonNode profileTreeNode, String organisationId) throws Exception {
        JsonNode agreements = profileTreeNode.get("sharingAgreementsCanAccess");

        ObjectMapper mapper = new ObjectMapper();

        JsonNode sharingAgreements = mapper.createObjectNode().putArray("sharingAgreementsHasAccess");

        if (agreements.isArray()) {
            for (JsonNode agreement : agreements) {
                JsonNode sharingAgreementNode = checkOrgCanAccessSharingAgreement(agreement.get("sharingAgreementId").asText(), organisationId);

                if (sharingAgreementNode != null) {
                    ((ArrayNode) sharingAgreements).add(sharingAgreementNode);
                    return sharingAgreements;
                }
            }
        }

        return null;
    }

    private JsonNode checkOrgCanAccessSharingAgreement(String agreementId, String organisationId) throws Exception {
        List<String> publisherUuids = MasterMappingEntity.getChildMappings(agreementId, MapType.DATASHARINGAGREEMENT.getMapType(), MapType.SUBSCRIBER.getMapType());

        List<OrganisationEntity> ret = new ArrayList<>();

        if (publisherUuids.size() > 0) {
            ret = OrganisationEntity.getOrganisationsFromList(publisherUuids);

            OrganisationEntity matchingOrg = ret.stream().filter(org -> org.getUuid().equals(organisationId)).findFirst().orElse(null);
            if (matchingOrg != null) {

                DataSharingAgreementEntity dsa = DataSharingAgreementEntity.getDSA(agreementId);

                return getOrganisationsForSharingAgreement(dsa);
            }
        }

        return null;
    }

    private JsonNode getSharingAgreementsForOrganisationLevel(JsonNode profileTreeNode, String organisationId) throws Exception {
        List<String> dsaUuids = MasterMappingEntity.getParentMappings(organisationId, MapType.SUBSCRIBER.getMapType(), MapType.DATASHARINGAGREEMENT.getMapType());
        List<DataSharingAgreementEntity> ret = new ArrayList<>();

        if (dsaUuids.size() > 0) {
            ret = DataSharingAgreementEntity.getDSAsFromList(dsaUuids);

            ObjectMapper mapper = new ObjectMapper();

            JsonNode sharingAgreements = mapper.createObjectNode().putArray("sharingAgreementsHasAccess");

            for (DataSharingAgreementEntity dsa : ret) {
                JsonNode sharingAgreement = getOrganisationsForSharingAgreement(dsa);
                ((ArrayNode) sharingAgreements).add(sharingAgreement);
                System.out.println(prettyPrintJsonString(sharingAgreements));
            }
            return sharingAgreements;
        }

        return null;
    }

    private JsonNode getOrganisationsForSharingAgreement(DataSharingAgreementEntity dsa) throws Exception {
        List<String> publisherUuids = MasterMappingEntity.getChildMappings(dsa.getUuid(), MapType.DATASHARINGAGREEMENT.getMapType(), MapType.PUBLISHER.getMapType());

        List<OrganisationEntity> ret = new ArrayList<>();

        if (publisherUuids.size() > 0)
            ret = OrganisationEntity.getOrganisationsFromList(publisherUuids);

        ObjectMapper mapper = new ObjectMapper();

        JsonNode sharingAgreement = mapper.createObjectNode();

        ArrayNode orgsArray = mapper.valueToTree(ret);
        JsonNode orgsNode = mapper.createObjectNode().putArray("organisations").addAll(orgsArray);
        ((ObjectNode) sharingAgreement).put("sharingAgreementId", dsa.getUuid());
        ((ObjectNode) sharingAgreement).put("sharingAgreementName", dsa.getName());
        ((ObjectNode) sharingAgreement).set("organisations", orgsNode);

        return sharingAgreement;


    }

    private static String prettyPrintJsonString(JsonNode jsonNode) throws Exception {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Object json = mapper.readValue(jsonNode.toString(), Object.class);
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
        } catch (Exception e) {
            throw new Exception("Converting Json to String failed : " + e.getMessage() );
        }
    }
}
