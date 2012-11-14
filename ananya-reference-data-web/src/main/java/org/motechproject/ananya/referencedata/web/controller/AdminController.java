package org.motechproject.ananya.referencedata.web.controller;

import org.motechproject.ananya.referencedata.web.domain.page.HomePage;
import org.motechproject.ananya.referencedata.web.domain.page.LoginPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class AdminController {

    private static Logger log = LoggerFactory.getLogger(AdminController.class);

    private LoginPage loginPage;
    private HomePage homePage;

    @Autowired
    public AdminController(LoginPage loginPage, HomePage homePage) {
        this.loginPage = loginPage;
        this.homePage = homePage;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/admin/login")
    public ModelAndView login(HttpServletRequest request) {
        final String error = request.getParameter("login_error");
        return loginPage.display(error);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/admin/home")
    public ModelAndView home() {
        return homePage.display();
    }
}
