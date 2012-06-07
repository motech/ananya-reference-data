package org.motechproject.ananya.referencedata.domain;

public class FrontLineWorkerContract {

    private String name;
    private String msisdn;
    private String designation;
    private LocationContract location;

    public FrontLineWorkerContract(String msisdn, String name, String designation, LocationContract location) {
        this.name = name;
        this.msisdn = msisdn;
        this.designation = designation;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public String getDesignation() {
        return designation;
    }

    public LocationContract getLocation() {
        return location;
    }
}
