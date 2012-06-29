package org.motechproject.ananya.referencedata.flw.metrics;

import org.motechproject.metrics.MetricsAgentBackend;

import java.util.Map;

public class DummyStats implements MetricsAgentBackend {
    @Override
    public void logEvent(String s, Map<String, String> stringStringMap) {
        //Don't do anything
    }

    @Override
    public void logEvent(String s) {
        //Don't do anything
    }

    @Override
    public void logTimedEvent(String s, long l) {
        //Don't do anything
    }
}
