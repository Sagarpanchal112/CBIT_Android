package com.tfb.cbit;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;

import com.onesignal.OneSignal;
import com.tfb.cbit.activities.SplashActivity;
import com.tfb.cbit.models.SelectedImage;
import com.tfb.cbit.models.anytimegame.Content;
import com.tfb.cbit.utility.CryptLib;
import com.tfb.cbit.utility.SocketUtils;

import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.crypto.NoSuchPaddingException;


public class CBit extends DarkThemeApplication {

    private static Context mContext;
    private static String OneSignalID;
    private static SocketUtils socketUtils;
    private static CryptLib cryptLib = null;
    public static String referealCode = "";
    public static String ImageDownload = "";
    public static final String CHANNEL_ID = "ALARM_SERVICE_CHANNEL";
   // public static ArrayList<Ticket> selectedTicketList = new ArrayList<>();
    public static ArrayList<SelectedImage> selectedImageArrayList = new ArrayList<>();
    public static ArrayList<Content> selectedTicketList = new ArrayList<>();
    private static final String ONESIGNAL_APP_ID = "7414ca8f-bea2-4dde-b68d-951745422b9e";


    @Override
    public void onCreate() {
        super.onCreate();
        //Fabric.with(this, new Crashlytics());
        mContext = this;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

    /*    Fabric build = new Fabric.Builder(mContext)
                .kits(new Crashlytics())
               // .debuggable(BuildConfig.DEBUG) // Enables Crashlytics debugger
                .build();
        Fabric.with(build);*/

     /*   CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(getString(R.string.Montserrat_Regular))
                .setFontAttrId(R.attr.fontPath)
                .build());
    */
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);
        OneSignal.promptForPushNotifications();
        createNotificationChannnel();
    }

    private void createNotificationChannnel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Alarm Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    public static Context getContext() {
        return mContext;
    }

    public static SocketUtils getSocketUtils() {
        if (socketUtils == null) {
            socketUtils = new SocketUtils(mContext);
        }
        return socketUtils;
    }

    public static CryptLib getCryptLib() {
        if (cryptLib == null) {
            try {
                cryptLib = new CryptLib();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            }
        }
        return cryptLib;
    }

}
