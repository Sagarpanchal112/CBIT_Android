package com.tfb.cbit.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tfb.cbit.CBit;
import com.tfb.cbit.R;
import com.tfb.cbit.api.APIClient;
import com.tfb.cbit.api.ApiCallback;
import com.tfb.cbit.api.NewApiCall;
import com.tfb.cbit.databases.DatabaseHandler;
import com.tfb.cbit.databinding.ActivityAboutUsBinding;
import com.tfb.cbit.databinding.ActivityPrivateContestDetailsBinding;
import com.tfb.cbit.event.UnAuthorizedEvent;
import com.tfb.cbit.fragments.ParticipantFragment;
import com.tfb.cbit.fragments.PrivateContestDetailFragment;
import com.tfb.cbit.models.private_contest_detail.PrivateContestDetailModel;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class PrivateContestDetailsActivity extends BaseAppCompactActivity {


    private Context context;

    public static final String CONTESTID = "contestId";
    public static final String CONTESTNAME = "contestName";
    private String contestId = "";
    private SessionUtil sessionUtil;

    private ActivityPrivateContestDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPrivateContestDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;

        Bundle bundle = getIntent().getExtras();
        if(bundle == null){
            finish();
            return;
        }
        sessionUtil = new SessionUtil(context);
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        contestId = bundle.getString(CONTESTID,"");
        if(contestId.isEmpty()){
            finish();
            return;
        }
        binding.toolbarTitle.setText(bundle.getString(CONTESTNAME,""));

        getPrivateContestDetails();
        binding.ivBack.setOnClickListener(view -> {
            onBackPressed();
        });

    }


    private void getPrivateContestDetails(){
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        String request="";
        try {
            jsonObject.put("contestId",contestId);
            request = jsonObject.toString();
            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request,getString(R.string.crypt_pass));
            data = request.getBytes("UTF-8");
            request = Base64.encodeToString(data,Base64.DEFAULT);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Call<ResponseBody> call = APIClient.getInstance()
                .getPrivateContestDetails(sessionUtil.getToken(),sessionUtil.getId(),request);
                //.getPrivateContestDetails(sessionUtil.getToken(),sessionUtil.getName(),contestId);
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Gson gson = new Gson();
                PrivateContestDetailModel pdm = gson.fromJson(responseData,PrivateContestDetailModel.class);
                if(pdm.getStatusCode() == Utils.StandardStatusCodes.SUCCESS){
                    binding. pbProgress.setVisibility(View.GONE);
                    if(pdm.getContent().getGameStatus().equalsIgnoreCase("start")){
                        getSupportFragmentManager()
                                .beginTransaction()
                                .add(R.id.frameContent, ParticipantFragment.newInstance(new Gson().toJson(pdm.getContent())))
                                .commit();
                    }else{
                        getSupportFragmentManager()
                                .beginTransaction()
                                .add(R.id.frameContent, PrivateContestDetailFragment.newInstance(new Gson().toJson(pdm.getContent())))
                                .commit();
                    }
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
        if(EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);

        super.onDestroy();
    }
}
