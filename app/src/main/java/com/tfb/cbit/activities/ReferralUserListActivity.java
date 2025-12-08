package com.tfb.cbit.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.util.Log;
import android.widget.TextView;

import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.tfb.cbit.R;
import com.tfb.cbit.adapter.ReferralUserListAdapter;
import com.tfb.cbit.databases.DatabaseHandler;
import com.tfb.cbit.databinding.ActivityAboutUsBinding;
import com.tfb.cbit.databinding.ActivityJoinUserListBinding;
import com.tfb.cbit.event.UnAuthorizedEvent;
import com.tfb.cbit.models.ReferralDetails.UserDetails;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;


public class ReferralUserListActivity extends BaseAppCompactActivity {


    private static final String TAG = "ReferralUserListActivit";
    public static final String CONTEST_NAME = "contestname";
    public static final String CONTEST_PRICE_ID = "contestpriceid";

    private String contestPriceId = "";
    private Context context;
    private SessionUtil sessionUtil;
     private List<UserDetails> userList = new ArrayList<>();
    private ReferralUserListAdapter userListAdapter = null;
    private ActivityJoinUserListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityJoinUserListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        sessionUtil = new SessionUtil(context);
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            finish();
            return;
        }
        binding.toolbarTitle.setText("Referral List");

        userList = (List<UserDetails>) getIntent().getSerializableExtra("RefferalList");
        Log.d(TAG, "USER:ISTINREff: " + userList.size());

        binding. rvUserList.setLayoutManager(new LinearLayoutManager(context));

        userListAdapter = new ReferralUserListAdapter(context, userList);
        binding. rvUserList.setAdapter(userListAdapter);
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
        if(EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);

        super.onDestroy();
    }


}
