package org.motechproject.ananya.referencedata.csv.response;

import java.util.ArrayList;
import java.util.List;

public class MsisdnImportValidationResponse {
    private List<String> message = new ArrayList<>();
    private boolean isValid = true;

    public void forDuplicateRecords() {
        invalidate("Duplicate records with same msisdn/new msisdn found");
    }

    public void forRequestIntegrity() {
        invalidate("Either of new msisdn or alternate contact number or both should be present");
    }

    public void forInvalidMsisdn() {
        invalidate("Invalid msisdn");
    }

    public void forMissingMsisdn() {
        invalidate("Missing msisdn");
    }

    public void forInvalidNewMsisdn() {
        invalidate("Invalid new msisdn");
    }

    public void forInvalidAlternateContactNumber() {
        invalidate("Invalid alternate contact number");
    }

    public void forMissingFlw() {
        invalidate("No FLW present in DB with msisdn");
    }

    public void forDuplicateFlwsByMsisdn() {
        invalidate("Duplicate FLWs present with same msisdn");
    }

    public void forInvalidVerificationStatus() {
        invalidate("Verification Status of FLW is INVALID or OTHER");
    }

    public void forDuplicateFlwsByNewMsisdn() {
        invalidate("Duplicate FLWs present with same new msisdn");
    }

    public boolean isValid() {
        return isValid;
    }

    public List<String> getMessage() {
        return message;
    }

    private void invalidate(String message) {
        isValid = false;
        if(!this.message.contains(message))
            this.message.add(message);
    }

    @Override
    public String toString() {
        return message.toString();
    }
}
