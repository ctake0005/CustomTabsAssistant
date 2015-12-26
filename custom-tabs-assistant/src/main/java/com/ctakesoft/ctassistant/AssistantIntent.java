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
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.AnimRes;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsSession;
import android.util.TypedValue;

import org.chromium.customtabsclient.shared.CustomTabsHelper;

public final class AssistantIntent {
    @SuppressWarnings("unused")
    private static final String TAG = AssistantIntent.class.getSimpleName();
    @SuppressWarnings("unused")
    private final AssistantIntent self = this;

    public static final class Builder {
        private Context mContext;
        private CustomTabsIntent.Builder mBuilder;

        private boolean mIsDefaultToolbarColor = true;
        private boolean mEnableUrlBarHiding = false;

        Builder(Context context, CustomTabsSession session) {
            mContext = context;
            mBuilder = new CustomTabsIntent.Builder(session);
        }

        /**
         * Sets the toolbar color.
         *
         * @param color {@link Color}
         */
        public Builder setToolbarColor(@ColorInt int color) {
            mBuilder.setToolbarColor(color);
            mIsDefaultToolbarColor = false;
            return this;
        }

        /**
         * Enables the url bar to hide as the user scrolls down on the page.
         */
        public Builder enableUrlBarHiding() {
            mEnableUrlBarHiding = true;
            return this;
        }

        /**
         * Sets the Close button icon for the custom tab.
         *
         * @param iconRes The icon drawable ID {@link android.graphics.drawable.Drawable}
         */
        public Builder setCloseButtonIcon(@DrawableRes int iconRes) {
            mBuilder.setCloseButtonIcon(BitmapFactory.decodeResource(mContext.getResources(), iconRes));
            return this;
        }

        /**
         * Sets the Close button icon to ArrowBack icon.
         */
        public Builder setCloseButtonIconToArrowBack() {
            mBuilder.setCloseButtonIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_arrow_back));
            return this;
        }

        /**
         * Sets whether the title should be shown in the custom tab.
         *
         * @param showTitle Whether the title should be shown.
         */
        public Builder setShowTitle(boolean showTitle) {
            mBuilder.setShowTitle(showTitle);
            return this;
        }

        /**
         * Sets the start animations,
         *
         * @param enterResId Resource ID of the "enter" animation for the browser.
         * @param exitResId  Resource ID of the "exit" animation for the application.
         */
        public Builder setStartAnimations(@AnimRes int enterResId, @AnimRes int exitResId) {
            mBuilder.setStartAnimations(mContext, enterResId, exitResId);
            return this;
        }

        /**
         * Sets the start animations to slide in right / slide out left,
         */
        public Builder setStartAnimationsRightToLeft() {
            mBuilder.setStartAnimations(mContext, R.anim.slide_in_right, R.anim.slide_out_left);
            return this;
        }

        /**
         * Sets the exit animations,
         *
         * @param enterResId Resource ID of the "enter" animation for the application.
         * @param exitResId  Resource ID of the "exit" animation for the browser.
         */
        public Builder setExitAnimations(@AnimRes int enterResId, @AnimRes int exitResId) {
            mBuilder.setExitAnimations(mContext, enterResId, exitResId);
            return this;
        }

        /**
         * Sets the exit animations to slide in left / slide out right,
         */
        public Builder setExitAnimationsLeftToRight() {
            mBuilder.setExitAnimations(mContext, R.anim.slide_in_left, R.anim.slide_out_right);
            return this;
        }

        // TODO: 2015/11/22 Menu & ActionButton
        /**
         * Adds a menu item.
         *
         * @param label Menu label.
         * @param pendingIntent Pending intent delivered when the menu item is clicked.
         */
        public Builder addMenuItem(@NonNull String label, @NonNull PendingIntent pendingIntent) {
            mBuilder.addMenuItem(label, pendingIntent);
            return this;
        }

        /**
         * Adds a menu item for starting activity.
         *
         * @param label Menu label.
         * @param activityCls Activity started when the menu item is clicked.
         */
        public Builder addMenuItemForStartActivity(@NonNull String label, @NonNull Class<?> activityCls) {
            Intent menuIntent = new Intent();
            menuIntent.setClass(mContext.getApplicationContext(), activityCls);
            // Optional animation configuration when the user clicks menu items.
//            Bundle menuBundle = ActivityOptions.makeCustomAnimation(mContext, android.R.anim.slide_in_left,
//                    android.R.anim.slide_out_right).toBundle();
//            PendingIntent pi = PendingIntent.getActivity(mContext.getApplicationContext(), 0, menuIntent, 0,
//                    menuBundle);
            PendingIntent pi = PendingIntent.getActivity(mContext.getApplicationContext(), 0, menuIntent, 0, null);
            addMenuItem(label, pi);
            return this;
        }

        /**
         * Set the action button.
         *
         * @param iconRes The icon drawable ID {@link android.graphics.drawable.Drawable}
         * @param description The description for the button. To be used for accessibility.
         * @param pendingIntent pending intent delivered when the button is clicked.
         * @param shouldTint Whether the action button should be tinted.
         */
        public Builder setActionButton(@DrawableRes int iconRes, @NonNull String description,
                                       @NonNull PendingIntent pendingIntent, boolean shouldTint) {
            Bitmap icon = BitmapFactory.decodeResource(mContext.getResources(), iconRes);
            mBuilder.setActionButton(icon, description, pendingIntent, shouldTint);
            return this;
        }

        /**
         * See {@link #setActionButton(int, String, PendingIntent, boolean)}
         */
        public Builder setActionButton(@DrawableRes int iconRes, @NonNull String description,
                                       @NonNull PendingIntent pendingIntent) {
            return setActionButton(iconRes, description, pendingIntent, false);
        }

        /**
         * Set the action button for sharing url.
         *
         * See {@link #setActionButton(int, String, PendingIntent, boolean)}
         */
        public Builder setActionButtonForShareUrl() {
            String shareLabel = mContext.getString(R.string.label_action_share);
            PendingIntent pendingIntent = createPendingIntent();

            return setActionButton(android.R.drawable.ic_menu_share, shareLabel, pendingIntent, false);
        }

        private PendingIntent createPendingIntent() {
            Intent actionIntent = new Intent(mContext.getApplicationContext(), ShareBroadcastReceiver.class);
            return PendingIntent.getBroadcast(mContext.getApplicationContext(), 0, actionIntent, 0);
        }

        /**
         * Combines all the options that have been set and returns a new {@link CustomTabsIntent}
         * object.
         */
        public AssistantIntent build() {
            if (mIsDefaultToolbarColor) {
                TypedValue outValue = new TypedValue();
                mContext.getTheme().resolveAttribute(R.attr.colorPrimary, outValue, true);
                int color = outValue.data;
                if (color != 0) {
                    mBuilder.setToolbarColor(color);
                }
            }

            CustomTabsIntent customTabsIntent = mBuilder.build();
            customTabsIntent.intent.putExtra(CustomTabsIntent.EXTRA_ENABLE_URLBAR_HIDING, mEnableUrlBarHiding);
            CustomTabsHelper.addKeepAliveExtra(mContext, customTabsIntent.intent);
            return new AssistantIntent(customTabsIntent);
        }

    }

    private CustomTabsIntent mCustomTabsIntent;

    private AssistantIntent(CustomTabsIntent customTabsIntent) {
        mCustomTabsIntent = customTabsIntent;
    }

    void setPackage(String packageName) {
        mCustomTabsIntent.intent.setPackage(packageName);
    }

    void launchUrl(Activity context, Uri uri) {
        mCustomTabsIntent.launchUrl(context, uri);
    }

}