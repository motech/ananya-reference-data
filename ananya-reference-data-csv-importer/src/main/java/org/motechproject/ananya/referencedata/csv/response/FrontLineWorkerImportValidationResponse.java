package org.motechproject.ananya.referencedata.csv.response;

import java.util.ArrayList;
import java.util.List;

public class FrontLineWorkerImportValidationResponse {
    private List<String> message = new ArrayList<String>();
    private boolean isValid = true;

    public void forBlankFieldsInLocation() {
        isValid = false;
        this.message.add("Blank district, block or panchayat");
    }

    public void forDuplicateLocation() {
        isValid = false;
        this.message.add("Location already present");
    }

    public void forInvalidMsisdn() {
        isValid = false;
        this.message.add("Invalid msisdn");
    }

    public void forInvalidAlternateContactNumber() {
        isValid = false;
        this.message.add("Invalid alternate contact number");
    }

    public void forInvalidId() {
        isValid = false;
        this.message.add("Invalid id");
    }

    public void forInvalidVerificationStatus() {
        isValid = false;
        this.message.add("Invalid verification status");
    }

    public void forInvalidLocation() {
        isValid = false;
        this.message.add("Invalid location");
    }

    public void forInvalidName() {
        isValid = false;
        this.message.add("Invalid name");
    }

    public boolean isValid() {
        return isValid;
    }

    public List<String> getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return message.toString();
    }

    public boolean isInValid() {
        return !isValid;
    }
}
