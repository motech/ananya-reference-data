package org.motechproject.ananya.referencedata.request;

public class FLWRequest {
    private String msisdn;
    private String name;
    private String designation;
    private LocationRequest locationRequest;
    
    public FLWRequest(){
    }

    public FLWRequest(String msisdn, String name, String designation, LocationRequest locationRequest) {
        this.msisdn = msisdn;
        this.name = name;
        this.designation = designation;
        this.locationRequest = locationRequest;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public String getName() {
        return name;
    }

    public String getDesignation() {
        return designation;
    }

    public LocationRequest getLocationRequest() {
        return locationRequest;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public void setLocationRequest(LocationRequest locationRequest) {
        this.locationRequest = locationRequest;
    }
}
