package org.motechproject.ananya.referencedata.mapper;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.referencedata.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.domain.Location;
import org.motechproject.ananya.referencedata.domain.Designation;
import org.motechproject.ananya.referencedata.request.FLWRequest;

public class FLWMapper {
    public static FrontLineWorker mapFrom(FLWRequest flwRequest, Location location) {
        Long msisdn = StringUtils.isBlank(flwRequest.getMsisdn()) ? null : Long.parseLong(formatMsisdn(flwRequest.getMsisdn()));

        return new FrontLineWorker(msisdn, trim(flwRequest.getName()), getDesignation(flwRequest.getDesignation()), location);
    }

    public static FrontLineWorker mapFrom(FrontLineWorker existingFrontLineWorker, FLWRequest flwRequest, Location location) {
        existingFrontLineWorker.setName(trim(flwRequest.getName()));
        existingFrontLineWorker.setDesignation(getDesignation(flwRequest.getDesignation()));
        existingFrontLineWorker.setLocation(location);

        return existingFrontLineWorker;
    }

    private static String trim(String name) {
        return StringUtils.defaultIfEmpty(StringUtils.strip(name), StringUtils.EMPTY);
    }

    public static String formatMsisdn(String msisdn) {
        return msisdn.length() == 10 ? "91" + msisdn : msisdn;
    }

    private static Designation getDesignation(String designation) {
        return !Designation.contains(designation) ? Designation.INVALID : Designation.valueOf(designation);
    }
}
