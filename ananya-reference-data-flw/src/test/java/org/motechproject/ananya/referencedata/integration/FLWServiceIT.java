package org.motechproject.ananya.referencedata.integration;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.referencedata.SpringIntegrationTest;
import org.motechproject.ananya.referencedata.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.repository.AllFrontLineWorkers;
import org.motechproject.ananya.referencedata.repository.AllLocations;
import org.motechproject.ananya.referencedata.request.FLWRequest;
import org.motechproject.ananya.referencedata.request.LocationRequest;
import org.motechproject.ananya.referencedata.service.FLWService;
import org.motechproject.ananya.referencedata.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static junit.framework.Assert.assertEquals;

public class FLWServiceIT extends SpringIntegrationTest {

    @Autowired
    private FLWService flwService;
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
        flwService.add(new FLWRequest("9999888822", "name", "ASHA", "district", "block", "panchayat"));

        List<FrontLineWorker> frontLineWorkerList = allFrontLineWorkers.getAll();

        assertEquals(1, frontLineWorkerList.size());
    }

    @Test
    public void shouldUpdateExistingFlw(){
        locationService.add(new LocationRequest("district", "block", "panchayat"));
        flwService.add(new FLWRequest("9999888822", "name", "ASHA", "district", "block", "panchayat"));
        flwService.update(new FLWRequest("9999888822", "newName", "ANM", "district", "block", "panchayat"));

        List<FrontLineWorker> frontLineWorkerList = allFrontLineWorkers.getAll();

        assertEquals(1, frontLineWorkerList.size());
    }
}
