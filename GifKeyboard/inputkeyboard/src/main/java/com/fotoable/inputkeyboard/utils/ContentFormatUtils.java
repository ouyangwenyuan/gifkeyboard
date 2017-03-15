/*
 * Copyright (c) 2017. @author ouyang copyright@fotoable Inc.  Anyone can copy this
 */

package com.fotoable.inputkeyboard.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.fotoable.inputkeyboard.constants.MediaType;
import com.fotoable.inputkeyboard.constants.StringConstant;


public class ContentFormatUtils {
    @NonNull
    public static String getUrlContentType1(@NonNull String url) {
        if (TextUtils.isEmpty(url) && !url.contains(MediaType.MP4)) {
            return "image/gif";
        }
        return "video/mp4";
    }

    public static String getUriContentType(Uri uri) {
        String url = uri.getLastPathSegment();
        if (uri.getScheme().startsWith("http")) {
            return "text/plain";
        }
        if (url.endsWith(MediaType.GIF)) {
            return MediaType.CONTENT_TYPE_GIF;
        } else if (url.endsWith(MediaType.JPEG) || url.endsWith(MediaType.JPG)) {
            return MediaType.CONTENT_TYPE_JPG;
        } else if (url.endsWith(MediaType.MP4)) {
            return MediaType.CONTENT_TYPE_MP4;
        } else if (url.endsWith(MediaType.PNG)) {
            return MediaType.CONTENT_TYPE_PNG;
        }
        return "image/*";
    }

    public static boolean isMP4(@NonNull String url) {
        return !TextUtils.isEmpty(url) && "video/mp4".equals(getUriContentType(Uri.parse(url)));
    }

    public static boolean isGif(@NonNull String url) {
        return !TextUtils.isEmpty(url) && "image/gif".equals(getUriContentType(Uri.parse(url)));
    }

    private boolean isGif(@NonNull Context context, @NonNull Uri uri) {
        int dotPosition;
        String filePath = "";
        if ("file".equals(uri.getScheme())) {
            filePath = uri.getLastPathSegment();
        } else {
            String[] filePathColumn = new String[]{"_data"};
            Cursor cursor = context.getContentResolver().query(uri, filePathColumn, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(cursor.getColumnIndex(filePathColumn[0]));
                }
                cursor.close();
            }
        }
        if (TextUtils.isEmpty(filePath)) {
            dotPosition = -1;
        } else {
            dotPosition = filePath.lastIndexOf(StringConstant.DOT);
        }
        return ".gif".equals(dotPosition > -1 ? filePath.substring(dotPosition) : "");
    }

    public static String getExtension(boolean hasAudio) {
        return hasAudio ? MediaType.MP4 : MediaType.GIF;
    }
}