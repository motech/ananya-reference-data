package org.motechproject.ananya.referencedata.csv.service;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.referencedata.csv.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.TestCase.assertNotNull;

public class LocationImportServiceIT extends SpringIntegrationTest {

    @Autowired
    CacheManager cacheManager;

    @Autowired
    LocationImportService locationImportService;
    private Cache cache;

    @Before
    public void setUp() {
        cache = cacheManager.getCache("locationSearchCache");
    }

    @Test
    public void shouldInvalidateCacheWhenCalled() {
        String key = "something";
        cache.put(new Element(key, "value"));
        assertNotNull(cache.get(key));

        locationImportService.invalidateCache();
        assertNull(cache.get(key));
    }

    @Test
    public void shouldCacheNullLocation() {
        assertEquals(0, cache.getSize());

        locationImportService.getFor("state", "district", "block", "panchayat");
        assertEquals(1, cache.getSize());
        Object key = cache.getKeys().get(0);
        Serializable value = cache.get(key).getValue();
        assertNull(value);
    }
}
