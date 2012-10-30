package org.motechproject.ananya.referencedata.csv.mapper;

import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.LocationStatus;
import org.motechproject.ananya.referencedata.csv.request.LocationImportRequest;

public class LocationImportMapper {
    public static Location mapFrom(Location alreadyPresentLocation, LocationImportRequest request) {
        alreadyPresentLocation.setStatus(LocationStatus.from(request.getStatus()));
        return alreadyPresentLocation;
    }

    public static Location mapFrom(LocationImportRequest request) {
        return new Location(request.getDistrict(), request.getBlock(), request.getPanchayat(), LocationStatus.VALID.name(),null);
    }
}
