package com.tfb.cbit.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.google.gson.Gson;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.tfb.cbit.CBit;
import com.tfb.cbit.R;
import com.tfb.cbit.adapter.AnyNumberTicketSelectionAdapter;
import com.tfb.cbit.adapter.AnyTicketSelectionAdapter;
import com.tfb.cbit.adapter.BricksAdapter;
import com.tfb.cbit.api.APIClient;
import com.tfb.cbit.api.ApiCallback;
import com.tfb.cbit.api.NewApiCall;
import com.tfb.cbit.databinding.ActivityAboutUsBinding;
import com.tfb.cbit.databinding.ActivityAnyTimeNumberGameBinding;
import com.tfb.cbit.event.UpdateUpcomingContestEvent;
import com.tfb.cbit.interfaces.OnItemClickListener;
import com.tfb.cbit.interfaces.OnItemLongClickListener;
import com.tfb.cbit.models.anytimegame.AnyTimeGameResponse;
import com.tfb.cbit.models.anytimegame.Content;
import com.tfb.cbit.models.contestdetails.ContestDetailsModel;
import com.tfb.cbit.models.contestdetails.Ticket;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
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

public class AnyTimeNumberGameActivity extends BaseAppCompactActivity implements OnItemClickListener, OnItemLongClickListener {

    private static final String TAG = "AnyTimeGameActivity";

    private Context context;
    public static final String CONTEST_ID = "contestid";
    public static final String CONTEST_NAME = "contestname";
    public static final String CONTEST_RTIME = "RTime";
    public static final String CONTEST_MinRange = "MinRange";
    public static final String CONTEST_MaxRange = "MaxRange";
    private String contestId = "";
    private SessionUtil sessionUtil;
    // private List<Ticket> ticketList = new ArrayList<>();
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
    public int no_of_players = 0, pending = 0, played = 0, max_winner = 0, max_winner_per = 0;
    //  ExamPagerAdapter examPagerAdapter;
    private String gameNo = "", contestPrizeId = "";
    private AnyNumberTicketSelectionAdapter ticketSelectionAdapter;
    SuperRecyclerView rvTicketSelection;

    String game_type;
    private ActivityAnyTimeNumberGameBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAnyTimeNumberGameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            finish();
            return;
        } else {
            game_type = bundle.getString("game_type");
            if (game_type.equals("rdb")) {
                binding.linSlot.setVisibility(View.GONE);
                //    toolbar_title.setText("Colour Colour");
            } else {
                binding.linSlot.setVisibility(View.VISIBLE);
                //  toolbar_title.setText("Number Slot");

            }
        }
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        newApiCall = new NewApiCall();
        sessionUtil = new SessionUtil(context);
        binding.rvBricks.setLayoutManager(new GridLayoutManager(context, 4));

        bricksItems.clear();
        bricksColorModel.clear();
        bricksAdapter = new BricksAdapter(context, bricksItems, bricksColorModel);
        binding.rvBricks.setAdapter(bricksAdapter);
        rvTicketSelection = findViewById(R.id.rvTicketSelection);

        if (rvTicketSelection.getRecyclerView().getItemAnimator() != null)
            ((SimpleItemAnimator) rvTicketSelection.getRecyclerView().getItemAnimator()).setSupportsChangeAnimations(false);
        rvTicketSelection.setLayoutManager(new LinearLayoutManager(context));
        ticketSelectionAdapter = new AnyNumberTicketSelectionAdapter(context, anyticketList);
        rvTicketSelection.setAdapter(ticketSelectionAdapter);
        ticketSelectionAdapter.setOnItemClickListener(this);
        ticketSelectionAdapter.setOnItemLongClickListener(this);

        // minrange = bundle.getInt(CONTEST_MinRange, 0);
        //  maxrange = bundle.getInt(CONTEST_MaxRange, 0);linearPay
        inItBricks(8, "", 2, 98);
        handler.removeCallbacks(runnable);
        isHandlerPost = handler.post(runnable);
        getAnyTimeDetails();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String newdate = sdf.format(date);
        //  long mill = Utils.convertMillSeconds(bundle.getString(CONTEST_RTIME, ""), newdate);

        binding.chkall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        binding.linearPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearPayClick();
            }
        });

        binding.tvPlayAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chkallClick();

            }
        });
        binding.ivBack.setOnClickListener(view -> {
            onBackPressed();
        });
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
        for (Content ticket : anyticketList) {
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

        double finalPrice = price;
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (finalPrice <= (Double.parseDouble(sessionUtil.getAmount()) + Double.parseDouble(sessionUtil.getWAmount()))) {
                    StringBuilder ticketIds = new StringBuilder();
                    StringBuilder contestId = new StringBuilder();
                    StringBuilder gameNo = new StringBuilder();
                    for (Content ticket : anyticketList) {
                        if (ticket.isSelected()) {
                            ticketIds.append(ticket.getContestpriceID()).append(",");
                            contestId.append(ticket.getContestID()).append(",");
                            gameNo.append(ticket.getGame_played()).append(",");
                        }
                    }
                    ticketIds = ticketIds.deleteCharAt(ticketIds.length() - 1);
                    contestId = contestId.deleteCharAt(contestId.length() - 1);
                    gameNo = gameNo.deleteCharAt(gameNo.length() - 1);

                    Intent intent = new Intent(AnyTimeNumberGameActivity.this, AnyTimeGameViewActivity.class);
                    intent.putExtra("contest_id", String.valueOf(contestId));
                    intent.putExtra("gameNo", gameNo + "");
                    intent.putExtra(AnyTimeGameHistoryActivity.CONTEST_PRIZE_ID, contestId + "");
                    //  intent.putExtra(GameViewActivity.CONTESTTITLE, String.valueOf(toolbar_title.getText().toString()));
                    intent.putExtra(GameViewActivity.CONTESTTYPE, String.valueOf(anyticketList.get(0).getGame_type()));
                    intent.putExtra("Tickets", ticketIds.toString());
                    startActivity(intent);
                    finish();

                } else {
                    double price = 0;
                    for (Content ticket : anyticketList) {
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
        });

        dialog.show();
    }

    protected void chkallClick() {
        int counter = 0;
        double price = 0;
        for (Content ticket : anyticketList) {
            ticket.setSelected(true);

        }
      /*  for (int i = 0; i < anyticketList.size(); i++) {
            if (anyticketList.get(i).getIsPurchased() == 0) {
                anyticketList.get(i).setSelected(chkall.isChecked());
            }

            ticketSelectionAdapter.notifyItemChanged(i);
        }
*/
        for (Content ticket : anyticketList) {
            if (ticket.isSelected()) {
                counter++;
                price = price + ticket.getAmount();
            }
        }

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
                //  linearPay.setVisibility(View.GONE);
            }
        }
        ticketSelectionAdapter.notifyDataSetChanged();
        //openConfirmationPopup();

    }

    protected void linearPayClick() {
        if (binding.tvPayTitle.getText().toString().equals("Play")) {
            //Popup Open
            int counter = 0;
            for (Content ticket : anyticketList) {
                if (ticket.isSelected()) {
                    counter++;
                }
            }
            if (counter == 0) {
                Utils.showToast(context, "Please select contest");

            } else {
                openConfirmationPopup();
            }
          /*  StringBuilder ticketIds = new StringBuilder();
            StringBuilder contestId = new StringBuilder();
            StringBuilder gameNo = new StringBuilder();
            CBit.selectedTicketList.clear();
            for (Content ticket : anyticketList) {
                if (ticket.isSelected()) {
                    CBit.selectedTicketList.add(ticket);
                    ticketIds.append(ticket.getContestpriceID()).append(",");
                    contestId.append(ticket.getContestID()).append(",");
                    gameNo.append(ticket.getGame_played()).append(",");
                }
            }
            ticketIds = ticketIds.deleteCharAt(ticketIds.length() - 1);
            contestId = contestId.deleteCharAt(contestId.length() - 1);
            gameNo = gameNo.deleteCharAt(gameNo.length() - 1);
            Intent intent = new Intent(AnyTimeGameActivity.this, AnyTimeGameViewActivity.class);
            intent.putExtra("contest_id", String.valueOf(contestId));
            intent.putExtra("gameNo", gameNo + "");
            intent.putExtra(AnyTimeGameHistoryActivity.CONTEST_PRIZE_ID, contestId + "");
            //  intent.putExtra(GameViewActivity.CONTESTTITLE, String.valueOf(toolbar_title.getText().toString()));
            intent.putExtra(GameViewActivity.CONTESTTYPE, String.valueOf(anyticketList.get(0).getGame_type()));
            intent.putExtra("Tickets", ticketIds.toString());
            startActivity(intent);
            finish();*/
        } else {
            double price = 0;
            for (Content ticket : anyticketList) {
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
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        String request = "";
        try {
            jsonObject.put("game_type", game_type);
            jsonObject.put("isSpinningMachine", "0");
            request = jsonObject.toString();
            Log.i("request", "=>" + request);
            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, getString(R.string.crypt_pass));
            data = request.getBytes(StandardCharsets.UTF_8);
            request = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Call<ResponseBody> call = APIClient
                .getInstance()
                .getAnyTimeGameList(sessionUtil.getToken(), sessionUtil.getId(), request);

        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                //  rvTicketSelection.showRecycler();
                Gson gson = new Gson();
                AnyTimeGameResponse anyTimeGameResponse = gson.fromJson(responseData, AnyTimeGameResponse.class);

                anyticketList.clear();
                no_of_players = anyTimeGameResponse.getContent().get(0).getNo_of_players();
                pending = anyTimeGameResponse.getContent().get(0).getPendingTickets();
                played = anyTimeGameResponse.getContent().get(0).getPlayers_played();
                anyticketList.addAll(anyTimeGameResponse.getContent());
                ticketSelectionAdapter.notifyDataSetChanged();
                binding.toolbarTitle.setText("Number Slots");
                binding.tvRemainingText.setText("Blue - Red");

                for (int i = 0; i < anyticketList.size(); i++) {
                    //  if(content.getContestID()==anyticketList.get(i).getContestID()){
                    contestId = String.valueOf(anyTimeGameResponse.getContent().get(i).getContestID());
                    contestPrizeId = String.valueOf(anyTimeGameResponse.getContent().get(i).getContestpriceID());
                    gameNo = String.valueOf(anyTimeGameResponse.getContent().get(i).getGame_played());

                    //}
                }
                //  getTicketDetails();


            }

            @Override
            public void failure(String responseData) {

            }
        });
    }

    /*  private void getAnyTimeGameContestList() {
          Call<ResponseBody> call = APIClient
                  .getInstance()
                  .getAnyTimeGameContestList(sessionUtil.getToken(), sessionUtil.getId(), "0");
          newApiCall.makeApiCall(context, false, call, new ApiCallback() {
              @Override
              public void success(String responseData) {
                  PrintLog.e(TAG, "getTicketDetails success :=> " + responseData);
                  //  rvTicketSelection.showRecycler();
                  Gson gson = new Gson();
                  AnyTimeGameContestList anyTimeGameResponse = gson.fromJson(responseData, AnyTimeGameContestList.class);
                  anyTimeGameContestListArrayList.addAll(anyTimeGameResponse.getContent());

                  Log.i("Size", "==>" + anyTimeGameContestListArrayList.size());

                  for (int i = 0; i < anyTimeGameContestListArrayList.size(); i++) {
                      fragments.add(new AnyTimeGameFragment().newInstance(i));
                  }
                  questionAdapter = new QuestionAdapter(getSupportFragmentManager(), fragments);
                  viewPager.setAdapter(questionAdapter);
                  final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                          .getDisplayMetrics());
                  viewPager.setPageMargin(pageMargin);
                  pagerTabStri.setViewPager(viewPager);
                  questionAdapter.notifyDataSetChanged();

              }

              @Override
              public void failure(String responseData) {

              }
          });
      }
    */
    private void getTicketDetails() {
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        String request = "";
        try {
            jsonObject.put("contest_id", contestId);
            jsonObject.put("GameNo", gameNo);
            jsonObject.put("contest_price_id", contestPrizeId);
            request = jsonObject.toString();
            Log.i("Send request", "==>" + request);
            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, getString(R.string.crypt_pass));
            data = request.getBytes(StandardCharsets.UTF_8);
            request = Base64.encodeToString(data, Base64.DEFAULT);

        } catch (Exception e) {
            e.printStackTrace();
        }


        Call<ResponseBody> call = APIClient.getInstance().contestDetailsAnyTimeGame(sessionUtil.getToken(), sessionUtil.getId(), request);
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
                ticketSelectionAdapter.setNoPlayer(no_of_players, pending, played);
                if (gtm.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    //  ticketList.clear();
                    System.out.println(gtm.getContent().getTickets());
                    //  ticketList.addAll(gtm.getContent().getTickets());
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


    @Subscribe
    public void onUpdateUpcomingContestEvent(UpdateUpcomingContestEvent updateUpcomingContestEvent) {

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


    @Override
    public void onStart() {
        super.onStart();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onStop() {
        super.onStop();
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

    @Override
    public void onItemClick(View view, int position) {
        if (view.getId() == R.id.chkSelect) {
            double price = 0;
            int counter = 0;
            if (binding.chkall.isChecked()) {
                binding.chkall.setChecked(false);
            } else {

            }
            anyticketList.get(position).setSelected(!anyticketList.get(position).isSelected());
            ticketSelectionAdapter.notifyItemChanged(position);
            for (Content ticket : anyticketList) {
                if (ticket.isSelected()) {
                    counter++;
                    price = price + ticket.getAmount();
                }
            }

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
        }
    }

    @Override
    public void onItemLongClick(View view, int position) {

    }
}