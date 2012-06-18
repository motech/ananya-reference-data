package org.motechproject.ananya.referencedata.csv.importer;

import org.motechproject.ananya.referencedata.domain.Location;
import org.motechproject.ananya.referencedata.domain.LocationList;
import org.motechproject.ananya.referencedata.request.FrontLineWorkerRequest;
import org.motechproject.ananya.referencedata.request.LocationRequest;
import org.motechproject.ananya.referencedata.response.FLWValidationResponse;
import org.motechproject.ananya.referencedata.service.FrontLineWorkerService;
import org.motechproject.ananya.referencedata.service.JsonHttpClient;
import org.motechproject.ananya.referencedata.service.LocationService;
import org.motechproject.ananya.referencedata.validators.FrontLineWorkerValidator;
import org.motechproject.importer.annotation.CSVImporter;
import org.motechproject.importer.annotation.Post;
import org.motechproject.importer.annotation.Validate;
import org.motechproject.importer.domain.Error;
import org.motechproject.importer.domain.ValidationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Component
@CSVImporter(entity = "FrontLineWorker", bean = FrontLineWorkerRequest.class)
public class FrontLineWorkerImporter {

    private FrontLineWorkerService frontLineWorkerService;
    private LocationService locationService;
    private Logger logger = LoggerFactory.getLogger(FrontLineWorkerImporter.class);
    private JsonHttpClient jsonHttpClient;
    private Properties clientServicesProperties;

    private final String KEY_FRONT_LINE_WORKER_BULK_IMPORT_URL = "front.line.worker.bulk.import.url";

    @Autowired
    public FrontLineWorkerImporter(FrontLineWorkerService frontLineWorkerService, LocationService locationService, JsonHttpClient jsonHttpClient, Properties clientServicesProperties) {
        this.frontLineWorkerService = frontLineWorkerService;
        this.locationService = locationService;
        this.jsonHttpClient = jsonHttpClient;
        this.clientServicesProperties = clientServicesProperties;
    }

    @Validate
    public ValidationResponse validate(List<Object> objects) {
        boolean isValid = true;
        int recordCounter = 0;
        LocationList locationList = new LocationList(locationService.getAll());
        List<Error> errors = new ArrayList<Error>();

        List<FrontLineWorkerRequest> frontLineWorkerRequests = convertToFLWRequest(objects);
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
    public void postData(List<Object> objects) throws IOException {
        logger.info("Started posting FLW data");
        List<FrontLineWorkerRequest> frontLineWorkerRequests = convertToFLWRequest(objects);
        JsonHttpClient.Response response = jsonHttpClient.post((String) clientServicesProperties.get(KEY_FRONT_LINE_WORKER_BULK_IMPORT_URL), frontLineWorkerRequests);
        checkForException(response);
        logger.info("Finished posting FLW data");
    }

    private void checkForException(JsonHttpClient.Response response) {
        if (HttpServletResponse.SC_OK != response.statusCode) {
            throw new IllegalStateException("Remote error: " + response.body);
        }
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
