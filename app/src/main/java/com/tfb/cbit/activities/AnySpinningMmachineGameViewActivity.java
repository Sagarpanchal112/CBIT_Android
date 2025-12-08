package com.tfb.cbit.activities;

import static com.tfb.cbit.utility.Utils.SOCKET_URI;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
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
import androidx.appcompat.widget.AppCompatCheckBox;
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
import com.tfb.cbit.databinding.ActivityAnySpinningMachineGameViewBinding;
import com.tfb.cbit.event.GameResultEvent;
import com.tfb.cbit.event.UpdateMyContestEvent;
import com.tfb.cbit.event.UpdateUpcomingContestEvent;
import com.tfb.cbit.interfaces.OnItemClickListener;
import com.tfb.cbit.interfaces.OnSlotListener;
import com.tfb.cbit.models.AnyTimeSpinningCatList;
import com.tfb.cbit.models.anytime.AnyTimeJoinContest;
import com.tfb.cbit.models.contestdetails.BoxJson;
import com.tfb.cbit.models.contestdetails.ContestDetailsModel;
import com.tfb.cbit.models.contestdetails.Slote;
import com.tfb.cbit.models.contestdetails.Ticket;
import com.tfb.cbit.models.contestdetails.UserSelect;
import com.tfb.cbit.models.contestdetails.WinningOptions;
import com.tfb.cbit.models.updategame.UpdateAllGameModel;
import com.tfb.cbit.services.AlarmService;
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;

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

public class AnySpinningMmachineGameViewActivity extends AppCompatActivity implements OnItemClickListener, OnSlotListener, RollControll.StopListener {

    private static final String TAG = AnySpinningMmachineGameViewActivity.class.getSimpleName();
    private Context context;
    private SessionUtil sessionUtil;
    RollControll mRoll1;
    RollControll mRoll2;
    RollControll mRoll3;
    RollControll mRoll4;
    RollControll mRoll5;
    ArrayList<String> iamgesList;
    public String objectNo1 = "1", objectNo2 = "2", objectNo3 = "3", contest_id = "",
            constest_title = "", constest_type = "", SelectedDisplayView = "", SelectedDisplayViewString = "", selectedImage = "", CheckGameStatus = "", tickets_id = "";
    private String contestId = "";
    private String gameNo = "", contestPrizeId = "";
    private String lst = "";
    private String random_lst = "";

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
    private Socket mSocket;
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
    private MediaPlayer mPlayer = null;
    CountDownTimer yourCountDownTimer;
    CountDownTimer finishCountDownTimer;
    public long countdown = 15000;
    private NewApiCall newApiCall;
    String SDCardPath = Environment.getExternalStorageDirectory() + "/.cbit/";
    private ActivityAnySpinningMachineGameViewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);

        binding = ActivityAnySpinningMachineGameViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        newApiCall = new NewApiCall();
        mRoll1 = new RollControll((ViewFlipper) findViewById(R.id.view_flipper_1), 3);
        mRoll2 = new RollControll((ViewFlipper) findViewById(R.id.view_flipper_2), 3);
        mRoll3 = new RollControll((ViewFlipper) findViewById(R.id.view_flipper_3), 3);
        mRoll4 = new RollControll((ViewFlipper) findViewById(R.id.view_flipper_4), 3);
        mRoll5 = new RollControll((ViewFlipper) findViewById(R.id.view_flipper_5), 3);
        getFromSdcard();
        cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        nInfo = cm.getActiveNetworkInfo();
        sessionUtil = new SessionUtil(context);
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        getBundelData();
        binding.linPause.setVisibility(View.GONE);

        configureSocketForSSL();

        connectSocket();

        //    getContestDetails(true);
        setCountDown();
     //   hideTimer(true);
        setupSlotDumy();

        getJoinContest(tickets_id);

        setRoll = true;
        binding.chkPause.setTag("0");
        binding.chkPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.chkPause.getTag().equals("1")) {
                    setCountDown();
                    mRoll1.setStop(false);
                    mRoll2.setStop(false);
                    mRoll3.setStop(false);
                    mRoll4.setStop(false);
                    mRoll5.setStop(false);
                    startRoll();
                    binding.tvPlay.setText("Pause");
                    binding.chkPause.setTag("0");
                } else {
                    yourCountDownTimer.cancel();
                    binding.tvPlay.setText("Play");
                    binding.chkPause.setTag("1");
                    mRoll1.setStop(true);
                    mRoll2.setStop(true);
                    mRoll3.setStop(true);
                    mRoll4.setStop(true);
                    mRoll5.setStop(true);

                }

            }
        });

        setTickets();
        binding.ivBack.setOnClickListener(view -> {
            onBackPressed();
        });
    }

    public void setCountDown() {
        yourCountDownTimer = new CountDownTimer(countdown, 1000) {
            public void onTick(long millisUntilFinished) {
                countdown = millisUntilFinished;
                binding.tvRemainingText.setText("Game Starts in: " + millisUntilFinished / 1000);
                setupSlotDumy();
                setRollDuration(String.valueOf((millisUntilFinished / 1000)));
                 }

            public void onFinish() {
                binding.linPause.setVisibility(View.GONE);

            }
        }.start();
        setupSlotDumy();

        //  setupSlotDumy();
       // startRoll();
    }

    String contest_price_game_list;

    private void getJoinContest(final String ticketsIds) {
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        String request = "";
        try {
            jsonObject.put("contest_id", contest_id);
            jsonObject.put("tickets", ticketsIds);
            jsonObject.put("list", lst);
            if (!random_lst.equals("")) {
                jsonObject.put("Randomlist", random_lst);

            } else {
                jsonObject.put("Randomlist", "[]");

            }
            request = jsonObject.toString();
            Log.d(TAG, "getJoinContest: " + contest_id + ">>" + request);
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
                    // if (!isAlreadyPurchased) {
                    //  UpcomingContestModel upcomingContestModel = new UpcomingContestModel(cdm.getContent().getStartDate(), cdm.getContent().getCurrentTime(), 1, String.valueOf(cdm.getContent().getId()), cdm.getContent().getName(), cdm.getContent().getGame_type());
                    //  DatabaseHandler databaseHandler = new DatabaseHandler(context);
                    //  upcomingContestModel.setId(databaseHandler.addContest(upcomingContestModel));
                    CBit.selectedTicketList.clear();
                    ArrayList<String> stringArrayList = new ArrayList<>();
                    for (int i = 0; i < joinContest.getGamenumber().size(); i++) {
                        stringArrayList.add(joinContest.getGamenumber().get(i).getGame_no());
                    }
                    gameNo = TextUtils.join(",", stringArrayList);
                    contest_price_game_list = gson.toJson(joinContest.getGamenumber());
                    getContestDetails(true);
                    // }
                } else {
                    Utils.showToast(context, joinContest.getMessage());
                }
            }

            @Override
            public void failure(String responseData) {

            }
        });
    }

    public static final String GAME_NO = "gameNo";

    public static final String CONTEST_PRIZE_ID = "contestPrizeId";

    public void getBundelData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            finish();
        } else {
            content = (AnyTimeSpinningCatList.Lst) bundle.getSerializable("Content");
            contest_id = bundle.getString(CONTESTID, "");
            Log.i("contest_id", "=>" + contest_id);
            gameNo = bundle.getString(GAME_NO, "");
            lst = bundle.getString("lst", "");
            random_lst = bundle.getString("random_lst", "");
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
            //  ticketList.addAll(CBit.selectedTicketList);
            Log.i("selectedTicketList size", "==>" + CBit.selectedTicketList.size());
            binding.toolbarTitle.setText("What's Most?");

        }
        if (bundle.getString("TAG", "").equals("reminder")) {
            if (sessionUtil.isLogin()) {
                isReminderScreen = true;
                stopAlramDiloag();
            } else {
                Intent intent = new Intent(context, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        }
    }


    private void getContestDetails(final boolean isRecall) {
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
                LogHelper.d(TAG, responseData);
                Gson gson = new Gson();
                cdm = gson.fromJson(responseData, ContestDetailsModel.class);

                ticketList.addAll(cdm.getContent().getTickets());
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                binding.rvOprions.setLayoutManager(linearLayoutManager);
                binding.gameNote.setText("What's Most?");
                optionsAdapter = new SpiningOptionsAdapter(context, ticketList.get(0).getSlotes(), CheckGameStatus);
                binding.rvOprions.setAdapter(optionsAdapter);
                boxJson.addAll(cdm.getContent().getBoxJson());
                winningOptionsList.addAll(cdm.getContent().getWinningOptions());
                int dumytimeCount = time - 41;
                ticketAdapter.setGameStatus(CheckGameStatus);
                ticketAdapter.notifyDataSetChanged();


                ticketAdapter.setGameStatus(cdm.getContent().getGameStatus());
                if (cdm.getContent().getGameStatus().equalsIgnoreCase(Utils.GAME_END)) {

                /*    EventBus.getDefault().post(new UpdateMyContestEvent());
                    Intent intent = new Intent(context, AnySpinerGameHistoryActivity.class);
                    intent.putExtra(AnySpinerGameHistoryActivity.CONTEST_ID, contestId);
                    intent.putExtra(AnySpinerGameHistoryActivity.GAME_NO, gameNo+"");
                    intent.putExtra(AnySpinerGameHistoryActivity.CONTEST_PRIZE_ID, contestPrizeId+"");
                    intent.putExtra("contest_price_game_list", contest_price_game_list);
                    startActivity(intent);
                    finish();*/
                } else if (cdm.getContent().getGameStatus().equalsIgnoreCase(Utils.GAME_START)) {
                    CheckGameStatus = cdm.getContent().getGameStatus();
                    setUpSlots();
                    //startRoll();
                    callGameStart();
                    //  long mill = Utils.convertMillSeconds(cdm.getContent().getStartDate(),cdm.getContent().getCurrentTime());
                    //  differenceSecond=mills-mill;
                } else if (cdm.getContent().getGameStatus().equalsIgnoreCase(Utils.GAME_NOT_START)) {
                    // startRoll();
                    hideTimer(isRecall);
                    CheckGameStatus = cdm.getContent().getGameStatus();
                    //  ticketList.addAll(CBit.selectedTicketList);
                    Log.i("selectedTicketList size", "==>" + CBit.selectedTicketList.size());


                } else {
                    hideTimer(isRecall);
                }
            }

            @Override
            public void failure(String responseData) {

            }
        });
    }

    private void hideTimer(boolean isRecall) {
        binding.tvLockNow.setAlpha(.5f);
        binding.tvLockNow.setEnabled(false);
        binding.tvText.setVisibility(View.GONE);
        binding.tvRemainingText.setVisibility(View.VISIBLE);
        //    ticketList.clear();
        for (Ticket ticket : cdm.getContent().getTickets()) {
            if (ticket.getIsPurchased() != 0) {
                //      ticketList.add(ticket);
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

    public void FadinAnimaiton(RecyclerView img) {
        Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        img.startAnimation(aniFade);
    }

    private void startRoll() {
        int totalTime = (150 - 30) * 100;
        mRoll1.start(totalTime, totalTime - 400, 0);
        mRoll2.start(totalTime, totalTime - 300, 0);
        mRoll3.start(totalTime, totalTime - 200, 0);
        mRoll4.start(totalTime, totalTime - 100, 0);
        mRoll5.start(totalTime, totalTime, 0);
        mRoll1.setDuration(100);
        mRoll2.setDuration(100);
        mRoll3.setDuration(100);
        mRoll4.setDuration(100);
        mRoll5.setDuration(100);

    }

    public void setRollDuration(String time) {
        if (time.equalsIgnoreCase("0")) {
            CheckGameStatus = "start";
            setUpSlots();
            callGameStart();
        } else if (time.equalsIgnoreCase("1")) {
            mRoll1.setStop(true);
            mRoll2.setStop(true);
            mRoll3.setStop(true);
            mRoll4.setStop(true);
            mRoll5.setStop(true);

        } else {
            mRoll1.setDuration(300);
            mRoll2.setDuration(300);
            mRoll3.setDuration(300);
            mRoll4.setDuration(300);
            mRoll5.setDuration(300);

        }

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
        if (view.getId() == R.id.tvLockNow) {//Toast.makeText(context, "clickk", Toast.LENGTH_SHORT).show();

            PrintLog.e(TAG, "tvLockNow click " + position + "");
            if (CheckGameStatus.equalsIgnoreCase(Utils.GAME_START)) {
                if (!ticketList.get(position).getMinValue().isEmpty()) {
                    PrintLog.e(TAG, "tvLockNow click in if" + position + "");
                    setAnsLock(ticketList.get(position).getContestId(), ticketList.get(position).getContestPriceId(),
                            ticketList.get(position).getMinValue(),
                            ticketList.get(position).getMaxValue(),
                            position,
                            ticketList.get(position).getDisplayView());
                } else {
                    //  Utils.showToast(context, "Please Select Any Number");
                }
            } else {
                Utils.showToast(context, "Game Not Started");
            }
        }
    }

    private void setAnsLock(int contestId, int contestPriceId, String startValue, String endValue, int position, String displayValue) {
        try {
            selectedPostion = position;
            JSONObject object = new JSONObject();
            object.put("userId", sessionUtil.getId());
            object.put("contestId", contestId);
            object.put("contestPriceId", contestPriceId);
            object.put("startValue", startValue);
            object.put("endValue", endValue);
            object.put("isLock", 1);
            object.put("position", position);
            object.put("DisplayValue", displayValue);
            object.put("gameNo", gameNo + "");
            object.put("IsLockAll", 0);

            byte[] data;
            String request = "";
            request = object.toString();
            Log.d(TAG, "setALLAnsLock: " + object.toString());
            Log.d(TAG, "setALLAnsLock: " + request);
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

    @Override
    public void onSlotValue(View view, int parentPos, int childPos) {
        for (int i = 0; i < ticketList.get(parentPos).getSlotes().size(); i++) {
            if (i == childPos) {
                ticketList.get(parentPos).getSlotes().get(i).setIsSelected(true);
                ticketList.get(parentPos).setMinValue(String.valueOf(ticketList.get(parentPos).getSlotes().get(childPos).getStartValue()));
                ticketList.get(parentPos).setMaxValue(String.valueOf(ticketList.get(parentPos).getSlotes().get(childPos).getEndValue()));
                ticketList.get(parentPos).setDisplayView(String.valueOf(ticketList.get(parentPos).getSlotes().get(childPos).getDisplayValue()));
                ticketList.get(parentPos).setImageView(String.valueOf(ticketList.get(parentPos).getSlotes().get(childPos).getImage()));
            } else {
                ticketList.get(parentPos).getSlotes().get(i).setIsSelected(false);
            }
        }

        ticketAdapter.notifyItemChanged(parentPos);
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
    }

    private void callGameStart() {
        binding.tvRemainingText.setVisibility(View.GONE);
        binding.tvText.setVisibility(View.VISIBLE);
        handler.removeCallbacks(runnable);
        ticketList.clear();
        if (CheckGameStatus.equalsIgnoreCase(Utils.GAME_START)) {
            binding.tvText.setVisibility(View.VISIBLE);
            binding.tvRemainingText.setVisibility(View.GONE);
            handler.removeCallbacks(runnable);
            if (finishCountDownTimer == null)
                finishCountDownTimer = new CountDownTimer(30000, 1000) {
                    public void onTick(long millisUntilFinished) {
                        binding.tvText.setText("" + millisUntilFinished / 1000);
                    }

                    public void onFinish() {
                        waitingPopupforLock();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                CheckGameStatus = "gameEnd";
                                //    EventBus.getDefault().post(new UpdateMyContestEvent());
                                Intent intent = new Intent(context, AnySpinerGameHistoryActivity.class);
                                intent.putExtra(AnyTimeGameHistoryActivity.CONTEST_ID, String.valueOf(contest_id));
                                intent.putExtra(AnyTimeGameHistoryActivity.GAME_NO, gameNo + "");
                                intent.putExtra(AnyTimeGameHistoryActivity.CONTEST_PRIZE_ID, tickets_id + "");
                                intent.putExtra("contest_price_game_list", contest_price_game_list + "");
                                //   intent.putExtra(AnyTimeGameResultActivity.IS_REMINDER, isReminderScreen);
                                startActivity(intent);
                                finish();
                            }
                        }, 3000);

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
            tempJsonList.addAll(boxJsonList);
            if (!isClick) {
                binding.tvLockNow.setAlpha(1f);
                binding.tvLockNow.setEnabled(true);
                binding.rvOprions.setClickable(true);

            } else {

                binding.rvOprions.setClickable(false);

            }
            optionsAdapter.setOnItemClickListener((view, position) -> {
                if (CheckGameStatus.equalsIgnoreCase(Utils.GAME_START)) {

                } else {
                    Utils.showToast(context, "Game Not Started");
                }
                selectedImage = ticketList.get(0).getSlotes().get(position).getImage();
                SelectedDisplayView = ticketList.get(0).getSlotes().get(position).getImage();
                SelectedDisplayViewString = ticketList.get(0).getSlotes().get(position).getDisplayValue();
            });

            binding.tvLockNow.setOnClickListener(v -> {
                if (CheckGameStatus.equalsIgnoreCase(Utils.GAME_START)) {
                    if (!SelectedDisplayView.isEmpty()) {
                        isClick = true;
                        binding.rvOprions.setAlpha(.5f);
                        binding.tvLockNow.setEnabled(false);
                        setALLAnsLock(SelectedDisplayView);
                        binding.imgLock.setVisibility(View.VISIBLE);
                        waitingPopupforLock();
                    } else {
                        //  Utils.showToast(context, "Please Select Any Number");
                    }
                }
            });
        } else {
            binding.tvText.setVisibility(View.GONE);
            binding.linBrickes.setVisibility(View.VISIBLE);
            binding.tvRemainingText.setVisibility(View.VISIBLE);
        }
        if (CheckGameStatus.equals(Utils.GAME_START)) {
            if (gamestatusCount == 0) {
                gamestatusCount++;
                optionsAdapter.setGameStatus(CheckGameStatus);
                optionsAdapter.notifyDataSetChanged();
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
                        userSelect.setDisplayValue(s.getDisplayValue());
                    else
                        userSelect.setDisplayValue(s.getDisplayValue());
                    userSelect.setStartValue(String.valueOf(s.getDisplayValue()));
                    userSelect.setEndValue(String.valueOf(s.getDisplayValue()));
                    userSelect.setSelectValue(String.valueOf(s.getImage()));
                    userSelect.setImage(s.getImage());
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
                ticketList.get(i).setImageView(String.valueOf(ticketList.get(i).getUserSelect().getImage()));

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
                binding.tvLockNow.setVisibility(View.GONE);
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
                binding.tvAnsSelection.setText(cdm.getContent().getLockAllData().get(0).getDisplayValue());

                binding.imgLock.setVisibility(View.VISIBLE);

            } else {
                binding.tvAnsSelection.setText("-");
                binding.tvAnsSelection.setText("-");
                binding.tvLockTime.setVisibility(View.GONE);

                binding.imgLock.setVisibility(View.GONE);
            }
            /* vishal change */

            binding.rvOprions.setClickable(false);
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
            SelectedDisplayView = ticketList.get(0).getSlotes().get(position).getImage();
            SelectedDisplayViewString = ticketList.get(0).getSlotes().get(position).getDisplayValue();
        });

        binding.tvLockNow.setOnClickListener(v -> {

            PrintLog.e(TAG, "tvLockNow click " + SelectedDisplayView + "");
            if (CheckGameStatus.equalsIgnoreCase(Utils.GAME_START)) {
                if (!SelectedDisplayView.isEmpty()) {
                    PrintLog.e(TAG, "tvLockNow click in if" + SelectedDisplayView + "");
                    isClick = true;
                    binding.  rvOprions.setAlpha(.5f);
                    binding.  tvLockNow.setEnabled(false);
                    setALLAnsLock(SelectedDisplayView);
                    binding.  imgLock.setVisibility(View.VISIBLE);
                    waitingPopupforLock();
                } else {
                    //  Utils.showToast(context, "Please Select Any Number");
                }
            }
        });

    }

    /* ---------- End Method ----------*/
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // Insert custom code here
            // inItChange();
            //    setUpSlots();
            // Repeat every 1 seconds
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

    private void setALLAnsLock(String displayValue) {
        try {
            JSONObject object = new JSONObject();
            object.put("userId", sessionUtil.getId());
            object.put("contestId", contest_id);
            object.put("contestPriceId", tickets_id);
            object.put("DisplayValue", displayValue);
            object.put("DisplayValueString", SelectedDisplayViewString);
            object.put("IsLockAll", "1");
            object.put("startValue", displayValue);
            object.put("endValue", displayValue);
            object.put("gameNo", gameNo);
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
            CBit.getSocketUtils().getmSocket().emit(SocketUtils.EVENT_ANYUPDATE_GAMEALL, request, anytimeUpdateGameAll);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public int selectedPostion;

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

                            binding. rvOprions.setClickable(false);
                            binding. imgLock.setVisibility(View.VISIBLE);
                            binding.   tvLockTime.setVisibility(View.VISIBLE);
                            binding.  tvLockTime.setText("Locked at: " + ugm.getLockTime());
                            binding.  tvLockNow.setText("Locked");
                            binding.   tvLockNow.setVisibility(View.GONE);
                            binding.   tvAnsSelection.setText(SelectedDisplayView);
                            binding.  tvAnsSelection.setVisibility(View.GONE);
                            binding.  imgAnsSelection.setVisibility(View.VISIBLE);
                            Glide.with(context)
                                    .load(SDCardPath + selectedImage)
                                    .into(binding.imgAnsSelection);

                            Log.d(TAG, "success: " + ugm.getContent().get(0).getIsLockTime() + ">>>" + SelectedDisplayView);

                            for (int i = 0; i < ugm.getContent().size(); i++) {
                                try {
                                    /* Vishal Change */
                                    for (int j = 0; j < ticketList.size(); j++) {
                                        /* Vishal Change if condition */
                                        if (ugm.getContent().get(i).getContestPriceId() == ticketList.get(j).getContestPriceId()) {
                                            System.err.println(j + " " + ticketList.get(j).getMinValue() + " " + ticketList.get(j).getMaxValue());
                                            ticketList.get(j).setIsLock(ugm.getContent().get(i).isIsLock());
                                            ticketList.get(j).setLockTime(ugm.getContent().get(i).getIsLockTime());
                                            ticketList.get(j).setDisplayView(SelectedDisplayView);
                                            ticketList.get(j).setMinValue(String.valueOf(ugm.getContent().get(i).getStartValue()));
                                            ticketList.get(j).setMaxValue(String.valueOf(ugm.getContent().get(i).getEndValue()));
                                            System.err.println(j + " " + ticketList.get(j).getMinValue() + " " + ticketList.get(j).getMaxValue());
//                                ticketList.get(i).setDisplayView(ugm.getContent().get(i).getDisplayValue());

                                            UserSelect userSelect = new UserSelect();
                                            userSelect.setImage(selectedImage);
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
                            // waitingPopupforLock();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    CheckGameStatus = "gameEnd";
                                    //    EventBus.getDefault().post(new UpdateMyContestEvent());
                                    Intent intent = new Intent(context, AnySpinerGameHistoryActivity.class);
                                    intent.putExtra(AnyTimeGameHistoryActivity.CONTEST_ID, String.valueOf(contest_id));
                                    intent.putExtra(AnyTimeGameHistoryActivity.GAME_NO, gameNo + "");
                                    intent.putExtra(AnyTimeGameHistoryActivity.CONTEST_PRIZE_ID, tickets_id + "");
                                    intent.putExtra("contest_price_game_list", contest_price_game_list + "");
                                    startActivity(intent);
                                    finish();
                                }
                            }, 10000);

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

    private void waitingPopup() {
        mPlayer = MediaPlayer.create(context, R.raw.timer);
        mPlayer.setLooping(true);
        mPlayer.start();
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
        }

        calculatingDialog.show();
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

                        }
                    }, 20000);
                }
            }
        });

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
        yourCountDownTimer.cancel();
        finishCountDownTimer.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPlayer != null && calculatingDialog != null) {
            if (calculatingDialog.isShowing())
                mPlayer.start();
        }
    }

    @Override
    protected void onDestroy() {

        yourCountDownTimer.cancel();
        finishCountDownTimer.cancel();
        try {
            if (calculatingDialog != null && calculatingDialog.isShowing()) {
                calculatingDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (isHandlerPost) {
                handler.removeCallbacks(runnable);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (mPlayer != null) {
                mPlayer.release();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (EventBus.getDefault().isRegistered(this))
                EventBus.getDefault().unregister(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
        getApplicationContext().stopService(intentService);

        super.onDestroy();
    }

    public void getFromSdcard() {
        iamgesList = new ArrayList<String>();// list of file paths
        File[] listFile;

        File file = new File(Environment.getExternalStorageDirectory(), ".cbit");

        if (file.isDirectory()) {
            listFile = file.listFiles();

            for (int i = 0; i < listFile.length; i++) {

                iamgesList.add(listFile[i].getAbsolutePath());

            }
        }
    }

    public void setUpSlots() {

        if (boxJson.size() == 15) {
            slot3By5();
        } else if (boxJson.size() == 20) {
            slot4By5();
        } else if (boxJson.size() == 25) {
            slot5By5();
        }
        ticketAdapter.notifyDataSetChanged();
       /* FadinAnimaiton(binding.rvI);
        FadinAnimaiton(binding.rvII);
        FadinAnimaiton(binding.rvIII);
        FadinAnimaiton(binding.rvIV);
        FadinAnimaiton(binding.rvV);
        FadinAnimaiton(binding.rvVI);
        FadinAnimaiton(binding.rvVII);
        FadinAnimaiton(binding.rvVIII);
        FadinAnimaiton(binding.rvIX);
        FadinAnimaiton(binding.rvX);
        FadinAnimaiton(binding.rvXI);
        FadinAnimaiton(binding.rvXII);
        FadinAnimaiton(binding.rvXIII);
        FadinAnimaiton(binding.rvXIV);
        FadinAnimaiton(binding.rvXV);*/


    }

    public void slot3By5() {
        setUpRecyclr(binding.rvI, 0, 2);
        setUpRecyclr(binding.rvIV, 3, 5);
        setUpRecyclr(binding.rvVII, 6, 8);
        setUpRecyclr(binding.rvX, 9, 11);
        setUpRecyclr(binding.rvXIII, 12, 14);

        setUpRecyclr(binding.rvII, 12, 14);
        setUpRecyclr(binding.rvIII, 9, 11);
        setUpRecyclr(binding.rvV, 6, 8);
        setUpRecyclr(binding.rvVI, 3, 5);
        setUpRecyclr(binding.rvVIII, 12, 14);
        setUpRecyclr(binding.rvIX, 0, 2);
        setUpRecyclr(binding.rvXI, 3, 5);
        setUpRecyclr(binding.rvXII, 6, 8);
        setUpRecyclr(binding.rvXIV, 0, 2);
        setUpRecyclr(binding.rvXV, 6, 8);
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
        for (int j = 0; j < winningOptionsList.size(); j++) {
            for (int i = startPos; i <= endPos; i++) {
                if (winningOptionsList.get(j).getObjectNo() == Integer.parseInt(boxJson.get(i).getNumber())) {
                    bricksItems.add(SDCardPath + winningOptionsList.get(j).getImage());

                }
            }
        }

        rv.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        ViewFliperItemAdapter ticketAdapter = new ViewFliperItemAdapter(context, bricksItems);
        rv.setAdapter(ticketAdapter);

    }

    public void setupSlotDumy() {
          FadinAnimaiton(binding.rvI);
          FadinAnimaiton(binding.rvIV);
          FadinAnimaiton(binding.rvVII);
         FadinAnimaiton(binding.rvX);
          FadinAnimaiton(binding.rvXIII);
        slot3By5Dumy();
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

    AnyTimeSpinningCatList.Lst content;

    public void setUpRecyclrDumy(RecyclerView rv, int startPos, int endPos) {
        ArrayList<String> bricksItems = new ArrayList<>();
        // this is dynamic image load from local doenloaded logic
        String SDCardPath = getFilesDir().getAbsolutePath() + "/";
        for (int i = startPos; i <= endPos; i++) {
            int randomId = new Random().nextInt(content.getItems().size());
            bricksItems.add(SDCardPath + content.getItems().get(randomId).getImage());
        }

        rv.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        ViewFliperItemAdapter ticketAdapter = new ViewFliperItemAdapter(context, bricksItems);
        rv.setAdapter(ticketAdapter);

    }


}