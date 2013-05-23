package org.motechproject.ananya.referencedata.flw.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.referencedata.flw.SpringIntegrationTest;
import org.motechproject.ananya.referencedata.flw.domain.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

public class AllFrontLineWorkersTest extends SpringIntegrationTest {

    @Autowired
    AllFrontLineWorkers allFrontLineWorkers;
    private Location location;

    @Before
    public void setUp() {
        template.deleteAll(template.loadAll(FrontLineWorker.class));
        template.deleteAll(template.loadAll(Location.class));
        location = new Location("state", "district", "block", "panchayat", LocationStatus.NOT_VERIFIED, null);
        template.save(location);
    }

    @After
    public void tearDown() {
        template.deleteAll(template.loadAll(FrontLineWorker.class));
        template.deleteAll(template.loadAll(Location.class));
    }

    @Test
    public void shouldAddFLWToDB() {
        template.save(location);
        FrontLineWorker frontLineWorker = new FrontLineWorker(1234567890L, "name", Designation.AWW, location);

        allFrontLineWorkers.add(frontLineWorker);

        List<FrontLineWorker> frontLineWorkerList = template.loadAll(FrontLineWorker.class);
        assertEquals(1, frontLineWorkerList.size());
        assertNotNull(frontLineWorkerList.get(0).getLastModified());
        assertNotNull(frontLineWorkerList.get(0).getFlwId());
    }

    @Test
    public void shouldUpdateFLWToDB() {
        long msisdn = 1234567890L;
        FrontLineWorker existingFrontLineWorker = new FrontLineWorker(msisdn, "name", Designation.AWW, location);
        String newName = "new_name";
        Designation newDesignation = Designation.ANM;
        String newDistrict = "District1";
        existingFrontLineWorker.setName(newName);
        LocationStatus status = LocationStatus.VALID;
        Location newLocation = new Location("state", newDistrict, "block1", "panchayat1", status, null);
        template.save(existingFrontLineWorker);
        template.save(newLocation);
        existingFrontLineWorker.setDesignation(newDesignation);
        existingFrontLineWorker.setLocation(newLocation);

        allFrontLineWorkers.createOrUpdate(existingFrontLineWorker);

        FrontLineWorker frontLineWorkerFromDb = allFrontLineWorkers.getByMsisdn(msisdn).get(0);
        assertEquals(newName, frontLineWorkerFromDb.getName());
        assertEquals(newDesignation.name(), frontLineWorkerFromDb.getDesignation());
        assertEquals(newDistrict, frontLineWorkerFromDb.getLocation().getDistrict());
        assertEquals(status, frontLineWorkerFromDb.getLocation().getStatus());
        assertNotNull(frontLineWorkerFromDb.getLastModified());
    }

    @Test
    public void shouldAddOrUpdateFLWToDB() {
        long msisdn = 1234567890L;
        FrontLineWorker existingFrontLineWorker = new FrontLineWorker(msisdn, "name", Designation.AWW, location);
        String newName = "new_name";
        Designation newDesignation = Designation.ANM;
        String newDistrict = "District1";
        existingFrontLineWorker.setName(newName);
        LocationStatus status = LocationStatus.VALID;
        Location newLocation = new Location("state", newDistrict, "block1", "panchayat1", status, null);
        template.save(newLocation);
        existingFrontLineWorker.setDesignation(newDesignation);
        existingFrontLineWorker.setLocation(newLocation);

        allFrontLineWorkers.createOrUpdate(existingFrontLineWorker);

        FrontLineWorker frontLineWorkerFromDb = allFrontLineWorkers.getByMsisdn(msisdn).get(0);
        assertNotNull(existingFrontLineWorker.getId());
        assertEquals(newName, frontLineWorkerFromDb.getName());
        assertEquals(newDesignation.name(), frontLineWorkerFromDb.getDesignation());
        assertEquals(newDistrict, frontLineWorkerFromDb.getLocation().getDistrict());
        assertEquals(status, frontLineWorkerFromDb.getLocation().getStatus());
        assertNotNull(frontLineWorkerFromDb.getLastModified());
    }

    @Test
    public void shouldGetAllFLWsFromDB() {
        FrontLineWorker frontLineWorker1 = new FrontLineWorker(1234567890L, "name", Designation.AWW, location);
        FrontLineWorker frontLineWorker2 = new FrontLineWorker(1234567890L, "name", Designation.AWW, location);

        allFrontLineWorkers.add(frontLineWorker1);
        allFrontLineWorkers.add(frontLineWorker2);

        List<FrontLineWorker> frontLineWorkerList = allFrontLineWorkers.getAll();
        assertEquals(2, frontLineWorkerList.size());
    }

    @Test
    public void shouldGetAnFLWForTheGivenMsisdn() {
        Long msisdn = 1234567890L;
        FrontLineWorker frontLineWorker = new FrontLineWorker(msisdn, "name", Designation.AWW, location);
        allFrontLineWorkers.add(frontLineWorker);

        List<FrontLineWorker> frontLineWorkerFromDB = allFrontLineWorkers.getByMsisdn(msisdn);

        assertEquals(1, frontLineWorkerFromDB.size());
        assertEquals("name", frontLineWorkerFromDB.get(0).getName());
    }

    @Test
    public void shouldAddAllFLWs() {
        ArrayList<FrontLineWorker> frontLineWorkers = new ArrayList<FrontLineWorker>();
        frontLineWorkers.add(new FrontLineWorker(1234567890L, "name1", Designation.AWW, location));
        frontLineWorkers.add(new FrontLineWorker(1234567800L, "name2", Designation.AWW, location));

        allFrontLineWorkers.createOrUpdateAll(frontLineWorkers);

        List<FrontLineWorker> frontLineWorkerList = template.loadAll(FrontLineWorker.class);
        assertEquals(2, frontLineWorkerList.size());
    }

    @Test
    public void shouldGetFLWByFlwId() {
        UUID flwId = UUID.randomUUID();
        Long msisdn = 1234567890L;
        String name = "name";
        Designation anm = Designation.ANM;
        allFrontLineWorkers.add(new FrontLineWorker(msisdn, name, anm, location, flwId, VerificationStatus.INVALID, null));

        FrontLineWorker actualFrontLineWorker = allFrontLineWorkers.getByFlwId(flwId);

        assertEquals(msisdn, actualFrontLineWorker.getMsisdn());
        assertEquals(flwId, actualFrontLineWorker.getFlwId());
        assertEquals(name, actualFrontLineWorker.getName());
    }

    @Test
    public void shouldReturnNullWhenFrontLineWorkerByFlwIdDoesNotExist() {
        FrontLineWorker frontLineWorker = allFrontLineWorkers.getByFlwId(UUID.randomUUID());
        assertNull(frontLineWorker);
    }

    @Test
    public void shouldGetAllFlwWithGivenLocation() {
        List<FrontLineWorker> expectedFrontLineWorkers = new ArrayList<FrontLineWorker>() {{
            add(new FrontLineWorker(1234567890L, "name", Designation.ANM, location));
            add(new FrontLineWorker(1234567891L, "name", Designation.ANM, location));
        }};
        template.saveOrUpdateAll(expectedFrontLineWorkers);

        List<FrontLineWorker> actualFrontLineWorkers = allFrontLineWorkers.getForLocation(location);

        assertEquals(expectedFrontLineWorkers, actualFrontLineWorkers);
    }

    @Test
    public void shouldGetByMsisdnWithNoStatus() {
        final long msisdn = 1234567890L;
        List<FrontLineWorker> flwWithStatus = new ArrayList<FrontLineWorker>() {{
            add(new FrontLineWorker(msisdn, "name", Designation.ANM, location, UUID.randomUUID(), VerificationStatus.SUCCESS, "reason"));
        }};
        ArrayList<FrontLineWorker> flwWithoutStatus = new ArrayList<FrontLineWorker>() {{
            add(new FrontLineWorker(msisdn, "name", Designation.ANM, location));
        }};
        template.saveOrUpdateAll(flwWithoutStatus);
        template.saveOrUpdateAll(flwWithStatus);

        List<FrontLineWorker> flwFromDb = allFrontLineWorkers.getByMsisdnWithStatus(msisdn);
        assertEquals(1, flwFromDb.size());
        assertEquals(flwWithStatus, flwFromDb);
    }
}