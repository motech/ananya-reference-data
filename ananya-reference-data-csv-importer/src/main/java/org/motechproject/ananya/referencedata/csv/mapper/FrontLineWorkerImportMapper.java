package org.motechproject.ananya.referencedata.csv.mapper;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.referencedata.csv.request.FrontLineWorkerImportRequest;
import org.motechproject.ananya.referencedata.flw.domain.Designation;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.VerificationStatus;
import org.motechproject.ananya.referencedata.flw.utils.PhoneNumber;

public class FrontLineWorkerImportMapper {
    public static FrontLineWorker mapToNewFlw(FrontLineWorkerImportRequest frontLineWorkerImportRequest, Location location) {
        Long msisdn = StringUtils.isBlank(frontLineWorkerImportRequest.getMsisdn()) ? null : formatMsisdn(frontLineWorkerImportRequest.getMsisdn());

        return new FrontLineWorker(msisdn, trim(frontLineWorkerImportRequest.getName()), Designation.from(frontLineWorkerImportRequest.getDesignation()), location, frontLineWorkerImportRequest.getVerificationStatus());
    }

    public static FrontLineWorker mapToExistingFlw(FrontLineWorker existingFrontLineWorker, FrontLineWorkerImportRequest request, Location location) {
        existingFrontLineWorker.setName(trim(request.getName()));
        existingFrontLineWorker.setDesignation(getDesignation(request.getDesignation()));
        existingFrontLineWorker.setLocation(location);
        existingFrontLineWorker.setVerificationStatus(VerificationStatus.from(request.getVerificationStatus()));

        return existingFrontLineWorker;
    }

    private static Designation getDesignation(String designation) {
        if(Designation.isValid(designation)) {
            return Designation.from(designation);
        }
        return null;
    }

    private static String trim(String name) {
        return StringUtils.defaultIfEmpty(StringUtils.strip(name), StringUtils.EMPTY);
    }

    public static Long formatMsisdn(String msisdn) {
        return PhoneNumber.formatPhoneNumber(msisdn);
    }

}
