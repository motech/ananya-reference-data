package org.motechproject.ananya.referencedata.flw.mapper;

import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.LocationContract;

public class LocationContractMapper {
    public static LocationContract mapFrom(Location location) {
        return new LocationContract(location.getDistrict(), location.getBlock(), location.getPanchayat());
    }
}
