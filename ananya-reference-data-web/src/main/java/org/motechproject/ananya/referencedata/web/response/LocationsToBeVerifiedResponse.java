package org.motechproject.ananya.referencedata.web.response;

import org.motechproject.export.annotation.ExportValue;

public class LocationsToBeVerifiedResponse extends LocationResponse{

    private String status;

    public LocationsToBeVerifiedResponse(String state, String district, String block, String panchayat, String status) {
        super(state, district,block,panchayat);
        this.status = status;
    }

    @ExportValue(column="status", index = 4)
    public String getStatus() {
        return status;
    }

    @ExportValue(column="newState", index = 5)
    public String getNewStateHeader() {
        return null;
    }

    @ExportValue(column="newDistrict", index = 6)
    public String getNewDistrictHeader() {
        return null;
    }


    @ExportValue(column="newBlock", index = 7)
    public String getNewBlockHeader() {
        return null;
    }

    @ExportValue(column="newPanchayat", index = 8)
    public String getNewPanchayatHeader() {
        return null;
    }
}


