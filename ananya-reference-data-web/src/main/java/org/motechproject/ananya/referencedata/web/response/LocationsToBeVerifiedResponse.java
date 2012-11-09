package org.motechproject.ananya.referencedata.web.response;

import org.motechproject.export.annotation.ExportValue;

public class LocationsToBeVerifiedResponse extends LocationResponse{

    private String status;

    public LocationsToBeVerifiedResponse(String district, String block, String panchayat,String status) {
        super(district,block,panchayat);
        this.status = status;
    }

    @ExportValue(column="status", index = 3)
    public String getStatus() {
        return status;
    }
}


