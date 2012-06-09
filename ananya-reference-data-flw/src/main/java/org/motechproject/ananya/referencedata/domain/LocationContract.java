package org.motechproject.ananya.referencedata.domain;

import org.joda.time.DateTime;

public class LocationContract {

    private String district;
    private String block;
    private String panchayat;
    private DateTime lastModified;

    public LocationContract(String district, String block, String panchayat, DateTime lastModified) {
        this.district = district;
        this.block = block;
        this.panchayat = panchayat;
        this.lastModified = lastModified;
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

    public DateTime getLastModified() {
        return lastModified;
    }
}
