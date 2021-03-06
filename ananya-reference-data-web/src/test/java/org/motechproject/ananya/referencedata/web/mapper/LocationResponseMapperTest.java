package org.motechproject.ananya.referencedata.web.mapper;

import org.junit.Test;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.LocationStatus;
import org.motechproject.ananya.referencedata.web.response.LocationResponse;
import org.motechproject.ananya.referencedata.web.response.LocationResponseList;
import org.motechproject.ananya.referencedata.web.response.LocationsToBeVerifiedResponse;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class LocationResponseMapperTest {

    @Test
    public void shouldMapToLocationResponseWithoutStatus(){
        List<Location> locationList = new ArrayList();
        Location location1 = new Location("S1", "D1", "B1", "P1", LocationStatus.IN_REVIEW, null);
        Location location2 = new Location("S2", "D2", "B2", "P2", LocationStatus.VALID, null);
        locationList.add(location1);
        locationList.add(location2);

        LocationResponseList locationResponses = LocationResponseMapper.mapValidLocations(locationList);

        assertLocationResponseWithoutState(location1,locationResponses.get(0));
        assertLocationResponseWithoutState(location2,locationResponses.get(1));
    }

    @Test
    public void shouldMapToLocationResponseWithStatus(){
        List<Location> locationList = new ArrayList();
        Location location1 = new Location("S1", "D1", "B1", "P1", LocationStatus.IN_REVIEW, null);
        Location location2 = new Location("S2", "D2", "B2", "P2", LocationStatus.VALID, null);
        locationList.add(location1);
        locationList.add(location2);

        LocationResponseList locationResponses = LocationResponseMapper.mapLocationsToBeVerified(locationList);

        LocationsToBeVerifiedResponse response1 = (LocationsToBeVerifiedResponse)locationResponses.get(0);
        LocationsToBeVerifiedResponse response2 = (LocationsToBeVerifiedResponse)locationResponses.get(1);
        assertLocationResponse(location1, response1);
        assertEquals(location1.getStatus().getDescription(), response1.getStatus());
        assertLocationResponse(location2,response2);
        assertEquals(location2.getStatus().getDescription(), response2.getStatus());

    }

    private void assertLocationResponse(Location expectedLocation, LocationResponse response) {
        assertLocationResponseWithoutState(expectedLocation,response);
        assertEquals(expectedLocation.getState(), response.getState());
    }
    private void assertLocationResponseWithoutState(Location expectedLocation, LocationResponse response) {
        assertEquals(expectedLocation.getBlock(), response.getBlock());
        assertEquals(expectedLocation.getDistrict(), response.getDistrict());
        assertEquals(expectedLocation.getPanchayat(), response.getPanchayat());
    }
}
