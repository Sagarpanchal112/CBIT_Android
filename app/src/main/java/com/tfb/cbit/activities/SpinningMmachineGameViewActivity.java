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
import com.tfb.cbit.databinding.ActivityAboutUsBinding;
import com.tfb.cbit.databinding.ActivitySpinningMachineGameViewBinding;
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
import com.tfb.cbit.utility.CountDown;
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

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.transports.WebSocket;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;

import static com.tfb.cbit.utility.Utils.SOCKET_URI;

public class SpinningMmachineGameViewActivity extends AppCompatActivity implements OnItemClickListener, OnSlotListener, RollControll.StopListener {
    private static final String TAG = SpinningMmachineGameViewActivity.class.getSimpleName();
    private Context context;
    private SessionUtil sessionUtil;

    RollControll mRoll1;
    RollControll mRoll2;
    RollControll mRoll3;
    RollControll mRoll4;
    RollControll mRoll5;
    ArrayList<String> iamgesList;
    public String objectNo1 = "1", objectNo2 = "2", objectNo3 = "3", contest_id = "",
            constest_title = "", constest_type = "", SelectedDisplayView = "", CheckGameStatus = "", selectedImage = "";
    public int time = 0;
    public static final String CONTESTID = "contest_id", CONTESTTITLE = "contest_title", CONTESTTYPE = "contest_type";
    public int gamestatusCount = 0, ticktCount = 0;
    private long startMill, startTime, endTime, fileSize;
    private boolean isHandlerPost = false, isReminderScreen = false, isClick = false, isGameStart = false, setRoll = false;
    private Handler handler = new Handler();
    private ContestDetailsModel cdm = null;
    public List<BoxJson> boxJson = new ArrayList<>();
    public List<WinningOptions> winningOptionsList = new ArrayList<>();
    private List<Ticket> ticketList = new ArrayList<>();
    List<BoxJson> tempJsonList = new ArrayList<>();
    public SpinningTicketAdapter ticketAdapter;
    public SpiningOptionsAdapter optionsAdapter;
    public Socket mSocket;
    private static final String SOCKET_PATH = "/socket.io";
    private Dialog calculatingDialog = null;
    private Dialog lockcalculatingDialog = null;
    private Dialog dialog = null;
    public Timer mTimer;
    public static final String EVENT_CONTEST_LIVE = "onContestLive";
    OkHttpClient client = new OkHttpClient();
    private static OkHttpClient okHttpClient;
    ConnectivityManager cm;
    NetworkInfo nInfo;
    private MediaPlayer mPlayer1 = null;
    private MediaPlayer mPlayer = null;
    long lockMilliSec = 0, totalMill = 0;
    long timeInMilliseconds;
    private PowerManager.WakeLock wl;
    String SDCardPath = "";
    private CustomDialog customDialog;
    private CountDown remainingTime = null;

    private ActivitySpinningMachineGameViewBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);

        binding = ActivitySpinningMachineGameViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        sessionUtil = new SessionUtil(context);
        getFromSdcard();
        getBundelData();
        cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        nInfo = cm.getActiveNetworkInfo();
        SDCardPath = getFilesDir().getAbsolutePath() + "/";

        //  configureSocketForSSL();
        //
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new CheckForConnection(), 0, 5 * 1000);

        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        mPlayer1 = MediaPlayer.create(context, R.raw.waiting_timer);
        mPlayer1.setLooping(true);
        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(
                Context.POWER_SERVICE);
        this.wl = pm.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK
                        | PowerManager.ON_AFTER_RELEASE,
                TAG);
        wl.acquire();


        mRoll1 = new RollControll((ViewFlipper) findViewById(R.id.view_flipper_1), 3);
        mRoll2 = new RollControll((ViewFlipper) findViewById(R.id.view_flipper_2), 3);
        mRoll3 = new RollControll((ViewFlipper) findViewById(R.id.view_flipper_3), 3);
        mRoll4 = new RollControll((ViewFlipper) findViewById(R.id.view_flipper_4), 3);
        mRoll5 = new RollControll((ViewFlipper) findViewById(R.id.view_flipper_5), 3);
        setTickets();
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

        //  if (mSocket == null) {
        mSocket.on(EVENT_CONTEST_LIVE, onContestLive);
        mSocket.connect();
        CBit.getSocketUtils().connect();

        binding.ivBack.setOnClickListener(view -> {
            backPressed();
        });
        //  }
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
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
                        getApplicationContext().stopService(intentService);

                    }
                }, 3000);    //  stopAlramDiloag();
            } else {
                Intent intent = new Intent(context, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        }
        getContestDetails(false);

    }


    public void backPressed() {
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

    private void getContestDetails(final boolean isRecall) {
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        String request = "";
        try {
            jsonObject.put("contest_id", contest_id);
            // jsonObject.put("isStart", !isRecall ? 1 : 0);
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
        //.contestDetails(sessionUtil.getToken(),sessionUtil.getName(),contestId);
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                LogHelper.d(TAG, responseData);
                Gson gson = new Gson();
                cdm = gson.fromJson(responseData, ContestDetailsModel.class);
                ticketList.addAll(cdm.getContent().getTickets());
                binding.gameNote.setText(cdm.getContent().getTitle());
                binding.toolbarTitle.setText(cdm.getContent().getTitle());
                String givenDateString = cdm.getContent().getStartDate();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    Date mDate = sdf.parse(givenDateString);
                    timeInMilliseconds = mDate.getTime();
                    System.out.println("Date in milli :: " + timeInMilliseconds);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (ticketList.get(0).getSlotes().size() > 3) {
                    binding.rvOprions.setLayoutManager(new GridLayoutManager(context, 4));
                    optionsAdapter = new SpiningOptionsAdapter(context, ticketList.get(0).getSlotes(), CheckGameStatus);
                    binding.rvOprions.setAdapter(optionsAdapter);

                } else {
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                    linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                    binding.rvOprions.setLayoutManager(linearLayoutManager);
                    optionsAdapter = new SpiningOptionsAdapter(context, ticketList.get(0).getSlotes(), CheckGameStatus);
                    binding.rvOprions.setAdapter(optionsAdapter);

                }
                winningOptionsList.clear();
                boxJson.clear();
                boxJson.addAll(cdm.getContent().getBoxJson());
                winningOptionsList.addAll(cdm.getContent().getWinningOptions());
                int dumytimeCount = time - 30;

                ticketAdapter.setGameStatus(cdm.getContent().getGameStatus());
                if (cdm.getContent().getGameStatus().equalsIgnoreCase(Utils.GAME_END)) {
                    EventBus.getDefault().post(new UpdateMyContestEvent());
                    Log.i("Result", "==> Goes to api");
                    if (!isGoResult) {
                        isGoResult = true;
                        Intent intent = new Intent(context, SpinerGameResultActivity.class);
                        intent.putExtra(SpinerGameResultActivity.CONTEST_ID, contest_id);
                        intent.putExtra(SpinerGameResultActivity.IS_REMINDER, isReminderScreen);
                        startActivity(intent);
                        finishAffinity();
                    }
                } else if (cdm.getContent().getGameStatus().equalsIgnoreCase(Utils.GAME_START)) {
                    callGameStart();
                    //   connectSocket();
                    //  long mill = Utils.convertMillSeconds(cdm.getContent().getStartDate(),cdm.getContent().getCurrentTime());
                    //  differenceSecond=mills-mill;
                } else {
                    hideTimer(isRecall);
                }
            }

            @Override
            public void failure(String responseData) {

            }
        });
    }

    public void makeNewJson() {
        int rows = 5, min = -1000, max = 1000, step = 50;
    }

    private void hideTimer(boolean isRecall) {
        binding.tvLockNow.setAlpha(.5f);
        binding.tvLockNow.setEnabled(false);
        binding.tvText.setVisibility(View.GONE);
        binding.tvRemainingText.setVisibility(View.VISIBLE);
        ticketList.clear();
        for (Ticket ticket : cdm.getContent().getTickets()) {
            if (ticket.getIsPurchased() != 0) {
                ticketList.add(ticket);
            }
        }
        ticketAdapter.notifyDataSetChanged();

        if (isRecall) {
            if (cdm.getContent().getLevel() == 1) {
            } else if (cdm.getContent().getLevel() == 2) {
            } else {
            }
            handler.removeCallbacks(runnable);
            isHandlerPost = handler.post(runnable);
        }

    }

    public void setUpSlots() {

        if (tempJsonList.size() == 15) {
            slot3By5();
        } else if (tempJsonList.size() == 20) {
            slot4By5();
        } else if (tempJsonList.size() == 25) {
            slot5By5();
        }
        ticketAdapter.notifyDataSetChanged();


    }

    public void slot3By5() {
        setUpRecyclr(binding.rvI, 0, 2);
        setUpRecyclr(binding.rvIV, 3, 5);
        setUpRecyclr(binding.rvVII, 6, 8);
        setUpRecyclr(binding.rvX, 9, 11);
        setUpRecyclr(binding.rvXIII, 12, 14);

     /*   setUpRecyclr(rv_II, 12, 14);
        setUpRecyclr(rv_III, 9, 11);
        setUpRecyclr(rv_V, 6, 8);
        setUpRecyclr(rv_VI, 3, 5);
        setUpRecyclr(rv_VIII, 12, 14);
        setUpRecyclr(rv_IX, 0, 2);
        setUpRecyclr(rv_XI, 3, 5);
        setUpRecyclr(rv_XII, 6, 8);
        setUpRecyclr(rv_XIV, 0, 2);
        setUpRecyclr(rv_XV, 6, 8);
*/
    }

    public void slot4By5() {
        setUpRecyclr(binding.rvI, 0, 3);
        setUpRecyclr(binding.rvIV, 4, 7);
        setUpRecyclr(binding.rvVII, 8, 11);
        setUpRecyclr(binding.rvX, 12, 15);
        setUpRecyclr(binding.rvXIII, 16, 19);

        setUpRecyclr(binding.rvII, 16, 19);
        setUpRecyclr(binding.rvIII, 12, 15);
        setUpRecyclr(binding.rvV, 8, 11);
        setUpRecyclr(binding.rvVI, 4, 7);
        setUpRecyclr(binding.rvVIII, 12, 14);
        setUpRecyclr(binding.rvIX, 0, 3);
        setUpRecyclr(binding.rvXI, 4, 7);
        setUpRecyclr(binding.rvXII, 12, 15);
        setUpRecyclr(binding.rvXIV, 0, 2);
        setUpRecyclr(binding.rvXV, 8, 11);

    }

    public void slot5By5() {
        setUpRecyclr(binding.rvI, 0, 4);
        setUpRecyclr(binding.rvIV, 5, 9);
        setUpRecyclr(binding.rvVII, 10, 14);
        setUpRecyclr(binding.rvX, 15, 19);
        setUpRecyclr(binding.rvXIII, 20, 24);

        setUpRecyclr(binding.rvII, 20, 24);
        setUpRecyclr(binding.rvIII, 15, 19);
        setUpRecyclr(binding.rvV, 10, 14);
        setUpRecyclr(binding.rvVI, 5, 9);
        setUpRecyclr(binding.rvVIII, 0, 4);
        setUpRecyclr(binding.rvIX, 10, 14);
        setUpRecyclr(binding.rvXI, 0, 4);
        setUpRecyclr(binding.rvXII, 20, 24);
        setUpRecyclr(binding.rvXIV, 15, 19);
        setUpRecyclr(binding.rvXV, 5, 9);
    }

    public void setUpRecyclr(RecyclerView rv, int startPos, int endPos) {
        ArrayList<String> bricksItems = new ArrayList<>();
        // this is dynamic image load from local doenloaded logic
        String SDCardPath = getFilesDir().getAbsolutePath() + "/";
       /* for (int j = 0; j < winningOptionsList.size(); j++) {
            for (int i = startPos; i <= endPos; i++) {
                if (Integer.parseInt(boxJson.get(i).getNumber()) == winningOptionsList.get(j).getObjectNo()) {
                    bricksItems.add(SDCardPath + winningOptionsList.get(j).getImage());
                }
            }
        }*/

        //   for (int j = 0; j < boxJson.size(); j++) {
        for (int i = startPos; i <= endPos; i++) {
            bricksItems.add(SDCardPath + tempJsonList.get(i).getImage());
        }
        //    }
        rv.setLayoutManager(new LinearLayoutManager(context));
        viewFliperItemAdapter = new ViewFliperItemAdapter(context, bricksItems);
        rv.setAdapter(viewFliperItemAdapter);

    }

    public void FadinAnimaiton(RecyclerView img) {
        Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        img.startAnimation(aniFade);
    }

    private void startRoll() {
        int totalTime = (time - 30) * 100;
        mRoll1.start(600, 600, 0);
        mRoll2.start(600, 600, 0);
        mRoll3.start(600, 600, 0);
        mRoll4.start(600, 600, 0);
        mRoll5.start(600, 600, 0);
        mRoll1.setDuration(600);
        mRoll2.setDuration(600);
        mRoll3.setDuration(600);
        mRoll4.setDuration(600);
        mRoll5.setDuration(600);
    }

    public boolean isSetUpSlot = false;

    public void setRollDuration(String time) {
        if (time.equalsIgnoreCase("00:01")) {
            mRoll1.setStop(true);
            mRoll2.setStop(true);
            mRoll3.setStop(true);
            mRoll4.setStop(true);
            mRoll5.setStop(true);
            // isSetUpSlot = true;
            // setUpSlots();

        }
      /*  if (time.equalsIgnoreCase("00:05")) {
            mRoll1.setDuration(400);
            mRoll2.setDuration(400);
            mRoll3.setDuration(400);
            mRoll4.setDuration(400);
            mRoll5.setDuration(400);
        }
        if (time.equalsIgnoreCase("00:07")) {
            mRoll1.setDuration(400);
            mRoll2.setDuration(400);
            mRoll3.setDuration(400);
            mRoll4.setDuration(400);
            mRoll5.setDuration(400);
        }*/
      /*  if (time.equalsIgnoreCase("00:10")) {
            mRoll1.setStop(true);
            mRoll2.setStop(true);
            mRoll3.setStop(true);
            mRoll4.setStop(true);
            mRoll5.setStop(true);
        }*/
    }

    public void setTickets() {
        binding.rvTickets.setLayoutManager(new LinearLayoutManager(context));
        if (binding.rvTickets.getItemAnimator() != null)
            ((SimpleItemAnimator) binding.rvTickets.getItemAnimator()).setSupportsChangeAnimations(false);
        ticketAdapter = new SpinningTicketAdapter(context, ticketList, CheckGameStatus);
        binding.rvTickets.setAdapter(ticketAdapter);
        ticketAdapter.setOnItemClickListener(this);
        ticketAdapter.setOnSlotListener(this);
    }

    @Override
    public void onItemClick(View view, int position) {
        if (view.getId() == R.id.tvLockNow) {
            if (CheckGameStatus.equalsIgnoreCase(Utils.GAME_START)) {
                if (!ticketList.get(position).getMinValue().isEmpty()) {
                    setAnsLock(ticketList.get(position).getContestPriceId(),
                            ticketList.get(position).getMinValue(),
                            ticketList.get(position).getMaxValue(),
                            position,
                            ticketList.get(position).getDisplayView());
                } else {
                    Utils.showToast(context, "Please Select Any Item");
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
            // CBit.getSocketUtils().getmSocket().emit(SocketUtils.EVENT_UPDATE_GAME, request, updateGame);
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

        //   ticketAdapter.notifyItemChanged(parentPos);
    }

    public void connectSocket() {

    }

    public boolean isGameStartBool = false, isStartSlot = false;

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
                                binding.tvLockNow.setAlpha(.5f);
                                binding.tvLockNow.setEnabled(false);
                                binding.tvLockNow.setEnabled(false);
                                binding.tvRemainingText.setText("Game Starts in ");
                                binding.tvRemainingText.append("00:" + jsonObject.getString("time"));
                                time = jsonObject.getInt("gameTime");
                                int gameTime = 0;
                                gameTime = (jsonObject.getInt("gameTime"));

                                List<BoxJson> boxJsonList = new ArrayList<>();
                                for (int i = 0; i < jsonObject.getJSONObject("contest").getJSONArray("boxJson").length(); i++) {
                                    JSONObject object = jsonObject.getJSONObject("contest").getJSONArray("boxJson").getJSONObject(i);
                                    BoxJson boxJson = new BoxJson();
                                    boxJson.setColor(object.getString("color"));
                                    boxJson.setSymbol(object.getString("symbol"));
                                    boxJson.setNumber(object.getString("number"));
                                    boxJson.setImage(object.getString("Image"));
                                    boxJsonList.add(boxJson);
                                }

                                tempJsonList.clear();
                                tempJsonList.addAll(boxJsonList);
                                int dumytimeCount = time - 30;
                                Log.d(TAG, "dumytimeCount : " + dumytimeCount + "");
                               /* if (dumytimeCount > 0 && !setRoll) {

                                    setupSlotDumy();
                                    startRoll();
                                    setRoll = true;
                                }*/
                                if (!isGameStartBool) {
                                    setUpSlots();
                                }

                                if (dumytimeCount > 0 && !isStartSlot) {
                                    isStartSlot = true;

                                /*    remainingTime = new CountDown(timeInMilliseconds, 1000) {
                                        @Override
                                        public void onTick(final long l) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        setupSlotDumy();

                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });
                                        }

                                        @Override
                                        public void onFinish() {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    // onBackPressed();
                                                }
                                            });
                                        }
                                    };
                                    remainingTime.start();*/
                                }
                                setRollDuration(jsonObject.getString("time"));
                                if (jsonObject.getString("time").equalsIgnoreCase("00:16")) {
                                    try {
                                        mPlayer1.start();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                Log.d(TAG, "onContestLive: " + jsonObject.getJSONObject("contest").getString("gameStatus"));
                                if (jsonObject.has("contest")) {
                                    CheckGameStatus = jsonObject.getJSONObject("contest").getString("gameStatus");
                                    //Edit by Ayaz on 19-aug
                                    if (CheckGameStatus.equals(Utils.GAME_START)) {
                                        if (gamestatusCount == 0) {
                                            gamestatusCount = 1;
                                            optionsAdapter.setGameStatus(CheckGameStatus);
                                            optionsAdapter.notifyDataSetChanged();
                                            ticketAdapter.setGameStatus(CheckGameStatus);
                                            ticketAdapter.notifyDataSetChanged();
                                        } else {
                                            for (int i = 0; i < ticketList.size(); i++) {
                                                for (Slote sObj : ticketList.get(i).getSlotes()) {
                                                    if (sObj.isIsSelected()) {
                                                        ticketAdapter.setGameStatus(CheckGameStatus);
                                                        //  ticketAdapter.notifyItemChanged(i);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    if (!isGameStartBool && gameTime <= 30) {
                                        try {
                                            if (mPlayer1 != null) {
                                                mPlayer1.stop();
                                                mPlayer1.release();
                                                mPlayer1 = null;
                                            }
                                        } catch (Exception e) {

                                        }
                                        isGameStartBool = true;
                                       /* if (remainingTime != null) {
                                            remainingTime.cancel();
                                            remainingTime.onFinish();
                                        }*/
                                        // setUpSlots();
                                        viewFliperItemAdapter.setStatus(true);
                                        viewFliperItemAdapter.notifyDataSetChanged();

                                        // setupSlotDumy();
                                        CountDownTimer Count = new CountDownTimer(gameTime * 1000, 100) {
                                            public void onTick(long millisUntilFinished) {
                                                int seconds = (int) ((millisUntilFinished / 1000));
                                                //  tvText.setText(seconds + ":" + millisUntilFinished % 1000);
                                                binding.tvText.setText(seconds + "");
                                                lockMilliSec = millisUntilFinished;
                                            }

                                            public void onFinish() {
                                                binding.tvText.setText("00:000");
                                            }
                                        };
                                        Count.start();
                                    }
                                    if (jsonObject.getJSONObject("contest").getString("gameStatus").equalsIgnoreCase(Utils.GAME_START)) {

                                        try {
                                            if (mPlayer1 != null) {
                                                mPlayer1.stop();
                                                mPlayer1.release();
                                                mPlayer1 = null;
                                            }
                                        } catch (Exception e) {

                                        }
                                      /*  if (isMyServiceRunning(AlarmService.class)) {
                                            AlarmService.stopALram();
                                        }*/
                                        binding.tvText.setVisibility(View.VISIBLE);
                                        binding.tvRemainingText.setVisibility(View.GONE);
                                        handler.removeCallbacks(runnable);
                                        Log.d(TAG, "callGameStart: " + jsonObject.getString("time"));
                                        // tvText.setText(jsonObject.getString("gameTime"));
                                      /*  List<BoxJson> boxJsonList = new ArrayList<>();
                                        for (int i = 0; i < jsonObject.getJSONObject("contest").getJSONArray("boxJson").length(); i++) {
                                            JSONObject object = jsonObject.getJSONObject("contest").getJSONArray("boxJson").getJSONObject(i);
                                            BoxJson boxJson = new BoxJson();
                                            boxJson.setColor(object.getString("color"));
                                            boxJson.setSymbol(object.getString("symbol"));
                                            boxJson.setNumber(object.getString("number"));
                                            boxJson.setImage(object.getString("Image"));
                                            boxJsonList.add(boxJson);
                                        }

                                        tempJsonList.clear();
                                        tempJsonList.addAll(boxJsonList);*/

                                        optionsAdapter.setOnItemClickListener((view, position) -> {
                                            if (CheckGameStatus.equalsIgnoreCase(Utils.GAME_START)) {

                                            } else {
                                                Utils.showToast(context, "Game Not Started");
                                            }

                                            SelectedDisplayView = ticketList.get(0).getSlotes().get(position).getDisplayValue();
                                            selectedImage = ticketList.get(0).getSlotes().get(position).getImage();
                                        });

                                        binding.tvLockNow.setOnClickListener(v -> {
                                            if (Utils.isNetworkAvailable(SpinningMmachineGameViewActivity.this)) {
                                                if (CheckGameStatus.equalsIgnoreCase(Utils.GAME_START)) {
                                                    if (SelectedDisplayView.isEmpty()) {
                                                        Utils.showToast(context, "Please Select Any Item");
                                                    } else {
                                                        isClick = true;
                                                        optionsAdapter.setGameStatus(true);
                                                        binding.rvOprions.setAlpha(.5f);
                                                        binding.tvLockNow.setEnabled(false);
                                                        binding.rvOprions.setClickable(false);
                                                        binding.rvOprions.setEnabled(false);
                                                        binding.Oprions.setClickable(false);
                                                        binding.Oprions.setEnabled(false);
                                                        binding.Oprions.setClickable(false);
                                                        long temp = 30000 - lockMilliSec;
                                                        totalMill = timeInMilliseconds + temp;
                                                        setALLAnsLock(SelectedDisplayView);
                                                        binding.tvLockNow.setVisibility(View.GONE);
                                                        binding.tvLockTime.setVisibility(View.VISIBLE);
                                                        binding.imgLock.setVisibility(View.VISIBLE);
                                                        waitingPopupforLock();
                                                    }
                                                }
                                            } else {
                                                Utils.showToast(SpinningMmachineGameViewActivity.this, "Please check your internet connection.");
                                            }
                                        });
                                        if (!isClick) {
                                            binding.tvLockNow.setAlpha(1f);
                                            binding.tvLockNow.setEnabled(true);
                                            binding.rvOprions.setClickable(true);
                                            optionsAdapter.setGameStatus(false);
                                        } else {
                                            optionsAdapter.setGameStatus(true);
                                            binding.rvOprions.setClickable(false);
                                            binding.rvOprions.setEnabled(false);
                                            binding.Oprions.setClickable(false);
                                            binding.Oprions.setEnabled(false);
                                        }
                                    } else {
                                        binding.tvText.setVisibility(View.GONE);
                                        binding.linBrickes.setVisibility(View.VISIBLE);
                                        binding.tvRemainingText.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                }

                            }
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

    private void callGameStart() {
        binding.tvRemainingText.setVisibility(View.GONE);
        binding.tvText.setVisibility(View.VISIBLE);
        handler.removeCallbacks(runnable);
        ticketList.clear();
        for (Ticket ticket : cdm.getContent().getTickets()) {
            if (ticket.getIsPurchased() != 0) {
                ticketList.add(ticket);
            }
        }
        ticketAdapter.notifyDataSetChanged();
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
                    userSelect.setSelectValue(String.valueOf(s.getImage()));
                }
            }
            ticketList.get(i).setUserSelect(userSelect);
        }
        for (int i = 0; i < ticketList.size(); i++) {
            if (ticketList.get(i).isIsLock()) {
                ticktCount = ticktCount + 1;
                ticketList.get(i).setMinValue(String.valueOf(ticketList.get(i).getUserSelect().getStartValue()));
                ticketList.get(i).setMaxValue(String.valueOf(ticketList.get(i).getUserSelect().getEndValue()));
                ticketList.get(i).setDisplayView(String.valueOf(ticketList.get(i).getUserSelect().getDisplayValue()));

            }
        }

        if (ticktCount == ticketList.size()) {
            isClick = true;
            binding.tvLockNow.setAlpha(0.5f);
            binding.tvLockNow.setEnabled(false);
            binding.tvLockTime.setVisibility(View.VISIBLE);
            binding.tvLockNow.setVisibility(View.GONE);
            boolean flag = true;


            if (flag) {
                binding.tvLockNow.setText("Locked");
                binding.tvLockTime.setText("Locked at: " + cdm.getContent().getLockAllData().get(0).getLockAllTime());
                for (Slote s : ticketList.get(0).getSlotes()) {
                    if (s.isIsSelected()) {
                        //  tvAnsSelection.setText(s.getSelectValue());
                        binding.tvAnsSelection.setText(s.getSelectValue());
                        binding.tvAnsSelection.setVisibility(View.GONE);
                        binding.imgAnsSelection.setVisibility(View.VISIBLE);
                        Glide.with(context)
                                .load(SDCardPath + s.getImage())
                                .into(binding.imgAnsSelection);
                    }
                }
                //   tvAnsSelection.setText(cdm.getContent().getLockAllData().get(0).getDisplayValue());

                binding.imgLock.setVisibility(View.VISIBLE);
                if (cdm.getContent().getLockAllData().get(0).getDisplayValue().equalsIgnoreCase("Red win")) {
                } else if (cdm.getContent().getLockAllData().get(0).getDisplayValue().equalsIgnoreCase("Draw")) {
                } else if (cdm.getContent().getLockAllData().get(0).getDisplayValue().equalsIgnoreCase("Blue win")) {
                }
            } else {
                binding.tvAnsSelection.setText("-");
                binding.tvAnsSelection.setText("-");
                binding.tvLockTime.setVisibility(View.GONE);

                binding.imgLock.setVisibility(View.GONE);
            }
            optionsAdapter.setGameStatus(true);
            binding.rvOprions.setClickable(false);
            binding.rvOprions.setEnabled(false);
            binding.Oprions.setClickable(false);
            binding.Oprions.setEnabled(false);
        } else {
            binding.tvLockNow.setAlpha(1f);
            binding.tvLockNow.setEnabled(true);
            binding.rvOprions.setClickable(true);
        }


        optionsAdapter.setOnItemClickListener((view, position) -> {
            if (CheckGameStatus.equalsIgnoreCase(Utils.GAME_START)) {

            } else {
                Utils.showToast(context, "Game Not Started");
            }

            selectedImage = ticketList.get(0).getSlotes().get(position).getImage();
            SelectedDisplayView = ticketList.get(0).getSlotes().get(position).getDisplayValue();
        });

        binding.tvLockNow.setOnClickListener(v -> {
            if (Utils.isNetworkAvailable(SpinningMmachineGameViewActivity.this)) {
                if (CheckGameStatus.equalsIgnoreCase(Utils.GAME_START)) {
                    if (!SelectedDisplayView.isEmpty()) {
                        isClick = true;
                        binding.rvOprions.setAlpha(.5f);
                        binding.rvOprions.setClickable(false);
                        binding.rvOprions.setEnabled(false);
                        optionsAdapter.setGameStatus(true);
                        binding.Oprions.setClickable(false);
                        binding.Oprions.setEnabled(false);
                        binding.tvLockNow.setEnabled(false);
                        setALLAnsLock(SelectedDisplayView);
                        binding.imgLock.setVisibility(View.VISIBLE);
                        waitingPopupforLock();
                    } else {
                        Utils.showToast(context, "Please Select Any Item");
                    }
                }
            } else {
                Utils.showToast(SpinningMmachineGameViewActivity.this, "Please check your internet connection.");

            }
        });

    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(runnable, 500);
        }
    };

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

    private void setALLAnsLock(String displayValue) {
        try {
            JSONObject object = new JSONObject();
            object.put("userId", sessionUtil.getId());
            object.put("contestId", contest_id);
            object.put("DisplayValue", displayValue);
            object.put("lock_time", getDate(totalMill, "yyyy-MM-dd HH:mm:ss.SSSS"));
            object.put("isLock", 1);

            byte[] data;
            String request = "";
            request = object.toString();
            Log.d(TAG, "setALLAnsLock: " + object.toString());
            Log.d(TAG, "setALLAnsLock: " + request);
            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, context.getString(R.string.crypt_pass));
            data = request.getBytes(StandardCharsets.UTF_8);
            request = Base64.encodeToString(data, Base64.DEFAULT);
//            getContestDetails();
//            getContestDetails();
            //  CBit.getSocketUtils().getmSocket().emit(SocketUtils.EVENT_UPDATE_GAMEALL, request, updateAllGame);
            mSocket.emit(SocketUtils.EVENT_UPDATE_GAMEALL, request, updateAllGame);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getDate(long milliSeconds, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
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

                            binding.  rvOprions.setClickable(false);
                            binding.  rvOprions.setEnabled(false);
                            binding. Oprions.setClickable(false);
                            binding. Oprions.setEnabled(false);
                            binding. rvTickets.setClickable(false);
                            binding. imgLock.setVisibility(View.VISIBLE);
                            binding.  tvLockTime.setVisibility(View.VISIBLE);
                            binding.  tvLockTime.setText("Locked at: " + ugm.getLockTime());
                            binding.  tvLockNow.setText("Locked");
                            binding.  tvAnsSelection.setText(SelectedDisplayView);
                            binding.  tvAnsSelection.setVisibility(View.GONE);
                            Log.d(TAG, "success: " + ugm.getContent().get(0).getIsLockTime() + ">>>" + SelectedDisplayView);
                            Log.d(TAG, "sizee: " + ugm.getContent().size());
                            binding. imgAnsSelection.setVisibility(View.VISIBLE);
                            Glide.with(context)
                                    .load(SDCardPath + selectedImage)
                                    .into(binding.imgAnsSelection);

                            for (int i = 0; i < ugm.getContent().size(); i++) {
                                try {
                                    /* Vishal Change */
                                    for (int j = 0; j < ticketList.size(); j++) {
                                        /* Vishal Change if condition */
                                        if (ugm.getContent().get(i).getContestPriceId() == ticketList.get(j).getContestPriceId()) {
                                            System.err.println(j + " " + ticketList.get(j).getMinValue() + " " + ticketList.get(j).getMaxValue());
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
                                            ticketAdapter.notifyItemChanged(i);
                                            break;
                                        }
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            if (lockcalculatingDialog != null && lockcalculatingDialog.isShowing()) {
                                lockcalculatingDialog.dismiss();
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
    public void onStop() {
        super.onStop();
    }

    ImageView ivAd;

    private void waitingPopup() {
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
            //   getAds();
        }

        calculatingDialog.show();
    }

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

    public boolean isGoResult = false;

    @Subscribe
    public void onUpdateUpcomingContestEvent(UpdateUpcomingContestEvent updateUpcomingContestEvent) {
        waitingPopup();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
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
                }
            }
        }, 10000);
    }

    @Subscribe
    public void onGameResultEvent(final GameResultEvent gameResultEvent) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (gameResultEvent.getResponse().equals(contest_id)) {
                    // EventBus.getDefault().post(new UpdateUpcomingContestEvent());
                    //  EventBus.getDefault().post(new UpdateMyContestEvent());
                }
            }
        });

    }


    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (dialog != null && dialog.isShowing()) {
                Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
                getApplicationContext().stopService(intentService);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            wl.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
       /* if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
        }*/
        if (mPlayer1 != null && mPlayer1.isPlaying()) {
            mPlayer1.pause();
        }
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
    }

    private void stopPlaying() {
        if (mPlayer1 != null) {
            mPlayer1.stop();
            mPlayer1.release();
            mPlayer1 = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
      /*  if (mPlayer != null && calculatingDialog != null) {
            if (calculatingDialog.isShowing())
             //   mPlayer.start();
        }*/
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
        if (CBit.getSocketUtils().getmSocket().connected()) {
            CBit.getSocketUtils().disConnect();
        }
        try {
            if (calculatingDialog != null && calculatingDialog.isShowing()) {
                calculatingDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (remainingTime != null) {
            remainingTime.cancel();
        }
        try {
            if (isHandlerPost) {
                handler.removeCallbacks(runnable);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            /*if (mPlayer != null) {
                mPlayer.release();
            }*/

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (EventBus.getDefault().isRegistered(this))
                EventBus.getDefault().unregister(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

       /* if (isMyServiceRunning(AlarmService.class)) {
            AlarmService.stopALram();
        }*/
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

    public void setupSlotDumy() {
        if (tempJsonList.size() == 15) {
            slot3By5Dumy();
        } else if (tempJsonList.size() == 20) {
            slot4By5Dumy();
        } else if (tempJsonList.size() == 25) {
            slot5By5Dumy();
        }
        //  FadinAnimaiton(rv_I);
        ///  FadinAnimaiton(rv_IV);
        //  FadinAnimaiton(rv_VII);
        //  FadinAnimaiton(rv_X);
        //  FadinAnimaiton(rv_XIII);
    }

    public void setupRollSlotDumy() {
        if (tempJsonList.size() == 15) {
            slot3By5Dumy();
        } else if (tempJsonList.size() == 20) {
            slot4By5Dumy();
        } else if (tempJsonList.size() == 25) {
            slot5By5Dumy();
        }

    }

    public void slot3By5Dumy() {
        setUpRecyclrDumy(binding.rvI, 0, 2);
        setUpRecyclrDumy(binding.rvIV, 3, 5);
        setUpRecyclrDumy(binding.rvVII, 6, 8);
        setUpRecyclrDumy(binding.rvX, 9, 11);
        setUpRecyclrDumy(binding.rvXIII, 12, 14);
        setUpRecyclrDumy(binding.rvII, 12, 14);
        setUpRecyclrDumy(binding.rvIII, 9, 11);
        setUpRecyclrDumy(binding.rvV, 6, 8);
        setUpRecyclrDumy(binding.rvVI, 3, 5);
        setUpRecyclrDumy(binding.rvVIII, 12, 14);
        setUpRecyclrDumy(binding.rvIX, 0, 2);
        setUpRecyclrDumy(binding.rvXI, 3, 5);
        setUpRecyclrDumy(binding.rvXII, 6, 8);
        setUpRecyclrDumy(binding.rvXIV, 0, 2);
        setUpRecyclrDumy(binding.rvXV, 6, 8);
    }

    public void slot4By5Dumy() {
    /*    setUpRecyclr(rv_I, 0, 3);
        setUpRecyclr(rv_IV, 4, 7);
        setUpRecyclr(rv_VII, 8, 11);
        setUpRecyclr(rv_X, 12, 15);
        setUpRecyclr(rv_XIII, 16, 19);
     */
        setUpRecyclrDumy(binding.rvI, 0, 3);
        setUpRecyclrDumy(binding.rvIV, 4, 7);
        setUpRecyclrDumy(binding.rvVII, 8, 11);
        setUpRecyclrDumy(binding.rvX, 12, 15);
        setUpRecyclrDumy(binding.rvXIII, 16, 19);
        setUpRecyclrDumy(binding.rvII, 16, 19);
        setUpRecyclrDumy(binding.rvIII, 12, 15);
        setUpRecyclrDumy(binding.rvV, 8, 11);
        setUpRecyclrDumy(binding.rvVI, 4, 7);
        setUpRecyclrDumy(binding.rvVIII, 12, 14);
        setUpRecyclrDumy(binding.rvIX, 0, 3);
        setUpRecyclrDumy(binding.rvXI, 4, 7);
        setUpRecyclrDumy(binding.rvXII, 12, 15);
        setUpRecyclrDumy(binding.rvXIV, 0, 2);
        setUpRecyclrDumy(binding.rvXV, 8, 11);
    }

    public void slot5By5Dumy() {
        setUpRecyclrDumy(binding.rvI, 0, 4);
        setUpRecyclrDumy(binding.rvIV, 5, 9);
        setUpRecyclrDumy(binding.rvVII, 10, 14);
        setUpRecyclrDumy(binding.rvX, 15, 19);
        setUpRecyclrDumy(binding.rvXIII, 20, 24);
        setUpRecyclrDumy(binding.rvII, 20, 24);
        setUpRecyclrDumy(binding.rvIII, 15, 19);
        setUpRecyclrDumy(binding.rvV, 10, 14);
        setUpRecyclrDumy(binding.rvVI, 5, 9);
        setUpRecyclrDumy(binding.rvVIII, 0, 4);
        setUpRecyclrDumy(binding.rvIX, 10, 14);
        setUpRecyclrDumy(binding.rvXI, 0, 4);
        setUpRecyclrDumy(binding.rvXII, 20, 24);
        setUpRecyclrDumy(binding.rvXIV, 15, 19);
        setUpRecyclrDumy(binding.rvXV, 5, 9);

    }

    ViewFliperItemAdapter viewFliperItemAdapter;

    public void setUpRecyclrDumy(RecyclerView rv, int startPos, int endPos) {
        ArrayList<String> bricksItems = new ArrayList<>();
        // this is dynamic image load from local doenloaded logic
        String SDCardPath = getFilesDir().getAbsolutePath() + "/";
       /* for (int i = startPos; i <= endPos; i++) {
            bricksItems.add(iamgesList.get(new Random().nextInt(iamgesList.size())));
        }*/
        for (int i = startPos; i <= endPos; i++) {
            bricksItems.add(SDCardPath + winningOptionsList.get(new Random().nextInt(winningOptionsList.size())).getImage());
        }
        rv.setLayoutManager(new LinearLayoutManager(context));
        viewFliperItemAdapter = new ViewFliperItemAdapter(context, bricksItems);
        rv.setAdapter(viewFliperItemAdapter);
        viewFliperItemAdapter.setStatus(false);

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