package org.motechproject.ananya.referencedata.web;

import org.motechproject.ananya.referencedata.request.FrontLineWorkerRequest;
import org.motechproject.ananya.referencedata.response.FLWResponse;
import org.motechproject.ananya.referencedata.service.FrontLineWorkerService;
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
    public
    @ResponseBody
    FLWResponse create(@RequestBody FrontLineWorkerRequest frontLineWorkerRequest) {
        return frontLineWorkerService.add(frontLineWorkerRequest);
    }

    @RequestMapping(params = "_method=PUT")
    public
    @ResponseBody
    FLWResponse update(@RequestBody FrontLineWorkerRequest frontLineWorkerRequest) {
        return frontLineWorkerService.update(frontLineWorkerRequest);
    }
}
