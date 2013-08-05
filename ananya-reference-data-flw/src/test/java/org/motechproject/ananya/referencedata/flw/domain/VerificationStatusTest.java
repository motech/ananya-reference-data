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
    public void shouldMapEmptyVerificationStatusToNull(){
        VerificationStatus status = VerificationStatus.from("");
        assertEquals(null,status);
    }
}
