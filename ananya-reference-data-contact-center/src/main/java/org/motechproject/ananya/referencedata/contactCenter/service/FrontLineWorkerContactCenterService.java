package org.motechproject.ananya.referencedata.contactCenter.service;

import org.motechproject.ananya.referencedata.contactCenter.mapper.FrontLineWorkerMapper;
import org.motechproject.ananya.referencedata.contactCenter.request.FrontLineWorkerVerificationWebRequest;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.VerificationStatus;
import org.motechproject.ananya.referencedata.flw.repository.AllFrontLineWorkers;
import org.motechproject.ananya.referencedata.flw.validators.Errors;
import org.motechproject.ananya.referencedata.flw.validators.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class FrontLineWorkerContactCenterService {
    private AllFrontLineWorkers allFrontLineWorkers;
    private LocationService locationService;

    @Autowired
    public FrontLineWorkerContactCenterService(AllFrontLineWorkers allFrontLineWorkers, LocationService locationService) {
        this.allFrontLineWorkers = allFrontLineWorkers;
        this.locationService = locationService;
    }

    public void updateVerifiedFlw(FrontLineWorkerVerificationWebRequest webRequest) {
        FrontLineWorkerVerificationRequest request = webRequest.getVerificationRequest();
        updateVerifiedFlw(request);
    }

    private void updateVerifiedFlw(FrontLineWorkerVerificationRequest request) {
        Errors validationErrors = request.validate();
        raiseExceptionIfThereAreErrors(validationErrors);

        UUID flwId = request.getFlwId();
        FrontLineWorker frontLineWorker = allFrontLineWorkers.getByFlwId(flwId);
        if (frontLineWorker == null)
            frontLineWorker = new FrontLineWorker(flwId);
        else
            validateMsisdnMatch(request.getMsisdn(), frontLineWorker.getMsisdn());
        FrontLineWorker updatedFrontLineWorker = FrontLineWorkerMapper.mapFrom(request, frontLineWorker);
        if (VerificationStatus.SUCCESS == request.getVerificationStatus()) {
            Location location = locationService.handleLocation(request.getLocation());
            updatedFrontLineWorker = FrontLineWorkerMapper.mapSuccessfulRegistration(frontLineWorker, location);
        }
        allFrontLineWorkers.createOrUpdate(updatedFrontLineWorker);
    }

    private void validateMsisdnMatch(Long requestMsisdn, Long existingMsisdn) {
        if (!requestMsisdn.equals(existingMsisdn))
            throw new ValidationException(String.format("Given msisdn %s does not match existing msisdn %s for the given id.", requestMsisdn, existingMsisdn));
    }

    private void raiseExceptionIfThereAreErrors(Errors validationErrors) {
        if (validationErrors.hasErrors()) {
            throw new ValidationException(validationErrors.allMessages());
        }
    }
}