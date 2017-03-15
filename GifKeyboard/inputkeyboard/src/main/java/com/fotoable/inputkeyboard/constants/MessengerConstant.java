/*
 * Copyright (c) 2017. @author ouyang copyright@fotoable Inc.  Anyone can copy this
 */

package com.fotoable.inputkeyboard.constants;

import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;

import com.fotoable.inputkeyboard.R;
import com.fotoable.inputkeyboard.model.IAppInfo;
import com.fotoable.inputkeyboard.model.MessageAppInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class MessengerConstant {
    public static final String AOSP_MESSAGES = "com.android.messaging";
    public static final String CHOMP = "com.p1.chompsms";
    public static final String EIGHT_SMS = "com.thinkleft.eightyeightsms.mms";
    public static final String FACEBOOK = "com.facebook.katana";
    public static final String FB_MESSENGER = "com.facebook.orca";
    public static final String GMAIL = "com.google.android.gm";
    public static final String GOOGLE_MESSENGER = "com.google.android.apps.messaging";
    public static final String GO_SMS = "com.jb.gosms";
    public static final String HANGOUTS = "com.google.android.talk";
    public static final String HIKE = "com.bsb.hike";
    public static final String HOVERCHAT = "com.ninja.sms.promo";
    public static final String HTC_MESSAGES = "com.htc.sense.mms";
    public static final String KAKAO_TALK = "com.kakao.talk";
    public static final String KIK = "kik.android";
    public static final String LINE = "jp.naver.line.android";
    public static final String MESSAGES = "com.android.mms";
    public static final String REDDIT = "com.reddit.frontpage";
    public static final String SKYPE = "com.skype.raider";
    public static final String SLACK = "com.Slack";
    public static final String TANGO = "com.sgiggle.production";
    public static final String TELEGRAM = "org.telegram.messenger";
    public static final String TWITTER = "com.twitter.android";
    public static final String VIBER = "com.viber.voip";
    public static final String VODAFONE = "com.vodafone.messaging";
    public static final String WE_CHAT = "com.tencent.mm";
    public static final String WHATSAPP = "com.whatsapp";
    public static final String NONE = "unknown";

    public static final List<IAppInfo> allAppInfos() {
        List<IAppInfo> appInfos = new ArrayList<>();
        for (int i = 0; i < ALL_KNOWN_MESSENGERS.size(); i++) {
            MessageAppInfo info = new MessageAppInfo(i, ALL_KNOWN_MESSENGERS.get(i), ALL_KNOWN_MESSENGERS.keyAt(i));
            appInfos.add(info);
        }
        return appInfos;
    }

    /**
     * 已知主流的发消息app的 包名和 app名
     */
    public static final ArrayMap<String, Integer> ALL_KNOWN_MESSENGERS = new ArrayMap<String, Integer>() {
        {
            put(NONE, Integer.valueOf(R.string.sdk_readable_app_name_none));
            put(FB_MESSENGER, Integer.valueOf(R.string.sdk_readable_app_name_facebook_messenger));
            put(WHATSAPP, Integer.valueOf(R.string.sdk_readable_app_name_whatsapp));
            put(MESSAGES, Integer.valueOf(R.string.sdk_readable_app_name_android_mms));
            put(HANGOUTS, Integer.valueOf(R.string.sdk_readable_app_name_hangouts));
            put(CHOMP, Integer.valueOf(R.string.sdk_readable_app_name_chomp_sms));
            put(SKYPE, Integer.valueOf(R.string.sdk_readable_app_name_skype));
            put(EIGHT_SMS, Integer.valueOf(R.string.sdk_readable_app_name_eight_sms));
            put(TWITTER, Integer.valueOf(R.string.sdk_readable_app_name_twitter));
            put(GO_SMS, Integer.valueOf(R.string.sdk_readable_app_name_go_sms_pro));
            put(TANGO, Integer.valueOf(R.string.sdk_readable_app_name_tango));
            put(KIK, Integer.valueOf(R.string.sdk_readable_app_name_kik));
            put(WE_CHAT, Integer.valueOf(R.string.sdk_readable_app_name_wechat));
            put(GOOGLE_MESSENGER, Integer.valueOf(R.string.sdk_readable_app_name_google_messenger));
            put(HIKE, Integer.valueOf(R.string.sdk_readable_app_name_hike));
            put(HTC_MESSAGES, Integer.valueOf(R.string.sdk_readable_app_name_htc_message));
            put(TELEGRAM, Integer.valueOf(R.string.sdk_readable_app_name_telegram));
            put(HOVERCHAT, Integer.valueOf(R.string.sdk_readable_app_name_hoverchat));
            put(VIBER, Integer.valueOf(R.string.sdk_readable_app_name_viber));
            put(KAKAO_TALK, Integer.valueOf(R.string.sdk_readable_app_name_kakaotalk));
            put(SLACK, Integer.valueOf(R.string.sdk_readable_app_name_slack));
            put(VODAFONE, Integer.valueOf(R.string.sdk_readable_app_name_vodafone_message_plus));
            put(FACEBOOK, Integer.valueOf(R.string.sdk_readable_app_name_facebook));
            put(WE_CHAT, Integer.valueOf(R.string.sdk_readable_app_name_wechat));
            put(LINE, Integer.valueOf(R.string.sdk_readable_app_name_line));
            put(REDDIT, Integer.valueOf(R.string.sdk_readable_app_name_reddit));
            put(GMAIL, Integer.valueOf(R.string.sdk_readable_app_name_gmail));
            put(AOSP_MESSAGES, Integer.valueOf(R.string.sdk_readable_app_name_aosp_message));
        }
    };

    /**
     * 已知主流的且支持一键分享的app
     */
    public static final Set<String> OTS_MESSENGERS = new HashSet<String>() {
        private static final long serialVersionUID = 3620144246244577268L;

        {
            add(FB_MESSENGER);
            add(WHATSAPP);
            add(MESSAGES);
            add(HANGOUTS);
            add(GOOGLE_MESSENGER);
            add(VIBER);
            add(KIK);
            add(GMAIL);
            add(WE_CHAT);
            add(AOSP_MESSAGES);
        }
    };

    public static boolean isOneTapSharingSupported(@NonNull String packageName) {
        return OTS_MESSENGERS.contains(packageName);
    }
}
