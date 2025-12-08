package com.tfb.cbit.activities;

import android.app.ActivityManager;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.tfb.cbit.CBit;
import com.tfb.cbit.R;
import com.tfb.cbit.adapter.BricksAdapter;
import com.tfb.cbit.adapter.TicketAdapter;
import com.tfb.cbit.api.APIClient;
import com.tfb.cbit.api.ApiCallback;
import com.tfb.cbit.api.NewApiCall;
import com.tfb.cbit.databases.DatabaseHandler;
import com.tfb.cbit.databinding.ActivityAboutUsBinding;
import com.tfb.cbit.databinding.ActivityGameViewBinding;
import com.tfb.cbit.event.GameAlertEvent;
import com.tfb.cbit.event.GameResultEvent;
import com.tfb.cbit.event.GameStartEvent;
import com.tfb.cbit.event.SocketConnectionEvent;
import com.tfb.cbit.event.UnAuthorizedEvent;
import com.tfb.cbit.event.UpdateMyContestEvent;
import com.tfb.cbit.event.UpdateUpcomingContestEvent;
import com.tfb.cbit.interfaces.OnItemClickListener;
import com.tfb.cbit.interfaces.OnRangeListener;
import com.tfb.cbit.interfaces.OnSlotListener;
import com.tfb.cbit.models.advertise.AdvertiseModel;
import com.tfb.cbit.models.contestdetails.BoxJson;
import com.tfb.cbit.models.contestdetails.ContestDetailsModel;
import com.tfb.cbit.models.contestdetails.Slote;
import com.tfb.cbit.models.contestdetails.Ticket;
import com.tfb.cbit.models.contestdetails.UserSelect;
import com.tfb.cbit.models.updategame.UpdateAllGameModel;
import com.tfb.cbit.models.updategame.UpdateGameModel;
import com.tfb.cbit.services.AlarmService;
import com.tfb.cbit.utility.CustomDialog;
import com.tfb.cbit.utility.LogHelper;
import com.tfb.cbit.utility.PrintLog;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.SocketUtils;
import com.tfb.cbit.utility.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
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

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.transports.WebSocket;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;

import static com.tfb.cbit.utility.Utils.SOCKET_URI;

public class GameViewActivity extends BaseAppCompactActivity implements OnItemClickListener, OnRangeListener, OnSlotListener {

    private Socket mSocket;
    public boolean isClick = false;
    private static final String SOCKET_PATH = "/socket.io";
    private Context context;
    public BricksAdapter bricksAdapter;
    List<BoxJson> tempJsonList = new ArrayList<>();
    ArrayList<Integer> bricksItems = new ArrayList<>();
    ArrayList<HashMap<String, Integer>> bricksColorModel = new ArrayList<>();
    int[] colorArray = {R.color.color_green, R.color.color_red, R.color.color_blue};
    private SessionUtil sessionUtil;
    private String contest_id = "", constest_title = "", constest_type = "";
    public static final String CONTESTID = "contest_id";
    public static final String CONTESTTITLE = "contest_title";
    public static final String CONTESTTYPE = "contest_type";
    private static final String TAG = GameViewActivity.class.getSimpleName();
    // Create the Handler
    private Handler handler = new Handler();
    private boolean isHandlerPost = false;
    private TicketAdapter ticketAdapter = null;
    private List<Ticket> ticketList = new ArrayList<>();
    private ContestDetailsModel cdm = null;
    private CustomDialog customDialog;
    //    private MediaPlayer mPlayer = null;
    private MediaPlayer mPlayer1 = null;
    private Dialog calculatingDialog = null;
    private Dialog lockcalculatingDialog = null;
    private Dialog dialog = null;
    private boolean isReminderScreen = false;
    public Timer mTimer;
    private boolean isGameStart = false;
    private PowerManager.WakeLock wl;
    private long differenceSecond = 0;
    long startMill;
    long endMill;
    public long mills;
    String SelectedDisplayView = "";

    /*Code for check kbps by ayaz*/
    long startTime;
    long endTime;
    long fileSize;
    OkHttpClient client = new OkHttpClient();
    // bandwidth in kbps
    private int POOR_BANDWIDTH = 150;
    private int AVERAGE_BANDWIDTH = 550;
    private int GOOD_BANDWIDTH = 2000;
    public static final String EVENT_CONTEST_LIVE = "onContestLive";
    private static OkHttpClient okHttpClient;

    Request request = new Request.Builder()
            .url("https://file-examples-com.github.io/uploads/2017/10/file_example_JPG_500kB.jpg")
            .build();
    int kilobytePerSec;
    ConnectivityManager cm;
    NetworkInfo nInfo;
    public int gamestatusCount = 0;

    String CheckGameStatus;
    int ticktCount = 0;
    long lockMilliSec = 0, totalMill = 0;
    long timeInMilliseconds;
    private ActivityGameViewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
        binding = ActivityGameViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setContentView(R.layout.activity_game_view);

        cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        nInfo = cm.getActiveNetworkInfo();
        binding.linPause.setVisibility(View.GONE);


        binding.rdgZero.clearCheck();
        binding.rdgFive.clearCheck();
        binding.rdgZero.setOnCheckedChangeListener(listener1);
        binding.rdgFive.setOnCheckedChangeListener(listener2);

        context = this;
        sessionUtil = new SessionUtil(context);
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            finish();
        } else {
            contest_id = bundle.getString(CONTESTID, "");
            constest_title = bundle.getString(CONTESTTITLE, "");
            constest_type = bundle.getString(CONTESTTYPE, "");
            if (contest_id.equalsIgnoreCase("")) {
                getIntent().getStringExtra(CONTESTID);
            }
            if (constest_title.equalsIgnoreCase("")) {
                getIntent().getStringExtra(CONTESTTITLE);
            }
            if (constest_type.equalsIgnoreCase("")) {
                getIntent().getStringExtra(CONTESTTYPE);
            }

            binding.toolbarTitle.setText(constest_title);

            if (bundle.getString("TAG", "").equals("reminder")) {
                if (sessionUtil.isLogin()) {
                    isReminderScreen = true;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
                            getApplicationContext().stopService(intentService);

                        }
                    }, 3000);
                    // stopAlramDiloag();

                } else {
                    Intent intent = new Intent(context, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }


            }
        }
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        binding.pbProgress.setVisibility(View.VISIBLE);
        binding.linearContent.setVisibility(View.GONE);

        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(
                Context.POWER_SERVICE);
        this.wl = pm.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK
                        | PowerManager.ON_AFTER_RELEASE,
                TAG);
        wl.acquire();



     /*   mPlayer = MediaPlayer.create(context, R.raw.heartbeat_new);
        mPlayer.setLooping(true);
     */
        mPlayer1 = MediaPlayer.create(context, R.raw.waiting_timer);
        mPlayer1.setLooping(true);
        binding.rvTickets.setLayoutManager(new LinearLayoutManager(context));
        if (binding.rvTickets.getItemAnimator() != null)
            ((SimpleItemAnimator) binding.rvTickets.getItemAnimator()).setSupportsChangeAnimations(false);
        ticketList.clear();
        ticketAdapter = new TicketAdapter(context, ticketList);
        Log.d(TAG, "AlramTickitlist: " + ticketList.size());
        ticketAdapter.setOnItemClickListener(this);
        ticketAdapter.setOnRangeListener(this);
        ticketAdapter.setOnSlotListener(this);
        binding.rvTickets.setAdapter(ticketAdapter);

        binding.rvBricks.setLayoutManager(new GridLayoutManager(context, 4));
        binding.rvBricksRes.setLayoutManager(new GridLayoutManager(context, 4));
        bricksAdapter = new BricksAdapter(context, bricksItems, bricksColorModel, tempJsonList);
        binding.rvBricks.setAdapter(bricksAdapter);

        getContestDetails(true);

        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new CheckForConnection(), 0, 5 * 1000);
        if (constest_type.equalsIgnoreCase("rdb")) {
            binding.linRdb.setVisibility(View.VISIBLE);
            //  gameNote.setText("Color with bigger total wins ");
            binding.linNine.setVisibility(View.GONE);
        } else if (constest_type.equalsIgnoreCase("0-9")) {
            binding.linRdb.setVisibility(View.GONE);
            // gameNote.setText("Total of digits in blue - Total of digits in red");
            binding.linNine.setVisibility(View.VISIBLE);
        }
        configureSocketForSSL(buildSslSocketFactory());
        try {
            IO.Options options = new IO.Options();
            options.path = SOCKET_PATH;
            options.transports = new String[]{WebSocket.NAME};
            options.reconnection = true;
            options.reconnectionDelay = 6000;
            options.webSocketFactory = okHttpClient;
            mSocket = IO.socket(SOCKET_URI, options);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        mSocket.on(EVENT_CONTEST_LIVE, onContestLive);
        mSocket.connect();
        CBit.getSocketUtils().connect();

        binding.ivBack.setOnClickListener(view -> {
            backpress();
        });
        binding.ivInfo.setOnClickListener(view -> {
            openInfoPopup();
        });
    }

    public void backpress() {
        try {
            mTimer.cancel();
            mSocket.off(EVENT_CONTEST_LIVE, onContestLive);
            mSocket.disconnect();
            mSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            wl.release();

        } catch (Exception e) {
            e.printStackTrace();
        }
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


    public void stopAlramDiloag() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    if (dialog != null && dialog.isShowing()) {

                    } else {
                        dialog = new Dialog(context);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        if (dialog.getWindow() != null)
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.setCancelable(false);
                        dialog.setContentView(R.layout.dialog_alarm_stop_layout);
                        TextView tvConstestID = dialog.findViewById(R.id.tvConstestID);
                        TextView tvConstestName = dialog.findViewById(R.id.tvConstestName);
                        tvConstestID.setText(contest_id);
                        tvConstestName.setText(constest_title);
                        Button btnStopAlarm = dialog.findViewById(R.id.btnStopAlarm);
                        btnStopAlarm.setOnClickListener(v -> {
                            Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
                            getApplicationContext().stopService(intentService);
                                  /*  if (mp != null && mp.isPlaying()) {
                                        mp.release();
                                        mp = null;
                                    }*/
                            if (dialog != null && dialog.isShowing()) {
                                dialog.dismiss();
                                dialog = null;
                            }
                        });
                        dialog.show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    PrintLog.e("MEDIA", e.getMessage());

                }

            }
        }, 1000);
    }

    private SSLSocketFactory buildSslSocketFactory() {
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream is = getResources().getAssets().open("raw/ssl.crt");
            InputStream caInput = new BufferedInputStream(is);
            Certificate ca;
            try {
                ca = cf.generateCertificate(caInput);
            } finally {
                caInput.close();
            }
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);
            return sslContext.getSocketFactory();

        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException |
                 CertificateException | IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUnAuthorizedEvent(UnAuthorizedEvent unAuthorizedEvent) {
        Utils.showToast(this, unAuthorizedEvent.getMessage());
        DatabaseHandler databaseHandler = new DatabaseHandler(this);
        databaseHandler.deleteTable();
        String fcmToken = sessionUtil.getFcmtoken();
        sessionUtil.logOut();
        sessionUtil.setFCMToken(fcmToken);
        Intent intent = new Intent(this, LoginSignUpActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void configureSocketForSSL(SSLSocketFactory sslSocketFactory) {
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream is = getResources().getAssets().open("raw/ssl.crt");
            InputStream caInput = new BufferedInputStream(is);
            Certificate ca;
            try {
                ca = cf.generateCertificate(caInput);
            } finally {
                caInput.close();
            }

            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);
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

    public boolean isGameStartBool = false;

    private Emitter.Listener onContestLive = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    try {
                        String res = "";
                        res = CBit.getCryptLib().decryptCipherTextWithRandomIV(args[0].toString(), context.getString(R.string.crypt_pass));
                        Log.d(TAG, "onContestLive:--> " + res);
                        try {
                            JSONObject jsonObject = new JSONObject(res);

                            if (cdm.getContent().getId() == jsonObject.getJSONObject("contest").getInt("id")) {
                                binding.tvNineLockNow.setAlpha(.5f);
                                binding.tvNineLockNow.setEnabled(false);
                                binding.tvRemainingText.setText("Game Starts in ");
                                binding.tvRemainingText.append("00:" + jsonObject.getString("time"));

                              /*  if (jsonObject.getString("time").equalsIgnoreCase("00:30")) {
                                    stopAlramDiloag();
                                }*/

                                if (jsonObject.getString("time").equalsIgnoreCase("00:16")) {
                                    //  stopPlaying();
                                    mPlayer1.start();
                                }
                                Log.d(TAG, "onContestLive: " + jsonObject.getJSONObject("contest").getString("gameStatus"));
                                if (jsonObject.has("contest")) {
                                    CheckGameStatus = jsonObject.getJSONObject("contest").getString("gameStatus");
                                    //Edit by Ayaz on 19-aug
                                    if (CheckGameStatus.equals(Utils.GAME_START)) {
                                        if (gamestatusCount == 0) {
                                            gamestatusCount++;
                                            ticketAdapter.setGameStatus(CheckGameStatus);
                                            ticketAdapter.notifyDataSetChanged();
                                        } else {
                                            for (int i = 0; i < ticketList.size(); i++) {
                                                for (Slote sObj : ticketList.get(i).getSlotes()) {
                                                    if (sObj.isIsSelected()) {
                                                        ticketAdapter.setGameStatus(CheckGameStatus);
                                                        ticketAdapter.notifyItemChanged(i);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    int gameTime = 0;
                                    gameTime = jsonObject.getInt("gameTime");
                                    if (!isGameStartBool && gameTime <= 30) {
                                        //  if (jsonObject.getString("gameTime").equalsIgnoreCase("30")) {
                                        isGameStartBool = true;
                                        CountDownTimer Count = new CountDownTimer(gameTime * 1000, 100) {
                                            public void onTick(long millisUntilFinished) {
                                                int seconds = (int) ((millisUntilFinished / 1000));
                                                binding.tvText.setText(seconds + "");
                                                //  tvText.setText(seconds+"");
                                                lockMilliSec = millisUntilFinished;
                                            }

                                            public void onFinish() {
                                                binding.tvText.setText("00");
                                            }
                                        };
                                        Count.start();

                                        // }
                                    }
                                  /*  if (jsonObject.getString("gameTime").equalsIgnoreCase("30")) {
                                        CountDownTimer Count = new CountDownTimer(30000, 100) {
                                            public void onTick(long millisUntilFinished) {
                                                int seconds = (int) ((millisUntilFinished / 1000));
                                                tvText.setText(seconds + ":" + millisUntilFinished % 1000);
                                                lockMilliSec = millisUntilFinished;
                                            }

                                            public void onFinish() {
                                                tvText.setText("00:000");
                                            }
                                        };
                                        Count.start();
                                    }*/

                                 /*   try {
                                        if (dialog == null && !dialog.isShowing() && isMyServiceRunning(AlarmService.class)) {
                                            AlarmService.stopALram();
                                        }
                                    } catch (Exception e) {

                                    }*/

                                    if (jsonObject.getJSONObject("contest").getString("gameStatus").equalsIgnoreCase(Utils.GAME_START)) {
                                        try {
                                            if (mPlayer1 != null) {
                                                mPlayer1.stop();
                                                mPlayer1.release();
                                                mPlayer1 = null;
                                            }
                                        } catch (Exception e) {

                                        }

                                       /* if (isMyServiceRunning(AlarmService.class)) {
                                            AlarmService.stopALram();
                                        }*/
                                        binding.rvBricks.setVisibility(View.GONE);
                                        binding.tvText.setVisibility(View.VISIBLE);
                                        binding.tvRemainingText.setVisibility(View.GONE);
                                        handler.removeCallbacks(runnable);
                                        Log.d(TAG, "callGameStart: " + jsonObject.getString("time"));
                                        // tvText.setText(jsonObject.getString("gameTime"));
                                        List<BoxJson> boxJsonList = new ArrayList<>();
                                        for (int i = 0; i < jsonObject.getJSONObject("contest").getJSONArray("boxJson").length(); i++) {
                                            JSONObject object = jsonObject.getJSONObject("contest").getJSONArray("boxJson").getJSONObject(i);
                                            BoxJson boxJson = new BoxJson();
                                            boxJson.setColor(object.getString("color"));
                                            boxJson.setSymbol(object.getString("symbol"));
                                            boxJson.setNumber(object.getString("number"));
                                            boxJsonList.add(boxJson);
                                        }

                                        tempJsonList.clear();
                                        if (bricksColorModel.size() > 0 && boxJsonList.size() > 0) {
                                            for (int i = 0; i < bricksColorModel.size(); i++) {
                                                HashMap<String, Integer> map = bricksColorModel.get(i);
                                                tempJsonList.add(i, boxJsonList.get(map.get("index")));
                                            }
                                        } else {
                                            tempJsonList.addAll(boxJsonList);
                                        }
                                        if (!isClick) {
                                            binding.tvNineLockNow.setAlpha(1f);
                                            binding.tvNineLockNow.setEnabled(true);
                                            colorrdb();
                                            color09();
                                            binding.rdBlue.setClickable(true);
                                            binding.rdDraw.setClickable(true);
                                            binding.rdRed.setClickable(true);
                                            binding.rdZero.setClickable(true);
                                            binding.rdOne.setClickable(true);
                                            binding.rdTwo.setClickable(true);
                                            binding.rdThree.setClickable(true);
                                            binding.rdFour.setClickable(true);
                                            binding.rdFive.setClickable(true);
                                            binding.rdSix.setClickable(true);
                                            binding.rdSeven.setClickable(true);
                                            binding.rdEight.setClickable(true);
                                            binding.rdNine.setClickable(true);
                                        } else {
                                            colorrdb();
                                            color09();
                                            binding.rdBlue.setClickable(false);
                                            binding.rdDraw.setClickable(false);
                                            binding.rdRed.setClickable(false);
                                            binding.rdZero.setClickable(false);
                                            binding.rdOne.setClickable(false);
                                            binding.rdTwo.setClickable(false);
                                            binding.rdThree.setClickable(false);
                                            binding.rdFour.setClickable(false);
                                            binding.rdFive.setClickable(false);
                                            binding.rdSix.setClickable(false);
                                            binding.rdSeven.setClickable(false);
                                            binding.rdEight.setClickable(false);
                                            binding.rdNine.setClickable(false);
                                        }

                                        binding.rdRdb.setOnCheckedChangeListener((group, checkedId) -> {
                                                    RadioButton radioButton = findViewById(checkedId);
                                                    SelectedDisplayView = String.valueOf(radioButton.getText());
                                                }
                                        );

                                        binding.tvNineLockNow.setOnClickListener(v -> {
                                            if (Utils.isNetworkAvailable(GameViewActivity.this)) {
                                                if (binding.linNine.getVisibility() == View.VISIBLE) {
                                                    if (binding.rdgZero.getCheckedRadioButtonId() == -1 && binding.rdgFive.getCheckedRadioButtonId() == -1) {
                                                        Utils.showToast(context, "Please Select Any Number");
                                                    } else {
                                                        isClick = true;
                                                        binding.tvNineLockNow.setAlpha(.5f);
                                                        binding.tvNineLockNow.setEnabled(false);
                                                        long temp = 30000 - lockMilliSec;
                                                        totalMill = timeInMilliseconds + temp;
                                                        setALLAnsLock(SelectedDisplayView);
                                                        binding.imgLock.setVisibility(View.VISIBLE);
                                                        binding.tvNineLockNow.setVisibility(View.GONE);
                                                        binding.tvLockTime.setVisibility(View.VISIBLE);
                                                        waitingPopupforLock();
                                                    }
                                                }
                                            } else {
                                                Utils.showToast(GameViewActivity.this, "Please check your internet connection.");
                                            }
                                            if (Utils.isNetworkAvailable(GameViewActivity.this)) {
                                                if (binding.linRdb.getVisibility() == View.VISIBLE) {
                                                    if (binding.rdRdb.getCheckedRadioButtonId() == -1) {
                                                        Utils.showToast(context, "Please Select Any Number");
                                                    } else {
                                                        isClick = true;
                                                        binding.tvNineLockNow.setAlpha(.5f);
                                                        binding.tvNineLockNow.setEnabled(false);
                                                        long temp = 30000 - lockMilliSec;
                                                        totalMill = timeInMilliseconds + temp;
                                                        setALLAnsLock(SelectedDisplayView);
                                                        binding.imgLock.setVisibility(View.VISIBLE);
                                                        binding.tvNineLockNow.setVisibility(View.GONE);
                                                        binding.tvLockTime.setVisibility(View.VISIBLE);
                                                        waitingPopupforLock();
                                                    }
                                                }
                                            } else {
                                                Utils.showToast(GameViewActivity.this, "Please check your internet connection.");
                                            }
                                        });
                                        binding.rvBricksRes.setAdapter(new BricksAdapter(context, tempJsonList, true));
                                        binding.rvBricksRes.setVisibility(View.VISIBLE);
                                    } else {
                                        grayrdb();
                                        gray09();
                                        binding.tvText.setVisibility(View.GONE);
                                        binding.rvBricksRes.setVisibility(View.GONE);
                                        binding.rvBricks.setVisibility(View.VISIBLE);
                                        binding.tvRemainingText.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    grayrdb();
                                    gray09();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

   /* private void stopPlaying() {
        if (mPlayer1 != null) {
            mPlayer1.stop();
            mPlayer1.release();
            mPlayer1 = null;
        }
    }*/

    public void colorrdb() {
        binding.rdBlue.setBackground(ContextCompat.getDrawable(context, R.drawable.price_group_sel_blue));
        binding.rdDraw.setBackground(ContextCompat.getDrawable(context, R.drawable.price_group_sel));
        binding.rdDraw.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        binding.rdRed.setBackground(ContextCompat.getDrawable(context, R.drawable.price_group_sel_red));
    }

    public void grayrdb() {
        binding.rdBlue.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        binding.rdDraw.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        binding.rdDraw.setTextColor(context.getResources().getColor(R.color.white));
        binding.rdRed.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));

    }

    public void color09() {
        binding.rdZero.setBackground(ContextCompat.getDrawable(context, R.drawable.price_group_sel));
        binding.rdOne.setBackground(ContextCompat.getDrawable(context, R.drawable.price_group_sel));
        binding.rdTwo.setBackground(ContextCompat.getDrawable(context, R.drawable.price_group_sel));
        binding.rdThree.setBackground(ContextCompat.getDrawable(context, R.drawable.price_group_sel));
        binding.rdFour.setBackground(ContextCompat.getDrawable(context, R.drawable.price_group_sel));
        binding.rdFive.setBackground(ContextCompat.getDrawable(context, R.drawable.price_group_sel));
        binding.rdSix.setBackground(ContextCompat.getDrawable(context, R.drawable.price_group_sel));
        binding.rdSeven.setBackground(ContextCompat.getDrawable(context, R.drawable.price_group_sel));
        binding.rdEight.setBackground(ContextCompat.getDrawable(context, R.drawable.price_group_sel));
        binding.rdNine.setBackground(ContextCompat.getDrawable(context, R.drawable.price_group_sel));
    }

    public void gray09() {
        binding.rdZero.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        binding.rdOne.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        binding.rdTwo.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        binding.rdThree.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        binding.rdFour.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        binding.rdFive.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        binding.rdSix.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        binding.rdSeven.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        binding.rdEight.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        binding.rdNine.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));

    }

    public void GetNetKBPS() {
        if (isConnected()) {
            startTime = System.currentTimeMillis();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    runOnUiThread(() -> {
                        // Toast.makeText(GameViewActivity.this, "Something wrong", Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void onResponse(okhttp3.Call call, Response response) throws IOException {
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

                    Headers responseHeaders = response.headers();
                    for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                        Log.d(TAG, responseHeaders.name(i) + ": " + responseHeaders.value(i));
                    }

                    InputStream input = response.body().byteStream();

                    try {
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        byte[] buffer = new byte[1024];

                        while (input.read(buffer) != -1) {
                            bos.write(buffer);
                        }
                        byte[] docBuffer = bos.toByteArray();
                        fileSize = bos.size();

                    } finally {
                        input.close();
                    }

                    endTime = System.currentTimeMillis();


                    // calculate how long it took by subtracting endtime from starttime

                    double timeTakenMills = Math.floor(endTime - startTime);  // time taken in milliseconds
                    double timeTakenSecs = timeTakenMills / 1000;  // divide by 1000 to get time in seconds
                    kilobytePerSec = (int) Math.round(1024 / timeTakenSecs);

                    // get the download speed by dividing the file size by time taken to download
                    double speed = fileSize / timeTakenMills;

                    Log.d(TAG, "Time taken in secs: " + timeTakenSecs);
                    Log.d(TAG, "kilobyte per sec: " + kilobytePerSec);
                    Log.d(TAG, "Download Speed: " + speed);
                    Log.d(TAG, "File size: " + fileSize);
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (kilobytePerSec <= AVERAGE_BANDWIDTH) {
                                binding.imgNet.setImageResource(R.drawable.net_red);
                            } else {
                                binding.imgNet.setImageResource(R.drawable.net_green);
                            }
                        }
                    });
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

    }

    public boolean isConnected() {
        boolean connected = false;
        try {
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }

    private RadioGroup.OnCheckedChangeListener listener1 = new RadioGroup.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId != -1) {
                binding.rdgFive.setOnCheckedChangeListener(null); // remove the listener before clearing so we don't throw that stackoverflow exception(like Vladimir Volodin pointed out)
                binding.rdgFive.clearCheck(); // clear the second RadioGroup!
                binding.rdgFive.setOnCheckedChangeListener(listener2); //reset the listener
                RadioButton radioButton = findViewById(checkedId);
                SelectedDisplayView = String.valueOf(radioButton.getText());
            }
        }
    };

    private RadioGroup.OnCheckedChangeListener listener2 = new RadioGroup.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId != -1) {
                binding.rdgZero.setOnCheckedChangeListener(null);
                binding.rdgZero.clearCheck();
                binding.rdgZero.setOnCheckedChangeListener(listener1);
                RadioButton radioButton = findViewById(checkedId);
                SelectedDisplayView = String.valueOf(radioButton.getText());

            }
        }
    };

    private Ack updateGame = new Ack() {
        @Override
        public void call(final Object... args) {
            PrintLog.e(TAG, "updateGame encrypt" + args[0].toString());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String res = "";
                    try {
                        res = CBit.getCryptLib().decryptCipherTextWithRandomIV(args[0].toString(), context.getString(R.string.crypt_pass));
                        PrintLog.e(TAG, "updateGame " + res);
                        Gson gson = new Gson();
                        Object data = new JSONObject(res);
                        Log.d(TAG, "datta>>>: " + data.toString());
                        // Toast.makeText(context, "call obj", Toast.LENGTH_SHORT).show();
                        UpdateGameModel ugm = gson.fromJson(res, UpdateGameModel.class);
                        if (ugm.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                            ticketList.get(ugm.getContent().getPosition()).setIsLock(ugm.getContent().isIsLock());
                            ticketList.get(ugm.getContent().getPosition()).setLockTime(ugm.getContent().getIsLockTime());
                            ticketList.get(ugm.getContent().getPosition()).setDisplayView(ugm.getContent().getDisplayValue());
                            UserSelect userSelect = new UserSelect();
                            userSelect.setStartValue(String.valueOf(ugm.getContent().getStartValue()));
                            userSelect.setEndValue(String.valueOf(ugm.getContent().getEndValue()));
                            ticketList.get(ugm.getContent().getPosition()).setUserSelect(userSelect);
                            PrintLog.e(TAG, "updateGame Result " + ugm.getContent().getPosition() + "");
                            //  ticketAdapter.notifyItemChanged(ugm.getContent().getPosition());
                            ticketAdapter.notifyDataSetChanged();
                        } else {
                            Utils.showToast(context, ugm.getMessage());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Ack updateAllGame = new Ack() {
        @Override
        public void call(final Object... args) {
            PrintLog.e(TAG, "updateGame encrypt" + args[0].toString());
            PrintLog.e(TAG, "updateGame dencrypt" + args[0].toString());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String res = "";
                    try {
                        res = CBit.getCryptLib().decryptCipherTextWithRandomIV(args[0].toString(), context.getString(R.string.crypt_pass));
                        PrintLog.e(TAG, "updateallGame1 " + res);
                        Gson gson = new Gson();
                        Object data = new JSONObject(res);
                        Log.d(TAG, "datta>>>: " + data.toString());
                        //  Toast.makeText(context, "call array", Toast.LENGTH_SHORT).show();
                        UpdateAllGameModel ugm = gson.fromJson(res, UpdateAllGameModel.class);
                        if (ugm.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {

                            //edit by ayaz 20-aug
                            /*grayrdb();
                            gray09();*/

                            binding.rdBlue.setClickable(false);
                            binding.rdDraw.setClickable(false);
                            binding.rdRed.setClickable(false);


                            binding.rdZero.setClickable(false);
                            binding.rdOne.setClickable(false);
                            binding.rdTwo.setClickable(false);
                            binding.rdThree.setClickable(false);
                            binding.rdFour.setClickable(false);
                            binding.rdFive.setClickable(false);
                            binding.rdSix.setClickable(false);
                            binding.rdSeven.setClickable(false);
                            binding.rdEight.setClickable(false);
                            binding.rdNine.setClickable(false);
                            binding.imgLock.setVisibility(View.VISIBLE);
                            binding.tvLockTime.setVisibility(View.VISIBLE);
                            binding.tvNineLockNow.setVisibility(View.GONE);
                            binding.tvLockTime.setText("Locked at: " + ugm.getLockTime());
                            binding.tvAnsSelection.setText(SelectedDisplayView);
                            binding.tvAnsSelection1.setText(SelectedDisplayView);
                            Log.d(TAG, "success: " + ugm.getContent().get(0).getIsLockTime() + ">>>" + SelectedDisplayView);
                            Log.d(TAG, "sizee: " + ugm.getContent().size());
                            if (lockcalculatingDialog != null && lockcalculatingDialog.isShowing()) {
                                lockcalculatingDialog.dismiss();
                            }
                            for (int i = 0; i < ugm.getContent().size(); i++) {
                                try {
                                    /* Vishal Change */
                                    for (int j = 0; j < ticketList.size(); j++) {
                                        /* Vishal Change if condition */
                                        if (ugm.getContent().get(i).getContestPriceId() == ticketList.get(j).getContestPriceId()) {
                                            System.err.println(j + " " + ticketList.get(j).getMinValue() + " " + ticketList.get(j).getMaxValue());
                                            if (ticketList.get(j).getMinValue().isEmpty()) {
                                                ticketList.get(j).setIsLock(ugm.getContent().get(i).isIsLock());
                                                ticketList.get(j).setLockTime(ugm.getLockTime());
                                                ticketList.get(j).setDisplayView(SelectedDisplayView);
                                                ticketList.get(j).setMinValue(String.valueOf(ugm.getContent().get(i).getStartValue()));
                                                ticketList.get(j).setMaxValue(String.valueOf(ugm.getContent().get(i).getEndValue()));
                                                System.err.println(j + " " + ticketList.get(j).getMinValue() + " " + ticketList.get(j).getMaxValue());
//                                ticketList.get(i).setDisplayView(ugm.getContent().get(i).getDisplayValue());

                                                UserSelect userSelect = new UserSelect();
                                                userSelect.setStartValue(String.valueOf(ugm.getContent().get(i).getStartValue()));
                                                userSelect.setEndValue(String.valueOf(ugm.getContent().get(i).getEndValue()));
                                                userSelect.setDisplayValue(String.valueOf(ugm.getContent().get(i).getDisplayValue()));
                                                userSelect.setSelectValue(String.valueOf(ugm.getContent().get(i).getDisplayValue()));

                                                ticketList.get(j).setUserSelect(userSelect);
                                            }
                                            ticketAdapter.notifyItemChanged(i);
                                            break;
                                        }
                                    }
/*
                                    ticketList.get(i).setIsLock(ugm.getContent().get(i).isIsLock());
                                    ticketList.get(i).setLockTime(ugm.getContent().get(i).getIsLockTime());
                                    ticketList.get(i).setDisplayView(SelectedDisplayView);
//                                ticketList.get(i).setDisplayView(ugm.getContent().get(i).getDisplayValue());

                                    UserSelect userSelect = new UserSelect();
                                    userSelect.setStartValue(String.valueOf(ugm.getContent().get(i).getStartValue()));
                                    userSelect.setEndValue(String.valueOf(ugm.getContent().get(i).getEndValue()));
                                    ticketList.get(i).setUserSelect(userSelect);
                                    ticketAdapter.notifyItemChanged(i);*/
                                    /* Vishal Change */

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }


                        } else {
                            Utils.showToast(context, ugm.getMessage());
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        }
    };

    @Override
    public void onItemClick(View view, int position) {
        int id = view.getId();
        if (id == R.id.tvMinus) {
            if (!ticketList.get(position).isIsLock()) {
                ticketList.get(position).setMinValue(String.valueOf(ticketList.get(position).getSlotes().get(0).getStartValue()));
                ticketList.get(position).setMaxValue(String.valueOf(ticketList.get(position).getSlotes().get(0).getEndValue()));
                ticketList.get(position).setDisplayView(String.valueOf(ticketList.get(position).getSlotes().get(0).getDisplayValue()));
                ticketAdapter.notifyItemChanged(position);
            }
        } else if (id == R.id.tvZero) {
            if (!ticketList.get(position).isIsLock()) {
                ticketList.get(position).setMinValue(String.valueOf(ticketList.get(position).getSlotes().get(1).getStartValue()));
                ticketList.get(position).setMaxValue(String.valueOf(ticketList.get(position).getSlotes().get(1).getEndValue()));
                ticketList.get(position).setDisplayView(String.valueOf(ticketList.get(position).getSlotes().get(1).getDisplayValue()));
                ticketAdapter.notifyItemChanged(position);
            }
        } else if (id == R.id.tvPlus) {
            if (!ticketList.get(position).isIsLock()) {
                ticketList.get(position).setMinValue(String.valueOf(ticketList.get(position).getSlotes().get(2).getStartValue()));
                ticketList.get(position).setMaxValue(String.valueOf(ticketList.get(position).getSlotes().get(2).getEndValue()));
                ticketList.get(position).setDisplayView(String.valueOf(ticketList.get(position).getSlotes().get(2).getDisplayValue()));
                ticketAdapter.notifyItemChanged(position);
            }
        } else if (id == R.id.tvLockNow) {//Toast.makeText(context, "clickk", Toast.LENGTH_SHORT).show();

            PrintLog.e(TAG, "tvLockNow click " + position + "");
            if (CheckGameStatus.equalsIgnoreCase(Utils.GAME_START)) {
                if (!ticketList.get(position).getMinValue().isEmpty()) {
                    PrintLog.e(TAG, "tvLockNow click in if" + position + "");
                    setAnsLock(ticketList.get(position).getContestPriceId(),
                            ticketList.get(position).getMinValue(),
                            ticketList.get(position).getMaxValue(),
                            position,
                            ticketList.get(position).getDisplayView());
                } else {
                    Utils.showToast(context, "Please Select Any Number");
                }
            } else {
                Utils.showToast(context, "Game Not Started");
            }
        }
    }

    private void setAnsLock(int contestPriceId, String startValue, String endValue, int position, String displayValue) {
        try {
            JSONObject object = new JSONObject();
            object.put("userId", sessionUtil.getId());
            object.put("contestId", contest_id);
            object.put("contestPriceId", contestPriceId);
            object.put("startValue", startValue);
            object.put("endValue", endValue);
            object.put("isLock", 1);
            object.put("position", position);
            object.put("displayValue", displayValue);
            long temp = 30000 - lockMilliSec;
            totalMill = timeInMilliseconds + temp;

            object.put("lock_time", getDate(totalMill, "yyyy-MM-dd HH:mm:ss.SSSS"));
            byte[] data;
            String request = "";
            request = object.toString();
            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, context.getString(R.string.crypt_pass));
            data = request.getBytes(StandardCharsets.UTF_8);
            request = Base64.encodeToString(data, Base64.DEFAULT);

            // Karan chage
            //CBit.getSocketUtils().getmSocket().emit(SocketUtils.EVENT_UPDATE_GAME, request, updateGame);
            mSocket.emit(SocketUtils.EVENT_UPDATE_GAME, request, updateGame);
            // getContestDetails(true);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    private void setALLAnsLock(String displayValue) {
        try {
            JSONObject object = new JSONObject();
            object.put("userId", sessionUtil.getId());
            object.put("contestId", contest_id);
            object.put("DisplayValue", displayValue);
            object.put("lock_time", getDate(totalMill, "yyyy-MM-dd HH:mm:ss.SSSS"));
            byte[] data;
            String request = "";
            request = object.toString();
            Log.d(TAG, "setALLAnsLock: " + object.toString());
            Log.d(TAG, "setALLAnsLock: " + request);
            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, context.getString(R.string.crypt_pass));
            data = request.getBytes(StandardCharsets.UTF_8);
            request = Base64.encodeToString(data, Base64.DEFAULT);
            //CBit.getSocketUtils().getmSocket().emit(SocketUtils.EVENT_UPDATE_GAMEALL, request, updateAllGame);
            mSocket.emit(SocketUtils.EVENT_UPDATE_GAMEALL, request, updateAllGame);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRangeValue(int minValue, int maxValue, int position) {
        PrintLog.d("TAG", "Min " + minValue + " " + "Max " + maxValue);
        ticketList.get(position).setMinValue(String.valueOf(minValue));
        ticketList.get(position).setMaxValue(String.valueOf(maxValue));
        ticketList.get(position).setDisplayView(minValue + " To " + maxValue);
        ticketAdapter.notifyItemChanged(position);
    }

    @Override
    public void onSlotValue(View view, int parentPos, int childPos) {
        for (int i = 0; i < ticketList.get(parentPos).getSlotes().size(); i++) {
            if (i == childPos) {
                ticketList.get(parentPos).getSlotes().get(i).setIsSelected(true);
                ticketList.get(parentPos).setMinValue(String.valueOf(ticketList.get(parentPos).getSlotes().get(childPos).getStartValue()));
                ticketList.get(parentPos).setMaxValue(String.valueOf(ticketList.get(parentPos).getSlotes().get(childPos).getEndValue()));
                ticketList.get(parentPos).setDisplayView(String.valueOf(ticketList.get(parentPos).getSlotes().get(childPos).getDisplayValue()));
            } else {
                ticketList.get(parentPos).getSlotes().get(i).setIsSelected(false);
            }
        }

        ticketAdapter.notifyItemChanged(parentPos);
    }

    public boolean isGoResult = false;

    @Subscribe
    public void onUpdateUpcomingContestEvent(UpdateUpcomingContestEvent updateUpcomingContestEvent) {
        waitingPopup();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
              /*  try {
                    if (calculatingDialog != null && calculatingDialog.isShowing()) {
                        calculatingDialog.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!isGoResult) {
                    isGoResult = true;
                    Intent intent = new Intent(context, SpinerGameResultActivity.class);
                    intent.putExtra(SpinerGameResultActivity.CONTEST_ID, contest_id);
                    intent.putExtra(SpinerGameResultActivity.IS_REMINDER, isReminderScreen);
                    startActivity(intent);
                    finishAffinity();
                }*/
            }
        }, 10000);
    }

    @Subscribe
    public void onGameResultEvent(final GameResultEvent gameResultEvent) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (gameResultEvent.getResponse().equals(contest_id)) {
                    EventBus.getDefault().post(new UpdateUpcomingContestEvent());
                    EventBus.getDefault().post(new UpdateMyContestEvent());
                    waitingPopup();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                         /*   if (mPlayer != null) {
                                mPlayer.release();
                                mPlayer = null;
                            }*/
                            if (mPlayer1 != null) {
                                mPlayer1.release();
                                mPlayer1 = null;
                            }
                            try {
                                if (calculatingDialog != null && calculatingDialog.isShowing()) {
                                    calculatingDialog.dismiss();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (!isGoResult) {
                                isGoResult = true;
                                Intent intent = new Intent(context, GameResultActivity.class);
                                intent.putExtra(GameResultActivity.CONTEST_ID, contest_id);
                                intent.putExtra(GameResultActivity.IS_REMINDER, isReminderScreen);
                                startActivity(intent);
                                finishAffinity();
                            }

                        }
                    }, 10000);

                }
            }
        });

    }

    private void waitingPopup() {
        //  mPlayer.start();
        if (calculatingDialog == null) {
            calculatingDialog = new Dialog(this);
            calculatingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            calculatingDialog.setCancelable(false);
            calculatingDialog.setContentView(R.layout.dialog_calculating_result);
            if (calculatingDialog.getWindow() != null) {
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                Window window = calculatingDialog.getWindow();
                lp.copyFrom(window.getAttributes());
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                DisplayMetrics metrics = getResources().getDisplayMetrics();
                lp.width = (int) (metrics.widthPixels * 0.90);
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                window.setAttributes(lp);
            }
            ivAd = calculatingDialog.findViewById(R.id.ivAd);
            //  getAds();

        }
        calculatingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                //  mp.release();
            }
        });

        calculatingDialog.show();
    }

    private void waitingPopupforLock() {
        if (lockcalculatingDialog == null) {
            lockcalculatingDialog = new Dialog(this);
            lockcalculatingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            lockcalculatingDialog.setCancelable(false);
            lockcalculatingDialog.setContentView(R.layout.dialog_ans_lock);

            if (lockcalculatingDialog.getWindow() != null) {
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                Window window = lockcalculatingDialog.getWindow();
                lp.copyFrom(window.getAttributes());
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                DisplayMetrics metrics = getResources().getDisplayMetrics();
                lp.width = (int) (metrics.widthPixels * 0.90);
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                window.setAttributes(lp);
            }
        }

        lockcalculatingDialog.show();
    }

    @Subscribe
    public void onGameStartEvent(GameStartEvent gameStartEvent) {
        if (gameStartEvent.getResponse().equals(contest_id)) {
            isGameStart = true;
            try {
                JSONObject object = new JSONObject();
                object.put("userId", sessionUtil.getId());
                object.put("contestId", contest_id);
                byte[] data;
                String request = "";
                request = object.toString();
                request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, context.getString(R.string.crypt_pass));
                data = request.getBytes(StandardCharsets.UTF_8);
                request = Base64.encodeToString(data, Base64.DEFAULT);
                startMill = System.currentTimeMillis();
                //   getContestDetails(false);
//                CBit.getSocketUtils().getmSocket().emit(SocketUtils.EVENT_CONTEST_DETAILS, request, contestDetails);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Subscribe
    public void onGameAlertEvent(GameAlertEvent gameAlertEvent) {
        if (contest_id.equals(gameAlertEvent.getConetestId())) {
            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(Integer.parseInt(contest_id));
        }
    }


    @Subscribe()
    public void onSocketConnectionEvent(final SocketConnectionEvent socketConnectionEvent) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PrintLog.e(TAG, socketConnectionEvent.getMessage());
                if (mSocket.connected()) {
                    if (customDialog != null)
                        customDialog.dismissProgress(context);
                    CBit.getSocketUtils().loginEmit(sessionUtil.getId());
                    PrintLog.e("TAG", "Game Socket Connect Socket ID " + mSocket.id());
                    try {
                        JSONObject object = new JSONObject();
                        object.put("userId", sessionUtil.getId());
                        object.put("contestId", contest_id);
                        byte[] data;
                        String request = "";
                        request = object.toString();
                        PrintLog.e("TAG", "Game Socket Connect " + contest_id + " " + request);
                        request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, context.getString(R.string.crypt_pass));
                        data = request.getBytes(StandardCharsets.UTF_8);
                        request = Base64.encodeToString(data, Base64.DEFAULT);
                        startMill = System.currentTimeMillis();
                        getContestDetails(true);
//                        CBit.getSocketUtils().getmSocket().emit(SocketUtils.EVENT_CONTEST_DETAILS, request, contestDetails);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (socketConnectionEvent.getMessage().equals("disconnected")) {
                   /* if(Utils.isNetworkAvailable(context)){
                        Utils.showToast(context,"Network Availalble");
                    }else{
                        Utils.showToast(context,"No Network Availalble");
                    }*/
                } else if (socketConnectionEvent.getMessage().equals("Error connecting")) {
                    if (customDialog == null) {
                        customDialog = new CustomDialog();
                        customDialog.displayProgress(context, getString(R.string.connecting));
                    } else {
                        if (!customDialog.progressDialog.isShowing())
                            customDialog.displayProgress(context, getString(R.string.connecting));
                    }
                }
            }
        });

    }

    private void getContestDetails(boolean isRecall) {
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        String request = "";
        try {
            jsonObject.put("contest_id", contest_id);
            //  jsonObject.put("isStart", !isRecall ? 1 : 0);
            jsonObject.put("isStart", 0);
            request = jsonObject.toString();
            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, getString(R.string.crypt_pass));
            data = request.getBytes(StandardCharsets.UTF_8);
            request = Base64.encodeToString(data, Base64.DEFAULT);
            PrintLog.e(TAG, "contestDetails request " + request);
            PrintLog.e(TAG, "contestDetails ID " + sessionUtil.getId());
            PrintLog.e(TAG, "contestDetails Token " + sessionUtil.getToken());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Call<ResponseBody> call = APIClient.getInstance()
                .contestDetails(sessionUtil.getToken(), sessionUtil.getId(), request);
        //.contestDetails(sessionUtil.getToken(),sessionUtil.getName(),contestId);
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                binding.pbProgress.setVisibility(View.GONE);
                binding.linearContent.setVisibility(View.VISIBLE);
                LogHelper.d(TAG, responseData);
                Gson gson = new Gson();
                cdm = gson.fromJson(responseData, ContestDetailsModel.class);

                Log.d(TAG, "success:---- " + cdm.getContent().getGameStatus());
                if (cdm.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {

                    binding.toolbarTitle.setText(cdm.getContent().getName());
                    ticketAdapter.setViewType(cdm.getContent().getType());
                    ticketAdapter.setGameStatus(cdm.getContent().getGameStatus());
                    ticketAdapter.setMinAns(cdm.getContent().getAnsRangeMin());
                    ticketAdapter.setMaxAns(cdm.getContent().getAnsRangeMax());
                    ticketAdapter.setGameType(cdm.getContent().getGame_type());
                    binding.gameNote.setText(cdm.getContent().getTitle());

                    String givenDateString = cdm.getContent().getStartDate();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    try {
                        Date mDate = sdf.parse(givenDateString);
                        timeInMilliseconds = mDate.getTime();
                        System.out.println("Date in milli :: " + timeInMilliseconds);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (cdm.getContent().getGameStatus().equalsIgnoreCase(Utils.GAME_END)) {
                        EventBus.getDefault().post(new UpdateMyContestEvent());
                        if (!isGoResult) {
                            isGoResult = true;
                            Intent intent = new Intent(context, GameResultActivity.class);
                            intent.putExtra(GameResultActivity.CONTEST_ID, contest_id);
                            intent.putExtra(GameResultActivity.IS_REMINDER, isReminderScreen);
                            startActivity(intent);
                            finishAffinity();
                        }
                    } else if (cdm.getContent().getGameStatus().equalsIgnoreCase(Utils.GAME_START)) {
                        callGameStart();
                    } else {
                        hideTimer(isRecall);
                    }
                }
            }

            @Override
            public void failure(String responseData) {

            }
        });
    }


    private void hideTimer(boolean isRecall) {
        binding.tvNineLockNow.setAlpha(.5f);
        binding.tvNineLockNow.setEnabled(false);
        //edit by ayaz 20-aug
        grayrdb();
        gray09();

        binding.tvText.setVisibility(View.GONE);
        binding.tvRemainingText.setVisibility(View.VISIBLE);
        ticketList.clear();
        for (Ticket ticket : cdm.getContent().getTickets()) {
            if (ticket.getIsPurchased() != 0) {
                ticketList.add(ticket);
            }
        }
        ticketAdapter.notifyDataSetChanged();
        bricksColorModel.clear();
        if (isRecall) {
            if (cdm.getContent().getLevel() == 0) {
                inItBricks(4, cdm.getContent().getGameMode(), cdm.getContent().getAnsRangeMin(), cdm.getContent().getAnsRangeMax());
            } else if (cdm.getContent().getLevel() == 1) {
                inItBricks(8, cdm.getContent().getGameMode(), cdm.getContent().getAnsRangeMin(), cdm.getContent().getAnsRangeMax());
            } else if (cdm.getContent().getLevel() == 2) {
                inItBricks(16, cdm.getContent().getGameMode(), cdm.getContent().getAnsRangeMin(), cdm.getContent().getAnsRangeMax());
            } else {
                inItBricks(32, cdm.getContent().getGameMode(), cdm.getContent().getAnsRangeMin(), cdm.getContent().getAnsRangeMax());
            }
            handler.removeCallbacks(runnable);
            isHandlerPost = handler.post(runnable);
        }

    }

    private void callGameStart() {
        Log.d("TAG", "BricksAdapter: callGameStart: ");
        binding.tvRemainingText.setVisibility(View.GONE);
        binding.tvText.setVisibility(View.VISIBLE);
        //Toast.makeText(context, "call start", Toast.LENGTH_SHORT).show();

        handler.removeCallbacks(runnable);

        ticketList.clear();
        for (Ticket ticket : cdm.getContent().getTickets()) {
            if (ticket.getIsPurchased() != 0) {
                ticketList.add(ticket);
            }

        }
        ticketAdapter.notifyDataSetChanged();

        /* Vishal Change */
        for (int i = 0; i < cdm.getContent().getTickets().size(); i++) {
            Ticket t = cdm.getContent().getTickets().get(i);
            UserSelect userSelect = new UserSelect();

            for (int j = 0; j < t.getSlotes().size(); j++) {
                Slote s = t.getSlotes().get(j);
                if (s.isIsSelected()) {
                    if (String.valueOf(s.getSelectValue()).length() > 0)
                        userSelect.setDisplayValue(s.getSelectValue());
                    else
                        userSelect.setDisplayValue(s.getDisplayValue());
                    userSelect.setStartValue(String.valueOf(s.getStartValue()));
                    userSelect.setEndValue(String.valueOf(s.getEndValue()));
                    userSelect.setSelectValue(String.valueOf(s.getSelectValue()));
                }
            }
            ticketList.get(i).setUserSelect(userSelect);
        }
        /* Vishal Change */

        for (int i = 0; i < ticketList.size(); i++) {
            if (ticketList.get(i).isIsLock()) {
                ticktCount = ticktCount + 1;
                ticketList.get(i).setMinValue(String.valueOf(ticketList.get(i).getUserSelect().getStartValue()));
                ticketList.get(i).setMaxValue(String.valueOf(ticketList.get(i).getUserSelect().getEndValue()));
                ticketList.get(i).setDisplayView(String.valueOf(ticketList.get(i).getUserSelect().getDisplayValue()));

            }
        }

        Log.d(TAG, "ticktCount: " + ticktCount);
        if (ticktCount == ticketList.size()) {
            isClick = true;
            binding.tvNineLockNow.setAlpha(0.5f);
            binding.tvNineLockNow.setEnabled(false);
            binding.tvLockTime.setVisibility(View.VISIBLE);
            binding.tvNineLockNow.setVisibility(View.GONE);
            /* vishal change */
            boolean flag = true;


            if (flag) {
                binding.tvLockTime.setText("Locked at: " + cdm.getContent().getLockAllData().get(0).getLockAllTime());
                for (Slote s : ticketList.get(0).getSlotes()) {
                    if (s.isIsSelected()) {
                        //  tvAnsSelection.setText(s.getSelectValue());
                        binding.tvAnsSelection1.setText(s.getSelectValue());
                    }
                }
                binding.tvAnsSelection1.setText(cdm.getContent().getLockAllData().get(0).getDisplayValue());
                colorrdb();
                color09();
                binding.imgLock.setVisibility(View.VISIBLE);
                if (constest_type.equalsIgnoreCase("rdb")) {
                    binding.gameNote.setText("Color with bigger total wins ");
                    binding.linRdb.setVisibility(View.VISIBLE);
                    binding.linNine.setVisibility(View.GONE);

                    if (cdm.getContent().getLockAllData().get(0).getDisplayValue().equalsIgnoreCase("Red win")) {
                        binding.rdRed.setChecked(true);
                    } else if (cdm.getContent().getLockAllData().get(0).getDisplayValue().equalsIgnoreCase("Draw")) {
                        binding.rdDraw.setChecked(true);
                    } else if (cdm.getContent().getLockAllData().get(0).getDisplayValue().equalsIgnoreCase("Blue win")) {
                        binding.rdBlue.setChecked(true);
                    }

                } else {
                    for (Slote s : ticketList.get(0).getSlotes()) {
                        if (s.isIsSelected()) {
                            switch (s.getSelectValue()) {
                                case "0":
                                    binding.rdZero.setChecked(true);
                                    break;
                                case "1":
                                    binding.rdOne.setChecked(true);
                                    break;
                                case "2":
                                    binding.rdTwo.setChecked(true);
                                    break;
                                case "3":
                                    binding.rdThree.setChecked(true);
                                    break;
                                case "4":
                                    binding.rdFour.setChecked(true);
                                    break;
                                case "5":
                                    binding.rdFive.setChecked(true);
                                    break;
                                case "6":
                                    binding.rdSix.setChecked(true);
                                    break;
                                case "7":
                                    binding.rdSeven.setChecked(true);
                                    break;
                                case "8":
                                    binding.rdEight.setChecked(true);
                                    break;
                                case "9":
                                    binding.rdNine.setChecked(true);
                                    break;
                            }
                            if (s.getStartValue() < 0)
                                binding.rdRed.setChecked(true);
                            else if (s.getStartValue() > 0)
                                binding.rdRed.setChecked(true);
                            else
                                binding.rdDraw.setChecked(true);
                        }
                    }
                }
            } else {
                binding.tvAnsSelection.setText("-");
                binding.tvAnsSelection1.setText("-");
                binding.tvLockTime.setVisibility(View.GONE);
                binding.imgLock.setVisibility(View.GONE);
            }
            /* vishal change */
            binding.rdBlue.setClickable(false);
            binding.rdDraw.setClickable(false);
            binding.rdRed.setClickable(false);
            binding.rdZero.setClickable(false);
            binding.rdOne.setClickable(false);
            binding.rdTwo.setClickable(false);
            binding.rdThree.setClickable(false);
            binding.rdFour.setClickable(false);
            binding.rdFive.setClickable(false);
            binding.rdSix.setClickable(false);
            binding.rdSeven.setClickable(false);
            binding.rdEight.setClickable(false);
            binding.rdNine.setClickable(false);
        } else {
            binding.tvNineLockNow.setAlpha(1f);
            binding.tvNineLockNow.setEnabled(true);
            //edit by ayaz 20-aug
            colorrdb();
            color09();
            binding.rdBlue.setClickable(true);
            binding.rdDraw.setClickable(true);
            binding.rdRed.setClickable(true);
            binding.rdZero.setClickable(true);
            binding.rdOne.setClickable(true);
            binding.rdTwo.setClickable(true);
            binding.rdThree.setClickable(true);
            binding.rdFour.setClickable(true);
            binding.rdFive.setClickable(true);
            binding.rdSix.setClickable(true);
            binding.rdSeven.setClickable(true);
            binding.rdEight.setClickable(true);
            binding.rdNine.setClickable(true);
        }
        binding.rdRdb.setOnCheckedChangeListener((group, checkedId) -> {
                    RadioButton radioButton = findViewById(checkedId);
                    SelectedDisplayView = String.valueOf(radioButton.getText());
                }
        );
        binding. tvNineLockNow.setOnClickListener(v -> {
            if (Utils.isNetworkAvailable(GameViewActivity.this)) {
                if (!cdm.getContent().getGameStatus().equals(Utils.GAME_NOT_START)) {
                    Log.d(TAG, "visibilitynine>>: " +binding. linNine.getVisibility());
                    Log.d(TAG, "visibilityrdb>>: " +binding. linRdb.getVisibility());
                    if (binding.linNine.getVisibility() == View.VISIBLE) {
                        if (binding.rdgZero.getCheckedRadioButtonId() == -1 &&binding. rdgFive.getCheckedRadioButtonId() == -1) {
                            Utils.showToast(context, "Please Select Any Number");
                        } else {
                            binding.   tvNineLockNow.setAlpha(.5f);
                            binding.  tvNineLockNow.setEnabled(false);
                            long temp = 30000 - lockMilliSec;
                            totalMill = timeInMilliseconds + temp;

                            setALLAnsLock(SelectedDisplayView);
                            binding. tvNineLockNow.setVisibility(View.GONE);
                            binding. tvLockTime.setVisibility(View.VISIBLE);

                            binding.  imgLock.setVisibility(View.VISIBLE);
                            waitingPopupforLock();
                        }
                    }
                    if (binding.linRdb.getVisibility() == View.VISIBLE) {
                        if (binding.rdRdb.getCheckedRadioButtonId() == -1) {
                            Utils.showToast(context, "Please Select Any Number");
                        } else {
                            binding. tvNineLockNow.setAlpha(.5f);
                            binding. tvNineLockNow.setEnabled(false);
                            long temp = 30000 - lockMilliSec;
                            totalMill = timeInMilliseconds + temp;
                            setALLAnsLock(SelectedDisplayView);
                            binding. imgLock.setVisibility(View.VISIBLE);
                            binding. tvNineLockNow.setVisibility(View.GONE);
                            binding.  tvLockTime.setVisibility(View.VISIBLE);
                            waitingPopupforLock();
                        }
                    }
                } else {
                    Utils.showToast(context, "Game Not Started");
                }
            } else {
                Utils.showToast(GameViewActivity.this, "Please check your internet connection.");

            }
        });

    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            inIt();
            handler.postDelayed(runnable, 500); // TODO: 05/08/20 800 to 500
        }
    };


    private void openInfoPopup() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_info);

        if (dialog.getWindow() != null) {
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            Window window = dialog.getWindow();
            lp.copyFrom(window.getAttributes());
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            lp.width = (int) (metrics.widthPixels * 0.90);
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);
        }
        LinearLayout linearRoot = dialog.findViewById(R.id.linearRoot);
        linearRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void inIt() {
        for (int i = 0; i < 4; i++) {
            HashMap<String, Integer> temp = bricksColorModel.get(bricksColorModel.size() - 1);
            bricksColorModel.remove(bricksColorModel.size() - 1);
            bricksColorModel.add(0, temp);
        }
        Collections.shuffle(bricksItems);
        bricksAdapter.notifyDataSetChanged();
    }

    public static int rand(int min, int max) {
        if (min > max || (max - min + 1 > Integer.MAX_VALUE)) {
            throw new IllegalArgumentException("Invalid range");
        }
        return new Random().nextInt(max - min + 1) + min;
    }

    private void inItBricks(int totalItem, String gameMode, int min, int max) {
        HashMap<String, Integer> map = new HashMap<>();
        if (totalItem == 4) {
            while (bricksItems.size() < 4) {
                bricksItems.add(Math.abs(rand(min, max)));
            }
            map.put("color", R.color.color_green);
            map.put("index", 0);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_blue);
            map.put("index", 1);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_green);
            map.put("index", 2);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_red);
            map.put("index", 3);
            bricksColorModel.add(map);


        } else if (totalItem == 8) {
            while (bricksItems.size() < 8) {
                bricksItems.add(Math.abs(rand(min, max)));
            }
            map.put("color", R.color.color_green);
            map.put("index", 0);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_blue);
            map.put("index", 1);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_green);
            map.put("index", 2);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_red);
            map.put("index", 3);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_blue);
            map.put("index", 4);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_green);
            map.put("index", 5);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_red);
            map.put("index", 6);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_green);
            map.put("index", 7);
            bricksColorModel.add(map);

        } else if (totalItem == 16) {

            while (bricksItems.size() < 16) {
                bricksItems.add(Math.abs(rand(min, max)));
            }

            map.put("color", R.color.color_green);
            map.put("index", 0);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_blue);
            map.put("index", 1);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_green);
            map.put("index", 2);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_red);
            map.put("index", 3);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_blue);
            map.put("index", 4);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_green);
            map.put("index", 5);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_red);
            map.put("index", 6);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_green);
            map.put("index", 7);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_green);
            map.put("index", 8);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_red);
            map.put("index", 9);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_green);
            map.put("index", 10);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_blue);
            map.put("index", 11);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_red);
            map.put("index", 12);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_green);
            map.put("index", 13);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_blue);
            map.put("index", 14);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_green);
            map.put("index", 15);
            bricksColorModel.add(map);
        } else {

            while (bricksItems.size() < 32) {
                bricksItems.add(Math.abs(rand(min, max)));
            }


            map.put("color", R.color.color_green);
            map.put("index", 0);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_blue);
            map.put("index", 1);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_green);
            map.put("index", 2);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_red);
            map.put("index", 3);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_blue);
            map.put("index", 4);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_green);
            map.put("index", 5);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_red);
            map.put("index", 6);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_green);
            map.put("index", 7);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_green);
            map.put("index", 8);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_red);
            map.put("index", 9);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_green);
            map.put("index", 10);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_blue);
            map.put("index", 11);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_red);
            map.put("index", 12);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_green);
            map.put("index", 13);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_blue);
            map.put("index", 14);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_green);
            map.put("index", 15);
            bricksColorModel.add(map);

            map.put("color", R.color.color_green);
            map.put("index", 16);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_blue);
            map.put("index", 17);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_green);
            map.put("index", 18);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_red);
            map.put("index", 19);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_blue);
            map.put("index", 20);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_green);
            map.put("index", 21);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_red);
            map.put("index", 22);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_green);
            map.put("index", 23);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_green);
            map.put("index", 24);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_red);
            map.put("index", 25);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_green);
            map.put("index", 26);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_blue);
            map.put("index", 27);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_red);
            map.put("index", 28);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_green);
            map.put("index", 29);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_blue);
            map.put("index", 30);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_green);
            map.put("index", 31);
            bricksColorModel.add(map);
        }
        Collections.shuffle(bricksItems);
        bricksAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
       /* if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
        }*/
        try {
            wl.release();
        } catch (Exception e) {
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
      /*  if (mPlayer != null && calculatingDialog != null) {
            if (calculatingDialog.isShowing())
                mPlayer.start();
        }*/
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
        if (!mSocket.connected()) {
            PrintLog.e(TAG, "mSocket Connected");
            // connectSocket();
        }
    }

    public void hasNoInternetConnection() {
        PrintLog.e(TAG, "Net No Connected");
        if (mSocket != null) {
            mSocket.off(EVENT_CONTEST_LIVE, onContestLive);
            mSocket.disconnect();
            mSocket.on(EVENT_CONTEST_LIVE, onContestLive);
            mSocket.connect();
        }
    }

    @Override
    protected void onDestroy() {
        try {
            if (mSocket != null) {
                mSocket.off(EVENT_CONTEST_LIVE, onContestLive);
                mSocket.disconnect();
                mSocket.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (calculatingDialog != null && calculatingDialog.isShowing()) {
                calculatingDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (isHandlerPost) {
            handler.removeCallbacks(runnable);
        }
     /*   if (mPlayer != null) {
            mPlayer.release();
        }*/
        if (mPlayer1 != null) {
            mPlayer1.release();
        }
        mTimer.cancel();

        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        if (CBit.getSocketUtils().getmSocket().connected()) {
            CBit.getSocketUtils().disConnect();
        }
       /* if (isMyServiceRunning(AlarmService.class)) {
            AlarmService.stopALram();
        }*/
        Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
        getApplicationContext().stopService(intentService);
        super.onDestroy();
    }

    ImageView ivAd;

    private void getAds() {
        Call<ResponseBody> call = APIClient.getInstance().getAds(sessionUtil.getToken(), sessionUtil.getId());
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, true, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Gson gson = new Gson();
                Log.e("TAG", "getAds " + responseData);
                AdvertiseModel am = gson.fromJson(responseData, AdvertiseModel.class);
                if (am.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    if (sessionUtil.getISAd() > 5) {
                        Glide.with(context)
                                .load(am.getContent().get(0).getImage())
                                .apply(Utils.getUserAvatarReques())
                                .into(ivAd);
                        sessionUtil.setISAd(sessionUtil.getISAd() + 1);
                    }
                }
            }

            @Override
            public void failure(String responseData) {
                Log.e("TAG", "Error " + responseData);
            }
        });
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
