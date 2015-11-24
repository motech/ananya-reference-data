package org.motechproject.ananya.referencedata.flw.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name ="upload_flw_metadata")
public class UploadFlwMetaData {

	
	
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(name="uuid")
	private String uuid;
	
	@Column(name="flw_passed")
	private int flwPassed;
	
	@Column(name="flw_failed")
	private int flwFailed;
	
	@Column(name="date_uploaded")
	private Date uploadedDate;
	

	public Date getUploadedDate() {
		return uploadedDate;
	}

	public void setUploadedDate(Date uploadedDate) {
		this.uploadedDate = uploadedDate;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public int getFlwPassed() {
		return flwPassed;
	}

	public void setFlwPassed(int flwPassed) {
		this.flwPassed = flwPassed;
	}

	public int getFlwFailed() {
		return flwFailed;
	}

	public void setFlwFailed(int flwFailed) {
		this.flwFailed = flwFailed;
	}
	
	
	
	
}
