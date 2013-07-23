package org.motechproject.ananya.referencedata.csv.mapper;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.referencedata.csv.request.FrontLineWorkerImportRequest;
import org.motechproject.ananya.referencedata.flw.domain.Designation;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.VerificationStatus;
import org.motechproject.ananya.referencedata.flw.utils.PhoneNumber;

import java.util.UUID;

public class FrontLineWorkerImportMapper {

    public static final String FLW_CSV_UPLOAD_REASON = "via CSV Upload";

    public static FrontLineWorker mapToNewFlw(FrontLineWorkerImportRequest request, Location location) {
        Long msisdn = StringUtils.isBlank(request.getMsisdn()) ? null : formatMsisdn(request.getMsisdn());
        return new FrontLineWorker(msisdn, trim(request.getName()), Designation.from(request.getDesignation()),
                location, request.getVerificationStatus(), UUID.randomUUID(), FLW_CSV_UPLOAD_REASON);
    }

    public static FrontLineWorker mapToExistingFlw(FrontLineWorker existingFrontLineWorker, FrontLineWorkerImportRequest request, Location location) {
        existingFrontLineWorker.setName(trim(request.getName()));
        existingFrontLineWorker.setDesignation(getDesignation(request.getDesignation()));
        existingFrontLineWorker.setLocation(location);
        existingFrontLineWorker.setVerificationStatus(VerificationStatus.from(request.getVerificationStatus()));
        existingFrontLineWorker.setReason(FLW_CSV_UPLOAD_REASON);

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
