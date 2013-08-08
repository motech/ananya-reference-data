package org.motechproject.ananya.referencedata.web.response;


import org.motechproject.export.annotation.ExportValue;

public class LocationResponseWithoutState extends LocationResponse {

    public LocationResponseWithoutState(String district, String block, String panchayat) {
        super(null, district, block, panchayat);
    }

    public String getState() {
        throw new UnsupportedOperationException("LocationResponseWithoutState does not support state");
    }

    @ExportValue(column="district", index = 0)
    public String getDistrict() {
        return super.getDistrict();
    }

    @ExportValue(column="block", index = 1)
    public String getBlock() {
        return super.getBlock();
    }

    @ExportValue(column="panchayat", index = 2)
    public String getPanchayat() {
        return super.getPanchayat();
    }
}
