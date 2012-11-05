package org.motechproject.ananya.referencedata.csv.validator;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.referencedata.csv.request.LocationImportRequest;
import org.motechproject.ananya.referencedata.csv.response.LocationValidationResponse;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.LocationStatus;
import org.motechproject.ananya.referencedata.flw.repository.AllLocations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class LocationImportValidator {

    private AllLocations allLocations;

    @Autowired
    public LocationImportValidator(AllLocations allLocations) {
        this.allLocations = allLocations;
    }

    public LocationValidationResponse validate(LocationImportRequest locationRequest, List<LocationImportRequest> locationImportRequests) {
        LocationValidationResponse locationValidationResponse = new LocationValidationResponse();

        validateDistrictBlockPanchayat(locationRequest, locationValidationResponse);

        validateStatus(locationRequest, locationValidationResponse);

        validateDuplicateEntries(locationImportRequests, locationValidationResponse);

        validateInvalidLocation(locationRequest, locationImportRequests, locationValidationResponse);

        validateExistenceInDb(locationRequest, locationValidationResponse);
        return locationValidationResponse;
    }

    private Location getLocationFormDb(String district, String block, String panchayat) {
        return allLocations.getFor(district, block, panchayat);
    }

    private void validateDistrictBlockPanchayat(LocationImportRequest request, LocationValidationResponse locationValidationResponse) {
        if (StringUtils.isEmpty(request.getDistrict())
                || StringUtils.isEmpty(request.getBlock())
                || StringUtils.isEmpty(request.getPanchayat()))
            locationValidationResponse.forBlankFieldsInLocation();
    }

    private void validateStatus(LocationImportRequest request, LocationValidationResponse locationValidationResponse) {
        if (StringUtils.isEmpty(request.getStatus()) || !LocationStatus.isValidCsvStatus(request.getStatus()))
            locationValidationResponse.forInvalidStatus();
    }

    private void validateExistenceInDb(LocationImportRequest locationRequest, LocationValidationResponse locationValidationResponse) {
        Location alreadyPresentLocation = getLocationFormDb(locationRequest.getDistrict(), locationRequest.getBlock(), locationRequest.getPanchayat());
        if (!LocationStatus.isNewStatus(locationRequest.getStatus()) && alreadyPresentLocation == null)
            locationValidationResponse.forLocationNotExisting();
        if (!LocationStatus.isNewStatus(locationRequest.getStatus()) && alreadyPresentLocation != null && !LocationStatus.isUpdatable(alreadyPresentLocation.getStatus().name()))
            locationValidationResponse.forLocationNotExisting();
        if (LocationStatus.isNewStatus(locationRequest.getStatus()) && alreadyPresentLocation != null)
            locationValidationResponse.forLocationExisting();

    }

    private void validateInvalidLocation(LocationImportRequest locationRequest, List<LocationImportRequest> locationImportRequests, LocationValidationResponse locationValidationResponse) {
        if (locationRequest.isForInvalidation())
            validateForInvalidLocations(locationRequest, locationValidationResponse, locationImportRequests);
        else {
            if (locationRequest.hasAlternateLocation()) {
                locationValidationResponse.forNeedlessAlternateLocation();
            }
        }
    }

    private void validateDuplicateEntries(List<LocationImportRequest> locationImportRequests, LocationValidationResponse locationValidationResponse) {
        List<LocationImportRequest> duplicateCheckList = new ArrayList<>();
        for (LocationImportRequest csvImportRequest : locationImportRequests) {
            if (duplicateCheckList.contains(csvImportRequest))
                locationValidationResponse.forDuplicateLocation();
            else duplicateCheckList.add(csvImportRequest);
        }
    }

    private void validateForInvalidLocations(LocationImportRequest locationRequest, LocationValidationResponse locationValidationResponse, List<LocationImportRequest> locationImportRequests) {
        if (!locationRequest.hasAlternateLocation()) {
            locationValidationResponse.forBlankAlternateLocation();
        } else if (!isAlternateLocationPresentInDbAsValid(locationRequest) && !validLocationPresentInCsv(locationRequest, locationImportRequests)) {
            locationValidationResponse.forInvalidAlternateLocation();
        }
    }

    private boolean isAlternateLocationPresentInDbAsValid(LocationImportRequest locationRequest) {
        Location alternateLocationFormDb = getLocationFormDb(locationRequest.getNewDistrict(), locationRequest.getNewBlock(), locationRequest.getNewPanchayat());
        return alternateLocationFormDb != null && LocationStatus.isValidStatus(alternateLocationFormDb.getStatus().name());
    }

    private boolean validLocationPresentInCsv(LocationImportRequest locationRequest, List<LocationImportRequest> locationImportRequests) {
        for (LocationImportRequest locationRequestFromCsv : locationImportRequests) {
            if (!LocationStatus.isValidAlternateLocationStatus(locationRequestFromCsv.getStatus())) break;
            if (locationRequestFromCsv.matchesLocation(locationRequest.getNewDistrict(),
                    locationRequest.getNewBlock(), locationRequest.getNewPanchayat()))
                return true;
        }
        return false;
    }
}