/*
 * Copyright (c) 2017. @author ouyang copyright@fotoable Inc.  Anyone can copy this
 */

package com.fotoable.inputkeyboard.utils;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.fotoable.inputkeyboard.constants.MediaType;
import com.fotoable.inputkeyboard.constants.MessengerConstant;


public class AbstractSendGifUtils {
    protected static Intent createBasicSendIntent(@NonNull String packageName) {
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setPackage(packageName);
        return intent;
    }

    public static Intent createUniversalSendIntent(@NonNull String packageName, @NonNull Uri uri) {
        Intent intent = createBasicSendIntent(packageName);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType(ContentFormatUtils.getUriContentType(uri));
        intent.putExtra("android.intent.extra.STREAM", uri);
        return intent;
    }

    public static Intent createUniversalSendIntent(@NonNull Uri uri) {
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        String type = ContentFormatUtils.getUriContentType(uri);
        intent.setType(type);
        if (MediaType.CONTENT_TYPE_TEXT.equals(type)) {
            intent.putExtra("android.intent.extra.TEXT", uri.toString());
        } else {
            intent.putExtra("android.intent.extra.STREAM", uri);
        }
        return intent;
    }


    public static Intent createUniversalSendUrlIntent(@NonNull String packageName, @NonNull String url) {
        Intent intent = createBasicSendIntent(packageName);
        intent.setType("text/plain");
        intent.putExtra("android.intent.extra.TEXT", url);
        return intent;
    }


    public static Intent createFBMessengerSendIntent(@NonNull Uri uri, int fbProtocolVersion, @NonNull String fbApiKey, @NonNull String gifId) {
        Intent intent = createUniversalSendIntent("com.facebook.orca", uri);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra("com.facebook.orca.extra.PROTOCOL_VERSION", fbProtocolVersion);
        intent.putExtra("com.facebook.orca.extra.APPLICATION_ID", fbApiKey);
        intent.putExtra("com.facebook.orca.extra.METADATA", gifId);
        return intent;
    }

    public static Intent createFBMessengerSendUrlIntent(@NonNull String url, int fbProtocolVersion, @NonNull String fbApiKey, @NonNull String gifId) {
        Intent intent = createUniversalSendUrlIntent("com.facebook.orca", url);
        intent.putExtra("com.facebook.orca.extra.PROTOCOL_VERSION", fbProtocolVersion);
        intent.putExtra("com.facebook.orca.extra.APPLICATION_ID", fbApiKey);
        intent.putExtra("com.facebook.orca.extra.METADATA", gifId);
        return intent;
    }

    public static Intent createTwitterSendIntent(@NonNull Uri uri) {
        Intent intent = createUniversalSendIntent(MessengerConstant.TWITTER, uri);
        intent.setClassName(MessengerConstant.TWITTER, "com.twitter.android.composer.ComposerActivity");
        return intent;
    }

    public static Intent createTwitterSendUrlIntent(@NonNull String url) {
        Intent intent = createUniversalSendUrlIntent(MessengerConstant.TWITTER, url);
        intent.setClassName(MessengerConstant.TWITTER, "com.twitter.android.composer.ComposerActivity");
        return intent;
    }

//    public static Intent createFBMessengerSendIntent(@NonNull Uri uri, @NonNull MDFBMessenger mMDFBMessenger) {
//        return createFBMessengerSendIntent(uri, mMDFBMessenger.getFbProtocolVersion(), mMDFBMessenger.getFbApiKey(), mMDFBMessenger.getGifId());
//    }

//    public static KikVideoMessage createKikSendIntent(@NonNull Activity activity, @NonNull String applicationId, @NonNull MDKik mdKik) {
//        return createKikSendIntent(activity, applicationId, mdKik.getGifId(), mdKik.getMp4Url(), mdKik.getTinyGifPreviewUrl(), mdKik.isHasAudio());
//    }
//
//    public static KikVideoMessage createKikSendIntent(@NonNull Activity activity, @NonNull String applicationId, @NonNull String gifId, @NonNull String mp4Url, @NonNull String previewUrl, boolean hasAudio) {
//        boolean z = true;
//        KikContentProvider.init(applicationId);
//        KikVideoMessage message = new KikVideoMessage(activity, mp4Url, previewUrl);
//        message.setShouldAutoplay(true);
//        message.setShouldLoop(true);
//        if (hasAudio) {
//            z = false;
//        }
//        message.setShouldBeMuted(z);
//        message.addFallbackUrl("http://gif.co/download?pid=" + gifId, KikMessagePlatform.KIK_MESSAGE_PLATFORM_ANDROID);
//        message.addFallbackUrl("http://gif.co/download?pid=" + gifId, KikMessagePlatform.KIK_MESSAGE_PLATFORM_IPHONE);
//        return message;
//    }
}