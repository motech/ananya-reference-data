package org.motechproject.ananya.referencedata.flw.mapper;

import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorkerContract;

import java.util.Date;

public class FrontLineWorkerContractMapper {

    public static FrontLineWorkerContract mapFrom(FrontLineWorker frontLineWorker) {
        return new FrontLineWorkerContract(frontLineWorker.getMsisdn().toString(), frontLineWorker.getName(), frontLineWorker.getDesignation(), new Date(frontLineWorker.getLastModified().getMillis()), LocationContractMapper.mapFrom(frontLineWorker.getLocation()), frontLineWorker.getFlwid());
    }
}
