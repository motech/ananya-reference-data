package org.motechproject.ananya.referencedata.csv.response;

import java.util.ArrayList;
import java.util.List;

public class MsisdnImportValidationResponse {
    private List<String> message = new ArrayList<>();
    private boolean isValid = true;

    public void forDuplicateMsisdnRecords() {
        invalidate("There are duplicate rows in CSV for MSISDN");
    }

    public void forDuplicateNewMsisdnRecords() {
        invalidate("There are duplicate rows in CSV for New MSISDN");
    }

    public void forRequestIntegrity() {
        invalidate("At least one of the updates, new msisdn or alternate contact number, should be present");
    }

    public void forInvalidMsisdn() {
        invalidate("MSISDN is not in a valid format");
    }

    public void forMissingMsisdn() {
        invalidate("MSISDN is not provided");
    }

    public void forInvalidNewMsisdn() {
        invalidate("New MSISDN is not in a valid format");
    }

    public void forInvalidAlternateContactNumber() {
        invalidate("Alternate contact number is not in a valid format");
    }

    public void forMissingFlw() {
        invalidate("Could not find an FLW record in database with provided MSISDN");
    }

    public void forDuplicateFlwsByMsisdn() {
        invalidate("Duplicate FLW records are present in database for provided MSISDN");
    }

    public void forInvalidVerificationStatus() {
        invalidate("Verification Status of FLW for provided MSISDN is INVALID");
    }

    public void forOtherVerificationStatus() {
        invalidate("Verification Status of FLW for provided MSISDN is OTHER");
    }

    public void forDuplicateFlwsByNewMsisdn() {
        invalidate("Duplicate FLW records present in database for provided New MSISDN");
    }

    public void forConflictingNewMsisdn() {
        invalidate("There is another record in CSV with MSISDN same as provided New MSISDN");
    }

    public void forConflictingMsisdn() {
        invalidate("There is another record in CSV with New MSISDN same as provided MSISDN");
    }

    public boolean isValid() {
        return isValid;
    }

    public List<String> getMessage() {
        return message;
    }

    private void invalidate(String message) {
        isValid = false;
        if (!this.message.contains(message))
            this.message.add(message);
    }

    @Override
    public String toString() {
        return message.toString();
    }
}
