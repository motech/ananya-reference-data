package org.motechproject.ananya.referencedata.web.domain.page;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

@Service
public class HomePage {
    private String viewName = "admin/home";

    public ModelAndView display() {
        return new ModelAndView(viewName);
    }
}
