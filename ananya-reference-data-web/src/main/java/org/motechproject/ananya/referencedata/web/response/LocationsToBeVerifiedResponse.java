package org.motechproject.ananya.referencedata.web.response;

import org.motechproject.export.annotation.ExportValue;

public class LocationsToBeVerifiedResponse extends LocationResponse{

    private String status;

    public LocationsToBeVerifiedResponse(String district, String block, String panchayat, String status) {
        super(district,block,panchayat);
        this.status = status;
    }

    @ExportValue(column="status", index = 3)
    public String getStatus() {
        return status;
    }

    @ExportValue(column="newDistrict", index = 4)
    public String getNewDistrictHeader() {
        return null;
    }


    @ExportValue(column="newBlock", index = 5)
    public String getNewBlockHeader() {
        return null;
    }

    @ExportValue(column="newPanchayat", index = 6)
    public String getNewPanchayatHeader() {
        return null;
    }
}


