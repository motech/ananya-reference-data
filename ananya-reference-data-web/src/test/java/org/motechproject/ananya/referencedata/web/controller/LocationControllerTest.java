package org.motechproject.ananya.referencedata.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.referencedata.contactCenter.service.LocationService;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.LocationStatus;
import org.motechproject.ananya.referencedata.web.response.LocationResponse;
import org.motechproject.ananya.referencedata.web.response.LocationResponseList;
import org.springframework.http.MediaType;
import org.springframework.test.web.server.MvcResult;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.motechproject.ananya.referencedata.web.utils.MVCTestUtils.mockMvc;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class LocationControllerTest {

    private LocationController locationController;
    @Mock
    private LocationService locationService;

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
    }

    @Test
    public void shouldGetLocationsToBeVerifiedCsv() throws Exception {
        Location location1 = new Location("d1", "b1", "p1", LocationStatus.NOT_VERIFIED, null);
        Location location2 = new Location("d2", "b2", "p2", LocationStatus.IN_REVIEW, null);
        String channel = "contact_center";
        when(locationService.getLocationsToBeVerified()).thenReturn(Arrays.asList(location1,location2));

        MvcResult mvcResult = mockMvc(locationController).perform(get("/locationsToBeVerified").param("channel", channel).accept(new MediaType("text", "csv")))
                .andExpect(status().isOk())
                .andExpect(content().type("text/csv"))
                .andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(response.contains("NOT VERIFIED"));
        assertTrue(response.contains("IN REVIEW"));

    }

    @Test
    public void shouldReturnCsvHeadersIfLocationsIsAnEmptyList() throws Exception {
        String channel = "contact_center";
        when(locationService.getLocationsToBeVerified()).thenReturn(new ArrayList<Location>());

        MvcResult mvcResult = mockMvc(locationController).perform(get("/locationsToBeVerified").param("channel", channel).accept(new MediaType("text", "csv")))
                .andExpect(status().isOk())
                .andExpect(content().type("text/csv"))
                .andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        System.out.println(response);

    }

    private void assertLocationResponse(Location expectedLocation, LocationResponse response) {
        LocationResponse locationResponse = response;
        assertEquals(expectedLocation.getBlock(), locationResponse.getBlock());
        assertEquals(expectedLocation.getDistrict(), locationResponse.getDistrict());
        assertEquals(expectedLocation.getPanchayat(), locationResponse.getPanchayat());
    }

    @Test
    public void shouldValidateChannel() throws Exception {
        String channel = "blah";


        mockMvc(locationController)
                .perform(get("/alllocations").param("channel", channel).accept(new MediaType("text", "csv", Charset.defaultCharset())))
                .andExpect(status().isBadRequest())
                .andReturn();
    }
}
