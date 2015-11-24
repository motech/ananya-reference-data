package org.motechproject.ananya.referencedata.flw.repository;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.motechproject.ananya.referencedata.flw.domain.UploadCsvFile;
import org.motechproject.ananya.referencedata.flw.domain.UploadLocationMetaData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class AllUploadLocationMetaData {
	
    @Autowired
    private DataAccessTemplate template;

    public AllUploadLocationMetaData() {
	
    }

    public void add(UploadLocationMetaData uploadLocationMetaData) {
         template.save(uploadLocationMetaData);
    }
    
    @Transactional(readOnly=true)
	public UploadLocationMetaData findLocationMetaDataByUUID(String uuid) {
		DetachedCriteria criteria = DetachedCriteria.forClass(UploadLocationMetaData.class);
        criteria.add(Restrictions.eq("uuid", uuid));
        List uploadMetaDataLst = template.findByCriteria(criteria);
        UploadLocationMetaData uploadLocationMetaData=new UploadLocationMetaData();
        if(uploadMetaDataLst.size() > 0) {
        	uploadLocationMetaData= (UploadLocationMetaData) uploadMetaDataLst.get(0);
        }
        return uploadLocationMetaData;       
	}
    
    

}
