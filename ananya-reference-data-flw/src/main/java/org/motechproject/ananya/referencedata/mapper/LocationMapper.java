package org.motechproject.ananya.referencedata.mapper;

import org.motechproject.ananya.referencedata.domain.Location;
import org.motechproject.ananya.referencedata.request.LocationRequest;

public class LocationMapper {
    public static Location mapFrom(LocationRequest request) {
        return new Location(request.getDistrict(), request.getBlock(), request.getPanchayat());
    }
}
