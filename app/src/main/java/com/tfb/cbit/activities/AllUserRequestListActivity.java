package com.tfb.cbit.activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.tfb.cbit.CBit;
import com.tfb.cbit.R;
import com.tfb.cbit.adapter.AllRequestAdapter;
import com.tfb.cbit.adapter.AllUserRequestAdapter;
import com.tfb.cbit.api.APIClient;
import com.tfb.cbit.api.ApiCallback;
import com.tfb.cbit.api.NewApiCall;
import com.tfb.cbit.databinding.ActivityAboutUsBinding;
import com.tfb.cbit.databinding.ActivityAllUserRequestListBinding;
import com.tfb.cbit.models.AllUserRequestModel;
import com.tfb.cbit.models.GroupUserModel;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class AllUserRequestListActivity extends AppCompatActivity {
    public String TAG = "AllUserRequestListActivity";
    private Context context;
    private SessionUtil sessionUtil;
    AllUserRequestAdapter gameContestAdapter;
    AllRequestAdapter allRequestAdapter;
    private List<GroupUserModel.Content> contentList = new ArrayList<>();
    private List<AllUserRequestModel.AllRequest> allRequestArrayList = new ArrayList<>();

    private ActivityAllUserRequestListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllUserRequestListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
         context = this;
        sessionUtil = new SessionUtil(context);

        LinearLayoutManager llm = new LinearLayoutManager(context);
        binding.rvGroupMember.setLayoutManager(llm);
        gameContestAdapter = new AllUserRequestAdapter(context, contentList);
        binding.rvGroupMember.setAdapter(gameContestAdapter);

        LinearLayoutManager llm1 = new LinearLayoutManager(context);
        binding.rvAllRequest.setLayoutManager(llm1);
        allRequestAdapter = new AllRequestAdapter(context, allRequestArrayList);
        binding.rvAllRequest.setAdapter(allRequestAdapter);
        allRequestAdapter.setOnItemClickListener(new AllRequestAdapter.OnAcceptOrDescline() {
            @Override
            public void onClick(int type, int pos) {
                acceptOrDelcineApi(type, pos);
            }
        });
        getMyPrivateGroup();
        getAllRequestGroup();
        binding.ivBack.setOnClickListener(view -> {
            onBackPressed();
        });
    }

    private void acceptOrDelcineApi(int type, int pos) {
        JSONObject jsonObject = new JSONObject();
        try {
            byte[] data;
            jsonObject.put("group_id", allRequestArrayList.get(pos).getGroup_id());
            jsonObject.put("request_id", type);
            jsonObject.put("user_id", allRequestArrayList.get(pos).getUser_id());
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
        Call<ResponseBody> call = APIClient.getInstance().acceptDeclineRequest(sessionUtil.getToken(), sessionUtil.getId(), request);
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, true, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Gson gson = new Gson();
                getMyPrivateGroup();
                getAllRequestGroup();

            }

            @Override
            public void failure(String responseData) {

            }
        });  }


    public String request = "";

    private void getAllRequestGroup() {
        JSONObject jsonObject = new JSONObject();
        try {
            byte[] data;
            jsonObject.put("group_id", 15);
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
        Call<ResponseBody> call = APIClient.getInstance().allRequestsPrivateGroup(sessionUtil.getToken(), sessionUtil.getId(), request);
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, true, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Gson gson = new Gson();
                AllUserRequestModel nm = gson.fromJson(responseData, AllUserRequestModel.class);
                if (nm.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    allRequestArrayList.clear();
                    allRequestArrayList.addAll(nm.getContent().allRequestArrayList);
                }

                allRequestAdapter.notifyDataSetChanged();

            }

            @Override
            public void failure(String responseData) {

            }
        });
    }

    private void getMyPrivateGroup() {
        JSONObject jsonObject = new JSONObject();
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
        Call<ResponseBody> call = APIClient.getInstance().PrivateGroupUserList(sessionUtil.getToken(), sessionUtil.getId(), request);
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, true, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Gson gson = new Gson();
                GroupUserModel nm = gson.fromJson(responseData, GroupUserModel.class);
                if (nm.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    contentList.clear();
                    contentList.addAll(nm.getContents());
                }

                gameContestAdapter.notifyDataSetChanged();

            }

            @Override
            public void failure(String responseData) {

            }
        });
    }

}