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

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.XAxis;
import com.osbitools.android.shared.ClientError;
import com.osbitools.android.shared.DataSetColumn;
import com.osbitools.android.shared.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * April 18, 2016
 *
 * Abstract Custom View for Chart Web Widget with mandatory X Axis (line, bar, pie, candle)
 */
public abstract class XAxisWebWidgetView extends ChartWebWidgetView {

    // Index columns
    private HashMap<String, Integer> _cmap = new HashMap<>();
    private ArrayList<DataSetColumn> columns = new ArrayList<>();

    // X Axis index in columns list
    private Integer _xidx;

    // Flag for xaxis date type
    boolean _fxdate;

    // Array List for X axis
    private ArrayList<String> _xds = new ArrayList<>();

    public XAxisWebWidgetView(Context context) {
        super(context);
    }

    public XAxisWebWidgetView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public XAxisWebWidgetView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void parseColumns() throws ClientError {
        JSONObject json = getWebWidget().getJson();

        try {
            JSONArray clist = json.getJSONObject("data").getJSONArray("columns");

            for (int i = 0; i < clist.length(); i++) {
                DataSetColumn col = new DataSetColumn(clist.getJSONObject(i));
                columns.add(col);
                _cmap.put(col.getName(), i);
            }
        } catch (JSONException e) {
            //-- 19
            throw new ClientError(19, e);
        }
    }

    public void setXAxisIndex() throws ClientError {
        // Find columns index for X Axis
        String xcol = getWebWidget().getPropByName("x_axis");
        if (Utils.isEmpty(xcol))
            //-- 20
            throw new ClientError(20, "X Axis Column is not defined");
        _xidx = getColumnIndex(xcol);
        if (_xidx == null)
            //-- 27
            throw new ClientError(27, "X Axis Column [" + xcol +
                    "] not found in result dataset");

        // Check if xaxis data is date type
        _fxdate = (columns.get(_xidx).getJavaType().lastIndexOf(".Date") ==
                columns.get(_xidx).getJavaType().length() - 5);

    }

    public Integer getColumnIndex(String name) {
        return _cmap.get(name);
    }

    public void addXAxisValue(int idx, JSONArray jrow) throws ClientError {
        try {
            _xds.add(jrow.getString(_xidx));
        } catch (JSONException e) {
            //-- 32
            throw new ClientError(32, e, "Unable retrieve X Axis value from row #" + idx);
        }

    }


    public void setXAxis(Chart chart) {
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        String xaxis_lbl = getWebWidget().getPropByName("x_axis_label");

        if (!isThumbnail()) {
            if (!Utils.isEmpty(xaxis_lbl))
                chart.setDescription(xaxis_lbl);

        }
    }
}
