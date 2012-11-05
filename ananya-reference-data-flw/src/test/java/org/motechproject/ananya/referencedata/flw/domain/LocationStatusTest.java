package org.motechproject.ananya.referencedata.flw.domain;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;

public class LocationStatusTest {

    @Test
    public void shouldReturnTheCorrectLocationStatus() {
        assertEquals(LocationStatus.VALID, LocationStatus.from(" valiD  "));
        assertEquals(LocationStatus.NOT_VERIFIED, LocationStatus.from("  Not_verified"));
        assertNull(LocationStatus.from("not verified"));
        assertNull(LocationStatus.from(null));
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
    public void shouldHaveStateChecksForNewState() {
        final LocationStatus fromState = LocationStatus.NEW;
        List<LocationStatus> validStates = new ArrayList<>();
        assertTransitions(fromState, validStates);
    }

    @Test
    public void shouldHaveStateChecksForInReviewState() {
        final LocationStatus fromState = LocationStatus.IN_REVIEW;
        List<LocationStatus> validStates = new ArrayList<LocationStatus>() {{
            add(LocationStatus.VALID);
            add(LocationStatus.IN_REVIEW);
            add(LocationStatus.INVALID);
        }};
        assertTransitions(fromState, validStates);
    }

    @Test
    public void shouldHaveStateChecksForValidState() {
        final LocationStatus fromState = LocationStatus.VALID;
        List<LocationStatus> validStates = new ArrayList<LocationStatus>() {{
            add(LocationStatus.VALID);
            add(LocationStatus.INVALID);
        }};
        assertTransitions(fromState, validStates);
    }

    @Test
    public void shouldHaveStateChecksForInValidState() {
        final LocationStatus fromState = LocationStatus.INVALID;
        List<LocationStatus> validStates = new ArrayList<LocationStatus>() {{
            add(LocationStatus.INVALID);
        }};
        assertTransitions(fromState, validStates);
    }

    @Test
    public void shouldHaveStateChecksForNotVerifiedState() {
        final LocationStatus fromState = LocationStatus.NOT_VERIFIED;
        List<LocationStatus> validStates = new ArrayList<LocationStatus>() {{
            add(LocationStatus.VALID);
            add(LocationStatus.IN_REVIEW);
            add(LocationStatus.INVALID);
        }};
        assertTransitions(fromState, validStates);
    }

    @Test
    public void shouldTestTransitionsForNew() {
        assertFalse(LocationStatus.NEW.canTransitionTo(LocationStatus.INVALID));
        assertFalse(LocationStatus.NEW.canTransitionTo(LocationStatus.VALID));
        assertFalse(LocationStatus.NEW.canTransitionTo(LocationStatus.IN_REVIEW));
        assertFalse(LocationStatus.NEW.canTransitionTo(LocationStatus.NOT_VERIFIED));
        assertFalse(LocationStatus.NEW.canTransitionTo(LocationStatus.NEW));
    }

    @Test
    public void shouldTestTransitionsForNotVerified() {
        assertFalse(LocationStatus.NOT_VERIFIED.canTransitionTo(LocationStatus.NOT_VERIFIED));
        assertFalse(LocationStatus.NOT_VERIFIED.canTransitionTo(LocationStatus.NEW));
        assertTrue(LocationStatus.NOT_VERIFIED.canTransitionTo(LocationStatus.VALID));
        assertTrue(LocationStatus.NOT_VERIFIED.canTransitionTo(LocationStatus.IN_REVIEW));
        assertTrue(LocationStatus.NOT_VERIFIED.canTransitionTo(LocationStatus.INVALID));
    }

    @Test
    public void shouldTestTransitionsForInReview() {
        assertFalse(LocationStatus.IN_REVIEW.canTransitionTo(LocationStatus.NOT_VERIFIED));
        assertFalse(LocationStatus.IN_REVIEW.canTransitionTo(LocationStatus.NEW));
        assertTrue(LocationStatus.IN_REVIEW.canTransitionTo(LocationStatus.IN_REVIEW));
        assertTrue(LocationStatus.IN_REVIEW.canTransitionTo(LocationStatus.INVALID));
        assertTrue(LocationStatus.IN_REVIEW.canTransitionTo(LocationStatus.VALID));
    }

    @Test
    public void shouldTestTransitionsForInvalid() {
        assertFalse(LocationStatus.INVALID.canTransitionTo(LocationStatus.NOT_VERIFIED));
        assertFalse(LocationStatus.INVALID.canTransitionTo(LocationStatus.NEW));
        assertFalse(LocationStatus.INVALID.canTransitionTo(LocationStatus.IN_REVIEW));
        assertFalse(LocationStatus.INVALID.canTransitionTo(LocationStatus.VALID));
        assertTrue(LocationStatus.INVALID.canTransitionTo(LocationStatus.INVALID));
    }

    @Test
    public void shouldTestTransitionsForValid() {
        assertFalse(LocationStatus.VALID.canTransitionTo(LocationStatus.NOT_VERIFIED));
        assertFalse(LocationStatus.VALID.canTransitionTo(LocationStatus.NEW));
        assertFalse(LocationStatus.VALID.canTransitionTo(LocationStatus.IN_REVIEW));
        assertTrue(LocationStatus.VALID.canTransitionTo(LocationStatus.VALID));
        assertTrue(LocationStatus.VALID.canTransitionTo(LocationStatus.INVALID));
    }

    private void assertTransitions(LocationStatus fromStatus, List<LocationStatus> validStates) {
        for (LocationStatus status : LocationStatus.values()) {
            if (validStates.contains(status))
                assertTrue("Should allow transition to : " + status.name(), fromStatus.canTransitionTo(status));
            else
                assertFalse("Should not allow transition to : " + status.name(), fromStatus.canTransitionTo(status));
        }
    }
}
