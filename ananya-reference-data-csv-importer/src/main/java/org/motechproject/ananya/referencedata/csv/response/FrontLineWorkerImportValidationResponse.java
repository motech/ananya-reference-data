package org.motechproject.ananya.referencedata.csv.response;

import java.util.ArrayList;
import java.util.List;

public class FrontLineWorkerImportValidationResponse {
    private List<String> message = new ArrayList<String>();
    private boolean isValid = true;

    public void forBlankFieldsInLocation() {
        isValid = false;
        addMessage("Blank district, block or panchayat");
    }

    public void forDuplicateLocation() {
        isValid = false;
        addMessage("Location already present");
    }

    public void forInvalidMsisdn() {
        isValid = false;
        addMessage("Invalid msisdn");
    }

    public void forNonMatchingMsisdn() {
        isValid = false;
        addMessage("Msisdn do not match");
    }


    public void forMissingFLW() {
        isValid = false;
        addMessage("FLW not found");
    }

    public void forInvalidAlternateContactNumber() {
        isValid = false;
        addMessage("Invalid alternate contact number");
    }

    public void forInvalidId() {
        isValid = false;
        addMessage("Invalid id");
    }

    public void forInvalidVerificationStatus() {
        isValid = false;
        addMessage("Invalid verification status");
    }

    public void forInvalidBlankVerificationStatus() {
        isValid = false;
        addMessage("Cannot update existing verification status to blank");
    }

    public void forInvalidLocation() {
        isValid = false;
        addMessage("Invalid location");
    }

    public void forInvalidName() {
        isValid = false;
        addMessage("Invalid name");
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

    private void addMessage(String message) {
        if(!this.message.contains(message))
            this.message.add(message);
    }
}
