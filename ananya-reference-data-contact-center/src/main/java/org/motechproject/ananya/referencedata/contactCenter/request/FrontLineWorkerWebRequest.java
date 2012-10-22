package org.motechproject.ananya.referencedata.contactCenter.request;

import org.codehaus.jackson.annotate.JsonProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "flw")
public class FrontLineWorkerWebRequest {
    @JsonProperty(value = "id")
    @XmlElement(name = "id")
    private String guid;

    @XmlElement(name = "verification-status")
    @JsonProperty(value = "verification-status")
    protected String verificationStatus;

    @XmlElement
    @JsonProperty
    private String reason;

    public FrontLineWorkerWebRequest() {
    }

    public FrontLineWorkerWebRequest(String guid, String verificationStatus, String reason) {
        this.guid = guid;
        this.verificationStatus = verificationStatus;
        this.reason = reason;
    }

    public String getGuid() {
        return guid;
    }

    public String getReason() {
        return reason;
    }

   public String getVerificationStatus() {
        return verificationStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FrontLineWorkerWebRequest that = (FrontLineWorkerWebRequest) o;

        if (guid != null ? !guid.equals(that.guid) : that.guid != null) return false;
        if (reason != null ? !reason.equals(that.reason) : that.reason != null) return false;
        if (verificationStatus != null ? !verificationStatus.equals(that.verificationStatus) : that.verificationStatus != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = guid != null ? guid.hashCode() : 0;
        result = 31 * result + (verificationStatus != null ? verificationStatus.hashCode() : 0);
        result = 31 * result + (reason != null ? reason.hashCode() : 0);
        return result;
    }
}