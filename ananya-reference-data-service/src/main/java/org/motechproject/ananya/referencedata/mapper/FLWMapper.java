package org.motechproject.ananya.referencedata.mapper;

import org.motechproject.ananya.referencedata.domain.Designation;
import org.motechproject.ananya.referencedata.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.domain.Location;
import org.motechproject.ananya.referencedata.request.FLWRequest;

public class FLWMapper {
    public static FrontLineWorker mapFrom(FLWRequest flwRequest, Location location) {
        return new FrontLineWorker(Long.parseLong(flwRequest.getMsisdn()), flwRequest.getName(), Designation.valueOf(flwRequest.getDesignation()), location);
    }

    public static FrontLineWorker mapFrom(FrontLineWorker existingFrontLineWorker, FLWRequest flwRequest, Location location) {
        existingFrontLineWorker.setName(flwRequest.getName());
        existingFrontLineWorker.setDesignation(Designation.valueOf(flwRequest.getDesignation()));
        existingFrontLineWorker.setLocation(location);

        return existingFrontLineWorker;
    }
}
