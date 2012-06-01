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
}
