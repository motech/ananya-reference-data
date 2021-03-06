package org.motechproject.ananya.referencedata.flw.repository;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.motechproject.ananya.referencedata.flw.domain.AnanyaReferenceDataProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class AllAnanyaReferenceDataProperties {
    
    @Autowired
    private DataAccessTemplate template;

    @Transactional(readOnly = true)
    public String getForSyncSwitch() {
        return getByName("syncSwitch");
    }

    @Transactional(readOnly = true)
    public List<AnanyaReferenceDataProperty> getAllProperties() {
        return template.loadAll(AnanyaReferenceDataProperty.class);
    }

    private String getByName(String name) {
        DetachedCriteria criteria = DetachedCriteria.forClass(AnanyaReferenceDataProperty.class);

        criteria.add(Restrictions.eq("name", name).ignoreCase());

        List namePropertyList = template.findByCriteria(criteria);
        AnanyaReferenceDataProperty forName = namePropertyList.isEmpty() ? null : (AnanyaReferenceDataProperty) namePropertyList.get(0);

        return forName == null ? StringUtils.EMPTY : forName.getValue();
    }
}
