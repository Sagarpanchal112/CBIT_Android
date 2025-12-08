package com.tfb.cbit.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hbb20.CountryCodePicker;
import com.tfb.cbit.R;
import com.tfb.cbit.api.APIClient;
import com.tfb.cbit.api.ApiCallback;
import com.tfb.cbit.api.NewApiCall;
import com.tfb.cbit.databases.DatabaseHandler;
import com.tfb.cbit.databinding.ActivityAboutUsBinding;
import com.tfb.cbit.databinding.ActivityTransferWalletBinding;
import com.tfb.cbit.event.UnAuthorizedEvent;
import com.tfb.cbit.models.wallet_transfer_otp.OTPModel;
import com.tfb.cbit.utility.MyValidator;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class TransferWalletActivity extends BaseAppCompactActivity {


    private Context context;
    private SessionUtil sessionUtil;
    public static final String TRANSFER_TYPE = "transfertype";
    private String type = "";

    private ActivityTransferWalletBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTransferWalletBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle = getIntent().getExtras();
        if(bundle == null){
            finish();
            return;
        }
        type = bundle.getString(TRANSFER_TYPE,"");
        context = this;
        sessionUtil = new SessionUtil(context);
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        if(type.equals("1")) {
            binding. tvBalanceTitle.setText(getString(R.string.withdrawablebal));
            binding. tvBalance.setText(String.valueOf(Utils.getCurrencyFormat(sessionUtil.getWAmount())));
        }else{
            binding. tvBalanceTitle.setText(getString(R.string.unutilizedbal));
            binding. tvBalance.setText(String.valueOf(Utils.getCurrencyFormat(sessionUtil.getAmount())));
        }
        binding.ivBack.setOnClickListener(view -> {
            onBackPressed();
        });
        binding.btnSend.setOnClickListener(view -> {
            btnSendClick();
        });
    }


    protected void btnSendClick(){
        if(isValidForm()){
            if(!binding.edtMobile.getText().toString().trim().equals(sessionUtil.getMob())) {
                try {
                    double amount = 0;
                    if(type.equals("1")) {
                        amount = Double.parseDouble(sessionUtil.getWAmount());
                    }else{
                        amount = Double.parseDouble(sessionUtil.getAmount());
                    }
                    if(Double.parseDouble(binding.edtAmount.getText().toString().trim())<amount){
                        sendOtp();
                    }else{
                        Utils.showToast(context,"Your balance is too low to proceed");
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else{
                binding. edtMobile.setError("You are not allowed to send funds on your own number.");
                binding. edtMobile.requestFocus();
            }
        }
    }

    private boolean isValidForm(){
        return MyValidator.isBlankETError(context,binding.edtMobile,"Enter Mobile No",10,15)
                && MyValidator.isBlankETError(context,binding.edtAmount,"Enter Amount",1,100);
    }

    private  void sendOtp(){
        Call<ResponseBody> call = APIClient.getInstance().sendOtp(sessionUtil.getToken(),sessionUtil.getId());
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, true, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Gson gson = new Gson();
                OTPModel otpModel = gson.fromJson(responseData,OTPModel.class);
                if(otpModel.getStatusCode() == Utils.StandardStatusCodes.SUCCESS){
                    Bundle bundle = new Bundle();
                    //bundle.putString("mobile",ccpCode.getFullNumberWithPlus()+edtMobile.getText().toString().trim());
                    bundle.putString("mobile",binding.edtMobile.getText().toString().trim());
                    bundle.putString("amount",binding.edtAmount.getText().toString().trim());
                    bundle.putString(TRANSFER_TYPE,type);
                    bundle.putString("otpId",String.valueOf(otpModel.getContent().getOtpId()));
                    bundle.putString("otp",String.valueOf(otpModel.getContent().getOtp()));
                    bundle.putString(OTPVerificationActivity.SCREEN_TYPE,"wallet");
                    Intent intent = new Intent(context,OTPVerificationActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                }else{
                    Utils.showToast(context,otpModel.getMessage());
                }
            }

            @Override
            public void failure(String responseData) {

            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();

        if (v != null &&
                (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
            float y = ev.getRawY() + v.getTop() - scrcoords[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom())
                Utils.hideKeyboard(this);
        }
        return super.dispatchTouchEvent(ev);
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
