package org.motechproject.ananya.referencedata.flw.mapper;

import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;

public class LocationMapper {
    public static Location mapFrom(LocationRequest request) {
        return new Location(request.getDistrict(), request.getBlock(), request.getPanchayat());
    }
}
