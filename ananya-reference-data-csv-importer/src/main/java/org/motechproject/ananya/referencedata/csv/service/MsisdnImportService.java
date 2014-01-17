package org.motechproject.ananya.referencedata.csv.service;

import org.motechproject.ananya.referencedata.csv.importer.MsisdnImporter;
import org.motechproject.ananya.referencedata.csv.request.MsisdnImportRequest;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.NewMsisdn;
import org.motechproject.ananya.referencedata.flw.repository.AllFrontLineWorkers;
import org.motechproject.ananya.referencedata.flw.service.SyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class MsisdnImportService {
    private static Logger logger = LoggerFactory.getLogger(MsisdnImporter.class);

    private AllFrontLineWorkers allFrontLineWorkers;
    private SyncService syncService;

    public MsisdnImportService() {
    }

    @Autowired
    public MsisdnImportService(AllFrontLineWorkers allFrontLineWorkers, SyncService syncService) {
        this.allFrontLineWorkers = allFrontLineWorkers;
        this.syncService = syncService;
    }

    @Transactional
    public void updateFLWContactDetailsWithoutValidations(List<MsisdnImportRequest> msisdnImportRequests) {
        List<FrontLineWorker> frontLineWorkersToUpdate = new ArrayList<>();
        List<FrontLineWorker> frontLineWorkersToSync = new ArrayList<>();
        for (MsisdnImportRequest msisdnImportRequest : msisdnImportRequests) {
            FrontLineWorker frontLineWorker = allFrontLineWorkers.getByMsisdn(msisdnImportRequest.msisdnAsLong()).get(0);
            updateAlternateContactNumber(frontLineWorker, msisdnImportRequest);
            FrontLineWorker frontLineWorkerToSync = changeMsisdnAndGetFLWToSync(frontLineWorker, msisdnImportRequest);

            frontLineWorkersToUpdate.add(frontLineWorker);
            frontLineWorkersToSync.add(frontLineWorkerToSync);
        }
        allFrontLineWorkers.createOrUpdateAll(frontLineWorkersToUpdate);
        syncService.syncAllFrontLineWorkers(frontLineWorkersToSync);
    }

    private FrontLineWorker changeMsisdnAndGetFLWToSync(FrontLineWorker frontLineWorker, MsisdnImportRequest request) {
        if (!request.isChangeMsisdn()) {
            return frontLineWorker;
        }
        allFrontLineWorkers.deleteByMsisdn(request.newMsisdnAsLong());

        frontLineWorker.setNewMsisdn(new NewMsisdn(request.getNewMsisdn(), frontLineWorker.getFlwId().toString()));
        FrontLineWorker frontLineWorkerToSync = frontLineWorker.clone();
        frontLineWorker.updateToNewMsisdn();
        return frontLineWorkerToSync;
    }

    private void updateAlternateContactNumber(FrontLineWorker frontLineWorker, MsisdnImportRequest request) {
        if (!request.isUpdateAlternateContactNumber()) {
            return;
        }
        frontLineWorker.setAlternateContactNumber(request.alternateContactNumberAsLong());
    }
}
