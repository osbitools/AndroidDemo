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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.osbitools.android.demo.ProjectList.ProjectItem;
import com.osbitools.android.shared.Constants;

/**
 * November 18, 2015
 *
 * A fragment representing a list of Projects.
 *
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ProjectListFragment extends Fragment {

    // Index of selected Project Item
    private Integer _pidx;

    // Fragment view
    View _view;

    private OnListFragmentInteractionListener mListener;

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(ProjectList.ProjectItem item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                                        Bundle savedInstanceState) {
        Log.d(Constants.DEBUG_TAG, "ProjectListFragment - onCreateView");

        _view = inflater.inflate(R.layout.project_list, container, false);
        final ListView plist = (ListView) _view.findViewById(R.id.project_list);

        final ProjectListAdapter adapter = new ProjectListAdapter(getContext(),
                R.layout.project_item, ProjectList.ITEMS.toArray(
                            new ProjectItem[ProjectList.ITEMS.size()]));

        plist.setAdapter(adapter);

        plist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                Log.d(Constants.DEBUG_TAG, "ProjectListAdapter - Selecting Views idx: " + position);
                v.setSelected(true);
                _pidx = position;

                // Transfer control to parent Activity
                mListener.onListFragmentInteraction(ProjectList.ITEMS.get(position));
            }
        });

        if (Constants.IS_LARGE) {
            _view.setLayoutParams(new LinearLayout.
                    LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 5f));

            if (_pidx != null) {
                adapter.setSelectedPosition(_pidx);
                plist.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(Constants.DEBUG_TAG, "ProjectListFragment: " +
                                                "POST performItemClick Selected " + _pidx);
                        plist.performItemClick(adapter.getSelectedView(), _pidx, 0);
                    }
                });
            }
        }

        return _view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private class ProjectListAdapter extends ArrayAdapter<ProjectItem> {

        private final Activity _ctx;
        private final ProjectItem[] _list;

        // Selected index
        private Integer _sidx;

        // Selected view
        private View _sview;

        public ProjectListAdapter(Context context, int rid, ProjectItem[] objects) {
            super(context, rid, objects);
            _ctx = (Activity) context;
            _list = objects;
        }

        public void setSelectedPosition(int idx) {
            _sidx = idx;
        }

        public View getSelectedView() {
            return _sview;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            Log.d(Constants.DEBUG_TAG, "ProjectListAdapter: getView position " +
                                                                position + ":" + view);
            if (view == null) {
                view = getLayoutInflater(Bundle.EMPTY).
                        inflate(R.layout.project_item, parent, false);

                TextView pname = (TextView) view.findViewById(R.id.pname);
                pname.setText(getResources().getIdentifier(_list[position].id + "_name",
                        "string", getContext().getPackageName()));

                TextView pdescr = (TextView) view.findViewById(R.id.pdescr);
                pdescr.setText(getResources().getIdentifier(_list[position].id + "_descr",
                        "string", getContext().getPackageName()));

                if (_sidx != null && _sidx == position) {
                    _sview = view;
                    Log.d(Constants.DEBUG_TAG, "ProjectListAdapter: getView Selected:" + position);
                }
            }

            return view;
        }
    }
}
