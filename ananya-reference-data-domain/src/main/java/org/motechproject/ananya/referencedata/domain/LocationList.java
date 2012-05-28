package org.motechproject.ananya.referencedata.domain;

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

    public boolean isAlreadyPresent(Location location) {
        return findFor(location.getDistrict(), location.getBlock(), location.getPanchayat()) != null;
    }

    public Location updateLocationCode(Location currentLocation) {
        Location location = findFor(currentLocation.getDistrict(), currentLocation.getBlock(), currentLocation.getPanchayat());

        if (location != null)
            return location;

        Integer districtCode = getDistrictCodeFor(currentLocation);
        Integer blockCode = getBlockCodeFor(currentLocation);
        Integer panchayatCode = getPanchayatCodeFor(currentLocation);
        currentLocation.updateLocationCode(districtCode, blockCode, panchayatCode);
        return currentLocation;
    }

    private Location findFor(String district, String block, String village) {
        for (Location location : locations) {
            if (location.equals(new Location(district, block, village, 0, 0, 0)))
                return location;
        }
        return null;
    }

    private Integer getDistrictCodeFor(Location currentLocation) {
        for (Location location : locations) {
            if (location.getDistrict().equals(currentLocation.getDistrict())) {
                return location.getDistrictCode();
            }
        }
        return getNextDistrictCode();
    }

    private Integer getBlockCodeFor(Location currentLocation) {
        for (Location location : locations) {
            if (location.getDistrict().equals(currentLocation.getDistrict())
                    && location.getBlock().equals(currentLocation.getBlock())) {
                return location.getBlockCode();
            }
        }
        return getNextBlockCode(currentLocation);
    }

    private Integer getPanchayatCodeFor(Location currentLocation) {
        int maxPanchayatCode = 0;
        for (Location location : locations) {
            if (location.getDistrict().equals(currentLocation.getDistrict())
                    && location.getBlock().equals(currentLocation.getBlock())
                    && location.getPanchayatCode() > maxPanchayatCode) {
                maxPanchayatCode = location.getPanchayatCode();
            }
        }
        return maxPanchayatCode + 1;
    }

    private Integer getNextDistrictCode() {
        int maxLocationCode = 0;
        for (Location location : locations) {
            if (location.getDistrictCode() > maxLocationCode) {
                maxLocationCode = location.getDistrictCode();
            }
        }
        return maxLocationCode + 1;
    }

    private Integer getNextBlockCode(Location currentLocation) {
        int maxBlockCode = 0;
        for (Location location : locations) {
            if (location.getDistrict().equals(currentLocation.getDistrict()) && location.getBlockCode() > maxBlockCode) {
                maxBlockCode = location.getBlockCode();
            }
        }
        return maxBlockCode + 1;
    }
}
