package org.motechproject.ananya.referencedata.request;

public class FLWRequest {
    private String msisdn;
    private String name;
    private String designation;
    private LocationRequest location;
    
    public FLWRequest(){
    }

    public FLWRequest(String msisdn, String name, String designation, LocationRequest location) {
        this.msisdn = msisdn;
        this.name = name;
        this.designation = designation;
        this.location = location;
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

    public LocationRequest getLocation() {
        return location;
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

    public void setLocation(LocationRequest location) {
        this.location = location;
    }
}
