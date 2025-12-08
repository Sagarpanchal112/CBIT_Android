package com.tfb.cbit.activities;

import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.tfb.cbit.R;
import com.tfb.cbit.adapter.PrivateRoomTicketsAdapter;
import com.tfb.cbit.api.APIClient;
import com.tfb.cbit.api.ApiCallback;
import com.tfb.cbit.api.NewApiCall;
import com.tfb.cbit.databinding.ActivityAboutUsBinding;
import com.tfb.cbit.databinding.ActivityJoinPrivateRoomBinding;
import com.tfb.cbit.interfaces.OnItemClickListener;
import com.tfb.cbit.models.AllRequestModel;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class JoinPrivateRoomActivity extends AppCompatActivity implements OnItemClickListener {
    public String TAG = "JoinPrivateRoomActivity";

    private Context context;
    private SessionUtil sessionUtil;
    PrivateRoomTicketsAdapter gameContestAdapter;
    private List<AllRequestModel.AllRequest> contentList = new ArrayList<>();
    public String contest_id = "";
    public static final String CONTEST_RTIME = "RTime";
    public String start_date;
    CountDownTimer startGameRemaining, entryClosing;
    private String currentTime = "";
    private ActivityJoinPrivateRoomBinding binding;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityJoinPrivateRoomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        sessionUtil = new SessionUtil(context);
        contest_id = getIntent().getStringExtra("contest_id");
        start_date = getIntent().getStringExtra(CONTEST_RTIME);
        binding.tvStartDate.setText("Date : " + Utils.getddMMyyyyformat(start_date));
        binding.tvGameTime.setText(Utils.getHHMM(start_date));
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        currentTime = df.format(Calendar.getInstance().getTime());
        long mill = Utils.convertMillSeconds(start_date, currentTime);
        long cmill = Utils.convertMillSeconds(start_date, currentTime);
        if (startGameRemaining != null) {
            startGameRemaining.cancel();
        }

        if (entryClosing != null) {
            entryClosing.cancel();
        }
        startGameRemaining = new CountDownTimer(mill, 1000) {
            @Override
            public void onTick(long l) {
                binding.tvRemainigTime.setText(String.format("%02d : %02d : %02d",
                        TimeUnit.MILLISECONDS.toHours(l),
                        TimeUnit.MILLISECONDS.toMinutes(l) -
                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(l)), // The change is in this line
                        TimeUnit.MILLISECONDS.toSeconds(l) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(l))));
            }

            @Override
            public void onFinish() {
                binding.tvRemainigTime.setText("00 : 00 : 00");
            }
        }.start();
        entryClosing = new CountDownTimer(cmill, 1000) {
            @Override
            public void onTick(long l) {
                binding.tvEntryClosingTime.setText(
                        String.format("%02d : %02d : %02d",
                                TimeUnit.MILLISECONDS.toHours(l),
                                TimeUnit.MILLISECONDS.toMinutes(l) -
                                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(l)), // The change is in this line
                                TimeUnit.MILLISECONDS.toSeconds(l) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(l)))
                );
            }

            @Override
            public void onFinish() {
                binding.tvEntryClosingTime.setText("00 : 00 : 00");
            }
        }.start();
        LinearLayoutManager llm = new LinearLayoutManager(context);
        binding.rvTicketsList.setLayoutManager(llm);
        gameContestAdapter = new PrivateRoomTicketsAdapter(context, contentList);
        binding.rvTicketsList.setAdapter(gameContestAdapter);
        gameContestAdapter.setOnItemClickListener(this);
        binding.tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("contest_id", "==>" + contest_id);
                Intent intent = new Intent(context, EditGameActivity.class);
                intent.putExtra("contest_id", contest_id);
                startActivity(intent);

            }
        });
        binding.tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SearchGroupActivity.class);
                startActivity(intent);

            }
        });
        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });
        binding.tvCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CreateGroupActivity.class);
                startActivity(intent);

            }
        });
        getAllPrivateGroup();
    }

    private void getAllPrivateGroup() {
        Call<ResponseBody> call = APIClient.getInstance().allContestRequests(sessionUtil.getToken(), sessionUtil.getId());
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                binding.rvTicketsList.showRecycler();
                Gson gson = new Gson();
                AllRequestModel nm = gson.fromJson(responseData, AllRequestModel.class);
                if (nm.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    contentList.clear();
                    contentList.addAll(nm.getContent().allRequestArrayList);
                }

                gameContestAdapter.notifyDataSetChanged();

            }

            @Override
            public void failure(String responseData) {

            }
        });
    }


    public String request;

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(context, TicketSelectionActivity.class);
        intent.putExtra(TicketSelectionActivity.CONTEST_ID, String.valueOf(contentList.get(position).getId()));
        intent.putExtra(TicketSelectionActivity.CONTEST_NAME, contentList.get(position).getName());
        intent.putExtra(TicketSelectionActivity.CONTEST_RTIME, contentList.get(position).getStartDate());
        intent.putExtra(TicketSelectionActivity.CONTEST_MinRange, contentList.get(position).getAnsRangeMin());
        intent.putExtra(TicketSelectionActivity.CONTEST_MaxRange, contentList.get(position).getAnsRangeMax());
        startActivity(intent);

      /*  JSONObject jsonObject = new JSONObject();
        byte[] data;
        try {
            jsonObject.put("contest_id", contest_id);
            jsonObject.put("group_id", contentList.get(position).getId());
            request = jsonObject.toString();
            Log.i("isLoadMore Request", "==>" + request);
            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, getString(R.string.crypt_pass));
            data = request.getBytes("UTF-8");
            request = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Call<ResponseBody> call = APIClient.getInstance()
                .requestToJoinPrivateGroup(sessionUtil.getToken(), sessionUtil.getId(), request);
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Log.i(TAG, "responseData :-> " + responseData);
                Gson gson = new Gson();
                CommonRes commonRes = gson.fromJson(responseData, CommonRes.class);
                if (commonRes.getStatus() == Utils.StandardStatusCodes.SUCCESS) {
                    Utils.showToast(context, commonRes.getMessage());

                } else {
                    Utils.showToast(context, commonRes.getMessage());
                }
            }

            @Override
            public void failure(String responseData) {


            }
        });*/
    }
}