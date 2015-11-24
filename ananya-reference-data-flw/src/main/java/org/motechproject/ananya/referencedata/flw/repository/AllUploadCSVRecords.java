package org.motechproject.ananya.referencedata.flw.repository;


import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.UploadCsvFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public class AllUploadCSVRecords {
	
	@Autowired
	private DataAccessTemplate template;
	
	public AllUploadCSVRecords(){
		
	}
	
	@Transactional
	public void add(UploadCsvFile uploadCsvFile) {
         template.save(uploadCsvFile);
         
	}
	
	@Transactional(readOnly=true)
	public UploadCsvFile findCsvFileByUUID(String uuid) {
		DetachedCriteria criteria = DetachedCriteria.forClass(UploadCsvFile.class);
        criteria.add(Restrictions.eq("uuid", uuid));
        List uploadCsvList = template.findByCriteria(criteria);
        if(uploadCsvList.size() > 0) {
        	return (UploadCsvFile) uploadCsvList.get(0);
        }
        return null;       
	}
	

}
