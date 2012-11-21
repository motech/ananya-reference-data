package org.motechproject.ananya.referencedata.flw.service;

import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.repository.AllFrontLineWorkers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FrontLineWorkerService {
    private AllFrontLineWorkers allFrontLineWorkers;

    @Autowired
    public FrontLineWorkerService(AllFrontLineWorkers allFrontLineWorkers) {
        this.allFrontLineWorkers = allFrontLineWorkers;
    }

    public void updateWithAlternateLocationForFLWsWith(Location currentLocation) {
        List<FrontLineWorker> frontLineWorkers = allFrontLineWorkers.getForLocation(currentLocation);
        for(FrontLineWorker frontLineWorker : frontLineWorkers) {
            frontLineWorker.setLocation(currentLocation.getAlternateLocation());
        }
        allFrontLineWorkers.createOrUpdateAll(frontLineWorkers);
    }
}