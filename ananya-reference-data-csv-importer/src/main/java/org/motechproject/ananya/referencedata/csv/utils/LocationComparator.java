package org.motechproject.ananya.referencedata.csv.utils;

import org.motechproject.ananya.referencedata.csv.request.LocationImportRequest;
import org.motechproject.ananya.referencedata.flw.domain.LocationStatus;

import java.util.Comparator;

public class LocationComparator implements Comparator<LocationImportRequest> {
    @Override
    public int compare(LocationImportRequest request1, LocationImportRequest request2) {
        if (LocationStatus.isInvalidStatus(request1.getStatus()) && LocationStatus.isValidAlternateLocationStatus(request2.getStatus()))
            return 1;
        if (LocationStatus.isValidAlternateLocationStatus(request1.getStatus()) && LocationStatus.isInvalidStatus(request2.getStatus()))
            return -1;
        return 0;
    }
}
