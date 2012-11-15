package org.motechproject.ananya.referencedata.web.domain;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class CsvUploadRequest {
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
}
