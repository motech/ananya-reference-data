package org.motechproject.ananya.referencedata.web.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.referencedata.contactCenter.service.LocationService;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.web.response.LocationResponse;
import org.motechproject.ananya.referencedata.web.response.LocationResponseList;
import org.motechproject.ananya.referencedata.web.validator.ValidationResponse;
import org.motechproject.ananya.referencedata.web.validator.WebRequestValidator;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LocationControllerTest {

    private LocationController locationController;
    @Mock
    private LocationService locationService;
    @Mock
    private WebRequestValidator webRequestValidator;

    @Test
    public void shouldGetLocationMasterCsv() throws Exception {
        final Location validLocation = new Location("d1", "b1", "p1", "valid");
        locationController = new LocationController(locationService, webRequestValidator);
        String channel = "contact_center";
        when(webRequestValidator.validateChannel(channel)).thenReturn(new ValidationResponse());
        when(locationService.getAllValidLocations()).thenReturn(new ArrayList<Location>() {{
            add(validLocation);
        }});

        LocationResponseList responses = locationController.getLocationMaster("contact_center");

        assertEquals(1, responses.size());
        LocationResponse locationResponse = responses.get(0);
        assertEquals(validLocation.getBlock(), locationResponse.getBlock());
        assertEquals(validLocation.getDistrict(), locationResponse.getDistrict());
        assertEquals(validLocation.getPanchayat(), locationResponse.getPanchayat());
    }
}
