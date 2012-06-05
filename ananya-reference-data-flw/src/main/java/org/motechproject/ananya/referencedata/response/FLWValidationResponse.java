package org.motechproject.ananya.referencedata.response;

import java.util.ArrayList;
import java.util.List;

public class FLWValidationResponse {
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

    public void forDuplicates() {
        isValid = false;
        this.message.add("Found duplicate FLW with the same MSISDN");
    }

    public boolean isInValid() {
        return !isValid;
    }
}