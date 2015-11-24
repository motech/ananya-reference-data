package org.motechproject.ananya.referencedata.contactCenter.service;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.referencedata.contactCenter.mapper.FrontLineWorkerMapper;
import org.motechproject.ananya.referencedata.contactCenter.request.FrontLineWorkerVerificationWebRequest;
import org.motechproject.ananya.referencedata.contactCenter.validator.FrontLineWorkerRequestValidator;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.LocationStatus;
import org.motechproject.ananya.referencedata.flw.domain.VerificationStatus;
import org.motechproject.ananya.referencedata.flw.repository.AllFrontLineWorkers;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;
import org.motechproject.ananya.referencedata.flw.service.SyncService;
import org.motechproject.ananya.referencedata.flw.validators.Errors;
import org.motechproject.ananya.referencedata.flw.validators.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class FrontLineWorkerContactCenterService {
    private AllFrontLineWorkers allFrontLineWorkers;
    private LocationService locationService;
    private FrontLineWorkerRequestValidator requestValidator;
    private SyncService syncService;

    public FrontLineWorkerContactCenterService() {
    }

    @Autowired
    public FrontLineWorkerContactCenterService(AllFrontLineWorkers allFrontLineWorkers, LocationService locationService, FrontLineWorkerRequestValidator requestValidator, SyncService syncService) {
        this.allFrontLineWorkers = allFrontLineWorkers;
        this.locationService = locationService;
        this.requestValidator = requestValidator;
        this.syncService = syncService;
    }

    @Transactional
    public void updateVerifiedFlw(FrontLineWorkerVerificationWebRequest webRequest) {
        FrontLineWorkerVerificationRequest request = webRequest.getVerificationRequest();
        updateVerifiedFlw(request);
    }

    private void updateVerifiedFlw(FrontLineWorkerVerificationRequest request) {
        Errors validationErrors = requestValidator.validate(request);
        raiseExceptionIfThereAreErrors(validationErrors,request.getLocation());
        saveAndSync(request);
    }
    

    /**
    * @author vishnu
    * Rectified null pointer issue.
    * @param locationRequest
    * @return
    */
   private String checkDuplicateLocation(LocationRequest locationRequest) {
//   	boolean flag = true;
	   String result = null;
   	  if (locationRequest != null && 
   			  !StringUtils.isEmpty(locationRequest.getDistrict()) && 
   			  !StringUtils.isEmpty(locationRequest.getBlock()) &&
              !StringUtils.isEmpty(locationRequest.getPanchayat()) &&
              !StringUtils.isEmpty(locationRequest.getState())) {
   		  List<Location> validLocationlist = locationService.getLocationbyStatus(locationRequest, LocationStatus.VALID); 
   		  List<Location> notVerifiedLocationList = locationService.getLocationbyStatus(locationRequest, LocationStatus.NOT_VERIFIED);
   		  if(validLocationlist.size() == 0) {
   			  if(notVerifiedLocationList.size()==0) {
   				  result = "add";
   			  }else if(notVerifiedLocationList.size()==1){
   				  result = "update";
   			  }else if (notVerifiedLocationList.size()>1){
   				  result = "error";
   			  }
   			  
   		  } 
   		  else if(validLocationlist.size()==1){
   			  result="update";
   			 }
   		  else{
   			  result="error";
   		  }
   	  }
   	return result;
   }

    private void saveAndSync(FrontLineWorkerVerificationRequest request) {
        FrontLineWorker frontLineWorker = getFrontLineWorker(request);
        FrontLineWorker flwFromDb = frontLineWorker.clone();
        frontLineWorker = mapFlwFields(request, frontLineWorker);
        FrontLineWorker frontLineWorkerForSync = frontLineWorker.clone();
        frontLineWorker.updateToNewMsisdn();
        if (noUpdate(frontLineWorker, flwFromDb)) return;

        allFrontLineWorkers.createOrUpdate(frontLineWorker);
        removeDuplicates(request);
        syncService.syncFrontLineWorker(frontLineWorkerForSync);
    }

    private void removeDuplicates(FrontLineWorkerVerificationRequest request) {
        if (request.duplicateMsisdnExists())
            allFrontLineWorkers.delete(request.duplicateFlwId());
    }

    private FrontLineWorker mapFlwFields(FrontLineWorkerVerificationRequest request, FrontLineWorker frontLineWorker) {
        FrontLineWorker updatedFrontLineWorker = FrontLineWorkerMapper.mapFrom(request, frontLineWorker);
        if (VerificationStatus.SUCCESS == request.getVerificationStatus()) {
            Location location = locationService.createAndFetch(request.getLocation());
            updatedFrontLineWorker = FrontLineWorkerMapper.mapSuccessfulRegistration(request, updatedFrontLineWorker, location);
        }
        return updatedFrontLineWorker;
    }

    private FrontLineWorker getFrontLineWorker(FrontLineWorkerVerificationRequest request) {
        UUID flwId = request.getFlwId();
        FrontLineWorker frontLineWorker = allFrontLineWorkers.getByFlwId(flwId);
        if (request.isDummyFlwId()) {
            frontLineWorker = getMatchingFLWForDummyFLWId(request);
        } else if (frontLineWorker == null) {
            frontLineWorker = new FrontLineWorker(flwId);
        } else
            validateMsisdnMatch(request.getMsisdn(), frontLineWorker.getMsisdn());
        return frontLineWorker;
    }

    private FrontLineWorker getMatchingFLWForDummyFLWId(FrontLineWorkerVerificationRequest request) {
        FrontLineWorker frontLineWorker;
        List<FrontLineWorker> frontLineWorkers = allFrontLineWorkers.getByMsisdn(request.getMsisdn());
        if (frontLineWorkers.size() == 1) {
            frontLineWorker = frontLineWorkers.get(0);
        } else {
            FrontLineWorker flwWithStatus = getFLWWithStatus(frontLineWorkers);
            frontLineWorker = flwWithStatus != null ? flwWithStatus : new FrontLineWorker(UUID.randomUUID());
        }
        return frontLineWorker;
    }

    private FrontLineWorker getFLWWithStatus(List<FrontLineWorker> frontLineWorkers) {
        for (FrontLineWorker frontLineWorker : frontLineWorkers) {
            if (frontLineWorker.hasBeenVerified()) return frontLineWorker;
        }
        return null;
    }

    private void validateMsisdnMatch(Long requestMsisdn, Long existingMsisdn) {
        if (!requestMsisdn.equals(existingMsisdn))
            throw new ValidationException(String.format("Given msisdn %s does not match existing msisdn %s for the given id.", requestMsisdn, existingMsisdn));
    }

    private void raiseExceptionIfThereAreErrors(Errors validationErrors, LocationRequest locationRequest) {
        if (validationErrors.hasErrors()) {
            throw new ValidationException(validationErrors.allMessages());
        } else {
        	if(checkDuplicateLocation(locationRequest)=="error") {
        		Errors errors = new Errors();
        		errors.add("Location already exists.");
        		throw new ValidationException(errors.allMessages());
        	}
        	else if(checkDuplicateLocation(locationRequest)=="update") {
        		Errors errors = new Errors();
        		errors.add("Location already exists.");
        	}
        }
    }

    private boolean noUpdate(FrontLineWorker updatedFrontLineWorker, FrontLineWorker flwFromDb) {
        return updatedFrontLineWorker.equals(flwFromDb);
    }

}