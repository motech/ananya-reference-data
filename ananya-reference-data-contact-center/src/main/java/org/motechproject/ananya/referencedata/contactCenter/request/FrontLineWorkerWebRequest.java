package org.motechproject.ananya.referencedata.contactCenter.request;

import org.codehaus.jackson.annotate.JsonProperty;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "flw")
public class FrontLineWorkerWebRequest {
    @JsonProperty(value = "id")
    @XmlElement(name = "id")
    private String flwId;

    @XmlElement
    @JsonProperty
    protected String verificationStatus;

    @XmlElement
    @JsonProperty
    private String reason;

    @XmlElement
    @JsonProperty
    private String name;

    @XmlElement
    @JsonProperty
    private String designation;

    @XmlElement
    @JsonProperty
    private LocationRequest location;

    public FrontLineWorkerWebRequest() {
    }

    public FrontLineWorkerWebRequest(String flwId, String verificationStatus, String reason) {
        this.flwId = flwId;
        this.verificationStatus = verificationStatus;
        this.reason = reason;
    }

    public FrontLineWorkerWebRequest(String flwId, String verificationStatus, String name, String designation, LocationRequest location) {
        this.flwId = flwId;
        this.verificationStatus = verificationStatus;
        this.name = name;
        this.designation = designation;
        this.location = location;
    }

    public String getFlwId() {
        return flwId;
    }

    public String getReason() {
        return reason;
    }

   public String getVerificationStatus() {
        return verificationStatus;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FrontLineWorkerWebRequest that = (FrontLineWorkerWebRequest) o;

        if (designation != null ? !designation.equals(that.designation) : that.designation != null) return false;
        if (flwId != null ? !flwId.equals(that.flwId) : that.flwId != null) return false;
        if (location != null ? !location.equals(that.location) : that.location != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (reason != null ? !reason.equals(that.reason) : that.reason != null) return false;
        if (verificationStatus != null ? !verificationStatus.equals(that.verificationStatus) : that.verificationStatus != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = flwId != null ? flwId.hashCode() : 0;
        result = 31 * result + (verificationStatus != null ? verificationStatus.hashCode() : 0);
        result = 31 * result + (reason != null ? reason.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (designation != null ? designation.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        return result;
    }
}