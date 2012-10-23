package org.motechproject.ananya.referencedata.contactCenter.mapper;

import org.motechproject.ananya.referencedata.contactCenter.request.FrontLineWorkerWebRequest;
import org.motechproject.ananya.referencedata.flw.domain.Designation;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.VerificationStatus;
import org.motechproject.ananya.referencedata.flw.mapper.LocationMapper;

public class FrontLineWorkerMapper {

    public static FrontLineWorker mapFrom(FrontLineWorkerWebRequest frontLineWorkerWebRequest, FrontLineWorker existingFrontLineWorker) {
        existingFrontLineWorker.setVerificationStatus(VerificationStatus.from(frontLineWorkerWebRequest.getVerificationStatus()));

        if (VerificationStatus.isSuccess(frontLineWorkerWebRequest.getVerificationStatus())) {
            existingFrontLineWorker.setName(frontLineWorkerWebRequest.getName());
            existingFrontLineWorker.setDesignation(Designation.getFor(frontLineWorkerWebRequest.getDesignation()));
            existingFrontLineWorker.setLocation(LocationMapper.mapFrom(frontLineWorkerWebRequest.getLocation()));
            existingFrontLineWorker.setReason(null);
            return existingFrontLineWorker;
        }
        existingFrontLineWorker.setReason(frontLineWorkerWebRequest.getReason());
        return existingFrontLineWorker;
    }
}