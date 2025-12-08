package com.tfb.cbit.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.tfb.cbit.CBit;
import com.tfb.cbit.R;
import com.tfb.cbit.adapter.AnyTicketSelectionAdapter;
import com.tfb.cbit.adapter.BricksAdapter;
import com.tfb.cbit.adapter.PaperChitTicketSelectionAdapter;
import com.tfb.cbit.api.APIClient;
import com.tfb.cbit.api.ApiCallback;
import com.tfb.cbit.api.NewApiCall;
import com.tfb.cbit.databinding.ActivityAboutUsBinding;
import com.tfb.cbit.databinding.ActivityPaperChitGameBinding;
import com.tfb.cbit.event.UpdateUpcomingContestEvent;
import com.tfb.cbit.interfaces.OnItemClickListener;
import com.tfb.cbit.interfaces.OnItemLongClickListener;
import com.tfb.cbit.models.anytimegame.AnyTimeGameResponse;
import com.tfb.cbit.models.anytimegame.Content;
import com.tfb.cbit.models.contestdetails.ContestDetailsModel;
import com.tfb.cbit.models.contestdetails.Ticket;
import com.tfb.cbit.utility.PrintLog;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class PaperChitGameActivity extends BaseAppCompactActivity implements OnItemClickListener, OnItemLongClickListener {
    private static final String TAG = "AnyTimeGameActivity";

    private Context context;
    private PaperChitTicketSelectionAdapter ticketSelectionAdapter;
    public static final String CONTEST_ID = "contestid";
    public static final String CONTEST_NAME = "contestname";
    public static final String CONTEST_RTIME = "RTime";
    public static final String CONTEST_MinRange = "MinRange";
    public static final String CONTEST_MaxRange = "MaxRange";
    private String contestId = "";
    private SessionUtil sessionUtil;
    private List<Ticket> ticketList = new ArrayList<>();
    private ArrayList<Ticket> selectedTicketList = new ArrayList<>();
    private List<Content> anyticketList = new ArrayList<>();
    private NewApiCall newApiCall;
    ContestDetailsModel gtm;

    private BricksAdapter bricksAdapter;
    private ArrayList<HashMap<String, Integer>> bricksColorModel = new ArrayList<>();
    private ArrayList<Integer> bricksItems = new ArrayList<>();
    private Handler handler = new Handler();
    private boolean isHandlerPost = false;
    int minrange, maxrange;
    private static final String BUNDLE_EXTRA = "bundle_extra";
    public int no_of_players = 0;
    private ActivityPaperChitGameBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaperChitGameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            finish();
            return;
        }
        binding.ivBack.setOnClickListener(view -> {
           backPressed();
        });
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

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
        binding. toolbarTitle.setText(bundle.getString(CONTEST_NAME, ""));
        if (binding.rvTicketSelection.getRecyclerView().getItemAnimator() != null)
            ((SimpleItemAnimator) binding.rvTicketSelection.getRecyclerView().getItemAnimator()).setSupportsChangeAnimations(false);
        binding.  rvTicketSelection.setLayoutManager(new LinearLayoutManager(context));
        ticketSelectionAdapter = new PaperChitTicketSelectionAdapter(context, ticketList);
        binding. rvTicketSelection.setAdapter(ticketSelectionAdapter);
        ticketSelectionAdapter.setOnItemClickListener(this);
        ticketSelectionAdapter.setOnItemLongClickListener(this);


        binding.rvBricks.setLayoutManager(new GridLayoutManager(context, 4));

        bricksItems.clear();
        bricksColorModel.clear();
        bricksAdapter = new BricksAdapter(context, bricksItems, bricksColorModel);
        binding. rvBricks.setAdapter(bricksAdapter);

        minrange = bundle.getInt(CONTEST_MinRange, 0);
        maxrange = bundle.getInt(CONTEST_MaxRange, 0);
        inItBricks(8, "", minrange, maxrange);
        handler.removeCallbacks(runnable);
        isHandlerPost = handler.post(runnable);

        binding.rvTicketSelection.showProgress();
        getAnyTimeDetails();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String newdate = sdf.format(date);
        long mill = Utils.convertMillSeconds(bundle.getString(CONTEST_RTIME, ""), newdate);

        binding.chkall.setOnClickListener(view -> {
            chkallClick() ;
        });
        binding.tvJoinPrivateRoom.setOnClickListener(view -> {
            Intent intent = new Intent(context, JoinPrivateRoomActivity.class);
            startActivity(intent);
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

        binding.  tvSelected.setText("Selected " + counter);
        if (binding.tvSelected.getVisibility() != View.VISIBLE)
            Utils.expand(binding.tvSelected);

        binding. linearPay.setVisibility(View.VISIBLE);
        if (price > (Double.parseDouble(sessionUtil.getAmount()) + Double.parseDouble(sessionUtil.getWAmount()))) {
            binding.tvPayTitle.setText(getString(R.string.addtowallet));
        } else {
            binding.  tvPayTitle.setText("Play");
        }
        if (counter == 0) {
            if (binding.tvSelected.getVisibility() == View.VISIBLE) {
                Utils.collapse(binding.tvSelected);
                binding.linearPay.setVisibility(View.GONE);
            }
        }
    }



    protected void linearPayClick() {
        if (binding.tvPayTitle.getText().toString().equals("Play")) {
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

    private void getAnyTimeDetails() {
        Call<ResponseBody> call = APIClient
                .getInstance()
                .getAnyTimeGameList(sessionUtil.getToken(), sessionUtil.getId(),"");
         newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                PrintLog.e(TAG, "getTicketDetails success :=> " + responseData);
                //  rvTicketSelection.showRecycler();
                Gson gson = new Gson();
                AnyTimeGameResponse anyTimeGameResponse = gson.fromJson(responseData, AnyTimeGameResponse.class);
                ticketList.clear();
                contestId = String.valueOf(anyTimeGameResponse.getContent().get(0).getContestID());
                no_of_players = anyTimeGameResponse.getContent().get(0).getNo_of_players();
                anyticketList.addAll(anyTimeGameResponse.getContent());
                getTicketDetails();


            }

            @Override
            public void failure(String responseData) {

            }
        });
    }

    @Subscribe
    public void onUpdateUpcomingContestEvent(UpdateUpcomingContestEvent updateUpcomingContestEvent) {
        getAnyTimeDetails();
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            inIt();
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
                    //    CBit.selectedTicketList.add(ticket);
                        ticketIds.append(ticket.getContestPriceId()).append(",");
                    }
                }
                ticketIds = ticketIds.deleteCharAt(ticketIds.length() - 1);
                Intent intent = new Intent(PaperChitGameActivity.this, AnyTimeGameViewActivity.class);
                intent.putExtra("contest_id", String.valueOf(contestId));
                intent.putExtra(AnyTimeGameViewActivity.CONTESTTITLE, String.valueOf(binding.toolbarTitle.getText().toString()));
                intent.putExtra(AnyTimeGameViewActivity.CONTESTTYPE, String.valueOf(anyticketList.get(0).getGame_type()));
                intent.putExtra("Tickets", ticketIds.toString());
                startActivity(intent);
                //  getJoinContest(ticketIds.toString());
            }
        });

        dialog.show();
    }


    @Override
    public void onStart() {
        super.onStart();
        if (Log.isLoggable("TAG", Log.VERBOSE)) {
            Log.v("TAG", "Starting and binding service");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onStop() {
        super.onStop();
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
                binding. rvTicketSelection.showRecycler();
                Gson gson = new Gson();
                gtm = gson.fromJson(responseData, ContestDetailsModel.class);
                binding.toolbarTitle.setText(gtm.getContent().getName());

                ticketSelectionAdapter.setViewType(gtm.getContent().getType());
                ticketSelectionAdapter.setMinAns(gtm.getContent().getAnsRangeMin());
                ticketSelectionAdapter.setMaxAns(gtm.getContent().getAnsRangeMax());
                ticketSelectionAdapter.setNoPlayer(no_of_players);
                if (gtm.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    ticketList.clear();
                    System.out.println(gtm.getContent().getTickets());
                    ticketList.addAll(gtm.getContent().getTickets());
                    binding. linearMarque.setVisibility(View.VISIBLE);
                    //String sourceString = "The Total Tickets Sold and Max Winners are "+"<b>"+"Updating Live"+"</b>"+" !!!          ";
                    //tvMarque.setText(Html.fromHtml(sourceString));
                    binding. tvMarque.setText(gtm.getContent().getScrollerContent());

                    //Animation marquee = AnimationUtils.loadAnimation(context, R.anim.marquee);
                    // tvMarque.startAnimation(marquee);
                    binding. tvMarque.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                    binding.  tvMarque.setMarqueeRepeatLimit(-1);
                    binding.  tvMarque.setSelected(true);
                    binding.  tvMarque.setHorizontallyScrolling(true);
                    binding.  tvMarque.setFocusable(true);
                    binding. tvMarque.setFocusableInTouchMode(true);
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

        if (view.getId() == R.id.chkSelect) {
            int counter = 0;
            double price = 0;
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
            if (binding.tvSelected.getVisibility() != View.VISIBLE)
                Utils.expand(binding.tvSelected);

            binding.linearPay.setVisibility(View.VISIBLE);
            if (price > (Double.parseDouble(sessionUtil.getAmount()) + Double.parseDouble(sessionUtil.getWAmount()))) {
                binding.tvPayTitle.setText(getString(R.string.addtowallet));
            } else {
                binding.tvPayTitle.setText("Play");
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
            binding.tvSelected.setText("Selected 1");
            Utils.expand(binding.tvSelected);
            binding.linearPay.setVisibility(View.VISIBLE);
            if (ticketList.get(position).getAmount() > Double.parseDouble(sessionUtil.getAmount())) {
                binding. tvPayTitle.setText(getString(R.string.addtowallet));
            } else {
                binding. tvPayTitle.setText("Play");
            }
        }
    }

    public void backPressed() {
        if (binding.mAlarmSetView.getVisibility() != View.VISIBLE)
            super.onBackPressed();
        else
            Toast.makeText(context, "Please set reminder!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {

        if (isHandlerPost) {
            handler.removeCallbacks(runnable);
        }
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}