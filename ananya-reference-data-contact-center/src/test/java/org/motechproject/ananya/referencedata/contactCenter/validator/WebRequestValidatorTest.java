package org.motechproject.ananya.referencedata.contactCenter.validator;

import org.junit.Test;
import org.motechproject.ananya.referencedata.flw.request.ChangeMsisdnRequest;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;
import org.motechproject.ananya.referencedata.flw.validators.Errors;

import java.util.UUID;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class WebRequestValidatorTest {

    private String flwId = UUID.randomUUID().toString();

    private WebRequestValidator validator = new WebRequestValidator();

    @Test
    public void shouldValidateMsisdn() {
        Errors errors = new Errors();
        validator.validateMsisdn("invalidmsisdn", errors);
        assertEquals(1, errors.getCount());
        assertEquals("msisdn field has invalid value", errors.allMessages());

        errors = new Errors();
        validator.validateMsisdn("", errors);
        assertEquals(1, errors.getCount());
        assertEquals("msisdn field is missing", errors.allMessages());

        errors = new Errors();
        validator.validateMsisdn(null, errors);
        assertEquals(1, errors.getCount());
        assertEquals("msisdn field is missing", errors.allMessages());

        errors = new Errors();
        validator.validateMsisdn("  ", errors);
        assertEquals(1, errors.getCount());
        assertEquals("msisdn field is missing", errors.allMessages());

        errors = new Errors();
        validator.validateMsisdn("919900503246", errors);
        assertEquals(1, errors.getCount());
        assertEquals("msisdn field has invalid value", errors.allMessages());

        errors = new Errors();
        validator.validateMsisdn("9900503246", errors);
        assertEquals(0, errors.getCount());
    }

    @Test
    public void shouldValidateVerificationStatus() {
        Errors errors = new Errors();
        validator.validateVerificationStatus("invalidVerificationStatus", errors);
        assertEquals(1, errors.getCount());
        assertEquals("verificationStatus field has invalid value", errors.allMessages());

        errors = new Errors();
        validator.validateVerificationStatus("", errors);
        assertEquals(1, errors.getCount());
        assertEquals("verificationStatus field is missing", errors.allMessages());

        errors = new Errors();
        validator.validateVerificationStatus(null, errors);
        assertEquals(1, errors.getCount());
        assertEquals("verificationStatus field is missing", errors.allMessages());

        errors = new Errors();
        validator.validateVerificationStatus("  ", errors);
        assertEquals(1, errors.getCount());
        assertEquals("verificationStatus field is missing", errors.allMessages());

        errors = new Errors();
        validator.validateVerificationStatus("SUCCESS", errors);
        assertEquals(0, errors.getCount());
    }


    @Test
    public void shouldValidateDesignation() {
        Errors errors = new Errors();
        validator.validateDesignation("invalidDesignation", errors);
        assertEquals(1, errors.getCount());
        assertEquals("designation field has invalid value", errors.allMessages());

        errors = new Errors();
        validator.validateDesignation("", errors);
        assertEquals(1, errors.getCount());
        assertEquals("designation field is missing", errors.allMessages());

        errors = new Errors();
        validator.validateDesignation(null, errors);
        assertEquals(1, errors.getCount());
        assertEquals("designation field is missing", errors.allMessages());

        errors = new Errors();
        validator.validateDesignation("  ", errors);
        assertEquals(1, errors.getCount());
        assertEquals("designation field has invalid value", errors.allMessages());

        errors = new Errors();
        validator.validateDesignation("ASHA", errors);
        assertEquals(0, errors.getCount());
    }

    @Test
    public void shouldValidateFlwId() {
        Errors errors = new Errors();
        validator.validateFlwId("invalidFwlId", errors);
        assertEquals(1, errors.getCount());
        assertEquals("id field is not in valid UUID format", errors.allMessages());

        errors = new Errors();
        validator.validateFlwId("", errors);
        assertEquals(1, errors.getCount());
        assertEquals("id field is missing", errors.allMessages());

        errors = new Errors();
        validator.validateFlwId(null, errors);
        assertEquals(1, errors.getCount());
        assertEquals("id field is missing", errors.allMessages());

        errors = new Errors();
        validator.validateFlwId("  ", errors);
        assertEquals(1, errors.getCount());
        assertEquals("id field is not in valid UUID format", errors.allMessages());

        errors = new Errors();
        validator.validateFlwId(UUID.randomUUID().toString(), errors);
        assertEquals(0, errors.getCount());
    }

    @Test
    public void shouldValidateChannel() {
        Errors errors = new Errors();
        validator.validateChannel("invalid channel", errors);
        assertEquals(1, errors.getCount());
        assertEquals("invalid channel: invalid channel", errors.allMessages());

        errors = new Errors();
        validator.validateChannel("", errors);
        assertEquals(1, errors.getCount());
        assertEquals("channel is missing", errors.allMessages());

        errors = new Errors();
        validator.validateChannel(null, errors);
        assertEquals(1, errors.getCount());
        assertEquals("channel is missing", errors.allMessages());

        errors = new Errors();
        validator.validateChannel("  ", errors);
        assertEquals(1, errors.getCount());
        assertEquals("invalid channel:   ", errors.allMessages());

        errors = new Errors();
        validator.validateChannel("contact_center", errors);
        assertEquals(0, errors.getCount());
    }

    @Test
    public void shouldReturnErrorForAnInvalidLocationRequest() {
        Errors errors = new Errors();
        validator.validateLocation(new LocationRequest(null, null, null, null, null), errors);

        assertEquals(4, errors.getCount());
        assertTrue(errors.hasMessage("district field is blank"));
        assertTrue(errors.hasMessage("block field is blank"));
        assertTrue(errors.hasMessage("panchayat field is blank"));
        assertTrue(errors.hasMessage("state field is blank"));
    }

    @Test
    public void shouldReturnErrorForANullLocationRequest() {
        Errors errors = new Errors();
        validator.validateLocation(null, errors);

        assertEquals(1, errors.getCount());
        assertTrue(errors.hasMessage("location is missing"));
    }

    @Test
    public void shouldValidateChangeMsisdnWithInvalidFormat() {
        Errors errors = new Errors();
        ChangeMsisdnRequest changeMsisdn = new ChangeMsisdnRequest("123", UUID.randomUUID().toString());
        validator.validateChangeMsisdn(changeMsisdn,errors, "SUCCESS", null);

        assertEquals(1, errors.getCount());
        assertTrue(errors.hasMessage("msisdn in newMsisdn field has invalid value"));
    }

    @Test
    public void shouldValidateChangeMsisdnWhenMsisdnIsMissing() {
        Errors errors = new Errors();
        ChangeMsisdnRequest changeMsisdn = new ChangeMsisdnRequest("", UUID.randomUUID().toString());
        validator.validateChangeMsisdn(changeMsisdn,errors, "SUCCESS", null);

        assertEquals(1, errors.getCount());
        assertTrue(errors.hasMessage("msisdn in newMsisdn field is missing"));
    }

    @Test
    public void shouldValidateFlwIdFormat() {
        Errors errors = new Errors();

        ChangeMsisdnRequest changeMsisdn = new ChangeMsisdnRequest("1234567890", "1");
        validator.validateChangeMsisdn(changeMsisdn,errors, "SUCCESS", null);
        assertEquals(1, errors.getCount());
        assertTrue(errors.hasMessage("id in newMsisdn field is not in valid UUID format"));
    }

    @Test
    public void shouldAllowNullOrBlankFlwIdInNewMsisdn() {
        Errors errors = new Errors();

        ChangeMsisdnRequest changeMsisdn = new ChangeMsisdnRequest("1234567890", null);
        validator.validateChangeMsisdn(changeMsisdn,errors, "SUCCESS", null);
        assertEquals(0, errors.getCount());

        changeMsisdn.setFlwId("");
        validator.validateChangeMsisdn(changeMsisdn,errors, "SUCCESS", null);
        assertEquals(0, errors.getCount());
    }

    @Test
    public void shouldIgnoreChangeMsisdnFieldWhenNullOrEmpty() {
        Errors errors = new Errors();
        ChangeMsisdnRequest changeMsisdn = new ChangeMsisdnRequest("", "");

        validator.validateChangeMsisdn(changeMsisdn,errors, "SUCCESS", null);
        assertEquals(0, errors.getCount());

        changeMsisdn = null;

        validator.validateChangeMsisdn(changeMsisdn,errors, "SUCCESS", null);
        assertEquals(0, errors.getCount());
    }

    @Test
    public void shouldNotAllowNewMsisdnForOthersAndInvalidStatuses() {
        Errors errors = new Errors();

        ChangeMsisdnRequest changeMsisdn = new ChangeMsisdnRequest("1234567890", null);
        validator.validateChangeMsisdn(changeMsisdn,errors, "INVALID", null);
        assertEquals(1, errors.getCount());
        assertTrue(errors.hasMessage("newMsisdn field should not be a part of the request"));

        errors = new Errors();
        validator.validateChangeMsisdn(changeMsisdn,errors, "OTHERS", null);
        assertEquals(1, errors.getCount());
        assertTrue(errors.hasMessage("newMsisdn field should not be a part of the request"));
    }

    @Test
    public void shouldNotContainErrorsForAValidLocationRequest() {
        Errors errors = new Errors();
        validator.validateLocation(new LocationRequest("district", "block", "panchayat", "state", null), errors);

        assertEquals(0, errors.getCount());
    }

    @Test
    public void changeMsisdnShouldBeADifferentNumber() {
        Errors errors = new Errors();
        validator.validateChangeMsisdn(new ChangeMsisdnRequest("1234567890",""), errors,"SUCCESS", "1234567890");

        assertEquals(1, errors.getCount());
        assertTrue(errors.hasMessage("New Msisdn cannot be same as current msisdn"));
    }
}