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

import android.util.SparseArray;

/**
 * November 23, 2015
 *
 * List of all possible error id's from this application with their definitions.
 */
public class ErrorList {
    private static final SparseArray<String> _map = new SparseArray<>();

    static {
        // Login errors
        addError(1, "Authentication Error");
        addError(2, "Authentication Error");
        addError(3, "Authentication Error");
        addError(4, "Authentication Error");
        addError(5, "Authentication Error");
        addError(6, "Authentication Error");

        // Web Service JSON Read Error
        addError(7, "Web Service Error Result Read Error");
        addError(8, "Web Service Error Result Read Error");
        addError(9, "Web Service Error Result Read Error");
        addError(10, "Web Service Error Result Read Error");

        // Logout errors
        addError(11, "Logout Error");
        addError(12, "Logout Error");
        addError(13, "Logout Error");
        addError(14, "Logout Error");
        addError(15, "Logout Error");
        addError(16, "Authentication Error");

        // Data Set Request
        addError(17, "DataSet Request Error");
        addError(18, "Data Visualization Error");

        // Data Set Parsing
        addError(19, "DataSet Parsing Error");
        addError(20, "DataSet Parsing Error");

        // Data Set Request
        addError(21, "DataSet Request Error");
        addError(22, "DataSet Request Error");
        addError(23, "DataSet Request Error");
        addError(24, "DataSet Request Error");
        addError(25, "DataSet Request Error");
        addError(26, "DataSet Request Error");

        // Data Set Parsing
        addError(27, "DataSet Parsing Error");
        addError(28, "DataSet Parsing Error");
        addError(29, "DataSet Parsing Error");
        addError(30, "DataSet Parsing Error");
        addError(31, "DataSet Parsing Error");
        addError(32, "DataSet Parsing Error");
        addError(33, "DataSet Parsing Error");
        addError(34, "DataSet Parsing Error");
        addError(35, "DataSet Parsing Error");
        addError(36, "DataSet Parsing Error");

        // addError(, "");
    }

    public static String getErrorById(int id) {
        String msg = _map.get(id);
        return (msg == null ) ? "Unknown Error" : msg;
    }

    public static void addError(int id, String msg) {
        _map.put(id, msg);
    }
}
