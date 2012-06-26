package org.motechproject.ananya.referencedata.flw.validators;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.repository.AllLocations;
import org.motechproject.ananya.referencedata.flw.response.FLWValidationResponse;

import java.util.ArrayList;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LocationValidatorTest {
    @Mock
    AllLocations allLocations;

    @Test
    public void shouldFailValidationIfFieldsAreBlank() {
        Location location = new Location("", "B1", "P1");
        when(allLocations.getFor("", "B1", "P1")).thenReturn(null);
        LocationValidator locationValidator = new LocationValidator(allLocations);

        FLWValidationResponse FLWValidationResponse = locationValidator.validate(location);

        assertFalse(FLWValidationResponse.isValid());
        assertTrue(FLWValidationResponse.getMessage().contains("Blank district, block or panchayat"));
    }

    @Test
    public void shouldFailValidationIfLocationIsAlreadyPresent() {
        Location location = new Location("D1", "B1", "P1");
        ArrayList<Location> locations = new ArrayList<Location>();
        locations.add(new Location("D1","B1","P1"));
        when(allLocations.getFor("D1", "B1", "P1")).thenReturn(location);
        LocationValidator locationValidator = new LocationValidator(allLocations);

        FLWValidationResponse FLWValidationResponse = locationValidator.validate(location);

        assertFalse(FLWValidationResponse.isValid());
        assertTrue(FLWValidationResponse.getMessage().contains("Location already present"));
    }

    @Test
    public void shouldPassValidationIfAllFieldsArePresent() {
        Location location = new Location("D1", "B1", "P1");
        when(allLocations.getFor("D1", "B1", "P1")).thenReturn(null);
        LocationValidator locationValidator = new LocationValidator(allLocations);

        FLWValidationResponse FLWValidationResponse = locationValidator.validate(location);

        assertTrue(FLWValidationResponse.isValid());
        assertTrue(FLWValidationResponse.getMessage().isEmpty());
    }
}
