package org.motechproject.ananya.referencedata.web.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.referencedata.contactCenter.service.LocationService;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.LocationStatus;
import org.springframework.test.web.server.MvcResult;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.motechproject.ananya.referencedata.web.utils.MVCTestUtils.mockMvc;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class HomeControllerTest {

    @Mock
    private LocationService locationService;

    @Test
    public void shouldReturnLocationsAsCSV() throws Exception {
        Location location = new Location("d", "b", "p", LocationStatus.NOT_VERIFIED, null);
        when(locationService.getLocationsToBeVerified()).thenReturn(Arrays.asList(location));

        HomeController homeController = new HomeController(locationService);
        MvcResult mvcResult = mockMvc(homeController).perform(get("/admin/home/download"))
                .andExpect(status().isOk()).andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertTrue(contentAsString.contains("d,b,p"));
    }

    @Test
    public void shouldThrowExceptionOnError() throws Exception {
        HomeController homeController = new HomeController(locationService);
        when(locationService.getLocationsToBeVerified()).thenThrow(new RuntimeException("aragorn"));

        MvcResult mvcResult = mockMvc(homeController).perform(get("/admin/home/download"))
                .andExpect(status().is(500)).andReturn();

        assertEquals("/admin/popup-error",mvcResult.getResponse().getForwardedUrl());
    }
    
}
