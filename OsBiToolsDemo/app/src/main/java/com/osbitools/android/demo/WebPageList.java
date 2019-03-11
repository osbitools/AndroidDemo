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

import com.osbitools.android.shared.WebPage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Dec 1, 2016
 *
 * Helper class for providing Web Page List interface
 */
public class WebPageList implements Serializable {

    /**
     * An array of Projects.
     */
    public final List<String> _items = new ArrayList<String>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public final Map<String, WebPageItem> _item_map = new HashMap<String, WebPageItem>();

    public void addItem(String fname, WebPage wp) {
        WebPageItem item = new WebPageItem(fname, wp);
        _items.add(fname);
        _item_map.put(fname, item);
    }

    public void sort() {
        Collections.sort(_items);
    }

    public WebPageItem getItem(int pos) {
        return _item_map.get(_items.get(pos));
    }

    public WebPageItem[] getItems() {
        WebPageItem[] res = new WebPageItem[_items.size()];
        for (int i = 0; i < _items.size(); i++)
            res[i] =  _item_map.get(_items.get(i));

        return res;
    }

    /**
     * Web Page Item. Wrapper around WebPage
     */
    public static class WebPageItem implements Serializable {
        // Web Page file name
        public final String _fname;

        // Visible web page name
        private final String _descr;

        // Pointer on WebPage object
        private final WebPage _wp;

        public WebPageItem(String fname, WebPage wp) {
            _fname = fname;
            _wp = wp;
            _descr = wp.getDescr();

        }

        public String getFileName() {
            return _fname;
        }

        public String getDescr() {
            return _descr;
        }

        public WebPage getWebPage() {
            return _wp;
        }

        @Override
        public String toString() {
            return _fname + " - " + _descr;
        }
    }
}
