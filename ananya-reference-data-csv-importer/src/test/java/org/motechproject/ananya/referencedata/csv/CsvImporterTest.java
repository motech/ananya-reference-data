package org.motechproject.ananya.referencedata.csv;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.referencedata.csv.exception.FileReadException;
import org.motechproject.ananya.referencedata.csv.exception.InvalidArgumentException;
import org.motechproject.ananya.referencedata.csv.exception.WrongNumberArgsException;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.LocationStatus;
import org.springframework.test.annotation.ExpectedException;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CsvImporterTest extends SpringIntegrationTest {
    @Before
    @After
    public void setUpAndTearDown() {
        template.deleteAll(template.loadAll(FrontLineWorker.class));
        template.deleteAll(template.loadAll(Location.class));
    }

    @Test
    public void shouldImportLocationData() throws Exception {
        Location location1 = new Location("D2", "B2", "P2", LocationStatus.NOT_VERIFIED, null);
        Location location2 = new Location("D2", "B2", "P4", LocationStatus.NOT_VERIFIED, null);
        Location location3 = new Location("D5", "B5", "P5", LocationStatus.VALID, null);
        Location location4 = new Location("D3", "B3", "P3", LocationStatus.VALID, null);
        Location location5 = new Location("D6", "B6", "P6", LocationStatus.NOT_VERIFIED, null);
        template.save(location1);
        template.save(location2);
        template.save(location3);
        template.save(location4);
        template.save(location5);
        URL locationData = this.getClass().getResource("/locationData.csv");
        String[] arguments = {"Location", locationData.getPath()};

        CsvImporter.main(arguments);

        List<Location> locationDimensions = template.loadAll(Location.class);
        assertEquals(6, locationDimensions.size());

        Location expectedLocation1 = new Location("D2", "B2", "P2", LocationStatus.VALID, null);
        Location expectedLocation2 = new Location("D2", "B2", "P4", LocationStatus.INVALID, expectedLocation1);
        Location expectedLocation5 = new Location("D1", "B1", "P1", LocationStatus.VALID, null);
        Location expectedLocation3 = new Location("D3", "B3", "P3", LocationStatus.INVALID, expectedLocation5);
        Location expectedLocation4 = new Location("D5", "B5", "P5", LocationStatus.VALID, null);
        Location expectedLocation6 = new Location("D6", "B6", "P6", LocationStatus.IN_REVIEW, null);
        assertTrue(locationDimensions.contains(expectedLocation1));
        assertTrue(locationDimensions.contains(expectedLocation2));
        assertTrue(locationDimensions.contains(expectedLocation3));
        assertTrue(locationDimensions.contains(expectedLocation4));
        assertTrue(locationDimensions.contains(expectedLocation5));
        assertTrue(locationDimensions.contains(expectedLocation6));
    }

    @Test
    public void shouldImportFlwData() throws Exception {
        Location location = new Location("D1", "B1", "P1", LocationStatus.VALID, null);
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
        Location location = new Location("D1", "B1", "P1", LocationStatus.VALID, null);
        template.save(location);
        URL flwData = this.getClass().getResource("/flwData.csv");
        String[] arguments = {"RandomEntityName", flwData.getPath()};

        CsvImporter.main(arguments);
    }

    @Test
    @ExpectedException(WrongNumberArgsException.class)
    public void shouldFailForWrongNumberOfArguments() throws Exception {
        Location location = new Location("D1", "B1", "P1", LocationStatus.VALID, null);
        template.save(location);
        URL flwData = this.getClass().getResource("/flwData.csv");
        String[] arguments = {"FrontLineWorker", flwData.getPath(), "unwanted-argument"};

        CsvImporter.main(arguments);
    }

    @Test
    @ExpectedException(FileReadException.class)
    public void shouldFailForInvalidImportFile() throws Exception {
        Location location = new Location("D1", "B1", "P1", LocationStatus.VALID, null);
        template.save(location);
        String[] arguments = {"FrontLineWorker", "random-file-path.csv"};

        CsvImporter.main(arguments);
    }

    @Test
    public void shouldImportLocationFromCsv() throws IOException {
        CsvImporter csvImporter = new CsvImporter();
        Location location1 = new Location("D2", "B2", "P2", LocationStatus.NOT_VERIFIED, null);
        template.save(location1);
        CommonsMultipartFile locationCsvFile = mock(CommonsMultipartFile.class);
        String csvData = "district,block,panchayat,status,newDistrict,newBlock,newPanchayat\n" +
                "\"D2\",\"B2\",\"P2\",\"VALID\"";
        when(locationCsvFile.getBytes()).thenReturn(csvData.getBytes());

        byte[] errors = csvImporter.importLocation(locationCsvFile);

        List<Location> locationDimensions = template.loadAll(Location.class);
        assertEquals(1, locationDimensions.size());
        assertEquals(LocationStatus.VALID, locationDimensions.get(0).getStatus());
        assertEquals(0, errors.length);
    }

    @Test
    public void shouldImportLocationFromCsvAndReturnErrorsIfAny() throws IOException {
        CsvImporter csvImporter = new CsvImporter();
        CommonsMultipartFile locationCsvFile = mock(CommonsMultipartFile.class);
        String csvData = "district,block,panchayat,status,newDistrict,newBlock,newPanchayat\n" +
                "\"D2\",\"B2\",\"P2\",\"VALID\"";
        when(locationCsvFile.getBytes()).thenReturn(csvData.getBytes());

        byte[] errors = csvImporter.importLocation(locationCsvFile);

        List<Location> locationDimensions = template.loadAll(Location.class);
        assertEquals(0, locationDimensions.size());
        assertEquals("district,block,panchayat,status,newDistrict,newBlock,newPanchayat,error\n" +
                "\"D2\",\"B2\",\"P2\",\"VALID\",\"\",\"\",\"\",\"[Location is not present in DB]\"\n", new String(errors));
    }

}