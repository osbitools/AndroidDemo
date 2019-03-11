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

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.osbitools.android.demo.R;
import com.osbitools.android.demo.ProjectList.ProjectItem;
import com.osbitools.android.shared.Constants;

/**
 * November 18, 2015
 *
 * A fragment representing a list of Web Pages.
 *
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class WebPageListFragment extends Fragment implements OnWebPageListFragmentUpdateListener {
    // Pointer on parent activity
    private OnListFragmentInteractionListener _pa;

    // Pointer on Web PAge List
    private ListView _lview;

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(WebPageList.WebPageItem item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                                        Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.wp_list, container, false);
        _lview = (ListView) view.findViewById(R.id.wp_list);

        super.onCreate(savedInstanceState);

        // Read Web Page List from bundle
        ProjectItem item;

        Bundle b = getArguments();
        if (b != null) {
            item = (ProjectItem) b.getSerializable("pitem");
            onWebPageListFragmentUpdate(item);
        }

        if (Constants.IS_LARGE) {
            LinearLayout.LayoutParams params = new LinearLayout.
                    LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 5f);
            params.setMargins((int) getResources().getDimension(R.dimen.cell_vpadding), 0, 0, 0);
            view.setLayoutParams(params);
        }

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnListFragmentInteractionListener) {
            _pa = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        _pa = null;
    }

    private class WebPageListAdapter extends ArrayAdapter<WebPageList.WebPageItem> {

        private final Activity _ctx;
        private final WebPageList.WebPageItem[] _list;

        public WebPageListAdapter(Context context, int rid, WebPageList.WebPageItem[] objects) {
            super(context, rid, objects);
            _ctx = (Activity) context;
            _list = objects;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            Log.d(Constants.DEBUG_TAG, "WebPageListAdapter: getView position " +
                                                                    position + ":" + view);

            if (view == null) {
                view = getLayoutInflater(Bundle.EMPTY).
                        inflate(R.layout.project_item, parent, false);

                TextView pname = (TextView) view.findViewById(R.id.pname);
                pname.setText(_list[position].getFileName());

                TextView pdescr = (TextView) view.findViewById(R.id.pdescr);
                pdescr.setText(_list[position].getDescr());
            }

            return view;
        }
    }

    @Override
    public void onWebPageListFragmentUpdate(ProjectList.ProjectItem item) {
        // Refresh Web Page List

        final WebPageList wpl = item.getWebPageList();

        WebPageListAdapter adapter = new WebPageListAdapter(getContext(),
                R.layout.wp_item, wpl != null ? wpl.getItems() : null);
        _lview.setAdapter(adapter);

        _lview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                v.setSelected(true);
                WebPageList.WebPageItem item = wpl.getItem(position);

                // Transfer control to parent Activity
                _pa.onListFragmentInteraction(item);
            }
        });
    }
}
