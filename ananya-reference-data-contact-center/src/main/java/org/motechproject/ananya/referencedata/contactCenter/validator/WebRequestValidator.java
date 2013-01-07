package org.motechproject.ananya.referencedata.contactCenter.validator;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.referencedata.domain.Channel;
import org.motechproject.ananya.referencedata.flw.domain.Designation;
import org.motechproject.ananya.referencedata.flw.domain.VerificationStatus;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;
import org.motechproject.ananya.referencedata.flw.utils.PhoneNumber;
import org.motechproject.ananya.referencedata.flw.validators.Errors;

import java.util.regex.Pattern;

public class WebRequestValidator {

    public void validateFlwId(String flwId, Errors errors) {
        if (StringUtils.isEmpty(flwId)) {
            errors.add("id field is missing");
            return;
        }
        if (!Pattern.matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$", flwId)) {
            errors.add("id field is not in valid UUID format");
        }
    }

    public void validateMsisdn(String msisdn, Errors errors) {
        if (StringUtils.isEmpty(msisdn)) {
            errors.add("msisdn field is missing");
            return;
        }
        if (!PhoneNumber.isValid(msisdn, false, false)) {
            errors.add("msisdn field has invalid value");
        }
    }

    public void validateVerificationStatus(String verificationStatus, Errors errors) {
        if (StringUtils.isEmpty(verificationStatus)) {
            errors.add("verificationStatus field is missing");
            return;
        }
        if (!VerificationStatus.isValid(verificationStatus))
            errors.add("verificationStatus field has invalid value");
    }

    public void validateDesignation(String designation, Errors errors) {
        if (StringUtils.isEmpty(designation)) {
            errors.add("designation field is missing");
            return;
        }
        if(!Designation.isValid(designation)) {
            errors.add("designation field has invalid value");
        }
    }

    public void validateChannel(String channel, Errors errors) {
        if(StringUtils.isEmpty(channel)) {
            errors.add("channel is missing");
            return ;
        }
        if (!Channel.isValid(channel)) {
            errors.add(String.format("invalid channel: %s", channel));
        }
    }

    public void validateLocation(LocationRequest locationRequest, Errors errors) {
        if (locationRequest == null) {
            errors.add("location is missing");
            return;
        }

        if (StringUtils.isEmpty(locationRequest.getDistrict()))
            errors.add("district field is blank");
        if (StringUtils.isEmpty(locationRequest.getBlock()))
            errors.add("block field is blank");
        if (StringUtils.isEmpty(locationRequest.getPanchayat()))
            errors.add("panchayat field is blank");
    }
}