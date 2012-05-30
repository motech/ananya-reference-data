package org.motechproject.ananya.referencedata.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.referencedata.SpringIntegrationTest;
import org.motechproject.ananya.referencedata.domain.Designation;
import org.motechproject.ananya.referencedata.domain.FLWData;
import org.motechproject.ananya.referencedata.domain.Location;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class AllFLWDataTest extends SpringIntegrationTest{

    @Autowired
    AllFLWData allFLWData;

    @Before
    @After
    public void setUp() {
        template.deleteAll(template.loadAll(FLWData.class));
    }

    @Test
    public void shouldAddFLWToDB() {
        Location location = new Location("district", "block", "panchayat");
        FLWData flwData = new FLWData(1234567890L, "name", Designation.ANGANWADI, location);

        allFLWData.add(flwData);

        List<FLWData> flwDataList = template.loadAll(FLWData.class);
        assertEquals(1, flwDataList.size());
    }

    @Test
    public void shouldGetAllFLWsFromDB() {
        Location location1 = new Location("district", "block", "panchayat");
        FLWData flwData1 = new FLWData(1234567890L, "name", Designation.ANGANWADI, location1);
        Location location2 = new Location("district", "block", "panchayat");
        FLWData flwData2 = new FLWData(1234567890L, "name", Designation.ANGANWADI, location2);

        allFLWData.add(flwData1);
        allFLWData.add(flwData2);

        List<FLWData> flwDataList = allFLWData.getAll();
        assertEquals(2, flwDataList.size());
    }
}
