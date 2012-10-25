package org.motechproject.ananya.referencedata.flw.domain;

import java.util.Date;
import java.util.UUID;

public class FrontLineWorkerContract {

    private String name;
    private String msisdn;
    private String designation;
    private Date lastModified;
    private LocationContract location;
    private UUID flwId;

    public FrontLineWorkerContract(String msisdn, String name, String designation, Date lastModified, LocationContract location, UUID flwId) {
        this.name = name;
        this.msisdn = msisdn;
        this.designation = designation;
        this.lastModified = lastModified;
        this.location = location;
        this.flwId = flwId;
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

    public UUID getFlwId() {
        return flwId;
    }
}
