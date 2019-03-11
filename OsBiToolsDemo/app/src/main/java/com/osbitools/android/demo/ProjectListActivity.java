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

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.osbitools.android.shared.*;
import com.osbitools.android.shared.wwg.WebWidget;

/**
 * November 18, 2015
 *
 * A screen with loaded web page that offers navigation between web widgets.
 */
public class ProjectListActivity extends NetProgressActivity
                implements ProjectListFragment.OnListFragmentInteractionListener,
                                WebPageListFragment.OnListFragmentInteractionListener,
                                    WebPageFragment.OnWebWidgetItemFragmentInteractionListener {

    // Keep track of the logout task to ensure we can cancel it if requested.
    private UserLogoutTask mLogoutTask = null;

    // Pointer on ProjectListFragment
    ProjectListFragment _pl_frag;

    // Pointer on WebPageList fragment
    private WebPageListFragment _wpl_frag;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_project_list);

        init();

        // However, if we're being restored from a previous state,
        // then we don't need to do anything and should return or else
        // we could end up with overlapping fragments.
        if (savedInstanceState != null) {
            Log.d(Constants.DEBUG_TAG, "ProjectListActivity: restored from previous state");
            return;
        }

        // Create an instance of ProjectListFragment
        Log.d(Constants.DEBUG_TAG, "ProjectListActivity: Creating ProjectListFragment");
        _pl_frag = new ProjectListFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.pl_frame, _pl_frag);
        if (Constants.IS_LARGE) {
            // For large layout add Web Page List Frame
            _wpl_frag = new WebPageListFragment();
            transaction.add(R.id.pl_frame, _wpl_frag);
        }

        transaction.commit();
    }

    @Override
    protected View getTopView() {
        return findViewById(R.id.pl_frame);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (Constants.IS_SECURITY) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_wp, menu);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.btn_sign_out)
            logout();
        return true;
    }

    @Override
    public void onBackPressed() {
        // Check if FragmentLayout. If yes and Project List is active than just logout
        if (Constants.IS_SECURITY && findViewById(R.id.project_list) != null)
            logout();
        else {
            super.onBackPressed();
        }
    }

    @Override
    protected View getProgressView() {
        return findViewById(R.id.logout_progress);
    }

    private void logout() {
        if (mLogoutTask != null)
            // Already running
            return;

        // Hide project list
        // getTopView().setVisibility(View.GONE);

        // Show a progress spinner, and kick off a background task to
        // perform the user login attempt.
        showProgress(true);

        mLogoutTask = new UserLogoutTask();
        mLogoutTask.execute();
    }

    @Override
    public void onListFragmentInteraction(ProjectList.ProjectItem item) {

        // Look for the Web Page List fragment from the activity layout
        // WebPageListFragment frag = (WebPageListFragment)
           //     getSupportFragmentManager().findFragmentById(R.id.wp_list_frag);

        if (_wpl_frag != null) {
            // If Web Page List fragment is available, we're in multi-pane layout...
            Log.d(Constants.DEBUG_TAG, "Updating existing WebPageListFragment");

            // Call a method in the ArticleFragment to update its content
            _wpl_frag.onWebPageListFragmentUpdate(item);

        } else {
            // If the frag is not available, we're in the one-pane layout and must swap frags...

            // Create fragment and give it an argument for the selected article
            Bundle args = new Bundle();
            args.putSerializable("pitem", item);

            // Create local variable
            WebPageListFragment frag = new WebPageListFragment();
            frag.setArguments(args);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.pl_frame, frag);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
        }
    }

    // @Override
    public void onListFragmentInteraction(WebPageList.WebPageItem item) {
        // Create fragment and give it an argument for the selected web page
        Bundle args = new Bundle();
        args.putSerializable("wp", item.getWebPage());

        WebPageFragment frag = new WebPageFragment();
        frag.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        if (Constants.IS_LARGE) {
            transaction.remove(_pl_frag).remove(_wpl_frag).add(R.id.pl_frame, frag);
        } else {
            transaction.replace(R.id.pl_frame, frag);
            // transaction.addToBackStack(null);
        }

        // Commit the transaction
        transaction.addToBackStack(null).commit();
    }

    @Override
    public void OnWebWidgetItemFragmentInteractionListener(WebWidget wwg) {
        // Create fragment and give it an argument for the selected web page
        Bundle args = new Bundle();
        args.putSerializable("wwg", wwg);

        WebWidgetFragment frag = new WebWidgetFragment();
        frag.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.pl_frame, frag);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

    /**
     * Represents an asynchronous logout task used to authenticate
     * the user.
     */
    public class UserLogoutTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Utils.logout();
            } catch (ClientError e) {
                // Ignore error
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mLogoutTask = null;
            showProgress(false, true);

            finish();
        }
    }
}
