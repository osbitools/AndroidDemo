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

/**
 * November 11, 2015
 *
 * Generic class for OsBi Mobile Errors
 */
public class ClientError extends Exception {

    // Error id
    private int _id;

    // Public detail error info
    private String _info;

    // System details
    private String[] _details;

    public ClientError(int id, String msg, String info, String[] details) {
        super(msg);
        _id = id;
        _info = info;
        _details = details;
    }

    public ClientError(int id, String info, String[] details) {
        this(id, ErrorList.getErrorById(id), info, details);
    }

    public ClientError(int id, String info, String details) {
        this(id, info, new String[] {details});
    }

    public ClientError(int id, Exception e) {
        this(id, null, (e == null ? null :
                ((e.getMessage() != null) ?
                        new String[] {e.getClass().getName() + " - " + e.getMessage()} :
                        (e.getCause() != null && e.getCause().getMessage() != null ?
                                new String[] {e.getCause().getClass().getName() + " - " +
                                        e.getCause().getMessage()} : null))));
    }

    public ClientError(int id, Exception e, String info) {
        this(id, info, (e == null ? null :
                ((e.getMessage() != null) ?
                        new String[] {e.getClass().getName() + " - " + e.getMessage()} :
                        (e.getCause() != null && e.getCause().getMessage() != null ?
                                new String[] {e.getCause().getClass().getName() + " - " +
                                        e.getCause().getMessage()} : null))));
    }

    public ClientError(int id, String info) {
        this(id, info, new String[] {});
    }

    @Override
    public String toString() {
        return getFullMessage("; ");
    }

    public String getFullMessage(){
        return getFullMessage("; ");
    }

    public String getFullMessage(String delim) {
        String res = "ERROR #" + _id + " - " + getMessage();

        String dmsg = "";
        if (Utils.isDebug() && _details != null && _details.length > 0) {
            dmsg = delim + "DETAILS - ";
            for (String s: _details)
                dmsg += "[" + s + "]";
        }

        return res + ((_info != null) ? delim + "INFO - " + _info : "") + dmsg;
    }
}
