package org.motechproject.ananya.referencedata.flw.mapper;

import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.LocationContract;

public class LocationContractMapper {
    public static LocationContract mapFrom(Location location) {
        return location != null ? new LocationContract(location.getDistrict(), location.getBlock(), location.getPanchayat(), location.getState()) : null;
    }
}
