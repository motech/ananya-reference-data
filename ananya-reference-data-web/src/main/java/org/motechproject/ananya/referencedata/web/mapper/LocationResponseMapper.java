package org.motechproject.ananya.referencedata.web.mapper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.web.response.LocationResponse;
import org.motechproject.ananya.referencedata.web.response.LocationResponseList;
import org.motechproject.ananya.referencedata.web.response.LocationsWithStatusResponse;

import java.util.List;

public class LocationResponseMapper {

    public static LocationResponseList mapWithoutStatus(List<Location> locationList) {
        List<LocationResponse> locationResponses = (List<LocationResponse>) CollectionUtils.collect(locationList, new Transformer() {
            @Override
            public Object transform(Object input) {
                Location request = (Location) input;
                return new LocationResponse(request.getDistrict(), request.getBlock(), request.getPanchayat());
            }
        });
        return new LocationResponseList(locationResponses,LocationResponse.class);
    }

    public static LocationResponseList mapWithStatus(List<Location> locationList) {
        List<LocationsWithStatusResponse> locationResponses = (List<LocationsWithStatusResponse>) CollectionUtils.collect(locationList, new Transformer() {
            @Override
            public Object transform(Object input) {
                Location request = (Location) input;
                return new LocationsWithStatusResponse(request.getDistrict(), request.getBlock(), request.getPanchayat(),request.getStatus().getDescription());
            }
        });
        return new LocationResponseList(locationResponses,LocationsWithStatusResponse.class);

    }
}
