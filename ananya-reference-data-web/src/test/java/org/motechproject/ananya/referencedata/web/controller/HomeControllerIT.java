package org.motechproject.ananya.referencedata.web.controller;

import org.junit.Test;
import org.motechproject.ananya.referencedata.web.functional.framework.SpringIntegrationTestCase;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertTrue;

public class HomeControllerIT extends SpringIntegrationTestCase {
    @Autowired
    private HomeController homeController;

    @Test
    public void shouldUseLimitInPropertiesFile() {
        assertTrue(homeController.exceedsMaximumNumberOfRecords(HomeControllerTest.createCSVRecordsWith(501)));
    }
}
