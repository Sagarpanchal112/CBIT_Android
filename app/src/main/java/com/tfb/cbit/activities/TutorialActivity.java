package com.tfb.cbit.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.tfb.cbit.R;
import com.tfb.cbit.adapter.TutorialSliderAdapter;
import com.tfb.cbit.api.APIClient;
import com.tfb.cbit.api.ApiCallback;
import com.tfb.cbit.api.NewApiCall;
import com.tfb.cbit.databinding.ActivityAboutUsBinding;
import com.tfb.cbit.databinding.ActivityTutorialBinding;
import com.tfb.cbit.models.HowtoPlayModel;
import com.tfb.cbit.utility.LogHelper;
import com.tfb.cbit.utility.PrintLog;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class TutorialActivity extends BaseAppCompactActivity {

    public static final String IS_REGISTER = "is_register";
    private Context context;
    private SessionUtil sessionUtil;
    private List<HowtoPlayModel.Contest> referralLists = new ArrayList<>();
    final List<Integer> imageList = new ArrayList<>();
    private ActivityTutorialBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTutorialBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        sessionUtil = new SessionUtil(context);

            //  imageList.add(R.drawable.step_17);
        getSpinningMachineitemByDate();

        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (i == referralLists.size() - 1) {
                    binding. tvSkip.setVisibility(View.GONE);
                    binding. btnGotIt.setVisibility(View.VISIBLE);
                } else {
                    binding. tvSkip.setVisibility(View.VISIBLE);
                    binding. btnGotIt.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        binding.btnGotIt.setOnClickListener(view -> {
            backPressed();
        });
        binding.tvSkip.setOnClickListener(view -> {
            backPressed();
        });

    }
    public void backPressed() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.getBoolean(IS_REGISTER, false)) {
                Intent intent = new Intent(context, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    public void getSpinningMachineitemByDate() {
        Call<ResponseBody> call = APIClient.getInstance().getDemoScreen(sessionUtil.getToken(), sessionUtil.getId());
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, true, call, new ApiCallback() {
            @Override
            public void success(String responseData) {

                LogHelper.d("TAG", "success: " + responseData);
                try {
                    JSONObject jObj = new JSONObject(responseData);
                    Gson gson = new Gson();
                    HowtoPlayModel nm = gson.fromJson(responseData, HowtoPlayModel.class);
                    if (nm.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                        referralLists.clear();
                        referralLists.addAll(nm.getContent().getContest());

                    }
                    TutorialSliderAdapter tutorialSliderAdapter = new TutorialSliderAdapter(context, nm.getContent().getContest());
                    binding. viewPager.setAdapter(tutorialSliderAdapter);

                } catch (Exception e) {

                }
            }

            @Override
            public void failure(String responseData) {
                PrintLog.e("TAG", "Failure");
            }
        });
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
