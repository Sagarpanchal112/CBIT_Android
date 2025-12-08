package com.tfb.cbit.activities;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings;
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
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.google.gson.Gson;
import com.tfb.cbit.CBit;
import com.tfb.cbit.R;
import com.tfb.cbit.adapter.SpinerTicketSelectionAdapter;
import com.tfb.cbit.adapter.SpiningOptionsAdapter;
import com.tfb.cbit.adapter.ViewFliperItemAdapter;
import com.tfb.cbit.api.APIClient;
import com.tfb.cbit.api.ApiCallback;
import com.tfb.cbit.api.NewApiCall;
import com.tfb.cbit.databases.DatabaseHandler;
import com.tfb.cbit.databinding.ActivityAboutUsBinding;
import com.tfb.cbit.databinding.ActivitySpiningTicketSelectionBinding;
import com.tfb.cbit.event.ContestLiveUpdate;
import com.tfb.cbit.event.UpdateTicketFooterEvent;
import com.tfb.cbit.interfaces.OnItemClickListener;
import com.tfb.cbit.interfaces.OnItemLongClickListener;
import com.tfb.cbit.models.DefaultJoinTicket;
import com.tfb.cbit.models.contestdetails.ContestDetailsModel;
import com.tfb.cbit.models.contestdetails.Ticket;
import com.tfb.cbit.models.contestdetails.WinningOptions;
import com.tfb.cbit.models.dbmodel.UpcomingContestModel;
import com.tfb.cbit.models.join_contest.JoinContest;
import com.tfb.cbit.services.TimerService;
import com.tfb.cbit.utility.CountDown;
import com.tfb.cbit.utility.LogHelper;
import com.tfb.cbit.utility.PrintLog;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class SpiningTicketSelectionActivity extends BaseAppCompactActivity implements OnItemClickListener, OnItemLongClickListener {
    public static final String TAG = "TicketSelectionActivity";

    public Context context;
    public SpinerTicketSelectionAdapter ticketSelectionAdapter;
    public static final String CONTEST_ID = "contestid";
    public static final String CONTEST_NAME = "contestname";
    public static final String CONTEST_RTIME = "RTime";
    public static final String CONTEST_TYPE = "MyGames";
    public static final String CONTEST_MinRange = "MinRange";
    public static final String CONTEST_MaxRange = "MaxRange";
    public String contestId = "";
    public SessionUtil sessionUtil;
    public List<Ticket> ticketList = new ArrayList<>();
    public NewApiCall newApiCall;
    ContestDetailsModel gtm;
    ArrayList<String> iamgesList;
    public ArrayList<HashMap<String, Integer>> bricksColorModel = new ArrayList<>();
    public ArrayList<Integer> bricksItems = new ArrayList<>();
    public Handler handler = new Handler();
    public boolean isHandlerPost = false;
    public List<WinningOptions> winningOptionsList = new ArrayList<>();
    public CountDown remainingTime = null;
    AlarmManager alarmManager;
    public TimerService timerService;
    public boolean serviceBound;

    public List<DefaultJoinTicket.Contest> defaultJoinTicketList = new ArrayList<>();

    public SpiningOptionsAdapter optionsAdapter;
    public String CheckGameStatus = "";
    private ActivitySpiningTicketSelectionBinding binding;


    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySpiningTicketSelectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            finish();
            return;
        }
        getFromSdcard();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                if (Build.MANUFACTURER.equals("OPPO")) {
                    Intent[] AUTO_START_OPPO = {
                            new Intent().setComponent(new ComponentName("com.coloros.safe", "com.coloros.safe.permission.startup.StartupAppListActivity")),
                            new Intent().setComponent(new ComponentName("com.coloros.safe", "com.coloros.safe.permission.startupapp.StartupAppListActivity")),
                            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.startupapp.StartupAppListActivity")),
                            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.startup.StartupAppListActivity"))
                    };

                    for (Intent intents : AUTO_START_OPPO) {
                        if (getPackageManager().resolveActivity(intents, PackageManager.MATCH_DEFAULT_ONLY) != null) {
                            try {
                                startActivity(intents);
                                break;
                            } catch (Exception e) {
                                Log.d(TAG, "OPPO - Exception: " + e.toString());
                            }
                        }
                    }
                } else {
                    intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    intent.setData(Uri.parse("package:" + packageName));
                    startActivity(intent);
                }
            }
        }
        newApiCall = new NewApiCall();
        sessionUtil = new SessionUtil(context);
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);


        binding.tvWallet.setText(Utils.getCurrencyFormat(String.valueOf(Double.parseDouble(sessionUtil.getAmount()) + Double.parseDouble(sessionUtil.getWAmount()))));
        binding.toolbarTitle.setText(bundle.getString(CONTEST_NAME, ""));
        contestId = bundle.getString(CONTEST_ID, "");
        if (binding.rvTicketSelection.getItemAnimator() != null)
            ((SimpleItemAnimator) binding.rvTicketSelection.getItemAnimator()).setSupportsChangeAnimations(false);
        binding.rvTicketSelection.setLayoutManager(new LinearLayoutManager(context));
        //  rvTicketSelection.setNestedScrollingEnabled(false);
        ticketSelectionAdapter = new SpinerTicketSelectionAdapter(context, ticketList);
        binding.rvTicketSelection.setAdapter(ticketSelectionAdapter);
        ticketSelectionAdapter.setOnItemClickListener(this);
        ticketSelectionAdapter.setOnItemLongClickListener(this);

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        getTicketDetails(false);
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String newdate = sdf.format(date);
        Log.i("Start date", "==>" + CONTEST_RTIME);
        long mill = Utils.convertMillSeconds(bundle.getString(CONTEST_RTIME, ""), newdate);

        remainingTime = new CountDown(mill, 1000) {
            @Override
            public void onTick(final long l) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //  tvRemainingText.setText("Game Starts in ");
                            binding.tvRemainingText.setText(
                                    String.format("%02d:%02d:%02d",
                                            TimeUnit.MILLISECONDS.toHours(l),
                                            TimeUnit.MILLISECONDS.toMinutes(l) -
                                                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(l)), // The change is in this line
                                            TimeUnit.MILLISECONDS.toSeconds(l) -
                                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(l)))
                            );
                            setupSlot();
                            FadinAnimaiton(binding.rvI);
                            FadinAnimaiton(binding.rvIV);
                            FadinAnimaiton(binding.rvVII);
                            FadinAnimaiton(binding.rvX);
                            FadinAnimaiton(binding.rvXIII);
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
                        if (binding.tvRemainingText != null)
                            binding.tvRemainingText.setText("00 : 00 : 00");
                        // onBackPressed();
                    }
                });
            }
        };
        remainingTime.start();

        binding.ivBack.setOnClickListener(view -> {
            onBackPressed();
        });
        binding.chkall.setOnClickListener(view -> {
            chkallClick();
        });
        binding.tvJoinPrivateRoom.setOnClickListener(view -> {
            openJOinPrivateRoom();
        });
        binding.ivInfo.setOnClickListener(view -> {
            openInfoPopup();
        });
        binding.linearPay.setOnClickListener(view -> {
            linearPayClick();
        });
    }

    private void getdefaultJoinTicket() {
        Call<ResponseBody> call = APIClient.getInstance().getdefaultJoinTicket(sessionUtil.getToken(), sessionUtil.getId());
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Log.i("sucess :", responseData);
                Gson gson = new Gson();
                DefaultJoinTicket nm = gson.fromJson(responseData, DefaultJoinTicket.class);
                if (nm.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    defaultJoinTicketList.clear();
                    defaultJoinTicketList.addAll(nm.getContents().getContest());
                    for (int i = 0; i < defaultJoinTicketList.size(); i++) {
                        for (int j = 0; j < ticketList.size(); j++) {
                            if (defaultJoinTicketList.get(i).getPrice() == ticketList.get(j).getAmount()) {
                                if (defaultJoinTicketList.get(i).isSelected()) {
                                    if (ticketList.get(j).getIsPurchased() != 1) {
                                        ticketList.get(j).setSelected(true);
                                    }
                                }
                            }
                        }
                    }
                    ticketSelectionAdapter.notifyDataSetChanged();
                    int counter = 0;
                    double price = 0;
                    for (Ticket ticket : ticketList) {
                        if (ticket.isSelected()) {
                            counter++;
                            price = price + ticket.getAmount();
                        }
                    }

                    binding.tvSelected.setText(counter + " Contest Selected ");
                    binding.tvPay.setText(Utils.getTwoDecimalFormat(Float.parseFloat(String.valueOf(price))));
                    if (binding.tvSelected.getVisibility() != View.VISIBLE)
                        Utils.expand(binding.tvSelected);

                    //shocaseBuy();
                    binding.linearPay.setVisibility(View.VISIBLE);
                    if (price > (Double.parseDouble(sessionUtil.getAmount()) + Double.parseDouble(sessionUtil.getWAmount()))) {
                        binding.tvPayTitle.setText(getString(R.string.addtowallet));
                        binding.tvWallet.setTextColor(ContextCompat.getColor(context, R.color.color_red));
                    } else {
                        binding.tvPayTitle.setText(getString(R.string.pay));
                        binding.tvWallet.setTextColor(ContextCompat.getColor(context, R.color.color_green));
                    }
                    if (counter == 0) {
                        if (binding.tvSelected.getVisibility() == View.VISIBLE) {
                            Utils.collapse(binding.tvSelected);
                            binding.linearPay.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void failure(String responseData) {

                Log.d("TAG", "success: " + responseData);
            }
        });
    }

    protected void chkallClick() {
        int counter = 0;
        double price = 0;

        for (int i = 0; i < ticketList.size(); i++) {
            if (ticketList.get(i).getIsPurchased() == 0) {
                ticketList.get(i).setSelected(binding.chkall.isChecked());
            }

            ticketSelectionAdapter.notifyItemChanged(i);
        }

        for (Ticket ticket : ticketList) {
            if (ticket.isSelected()) {
                counter++;
                price = price + ticket.getAmount();
            }
        }

        binding.tvSelected.setText(counter + " Contest Selected ");
        binding.tvPay.setText(Utils.getTwoDecimalFormat(Float.parseFloat(String.valueOf(price))));
        if (binding.tvSelected.getVisibility() != View.VISIBLE)
            Utils.expand(binding.tvSelected);

        //shocaseBuy();
        binding.linearPay.setVisibility(View.VISIBLE);
        if (price > (Double.parseDouble(sessionUtil.getAmount()) + Double.parseDouble(sessionUtil.getWAmount()))) {
            binding.tvPayTitle.setText(getString(R.string.addtowallet));
            binding.tvWallet.setTextColor(ContextCompat.getColor(context, R.color.color_red));
        } else {
            binding.tvPayTitle.setText(getString(R.string.pay));
            binding.tvWallet.setTextColor(ContextCompat.getColor(context, R.color.color_green));
        }
        if (counter == 0) {
            if (binding.tvSelected.getVisibility() == View.VISIBLE) {
                Utils.collapse(binding.tvSelected);
                binding.linearPay.setVisibility(View.GONE);
            }
        }
    }

    public void openJOinPrivateRoom() {
        Intent intent = new Intent(context, JoinPrivateRoomActivity.class);
        startActivity(intent);
    }


    private void openInfoPopup() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_buy_tickets);

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
        Button btnOk = dialog.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    protected void linearPayClick() {
        if (binding.tvPayTitle.getText().toString().equals(getString(R.string.pay))) {
            //Popup Open
            openConfirmationPopup();
        } else {
            double price = 0;
            for (Ticket ticket : ticketList) {
                if (ticket.isSelected()) {
                    price = price + ticket.getAmount();
                }
            }
            price = price - (Double.parseDouble(sessionUtil.getAmount()) + Double.parseDouble(sessionUtil.getWAmount()));
            Intent intent = new Intent(context, AddMoneyActivity.class);
            intent.putExtra(AddMoneyActivity.AMOUNT_VALUE, String.valueOf(Math.ceil(price)));
            startActivity(intent);
        }
    }

    private void openConfirmationPopup() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.ticket_confirmation_popup);

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

        TextView tvPrimaryAmount = dialog.findViewById(R.id.tvPrimaryAmount);
        TextView tvSecondaryAmount = dialog.findViewById(R.id.tvSecondaryAmount);
        TextView tvTotalAmount = dialog.findViewById(R.id.tvTotalAmount);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        Button btnOk = dialog.findViewById(R.id.btnOk);
        double primaryPrice = Double.parseDouble(sessionUtil.getAmount());
        double SecondaryPrice = Double.parseDouble(sessionUtil.getWAmount());
        double price = 0;
        for (Ticket ticket : ticketList) {
            if (ticket.isSelected()) {
                price = price + ticket.getAmount();
            }
        }

        if (price <= primaryPrice) {
            tvPrimaryAmount.setText(Utils.getTwoDecimalFormat(Float.parseFloat(String.valueOf(price))));
            tvSecondaryAmount.setText(Utils.INDIAN_RUPEES + "0.00");
            tvTotalAmount.setText(Utils.getTwoDecimalFormat(Float.parseFloat(String.valueOf(price))));
        } else {
            double remainingPrice = price - primaryPrice;
            tvPrimaryAmount.setText(Utils.getTwoDecimalFormat(Float.parseFloat(String.valueOf(primaryPrice))));
            tvSecondaryAmount.setText(Utils.getTwoDecimalFormat(Float.parseFloat(String.valueOf(remainingPrice))));
            tvTotalAmount.setText(Utils.getTwoDecimalFormat(Float.parseFloat(String.valueOf(price))));
        }

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                StringBuilder ticketIds = new StringBuilder();
                for (Ticket ticket : ticketList) {
                    if (ticket.isSelected()) {
                        ticketIds.append(ticket.getContestPriceId()).append(",");
                    }
                }
                ticketIds = ticketIds.deleteCharAt(ticketIds.length() - 1);
                getJoinContest(ticketIds.toString());
            }
        });

        dialog.show();
    }

    private void getJoinContest(final String ticketsIds) {
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        String request = "";
        try {
            jsonObject.put("contest_id", contestId);
            jsonObject.put("tickets", ticketsIds);
            Log.d(TAG, "getJoinContest: " + contestId + ">>" + ticketsIds);
            request = jsonObject.toString();
            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, getString(R.string.crypt_pass));
            data = request.getBytes(StandardCharsets.UTF_8);
            request = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Call<ResponseBody> call = APIClient.getInstance().joinContest(sessionUtil.getToken(), sessionUtil.getId(), request);

        newApiCall.makeApiCall(context, true, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                DecimalFormat format = new DecimalFormat("0.##");
                Gson gson = new Gson();
                JoinContest joinContest = gson.fromJson(responseData, JoinContest.class);
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
                    if (!isAlreadyPurchased) {
                        if (gtm != null) {
                            String contestDate = "", servertime = "";
                            contestDate = gtm.getContent().getStartDate();
                            servertime = gtm.getContent().getCurrentTime();
                            PrintLog.e("TAG", "Start Date " + contestDate + " Server Time " + servertime);
                            UpcomingContestModel upcomingContestModel = new UpcomingContestModel(contestDate, servertime, 1, String.valueOf(gtm.getContent().getId()), gtm.getContent().getName(), gtm.getContent().getGame_type());
                            DatabaseHandler databaseHandler = new DatabaseHandler(context);
                            upcomingContestModel.setId(databaseHandler.addContest(upcomingContestModel));
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Calendar calendar = Calendar.getInstance();
                            try {
                                calendar.setTime(sdf.parse(upcomingContestModel.getContestDateTime()));
                            } catch (ParseException e) {
                            }
                            startAlert(upcomingContestModel);

                            startActivity(new Intent(context, PaymentSummaryActivity.class));
                            finish();
                        }
                    } else {
                        startActivity(new Intent(context, PaymentSummaryActivity.class));
                        finish();
                    }
                } else {
                    Utils.showToast(context, joinContest.getMessage());
                }
            }

            @Override
            public void failure(String responseData) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Log.isLoggable("TAG", Log.VERBOSE)) {
            Log.v("TAG", "Starting and binding service");
        }
        Intent i = new Intent(this, TimerService.class);
        startService(i);
        bindService(i, mConnection, 0);


    }

    public ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            if (Log.isLoggable("TAG", Log.VERBOSE)) {
                Log.v("TAG", "Service bound");
            }
            TimerService.RunServiceBinder binder = (TimerService.RunServiceBinder) service;

            timerService = binder.getService();

            serviceBound = true;
            // Ensure the service is not in the foreground when bound

            // Update the UI if the service is already running the timer
            if (timerService.isTimerRunning()) {

            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (Log.isLoggable("TAG", Log.VERBOSE)) {
                Log.v("TAG", "Service disconnect");
            }
            serviceBound = false;
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onStop() {
        super.onStop();

        if (serviceBound) {
            // If a timer is active, foreground the service, otherwise kill the service
            if (timerService.isTimerRunning()) {
                timerService.foreground();

            } else {
                stopService(new Intent(this, TimerService.class));
                //      Utils.appendLog("Alaram service stop==>" + Utils.getTodayDate());
            }
            // Unbind the service
            unbindService(mConnection);
            serviceBound = false;
        }

    }


    public void startAlert(UpcomingContestModel ucm) {
        long mill = 0;
        mill = Utils.convertMillSecondsReminder(ucm.getContestDateTime());
        timerService.startTimer(this, ucm.getContestID(), ucm.getContestName(), ucm.getContestType(), mill);
    }


    private void getTicketDetails(boolean isRecall) {
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        String request = "";
        try {
            jsonObject.put("contest_id", contestId);

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
        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                LogHelper.e(TAG, "getTicketDetails success :=> " + responseData);
                Gson gson = new Gson();
                gtm = gson.fromJson(responseData, ContestDetailsModel.class);
                ticketSelectionAdapter.setViewType(gtm.getContent().getType());
                ticketSelectionAdapter.setMinAns(gtm.getContent().getAnsRangeMin());
                ticketSelectionAdapter.setMaxAns(gtm.getContent().getAnsRangeMax());
                binding.gameNote.setText(gtm.getContent().getTitle());
                if (gtm.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    ticketList.clear();
                    System.out.println(gtm.getContent().getTickets());
                    ticketList.addAll(gtm.getContent().getTickets());

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

                    binding.linearMarque.setVisibility(View.VISIBLE);
                    //String sourceString = "The Total Tickets Sold and Max Winners are "+"<b>"+"Updating Live"+"</b>"+" !!!          ";
                    //tvMarque.setText(Html.fromHtml(sourceString));
                    binding.tvMarque.setText(gtm.getContent().getScrollerContent());

                    //Animation marquee = AnimationUtils.loadAnimation(context, R.anim.marquee);
                    // tvMarque.startAnimation(marquee);
                    binding.tvMarque.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                    binding.tvMarque.setMarqueeRepeatLimit(-1);
                    binding.tvMarque.setSelected(true);
                    binding.tvMarque.setHorizontallyScrolling(true);
                    binding.tvMarque.setFocusable(true);
                    binding.tvMarque.setFocusableInTouchMode(true);
                    winningOptionsList.addAll(gtm.getContent().getWinningOptions());

                    setupSlot();

                } else {
                    Utils.showToast(context, gtm.getMessage());
                }
                ticketSelectionAdapter.notifyDataSetChanged();
                if (!getIntent().getExtras().getString(CONTEST_TYPE, "").equals("MyGames")) {
                    getdefaultJoinTicket();
                }


            }

            @Override
            public void failure(String responseData) {

            }
        });
    }


    @Override
    public void onItemClick(View view, int position) {

        if (view.getId() == R.id.chkSelect) {
            int counter = 0;
            double price = 0;

            //if(counter>0){
            if (binding.chkall.isChecked()) {
                binding.chkall.setChecked(false);
            } else {

            }
            ticketList.get(position).setSelected(!ticketList.get(position).isSelected());
            ticketSelectionAdapter.notifyItemChanged(position);
            for (Ticket ticket : ticketList) {
                if (ticket.isSelected()) {
                    counter++;
                    price = price + ticket.getAmount();
                }
            }
           /* if(ticketList.get(position).isSelected())
            {
                counter++;
                price = price + ticketList.get(position).getAmount();
            }else{
                counter--;
                price = price - ticketList.get(position).getAmount();
            }*/
            binding.tvSelected.setText(counter + " Contest Selected ");
            binding.tvPay.setText(Utils.getTwoDecimalFormat(Float.parseFloat(String.valueOf(price))));
            if (binding.tvSelected.getVisibility() != View.VISIBLE)
                Utils.expand(binding.tvSelected);

            binding.linearPay.setVisibility(View.VISIBLE);
            if (price > (Double.parseDouble(sessionUtil.getAmount()) + Double.parseDouble(sessionUtil.getWAmount()))) {
                binding.tvPayTitle.setText(getString(R.string.addtowallet));
                binding.tvWallet.setTextColor(ContextCompat.getColor(context, R.color.color_red));
            } else {
                binding.tvPayTitle.setText(getString(R.string.pay));
                binding.tvWallet.setTextColor(ContextCompat.getColor(context, R.color.color_green));
            }
            if (counter == 0) {
                if (binding.tvSelected.getVisibility() == View.VISIBLE) {
                    Utils.collapse(binding.tvSelected);
                    binding.linearPay.setVisibility(View.GONE);
                }
            }
        } else {
            Intent intent = new Intent(context, JoinUserListActivity.class);
            intent.putExtra(JoinUserListActivity.CONTEST_NAME, gtm.getContent().getName());
            intent.putExtra(JoinUserListActivity.CONTEST_PRICE_ID, ticketList.get(position).getContestPriceId() + "");
            startActivity(intent);
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFooterUpdate(UpdateTicketFooterEvent updateTicketFooterEvent) {
        double price = 0;
        for (Ticket ticket : ticketList) {
            if (ticket.isSelected()) {
                price = price + ticket.getAmount();
            }
        }
        binding.tvWallet.setText(Utils.getCurrencyFormat(String.valueOf(Double.parseDouble(sessionUtil.getAmount()) + Double.parseDouble(sessionUtil.getWAmount()))));
        binding.tvPay.setText(Utils.getTwoDecimalFormat(Float.parseFloat(String.valueOf(price))));
        if (price > (Double.parseDouble(sessionUtil.getAmount()) + Double.parseDouble(sessionUtil.getWAmount()))) {
            binding.tvPayTitle.setText(getString(R.string.addtowallet));
            binding.tvWallet.setTextColor(ContextCompat.getColor(context, R.color.color_red));
        } else {
            binding.tvPayTitle.setText(getString(R.string.pay));
            binding.tvWallet.setTextColor(ContextCompat.getColor(context, R.color.color_green));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onContestLiveUpdate(ContestLiveUpdate contestLiveUpdate) {
        if (ticketList.size() > 0) {
            try {
                JSONArray jsonArray = new JSONArray(contestLiveUpdate.getResponse());
                if (jsonArray.length() > 0) {
                    JSONObject object = jsonArray.getJSONObject(0);
                    if (object.getInt("contestId") == gtm.getContent().getId()) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            for (int j = 0; j < ticketList.size(); j++) {
                                if (obj.getInt("contestPriceId") == ticketList.get(j).getContestPriceId()) {
                                    ticketList.get(j).setMaxWinners(obj.getInt("maxWinners"));
                                    ticketList.get(j).setTotalTickets(obj.getInt("totalTickets"));
                                    ticketList.get(j).setTotalWinnings(obj.getInt("totalWinnings"));

                                    ticketSelectionAdapter.notifyItemChanged(j);
                                }
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onItemLongClick(View view, int position) {
        boolean isSelected = false;
        for (Ticket ticket : ticketList) {
            if (ticket.isSelected()) {
                isSelected = true;
                break;
            }
        }

        if (!isSelected) {
            ticketList.get(position).setSelected(!ticketList.get(position).isSelected());
            ticketSelectionAdapter.notifyItemChanged(position);
            binding.tvSelected.setText("1 Contest Selected ");
            Utils.expand(binding.tvSelected);
            binding.linearPay.setVisibility(View.VISIBLE);
            binding.tvPay.setText(Utils.getTwoDecimalFormat(Float.parseFloat(String.valueOf(ticketList.get(position).getAmount()))));
            if (ticketList.get(position).getAmount() > Double.parseDouble(sessionUtil.getAmount())) {
                binding.tvPayTitle.setText(getString(R.string.addtowallet));
                binding.tvWallet.setTextColor(ContextCompat.getColor(context, R.color.color_red));
            } else {
                binding.tvPayTitle.setText(getString(R.string.pay));
                binding.tvWallet.setTextColor(ContextCompat.getColor(context, R.color.color_green));
            }
        }
    }


    @Override
    protected void onDestroy() {


        if (remainingTime != null) {
            remainingTime.cancel();
        }
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    public void setupSlot() {
        if (gtm.getContent().getRows() == 3)
            slot3By5();
        if (gtm.getContent().getRows() == 4)
            slot4By5();
        if (gtm.getContent().getRows() == 5)
            slot5By5();
    }

    public void slot3By5() {
        setUpRecyclr(binding.rvI, 0, 2);
        setUpRecyclr(binding.rvIV, 3, 5);
        setUpRecyclr(binding.rvVII, 6, 8);
        setUpRecyclr(binding.rvX, 9, 11);
        setUpRecyclr(binding.rvXIII, 12, 14);

    }

    public void slot4By5() {
        setUpRecyclr(binding.rvI, 0, 3);
        setUpRecyclr(binding.rvIV, 4, 7);
        setUpRecyclr(binding.rvVII, 8, 11);
        setUpRecyclr(binding.rvX, 12, 15);
        setUpRecyclr(binding.rvXIII, 16, 19);

    }

    public void slot5By5() {
        setUpRecyclr(binding.rvI, 0, 4);
        setUpRecyclr(binding.rvIV, 5, 9);
        setUpRecyclr(binding.rvVII, 10, 14);
        setUpRecyclr(binding.rvX, 15, 19);
        setUpRecyclr(binding.rvXIII, 20, 24);

    }

    public void setUpRecyclr(RecyclerView rv, int startPos, int endPos) {
        ArrayList<String> bricksItems = new ArrayList<>();
        // this is dynamic image load from local doenloaded logic
        String SDCardPath = getFilesDir().getAbsolutePath() + "/";
        for (int i = startPos; i <= endPos; i++) {
            bricksItems.add(SDCardPath + winningOptionsList.get(new Random().nextInt(winningOptionsList.size())).getImage());
            LogHelper.d("getFromSdcard ", SDCardPath + winningOptionsList.get(new Random().nextInt(winningOptionsList.size())).getImage());
        }

        rv.setLayoutManager(new LinearLayoutManager(context));
        ViewFliperItemAdapter ticketAdapter = new ViewFliperItemAdapter(context, bricksItems);
        rv.setAdapter(ticketAdapter);

    }

    public void FadinAnimaiton(RecyclerView img) {
        Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        img.startAnimation(aniFade);
    }

    public void getFromSdcard() {
        iamgesList = new ArrayList<String>();// list of file paths
        File[] listFile;

        File file = new File(getFilesDir().getAbsolutePath());

        if (file.isDirectory()) {
            listFile = file.listFiles();

            for (int i = 0; i < listFile.length; i++) {

                iamgesList.add(listFile[i].getAbsolutePath());
                LogHelper.d("Total Download ::: ", listFile[i].getAbsolutePath() + "");

            }
        }
    }
}
