/*
 * Open Source Business Intelligence Tools - http://www.osbitools.com/
 *
 * Copyright (c) 2016. IvaLab Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the FreeBSD Project.
 */

package com.osbitools.android.shared;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.Log;

import com.osbitools.android.demo.R;
import com.osbitools.android.demo.views.ContainerWebWidgetView;
import com.osbitools.android.demo.views.WebWidgetView;
import com.osbitools.android.shared.wwg.*;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * November 19, 2015
 *
 * Class with generic utils.
 */
public class Utils {

    // Name of HTTP Header with cookie
    private static final String HEADER_SET_COOKIES = "Set-Cookie";

    // Name of security cookie
    private static final String SECURE_COOKIE_NAME = "STOKEN";

    // Security token since last successful login
    private static String _stoken;

    public static boolean login(String usr, String pwd) throws ClientError {
        // Post authentication request
        HttpURLConnection conn = null;
        byte[] params = ("usr=" + usr + "&pwd=" + pwd).getBytes();

        try {
            URL url;
            try {
                url = new URL(getAppPath() + Constants.URL_AUTH +
                                        (isDebug() ? "?debug=on" : ""));
            } catch (MalformedURLException e) {
                //-- 1
                throw new ClientError(1, e, "Configuration Error");
            }

            try {
                conn = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                //-- 2
                throw new ClientError(2, e, "Initialization Error");
            }

            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            try {
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded; charset=UTF-8");
                conn.setRequestProperty("Content-Length",
                        String.valueOf(params.length));
            } catch (ProtocolException e) {
                //-- 3
                throw new ClientError(3, e, "Protocol Error");
            }

            conn.setDoInput(true);
            conn.setDoOutput(true);

            // Set username & password parameters
            try {
                OutputStream out = conn.getOutputStream();
                out.write(params);
                out.close();
            } catch (IOException e) {
                //-- 4
                throw new ClientError(4, e, "Communication Error");
            }

            // Starts the query
            try {
                conn.connect();
                int code = conn.getResponseCode();

                if (code != 200) {
                    //-- 5
                    throw new ClientError(5, "Http Error Code #" + code);
                } else {
                    // Read&Save security token
                    Map<String, List<String>> headerFields = conn.getHeaderFields();
                    List<String> cookiesHeader = headerFields.get(HEADER_SET_COOKIES);

                    if (cookiesHeader != null) {
                        for (String chr : cookiesHeader) {
                            for (HttpCookie cookie : HttpCookie.parse(chr)) {
                                if (cookie.getName().equals(SECURE_COOKIE_NAME)) {
                                    _stoken = cookie.getValue();
                                    break;
                                }
                            }
                        }
                    } else {
                        //-- 16
                        throw new ClientError(16, "HTTP Header with Security Token",
                                "'" + HEADER_SET_COOKIES + "' header not found");
                    }

                    if (_stoken == null)
                        //-- 17
                        throw new ClientError(17, "Security Cookie not found",
                                "'" + SECURE_COOKIE_NAME + "' cookie not found");
                }
            } catch (IOException e) {
                //-- 6
                throw new ClientError(6, e, "Connection Error");
            }
        } finally {
            if (conn != null)
                conn.disconnect();
        }

        return true;
    }

    public static void logout() throws ClientError {
        HttpURLConnection conn = null;

        try {
            URL url;
            try {
                url = new URL(getAppPath() + Constants.URL_LOGOUT +
                                                (isDebug() ? "?debug=on" : ""));
            } catch (MalformedURLException e) {
                //-- 11
                throw new ClientError(11, e, "Configuration Error");
            }

            try {
                conn = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                //-- 12
                throw new ClientError(12, e, "Initialization Error");
            }

            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            try {
                conn.setRequestMethod("GET");
            } catch (ProtocolException e) {
                //-- 13
                throw new ClientError(13, "Protocol Error");
            }

            conn.setRequestProperty("Cookie", SECURE_COOKIE_NAME + "=" + _stoken);

            try {
                conn.connect();
                int code = conn.getResponseCode();

                if (code != 200)
                    //-- 14
                    throw new ClientError(14, "Http Error Code #" + code);
            } catch (IOException e) {
                //-- 15
                throw new ClientError(15, e, "Connection Error");
            }
        } finally {
            if (conn != null)
                conn.disconnect();

            // Clear security token
            _stoken = null;
        }
    }

    public static String getDataSet(String mname) throws ClientError {
        // Post authentication request
        HttpURLConnection conn = null;

        try {
            URL url;
            try {
                url = new URL(getAppPath() + Constants.URL_DS +"?map=" + mname + ".xml" +
                            (isDebug() ? "&debug=on" : "") + (isTrace() ? "&trace=on" : ""));
            } catch (MalformedURLException e) {
                //-- 21
                throw new ClientError(21, e, "Configuration Error");
            }

            try {
                Log.d(Constants.DEBUG_TAG, "URL: " + url.toString());
                conn = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                //-- 22
                throw new ClientError(22, e, "Initialization Error");
            }

            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);

            try {
                conn.setRequestMethod("GET");
            } catch (ProtocolException e) {
                //-- 23
                throw new ClientError(23, e, "Protocol Error");
            }

            // Set security cookie (if any)
            if (_stoken != null)
                conn.setRequestProperty("Cookie", SECURE_COOKIE_NAME + "=" + _stoken);

            // Starts the query
            try {
                conn.connect();
                int code = conn.getResponseCode();

                if (code != 200)
                    //-- 25
                    throw new ClientError(25, "Http Error Code #" + code);

                StringBuilder response = new StringBuilder();
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader input = new BufferedReader(
                            new InputStreamReader(conn.getInputStream()), 8192);

                    String line;
                    while ((line = input.readLine()) != null)
                        response.append(line);

                    input.close();
                }
                return response.toString();

            } catch (IOException e) {
                //-- 26
                throw new ClientError(26, e, "Connection Error");
            }
        } finally {
            if (conn != null)
                conn.disconnect();
        }
    }

    public static String removeFileExt(String fname) {
        return fname != null ? fname.substring(0, fname.lastIndexOf('.')) : null;
    }

    public static String getAppPath() {
        return Constants.WS_HOST + Constants.CORE_PATH;
    }

    public static boolean isDebug() {
        return Constants.TEMP_DEBUG_FLAG;
    }

    public static boolean isTrace() {
        return Constants.TEMP_TRACE_FLAG;
    }

    public static boolean isEmpty(String str) {
        return str == null || str.equals("");
    }

    public static WebWidgetView getWebWidgetView(Context context, WebWidget wwg, boolean thumb) {
        String cname = "com.osbitools.android.demo.views." +
                                wwg.getClass().getSimpleName() + "View";
        Log.d(Constants.DEBUG_TAG, "Creating WebWidgetView - from " +
                                                cname + " for " + wwg.getUID());

        Class<WebWidgetView> c;
        try {
            c = (Class<WebWidgetView>) Class.forName(cname);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        try {
            Constructor<WebWidgetView> co = c.getConstructor(Context.class);
            WebWidgetView view = co.newInstance(context);
            view.init(context, wwg, thumb);

            Log.d(Constants.DEBUG_TAG, "Creating WebWidgetView - " + cname +
                                            " for " + wwg.getUID() + " created");

            return view;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
