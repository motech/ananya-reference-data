package org.motechproject.ananya.referencedata.flw.mapper;

import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.LocationStatus;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;

public class LocationMapper {
    public static Location mapFrom(LocationRequest request) {
        return new Location(request.getState(), request.getDistrict(), request.getBlock(), request.getPanchayat(), LocationStatus.NOT_VERIFIED, null);
    }
}
