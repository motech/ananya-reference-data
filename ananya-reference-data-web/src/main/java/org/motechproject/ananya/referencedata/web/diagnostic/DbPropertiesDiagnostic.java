package org.motechproject.ananya.referencedata.web.diagnostic;

import org.motechproject.ananya.referencedata.flw.domain.AnanyaReferenceDataProperty;
import org.motechproject.ananya.referencedata.flw.service.AnanyaReferenceDataPropertiesService;
import org.motechproject.diagnostics.annotation.Diagnostic;
import org.motechproject.diagnostics.diagnostics.DiagnosticLog;
import org.motechproject.diagnostics.response.DiagnosticsResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DbPropertiesDiagnostic {
    private AnanyaReferenceDataPropertiesService propertiesService;

    @Autowired
    public DbPropertiesDiagnostic(AnanyaReferenceDataPropertiesService propertiesService ) {
        this.propertiesService = propertiesService;
    }

    @Diagnostic(name = "DbProperties")
    public DiagnosticsResult performDiagnosis() {
        DiagnosticLog diagnosticLog = new DiagnosticLog();
        boolean status;

        diagnosticLog.add("Key/Value pairs");
        try {
            List<AnanyaReferenceDataProperty> ananyaReferenceDataProperties = propertiesService.getAllProperties();
            for (AnanyaReferenceDataProperty property : ananyaReferenceDataProperties) {
                diagnosticLog.add(property.getName() + "=" + property.getValue());
            }
            status = true;
        } catch (Exception e) {
            status = false;
            diagnosticLog.addError(e);
        }

        return new DiagnosticsResult(status, diagnosticLog.toString());
    }
}
