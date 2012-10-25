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

    public void updateVerifiedFlw(FrontLineWorkerWebRequest frontLineWorkerWebRequest) {
        FrontLineWorker existingFrontLineWorker = allFrontLineWorkers.getByFlwId(frontLineWorkerWebRequest.getFlwId());
        if (existingFrontLineWorker == null)
            throw new ValidationException("FLW with given [id] is not present in MoTeCH");

        FrontLineWorker frontLineWorker = FrontLineWorkerMapper.mapFrom(frontLineWorkerWebRequest, existingFrontLineWorker);
        if (VerificationStatus.isSuccess(frontLineWorkerWebRequest.getVerificationStatus())) {
            Location location = locationService.handleLocation(frontLineWorkerWebRequest.getLocation());
            frontLineWorker = FrontLineWorkerMapper.mapSuccessfulRegistration(existingFrontLineWorker, location);
        }
        allFrontLineWorkers.update(frontLineWorker);
    }
}