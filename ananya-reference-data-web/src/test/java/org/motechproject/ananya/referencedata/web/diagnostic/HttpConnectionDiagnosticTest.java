package org.motechproject.ananya.referencedata.web.diagnostic;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.diagnostics.response.DiagnosticsResult;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Properties;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HttpConnectionDiagnosticTest {

    @Mock
    private HttpURLConnection connection;

    @Test
    public void shouldReturnSuccessResponseWhenHttpConnectionIsSuccessful() throws IOException {
        Properties properties = new Properties();
        properties.put("ananya.bbc.url", "testurl.com");
        HttpConnectionDiagnosticStub httpConnectionDiagnosticStub = new HttpConnectionDiagnosticStub(properties, connection);

        when(connection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);
        when(connection.getResponseMessage()).thenReturn("Successful Connection");

        DiagnosticsResult diagnosticsResult = httpConnectionDiagnosticStub.performDiagnosis();

        assertTrue(diagnosticsResult.getStatus());
        assertTrue(diagnosticsResult.getMessage().contains("Successful Connection"));
    }

    @Test
    public void shouldReturnErrorMessageWhenConnectionFails() throws IOException {
        Properties properties = new Properties();
        properties.put("ananya.bbc.url", "testurl.com");
        HttpConnectionDiagnosticStub httpConnectionDiagnosticStub = new HttpConnectionDiagnosticStub(properties, connection);

        when(connection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_BAD_REQUEST);
        when(connection.getResponseMessage()).thenReturn("Error in Connection");

        DiagnosticsResult diagnosticsResult = httpConnectionDiagnosticStub.performDiagnosis();

        assertFalse(diagnosticsResult.getStatus());
        assertTrue(diagnosticsResult.getMessage().contains("Error in Connection"));
    }

    @Test
    public void shouldNotThrowAnErrorWhenAnanyaUrlPropertyDoesNotExist() throws IOException {
        Properties properties = new Properties();
        HttpConnectionDiagnostic httpConnectionDiagnosticStub = new HttpConnectionDiagnostic();
        httpConnectionDiagnosticStub.setProperties(properties);

        DiagnosticsResult diagnosticsResult = httpConnectionDiagnosticStub.performDiagnosis();

        assertFalse(diagnosticsResult.getStatus());
        assertTrue(diagnosticsResult.getMessage().contains("Property ananya.bbc.url does not exist."));
    }

    @Test
    public void shouldReturnNullWhenPropertyFileDoesNotExist() throws IOException {
        HttpConnectionDiagnosticStub httpConnectionDiagnosticStub = new HttpConnectionDiagnosticStub(null, connection);

        DiagnosticsResult diagnosticsResult = httpConnectionDiagnosticStub.performDiagnosis();
        assertNull(diagnosticsResult);
    }

}

class HttpConnectionDiagnosticStub extends HttpConnectionDiagnostic {

    private HttpURLConnection connection;

    public HttpConnectionDiagnosticStub(@Qualifier("referencedataProperties") Properties properties, HttpURLConnection connection) {
        this.connection = connection;
        setProperties(properties);
    }

    protected HttpURLConnection getConnection() throws IOException {
        return connection;

    }
}
