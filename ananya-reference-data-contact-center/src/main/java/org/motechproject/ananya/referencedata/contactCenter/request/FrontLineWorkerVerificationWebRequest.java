package org.motechproject.ananya.referencedata.contactCenter.request;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.motechproject.ananya.referencedata.contactCenter.service.FrontLineWorkerVerificationRequest;
import org.motechproject.ananya.referencedata.contactCenter.validator.WebRequestValidator;
import org.motechproject.ananya.referencedata.flw.domain.Designation;
import org.motechproject.ananya.referencedata.flw.domain.VerificationStatus;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;
import org.motechproject.ananya.referencedata.flw.utils.PhoneNumber;
import org.motechproject.ananya.referencedata.flw.validators.Errors;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.UUID;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.motechproject.ananya.referencedata.flw.utils.PhoneNumber.*;

@XmlRootElement(name = "flw")
public class FrontLineWorkerVerificationWebRequest {
    @JsonProperty(value = "id")
    @XmlElement(name = "id")
    private String flwId;

    @XmlElement
    @JsonProperty
    protected String msisdn;

    @XmlElement
    @JsonProperty
    protected String alternateContactNumber;

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

    @JsonIgnore
    private String channel;

    public FrontLineWorkerVerificationWebRequest() {
    }

    public FrontLineWorkerVerificationWebRequest(String flwId, String msisdn, String alternateContactNumber, String verificationStatus, String name, String designation, LocationRequest location, String reason) {
        this.flwId = flwId;
        this.msisdn = msisdn;
        this.alternateContactNumber = alternateContactNumber;
        this.verificationStatus = verificationStatus;
        this.name = name;
        this.designation = designation;
        this.location = location;
        this.reason = reason;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FrontLineWorkerVerificationWebRequest that = (FrontLineWorkerVerificationWebRequest) o;

        if (flwId != null ? !flwId.equals(that.flwId) : that.flwId != null) return false;
        if (msisdn != null ? !msisdn.equals(that.msisdn) : that.msisdn != null) return false;
        if (alternateContactNumber != null ? !alternateContactNumber.equals(that.alternateContactNumber) : that.alternateContactNumber != null) return false;
        if (verificationStatus != null ? !verificationStatus.equals(that.verificationStatus) : that.verificationStatus != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (designation != null ? !designation.equals(that.designation) : that.designation != null) return false;
        if (location != null ? !location.equals(that.location) : that.location != null) return false;
        if (reason != null ? !reason.equals(that.reason) : that.reason != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = flwId != null ? flwId.hashCode() : 0;
        result = 31 * result + (msisdn != null ? msisdn.hashCode() : 0);
        result = 31 * result + (alternateContactNumber != null ? alternateContactNumber.hashCode() : 0);
        result = 31 * result + (verificationStatus != null ? verificationStatus.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (designation != null ? designation.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + (reason != null ? reason.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public Errors validate() {
        Errors errors = new Errors();
        WebRequestValidator validator = new WebRequestValidator();
        validator.validateFlwId(flwId, errors);
        validator.validateMsisdn(msisdn, errors);
        validator.validateAlternateContactNumber(alternateContactNumber, errors);
        validator.validateVerificationStatus(verificationStatus, errors);
        validator.validateChannel(channel, errors);

        if(designation != null) {
            validator.validateDesignation(designation, errors);
        }

        return errors;
    }

    @JsonIgnore
    public FrontLineWorkerVerificationRequest getVerificationRequest() {
        Designation designationEnum = designation == null ? null: Designation.from(designation);
        Long altNumber = isBlank(alternateContactNumber) ? null: formatPhoneNumber(alternateContactNumber);
        FrontLineWorkerVerificationRequest verificationRequest = new FrontLineWorkerVerificationRequest(UUID.fromString(flwId),
                formatPhoneNumber(msisdn), altNumber, VerificationStatus.from(verificationStatus),
                name, designationEnum, location, reason);
        return verificationRequest;
    }
}