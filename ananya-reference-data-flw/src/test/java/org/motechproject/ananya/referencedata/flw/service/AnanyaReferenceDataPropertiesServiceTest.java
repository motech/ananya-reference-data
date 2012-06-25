package org.motechproject.ananya.referencedata.flw.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.referencedata.flw.domain.AnanyaReferenceDataProperty;
import org.motechproject.ananya.referencedata.flw.repository.AllAnanyaReferenceDataProperties;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
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
    @Test
    public void shouldReturnAllDbProperties() {
        List<AnanyaReferenceDataProperty> ananyaReferenceDataProperties = Arrays.asList(new AnanyaReferenceDataProperty("syncSwitch", "on"), new AnanyaReferenceDataProperty("dummy", "dum"));
        when(allDbProperties.getAllProperties()).thenReturn(ananyaReferenceDataProperties);

        List<AnanyaReferenceDataProperty> allDbProperties = propertiesService.getAllProperties();

        assertEquals(2, allDbProperties.size());
    }
}
