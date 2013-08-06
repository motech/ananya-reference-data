package org.motechproject.ananya.referencedata.contactCenter.validator;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.referencedata.domain.Channel;
import org.motechproject.ananya.referencedata.flw.domain.Designation;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.VerificationStatus;
import org.motechproject.ananya.referencedata.flw.request.ChangeMsisdnRequest;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;
import org.motechproject.ananya.referencedata.flw.utils.PhoneNumber;
import org.motechproject.ananya.referencedata.flw.validators.Errors;

import java.util.regex.Pattern;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;

public class WebRequestValidator {

    public void validateFlwId(String flwId, Errors errors) {
        if (StringUtils.isEmpty(flwId)) {
            errors.add("id field is missing");
            return;
        }
        validateFlwIdFormat(flwId, "id", errors);
    }

    public void validateMsisdn(String msisdn, Errors errors) {
        validMsisdnWithMissing(msisdn, "msisdn", errors);
    }

    public void validateVerificationStatus(String verificationStatus, Errors errors) {
        if (isBlank(verificationStatus)) {
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
        if (!Designation.isValid(designation)) {
            errors.add("designation field has invalid value");
        }
    }

    public void validateChannel(String channel, Errors errors) {
        if (StringUtils.isEmpty(channel)) {
            errors.add("channel is missing");
            return;
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
        if (StringUtils.isEmpty(locationRequest.getState()))
            errors.add("state field is blank");
    }

    public void validateAlternateContactNumber(String alternateContactNumber, String verificationStatus, Errors errors) {
        if (verificationSuccess(verificationStatus) && alternateContactNumberMissing(alternateContactNumber)) {
            errors.add("alternate contact number field is mandatory");
            return;
        }
        if (verificationNotSuccess(verificationStatus) && alternateContactNumberPresent(alternateContactNumber)) {
            errors.add("alternate contact number should not be a part of the request");
            return;
        }
        if (StringUtils.isNotBlank(alternateContactNumber))
            validateMsisdnFormat(alternateContactNumber, "alternate_contact_number", errors);
    }

    void validateMsisdnFormat(String msisdn, final String fieldName, Errors errors) {
        if (!PhoneNumber.isValid(msisdn, false, false)) {
            errors.add(fieldName + " field has invalid value");
        }
    }

    private boolean alternateContactNumberMissing(String alternateContactNumber) {
        return alternateContactNumber == null;
    }

    private boolean alternateContactNumberPresent(String alternateContactNumber) {
        return !alternateContactNumberMissing(alternateContactNumber);
    }

    private boolean verificationNotSuccess(String verificationStatus) {
        return !verificationSuccess(verificationStatus);
    }

    private boolean verificationSuccess(String verificationStatus) {
        return VerificationStatus.SUCCESS.name().equals(verificationStatus);
    }

    private void validateFlwIdFormat(String flwId, String id, Errors errors) {
        if (!Pattern.matches(FrontLineWorker.FLW_ID_FORMAT, flwId)) {
            errors.add(id +" field is not in valid UUID format");
        }
    }
    private void validMsisdnWithMissing(String msisdn, String fieldName, Errors errors) {
        if (StringUtils.isEmpty(msisdn)) {
            errors.add(fieldName +" field is missing");
            return;
        }
        validateMsisdnFormat(msisdn, fieldName, errors);
    }

    public void validateChangeMsisdn(ChangeMsisdnRequest changeMsisdn, Errors errors) {
        if(changeMsisdn == null || (isBlank(changeMsisdn.getMsisdn()) && isBlank(changeMsisdn.getFlwId())))
            return;
        validMsisdnWithMissing(changeMsisdn.getMsisdn(),"msisdn in newMsisdn", errors);
        if(isNotBlank(changeMsisdn.getFlwId()))
            validateFlwIdFormat(changeMsisdn.getFlwId(), "id in newMsisdn", errors);
    }
}