package org.motechproject.ananya.referencedata.admin.security.repository;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "prototype")
public class AdminDataAccessTemplate extends HibernateTemplate {

    @Autowired
    public AdminDataAccessTemplate(@Qualifier(value = "securitySessionFactory") SessionFactory sessionFactory) {
        super(sessionFactory);
        setAllowCreate(false);
    }
}