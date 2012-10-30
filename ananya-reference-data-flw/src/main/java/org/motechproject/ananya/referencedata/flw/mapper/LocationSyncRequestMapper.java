package org.motechproject.ananya.referencedata.flw.mapper;

import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.service.request.LocationRequest;
import org.motechproject.ananya.referencedata.flw.service.request.LocationSyncRequest;

public class LocationSyncRequestMapper {

    public static LocationSyncRequest map(Location location) {
        Location alternateLocation = location.getAlternateLocation();
        LocationRequest actualLocation = new LocationRequest(location.getDistrict(), location.getBlock(), location.getPanchayat());
        LocationRequest newLocation = alternateLocation == null
                ? actualLocation : new LocationRequest(alternateLocation.getDistrict(), alternateLocation.getBlock(), alternateLocation.getPanchayat());

        return new LocationSyncRequest(actualLocation, newLocation, location.getStatus(), location.getLastModified());
    }
}