package org.motechproject.ananya.referencedata.flw.request;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "newMsisdn")
@XmlAccessorType(XmlAccessType.FIELD)
public class ChangeMsisdnRequest {
    @XmlElement
    @JsonProperty
    private String msisdn;

    @XmlElement
    @JsonProperty
    private String flwId;

    public ChangeMsisdnRequest(String msisdn, String flwId) {
        this.msisdn = msisdn;
        this.flwId = flwId;
    }

    public ChangeMsisdnRequest(){}

    public String getMsisdn() {
        return msisdn;
    }

    public String getFlwId() {
        return flwId;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public void setFlwId(String flwId) {
        this.flwId = flwId;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChangeMsisdnRequest that = (ChangeMsisdnRequest) o;

        if (msisdn != null ? !msisdn.equals(that.msisdn) : that.msisdn != null) return false;
        if (flwId != null ? !flwId.equals(that.flwId) : that.flwId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = msisdn != null ? msisdn.hashCode() : 0;
        result = 31 * result + (flwId != null ? flwId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
