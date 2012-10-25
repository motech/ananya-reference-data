package org.motechproject.ananya.referencedata.flw.domain;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class VerificationStatusTest {

    @Test
    public void shouldValidateVerificationStatus() {
        assertTrue(VerificationStatus.isValid("SUCCESS"));
        assertTrue(VerificationStatus.isValid("INVALID  "));
        assertTrue(VerificationStatus.isValid("OthER"));
        assertFalse(VerificationStatus.isValid("Purple"));
    }

    @Test
    public void shouldCheckForSuccessVerificationState() {
        assertTrue(VerificationStatus.isSuccess("SUCCESS"));
        assertTrue(VerificationStatus.isSuccess("  SUCCESS"));
        assertTrue(VerificationStatus.isSuccess("SuCCEsS"));
        assertFalse(VerificationStatus.isSuccess("Legolas"));
        assertFalse(VerificationStatus.isSuccess(null));
    }
}
