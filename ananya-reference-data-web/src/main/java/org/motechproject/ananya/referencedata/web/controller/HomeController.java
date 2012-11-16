package org.motechproject.ananya.referencedata.web.controller;

import org.motechproject.ananya.referencedata.contactCenter.service.LocationService;
import org.motechproject.ananya.referencedata.web.exception.PopUpException;
import org.motechproject.ananya.referencedata.web.mapper.LocationResponseMapper;
import org.motechproject.ananya.referencedata.web.response.LocationResponseList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@Controller
public class HomeController extends BaseController {

    private LocationService locationService;

    @Autowired
    public HomeController(LocationService locationService) {
        this.locationService = locationService;
    }

    @RequestMapping(method = RequestMethod.GET, value = {"/admin", "/admin/home"})
    public ModelAndView home() {
        return new ModelAndView("admin/home");

    }

    @RequestMapping(method = RequestMethod.GET, value = "/admin/home/download", produces = "text/csv")
    @ResponseBody
    public LocationResponseList locationResponseList() throws IOException {
        try {
            return LocationResponseMapper.mapWithStatus(locationService.getLocationsToBeVerified());
        } catch (Exception e) {
            throw new PopUpException(e.getMessage());
        }
    }
}
