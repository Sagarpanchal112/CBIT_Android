package com.tfb.cbit.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.tfb.cbit.CBit;
import com.tfb.cbit.R;
import com.tfb.cbit.adapter.UserListAdapter;
import com.tfb.cbit.api.APIClient;
import com.tfb.cbit.api.ApiCallback;
import com.tfb.cbit.api.NewApiCall;
import com.tfb.cbit.databases.DatabaseHandler;
import com.tfb.cbit.databinding.ActivityAboutUsBinding;
import com.tfb.cbit.databinding.ActivityJoinUserListBinding;
import com.tfb.cbit.event.UnAuthorizedEvent;
import com.tfb.cbit.models.private_contest_detail.User;
import com.tfb.cbit.models.user_join_list.UserJoinModel;
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

public class JoinUserListActivity extends BaseAppCompactActivity {


    public static final String CONTEST_NAME = "contestname";
    public static final String CONTEST_PRICE_ID = "contestpriceid";
    public static final String CONTEST_GAME_NO = "gameNo";

    private String contestPriceId = "";
    public int GameNo = 0;
    private Context context;
    private SessionUtil sessionUtil;
    private List<User> userList = new ArrayList<>();
    private UserListAdapter userListAdapter = null;
    private ActivityJoinUserListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityJoinUserListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        sessionUtil = new SessionUtil(context);
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            finish();
            return;
        }
        binding. toolbarTitle.setText(bundle.getString(CONTEST_NAME, ""));
        contestPriceId = bundle.getString(CONTEST_PRICE_ID, "");
        GameNo = bundle.getInt(CONTEST_GAME_NO, 0);
        binding. rvUserList.setLayoutManager(new LinearLayoutManager(context));
        userListAdapter = new UserListAdapter(context, userList);
        binding. rvUserList.setAdapter(userListAdapter);
        binding. rvUserList.showProgress();
        getJoinUserList();
        binding.ivBack.setOnClickListener(view -> {
            onBackPressed();
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUnAuthorizedEvent(UnAuthorizedEvent unAuthorizedEvent) {
        Utils.showToast(context, unAuthorizedEvent.getMessage());
        DatabaseHandler databaseHandler = new DatabaseHandler(context);
        databaseHandler.deleteTable();
        String fcmToken = sessionUtil.getFcmtoken();
        sessionUtil.logOut();
        sessionUtil.setFCMToken(fcmToken);
        Intent intent = new Intent(context, LoginSignUpActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }



    private void getJoinUserList() {
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        String request = "";
        try {
            jsonObject.put("contestPriceId", contestPriceId);
            jsonObject.put("GameNo", GameNo);
            request = jsonObject.toString();
            Log.i("send request","==>"+request);
            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, getString(R.string.crypt_pass));
            data = request.getBytes("UTF-8");
            request = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Call<ResponseBody> call = APIClient.getInstance().joinUserList(
                sessionUtil.getToken(), sessionUtil.getId(), request);
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                binding.  rvUserList.showRecycler();
                Gson gson = new Gson();
                UserJoinModel userJoinModel = gson.fromJson(responseData, UserJoinModel.class);
                if (userJoinModel.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    userList.addAll(userJoinModel.getContent());
                }
                userListAdapter.notifyDataSetChanged();
            }

            @Override
            public void failure(String responseData) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);

        super.onDestroy();
    }
}
