package org.motechproject.ananya.referencedata.csv.importer;

import org.motechproject.ananya.referencedata.domain.Location;
import org.motechproject.ananya.referencedata.domain.LocationList;
import org.motechproject.ananya.referencedata.request.FrontLineWorkerRequest;
import org.motechproject.ananya.referencedata.request.LocationRequest;
import org.motechproject.ananya.referencedata.response.FLWValidationResponse;
import org.motechproject.ananya.referencedata.service.FrontLineWorkerService;
import org.motechproject.ananya.referencedata.service.LocationService;
import org.motechproject.ananya.referencedata.validators.FrontLineWorkerValidator;
import org.motechproject.importer.annotation.CSVImporter;
import org.motechproject.importer.annotation.Post;
import org.motechproject.importer.annotation.Validate;
import org.motechproject.importer.domain.Error;
import org.motechproject.importer.domain.ValidationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@CSVImporter(entity = "FrontLineWorker", bean = FrontLineWorkerRequest.class)
public class FrontLineWorkerImporter {

    private FrontLineWorkerService frontLineWorkerService;
    private LocationService locationService;

    @Autowired
    public FrontLineWorkerImporter(FrontLineWorkerService frontLineWorkerService, LocationService locationService) {
        this.frontLineWorkerService = frontLineWorkerService;
        this.locationService = locationService;
    }

    @Validate
    public ValidationResponse validate(List<Object> objects) {
        boolean isValid = true;
        LocationList locationList = new LocationList(locationService.getAll());
        List<Error> errors = new ArrayList<Error>();

        List<FrontLineWorkerRequest> frontLineWorkerRequests = convertToFLWRequest(objects);
        FrontLineWorkerValidator frontLineWorkerValidator = new FrontLineWorkerValidator();
        addHeader(errors);
        for (FrontLineWorkerRequest frontLineWorkerRequest : frontLineWorkerRequests) {
            Location location = getLocationFor(frontLineWorkerRequest.getLocation(), locationList);
            FLWValidationResponse response = frontLineWorkerValidator.validateWithBulkValidation(frontLineWorkerRequest, location, frontLineWorkerRequests);
            if (response.isInValid()) {
                isValid = false;
            }
            errors.add(new Error(frontLineWorkerRequest.toCSV() + "," + response.getMessage()));
        }
        return constructValidationResponse(isValid, errors);
    }

    @Post
    public void postData(List<Object> objects) {
        List<FrontLineWorkerRequest> frontLineWorkerRequests = convertToFLWRequest(objects);
        frontLineWorkerService.addAllWithoutValidations(frontLineWorkerRequests);
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

    private List<FrontLineWorkerRequest> convertToFLWRequest(List<Object> objects) {
        List<FrontLineWorkerRequest> frontLineWorkerRequests = new ArrayList<FrontLineWorkerRequest>();
        for (Object object : objects) {
            frontLineWorkerRequests.add((FrontLineWorkerRequest) object);
        }
        return frontLineWorkerRequests;
    }

    private void addHeader(List<Error> errors) {
        errors.add(new Error("mssidn,name,desigantion,district,block,panchayat,error"));
    }
}
