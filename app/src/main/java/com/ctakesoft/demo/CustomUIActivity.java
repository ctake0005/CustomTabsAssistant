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

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.ctakesoft.ctassistant.Assistant;
import com.ctakesoft.ctassistant.AssistantIntent;
import com.ctakesoft.ctassistant.ConnectionCallback;

public class CustomUIActivity extends AppCompatActivity implements View.OnClickListener, ConnectionCallback {
    private static final String TAG = CustomUIActivity.class.getSimpleName();

    private EditText mUrlEditText;
    private EditText mCustomTabColorEditText;
    private CheckBox mShowActionButtonCheckbox;
    private CheckBox mAddMenusCheckbox;
    private CheckBox mShowTitleCheckBox;
    private CheckBox mCustomAnimationCheckBox;
    private CheckBox mAutoHideAppBarCheckbox;
    private CheckBox mChangeCloseButtonCheckbox;

    private Assistant mAssistant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_ui);
        findViewById(R.id.start_custom_tab).setOnClickListener(this);
        findViewById(R.id.button_may_launch_url).setOnClickListener(this);

        mUrlEditText = (EditText) findViewById(R.id.url);
        mCustomTabColorEditText = (EditText) findViewById(R.id.custom_toolbar_color);
        mShowActionButtonCheckbox = (CheckBox) findViewById(R.id.custom_show_action_button);
        mAddMenusCheckbox = (CheckBox) findViewById(R.id.custom_add_menus);
        mShowTitleCheckBox = (CheckBox) findViewById(R.id.show_title);
        mCustomAnimationCheckBox = (CheckBox) findViewById(R.id.custom_animation);
        mAutoHideAppBarCheckbox = (CheckBox) findViewById(R.id.auto_hide_checkbox);
        mChangeCloseButtonCheckbox = (CheckBox) findViewById(R.id.custom_back_button_checkbox);

        mAssistant = new Assistant(this, null);
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
                openCustomTab();
                break;
            case R.id.button_may_launch_url:
                String url = mUrlEditText.getText().toString();
                mAssistant.preLoad(url);
                break;
            default:
                //Unknown View Clicked
        }
    }

    private void openCustomTab() {
        String url = mUrlEditText.getText().toString();

        AssistantIntent.Builder builder = mAssistant.createIntentBuilder();

        try {
            int color = Color.parseColor(mCustomTabColorEditText.getText().toString());
            builder.setToolbarColor(color);
        } catch (IllegalArgumentException | StringIndexOutOfBoundsException e) {
            Log.i(TAG, "Unable to parse Color: " + mCustomTabColorEditText.getText());
        }

        if (mShowActionButtonCheckbox.isChecked()) {
            builder.setActionButtonForShareUrl();
        }

        if (mAddMenusCheckbox.isChecked()) {
            String menuItemTitle = getString(R.string.menu_item_title);
            builder.addMenuItemForStartActivity(menuItemTitle, NextActivity.class);
        }

        builder.setShowTitle(mShowTitleCheckBox.isChecked());

        if (mCustomAnimationCheckBox.isChecked()) {
            builder.setStartAnimationsRightToLeft();
            builder.setExitAnimationsLeftToRight();
        }

        if (mAutoHideAppBarCheckbox.isChecked()) {
            builder.enableUrlBarHiding();
        }

        if (mChangeCloseButtonCheckbox.isChecked()) {
            builder.setCloseButtonIconToArrowBack();
        }

        mAssistant.launch(builder.build(), url);
    }

    @Override
    public void onConnected() {
        Log.d(TAG, "onConnected() called with " + "");
    }

    @Override
    public void onDisconnected() {
        Log.d(TAG, "onDisconnected() called with " + "");
    }

    @Override
    public void onFailed() {
        Log.d(TAG, "onFailed() called with " + "");
    }
}
