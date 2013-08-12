package org.motechproject.ananya.referencedata.contactCenter.mapper;

import org.motechproject.ananya.referencedata.contactCenter.service.FrontLineWorkerVerificationRequest;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.NewMsisdn;
import org.motechproject.ananya.referencedata.flw.request.ChangeMsisdnRequest;

public class FrontLineWorkerMapper {

    public static FrontLineWorker mapSuccessfulRegistration(FrontLineWorkerVerificationRequest request, FrontLineWorker existingFrontLineWorker, Location location) {
        existingFrontLineWorker.setLocation(location);
        existingFrontLineWorker.setName(request.getName());
        existingFrontLineWorker.setDesignation(request.getDesignation());
        existingFrontLineWorker.setAlternateContactNumber(request.getAlternateContactNumber());
        ChangeMsisdnRequest changeMsisdnRequest = request.getChangeMsisdnRequest();
        if (request.hasMsisdnChange())
            existingFrontLineWorker.setNewMsisdn(new NewMsisdn(changeMsisdnRequest.getMsisdn(), changeMsisdnRequest.getFlwId()));
        return existingFrontLineWorker;
    }

    public static FrontLineWorker mapFrom(FrontLineWorkerVerificationRequest frontLineWorkerVerificationRequest, FrontLineWorker existingFrontLineWorker) {
        existingFrontLineWorker.setMsisdn(frontLineWorkerVerificationRequest.getMsisdn());
        existingFrontLineWorker.setVerificationStatus(frontLineWorkerVerificationRequest.getVerificationStatus());
        existingFrontLineWorker.setReason(frontLineWorkerVerificationRequest.getReason());
        return existingFrontLineWorker;
    }
}