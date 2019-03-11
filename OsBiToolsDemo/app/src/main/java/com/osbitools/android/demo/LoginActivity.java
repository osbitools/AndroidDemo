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
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.osbitools.android.shared.*;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Map;

/**
 * November 16, 2015
 *
 * A login screen that offers login via username/password.
 */
public class LoginActivity extends NetProgressActivity {

    // Keep track of the login task to ensure we can cancel it if requested.
    private UserLoginTask mAuthTask = null;

    // Progress form during login
    private View _vprogress;

    // Top login form
    private View _vlogin;

    private EditText mPasswordView;
    private AutoCompleteTextView mUsernameView;
    private TextView mErrorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Display display = getWindowManager().getDefaultDisplay();
        Constants.DISPLAY_WIDTH = display.getWidth();
        Constants.DISPLAY_HEIGHT = display.getHeight();

        try {
            // Load Web Widget for each project/file
            for (Map.Entry<String, ProjectList.ProjectItem> entry :
                    ProjectList.ITEM_MAP.entrySet()) {
                ProjectList.ProjectItem pitem = entry.getValue();

                // Load lang set
                int rid = this.getResources().
                        getIdentifier(pitem.id + "_ll_set", "xml", this.getPackageName());
                if (rid == 0)
                    throw new RuntimeException("LangLabelsSet file " + pitem.id +
                            "_ll_set.xml not found.");
                pitem.loadLangSetRes(this.getResources().getXml(rid));

                for (String fname : pitem.getFiles()) {
                    String fn = pitem.id + "_" + fname;
                    rid = this.getResources().getIdentifier(fn, "xml", this.getPackageName());
                    if (rid == 0)
                        throw new RuntimeException("XML file " + fn + ".xml not found.");
                }

                // Sort web page after
                pitem.getWebPageList().sort();
            }
        } catch (IOException | XmlPullParserException e) {
            // Log & finish
            Log.wtf(Constants.DEBUG_TAG, "Unable load web page from xml");
            finish();
        }

        setContentView(R.layout.activity_login);

        // Set up the login form.
        mPasswordView = (EditText) findViewById(R.id.pwd);
        mUsernameView = (AutoCompleteTextView) findViewById(R.id.usr);
        mErrorView = (TextView) findViewById(R.id.error);
        final Button mSignInButton = (Button) findViewById(R.id.sign_in_button);

        /// TEST START
        mUsernameView.setText("user");
        mPasswordView.setText("test1234");
        mSignInButton.setEnabled(true);
        /// TEST END

        // Check if network present
        Integer error = null;
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null) {
            error = R.string.err_net_not_available;
        } else if (!networkInfo.isConnected()) {
            error = R.string.err_net_not_connected;
        }

        if (error != null) {
            mErrorView.setText(getString(error));
            mErrorView.setVisibility(View.VISIBLE);
            return;
        }

        mUsernameView.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable text) {
                mErrorView.setVisibility(View.GONE);
                mSignInButton.setEnabled(text.length() > 0 &&
                        mPasswordView.getText().length() > 0);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mPasswordView.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable text) {
                mErrorView.setVisibility(View.GONE);
                mSignInButton.setEnabled(text.length() > 0 &&
                        mUsernameView.getText().length() > 0);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        _vlogin = findViewById(R.id.login_form);
        _vprogress = findViewById(R.id.login_progress);

        init();
    }

    @Override
    protected View getTopView() {
        return _vlogin;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Constants.IS_SECURITY)
            _vlogin.setVisibility(View.VISIBLE);
    }

    @Override
    protected View getProgressView() {
        return _vprogress;
    }

    /**
     * Attempts to sign in OsBiCore Web Service.
     */
    private void attemptLogin() {
        if (mAuthTask != null)
            return;

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString();

        // Show a progress spinner, and kick off a background task to
        // perform the user login attempt.
        mErrorView.setVisibility(View.GONE);
        showProgress(true);

        mAuthTask = new UserLoginTask(username, mPasswordView.getText().toString());
        mAuthTask.execute((Void) null);
    }

    /**
     * Use an AsyncTask to fetch the username on a background thread, and update
     * the username text field with results on the main UI thread.
     */
    /*
    class SetupEmailAutoCompleteTask extends AsyncTask<Void, Void, List<String>> {

        @Override
        protected List<String> doInBackground(Void... voids) {
            ArrayList<String> emailAddressCollection = new ArrayList<>();

            // Get all emails from the user's contacts and copy them to a list.
            ContentResolver cr = getContentResolver();
            Cursor emailCur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                    null, null, null);
            while (emailCur.moveToNext()) {
                String username = emailCur.getString(emailCur.getColumnIndex(ContactsContract
                        .CommonDataKinds.Email.DATA));
                emailAddressCollection.add(username);
            }
            emailCur.close();

            return emailAddressCollection;
        }

        @Override
        protected void onPostExecute(List<String> emailAddressCollection) {
            addEmailsToAutoComplete(emailAddressCollection);
        }
    }
    */

    /*
    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mUsernameView.setAdapter(adapter);
    }
    */

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String _usr;
        private final String _pwd;

        // Error message during login
        private ClientError _err;

        UserLoginTask(String usr, String pwd) {
            _usr = usr;
            _pwd = pwd;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // Attempting authentication against a OsBiTools Core Web Service.
            try {
                _err = null;
                return Utils.login(_usr, _pwd);
            } catch (ClientError e) {
                _err = e;
                Log.e(Constants.DEBUG_TAG, e.getFullMessage());
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false, success);

            if (success) {
                // Transfer control to ProjectList Activity
                Intent intent = new Intent(LoginActivity.this, ProjectListActivity.class);
                startActivity(intent);
            } else {
                final TextView mErrorView = (TextView) findViewById(R.id.error);
                mErrorView.setText(_err.getFullMessage("\n"));
                mErrorView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}
