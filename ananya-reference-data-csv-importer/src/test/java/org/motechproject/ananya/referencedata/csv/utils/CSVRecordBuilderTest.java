package org.motechproject.ananya.referencedata.csv.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CSVRecordBuilderTest {

    @Test
    public void shouldFormatToCsvFormatForSingleColumn() {
        CSVRecordBuilder builder = new CSVRecordBuilder();
        builder.appendColumn("column");

        assertEquals("\"column\"", builder.toString());
    }

    @Test
    public void shouldFormatToCsvFormatForMultipleColumn() {
        CSVRecordBuilder builder = new CSVRecordBuilder(true);

        builder.appendColumn("column1").appendColumn("column2").appendColumn("column3");

        assertEquals("\"column1\",\"column2\",\"column3\"", builder.toString());
    }

    @Test
    public void shouldHandleNullAndEmptyStrings() {
        CSVRecordBuilder builder = new CSVRecordBuilder();

        builder.appendColumn((String) null).appendColumn("").appendColumn("  ").appendColumn((String) null);

        assertEquals("\"\",\"\",\"  \",\"\"", builder.toString());
    }

    @Test
    public void shouldEscapeQuotes() {
        CSVRecordBuilder builder = new CSVRecordBuilder();

        builder.appendColumn("name, is \"khan\"", "hello\"\"");

        assertEquals("\"name, is \"\"khan\"\"\",\"hello\"\"\"\"\"", builder.toString());
    }

    @Test
    public void shouldNotQuoteValues() {
        CSVRecordBuilder builder = new CSVRecordBuilder(false);

        builder.appendColumn("name, is \"khan\"", "hello\"\"");

        assertEquals("name, is \"khan\",hello\"\"", builder.toString());
    }
}