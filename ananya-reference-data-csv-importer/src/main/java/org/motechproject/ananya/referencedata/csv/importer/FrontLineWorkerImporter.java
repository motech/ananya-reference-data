package org.motechproject.ananya.referencedata.csv.importer;

import org.motechproject.ananya.referencedata.csv.request.FrontLineWorkerImportRequest;
import org.motechproject.ananya.referencedata.csv.request.LocationImportCSVRequest;
import org.motechproject.ananya.referencedata.csv.response.FrontLineWorkerImportValidationResponse;
import org.motechproject.ananya.referencedata.csv.service.FrontLineWorkerImportService;
import org.motechproject.ananya.referencedata.csv.service.LocationImportService;
import org.motechproject.ananya.referencedata.csv.utils.LocationComparator;
import org.motechproject.ananya.referencedata.csv.validator.FrontLineWorkerImportRequestValidator;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;
import org.motechproject.importer.annotation.CSVImporter;
import org.motechproject.importer.annotation.Post;
import org.motechproject.importer.annotation.Sync;
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

@Component
@CSVImporter(entity = "FrontLineWorker", bean = FrontLineWorkerImportRequest.class)
public class FrontLineWorkerImporter {

    private FrontLineWorkerImportService frontLineWorkerImportService;
    private LocationImportService locationImportService;
    private Logger logger = LoggerFactory.getLogger(FrontLineWorkerImporter.class);
    private FrontLineWorkerImportRequestValidator frontLineWorkerValidator;

    @Autowired
    public FrontLineWorkerImporter(FrontLineWorkerImportService frontLineWorkerImportService, LocationImportService locationImportService
            , FrontLineWorkerImportRequestValidator frontLineWorkerValidator) {
        this.frontLineWorkerImportService = frontLineWorkerImportService;
        this.locationImportService = locationImportService;
        this.frontLineWorkerValidator = frontLineWorkerValidator;
    }

    @Validate
    public ValidationResponse validate(List<FrontLineWorkerImportRequest> frontLineWorkerImportRequests) {
        boolean isValid = true;
        int recordCounter = 0;
        List<Error> errors = new ArrayList<Error>();

        addHeader(errors);
        logger.info("Started validating FLW csv records");
        for (FrontLineWorkerImportRequest frontLineWorkerImportRequest : frontLineWorkerImportRequests) {
            Location location = getLocationFor(frontLineWorkerImportRequest.getLocation());
            FrontLineWorkerImportValidationResponse responseImport = frontLineWorkerValidator.validate(frontLineWorkerImportRequests, frontLineWorkerImportRequest, location);
            if (responseImport.isInValid()) {
                isValid = false;
            }
            logger.info("Validated FLW record number : " + recordCounter++ + "with validation status : " + isValid);
            errors.add(new Error(frontLineWorkerImportRequest.toCSV() + "," + "\"" + responseImport.getMessage() + "\""));
        }
        logger.info("Completed validating FLW csv records");
        locationImportService.invalidateCache();
        return constructValidationResponse(isValid, errors);
    }

    @Post
    public String postData(List<FrontLineWorkerImportRequest> frontLineWorkerImportRequests) {
        logger.info("Started posting FLW data");
         String uuid_persist = frontLineWorkerImportService.addAllWithoutValidations(frontLineWorkerImportRequests);
        logger.info("Finished posting FLW data");
        return uuid_persist;
    }
    
    @Sync
    public String syncData(List<String> uuids) {
    	 return frontLineWorkerImportService.syncflw(uuids.get(0));
    }

    private ValidationResponse constructValidationResponse(boolean isValid, List<Error> errors) {
        ValidationResponse validationResponse = new ValidationResponse(isValid);
        for (Error error : errors)
            validationResponse.addError(error);
        return validationResponse;
    }

    private Location getLocationFor(LocationRequest locationRequest) {
        return locationImportService.getFor(locationRequest.getState(), locationRequest.getDistrict(), locationRequest.getBlock(), locationRequest.getPanchayat());
    }

    private void addHeader(List<Error> errors) {
        errors.add(new Error("id,msisdn,alternate_contact_number,name,designation,verification_status,state,district,block,panchayat,error"));
    }
}
