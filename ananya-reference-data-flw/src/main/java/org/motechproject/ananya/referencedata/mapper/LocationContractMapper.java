package org.motechproject.ananya.referencedata.mapper;

import org.motechproject.ananya.referencedata.domain.Location;
import org.motechproject.ananya.referencedata.domain.LocationContract;

public class LocationContractMapper {
    public static LocationContract mapFrom(Location location) {
        return new LocationContract(location.getDistrict(), location.getBlock(), location.getPanchayat());
    }
}
