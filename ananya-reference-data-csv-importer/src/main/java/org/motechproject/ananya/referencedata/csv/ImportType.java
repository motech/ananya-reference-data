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
        public String errorMessage(int maximumNumberOfRecords) {
            return String.format("FLW file can have a maximum of %d records.", maximumNumberOfRecords);
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
        public String errorMessage(int maximumNumberOfRecords) {
            return "";
        }

        @Override
        public String responseFilePrefix() {
            return "location_upload_failures";
        }
    }, Msisdn {
        @Override
        void performAction(String importFile, CSVDataImporter csvDataImporter) {
            csvDataImporter.importData(ImportType.Msisdn.name(), true, importFile);
        }

        @Override
        public String successMessage() {
            return "MSISDNs have been updated successfully.";
        }

        @Override
        public String errorMessage(int maximumNumberOfRecords) {
            return String.format("MSISDN CSV file can have a maximum of %d records.", maximumNumberOfRecords);
        }

        @Override
        public String responseFilePrefix() {
            return "msisdn_upload_failures";
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
    public abstract String errorMessage(int maximumNumberOfRecords);
    public abstract String responseFilePrefix();
}
