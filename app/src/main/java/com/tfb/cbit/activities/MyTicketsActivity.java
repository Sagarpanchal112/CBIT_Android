package com.tfb.cbit.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.tfb.cbit.CBit;
import com.tfb.cbit.R;
import com.tfb.cbit.adapter.MyTicketAdapter;
import com.tfb.cbit.adapter.OptionsAdapter;
import com.tfb.cbit.api.APIClient;
import com.tfb.cbit.api.ApiCallback;
import com.tfb.cbit.api.NewApiCall;
import com.tfb.cbit.databases.DatabaseHandler;
import com.tfb.cbit.databinding.ActivityAboutUsBinding;
import com.tfb.cbit.databinding.ActivityMyTicketsBinding;
import com.tfb.cbit.event.ContestLiveUpdate;
import com.tfb.cbit.event.UnAuthorizedEvent;
import com.tfb.cbit.interfaces.OnItemClickListener;
import com.tfb.cbit.models.contestdetails.ContestDetailsModel;
import com.tfb.cbit.models.contestdetails.Ticket;
import com.tfb.cbit.utility.PrintLog;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class MyTicketsActivity extends BaseAppCompactActivity implements OnItemClickListener {
    private static final String TAG = "MyTicketsActivity";
    private MyTicketAdapter myTicketAdapter = null;
    private Context context;
    public static final String CONTEST_ID = "contestid";
    public static final String CONTEST_NAME = "contestname";
    public static final String CONTEST_RTIME = "RTime";
    private String contestId = "";
    private List<Ticket> ticketList = new ArrayList<>();
    private SessionUtil sessionUtil;
    private ContestDetailsModel gtm;
    Bundle bundle;
    private ActivityMyTicketsBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyTicketsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        bundle = getIntent().getExtras();
        if (bundle == null) {
            finish();
            return;
        }
        sessionUtil = new SessionUtil(context);
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        binding.toolbarTitle.setText(bundle.getString(CONTEST_NAME, ""));
        contestId = bundle.getString(CONTEST_ID, "");
        binding.rvTickets.setLayoutManager(new LinearLayoutManager(context));
        if (binding.rvTickets.getRecyclerView().getItemAnimator() != null)
            ((SimpleItemAnimator) binding.rvTickets.getRecyclerView().getItemAnimator()).setSupportsChangeAnimations(false);
        myTicketAdapter = new MyTicketAdapter(context, ticketList);
        myTicketAdapter.setOnItemClickListener(this);
        binding.rvTickets.setAdapter(myTicketAdapter);

        binding.rvTickets.showProgress();
        getTicketDetails();
        binding.ivBack.setOnClickListener(view -> {
            onBackPressed();
        });
        binding.linearBuyMore.setOnClickListener(view -> {
            Intent intent = new Intent(context, TicketSelectionActivity.class);
            intent.putExtra(TicketSelectionActivity.CONTEST_ID, contestId);
            intent.putExtra(TicketSelectionActivity.CONTEST_NAME, binding.toolbarTitle.getText().toString());
            intent.putExtra(TicketSelectionActivity.CONTEST_RTIME, bundle.getString(CONTEST_RTIME, ""));
            startActivity(intent);
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
            data = request.getBytes("UTF-8");
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
                binding.rvTickets.showRecycler();
                Gson gson = new Gson();
                gtm = gson.fromJson(responseData, ContestDetailsModel.class);
                myTicketAdapter.setViewType(gtm.getContent().getType());
                myTicketAdapter.setMinAns(gtm.getContent().getAnsRangeMin());
                myTicketAdapter.setMaxAns(gtm.getContent().getAnsRangeMax());
                if (gtm.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    ticketList.clear();
                    int counter = 0;
                   /* for(Ticket ticket : gtm.getContent().getTickets()){
                        if(ticket.getIsPurchased() == 1){
                            counter++;
                            ticketList.add(ticket);
                        }
                    }*/
                    ticketList.addAll(gtm.getContent().getTickets());

                    if (counter == gtm.getContent().getTickets().size()) {
                        binding. linearBuyMore.setVisibility(View.GONE);
                    } else {
                        // linearBuyMore.setVisibility(View.VISIBLE);
                    }
                    for (int j = 0; j < gtm.getContent().getTickets().size(); j++) {
                        for (int i = 0; i < gtm.getContent().getTickets().get(j).getSlotes().size(); i++) {
                            if (gtm.getContent().getTickets().get(i).getSlotes().size() == 2 ||
                                    gtm.getContent().getTickets().get(i).getSlotes().size() == 3) {
                                if (gtm.getContent().getTickets().get(i).getSlotes().size() == 2) {
                                    binding. tvMinus.setText(gtm.getContent().getTickets().get(i).getSlotes().get(0).getDisplayValue());
                                    binding. tvZero.setText(gtm.getContent().getTickets().get(i).getSlotes().get(1).getDisplayValue());
                                    binding. tvPlus.setVisibility(View.GONE);
                                } else {
                                    //   linear3Options.setBackgroundColor(Color.parseColor("#E6E2E2"));
                                    binding. tvMinus.setText(gtm.getContent().getTickets().get(i).getSlotes().get(0).getDisplayValue());
                                    binding.tvZero.setText(gtm.getContent().getTickets().get(i).getSlotes().get(1).getDisplayValue());
                                    binding. tvPlus.setText(gtm.getContent().getTickets().get(i).getSlotes().get(2).getDisplayValue());
                                    binding. tvPlus.setVisibility(View.VISIBLE);
                                    if (gtm.getContent().getTickets().get(i).getSlotes().get(0).getDisplayValue().equalsIgnoreCase("Red win")) {
                                        //  fixedHolder.tvMinus.setBackgroundColor(Color.parseColor("#fb0102"));
                                        binding.tvMinus.setBackgroundResource(R.drawable.bg_red);
                                        binding. tvMinus.setTextColor(Color.parseColor("#ffffff"));
                                    }
                                    if (gtm.getContent().getTickets().get(i).getSlotes().get(2).getDisplayValue().equalsIgnoreCase("Blue win")) {
                                        //fixedHolder.tvPlus.setBackgroundColor(Color.parseColor("#0433ff"));
                                        binding. tvPlus.setBackgroundResource(R.drawable.bg_blue);
                                        binding. tvPlus.setTextColor(Color.parseColor("#ffffff"));
                                    }
                                }
                                binding. linear3Options.setVisibility(View.VISIBLE);
                                binding. rvOprions.setVisibility(View.GONE);
                            } else {
                                binding. linear3Options.setVisibility(View.GONE);
                                binding. rvOprions.setVisibility(View.VISIBLE);
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                                linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                                binding. rvOprions.setLayoutManager(linearLayoutManager);
                                binding. rvOprions.setAdapter(new OptionsAdapter(context, gtm.getContent().getTickets().get(i).getSlotes()));
                            }
                        }
                    }

                    if (counter <= 1) {
                        binding.tvSelected.setText(String.valueOf(counter + " Contest joined ..."));
                    } else {
                        binding. tvSelected.setText(String.valueOf(counter + " Contests joined ..."));
                    }

                    binding.  linearMarque.setVisibility(View.VISIBLE);
                    // String sourceString = "The Total Tickets Sold and Max Winners are "+"<b>"+"Updating Live"+"</b>"+" !!!          ";
                    // tvMarque.setText(Html.fromHtml(sourceString));
                    binding. tvMarque.setText(gtm.getContent().getScrollerContent());

                    //Animation marquee = AnimationUtils.loadAnimation(context, R.anim.marquee);
                    // tvMarque.startAnimation(marquee);
                    binding. tvMarque.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                    binding. tvMarque.setMarqueeRepeatLimit(-1);
                    binding. tvMarque.setSelected(true);
                    binding. tvMarque.setHorizontallyScrolling(true);
                    binding. tvMarque.setFocusable(true);
                    binding.  tvMarque.setFocusableInTouchMode(true);
                }/*else{
                    Utils.showToast(context,gtm.getMessage());
                }*/
                myTicketAdapter.notifyDataSetChanged();
            }

            @Override
            public void failure(String responseData) {

            }
        });
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

                                    myTicketAdapter.notifyItemChanged(j);
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
    public void onItemClick(View view, int position) {
        switch (view.getId()) {
            default:
                Intent intent = new Intent(context, JoinUserListActivity.class);
                intent.putExtra(JoinUserListActivity.CONTEST_NAME, gtm.getContent().getName());
                intent.putExtra(JoinUserListActivity.CONTEST_PRICE_ID, ticketList.get(position).getContestPriceId() + "");
                startActivity(intent);
                break;
        }
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

    @Override
    protected void onDestroy() {
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);

        super.onDestroy();
    }
}
