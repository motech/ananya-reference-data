package org.motechproject.ananya.referencedata.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.referencedata.web.domain.page.LoginPage;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AdminControllerTest {

    @Mock
    private LoginPage loginPage;

    private AdminController adminController;

    @Before
    public void setUp(){
        adminController = new AdminController(loginPage);
    }

    @Test
    public void shouldLoginErrors() {
        String loginError = "error";
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("login_error")).thenReturn(loginError);
        ModelAndView expectedModelAndView = new ModelAndView();

        when(loginPage.display(loginError)).thenReturn(expectedModelAndView);

        SecurityContext mockSecurityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(mockSecurityContext);
        Authentication mockAuthentication = mock(Authentication.class);
        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);
        when(mockAuthentication.getPrincipal()).thenReturn("anonymousUser");


        ModelAndView actualModelAndView = adminController.login(request);


        assertEquals(expectedModelAndView, actualModelAndView);
    }

    @Test
    public void shouldRedirectToAdminHomeWhenAlreadyLoggedIn() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        ModelAndView expectedModelAndView = new ModelAndView("redirect:/admin/home");

        SecurityContext mockSecurityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(mockSecurityContext);
        Authentication mockAuthentication = mock(Authentication.class);
        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);
        UserDetails mockUserDetails = mock(UserDetails.class);
        when(mockAuthentication.getPrincipal()).thenReturn(mockUserDetails);
        when(mockUserDetails.isAccountNonExpired()).thenReturn(true);
        when(mockUserDetails.isAccountNonLocked()).thenReturn(true);
        when(mockUserDetails.isCredentialsNonExpired()).thenReturn(true);
        when(mockUserDetails.isEnabled()).thenReturn(true);


        ModelAndView actualModelAndView = adminController.login(request);


        assertEquals(expectedModelAndView.getViewName(), actualModelAndView.getViewName());
    }
}
