package org.motechproject.ananya.referencedata.web.controller;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.referencedata.contactCenter.request.FrontLineWorkerVerificationWebRequest;
import org.motechproject.ananya.referencedata.contactCenter.service.FrontLineWorkerContactCenterService;
import org.motechproject.ananya.referencedata.contactCenter.service.LocationService;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.LocationStatus;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;
import org.motechproject.ananya.referencedata.flw.response.BaseResponse;
import org.motechproject.ananya.referencedata.flw.validators.Errors;
import org.motechproject.ananya.referencedata.flw.validators.ValidationException;
import org.motechproject.ananya.referencedata.web.service.DefaultRequestValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class FrontLineWorkerController extends BaseController {
    private FrontLineWorkerContactCenterService frontLineWorkerContactCenterService;
    private DefaultRequestValues defaultRequestValues;
    private LocationService locationService;

    @Autowired
    public FrontLineWorkerController(FrontLineWorkerContactCenterService frontLineWorkerContactCenterService
    		, DefaultRequestValues defaultRequestValues
    		, LocationService locationService) {
        this.frontLineWorkerContactCenterService = frontLineWorkerContactCenterService;
        this.defaultRequestValues = defaultRequestValues;
        this.locationService = locationService;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/flw")
    @ResponseBody
    public BaseResponse updateVerifiedFlw(@RequestBody FrontLineWorkerVerificationWebRequest frontLineWorkerWebRequest, @RequestParam String channel ) {
        defaultRequestValues.update(frontLineWorkerWebRequest);
        frontLineWorkerWebRequest.setChannel(channel);
        validateRequest(frontLineWorkerWebRequest);
        frontLineWorkerContactCenterService.updateVerifiedFlw(frontLineWorkerWebRequest);
        return BaseResponse.success("The FLW has been updated successfully");
    }

    private void validateRequest(FrontLineWorkerVerificationWebRequest frontLineWorkerWebRequest) {
        Errors validationErrors = frontLineWorkerWebRequest.validate();
        raiseExceptionIfThereAreErrors(validationErrors, frontLineWorkerWebRequest);
    }


    private void raiseExceptionIfThereAreErrors(Errors validationErrors,FrontLineWorkerVerificationWebRequest frontLineWorkerWebRequest) {
        if (validationErrors.hasErrors()) {
            throw new ValidationException(validationErrors.allMessages());
        }
    }
    
    
}