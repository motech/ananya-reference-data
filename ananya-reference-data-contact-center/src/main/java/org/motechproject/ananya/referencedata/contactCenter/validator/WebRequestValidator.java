package org.motechproject.ananya.referencedata.contactCenter.validator;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.motechproject.ananya.referencedata.flw.domain.VerificationStatus.SUCCESS;
import static org.motechproject.ananya.referencedata.flw.domain.VerificationStatus.isValid;

import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.referencedata.contactCenter.service.LocationService;
import org.motechproject.ananya.referencedata.domain.Channel;
import org.motechproject.ananya.referencedata.flw.domain.Designation;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.LocationStatus;
import org.motechproject.ananya.referencedata.flw.repository.AllLocations;
import org.motechproject.ananya.referencedata.flw.repository.DataAccessTemplate;
import org.motechproject.ananya.referencedata.flw.request.ChangeMsisdnRequest;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;
import org.motechproject.ananya.referencedata.flw.utils.PhoneNumber;
import org.motechproject.ananya.referencedata.flw.validators.Errors;
import org.springframework.beans.factory.annotation.Autowired;

public class WebRequestValidator {
	
	@Autowired
	private LocationService locationService;

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
        if (!isValid(verificationStatus))
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

    public void validateChangeMsisdn(ChangeMsisdnRequest changeMsisdn, Errors errors, String verificationStatus, String currentMsisdn) {
        if (changeMsisdn == null || (isBlank(changeMsisdn.getMsisdn()) && isBlank(changeMsisdn.getFlwId())))
            return;
        if (!SUCCESS.name().equals(verificationStatus)) {
            errors.add("newMsisdn field should not be a part of the request");
            return;
        }
        validateNewMsisdn(changeMsisdn, errors, currentMsisdn);
    }

    private void validateNewMsisdn(ChangeMsisdnRequest changeMsisdn, Errors errors, String currentMsisdn) {
        if (StringUtils.isBlank(changeMsisdn.getMsisdn())) {
            errors.add("msisdn in newMsisdn field is missing");
            return;
        }
        validateMsisdnFormat(changeMsisdn.getMsisdn(), "msisdn in newMsisdn", errors);
        if (changeMsisdn.getMsisdn().equals(currentMsisdn)) {
            errors.add("New msisdn cannot be same as current msisdn");
            return;
        }
        if (isNotBlank(changeMsisdn.getFlwId()))
            validateFlwIdFormat(changeMsisdn.getFlwId(), "id in newMsisdn", errors);
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
        return SUCCESS.name().equals(verificationStatus);
    }

    private void validateFlwIdFormat(String flwId, String messagePrefix, Errors errors) {
        if (!Pattern.matches(FrontLineWorker.FLW_ID_FORMAT, flwId)) {
            errors.add(messagePrefix + " field is not in valid UUID format");
        }
    }

    private void validMsisdnWithMissing(String msisdn, String fieldName, Errors errors) {
        if (StringUtils.isBlank(msisdn)) {
            errors.add(fieldName + " field is missing");
            return;
        }
        validateMsisdnFormat(msisdn, fieldName, errors);
    }
}