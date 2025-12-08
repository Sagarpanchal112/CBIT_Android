package com.tfb.cbit.notificationreciver;

import android.content.Context;
import android.content.Intent;
import android.util.Log;


import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenedResult;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by abc on 1/3/2018.
 */

public class NotificationOpenedHandler implements OneSignal.OSNotificationOpenedHandler {

    private static final String TAG = "NotificationReceivedHan";
    Context context;

    public NotificationOpenedHandler(Context context) {
        this.context = context;
    }



    @Override
    public void notificationOpened(OSNotificationOpenedResult osNotificationOpenedResult) {
        OSNotificationAction.ActionType actionType = osNotificationOpenedResult.getAction().getType();
        JSONObject data = null;
        try {
            data = osNotificationOpenedResult.toJSONObject().getJSONObject("notification").getJSONObject("payload").getJSONObject("additionalData");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String customKey;

//        Log.i("OneSignal", "==>" + result.stringify());

        if (data != null) {
            customKey = data.optString("customkey", null);
            if (customKey != null)
                Log.i("OneSignalExample", "customkey set with value: " + customKey);
        }

        if (actionType == OSNotificationAction.ActionType.ActionTaken)
            Log.i("OneSignalExample", "Button pressed with id: " + osNotificationOpenedResult.getAction().getActionId());
        Intent intent = null;
//        if (L.isLogin(context) && data.has(Constant.type1) && data.has(Constant.id)) {
//            try {
//                String type = data.getString(Constant.type1);
//                String id = data.getString(Constant.id);
//                if (type.equalsIgnoreCase(Constant.chat)) {
//                    intent = new Intent(context, GameViewActivity.class);
//                    intent.putExtra(Constant.userId, id);
//                    intent.putExtra(Constant.userimg, data.getString(Constant.userimg));
//                    intent.putExtra(Constant.username, data.getString(Constant.username));
//                } else if (type.equalsIgnoreCase(Constant.peep)) {
//                    intent = new Intent(context, PeepDetails.class);
//                    intent.putExtra(Constant.peepID, id);
//                    intent.putExtra(Constant.type, type);
//                } else if (type.equalsIgnoreCase(Constant.user)) {
//                    intent = new Intent(context, OtherUserProfile.class);
//                    intent.putExtra(Constant.userId, id);
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//        }
        if (intent != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
        // Add the following to your AndroidManifest.xml to prevent the launching of your main Activity
        //   if you are calling startActivity above.


    }
}
