package org.motechproject.ananya.referencedata.flw.domain;

import java.util.Date;

public class FrontLineWorkerContract {

    private String name;
    private String msisdn;
    private String designation;
    private Date lastModified;
    private LocationContract location;
    private String flwGuid;

    public FrontLineWorkerContract(String msisdn, String name, String designation, Date lastModified, LocationContract location, String flwGuid) {
        this.name = name;
        this.msisdn = msisdn;
        this.designation = designation;
        this.lastModified = lastModified;
        this.location = location;
        this.flwGuid = flwGuid;
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

    public Date getLastModified() {
        return lastModified;
    }

    public String getFlwGuid() {
        return flwGuid;
    }
}
