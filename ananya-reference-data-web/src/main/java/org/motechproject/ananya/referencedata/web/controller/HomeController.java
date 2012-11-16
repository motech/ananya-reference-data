package org.motechproject.ananya.referencedata.web.controller;

import org.motechproject.ananya.referencedata.domain.Channel;
import org.motechproject.ananya.referencedata.web.response.LocationResponseList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class HomeController {

    private LocationController locationController;

    @Autowired
    public HomeController(LocationController locationController) {
        this.locationController = locationController;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/admin/home")
    public ModelAndView home() {
        return new ModelAndView("admin/home");

    }

    @RequestMapping(method = RequestMethod.GET, value = "/admin/home/download", produces = "text/csv")
    @ResponseBody
    public LocationResponseList locationResponseList() throws IOException {
        return locationController.getLocationsToBeVerified(Channel.CONTACT_CENTER.name());
    }

}
