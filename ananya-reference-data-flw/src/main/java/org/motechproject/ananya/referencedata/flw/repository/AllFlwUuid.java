package org.motechproject.ananya.referencedata.flw.repository;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.motechproject.ananya.referencedata.flw.domain.FlwUuid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class AllFlwUuid {
		@Autowired
	    private DataAccessTemplate template;

	    public AllFlwUuid() {
			
		}
	    
	    public void add(FlwUuid flwUuid) {
	        template.save(flwUuid);
	    }
	
	    @Transactional(readOnly = true)
	    public List<FlwUuid> getFor(String uuid) {
	        DetachedCriteria criteria = DetachedCriteria.forClass(FlwUuid.class);
	        criteria.add(Restrictions.eq("uuid", uuid).ignoreCase());
	        @SuppressWarnings("unchecked")
			List<FlwUuid> flwUuids = template.findByCriteria(criteria);
	        return flwUuids.isEmpty() ? null :  flwUuids;
	    }

	
	
}
