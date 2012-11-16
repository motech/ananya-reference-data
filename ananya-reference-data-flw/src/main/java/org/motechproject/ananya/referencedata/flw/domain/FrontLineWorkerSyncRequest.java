package org.motechproject.ananya.referencedata.flw.domain;

import org.apache.commons.lang.builder.EqualsBuilder;

import java.util.Date;

public class FrontLineWorkerSyncRequest {

    private String name;
    private String msisdn;
    private String designation;
    private Date lastModified;
    private LocationContract location;
    private String flwId;

    public FrontLineWorkerSyncRequest(String msisdn, String name, String designation, Date lastModified, LocationContract location, String flwId, String verificationStatus) {
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

    public String getFlwId() {
        return flwId;
    }

    @Override
    public boolean equals(Object other) {
        return EqualsBuilder.reflectionEquals(this, other);
    }
}
