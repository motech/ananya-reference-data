package org.motechproject.ananya.referencedata.web.controller;

import org.motechproject.ananya.referencedata.contactCenter.request.FrontLineWorkerVerificationWebRequest;
import org.motechproject.ananya.referencedata.contactCenter.service.FrontLineWorkerContactCenterService;
import org.motechproject.ananya.referencedata.flw.response.BaseResponse;
import org.motechproject.ananya.referencedata.flw.validators.Errors;
import org.motechproject.ananya.referencedata.flw.validators.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class FrontLineWorkerController extends BaseController {
    private FrontLineWorkerContactCenterService frontLineWorkerContactCenterService;

    @Autowired
    public FrontLineWorkerController(FrontLineWorkerContactCenterService frontLineWorkerContactCenterService) {
        this.frontLineWorkerContactCenterService = frontLineWorkerContactCenterService;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/flw")
    @ResponseBody
    public BaseResponse updateVerifiedFlw(@RequestBody FrontLineWorkerVerificationWebRequest frontLineWorkerWebRequest, @RequestParam String channel ) {
        frontLineWorkerWebRequest.setChannel(channel);
        validateRequest(frontLineWorkerWebRequest, channel);

        frontLineWorkerContactCenterService.updateVerifiedFlw(frontLineWorkerWebRequest);
        return BaseResponse.success("The FLW has been updated successfully");
    }

    private void validateRequest(FrontLineWorkerVerificationWebRequest frontLineWorkerWebRequest, String channel) {
        Errors validationErrors = frontLineWorkerWebRequest.validate();
        raiseExceptionIfThereAreErrors(validationErrors);
    }


    private void raiseExceptionIfThereAreErrors(Errors validationErrors) {
        if (validationErrors.hasErrors()) {
            throw new ValidationException(validationErrors.allMessages());
        }
    }
}