package org.motechproject.ananya.referencedata.csv.listener;

import org.motechproject.event.EventListener;
import org.motechproject.event.EventListenerRegistry;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EventListenerRegistryStub extends EventListenerRegistry {
    @Override
    public void registerListener(EventListener listener, List<String> subjects) {
    }

    @Override
    public void registerListener(EventListener listener, String subject) {
    }

    @Override
    public Set<EventListener> getListeners(String subject) {
        HashSet<EventListener> eventListeners = new HashSet<EventListener>();
        eventListeners.add(new DummyEventListener());
        return eventListeners;
    }
}
