package org.motechproject.ananya.referencedata.flw.validators;

import org.junit.Test;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.LocationList;
import org.motechproject.ananya.referencedata.flw.response.FLWValidationResponse;

import java.util.ArrayList;

import static junit.framework.Assert.*;

public class LocationValidatorTest {
    @Test
    public void shouldFailValidationIfFieldsAreBlank() {
        Location location = new Location("", "B1", "P1", 123, 234, 456);
        LocationList locationList = new LocationList(new ArrayList<Location>());
        LocationValidator locationValidator = new LocationValidator(locationList);

        FLWValidationResponse FLWValidationResponse = locationValidator.validate(location);

        assertFalse(FLWValidationResponse.isValid());
        assertTrue(FLWValidationResponse.getMessage().contains("Blank district, block or panchayat"));
    }

    @Test
    public void shouldFailValidationIfLocationIsAlreadyPresent() {
        Location location = new Location("D1", "B1", "P1");
        ArrayList<Location> locations = new ArrayList<Location>();
        locations.add(new Location("D1","B1","P1",123,34,23));
        LocationList locationList = new LocationList(locations);
        LocationValidator locationValidator = new LocationValidator(locationList);

        FLWValidationResponse FLWValidationResponse = locationValidator.validate(location);

        assertFalse(FLWValidationResponse.isValid());
        assertTrue(FLWValidationResponse.getMessage().contains("Location already present"));
    }

    @Test
    public void shouldPassValidationIfAllFieldsArePresent() {
        Location location = new Location("D1", "B1", "P1", 123, 234, 456);
        LocationList locationList = new LocationList(new ArrayList<Location>());
        LocationValidator locationValidator = new LocationValidator(locationList);

        FLWValidationResponse FLWValidationResponse = locationValidator.validate(location);

        assertTrue(FLWValidationResponse.isValid());
        assertTrue(FLWValidationResponse.getMessage().isEmpty());
    }
}
