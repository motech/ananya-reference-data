package org.motechproject.ananya.referencedata.contactCenter.mapper;

import org.motechproject.ananya.referencedata.contactCenter.request.FrontLineWorkerWebRequest;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;

public class FrontLineWorkerMapper {

    public static FrontLineWorker mapFrom(FrontLineWorkerWebRequest frontLineWorkerWebRequest, FrontLineWorker existingFrontLineWorker) {
        existingFrontLineWorker.setVerificationStatus(frontLineWorkerWebRequest.getVerificationStatus());
        existingFrontLineWorker.setReason(frontLineWorkerWebRequest.getReason());
        return existingFrontLineWorker;
    }
}
