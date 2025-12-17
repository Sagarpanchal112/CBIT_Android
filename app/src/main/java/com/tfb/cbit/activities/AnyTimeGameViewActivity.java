package com.tfb.cbit.activities;

import static com.tfb.cbit.utility.Utils.SOCKET_URI;

import android.app.Dialog;
import android.app.NotificationManager;
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
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

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
import com.tfb.cbit.models.anytime.AnyTimeJoinContest;
import com.tfb.cbit.models.anytimegame.Content;
import com.tfb.cbit.models.contestdetails.BoxJson;
import com.tfb.cbit.models.contestdetails.ContestDetailsModel;
import com.tfb.cbit.models.contestdetails.Slote;
import com.tfb.cbit.models.contestdetails.Ticket;
import com.tfb.cbit.models.contestdetails.UserSelect;
import com.tfb.cbit.models.updategame.UpdateAllGameModel;
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
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
import io.socket.engineio.client.transports.WebSocket;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class AnyTimeGameViewActivity extends BaseAppCompactActivity implements OnItemClickListener, OnRangeListener, OnSlotListener {
    private Socket mSocket;
    public boolean isClick = false;
    private static final String SOCKET_PATH = "/socket.io";
    private Context context;
    public BricksAdapter bricksAdapter;
    List<BoxJson> tempJsonList = new ArrayList<>();
    ArrayList<Integer> bricksItems = new ArrayList<>();
    ArrayList<HashMap<String, Integer>> bricksColorModel = new ArrayList<>();
    private SessionUtil sessionUtil;
    private String contest_id = "", constest_title = "", constest_type = "", tickets_id = "";
    public static final String CONTESTID = "contest_id";
    public static final String GAME_NO = "gameNo";
    public static final String CONTEST_PRIZE_ID = "contestPrizeId";
    public static final String CONTESTTITLE = "contest_title";
    public static final String CONTESTTYPE = "contest_type";
    private static final String TAG = AnyTimeGameViewActivity.class.getSimpleName();
    private Handler handler = new Handler();
    private boolean isHandlerPost = false;
    private TicketAdapter ticketAdapter = null;
    private List<Ticket> ticketList = new ArrayList<>();
    private ContestDetailsModel cdm = null;
    private CustomDialog customDialog;
    private MediaPlayer mPlayer = null;
    private Dialog calculatingDialog = null;
    private Dialog lockcalculatingDialog = null;
    private Dialog dialog = null;
    private boolean isReminderScreen = false;
    public Timer mTimer;
    private boolean isGameStart = false;
    private PowerManager.WakeLock wl;
    private long differenceSecond = 0;
    long startMill;
    String SelectedDisplayView = "";
    String startDisplayView = "";
    String endDisplayView = "";

    private static OkHttpClient okHttpClient;
    ConnectivityManager cm;
    NetworkInfo nInfo;
    public int gamestatusCount = 0;
    String CheckGameStatus;
    int ticktCount = 0;
    private NewApiCall newApiCall;
    CountDownTimer yourCountDownTimer;
    CountDownTimer finishCountDownTimer;
    public long countdown = 15000;
    private String contestId = "", gameNo = "", contestPrizeId = "";
    public boolean isLockAll = false;
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
        cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        nInfo = cm.getActiveNetworkInfo();
        binding.rvBricks.setVisibility(View.VISIBLE);
        configureSocketForSSL(buildSslSocketFactory());
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
        newApiCall = new NewApiCall();
        binding.rdgZero.clearCheck();
        binding.rdgFive.clearCheck();
        binding.rdgZero.setOnCheckedChangeListener(listener1);
        binding.rdgFive.setOnCheckedChangeListener(listener2);
        context = this;
        sessionUtil = new SessionUtil(context);
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        mSocket.connect();

        //  pbProgress.setVisibility(View.VISIBLE);
        binding.linearContent.setVisibility(View.VISIBLE);
        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(
                Context.POWER_SERVICE);
        this.wl = pm.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK
                        | PowerManager.ON_AFTER_RELEASE,
                TAG);
        wl.acquire();
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            finish();
        } else {
            contest_id = bundle.getString(CONTESTID, "");
            Log.i("contest_id", "=>" + contest_id);
            gameNo = bundle.getString(GAME_NO, "");
            contestPrizeId = bundle.getString(CONTEST_PRIZE_ID, "");

            constest_title = bundle.getString(CONTESTTITLE, "");
            constest_type = bundle.getString(CONTESTTYPE, "");
            tickets_id = bundle.getString("Tickets", "");
            if (contest_id.equalsIgnoreCase("")) {
                getIntent().getStringExtra(CONTESTID);
            }
            if (constest_title.equalsIgnoreCase("")) {
                getIntent().getStringExtra(CONTESTTITLE);
            }
            if (constest_type.equalsIgnoreCase("")) {
                getIntent().getStringExtra(CONTESTTYPE);
            }

            //  toolbar_title.setText(constest_title);
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
        }


        mPlayer = MediaPlayer.create(context, R.raw.timer);
        mPlayer.setLooping(true);

        binding.rvTickets.setLayoutManager(new LinearLayoutManager(context));
        if (binding.rvTickets.getItemAnimator() != null)
            ((SimpleItemAnimator) binding.rvTickets.getItemAnimator()).setSupportsChangeAnimations(false);
        ticketList.clear();
        ticketAdapter = new TicketAdapter(context, ticketList);
        ticketAdapter.setOnItemClickListener(this);
        ticketAdapter.setOnRangeListener(this);
        ticketAdapter.setOnSlotListener(this);
        binding.rvTickets.setAdapter(ticketAdapter);

        binding.rvBricks.setLayoutManager(new GridLayoutManager(context, 4));
        binding.rvBricksRes.setLayoutManager(new GridLayoutManager(context, 4));
        bricksAdapter = new BricksAdapter(context, bricksItems, bricksColorModel, tempJsonList);
        binding.rvBricks.setAdapter(bricksAdapter);
        inItBricks(8, "", 2, 98);
        // getContestDetails(true);
        binding.chkPause.setTag("0");
        binding.chkPause.setVisibility(View.GONE);
        binding.linPause.setVisibility(View.GONE);
        binding.chkPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.chkPause.getTag().equals("1")) {
                    setCountDown();
                    binding.tvPlay.setText("Pause");
                    binding.chkPause.setTag("0");
                } else {
                    yourCountDownTimer.cancel();
                    binding.tvPlay.setText("Play");
                    binding.chkPause.setTag("1");
                }

            }
        });
        //  getContestDetails(true);
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new CheckForConnection(), 0, 5 * 1000);
        getJoinContest(tickets_id);

        binding.ivBack.setOnClickListener(view -> {
            if (isReminderScreen) {
                Intent intent = new Intent(context, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                super.onBackPressed();
            }
        });
        binding.ivInfo.setOnClickListener(view -> {
            openInfoPopup();
        });


    }

    public int no_of_players = 0, pending = 0, played = 0;

    private List<Content> anyticketList = new ArrayList<>();

    public void setCountDown() {
        //  getJoinContest(tickets_id);
        yourCountDownTimer = new CountDownTimer(countdown, 1000) {
            public void onTick(long millisUntilFinished) {
                countdown = millisUntilFinished;
                binding.tvRemainingText.setText("Game Starts in: " + millisUntilFinished / 1000);
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                binding.linPause.setVisibility(View.GONE);
                CheckGameStatus = "start";
                callGameStart();
                if (cdm.getContent().getGame_type().equalsIgnoreCase("rdb")) {
                    binding.linRdb.setVisibility(View.VISIBLE);
                    binding.gameNote.setText("Color with bigger total wins ");
                    binding.linNine.setVisibility(View.GONE);
                } else if (cdm.getContent().getGame_type().equalsIgnoreCase("0-9")) {
                    binding.linRdb.setVisibility(View.GONE);
                    binding.gameNote.setText("Total of digits in blue - Total of digits in red");
                    binding.linNine.setVisibility(View.VISIBLE);
                }
            }
        }.start();
    }

    String contest_price_game_list;

    private void getJoinContest(final String ticketsIds) {
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        String request = "";
        try {
            jsonObject.put("contest_id", contest_id);
            jsonObject.put("tickets", ticketsIds);
            jsonObject.put("list", "[]");
            jsonObject.put("Randomlist", "[]");
            Log.d(TAG, "getJoinContest: " + contest_id + ">>" + ticketsIds);
            request = jsonObject.toString();
            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, getString(R.string.crypt_pass));
            data = request.getBytes(StandardCharsets.UTF_8);
            request = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Call<ResponseBody> call = APIClient.getInstance().AnyTimejoinContest(sessionUtil.getToken(), sessionUtil.getId(), request);

        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                DecimalFormat format = new DecimalFormat("0.##");
                Gson gson = new Gson();
                AnyTimeJoinContest joinContest = gson.fromJson(responseData, AnyTimeJoinContest.class);
                if (joinContest.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    sessionUtil.setAmount(String.valueOf(format.format(joinContest.getContent().getPbAmount())));
                    sessionUtil.setWAmount(String.valueOf(format.format(joinContest.getContent().getSbAmount())));
                    //Set Alarm
                    boolean isAlreadyPurchased = false;
                    for (Ticket ticket : ticketList) {
                        if (ticket.getIsPurchased() == 1) {
                            isAlreadyPurchased = true;
                        }
                    }
                    //   if (!isAlreadyPurchased) {

                    //  UpcomingContestModel upcomingContestModel = new UpcomingContestModel(cdm.getContent().getStartDate(), cdm.getContent().getCurrentTime(), 1, String.valueOf(cdm.getContent().getId()), cdm.getContent().getName(), cdm.getContent().getGame_type());
                    //  DatabaseHandler databaseHandler = new DatabaseHandler(context);
                    //  upcomingContestModel.setId(databaseHandler.addContest(upcomingContestModel));
                    ArrayList<String> stringArrayList = new ArrayList<>();
                    for (int i = 0; i < joinContest.getGamenumber().size(); i++) {
                        stringArrayList.add(joinContest.getGamenumber().get(i).getGame_no());
                    }
                    gameNo = TextUtils.join(",", stringArrayList);
                    contest_price_game_list = gson.toJson(joinContest.getGamenumber());
                    getContestDetails(true);
                    //  }
                } else {
                    Utils.showToast(context, joinContest.getMessage());
                }
            }

            @Override
            public void failure(String responseData) {

            }
        });
    }

    @Subscribe
    public void onGameAlertEvent(GameAlertEvent gameAlertEvent) {
        if (contest_id.equals(gameAlertEvent.getConetestId())) {
            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(Integer.parseInt(contest_id));
        }
    }

    @Subscribe
    public void onGameResultEvent(final GameResultEvent gameResultEvent) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (gameResultEvent.getResponse().equals(contest_id)) {
                    EventBus.getDefault().post(new UpdateUpcomingContestEvent());
                    EventBus.getDefault().post(new UpdateMyContestEvent());
               /*     new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mPlayer != null) {
                                mPlayer.release();
                                mPlayer = null;
                            }
                            try {
                                if (calculatingDialog != null && calculatingDialog.isShowing()) {
                                    calculatingDialog.dismiss();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            Intent intent = new Intent(context, AnyTimeGameHistoryActivity.class);
                            intent.putExtra(AnyTimeGameHistoryActivity.CONTEST_ID, String.valueOf(contest_id));
                            intent.putExtra(AnyTimeGameHistoryActivity.GAME_NO, gameNo + "");
                            intent.putExtra(AnyTimeGameHistoryActivity.CONTEST_PRIZE_ID, tickets_id + "");
                            intent.putExtra("contest_price_game_list", contest_price_game_list + "");
                            //   intent.putExtra(AnyTimeGameResultActivity.IS_REMINDER, isReminderScreen);
                            startActivity(intent);
                            finish();
                        }
                    }, 20000);
              */
                }
            }
        });

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
                getContestDetails(false);
                // CBit.getSocketUtils().getmSocket().emit(SocketUtils.EVENT_CONTEST_DETAILS, request, cdm);
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
    public void onUpdateUpcomingContestEvent(UpdateUpcomingContestEvent updateUpcomingContestEvent) {
        getContestDetails(true);
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

    public void colorrdb() {
        binding.rdBlue.setBackground(ContextCompat.getDrawable(context, R.drawable.price_group_sel_blue));
        binding.rdDraw.setBackground(ContextCompat.getDrawable(context, R.drawable.price_group_sel));
        binding.rdDraw.setTextColor(context.getResources().getColor(R.color.lightblue));
        binding.rdRed.setBackground(ContextCompat.getDrawable(context, R.drawable.price_group_sel_red));
    }

    public void grayrdb() {
        binding.rdBlue.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        binding.rdDraw.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        binding.rdDraw.setTextColor(context.getResources().getColor(R.color.white));
        binding.rdRed.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));

    }

    public void color09() {
        binding.rdZero.setBackground(ContextCompat.getDrawable(context, R.drawable.new_price_group_sel));
        binding.rdOne.setBackground(ContextCompat.getDrawable(context, R.drawable.new_price_group_sel));
        binding.rdTwo.setBackground(ContextCompat.getDrawable(context, R.drawable.new_price_group_sel));
        binding.rdThree.setBackground(ContextCompat.getDrawable(context, R.drawable.new_price_group_sel));
        binding.rdFour.setBackground(ContextCompat.getDrawable(context, R.drawable.new_price_group_sel));
        binding.rdFive.setBackground(ContextCompat.getDrawable(context, R.drawable.new_price_group_sel));
        binding.rdSix.setBackground(ContextCompat.getDrawable(context, R.drawable.new_price_group_sel));
        binding.rdSeven.setBackground(ContextCompat.getDrawable(context, R.drawable.new_price_group_sel));
        binding.rdEight.setBackground(ContextCompat.getDrawable(context, R.drawable.new_price_group_sel));
        binding.rdNine.setBackground(ContextCompat.getDrawable(context, R.drawable.new_price_group_sel));
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

    private Ack anytimeUpdateGameAll = new Ack() {
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

                            for (int i = 0; i < ugm.getContent().size(); i++) {
                                try {
                                    /* Vishal Change */
                                    for (int j = 0; j < ticketList.size(); j++) {
                                        /* Vishal Change if condition */
                                        if (ugm.getContent().get(i).getContestPriceId() == ticketList.get(j).getContestPriceId()) {
                                            System.err.println(j + " " + ticketList.get(j).getMinValue() + " " + ticketList.get(j).getMaxValue());
                                            if (ticketList.get(j).getMinValue().isEmpty()) {
                                                ticketList.get(j).setIsLock(ugm.getContent().get(i).isIsLock());
                                                ticketList.get(j).setLockTime(ugm.getContent().get(i).getIsLockTime());
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

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            if (lockcalculatingDialog != null && lockcalculatingDialog.isShowing()) {
                                lockcalculatingDialog.dismiss();
                            }

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    CheckGameStatus = "gameEnd";
                                    isLockAll = true;
                                    Intent intent = new Intent(context, AnyTimeGameHistoryActivity.class);
                                    intent.putExtra(AnyTimeGameHistoryActivity.CONTEST_ID, String.valueOf(contest_id));
                                    intent.putExtra(AnyTimeGameHistoryActivity.GAME_NO, gameNo + "");
                                    intent.putExtra(AnyTimeGameHistoryActivity.CONTEST_PRIZE_ID, tickets_id + "");
                                    intent.putExtra("contest_price_game_list", contest_price_game_list + "");
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            }, 5000);
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
                    setAnsLock(ticketList.get(position).getContestPriceId(), ticketList.get(position).getGame_no(), ticketList.get(position).getContestId(),
                            ticketList.get(position).getMinValue(),
                            ticketList.get(position).getMaxValue(),
                            position,
                            ticketList.get(position).getDisplayView());
                } else {
                    // Utils.showToast(context, "Please Select Any Number");
                }
            } else {
                Utils.showToast(context, "Game Not Started");
            }
        }
    }

    public int selectedPostion;

    private void setAnsLock(int contestPriceId, int gameNo, int contest_id, String startValue, String endValue, int position, String displayValue) {
        try {
            if (displayValue.equals("Red Win")) {
                displayValue = "Red";
            } else if (displayValue.equals("Draw")) {
                displayValue = "Draw";

            } else if (displayValue.equals("Blue Win")) {
                displayValue = "Blue";

            }
            JSONObject object = new JSONObject();
            object.put("userId", sessionUtil.getId());
            object.put("contestId", contest_id + "");
            object.put("contestPriceId", contestPriceId + "");
            object.put("gameNo", gameNo + "");
            object.put("startValue", startValue);
            object.put("endValue", endValue);
            object.put("IsLockAll", 0);
            selectedPostion = position;
         /*   object.put("isLock", 1);
            object.put("position", position);
         */
            object.put("DisplayValue", displayValue);
            byte[] data;
            String request = "";
            request = object.toString();
            Log.d(TAG, "setALLAnsLock: " + object.toString());
            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, context.getString(R.string.crypt_pass));
            data = request.getBytes(StandardCharsets.UTF_8);
            request = Base64.encodeToString(data, Base64.DEFAULT);
            CBit.getSocketUtils().getmSocket().emit(SocketUtils.EVENT_ANYUPDATE_GAME, request, updateGame);
            // getContestDetails(true);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                        UpdateAllGameModel ugm = gson.fromJson(res, UpdateAllGameModel.class);
                        if (ugm.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                            ticketList.get(selectedPostion).setIsLock(true);
                            ticketList.get(selectedPostion).setLockTime(ugm.getLockTime());
                            ticketList.get(selectedPostion).setDisplayView(ugm.getContent().get(0).getDisplayValue());
                            UserSelect userSelect = new UserSelect();
                            userSelect.setStartValue(String.valueOf(ugm.getContent().get(0).getDisplayValue()));
                            userSelect.setEndValue(String.valueOf(ugm.getContent().get(0).getDisplayValue()));
                            ticketList.get(selectedPostion).setUserSelect(userSelect);
                            PrintLog.e(TAG, "updateGame Result " + selectedPostion + "");
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

    private void setALLAnsLock(String displayValue) {
        try {
            if (displayValue.equalsIgnoreCase("Red win")) {
                startDisplayView = "-100";
                endDisplayView = "-1";
                displayValue = "Red";
            } else if (displayValue.equalsIgnoreCase("Draw")) {
                startDisplayView = "0";
                endDisplayView = "0";
                displayValue = "Draw";

            } else if (displayValue.equalsIgnoreCase("Blue win")) {
                startDisplayView = "1";
                endDisplayView = "100";
                displayValue = "Blue";

            }
            JSONObject object = new JSONObject();
            object.put("userId", sessionUtil.getId());
            object.put("contestId", contest_id);
            object.put("DisplayValue", displayValue);
            object.put("IsLockAll", "1");
            object.put("startValue", startDisplayView);
            object.put("endValue", endDisplayView);
            object.put("gameNo", gameNo);
            object.put("contestPriceId", tickets_id);

            byte[] data;
            String request = "";
            request = object.toString();
            Log.d(TAG, "setALLAnsLock: " + request);
            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, context.getString(R.string.crypt_pass));
            data = request.getBytes(StandardCharsets.UTF_8);
            request = Base64.encodeToString(data, Base64.DEFAULT);
            //   CBit.getSocketUtils().getmSocket().emit(SocketUtils.EVENT_UPDATE_GAMEALL, request, updateAllGame);
            CBit.getSocketUtils().getmSocket().emit(SocketUtils.EVENT_ANYUPDATE_GAMEALL, request, anytimeUpdateGameAll);
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

    private void getContestDetails(boolean isRecall) {
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        String request = "";
        try {
            jsonObject.put("contest_id", contest_id);
            jsonObject.put("GameNo", gameNo);
            jsonObject.put("contest_price_id", tickets_id);
            jsonObject.put("contest_price_game_list", contest_price_game_list);
            request = jsonObject.toString();
            PrintLog.e(TAG, "contestDetails request " + request);
            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, getString(R.string.crypt_pass));
            data = request.getBytes(StandardCharsets.UTF_8);
            request = Base64.encodeToString(data, Base64.DEFAULT);
            PrintLog.e(TAG, "contestDetails ID " + sessionUtil.getId());
            PrintLog.e(TAG, "contestDetails Token " + sessionUtil.getToken());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Call<ResponseBody> call = APIClient.getInstance()
                .contestDetailsAnyTimeGame(sessionUtil.getToken(), sessionUtil.getId(), request);
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

                    if (cdm.getContent().getGame_type().equals("rdb")) {
                        binding.toolbarTitle.setText("Which Colour has a bigger total?");
                        // tv_title.setText("Which Colour has a bigger total?");

                    } else {
                        binding.toolbarTitle.setText("Blue-Red");
                        //  tv_title.setText("Blue-Red");

                    }
                    ticketAdapter.setViewType(cdm.getContent().getType());
                    ticketAdapter.setGameStatus(cdm.getContent().getGameStatus());
                    ticketAdapter.setMinAns(cdm.getContent().getAnsRangeMin());
                    ticketAdapter.setMaxAns(cdm.getContent().getAnsRangeMax());
                    if (cdm.getContent().getGameStatus().equalsIgnoreCase(Utils.GAME_END)) {
                        Log.d(TAG, "success:---- else if GAME_END");
                        //   EventBus.getDefault().post(new UpdateMyContestEvent());
                       /* Intent intent = new Intent(context, AnyTimeGameHistoryActivity.class);
                        intent.putExtra(AnyTimeGameHistoryActivity.CONTEST_ID, String.valueOf(contest_id));
                        intent.putExtra(AnyTimeGameHistoryActivity.GAME_NO, gameNo + "");
                        intent.putExtra(AnyTimeGameHistoryActivity.CONTEST_PRIZE_ID, tickets_id + "");
                        intent.putExtra("contest_price_game_list", contest_price_game_list + "");
                        //   intent.putExtra(AnyTimeGameResultActivity.IS_REMINDER, isReminderScreen);
                        startActivity(intent);
                        finish();*/
                    } else if (cdm.getContent().getGameStatus().equalsIgnoreCase(Utils.GAME_START)) {
                        Log.d(TAG, "success:---- else if GAME START");
                        CheckGameStatus = cdm.getContent().getGameStatus();
                        callGameStart();
                        if (cdm.getContent().getGame_type().equalsIgnoreCase("rdb")) {
                            binding.   linRdb.setVisibility(View.VISIBLE);
                            binding.   gameNote.setText("Color with bigger total wins ");
                            binding.  linNine.setVisibility(View.GONE);
                        } else if (cdm.getContent().getGame_type().equalsIgnoreCase("0-9")) {
                            binding.  linRdb.setVisibility(View.GONE);
                            binding. gameNote.setText("Total of digits in blue - Total of digits in red");
                            binding. linNine.setVisibility(View.VISIBLE);
                        }
                        //  long mill = Utils.convertMillSeconds(cdm.getContent().getStartDate(),cdm.getContent().getCurrentTime());
                        //  differenceSecond=mills-mill;
                    } else if (cdm.getContent().getGameStatus().equalsIgnoreCase(Utils.GAME_NOT_START)) {
                        Log.d(TAG, "success:---- else if GAME_NOT_START");
                        //  ticketList.clear();
                        setCountDown();
                        hideTimer(isRecall);
                        // ticketList.addAll(CBit.selectedTicketList);
                        ticketAdapter.notifyDataSetChanged();
                        if (cdm.getContent().getGame_type().equalsIgnoreCase("rdb")) {
                            binding.  linRdb.setVisibility(View.VISIBLE);
                            binding.  gameNote.setText("Color with bigger total wins ");
                            binding.  linNine.setVisibility(View.GONE);
                        } else if (cdm.getContent().getGame_type().equalsIgnoreCase("0-9")) {
                            binding.  linRdb.setVisibility(View.GONE);
                            binding.  gameNote.setText("Total of digits in blue - Total of digits in red");
                            binding.  linNine.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Log.d(TAG, "success:---- else");

                        binding. rvBricks.setVisibility(View.VISIBLE);
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
        binding. tvNineLockNow.setAlpha(.5f);
        binding. tvNineLockNow.setEnabled(false);
        //edit by ayaz 20-aug
        grayrdb();
        gray09();

        //  tvText.setVisibility(View.GONE);
        //  tvRemainingText.setVisibility(View.VISIBLE);
        ticketList.clear();
        for (Ticket ticket : cdm.getContent().getTickets()) {
            if (ticket.getIsPurchased() != 0) {
                ticketList.add(ticket);
            }
        }
        Log.d(TAG, "success:---- hideTimer" + ticketList.size());

        ticketAdapter.notifyDataSetChanged();
        bricksColorModel.clear();
        if (isRecall) {
            if (cdm.getContent().getLevel() == 1) {
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
        binding. tvRemainingText.setVisibility(View.GONE);
        binding. tvText.setVisibility(View.VISIBLE);
        //Toast.makeText(context, "call start", Toast.LENGTH_SHORT).show();
        binding.tvNineLockNow.setAlpha(.5f);
        binding. tvNineLockNow.setEnabled(false);
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

        Log.i("CheckGameStatus", "==>" + CheckGameStatus);

        if (CheckGameStatus.equalsIgnoreCase(Utils.GAME_START)) {

            binding. rvBricks.setVisibility(View.GONE);
            binding. tvText.setVisibility(View.VISIBLE);
            binding. tvRemainingText.setVisibility(View.GONE);
            handler.removeCallbacks(runnable);
            if (finishCountDownTimer == null)
                finishCountDownTimer = new CountDownTimer(30000, 1000) {
                    public void onTick(long millisUntilFinished) {
                        binding. tvText.setText("" + millisUntilFinished / 1000);
                        //here you can have your logic to set text to edittext
                    }

                    public void onFinish() {
                        waitingPopupforLock();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (!isLockAll) {
                                    CheckGameStatus = "gameEnd";
                                    // EventBus.getDefault().post(new UpdateMyContestEvent());
                                    Intent intent = new Intent(context, AnyTimeGameHistoryActivity.class);
                                    intent.putExtra(AnyTimeGameHistoryActivity.CONTEST_ID, String.valueOf(contest_id));
                                    intent.putExtra(AnyTimeGameHistoryActivity.GAME_NO, gameNo + "");
                                    intent.putExtra(AnyTimeGameHistoryActivity.CONTEST_PRIZE_ID, tickets_id + "");
                                    intent.putExtra("contest_price_game_list", contest_price_game_list + "");
                                    //   intent.putExtra(AnyTimeGameResultActivity.IS_REMINDER, isReminderScreen);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        }, 5000);

                        //getContestDetails(true);
                    }
                }.start();
            List<BoxJson> boxJsonList = new ArrayList<>();
            for (int i = 0; i < cdm.getContent().getBoxJson().size(); i++) {
                BoxJson boxJson = new BoxJson();
                boxJson.setColor(cdm.getContent().getBoxJson().get(i).getColor());
                boxJson.setSymbol(cdm.getContent().getBoxJson().get(i).getSymbol());
                boxJson.setNumber(cdm.getContent().getBoxJson().get(i).getNumber());
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
            Log.d(TAG, "callGameStart: " + tempJsonList.size());
            Log.d(TAG, "callGameStart: " + tempJsonList.size());
            Log.d(TAG, "isClick: " + !isClick);

            if (!isClick) {
                binding.tvNineLockNow.setAlpha(1f);
                binding. tvNineLockNow.setEnabled(true);
                //edit by ayaz 20-aug
                colorrdb();
                color09();

                binding. rdBlue.setClickable(true);
                binding. rdDraw.setClickable(true);
                binding. rdRed.setClickable(true);

                binding. rdZero.setClickable(true);
                binding.  rdOne.setClickable(true);
                binding.  rdTwo.setClickable(true);
                binding.  rdThree.setClickable(true);
                binding.  rdFour.setClickable(true);
                binding.  rdFive.setClickable(true);
                binding.   rdSix.setClickable(true);
                binding.   rdSeven.setClickable(true);
                binding.  rdEight.setClickable(true);
                binding.  rdNine.setClickable(true);
            } else {
                /* Vishal Change */
                //edit by ayaz 20-aug
                colorrdb();
                color09();
                /* Vishal Change */

                binding. rdBlue.setClickable(false);
                binding.  rdDraw.setClickable(false);
                binding.  rdRed.setClickable(false);


                binding.  rdZero.setClickable(false);
                binding.  rdOne.setClickable(false);
                binding.  rdTwo.setClickable(false);
                binding. rdThree.setClickable(false);
                binding.  rdFour.setClickable(false);
                binding. rdFive.setClickable(false);
                binding. rdSix.setClickable(false);
                binding.  rdSeven.setClickable(false);
                binding.  rdEight.setClickable(false);
                binding.  rdNine.setClickable(false);
            }

            binding.rdRdb.setOnCheckedChangeListener((group, checkedId) -> {
                        RadioButton radioButton = findViewById(checkedId);
                        SelectedDisplayView = String.valueOf(radioButton.getText());


                    }
            );

            binding. tvNineLockNow.setOnClickListener(v -> {
                if (binding.linNine.getVisibility() == View.VISIBLE) {
                    if (binding.rdgZero.getCheckedRadioButtonId() == -1 && binding.rdgFive.getCheckedRadioButtonId() == -1) {
                        //   Utils.showToast(context, "Please Select Any Number");
                    } else {
                        isClick = true;
                        binding.tvNineLockNow.setAlpha(.5f);
                        binding. tvNineLockNow.setEnabled(false);
                        setALLAnsLock(SelectedDisplayView);
                        binding. imgLock.setVisibility(View.VISIBLE);
                        waitingPopupforLock();
                    }
                }
                if (binding.linRdb.getVisibility() == View.VISIBLE) {
                    if (binding.rdRdb.getCheckedRadioButtonId() == -1) {
                        //  Utils.showToast(context, "Please Select Any Number");
                    } else {
                        isClick = true;
                        binding.tvNineLockNow.setAlpha(.5f);
                        binding. tvNineLockNow.setEnabled(false);
                        setALLAnsLock(SelectedDisplayView);
                        binding.  imgLock.setVisibility(View.VISIBLE);
                        waitingPopupforLock();
                    }
                }
            });

            binding. rvBricksRes.setAdapter(new BricksAdapter(context, tempJsonList, true));
            binding. rvBricksRes.setVisibility(View.VISIBLE);
        } else {
            /* Vishal Change */
            grayrdb();
            gray09();
            /* Vishal Change */
            binding.tvText.setVisibility(View.GONE);
            binding. rvBricksRes.setVisibility(View.GONE);
            binding. rvBricks.setVisibility(View.VISIBLE);
            binding. tvRemainingText.setVisibility(View.VISIBLE);
        }


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
            binding.  tvNineLockNow.setAlpha(0.5f);
            binding.  tvNineLockNow.setEnabled(false);
            binding.  tvLockTime.setVisibility(View.VISIBLE);
            binding.  tvNineLockNow.setVisibility(View.GONE);
            binding. tvNineLockNow.setVisibility(View.GONE);
            boolean flag = true;

         /*   if (flag) {
               // tvLockTime.setText("Locked at: " + cdm.getContent().getLockAllData().get(0).getLockAllTime());
                for (Slote s : ticketList.get(0).getSlotes()) {
                    if (s.isIsSelected()) {
                        tvAnsSelection1.setText(s.getSelectValue());
                    }
                }
               // tvAnsSelection1.setText(cdm.getContent().getLockAllData().get(0).getDisplayValue());
                colorrdb();
                color09();
                imgLock.setVisibility(View.VISIBLE);
                if (constest_type.equalsIgnoreCase("rdb")) {
                    gameNote.setText("Color with bigger total wins ");
                    linRdb.setVisibility(View.VISIBLE);
                    linNine.setVisibility(View.GONE);

                    if (cdm.getContent().getLockAllData().get(0).getDisplayValue().equalsIgnoreCase("Red win")) {
                        rdRed.setChecked(true);
                    } else if (cdm.getContent().getLockAllData().get(0).getDisplayValue().equalsIgnoreCase("Draw")) {
                        rdDraw.setChecked(true);
                    } else if (cdm.getContent().getLockAllData().get(0).getDisplayValue().equalsIgnoreCase("Blue win")) {
                        rdBlue.setChecked(true);
                    }

                } else {
                    for (Slote s : ticketList.get(0).getSlotes()) {
                        if (s.isIsSelected()) {
                            switch (s.getSelectValue()) {
                                case "0":
                                    rdZero.setChecked(true);
                                    break;
                                case "1":
                                    rdOne.setChecked(true);
                                    break;
                                case "2":
                                    rdTwo.setChecked(true);
                                    break;
                                case "3":
                                    rdThree.setChecked(true);
                                    break;
                                case "4":
                                    rdFour.setChecked(true);
                                    break;
                                case "5":
                                    rdFive.setChecked(true);
                                    break;
                                case "6":
                                    rdSix.setChecked(true);
                                    break;
                                case "7":
                                    rdSeven.setChecked(true);
                                    break;
                                case "8":
                                    rdEight.setChecked(true);
                                    break;
                                case "9":
                                    rdNine.setChecked(true);
                                    break;
                            }
                            if (s.getStartValue() < 0)
                                rdRed.setChecked(true);
                            else if (s.getStartValue() > 0)
                                rdRed.setChecked(true);
                            else
                                rdDraw.setChecked(true);
                        }
                    }
                }
            } else {
                tvAnsSelection.setText("-");
                tvAnsSelection1.setText("-");
                tvLockTime.setVisibility(View.GONE);

                imgLock.setVisibility(View.GONE);
            }*/
            binding. rdBlue.setClickable(false);
            binding. rdDraw.setClickable(false);
            binding. rdRed.setClickable(false);
            binding. rdZero.setClickable(false);
            binding. rdOne.setClickable(false);
            binding.  rdTwo.setClickable(false);
            binding.  rdThree.setClickable(false);
            binding. rdFour.setClickable(false);
            binding. rdFive.setClickable(false);
            binding.  rdSix.setClickable(false);
            binding.  rdSeven.setClickable(false);
            binding.  rdEight.setClickable(false);
            binding.  rdNine.setClickable(false);
        } else {
            binding.  tvNineLockNow.setAlpha(1f);
            binding.  tvNineLockNow.setEnabled(true);
            colorrdb();
            color09();
            binding.  rdBlue.setClickable(true);
            binding. rdDraw.setClickable(true);
            binding. rdRed.setClickable(true);
            binding.  rdZero.setClickable(true);
            binding.  rdOne.setClickable(true);
            binding.   rdTwo.setClickable(true);
            binding.   rdThree.setClickable(true);
            binding.  rdFour.setClickable(true);
            binding. rdFive.setClickable(true);
            binding. rdSix.setClickable(true);
            binding.  rdSeven.setClickable(true);
            binding.  rdEight.setClickable(true);
            binding.  rdNine.setClickable(true);
        }
        binding. rdRdb.setOnCheckedChangeListener((group, checkedId) -> {
                    RadioButton radioButton = findViewById(checkedId);
                    SelectedDisplayView = String.valueOf(radioButton.getText());
                }
        );
        binding. tvNineLockNow.setOnClickListener(v -> {
            //     if (!cdm.getContent().getGameStatus().equals(Utils.GAME_NOT_START)) {
            Log.d(TAG, "visibilitynine>>: " +binding. linNine.getVisibility());
            Log.d(TAG, "visibilityrdb>>: " + binding.linRdb.getVisibility());
            if (binding.linNine.getVisibility() == View.VISIBLE) {
                if (binding.rdgZero.getCheckedRadioButtonId() == -1 && binding.rdgFive.getCheckedRadioButtonId() == -1) {
                    // Utils.showToast(context, "Please Select Any Number");
                } else {
                    binding. tvNineLockNow.setAlpha(.5f);
                    binding. tvNineLockNow.setEnabled(false);

                    setALLAnsLock(SelectedDisplayView);
                    binding. imgLock.setVisibility(View.VISIBLE);
                    waitingPopupforLock();
                }
            }
            if (binding.linRdb.getVisibility() == View.VISIBLE) {
                if (binding.rdRdb.getCheckedRadioButtonId() == -1) {
                    //  Utils.showToast(context, "Please Select Any Number");
                } else {
                    binding. tvNineLockNow.setAlpha(.5f);
                    binding. tvNineLockNow.setEnabled(false);
                    setALLAnsLock(SelectedDisplayView);
                    binding. imgLock.setVisibility(View.VISIBLE);
                    waitingPopupforLock();
                }
            }
          /*  } else {
                Utils.showToast(context, "Game Not Started");
            }*/
        });

    }

    /* ---------- End Method ----------*/
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
        if (totalItem == 8) {
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
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
        }

        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
        lockcalculatingDialog.dismiss();

        finishCountDownTimer.cancel();
        yourCountDownTimer.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPlayer != null && calculatingDialog != null) {
            if (calculatingDialog.isShowing())
                mPlayer.start();
        }
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
    }

    public void hasNoInternetConnection() {
        PrintLog.e(TAG, "Net No Connected");
        CBit.getSocketUtils().disConnect();
        CBit.getSocketUtils().connect();
    }

    @Subscribe()
    public void onSocketConnectionEvent(final SocketConnectionEvent socketConnectionEvent) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PrintLog.e(TAG, socketConnectionEvent.getMessage());
                if (CBit.getSocketUtils().getmSocket().connected()) {
                    if (customDialog != null)
                        customDialog.dismissProgress(context);
                    CBit.getSocketUtils().loginEmit(sessionUtil.getId());
                    PrintLog.e("TAG", "Game Socket Connect Socket ID " + CBit.getSocketUtils().getmSocket().id());
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

    @Override
    protected void onDestroy() {
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
        if (mPlayer != null) {
            mPlayer.release();
        }
        mTimer.cancel();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        wl.release();
        Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
        getApplicationContext().stopService(intentService);
        yourCountDownTimer.cancel();
        yourCountDownTimer.onFinish();
        super.onDestroy();
    }

}