package org.motechproject.ananya.referencedata.mapper;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.referencedata.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.domain.Location;
import org.motechproject.ananya.referencedata.domain.Designation;
import org.motechproject.ananya.referencedata.request.FrontLineWorkerRequest;

public class FrontLineWorkerMapper {
    public static FrontLineWorker mapFrom(FrontLineWorkerRequest frontLineWorkerRequest, Location location) {
        Long msisdn = StringUtils.isBlank(frontLineWorkerRequest.getMsisdn()) ? null : Long.parseLong(formatMsisdn(frontLineWorkerRequest.getMsisdn()));

        return new FrontLineWorker(msisdn, trim(frontLineWorkerRequest.getName()), getDesignation(frontLineWorkerRequest.getDesignation()), location);
    }

    public static FrontLineWorker mapFrom(FrontLineWorker existingFrontLineWorker, FrontLineWorkerRequest frontLineWorkerRequest, Location location) {
        existingFrontLineWorker.setName(trim(frontLineWorkerRequest.getName()));
        existingFrontLineWorker.setDesignation(getDesignation(frontLineWorkerRequest.getDesignation()));
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
