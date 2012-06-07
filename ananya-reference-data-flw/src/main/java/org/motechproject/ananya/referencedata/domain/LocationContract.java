package org.motechproject.ananya.referencedata.domain;

public class LocationContract {

    private String district;
    private String block;
    private String panchayat;

    public LocationContract(String district, String block, String panchayat) {
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
}
