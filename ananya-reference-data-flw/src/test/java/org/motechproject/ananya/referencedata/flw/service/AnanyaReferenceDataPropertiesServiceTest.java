package org.motechproject.ananya.referencedata.flw.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.referencedata.flw.repository.AllAnanyaReferenceDataProperties;

import static junit.framework.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class AnanyaReferenceDataPropertiesServiceTest {
    private AnanyaReferenceDataPropertiesService propertiesService;

    @Mock
    private AllAnanyaReferenceDataProperties allDbProperties;

    @Before
    public void setUp() {
        initMocks(this);
        propertiesService = new AnanyaReferenceDataPropertiesService(allDbProperties);
    }

    @Test
    public void shouldReturnTrueForIsSyncOnWhenSyncPropertyHasBeenSetToOn() {
        when(allDbProperties.getForSyncSwitch()).thenReturn("on");

        assertTrue(propertiesService.isSyncOn());
    }
}
