package org.motechproject.ananya.referencedata;

import org.apache.commons.lang.time.StopWatch;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.motechproject.ananya.referencedata.csv.CsvImporter;
import org.motechproject.ananya.referencedata.flw.domain.Location;

import java.net.URL;

import static junit.framework.Assert.assertEquals;

@Ignore
public class LocationPerformanceTest extends SpringIntegrationTest {

    @Before
    @After
    public void setUp() {
        template.deleteAll(template.loadAll(Location.class));
    }

    @Test
    public void shouldImportLocationDataFor100Records() throws Exception {
        URL locationData = this.getClass().getResource("/locations_100.csv");
        int locationCount = 100;
        loadLocationData(locationData, locationCount);
        assertEquals(locationCount, template.loadAll(Location.class).size());
    }

    @Test
    public void shouldImportLocationDataFor500Records() throws Exception {
        URL locationData = this.getClass().getResource("/locations_500.csv");
        int locationCount = 500;
        loadLocationData(locationData, locationCount);
        assertEquals(locationCount, template.loadAll(Location.class).size());
    }

    @Test
    public void shouldImportLocationDataFor1000Records() throws Exception {
        URL locationData = this.getClass().getResource("/locations_1000.csv");
        int locationCount = 1000;
        loadLocationData(locationData, locationCount);
        assertEquals(locationCount, template.loadAll(Location.class).size());
    }

    @Test
    public void shouldImportLocationDataFor2500Records() throws Exception {
        URL locationData = this.getClass().getResource("/locations_2500.csv");
        int locationCount = 2500;
        loadLocationData(locationData, locationCount);
        assertEquals(locationCount, template.loadAll(Location.class).size());
    }

    @Test
    public void shouldImportLocationDataFor5000Records() throws Exception {
        URL locationData = this.getClass().getResource("/locations_5000.csv");
        int locationCount = 5000;
        loadLocationData(locationData, locationCount);
        assertEquals(locationCount, template.loadAll(Location.class).size());
    }

    public static void loadLocationData(URL locationData, int count) throws Exception {
        String[] arguments = {"Location", locationData.getPath()};
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        CsvImporter.main(arguments);

        stopWatch.stop();
        System.out.println("Total time to load " + count + "locations : " + stopWatch.getTime() + "ms");
    }
}
