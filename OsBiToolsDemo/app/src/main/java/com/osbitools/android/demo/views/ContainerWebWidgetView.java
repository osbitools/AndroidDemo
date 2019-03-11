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

package com.osbitools.android.demo.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.osbitools.android.demo.R;
import com.osbitools.android.shared.wwg.ContainerWebWidget;
import com.osbitools.android.shared.wwg.WebWidget;

/**
 * January 17, 2016
 *
 * View for Container Web Widget
 *
 */
public class ContainerWebWidgetView extends WebWidgetView<ContainerWebWidget> {
    // Header Icon File Name
    String _hicon;

    // Header Title
    String _htitle;

    // Header view. Could be hidden for Utility Box
    View _vheader;

    // Body
    FrameLayout _vbody;

    public ContainerWebWidgetView(Context context) {
        super(context);
    }

    public ContainerWebWidgetView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ContainerWebWidgetView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void init(Context context, WebWidget wwg, boolean thumb) {
        super.init(context, wwg, thumb);

        _hicon = getWebWidget().getIconName();
        _htitle = getWebWidget().getTitle();

        LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.wwg_cont, this, true);

        _vbody = (FrameLayout) this.findViewById(R.id.cont_body);
        _vheader = this.findViewById(R.id.cont_header);

        ImageView pview = (ImageView) this.findViewById(R.id.cont_header_icon);
        TextView tview = (TextView) this.findViewById(R.id.cont_header_title);


        // Find icon by id
        if (_hicon != null)
            pview.setImageResource(getResources().getIdentifier(
                    wwg.getWebPage().getProjectName() +
                                    "_" + _hicon, "drawable", context.getPackageName()));
        else
            pview.setVisibility(GONE);

        if (_htitle != null)
            tview.setText(_htitle);
        else
            tview.setVisibility(GONE);

        if (pview.getVisibility() == GONE && tview.getVisibility() == GONE)
            _vheader.setVisibility(GONE);

        // Add child web widgets
        for (WebWidget twwg: getWebWidget().getWebWidgets()) {
            /*
            String cname = getClass().getPackage() + "." + twwg.getWebClassType() + "WebWidgetView";

            try {
                Class<WebWidgetView> c = (Class<WebWidgetView>) Class.forName(cname);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Unknown WebWidget class " + cname +
                                                        ". ERROR: " + e.getMessage());
            }
            */

            ChartWebWidgetView cview = new ChartWebWidgetView(context);
            cview.init(context, twwg, thumb);

            _vbody.addView(cview);
        }
    }

    @Override
    public ContainerWebWidget getWebWidget() {
        return (ContainerWebWidget) getWebWidgetPtr();
    }

}
