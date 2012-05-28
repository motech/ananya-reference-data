package org.motechproject.ananya.referencedata.response;

public class ValidationResponse {
    private String message;
    private boolean isValid;

    public ValidationResponse forSuccessfulCreation() {
        isValid = true;
        this.message = "Successfully created location";
        return this;
    }

    public ValidationResponse forBlankFields() {
        isValid = false;
        this.message = "Blank district, block or panchayat";
        return this;
    }

    public ValidationResponse forDuplicate() {
        isValid = false;
        this.message = "Location already present";
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
