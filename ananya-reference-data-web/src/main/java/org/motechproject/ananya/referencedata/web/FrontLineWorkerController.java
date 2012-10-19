package org.motechproject.ananya.referencedata.web;

import org.motechproject.ananya.referencedata.flw.response.FrontLineWorkerResponse;
import org.motechproject.ananya.referencedata.flw.service.FrontLineWorkerCsvService;
import org.motechproject.ananya.referencedata.flw.validators.Errors;
import org.motechproject.ananya.referencedata.flw.validators.ValidationException;
import org.motechproject.ananya.referencedata.web.request.FrontLineWorkerWebRequest;
import org.motechproject.ananya.referencedata.web.validators.FrontLineWorkerWebRequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/flw")
public class FrontLineWorkerController extends BaseController {
    private FrontLineWorkerCsvService frontLineWorkerCsvService;

    @Autowired
    public FrontLineWorkerController(FrontLineWorkerCsvService frontLineWorkerCsvService) {
        this.frontLineWorkerCsvService = frontLineWorkerCsvService;
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public FrontLineWorkerResponse updateVerifiedFlw(@RequestBody FrontLineWorkerWebRequest frontLineWorkerWebRequest) {
        validateRequest(frontLineWorkerWebRequest);
        //TODO
        return null;
    }

    private void validateRequest(FrontLineWorkerWebRequest frontLineWorkerWebRequest) {
        Errors errors = new FrontLineWorkerWebRequestValidator().validateFrontLineWorkerRequest(frontLineWorkerWebRequest);
        if(errors.hasErrors())
            throw new ValidationException(errors.allMessages());
    }

}
