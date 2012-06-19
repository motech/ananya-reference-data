package org.motechproject.ananya.referencedata.flw.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.referencedata.SpringIntegrationTest;
import org.motechproject.ananya.referencedata.flw.domain.AnanyaReferenceDataProperty;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.Assert.assertEquals;

public class AllAnanyaReferenceDataPropertiesTest extends SpringIntegrationTest{
    @Autowired
    private AllAnanyaReferenceDataProperties allDbProperties;

    @Before
    public void setUp() {
        template.save(new AnanyaReferenceDataProperty("syncSwitch", "on"));
    }

    @After
    public void tearDown() {
        template.deleteAll(template.loadAll(AnanyaReferenceDataProperty.class));
    }

    @Test
    public void shouldGetForSyncSwitch() {
        assertEquals("on", allDbProperties.getForSyncSwitch());
    }
}
