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
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.osbitools.android.demo.DataMarkerView;
import com.osbitools.android.demo.R;
import com.osbitools.android.demo.VerticalTextView;
import com.osbitools.android.demo.formatter.NumberValueFormatter;
import com.osbitools.android.demo.formatter.XDateFormatter;
import com.osbitools.android.shared.ClientError;
import com.osbitools.android.shared.Constants;
import com.osbitools.android.shared.DataSetColumn;
import com.osbitools.android.shared.Utils;
import com.osbitools.android.shared.wwg.ChartWebWidget;
import com.github.mikephil.charting.charts.LineChart;
import com.osbitools.android.shared.wwg.WebWidget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * April 18, 2016
 *
 * Custom View for Chart Web Widget
 */
public class ChartWebWidgetView extends DataSetWebWidgetView<ChartWebWidget> implements OnChartValueSelectedListener {

    // Pointer on ViewGroup that contains chart only
    private ViewGroup _vbody;

    public ChartWebWidgetView(Context context) {
        super(context);
        _vbody = this;
    }

    public ChartWebWidgetView(Context context, AttributeSet attrs) {
        super(context, attrs);
        _vbody = this;
    }

    public ChartWebWidgetView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        _vbody = this;
    }

    @Override
    public void init(Context context, WebWidget wwg, boolean thumb) {
        super.init(context, wwg, thumb);
    }

    @Override
    public void onDataReady(JSONObject json) throws ClientError {
        Log.d(Constants.DEBUG_TAG, "ChartWebWidgetView - Processing" + getWebWidget().getUID() +
                                                        ":" + getWebWidget().getWebClassName());
        // Array of data
        JSONArray jdata;

        // Index columns
        HashMap<String, Integer> cmap = new HashMap<>();
        ArrayList<DataSetColumn> columns = new ArrayList<>();

        try {
            jdata = json.getJSONArray("data");
            JSONArray clist = json.getJSONArray("columns");

            for (int i = 0; i < clist.length(); i++) {
                DataSetColumn col = new DataSetColumn(clist.getJSONObject(i));
                columns.add(col);
                cmap.put(col.getName(), i);
            }
        } catch (JSONException e) {
            //-- 19
            throw new ClientError(19, e);
        }

        // Draw chart
        Integer xidx;
        Chart<?> chart = null;
        ChartWebWidget wwg = getWebWidget();
        String wcn = wwg.getWebClassName();

        // TODO Support basics line/bar/pie
        switch (wcn) {
            case "com.osbitools.charts.jqplot.line":
                Log.d(Constants.DEBUG_TAG, "ChartWebWidgetView - Creating Line Chart");

                // Find columns index for X Axis
                String xcol = wwg.getPropByName("x_axis");
                if (Utils.isEmpty(xcol))
                    //-- 20
                    throw new ClientError(20, "X Axis Column is not defined");
                xidx = cmap.get(xcol);
                if (xidx == null)
                    //-- 27
                    throw new ClientError(27, "X Axis Column [" + xcol +
                                            "] not found in result dataset");

                // Check if xaxis data is date type
                boolean fxdate = (columns.get(xidx).getJavaType().lastIndexOf(".Date") ==
                                            columns.get(xidx).getJavaType().length() - 5);

                    // Check each series and get indexes for Y Axis
                ArrayList<Integer> slist = new ArrayList<>();
                ArrayList<List<Entry>> ldsl = new ArrayList<>();
                List<Map<String, String>> series = wwg.getPropGroup("series");
                if (series == null)
                    //-- 28
                    throw new ClientError(28, "Series for Y Axis Column not defined");

                for (int i = 0; i < series.size(); i++) {
                    Map<String, String> smap = series.get(i);
                    ldsl.add(new ArrayList<Entry>());
                    String ycol = smap.get("y_axis");

                    if (Utils.isEmpty(ycol))
                        //-- 29
                        throw new ClientError(29, "Y Axis Column is not defined for series #" + i);

                    Integer yidx = cmap.get(ycol);
                    if (yidx == null)
                        //-- 30
                        throw new ClientError(30, "Y Axis Column [" + ycol +
                                "] from series #" + i + " not found in result dataset");

                    slist.add(yidx);
                }

                // Go through data array and create line chart dataset
                ArrayList<String> xds = new ArrayList<>();
                ArrayList<ILineDataSet> yds = new ArrayList<>();

                // Flag to show point labels
                boolean fspl = "true".equals(wwg.getPropByName("is_show_point_labels"));

                // Y Axis formatter
                String yaxis_fmt = wwg.getPropByName("y_axis_fmt");

                // Fill up data set
                for (int i = 0; i < jdata.length(); i++) {
                    JSONArray jrow;

                    try {
                        jrow = jdata.getJSONArray(i);
                    } catch (JSONException e) {
                        //-- 31
                        throw new ClientError(31, e, "Unable retrieve row #" + i + " from dataset");
                    }

                    try {
                        xds.add(jrow.getString(xidx));
                    } catch (JSONException e) {
                        //-- 32
                        throw new ClientError(32, e, "Unable retrieve X Axis value from row #" + i);
                    }

                    // List<Entry> ds = new ArrayList<Entry>();
                    for (int j = 0; j < slist.size(); j++ ) {
                        int sidx = slist.get(j);
                        List<Entry> ds = ldsl.get(j);
                        try {
                            if (!jrow.isNull(sidx))
                                ds.add(new Entry((float)
                                        (!fspl || Utils.isEmpty(yaxis_fmt) ? jrow.getDouble(sidx) :
                                                Double.parseDouble(String.format(
                                                        yaxis_fmt, jrow.getDouble(sidx)))), i));
                        } catch (JSONException e) {
                            //-- 33
                            throw new ClientError(33, e,
                                        "Unable retrieve Y Axis value from row #" + i);
                        }
                    }
                }

                for (int i = 0; i < slist.size(); i++ ) {
                    String cstr = series.get(i).get("color");
                    int color = Utils.isEmpty(cstr) ?
                            Constants.DEF_COLORS[i % Constants.DEF_COLORS.length] :
                                                                        Color.parseColor(cstr);
                    LineDataSet lds = new LineDataSet(ldsl.get(i), series.get(i).get("label"));

                    lds.setColor(color);
                    lds.setDrawCircles(true);
                    lds.setCircleColor(color);
                    lds.setDrawCircleHole(false);

                    Log.d(Constants.DEBUG_TAG,
                            "ChartWebWidgetView - Circle Raduis Before:" + lds.getCircleRadius());

                    if (isThumbnail()) {
                        // Double line for thumbnail chart
                        lds.setLineWidth(lds.getLineWidth() * 2);

                        // Double highlight circles. Some bug in library require code below
                        lds.setCircleRadius(lds.getCircleRadius());
                    } else {
                        // Adjust point circle
                        if (!Constants.IS_LARGE)
                            lds.setCircleRadius(lds.getCircleRadius()/2);
                    }

                    Log.d(Constants.DEBUG_TAG,
                            "ChartWebWidgetView - Circle Raduis After:" + lds.getCircleRadius() +
                            ": Is Thumnb: " + isThumbnail() + ":IS_LARGE:" + Constants.IS_LARGE);

                    lds.setDrawValues(fspl);
                    yds.add(lds);
                }

                // LineDataSet lds = new LineDataSet(ds, null);

                // lds.setLineWidth(15);
                // lds.setCircleColor(Color.BLUE);

                // Check if series color set
                // String color = wwg.getPropGroup("series").get(j)

                // yds.add(lds);

                chart = new LineChart(getContext());
                chart.setLayoutParams(new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                                            LayoutParams.MATCH_PARENT));
                // create a custom MarkerView (extend MarkerView) and specify the layout
                // to use for it
                DataMarkerView mv = new DataMarkerView(getContext(), R.layout.marker_view);

                // set the marker to the chart
                chart.setMarkerView(mv);

                LineData data = new LineData(xds, yds);
                if (fspl && !Utils.isEmpty(yaxis_fmt))
                    data.setValueFormatter(new NumberValueFormatter(yaxis_fmt));

                ((LineChart) chart).setData(data);

                chart.getData().setHighlightEnabled(!"true".equals(
                                    wwg.getPropByName("is_show_point_labels")));

                ((LineChart) chart).getAxisRight().setEnabled(false);
                // ((LineChart) chart).setHighlightPerDragEnabled(true);
                String xaxis_fmt = wwg.getPropByName("x_axis_fmt");
                if (!Utils.isEmpty(xaxis_fmt)) {
                    if (fxdate)
                        // Special date formatting for X Axis
                        chart.getXAxis().setValueFormatter(new XDateFormatter(xaxis_fmt));
                }
                chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

                String xaxis_lbl = wwg.getPropByName("x_axis_label");
                String yaxis_lbl = wwg.getPropByName("y_axis_label");

                if (!isThumbnail()) {
                    if (!Utils.isEmpty(xaxis_lbl))
                        chart.setDescription(xaxis_lbl);

                    if (!Utils.isEmpty(yaxis_lbl)) {
                        LinearLayout ll = new LinearLayout(getContext());

                        ll.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                                LayoutParams.MATCH_PARENT));
                        ll.setOrientation(LinearLayout.HORIZONTAL);

                        getChartViewBody().addView(ll);
                        setChartViewBody(ll);

                        VerticalTextView yAxisLabel = new VerticalTextView(getContext(), null);
                        yAxisLabel.setRotation(false);
                        yAxisLabel.setText(yaxis_lbl);
                        yAxisLabel.setGravity(Gravity.CENTER_HORIZONTAL);
                        FrameLayout.LayoutParams yparams = new FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.WRAP_CONTENT,
                                LayoutParams.MATCH_PARENT);
                        // yparams.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
                        getChartViewBody().addView(yAxisLabel, yparams);

                        /*
                         FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.WRAP_CONTENT,
                                    FrameLayout.LayoutParams.WRAP_CONTENT);
                        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
                        params.setMargins(0, 0, 0, 20);
                        container.addView(xAxisName, params);
                        */
                    }
                }

                break;

            case "com.osbitools.charts.jqplot.pie":
                break;

            case "com.osbitools.demo.tbl_hbar":
                break;

            case "com.osbitools.charts.jqplot.bar":
                break;

            default:
                //-- 18
                throw new ClientError(18, "Unknown chart class " + wcn);
        }

        // TODO
        if (chart != null) {
            Log.d(Constants.DEBUG_TAG, "ChartWebWidgetView - Show Chart. Thumbnail: " +
                                                                            isThumbnail());

            if (isThumbnail()) {
                // Draw on bitmap
                int size = Constants.DISPLAY_HEIGHT < Constants.DISPLAY_WIDTH ?
                        Constants.DISPLAY_HEIGHT : Constants.DISPLAY_WIDTH;
                Log.d(Constants.DEBUG_TAG, "ChartWebWidgetView - Bitmap Size: " +
                                                                    size + "x" + size);

                // chart.setLayoutParams(new ViewGroup.LayoutParams(size, size));
                Bitmap bm = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bm);

                chart.layout(0, 0, size, size);

                // Strip all axes
                XAxis xaxis = chart.getXAxis();
                xaxis.setAxisLineWidth(xaxis.getAxisLineWidth() * 2);
                xaxis.setEnabled(true);
                xaxis.setDrawLabels(true);
                xaxis.setDrawAxisLine(true);
                xaxis.setDrawGridLines(false);
                xaxis.setPosition(XAxis.XAxisPosition.BOTTOM);

                YAxis yaxis = ((LineChart) chart).getAxisLeft();
                yaxis.setEnabled(true);
                yaxis.setAxisLineWidth(yaxis.getAxisLineWidth() * 2);
                yaxis.setDrawGridLines(false);
                yaxis.setDrawAxisLine(true);

                chart.getLegend().setEnabled(false);

                chart.draw(canvas);

                ImageView vimg = new ImageView(getContext());
                // vimg.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
                vimg.setImageBitmap(bm);

                getChartViewBody().addView(vimg);
            } else {
                if ("true".equals(wwg.getPropByName("is_animate")))
                    chart.animateX(2000);

                chart.getLegend().setEnabled(
                        "true".equals(wwg.getPropByName("is_show_legend")));
                chart.getLegend().setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);

                // chart.setTouchEnabled(true);
                chart.setOnChartValueSelectedListener(this);
                ((LineChart) chart).setDrawGridBackground(false);

                getChartViewBody().addView(chart);
            }
        }
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        Log.d(Constants.DEBUG_TAG, "ChartWebWidgetView - Entry selected: " + e.toString());

        /*
        Log.i("LOWHIGH", "low: " + mChart.getLowestVisibleXIndex() + "," +
                                    "high: " + mChart.getHighestVisibleXIndex());
        Log.i("MIN MAX", "xmin: " + mChart.getXChartMin() + ", " +
                "xmax: " + mChart.getXChartMax() + ", ymin: " + mChart.getYChartMin() + ", " +
                    "ymax: " + mChart.getYChartMax());
        */
    }

    @Override
    public void onNothingSelected() {}

    @Override
    public ChartWebWidget getWebWidget() {
        return (ChartWebWidget) getWebWidgetPtr();
    }

    public ViewGroup getChartViewBody() {
        return _vbody;
    }

    public void setChartViewBody(ViewGroup view) {
        _vbody = view;
    }
}
