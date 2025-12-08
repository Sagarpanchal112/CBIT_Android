package com.tfb.cbit.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.widget.NestedScrollView;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tfb.cbit.R;
import com.tfb.cbit.api.APIClient;
import com.tfb.cbit.api.ApiCallback;
import com.tfb.cbit.api.NewApiCall;
import com.tfb.cbit.databinding.ActivityAboutUsBinding;
import com.tfb.cbit.databinding.ActivityReferralBinding;
import com.tfb.cbit.models.ReferralDetails.ReferralDetailsModel;
import com.tfb.cbit.models.ReferralDetails.UserDetails;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.Utils;

import java.io.Serializable;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class ReferralActivity extends BaseAppCompactActivity implements Serializable {


    private SessionUtil sessionUtil;
    private NewApiCall newApiCall;
    private ArrayList<UserDetails> ReferalList = new ArrayList<>();
    private ActivityReferralBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReferralBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sessionUtil = new SessionUtil(this);
        newApiCall = new NewApiCall();
        binding.tvCode.setText(sessionUtil.getReferralcode());
        getReferralDeatils();

        binding.ivBack.setOnClickListener(view -> {
            onBackPressed();
        });
        binding.btnInvite.setOnClickListener(view -> {
            Utils.shareReferralCode(ReferralActivity.this);
        });
        binding.tvTotalRef.setOnClickListener(view -> {
            if (ReferalList.size() != 0) {
                Intent intent = new Intent(ReferralActivity.this, ReferralUserListActivity.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("RefferalList", ReferalList);
                startActivity(intent);
            } else {
                Toast.makeText(ReferralActivity.this, "List Is Empty", Toast.LENGTH_SHORT).show();
            }
        });
        binding.tvCode.setOnClickListener(view -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("", binding.tvCode.getText().toString());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(ReferralActivity.this, "Text Copied", Toast.LENGTH_SHORT).show();

        });

    }


    private void getReferralDeatils() {
        Call<ResponseBody> call = APIClient.getInstance().ReferralDetails(sessionUtil.getToken(), sessionUtil.getId());
        newApiCall.makeApiCall(ReferralActivity.this, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Gson gson = new Gson();
                ReferralDetailsModel detailsModel = gson.fromJson(responseData, ReferralDetailsModel.class);
                if (detailsModel.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    ReferalList.addAll(detailsModel.getContent().getReferralList());
                    binding.tvTotalRef.setText("Total Referrals : " + detailsModel.getContent().getReferralTotal());
                    binding.tvTotalInr.setText("Total INR Handouts received - \u20B9 " + detailsModel.getContent().getReferralAmount());
                }
            }

            @Override
            public void failure(String responseData) {
            }
        });
    }
}
