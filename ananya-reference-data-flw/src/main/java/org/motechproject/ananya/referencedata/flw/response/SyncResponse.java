package org.motechproject.ananya.referencedata.flw.response;

public class SyncResponse {
	
	private String status;
	
	private boolean responseStatus;
	
	public SyncResponse(String status, boolean responseStatus) {
		this.status = status;
		this.responseStatus = responseStatus;
	}

	public String getStatus() {
		return status;
	}

	public boolean isResponseStatus() {
		return responseStatus;
	}

	

	
	
	
	
	

}
