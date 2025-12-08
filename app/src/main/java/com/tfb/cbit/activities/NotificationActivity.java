package com.tfb.cbit.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.tfb.cbit.R;
import com.tfb.cbit.adapter.NotificationAdapter;
import com.tfb.cbit.api.APIClient;
import com.tfb.cbit.api.ApiCallback;
import com.tfb.cbit.api.NewApiCall;
import com.tfb.cbit.databases.DatabaseHandler;
import com.tfb.cbit.databinding.ActivityAboutUsBinding;
import com.tfb.cbit.databinding.ActivityNotificationBinding;
import com.tfb.cbit.event.UnAuthorizedEvent;
import com.tfb.cbit.models.notification.Content;
import com.tfb.cbit.models.notification.NotificationModel;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class NotificationActivity extends BaseAppCompactActivity {

    private Context context;
    private NotificationAdapter notificationAdapter;
    private SessionUtil sessionUtil;
    private List<Content> notificationList = new ArrayList<>();

    private ActivityNotificationBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        sessionUtil = new SessionUtil(context);
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        LinearLayoutManager llm = new LinearLayoutManager(context);
        binding. rvNotificationList.setLayoutManager(llm);
        notificationAdapter = new NotificationAdapter(context,notificationList);
        binding.rvNotificationList.setAdapter(notificationAdapter);

        binding.  rvNotificationList.showProgress();
        getNotification();
        binding.ivBack.setOnClickListener(view -> {
            onBackPressed();
        });
    }

    private void getNotification(){
        Call<ResponseBody> call = APIClient.getInstance().getNotification(sessionUtil.getToken(),sessionUtil.getId());
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                binding. rvNotificationList.showRecycler();
                Gson gson = new Gson();
                NotificationModel nm = gson.fromJson(responseData,NotificationModel.class);
                if(nm.getStatusCode() == Utils.StandardStatusCodes.SUCCESS){
                    notificationList.clear();
                    notificationList.addAll(nm.getContent());
                }

                notificationAdapter.notifyDataSetChanged();
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
