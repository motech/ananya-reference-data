package org.motechproject.ananya.referencedata.web.mapper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.web.response.LocationResponse;
import org.motechproject.ananya.referencedata.web.response.LocationResponseList;
import org.motechproject.ananya.referencedata.web.response.LocationResponseWithoutState;
import org.motechproject.ananya.referencedata.web.response.LocationsToBeVerifiedResponse;

import java.util.List;

public class LocationResponseMapper {

    public static LocationResponseList mapValidLocations(List<Location> locationList) {
        List<LocationResponseWithoutState> locationResponses = (List<LocationResponseWithoutState>) CollectionUtils.collect(locationList, new Transformer() {
            @Override
            public Object transform(Object input) {
                Location request = (Location) input;
                return new LocationResponseWithoutState(request.getDistrict(), request.getBlock(), request.getPanchayat());
            }
        });
        return new LocationResponseList(locationResponses,LocationResponseWithoutState.class);
    }

    public static LocationResponseList mapLocationsToBeVerified(List<Location> locationList) {
        List<LocationsToBeVerifiedResponse> locationResponses = (List<LocationsToBeVerifiedResponse>) CollectionUtils.collect(locationList, new Transformer() {
            @Override
            public Object transform(Object input) {
                Location request = (Location) input;
                return new LocationsToBeVerifiedResponse(request.getState(), request.getDistrict(), request.getBlock(), request.getPanchayat(),request.getStatus().getDescription());
            }
        });
        return new LocationResponseList(locationResponses,LocationsToBeVerifiedResponse.class);

    }
}
