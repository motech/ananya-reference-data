package org.motechproject.ananya.referencedata.csv.service;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.Predicate;
import org.motechproject.ananya.referencedata.csv.request.LocationImportCSVRequest;
import org.motechproject.ananya.referencedata.csv.utils.CollectionUtils;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.LocationFilename;
import org.motechproject.ananya.referencedata.flw.domain.LocationStatus;
import org.motechproject.ananya.referencedata.flw.domain.UploadLocationMetaData;
import org.motechproject.ananya.referencedata.flw.repository.AllLocationFilename;
import org.motechproject.ananya.referencedata.flw.repository.AllLocations;
import org.motechproject.ananya.referencedata.flw.repository.AllUploadLocationMetaData;
import org.motechproject.ananya.referencedata.flw.response.SyncResponse;
import org.motechproject.ananya.referencedata.flw.service.FrontLineWorkerService;
import org.motechproject.ananya.referencedata.flw.service.SyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ibm.icu.util.Calendar;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class LocationImportService {
	
	private static final String FAILED = "failed";
	private static final String PASSED = "passed";
	private static final String SEPARATOR = ",";
	
    private AllLocations allLocations;
    private FrontLineWorkerService frontLineWorkerService;
    private SyncService syncService;
    private AllUploadLocationMetaData allUploadMetaData;
    private AllLocationFilename allLocationFilename;
   

    public LocationImportService() {
    }

    @Autowired
    public LocationImportService(AllLocations allLocations,
                                 FrontLineWorkerService frontLineWorkerService,
                                 SyncService syncService,
                                 AllUploadLocationMetaData allUploadMetaData,
                                 AllLocationFilename allLocationFilename
                                ) {
        this.allLocations = allLocations;
        this.frontLineWorkerService = frontLineWorkerService;
        this.syncService = syncService;
        this.allUploadMetaData = allUploadMetaData;
        this.allLocationFilename = allLocationFilename;
    }

    @Cacheable(value = "locationSearchCache")
    public Location getFor(String state, String district, String block, String panchayat) {
        return allLocations.getFor(state, district, block, panchayat);
    }

    @CacheEvict(value = "locationSearchCache", allEntries = true)
    public void invalidateCache() {
    }

    @Transactional
    public String  addAllWithoutValidations(List<LocationImportCSVRequest> locationImportCSVRequests) {
        
    	final String uuid_persist = UUID.randomUUID().toString();
    	
    	processNewLocationRequests(locationImportCSVRequests,uuid_persist);

    	processValidAndInReviewLocationsRequests(locationImportCSVRequests,uuid_persist);

        processInvalidatingLocationRequests(locationImportCSVRequests,uuid_persist);
    	
    	return uuid_persist;
 
    }
    

		
    	
    

    private void processNewLocationRequests(List<LocationImportCSVRequest> locationImportCSVRequests,final String uuid_perist) {
    	CollectionUtils.forAllDo(locationImportCSVRequests,
                hasStatus(Arrays.asList(LocationStatus.NEW)), new Closure() {
            @Override
            public void execute(Object input) {
                LocationImportCSVRequest csvRequest = (LocationImportCSVRequest) input;

                Location location = new Location(
                        csvRequest.getState(), csvRequest.getDistrict(),
                        csvRequest.getBlock(),
                        csvRequest.getPanchayat(),
                        LocationStatus.VALID,
                        null);
                allLocations.add(location);
                LocationFilename locationFilename = new LocationFilename();
                locationFilename.setUuid(uuid_perist);
                locationFilename.setLocationId(location.getId());
                allLocationFilename.add(locationFilename);
                
            }
        }
        );
    }
    
    
    private void processValidAndInReviewLocationsRequests(List<LocationImportCSVRequest> locationImportCSVRequests,final String uuid_perist) {
        CollectionUtils.forAllDo(locationImportCSVRequests,
                hasStatus(Arrays.asList(LocationStatus.VALID, LocationStatus.IN_REVIEW)), new Closure() {
            @Override
            public void execute(Object input) {
                LocationImportCSVRequest csvRequest = (LocationImportCSVRequest) input;

                Location updatedLocation = allLocations.getFor(
                        csvRequest.getState(), csvRequest.getDistrict(),
                        csvRequest.getBlock(),
                        csvRequest.getPanchayat()
                );

                Location unchangedLocationInDb = updatedLocation.clone();
                updatedLocation.setStatus(LocationStatus.from(csvRequest.getStatus()));
                if (doesIdenticalLocationExistsInDB(unchangedLocationInDb, updatedLocation)) return;

                allLocations.update(updatedLocation);
                LocationFilename locationFilename = new LocationFilename();
                locationFilename.setUuid(uuid_perist);
                locationFilename.setLocationId(updatedLocation.getId());
                allLocationFilename.add(locationFilename);
                
            }
        }
        );
		
    }
    
    
    private void processInvalidatingLocationRequests(List<LocationImportCSVRequest> locationImportCSVRequests,final String uuid_perist) {
      
 	   CollectionUtils.forAllDo(locationImportCSVRequests,
             hasStatus(Arrays.asList(LocationStatus.INVALID)), new Closure() {
         @Override
         public void execute(Object input) {
             LocationImportCSVRequest csvRequest = (LocationImportCSVRequest) input;


             Location updatedInvalidLocation = allLocations.getFor(
                     csvRequest.getState(), csvRequest.getDistrict(),
                     csvRequest.getBlock(),
                     csvRequest.getPanchayat()
             );
             Location validLocationFromDb = allLocations.getFor(
                     csvRequest.getNewState(), csvRequest.getNewDistrict(),
                     csvRequest.getNewBlock(),
                     csvRequest.getNewPanchayat()
             );

             Location unchangedInvalidLocationFromDb = updatedInvalidLocation.clone();

             updatedInvalidLocation.setStatus(LocationStatus.from(csvRequest.getStatus()));
             updatedInvalidLocation.setAlternateLocation(validLocationFromDb);

             if(doesIdenticalLocationExistsInDB(unchangedInvalidLocationFromDb, updatedInvalidLocation)) return;

             allLocations.update(updatedInvalidLocation);
             frontLineWorkerService.updateWithAlternateLocationForFLWsWith(updatedInvalidLocation);
             LocationFilename locationFilename = new LocationFilename();
             locationFilename.setUuid(uuid_perist);
             locationFilename.setLocationId(updatedInvalidLocation.getId());
             allLocationFilename.add(locationFilename);
         }
     }
     );
		
 }
    	

    
    
    @Transactional
    public String syncAllLocations(String uuid_loc){
    	List<SyncResponse> syncResponses = new ArrayList<SyncResponse>();
    	
    	List<LocationFilename> locationFilenames = allLocationFilename.getFor(uuid_loc);
    	List <Location> locations = new ArrayList<Location>();
    	for(LocationFilename locationFilename : locationFilenames) {
    		int loc_id = locationFilename.getLocationId();
    		locations.add(allLocations.getForid(loc_id));
    	}
    	
    	for (Location location : locations) {
    		
    		syncResponses.addAll(syncService.syncLocation(location));
			
		}
    	/**syncResponses.addAll(processNewLocationRequestsSync(locationImportCSVRequests));
    	syncResponses.addAll(processInvalidatingLocationRequestsSync(locationImportCSVRequests));
    	syncResponses.addAll(processValidAndInReviewLocationsRequestsSync(locationImportCSVRequests));
    	*/
    	
    	List<SyncResponse> failedInvalid = new ArrayList<SyncResponse>();
    	List<SyncResponse> failedValid = new ArrayList<SyncResponse>();
    	List<SyncResponse> passedInvalid = new ArrayList<SyncResponse>();
    	List<SyncResponse> passedValid = new ArrayList<SyncResponse>();
    	for(SyncResponse syncResponse : syncResponses) {
    		if(syncResponse.isResponseStatus()==false & syncResponse.getStatus().equals("VALID")) {
    			failedValid.add(syncResponse);
    		} 
    		if(syncResponse.isResponseStatus()==false & syncResponse.getStatus().equals("INVALID")) {
    			failedInvalid.add(syncResponse);
    		} 
    		if(syncResponse.isResponseStatus()==true & syncResponse.getStatus().equals("VALID")) {
    			passedValid.add(syncResponse);
    		} 
    		if(syncResponse.isResponseStatus()==true & syncResponse.getStatus().equals("INVALID")) {
    			passedInvalid.add(syncResponse);
    		}
    	}
    	int fv=failedValid.size();
    	int fi=failedInvalid.size();
    	int pv=passedValid.size();
    	int pi=passedInvalid.size();
    	Timestamp currentTimestamp = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
    	
    	final String uuid = UUID.randomUUID().toString();
    	UploadLocationMetaData uploadMetaData = new UploadLocationMetaData();
    	uploadMetaData.setUuid(uuid);
    	uploadMetaData.setFailedInvalid(fi);
    	uploadMetaData.setFailedValid(fv);
    	uploadMetaData.setPassedInvalid(pi);
    	uploadMetaData.setPassedValid(pv);
    	uploadMetaData.setUploadedDate(currentTimestamp);
    	
    	allUploadMetaData.add(uploadMetaData);
    	
    	if(fv > 0 | fi >0 ) {
    		return FAILED+SEPARATOR+uuid;
    	} else if (pv > 0 | pi > 0){
			return PASSED+SEPARATOR+uuid;
		} else {
			return FAILED+SEPARATOR+uuid;
		}
			
    }

    private List<SyncResponse> processNewLocationRequestsSync(List<LocationImportCSVRequest> locationImportCSVRequests) {
    	final List<SyncResponse> syncResponses = new ArrayList<SyncResponse>();
    	CollectionUtils.forAllDo(locationImportCSVRequests,
                hasStatus(Arrays.asList(LocationStatus.NEW)), new Closure() {
            @Override
            public void execute(Object input) {
                LocationImportCSVRequest csvRequest = (LocationImportCSVRequest) input;

                Location location = new Location(
                        csvRequest.getState(), csvRequest.getDistrict(),
                        csvRequest.getBlock(),
                        csvRequest.getPanchayat(),
                        LocationStatus.VALID,
                        null);
                  syncResponses.addAll(syncService.syncLocation(location));
            }
        }
        );
		return syncResponses;
    }
    
    
    
    private List<SyncResponse> processValidAndInReviewLocationsRequestsSync(List<LocationImportCSVRequest> locationImportCSVRequests) {
    	final List<SyncResponse> syncResponses = new ArrayList<SyncResponse>();
        CollectionUtils.forAllDo(locationImportCSVRequests,
                hasStatus(Arrays.asList(LocationStatus.VALID, LocationStatus.IN_REVIEW)), new Closure() {
            @Override
            public void execute(Object input) {
                LocationImportCSVRequest csvRequest = (LocationImportCSVRequest) input;

                Location updatedLocation = allLocations.getFor(
                        csvRequest.getState(), csvRequest.getDistrict(),
                        csvRequest.getBlock(),
                        csvRequest.getPanchayat()
                );

                Location unchangedLocationInDb = updatedLocation.clone();
                updatedLocation.setStatus(LocationStatus.from(csvRequest.getStatus()));
                if (doesIdenticalLocationExistsInDB(unchangedLocationInDb, updatedLocation)) return;

                
                syncResponses.addAll(syncService.syncLocation(updatedLocation));
            }
        }
        );
		return syncResponses;
    }
     
       
       private List<SyncResponse> processInvalidatingLocationRequestsSync(List<LocationImportCSVRequest> locationImportCSVRequests) {
    	   final List<SyncResponse> syncResponses = new ArrayList<SyncResponse>();
           final List<Location> locations = new ArrayList<Location>();
    	   CollectionUtils.forAllDo(locationImportCSVRequests,
                hasStatus(Arrays.asList(LocationStatus.INVALID)), new Closure() {
            @Override
            public void execute(Object input) {
                LocationImportCSVRequest csvRequest = (LocationImportCSVRequest) input;


                Location updatedInvalidLocation = allLocations.getFor(
                        csvRequest.getState(), csvRequest.getDistrict(),
                        csvRequest.getBlock(),
                        csvRequest.getPanchayat()
                );
                Location validLocationFromDb = allLocations.getFor(
                        csvRequest.getNewState(), csvRequest.getNewDistrict(),
                        csvRequest.getNewBlock(),
                        csvRequest.getNewPanchayat()
                );

                Location unchangedInvalidLocationFromDb = updatedInvalidLocation.clone();

                updatedInvalidLocation.setStatus(LocationStatus.from(csvRequest.getStatus()));
                updatedInvalidLocation.setAlternateLocation(validLocationFromDb);

                if(doesIdenticalLocationExistsInDB(unchangedInvalidLocationFromDb, updatedInvalidLocation)) return;

                syncResponses.addAll(syncService.syncLocation(updatedInvalidLocation));
            }
        }
        );
		return syncResponses;
    }

    private boolean doesIdenticalLocationExistsInDB(Location locationInDb, Location updatedLocation) {
        return locationInDb.equals(updatedLocation);
    }

    private Predicate hasStatus(final List<LocationStatus> statuses) {
        return new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                LocationImportCSVRequest csvRequest = (LocationImportCSVRequest) object;

                return statuses.contains(LocationStatus.from(csvRequest.getStatus()));
            }
        };}
        
        
}
