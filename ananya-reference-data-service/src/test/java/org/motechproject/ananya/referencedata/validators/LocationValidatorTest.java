package org.motechproject.ananya.referencedata.validators;

import org.junit.Test;
import org.motechproject.ananya.referencedata.domain.Location;
import org.motechproject.ananya.referencedata.domain.LocationList;
import org.motechproject.ananya.referencedata.response.ValidationResponse;

import java.util.ArrayList;

import static junit.framework.Assert.*;

public class LocationValidatorTest {
    @Test
    public void shouldFailValidationIfFieldsAreBlank() {
        Location location = new Location("", "B1", "P1", 123, 234, 456);
        LocationList locationList = new LocationList(new ArrayList<Location>());
        LocationValidator locationValidator = new LocationValidator(locationList);

        ValidationResponse validationResponse = locationValidator.validate(location);

        assertFalse(validationResponse.isValid());
        assertEquals("Blank district, block or panchayat", validationResponse.getMessage());
    }

    @Test
    public void shouldFailValidationIfLocationIsAlreadyPresent() {
        Location location = new Location("D1", "B1", "P1");
        ArrayList<Location> locations = new ArrayList<Location>();
        locations.add(new Location("D1","B1","P1",123,34,23));
        LocationList locationList = new LocationList(locations);
        LocationValidator locationValidator = new LocationValidator(locationList);

        ValidationResponse validationResponse = locationValidator.validate(location);

        assertFalse(validationResponse.isValid());
        assertEquals("Location already present", validationResponse.getMessage());
    }

    @Test
    public void shouldPassValidationIfAllFieldsArePresent() {
        Location location = new Location("D1", "B1", "P1", 123, 234, 456);
        LocationList locationList = new LocationList(new ArrayList<Location>());
        LocationValidator locationValidator = new LocationValidator(locationList);

        ValidationResponse validationResponse = locationValidator.validate(location);

        assertTrue(validationResponse.isValid());
        assertEquals("Successfully created location", validationResponse.getMessage());
    }
}
