package org.motechproject.ananya.referencedata.web;

import org.motechproject.ananya.referencedata.request.FrontLineWorkerRequest;
import org.motechproject.ananya.referencedata.request.FrontLineWorkerRequestList;
import org.motechproject.ananya.referencedata.response.FrontLineWorkerResponse;
import org.motechproject.ananya.referencedata.service.FrontLineWorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping(value = "/flw")
public class FrontLineWorkerController extends BaseController {
    private FrontLineWorkerService frontLineWorkerService;

    @Autowired
    public FrontLineWorkerController(FrontLineWorkerService frontLineWorkerService) {
        this.frontLineWorkerService = frontLineWorkerService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public
    @ResponseBody
    FrontLineWorkerResponse createOrUpdate(@RequestBody FrontLineWorkerRequest frontLineWorkerRequest) {
        return frontLineWorkerService.createOrUpdate(frontLineWorkerRequest);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/bulk_import")
    public
    @ResponseBody
    void createOrUpdateAll(@RequestBody FrontLineWorkerRequestList frontLineWorkerRequest) {
        frontLineWorkerService.addAllWithoutValidations(frontLineWorkerRequest);
    }
}
