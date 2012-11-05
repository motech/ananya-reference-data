package org.motechproject.ananya.referencedata.contactCenter.validator;

import org.junit.Test;
import org.motechproject.ananya.referencedata.flw.validators.Errors;

import java.util.UUID;

import static junit.framework.Assert.assertEquals;

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
        assertEquals("verificationStatus field has invalid value", errors.allMessages());

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
}