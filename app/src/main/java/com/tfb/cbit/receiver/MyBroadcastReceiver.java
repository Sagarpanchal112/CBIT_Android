package com.tfb.cbit.receiver;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.os.Vibrator;
import android.provider.Settings;
import androidx.core.app.NotificationCompat;

import com.tfb.cbit.R;
import com.tfb.cbit.activities.GameViewActivity;
import com.tfb.cbit.models.dbmodel.UpcomingContestModel;

import java.util.Date;
import java.util.Objects;

import static android.content.Context.POWER_SERVICE;


public class MyBroadcastReceiver extends BroadcastReceiver
{
    private static final String BUNDLE_EXTRA = "bundle_extra";
    private static final String ALARM_KEY = "alarm_key";
    private PowerManager.WakeLock screenWakeLock;
    MediaPlayer mp;
    @SuppressLint("InvalidWakeLockTag")
    @Override
    public void onReceive(Context context, Intent intent) {

        mp= MediaPlayer.create(context, R.raw.alarm);
        mp.start();
       // Toast.makeText(context, "Alarm....", Toast.LENGTH_LONG).show();
        PowerManager pm = (PowerManager) context.getSystemService(POWER_SERVICE);
        if (screenWakeLock == null)
        {
            screenWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
            screenWakeLock.acquire();

            KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("TAG");
            keyguardLock.disableKeyguard();

            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            long[] s = { 0, 100, 10, 500, 10, 100, 0, 500, 10, 100, 10, 500 };
            vibrator.vibrate(s, -1);
        }


        // If android 10 or higher
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P)
//        {
//            Toast.makeText(context, "Set Notification", Toast.LENGTH_LONG).show();
            sendNotification(context,intent);
//        }
//        else
//        {
//            Toast.makeText(context, "Open App", Toast.LENGTH_LONG).show();
//            // If lower than Android 10, we use the normal method ever.
//            openApp(context,intent);
//        }
   }

    @SuppressLint("BatteryLife")
    public void openApp(Context context, Intent intent2)
    {
        Intent intent = new Intent(context, GameViewActivity.class);
        UpcomingContestModel upcomingContestModel = intent2.getBundleExtra(BUNDLE_EXTRA).getParcelable(ALARM_KEY);
        PowerManager pm = (PowerManager) context.getSystemService(POWER_SERVICE);
        String packageName = context.getPackageName();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (pm.isIgnoringBatteryOptimizations(packageName))
                intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
            else {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
            }
        }
        if (upcomingContestModel != null) {
            intent.putExtra(GameViewActivity.CONTESTID, upcomingContestModel.getContestID());
            intent.putExtra(GameViewActivity.CONTESTTITLE, upcomingContestModel.getContestName());
            intent.putExtra(GameViewActivity.CONTESTTYPE, upcomingContestModel.getContestType());
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (mp !=null)
            mp.stop();

        if (screenWakeLock != null)
            screenWakeLock.release();

        context.startActivity(intent);
    }

    @SuppressLint("ObsoleteSdkInt")
    public void sendNotification(Context context, Intent intent) {

        int NOTIFICATION_ID = (int) new Date().getTime();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String CHANNEL_ID = "cbit_channel_01";
        CharSequence name = "cbit_channel";
        String description = "This is cbit channel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
                channel.setDescription(description);
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                Objects.requireNonNull(notificationManager).createNotificationChannel(channel);
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.app_green_icon)
                .setContentTitle("CBit")
                .setContentText("Your contest will start in 1 minutes");

        Intent resultIntent = new Intent(context, GameViewActivity.class);

        UpcomingContestModel upcomingContestModel = intent.getBundleExtra(BUNDLE_EXTRA).getParcelable(ALARM_KEY);
        if (upcomingContestModel != null) {
            resultIntent.putExtra(GameViewActivity.CONTESTID, upcomingContestModel.getContestID());
            resultIntent.putExtra(GameViewActivity.CONTESTTITLE, upcomingContestModel.getContestName());
            resultIntent.putExtra(GameViewActivity.CONTESTTYPE, upcomingContestModel.getContestType());
            resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        TaskStackBuilder stackBuilder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(GameViewActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(resultPendingIntent);
        }
        Objects.requireNonNull(notificationManager).notify(NOTIFICATION_ID, builder.build());
    }
}
