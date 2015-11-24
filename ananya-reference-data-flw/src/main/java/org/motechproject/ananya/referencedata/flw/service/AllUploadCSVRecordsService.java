package org.motechproject.ananya.referencedata.flw.service;

import java.util.UUID;

import org.motechproject.ananya.referencedata.flw.domain.UploadCsvFile;
import org.motechproject.ananya.referencedata.flw.repository.AllUploadCSVRecords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AllUploadCSVRecordsService {

	private AllUploadCSVRecords allUploadCSVRecords;
	
	
	
	@Autowired
	public AllUploadCSVRecordsService(AllUploadCSVRecords allUploadCSVRecords){
		this.allUploadCSVRecords=allUploadCSVRecords;
	}
	
	public String csvContent(String content){
		UploadCsvFile uploadCsvFile=new UploadCsvFile();
		uploadCsvFile.setUuid(UUID.randomUUID().toString());
		uploadCsvFile.setContent(content);
		allUploadCSVRecords.add(uploadCsvFile);
		return uploadCsvFile.getUuid();
	}
	
	
	
	public UploadCsvFile findCsvFileByUUID(String uuid) {
		return allUploadCSVRecords.findCsvFileByUUID(uuid);     
	}
}
