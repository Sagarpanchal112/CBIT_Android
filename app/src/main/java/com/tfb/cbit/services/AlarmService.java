package com.tfb.cbit.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.tfb.cbit.R;
import com.tfb.cbit.activities.GameViewActivity;
import com.tfb.cbit.activities.SpinningMmachineGameViewActivity;
import com.tfb.cbit.utility.Utils;

import static com.tfb.cbit.CBit.CHANNEL_ID;


public class AlarmService extends Service {
    private static MediaPlayer mediaPlayer;
    // private static Vibrator vibrator;
    private PowerManager.WakeLock wakeLock;
    private final IBinder serviceBinder = new RunServiceBinder();
    public int CONTESTID = 0;
    public String CONTESTTITLE = "cbit";
    public String CONTESTTYPE = "";


    public class RunServiceBinder extends Binder {
        public AlarmService getService() {
            return AlarmService.this;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate() {
        super.onCreate();
   /*     try {
            final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "PARTIAL_WAKE_LOCK_TAG");

        } catch (Exception e) {

        }
*/

        try {
            mediaPlayer = MediaPlayer.create(this, R.raw.alarm);
            mediaPlayer.setLooping(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //   vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

    }

    public static void stopALram() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            //     vibrator.cancel();
          //  Utils.appendLog("Alaram stop==>" + Utils.getTodayDate());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        switch (am.getRingerMode()) {
            case AudioManager.RINGER_MODE_SILENT:
                Log.i("MyApp","Silent mode");
                mediaPlayer.stop();
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                mediaPlayer.start();
                break;
            case AudioManager.RINGER_MODE_NORMAL:
                mediaPlayer.start();
                break;
        }
      //  Utils.appendLog("Alaram ring==>" + Utils.getTodayDate());
        Intent notificationIntent;
        CONTESTID = Integer.parseInt(intent.getStringExtra(SpinningMmachineGameViewActivity.CONTESTID));
        CONTESTTITLE = intent.getStringExtra(SpinningMmachineGameViewActivity.CONTESTTITLE);
        CONTESTTYPE = intent.getStringExtra(SpinningMmachineGameViewActivity.CONTESTTYPE);
        if (CONTESTTYPE.equalsIgnoreCase("spinning-machine")) {
            notificationIntent = new Intent(this, SpinningMmachineGameViewActivity.class);
            Log.i("SpinningMmachine", "==>" + CONTESTTITLE);
            notificationIntent.putExtra("contest_id", CONTESTID + "");
            notificationIntent.putExtra("contest_title", intent.getStringExtra(SpinningMmachineGameViewActivity.CONTESTTITLE));
            notificationIntent.putExtra("contest_type", intent.getStringExtra(SpinningMmachineGameViewActivity.CONTESTTYPE));
        } else {
            notificationIntent = new Intent(this, GameViewActivity.class);
            Log.i("GameViewActivity", "==>" + CONTESTTITLE);
            notificationIntent.putExtra("contest_id", CONTESTID + "");
            notificationIntent.putExtra("contest_title", intent.getStringExtra(GameViewActivity.CONTESTTITLE));
            notificationIntent.putExtra("contest_type", intent.getStringExtra(GameViewActivity.CONTESTTYPE));
        }
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        notificationIntent.putExtra("TAG", "reminder");

        PendingIntent pendingIntent = PendingIntent.getActivity(this, CONTESTID, notificationIntent, 0);
        startActivity(notificationIntent);
        Log.i("alarmTitle 1", "==>" + intent.getStringExtra(CONTESTTITLE));
        String alarmTitle = String.format("%s Alarm", intent.getStringExtra(CONTESTTITLE));

        Log.i("alarmTitle 2", "==>" + alarmTitle);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(CONTESTTITLE)
                .setContentText("Ring Ring")
                .setSmallIcon(R.mipmap.app_green_icon)
                .setContentIntent(pendingIntent)
                .setCategory(Notification.CATEGORY_ALARM)
                .setPriority(Notification.PRIORITY_HIGH)
                .build();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = Build.VERSION.SDK_INT >= 20 ? pm.isInteractive() : pm.isScreenOn(); // check if screen is on
        if (!isScreenOn) {
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "myApp:notificationLock");
            wl.acquire(3000); //set your time in milliseconds
        }

        long[] pattern = {0, 100, 1000};
        // vibrator.vibrate(pattern, 0);
        startForeground(CONTESTID, notification);
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        mediaPlayer.stop();
        //  vibrator.cancel();
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        mediaPlayer.stop();
        // vibrator.cancel();
        super.onTaskRemoved(rootIntent);
        // Your job when the service stops.
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return serviceBinder;
    }

}
