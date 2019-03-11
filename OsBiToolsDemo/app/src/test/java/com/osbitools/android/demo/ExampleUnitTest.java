package com.osbitools.android.demo;

import com.osbitools.android.demo.formatter.XDateFormatter;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void testDateFormat() throws Exception {
        XDateFormatter df = new XDateFormatter("%Y");
        assertEquals("1904", df.getXValue("1904-01-01", 0, null));
    }

    @Test
    public void testDateFormatEx() throws Exception {
        DateTimeFormatter df = DateTimeFormat.forPattern("M/d/Y");
        assertEquals("12/31/1904",  (new DateTime("1904-12-31", DateTimeZone.UTC)).toString(df));
    }

    @Test
    public void testNumberFormat() throws Exception {
        DateTimeFormatter df = DateTimeFormat.forPattern("M/d/Y");
        assertEquals("2.2",  String.format("%.1f", 2.2456789));
    }

}
