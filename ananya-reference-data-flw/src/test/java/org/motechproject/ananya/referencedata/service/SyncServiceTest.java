package org.motechproject.ananya.referencedata.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.referencedata.domain.SyncEventKeys;
import org.motechproject.context.EventContext;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class SyncServiceTest {
    @Mock
    private EventContext eventContext;

    @Before
    public void setUp(){
        initMocks(this);
    }

    @Test
    public void shouldPublishFlwDataIntoQueue() {
        int flwId = 1234;
        new SyncService(eventContext).syncFrontLineWorker(flwId);
        verify(eventContext).send(SyncEventKeys.FRONT_LINE_WORKER_DATA_MESSAGE, flwId);
    }
}
