package org.motechproject.ananya.referencedata.flw.service;

import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.response.SyncResponse;
import org.motechproject.ananya.referencedata.flw.response.SyncResponseFlw;
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

    public List<SyncResponseFlw> syncAllFrontLineWorkers(List<FrontLineWorker> frontLineWorkers) {
    	List<SyncResponseFlw> syncResponseFlws = new ArrayList<SyncResponseFlw>();
        if (propertiesService.isSyncOn()) {
            syncResponseFlws.addAll(frontLineWorkerSyncService.sync(frontLineWorkers));
        }
        return syncResponseFlws;
    }

    public List<SyncResponse> syncLocation(Location location) {
    	List<SyncResponse> syncResponseList = new ArrayList<SyncResponse>();
        if (propertiesService.isSyncOn()) {
            syncResponseList.addAll(locationSyncService.sync(location));
        }
        return syncResponseList;
    }
}
