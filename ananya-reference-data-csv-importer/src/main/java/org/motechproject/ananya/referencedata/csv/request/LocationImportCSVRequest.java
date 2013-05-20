package org.motechproject.ananya.referencedata.csv.request;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.motechproject.ananya.referencedata.csv.response.LocationValidationResponse;
import org.motechproject.ananya.referencedata.csv.utils.CSVRecordBuilder;
import org.motechproject.ananya.referencedata.flw.domain.LocationStatus;

public class LocationImportCSVRequest {
    private String state;
    private String district;
    private String block;
    private String panchayat;
    private String newState;
    private String newDistrict;
    private String newBlock;
    private String newPanchayat;
    private String status;

    public LocationImportCSVRequest() {
    }

    public String getNewState() {
        return newState;
    }

    public void setNewState(String newState) {
        this.newState = newState;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDistrict() {
        return district;
    }

    public String getBlock() {
        return block;
    }

    public String getPanchayat() {
        return panchayat;
    }

    public String getStatus() {
        return status;
    }

    public LocationStatus getStatusEnum() {
        return LocationStatus.from(status);
    }

    public String getNewDistrict() {
        return newDistrict;
    }

    public String getNewBlock() {
        return newBlock;
    }

    public String getNewPanchayat() {
        return newPanchayat;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public void setPanchayat(String panchayat) {
        this.panchayat = panchayat;
    }

    public void setNewDistrict(String newDistrict) {
        this.newDistrict = newDistrict;
    }

    public void setNewBlock(String newBlock) {
        this.newBlock = newBlock;
    }

    public void setNewPanchayat(String newPanchayat) {
        this.newPanchayat = newPanchayat;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String toCSV() {
        CSVRecordBuilder builder = new CSVRecordBuilder();
        builder.appendColumn(district)
                .appendColumn(block)
                .appendColumn(panchayat)
                .appendColumn(status)
                .appendColumn(newDistrict)
                .appendColumn(newBlock)
                .appendColumn(newPanchayat);
        return builder.toString();
    }

    public boolean hasAlternateLocation() {
        return StringUtils.isNotEmpty(newBlock)
                && StringUtils.isNotEmpty(newDistrict)
                && StringUtils.isNotEmpty(newPanchayat);
    }

    public boolean matchesLocation(String district, String block, String panchayat) {
        return this.district.equals(district) && this.block.equals(block) && this.panchayat.equals(panchayat);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocationImportCSVRequest that = (LocationImportCSVRequest) o;

        return new EqualsBuilder()
                .append(toUpperCase(this.district), toUpperCase(that.district))
                .append(toUpperCase(this.block), toUpperCase(that.block))
                .append(toUpperCase(this.panchayat), toUpperCase(that.panchayat))
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(toUpperCase(this.district))
                .append(toUpperCase(this.block))
                .append(toUpperCase(this.panchayat))
                .toHashCode();
    }

    private String toUpperCase(String value) {
        return value != null ? value.toUpperCase() : null;
    }

    public void validate(LocationValidationResponse validationResponse) {
        validateLocationFields(validationResponse);
        validateStatus(validationResponse);
    }

    private void validateLocationFields(LocationValidationResponse validationResponse) {
        if(StringUtils.isEmpty(district)
                || StringUtils.isEmpty(block)
                || StringUtils.isEmpty(panchayat))
            validationResponse.forBlankFieldsInLocation();
    }

    private void validateStatus(LocationValidationResponse validationResponse) {
        if (!LocationStatus.isValid(status) || !LocationStatus.from(status).isValidCsvStatus())
            validationResponse.forInvalidStatus();
    }

    public String getHeaderRowForErrors() {
        CSVRecordBuilder builder = new CSVRecordBuilder(false);
        builder.appendColumn("district", "block", "panchayat", "status", "newDistrict", "newBlock", "newPanchayat", "error");
        return builder.toString();
    }
}