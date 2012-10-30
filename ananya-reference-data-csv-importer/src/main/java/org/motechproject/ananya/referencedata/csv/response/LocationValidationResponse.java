package org.motechproject.ananya.referencedata.csv.response;

import java.util.ArrayList;
import java.util.List;

public class LocationValidationResponse {
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

    public void forInvalidLocation() {
        isValid = false;
        this.message.add("Invalid location");
    }

    public void forInvalidStatus() {
        isValid = false;
        this.message.add("Invalid status");
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
