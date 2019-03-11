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

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.osbitools.android.demo.views.ContainerWebWidgetView;
import com.osbitools.android.demo.views.WebWidgetView;
import com.osbitools.android.shared.*;
import com.osbitools.android.shared.wwg.*;

/**
 * March 17, 2016
 *
 * Custom Adapter for WebWidget Panel
 */
public class WebPagePanelAdapter extends BaseAdapter {
    private final WebPage _wp;

    private final Context _ctx;

    public WebPagePanelAdapter(Context context, WebPage wp) {
        _wp = wp;
        _ctx = context;
    }

    @Override
    public int getCount() {
        return _wp.getTopWebWidgetsCount();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Log.d(Constants.DEBUG_TAG, "WebPagePanelAdapter: getView position " + position);
        WebWidget wwg = _wp.getTopWebWidget(position);

        if (view == null) {
            view = Utils.getWebWidgetView(_ctx, wwg, !Constants.IS_LARGE);

            if (((WebWidgetView) view).isThumbnail())
                // Add special background for panel look like button
                view.setBackgroundResource(R.drawable.list_selector);
            else
                view.setBackgroundResource(R.drawable.list_item_border);
        }

        return view;
    }

    public WebPage getWebPage() {
        return _wp;
    }
}
