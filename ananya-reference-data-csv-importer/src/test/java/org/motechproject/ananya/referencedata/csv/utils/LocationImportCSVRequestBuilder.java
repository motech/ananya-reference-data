package org.motechproject.ananya.referencedata.csv.utils;

import org.motechproject.ananya.referencedata.csv.request.LocationImportCSVRequest;

public class LocationImportCSVRequestBuilder {
    private String district;
    private String block;
    private String panchayat;
    private String newDistrict;
    private String newBlock;
    private String newPanchayat;
    private String status;

    public LocationImportCSVRequestBuilder withDistrict(String district) {
        this.district = district;
        return this;
    }

    public LocationImportCSVRequestBuilder withBlock(String block) {
        this.block = block;
        return this;
    }

    public LocationImportCSVRequestBuilder withPanchayat(String panchayat) {
        this.panchayat = panchayat;
        return this;
    }

    public LocationImportCSVRequestBuilder withNewDistrict(String newDistrict) {
        this.newDistrict = newDistrict;
        return this;
    }

    public LocationImportCSVRequestBuilder withNewBlock(String newBlock) {
        this.newBlock = newBlock;
        return this;
    }

    public LocationImportCSVRequestBuilder withNewPanchayat(String newPanchayat) {
        this.newPanchayat = newPanchayat;
        return this;
    }

    public LocationImportCSVRequestBuilder withStatus(String status) {
        this.status = status;
        return this;
    }

    public LocationImportCSVRequestBuilder withDefaults() {
        return this;
    }

    public LocationImportCSVRequest build() {
        LocationImportCSVRequest csvRequest = new LocationImportCSVRequest();
        csvRequest.setBlock(block);
        csvRequest.setDistrict(district);
        csvRequest.setPanchayat(panchayat);
        csvRequest.setNewDistrict(newDistrict);
        csvRequest.setNewBlock(newBlock);
        csvRequest.setNewPanchayat(newPanchayat);
        csvRequest.setStatus(status);
        return csvRequest;
    }

    public LocationImportCSVRequest buildWith(String district, String block, String panchayat, String staus, String newDistrict, String newBlock, String newPanchayat) {
        return new LocationImportCSVRequestBuilder()
                .withDefaults().withBlock(block)
                .withDistrict(district).withPanchayat(panchayat)
                .withStatus(staus).withNewBlock(newBlock)
                .withNewDistrict(newDistrict).withNewPanchayat(newPanchayat)
                .build();
    }

    public LocationImportCSVRequest buildWith(String district, String block, String panchayat, String staus) {
        return buildWith(district, block, panchayat, staus, null, null, null);
    }
}
