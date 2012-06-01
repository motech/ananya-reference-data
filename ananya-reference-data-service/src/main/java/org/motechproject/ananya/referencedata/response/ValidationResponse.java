package org.motechproject.ananya.referencedata.response;

import java.util.ArrayList;
import java.util.List;

public class ValidationResponse {
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

    public void forInvalidDesignation() {
        isValid = false;
        this.message.add("Invalid designation");
    }

    public void forInvalidLocation() {
        isValid = false;
        this.message.add("Invalid location");
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
}
