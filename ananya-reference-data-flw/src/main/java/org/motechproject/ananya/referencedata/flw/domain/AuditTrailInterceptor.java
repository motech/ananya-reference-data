package org.motechproject.ananya.referencedata.flw.domain;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Arrays;

public class AuditTrailInterceptor extends EmptyInterceptor {

    @Override
    public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        setLastModified(state, propertyNames);
        return true;
    }

    @Override
    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
        setLastModified(currentState, propertyNames);
        return true;
    }

    private void setLastModified(Object[] state, String[] propertyNames) {
        int lastModifiedPropertyIndex = Arrays.asList(propertyNames).indexOf("lastModified");
        if (lastModifiedPropertyIndex >= 0)
            state[lastModifiedPropertyIndex] = new Timestamp(DateTime.now().getMillis());
    }
}