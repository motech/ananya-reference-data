package org.motechproject.ananya.referencedata.csv.utils;

import org.motechproject.ananya.referencedata.csv.request.LocationImportCSVRequest;
import org.motechproject.ananya.referencedata.flw.domain.LocationStatus;

import java.util.Comparator;

public class LocationComparator implements Comparator<LocationImportCSVRequest> {
    @Override
    public int compare(LocationImportCSVRequest csvRequest1, LocationImportCSVRequest csvRequest2) {
        LocationStatus thisStatus = csvRequest1.getStatusEnum();
        LocationStatus thatStatus = csvRequest2.getStatusEnum();

        int thisWeightage = getStatusWeightage(thisStatus);
        int thatWeightage = getStatusWeightage(thatStatus);
        return thisWeightage > thatWeightage ? -1 : thisWeightage < thatWeightage ? 1 : 0;
    }

    public int getStatusWeightage(LocationStatus status) {
        return status == LocationStatus.NEW || status == LocationStatus.VALID ? 2 : 1;
    }

}