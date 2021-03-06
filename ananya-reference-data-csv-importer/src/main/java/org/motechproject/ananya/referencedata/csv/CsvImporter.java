package org.motechproject.ananya.referencedata.csv;


import org.motechproject.ananya.referencedata.csv.exception.FileReadException;
import org.motechproject.ananya.referencedata.csv.exception.InvalidArgumentException;
import org.motechproject.ananya.referencedata.csv.exception.WrongNumberArgsException;
import org.motechproject.importer.CSVDataImporter;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class CsvImporter {

    private static final String APPLICATION_CONTEXT_XML = "applicationContext-csv-importer.xml";
    private static ClassPathXmlApplicationContext context;

    public static void main(String args[]) throws Exception {
        try {
            validateArguments(args);
            String entityType = args[0];
            String filePath = args[1];
            ImportType importType = validateAndSetImportType(entityType);
            validateImportFile(filePath);

            importFile(filePath, importType);
        } catch (Exception exception) {
            throw exception;
        } finally {
            if (context != null) {
                context.close();
            }
        }
    }

    private static void importFile(String filePath, ImportType importType) {
        context = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_XML);
        CSVDataImporter csvDataImporter = (CSVDataImporter) context.getBean("csvDataImporter");
        importType.performAction(filePath, csvDataImporter);
    }

    private static void validateArguments(String[] args) throws WrongNumberArgsException {
        if (args.length != 2)
            throw new WrongNumberArgsException("Wrong number of arguments. Arguments expected in order : <entity_type> <file_name>");
    }

    private static ImportType validateAndSetImportType(String entity) throws Exception {
        if (ImportType.isInValid(entity))
            throw new InvalidArgumentException("Invalid entity. Valid entities are : FrontLineWorker, Location");
        return ImportType.findFor(entity);
    }

    private static void validateImportFile(String importFile) throws FileReadException {
        if (!new File(importFile).canRead()) {
            throw new FileReadException("Cannot read import file " + importFile);
        }
    }
}

