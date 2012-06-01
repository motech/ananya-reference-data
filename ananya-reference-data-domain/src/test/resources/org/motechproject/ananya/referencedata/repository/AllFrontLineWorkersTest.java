package org.motechproject.ananya.referencedata.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.referencedata.SpringIntegrationTest;
import org.motechproject.ananya.referencedata.domain.Designation;
import org.motechproject.ananya.referencedata.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.domain.Location;
import org.springframework.beans.factory.annotation.Autowired;

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
        FrontLineWorker frontLineWorker = new FrontLineWorker(1234567890L, "name", Designation.ANGANWADI, location);

        allFrontLineWorkers.add(frontLineWorker);

        List<FrontLineWorker> frontLineWorkerList = template.loadAll(FrontLineWorker.class);
        assertEquals(1, frontLineWorkerList.size());
    }

    @Test
    public void shouldUpdateFLWToDB() {
        long msisdn = 1234567890L;
        FrontLineWorker existingFrontLineWorker = new FrontLineWorker(msisdn, "name", Designation.ANGANWADI, new Location("district", "block", "panchayat"));
        allFrontLineWorkers.add(existingFrontLineWorker);

        String newName = "new_name";
        Designation newDesignation = Designation.ANM;
        String newDistrict = "district1";
        existingFrontLineWorker.setName(newName);
        existingFrontLineWorker.setDesignation(newDesignation);
        existingFrontLineWorker.setLocation(new Location(newDistrict, "block1", "panchayat1"));
        allFrontLineWorkers.update(existingFrontLineWorker);

        FrontLineWorker frontLineWorkerFromDb = allFrontLineWorkers.getFor(msisdn);
        assertEquals(newName, frontLineWorkerFromDb.getName());
        assertEquals(newDesignation.name(), frontLineWorkerFromDb.getDesignation());
        assertEquals(newDistrict, frontLineWorkerFromDb.getLocation().getDistrict());
    }

    @Test
    public void shouldGetAllFLWsFromDB() {
        Location location1 = new Location("district", "block", "panchayat");
        FrontLineWorker frontLineWorker1 = new FrontLineWorker(1234567890L, "name", Designation.ANGANWADI, location1);
        Location location2 = new Location("district", "block", "panchayat");
        FrontLineWorker frontLineWorker2 = new FrontLineWorker(1234567890L, "name", Designation.ANGANWADI, location2);

        allFrontLineWorkers.add(frontLineWorker1);
        allFrontLineWorkers.add(frontLineWorker2);

        List<FrontLineWorker> frontLineWorkerList = allFrontLineWorkers.getAll();
        assertEquals(2, frontLineWorkerList.size());
    }

    @Test
    public void shouldGetAnFLWForTheGivenMsisdn() {
        Location location = new Location("district", "block", "panchayat");
        Long msisdn = 1234567890L;
        FrontLineWorker frontLineWorker = new FrontLineWorker(msisdn, "name", Designation.ANGANWADI, location);
        allFrontLineWorkers.add(frontLineWorker);

        FrontLineWorker frontLineWorkerFromDB = allFrontLineWorkers.getFor(msisdn);

        assertEquals("name", frontLineWorkerFromDB.getName());
    }
}
