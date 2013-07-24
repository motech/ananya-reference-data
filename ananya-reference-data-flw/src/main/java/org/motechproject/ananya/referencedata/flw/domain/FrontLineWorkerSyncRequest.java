package org.motechproject.ananya.referencedata.flw.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.joda.time.DateTime;

import java.io.Serializable;

public class FrontLineWorkerSyncRequest implements Serializable {
    private String alternateContactNumber;
    private String name;
    private String msisdn;
    private String designation;
    private DateTime lastModified;
    private LocationContract location;
    private String flwId;
    private String verificationStatus;

    public FrontLineWorkerSyncRequest(String msisdn, Long alternateContactNumber, String name, String designation, DateTime lastModified, LocationContract location, String flwId, String verificationStatus) {
        this.msisdn = msisdn;
        this.alternateContactNumber = alternateContactNumber == null ? null : alternateContactNumber.toString();
        this.name = name;
        this.designation = designation;
        this.lastModified = lastModified;
        this.location = location;
        this.flwId = flwId;
        this.verificationStatus = verificationStatus;
    }

    public String getName() {
        return name;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public String getAlternateContactNumber() {
        return alternateContactNumber;
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

    public String getFlwId() {
        return flwId;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    @Override
    public boolean equals(Object other) {
        return EqualsBuilder.reflectionEquals(this, other);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
