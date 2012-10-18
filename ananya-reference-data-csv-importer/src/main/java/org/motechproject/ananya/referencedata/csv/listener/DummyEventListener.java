package org.motechproject.ananya.referencedata.csv.listener;

import org.motechproject.event.EventListener;
import org.motechproject.event.MotechEvent;

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
