package org.motechproject.ananya.referencedata.csv.listener;

import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.EventListener;

public class DummyEventListener implements EventListener {

    @Override
    public void handle(MotechEvent event) {
        throw new IllegalStateException();
    }

    @Override
    public String getIdentifier() {
        return "TestEventListener";
    }
}
