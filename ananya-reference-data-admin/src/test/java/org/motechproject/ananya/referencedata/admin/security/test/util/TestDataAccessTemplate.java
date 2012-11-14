package org.motechproject.ananya.referencedata.admin.security.test.util;

import org.hibernate.SessionFactory;
import org.junit.Ignore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;

@Scope(value = "prototype")
@Ignore
@Component
public class TestDataAccessTemplate extends HibernateTemplate {

    @Autowired
    public TestDataAccessTemplate(@Qualifier(value = "securitySessionFactory") SessionFactory sessionFactory) {
        super(sessionFactory);
        setAllowCreate(true);
    }

    public Object getUniqueResult(String namedQueryName, String[] parameterNames, Object[] parameterValues) {
        return DataAccessUtils.uniqueResult(findByNamedQueryAndNamedParam(namedQueryName, parameterNames, parameterValues));
    }
}