package com.tfb.cbit.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.legacy.content.WakefulBroadcastReceiver;

import com.tfb.cbit.activities.GameViewActivity;
import com.tfb.cbit.services.AlarmAlertWakeLock;
import com.tfb.cbit.services.AlarmService;
import com.tfb.cbit.services.RescheduleAlarmsService;

import static android.content.Context.POWER_SERVICE;

public class AlarmBroadcastReceiver extends BroadcastReceiver {
    public static final String TITLE = "TITLE";

    @Override
    public void onReceive(Context context, Intent intent) {
        //wake lock is need to keep timer alive when device goes to sleep mode
        Log.i("TAG", "ONRECEVIE");
        PowerManager powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "com.tfb.cbit.receiver::AlarmBroadcastReceiverTag");
        wakeLock.acquire(60000);

        startAlarmService(context, intent);
      //  AlarmAlertWakeLock.acquireCpuWakeLock(context);
    }

    public void setAlarm(Context context)
    {
        AlarmManager am =( AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, AlarmBroadcastReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60 * 10, pi); // Millisec * Second * Minute
    }

    private void startAlarmService(Context context, Intent intent) {
        Intent intentService = new Intent(context, AlarmService.class);
        intentService.putExtra(GameViewActivity.CONTESTID, intent.getStringExtra(GameViewActivity.CONTESTID));
        intentService.putExtra(GameViewActivity.CONTESTTITLE, intent.getStringExtra(GameViewActivity.CONTESTTITLE));
        intentService.putExtra(GameViewActivity.CONTESTTYPE, intent.getStringExtra(GameViewActivity.CONTESTTYPE));
        ContextCompat.startForegroundService(context,intentService);
     /*   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(context,intentService);
        } else {
            context.startService(intentService);
        }*/
    }

    private void startRescheduleAlarmsService(Context context) {
        Intent intentService = new Intent(context, RescheduleAlarmsService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intentService);
        } else {
            context.startService(intentService);
        }
    }

}
