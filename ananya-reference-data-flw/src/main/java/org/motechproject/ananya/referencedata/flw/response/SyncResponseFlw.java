package org.motechproject.ananya.referencedata.flw.response;

public class SyncResponseFlw {
	
	private boolean responseStatus;
	
	public SyncResponseFlw(boolean responseStatus) {
		this.responseStatus = responseStatus;			
	}
	
	public boolean isResponseStatus() {
		return responseStatus;
	}


}
