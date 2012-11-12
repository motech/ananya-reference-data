package org.motechproject.ananya.referencedata.web.response;

import org.motechproject.export.annotation.ExportValue;

public class LocationsWithoutStatusResponse {
    private String district;
    private String block;
    private String panchayat;

    public LocationsWithoutStatusResponse(String district, String block, String panchayat) {
        this.district = district;
        this.block = block;
        this.panchayat = panchayat;
    }

    @ExportValue(column="district", index = 0)
    public String getDistrict() {
        return district;
    }

    @ExportValue(column="block", index = 1)
    public String getBlock() {
        return block;
    }

    @ExportValue(column="panchayat", index = 2)
    public String getPanchayat() {
        return panchayat;
    }
}


