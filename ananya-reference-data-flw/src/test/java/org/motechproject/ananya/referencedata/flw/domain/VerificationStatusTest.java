package org.motechproject.ananya.referencedata.flw.domain;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class VerificationStatusTest {

    @Test
    public void shouldValidateVerificationStatus() {
        assertTrue(VerificationStatus.isValid("   SUCCEss   "));
        assertTrue(VerificationStatus.isValid("INVALID  "));
        assertTrue(VerificationStatus.isValid("OthER"));
        assertFalse(VerificationStatus.isValid("Purple"));
    }

    @Test
    public void shouldMapEmptyVerificationStatusToNull() {
        VerificationStatus status = VerificationStatus.from("");
        assertEquals(null, status);
    }

    @Test
    public void shouldCheckIfGivenVerificationStatusIsInvalid() {
        assertTrue(VerificationStatus.isInvalid("INVALID"));

        assertFalse(VerificationStatus.isInvalid("success"));
        assertFalse(VerificationStatus.isInvalid("other"));
        assertFalse(VerificationStatus.isInvalid(null));
        assertFalse(VerificationStatus.isInvalid(""));
    }

    @Test
    public void shouldCheckIfGivenVerificationStatusIsOther() {
        assertTrue(VerificationStatus.isOther("other"));

        assertFalse(VerificationStatus.isOther("success"));
        assertFalse(VerificationStatus.isOther("INVALID"));
        assertFalse(VerificationStatus.isOther(null));
        assertFalse(VerificationStatus.isOther("  "));
    }
}
