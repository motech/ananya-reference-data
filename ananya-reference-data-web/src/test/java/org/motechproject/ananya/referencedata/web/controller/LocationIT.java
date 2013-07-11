package org.motechproject.ananya.referencedata.web.controller;

import org.junit.Assert;
import org.junit.Test;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.repository.AllLocations;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;
import org.motechproject.ananya.referencedata.web.controller.LocationController;
import org.motechproject.ananya.referencedata.web.functional.framework.SpringIntegrationTestCase;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

public class LocationIT extends SpringIntegrationTestCase {
    @Autowired
    private LocationController locationController;
    @Autowired
    private AllLocations allLocations;

    @Test
    public void shouldSaveLocationWithoutState() throws IOException {
        Location location = allLocations.getFor("Bihar", "d1", "b1", "p1");
        Assert.assertNull(location);
        locationController.syncLocation(new LocationRequest("d1", "b1", "p1", null));
        location = allLocations.getFor("Bihar", "d1", "b1", "p1");
        Assert.assertEquals("Bihar", location.getState());
        Assert.assertEquals("D1", location.getDistrict());
        Assert.assertEquals("B1", location.getBlock());
        Assert.assertEquals("P1", location.getPanchayat());
    }

    @Test
    public void shouldSaveLocationWithState() throws IOException {
        Location location = allLocations.getFor("Orissa", "d1", "b1", "p1");
        Assert.assertNull(location);
        locationController.syncLocation(new LocationRequest("d1", "b1", "p1", "Orissa"));
        location = allLocations.getFor("Orissa", "d1", "b1", "p1");
        Assert.assertEquals("Orissa", location.getState());
        Assert.assertEquals("D1", location.getDistrict());
        Assert.assertEquals("B1", location.getBlock());
        Assert.assertEquals("P1", location.getPanchayat());
    }
}