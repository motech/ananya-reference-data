package org.motechproject.ananya.referencedata.web.domain;

import java.io.Serializable;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class CsvUploadRequest implements Serializable {

	private CommonsMultipartFile fileData;

    public CsvUploadRequest() {
    }

    public CsvUploadRequest(CommonsMultipartFile fileData) {
        this.fileData = fileData;
    }

    public CommonsMultipartFile getFileData() {
        return fileData;
    }

    public void setFileData(CommonsMultipartFile fileData) {
        this.fileData = fileData;
    }

    public String getStringContent() {
        return new String(this.fileData.getBytes());
    }
}
