package org.motechproject.ananya.referencedata.csv.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.referencedata.csv.mapper.FrontLineWorkerImportMapper;
import org.motechproject.ananya.referencedata.csv.request.FrontLineWorkerImportRequest;
import org.motechproject.ananya.referencedata.flw.domain.FlwUuid;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.LocationFilename;
import org.motechproject.ananya.referencedata.flw.domain.UploadFlwMetaData;
import org.motechproject.ananya.referencedata.flw.repository.AllFlwUuid;
import org.motechproject.ananya.referencedata.flw.repository.AllFrontLineWorkers;
import org.motechproject.ananya.referencedata.flw.repository.AllLocations;
import org.motechproject.ananya.referencedata.flw.repository.AllUploadFlwMetaData;
import org.motechproject.ananya.referencedata.flw.repository.AllUploadLocationMetaData;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;
import org.motechproject.ananya.referencedata.flw.response.SyncResponse;
import org.motechproject.ananya.referencedata.flw.response.SyncResponseFlw;
import org.motechproject.ananya.referencedata.flw.service.SyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ibm.icu.util.Calendar;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.apache.commons.lang.StringUtils.isNotBlank;

@Service
public class FrontLineWorkerImportService {
	
	private static final String FAILED = "failed";
	private static final String PASSED = "passed";
	private static final String SEPARATOR = ",";

    private AllLocations allLocations;
    private AllFrontLineWorkers allFrontLineWorkers;
    private SyncService syncService;
    private AllUploadFlwMetaData allUploadFlwMetaData;
    private AllFlwUuid allFlwUuid;
    public FrontLineWorkerImportService() {
    }

    @Autowired
    public FrontLineWorkerImportService(AllLocations allLocations, AllFrontLineWorkers allFrontLineWorkers, SyncService syncService , AllUploadFlwMetaData allUploadFlwMetaData,AllFlwUuid allFlwUuid) {
        this.allLocations = allLocations;
        this.allFrontLineWorkers = allFrontLineWorkers;
        this.syncService = syncService;
        this.allUploadFlwMetaData=allUploadFlwMetaData;
        this.allFlwUuid=allFlwUuid;
    }

    @Transactional
    public String addAllWithoutValidations(List<FrontLineWorkerImportRequest> frontLineWorkerImportRequests) {
    	final String uuid_persist = UUID.randomUUID().toString();
    	List<FrontLineWorker> frontLineWorkers = new ArrayList<>();
        for (FrontLineWorkerImportRequest frontLineWorkerImportRequest : frontLineWorkerImportRequests) {
            LocationRequest locationRequest = frontLineWorkerImportRequest.getLocation();
            Location location = getExistingLocation(locationRequest);

            FrontLineWorker frontLineWorkerToBeSaved = constructFrontLineWorkerForBulkImport(frontLineWorkerImportRequest, location, frontLineWorkerImportRequests);

            frontLineWorkers.add(frontLineWorkerToBeSaved);
        }

        saveAllFLWToDB(frontLineWorkers, uuid_persist);
        return uuid_persist;
    }
    
    @Transactional
    public String syncflw(String uuid_flw) {
    	List<FlwUuid> flwUuids = allFlwUuid.getFor(uuid_flw);
    	List <FrontLineWorker> frontLineWorkers = new ArrayList<FrontLineWorker>();
    	for(FlwUuid flwUuid : flwUuids) {
    		int flw_id = flwUuid.getFlwId();
    		frontLineWorkers.add(allFrontLineWorkers.getForid(flw_id));
    	}
    	
        List<SyncResponseFlw> syncResponsesFlws = new ArrayList<SyncResponseFlw>();
        syncResponsesFlws.addAll(syncService.syncAllFrontLineWorkers(frontLineWorkers));
     	List<SyncResponseFlw> flwPassed = new ArrayList<SyncResponseFlw>();
    	List<SyncResponseFlw> flwFailed = new ArrayList<SyncResponseFlw>();
        for (SyncResponseFlw syncResponse : syncResponsesFlws) {
        	if(syncResponse.isResponseStatus()==true) {
    			flwPassed.add(syncResponse);
    		} 
        	if(syncResponse.isResponseStatus()==false) {
    		   flwFailed.add(syncResponse);
    		}
		}
        final String uuid = UUID.randomUUID().toString();
        Timestamp currentTimestamp = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
        int v = flwPassed.size();
        int iv = flwFailed.size();
        UploadFlwMetaData uploadFlwMetaData=new UploadFlwMetaData();
        uploadFlwMetaData.setFlwPassed(v);
        uploadFlwMetaData.setFlwFailed(iv);
        uploadFlwMetaData.setUuid(uuid);
        uploadFlwMetaData.setUploadedDate(currentTimestamp);
        allUploadFlwMetaData.add(uploadFlwMetaData);
        
        String result = null;
        if(v > 0 & iv == 0) {
        	result = PASSED+SEPARATOR+uuid;
        }else if (iv > 0) {
        	result = FAILED+SEPARATOR+uuid;
        }
		return result;
        		
    }

    private Predicate flwWithVerificationStatus() {
        return new Predicate() {
            @Override
            public boolean evaluate(Object o) {
                return isNotBlank(((FrontLineWorker) o).getVerificationStatus());
            }
        };
    }

    private Location getExistingLocation(LocationRequest locationRequest) {
        Location location = allLocations.getFor(locationRequest.getState(), locationRequest.getDistrict(), locationRequest.getBlock(), locationRequest.getPanchayat());
        return location.isInvalid() ? location.getAlternateLocation() : location;
    }

    private void saveAllFLWToDB(List<FrontLineWorker> frontLineWorkers,final String uuid_perist) {
        allFrontLineWorkers.createOrUpdateAll(frontLineWorkers);
        for (FrontLineWorker frontLineWorker : frontLineWorkers) {
        FlwUuid flwUuid = new FlwUuid();
        flwUuid.setUuid(uuid_perist);
        flwUuid.setFlwId(frontLineWorker.getId());
        allFlwUuid.add(flwUuid);
        }
    }

    public List<FrontLineWorker> getAllByMsisdn(Long msisdn) {
        return allFrontLineWorkers.getByMsisdn(msisdn);
    }

    private FrontLineWorker constructFrontLineWorkerForBulkImport(FrontLineWorkerImportRequest frontLineWorkerRequest, Location location, List<FrontLineWorkerImportRequest> frontLineWorkerRequests) {
        if (hasDuplicatesInCSV(frontLineWorkerRequest, frontLineWorkerRequests)) {
            return FrontLineWorkerImportMapper.mapToNewFlw(frontLineWorkerRequest, location);
        }
        FrontLineWorker flwToBeUpdated = flwToBeUpdated(frontLineWorkerRequest);
        if (flwToBeUpdated != null)
            return FrontLineWorkerImportMapper.mapToExistingFlw(flwToBeUpdated, frontLineWorkerRequest, location);
        return FrontLineWorkerImportMapper.mapToNewFlw(frontLineWorkerRequest, location);
    }

    private FrontLineWorker flwToBeUpdated(FrontLineWorkerImportRequest frontLineWorkerRequest) {
        List<FrontLineWorker> frontLineWorkersWithSameMsisdn = existingFLW(frontLineWorkerRequest);
        if(frontLineWorkersWithSameMsisdn.size() == 1)
            return frontLineWorkersWithSameMsisdn.get(0);
        return (FrontLineWorker) CollectionUtils.find(frontLineWorkersWithSameMsisdn, flwWithVerificationStatus());
    }

    private boolean hasDuplicatesInCSV(FrontLineWorkerImportRequest frontLineWorkerRequest, List<FrontLineWorkerImportRequest> frontLineWorkerRequests) {
        return CollectionUtils.cardinality(frontLineWorkerRequest, frontLineWorkerRequests) != 1;
    }

    private List<FrontLineWorker> existingFLW(FrontLineWorkerImportRequest frontLineWorkerRequest) {
        String msisdn = frontLineWorkerRequest.getMsisdn();
        return StringUtils.isBlank(msisdn) ? new ArrayList<FrontLineWorker>() : allFrontLineWorkers.getByMsisdn(FrontLineWorkerImportMapper.formatMsisdn(msisdn));
    }
}
