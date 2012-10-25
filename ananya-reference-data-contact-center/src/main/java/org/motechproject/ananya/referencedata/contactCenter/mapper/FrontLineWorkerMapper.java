package org.motechproject.ananya.referencedata.contactCenter.mapper;

import org.motechproject.ananya.referencedata.contactCenter.request.FrontLineWorkerWebRequest;
import org.motechproject.ananya.referencedata.flw.domain.Designation;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.VerificationStatus;

public class FrontLineWorkerMapper {

    public static FrontLineWorker mapSuccessfulRegistration(FrontLineWorker existingFrontLineWorker, Location location) {
        existingFrontLineWorker.setLocation(location);
        return existingFrontLineWorker;
    }

    public static FrontLineWorker mapFrom(FrontLineWorkerWebRequest frontLineWorkerWebRequest, FrontLineWorker existingFrontLineWorker) {
        existingFrontLineWorker.setMsisdn(Long.parseLong(frontLineWorkerWebRequest.getMsisdn()));
        existingFrontLineWorker.setVerificationStatus(VerificationStatus.from(frontLineWorkerWebRequest.getVerificationStatus()));
        existingFrontLineWorker.setReason(frontLineWorkerWebRequest.getReason());
        existingFrontLineWorker.setName(frontLineWorkerWebRequest.getName());
        existingFrontLineWorker.setDesignation(Designation.getFor(frontLineWorkerWebRequest.getDesignation()));
        return existingFrontLineWorker;
    }
}