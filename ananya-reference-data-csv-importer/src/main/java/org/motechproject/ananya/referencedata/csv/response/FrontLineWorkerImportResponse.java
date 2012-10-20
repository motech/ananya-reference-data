package org.motechproject.ananya.referencedata.csv.response;

import org.apache.commons.lang.StringUtils;

public class FrontLineWorkerImportResponse {
    private String message;

    public String getMessage() {
        return message;
    }

    public FrontLineWorkerImportResponse withValidationResponse(FrontLineWorkerImportValidationResponse FrontLineWorkerImportValidationResponse) {
        this.message = StringUtils.join(FrontLineWorkerImportValidationResponse.getMessage(), ',');
        return this;
    }

    public FrontLineWorkerImportResponse withCreatedOrUpdated() {
        this.message = "FLW created/updated successfully";
        return this;
    }
}
