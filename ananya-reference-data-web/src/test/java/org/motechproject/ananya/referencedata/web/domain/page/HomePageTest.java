package org.motechproject.ananya.referencedata.web.domain.page;

import org.junit.Test;
import org.springframework.web.servlet.ModelAndView;

import static junit.framework.Assert.assertEquals;

public class HomePageTest {
    @Test
    public void shouldReturnHomePageViewName() {
        ModelAndView modelAndView = new HomePage().display();

        assertEquals("admin/home", modelAndView.getViewName());
    }
}
