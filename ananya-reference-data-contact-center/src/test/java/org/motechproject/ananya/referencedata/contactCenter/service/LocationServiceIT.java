package org.motechproject.ananya.referencedata.contactCenter.service;

import org.junit.Test;
import org.motechproject.ananya.referencedata.contactCenter.SpringIntegrationTest;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.mapper.LocationMapper;
import org.motechproject.ananya.referencedata.flw.repository.AllLocations;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class LocationServiceIT extends SpringIntegrationTest {

    @Autowired
    private AllLocations allLocations;
    @Autowired
    private LocationService locationService;

    @Test
    public void shouldHandleSavingOfNewLocationWhichDoesNotExist() {
        Location locationInDb = new Location("d", "b", "p", "VALID");
        allLocations.add(locationInDb);

        Location location = locationService.handleLocation(new LocationRequest("district", "block", "panchayat"));

        assertEquals(2, template.loadAll(Location.class).size());
        assertEquals("NOT VERIFIED", location.getStatus());
    }

    @Test
    public void shouldReturnAnExistingLocation() {
        LocationRequest request = new LocationRequest("district", "block", "panchayat");
        Location location = LocationMapper.mapFrom(request);
        allLocations.add(location);

        Location existingLocation = locationService.handleLocation(request);

        assertEquals(1, template.loadAll(Location.class).size());
        assertNotNull(existingLocation);
        assertEquals(location, existingLocation);
    }
}