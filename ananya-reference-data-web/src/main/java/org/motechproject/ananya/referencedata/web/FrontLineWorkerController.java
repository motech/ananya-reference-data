package org.motechproject.ananya.referencedata.web;

import org.motechproject.ananya.referencedata.flw.request.FrontLineWorkerRequest;
import org.motechproject.ananya.referencedata.flw.response.FrontLineWorkerResponse;
import org.motechproject.ananya.referencedata.flw.service.FrontLineWorkerService;
import org.motechproject.ananya.referencedata.web.validator.Errors;
import org.motechproject.ananya.referencedata.web.validator.FrontLineWorkerWebRequestValidator;
import org.motechproject.ananya.referencedata.web.validator.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/flw")
public class FrontLineWorkerController extends BaseController {
    private FrontLineWorkerService frontLineWorkerService;

    @Autowired
    public FrontLineWorkerController(FrontLineWorkerService frontLineWorkerService) {
        this.frontLineWorkerService = frontLineWorkerService;
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public FrontLineWorkerResponse updateVerifiedFlw(@RequestBody FrontLineWorkerRequest frontLineWorkerRequest) {
        validateRequest(frontLineWorkerRequest);
        return frontLineWorkerService.updateVerifiedFlw(frontLineWorkerRequest);
    }

    private void validateRequest(FrontLineWorkerRequest frontLineWorkerRequest) {
        Errors errors = new FrontLineWorkerWebRequestValidator().validateFrontLineWorkerRequest(frontLineWorkerRequest);
        if(errors.hasErrors())
            throw new ValidationException(errors.allMessages());
    }

}
