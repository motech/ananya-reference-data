package org.motechproject.ananya.referencedata.csv.mapper;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.referencedata.csv.request.FrontLineWorkerImportRequest;
import org.motechproject.ananya.referencedata.flw.domain.Designation;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.utils.PhoneNumber;

public class FrontLineWorkerImportMapper {
    public static FrontLineWorker mapToNewFlw(FrontLineWorkerImportRequest frontLineWorkerImportRequest, Location location) {
        Long msisdn = StringUtils.isBlank(frontLineWorkerImportRequest.getMsisdn()) ? null : formatMsisdn(frontLineWorkerImportRequest.getMsisdn());

        return new FrontLineWorker(msisdn, trim(frontLineWorkerImportRequest.getName()), Designation.getFor(frontLineWorkerImportRequest.getDesignation()), location);
    }

    public static FrontLineWorker mapToExistingFlw(FrontLineWorker existingFrontLineWorker, FrontLineWorkerImportRequest frontLineWorkerImportRequest, Location location) {
        existingFrontLineWorker.setName(trim(frontLineWorkerImportRequest.getName()));
        existingFrontLineWorker.setDesignation(Designation.getFor(frontLineWorkerImportRequest.getDesignation()));
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
