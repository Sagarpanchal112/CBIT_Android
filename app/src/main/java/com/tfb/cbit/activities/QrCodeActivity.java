package com.tfb.cbit.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.tfb.cbit.R;
import com.tfb.cbit.api.APIClient;
import com.tfb.cbit.api.ApiCallback;
import com.tfb.cbit.api.NewApiCall;
import com.tfb.cbit.databinding.ActivityAboutUsBinding;
import com.tfb.cbit.databinding.ActivityQrCodeBinding;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class QrCodeActivity extends AppCompatActivity {
    private NewApiCall newApiCall;
    private Context context;
    private SessionUtil sessionUtil;
    private ActivityQrCodeBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQrCodeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        sessionUtil = new SessionUtil(context);
        newApiCall = new NewApiCall();
        getUserQrCode();
        binding.ivBack.setOnClickListener(view -> {
            onBackPressed();
        });
    }

    private void getUserQrCode(){
        Call<ResponseBody> call = APIClient
                .getInstance()
                .getUserQrCode(sessionUtil.getToken(),sessionUtil.getId());
        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Gson gson = new Gson();
                Log.i("sucess :", responseData);

                try {
                    JSONObject jObj = new JSONObject(responseData);
                    if (jObj.getInt("statusCode") == Utils.StandardStatusCodes.SUCCESS) {
                        JSONObject jcontent = jObj.getJSONObject("content");
                        Glide.with(context).load(jcontent.getString("path")).apply(Utils.getUserAvatarReques()).into(binding.ivQrcode);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(String responseData) {

            }
        });
    }

}