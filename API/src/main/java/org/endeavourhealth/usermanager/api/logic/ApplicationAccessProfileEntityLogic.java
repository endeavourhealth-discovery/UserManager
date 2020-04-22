package org.endeavourhealth.usermanager.api.logic;

import org.endeavourhealth.core.database.dal.usermanager.models.JsonApplicationAccessProfile;
import org.endeavourhealth.usermanager.api.DAL.ApplicationAccessProfileDAL;

import javax.ws.rs.core.Response;
import java.util.List;

import static org.endeavourhealth.coreui.endpoints.AbstractEndpoint.clearLogbackMarkers;

public class ApplicationAccessProfileEntityLogic {

    public Response saveApplicationProfiles(List<JsonApplicationAccessProfile> applicationProfiles, String userRoleId) throws Exception {

        for (JsonApplicationAccessProfile applicationProfile : applicationProfiles) {
            new ApplicationAccessProfileDAL().saveApplicationProfile(applicationProfile, userRoleId);
        }

        clearLogbackMarkers();
        return Response
                .ok()
                .build();
    }
}
