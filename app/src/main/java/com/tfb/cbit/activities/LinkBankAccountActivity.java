package com.tfb.cbit.activities;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import com.google.gson.Gson;
import com.tfb.cbit.R;
import com.tfb.cbit.api.APIClient;
import com.tfb.cbit.api.ApiCallback;
import com.tfb.cbit.api.NewApiCall;
import com.tfb.cbit.databases.DatabaseHandler;
import com.tfb.cbit.databinding.ActivityAboutUsBinding;
import com.tfb.cbit.databinding.ActivityLinkBankAccountBinding;
import com.tfb.cbit.event.AddBankEvent;
import com.tfb.cbit.event.UnAuthorizedEvent;
import com.tfb.cbit.models.CommonRes;
import com.tfb.cbit.utility.MyValidator;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class LinkBankAccountActivity extends BaseAppCompactActivity {


    private SessionUtil sessionUtil = null;
    private Context context;

    private ActivityLinkBankAccountBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLinkBankAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        sessionUtil = new SessionUtil(context);
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        binding.ivBack.setOnClickListener(view -> {
            onBackPressed();
        });

        binding.btnSubmit.setOnClickListener(view -> {
            if(isValidForm()){
                addbank();
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
    private boolean isValidForm(){
        return MyValidator.isBlankETError(context,binding.edtBankName,"Enter Bank Name",1,100)&&
                MyValidator.isBlankETError(context,binding.edtBankAccountNumber,"Enter Bank Account Number",1,100)&&
                MyValidator.isBlankETError(context,binding.edtBankIfscCode,"Enter Bank IFSC Code",1,100);

    }

    private void addbank(){
        Call<ResponseBody> call = APIClient
                .getInstance().addBank(sessionUtil.getToken(),sessionUtil.getId(),
                        binding. edtBankName.getText().toString().trim(),binding.edtBankAccountNumber.getText().toString().trim(),
                        binding.  edtBankIfscCode.getText().toString().trim());
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, true, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Gson gson = new Gson();
                CommonRes commonRes = gson.fromJson(responseData,CommonRes.class);
                if(commonRes.getStatus() == Utils.StandardStatusCodes.SUCCESS){
                    EventBus.getDefault().post(new AddBankEvent());
                    finish();
                }else{
                    Utils.showToast(context,commonRes.getMessage());
                }
            }

            @Override
            public void failure(String responseData) {
            }
        });
    }

    @Override
    protected void onDestroy() {
        if(EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
