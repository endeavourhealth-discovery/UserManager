package org.endeavourhealth.datasharingmanager.api.database.models;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.endeavourhealth.common.config.ConfigManager;
import org.endeavourhealth.datasharingmanager.api.database.MapType;
import org.endeavourhealth.datasharingmanager.api.database.PersistenceManager;
import org.endeavourhealth.datasharingmanager.api.json.JsonAddress;
import org.endeavourhealth.datasharingmanager.api.json.JsonMarker;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "address", schema = "data_sharing_manager")
public class AddressEntity {
    private String uuid;
    private String organisationUuid;
    private String buildingName;
    private String numberAndStreet;
    private String locality;
    private String city;
    private String county;
    private String postcode;
    private Double lat;
    private Double lng;
    private Byte geolocationReprocess;

    @Id
    @Column(name = "uuid", nullable = false, length = 36)
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Basic
    @Column(name = "organisation_uuid", nullable = false, length = 36)
    public String getOrganisationUuid() {
        return organisationUuid;
    }

    public void setOrganisationUuid(String organisationUuid) {
        this.organisationUuid = organisationUuid;
    }

    @Basic
    @Column(name = "building_name", nullable = true, length = 100)
    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    @Basic
    @Column(name = "Number_and_street", nullable = true, length = 100)
    public String getNumberAndStreet() {
        return numberAndStreet;
    }

    public void setNumberAndStreet(String numberAndStreet) {
        this.numberAndStreet = numberAndStreet;
    }

    @Basic
    @Column(name = "locality", nullable = true, length = 100)
    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    @Basic
    @Column(name = "city", nullable = true, length = 100)
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Basic
    @Column(name = "county", nullable = true, length = 100)
    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    @Basic
    @Column(name = "postcode", nullable = true, length = 100)
    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    @Basic
    @Column(name = "lat", nullable = true, precision = 6)
    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    @Basic
    @Column(name = "lng", nullable = true, precision = 6)
    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    @Basic
    @Column(name = "geolocation_reprocess", nullable = true)
    public Byte getGeolocationReprocess() {
        return geolocationReprocess;
    }

    public void setGeolocationReprocess(Byte geolocationReprocess) {
        this.geolocationReprocess = geolocationReprocess;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AddressEntity that = (AddressEntity) o;

        if (uuid != null ? !uuid.equals(that.uuid) : that.uuid != null) return false;
        if (organisationUuid != null ? !organisationUuid.equals(that.organisationUuid) : that.organisationUuid != null)
            return false;
        if (buildingName != null ? !buildingName.equals(that.buildingName) : that.buildingName != null) return false;
        if (numberAndStreet != null ? !numberAndStreet.equals(that.numberAndStreet) : that.numberAndStreet != null)
            return false;
        if (locality != null ? !locality.equals(that.locality) : that.locality != null) return false;
        if (city != null ? !city.equals(that.city) : that.city != null) return false;
        if (county != null ? !county.equals(that.county) : that.county != null) return false;
        if (postcode != null ? !postcode.equals(that.postcode) : that.postcode != null) return false;
        if (lat != null ? !lat.equals(that.lat) : that.lat != null) return false;
        if (lng != null ? !lng.equals(that.lng) : that.lng != null) return false;
        if (geolocationReprocess != null ? !geolocationReprocess.equals(that.geolocationReprocess) : that.geolocationReprocess != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = uuid != null ? uuid.hashCode() : 0;
        result = 31 * result + (organisationUuid != null ? organisationUuid.hashCode() : 0);
        result = 31 * result + (buildingName != null ? buildingName.hashCode() : 0);
        result = 31 * result + (numberAndStreet != null ? numberAndStreet.hashCode() : 0);
        result = 31 * result + (locality != null ? locality.hashCode() : 0);
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + (county != null ? county.hashCode() : 0);
        result = 31 * result + (postcode != null ? postcode.hashCode() : 0);
        result = 31 * result + (lat != null ? lat.hashCode() : 0);
        result = 31 * result + (lng != null ? lng.hashCode() : 0);
        result = 31 * result + (geolocationReprocess != null ? geolocationReprocess.hashCode() : 0);
        return result;
    }

    public AddressEntity() {

    }

    public AddressEntity(JsonAddress address) {
        this.uuid = address.getUuid();
        this.organisationUuid = address.getOrganisationUuid();
        this.buildingName = address.getBuildingName();
        this.numberAndStreet = address.getNumberAndStreet();
        this.locality = address.getLocality();
        this.city = address.getCity();
        this.county = address.getCounty();
        this.postcode = address.getPostcode();
        this.lat = address.getLat();
        this.lng = address.getLng();
        this.geolocationReprocess = 0;
    }

    public static List<AddressEntity> getAddressesForOrganisation(String uuid) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<AddressEntity> cq = cb.createQuery(AddressEntity.class);
        Root<AddressEntity> rootEntry = cq.from(AddressEntity.class);

        Predicate predicate = cb.equal(cb.upper(rootEntry.get("organisationUuid")), uuid.toUpperCase());
        cq.where(predicate);

        TypedQuery<AddressEntity> query = entityManager.createQuery(cq);
        List<AddressEntity> ret = query.getResultList();
        entityManager.close();
        return ret;
    }

    public static void bulkSaveAddresses(List<AddressEntity> addressEntities) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        int batchSize = 50;

        entityManager.getTransaction().begin();

        for (int i = 0; i < addressEntities.size(); i++) {
            AddressEntity addressEntity = addressEntities.get(i);
            entityManager.persist(addressEntity);
            if (i % batchSize == 0){
                entityManager.flush();
                entityManager.clear();
            }
        }

        entityManager.getTransaction().commit();

        entityManager.close();
    }

    public static void saveAddress(JsonAddress address) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        AddressEntity addressEntity = new AddressEntity(address);
        addressEntity.setUuid(address.getUuid());
        entityManager.getTransaction().begin();
        entityManager.persist(addressEntity);
        entityManager.getTransaction().commit();

        entityManager.close();
    }

    public static void updateAddress(JsonAddress address) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        AddressEntity addressEntity = entityManager.find(AddressEntity.class, address.getUuid());
        entityManager.getTransaction().begin();
        addressEntity.setOrganisationUuid(address.getOrganisationUuid());
        addressEntity.setBuildingName(address.getBuildingName());
        addressEntity.setNumberAndStreet(address.getNumberAndStreet());
        addressEntity.setLocality(address.getLocality());
        addressEntity.setCity(address.getCity());
        addressEntity.setCounty(address.getCounty());
        addressEntity.setPostcode(address.getPostcode());
        addressEntity.setGeolocationReprocess((byte)0);
        entityManager.getTransaction().commit();

        entityManager.close();
    }

    public static void updateGeolocation(JsonAddress address) throws Exception {
        EntityManager entityManager = PersistenceManager.getEntityManager();

        AddressEntity addressEntity = entityManager.find(AddressEntity.class, address.getUuid());
        entityManager.getTransaction().begin();
        addressEntity.setLat(address.getLat());
        addressEntity.setLng(address.getLng());
        addressEntity.setGeolocationReprocess((byte)0);
        entityManager.getTransaction().commit();

        entityManager.close();
    }

    private static List<Object[]> getAddressMarkers(String parentUUID, Short parentMapType, Short childMapType) throws Exception {

        EntityManager entityManager = PersistenceManager.getEntityManager();

        Query query = entityManager.createQuery(
                "select o.name, a.lat, a.lng from OrganisationEntity o " +
                        "inner join AddressEntity a on a.organisationUuid = o.uuid " +
                        "inner join MasterMappingEntity mm on mm.childUuid = o.uuid and mm.childMapTypeId = :childMap " +
                        "where mm.parentUuid = :parentUuid " +
                        "and mm.parentMapTypeId = :parentMap");
        query.setParameter("parentUuid", parentUUID);
        query.setParameter("childMap", childMapType);
        query.setParameter("parentMap", parentMapType);

        List<Object[]> result = query.getResultList();

        entityManager.close();

        return result;
    }

    public static Response getOrganisationMarkers(String regionUuid, Short parentMapType, Short childMapType) throws Exception {

        List<Object[]> markers = getAddressMarkers(regionUuid, parentMapType, childMapType);

        List<JsonMarker> ret = new ArrayList<>();

        for (Object[] marker : markers) {
            String name = marker[0].toString();
            Double lat = marker[1]==null?0.0:Double.parseDouble(marker[1].toString());
            Double lng = marker[2]==null?0.0:Double.parseDouble(marker[2].toString());

            JsonMarker jsonMarker = new JsonMarker();
            jsonMarker.setName(name);
            jsonMarker.setLat(lat);
            jsonMarker.setLng(lng);

            ret.add(jsonMarker);
        }

        return Response
                .ok()
                .entity(ret)
                .build();
    }

    public static void getGeoLocationsForOrganisations(List<UUID> organisationUuids) throws Exception {

        for (UUID org : organisationUuids) {
            List<AddressEntity> addressEntities = AddressEntity.getAddressesForOrganisation(org.toString());

            for (AddressEntity address : addressEntities) {
                JsonAddress jsonAddress = new JsonAddress(address);
                getGeolocation(jsonAddress);
            }
        }
    }

    public static void getGeolocation(JsonAddress address) throws Exception {
        Client client = ClientBuilder.newClient();

        JsonNode json = ConfigManager.getConfigurationAsJson("GoogleMapsAPI");
        String url = json.get("url").asText();
        String apiKey = json.get("apiKey").asText();

        WebTarget resource = client.target(url + address.getPostcode().replace(" ", "+") + "&key=" + apiKey);

        Invocation.Builder request = resource.request();
        request.accept(MediaType.APPLICATION_JSON_TYPE);

        Response response = request.get();

        if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
            String s = response.readEntity(String.class);
            JsonParser parser = new JsonParser();
            JsonElement obj = parser.parse(s);
            JsonObject jo = obj.getAsJsonObject();
            JsonElement results = jo.getAsJsonArray("results").get(0);
            JsonObject location = results.getAsJsonObject().getAsJsonObject("geometry").getAsJsonObject("location");

            address.setLat(Double.parseDouble(location.get("lat").toString()));
            address.setLng(Double.parseDouble(location.get("lng").toString()));

            AddressEntity.updateGeolocation(address);
        }


    }
}
