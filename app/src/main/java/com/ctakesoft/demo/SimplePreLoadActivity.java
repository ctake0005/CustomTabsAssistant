/*
 * Copyright 2015 ctakesoft.com<hal1000@ctakesoft.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ctakesoft.demo;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ctakesoft.ctassistant.Assistant;
import com.ctakesoft.ctassistant.ConnectionCallback;

/**
 * The simplest way to use Chrome Custom Tabs with speeding process.
 */
public class SimplePreLoadActivity extends AppCompatActivity implements View.OnClickListener {

    private Assistant mAssistant;
    private String mUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_preload);

        final View button = findViewById(R.id.start_custom_tab);
        button.setOnClickListener(this);
        button.setEnabled(false);
        mAssistant = new Assistant(this, new ConnectionCallback() {
            @Override
            public void onConnected() {
                mAssistant.preLoad(mUrl = getResources().getString(R.string.default_test_url));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        button.setEnabled(true);
                    }
                }, 2000); // Maybe, preload is finished.
            }

            @Override
            public void onDisconnected() {
            }

            @Override
            public void onFailed() {
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAssistant.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAssistant.disConnect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAssistant.destroy();
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        switch (viewId) {
            case R.id.start_custom_tab:
                mAssistant.launch(mAssistant.createIntentBuilder().build(), mUrl);
                break;
            default:
                //Unknown View Clicked
        }
    }
}
