package org.motechproject.ananya.referencedata.admin.security.repository;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.motechproject.ananya.referencedata.admin.security.model.AdminUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class AllAdminUsers {

    @Autowired
    private AdminDataAccessTemplate template;

    public AdminUser findByName(String username) {
        DetachedCriteria criteria = DetachedCriteria.forClass(AdminUser.class);
        criteria.add(Restrictions.eq("name", username));
        List<AdminUser> adminUsers = template.findByCriteria(criteria, 0, 1);
        return adminUsers.get(0);
    }

    public void save(AdminUser adminUser) {
        template.save(adminUser);
    }

}
