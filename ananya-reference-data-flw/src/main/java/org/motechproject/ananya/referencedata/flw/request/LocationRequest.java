package org.motechproject.ananya.referencedata.flw.request;

import java.io.Serializable;

public class LocationRequest implements Serializable {
    private String district;
    private String block;
    private String panchayat;

    public LocationRequest() {
    }

    public LocationRequest(String district, String block, String panchayat) {
        this.district = district;
        this.block = block;
        this.panchayat = panchayat;
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

    public void setDistrict(String district) {
        this.district = district;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public void setPanchayat(String panchayat) {
        this.panchayat = panchayat;
    }

    public String toCSV() {
        return "\"" + district + "\"" + "," + "\"" + block + "\"" + "," +  "\"" + panchayat + "\"";
    }
}
