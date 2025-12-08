package com.tfb.cbit.notificationreciver;

import android.content.Context;
import android.content.Intent;
import android.util.Log;


import com.onesignal.OSNotificationReceivedEvent;
import com.onesignal.OneSignal;

import org.json.JSONObject;

/**
 * Created by abc on 12/22/2017.
 */

public class NotificationReceivedHandler implements OneSignal.OSRemoteNotificationReceivedHandler {

    private static final String TAG = "NotificationReceivedHan";

    public NotificationReceivedHandler() {
    }




    @Override
    public void remoteNotificationReceived(Context context, OSNotificationReceivedEvent osNotificationReceivedEvent) {

    }
}
