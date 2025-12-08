package com.tfb.cbit.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;

import android.widget.TextView;

import com.google.gson.Gson;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.tfb.cbit.R;
import com.tfb.cbit.adapter.UserListAdapter;
import com.tfb.cbit.databases.DatabaseHandler;
import com.tfb.cbit.databinding.ActivityAboutUsBinding;
import com.tfb.cbit.databinding.ActivityUserListBinding;
import com.tfb.cbit.event.UnAuthorizedEvent;
import com.tfb.cbit.models.private_contest_detail.Ticket;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class UserListActivity extends BaseAppCompactActivity {

    public static final String CONTESTNAME = "contestname";
    public static final String TICKET = "ticket";
    private Ticket ticket = null;
    private Context context;
    private SessionUtil sessionUtil;

    private ActivityUserListBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserListBinding.inflate(getLayoutInflater());
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

        binding.rvUserList.setLayoutManager(new LinearLayoutManager(context));
        binding.rvUserList.showProgress();
        binding.toolbarTitle.setText(bundle.getString(CONTESTNAME, ""));
        ticket = new Gson().fromJson(bundle.getString(TICKET, ""), Ticket.class);
        if (ticket != null) {
            binding.rvUserList.setAdapter(new UserListAdapter(context, ticket.getUsers()));
            binding.rvUserList.showRecycler();
        }
        binding.ivBack.setOnClickListener(view -> {
            onBackPressed();
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
}
