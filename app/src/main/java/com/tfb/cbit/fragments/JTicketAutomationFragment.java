package com.tfb.cbit.fragments;


import android.content.Context;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.tfb.cbit.CBit;
import com.tfb.cbit.R;
import com.tfb.cbit.api.APIClient;
import com.tfb.cbit.api.ApiCallback;
import com.tfb.cbit.api.NewApiCall;
import com.tfb.cbit.databinding.FragmentAboutBinding;
import com.tfb.cbit.databinding.FragmentAutomationBinding;
import com.tfb.cbit.models.AllRequestModel;
import com.tfb.cbit.models.AutoPilotModel;
import com.tfb.cbit.utility.LogHelper;
import com.tfb.cbit.utility.PrintLog;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class JTicketAutomationFragment extends Fragment {

    private static final String TAG = "RedeemJTicketFragment";

    private Context context;
    private SessionUtil sessionUtil;


    public JTicketAutomationFragment() {
        // Required empty public constructor
    }


    public static JTicketAutomationFragment newInstance() {
        JTicketAutomationFragment fragment = new JTicketAutomationFragment();
        return fragment;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private FragmentAutomationBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAutomationBinding.bind(LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_automation, container, false));
        View view = binding.getRoot();
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionUtil = new SessionUtil(context);
        Log.d(TAG, "IsAuto:" + sessionUtil.getISAutoPilot());
        Log.d(TAG, "IsRedeem:" + sessionUtil.getISRedeem());

        if (sessionUtil.getISAutoPilot() == 1) {
            binding.switchAutoPilotUpdate.setChecked(true);
            binding.switchRedeemDailyQoutaUpdate.setChecked(false);
        }

        if (sessionUtil.getISRedeem() == 1) {
            binding.switchRedeemDailyQoutaUpdate.setChecked(true);
            binding.switchAutoPilotUpdate.setChecked(false);
        }
        getSpinningMachineitemByDate();
        binding.webInstarction.loadUrl("file:///android_asset/auto_pilot_mode.html");

        binding.switchAutoPilotUpdate.setOnClickListener(view1 -> {
            switchAutoPilotUpdateClick();
        });
        binding.switchRedeemDailyQoutaUpdate.setOnClickListener(view1 -> {
            switchRedeemDailyQoutaUpdateClick();
        });
    }

    public void getSpinningMachineitemByDate() {
        Call<ResponseBody> call = APIClient.getInstance().getautopilotcontent(sessionUtil.getToken(), sessionUtil.getId());
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, true, call, new ApiCallback() {
            @Override
            public void success(String responseData) {

                LogHelper.d(TAG, "success: " + responseData);
                try {
                    JSONObject jObj = new JSONObject(responseData);
                    Gson gson = new Gson();
                    AutoPilotModel nm = gson.fromJson(responseData, AutoPilotModel.class);
                    if (nm.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                        binding.tvText.setText(nm.getContent().getContest());

                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void failure(String responseData) {
                PrintLog.e("TAG", "Failure");
            }
        });
    }

    protected void switchAutoPilotUpdateClick() {
        autoPilotUpdate(binding.switchAutoPilotUpdate.isChecked() ? 1 : 0);
    }

    protected void switchRedeemDailyQoutaUpdateClick() {
        RedeemDailyQouta(binding.switchRedeemDailyQoutaUpdate.isChecked() ? 1 : 0);
    }

    private void autoPilotUpdate(int sw) {
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        String request = "";
        try {
            jsonObject.put("autoPilot", sw);
            request = jsonObject.toString();
            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, getString(R.string.crypt_pass));
            data = request.getBytes(StandardCharsets.UTF_8);
            request = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Call<ResponseBody> call = APIClient.getInstance()
                .AutoPilotUpdate(sessionUtil.getToken(), sessionUtil.getId(), request);
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, true, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Log.d(TAG, "success: " + responseData);
                sessionUtil.setISAutoPilot(binding.switchAutoPilotUpdate.isChecked() ? 1 : 0);
                binding.switchAutoPilotUpdate.setChecked(sessionUtil.getISAutoPilot() == 1);
                if (sessionUtil.getISAutoPilot() == 1) {
                    binding.switchAutoPilotUpdate.setChecked(true);
                    binding.switchRedeemDailyQoutaUpdate.setChecked(false);
                }
            }

            @Override
            public void failure(String responseData) {

            }
        });
    }

    private void RedeemDailyQouta(int sw) {
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        String request = "";
        try {
            jsonObject.put("isRedeem", sw);
            request = jsonObject.toString();
            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, getString(R.string.crypt_pass));
            data = request.getBytes(StandardCharsets.UTF_8);
            request = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Call<ResponseBody> call = APIClient.getInstance()
                .RedeemDailyQoutaUpdate(sessionUtil.getToken(), sessionUtil.getId(), request);
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, true, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Log.d(TAG, "success: " + responseData);
                sessionUtil.setISRedeem(binding.switchRedeemDailyQoutaUpdate.isChecked() ? 1 : 0);
                binding.switchRedeemDailyQoutaUpdate.setChecked(sessionUtil.getISRedeem() == 1);
                if (sessionUtil.getISRedeem() == 1) {
                    binding.switchRedeemDailyQoutaUpdate.setChecked(true);
                    binding.switchAutoPilotUpdate.setChecked(false);
                }
            }

            @Override
            public void failure(String responseData) {

            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}
