/*
 * Copyright (c) 2017. @author ouyang copyright@fotoable Inc.  Anyone can copy this
 */

package com.fotoable.inputkeyboard.constants;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.net.URLDecoder;
import java.net.URLEncoder;

public abstract class StringConstant {
    public static final String AT = "@";
    public static final String COLON = ":";
    public static final String COMMA = ",";
    public static final String DASH = "-";
    public static final String DOT = ".";
    public static final String EMPTY = "";
    public static final String HASH = "#";
    public static final String NEW_LINE = "\n";
    public static final String SLASH = "/";
    public static final String SPACE = " ";
    public static final String UNKNOWN = "UNKNOWN";
    public static final String UTF8 = "UTF-8";

    @NonNull
    public static String encode(@NonNull String string) {
        return encode(string, "UTF-8");
    }

    @NonNull
    public static String encode(@NonNull String string, @Nullable String format) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        try {
            if (TextUtils.isEmpty(format)) {
                format = "UTF-8";
            }
            return URLEncoder.encode(string, format);
        } catch (Throwable th) {
            return string;
        }
    }

    @NonNull
    public static String decode(@NonNull String string) {
        return decode(string, "UTF-8");
    }

    @NonNull
    public static String decode(@NonNull String string, @Nullable String format) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        try {
            if (TextUtils.isEmpty(format)) {
                format = "UTF-8";
            }
            return URLDecoder.decode(string, format);
        } catch (Throwable th) {
            return string;
        }
    }
}
