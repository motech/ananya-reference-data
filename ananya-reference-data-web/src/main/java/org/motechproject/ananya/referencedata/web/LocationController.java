package org.motechproject.ananya.referencedata.web;

import org.motechproject.ananya.referencedata.request.LocationRequest;
import org.motechproject.ananya.referencedata.response.LocationCreationResponse;
import org.motechproject.ananya.referencedata.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/location")
public class    LocationController {

    private LocationService locationService;

    @Autowired
    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView create(@RequestBody LocationRequest locationRequest) {
        LocationCreationResponse locationCreationResponse = locationService.add(locationRequest);
        return new ModelAndView("creation").addObject("response",locationCreationResponse);
    }
}
