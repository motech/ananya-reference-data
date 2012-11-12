package org.motechproject.ananya.referencedata.csv.utils;

import org.apache.commons.lang.StringUtils;

public class CSVRecordBuilder {

    private StringBuilder stringBuilder = new StringBuilder();
    private static final String DELIMITER = ",";
    private boolean isEmpty;
    private boolean shouldQuoteValues;

    public CSVRecordBuilder() {
        this(true);
    }

    public CSVRecordBuilder(boolean shouldQuoteValues) {
        this.shouldQuoteValues = shouldQuoteValues;
        isEmpty = true;
    }

    public CSVRecordBuilder appendColumn(String... values) {
        for(String value: values) {
            appendColumn(value);
        }
        return this;
    }

    public CSVRecordBuilder appendColumn(String value) {
        appendDelimiter();
        value = fixValue(value);

        if(shouldQuoteValues) {
            appendQuotedValue(value);
        } else {
            appendValue(value);
        }

        return this;
    }

    private void appendDelimiter() {
        if (!isEmpty) {
            stringBuilder.append(DELIMITER);
        }
        isEmpty = false;
    }

    private void appendQuotedValue(String value) {
        stringBuilder.append("\"");
        appendValue(escapeQuotes(value));
        stringBuilder.append("\"");
    }

    private void appendValue(String value) {
        stringBuilder.append(value);
    }

    private String escapeQuotes(String value) {
        return value.replaceAll("\"", "\"\"");
    }

    private String fixValue(String value) {
        if (value == null)
            return StringUtils.EMPTY;
        return value;
    }

    @Override
    public String toString() {
        return stringBuilder.toString();
    }
}