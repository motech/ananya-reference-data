package org.motechproject.ananya.referencedata.contactCenter.service;

import org.motechproject.ananya.referencedata.contactCenter.mapper.FrontLineWorkerMapper;
import org.motechproject.ananya.referencedata.contactCenter.request.FrontLineWorkerWebRequest;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.repository.AllFrontLineWorkers;
import org.motechproject.ananya.referencedata.flw.validators.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FrontLineWorkerService {
    private AllFrontLineWorkers allFrontLineWorkers;

    @Autowired
    public FrontLineWorkerService(AllFrontLineWorkers allFrontLineWorkers) {
        this.allFrontLineWorkers = allFrontLineWorkers;
    }

    public void updateVerifiedFlw(FrontLineWorkerWebRequest frontLineWorkerWebRequest) {
        FrontLineWorker existingFrontLineWorker = allFrontLineWorkers.getByFlwId(frontLineWorkerWebRequest.getFlwId());
        if(existingFrontLineWorker == null)
            throw new ValidationException("FLW-Id is not present in MoTeCH");
        allFrontLineWorkers.update(FrontLineWorkerMapper.mapFrom(frontLineWorkerWebRequest, existingFrontLineWorker));
    }
}
