package org.motechproject.ananya.referencedata.csv.service;

import org.motechproject.ananya.referencedata.csv.importer.MsisdnImporter;
import org.motechproject.ananya.referencedata.csv.request.MsisdnImportRequest;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.NewMsisdn;
import org.motechproject.ananya.referencedata.flw.repository.AllFrontLineWorkers;
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

    public MsisdnImportService() {
    }

    @Autowired
    public MsisdnImportService(AllFrontLineWorkers allFrontLineWorkers) {
        this.allFrontLineWorkers = allFrontLineWorkers;
    }

    @Transactional
    public void updateFLWContactDetailsWithoutValidations(List<MsisdnImportRequest> msisdnImportRequests) {
        List<FrontLineWorker> frontLineWorkersToUpdate = new ArrayList<>();
        for (MsisdnImportRequest msisdnImportRequest : msisdnImportRequests) {
            FrontLineWorker frontLineWorker = allFrontLineWorkers.getByMsisdn(msisdnImportRequest.msisdnAsLong()).get(0);
            updateNewMsisdn(frontLineWorker, msisdnImportRequest);

            frontLineWorkersToUpdate.add(frontLineWorker);
        }
        allFrontLineWorkers.createOrUpdateAll(frontLineWorkersToUpdate);
    }

    private void updateNewMsisdn(FrontLineWorker frontLineWorker, MsisdnImportRequest request) {
        if (!request.isChangeMsisdn()) {
            return;
        }
        allFrontLineWorkers.deleteByMsisdn(request.newMsisdnAsLong());

        frontLineWorker.setNewMsisdn(new NewMsisdn(request.getNewMsisdn(), frontLineWorker.getFlwId().toString()));
        frontLineWorker.updateToNewMsisdn();
    }
}
