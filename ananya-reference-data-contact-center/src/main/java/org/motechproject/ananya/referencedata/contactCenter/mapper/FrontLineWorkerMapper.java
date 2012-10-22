package org.motechproject.ananya.referencedata.contactCenter.mapper;

import org.motechproject.ananya.referencedata.contactCenter.request.FrontLineWorkerWebRequest;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.VerificationStatus;

public class FrontLineWorkerMapper {

    public static FrontLineWorker mapFrom(FrontLineWorkerWebRequest frontLineWorkerWebRequest, FrontLineWorker existingFrontLineWorker) {
        existingFrontLineWorker.setVerificationStatus(VerificationStatus.valueOf(frontLineWorkerWebRequest.getVerificationStatus()));
        existingFrontLineWorker.setReason(frontLineWorkerWebRequest.getReason());
        return existingFrontLineWorker;
    }
}