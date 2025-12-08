package com.tfb.cbit.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.tfb.cbit.BuildConfig;
import com.tfb.cbit.R;
import com.tfb.cbit.activities.GameResultActivity;
import com.tfb.cbit.activities.PrivateContestDetailsActivity;
import com.tfb.cbit.activities.SplashActivity;
import com.tfb.cbit.utility.SessionUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;


public class FireMsgService extends FirebaseMessagingService {
    private static final String TAG = FireMsgService.class.getName();

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d(TAG, "Token " + s);
        if (!s.isEmpty()) {
            SessionUtil sessionUtil = new SessionUtil(getApplicationContext());
            sessionUtil.setFCMToken(s);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.i(TAG, "Message received [" + remoteMessage + "]");
            if (remoteMessage != null) {
            // Create Notification
            int notificationID = (int) System.currentTimeMillis();

                Log.d(TAG, "remoteMessage: "+remoteMessage.getData().toString());
            PendingIntent pendingIntent = null;
            Map<String, String> map = remoteMessage.getData();
            JSONObject json = null;
            if (remoteMessage.getData().size() > 0) {
                try {
                    json = new JSONObject(map);
                    Log.d(TAG, "onMessageReceivedjson: "+json.toString());
                    String contestId = "", contestName = "", msg = "", type = "";
                    if (json.has("contestId")) {
                        contestId = json.getString("contestId");
                    }

                    if (json.has("contestName")) {
                        contestName = json.getString("contestName");
                    }

                    if (json.has("msg")) {
                        msg = json.getString("msg");
                    }

                    if (json.has("type")) {
                        type = json.getString("type");
                    }

                    if (type.equals("private")) {
                        Intent intent = new Intent(this, PrivateContestDetailsActivity.class);
                        intent.putExtra(PrivateContestDetailsActivity.CONTESTID, contestId);
                        intent.putExtra(PrivateContestDetailsActivity.CONTESTNAME, contestName);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        pendingIntent = PendingIntent.getActivity(this, notificationID,
                                intent, PendingIntent.FLAG_ONE_SHOT);
                    } else if (type.equals("history")) {
                        Intent intent = new Intent(this, GameResultActivity.class);
                        intent.putExtra(GameResultActivity.CONTEST_ID, contestId);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        pendingIntent = PendingIntent.getActivity(this, notificationID,
                                intent, PendingIntent.FLAG_ONE_SHOT);
                    } else {
                        Intent intent = new Intent(this, SplashActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        pendingIntent = PendingIntent.getActivity(this, notificationID,
                                intent, PendingIntent.FLAG_ONE_SHOT);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Exception: " + e.getMessage());
                }
            } else {
                Intent intent = new Intent(this, SplashActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                pendingIntent = PendingIntent.getActivity(this, notificationID,
                        intent, PendingIntent.FLAG_ONE_SHOT);
            }

            Bitmap icon = BitmapFactory.decodeResource(getResources(),
                    R.mipmap.app_green_icon);

            String CHANNEL_ID = this.getApplicationContext().getPackageName();
            CharSequence name = this.getString(R.string.app_name);
            String Description = "This is CBit channel";

            final int NOTIFICATION_COLOR = getResources().getColor(R.color.colorPrimary);

            final Uri NOTIFICATION_SOUND_URI = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + BuildConfig.APPLICATION_ID + "/" + R.raw.alarm);
//            Uri soundUri = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.alarm_clock);

            final long[] VIBRATE_PATTERN = {100, 200, 300, 400, 500, 400, 300, 200, 400};

            NotificationCompat.Builder notificationBuilder = new
                    NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.notification_briefcase)
                    .setColor(NOTIFICATION_COLOR)
                    .setLargeIcon(icon)
                    .setAutoCancel(true)
                    .setVibrate(VIBRATE_PATTERN);
                Log.d(TAG, "onMessageReceived>>>>>> "+"callll");

            if (json != null) {

                try {
                    notificationBuilder.setContentTitle(this.getString(R.string.app_name));
                    if (json.has("msg"))
                    notificationBuilder.setContentText(json.getString("msg"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            NotificationManager notificationManager =
                    (NotificationManager)
                            getSystemService(Context.NOTIFICATION_SERVICE);

            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;


                // Changing Default mode of notification
                notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
                // Creating an Audio Attribute
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build();


                NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                mChannel.setDescription(Description);
                mChannel.enableLights(true);
                mChannel.setLightColor(Color.RED);
                mChannel.enableVibration(true);
//                if (soundUri != null)
//                    mChannel.setSound(soundUri, audioAttributes);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                mChannel.setShowBadge(false);
                assert notificationManager != null;
                notificationManager.createNotificationChannel(mChannel);
            }

            if (pendingIntent != null)
                notificationBuilder.setContentIntent(pendingIntent);


            assert notificationManager != null;
            notificationManager.notify(0, notificationBuilder.build());
        }
    }
}
