package com.tfb.cbit.activities;

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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.google.gson.Gson;
import com.tfb.cbit.CBit;
import com.tfb.cbit.R;
import com.tfb.cbit.adapter.BricksAdapter;
import com.tfb.cbit.adapter.OptionsAdapter;
import com.tfb.cbit.adapter.TicketSelectionAdapter;
import com.tfb.cbit.api.APIClient;
import com.tfb.cbit.api.ApiCallback;
import com.tfb.cbit.api.NewApiCall;
import com.tfb.cbit.databases.DatabaseHandler;
import com.tfb.cbit.databinding.ActivityAboutUsBinding;
import com.tfb.cbit.databinding.ActivityTicketSelectionBinding;
import com.tfb.cbit.event.ContestLiveUpdate;
import com.tfb.cbit.event.UpdateTicketFooterEvent;
import com.tfb.cbit.interfaces.OnItemClickListener;
import com.tfb.cbit.interfaces.OnItemLongClickListener;
import com.tfb.cbit.models.DefaultJoinTicket;
import com.tfb.cbit.models.contestdetails.ContestDetailsModel;
import com.tfb.cbit.models.contestdetails.Ticket;
import com.tfb.cbit.models.dbmodel.UpcomingContestModel;
import com.tfb.cbit.models.join_contest.JoinContest;
import com.tfb.cbit.services.TimerService;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class TicketSelectionActivity extends BaseAppCompactActivity implements OnItemClickListener, OnItemLongClickListener {
    public static final String TAG = "TicketSelectionActivity";


    public Context context;
    public TicketSelectionAdapter ticketSelectionAdapter;
    public static final String CONTEST_ID = "contestid";
    public static final String CONTEST_NAME = "contestname";
    public static final String CONTEST_RTIME = "RTime";
    public static final String CONTEST_MinRange = "MinRange";
    public static final String CONTEST_MaxRange = "MaxRange";
    public static final String CONTEST_TYPE = "MyGames";
    public String contestId = "";
    public SessionUtil sessionUtil;
    public List<Ticket> ticketList = new ArrayList<>();
    public NewApiCall newApiCall;
    ContestDetailsModel gtm;
    public BricksAdapter bricksAdapter;
    public ArrayList<HashMap<String, Integer>> bricksColorModel = new ArrayList<>();
    public ArrayList<Integer> bricksItems = new ArrayList<>();
    public Handler handler = new Handler();
    public boolean isHandlerPost = false;
    public CountDown remainingTime = null;
    int minrange, maxrange;
    AlarmManager alarmManager;
    public static final String BUNDLE_EXTRA = "bundle_extra";
    public static final String ALARM_KEY = "alarm_key";
    public TimerService timerService;
    public boolean serviceBound;

    private List<DefaultJoinTicket.Contest> defaultJoinTicketList = new ArrayList<>();
    private ActivityTicketSelectionBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTicketSelectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            finish();
            return;
        }
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


        binding. tvWallet.setText(Utils.getCurrencyFormat(String.valueOf(Double.parseDouble(sessionUtil.getAmount()) + Double.parseDouble(sessionUtil.getWAmount()))));
        binding. toolbarTitle.setText(bundle.getString(CONTEST_NAME, ""));
        contestId = bundle.getString(CONTEST_ID, "");
        if (binding.rvTicketSelection.getItemAnimator() != null)
            ((SimpleItemAnimator) binding.rvTicketSelection.getItemAnimator()).setSupportsChangeAnimations(false);
        binding. rvTicketSelection.setLayoutManager(new LinearLayoutManager(context));
        ticketSelectionAdapter = new TicketSelectionAdapter(context, ticketList);
        binding.rvTicketSelection.setAdapter(ticketSelectionAdapter);
        ticketSelectionAdapter.setOnItemClickListener(this);
        ticketSelectionAdapter.setOnItemLongClickListener(this);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        binding. rvBricks.setLayoutManager(new GridLayoutManager(context, 4));

        bricksItems.clear();
        bricksColorModel.clear();
        bricksAdapter = new BricksAdapter(context, bricksItems, bricksColorModel);
        binding.rvBricks.setAdapter(bricksAdapter);

        minrange = bundle.getInt(CONTEST_MinRange, 0);
        maxrange = bundle.getInt(CONTEST_MaxRange, 0);
       // inItBricks(8, "", minrange, maxrange);


        getTicketDetails();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String newdate = sdf.format(date);
        long mill = Utils.convertMillSeconds(bundle.getString(CONTEST_RTIME, ""), newdate);

        remainingTime = new CountDown(mill, 1000) {
            @Override
            public void onTick(final long l) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            binding. tvRemainingText.setText("Game Starts in ");
                            binding. tvRemainingText.append(
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
                        if (binding.tvRemainingText != null)
                            binding. tvRemainingText.setText("00 : 00 : 00");
                        // onBackPressed();
                    }
                });
            }
        };
        remainingTime.start();
        binding. tvJoinPrivateRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, JoinPrivateRoomActivity.class);
                intent.putExtra(CONTEST_RTIME, getIntent().getStringExtra(CONTEST_RTIME));
                intent.putExtra("contest_id", contestId);
                startActivity(intent);
            }
        });
     /*   TutoShowcase.from(this)
                .setListener(new TutoShowcase.Listener() {
                    @Override
                    public void onDismissed() {

                    }
                })
                .setContentView(R.layout.tuto_showcase_tuto_sample)
                .setFitsSystemWindows(true)
                .on(R.id.chkall)
                .addCircle()
                .withBorder()
                .onClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                })
                .show();*/

        binding.ivBack.setOnClickListener(view -> {
            onBackPressed();
        });
        binding.chkall.setOnClickListener(view -> {
            chkallClick();
        });
        binding.ivInfo.setOnClickListener(view -> {
            openInfoPopup();
        });
        binding.linearPay.setOnClickListener(view -> {
            linearPayClick();
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
        binding. tvSelected.setText(counter + " Contests Selected ");

        //  tvSelected.setText("Selected " + counter);
        binding. tvPay.setText(Utils.getTwoDecimalFormat(Float.parseFloat(String.valueOf(price))));
        if (binding.tvSelected.getVisibility() != View.VISIBLE)
            Utils.expand(binding.tvSelected);

        //  shocaseBuy();
        binding.linearPay.setVisibility(View.VISIBLE);
        if (price > (Double.parseDouble(sessionUtil.getAmount()) + Double.parseDouble(sessionUtil.getWAmount()))) {
            binding. tvPayTitle.setText(getString(R.string.addtowallet));
            binding. tvWallet.setTextColor(ContextCompat.getColor(context, R.color.color_red));
        } else {
            binding. tvPayTitle.setText(getString(R.string.pay));
            binding. tvWallet.setTextColor(ContextCompat.getColor(context, R.color.color_green));
        }
        if (counter == 0) {
            if (binding.tvSelected.getVisibility() == View.VISIBLE) {
                Utils.collapse(binding.tvSelected);
                binding. linearPay.setVisibility(View.GONE);
            }
        }
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

                    binding. tvSelected.setText(counter + " Contests Selected ");
                    //  tvSelected.setText("Selected " + counter);
                    binding. tvPay.setText(Utils.getTwoDecimalFormat(Float.parseFloat(String.valueOf(price))));
                    if (binding.tvSelected.getVisibility() != View.VISIBLE)
                        Utils.expand(binding.tvSelected);

                    //shocaseBuy();
                    binding. linearPay.setVisibility(View.VISIBLE);
                    if (price > (Double.parseDouble(sessionUtil.getAmount()) + Double.parseDouble(sessionUtil.getWAmount()))) {
                        binding.  tvPayTitle.setText(getString(R.string.addtowallet));
                        binding.  tvWallet.setTextColor(ContextCompat.getColor(context, R.color.color_red));
                    } else {
                        binding. tvPayTitle.setText(getString(R.string.pay));
                        binding. tvWallet.setTextColor(ContextCompat.getColor(context, R.color.color_green));
                    }
                    if (counter == 0) {
                        if (binding.tvSelected.getVisibility() == View.VISIBLE) {
                            Utils.collapse(binding.tvSelected);
                            binding.  linearPay.setVisibility(View.GONE);
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

//    public void shocaseBuy() {
//        TutoShowcase.from(this)
//                .setListener(new TutoShowcase.Listener() {
//                    @Override
//                    public void onDismissed() {
//                        Toast.makeText(context, "Tutorial dismissed", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .setContentView(R.layout.tuto_showcase_tuto_buy_sample)
//                .setFitsSystemWindows(true)
//                .on(R.id.tvPayTitle)
//                .addCircle()
//                .withBorder()
//                .onClick(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                    }
//                })
//                .show();
//    }


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


        } else  if (totalItem == 8) {
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
            PrintLog.e(TAG, "getTicketDetails request :=> " + request);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Call<ResponseBody> call = APIClient.getInstance().contestDetails(sessionUtil.getToken(), sessionUtil.getId(), request);
        //.contestDetails(sessionUtil.getToken(),sessionUtil.getName(),contestId);
        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                PrintLog.e(TAG, "getTicketDetails success :=> " + responseData);
                Gson gson = new Gson();
                gtm = gson.fromJson(responseData, ContestDetailsModel.class);
                ticketSelectionAdapter.setViewType(gtm.getContent().getType());
                ticketSelectionAdapter.setMinAns(gtm.getContent().getAnsRangeMin());
                ticketSelectionAdapter.setMaxAns(gtm.getContent().getAnsRangeMax());
                binding.  gameNote.setText(gtm.getContent().getTitle());
                if (gtm.getContent().getLevel() == 0) {
                    inItBricks(4, gtm.getContent().getGameMode(), gtm.getContent().getAnsRangeMin(), gtm.getContent().getAnsRangeMax());
                } else if (gtm.getContent().getLevel() == 1) {
                    inItBricks(8, gtm.getContent().getGameMode(), gtm.getContent().getAnsRangeMin(), gtm.getContent().getAnsRangeMax());
                } else if (gtm.getContent().getLevel() == 2) {
                    inItBricks(16, gtm.getContent().getGameMode(), gtm.getContent().getAnsRangeMin(), gtm.getContent().getAnsRangeMax());
                } else {
                    inItBricks(32, gtm.getContent().getGameMode(), gtm.getContent().getAnsRangeMin(), gtm.getContent().getAnsRangeMax());
                }
                handler.removeCallbacks(runnable);
                isHandlerPost = handler.post(runnable);
                if (gtm.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    ticketList.clear();
                    System.out.println(gtm.getContent().getTickets());
                    ticketList.addAll(gtm.getContent().getTickets());
                    binding.  linearMarque.setVisibility(View.VISIBLE);
                    //String sourceString = "The Total Tickets Sold and Max Winners are "+"<b>"+"Updating Live"+"</b>"+" !!!          ";
                    //tvMarque.setText(Html.fromHtml(sourceString));
                    binding. tvMarque.setText(gtm.getContent().getScrollerContent());

                    //Animation marquee = AnimationUtils.loadAnimation(context, R.anim.marquee);
                    // tvMarque.startAnimation(marquee);
                    binding.  tvMarque.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                    binding.  tvMarque.setMarqueeRepeatLimit(-1);
                    binding.  tvMarque.setSelected(true);
                    binding. tvMarque.setHorizontallyScrolling(true);
                    binding. tvMarque.setFocusable(true);
                    binding. tvMarque.setFocusableInTouchMode(true);
                    for (int j = 0; j < ticketList.size(); j++) {
                        for (int i = 0; i < ticketList.get(j).getSlotes().size(); i++) {
                            if (ticketList.get(j).getSlotes().size() == 2 ||
                                    ticketList.get(j).getSlotes().size() == 3) {
                                if (ticketList.get(j).getSlotes().size() == 2) {
                                    binding. tvMinus.setText(ticketList.get(j).getSlotes().get(0).getDisplayValue());
                                    binding.  tvZero.setText(ticketList.get(j).getSlotes().get(1).getDisplayValue());
                                    binding.   tvPlus.setVisibility(View.GONE);
                                } else {
                                    binding.  tvMinus.setText(ticketList.get(j).getSlotes().get(0).getDisplayValue());
                                    binding.  tvZero.setText(ticketList.get(j).getSlotes().get(1).getDisplayValue());
                                    binding.  tvPlus.setText(ticketList.get(j).getSlotes().get(2).getDisplayValue());
                                    binding. tvPlus.setVisibility(View.VISIBLE);
                                    if (ticketList.get(j).getSlotes().get(0).getDisplayValue().equalsIgnoreCase("Red win")) {
                                        //  fixedHolder.tvMinus.setBackgroundColor(Color.parseColor("#fb0102"));
                                        binding. tvMinus.setBackgroundResource(R.drawable.bg_red);
                                        binding. tvMinus.setTextColor(Color.parseColor("#ffffff"));
                                        binding. linear3Options.setBackgroundColor(Color.parseColor("#E6E2E2"));
                                    }
                                    if (ticketList.get(j).getSlotes().get(2).getDisplayValue().equalsIgnoreCase("Blue win")) {
                                        //fixedHolder.tvPlus.setBackgroundColor(Color.parseColor("#0433ff"));
                                        binding.  tvPlus.setBackgroundResource(R.drawable.bg_blue);
                                        binding.  tvPlus.setTextColor(Color.parseColor("#ffffff"));
                                    }
                                }
                                binding.  linear3Options.setVisibility(View.VISIBLE);
                                binding.  rvOprions.setVisibility(View.GONE);
                            } else {
                                binding.  linear3Options.setVisibility(View.GONE);
                                binding.  rvOprions.setVisibility(View.VISIBLE);
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                                linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                                binding.  rvOprions.setLayoutManager(linearLayoutManager);
                                binding.  rvOprions.setAdapter(new OptionsAdapter(context, ticketList.get(j).getSlotes()));
                            }
                        }
                    }

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
            binding.tvSelected.setText(counter + " Contests Selected ");
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
        binding. tvPay.setText(Utils.getTwoDecimalFormat(Float.parseFloat(String.valueOf(price))));
        if (price > (Double.parseDouble(sessionUtil.getAmount()) + Double.parseDouble(sessionUtil.getWAmount()))) {
            binding.  tvPayTitle.setText(getString(R.string.addtowallet));
            binding.  tvWallet.setTextColor(ContextCompat.getColor(context, R.color.color_red));
        } else {
            binding. tvPayTitle.setText(getString(R.string.pay));
            binding. tvWallet.setTextColor(ContextCompat.getColor(context, R.color.color_green));
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
            binding.  tvSelected.setText("Selected 1");
            Utils.expand(binding.tvSelected);
            binding.  linearPay.setVisibility(View.VISIBLE);
            binding.  tvPay.setText(Utils.getTwoDecimalFormat(Float.parseFloat(String.valueOf(ticketList.get(position).getAmount()))));
            if (ticketList.get(position).getAmount() > Double.parseDouble(sessionUtil.getAmount())) {
                binding. tvPayTitle.setText(getString(R.string.addtowallet));
                binding. tvWallet.setTextColor(ContextCompat.getColor(context, R.color.color_red));
            } else {
                binding.  tvPayTitle.setText(getString(R.string.pay));
                binding.  tvWallet.setTextColor(ContextCompat.getColor(context, R.color.color_green));
            }
        }
    }



    @Override
    protected void onDestroy() {
        if (isHandlerPost) {
            handler.removeCallbacks(runnable);
        }
        if (remainingTime != null) {
            remainingTime.cancel();
        }
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
