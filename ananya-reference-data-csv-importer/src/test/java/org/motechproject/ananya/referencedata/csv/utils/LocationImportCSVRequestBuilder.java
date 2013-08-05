package org.motechproject.ananya.referencedata.csv.utils;

import org.motechproject.ananya.referencedata.csv.request.LocationImportCSVRequest;

public class LocationImportCSVRequestBuilder {
    private String district;
    private String block;
    private String panchayat;
    private String state;
    private String newDistrict;
    private String newBlock;
    private String newPanchayat;
    private String newState;
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
        csvRequest.setState(state);
        csvRequest.setNewState(newState);
        return csvRequest;
    }

    public LocationImportCSVRequest buildWith(String state, String district, String block, String panchayat, String staus, String newState, String newDistrict, String newBlock, String newPanchayat) {
        return new LocationImportCSVRequestBuilder()
                .withDefaults().withBlock(block)
                .withState(state)
                .withDistrict(district).withPanchayat(panchayat)
                .withStatus(staus).withNewBlock(newBlock)
                .withNewDistrict(newDistrict).withNewPanchayat(newPanchayat)
                .withNewState(newState)
                .build();
    }

    public LocationImportCSVRequestBuilder withState(String state) {
        this.state = state;
        return this;
    }

    public LocationImportCSVRequestBuilder withNewState(String state) {
        this.newState = state;
        return this;
    }

    public LocationImportCSVRequest buildWith(String district, String block, String panchayat, String staus) {
        return buildWith(state, district, block, panchayat, staus, newState, null, null, null);
    }
}
