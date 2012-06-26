package org.motechproject.ananya.referencedata.flw.domain;

import java.util.ArrayList;
import java.util.List;

public class LocationList {
    private List<Location> locations;

    public LocationList(List<Location> locations) {
        this.locations = new ArrayList<Location>();
        for (Location location : locations) {
            this.locations.add(location);
        }
    }

    public void add(Location location) {
        locations.add(location);
    }

    public boolean isAlreadyPresent(Location location) {
        return findFor(location.getDistrict(), location.getBlock(), location.getPanchayat()) != null;
    }

    public Location findFor(String district, String block, String village) {
        for (Location location : locations) {
            if (location.equals(new Location(district, block, village)))
                return location;
        }
        return null;
    }
}
