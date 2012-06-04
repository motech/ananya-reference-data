package org.motechproject.ananya.referencedata.web.interceptors;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.referencedata.web.annotations.Authenticated;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Properties;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class AuthenticationInterceptorTest {
    @Mock
    private Properties apiKeys;
    @Mock
    private HttpServletRequest request;
    @Mock
    private Object handler;
    @Mock
    private HttpServletResponse response;
    @Mock
    private PrintWriter printWriter;

    private AuthenticationInterceptor authenticationInterceptor;

    @Before
    public void setUp() {
        initMocks(this);
        authenticationInterceptor = new AuthenticationInterceptor(apiKeys);
    }

    @Test
    public void shouldFailAuthenticationIfAPIKeyIsNotPresent() throws Exception {
        String apiKey = "1234";
        when(apiKeys.containsValue(apiKey)).thenReturn(false);
        when(request.getHeader("APIKey")).thenReturn(apiKey);
        when(response.getWriter()).thenReturn(printWriter);

        boolean shouldContinue = authenticationInterceptor.preHandle(request, response, new AuthenticatedClass());

        verify(response).setContentType("application/json");
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(printWriter).write("{\"Error\" : \"API key does not match.\"}");
        assertFalse(shouldContinue);
    }

    @Test
    public void shouldAuthenticateBasedOnAuthenticatedAnnotation() throws Exception {
        authenticationInterceptor.preHandle(request, null, new UnAuthenticatedClass());

        verifyZeroInteractions(apiKeys);
        verifyZeroInteractions(request);
    }

    @Test
    public void shouldAuthenticateBasedOnAPIKey() throws Exception {
        String apiKey = "1234";
        when(apiKeys.containsValue(apiKey)).thenReturn(true);
        when(request.getHeader("APIKey")).thenReturn(apiKey);

        boolean shouldContinue = authenticationInterceptor.preHandle(request, response, new AuthenticatedClass());

        assertTrue(shouldContinue);
    }

    @Authenticated
    class AuthenticatedClass {
    }

    class UnAuthenticatedClass {
    }
}
