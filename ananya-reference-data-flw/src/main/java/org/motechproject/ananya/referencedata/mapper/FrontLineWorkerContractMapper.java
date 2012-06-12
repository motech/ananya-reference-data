package org.motechproject.ananya.referencedata.mapper;

import org.motechproject.ananya.referencedata.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.domain.FrontLineWorkerContract;

import java.util.Date;

public class FrontLineWorkerContractMapper {

    public static FrontLineWorkerContract mapFrom(FrontLineWorker frontLineWorker) {
        return new FrontLineWorkerContract(frontLineWorker.getMsisdn().toString(), frontLineWorker.getName(), frontLineWorker.getDesignation(), new Date(frontLineWorker.getLastModified().getMillis()), LocationContractMapper.mapFrom(frontLineWorker.getLocation()));
    }
}
