package org.motechproject.ananya.referencedata.csv.request;

import org.motechproject.ananya.referencedata.csv.utils.CSVRecordBuilder;
import org.motechproject.ananya.referencedata.flw.utils.PhoneNumber;
import org.motechproject.importer.annotation.ColumnName;

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

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    @ColumnName(name = "new_msisdn")
    public void setNewMsisdn(String newMsisdn) {
        this.newMsisdn = newMsisdn;
    }

    @ColumnName(name = "alternate_contact_number")
    public void setAlternateContactNumber(String alternateContactNumber) {
        this.alternateContactNumber = alternateContactNumber;
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
