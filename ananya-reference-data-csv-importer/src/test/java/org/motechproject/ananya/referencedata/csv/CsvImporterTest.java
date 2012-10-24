package org.motechproject.ananya.referencedata.csv;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.referencedata.csv.exception.FileReadException;
import org.motechproject.ananya.referencedata.csv.exception.InvalidArgumentException;
import org.motechproject.ananya.referencedata.csv.exception.WrongNumberArgsException;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.springframework.test.annotation.ExpectedException;

import java.net.URL;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class CsvImporterTest extends SpringIntegrationTest {
    @Before
    @After
    public void setUpAndTearDown() {
        template.deleteAll(template.loadAll(FrontLineWorker.class));
        template.deleteAll(template.loadAll(Location.class));
    }

    @Test
    public void shouldImportLocationData() throws Exception {
        URL locationData = this.getClass().getResource("/locationData.csv");
        String[] arguments = {"Location", locationData.getPath()};

        CsvImporter.main(arguments);

        List<Location> locationDimensions = template.loadAll(Location.class);
        assertEquals(1, locationDimensions.size());
        assertEquals("D2", locationDimensions.get(0).getDistrict());
        assertEquals("B2", locationDimensions.get(0).getBlock());
        assertEquals("P2", locationDimensions.get(0).getPanchayat());
    }

    @Test
    public void shouldImportFlwData() throws Exception {
        Location location = new Location("D1", "B1", "P1", "VALID");
        template.save(location);
        URL flwData = this.getClass().getResource("/flwData.csv");
        String[] arguments = {"FrontLineWorker", flwData.getPath()};

        CsvImporter.main(arguments);

        List<FrontLineWorker> frontLineWorkers = template.loadAll(FrontLineWorker.class);
        assertEquals(1, frontLineWorkers.size());
        assertEquals("919988776655", frontLineWorkers.get(0).getMsisdn().toString());
    }

    @Test
    @ExpectedException(InvalidArgumentException.class)
    public void shouldFailForRandomEntityNames() throws Exception {
        Location location = new Location("D1", "B1", "P1", "VALID");
        template.save(location);
        URL flwData = this.getClass().getResource("/flwData.csv");
        String[] arguments = {"RandomEntityName", flwData.getPath()};

        CsvImporter.main(arguments);
    }

    @Test
    @ExpectedException(WrongNumberArgsException.class)
    public void shouldFailForWrongNumberOfArguments() throws Exception {
        Location location = new Location("D1", "B1", "P1", "VALID");
        template.save(location);
        URL flwData = this.getClass().getResource("/flwData.csv");
        String[] arguments = {"FrontLineWorker", flwData.getPath(), "unwanted-argument"};

        CsvImporter.main(arguments);
    }

    @Test
    @ExpectedException(FileReadException.class)
    public void shouldFailForInvalidImportFile() throws Exception {
        Location location = new Location("D1", "B1", "P1", "VALID");
        template.save(location);
        String[] arguments = {"FrontLineWorker", "random-file-path.csv"};

        CsvImporter.main(arguments);
    }
}
