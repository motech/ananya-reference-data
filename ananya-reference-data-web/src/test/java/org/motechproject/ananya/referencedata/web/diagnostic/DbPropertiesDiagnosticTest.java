package org.motechproject.ananya.referencedata.web.diagnostic;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.referencedata.flw.domain.AnanyaReferenceDataProperty;
import org.motechproject.ananya.referencedata.flw.service.AnanyaReferenceDataPropertiesService;
import org.motechproject.diagnostics.response.DiagnosticsResult;

import java.util.Arrays;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DbPropertiesDiagnosticTest {
    @Mock
    private AnanyaReferenceDataPropertiesService propertiesService;

    @Test
    public void shouldReturnAllPropertiesFromTheDb(){
        when(propertiesService.getAllProperties()).thenReturn(Arrays.asList(new AnanyaReferenceDataProperty("sync", "on")));

        DiagnosticsResult diagnosticsResult = new DbPropertiesDiagnostic(propertiesService).performDiagnosis();

        assertTrue(diagnosticsResult.getStatus());
        assertTrue(diagnosticsResult.getMessage().contains("sync=on"));
    }

    @Test
    public void shouldFailDiagnosticsIfExceptionHappens(){
        String exceptionMessage = "Me no work";
        when(propertiesService.getAllProperties()).thenThrow(new IllegalStateException(exceptionMessage));

        DiagnosticsResult diagnosticsResult = new DbPropertiesDiagnostic(propertiesService).performDiagnosis();

        assertFalse(diagnosticsResult.getStatus());
        assertTrue(diagnosticsResult.getMessage().contains(exceptionMessage));
    }
}
