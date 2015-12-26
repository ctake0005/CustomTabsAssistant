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

package com.ctakesoft.ctassistant;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsCallback;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.customtabs.CustomTabsSession;
import android.text.TextUtils;
import android.util.Log;

import org.chromium.customtabsclient.shared.CustomTabsHelper;
import org.chromium.customtabsclient.shared.ServiceConnection;
import org.chromium.customtabsclient.shared.ServiceConnectionCallback;

public final class Assistant implements ServiceConnectionCallback {
    @SuppressWarnings("unused")
    private static final String TAG = Assistant.class.getSimpleName();
    @SuppressWarnings("unused")
    private final Assistant self = this;

    private static class NavigationCallback extends CustomTabsCallback {
        @Override
        public void onNavigationEvent(int navigationEvent, Bundle extras) {
            Log.i(TAG, "onNavigationEvent: Code = " + navigationEvent);
        }
    }

    private Context mContext;
    private ConnectionCallback mCallback;

    private CustomTabsSession mCustomTabsSession;
    private CustomTabsClient mClient;
    private CustomTabsServiceConnection mConnection;
    private String mPackageNameToBind;

    public Assistant(@NonNull Activity context, @Nullable ConnectionCallback callback) {
        mContext = context;
        mCallback = callback;
    }

    public void connect() {
        if (!bindCustomTabsService()) {
            if (mCallback != null) {
                mCallback.onFailed();
            }
        }
    }

    public void disConnect() {
        unbindCustomTabsService();
    }

    public void destroy() {
        mCallback = null;
    }

    public void preLoad(@NonNull String urlString) {
        // pre load
        CustomTabsSession session = getSession();
        if (session == null || !session.mayLaunchUrl(Uri.parse(urlString), null, null)) {
            Log.w(TAG, "createIntentBuilder: mayLaunchUrl failed");
        }
    }

    public AssistantIntent.Builder createIntentBuilder() {
        return new AssistantIntent.Builder(mContext, getSession());
    }

    public void launch(@NonNull AssistantIntent assistantIntent, @NonNull String urlString) {
        openCustomTab((Activity) mContext, assistantIntent, Uri.parse(urlString), new AssistantWebView());
    }

    private boolean bindCustomTabsService() {
        if (mClient != null) {
            return false;
        }
        if (TextUtils.isEmpty(mPackageNameToBind)) {
            mPackageNameToBind = CustomTabsHelper.getPackageNameToUse(mContext);
            if (mPackageNameToBind == null) {
                return false;
            }
        }
        mConnection = new ServiceConnection(this);
        boolean ok = CustomTabsClient.bindCustomTabsService(mContext, mPackageNameToBind, mConnection);
        if (!ok) {
            mConnection = null;
        }
        return ok;
    }

    private void unbindCustomTabsService() {
        if (mConnection == null) {
            // not connected.
            return;
        }
        mContext.unbindService(mConnection);
        mClient = null;
        mCustomTabsSession = null;
    }

    private CustomTabsSession getSession() {
        if (mClient == null) {
            mCustomTabsSession = null;
        } else if (mCustomTabsSession == null) {
            mCustomTabsSession = mClient.newSession(new NavigationCallback());
        }
        return mCustomTabsSession;
    }

    @Override
    public void onServiceConnected(CustomTabsClient client) {
        Log.d(TAG, "onServiceConnected() called with " + "client = [" + client + "]");
        mClient = client;

        if (mClient != null) {
            if (!mClient.warmup(0)) {
                Log.w(TAG, "onServiceConnected: WarmUp failed");
            }
        }

        if (mCallback != null) {
            mCallback.onConnected();
        }
    }

    @Override
    public void onServiceDisconnected() {
        Log.d(TAG, "onServiceDisconnected() called with " + "");
        mClient = null;
        if (mCallback != null) {
            mCallback.onDisconnected();
        }
    }

    /**
     * Opens the URL on a Custom Tab if possible. Otherwise fallsback to opening it on a WebView.
     *
     * @param activity        The host activity.
     * @param assistantIntent a CustomTabsIntent to be used if Custom Tabs is available.
     * @param uri             the Uri to be opened.
     * @param fallback        a CustomTabFallback to be used if Custom Tabs is not available.
     */
    static void openCustomTab(Activity activity,
                              AssistantIntent assistantIntent,
                              Uri uri,
                              CustomTabFallback fallback) {
        String packageName = CustomTabsHelper.getPackageNameToUse(activity);

        //If we cant find a package name, it means theres no browser that supports
        //Chrome Custom Tabs installed. So, we fallback to the webview
        if (packageName == null) {
            if (fallback != null) {
                fallback.openUri(activity, uri);
            }
        } else {
            assistantIntent.setPackage(packageName);
            assistantIntent.launchUrl(activity, uri);
        }
    }

    /**
     * To be used as a fallback to open the Uri when Custom Tabs is not available.
     */
    interface CustomTabFallback {
        /**
         * @param activity The Activity that wants to open the Uri.
         * @param uri      The uri to be opened by the fallback.
         */
        void openUri(Activity activity, Uri uri);
    }


}
