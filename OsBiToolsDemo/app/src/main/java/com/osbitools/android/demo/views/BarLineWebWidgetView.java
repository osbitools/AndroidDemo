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

import com.osbitools.android.shared.ClientError;
import com.osbitools.android.shared.Utils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * April 18, 2016
 *
 * Abstract Custom View for Chart Web Widget with mandatory X and Y Axis (tbl_bar, pie)
 */
public abstract class BarLineWebWidgetView extends DualAxisWebWidgetView {

    public BarLineWebWidgetView(Context context) {
        super(context);
    }

    public BarLineWebWidgetView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BarLineWebWidgetView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    void addYAxisTitle(String title) {

    }
}
