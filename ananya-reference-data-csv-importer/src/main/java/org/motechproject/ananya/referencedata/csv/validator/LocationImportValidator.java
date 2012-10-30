package org.motechproject.ananya.referencedata.csv.validator;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.referencedata.csv.request.LocationImportRequest;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.LocationStatus;
import org.motechproject.ananya.referencedata.flw.repository.AllLocations;
import org.motechproject.ananya.referencedata.csv.response.LocationValidationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LocationImportValidator {

    private AllLocations allLocations;

    @Autowired
    public LocationImportValidator(AllLocations allLocations) {
        this.allLocations = allLocations;
    }

    public LocationValidationResponse validate(LocationImportRequest locationRequest) {
        LocationValidationResponse locationValidationResponse = new LocationValidationResponse();
        String district = locationRequest.getDistrict();
        String block = locationRequest.getBlock();
        String panchayat = locationRequest.getPanchayat();
        String status = locationRequest.getStatus();

        if (StringUtils.isEmpty(district)
                || StringUtils.isEmpty(block)
                || StringUtils.isEmpty(panchayat)) {
            locationValidationResponse.forBlankFieldsInLocation();
        }

        Location alreadyPresentLocation = allLocations.getFor(district, block, panchayat);
        if (alreadyPresentLocation != null && alreadyPresentLocation.getStatus().equals(status)){
            locationValidationResponse.forDuplicateLocation();
        }

        if(!LocationStatus.isValid(status)){
            locationValidationResponse.forInvalidStatus();
        }
        return locationValidationResponse;
    }
}
