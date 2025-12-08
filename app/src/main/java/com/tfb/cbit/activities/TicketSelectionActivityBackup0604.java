/*
package com.tfb.cbit.activities;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.tfb.cbit.CBit;
import com.tfb.cbit.R;
import com.tfb.cbit.adapter.BricksAdapter;
import com.tfb.cbit.adapter.TicketSelectionAdapter;
import com.tfb.cbit.api.APIClient;
import com.tfb.cbit.api.ApiCallback;
import com.tfb.cbit.api.NewApiCall;
import com.tfb.cbit.databases.DatabaseHandler;
import com.tfb.cbit.event.ContestLiveUpdate;
import com.tfb.cbit.event.UpdateTicketFooterEvent;
import com.tfb.cbit.interfaces.OnItemClickListener;
import com.tfb.cbit.interfaces.OnItemLongClickListener;
import com.tfb.cbit.models.contestdetails.ContestDetailsModel;
import com.tfb.cbit.models.contestdetails.Ticket;
import com.tfb.cbit.models.dbmodel.UpcomingContestModel;
import com.tfb.cbit.models.join_contest.JoinContest;
import com.tfb.cbit.receiver.AlarmReceiver;
import com.tfb.cbit.utility.CountDown;
import com.tfb.cbit.utility.PrintLog;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class TicketSelectionActivityBackup0604 extends BaseAppCompactActivity implements OnItemClickListener, OnItemLongClickListener {
    private static final String TAG = "TicketSelectionActivity";
    @BindView(R.id.toolbar_title)
    TextView toolbar_title;
    @BindView(R.id.tvSelected)
    TextView tvSelected;
    @BindView(R.id.linearPay)
    LinearLayout linearPay;
    @BindView(R.id.tvPayTitle)
    TextView tvPayTitle;
    @BindView(R.id.tvPay)
    TextView tvPay;
    @BindView(R.id.tvWallet)
    TextView tvWallet;
    @BindView(R.id.rvTicketSelection)
    SuperRecyclerView rvTicketSelection;
    @BindView(R.id.tvMarque)
    TextView tvMarque;
    @BindView(R.id.linearMarque)
    LinearLayout linearMarque;
    @BindView(R.id.chkall)
    CheckBox chkall;
    private Context context;
    private Unbinder unbinder = null;
    private TicketSelectionAdapter ticketSelectionAdapter;
    public static final String CONTEST_ID = "contestid";
    public static final String CONTEST_NAME = "contestname";
    public static final String CONTEST_RTIME = "RTime";
    public static final String CONTEST_MinRange = "MinRange";
    public static final String CONTEST_MaxRange = "MaxRange";
    private String contestId = "";
    private SessionUtil sessionUtil;
    private List<Ticket> ticketList = new ArrayList<>();
    private NewApiCall newApiCall;
    ContestDetailsModel gtm;

    @BindView(R.id.tv_RemainingText)
    TextView tvRemainingText;

    @BindView(R.id.rvBricks)
    RecyclerView rvBricks;
    private BricksAdapter bricksAdapter;
    private ArrayList<HashMap<String, Integer>> bricksColorModel = new ArrayList<>();
    private ArrayList<Integer> bricksItems = new ArrayList<>();
    private Handler handler = new Handler();
    private boolean isHandlerPost = false;

    private CountDown remainingTime = null;
    private String currentTime = "";
    CountDownTimer startGameRemaining;

    int minrange, maxrange;

    AlarmManager alarmManager;
    PendingIntent pendingIntent;

    private static final String BUNDLE_EXTRA = "bundle_extra";
    private static final String ALARM_KEY = "alarm_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_selection);
        unbinder = ButterKnife.bind(this);
        context = this;
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            finish();
            return;
        }

        newApiCall = new NewApiCall();
        sessionUtil = new SessionUtil(context);
        tvWallet.setText(Utils.getCurrencyFormat(String.valueOf(Double.parseDouble(sessionUtil.getAmount()) + Double.parseDouble(sessionUtil.getWAmount()))));
        toolbar_title.setText(bundle.getString(CONTEST_NAME, ""));
        contestId = bundle.getString(CONTEST_ID, "");
        if (rvTicketSelection.getRecyclerView().getItemAnimator() != null)
            ((SimpleItemAnimator) rvTicketSelection.getRecyclerView().getItemAnimator()).setSupportsChangeAnimations(false);
        rvTicketSelection.setLayoutManager(new LinearLayoutManager(context));
        ticketSelectionAdapter = new TicketSelectionAdapter(context, ticketList);
        rvTicketSelection.setAdapter(ticketSelectionAdapter);
        ticketSelectionAdapter.setOnItemClickListener(this);
        ticketSelectionAdapter.setOnItemLongClickListener(this);

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        rvBricks.setLayoutManager(new GridLayoutManager(context, 4));

        bricksItems.clear();
        bricksColorModel.clear();
        bricksAdapter = new BricksAdapter(context, bricksItems, bricksColorModel);
        rvBricks.setAdapter(bricksAdapter);

        minrange = bundle.getInt(CONTEST_MinRange, 0);
        maxrange = bundle.getInt(CONTEST_MaxRange, 0);
        inItBricks(8, "", minrange, maxrange);
        handler.removeCallbacks(runnable);
        isHandlerPost = handler.post(runnable);

        rvTicketSelection.showProgress();
        getTicketDetails();
        EventBus.getDefault().register(this);
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String newdate = sdf.format(date);
        long mill = Utils.convertMillSeconds(bundle.getString(CONTEST_RTIME, ""), newdate);

      */
/*  if (remainingTime != null) {
            remainingTime.cancel();
            remainingTime = null;
        }*//*

        remainingTime = new CountDown(mill, 1000) {
            @Override
            public void onTick(final long l) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            tvRemainingText.setText("Game Starts in ");
                            tvRemainingText.append(
                                    String.format("%02d:%02d:%02d",
                                            TimeUnit.MILLISECONDS.toHours(l),
                                            TimeUnit.MILLISECONDS.toMinutes(l) -
                                                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(l)), // The change is in this line
                                            TimeUnit.MILLISECONDS.toSeconds(l) -
                                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(l)))
                            );
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
                        if (tvRemainingText != null)
                            tvRemainingText.setText("00 : 00 : 00");
                        // onBackPressed();
                    }
                });
            }
        };
        remainingTime.start();

    }


    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // Insert custom code here
            // inItChange();
            inIt();
            // Repeat every 1 seconds
            handler.postDelayed(runnable, 500);
        }
    };

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

       */
/* if(gameMode.equalsIgnoreCase("private")){
            int i=5;
            while (i<=95){
                bricksItems.add(i);
                i = i +5;
            }
            bricksItems.add(5);
        }else{
            int i=50;
            while (i<=950){
                bricksItems.add(i);
                i = i +50;
            }
            bricksItems.add(50);
        }*//*

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

    }

    @OnClick(R.id.ivBack)
    protected void ivBackClick() {
        onBackPressed();
    }

    @OnClick(R.id.chkall)
    protected void chkallClick() {
        int counter = 0;
        double price = 0;

        for (int i = 0; i < ticketList.size(); i++) {

            ticketList.get(i).setSelected(chkall.isChecked());
            ticketSelectionAdapter.notifyItemChanged(i);
        }

        for (Ticket ticket : ticketList) {
            if (ticket.isSelected()) {
                counter++;
                price = price + ticket.getAmount();
            }
        }

        tvSelected.setText("Selected " + counter);
        tvPay.setText(Utils.getTwoDecimalFormat(Float.parseFloat(String.valueOf(price))));
        if (tvSelected.getVisibility() != View.VISIBLE)
            Utils.expand(tvSelected);
        linearPay.setVisibility(View.VISIBLE);
        if (price > (Double.parseDouble(sessionUtil.getAmount()) + Double.parseDouble(sessionUtil.getWAmount()))) {
            tvPayTitle.setText(getString(R.string.addtowallet));
            tvWallet.setTextColor(ContextCompat.getColor(context, R.color.color_red));
        } else {
            tvPayTitle.setText(getString(R.string.pay));
            tvWallet.setTextColor(ContextCompat.getColor(context, R.color.color_green));
        }
        if (counter == 0) {
            if (tvSelected.getVisibility() == View.VISIBLE) {
                Utils.collapse(tvSelected);
                linearPay.setVisibility(View.GONE);
            }
        }
    }

    @OnClick(R.id.ivInfo)
    protected void ivInfoClick() {
        openInfoPopup();
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

    @OnClick(R.id.linearPay)
    protected void linearPayClick() {
        if (tvPayTitle.getText().toString().equals(getString(R.string.pay))) {
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
                //Log.d("TAG",ticketIds.toString());

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
        Call<ResponseBody> call = APIClient.getInstance()
                .joinContest(sessionUtil.getToken(), sessionUtil.getId(), request);
        // .joinContest(sessionUtil.getToken(),sessionUtil.getName(),contestId,ticketsIds);

        newApiCall.makeApiCall(context, true, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                DecimalFormat format = new DecimalFormat("0.##");
                Gson gson = new Gson();
                JoinContest joinContest = gson.fromJson(responseData, JoinContest.class);
                if (joinContest.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    //EventBus.getDefault().post(new UpdateUpcomingContestEvent());
                    //EventBus.getDefault().post(new UpdateMyContestEvent());
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
                            PrintLog.d(GameViewActivity.CONTESTID, gtm.getContent().getId() + " Ticket Selection Constest ID");
                            UpcomingContestModel upcomingContestModel = new UpcomingContestModel(contestDate, servertime, 1, String.valueOf(gtm.getContent().getId()), gtm.getContent().getName(), gtm.getContent().getGame_type());
                            DatabaseHandler databaseHandler = new DatabaseHandler(context);
                            upcomingContestModel.setId(databaseHandler.addContest(upcomingContestModel));
                            AlarmReceiver.setReminderAlarm(context, upcomingContestModel);

                        }
                    }
                    startActivity(new Intent(context, PaymentSummaryActivity.class));
                    finish();
                } else {
                    Utils.showToast(context, joinContest.getMessage());
                }
            }

            @Override
            public void failure(String responseData) {

            }
        });
    }

    private void getTicketDetails() {
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
                rvTicketSelection.showRecycler();
                Gson gson = new Gson();
                gtm = gson.fromJson(responseData, ContestDetailsModel.class);
                ticketSelectionAdapter.setViewType(gtm.getContent().getType());
                ticketSelectionAdapter.setMinAns(gtm.getContent().getAnsRangeMin());
                ticketSelectionAdapter.setMaxAns(gtm.getContent().getAnsRangeMax());
                if (gtm.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    ticketList.clear();
                    ticketList.addAll(gtm.getContent().getTickets());
                    linearMarque.setVisibility(View.VISIBLE);
                    //String sourceString = "The Total Tickets Sold and Max Winners are "+"<b>"+"Updating Live"+"</b>"+" !!!          ";
                    //tvMarque.setText(Html.fromHtml(sourceString));
                    tvMarque.setText(gtm.getContent().getScrollerContent());

                    //Animation marquee = AnimationUtils.loadAnimation(context, R.anim.marquee);
                    // tvMarque.startAnimation(marquee);
                    tvMarque.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                    tvMarque.setMarqueeRepeatLimit(-1);
                    tvMarque.setSelected(true);
                    tvMarque.setHorizontallyScrolling(true);
                    tvMarque.setFocusable(true);
                    tvMarque.setFocusableInTouchMode(true);
                } else {
                    Utils.showToast(context, gtm.getMessage());
                }
                ticketSelectionAdapter.notifyDataSetChanged();
            }

            @Override
            public void failure(String responseData) {

            }
        });
    }


    @Override
    public void onItemClick(View view, int position) {

        switch (view.getId()) {
            case R.id.chkSelect:
                int counter = 0;
                double price = 0;
        */
/*for(Ticket ticket : ticketList){
            if(ticket.isSelected()){
               counter++;
               price = price + ticket.getAmount();
            }
        }*//*

                //if(counter>0){
                if (chkall.isChecked()) {
                    chkall.setChecked(false);
                }
                ticketList.get(position).setSelected(!ticketList.get(position).isSelected());
                ticketSelectionAdapter.notifyItemChanged(position);
                for (Ticket ticket : ticketList) {
                    if (ticket.isSelected()) {
                        counter++;
                        price = price + ticket.getAmount();
                    }
                }
           */
/* if(ticketList.get(position).isSelected())
            {
                counter++;
                price = price + ticketList.get(position).getAmount();
            }else{
                counter--;
                price = price - ticketList.get(position).getAmount();
            }*//*

                tvSelected.setText(counter + " Contest Selected ");
                tvPay.setText(Utils.getTwoDecimalFormat(Float.parseFloat(String.valueOf(price))));
                if (tvSelected.getVisibility() != View.VISIBLE)
                    Utils.expand(tvSelected);
                linearPay.setVisibility(View.VISIBLE);
                if (price > (Double.parseDouble(sessionUtil.getAmount()) + Double.parseDouble(sessionUtil.getWAmount()))) {
                    tvPayTitle.setText(getString(R.string.addtowallet));
                    tvWallet.setTextColor(ContextCompat.getColor(context, R.color.color_red));
                } else {
                    tvPayTitle.setText(getString(R.string.pay));
                    tvWallet.setTextColor(ContextCompat.getColor(context, R.color.color_green));
                }
                if (counter == 0) {
                    if (tvSelected.getVisibility() == View.VISIBLE) {
                        Utils.collapse(tvSelected);
                        linearPay.setVisibility(View.GONE);
                    }
                }
                break;
            default:
                Intent intent = new Intent(context, JoinUserListActivity.class);
                intent.putExtra(JoinUserListActivity.CONTEST_NAME, gtm.getContent().getName());
                intent.putExtra(JoinUserListActivity.CONTEST_PRICE_ID, ticketList.get(position).getContestPriceId() + "");
                startActivity(intent);
                break;
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
        tvWallet.setText(Utils.getCurrencyFormat(String.valueOf(Double.parseDouble(sessionUtil.getAmount()) + Double.parseDouble(sessionUtil.getWAmount()))));
        tvPay.setText(Utils.getTwoDecimalFormat(Float.parseFloat(String.valueOf(price))));
        if (price > (Double.parseDouble(sessionUtil.getAmount()) + Double.parseDouble(sessionUtil.getWAmount()))) {
            tvPayTitle.setText(getString(R.string.addtowallet));
            tvWallet.setTextColor(ContextCompat.getColor(context, R.color.color_red));
        } else {
            tvPayTitle.setText(getString(R.string.pay));
            tvWallet.setTextColor(ContextCompat.getColor(context, R.color.color_green));
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
            tvSelected.setText("Selected 1");
            Utils.expand(tvSelected);
            linearPay.setVisibility(View.VISIBLE);
            tvPay.setText(Utils.getTwoDecimalFormat(Float.parseFloat(String.valueOf(ticketList.get(position).getAmount()))));
            if (ticketList.get(position).getAmount() > Double.parseDouble(sessionUtil.getAmount())) {
                tvPayTitle.setText(getString(R.string.addtowallet));
                tvWallet.setTextColor(ContextCompat.getColor(context, R.color.color_red));
            } else {
                tvPayTitle.setText(getString(R.string.pay));
                tvWallet.setTextColor(ContextCompat.getColor(context, R.color.color_green));
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        if (isHandlerPost) {
            handler.removeCallbacks(runnable);
        }
        if (remainingTime != null) {
            remainingTime.cancel();
        }
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
*/
