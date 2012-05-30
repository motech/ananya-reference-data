package org.motechproject.ananya.referencedata.integration;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.referencedata.domain.FLWData;
import org.motechproject.ananya.referencedata.repository.AllFLWData;
import org.motechproject.ananya.referencedata.repository.AllLocations;
import org.motechproject.ananya.referencedata.request.FLWRequest;
import org.motechproject.ananya.referencedata.request.LocationRequest;
import org.motechproject.ananya.referencedata.service.FLWService;
import org.motechproject.ananya.referencedata.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static junit.framework.Assert.assertEquals;

public class FLWServiceIT extends SpringIntegrationTest{

    @Autowired
    private FLWService flwService;
    @Autowired
    private LocationService locationService;
    @Autowired
    private AllFLWData allFLWData;
    @Autowired
    private AllLocations allLocations;

    @Before
    @After
    public void setUp(){
        template.deleteAll(allFLWData.getAll());
        template.deleteAll(allLocations.getAll());
    }

    @Test
    public void shouldAddNewFlw(){
        locationService.add(new LocationRequest("district", "block", "panchayat"));
        flwService.add(new FLWRequest("9999888822", "name", "ASHA", "district", "block", "panchayat"));

        List<FLWData> flwDataList = allFLWData.getAll();

        assertEquals(1, flwDataList.size());
    }
}
