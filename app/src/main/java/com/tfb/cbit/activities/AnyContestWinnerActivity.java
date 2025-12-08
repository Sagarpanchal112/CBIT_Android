package com.tfb.cbit.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.tfb.cbit.CBit;
import com.tfb.cbit.R;
import com.tfb.cbit.adapter.WinnerAdapter;
import com.tfb.cbit.api.APIClient;
import com.tfb.cbit.api.ApiCallback;
import com.tfb.cbit.api.NewApiCall;
import com.tfb.cbit.databases.DatabaseHandler;
import com.tfb.cbit.databinding.ActivityAboutUsBinding;
import com.tfb.cbit.databinding.ActivityContestWinnerBinding;
import com.tfb.cbit.event.UnAuthorizedEvent;
import com.tfb.cbit.models.contestwinner.Content;
import com.tfb.cbit.models.contestwinner.ContestWinnerModel;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class AnyContestWinnerActivity extends BaseAppCompactActivity implements WinnerAdapter.OnLoadMoreListener {


    private Context context;
     private WinnerAdapter winnerAdapter;
    public static final String CONTESTPRICEID = "contestPriceID";
    public static final String CONTEST_NAME = "contestName";
    public static final String ISPRIVATE = "isprivate";
    private String contestPriceId = "";
    private String game_no = "";
    private List<Content> winnerList = new ArrayList<>();
    private SessionUtil sessionUtil;
    private boolean isPrivateContest = false;
    private ActivityContestWinnerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContestWinnerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            finish();
            return;
        }
        binding.toolbarTitle.setText(bundle.getString(CONTEST_NAME, ""));
        isPrivateContest = bundle.getBoolean(ISPRIVATE, false);
        sessionUtil = new SessionUtil(context);
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        contestPriceId = bundle.getString(CONTESTPRICEID, "");
        game_no = bundle.getString("game_no", "");
        binding.rvWinnerList.setLayoutManager(new LinearLayoutManager(context));
        winnerAdapter = new WinnerAdapter(context, winnerList);
        binding.rvWinnerList.setAdapter(winnerAdapter);
        ((TextView) binding.rvWinnerList.getEmptyView().findViewById(R.id.tvNoDataTitle)).setText("No Winner");

        binding.rvWinnerList.getRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager llManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (dy > 0 && llManager.findLastCompletelyVisibleItemPosition() == (winnerAdapter.getItemCount() - 1)) {
                    winnerAdapter.showLoading();
                }
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        binding.rvWinnerList.showProgress();
        binding.linearFooter.setVisibility(View.GONE);
        getWinnerData(false);
        binding.ivBack.setOnClickListener(view -> {
            onBackPressed();
        });
    }


    public String request = "";

    private void getWinnerData(boolean isLoadMore) {
        if (!isLoadMore) {
            JSONObject jsonObject = new JSONObject();
            byte[] data;
            try {
                jsonObject.put("contestPriceId", contestPriceId);
                jsonObject.put("game_no", game_no);
                jsonObject.put("start", "0");
                jsonObject.put("limit", "10");
                request = jsonObject.toString();
                Log.i("send request", "==>" + request);
                request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, getString(R.string.crypt_pass));
                data = request.getBytes("UTF-8");
                request = Base64.encodeToString(data, Base64.DEFAULT);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Call<ResponseBody> call = APIClient.getInstance()
                .anytimeWinnerList(sessionUtil.getToken(), sessionUtil.getId(), request);
        //.winnerList(sessionUtil.getToken(),sessionUtil.getName(),contestPriceId);
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                binding.rvWinnerList.showRecycler();
                Gson gson = new Gson();
                ContestWinnerModel cwm = gson.fromJson(responseData, ContestWinnerModel.class);
                if (cwm.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    winnerList.clear();
                    winnerList.addAll(cwm.getContent());
                    if (!isLoadMore) {
                        winnerAdapter.addAllClass(cwm.getContent());
                        winnerAdapter.notifyDataSetChanged();
                        if (isPrivateContest) {
                            binding. linearFooter.setVisibility(View.GONE);
                        } else {
                            if (winnerList.size() > 0) {
                                binding.linearFooter.setVisibility(View.VISIBLE);
                                binding.tvDate.setText(String.valueOf("Date: " + winnerList.get(0).getContestStartDate()));
                                binding. tvTime.setText(String.valueOf("Time: " + winnerList.get(0).getContestStartTime()));
                                binding.tvTotalWinnings.setText(String.valueOf("Winning amount " + Utils.INDIAN_RUPEES + winnerList.get(0).getWinAmount() + "/Person"));
                            }
                        }
                    } else {
                        Log.i("TAG", "isLoadMore +" + cwm.getContent().size());
                        winnerAdapter.dismissLoading();
                        winnerAdapter.addItemMore(cwm.getContent());
                        winnerAdapter.setMore(true);
                    }
                } else {
                    winnerAdapter.dismissLoading();
                }
            }

            @Override
            public void failure(String responseData) {

            }
        });
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

    @Override
    public void onLoadMore() {
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        try {
            jsonObject.put("contestPriceId", contestPriceId);
            jsonObject.put("start", winnerAdapter.getItemCount());
            jsonObject.put("limit", "10");
            request = jsonObject.toString();
            Log.i("NoLoadMore Request", "==>" + request);
            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, getString(R.string.crypt_pass));
            data = request.getBytes("UTF-8");
            request = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        getWinnerData(true);
    }
}
