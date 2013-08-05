package org.motechproject.ananya.referencedata.csv.validator;

import org.apache.commons.collections.CollectionUtils;
import org.motechproject.ananya.referencedata.csv.request.LocationImportCSVRequest;
import org.motechproject.ananya.referencedata.csv.response.LocationValidationResponse;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.LocationStatus;
import org.motechproject.ananya.referencedata.flw.repository.AllLocations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LocationImportValidator {

    private AllLocations allLocations;

    @Autowired
    public LocationImportValidator(AllLocations allLocations) {
        this.allLocations = allLocations;
    }

    public LocationValidationResponse validate(LocationImportCSVRequest csvRequest, List<LocationImportCSVRequest> csvRequests) {
        LocationValidationResponse validationResponse = new LocationValidationResponse();
        csvRequest.validate(validationResponse);
        if (validationResponse.isInValid()) {
            return validationResponse;
        }
        validateDuplicateEntries(csvRequest, csvRequests, validationResponse);
        validateExistenceInDb(csvRequest, validationResponse);
        validateInvalidLocation(csvRequest, csvRequests, validationResponse);
        return validationResponse;
    }

    private void validateDuplicateEntries(LocationImportCSVRequest locationCSVRequest, List<LocationImportCSVRequest> locationImportCSVRequests, LocationValidationResponse validationResponse) {
        if (CollectionUtils.cardinality(locationCSVRequest, locationImportCSVRequests) > 1)
            validationResponse.forDuplicateLocation();
    }

    private void validateExistenceInDb(LocationImportCSVRequest csvRequest, LocationValidationResponse validationResponse) {
        LocationStatus status = LocationStatus.from(csvRequest.getStatus());
        Location alreadyPresentLocation = getLocationFormDb(csvRequest.getState(), csvRequest.getDistrict(), csvRequest.getBlock(), csvRequest.getPanchayat());
        if (status == LocationStatus.NEW && alreadyPresentLocation != null)
            validationResponse.forLocationExisting();
        if (status != LocationStatus.NEW) {
            if (alreadyPresentLocation == null) {
                validationResponse.forLocationNotExisting();
                return;
            }
            if (!alreadyPresentLocation.getStatus().canTransitionTo(status))
                validationResponse.forCannotTransitionFromExistingState();
        }
    }

    private void validateInvalidLocation(LocationImportCSVRequest locationCSVRequest, List<LocationImportCSVRequest> locationImportCSVRequests, LocationValidationResponse validationResponse) {
        LocationStatus status = LocationStatus.from(locationCSVRequest.getStatus());
        if (status == LocationStatus.INVALID) {
            validateAlternateLocation(locationCSVRequest, locationImportCSVRequests, validationResponse);
            return;
        }
        if (locationCSVRequest.hasAlternateLocation()) {
            validationResponse.forNeedlessAlternateLocation();
        }
    }

    private void validateAlternateLocation(LocationImportCSVRequest csvRequest, List<LocationImportCSVRequest> csvRequests, LocationValidationResponse validationResponse) {
        if (!csvRequest.hasAlternateLocation()) {
            validationResponse.forBlankAlternateLocation();
            return;
        }
        LocationImportCSVRequest alternateLocationFromCSV = getIfPresentInCsv(csvRequest, csvRequests);
        LocationStatus statusOfAlternateLocation = alternateLocationFromCSV != null ? LocationStatus.from(alternateLocationFromCSV.getStatus()) : null;
        if (alternateLocationFromCSV != null && (statusOfAlternateLocation == null || !statusOfAlternateLocation.isValidStatusForAlternateLocation())) {
            validationResponse.forInvalidAlternateLocation();
        }
        if (alternateLocationFromCSV == null && !isAlternateLocationPresentInDbAsValid(csvRequest))
            validationResponse.forInvalidAlternateLocation();
    }

    private LocationImportCSVRequest getIfPresentInCsv(LocationImportCSVRequest locationCSVRequest, List<LocationImportCSVRequest> locationImportCSVRequests) {
        for (LocationImportCSVRequest locationCSVRequestFromCsv : locationImportCSVRequests) {
            if (locationCSVRequestFromCsv.matchesLocation(locationCSVRequest.getNewState(), locationCSVRequest.getNewDistrict(),
                    locationCSVRequest.getNewBlock(), locationCSVRequest.getNewPanchayat()))
                return locationCSVRequestFromCsv;
        }
        return null;
    }

    private boolean isAlternateLocationPresentInDbAsValid(LocationImportCSVRequest locationCSVRequest) {
        Location alternateLocationFormDb = getLocationFormDb(locationCSVRequest.getNewState(), locationCSVRequest.getNewDistrict(), locationCSVRequest.getNewBlock(), locationCSVRequest.getNewPanchayat());
        return alternateLocationFormDb != null && alternateLocationFormDb.isValidatedLocation();
    }

    private Location getLocationFormDb(String state, String district, String block, String panchayat) {
        return allLocations.getFor(state, district, block, panchayat);
    }
}