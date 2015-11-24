package org.motechproject.ananya.referencedata.flw.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author vishnu
 *
 */
@Entity
@Table(name ="upload_loc_metadata")
public class UploadLocationMetaData  {
	
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(name="uuid")
	private String uuid;
	
	@Column(name="passed_valid")
	private int passedValid;
	
	@Column(name="passed_invalid")
	private int passedInvalid;
	
	@Column(name="failed_valid")
	private int failedValid;
	
	@Column(name="failed_invalid")
	private int failedInvalid;
	
	@Column(name="date_uploaded")
	private Date uploadedDate;
	
	
	

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

	public int getPassedValid() {
		return passedValid;
	}

	public void setPassedValid(int passedValid) {
		this.passedValid = passedValid;
	}

	public int getPassedInvalid() {
		return passedInvalid;
	}

	public void setPassedInvalid(int passedInvalid) {
		this.passedInvalid = passedInvalid;
	}

	public int getFailedValid() {
		return failedValid;
	}

	public void setFailedValid(int failedValid) {
		this.failedValid = failedValid;
	}

	public int getFailedInvalid() {
		return failedInvalid;
	}

	public void setFailedInvalid(int failedInvalid) {
		this.failedInvalid = failedInvalid;
	}

	public Date getUploadedDate() {
		return uploadedDate;
	}

	public void setUploadedDate(Date uploadedDate) {
		this.uploadedDate = uploadedDate;
	}
}
