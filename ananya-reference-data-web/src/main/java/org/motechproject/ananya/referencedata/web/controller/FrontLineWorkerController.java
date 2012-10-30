package org.motechproject.ananya.referencedata.web.controller;

import org.motechproject.ananya.referencedata.contactCenter.request.FrontLineWorkerWebRequest;
import org.motechproject.ananya.referencedata.contactCenter.service.FrontLineWorkerContactCenterService;
import org.motechproject.ananya.referencedata.contactCenter.validator.FrontLineWorkerWebRequestValidator;
import org.motechproject.ananya.referencedata.flw.response.BaseResponse;
import org.motechproject.ananya.referencedata.flw.validators.Errors;
import org.motechproject.ananya.referencedata.flw.validators.ValidationException;
import org.motechproject.ananya.referencedata.web.validator.WebRequestValidator;
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
    public BaseResponse updateVerifiedFlw(@RequestBody FrontLineWorkerWebRequest frontLineWorkerWebRequest, @RequestParam String channel ) {
        validateRequest(frontLineWorkerWebRequest, channel);
        frontLineWorkerContactCenterService.updateVerifiedFlw(frontLineWorkerWebRequest);
        return BaseResponse.success();
    }

    private void validateRequest(FrontLineWorkerWebRequest frontLineWorkerWebRequest, String channel) {
        WebRequestValidator webRequestValidator = new WebRequestValidator();
        Errors errors = webRequestValidator.validateChannel(channel);
        errors.addAll(FrontLineWorkerWebRequestValidator.validate(frontLineWorkerWebRequest));
        if (errors.hasErrors())
            throw new ValidationException(errors.allMessages());
    }
}