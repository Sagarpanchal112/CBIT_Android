package com.tfb.cbit.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.tfb.cbit.CBit;
import com.tfb.cbit.R;
import com.tfb.cbit.activities.AnySpinerGameHistoryActivity;
import com.tfb.cbit.activities.AnyTimeGameHistoryActivity;
import com.tfb.cbit.activities.BaseAppCompactActivity;
import com.tfb.cbit.activities.GameResultActivity;
import com.tfb.cbit.activities.HistoryGameResultActivity;
import com.tfb.cbit.activities.HistorySpinningResultActivity;
import com.tfb.cbit.activities.SpinerGameResultActivity;
import com.tfb.cbit.adapter.HistoryAdapter;
import com.tfb.cbit.api.APIClient;
import com.tfb.cbit.api.ApiCallback;
import com.tfb.cbit.api.NewApiCall;
import com.tfb.cbit.databinding.ActivityAboutUsBinding;
import com.tfb.cbit.databinding.FragmentHistoryTabBinding;
import com.tfb.cbit.event.UpdateHistoryEvent;
import com.tfb.cbit.interfaces.OnItemClickListener;
import com.tfb.cbit.models.anytime.GameNumber;
import com.tfb.cbit.models.contestdetails.ContestDetailsModel;
import com.tfb.cbit.models.history.Content;
import com.tfb.cbit.models.history.HistoryModel;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;


public class LiveHistoryTabFragment extends BaseAppCompactActivity implements OnItemClickListener, HistoryAdapter.OnLoadMoreListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private Context context;
    private SessionUtil sessionUtil;
    private HistoryAdapter historyAdapter;
    private NewApiCall newApiCall;
    private List<Content> tempHistoryList = new ArrayList<>();
    private List<Content> historyList = new ArrayList<>();
    public String request = "";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
   public String jType = "";






    private FragmentHistoryTabBinding binding;


    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentHistoryTabBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;

        sessionUtil = new SessionUtil(context);
        newApiCall = new NewApiCall();

        LinearLayoutManager llm = new LinearLayoutManager(context);
        binding. rvHistoryList.setLayoutManager(llm);
        historyAdapter = new HistoryAdapter(context);
        historyAdapter.setOnItemClickListener(this);
        binding. rvHistoryList.setAdapter(historyAdapter);
        binding. rvHistoryList.showProgress();
        binding. rvHistoryList.getRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager llManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (dy > 0 && llManager.findLastCompletelyVisibleItemPosition() == (historyAdapter.getItemCount() - 2)) {
                    historyAdapter.showLoading();
                }

            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }
        });
        binding.ivBack.setOnClickListener(view -> {
            onBackPressed();
        });
    }

    @Override
    public void onResume() {


        binding. rvHistoryList.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getHistory(false);
            }
        });
        getHistory(false);
        super.onResume();
    }

    @Override
    public void onItemClick(View view, int position) {
        Log.i("position","==>"+position);
        if(historyAdapter.historyList.get(position).getIs_watch().equals("0")){
            if(historyAdapter.historyList.get(position).getGame_time().equals("-")) {
                getAnyContestDetails(historyAdapter.historyList.get(position).getId() + "",historyAdapter.historyList.get(position).getGame_no(),historyAdapter.historyList.get(position).getContestPriceID() );
            }else{
                updateIsWatch(historyAdapter.historyList.get(position).getId(),historyAdapter.historyList.get(position).getGame_no(),historyAdapter.historyList.get(position).getContestPriceID());

            }

        }else{
           // updateIsWatch(historyList.get(position).getId(),historyList.get(position).getGame_no(),historyList.get(position).getContestPriceID());
           // getContestDetails(historyList.get(position).getId(),historyList.get(position).getGame_no(),historyList.get(position).getContestPriceID());
            Gson gson = new Gson();
            getAnyContestDetails(historyAdapter.historyList.get(position).getId() + "",historyAdapter.historyList.get(position).getGame_no(),historyAdapter.historyList.get(position).getContestPriceID() );

        }
        //startActivity(new Intent(context, ContestHistoryActivity.class));
    }

    private void getHistory(boolean isLoadMore) {
        if (!isLoadMore) {
            JSONObject jsonObject = new JSONObject();
            byte[] data;
            try {
                jsonObject.put("start", "0");
                jsonObject.put("limit", "10");
                jsonObject.put("is_anytimegame", "1");
                request = jsonObject.toString();
                request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, getString(R.string.crypt_pass));
                data = request.getBytes("UTF-8");
                request = Base64.encodeToString(data, Base64.DEFAULT);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Call<ResponseBody> call = APIClient.getInstance().contestHistory(sessionUtil.getToken(), sessionUtil.getId(), request);
        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                binding. rvHistoryList.showRecycler();
                Gson gson = new Gson();
                HistoryModel hm = gson.fromJson(responseData, HistoryModel.class);
                if (hm.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    tempHistoryList.clear();
                    historyList.clear();
                    historyList.addAll(hm.getContent());
                    if (!isLoadMore) {
                        for (int i = 0; i < hm.getContent().size(); i++) {
                                    tempHistoryList.add(hm.getContent().get(i));
                        }
                      //  Log.i("TAG", "jType : " + jType + " SIZE: -> "+tempHistoryList.size() );
                        historyAdapter.addAllClass(hm.getContent());
                        historyAdapter.notifyDataSetChanged();


                    } else {
                        Log.i("TAG", "isLoadMore ");
                        tempHistoryList.clear();
                        for (int i = 0; i < hm.getContent().size(); i++) {
                                    tempHistoryList.add(hm.getContent().get(i));

                        }
                        historyAdapter.dismissLoading();
                        historyAdapter.addItemMore(hm.getContent());
                        historyAdapter.setMore(true);


                    }
                } else {
                    historyAdapter.dismissLoading();

                }

            }

            @Override
            public void failure(String responseData) {
                try {
                    historyAdapter.dismissLoading();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }




   private ArrayList<GameNumber> gamenumberArray=new ArrayList<>();

    private void getAnyContestDetails(String contestId, int gameNo, int contestPriceID) {
        GameNumber gameNumber=new GameNumber();
        gameNumber.setGame_no(gameNo+"");
        gameNumber.setContestPriceId(contestPriceID+"");
        gamenumberArray.clear();
        gamenumberArray.add(gameNumber);
        Gson gson = new Gson();

        JSONObject jsonObject = new JSONObject();
        byte[] data;
        String request = "";
        try {
            jsonObject.put("contest_id", contestId);
            jsonObject.put("GameNo", gameNo);
            jsonObject.put("contest_price_id", contestPriceID);
            jsonObject.put("contest_price_game_list", gson.toJson(gamenumberArray));
            request = jsonObject.toString();
            Log.i("request :", request);
            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, getString(R.string.crypt_pass));
            data = request.getBytes(StandardCharsets.UTF_8);
            request = Base64.encodeToString(data, Base64.DEFAULT);
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
                Gson gson = new Gson();
                Log.i("sucess :", responseData);
                ContestDetailsModel gtm = gson.fromJson(responseData, ContestDetailsModel.class);
                if (gtm.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    if (gtm.getContent().getIs_anytimegame()==1) {
                        if (gtm.getContent().getGame_type().equalsIgnoreCase("spinning-machine")) {
                            Intent intent = new Intent(context, AnySpinerGameHistoryActivity.class);
                            intent.putExtra(AnySpinerGameHistoryActivity.CONTEST_ID, contestId);
                            intent.putExtra(AnySpinerGameHistoryActivity.GAME_NO, gameNo+"");
                            intent.putExtra(AnySpinerGameHistoryActivity.CONTEST_PRIZE_ID, contestPriceID+"");
                            intent.putExtra("contest_price_game_list", gson.toJson(gamenumberArray));
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(context, AnyTimeGameHistoryActivity.class);
                            intent.putExtra(AnyTimeGameHistoryActivity.CONTEST_ID, contestId);
                            intent.putExtra(AnyTimeGameHistoryActivity.GAME_NO, gameNo+"");
                            intent.putExtra(AnyTimeGameHistoryActivity.CONTEST_PRIZE_ID, contestPriceID+"");
                            intent.putExtra("contest_price_game_list", gson.toJson(gamenumberArray));
                            startActivity(intent);
                      }
                    }else{
                        if (gtm.getContent().getGame_type().equalsIgnoreCase("spinning-machine")) {
                            Intent intent = new Intent(context, HistorySpinningResultActivity.class);
                            intent.putExtra(HistorySpinningResultActivity.CONTEST_ID, contestId);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(context, HistoryGameResultActivity.class);
                            intent.putExtra(HistoryGameResultActivity.CONTEST_ID, contestId);
                            startActivity(intent);
                        }
                    }

                } else {
                    Utils.showToast(context, gtm.getMessage());
                }
            }

            @Override
            public void failure(String responseData) {

            }
        });
    }

    private void updateIsWatch(String contestId, int gameNo, int contestPriceID) {
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        String request = "";
        try {
            jsonObject.put("contest_id", contestId.toString());
            jsonObject.put("game_no", gameNo+"");
            jsonObject.put("contestPriceId", contestPriceID+"");
            jsonObject.put("is_watch", "1");
            request = jsonObject.toString();
            Log.i("request :", request);
            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, getString(R.string.crypt_pass));
            data = request.getBytes(StandardCharsets.UTF_8);
            request = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Call<ResponseBody> call = APIClient.getInstance()
                .updateIsWatch(sessionUtil.getToken(), sessionUtil.getId(), request);
        //.contestDetails(sessionUtil.getToken(),sessionUtil.getName(),contestId);
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Gson gson = new Gson();
                Log.i("sucess :", responseData);
                getAnyContestDetails(contestId,gameNo,contestPriceID);

            }

            @Override
            public void failure(String responseData) {

            }
        });
    }

    @Override
    public void onLoadMore() {
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        try {
            jsonObject.put("start", historyAdapter.getItemCount());
            jsonObject.put("limit", "10");
            jsonObject.put("is_anytimegame", "1");
            request = jsonObject.toString();
            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, getString(R.string.crypt_pass));
            data = request.getBytes(StandardCharsets.UTF_8);
            request = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        getHistory(true);
    }

}