package com.tfb.cbit.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.tfb.cbit.CBit;
import com.tfb.cbit.R;
import com.tfb.cbit.databases.DatabaseHandler;
import com.tfb.cbit.databinding.ActivityAboutUsBinding;
import com.tfb.cbit.databinding.ActivityPrivateGameListingBinding;
import com.tfb.cbit.event.SocketConnectionEvent;
import com.tfb.cbit.event.UnAuthorizedEvent;
import com.tfb.cbit.fragments.PrivateContestFragment;
import com.tfb.cbit.utility.CustomDialog;
import com.tfb.cbit.utility.PrintLog;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class PrivateGameListingActivity extends BaseAppCompactActivity {

    private static final String TAG = "PrivateGameListingActiv";
    public static final String JOINBYCODE = "joinbycode";
    private String code = "";
    private SessionUtil sessionUtil;
    private Context context;
    private CustomDialog customDialog;
    private boolean isComeSeepLink = false;

    private ActivityPrivateGameListingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPrivateGameListingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        sessionUtil = new SessionUtil(context);
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        if(sessionUtil.isLogin()){
            //Connect Socket If Login
            if(!CBit.getSocketUtils().getmSocket().connected()) {
                customDialog = new CustomDialog();
                customDialog.displayProgress(context,getString(R.string.loading));
                CBit.getSocketUtils().connect();
            }

            Intent intent = getIntent();
            Uri uri = intent.getData();
            if (uri != null) {
                if ("https".equals(uri.getScheme()) && getString(R.string.host_deep_link).equals(uri.getHost())) {
                    code = uri.getQueryParameter("code");
                    Log.d(TAG, "Code>>: "+code);
                }
                isComeSeepLink = true;
            }else{
                Bundle bundle = getIntent().getExtras();
                if(bundle!=null){
                    code = bundle.getString(JOINBYCODE,"");
                }
            }

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.frameContent, PrivateContestFragment.newInstance(code))
                    .commit();
        }else{
            Intent intent = new Intent(context,LoginSignUpActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        binding.ivBack.setOnClickListener(view -> {
            if(isComeSeepLink){
                Intent intent = new Intent(context,HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }else {
                super.onBackPressed();
            }
        });

    }


    @Subscribe()
    public void onSocketConnectionEvent(final SocketConnectionEvent socketConnectionEvent){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PrintLog.e("TAG",socketConnectionEvent.getMessage());
                if( CBit.getSocketUtils().getmSocket().connected()){
                    if(customDialog!=null)
                        customDialog.dismissProgress(context);
                    CBit.getSocketUtils().loginEmit(sessionUtil.getId());
                }else if(socketConnectionEvent.getMessage().equals("disconnected")){
                   /* if(Utils.isNetworkAvailable(context)){
                        Utils.showToast(context,"Network Availalble");
                    }else{
                        Utils.showToast(context,"No Network Availalble");
                    }*/
                }else if(socketConnectionEvent.getMessage().equals("Error connecting")){
                    if(customDialog == null) {
                        customDialog = new CustomDialog();
                    }
                    if(!customDialog.progressDialog.isShowing())
                        customDialog.displayProgress(context,getString(R.string.connecting));
                }else if(socketConnectionEvent.getMessage().equals("On Reconnecting")){
                    if(customDialog == null) {
                        customDialog = new CustomDialog();
                    }
                    if(!customDialog.progressDialog.isShowing())
                        customDialog.displayProgress(context,getString(R.string.connecting));
                }/*else{
                    String fcmToken = sessionUtil.getFcmtoken();
                    sessionUtil.logOut();
                    sessionUtil.setFCMToken(fcmToken);
                    Intent intent = new Intent(context,LoginSignUpActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }*/
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
