package org.motechproject.ananya.referencedata.web.diagnostic;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.ClosedInputStream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.diagnostics.response.DiagnosticsResult;
import org.springframework.beans.factory.annotation.Qualifier;
import sun.net.www.content.text.PlainTextInputStream;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
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
        properties.put("bbc.heartbeat.url", "testurl.com");
        HttpConnectionDiagnosticStub httpConnectionDiagnosticStub = new HttpConnectionDiagnosticStub(properties, connection);

        when(connection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);

        DiagnosticsResult diagnosticsResult = httpConnectionDiagnosticStub.performDiagnosis();

        assertTrue(diagnosticsResult.getStatus());
        assertTrue(diagnosticsResult.getMessage().contains("Successful http connection to Ananya-BBC"));
    }

    @Test
    public void shouldReturnErrorMessageWhenConnectionFails() throws IOException {
        Properties properties = new Properties();
        properties.put("bbc.heartbeat.url", "testurl.com");
        HttpConnectionDiagnosticStub httpConnectionDiagnosticStub = new HttpConnectionDiagnosticStub(properties, connection);

        when(connection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_BAD_REQUEST);
        when(connection.getResponseMessage()).thenReturn("Forbidden");


        DiagnosticsResult diagnosticsResult = httpConnectionDiagnosticStub.performDiagnosis();

        assertFalse(diagnosticsResult.getStatus());
        assertTrue(diagnosticsResult.getMessage().contains("Http connection to Ananya-BBC failed. Forbidden"));
    }

    @Test
    public void shouldNotThrowAnErrorWhenAnanyaUrlPropertyDoesNotExist() throws IOException {
        Properties properties = new Properties();
        HttpConnectionDiagnostic httpConnectionDiagnosticStub = new HttpConnectionDiagnostic();
        httpConnectionDiagnosticStub.setProperties(properties);

        DiagnosticsResult diagnosticsResult = httpConnectionDiagnosticStub.performDiagnosis();

        assertFalse(diagnosticsResult.getStatus());
        assertTrue(diagnosticsResult.getMessage().contains("Property bbc.heartbeat.url does not exist."));
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
