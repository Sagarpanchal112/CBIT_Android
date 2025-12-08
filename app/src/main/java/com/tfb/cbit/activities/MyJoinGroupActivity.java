package com.tfb.cbit.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.tfb.cbit.R;
import com.tfb.cbit.adapter.MyPrivateRoomTicketsAdapter;
import com.tfb.cbit.api.APIClient;
import com.tfb.cbit.api.ApiCallback;
import com.tfb.cbit.api.NewApiCall;
import com.tfb.cbit.databinding.ActivityAboutUsBinding;
import com.tfb.cbit.databinding.ActivityMyGroupBinding;
import com.tfb.cbit.models.private_group.PrivateGroupResponse;
import com.tfb.cbit.utility.PrintLog;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.Utils;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class MyJoinGroupActivity extends AppCompatActivity {
    public String TAG = "MyJoinGroupActivity";


    private Context context;
    private SessionUtil sessionUtil;
    MyPrivateRoomTicketsAdapter gameContestAdapter;
    private List<PrivateGroupResponse.Content> contentList = new ArrayList<>();
    private ActivityMyGroupBinding binding;


    @Override
    protected void onResume() {
        super.onResume();
        getMyPrivateGroup();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyGroupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        sessionUtil = new SessionUtil(context);

        LinearLayoutManager llm = new LinearLayoutManager(context);
        binding.rvTicketsList.setLayoutManager(llm);
        gameContestAdapter = new MyPrivateRoomTicketsAdapter(context, contentList);
        binding.rvTicketsList.setAdapter(gameContestAdapter);
        binding.tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SearchGroupActivity.class);
                startActivity(intent);

            }
        });
        binding.tvCreate.setOnClickListener(view -> {
            Intent intent = new Intent(context, CreateGroupActivity.class);
            startActivity(intent);
        });
        binding.ivBack.setOnClickListener(view -> {
            onBackPressed();
        });
    }


    private void getMyPrivateGroup() {
        Call<ResponseBody> call = APIClient.getInstance().UserJoinedGroupList(sessionUtil.getToken(), sessionUtil.getId());
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                //   rvTicketsList.showRecycler();
                PrintLog.e(TAG, "userJoinedGroupList:=> " + responseData);
                Gson gson = new Gson();
                PrivateGroupResponse nm = gson.fromJson(responseData, PrivateGroupResponse.class);
                if (nm.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    contentList.clear();
                    contentList.addAll(nm.getContent());
                }

                gameContestAdapter.notifyDataSetChanged();
            }

            @Override
            public void failure(String responseData) {

            }
        });
    }


}