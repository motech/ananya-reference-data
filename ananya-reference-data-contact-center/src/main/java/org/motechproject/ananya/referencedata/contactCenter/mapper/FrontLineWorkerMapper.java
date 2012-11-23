package org.motechproject.ananya.referencedata.contactCenter.mapper;

import org.motechproject.ananya.referencedata.contactCenter.service.FrontLineWorkerVerificationRequest;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.Location;

public class FrontLineWorkerMapper {

    public static FrontLineWorker mapSuccessfulRegistration(FrontLineWorkerVerificationRequest frontLineWorkerVerificationRequest, FrontLineWorker existingFrontLineWorker, Location location) {
        existingFrontLineWorker.setLocation(location);
        existingFrontLineWorker.setName(frontLineWorkerVerificationRequest.getName());
        existingFrontLineWorker.setDesignation(frontLineWorkerVerificationRequest.getDesignation());
        return existingFrontLineWorker;
    }

    public static FrontLineWorker mapFrom(FrontLineWorkerVerificationRequest frontLineWorkerVerificationRequest, FrontLineWorker existingFrontLineWorker) {
        existingFrontLineWorker.setMsisdn(frontLineWorkerVerificationRequest.getMsisdn());
        existingFrontLineWorker.setVerificationStatus(frontLineWorkerVerificationRequest.getVerificationStatus());
        existingFrontLineWorker.setReason(frontLineWorkerVerificationRequest.getReason());
        return existingFrontLineWorker;
    }
}