package org.motechproject.ananya.referencedata.integration;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.referencedata.SpringIntegrationTest;
import org.motechproject.ananya.referencedata.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.repository.AllFrontLineWorkers;
import org.motechproject.ananya.referencedata.repository.AllLocations;
import org.motechproject.ananya.referencedata.request.FrontLineWorkerRequest;
import org.motechproject.ananya.referencedata.request.LocationRequest;
import org.motechproject.ananya.referencedata.service.FrontLineWorkerService;
import org.motechproject.ananya.referencedata.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class FrontLineWorkerServiceIT extends SpringIntegrationTest {

    @Autowired
    private FrontLineWorkerService frontLineWorkerService;
    @Autowired
    private LocationService locationService;
    @Autowired
    private AllFrontLineWorkers allFrontLineWorkers;
    @Autowired
    private AllLocations allLocations;

    @Before
    @After
    public void setUp(){
        template.deleteAll(allFrontLineWorkers.getAll());
        template.deleteAll(allLocations.getAll());
    }

    @Test
    public void shouldAddNewFlw(){
        locationService.add(new LocationRequest("district", "block", "panchayat"));
        frontLineWorkerService.add(new FrontLineWorkerRequest("9999888822", "name", "ASHA",  new LocationRequest("district", "block", "panchayat")));

        List<FrontLineWorker> frontLineWorkerList = allFrontLineWorkers.getAll();

        assertEquals(1, frontLineWorkerList.size());
    }

    @Test
    public void shouldUpdateExistingFlw(){
        locationService.add(new LocationRequest("district", "block", "panchayat"));
        frontLineWorkerService.add(new FrontLineWorkerRequest("9999888822", "name", "ASHA", new LocationRequest("district", "block", "panchayat")));
        frontLineWorkerService.update(new FrontLineWorkerRequest("9999888822", "newName", "ANM", new LocationRequest("district", "block", "panchayat")));

        List<FrontLineWorker> frontLineWorkerList = allFrontLineWorkers.getAll();

        assertEquals(1, frontLineWorkerList.size());
    }

    @Test
    public void shouldGetFLWById() {
        locationService.add(new LocationRequest("district", "block", "panchayat"));
        frontLineWorkerService.add(new FrontLineWorkerRequest("9999888822", "name", "ASHA",  new LocationRequest("district", "block", "panchayat")));
        FrontLineWorker frontLineWorkerFromDb = allFrontLineWorkers.getAll().get(0);

        assertNotNull(frontLineWorkerService.getById(frontLineWorkerFromDb.getId()));
    }
}
