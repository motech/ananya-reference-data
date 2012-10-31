package org.motechproject.ananya.referencedata.flw.domain;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

public class DesignationTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldValidateDesignation() {
        assertTrue(Designation.isValid("  asHA  "));
        assertTrue(Designation.isValid("ANM"));
        assertTrue(Designation.isValid("AWW"));
        assertFalse(Designation.isValid(""));
        assertFalse(Designation.isValid("  "));
        assertFalse(Designation.isValid(null));
        assertFalse(Designation.isValid("invalid"));
    }

    @Test
    public void shouldReturnDesignationEnumForValidDesignations() {
        assertEquals(Designation.ASHA, Designation.from("  aSHa  "));
        assertEquals(Designation.ANM, Designation.from("ANM"));
        assertEquals(Designation.AWW, Designation.from("AWW"));
    }

    @Test
    public void shouldThrowExceptionForInvalidDesignations() {
        expectedException.expect(IllegalArgumentException.class);
        Designation.from("invalid");
    }
}
