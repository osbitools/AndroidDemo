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

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;

import com.osbitools.android.shared.Config;
import com.osbitools.android.shared.Constants;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Map;

/**
 * April 19, 2015
 *
 * Displatcher activity
 */
public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(Constants.DEBUG_TAG, "Configuration loaded: " + Config.isLoaded());

        if (!Config.isLoaded()) {
            Log.d(Constants.DEBUG_TAG, "Loading project configuration");

            // Load Project List
            Config.load();

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

                        pitem.loadWebPage(fname, this.getResources().getXml(rid));
                    }

                    // Sort web page after
                    pitem.getWebPageList().sort();
                }
            } catch (IOException | XmlPullParserException e) {
                // Log & finish
                Log.wtf(Constants.DEBUG_TAG, "Unable load web page from xml");
                finish();
            }
        }

        Display display = getWindowManager().getDefaultDisplay();
        Constants.DISPLAY_WIDTH = display.getWidth();
        Constants.DISPLAY_HEIGHT = display.getHeight();

        int smask = (getResources().getConfiguration().screenLayout &
                                            Configuration.SCREENLAYOUT_SIZE_MASK);
        Constants.IS_LARGE = !(
                smask == Configuration.SCREENLAYOUT_SIZE_SMALL ||
                        smask == Configuration.SCREENLAYOUT_SIZE_NORMAL);
        Log.d(Constants.DEBUG_TAG, "Large layout: " + Constants.IS_LARGE);

        Intent intent = Constants.IS_SECURITY ? new Intent(this,LoginActivity.class) :
                                                    new Intent(this, ProjectListActivity.class);

        startActivity(intent);
        finish();
    }
}
