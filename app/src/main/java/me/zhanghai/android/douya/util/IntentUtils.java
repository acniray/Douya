/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

public class IntentUtils {

    private static final String ACTION_INSTALL_SHORTCUT =
            "com.android.launcher.action.INSTALL_SHORTCUT";

    private static final String MIME_TYPE_TEXT_PLAIN = "text/plain";
    private static final String MIME_TYPE_IMAGE_ANY = "image/*";
    private static final String MIME_TYPE_ANY = "*/*";

    private IntentUtils() {}

    public static Intent withChooser(Intent intent) {
        return Intent.createChooser(intent, null);
    }

    public static Intent makeInstallShortcut(int iconRes, int nameRes, Class<?> intentClass,
                                             Context context) {
        return new Intent()
                .setAction(ACTION_INSTALL_SHORTCUT)
                .putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(context.getApplicationContext(),
                        intentClass))
                .putExtra(Intent.EXTRA_SHORTCUT_NAME, context.getString(nameRes))
                .putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                        Intent.ShortcutIconResource.fromContext(context, iconRes));
    }

    public static Intent makeLaunchApp(String packageName, Context context) {
        return context.getPackageManager().getLaunchIntentForPackage(packageName);
    }

    private static Intent makePickFile(String mimeType, String[] mimeTypes, boolean allowMultiple) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT)
                .addCategory(Intent.CATEGORY_OPENABLE)
                .setType(mimeType);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (mimeTypes != null && mimeTypes.length > 0) {
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            if (allowMultiple) {
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            }
        }
        return intent;
    }

    public static Intent makePickFile(boolean allowMultiple) {
        return makePickFile(MIME_TYPE_ANY, null, allowMultiple);
    }

    public static Intent makePickFile(String mimeType, boolean allowMultiple) {
        return makePickFile(mimeType, new String[] { mimeType }, allowMultiple);
    }

    public static Intent makePickFile(String[] mimeTypes, boolean allowMultiple) {
        String mimeType = mimeTypes != null && mimeTypes.length == 1 ? mimeTypes[0] : MIME_TYPE_ANY;
        return makePickFile(mimeType, mimeTypes, allowMultiple);
    }

    // TODO: Use android.support.v4.app.ShareCompat ?

    // NOTE: Before Build.VERSION_CODES.JELLY_BEAN htmlText will be no-op.
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static Intent makeSendText(CharSequence text, String htmlText) {
        Intent intent = new Intent()
                .setAction(Intent.ACTION_SEND)
                .putExtra(Intent.EXTRA_TEXT, text);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && htmlText != null) {
            intent.putExtra(Intent.EXTRA_HTML_TEXT, htmlText);
        }
        return intent.setType(MIME_TYPE_TEXT_PLAIN);
    }

    public static Intent makeSendText(CharSequence text) {
        return makeSendText(text, null);
    }

    public static Intent makeSendImage(Uri uri, CharSequence text) {
        return new Intent()
                .setAction(Intent.ACTION_SEND)
                // For maximum compatibility.
                .putExtra(Intent.EXTRA_TEXT, text)
                .putExtra(Intent.EXTRA_TITLE, text)
                .putExtra(Intent.EXTRA_SUBJECT, text)
                // HACK: WeChat moments respects this extra only.
                .putExtra("Kdescription", text)
                .putExtra(Intent.EXTRA_STREAM, uri)
                .setType(MIME_TYPE_IMAGE_ANY);
    }

    public static Intent makeSendImage(Uri uri) {
        return makeSendImage(uri, null);
    }

    public static Intent makeSyncSettings(String[] authorities) {
        Intent intent = new Intent(Settings.ACTION_SYNC_SETTINGS);
        if (!ArrayUtils.isEmpty(authorities)) {
            intent.putExtra(Settings.EXTRA_AUTHORITIES, authorities);
        }
        return intent;
    }

    public static Intent makeSyncSettings(String authority) {
        return makeSyncSettings(authority != null ? new String[] { authority } : null);
    }

    public static Intent makeSyncSettings() {
        return makeSyncSettings((String) null);
    }

    public static Intent makeView(Uri uri) {
        return new Intent(Intent.ACTION_VIEW, uri);
    }

    public static Intent makeViewAppInMarket(String packageName) {
        return makeView(Uri.parse("market://details?id=" + packageName));
    }
}
