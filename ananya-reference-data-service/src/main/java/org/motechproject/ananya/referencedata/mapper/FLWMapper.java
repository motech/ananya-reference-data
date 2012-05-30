package org.motechproject.ananya.referencedata.mapper;

import org.motechproject.ananya.referencedata.domain.Designation;
import org.motechproject.ananya.referencedata.domain.FLWData;
import org.motechproject.ananya.referencedata.domain.Location;
import org.motechproject.ananya.referencedata.request.FLWRequest;

public class FLWMapper {
    public static FLWData mapFrom(FLWRequest flwRequest, Location location) {
        return new FLWData(Long.parseLong(flwRequest.getMsisdn()), flwRequest.getName(), Designation.valueOf(flwRequest.getDesignation()), location);
    }
}
