package com.tfb.cbit.receiver;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.provider.Settings;
import androidx.legacy.content.WakefulBroadcastReceiver;

import com.tfb.cbit.R;
import com.tfb.cbit.activities.GameViewActivity;
import com.tfb.cbit.models.dbmodel.UpcomingContestModel;
import com.tfb.cbit.utility.PrintLog;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.Utils;

import static android.content.Context.POWER_SERVICE;

public class AlarmReceiver extends WakefulBroadcastReceiver {

    static final String ACTION_PROCESS_UPDATES =
            "com.tfb.cbit.receiver.action.PROCESS_UPDATES";

    private static final String TAG = "AlarmReceiver";
    MediaPlayer mp;
    private static final String BUNDLE_EXTRA = "bundle_extra";
    private static final String ALARM_KEY = "alarm_key";
    private PowerManager.WakeLock screenWakeLock;
    @SuppressLint("InvalidWakeLockTag")
    @Override
    public void onReceive(Context context, Intent intent) {
        PrintLog.e(TAG, "Alarm Received");

        //this will sound the alarm tone
        //this will sound the alarm once, if you wish to
        //raise alarm in loop continuously then use MediaPlayer and setLooping(true)
       mp= MediaPlayer.create(context, R.raw.alarm);
        mp.start();

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

        SessionUtil sessionUtil = new SessionUtil(context);
        if (sessionUtil.isLogin()) {
            Intent notify = new Intent(context, GameViewActivity.class);
            notify.putExtra("TAG", "reminder");
            UpcomingContestModel upcomingContestModel = intent.getBundleExtra(BUNDLE_EXTRA).getParcelable(ALARM_KEY);
            if (upcomingContestModel != null) {
                notify.putExtra(GameViewActivity.CONTESTID, upcomingContestModel.getContestID());
                notify.putExtra(GameViewActivity.CONTESTTITLE, upcomingContestModel.getContestName());
                notify.putExtra(GameViewActivity.CONTESTTYPE, upcomingContestModel.getContestType());
                notify.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                String packageName = context.getPackageName();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (pm.isIgnoringBatteryOptimizations(packageName))
                        notify.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                    else {
                        notify.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                        notify.setData(Uri.parse("package:" + packageName));
                    }
                }
                context.startActivity(notify);

                if (mp !=null)
                    mp.stop();

                if (screenWakeLock != null)
                    screenWakeLock.release();
            } else {
                PrintLog.e(TAG, "Model Null Get");
            }
        }
    }

    public static void setReminderAlarm(Context context, UpcomingContestModel ucm) {


        final Intent intent = new Intent(context, AlarmReceiver.class);
        final Bundle bundle = new Bundle();
        intent.setAction(ACTION_PROCESS_UPDATES);
        bundle.putParcelable(ALARM_KEY, ucm);
        intent.putExtra(BUNDLE_EXTRA, bundle);


//        intent.setClass(context, AlarmReceiver.class);
        long mill = 0, currentMill = 0;
        mill = Utils.convertMillSecondsReminder(ucm.getContestDateTime());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) ucm.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager=(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        PrintLog.e("TAG_Alarm", (mill) + " mill Seconds");
        if (Build.VERSION.SDK_INT >= 23)
        {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mill, pendingIntent);
        }
        else if (Build.VERSION.SDK_INT >= 19)
        {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, mill, pendingIntent);
        }
        else
        {
            alarmManager.set(AlarmManager.RTC_WAKEUP, mill, pendingIntent);
        }
       // Enable BootReceiver Component
        setBootReceiverEnabled(context,PackageManager.COMPONENT_ENABLED_STATE_ENABLED);
    }

    private static void setBootReceiverEnabled(Context context, int componentEnabledStateEnabled) {
        ComponentName componentName = new ComponentName(context, AlarmReceiver.class);
        PackageManager packageManager = context.getPackageManager();
        packageManager.setComponentEnabledSetting(componentName,
                componentEnabledStateEnabled,
                PackageManager.DONT_KILL_APP);
    }
}
