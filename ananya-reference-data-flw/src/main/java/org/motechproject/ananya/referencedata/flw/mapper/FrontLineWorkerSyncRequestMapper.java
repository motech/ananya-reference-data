package org.motechproject.ananya.referencedata.flw.mapper;

import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorkerSyncRequest;

import java.util.Date;

public class FrontLineWorkerSyncRequestMapper {

    public static FrontLineWorkerSyncRequest mapFrom(FrontLineWorker frontLineWorker) {
        return new FrontLineWorkerSyncRequest(frontLineWorker.getMsisdn().toString(),
                frontLineWorker.getName(),
                frontLineWorker.getDesignation(),
                new Date(frontLineWorker.getLastModified().getMillis()),
                LocationContractMapper.mapFrom(frontLineWorker.getLocation()),
                frontLineWorker.getFlwId().toString(),
                frontLineWorker.getVerificationStatus());
    }
}
