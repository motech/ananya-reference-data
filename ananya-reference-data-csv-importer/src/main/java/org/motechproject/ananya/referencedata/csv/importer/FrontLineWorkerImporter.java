package org.motechproject.ananya.referencedata.csv.importer;

import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.LocationList;
import org.motechproject.ananya.referencedata.flw.request.FrontLineWorkerRequest;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;
import org.motechproject.ananya.referencedata.flw.response.FLWValidationResponse;
import org.motechproject.ananya.referencedata.flw.service.FrontLineWorkerService;
import org.motechproject.ananya.referencedata.flw.service.LocationService;
import org.motechproject.ananya.referencedata.flw.validators.FrontLineWorkerValidator;
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
@CSVImporter(entity = "FrontLineWorker", bean = FrontLineWorkerRequest.class)
public class FrontLineWorkerImporter {

    private FrontLineWorkerService frontLineWorkerService;
    private LocationService locationService;
    private Logger logger = LoggerFactory.getLogger(FrontLineWorkerImporter.class);

    @Autowired
    public FrontLineWorkerImporter(FrontLineWorkerService frontLineWorkerService, LocationService locationService) {
        this.frontLineWorkerService = frontLineWorkerService;
        this.locationService = locationService;
    }

    @Validate
    public ValidationResponse validate(List<FrontLineWorkerRequest> frontLineWorkerRequests) {
        boolean isValid = true;
        int recordCounter = 0;
        LocationList locationList = new LocationList(locationService.getAll());
        List<Error> errors = new ArrayList<Error>();

        FrontLineWorkerValidator frontLineWorkerValidator = new FrontLineWorkerValidator();
        addHeader(errors);
        logger.info("Started validating FLW csv records");
        for (FrontLineWorkerRequest frontLineWorkerRequest : frontLineWorkerRequests) {
            Location location = getLocationFor(frontLineWorkerRequest.getLocation(), locationList);
            FLWValidationResponse response = frontLineWorkerValidator.validate(frontLineWorkerRequest, location);
            if (response.isInValid()) {
                isValid = false;
            }
            logger.info("Validated FLW record number : " + recordCounter++ + "with validation status : " + isValid);
            errors.add(new Error(frontLineWorkerRequest.toCSV() + "," + "\"" + response.getMessage() + "\""));
        }
        logger.info("Completed validating FLW csv records");
        return constructValidationResponse(isValid, errors);
    }

    @Post
    public void postData(List<FrontLineWorkerRequest> frontLineWorkerRequests) {
        logger.info("Started posting FLW data");
        frontLineWorkerService.addAllWithoutValidations(frontLineWorkerRequests);
        logger.info("Finished posting FLW data");
    }

    private ValidationResponse constructValidationResponse(boolean isValid, List<Error> errors) {
        ValidationResponse validationResponse = new ValidationResponse(isValid);
        for (Error error : errors)
            validationResponse.addError(error);
        return validationResponse;
    }

    private Location getLocationFor(LocationRequest locationRequest, LocationList locationList) {
        return locationList.findFor(locationRequest.getDistrict(), locationRequest.getBlock(), locationRequest.getPanchayat());
    }

    private void addHeader(List<Error> errors) {
        errors.add(new Error("mssidn,name,desigantion,district,block,panchayat,error"));
    }
}
