package org.motechproject.ananya.referencedata.contactCenter.service;

import org.motechproject.ananya.referencedata.contactCenter.mapper.FrontLineWorkerMapper;
import org.motechproject.ananya.referencedata.contactCenter.request.FrontLineWorkerVerificationWebRequest;
import org.motechproject.ananya.referencedata.contactCenter.validator.FrontLineWorkerRequestValidator;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.VerificationStatus;
import org.motechproject.ananya.referencedata.flw.repository.AllFrontLineWorkers;
import org.motechproject.ananya.referencedata.flw.service.SyncService;
import org.motechproject.ananya.referencedata.flw.validators.Errors;
import org.motechproject.ananya.referencedata.flw.validators.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class FrontLineWorkerContactCenterService {
    private AllFrontLineWorkers allFrontLineWorkers;
    private LocationService locationService;
    private FrontLineWorkerRequestValidator requestValidator;
    private SyncService syncService;

    public FrontLineWorkerContactCenterService() {
    }

    @Autowired
    public FrontLineWorkerContactCenterService(AllFrontLineWorkers allFrontLineWorkers, LocationService locationService, FrontLineWorkerRequestValidator requestValidator, SyncService syncService) {
        this.allFrontLineWorkers = allFrontLineWorkers;
        this.locationService = locationService;
        this.requestValidator = requestValidator;
        this.syncService = syncService;
    }

    @Transactional
    public void updateVerifiedFlw(FrontLineWorkerVerificationWebRequest webRequest) {
        FrontLineWorkerVerificationRequest request = webRequest.getVerificationRequest();
        updateVerifiedFlw(request);
    }

    private void updateVerifiedFlw(FrontLineWorkerVerificationRequest request) {
        Errors validationErrors = requestValidator.validate(request);
        raiseExceptionIfThereAreErrors(validationErrors);
        saveAndSync(request);
    }

    private void saveAndSync(FrontLineWorkerVerificationRequest request) {
        FrontLineWorker updatedFrontLineWorker = constructFrontLineWorker(request);
        allFrontLineWorkers.createOrUpdate(updatedFrontLineWorker);
        syncService.syncFrontLineWorker(updatedFrontLineWorker);
    }

    private FrontLineWorker constructFrontLineWorker(FrontLineWorkerVerificationRequest request) {
        FrontLineWorker frontLineWorker = getFrontLineWorker(request);
        FrontLineWorker updatedFrontLineWorker = FrontLineWorkerMapper.mapFrom(request, frontLineWorker);
        if (VerificationStatus.SUCCESS == request.getVerificationStatus()) {
            Location location = locationService.createAndFetch(request.getLocation());
            updatedFrontLineWorker = FrontLineWorkerMapper.mapSuccessfulRegistration(frontLineWorker, location);
        }
        return updatedFrontLineWorker;
    }

    private FrontLineWorker getFrontLineWorker(FrontLineWorkerVerificationRequest request) {
        UUID flwId = request.getFlwId();
        FrontLineWorker frontLineWorker = allFrontLineWorkers.getByFlwId(flwId);
        if (request.isDummyFlwId()) {
            List<FrontLineWorker> frontLineWorkers = allFrontLineWorkers.getByMsisdn(request.getMsisdn());
            frontLineWorker = frontLineWorkers.size() == 1 ? frontLineWorkers.get(0) : new FrontLineWorker(UUID.randomUUID());
        } else if (frontLineWorker == null) {
            frontLineWorker = new FrontLineWorker(flwId);
        } else
            validateMsisdnMatch(request.getMsisdn(), frontLineWorker.getMsisdn());
        return frontLineWorker;
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