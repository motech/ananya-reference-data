package org.motechproject.ananya.referencedata;

import org.apache.commons.lang.time.StopWatch;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.referencedata.csv.CsvImporter;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.Location;

import java.net.URL;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class FLWPerformanceTest extends SpringIntegrationTest {

    private static DetachedCriteria criteria;
    private static boolean locationLoaded = false;
    private int numberOfFlwsToBeCreated;

    public void createAllLocations() throws Exception {
        if (!locationLoaded) {
            template.deleteAll(template.loadAll(Location.class));
            URL locationData = this.getClass().getResource("/locations_5000.csv");
            LocationPerformanceTest.loadLocationData(locationData, 5000);
            locationLoaded = true;
        }
    }

    @Before
    public void setUp() throws Exception {
        template.deleteAll(template.loadAll(FrontLineWorker.class));
        createAllLocations();
        criteria = DetachedCriteria.forClass(FrontLineWorker.class);
        criteria.setProjection(Projections.rowCount());
    }

    @AfterClass
    public static void tearDown() {
        template.deleteAll(template.loadAll(FrontLineWorker.class));
        template.deleteAll(template.loadAll(Location.class));
    }

    @Test
    public void shouldImportFLWDataFor100Records() throws Exception {
        URL flwData = this.getClass().getResource("/flws_100.csv");
        numberOfFlwsToBeCreated = 100;
        loadFLWData(flwData, numberOfFlwsToBeCreated);
        assertNumberOfFlwsCreated(numberOfFlwsToBeCreated);
    }

    @Test
    public void shouldImportFLWDataFor500Records() throws Exception {
        URL flwData = this.getClass().getResource("/flws_500.csv");
        numberOfFlwsToBeCreated = 500;
        loadFLWData(flwData, numberOfFlwsToBeCreated);
        assertNumberOfFlwsCreated(numberOfFlwsToBeCreated);
    }

    @Test
    public void shouldImportFLWDataFor1000Records() throws Exception {
        URL flwData = this.getClass().getResource("/flws_1000.csv");
        numberOfFlwsToBeCreated = 1000;
        loadFLWData(flwData, numberOfFlwsToBeCreated);
        assertNumberOfFlwsCreated(numberOfFlwsToBeCreated);
    }

    @Test
    public void shouldImportFLWDataFor5000Records() throws Exception {
        URL flwData = this.getClass().getResource("/flws_5000.csv");
        numberOfFlwsToBeCreated = 5000;
        loadFLWData(flwData, numberOfFlwsToBeCreated);
        assertNumberOfFlwsCreated(numberOfFlwsToBeCreated);
    }

    @Test
    public void shouldImportFLWDataFor10000Records() throws Exception {
        URL flwData = this.getClass().getResource("/flws_10000.csv");
        numberOfFlwsToBeCreated = 10000;
        loadFLWData(flwData, numberOfFlwsToBeCreated);
        assertNumberOfFlwsCreated(numberOfFlwsToBeCreated);
    }

    @Test
    public void shouldImportFLWDataFor25000Records() throws Exception {
        URL flwData = this.getClass().getResource("/flws_25000.csv");
        numberOfFlwsToBeCreated = 25000;
        loadFLWData(flwData, numberOfFlwsToBeCreated);
        assertNumberOfFlwsCreated(numberOfFlwsToBeCreated);
    }

    @Test
    public void shouldImportFLWDataFor50000Records() throws Exception {
        URL flwData = this.getClass().getResource("/flws_50000.csv");
        numberOfFlwsToBeCreated = 50000;
        loadFLWData(flwData, numberOfFlwsToBeCreated);
        assertNumberOfFlwsCreated(numberOfFlwsToBeCreated);
    }

    private void loadFLWData(URL flwData, int count) throws Exception {
        String[] arguments = {"FrontLineWorker", flwData.getPath()};
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        CsvImporter.main(arguments);

        stopWatch.stop();
        System.out.println("Total time to load " + count + "flw : " + stopWatch.getTime() + "ms");
    }

    private void assertNumberOfFlwsCreated(int expectedNumberOfFlws) {
        List actualNumberOfFlws = template.findByCriteria(criteria);

        assertEquals(expectedNumberOfFlws, actualNumberOfFlws.get(0));
    }
}