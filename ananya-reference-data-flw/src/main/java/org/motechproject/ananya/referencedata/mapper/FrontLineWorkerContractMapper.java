package org.motechproject.ananya.referencedata.mapper;

import org.motechproject.ananya.referencedata.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.domain.FrontLineWorkerContract;

public class FrontLineWorkerContractMapper {

    public static FrontLineWorkerContract mapFrom(FrontLineWorker frontLineWorker) {
        return new FrontLineWorkerContract(frontLineWorker.getMsisdn().toString(), frontLineWorker.getName(), frontLineWorker.getDesignation(), LocationContractMapper.mapFrom(frontLineWorker.getLocation()));
    }
}
