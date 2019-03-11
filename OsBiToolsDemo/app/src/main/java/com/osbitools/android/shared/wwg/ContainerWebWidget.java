/*
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

import com.osbitools.android.shared.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * November 25, 2015
 *
 * Web Widget container class
 */
public class ContainerWebWidget extends WebWidget {
    // Data Flag
    private boolean _fdata = false;

    // Container icon name
    private String _iname;

    // Container header
    private String _title;

    // List of web widgets
    private List<WebWidget> _list = new ArrayList<>();

    public ContainerWebWidget(XmlResourceParser xpp) {
        super(xpp);
    }

    @Override
    public String getWebClassType() {
        return "Container";
    }

    public void addWebWidget(WebWidget wwg) {
        _list.add(wwg);
        wwg.setParent(this);

        if (wwg instanceof DataSetWebWidget)
            // Mark data flag in all hierarchy
            raiseDataFlag();
    }

    public List<WebWidget> getWebWidgets() {
        return _list;
    }

    private void raiseDataFlag() {
        _fdata = true;

        if (hasParent())
            ((ContainerWebWidget) getParent()).raiseDataFlag();
    }

    public boolean hasData() {
        return _fdata;
    }

    public String getPropValue(String key, String value) {
        return "header_icon".equals(key) ? Utils.removeFileExt(value) : value;
    }

    public String getIconName() {
        return getPropByName("header_icon");
    }

    public String getTitle() {
        return getPropByName("header_title");
    }
}
