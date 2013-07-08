package org.motechproject.ananya.referencedata.csv;

import org.apache.commons.lang.StringUtils;
import org.motechproject.importer.CSVDataImporter;

public enum ImportType {
    FrontLineWorker() {
        @Override
        void performAction(String importFile, CSVDataImporter csvDataImporter) {
            csvDataImporter.importData(ImportType.FrontLineWorker.name(), importFile);
        }

        @Override
        public String successMessage() {
            return "FLWs Uploaded Successfully.";
        }

        @Override
        public String errorMessage() {
            return "FLW file can have a maximum of 5000 records.";
        }

        @Override
        public String responseFilePrefix() {
            return "flw_upload_failures";
        }
    }, Location {
        @Override
        void performAction(String importFile, CSVDataImporter csvDataImporter) {
            csvDataImporter.importData(ImportType.Location.name(), importFile);
        }

        @Override
        public String successMessage() {
            return "Locations Uploaded Successfully.";
        }

        @Override
        public String errorMessage() {
            return "Location file can have a maximum of 5000 records.";
        }

        @Override
        public String responseFilePrefix() {
            return "location_upload_failures";
        }
    };

    public static boolean isInValid(String entity) {
        return findFor(entity) == null;
    }

    public static ImportType findFor(String entity) {
        for (ImportType importType : ImportType.values()) {
            if (StringUtils.equalsIgnoreCase(importType.name(), (StringUtils.trimToEmpty(entity)))) {
                return importType;
            }
        }
        return null;
    }

    abstract void performAction(String importFile, CSVDataImporter csvDataImporter);
    public abstract String successMessage();
    public abstract String errorMessage();
    public abstract String responseFilePrefix();
}
