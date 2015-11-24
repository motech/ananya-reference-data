package org.motechproject.ananya.referencedata.flw.repository;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.motechproject.ananya.referencedata.flw.domain.UploadCsvFile;
import org.motechproject.ananya.referencedata.flw.domain.UploadFlwMetaData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public class AllUploadFlwMetaData {
	
	@Autowired
    private DataAccessTemplate template;
	public AllUploadFlwMetaData(){
	}
	
	public void add(UploadFlwMetaData uploadFlwMetaData){
		template.save(uploadFlwMetaData);
	}
	
	@Transactional(readOnly=true)
	public UploadFlwMetaData findFlwMetaDataByUUID(String uuid) {
		DetachedCriteria criteria = DetachedCriteria.forClass(UploadFlwMetaData.class);
        criteria.add(Restrictions.eq("uuid", uuid));
        List uploadMetaDataLst = template.findByCriteria(criteria);
        UploadFlwMetaData uploadFlwMetaData=new UploadFlwMetaData();
        if(uploadMetaDataLst.size() > 0) {
        	uploadFlwMetaData= (UploadFlwMetaData) uploadMetaDataLst.get(0);
        }
        return uploadFlwMetaData;       
	}
	
	

}
