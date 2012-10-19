package org.motechproject.ananya.referencedata.csv.importer;

import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.request.FrontLineWorkerCsvRequest;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;
import org.motechproject.ananya.referencedata.flw.response.FLWValidationResponse;
import org.motechproject.ananya.referencedata.flw.service.FrontLineWorkerCsvService;
import org.motechproject.ananya.referencedata.flw.service.LocationService;
import org.motechproject.ananya.referencedata.flw.validators.FrontLineWorkerCsvRequestValidator;
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
import java.util.List;

@Component
@CSVImporter(entity = "FrontLineWorker", bean = FrontLineWorkerCsvRequest.class)
public class FrontLineWorkerImporter {

    private FrontLineWorkerCsvService frontLineWorkerCsvService;
    private LocationService locationService;
    private Logger logger = LoggerFactory.getLogger(FrontLineWorkerImporter.class);

    @Autowired
    public FrontLineWorkerImporter(FrontLineWorkerCsvService frontLineWorkerCsvService, LocationService locationService) {
        this.frontLineWorkerCsvService = frontLineWorkerCsvService;
        this.locationService = locationService;
    }

    @Validate
    public ValidationResponse validate(List<FrontLineWorkerCsvRequest> frontLineWorkerCsvRequests) {
        boolean isValid = true;
        int recordCounter = 0;
        List<Error> errors = new ArrayList<Error>();

        FrontLineWorkerCsvRequestValidator frontLineWorkerValidator = new FrontLineWorkerCsvRequestValidator();
        addHeader(errors);
        logger.info("Started validating FLW csv records");
        for (FrontLineWorkerCsvRequest frontLineWorkerCsvRequest : frontLineWorkerCsvRequests) {
            Location location = getLocationFor(frontLineWorkerCsvRequest.getLocation());
            FLWValidationResponse response = frontLineWorkerValidator.validate(frontLineWorkerCsvRequest, location);
            if (response.isInValid()) {
                isValid = false;
            }
            logger.info("Validated FLW record number : " + recordCounter++ + "with validation status : " + isValid);
            errors.add(new Error(frontLineWorkerCsvRequest.toCSV() + "," + "\"" + response.getMessage() + "\""));
        }
        logger.info("Completed validating FLW csv records");
        return constructValidationResponse(isValid, errors);
    }

    @Post
    public void postData(List<FrontLineWorkerCsvRequest> frontLineWorkerCsvRequests) {
        logger.info("Started posting FLW data");
        frontLineWorkerCsvService.addAllWithoutValidations(frontLineWorkerCsvRequests);
        logger.info("Finished posting FLW data");
    }

    private ValidationResponse constructValidationResponse(boolean isValid, List<Error> errors) {
        ValidationResponse validationResponse = new ValidationResponse(isValid);
        for (Error error : errors)
            validationResponse.addError(error);
        return validationResponse;
    }

    private Location getLocationFor(LocationRequest locationRequest) {
        return locationService.getFor(locationRequest.getDistrict(), locationRequest.getBlock(), locationRequest.getPanchayat());
    }

    private void addHeader(List<Error> errors) {
        errors.add(new Error("msisdn,name,designation,district,block,panchayat,error"));
    }
}
