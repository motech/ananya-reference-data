package org.motechproject.ananya.referencedata.web.domain.page;


import org.junit.Test;
import org.springframework.web.servlet.ModelAndView;

import static junit.framework.Assert.assertEquals;

public class LoginPageTest {

    @Test
    public void shouldAddErrorObjectToModelAndView() {
        String errorMessage = "error message";

        ModelAndView display = new LoginPage().display(errorMessage);

        assertEquals(errorMessage, display.getModel().get("error"));
        assertEquals("admin/login", display.getViewName());
    }
}
