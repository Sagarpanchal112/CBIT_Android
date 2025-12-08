package com.tfb.cbit.services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.tfb.cbit.R;
import com.tfb.cbit.activities.GameViewActivity;
import com.tfb.cbit.activities.SplashActivity;
import com.tfb.cbit.receiver.AlarmBroadcastReceiver;
import com.tfb.cbit.utility.Utils;

import static com.tfb.cbit.receiver.AlarmBroadcastReceiver.TITLE;

public class TimerService extends Service {

    private static final String TAG = TimerService.class.getSimpleName();
    private long startTime, endTime;
    // Is the service tracking time?
    private boolean isTimerRunning;
    private boolean serviceBound;
    // Foreground notification id
    private static final int NOTIFICATION_ID = 1;

    // Service binder
    private final IBinder serviceBinder = new RunServiceBinder();

    public class RunServiceBinder extends Binder {
        public TimerService getService() {
            return TimerService.this;
        }
    }

    @Override
    public void onCreate() {
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG, "Creating service");
        }
        isTimerRunning = false;
        super.onCreate();
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(context, serviceIntent);
        } else {
            context.startService(serviceIntent);
        }*/
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG, "Starting service");
        }
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG, "Binding service");
        }
        return serviceBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG, "Destroying service");
        }
    }

    public void startTimer(Context context, String id, String Name, String type, long mill) {

        Log.i("Alarm", "==>Set alarm");
        schedule(context, id, Name, type, mill);
        isTimerRunning = true;
     //   Utils.appendLog("Alaram set==>" + mill);

    }

    public boolean isTimerRunning() {
        return isTimerRunning;
    }

    public void foreground() {
        String NOTIFICATION_CHANNEL_ID = "com.tfb.cbit";
        String channelName = "Game Active";
        NotificationChannel chan = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.mipmap.app_green_icon)
                .setContentTitle("Cbit Original")
                .setContentText("Tap to return to the timer")
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setVibrate(new long[]{0L})
                .build();
        Intent resultIntent = new Intent(this, SplashActivity.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(this, 0, resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(resultPendingIntent);
        startForeground(NOTIFICATION_ID, notification);
    }

    public void background() {
        stopForeground(true);
    }

    public void schedule(Context context, String id, String Name, String type, long mill) {
        Log.i("Alarm", "==>schedule alarm");
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        intent.putExtra(GameViewActivity.CONTESTID, id);
        intent.putExtra(GameViewActivity.CONTESTTITLE, Name);
        intent.putExtra(GameViewActivity.CONTESTTYPE, type);
        intent.putExtra(TITLE, Name);
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, Integer.parseInt(id), intent, 0);
        if (Build.VERSION.SDK_INT >= 21) {
            AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(mill, alarmPendingIntent);
            alarmManager.setAlarmClock(alarmClockInfo,
                    alarmPendingIntent);

        } else if (Build.VERSION.SDK_INT >= 19) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, mill, alarmPendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, mill, alarmPendingIntent);
        }
    }

}