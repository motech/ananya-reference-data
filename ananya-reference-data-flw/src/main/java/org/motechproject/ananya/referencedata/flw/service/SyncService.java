package org.motechproject.ananya.referencedata.flw.service;

import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SyncService {
    private AnanyaReferenceDataPropertiesService propertiesService;
    private LocationSyncService locationSyncService;
    private FrontLineWorkerSyncService frontLineWorkerSyncService;

    @Autowired
    public SyncService(AnanyaReferenceDataPropertiesService propertiesService, LocationSyncService locationSyncService, FrontLineWorkerSyncService frontLineWorkerSyncService) {
        this.propertiesService = propertiesService;
        this.locationSyncService = locationSyncService;
        this.frontLineWorkerSyncService = frontLineWorkerSyncService;
    }

    public void syncFrontLineWorker(final FrontLineWorker frontLineWorker) {
        if (propertiesService.isSyncOn()) {
            ArrayList<FrontLineWorker> frontLineWorkers = new ArrayList<FrontLineWorker>() {{
                add(frontLineWorker);
            }};
            frontLineWorkerSyncService.sync(frontLineWorkers);
        }
    }

    public void syncAllFrontLineWorkers(List<FrontLineWorker> frontLineWorkers) {
        if (propertiesService.isSyncOn()) {
            frontLineWorkerSyncService.sync(frontLineWorkers);
        }
    }

    public void syncAllLocations(ArrayList<Location> locations) {
        if (propertiesService.isSyncOn()) {
            locationSyncService.sync(locations);
        }
    }
}
