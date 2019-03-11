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

import com.osbitools.android.shared.wwg.WebWidget;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * November 25, 2015
 *
 * Web Page container
 */
public class WebPage implements Serializable {
    // Hierarchy of all panels
    private List<List<WebWidget>> _panels = new ArrayList<>();

    // Current panel
    private List<WebWidget> _panel;

    // List of top panel web widgets
    private List<WebWidget> _tlist = new ArrayList<>();

    // List of indexed web widgets
    // SparseArray<WebWidget> _wlist = new SparseArray<>();
    HashMap<Integer, WebWidget> _wlist = new HashMap<>();

    // Description
    String _descr;

    // Counter for max widget in all panels. Used to calculate # of columnt in grid view
    private int _mcnt = 0;

    // Project Name
    String _pname;

    public WebPage(String pname) {
        _pname = pname;
    }

    public List<List<WebWidget>> getPanels() {
        return _panels;
    }

    public List<WebWidget> addPanel() {
        // Initiate current panel
        _panel = new ArrayList<>();

        // Add to global list
        _panels.add(_panel);

        return _panel;
    }

    public void addPanelWebWidget(WebWidget wwg) {
        _panel.add(wwg);
        _tlist.add(wwg);
        addWebWidget(wwg);
        wwg.setWebPage(this);

        // Adjust max counter
        if (_panel.size() > _mcnt)
            _mcnt = _panel.size();
    }

    public WebWidget getTopWebWidget(int pos) {
        return _tlist.get(pos);
    }

    public int getTopWebWidgetsCount() {
        return _tlist.size();
    }

    public void addWebWidget(WebWidget wwg) {
        _wlist.put(wwg.getUID(), wwg);
    }

    public String getDescr() {
        return _descr;
    }

    public void setDescr(String descr) {
        _descr = descr;
    }

    public int getWebWidgetCount() {
        return _wlist.size();
    }

    public int getMaxPanelWidgetCount() {
        return _mcnt;
    }

    public String getProjectName() {
        return _pname;
    }
}
