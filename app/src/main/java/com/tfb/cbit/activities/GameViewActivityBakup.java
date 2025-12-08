//package com.tfb.cbit.activities;
//
//import android.app.Dialog;
//import android.app.NotificationManager;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Color;
//import android.graphics.drawable.ColorDrawable;
//import android.media.MediaPlayer;
//import android.net.ConnectivityManager;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.PowerManager;
//import android.util.Base64;
//import android.util.DisplayMetrics;
//import android.util.Log;
//import android.view.View;
//import android.view.Window;
//import android.view.WindowManager;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ProgressBar;
//import android.widget.RadioButton;
//import android.widget.RadioGroup;
//import android.widget.TextView;
//
//import androidx.appcompat.widget.AppCompatImageView;
//import androidx.appcompat.widget.Toolbar;
//import androidx.recyclerview.widget.GridLayoutManager;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import androidx.recyclerview.widget.SimpleItemAnimator;
//
//import com.google.gson.Gson;
//import com.tfb.cbit.CBit;
//import com.tfb.cbit.R;
//import com.tfb.cbit.adapter.BricksAdapter;
//import com.tfb.cbit.adapter.TicketAdapter;
//import com.tfb.cbit.api.APIClient;
//import com.tfb.cbit.api.ApiCallback;
//import com.tfb.cbit.api.NewApiCall;
//import com.tfb.cbit.event.GameAlertEvent;
//import com.tfb.cbit.event.GameResultEvent;
//import com.tfb.cbit.event.GameStartEvent;
//import com.tfb.cbit.event.SocketConnectionEvent;
//import com.tfb.cbit.event.UpdateMyContestEvent;
//import com.tfb.cbit.event.UpdateUpcomingContestEvent;
//import com.tfb.cbit.interfaces.OnItemClickListener;
//import com.tfb.cbit.interfaces.OnRangeListener;
//import com.tfb.cbit.interfaces.OnSlotListener;
//import com.tfb.cbit.models.contestdetails.BoxJson;
//import com.tfb.cbit.models.contestdetails.ContestDetailsModel;
//import com.tfb.cbit.models.contestdetails.Ticket;
//import com.tfb.cbit.models.contestdetails.UserSelect;
//import com.tfb.cbit.models.updategame.UpdateAllGameModel;
//import com.tfb.cbit.models.updategame.UpdateGameModel;
//import com.tfb.cbit.utility.CountDown;
//import com.tfb.cbit.utility.CustomDialog;
//import com.tfb.cbit.utility.PrintLog;
//import com.tfb.cbit.utility.SessionUtil;
//import com.tfb.cbit.utility.SocketUtils;
//import com.tfb.cbit.utility.Utils;
//
//import org.greenrobot.eventbus.EventBus;
//import org.greenrobot.eventbus.Subscribe;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.UnsupportedEncodingException;
//import java.nio.charset.StandardCharsets;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Random;
//import java.util.Timer;
//import java.util.TimerTask;
//import java.util.concurrent.TimeUnit;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//import butterknife.OnClick;
//import io.socket.client.Ack;
//import okhttp3.ResponseBody;
//import retrofit2.Call;
//
//public class GameViewActivityBakup extends BaseAppCompactActivity implements OnItemClickListener, OnRangeListener, OnSlotListener {
//
//
//    @BindView(R.id.toolbar_title)
//    TextView toolbar_title;
//    @BindView(R.id.rvBricks)
//    RecyclerView rvBricks;
//    @BindView(R.id.rvTickets)
//    RecyclerView rvTickets;
//    @BindView(R.id.tvText)
//    TextView tvText;
//    @BindView(R.id.tvRemainingText)
//    TextView tvRemainingText;
//    @BindView(R.id.pbProgress)
//    ProgressBar pbProgress;
//    @BindView(R.id.linearContent)
//    LinearLayout linearContent;
//
//    @BindView(R.id.lin_rdb)
//    LinearLayout linRdb;
//    @BindView(R.id.tv_nine_LockNow)
//    TextView tvNineLockNow;
//    @BindView(R.id.lin_nine)
//    LinearLayout linNine;
//    @BindView(R.id.img_lock)
//    ImageView imgLock;
//    @BindView(R.id.tvAnsSelection)
//    TextView tvAnsSelection;
//    @BindView(R.id.tvLockTime)
//    TextView tvLockTime;
//    @BindView(R.id.rd_zero)
//    RadioButton rdZero;
//    @BindView(R.id.rd_one)
//    RadioButton rdOne;
//    @BindView(R.id.rd_two)
//    RadioButton rdTwo;
//    @BindView(R.id.rd_three)
//    RadioButton rdThree;
//    @BindView(R.id.rd_four)
//    RadioButton rdFour;
//    @BindView(R.id.rd_five)
//    RadioButton rdFive;
//    @BindView(R.id.rd_six)
//    RadioButton rdSix;
//    @BindView(R.id.rd_seven)
//    RadioButton rdSeven;
//    @BindView(R.id.rd_eight)
//    RadioButton rdEight;
//    @BindView(R.id.rd_nine)
//    RadioButton rdNine;
//    @BindView(R.id.ivBack)
//    ImageView ivBack;
//    @BindView(R.id.ivInfo)
//    AppCompatImageView ivInfo;
//    @BindView(R.id.toolbar)
//    Toolbar toolbar;
//    @BindView(R.id.rd_red)
//    RadioButton rdRed;
//    @BindView(R.id.rd_draw)
//    RadioButton rdDraw;
//    @BindView(R.id.rd_blue)
//    RadioButton rdBlue;
//    @BindView(R.id.rd_rdb)
//    RadioGroup rdRdb;
//    @BindView(R.id.rvOprions)
//    LinearLayout rvOprions;
//    @BindView(R.id.linearSelection)
//    LinearLayout linearSelection;
//    @BindView(R.id.rdg_zero)
//    RadioGroup rdgZero;
//    @BindView(R.id.rdg_five)
//    RadioGroup rdgFive;
//    private Context context;
//    private BricksAdapter bricksAdapter;
//    private ArrayList<Integer> bricksItems = new ArrayList<>();
//    private ArrayList<HashMap<String, Integer>> bricksColorModel = new ArrayList<>();
//    int[] colorArray = {R.color.color_green, R.color.color_red, R.color.color_blue};
//    private CountDown startingTime = null;
//    //private CountDownTimer startingTime = null;
//    //private CountDownTimer remainingTime = null;
//    private CountDown remainingTime = null;
//    private SessionUtil sessionUtil;
//    private String contest_id = "", constest_title = "", constest_type = "";
//    public static final String CONTESTID = "contest_id";
//    public static final String CONTESTTITLE = "contest_title";
//    public static final String CONTESTTYPE = "contest_type";
//    private static final String TAG = GameViewActivityBakup.class.getSimpleName();
//    // Create the Handler
//    private Handler handler = new Handler();
//    private boolean isHandlerPost = false;
//    private TicketAdapter ticketAdapter = null;
//    private List<Ticket> ticketList = new ArrayList<>();
//    private ContestDetailsModel cdm = null;
//    private CustomDialog customDialog;
//    private MediaPlayer mPlayer = null;
//    private Dialog calculatingDialog = null;
//    private Dialog lockcalculatingDialog = null;
//    private MediaPlayer mp = null;
//    private Dialog dialog = null;
//    private boolean isReminderScreen = false;
//    public Timer mTimer;
//    private boolean isGameStart = false;
//    private PowerManager.WakeLock wl;
//    private long differenceSecond = 0;
//    long startMill;
//    long endMill;
//
//    String SelectedDisplayView = "";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        final Window win = getWindow();
//        win.addFlags( WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
//                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
//                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
//                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
//                WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON );
//
//        setContentView(R.layout.activity_game_view);
//        ButterKnife.bind(this);
//
//
//        rdgZero.clearCheck();
//        rdgFive.clearCheck();
//        rdgZero.setOnCheckedChangeListener(listener1);
//        rdgFive.setOnCheckedChangeListener(listener2);
//
//        context = this;
//        sessionUtil = new SessionUtil(context);
//        pbProgress.setVisibility(View.VISIBLE);
//        linearContent.setVisibility(View.GONE);
//
//        //PowerManager Use https://github.com/socketio/socket.io-client-java/issues/84
//        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(
//                Context.POWER_SERVICE);
//        this.wl = pm.newWakeLock(
//                PowerManager.PARTIAL_WAKE_LOCK
//                        | PowerManager.ON_AFTER_RELEASE,
//                TAG);
//        wl.acquire();
//
//        Bundle bundle = getIntent().getExtras();
//        if (bundle == null) {
//            finish();
//        } else {
//            contest_id = bundle.getString(CONTESTID, "");
//            constest_title = bundle.getString(CONTESTTITLE, "");
//            constest_type = bundle.getString(CONTESTTYPE, "");
//
//            if (contest_id.equalsIgnoreCase("")){
//                getIntent().getStringExtra(CONTESTID);
//            }
//            if (constest_title.equalsIgnoreCase("")){
//                getIntent().getStringExtra(CONTESTTITLE);
//            }
//            if (constest_type.equalsIgnoreCase("")){
//                getIntent().getStringExtra(CONTESTTYPE);
//            }
//
//            toolbar_title.setText(constest_title);
//            Log.d(TAG, "contest_id: " + contest_id);
//            Log.d(TAG, "contest_type: " + constest_type);
//
//            PrintLog.e(GameViewActivityBakup.CONTESTID, contest_id + " GameView Constest ID");
//            if (bundle.getString("TAG", "").equals("reminder")) {
//                if (sessionUtil.isLogin()) {
//                    isReminderScreen = true;
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            mp = MediaPlayer.create(context, R.raw.alarm);
//                            try {
//                                mp.start();
//                                dialog = new Dialog(context);
//                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                                if (dialog.getWindow() != null)
//                                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                                dialog.setCancelable(false);
//                                dialog.setContentView(R.layout.dialog_alarm_stop_layout);
//                                TextView tvConstestID = dialog.findViewById(R.id.tvConstestID);
//                                TextView tvConstestName = dialog.findViewById(R.id.tvConstestName);
//                                tvConstestID.setText(contest_id);
//                                tvConstestName.setText(constest_title);
//                                Button btnStopAlarm = dialog.findViewById(R.id.btnStopAlarm);
//                                btnStopAlarm.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        if (mp != null && mp.isPlaying()) {
//                                            mp.release();
//                                            mp = null;
//                                        }
//                                        if (dialog != null && dialog.isShowing()) {
//                                            dialog.dismiss();
//                                            dialog = null;
//                                        }
//                                    }
//                                });
//                                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                                    @Override
//                                    public void onCompletion(MediaPlayer mp) {
//                                        if (dialog != null && dialog.isShowing()) {
//                                            dialog.dismiss();
//                                            dialog = null;
//                                        }
//                                    }
//                                });
//                                dialog.show();
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                                PrintLog.e("MEDIA", e.getMessage());
//                                mp = MediaPlayer.create(context, R.raw.alarm);
//                                mp.start();
//                            }
//
//                        }
//                    }, 1000);
//
//                } else {
//                    Intent intent = new Intent(context, HomeActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(intent);
//                    finish();
//                }
//
//
//            }
//        }
//        mPlayer = MediaPlayer.create(context, R.raw.timer);
//        mPlayer.setLooping(true);
//        EventBus.getDefault().register(this);
//
//        rvTickets.setLayoutManager(new LinearLayoutManager(context));
//        if (rvTickets.getItemAnimator() != null)
//            ((SimpleItemAnimator) rvTickets.getItemAnimator()).setSupportsChangeAnimations(false);
//        ticketList.clear();
//        ticketAdapter = new TicketAdapter(context, ticketList);
//        Log.d(TAG, "AlramTickitlist: " + ticketList.size());
//        ticketAdapter.setOnItemClickListener(this);
//        ticketAdapter.setOnRangeListener(this);
//        ticketAdapter.setOnSlotListener(this);
//        rvTickets.setAdapter(ticketAdapter);
//        rvBricks.setLayoutManager(new GridLayoutManager(context, 4));
//
//
//        // PrintLog.d(TAG,""+ CBit.getSocketUtils().getmSocket().connected());
////        if (CBit.getSocketUtils().getmSocket().connected()) {
////            try {
////                JSONObject object = new JSONObject();
////                object.put("userId", sessionUtil.getId());
////                object.put("contestId", contest_id);
////                byte[] data;
////                String request = "";
////                request = object.toString();
////                PrintLog.e("TAG", "Socket ID " + CBit.getSocketUtils().getmSocket().id());
////                PrintLog.e("TAG", contest_id + " " + request);
////                request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, context.getString(R.string.crypt_pass));
////                data = request.getBytes(StandardCharsets.UTF_8);
////                request = Base64.encodeToString(data, Base64.DEFAULT);
////                startMill = System.currentTimeMillis();
//////                CBit.getSocketUtils().getmSocket().emit(SocketUtils.EVENT_CONTEST_DETAILS, request, contestDetails);
////            } catch (JSONException e) {
////                e.printStackTrace();
////            } catch (UnsupportedEncodingException e) {
////                e.printStackTrace();
////            } catch (Exception e) {
////                e.printStackTrace();
////            }
////
////        } else {
////            CBit.getSocketUtils().connect();
////        }
//
//        getContestDetails();
//
//        //BackGround Check Network
//        mTimer = new Timer();
//        mTimer.scheduleAtFixedRate(new CheckForConnection(), 0, 5 * 1000);
//        if (constest_type.equalsIgnoreCase("rdb")) {
//            linRdb.setVisibility(View.VISIBLE);
//            linNine.setVisibility(View.GONE);
//        } else if (constest_type.equalsIgnoreCase("0-9")) {
//            linRdb.setVisibility(View.GONE);
//            linNine.setVisibility(View.VISIBLE);
//        }
//
//    }
//
//    private RadioGroup.OnCheckedChangeListener listener1 = new RadioGroup.OnCheckedChangeListener() {
//
//        @Override
//        public void onCheckedChanged(RadioGroup group, int checkedId) {
//            if (checkedId != -1) {
//                rdgFive.setOnCheckedChangeListener(null); // remove the listener before clearing so we don't throw that stackoverflow exception(like Vladimir Volodin pointed out)
//                rdgFive.clearCheck(); // clear the second RadioGroup!
//                rdgFive.setOnCheckedChangeListener(listener2); //reset the listener
//                RadioButton radioButton = findViewById(checkedId);
//                SelectedDisplayView = String.valueOf(radioButton.getText());
//            }
//        }
//    };
//    private RadioGroup.OnCheckedChangeListener listener2 = new RadioGroup.OnCheckedChangeListener() {
//
//        @Override
//        public void onCheckedChanged(RadioGroup group, int checkedId) {
//            if (checkedId != -1) {
//                rdgZero.setOnCheckedChangeListener(null);
//                rdgZero.clearCheck();
//                rdgZero.setOnCheckedChangeListener(listener1);
//                RadioButton radioButton = findViewById(checkedId);
//                SelectedDisplayView = String.valueOf(radioButton.getText());
//
//            }
//        }
//    };
//
//
//    private Ack updateGame = new Ack() {
//        @Override
//        public void call(final Object... args) {
//            PrintLog.e(TAG, "updateGame encrypt" + args[0].toString());
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    String res = "";
//                    try {
//                        res = CBit.getCryptLib().decryptCipherTextWithRandomIV(args[0].toString(), context.getString(R.string.crypt_pass));
//                        PrintLog.e(TAG, "updateGame " + res);
//                        Gson gson = new Gson();
//                        Object data = new JSONObject(res);
//                        Log.d(TAG, "datta>>>: " + data.toString());
//                        // Toast.makeText(context, "call obj", Toast.LENGTH_SHORT).show();
//                        UpdateGameModel ugm = gson.fromJson(res, UpdateGameModel.class);
//                        if (ugm.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
//                            ticketList.get(ugm.getContent().getPosition()).setIsLock(ugm.getContent().isIsLock());
//                            ticketList.get(ugm.getContent().getPosition()).setLockTime(ugm.getContent().getIsLockTime());
//                            ticketList.get(ugm.getContent().getPosition()).setDisplayView(ugm.getContent().getDisplayValue());
//                            UserSelect userSelect = new UserSelect();
//                            userSelect.setStartValue(String.valueOf(ugm.getContent().getStartValue()));
//                            userSelect.setEndValue(String.valueOf(ugm.getContent().getEndValue()));
//                            ticketList.get(ugm.getContent().getPosition()).setUserSelect(userSelect);
//                            ticketAdapter.notifyItemChanged(ugm.getContent().getPosition());
//                        } else {
//                            Utils.showToast(context, ugm.getMessage());
//                        }
//
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//
//        }
//    };
//    private Ack updateAllGame = new Ack() {
//        @Override
//        public void call(final Object... args) {
//            PrintLog.e(TAG, "updateGame encrypt" + args[0].toString());
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    String res = "";
//                    try {
//                        res = CBit.getCryptLib().decryptCipherTextWithRandomIV(args[0].toString(), context.getString(R.string.crypt_pass));
//                        PrintLog.e(TAG, "updateallGame1 " + res);
//                        Gson gson = new Gson();
//                        Object data = new JSONObject(res);
//                        Log.d(TAG, "datta>>>: " + data.toString());
//                        //  Toast.makeText(context, "call array", Toast.LENGTH_SHORT).show();
//                        UpdateAllGameModel ugm = gson.fromJson(res, UpdateAllGameModel.class);
//                        if (ugm.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
//
//                            rdBlue.setClickable(false);
//                            rdDraw.setClickable(false);
//                            rdRed.setClickable(false);
//
//                            rdZero.setClickable(false);
//                            rdOne.setClickable(false);
//                            rdTwo.setClickable(false);
//                            rdThree.setClickable(false);
//                            rdFour.setClickable(false);
//                            rdFive.setClickable(false);
//                            rdSix.setClickable(false);
//                            rdSeven.setClickable(false);
//                            rdEight.setClickable(false);
//                            rdNine.setClickable(false);
//
//
//                            //imgLock.setVisibility(View.VISIBLE);
//                            tvLockTime.setVisibility(View.VISIBLE);
//                            tvNineLockNow.setVisibility(View.GONE);
//
//                            tvLockTime.setText("Locked at: " + ugm.getContent().get(0).getIsLockTime());
//                            tvAnsSelection.setText(SelectedDisplayView);
//                            Log.d(TAG, "success: " + ugm.getContent().get(0).getIsLockTime() + ">>>" + SelectedDisplayView);
//                            Log.d(TAG, "sizee: " + ugm.getContent().size());
//
//                            for (int i = 0; i < ugm.getContent().size(); i++) {
//                                ticketList.get(i).setIsLock(ugm.getContent().get(i).isIsLock());
//                                ticketList.get(i).setLockTime(ugm.getContent().get(i).getIsLockTime());
//                                ticketList.get(i).setDisplayView(SelectedDisplayView);
////                                ticketList.get(i).setDisplayView(ugm.getContent().get(i).getDisplayValue());
//
//                                UserSelect userSelect = new UserSelect();
//                                userSelect.setStartValue(String.valueOf(ugm.getContent().get(i).getStartValue()));
//                                userSelect.setEndValue(String.valueOf(ugm.getContent().get(i).getEndValue()));
//                                ticketList.get(i).setUserSelect(userSelect);
//                                ticketAdapter.notifyItemChanged(i);
//                            }
//
//                            if (lockcalculatingDialog != null && lockcalculatingDialog.isShowing()) {
//                                lockcalculatingDialog.dismiss();
//                            }
//                        } else {
//                            Utils.showToast(context, ugm.getMessage());
//                        }
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//
//        }
//    };
//
//
//    @Override
//    public void onItemClick(View view, int position) {
//        switch (view.getId()) {
//            case R.id.tvMinus:
//                if (!ticketList.get(position).isIsLock()) {
//                    ticketList.get(position).setMinValue(String.valueOf(ticketList.get(position).getSlotes().get(0).getStartValue()));
//                    ticketList.get(position).setMaxValue(String.valueOf(ticketList.get(position).getSlotes().get(0).getEndValue()));
//                    ticketList.get(position).setDisplayView(String.valueOf(ticketList.get(position).getSlotes().get(0).getDisplayValue()));
//                    ticketAdapter.notifyItemChanged(position);
//                }
//                break;
//            case R.id.tvZero:
//                if (!ticketList.get(position).isIsLock()) {
//                    ticketList.get(position).setMinValue(String.valueOf(ticketList.get(position).getSlotes().get(1).getStartValue()));
//                    ticketList.get(position).setMaxValue(String.valueOf(ticketList.get(position).getSlotes().get(1).getEndValue()));
//                    ticketList.get(position).setDisplayView(String.valueOf(ticketList.get(position).getSlotes().get(1).getDisplayValue()));
//                    ticketAdapter.notifyItemChanged(position);
//                }
//                break;
//            case R.id.tvPlus:
//                if (!ticketList.get(position).isIsLock()) {
//                    ticketList.get(position).setMinValue(String.valueOf(ticketList.get(position).getSlotes().get(2).getStartValue()));
//                    ticketList.get(position).setMaxValue(String.valueOf(ticketList.get(position).getSlotes().get(2).getEndValue()));
//                    ticketList.get(position).setDisplayView(String.valueOf(ticketList.get(position).getSlotes().get(2).getDisplayValue()));
//                    ticketAdapter.notifyItemChanged(position);
//                }
//                break;
//            case R.id.tvLockNow:
//                //Toast.makeText(context, "clickk", Toast.LENGTH_SHORT).show();
//                if (!cdm.getContent().getGameStatus().equals(Utils.GAME_NOT_START)) {
//                    if (!ticketList.get(position).getMinValue().isEmpty()) {
//                        setAnsLock(ticketList.get(position).getContestPriceId(),
//                                ticketList.get(position).getMinValue(),
//                                ticketList.get(position).getMaxValue(),
//                                position,
//                                ticketList.get(position).getDisplayView());
//                    } else {
//                        Utils.showToast(context, "Please Select Any Number");
//                    }
//                } else {
//                    Utils.showToast(context, "Game Not Started");
//                }
//                break;
//        }
//    }
//
//    private void setAnsLock(int contestPriceId, String startValue, String endValue, int position, String displayValue) {
//        try {
//            JSONObject object = new JSONObject();
//            object.put("userId", sessionUtil.getId());
//            object.put("contestId", contest_id);
//            object.put("contestPriceId", contestPriceId);
//            object.put("startValue", startValue);
//            object.put("endValue", endValue);
//            object.put("isLock", 1);
//            object.put("position", position);
//            object.put("displayValue", displayValue);
//            byte[] data;
//            String request = "";
//            request = object.toString();
//            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, context.getString(R.string.crypt_pass));
//            data = request.getBytes(StandardCharsets.UTF_8);
//            request = Base64.encodeToString(data, Base64.DEFAULT);
//            CBit.getSocketUtils().getmSocket().emit(SocketUtils.EVENT_UPDATE_GAME, request, updateGame);
////            getContestDetails();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void setALLAnsLock(String displayValue) {
//        try {
//            JSONObject object = new JSONObject();
//            object.put("userId", sessionUtil.getId());
//            object.put("contestId", contest_id);
//            object.put("DisplayValue", displayValue);
//
//            byte[] data;
//            String request = "";
//            request = object.toString();
//            Log.d(TAG, "setALLAnsLock: " + object.toString());
//            Log.d(TAG, "setALLAnsLock: " + request);
//            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, context.getString(R.string.crypt_pass));
//            data = request.getBytes(StandardCharsets.UTF_8);
//            request = Base64.encodeToString(data, Base64.DEFAULT);
////            getContestDetails();
//            CBit.getSocketUtils().getmSocket().emit(SocketUtils.EVENT_UPDATE_GAMEALL, request, updateAllGame);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void onRangeValue(int minValue, int maxValue, int position) {
//        PrintLog.d("TAG", "Min " + minValue + " " + "Max " + maxValue);
//        ticketList.get(position).setMinValue(String.valueOf(minValue));
//        ticketList.get(position).setMaxValue(String.valueOf(maxValue));
//        ticketList.get(position).setDisplayView(minValue + " To " + maxValue);
//        ticketAdapter.notifyItemChanged(position);
//    }
//
//    @Override
//    public void onSlotValue(View view, int parentPos, int childPos) {
//        for (int i = 0; i < ticketList.get(parentPos).getSlotes().size(); i++) {
//            if (i == childPos) {
//                ticketList.get(parentPos).getSlotes().get(i).setIsSelected(true);
//                ticketList.get(parentPos).setMinValue(String.valueOf(ticketList.get(parentPos).getSlotes().get(childPos).getStartValue()));
//                ticketList.get(parentPos).setMaxValue(String.valueOf(ticketList.get(parentPos).getSlotes().get(childPos).getEndValue()));
//                ticketList.get(parentPos).setDisplayView(String.valueOf(ticketList.get(parentPos).getSlotes().get(childPos).getDisplayValue()));
//            } else {
//                ticketList.get(parentPos).getSlotes().get(i).setIsSelected(false);
//            }
//        }
//
//        ticketAdapter.notifyItemChanged(parentPos);
//    }
//
//    @Subscribe
//    public void onGameResultEvent(final GameResultEvent gameResultEvent) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                if (gameResultEvent.getResponse().equals(contest_id)) {
//                    EventBus.getDefault().post(new UpdateUpcomingContestEvent());
//                    EventBus.getDefault().post(new UpdateMyContestEvent());
//                    waitingPopup();
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (mPlayer != null) {
//                                mPlayer.release();
//                                mPlayer = null;
//                            }
//                            try {
//                                if (calculatingDialog != null && calculatingDialog.isShowing()) {
//                                    calculatingDialog.dismiss();
//                                }
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//
//                            Intent intent = new Intent(context, GameResultActivity.class);
//                            intent.putExtra(GameResultActivity.CONTEST_ID, contest_id);
//                            intent.putExtra(GameResultActivity.IS_REMINDER, isReminderScreen);
//                            startActivity(intent);
//                            finish();
//                        }
//                    }, 20000);
//                }
//            }
//        });
//
//    }
//
//    private void waitingPopup() {
//        mPlayer.start();
//        if (calculatingDialog == null) {
//            calculatingDialog = new Dialog(this);
//            calculatingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//            calculatingDialog.setCancelable(false);
//            calculatingDialog.setContentView(R.layout.dialog_calculating_result);
//
//            if (calculatingDialog.getWindow() != null) {
//                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//                Window window = calculatingDialog.getWindow();
//                lp.copyFrom(window.getAttributes());
//                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                DisplayMetrics metrics = getResources().getDisplayMetrics();
//                lp.width = (int) (metrics.widthPixels * 0.90);
//                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
//                window.setAttributes(lp);
//            }
//        }
//
//        calculatingDialog.show();
//    }
//    private void waitingPopupforLock() {
//        if (lockcalculatingDialog == null) {
//            lockcalculatingDialog = new Dialog(this);
//            lockcalculatingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//            lockcalculatingDialog.setCancelable(false);
//            lockcalculatingDialog.setContentView(R.layout.dialog_ans_lock);
//
//            if (lockcalculatingDialog.getWindow() != null) {
//                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//                Window window = lockcalculatingDialog.getWindow();
//                lp.copyFrom(window.getAttributes());
//                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                DisplayMetrics metrics = getResources().getDisplayMetrics();
//                lp.width = (int) (metrics.widthPixels * 0.90);
//                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
//                window.setAttributes(lp);
//            }
//        }
//
//        lockcalculatingDialog.show();
//    }
//    @Subscribe
//    public void onGameStartEvent(GameStartEvent gameStartEvent) {
//        if (gameStartEvent.getResponse().equals(contest_id)) {
//            isGameStart = true;
//            try {
//                JSONObject object = new JSONObject();
//                object.put("userId", sessionUtil.getId());
//                object.put("contestId", contest_id);
//                byte[] data;
//                String request = "";
//                request = object.toString();
//                request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, context.getString(R.string.crypt_pass));
//                data = request.getBytes(StandardCharsets.UTF_8);
//                request = Base64.encodeToString(data, Base64.DEFAULT);
//                startMill = System.currentTimeMillis();
//                getContestDetails();
////                CBit.getSocketUtils().getmSocket().emit(SocketUtils.EVENT_CONTEST_DETAILS, request, contestDetails);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    @Subscribe
//    public void onGameAlertEvent(GameAlertEvent gameAlertEvent) {
//        if (contest_id.equals(gameAlertEvent.getConetestId())) {
//            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
//            notificationManager.cancel(Integer.parseInt(contest_id));
//        }
//    }
//
//
//    @Subscribe()
//    public void onSocketConnectionEvent(final SocketConnectionEvent socketConnectionEvent) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                PrintLog.e(TAG, socketConnectionEvent.getMessage());
//                if (CBit.getSocketUtils().getmSocket().connected()) {
//                    if (customDialog != null)
//                        customDialog.dismissProgress(context);
//                    CBit.getSocketUtils().loginEmit(sessionUtil.getId());
//                    PrintLog.e("TAG", "Game Socket Connect Socket ID " + CBit.getSocketUtils().getmSocket().id());
//                    try {
//                        JSONObject object = new JSONObject();
//                        object.put("userId", sessionUtil.getId());
//                        object.put("contestId", contest_id);
//                        byte[] data;
//                        String request = "";
//                        request = object.toString();
//                        PrintLog.e("TAG", "Game Socket Connect " + contest_id + " " + request);
//                        request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, context.getString(R.string.crypt_pass));
//                        data = request.getBytes(StandardCharsets.UTF_8);
//                        request = Base64.encodeToString(data, Base64.DEFAULT);
//                        startMill = System.currentTimeMillis();
//                        getContestDetails();
////                        CBit.getSocketUtils().getmSocket().emit(SocketUtils.EVENT_CONTEST_DETAILS, request, contestDetails);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    } catch (UnsupportedEncodingException e) {
//                        e.printStackTrace();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                } else if (socketConnectionEvent.getMessage().equals("disconnected")) {
//                   /* if(Utils.isNetworkAvailable(context)){
//                        Utils.showToast(context,"Network Availalble");
//                    }else{
//                        Utils.showToast(context,"No Network Availalble");
//                    }*/
//                } else if (socketConnectionEvent.getMessage().equals("Error connecting")) {
//                    if (customDialog == null) {
//                        customDialog = new CustomDialog();
//                        customDialog.displayProgress(context, getString(R.string.connecting));
//                    } else {
//                        if (!customDialog.progressDialog.isShowing())
//                            customDialog.displayProgress(context, getString(R.string.connecting));
//                    }
//                }
//            }
//        });
//
//    }
//
//    private void getContestDetails() {
//        JSONObject jsonObject = new JSONObject();
//        byte[] data;
//        String request = "";
//        try {
//            jsonObject.put("contest_id", contest_id);
//            request = jsonObject.toString();
//            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, getString(R.string.crypt_pass));
//            data = request.getBytes(StandardCharsets.UTF_8);
//            request = Base64.encodeToString(data, Base64.DEFAULT);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        Call<ResponseBody> call = APIClient.getInstance()
//                .contestDetails(sessionUtil.getToken(), sessionUtil.getId(), request);
//        //.contestDetails(sessionUtil.getToken(),sessionUtil.getName(),contestId);
//        NewApiCall newApiCall = new NewApiCall();
//        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
//            @Override
//            public void success(String responseData) {
//                pbProgress.setVisibility(View.GONE);
//                linearContent.setVisibility(View.VISIBLE);
//                PrintLog.e(TAG, "contestDetails encrypt " + responseData);
//                String res = null;
//                PrintLog.e(TAG, "contestDetails decrypt " + res);
//                Gson gson = new Gson();
//                cdm =  gson.fromJson(responseData, ContestDetailsModel.class);
//                if (cdm.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
//
//                    toolbar_title.setText(cdm.getContent().getName());
//                    ticketAdapter.setViewType(cdm.getContent().getType());
//                    ticketAdapter.setGameStatus(cdm.getContent().getGameStatus());
//                    ticketAdapter.setMinAns(cdm.getContent().getAnsRangeMin());
//                    ticketAdapter.setMaxAns(cdm.getContent().getAnsRangeMax());
//                    if (cdm.getContent().getGameStatus().equalsIgnoreCase(Utils.GAME_END)) {
//                        if (mp != null && mp.isPlaying()) {
//                            mp.release();
//                            mp = null;
//                        }
//                        EventBus.getDefault().post(new UpdateMyContestEvent());
//                        Intent intent = new Intent(context, GameResultActivity.class);
//                        intent.putExtra(GameResultActivity.CONTEST_ID, contest_id);
//                        intent.putExtra(GameResultActivity.IS_REMINDER, isReminderScreen);
//                        startActivity(intent);
//                        finish();
//                    } else if (cdm.getContent().getGameStatus().equalsIgnoreCase(Utils.GAME_START)) {
//
//                        tvRemainingText.setVisibility(View.GONE);
//                        tvText.setVisibility(View.VISIBLE);
//                        //Toast.makeText(context, "call start", Toast.LENGTH_SHORT).show();
//                        tvNineLockNow.setAlpha(1f);
//                        tvNineLockNow.setEnabled(true);
//                        handler.removeCallbacks(runnable);
//                        List<BoxJson> boxJsonList = cdm.getContent().getBoxJson();
//                        List<BoxJson> tempJsonList = new ArrayList<>();
//                        if (bricksColorModel.size() > 0) {
//                            for (int i = 0; i < bricksColorModel.size(); i++) {
//                                HashMap<String, Integer> map = bricksColorModel.get(i);
//                                //boxJsonList.set(i, boxJsonList.get(map.get("index")));
//                                tempJsonList.add(i, boxJsonList.get(map.get("index")));
//                            }
//                        } else {
//                            tempJsonList.addAll(boxJsonList);
//                        }
//
//                        bricksAdapter = new BricksAdapter(context, tempJsonList, true);
//                        rvBricks.setAdapter(bricksAdapter);
//                        ticketList.clear();
//                        //ticketList.addAll(cdm.getContent().getTickets());
//                        for (Ticket ticket : cdm.getContent().getTickets()) {
//                            if (ticket.getIsPurchased() != 0) {
//                                ticketList.add(ticket);
//                            }
//                        }
//                        ticketAdapter.notifyDataSetChanged();
//                        if (startingTime != null) {
//                            startingTime.cancel();
//                            startingTime = null;
//                        }
//
//
//                        rdBlue.setClickable(true);
//                        rdDraw.setClickable(true);
//                        rdRed.setClickable(true);
//
//                        rdZero.setClickable(true);
//                        rdOne.setClickable(true);
//                        rdTwo.setClickable(true);
//                        rdThree.setClickable(true);
//                        rdFour.setClickable(true);
//                        rdFive.setClickable(true);
//                        rdSix.setClickable(true);
//                        rdSeven.setClickable(true);
//                        rdEight.setClickable(true);
//                        rdNine.setClickable(true);
//
//                        rdRdb.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//                                                             @Override
//                                                             public void onCheckedChanged(RadioGroup group, int checkedId) {
//                                                                 RadioButton radioButton = findViewById(checkedId);
//                                                                 //Toast.makeText(getBaseContext(), radioButton.getText(), Toast.LENGTH_SHORT).show();
//                                                                 SelectedDisplayView = String.valueOf(radioButton.getText());
//                                                             }
//                                                         }
//                        );
//                        tvNineLockNow.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                if (!cdm.getContent().getGameStatus().equals(Utils.GAME_NOT_START)) {
//                                    Log.d(TAG, "visibilitynine>>: " + linNine.getVisibility());
//                                    Log.d(TAG, "visibilityrdb>>: " + linRdb.getVisibility());
//                                    if (linNine.getVisibility() == View.VISIBLE) {
//                                        if (rdgZero.getCheckedRadioButtonId() == -1 && rdgFive.getCheckedRadioButtonId() == -1) {
//                                            Utils.showToast(context, "Please Select Any Number");
//                                        } else {
//                                            tvNineLockNow.setAlpha(.5f);
//                                            tvNineLockNow.setEnabled(false);
//                                            setALLAnsLock(SelectedDisplayView);
//                                            imgLock.setVisibility(View.VISIBLE);
//                                            waitingPopupforLock();}
//                                    }
//                                    if (linRdb.getVisibility() == View.VISIBLE) {
//                                        if (rdRdb.getCheckedRadioButtonId() == -1) {
//                                            Utils.showToast(context, "Please Select Any Number");
//                                        } else {
//                                            tvNineLockNow.setAlpha(.5f);
//                                            tvNineLockNow.setEnabled(false);
//                                            setALLAnsLock(SelectedDisplayView);
//                                            imgLock.setVisibility(View.VISIBLE);
//                                            waitingPopupforLock();
//                                        }
//                                    }
//                                } else {
//                                    Utils.showToast(context, "Game Not Started");
//                                }
//                            }
//                        });
//                        tvText.setText(String.valueOf(cdm.getContent().getDuration() - differenceSecond));
//                        startingTime = new CountDown(((cdm.getContent().getDuration() - differenceSecond) * 1000), 1000) {
//                            @Override
//                            public void onTick(final long remainingMillSec) {
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        try {
//                                            tvText.setText(new SimpleDateFormat("ss").format(remainingMillSec));
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                        }
//                                    }
//                                });
//                            }
//
//                            @Override
//                            public void onFinish() {
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        try {
//                                            tvText.setText("00");
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                        }
//                                    }
//                                });
//                            }
//                        };
//                        startingTime.start();
//                    } else {
//                        tvNineLockNow.setAlpha(.5f);
//                        tvNineLockNow.setEnabled(false);
//                        tvText.setVisibility(View.GONE);
//                        tvRemainingText.setVisibility(View.VISIBLE);
//                        long mill = Utils.convertMillSeconds(cdm.getContent().getStartDate(),
//                                cdm.getContent().getCurrentTime());
//                        if (remainingTime != null) {
//                            remainingTime.cancel();
//                            remainingTime = null;
//                        }
//                        remainingTime = new CountDown(mill, 1000) {
//                            @Override
//                            public void onTick(final long l) {
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        try {
//                                            tvRemainingText.setText("Game Starts in ");
//                                            tvRemainingText.append(
//                                                    String.format("%02d:%02d:%02d",
//                                                            TimeUnit.MILLISECONDS.toHours(l),
//                                                            TimeUnit.MILLISECONDS.toMinutes(l) -
//                                                                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(l)), // The change is in this line
//                                                            TimeUnit.MILLISECONDS.toSeconds(l) -
//                                                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(l)))
//                                            );
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                        }
//                                    }
//                                });
//                            }
//
//                            @Override
//                            public void onFinish() {
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        new Handler().postDelayed(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                if (!isGameStart) {
//                                                    //Sometime rare case when not getting start event
//                                                    try {
//                                                        JSONObject object = new JSONObject();
//                                                        object.put("userId", sessionUtil.getId());
//                                                        object.put("contestId", contest_id);
//                                                        byte[] data;
//                                                        String request = "";
//                                                        request = object.toString();
//                                                        PrintLog.e("TAG", "Game Socket Connect " + contest_id + " " + request);
//                                                        request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, context.getString(R.string.crypt_pass));
//                                                        data = request.getBytes(StandardCharsets.UTF_8);
//                                                        request = Base64.encodeToString(data, Base64.DEFAULT);
//                                                        startMill = System.currentTimeMillis();
//                                                        getContestDetails();
//                                                       // CBit.getSocketUtils().getmSocket().emit(SocketUtils.EVENT_CONTEST_DETAILS, request, contestDetails);
//                                                    } catch (JSONException e) {
//                                                        e.printStackTrace();
//                                                    } catch (UnsupportedEncodingException e) {
//                                                        e.printStackTrace();
//                                                    } catch (Exception e) {
//                                                        e.printStackTrace();
//                                                    }
//                                                }
//                                            }
//                                        }, 100); // TODO: 05/08/20 Change 1500 to 100
//                                    }
//                                });
//
//                            }
//                        };
//                        remainingTime.start();
//                        //Game Not Start
//                        bricksItems.clear();
//                        bricksColorModel.clear();
//                        bricksAdapter = new BricksAdapter(context, bricksItems, bricksColorModel);
//                        rvBricks.setAdapter(bricksAdapter);
//                        ticketList.clear();
//                        for (Ticket ticket : cdm.getContent().getTickets()) {
//                            if (ticket.getIsPurchased() != 0) {
//                                ticketList.add(ticket);
//                            }
//                        }
//
//                        ticketAdapter.notifyDataSetChanged();
//                        if (cdm.getContent().getLevel() == 1) {
//                            inItBricks(8, cdm.getContent().getGameMode(), cdm.getContent().getAnsRangeMin(), cdm.getContent().getAnsRangeMax());
//
//                        } else if (cdm.getContent().getLevel() == 2) {
//                            inItBricks(16, cdm.getContent().getGameMode(), cdm.getContent().getAnsRangeMin(), cdm.getContent().getAnsRangeMax());
//
//                        } else {
//                            inItBricks(32, cdm.getContent().getGameMode(), cdm.getContent().getAnsRangeMin(), cdm.getContent().getAnsRangeMax());
//
//                        }
//                        handler.removeCallbacks(runnable);
//                        isHandlerPost = handler.post(runnable);
//                    }
//                }
//            }
//
//            @Override
//            public void failure(String responseData) {
//
//            }
//        });
//    }
///* ---------- End Method ----------*/
//    private Runnable runnable = new Runnable() {
//        @Override
//        public void run() {
//            // Insert custom code here
//            // inItChange();
//            inIt();
//            // Repeat every 1 seconds
//            handler.postDelayed(runnable, 500);
//        }
//    };
//
//    @OnClick(R.id.ivBack)
//    protected void ivBackClick() {
//        onBackPressed();
//    }
//
//    @Override
//    public void onBackPressed() {
//        if (isReminderScreen) {
//            Intent intent = new Intent(context, HomeActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(intent);
//            finish();
//        } else {
//            super.onBackPressed();
//        }
//    }
//
//    @OnClick(R.id.ivInfo)
//    protected void ivInfoClick() {
//        openInfoPopup();
//    }
//
//    private void openInfoPopup() {
//        final Dialog dialog = new Dialog(this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setCancelable(true);
//        dialog.setContentView(R.layout.dialog_info);
//
//        if (dialog.getWindow() != null) {
//            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//            Window window = dialog.getWindow();
//            lp.copyFrom(window.getAttributes());
//            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//            DisplayMetrics metrics = getResources().getDisplayMetrics();
//            lp.width = (int) (metrics.widthPixels * 0.90);
//            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
//            window.setAttributes(lp);
//        }
//        LinearLayout linearRoot = dialog.findViewById(R.id.linearRoot);
//        linearRoot.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//       /* ImageView ivClose = dialog.findViewById(R.id.ivClose);
//        ivClose.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//            }
//        });*/
//
//        dialog.show();
//    }
//
//    private void inIt() {
//        for (int i = 0; i < 4; i++) {
//            HashMap<String, Integer> temp = bricksColorModel.get(bricksColorModel.size() - 1);
//            bricksColorModel.remove(bricksColorModel.size() - 1);
//            bricksColorModel.add(0, temp);
//        }
//        Collections.shuffle(bricksItems);
//        bricksAdapter.notifyDataSetChanged();
//    }
//
//    public static int rand(int min, int max) {
//        if (min > max || (max - min + 1 > Integer.MAX_VALUE)) {
//            throw new IllegalArgumentException("Invalid range");
//        }
//
//        return new Random().nextInt(max - min + 1) + min;
//    }
//
//    private void inItBricks(int totalItem, String gameMode, int min, int max) {
//
//        HashMap<String, Integer> map = new HashMap<>();
//        if (totalItem == 8) {
//            while (bricksItems.size() < 8) {
//                bricksItems.add(Math.abs(rand(min, max)));
//            }
//            map.put("color", R.color.color_green);
//            map.put("index", 0);
//            bricksColorModel.add(map);
//
//            map = new HashMap<>();
//            map.put("color", R.color.color_blue);
//            map.put("index", 1);
//            bricksColorModel.add(map);
//
//            map = new HashMap<>();
//            map.put("color", R.color.color_green);
//            map.put("index", 2);
//            bricksColorModel.add(map);
//
//            map = new HashMap<>();
//            map.put("color", R.color.color_red);
//            map.put("index", 3);
//            bricksColorModel.add(map);
//
//            map = new HashMap<>();
//            map.put("color", R.color.color_blue);
//            map.put("index", 4);
//            bricksColorModel.add(map);
//
//            map = new HashMap<>();
//            map.put("color", R.color.color_green);
//            map.put("index", 5);
//            bricksColorModel.add(map);
//
//            map = new HashMap<>();
//            map.put("color", R.color.color_red);
//            map.put("index", 6);
//            bricksColorModel.add(map);
//
//            map = new HashMap<>();
//            map.put("color", R.color.color_green);
//            map.put("index", 7);
//            bricksColorModel.add(map);
//
//        } else if (totalItem == 16) {
//
//            while (bricksItems.size() < 16) {
//                bricksItems.add(Math.abs(rand(min, max)));
//            }
//
//            map.put("color", R.color.color_green);
//            map.put("index", 0);
//            bricksColorModel.add(map);
//
//            map = new HashMap<>();
//            map.put("color", R.color.color_blue);
//            map.put("index", 1);
//            bricksColorModel.add(map);
//
//            map = new HashMap<>();
//            map.put("color", R.color.color_green);
//            map.put("index", 2);
//            bricksColorModel.add(map);
//
//            map = new HashMap<>();
//            map.put("color", R.color.color_red);
//            map.put("index", 3);
//            bricksColorModel.add(map);
//
//            map = new HashMap<>();
//            map.put("color", R.color.color_blue);
//            map.put("index", 4);
//            bricksColorModel.add(map);
//
//            map = new HashMap<>();
//            map.put("color", R.color.color_green);
//            map.put("index", 5);
//            bricksColorModel.add(map);
//
//            map = new HashMap<>();
//            map.put("color", R.color.color_red);
//            map.put("index", 6);
//            bricksColorModel.add(map);
//
//            map = new HashMap<>();
//            map.put("color", R.color.color_green);
//            map.put("index", 7);
//            bricksColorModel.add(map);
//
//            map = new HashMap<>();
//            map.put("color", R.color.color_green);
//            map.put("index", 8);
//            bricksColorModel.add(map);
//
//            map = new HashMap<>();
//            map.put("color", R.color.color_red);
//            map.put("index", 9);
//            bricksColorModel.add(map);
//
//            map = new HashMap<>();
//            map.put("color", R.color.color_green);
//            map.put("index", 10);
//            bricksColorModel.add(map);
//
//            map = new HashMap<>();
//            map.put("color", R.color.color_blue);
//            map.put("index", 11);
//            bricksColorModel.add(map);
//
//            map = new HashMap<>();
//            map.put("color", R.color.color_red);
//            map.put("index", 12);
//            bricksColorModel.add(map);
//
//            map = new HashMap<>();
//            map.put("color", R.color.color_green);
//            map.put("index", 13);
//            bricksColorModel.add(map);
//
//            map = new HashMap<>();
//            map.put("color", R.color.color_blue);
//            map.put("index", 14);
//            bricksColorModel.add(map);
//
//            map = new HashMap<>();
//            map.put("color", R.color.color_green);
//            map.put("index", 15);
//            bricksColorModel.add(map);
//        } else {
//
//            while (bricksItems.size() < 32) {
//                bricksItems.add(Math.abs(rand(min, max)));
//            }
//
//
//            map.put("color", R.color.color_green);
//            map.put("index", 0);
//            bricksColorModel.add(map);
//
//            map = new HashMap<>();
//            map.put("color", R.color.color_blue);
//            map.put("index", 1);
//            bricksColorModel.add(map);
//
//            map = new HashMap<>();
//            map.put("color", R.color.color_green);
//            map.put("index", 2);
//            bricksColorModel.add(map);
//
//            map = new HashMap<>();
//            map.put("color", R.color.color_red);
//            map.put("index", 3);
//            bricksColorModel.add(map);
//
//            map = new HashMap<>();
//            map.put("color", R.color.color_blue);
//            map.put("index", 4);
//            bricksColorModel.add(map);
//
//            map = new HashMap<>();
//            map.put("color", R.color.color_green);
//            map.put("index", 5);
//            bricksColorModel.add(map);
//
//            map = new HashMap<>();
//            map.put("color", R.color.color_red);
//            map.put("index", 6);
//            bricksColorModel.add(map);
//
//            map = new HashMap<>();
//            map.put("color", R.color.color_green);
//            map.put("index", 7);
//            bricksColorModel.add(map);
//
//            map = new HashMap<>();
//            map.put("color", R.color.color_green);
//            map.put("index", 8);
//            bricksColorModel.add(map);
//
//            map = new HashMap<>();
//            map.put("color", R.color.color_red);
//            map.put("index", 9);
//            bricksColorModel.add(map);
//
//            map = new HashMap<>();
//            map.put("color", R.color.color_green);
//            map.put("index", 10);
//            bricksColorModel.add(map);
//
//            map = new HashMap<>();
//            map.put("color", R.color.color_blue);
//            map.put("index", 11);
//            bricksColorModel.add(map);
//
//            map = new HashMap<>();
//            map.put("color", R.color.color_red);
//            map.put("index", 12);
//            bricksColorModel.add(map);
//
//            map = new HashMap<>();
//            map.put("color", R.color.color_green);
//            map.put("index", 13);
//            bricksColorModel.add(map);
//
//            map = new HashMap<>();
//            map.put("color", R.color.color_blue);
//            map.put("index", 14);
//            bricksColorModel.add(map);
//
//            map = new HashMap<>();
//            map.put("color", R.color.color_green);
//            map.put("index", 15);
//            bricksColorModel.add(map);
//
//            map.put("color", R.color.color_green);
//            map.put("index", 16);
//            bricksColorModel.add(map);
//
//            map = new HashMap<>();
//            map.put("color", R.color.color_blue);
//            map.put("index", 17);
//            bricksColorModel.add(map);
//
//            map = new HashMap<>();
//            map.put("color", R.color.color_green);
//            map.put("index", 18);
//            bricksColorModel.add(map);
//
//            map = new HashMap<>();
//            map.put("color", R.color.color_red);
//            map.put("index", 19);
//            bricksColorModel.add(map);
//
//            map = new HashMap<>();
//            map.put("color", R.color.color_blue);
//            map.put("index", 20);
//            bricksColorModel.add(map);
//
//            map = new HashMap<>();
//            map.put("color", R.color.color_green);
//            map.put("index", 21);
//            bricksColorModel.add(map);
//
//            map = new HashMap<>();
//            map.put("color", R.color.color_red);
//            map.put("index", 22);
//            bricksColorModel.add(map);
//
//            map = new HashMap<>();
//            map.put("color", R.color.color_green);
//            map.put("index", 23);
//            bricksColorModel.add(map);
//
//            map = new HashMap<>();
//            map.put("color", R.color.color_green);
//            map.put("index", 24);
//            bricksColorModel.add(map);
//
//            map = new HashMap<>();
//            map.put("color", R.color.color_red);
//            map.put("index", 25);
//            bricksColorModel.add(map);
//
//            map = new HashMap<>();
//            map.put("color", R.color.color_green);
//            map.put("index", 26);
//            bricksColorModel.add(map);
//
//            map = new HashMap<>();
//            map.put("color", R.color.color_blue);
//            map.put("index", 27);
//            bricksColorModel.add(map);
//
//            map = new HashMap<>();
//            map.put("color", R.color.color_red);
//            map.put("index", 28);
//            bricksColorModel.add(map);
//
//            map = new HashMap<>();
//            map.put("color", R.color.color_green);
//            map.put("index", 29);
//            bricksColorModel.add(map);
//
//            map = new HashMap<>();
//            map.put("color", R.color.color_blue);
//            map.put("index", 30);
//            bricksColorModel.add(map);
//
//            map = new HashMap<>();
//            map.put("color", R.color.color_green);
//            map.put("index", 31);
//            bricksColorModel.add(map);
//
//        }
//
//
//        Collections.shuffle(bricksItems);
//
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        if (mPlayer != null && mPlayer.isPlaying()) {
//            mPlayer.pause();
//        }
//        if (mp != null && mp.isPlaying()) {
//            mp.release();
//            mp = null;
//        }
//        if (dialog != null && dialog.isShowing()) {
//            dialog.dismiss();
//            dialog = null;
//        }
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (mPlayer != null && calculatingDialog != null) {
//            if (calculatingDialog.isShowing())
//                mPlayer.start();
//        }
//    }
//
//    class CheckForConnection extends TimerTask {
//        @Override
//        public void run() {
//            new CheckNetwork().execute();
//
//        }
//    }
//
//    class CheckNetwork extends AsyncTask<Void, Void, Boolean> {
//
//        @Override
//        protected Boolean doInBackground(Void... voids) {
//            return isNetworkConnected();
//        }
//
//        @Override
//        protected void onPostExecute(Boolean aBoolean) {
//            super.onPostExecute(aBoolean);
//            if (aBoolean) {
//                hasInternetConnection();
//            } else {
//                hasNoInternetConnection();
//            }
//        }
//    }
//
//    private boolean isNetworkConnected() {
//        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//
//        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
//    }
//
//
//    public void hasInternetConnection() {
//        PrintLog.e(TAG, "Net Connected");
//    }
//
//
//    public void hasNoInternetConnection() {
//        PrintLog.e(TAG, "Net No Connected");
//        CBit.getSocketUtils().disConnect();
//        CBit.getSocketUtils().connect();
//    }
//
//
//    @Override
//    protected void onDestroy() {
//        try {
//            if (calculatingDialog != null && calculatingDialog.isShowing()) {
//                calculatingDialog.dismiss();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        if (mp != null) {
//            mp.release();
//            mp = null;
//        }
//        if (isHandlerPost) {
//            handler.removeCallbacks(runnable);
//        }
//        if (startingTime != null) {
//            startingTime.cancel();
//        }
//        if (remainingTime != null) {
//            remainingTime.cancel();
//        }
//        if (mPlayer != null) {
//            mPlayer.release();
//        }
//        mTimer.cancel();
//        EventBus.getDefault().unregister(this);
//        wl.release();
//        super.onDestroy();
//    }
//}
