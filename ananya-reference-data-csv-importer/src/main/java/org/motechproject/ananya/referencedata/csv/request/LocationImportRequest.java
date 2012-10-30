package org.motechproject.ananya.referencedata.csv.request;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.referencedata.flw.domain.LocationStatus;

public class LocationImportRequest {
    private String district;
    private String block;
    private String panchayat;
    private String newDistrict;
    private String newBlock;
    private String newPanchayat;
    private String status = LocationStatus.NOT_VERIFIED.name();

    public LocationImportRequest() {
    }

    public LocationImportRequest(String district, String block, String panchayat) {
        this.district = district;
        this.block = block;
        this.panchayat = panchayat;
    }

    public LocationImportRequest(String district, String block, String panchayat, String status) {
        this.district = district;
        this.block = block;
        this.panchayat = panchayat;
        this.status = status;
    }

    public LocationImportRequest(String district, String block, String panchayat, String status,
                                 String newDistrict, String newBlock, String newPanchayat) {
        this.district = district;
        this.block = block;
        this.panchayat = panchayat;
        this.newDistrict = newDistrict;
        this.newBlock = newBlock;
        this.newPanchayat = newPanchayat;
        this.status = status;
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
        return "\"" + district + "\"" + "," + "\"" + block + "\"" + "," +  "\"" + panchayat + "\"";
    }

    public boolean isForInvalidation() {
        return LocationStatus.isInvalidStatus(status);
    }

    public boolean hasAlternateLocation() {
        return StringUtils.isNotEmpty(newBlock)
                && StringUtils.isNotEmpty(newDistrict)
                && StringUtils.isNotEmpty(newPanchayat);
    }
}
