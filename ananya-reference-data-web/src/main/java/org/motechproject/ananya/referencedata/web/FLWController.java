package org.motechproject.ananya.referencedata.web;

import org.motechproject.ananya.referencedata.request.FLWRequest;
import org.motechproject.ananya.referencedata.response.FLWResponse;
import org.motechproject.ananya.referencedata.service.FLWService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/flw")
public class FLWController {
    private FLWService flwService;

    @Autowired
    public FLWController(FLWService flwService) {
        this.flwService = flwService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public
    @ResponseBody
    FLWResponse create(@RequestBody FLWRequest flwRequest) {
        return flwService.add(flwRequest);
    }
}
