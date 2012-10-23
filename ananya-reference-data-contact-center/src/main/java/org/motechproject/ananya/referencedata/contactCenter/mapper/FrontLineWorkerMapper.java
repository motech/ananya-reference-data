package org.motechproject.ananya.referencedata.contactCenter.mapper;

import org.motechproject.ananya.referencedata.contactCenter.request.FrontLineWorkerWebRequest;
import org.motechproject.ananya.referencedata.flw.domain.Designation;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.VerificationStatus;

public class FrontLineWorkerMapper {

    public static FrontLineWorker mapSuccessfulRegistration(FrontLineWorkerWebRequest frontLineWorkerWebRequest, FrontLineWorker existingFrontLineWorker, Location location) {
        existingFrontLineWorker.setVerificationStatus(VerificationStatus.from(frontLineWorkerWebRequest.getVerificationStatus()));
        existingFrontLineWorker.setName(frontLineWorkerWebRequest.getName());
        existingFrontLineWorker.setDesignation(Designation.getFor(frontLineWorkerWebRequest.getDesignation()));
        existingFrontLineWorker.setLocation(location);
        existingFrontLineWorker.setReason(null);
        return existingFrontLineWorker;
    }

    public static FrontLineWorker mapUnsuccessfulRegistration(FrontLineWorkerWebRequest frontLineWorkerWebRequest, FrontLineWorker existingFrontLineWorker) {
        existingFrontLineWorker.setVerificationStatus(VerificationStatus.from(frontLineWorkerWebRequest.getVerificationStatus()));
        existingFrontLineWorker.setReason(frontLineWorkerWebRequest.getReason());
        return existingFrontLineWorker;
    }
}