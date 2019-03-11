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

package com.osbitools.android.demo;

import android.content.res.XmlResourceParser;
import android.util.Log;

import com.osbitools.android.shared.LangLabelsSet;
import com.osbitools.android.shared.WebPage;
import com.osbitools.android.shared.wwg.ChartWebWidget;
import com.osbitools.android.shared.wwg.ContainerWebWidget;
import com.osbitools.android.shared.wwg.WebWidget;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * November 16, 2015
 *
 * Helper class for Project List storage.
 */
public class ProjectList {

    // An array of Projects.
    public static final List<ProjectItem> ITEMS = new ArrayList<ProjectItem>();

    // A map of sample (dummy) items, by ID.
    public static final Map<String, ProjectItem> ITEM_MAP = new HashMap<String, ProjectItem>();

    public static void addItem(ProjectItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    public static void clear() {
        ITEMS.clear();
        ITEM_MAP.clear();
    }

    /**
     * A Project item representing a piece of content.
     */
    public static class ProjectItem implements Serializable {
        private final int _idx;
        public final String id;

        // List & Map of included files
        private final WebPageList _wpl = new WebPageList();

        // List of initially configured files
        private final String[] _files;

        // Lang Set
        private LangLabelsSet _lls;

        public ProjectItem(int idx, String id, String[] files) {
            _idx = idx;
            this.id = id;
            _files = files;
        }

        public int getIndex() {
            return _idx;
        }

        public String[] getFiles() {
            return _files;
        }

        public void addWebPage(String fname, WebPage wp) {
            _wpl.addItem(fname, wp);
        }

        public WebPageList getWebPageList() {
            return _wpl;
        }

        @Override
        public String toString() {
            return _idx + ":" + id;
        }

        public void loadLangSetRes(XmlResourceParser xpp)
                throws IOException, XmlPullParserException {
            _lls = new LangLabelsSet(Locale.getDefault().getLanguage());

            // Current tag name
            String tname;

            // Current label id
            String lid = null;

            int et = xpp.getEventType();
            while (et != XmlPullParser.END_DOCUMENT) {
                switch (et) {
                    case XmlPullParser.START_TAG:
                        tname = xpp.getName();

                        switch (tname) {
                            case "ll_set":
                                // TODO Process document based on version number
                                int vmax = xpp.getAttributeIntValue(null, "ver_max", -1);
                                int vmin = xpp.getAttributeIntValue(null, "ver_min", -1);
                                Log.d("TEMP", "Parsing v. " + vmax + "." + vmin);

                                // Load lang list
                                _lls.setLangSet(xpp.getAttributeValue(null, "lang_list"));

                                break;

                            case "lang_label":
                                lid = xpp.getAttributeValue(null, "id");
                                break;

                            case "ll_def":
                                _lls.addLangLabel(lid, xpp);
                                break;
                        }

                        break;
                }

                et = xpp.next();
            }
        }

        public LangLabelsSet getLangLabelsSet() {
            return _lls;
        }

        public void loadWebPage(String fname, XmlResourceParser xpp)
                                            throws IOException, XmlPullParserException {
            WebPage wp = new WebPage(id);

            // Current web widget container
            ContainerWebWidget wwc = null;

            // Current web widget
            WebWidget wwg = null;

            // Current web widget tag
            String wtag = null;

            // Current property group name
            String pgname = null;

            int et = xpp.getEventType();
            while (et != XmlPullParser.END_DOCUMENT) {

                switch (et) {
                    case XmlPullParser.START_TAG:
                        String tname = xpp.getName();

                        switch (tname) {
                            case "wp":
                                // TODO Process document based on version number
                                int vmax = xpp.getAttributeIntValue(null, "ver_max", -1);
                                int vmin = xpp.getAttributeIntValue(null, "ver_min", -1);

                                wp.setDescr(xpp.getAttributeValue(null, "descr"));

                                // Log.d("TEMP", "Parsing v. " + vmax + "." + vmin);

                                break;

                            case "panel":
                                wp.addPanel();

                                break;

                            case "wwg_cont":
                                wtag = tname;
                                ContainerWebWidget container = new ContainerWebWidget(xpp);
                                wwg = container;

                                // Add web widget to panel or container
                                if (wwc != null) {
                                    // Add web widget to current container
                                    wwc.addWebWidget(container);

                                    // Add web widget to global indexed list
                                    wp.addWebWidget(container);
                                } else {
                                    wp.addPanelWebWidget(container);
                                }

                                // Set new container handle
                                wwc = container;

                                break;

                            case "wwg_chart":
                                wtag = tname;
                                wwg = new ChartWebWidget(xpp);

                                // Add web widget to panel or container
                                if (wwc != null) {
                                    // Add web widget to current container
                                    wwc.addWebWidget(wwg);

                                    // Add web widget to global indexed list
                                    wp.addWebWidget(wwg);
                                } else {
                                    wp.addPanelWebWidget(wwg);
                                }
                                break;

                            case "prop":
                                if (pgname == null)
                                    wwg.addProp(xpp.getAttributeValue(null, "name"),
                                        xpp.getAttributeValue(null, "value"), _lls);
                                else
                                    wwg.addPropGroupValue(pgname,
                                            xpp.getAttributeValue(null, "name"),
                                                    xpp.getAttributeValue(null, "value"), _lls);
                                break;

                            case "prop_groups":
                                wwg.initPropGroups();
                                break;

                            case "prop_group":
                                // Remember current property group
                                pgname = xpp.getAttributeValue(null, "name");
                                wwg.initPropGroup(pgname);
                                break;

                            case "props":
                                if (pgname != null)
                                    wwg.appendPropGroup(pgname);
                                break;

                        }

                        break;

                    case XmlPullParser.END_TAG:
                        tname = xpp.getName();
                        switch (tname) {
                            case "wwg_cont":
                                // Check if any parent container
                                ContainerWebWidget container = (ContainerWebWidget) wwc.getParent();

                                // Restore/Clear handle for web widget container
                                wwc = (container != null) ? container : null;
                                break;

                            case "prop_group":
                                // Reset current property group
                                pgname = null;
                        }

                        break;

                /*
                case XmlPullParser.TEXT:
                    sb.append("\nTEXT: "+xpp.getText());
                    break;
                */
                }

                et = xpp.next();
            }

            addWebPage(fname, wp);
        }
    }
}
