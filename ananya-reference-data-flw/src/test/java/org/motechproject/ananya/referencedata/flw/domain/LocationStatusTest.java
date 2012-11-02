package org.motechproject.ananya.referencedata.flw.domain;

import org.junit.Test;

import static junit.framework.Assert.*;

public class LocationStatusTest {

    @Test
    public void shouldReturnTheCorrectLocationStatus() {
        assertEquals(LocationStatus.VALID, LocationStatus.from(" valiD  "));
        assertEquals(LocationStatus.NOT_VERIFIED, LocationStatus.from("  Not_verified"));
        assertNull(LocationStatus.from("not verified"));
    }

    @Test
    public void shouldCheckIfStatusIsValidOrNew() {
        assertTrue(LocationStatus.isValidAlternateLocationStatus("Valid"));
        assertTrue(LocationStatus.isValidAlternateLocationStatus("new"));
        assertFalse(LocationStatus.isValidAlternateLocationStatus("InValid"));
        assertFalse(LocationStatus.isValidAlternateLocationStatus("In review"));
    }

    @Test
    public void shouldCheckIfStatusIsValid() {
        assertTrue(LocationStatus.isValidStatus("Valid"));
        assertFalse(LocationStatus.isValidStatus("InValid"));
        assertFalse(LocationStatus.isValidStatus("In review"));
    }

    @Test
    public void shouldCheckIfStatusIsInvalid() {
        assertTrue(LocationStatus.isInvalidStatus("InValid"));
        assertFalse(LocationStatus.isInvalidStatus("Valid"));
        assertFalse(LocationStatus.isInvalidStatus("In_review"));
    }

    @Test
    public void shouldCheckIfStatusIsNew() {
        assertTrue(LocationStatus.isNewStatus("new"));
        assertFalse(LocationStatus.isNewStatus("Valid"));
        assertFalse(LocationStatus.isNewStatus("In_review"));
    }

    @Test
    public void shouldTestForAValidCsvStatus() {
        assertTrue(LocationStatus.isValidCsvStatus("new"));
        assertTrue(LocationStatus.isValidCsvStatus("Valid"));
        assertTrue(LocationStatus.isValidCsvStatus("in_review"));
        assertTrue(LocationStatus.isValidCsvStatus("invalid"));
        assertFalse(LocationStatus.isValidCsvStatus("not_verified"));
    }

    @Test
    public void shouldTestIfStatusIsUpdtable() {
        assertTrue(LocationStatus.isUpdatable("in_review"));
        assertTrue(LocationStatus.isUpdatable("not_Verified"));
        assertFalse(LocationStatus.isUpdatable("invalid"));
        assertFalse(LocationStatus.isUpdatable("Valid"));
        assertFalse(LocationStatus.isUpdatable("NEW"));
    }
}
