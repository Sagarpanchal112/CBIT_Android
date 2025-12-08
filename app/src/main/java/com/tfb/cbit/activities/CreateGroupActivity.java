package com.tfb.cbit.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.tfb.cbit.api.APIClient;
import com.tfb.cbit.api.ApiCallback;
import com.tfb.cbit.api.NewApiCall;
import com.tfb.cbit.databinding.ActivityCreateGroupBinding;
import com.tfb.cbit.utility.MyValidator;
import com.tfb.cbit.utility.SessionUtil;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class CreateGroupActivity extends AppCompatActivity {

    private Context context;

    private String TAG = "CreateGroupActivity";
    private SessionUtil sessionUtil = null;
    private ActivityCreateGroupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateGroupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        sessionUtil = new SessionUtil(context);
        binding.ivBack.setOnClickListener(view -> {
            onBackPressed();
        });
        binding.btnNext.setOnClickListener(view -> {
            if (isValidForm()) {
                addbank();
            }
        });
    }


    private boolean isValidForm() {
        if (!MyValidator.isBlankETError(context, binding.edtGrouptName, "Enter Group Name", 1, 100)) {
            return false;
        }
        return true;
    }

    private void addbank() {
        Call<ResponseBody> call = APIClient
                .getInstance().createGroup(sessionUtil.getToken(), sessionUtil.getId(), binding.edtGrouptName.getText().toString());
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, true, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Log.i(TAG, "responseData :-> " + responseData);
                Intent i = new Intent(CreateGroupActivity.this, MyPrivateGroupActivity.class);
                startActivity(i);
            }

            @Override
            public void failure(String responseData) {
                Log.i(TAG, "responseData :-> " + responseData);
            }
        });
    }

}