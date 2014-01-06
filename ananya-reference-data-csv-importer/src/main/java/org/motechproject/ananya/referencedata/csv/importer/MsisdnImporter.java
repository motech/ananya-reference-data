package org.motechproject.ananya.referencedata.csv.importer;

import org.apache.commons.collections.CollectionUtils;
import org.motechproject.ananya.referencedata.csv.request.MsisdnImportRequest;
import org.motechproject.ananya.referencedata.csv.response.MsisdnImportValidationResponse;
import org.motechproject.ananya.referencedata.csv.service.MsisdnImportService;
import org.motechproject.ananya.referencedata.csv.utils.CSVRecordBuilder;
import org.motechproject.ananya.referencedata.csv.validator.MsisdnImportRequestValidator;
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
@CSVImporter(entity = "Msisdn", bean = MsisdnImportRequest.class)
public class MsisdnImporter {

    private static Logger logger = LoggerFactory.getLogger(MsisdnImporter.class);

    private MsisdnImportRequestValidator msisdnImportRequestValidator;
    private MsisdnImportService msisdnImportService;

    @Autowired
    public MsisdnImporter(MsisdnImportRequestValidator msisdnImportRequestValidator, MsisdnImportService msisdnImportService) {
        this.msisdnImportRequestValidator = msisdnImportRequestValidator;
        this.msisdnImportService = msisdnImportService;
    }

    @Validate
    public ValidationResponse validate(List<MsisdnImportRequest> msisdnImportRequests) {
        boolean isValid = true;
        int recordCounter = 0;
        List<Error> errors = new ArrayList<>();
        List<MsisdnImportRequest> invalidRequests = new ArrayList<>();

        logger.info("Started validating MSISDN csv records");
        for (MsisdnImportRequest msisdnImportRequest : msisdnImportRequests) {
            MsisdnImportValidationResponse validationResponse = msisdnImportRequestValidator.validate(msisdnImportRequests, msisdnImportRequest);

            logger.info("Validated MSISDN record number : " + recordCounter++ + "with validation status : " + validationResponse.isValid());
            if (validationResponse.isValid()) {
                continue;
            }

            isValid = false;
            invalidRequests.add(msisdnImportRequest);
            addError(errors, msisdnImportRequest, validationResponse);
        }
        logger.info("Completed validating MSISDN csv records");
        return constructValidationResponse(isValid, errors, invalidRequests);
    }

    @Post
    public void postData(List<MsisdnImportRequest> msisdnImportRequests) {
        logger.info("Started updating MSISDN data");
        msisdnImportService.updateFLWContactDetailsWithoutValidations(msisdnImportRequests);
        logger.info("Finished updating MSISDN data");
    }

    private ValidationResponse constructValidationResponse(boolean isValid, List<Error> errors, List<MsisdnImportRequest> invalidRequests) {
        ValidationResponse validationResponse = new ValidationResponse(isValid);
        validationResponse.addErrors(errors);
        for (MsisdnImportRequest invalidRequest : invalidRequests) {
            validationResponse.addInvalidRecord(invalidRequest);
        }

        return validationResponse;
    }

    private void addError(List<Error> errors, MsisdnImportRequest msisdnImportRequest, MsisdnImportValidationResponse validationResponse) {
        addHeader(errors);
        String errorMessage = new CSVRecordBuilder(msisdnImportRequest.toCSV(), true)
                .appendColumn(validationResponse.getMessage().toString())
                .toString();
        errors.add(new Error(errorMessage));
    }

    private void addHeader(List<Error> errors) {
        if (CollectionUtils.isNotEmpty(errors))
            return;
        String header = new CSVRecordBuilder(false)
                .appendColumn("msisdn", "new_msisdn", "alternate_contact_number", "error")
                .toString();
        errors.add(new Error(header));
    }
}
