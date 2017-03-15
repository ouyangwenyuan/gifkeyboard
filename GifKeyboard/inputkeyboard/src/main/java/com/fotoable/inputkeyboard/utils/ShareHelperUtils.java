/*
 * Copyright (c) 2017. @author ouyang copyright@fotoable Inc.  Anyone can copy this
 */

package com.fotoable.inputkeyboard.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.fotoable.inputkeyboard.R;
import com.fotoable.inputkeyboard.constants.MessengerConstant;
import com.fotoable.inputkeyboard.constants.StringConstant;
import com.fotoable.inputkeyboard.model.IAppInfo;
import com.fotoable.inputkeyboard.model.ImediaInfo;
import com.fotoable.inputkeyboard.model.Result;

import java.io.File;
import java.util.Collection;

import static android.content.ContentValues.TAG;

/**
 * @author ouyangwenyuan
 *         分享到 facebook， whatsapp， twitter，email， message，fb messager，instagram， ablum  and so on.
 */
public abstract class ShareHelperUtils {
    public static void shareContentWithChooser(@NonNull Context activity, @NonNull Uri uri) {
        Intent intent = null;
        if (uri.getScheme().startsWith("file")) {
            uri = getImageContentUri(activity, uri.getPath());
            MyLog.i("scheme =" + uri);
        }
        activity.startActivity(Intent.createChooser(AbstractSendGifUtils.createUniversalSendIntent(uri), activity.getString(R.string.chooser_message_send_gif)));
    }

    private static final String REDIRECT_WEBSITE_URL = "https://www.tenor.co/";

    /**
     * if you can not open the app form package name ,then jump to google play to download app.
     *
     * @param context
     * @param packageName
     */
    public static void openPlayStoreWithPackage(Context context, String packageName) {
        try {
            context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + packageName)));
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
        }
    }

    /**
     * 分享到某个app中
     *
     * @param activity
     * @param appInfo
     * @param mediaInfo
     */
    public static void shareToApp(Activity activity, IAppInfo appInfo, ImediaInfo mediaInfo) {
//        1、检查url类型，确定app是否支持此类分享类型
//        2、检查app 包名，检查是否已安装
//        3、检查分享的页面是否存在，当前版本是否支持分享
//        4、启动分享页面或者直接分享
//        5、分享失败处理
    }

    public static void shareToApp(Context activity, String packageName, String name, String url) {
        Uri uri = Uri.parse(url);
        MyLog.i("share to " + name + ",url =" + url + ",uri= " + uri);
        Intent intent = null;
        try {
//            if (MessengerConstant.FB_MESSENGER.equals(packageName) || MessengerConstant.FACEBOOK.equals(packageName)) {
//                intent = AbstractSendGifUtils.createUniversalSendUrlIntent(packageName, url);
//            } else {
//                Uri.parse("file:///storage/emulated/0/pictures/sentgifs/gif_for_messenger_riffsy/4862354.gif");//
//                uri = getUriforPath(activity, "/storage/emulated/0/pictures/sentgifs/gif_for_messenger_riffsy/4862354.gif");
            if (uri.getScheme().startsWith("content")) {
                intent = AbstractSendGifUtils.createUniversalSendIntent(packageName, uri);
            } else if (uri.getScheme().startsWith("http")) {
                intent = AbstractSendGifUtils.createUniversalSendUrlIntent(packageName, url);
            } else if (uri.getScheme().startsWith("file")) {
                uri = getImageContentUri(activity, uri.getPath());
                MyLog.i("scheme =" + uri);
                intent = AbstractSendGifUtils.createUniversalSendIntent(packageName, uri);
            } else {
                MyLog.i("scheme =" + uri.getScheme());
                intent = AbstractSendGifUtils.createUniversalSendUrlIntent(packageName, url);
            }
//            }
            shareThroughIntent(activity, packageName, intent);
        } catch (Exception e) {
            e.printStackTrace();
            intent = AbstractSendGifUtils.createUniversalSendUrlIntent(packageName, url);
            shareThroughIntent(activity, packageName, intent);
        }


//        if (MessengerConstant.TWITTER.equals(packageName)) {
//            shareWithTwitter(activity, new Intent().setPackage(packageName));
//        } else if (MessengerConstant.WHATSAPP.equals(packageName)) {
//            shareWithWhatsApp(activity, uri);
//        } else if (MessengerConstant.FB_MESSENGER.equals(packageName)) {
//            shareWithMessenger(activity, uri, packageName);
//        } else if (MessengerConstant.FACEBOOK.equals(packageName)) {
//            shareGifWithFacebook(activity, uri, uri);
//        } else {
//            shareLinkWith(activity, url, packageName);
//        }

    }

    /**
     * open a search list use the tag.
     */
//    public static void openSearchActivity(Activity activity, String tag, boolean newSearchSession) {
//        tag = tag.replaceAll(StringConstant.HASH, "");
//
//        Intent intent = new Intent(activity, NewSearchActivity.class);
//        intent.putExtra(NewSearchActivity.KEY_QUERY, tag);
//        activity.startActivityForResult(intent, ExpandedSearchViewActivity.REQUEST_TO_SEARCH_ACTIVITY);
//    }

//    public static void openCollectionChooser(@NonNull Activity activity, @Nullable Result result) {
//        if (activity != null) {
//            Intent intent = new Intent(activity, SelectCollectionActivity.class);
//            intent.putExtra(SelectCollectionActivity.EXTRA_TAG_STRING, AbstractStringUtils.join(result.getTags(), StringConstant.COMMA));
//            intent.putExtra(SelectCollectionActivity.EXTRA_GIF_ID, result.getId());
//            activity.startActivity(intent);
//        }
//    }


//    public static void openCollection(Activity activity, String collectionName, String collectionDisplayName) {
//        if (collectionName != null) {
//            Intent intent = new Intent(activity, NewCollectionActivity.class);
//            intent.putExtra(NewCollectionActivity.COLLECTION_NAME, collectionName);
//            intent.putExtra(NewCollectionActivity.COLLECTION_DISPLAY_NAME, collectionDisplayName);
//            activity.startActivity(intent);
//            activity.overridePendingTransition(R.anim.search_view_fade_in, R.anim.search_view_fade_out);
//        }
//    }
    public static boolean shareWithMessenger(@NonNull Activity activity, @NonNull Uri uri, @NonNull String metadata) {
        if (activity == null || uri == null) {
            return false;
        }
//        if (!AbstractPermissionUtils.hasWriteExternalStoragePermission(activity)) {
//            activity.startActivity(new Intent(activity, PermissionsActivity.class));
//        }
        Intent intent = AbstractSendGifUtils.createFBMessengerSendIntent(uri, 20150314, "f73680e85183df2ff7467a9b14570bd9cbfb329d", metadata);
        if (AbstractListUtils.isEmpty(activity.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY))) {
            showInstallDialog(activity, (int) R.string.app_not_installed, (int) R.string.no_app_installed, "com.facebook.orca");
            return false;
        }
        activity.startActivityForResult(intent, 1);
        return true;
    }

    public static boolean shareWithWhatsApp(Activity activity, Uri uri) {
        if (activity == null || uri == null) {
            return false;
        }
        String packageName = MessengerConstant.WHATSAPP;
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setFlags(1);
        intent.setType("video/*");
        intent.setPackage(packageName);
        intent.putExtra("android.intent.extra.STREAM", uri);
        return shareThroughIntent(activity, packageName, intent);
    }

    public static void shareWithKik(Activity activity, String mSharedMediaUrl, String previewUrl, boolean hasAudio, String id) {
        boolean z = true;
//        KikVideoMessage message = new KikVideoMessage(activity, mSharedMediaUrl, previewUrl);
//        message.setShouldAutoplay(true);
//        message.setShouldLoop(true);
//        if (hasAudio) {
//            z = false;
//        }
//        message.setShouldBeMuted(z);
//        message.addFallbackUrl("http://gif.co/download?pid=" + id, KikMessagePlatform.KIK_MESSAGE_PLATFORM_ANDROID);
//        message.addFallbackUrl("http://gif.co/download?pid=" + id, KikMessagePlatform.KIK_MESSAGE_PLATFORM_IPHONE);
//        KikClient.getInstance().sendKikMessage(activity, message);
    }

    public static void shareLinkWith(@Nullable Activity activity, @Nullable String url, @Nullable String packageName) {
        if (activity != null && !TextUtils.isEmpty(url) && !TextUtils.isEmpty(packageName)) {
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setFlags(1);
            intent.setType("text/plain");
            intent.setPackage(packageName);
            intent.putExtra("android.intent.extra.TEXT", url);
            shareThroughIntent(activity, packageName, intent);
        }
    }

    public static boolean shareThroughIntent(@Nullable Context activity, @Nullable String packageName, @Nullable Intent intent) {
        if (activity == null || TextUtils.isEmpty(packageName) || intent == null) {
            return false;
        }
        if (AbstractListUtils.isEmpty(activity.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY))) {
            return showInstallDialog(activity, R.string.app_not_installed, R.string.no_app_installed, MessengerConstant.NONE);
        }
        activity.startActivity(intent);
        return true;
    }


    public static void shareGifWithFacebook(Activity activity, Uri contentUrl, Uri previewUrl) {
//        if (SessionUtils.getInstalledPackages().contains(MessengerConstant.FACEBOOK)) {
//            ShareLinkContent content = ((Builder) new Builder().setContentUrl(contentUrl)).setImageUrl(previewUrl).build();
//            ShareDialog dialog = new ShareDialog(activity);
//            dialog.show(content);
//            return;
//        }
        showInstallDialog(activity, R.string.app_not_installed, R.string.no_app_installed, MessengerConstant.FACEBOOK);
    }


    public static void shareWithTwitter(Activity activity, Intent sendIntent) {
        shareThroughIntent(activity, MessengerConstant.TWITTER, sendIntent);
    }

    public static boolean shareLinkWithReddit(Activity activity, String url) {
        if (activity == null || url == null) {
            return false;
        }
        String packageName = MessengerConstant.REDDIT;
        Intent intent = activity.getPackageManager().getLaunchIntentForPackage(packageName);
        if (intent != null) {
            return shareThroughIntent(activity, packageName, intent);
        }
        return showInstallDialog(activity, R.string.app_not_installed, R.string.no_app_installed, MessengerConstant.TWITTER);
    }

    private static boolean showInstallDialog(@Nullable final Context activity, @Nullable String title, @Nullable String content, @Nullable final String packageName) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle((CharSequence) title);
        }
        if (!TextUtils.isEmpty(content)) {
            builder.setMessage((CharSequence) content);
        }
        builder.setPositiveButton(R.string.install_now, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openPlayStoreWithPackage(activity, packageName);
            }
        });
        builder.setNegativeButton(R.string.cancel, null);
        builder.create().show();
        return true;
    }

    private static boolean showInstallDialog(@Nullable Context activity, @StringRes int titleResId, @StringRes int contentResId, @Nullable String packageName) {
        return showInstallDialog(activity, activity.getString(titleResId), activity.getString(contentResId), packageName);
    }

    public static void showRemoveCollectionDialog(@Nullable Activity activity, @Nullable final Collection collection) {
//        if (activity != null && !AbstractUIUtils.isActivityDestroyed(activity) && collection != null) {
//            new TenorMaterialDialog.Builder(activity).title(activity.getString(R.string.remove_collection)).content(activity.getString(R.string.remove_collection_named, new Object[]{collection.getName()})).positiveText((int) R.string.remove).negativeText((int) R.string.cancel).onPositive(new SingleButtonCallback() {
//                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                    String collectionName = collection.getName();
//                    if (TextUtils.isEmpty(RiffsyEventTracker.getInstance().getUserToken())) {
//                        DatabaseHelper.removeCollection(collection, false);
//                        BusManager.getBus().post(new UpdateCollectionEvent(collectionName, 204));
//                        return;
//                    }
//                    BusManager.getBus().post(new UpdateCollectionEvent(collectionName, UpdateCollectionEvent.TYPE_REMOVE_BACKEND));
//                }
//            }).autoDismiss(true).build().show();
//        }
    }

    private static void redirectToUrl(@Nullable Activity activity, @Nullable String url) {
        if (activity != null && !TextUtils.isEmpty(url)) {
            try {
                Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(url));
                browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(browserIntent);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(activity, "can not open", Toast.LENGTH_LONG).show();
            }
        }
    }

    public static void redirectToWebsiteUrl(@Nullable Activity activity) {
        redirectToUrl(activity, REDIRECT_WEBSITE_URL);
    }

    public static void redirectToWebsiteUrl(@Nullable Activity activity, @Nullable String searchTag) {
        StringBuilder tenorUrl = new StringBuilder(REDIRECT_WEBSITE_URL);
        if (!TextUtils.isEmpty(searchTag)) {
            tenorUrl.append("search/");
            tenorUrl.append(searchTag.trim().replace("\\s+", StringConstant.DASH));
            tenorUrl.append("-gifs");
        }
        redirectToUrl(activity, tenorUrl.toString());
    }

    public static void redirectToWebsiteUrl(@Nullable Activity activity, @Nullable Result result) {
        String id = result != null ? result.getId() : "";
        StringBuilder tenorUrl = new StringBuilder(REDIRECT_WEBSITE_URL);
        if (!TextUtils.isEmpty(id)) {
            tenorUrl.append("view/");
            tenorUrl.append(id);
        }
        redirectToUrl(activity, tenorUrl.toString());
    }

    public static void openUrlInBrowser(Context context, String url) {
        Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(url));
        browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(browserIntent);
    }

    public static void redirectToApp(Fragment fragment, String packageName) {
        if (fragment.isAdded()) {
            redirectToApp(fragment.getActivity(), packageName);
        }
    }

    public static boolean redirectToApp(Activity activity, String packageName, String message) {
        try {
            PackageManager packageManager = activity.getPackageManager();
            Intent launchIntent = packageManager.getLaunchIntentForPackage(packageName);
            if (!AbstractListUtils.isEmpty(packageManager.queryIntentActivities(launchIntent, PackageManager.MATCH_DEFAULT_ONLY))) {
                activity.startActivityForResult(launchIntent, 1);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
        }
        return false;
    }


    public static void redirectToApp(Activity activity, String packageName) {
        redirectToApp(activity, packageName, "app is not install");
    }

    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    public static Uri getUriforPath(Context context, String path) {
//        String type = Utils.ensureNotNull(intent.getType());
//        Log.d(TAG, "uri is " + uri);
//        if (uri.getScheme().equals("file") && (type.contains("image/"))) {
//            String path = uri.getEncodedPath();
//        if (context.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != PERMISSION_GRANTED) {
//            context.grantUriPermission(context.getPackageName(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI, Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        }
        Log.d(TAG, "path1 is " + path);
        if (path != null) {
            path = Uri.decode(path);
            Log.d(TAG, "path2 is " + path);
            ContentResolver cr = context.getContentResolver();
            StringBuffer buff = new StringBuffer();

            buff.append("(")
                    .append(MediaStore.Images.ImageColumns.DATA)
                    .append("=")
                    .append("'" + path + "'")
                    .append(")");
            Cursor cur = cr.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Images.ImageColumns._ID},
                    buff.toString(), null, null);
            int index = 0;
            for (cur.moveToFirst(); !cur.isAfterLast(); cur
                    .moveToNext()) {
                index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                // set _id value
                index = cur.getInt(index);
            }
            if (index == 0) {
                //do nothing
            } else {
                Uri uri_temp = Uri
                        .parse("content://media/external/images/media/"
                                + index);
                Log.d(TAG, "uri_temp is " + uri_temp);
                if (uri_temp != null) {
                    return uri_temp;
                }
            }
        }
        return null;
    }

    /**
     * Gets the content:// URI from the given corresponding path to a file
     * 绝对路径转uri
     *
     * @param context
     * @param filePath
     * @return content Uri
     */
    public static Uri getImageContentUri(Context context, String filePath) {
//        String filePath = imageFile.getAbsolutePath();
        Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (ContextCompat.checkSelfPermission(context, "android.permission.READ_EXTERNAL_STORAGE") != PERMISSION_GRANTED) {
//                ((Activity) context).requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"}, 0);
//            }
//            if (context.checkUriPermission(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, Process.myPid(), Process.myUid(), Intent.FLAG_GRANT_READ_URI_PERMISSION) != PERMISSION_GRANTED) {
//                context.grantUriPermission(context.getPackageName(), contentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            }
//        }
        Cursor cursor = context.getContentResolver().query(contentUri,
                new String[]{MediaStore.Images.Media._ID}, MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
//            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(contentUri, "" + id);
        } else {
            File imageFile = new File(filePath);
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }
}
