package org.motechproject.ananya.referencedata.response;

public class ValidationResponse {
    private String message;
    private boolean isValid = true;

    public ValidationResponse forSuccessfulCreationOfLocation() {
        isValid = true;
        this.message = "Successfully created location";
        return this;
    }

    public ValidationResponse forBlankFieldsInLocation() {
        isValid = false;
        this.message = "Blank district, block or panchayat";
        return this;
    }

    public ValidationResponse forDuplicateLocation() {
        isValid = false;
        this.message = "Location already present";
        return this;
    }

    public ValidationResponse forInvalidMsisdn() {
        isValid = false;
        this.message = "Invalid msisdn";
        return this;
    }

    public ValidationResponse forInvalidDesignation() {
        isValid = false;
        this.message = "Invalid designation";
        return this;
    }

    public ValidationResponse forInvalidLocation() {
        isValid = false;
        this.message = "Invalid location";
        return this;
    }

    public boolean isValid() {
        return isValid;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return message;
    }
}
