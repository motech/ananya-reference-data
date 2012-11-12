package org.motechproject.ananya.referencedata.csv.importer;

import org.motechproject.ananya.referencedata.csv.request.LocationImportCSVRequest;
import org.motechproject.ananya.referencedata.csv.response.LocationValidationResponse;
import org.motechproject.ananya.referencedata.csv.service.LocationImportService;
import org.motechproject.ananya.referencedata.csv.utils.LocationComparator;
import org.motechproject.ananya.referencedata.csv.validator.LocationImportValidator;
import org.motechproject.importer.annotation.CSVImporter;
import org.motechproject.importer.annotation.Post;
import org.motechproject.importer.annotation.Validate;
import org.motechproject.importer.domain.Error;
import org.motechproject.importer.domain.ValidationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@CSVImporter(entity = "Location", bean = LocationImportCSVRequest.class)
@Component
public class LocationImporter {

    private LocationImportService locationImportService;
    private LocationImportValidator locationImportValidator;
    private Logger logger = LoggerFactory.getLogger(LocationImporter.class);

    @Autowired
    public LocationImporter(LocationImportService locationImportService, LocationImportValidator locationImportValidator) {
        this.locationImportService = locationImportService;
        this.locationImportValidator = locationImportValidator;
    }

    @Validate
    public ValidationResponse validate(List<Object> objects) {
        boolean isValid = true;
        int recordCounter = 1;
        List<Error> errors = new ArrayList<Error>();

        List<LocationImportCSVRequest> locationImportCSVRequests = convertToLocationRequest(objects);

        addHeader(errors);
        logger.info("Started validating location csv records");
        for (LocationImportCSVRequest locationCSVRequest : locationImportCSVRequests) {
            LocationValidationResponse locationValidationResponse = locationImportValidator.validate(locationCSVRequest, locationImportCSVRequests);
            if (locationValidationResponse.isInValid()) {
                isValid = false;
            }
            logger.info("Validated location record number : " + recordCounter++ + " with validation status : " + isValid);
            errors.add(new Error(locationCSVRequest.toCSV() + "," + "\"" + locationValidationResponse.getMessage() + "\""));
        }
        logger.info("Completed validating location csv records");
        return constructValidationResponse(isValid, errors);
    }

    @Post
    public void postData(List<Object> objects) {
        logger.info("Started posting location data");
        List<LocationImportCSVRequest> locationImportCSVRequests = convertToLocationRequest(objects);
        Collections.sort(locationImportCSVRequests, new LocationComparator());
        locationImportService.addAllWithoutValidations(locationImportCSVRequests);
        logger.info("Finished posting location data");
    }

    private ValidationResponse constructValidationResponse(boolean isValid, List<Error> errors) {
        ValidationResponse validationResponse = new ValidationResponse(isValid);
        for (Error error : errors)
            validationResponse.addError(error);
        return validationResponse;
    }

    private List<LocationImportCSVRequest> convertToLocationRequest(List<Object> objects) {
        List<LocationImportCSVRequest> locationCSVRequests = new ArrayList<>();
        for (Object object : objects) {
            locationCSVRequests.add((LocationImportCSVRequest) object);
        }

        return locationCSVRequests;
    }

    private boolean addHeader(List<Error> errors) {
        return errors.add(new Error("district,block,panchayat,error"));
    }
}
