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
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.osbitools.android.demo.ProjectListActivity;
import com.osbitools.android.demo.R;
import com.osbitools.android.shared.ClientError;
import com.osbitools.android.shared.Constants;
import com.osbitools.android.shared.Utils;
import com.osbitools.android.shared.wwg.ContainerWebWidget;
import com.osbitools.android.shared.wwg.DataSetWebWidget;
import com.osbitools.android.shared.wwg.WebWidget;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

/**
 * April 16, 2016
 *
 * View for DataSet based Web Widget
 *
 */
public abstract class DataSetWebWidgetView<T> extends WebWidgetView<DataSetWebWidget> {

    // Progress Bar
    private View _vprogress;

    // Error text
    private TextView _verror;

    // DataSet retrieval task
    private DataSetRequestTask _task;

    public DataSetWebWidgetView(Context context) {
        super(context);
    }

    public DataSetWebWidgetView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DataSetWebWidgetView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int w = getMeasuredWidth();
        Log.d(Constants.DEBUG_TAG, "DataSetWebWidgetView - onMeasure: width " + w);
        setMeasuredDimension(w, w); //Snap to width

        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    @Override
    public void init(Context context, WebWidget wwg, boolean thumb) {
        super.init(context, wwg, thumb);

        this.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                                                             ViewGroup.LayoutParams.FILL_PARENT));
        LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.wwg_ds, this, true);

        _vprogress = this.findViewById(R.id.ds_progress);

        _verror = (TextView) this.findViewById(R.id.ds_error);

        if (getWebWidget().hasJson()) {
            // Go directly on processing
            doOnDataReady(getWebWidget().getJson());
        } else {
            // Start async. data retrieval thread
            _vprogress.setVisibility(View.VISIBLE);
            _task = new DataSetRequestTask(getWebWidget().getDataSetName(), this);
            _task.execute((Void) null);
        }
    }

    public void doOnDataSuccess(String jstr) {
        _vprogress.setVisibility(View.GONE);

        try {
            // Save json for further reuse
            JSONObject jobj = new JSONObject(jstr);
            getWebWidget().setJson(jobj);
            doOnDataReady(jobj);
        } catch (JSONException e) {
            //-- 17
            doOnDataError(new ClientError(17, e));
        }
    }

    private void doOnDataReady(JSONObject jobj) {
        try {
            onDataReady(jobj);
        } catch (ClientError clientError) {
            doOnDataError(clientError);
        }
    }

    public void doOnDataError(ClientError error) {
        _vprogress.setVisibility(View.GONE);

        _verror.setVisibility(VISIBLE);
        _verror.setText(error.getFullMessage());
    }

    public TextView getErrorTextView() {
        return _verror;
    }

    public abstract void onDataReady(JSONObject json) throws ClientError;

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class DataSetRequestTask extends AsyncTask<Void, Void, Boolean> {

        // DataSet Map Name
        private final String _mname;

        // Error message during login
        private ClientError _err;

        // Result set
        private String _json;

        private DataSetWebWidgetView _view;

        public DataSetRequestTask(String mname, DataSetWebWidgetView view) {
            _view = view;
            _mname = mname;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // Attempting authentication against a OsBiTools Core Web Service.
            try {
                _err = null;

                _json = Utils.getDataSet(_mname);
                return true;
            } catch (ClientError e) {
                _err = e;
                Log.e(Constants.DEBUG_TAG, e.getFullMessage());
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success)
                _view.doOnDataSuccess(_json);
            else
                _view.doOnDataError(_err);
        }
    }
}
