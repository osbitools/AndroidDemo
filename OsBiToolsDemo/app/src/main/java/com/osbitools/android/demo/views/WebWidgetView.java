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
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;

import com.osbitools.android.demo.R;
import com.osbitools.android.shared.wwg.WebWidget;

/**
 * January 17, 2016
 *
 * Abstract class for all WebWidget classes
 *
 */
public abstract class WebWidgetView<T extends WebWidget> extends FrameLayout {

    // Pointer on WebWidget
    private WebWidget _wwg;

    // Flag to indicate if view is draws as thumbnail or full screen
    private boolean _thumb;

    // Status of data
    public WebWidgetView(Context context) {
        super(context);
        this.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                                                        AbsListView.LayoutParams.FILL_PARENT));

        // Old android crashes
        // this.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
           //                                                 ViewGroup.LayoutParams.FILL_PARENT));
    }

    public WebWidgetView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WebWidgetView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void init(Context context, WebWidget wwg, boolean thumb) {
        _wwg = wwg;
        _thumb  = thumb;
    }

    public WebWidget getWebWidgetPtr() {
        return _wwg;
    }

    public abstract T getWebWidget();

    public boolean isThumbnail() {
        return _thumb;
    }
}
