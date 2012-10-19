package org.motechproject.ananya.referencedata.flw.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.referencedata.SpringIntegrationTest;
import org.motechproject.ananya.referencedata.flw.domain.Designation;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class AllFrontLineWorkersTest extends SpringIntegrationTest{

    @Autowired
    AllFrontLineWorkers allFrontLineWorkers;
    private Location location;

    @Before
    public void setUp() {
        template.deleteAll(template.loadAll(FrontLineWorker.class));
        template.deleteAll(template.loadAll(Location.class));
        location = new Location("district", "block", "panchayat");
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
        assertNotNull(frontLineWorkerList.get(0).getFlwGuid());
    }

    @Test
    public void shouldUpdateFLWToDB() {
        long msisdn = 1234567890L;
        FrontLineWorker existingFrontLineWorker = new FrontLineWorker(msisdn, "name", Designation.AWW, location);
        String newName = "new_name";
        Designation newDesignation = Designation.ANM;
        String newDistrict = "district1";
        existingFrontLineWorker.setName(newName);
        Location newLocation = new Location(newDistrict, "block1", "panchayat1");
        template.save(existingFrontLineWorker);
        template.save(newLocation);
        existingFrontLineWorker.setDesignation(newDesignation);
        existingFrontLineWorker.setLocation(newLocation);

        allFrontLineWorkers.createOrUpdate(existingFrontLineWorker);

        FrontLineWorker frontLineWorkerFromDb = allFrontLineWorkers.getByMsisdn(msisdn).get(0);
        assertEquals(newName, frontLineWorkerFromDb.getName());
        assertEquals(newDesignation.name(), frontLineWorkerFromDb.getDesignation());
        assertEquals(newDistrict, frontLineWorkerFromDb.getLocation().getDistrict());
        assertNotNull(frontLineWorkerFromDb.getLastModified());
    }

    @Test
    public void shouldAddOrUpdateFLWToDB() {
        long msisdn = 1234567890L;
        FrontLineWorker existingFrontLineWorker = new FrontLineWorker(msisdn, "name", Designation.AWW, location);
        String newName = "new_name";
        Designation newDesignation = Designation.ANM;
        String newDistrict = "district1";
        existingFrontLineWorker.setName(newName);
        Location newLocation = new Location(newDistrict, "block1", "panchayat1");
        template.save(newLocation);
        existingFrontLineWorker.setDesignation(newDesignation);
        existingFrontLineWorker.setLocation(newLocation);

        allFrontLineWorkers.createOrUpdate(existingFrontLineWorker);

        FrontLineWorker frontLineWorkerFromDb = allFrontLineWorkers.getByMsisdn(msisdn).get(0);
        assertNotNull(existingFrontLineWorker.getId());
        assertEquals(newName, frontLineWorkerFromDb.getName());
        assertEquals(newDesignation.name(), frontLineWorkerFromDb.getDesignation());
        assertEquals(newDistrict, frontLineWorkerFromDb.getLocation().getDistrict());
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
    public void shouldGetFLWByGuid() {
        String guid = "guid";
        Long msisdn = 1234567890L;
        String name = "name";
        Designation anm = Designation.ANM;
        allFrontLineWorkers.add(new FrontLineWorker(msisdn, name, anm, location, guid, null, null));

        FrontLineWorker actualFrontLineWorker = allFrontLineWorkers.getByGuid(guid);

        assertEquals(msisdn, actualFrontLineWorker.getMsisdn());
        assertEquals(guid, actualFrontLineWorker.getFlwGuid());
        assertEquals(name, actualFrontLineWorker.getName());
    }

    @Test
    public void shouldReturnNullWhenFrontLineWorkerByGuidDoesNotExist() {
        FrontLineWorker frontLineWorker = allFrontLineWorkers.getByGuid("abcd");
        assertNull(frontLineWorker);
    }

    @Test
    public void shouldUpdateFrontLineWorker() {
        FrontLineWorker frontLineWorker = new FrontLineWorker(9988776655L, "name", Designation.ANM, location, "guid", null, null);
        template.save(frontLineWorker);
        frontLineWorker.setReason("random");
        frontLineWorker.setVerificationStatus("INVALID");

        allFrontLineWorkers.update(frontLineWorker);

        FrontLineWorker frontLineWOrkerFromDb = allFrontLineWorkers.getByGuid("guid");
        assertEquals(frontLineWorker.getId(), frontLineWOrkerFromDb.getId());
        assertEquals(frontLineWorker.getFlwGuid(), frontLineWOrkerFromDb.getFlwGuid());
        assertEquals(frontLineWorker.getName(), frontLineWOrkerFromDb.getName());
        assertEquals(frontLineWorker.getVerificationStatus(), frontLineWOrkerFromDb.getVerificationStatus());
        assertEquals(frontLineWorker.getReason(), frontLineWOrkerFromDb.getReason());
    }
}
