package org.motechproject.ananya.referencedata.contactCenter.service;

import org.motechproject.ananya.referencedata.contactCenter.mapper.FrontLineWorkerMapper;
import org.motechproject.ananya.referencedata.contactCenter.request.FrontLineWorkerWebRequest;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.VerificationStatus;
import org.motechproject.ananya.referencedata.flw.repository.AllFrontLineWorkers;
import org.motechproject.ananya.referencedata.flw.validators.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FrontLineWorkerService {
    private AllFrontLineWorkers allFrontLineWorkers;
    private LocationService locationService;

    @Autowired
    public FrontLineWorkerService(AllFrontLineWorkers allFrontLineWorkers, LocationService locationService) {
        this.allFrontLineWorkers = allFrontLineWorkers;
        this.locationService = locationService;
    }

    public void updateVerifiedFlw(FrontLineWorkerWebRequest webRequest) {
        FrontLineWorker frontLineWorker = allFrontLineWorkers.getByFlwId(webRequest.getFlwId());
        if (frontLineWorker == null)
            frontLineWorker = new FrontLineWorker(webRequest.getFlwId());
        else
            validateMsisdnMatch(webRequest.getMsisdn(), frontLineWorker.getMsisdn());
        FrontLineWorker updatedFrontLineWorker = FrontLineWorkerMapper.mapFrom(webRequest, frontLineWorker);
        if (VerificationStatus.isSuccess(webRequest.getVerificationStatus())) {
            Location location = locationService.handleLocation(webRequest.getLocation());

            updatedFrontLineWorker = FrontLineWorkerMapper.mapSuccessfulRegistration(frontLineWorker, location);
        }
        allFrontLineWorkers.createOrUpdate(updatedFrontLineWorker);
    }

    private void validateMsisdnMatch(String requestMsisdn, Long existingMsisdn) {
        if (!requestMsisdn.equals(existingMsisdn.toString()))
            throw new ValidationException(String.format("Given msisdn %s does not match existing msisdn %s for the given id.", requestMsisdn, existingMsisdn));
    }
}