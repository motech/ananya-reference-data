package org.motechproject.ananya.referencedata.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.referencedata.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.domain.Location;
import org.motechproject.ananya.referencedata.SpringIntegrationTest;
import org.motechproject.ananya.referencedata.domain.Designation;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class AllFrontLineWorkersTest extends SpringIntegrationTest{

    @Autowired
    AllFrontLineWorkers allFrontLineWorkers;

    @Before
    @After
    public void setUp() {
        template.deleteAll(template.loadAll(FrontLineWorker.class));
        template.deleteAll(template.loadAll(Location.class));
    }

    @Test
    public void shouldAddFLWToDB() {
        Location location = new Location("district", "block", "panchayat");
        FrontLineWorker frontLineWorker = new FrontLineWorker(1234567890L, "name", Designation.AWW, location);

        allFrontLineWorkers.add(frontLineWorker);

        List<FrontLineWorker> frontLineWorkerList = template.loadAll(FrontLineWorker.class);
        assertEquals(1, frontLineWorkerList.size());
    }

    @Test
    public void shouldGetFLWFromDBByTheGivenId() {
        Location location = new Location("district", "block", "panchayat");
        long msisdn = 1234567890L;
        FrontLineWorker frontLineWorker = new FrontLineWorker(msisdn, "name", Designation.AWW, location);

        allFrontLineWorkers.add(frontLineWorker);

        FrontLineWorker frontLineWorkerFromDb = allFrontLineWorkers.getById(frontLineWorker.getId());
        assertEquals((Long)msisdn, frontLineWorkerFromDb.getMsisdn());
    }

    @Test
    public void shouldUpdateFLWToDB() {
        long msisdn = 1234567890L;
        FrontLineWorker existingFrontLineWorker = new FrontLineWorker(msisdn, "name", Designation.AWW, new Location("district", "block", "panchayat"));
        allFrontLineWorkers.add(existingFrontLineWorker);

        String newName = "new_name";
        Designation newDesignation = Designation.ANM;
        String newDistrict = "district1";
        existingFrontLineWorker.setName(newName);
        existingFrontLineWorker.setDesignation(newDesignation);
        existingFrontLineWorker.setLocation(new Location(newDistrict, "block1", "panchayat1"));
        allFrontLineWorkers.update(existingFrontLineWorker);

        FrontLineWorker frontLineWorkerFromDb = allFrontLineWorkers.getByMsisdn(msisdn);
        assertEquals(newName, frontLineWorkerFromDb.getName());
        assertEquals(newDesignation.name(), frontLineWorkerFromDb.getDesignation());
        assertEquals(newDistrict, frontLineWorkerFromDb.getLocation().getDistrict());
    }

    @Test
    public void shouldGetAllFLWsFromDB() {
        Location location1 = new Location("district", "block", "panchayat");
        FrontLineWorker frontLineWorker1 = new FrontLineWorker(1234567890L, "name", Designation.AWW, location1);
        Location location2 = new Location("district", "block", "panchayat");
        FrontLineWorker frontLineWorker2 = new FrontLineWorker(1234567890L, "name", Designation.AWW, location2);

        allFrontLineWorkers.add(frontLineWorker1);
        allFrontLineWorkers.add(frontLineWorker2);

        List<FrontLineWorker> frontLineWorkerList = allFrontLineWorkers.getAll();
        assertEquals(2, frontLineWorkerList.size());
    }

    @Test
    public void shouldGetAnFLWForTheGivenMsisdn() {
        Location location = new Location("district", "block", "panchayat");
        Long msisdn = 1234567890L;
        FrontLineWorker frontLineWorker = new FrontLineWorker(msisdn, "name", Designation.AWW, location);
        allFrontLineWorkers.add(frontLineWorker);

        FrontLineWorker frontLineWorkerFromDB = allFrontLineWorkers.getByMsisdn(msisdn);

        assertEquals("name", frontLineWorkerFromDB.getName());
    }

    @Test
    public void shouldAddAllFLWs() {
        Location location = new Location("district", "block", "panchayat");
        ArrayList<FrontLineWorker> frontLineWorkers = new ArrayList<FrontLineWorker>();
        frontLineWorkers.add(new FrontLineWorker(1234567890L, "name1", Designation.AWW, location));
        frontLineWorkers.add(new FrontLineWorker(1234567800L, "name2", Designation.AWW, location));

        allFrontLineWorkers.addAll(frontLineWorkers);

        List<FrontLineWorker> frontLineWorkerList = template.loadAll(FrontLineWorker.class);
        assertEquals(2, frontLineWorkerList.size());
    }
}
