package org.motechproject.ananya.referencedata.contactCenter.service;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.referencedata.contactCenter.validator.LocationWebRequestValidator;
import org.motechproject.ananya.referencedata.flw.domain.Designation;
import org.motechproject.ananya.referencedata.flw.domain.VerificationStatus;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;
import org.motechproject.ananya.referencedata.flw.validators.Errors;

import java.util.UUID;
import java.util.regex.Pattern;

public class FrontLineWorkerVerificationRequest {

    private VerificationStatus verificationStatus;
    private Long msisdn;
    private UUID flwId;
    private String name;
    private Designation designation;
    private LocationRequest location;
    private String reason;

    public FrontLineWorkerVerificationRequest(UUID flwId, Long msisdn, VerificationStatus verificationStatus, String name, Designation designation, LocationRequest location, String reason) {
        this.verificationStatus = verificationStatus;
        this.msisdn = msisdn;
        this.flwId = flwId;
        this.name = name;
        this.designation = designation;
        this.location = location;
        this.reason = reason;
    }

    public VerificationStatus getVerificationStatus() {
        return verificationStatus;
    }

    public Long getMsisdn() {
        return msisdn;
    }

    public UUID getFlwId() {
        return flwId;
    }

    public String getName() {
        return name;
    }

    public Designation getDesignation() {
        return designation;
    }

    public LocationRequest getLocation() {
        return location;
    }

    public String getReason() {
        return reason;
    }

    public Errors validate() {
        Errors errors = new Errors();
        if (VerificationStatus.SUCCESS == verificationStatus) {
            validateSuccessulVerification(errors);
            return errors;
        }

        validateInvalidOtherRequest(errors);
        return errors;
    }

    private void validateInvalidOtherRequest(Errors errors) {
        if (StringUtils.isBlank(reason)) {
            errors.add("reason field has blank value");
        }
        if (name != null) {
            errors.add("name field should not be a part of the request");
        }
        if (location != null) {
            errors.add("location field should not be a part of the request");
        }
        if (designation != null) {
            errors.add("designation field should not be a part of the request");
        }
    }

    private void validateSuccessulVerification(Errors errors) {
        if (designation == null) {
            errors.add("designation field has invalid/blank value");
        }
        if (StringUtils.isBlank(name) || !Pattern.matches("[a-zA-Z0-9\\s\\.]*", name)) {
            errors.add("name field has invalid/blank value");
        }
        errors.addAll(LocationWebRequestValidator.validate(location));
        if (reason != null) {
            errors.add("reason field should not be a part of the request");
        }
    }
}
