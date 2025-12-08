package com.tfb.cbit.activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.tfb.cbit.CBit;
import com.tfb.cbit.R;
import com.tfb.cbit.adapter.MyPrivateRoomTicketsAdapter;
import com.tfb.cbit.api.APIClient;
import com.tfb.cbit.api.ApiCallback;
import com.tfb.cbit.api.NewApiCall;
import com.tfb.cbit.databinding.ActivityAboutUsBinding;
import com.tfb.cbit.databinding.ActivityMyPrivateGroupBinding;
import com.tfb.cbit.models.private_group.PrivateGroupResponse;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class MyPrivateGroupActivity extends AppCompatActivity {
    public String TAG = "MyPrivateGroupActivity";

    private Context context;
    private SessionUtil sessionUtil;
    MyPrivateRoomTicketsAdapter gameContestAdapter;
    private List<PrivateGroupResponse.Content> contentList = new ArrayList<>();
    public String contest_id = "";
    private ActivityMyPrivateGroupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyPrivateGroupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        sessionUtil = new SessionUtil(context);
        contest_id = getIntent().getStringExtra("contest_id");

        LinearLayoutManager llm = new LinearLayoutManager(context);
        binding. rvTicketsList.setLayoutManager(llm);
        gameContestAdapter = new MyPrivateRoomTicketsAdapter(context, contentList);
        binding. rvTicketsList.setAdapter(gameContestAdapter);
        getMyPrivateGroup();
        getAllPrivateGroup();

        binding.ivBack.setOnClickListener(view -> {
            onBackPressed();
        });
    }

    private void getMyPrivateGroup() {
        Call<ResponseBody> call = APIClient.getInstance().allUsersPrivateGroup(sessionUtil.getToken(), sessionUtil.getId());
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                binding.  rvTicketsList.showRecycler();
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

    public String request = "";

    private void getAllPrivateGroup() {
        JSONObject jsonObject = new JSONObject();
        Gson gson = new Gson();
        try {
            byte[] data;
            jsonObject.put("group_id", 16);
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
        Call<ResponseBody> call = APIClient.getInstance().allRequestsPrivateGroup(sessionUtil.getToken(), sessionUtil.getId(),request);
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                binding. rvTicketsList.showRecycler();
                Gson gson = new Gson();
                 }

            @Override
            public void failure(String responseData) {

            }
        });
    }



}