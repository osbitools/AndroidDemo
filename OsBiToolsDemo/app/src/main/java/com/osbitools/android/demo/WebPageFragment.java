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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;

import com.osbitools.android.demo.views.WebWidgetView;
import com.osbitools.android.shared.Constants;
import com.osbitools.android.shared.Utils;
import com.osbitools.android.shared.WebPage;
import com.osbitools.android.shared.wwg.WebWidget;

/**
 * November 18, 2015
 *
 * A fragment representing a list of Web Pages.
 *
 * Activities containing this fragment MUST implement the {@link OnWebWidgetItemFragmentInteractionListener}
 * interface.
 */
public class WebPageFragment extends Fragment implements OnWebPageFragmentUpdateListener {

    // Pointer on parent activity
    private OnWebWidgetItemFragmentInteractionListener _pa;

    // Top frame
    private FrameLayout _tframe;

    private LayoutInflater _inflater;

    // Pointer on grid view to display multiple widgets
    GridView _gview;

    // Display pointer
    Display _display;

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnWebWidgetItemFragmentInteractionListener {
        void OnWebWidgetItemFragmentInteractionListener(WebWidget wwg);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                                        Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        _display = getActivity().getWindowManager().getDefaultDisplay();

        _inflater = inflater;
        _tframe = (FrameLayout) inflater.inflate(R.layout.wp, container, false);

        // Read Web Page from bundle
        Bundle b = getArguments();
        if (b != null)
            onWebPageFragmentUpdate((WebPage) b.getSerializable("wp"));

        return _tframe;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnWebWidgetItemFragmentInteractionListener) {
            _pa = (OnWebWidgetItemFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnWebWidgetItemFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        _pa = null;
    }

    @Override
    public void onWebPageFragmentUpdate(final WebPage wp) {
        int cnt = wp.getMaxPanelWidgetCount();
        if (cnt == 1) {
            WebWidgetView view = Utils.getWebWidgetView(getContext(),
                                                    wp.getTopWebWidget(0), false);
            _tframe.addView(view);
        } else {
            // if (_gview == null)
                // Add dynamically grid view
                _gview = (GridView) _inflater.inflate(R.layout.wp_grid, _tframe).
                                                                findViewById(R.id.wwg_list);

            int ornt = _display.getOrientation();
            int mlen = ((ornt == Surface.ROTATION_90 || ornt == Surface.ROTATION_270) ?
                    Constants.DISPLAY_WIDTH :
                        Constants.DISPLAY_HEIGHT) / Constants.MIN_CHART_THUMB_WIDTH;

            _gview.setNumColumns(Math.min(mlen, wp.getMaxPanelWidgetCount()));
            _gview.setAdapter(new WebPagePanelAdapter(getContext(), wp));

            if (!Constants.IS_LARGE) {
                _gview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView parent, View view, int position, long id) {
                        _pa.OnWebWidgetItemFragmentInteractionListener(
                                                        wp.getTopWebWidget(position));
                    }
                });
            }
        }
    }
}
