package com.osbitools.android.shared;

import android.graphics.Color;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * April 15, 2016
 *
 * Shared constants
 */
public class Constants {
    // Minimum chart thumbnail width, dp
    public static final int MIN_CHART_THUMB_WIDTH = 150;

    // Log category
    public static final String DEBUG_TAG = "OsBiToolsMobileDemo";

    // Display width
    public static int DISPLAY_WIDTH;

    // Display height
    public static int DISPLAY_HEIGHT;

    // Security Flag
    public static final boolean IS_SECURITY = false;

    // Default jqPlot color palette
    public static int[] DEF_COLORS = new int[] {
            Color.parseColor("#4bb2c5"),
            Color.parseColor("#EAA228"),
            Color.parseColor("#c5b47f"),
            Color.parseColor("#579575"),
            Color.parseColor("#839557"),
            Color.parseColor("#958c12"),
            Color.parseColor("#953579"),
            Color.parseColor("#4b5de4"),
            Color.parseColor("#d8b83f"),
            Color.parseColor("#ff5800"),
            Color.parseColor("#0085cc"),
            Color.parseColor("#c747a3"),
            Color.parseColor("#cddf54"),
            Color.parseColor("#FBD178"),
            Color.parseColor("#26B4E3"),
            Color.parseColor("#bd70c7")
    };

    // TEST START Web Service hard coded parameters
    public static final Boolean TEMP_DEBUG_FLAG = true;
    public static final Boolean TEMP_TRACE_FLAG = true;
    /// TEST END

    // Base path to Web Service host
    public static final String WS_HOST = "http://www.osbitools.com/live_demo";
    // public static final String WS_HOST = "http://10.0.2.2";

    public static final String CORE_PATH = "/osbiws_core";

    public static final String URL_AUTH = "/rest/auth";

    public static final String URL_LOGOUT = "/rest/logout";

    public static final String URL_DS = "/rest/ds";

    // Flag for large layout
    public static boolean IS_LARGE;
}
