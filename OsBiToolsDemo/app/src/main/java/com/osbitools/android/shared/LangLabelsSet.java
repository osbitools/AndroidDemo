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

import android.content.res.XmlResourceParser;

import java.io.Serializable;
import java.util.HashMap;

/**
 * November 25, 2015
 *
 * Class to hold loaded lang_set.xml
 */
public class LangLabelsSet implements Serializable {

    // Language Set
    private String[] _ls;

    HashMap<String, HashMap<String, String>> _labels = new HashMap<>();

    // Current language
    private final String _lang;

    public LangLabelsSet(String lang) {
        _lang = lang;
    }

    public void setLangSet(String ls) {
        _ls = ls.split(",");
    }

    public String[] getLangSet() {
        return _ls;
    }

    public void addLangLabel(String id, XmlResourceParser xpp) {
        addLangLabel(id, xpp.getAttributeValue(null, "lang"),
                                xpp.getAttributeValue(null, "value"));
    }

    public void addLangLabel(String id, String lang, String text) {
        HashMap<String, String> label = _labels.get(id);

        if (label == null) {
            label = new HashMap<>();
            _labels.put(id, label);
        }

        label.put(lang, text);
    }

    public String getLangLabel(String id) {
        HashMap<String, String> label = _labels.get(id);

        return (label != null) ? label.get(_lang) : null;
    }
}
