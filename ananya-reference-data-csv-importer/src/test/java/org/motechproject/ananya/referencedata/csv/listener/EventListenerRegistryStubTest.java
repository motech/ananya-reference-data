package org.motechproject.ananya.referencedata.csv.listener;

import org.junit.Test;
import org.motechproject.server.event.EventListener;

import java.util.Set;

import static org.junit.Assert.assertEquals;

public class EventListenerRegistryStubTest {

    @Test
    public void shouldReturnTheDummyListener() {
        EventListenerRegistryStub eventListenerRegistryStub = new EventListenerRegistryStub();

        Set<EventListener> listeners = eventListenerRegistryStub.getListeners("");

        assertEquals(1, listeners.size());
        assertEquals(DummyEventListener.class, (listeners.toArray()[0]).getClass());
    }
}
