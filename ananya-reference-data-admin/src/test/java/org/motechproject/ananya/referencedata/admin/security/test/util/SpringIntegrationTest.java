package org.motechproject.ananya.referencedata.admin.security.test.util;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.motechproject.ananya.referencedata.admin.security.model.AdminUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-AdminConsole.xml")
public abstract class SpringIntegrationTest {

    @Autowired
    @Qualifier("testDataAccessTemplate")
    protected TestDataAccessTemplate template;

    @Before
    @After
    public void tearDown(){
        template.deleteAll(template.loadAll(AdminUser.class));
    }

}
