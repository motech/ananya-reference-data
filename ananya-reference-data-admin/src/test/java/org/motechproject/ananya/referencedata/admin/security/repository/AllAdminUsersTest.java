package org.motechproject.ananya.referencedata.admin.security.repository;

import org.junit.Test;
import org.motechproject.ananya.referencedata.admin.security.model.AdminUser;
import org.motechproject.ananya.referencedata.admin.security.repository.AllAdminUsers;
import org.motechproject.ananya.referencedata.admin.security.test.util.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;

public class AllAdminUsersTest extends SpringIntegrationTest {

    @Autowired
    AllAdminUsers allAdminUsers;

    @Test
    public void shouldSaveAdminUsers() {
        AdminUser adminUser = new AdminUser("admin", "password");
        allAdminUsers.save(adminUser);
        AdminUser adminUserFromDb = allAdminUsers.findByName("admin");
        assertEquals(adminUser, adminUserFromDb);
    }
}
