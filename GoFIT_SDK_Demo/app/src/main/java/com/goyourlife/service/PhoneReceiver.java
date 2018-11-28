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

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.golife.contract.AppContract;

public class PhoneReceiver extends PhoneStateListener {

    private boolean mRinged = false;

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        super.onCallStateChanged(state, incomingNumber);

        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE:
            case TelephonyManager.CALL_STATE_OFFHOOK:
                if (mRinged) {
                    if (NotificationListenerService.getContext() != null) {
                        mRinged = false;
                        NotificationListenerService.doSendIncomingEventToDevice(AppContract.emIncomingMessageType.phoneOff, null, "Notification_Phone");
                    }
                }
                break;

            case TelephonyManager.CALL_STATE_RINGING:
                try {
                    if (NotificationListenerService.getContext() != null) {
                        mRinged = true;
                        String contractName = getContractName(NotificationListenerService.getContext(), incomingNumber);
                        NotificationListenerService.doSendIncomingEventToDevice(AppContract.emIncomingMessageType.phoneOn,
                                contractName.equals(incomingNumber) ? contractName : contractName + ":" + incomingNumber,
                                "Notification_Phone");
                    }
                } catch (NullPointerException e) {
                    if (NotificationListenerService.getContext() != null) {
                        mRinged = true;
                        NotificationListenerService.doSendIncomingEventToDevice(AppContract.emIncomingMessageType.phoneOn,
                                "No Caller ID",
                                "Notification_Phone");
                    }
                }
                break;
        }
    }

    private String getContractName(Context context, String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() == 0) {
            return "No Caller ID";
        } else {
            if (context != null) {
                try {
                    String[] projection = new String[]{ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup.LOOKUP_KEY};
                    Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));

                    ContentResolver contentResolver = context.getContentResolver();
                    if (contentResolver != null) {
                        Cursor cursor = contentResolver.query(uri, projection, ContactsContract.PhoneLookup.NUMBER + " = ?", new String[]{phoneNumber}, null);
                        if (cursor != null) {
                            if (cursor.getCount() > 0) {
                                cursor.moveToFirst();
                                phoneNumber = cursor.getString(1);
                            }
                            cursor.close();
                        }
                    }

                    return phoneNumber;
                } catch (Exception e) {
                    return phoneNumber;
                }
            } else {
                return phoneNumber;
            }
        }
    }
}
