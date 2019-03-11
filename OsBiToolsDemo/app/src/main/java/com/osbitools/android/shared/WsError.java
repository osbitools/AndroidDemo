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

import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;

/**
 * November 23, 2015
 *
 * Class to parset & store OsBiTools Web Services error returned in JSON format
 */
public class WsError {

    // Request ID
    private Integer _rid;

    // Total Web Service error
    private String _err;

    public WsError(HttpURLConnection conn) throws ClientError {
        InputStream is;

        // Read JSON with error description
        try {
            is = conn.getInputStream();
        } catch (IOException e) {
            //-- 7
            throw new ClientError(7, e, "Error read HTTP response");
        }

        JsonReader reader;
        try {
            reader = new JsonReader(new InputStreamReader(is, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            //-- 8
            throw new ClientError(8, e, "UTF-8 not supported");
        }

        try {
            reader.beginObject();
                        /*
                        Expected next format
                        {
                            "request_id":xxxxx,
                            "error": {
                                "id": 2,
                                "msg": "Authentication Failed",
                                "info": "Invalid username and/or password",
                                "details": ["User Not Found"]
                            }
                        }
                        */

            boolean ferr = false;

            // Read the whole error object
            label:
            while (reader.hasNext()) {
                String name = reader.nextName();
                switch (name) {
                    case "request_id":
                        _rid = reader.nextInt();
                        break;
                    case "error":
                        ferr = true;
                        reader.beginObject();
                        break label;
                    default:
                        reader.skipValue();
                        break;
                }
            }

            if (_rid == null) {
                // Not en error structure
                return;
            } else if (!ferr) {
                //-- 9
                throw new ClientError(9, "Invalid error message structure.",
                                                            "Tag 'error' not found.");
            }

            // Read error details
            int id = 0;
            String msg = null;
            String info = null;
            String[] details = null;

            while (reader.hasNext()) {
                String name = reader.nextName();
                switch (name) {
                    case "id":
                        id = reader.nextInt();
                        break;
                    case "msg":
                        msg = reader.nextString();
                        break;
                    case "info":
                        info = reader.nextString();
                        break;
                    case "details":
                        details = readJsonArray(reader);
                        break;
                    default:
                        reader.skipValue();
                        break;
                }
            }

            // Close all handles
            reader.endObject();
            reader.endObject();

            _err = "Request Id - " + _rid + "; " +
                    (new ClientError(id, msg, info, details)).toString();
        } catch (IOException e) {
            //-- 10
            throw new ClientError(10, e, "Unable read error object.");
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                // Ignore error
            }
        }
    }

    private String[] readJsonArray(JsonReader reader) throws IOException {
        ArrayList<String> res = new ArrayList<>();

        reader.beginArray();
        while (reader.hasNext())
            res.add(reader.nextString());
        reader.endArray();

        return res.toArray(new String[res.size()]);
    }

    public String toString() {
        return (_rid != null) ? _err : null;
    }
}
