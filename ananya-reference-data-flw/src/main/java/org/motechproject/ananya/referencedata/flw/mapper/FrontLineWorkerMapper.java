package org.motechproject.ananya.referencedata.flw.mapper;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.referencedata.flw.domain.Designation;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.request.FrontLineWorkerCsvRequest;
import org.motechproject.ananya.referencedata.flw.utils.PhoneNumber;

public class FrontLineWorkerMapper {
    public static FrontLineWorker mapFrom(FrontLineWorkerCsvRequest frontLineWorkerCsvRequest, Location location) {
        Long msisdn = StringUtils.isBlank(frontLineWorkerCsvRequest.getMsisdn()) ? null : formatMsisdn(frontLineWorkerCsvRequest.getMsisdn());

        return new FrontLineWorker(msisdn, trim(frontLineWorkerCsvRequest.getName()), Designation.getFor(frontLineWorkerCsvRequest.getDesignation()), location);
    }

    public static FrontLineWorker mapFrom(FrontLineWorker existingFrontLineWorker, FrontLineWorkerCsvRequest frontLineWorkerCsvRequest, Location location) {
        existingFrontLineWorker.setName(trim(frontLineWorkerCsvRequest.getName()));
        existingFrontLineWorker.setDesignation(Designation.getFor(frontLineWorkerCsvRequest.getDesignation()));
        existingFrontLineWorker.setLocation(location);

        return existingFrontLineWorker;
    }

    private static String trim(String name) {
        return StringUtils.defaultIfEmpty(StringUtils.strip(name), StringUtils.EMPTY);
    }

    public static Long formatMsisdn(String msisdn) {
        return PhoneNumber.formatPhoneNumber(msisdn);
    }
}
