package org.motechproject.ananya.referencedata.mapper;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.referencedata.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.domain.Location;
import org.motechproject.ananya.referencedata.domain.Designation;
import org.motechproject.ananya.referencedata.request.FLWRequest;

public class FLWMapper {
    public static FrontLineWorker mapFrom(FLWRequest flwRequest, Location location) {
        Long msisdn = StringUtils.isBlank(flwRequest.getMsisdn()) ? null : Long.parseLong(flwRequest.getMsisdn());
        return new FrontLineWorker(msisdn, flwRequest.getName(), Designation.valueOf(flwRequest.getDesignation()), location);
    }

    public static FrontLineWorker mapFrom(FrontLineWorker existingFrontLineWorker, FLWRequest flwRequest, Location location) {
        existingFrontLineWorker.setName(flwRequest.getName());
        existingFrontLineWorker.setDesignation(Designation.valueOf(flwRequest.getDesignation()));
        existingFrontLineWorker.setLocation(location);

        return existingFrontLineWorker;
    }
}
