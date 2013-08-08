package org.motechproject.ananya.referencedata.web.response;

import org.motechproject.export.annotation.ExportValue;

public class LocationResponse {
    private String state;
    private String district;
    private String block;
    private String panchayat;

    public LocationResponse(String state, String district, String block, String panchayat) {
        this.state = state;
        this.district = district;
        this.block = block;
        this.panchayat = panchayat;
    }
    @ExportValue(column="state", index = 0)
    public String getState() {
        return state;
    }

    @ExportValue(column="district", index = 1)
    public String getDistrict() {
        return district;
    }

    @ExportValue(column="block", index = 2)
    public String getBlock() {
        return block;
    }

    @ExportValue(column="panchayat", index = 3)
    public String getPanchayat() {
        return panchayat;
    }
}



