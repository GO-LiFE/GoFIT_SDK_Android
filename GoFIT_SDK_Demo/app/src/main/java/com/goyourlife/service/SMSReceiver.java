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

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsMessage;

import com.golife.contract.AppContract;

public class SMSReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (NotificationListenerService.getContext() != null) {
            final Bundle bundle = intent.getExtras();

            if (bundle != null) {
                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (int i = 0; i < pdusObj.length; i++) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String message = currentMessage.getDisplayMessageBody();
                    if (message == null)
                        message = "";
                    String name = getContractName(NotificationListenerService.getContext(), currentMessage.getDisplayOriginatingAddress());
                    String sendmessage = (message.length() > 0) ? name + ":" + message : name;

                    NotificationListenerService.doSendIncomingEventToDevice(AppContract.emIncomingMessageType.SMS, sendmessage, "Notification_SMS");
                }
            }
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
