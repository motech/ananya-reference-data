package org.motechproject.ananya.referencedata.flw.mapper;

import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorkerSyncRequest;

public class FrontLineWorkerSyncRequestMapper {

    public static FrontLineWorkerSyncRequest mapFrom(FrontLineWorker frontLineWorker) {
        return new FrontLineWorkerSyncRequest(frontLineWorker.getMsisdn().toString(),
                frontLineWorker.getName(),
                frontLineWorker.getDesignation(),
                frontLineWorker.getLastModified(),
                LocationContractMapper.mapFrom(frontLineWorker.getLocation()),
                frontLineWorker.getFlwId().toString(),
                frontLineWorker.getVerificationStatus());
    }
}
