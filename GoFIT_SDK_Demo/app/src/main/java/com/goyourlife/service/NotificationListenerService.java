/*
 * Copyright (C) GOYOURLIFE INC. - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Rik Tsai <rik.tsai@goyourlife.com>, July 2018.
 *
 * Project : GoFIT SDK (code name : GoFIT SDK)
 *
 * Test App for GoFIT SDK.
 *
 * @author Rik Tsai <rik.tsai@goyourlife.com>
 * @link http://www.goyourlife.com
 * @copyright Copyright &copy; 2018 GOYOURLIFE INC.
 */
package com.goyourlife.service;

import android.app.Notification;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.widget.RemoteViews;

import com.golife.contract.AppContract;
import com.goyourlife.gofit_demo.MainActivity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Objects;

@SuppressWarnings("ResourceType")
public class NotificationListenerService extends android.service.notification.NotificationListenerService {

    private static Context mContext = null;
    private PhoneReceiver mPhoneReceiver = null;

    private AppContract.emIncomingMessageType lastType = null;
    private String lastMessage = "";
    private long lastPostTime = -1;

    private final String[] packageList = {
            "com.google.android.gm",
            "com.google.android.talk",
            "com.android.calendar",
            "com.google.android.calendar",
            "com.facebook.katana",
            "jp.naver.line.android",
            "com.whatsapp",
            "com.tencent.mobileqq",
            "com.tencent.mm",
            ""
    };

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = NotificationListenerService.this;
        mPhoneReceiver = new PhoneReceiver();
        ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).listen(mPhoneReceiver, PhoneStateListener.LISTEN_CALL_STATE);

        AppContract.notificationServiceContext = mContext;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mContext = null;
        ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).listen(mPhoneReceiver, PhoneStateListener.LISTEN_NONE);
        mPhoneReceiver = null;

        AppContract.notificationServiceContext = null;
    }

    public static Context getContext() {
        return mContext;
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        AppContract.emIncomingMessageType type = AppContract.emIncomingMessageType.Default;
        for (int i = 0; i < packageList.length; i++) {
            if (packageList[i].equals(sbn.getPackageName())) {
                type = AppContract.emIncomingMessageType.values()[i];
                break;
            }
        }

        String message = "";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            message = getNotificationSinceKITKAT(sbn.getNotification().extras);
            message = message.replace("：", ":");
        }

        if (message.length() == 0) {
            if (sbn.getNotification().tickerText != null) {
                message = sbn.getNotification().tickerText.toString();
                if (message.length() == 0) {
                    try {
                        RemoteViews rv = sbn.getNotification().tickerView;
                        for (Field field : rv.getClass().getDeclaredFields()) {
                            field.setAccessible(true);

                            if (field.getName().equals("mActions")) {
                                ArrayList<Objects> things = (ArrayList<Objects>) field.get(rv);
                                for (Objects object : things) {
                                    for (Field innerField : object.getClass().getDeclaredFields()) {
                                        if (innerField.getName().equals("value")) {
                                            Object innerObj = innerField.get(object);
                                            if (innerObj instanceof String || innerObj instanceof SpannableString) {
                                                if (message.length() > 0) {
                                                    message += ":";
                                                }

                                                message += innerField.get(object).toString();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                message = message.replace("：", ":");
            } else {
                message = type == AppContract.emIncomingMessageType.Default ? "New Message!" : getApplicationName(sbn.getPackageName());
            }
        }

        if (message == null) {
            message = "";
        }
        long postTime = sbn.getPostTime();
        if (!(type == lastType && message.equals(lastMessage) && (postTime - lastPostTime < 3000))) {
            doSendIncomingEventToDevice(type, message, sbn.getPackageName());
            lastType = type;
            lastMessage = message;
            lastPostTime = postTime;
        }
    }

    private String getNotificationSinceKITKAT(Bundle extras) {
        try {
            String retValue = "";
            CharSequence title = extras.getCharSequence(Notification.EXTRA_TITLE);
            if (title != null) {
                retValue += title.toString();
            }

            CharSequence text = extras.getCharSequence(Notification.EXTRA_TEXT);
            if (text != null) {
                if (!retValue.isEmpty()) {
                    retValue += ":";
                }
                retValue += text.toString();
            }

            CharSequence subText = extras.getCharSequence(Notification.EXTRA_SUB_TEXT);
            if (subText != null) {
                if (!retValue.isEmpty()) {
                    retValue += ":";
                }
                retValue += subText.toString();
            }

            return retValue;
        } catch (Exception e) {
            return "";
        }
    }

    private String getApplicationName(String packageName) {
        try {
            return getPackageManager().getApplicationLabel(getPackageManager().getApplicationInfo(packageName, 0)).toString() + " has New Message!";
        } catch (Exception e) {
            return "New Message!";
        }
    }

    public static void doSendIncomingEventToDevice(AppContract.emIncomingMessageType type, String message, String packageName) {
        if (message == null) {
            message = "";
        }


        if (MainActivity._goFITSdk != null) {
            MainActivity._goFITSdk.doSendIncomingMessage(type, message, packageName);
        }
    }
}
