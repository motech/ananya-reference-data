package org.motechproject.ananya.referencedata.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.referencedata.contactCenter.service.LocationService;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.LocationStatus;
import org.motechproject.ananya.referencedata.web.domain.CsvUploadRequest;
import org.motechproject.ananya.referencedata.web.response.LocationResponse;
import org.motechproject.ananya.referencedata.web.response.LocationResponseList;
import org.springframework.http.MediaType;

import java.nio.charset.Charset;
import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.motechproject.ananya.referencedata.web.utils.MVCTestUtils.mockMvc;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class LocationControllerTest {

    private LocationController locationController;
    @Mock
    private LocationService locationService;
    @Mock
    private CsvUploadRequest mockLocationsCsvRequest;

    @Before
    public void setUp() {
        locationController = new LocationController(locationService);
    }

    @Test
    public void shouldGetLocationMasterCsv() throws Exception {
        final Location validLocation = new Location("d1", "b1", "p1", LocationStatus.VALID, null);
        String channel = "contact_center";
        when(locationService.getAllValidLocations()).thenReturn(new ArrayList<Location>() {{
            add(validLocation);
        }});

        LocationResponseList responses = locationController.getLocationMaster(channel);

        assertEquals(1, responses.size());
        assertLocationResponse(validLocation, responses.get(0));
        assertNotNull(responses.getFileName());
    }

    @Test
    public void shouldValidateChannel() throws Exception {
        String channel = "blah";
        mockMvc(locationController)
                .perform(get("/alllocations").param("channel", channel).accept(new MediaType("text", "csv", Charset.defaultCharset())))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    private void assertLocationResponse(Location expectedLocation, LocationResponse response) {
        LocationResponse locationResponse = response;
        assertEquals(expectedLocation.getBlock(), locationResponse.getBlock());
        assertEquals(expectedLocation.getDistrict(), locationResponse.getDistrict());
        assertEquals(expectedLocation.getPanchayat(), locationResponse.getPanchayat());
    }
}
