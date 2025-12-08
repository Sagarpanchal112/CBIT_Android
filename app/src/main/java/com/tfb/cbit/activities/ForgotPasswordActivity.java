package com.tfb.cbit.activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;
import com.tfb.cbit.CBit;
import com.tfb.cbit.R;
import com.tfb.cbit.api.APIClient;
import com.tfb.cbit.api.ApiCallback;
import com.tfb.cbit.api.NewApiCall;
import com.tfb.cbit.databinding.ActivityAboutUsBinding;
import com.tfb.cbit.databinding.ActivityForgotPasswordBinding;
import com.tfb.cbit.models.CommonRes;
import com.tfb.cbit.utility.MyValidator;
import com.tfb.cbit.utility.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class ForgotPasswordActivity extends BaseAppCompactActivity {


    private Context context;
    private ActivityForgotPasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        binding.ivBack.setOnClickListener(view -> {
            onBackPressed();
        });
        binding.btnSend.setOnClickListener(view -> {
            if (isValidForm()) {
                forgotPass();
            }
        });
    }



    private boolean isValidForm(){
        return MyValidator.isBlankETError(context,binding.edtEmail,"Enter Register Email ID",1,100)&&
                MyValidator.isValidEmail(context,"Enter Valid Email ID",binding.edtEmail);
    }

    private void forgotPass(){
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        String request="";
        try {
            jsonObject.put("email",binding.edtEmail.getText().toString().trim());
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
                .forgotPassword(request);
                //.forgotPassword(edtEmail.getText().toString().trim());
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, true, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Gson gson = new Gson();
                CommonRes commonRes = gson.fromJson(responseData,CommonRes.class);
                if(commonRes.getStatus() == Utils.StandardStatusCodes.SUCCESS){
                    finish();
                }
                Utils.showToast(context,commonRes.getMessage());
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
