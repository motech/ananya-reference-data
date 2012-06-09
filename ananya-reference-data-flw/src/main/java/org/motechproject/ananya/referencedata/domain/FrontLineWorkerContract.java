package org.motechproject.ananya.referencedata.domain;

import org.joda.time.DateTime;

public class FrontLineWorkerContract {

    private String name;
    private String msisdn;
    private String designation;
    private DateTime lastModified;
    private LocationContract location;

    public FrontLineWorkerContract(String msisdn, String name, String designation, DateTime lastModified, LocationContract location) {
        this.name = name;
        this.msisdn = msisdn;
        this.designation = designation;
        this.lastModified = lastModified;
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

    public DateTime getLastModified() {
        return lastModified;
    }
}
