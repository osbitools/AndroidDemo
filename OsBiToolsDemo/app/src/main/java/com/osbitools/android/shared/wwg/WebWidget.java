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

package com.osbitools.android.shared.wwg;

import android.content.res.XmlResourceParser;

import com.osbitools.android.shared.LangLabelsSet;
import com.osbitools.android.shared.WebPage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * November 24, 2015
 *
 * Web Widget class for binding
 */
public abstract class WebWidget implements Serializable {
    // Unique Identifier
    private int _uid;

    // Web Widget Class Name
    private String _cname;

    // List of properties
    Map<String, String> _props = new HashMap<>();

    // List of groups of property groups
    Map<String, List<Map<String, String>>> _pgroups;

    // Pointer on parent web widget
    private WebWidget _parent;

    // Root web page
    WebPage _wp;

    public WebWidget(XmlResourceParser xpp) {
        _uid = xpp.getAttributeIntValue(null, "uid", 0);
        _cname = xpp.getAttributeValue(null, "class_name");
    }

    public int getUID() {
        return _uid;
    }

    public String getWebClassName() {
        return _cname;
    }

    public abstract String getWebClassType();

    public void addProp(String key, String value, LangLabelsSet lls) {
        _props.put(key, checkPropValue(key, value, lls));
    }

    public String checkPropValue(String key, String value, LangLabelsSet lls) {
        return (value != null &&  value.length() > 3 &&
                value.substring(0, 3).equals("LX_")) ?
                        lls.getLangLabel(value) : getPropValue(key, value);
    }

    public String getPropValue(String key, String value) {
        return value;
    }

    public String getPropByName(String key) {
        return _props.get(key);
    }

    public WebWidget getParent() {
        return _parent;
    }

    public void setParent(WebWidget parent) {
        _parent = parent;
    }

    public boolean hasParent() {
        return _parent != null;
    }

    @Override
    public String toString() {
        return _uid + ":" + _cname;
    }

    public void initPropGroups() {
        _pgroups = new HashMap<>();
    }

    public void initPropGroup(String key) {
        _pgroups.put(key, new ArrayList());
    }

    public void appendPropGroup(String pgname) {
        _pgroups.get(pgname).add(new HashMap<String, String>());
    }

    public void addPropGroupValue(String pgname, String key , String value, LangLabelsSet lls) {
        List<Map<String, String>> list = _pgroups.get(pgname);
        list.get(list.size() - 1).put(key, checkPropValue(key, value, lls));
    }

    public List<Map<String, String>> getPropGroup(String key) {
        return _pgroups.get(key);
    }

    public WebPage getWebPage() {
        return (_parent != null) ? _parent.getWebPage() : _wp;
    }

    public void setWebPage(WebPage wp) {
        _wp = wp;
    }
}
