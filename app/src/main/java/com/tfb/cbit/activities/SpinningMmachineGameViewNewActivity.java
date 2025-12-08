/*
package com.tfb.cbit.activities;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.tfb.cbit.CBit;
import com.tfb.cbit.R;
import com.tfb.cbit.adapter.SpiningOptionsAdapter;
import com.tfb.cbit.adapter.SpinningTicketAdapter;
import com.tfb.cbit.adapter.ViewFliperItemAdapter;
import com.tfb.cbit.api.APIClient;
import com.tfb.cbit.api.ApiCallback;
import com.tfb.cbit.api.NewApiCall;
import com.tfb.cbit.event.GameResultEvent;
import com.tfb.cbit.event.SocketConnectionEvent;
import com.tfb.cbit.event.UpdateMyContestEvent;
import com.tfb.cbit.event.UpdateUpcomingContestEvent;
import com.tfb.cbit.interfaces.OnItemClickListener;
import com.tfb.cbit.interfaces.OnSlotListener;
import com.tfb.cbit.models.advertise.AdvertiseModel;
import com.tfb.cbit.models.contestdetails.BoxJson;
import com.tfb.cbit.models.contestdetails.ContestDetailsModel;
import com.tfb.cbit.models.contestdetails.Slote;
import com.tfb.cbit.models.contestdetails.Ticket;
import com.tfb.cbit.models.contestdetails.UserSelect;
import com.tfb.cbit.models.contestdetails.WinningOptions;
import com.tfb.cbit.models.updategame.UpdateAllGameModel;
import com.tfb.cbit.models.updategame.UpdateGameModel;
import com.tfb.cbit.services.AlarmService;
import com.tfb.cbit.utility.CustomDialog;
import com.tfb.cbit.utility.LogHelper;
import com.tfb.cbit.utility.PrintLog;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.SocketUtils;
import com.tfb.cbit.utility.Utils;
import com.tfb.cbit.views.RollControll;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.transports.WebSocket;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;

import static com.tfb.cbit.utility.Utils.SOCKET_URI;

public class SpinningMmachineGameViewNewActivity extends AppCompatActivity implements OnItemClickListener, OnSlotListener, RollControll.StopListener {
    private static final String TAG = SpinningMmachineGameViewNewActivity.class.getSimpleName();
    private Context context;
    private SessionUtil sessionUtil;




    public PowerManager.WakeLock wl;
  n)
    ImageView imgAnsSelection;
    String SDCardPath = "";



    RollControll mRoll1;
    RollControll mRoll2;
    RollControll mRoll3;
    RollControll mRoll4;
    RollControll mRoll5;

    ImageView ivAd;

    public String contest_id = "", constest_title = "", constest_type = "", SelectedDisplayView = "", CheckGameStatus = "", selectedImage = "";
    public static final String CONTESTID = "contest_id", CONTESTTITLE = "contest_title", CONTESTTYPE = "contest_type",EVENT_CONTEST_LIVE = "onContestLive";

    public int gamestatusCount = 0, ticktCount = 0,time = 0;

    public long startMill,lockMilliSec = 0, totalMill = 0,timeInMilliseconds;

    public boolean isHandlerPost = false, isReminderScreen = false, isClick = false, setRoll = false;
    public boolean isGameStartBool = false, isStartSlot = false;

    ArrayList<String> iamgesList;
    public List<BoxJson> boxJson = new ArrayList<>();
    public List<WinningOptions> winningOptionsList = new ArrayList<>();
    public List<Ticket> ticketList = new ArrayList<>();
    List<BoxJson> tempJsonList = new ArrayList<>();

    public SpinningTicketAdapter ticketAdapter;
    public SpiningOptionsAdapter optionsAdapter;
    public ContestDetailsModel cdm = null;


    public Socket mSocket;
    public static final String SOCKET_PATH = "/socket.io";

    public Dialog calculatingDialog = null;
    public Dialog lockcalculatingDialog = null;
    public Dialog dialog = null;
    public CustomDialog customDialog;
    public Timer mTimer;


    public Handler handler = new Handler();
    public static OkHttpClient okHttpClient;
    ConnectivityManager cm;
    NetworkInfo nInfo;

    public MediaPlayer mPlayer1 = null;
    public MediaPlayer mPlayer = null;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);

        setContentView(R.layout.activity_spinning_machine_game_view);

        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(
                Context.POWER_SERVICE);
        this.wl = pm.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK
                        | PowerManager.ON_AFTER_RELEASE,
                TAG);
        wl.acquire();


        context = this;
        ButterKnife.bind(this);
        cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        nInfo = cm.getActiveNetworkInfo();
        sessionUtil = new SessionUtil(context);
        SDCardPath = getFilesDir().getAbsolutePath() + "/";


        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new CheckForConnection(), 0, 5 * 1000);

        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        mPlayer1 = MediaPlayer.create(context, R.raw.waiting_timer);
        mPlayer1.setLooping(true);



        mRoll1 = new RollControll((ViewFlipper) findViewById(R.id.view_flipper_1), 3);
        mRoll2 = new RollControll((ViewFlipper) findViewById(R.id.view_flipper_2), 3);
        mRoll3 = new RollControll((ViewFlipper) findViewById(R.id.view_flipper_3), 3);
        mRoll4 = new RollControll((ViewFlipper) findViewById(R.id.view_flipper_4), 3);
        mRoll5 = new RollControll((ViewFlipper) findViewById(R.id.view_flipper_5), 3);


        getBundelData();
        getFromSdcard();

    }


    @Override
    public void onItemClick(View view, int position) {

    }

    @Override
    public void onSlotValue(View view, int parentPos, int childPos) {

    }

    class CheckForConnection extends TimerTask {
        @Override
        public void run() {
            new CheckNetwork().execute();

        }
    }

    class CheckNetwork extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            return isNetworkConnected();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                hasInternetConnection();
            } else {
                hasNoInternetConnection();
            }
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public void hasInternetConnection() {
        PrintLog.e(TAG, "Net Connected");
        mSocket.connect();
    }

    public void hasNoInternetConnection() {
        PrintLog.e(TAG, "Net Disconnect");
        mSocket.disconnect();
        mSocket.off(Socket.EVENT_RECONNECT, onContestLive);
    }



    @OnClick(R.id.ivBack)
    protected void ivBackClick() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        try {
            if (dialog != null && dialog.isShowing()) {
                Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
                getApplicationContext().stopService(intentService);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (isReminderScreen) {
            Intent intent = new Intent(context, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (dialog != null && dialog.isShowing()) {
                Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
                getApplicationContext().stopService(intentService);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (mPlayer1 != null && mPlayer1.isPlaying()) {
            mPlayer1.pause();
        }
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        try {
            if (calculatingDialog != null && calculatingDialog.isShowing()) {
                calculatingDialog.dismiss();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (EventBus.getDefault().isRegistered(this))
                EventBus.getDefault().unregister(this);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        wl.release();
        */
/*if (isMyServiceRunning(AlarmService.class)) {
            AlarmService.stopALram();
        }*//*

        Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
        getApplicationContext().stopService(intentService);

        super.onDestroy();
    }

    public void getFromSdcard() {
        iamgesList = new ArrayList<String>();// list of file paths
        File[] listFile;

        File file = new File(android.os.Environment.getExternalStorageDirectory(), ".cbit");

        if (file.isDirectory()) {
            listFile = file.listFiles();

            for (int i = 0; i < listFile.length; i++) {

                iamgesList.add(listFile[i].getAbsolutePath());

            }
        }
    }


    public void getBundelData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            finish();
        } else {
            contest_id = bundle.getString(CONTESTID, "");
            constest_title = bundle.getString(CONTESTTITLE, "");
            constest_type = bundle.getString(CONTESTTYPE, "");

            if (contest_id.equalsIgnoreCase("")) {
                contest_id = getIntent().getStringExtra(CONTESTID);
            }
            if (constest_title.equalsIgnoreCase("")) {
                constest_title = getIntent().getStringExtra(CONTESTTITLE);
            }
            if (constest_type.equalsIgnoreCase("")) {
                constest_type = getIntent().getStringExtra(CONTESTTYPE);
            }
        }
        if (bundle.getString("TAG", "").equals("reminder")) {
            if (sessionUtil.isLogin()) {
                isReminderScreen = true;
            } else {
                Intent intent = new Intent(context, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        }
        getContestDetails(false);

    }

    private void getContestDetails(final boolean isRecall) {
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        String request = "";
        try {
            jsonObject.put("contest_id", contest_id);
            jsonObject.put("isStart", 0);
            request = jsonObject.toString();
            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, getString(R.string.crypt_pass));
            data = request.getBytes(StandardCharsets.UTF_8);
            request = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Call<ResponseBody> call = APIClient.getInstance()
                .contestDetails(sessionUtil.getToken(), sessionUtil.getId(), request);
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                LogHelper.d(TAG, responseData);
                Gson gson = new Gson();


                configureSocketForSSL();
                connectSocket();
            }

            @Override
            public void failure(String responseData) {

            }
        });
    }


    private void configureSocketForSSL() {
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            // From https://www.washington.edu/itconnect/security/ca/load-der.crt
            InputStream is = getResources().getAssets().open("raw/ssl.crt");
            InputStream caInput = new BufferedInputStream(is);
            Certificate ca;
            try {
                ca = cf.generateCertificate(caInput);
                // System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
            } finally {
                caInput.close();
            }

            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf1 = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf1.init(keyStore);
            HostnameVerifier hostnameVerifier = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {

                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {

                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[0];
                }
            }};
            X509TrustManager trustManager = (X509TrustManager) trustAllCerts[0];

            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, null);
            SSLSocketFactory sslSocketFactorys = sslContext.getSocketFactory();

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .hostnameVerifier(hostnameVerifier)
                    .sslSocketFactory(sslSocketFactorys, trustManager)
                    .build();
            IO.Options opts = new IO.Options();
            opts.callFactory = okHttpClient;
            opts.webSocketFactory = okHttpClient;
            mSocket = IO.socket(SOCKET_URI, opts);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void connectSocket() {
        try {
            IO.Options options = new IO.Options();
            options.path = SOCKET_PATH;
            options.transports = new String[]{WebSocket.NAME};
            options.reconnection = true;
            options.reconnectionDelay = 5000;
            options.webSocketFactory = okHttpClient;
            mSocket = IO.socket(SOCKET_URI, options);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        mSocket.on(EVENT_CONTEST_LIVE, onContestLive);
        mSocket.connect();
        Log.d(TAG, "connected " + mSocket.connected());
    }

    private Emitter.Listener onContestLive = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    try {
                        String res = "";
                        res = CBit.getCryptLib().decryptCipherTextWithRandomIV(args[0].toString(), context.getString(R.string.crypt_pass));
//                        EventBus.getDefault().post(new ContestLive(res));
                        Log.d(TAG, "onContestLive:--> " + res);
                        try {
                            JSONObject jsonObject = new JSONObject(res);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } catch (
                            Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

}*/
