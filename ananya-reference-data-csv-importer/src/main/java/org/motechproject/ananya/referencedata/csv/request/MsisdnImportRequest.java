package org.motechproject.ananya.referencedata.csv.request;

import org.motechproject.ananya.referencedata.csv.utils.CSVRecordBuilder;
import org.motechproject.ananya.referencedata.flw.utils.PhoneNumber;

import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.apache.commons.lang.StringUtils.isNotEmpty;

public class MsisdnImportRequest {
    private String msisdn;
    private String newMsisdn;
    private String alternateContactNumber;

    public MsisdnImportRequest() {
    }

    public MsisdnImportRequest(String msisdn, String newMsisdn, String alternateContactNumber) {
        this.msisdn = msisdn;
        this.newMsisdn = newMsisdn;
        this.alternateContactNumber = alternateContactNumber;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public String getNewMsisdn() {
        return newMsisdn;
    }

    public String getAlternateContactNumber() {
        return alternateContactNumber;
    }

    public Long msisdnAsLong() {
        return isNotEmpty(msisdn) ? PhoneNumber.formatPhoneNumber(msisdn) : null;
    }

    public Long newMsisdnAsLong() {
        return isNotEmpty(newMsisdn) ? PhoneNumber.formatPhoneNumber(newMsisdn) : null;
    }

    public boolean isChangeMsisdn() {
        return isNotBlank(newMsisdn);
    }

    public boolean isUpdateAlternateContactNumber() {
        return isNotBlank(alternateContactNumber);
    }

    public String toCSV() {
        return new CSVRecordBuilder()
                .appendColumn(msisdn)
                .appendColumn(newMsisdn)
                .appendColumn(alternateContactNumber)
                .toString();
    }
}
