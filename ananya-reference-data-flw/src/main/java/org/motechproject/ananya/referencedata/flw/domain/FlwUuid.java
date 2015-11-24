package org.motechproject.ananya.referencedata.flw.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="flwuuid")
public class FlwUuid {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	

	@Column(name="uuid")
	private String uuid;
	
	@Column(name="flw_id")
	private int flwId;

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

	public int getFlwId() {
		return flwId;
	}

	public void setFlwId(int flwId) {
		this.flwId = flwId;
	}

	
}
