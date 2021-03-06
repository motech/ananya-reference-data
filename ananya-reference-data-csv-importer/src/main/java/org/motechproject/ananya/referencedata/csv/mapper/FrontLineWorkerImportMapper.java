package org.motechproject.ananya.referencedata.csv.mapper;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.referencedata.csv.request.FrontLineWorkerImportRequest;
import org.motechproject.ananya.referencedata.flw.domain.Designation;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.VerificationStatus;
import org.motechproject.ananya.referencedata.flw.utils.PhoneNumber;

import java.util.UUID;

import static org.apache.commons.lang.StringUtils.*;

public class FrontLineWorkerImportMapper {

    public static final String FLW_CSV_UPLOAD_REASON = "via CSV Upload";

    public static FrontLineWorker mapToNewFlw(FrontLineWorkerImportRequest request, Location location) {
        Long msisdn = isBlank(request.getMsisdn()) ? null : formatMsisdn(request.getMsisdn());
        Long alternateContactNumber = isBlank(request.getAlternateContactNumber()) ? null : formatMsisdn(request.getAlternateContactNumber());
        String verificationStatus = isBlank(request.getVerificationStatus())? null :request.getVerificationStatus();
        return new FrontLineWorker(msisdn, alternateContactNumber, trim(request.getName()), Designation.from(request.getDesignation()),
                location, verificationStatus, UUID.randomUUID(), FLW_CSV_UPLOAD_REASON);
    }

    public static FrontLineWorker mapToExistingFlw(FrontLineWorker existingFrontLineWorker, FrontLineWorkerImportRequest request, Location location) {
        if(isNotBlank(request.getAlternateContactNumber()))
            existingFrontLineWorker.setAlternateContactNumber(formatMsisdn(request.getAlternateContactNumber()));
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
        return defaultIfEmpty(strip(name), EMPTY);
    }

    public static Long formatMsisdn(String msisdn) {
        return PhoneNumber.formatPhoneNumber(msisdn);
    }

}
