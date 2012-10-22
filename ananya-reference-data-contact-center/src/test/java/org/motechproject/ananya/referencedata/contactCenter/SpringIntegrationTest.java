package org.motechproject.ananya.referencedata.contactCenter;

import org.junit.After;
import org.junit.runner.RunWith;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-contact-center.xml")
public abstract class SpringIntegrationTest {

    @Autowired
    @Qualifier("testDataAccessTemplate")
    protected TestDataAccessTemplate template;

    @After
    public void tearDown(){
        template.deleteAll(template.loadAll(FrontLineWorker.class));
        template.deleteAll(template.loadAll(Location.class));
    }
}