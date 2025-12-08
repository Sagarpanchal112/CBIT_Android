package com.tfb.cbit.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tfb.cbit.CBit;
import com.tfb.cbit.R;
import com.tfb.cbit.api.APIClient;
import com.tfb.cbit.api.ApiCallback;
import com.tfb.cbit.api.NewApiCall;
import com.tfb.cbit.databases.DatabaseHandler;
import com.tfb.cbit.databinding.ActivityAboutUsBinding;
import com.tfb.cbit.databinding.ActivityRedeemBinding;
import com.tfb.cbit.event.UnAuthorizedEvent;
import com.tfb.cbit.models.accounts.AccountsModel;
import com.tfb.cbit.models.accounts.Content;
import com.tfb.cbit.models.redeem.RedeemModel;
import com.tfb.cbit.utility.CustomDialog;
import com.tfb.cbit.utility.MyValidator;
import com.tfb.cbit.utility.PrintLog;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class RedeemActivity extends BaseAppCompactActivity {

    private static final String TAG = "RedeemActivity";

    private Context context;
    private SessionUtil sessionUtil;
    private NewApiCall newApiCall;
    private ArrayAdapter<String> accountsAdapter;
    private List<String> accountList = new ArrayList<>();
    private List<Content> contentArrayList = new ArrayList<>();
    private ActivityRedeemBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRedeemBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        sessionUtil = new SessionUtil(context);
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        newApiCall = new NewApiCall();
        binding.tvBalance.setText(String.valueOf(Utils.getCurrencyFormat(sessionUtil.getWAmount())));

        accountList.add("Select Account");
        accountsAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item,
                accountList);
        accountsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerAccount.setAdapter(accountsAdapter);

        getAccounts();

        binding.ivBack.setOnClickListener(view -> {
            onBackPressed();
        });
        binding.btnRedeem.setOnClickListener(view -> {
            if (isValidForm()) {
                redeem();
            }
        });
    }


    private boolean isValidForm() {
        if (binding.spinnerAccount.getSelectedItemPosition() == 0) {
            Utils.showToast(context, "Select Account");
            return false;
        } else if (!MyValidator.isBlankETError(context, binding.edtAmount, "Enter Amount", 1, 100)) {
            return false;
        }
        return true;
    }

    private void getAccounts() {
        Call<ResponseBody> call = APIClient
                .getInstance()
                .accounts(sessionUtil.getToken(), sessionUtil.getId());

        newApiCall.makeApiCall(context, true, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Gson gson = new Gson();
                AccountsModel accountsModel = gson.fromJson(responseData, AccountsModel.class);
                if (accountsModel.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    contentArrayList.addAll(accountsModel.getContent());
                    for (Content content : contentArrayList) {
                        String accountNumber = "XX";
                        if (content.getAccountNo().length() > 4) {
                            accountNumber = accountNumber + content.getAccountNo().substring(content.getAccountNo().length() - 4);
                        } else {
                            accountNumber = accountNumber + content.getAccountNo();
                        }
                        accountList.add(content.getBankName() + " " + accountNumber);
                    }
                }

                accountsAdapter.notifyDataSetChanged();

                if (contentArrayList.size() < 1) {
                    CustomDialog customDialog = new CustomDialog();
                    customDialog.showDialogTwoButton(context, "", "Link Bank Account", "Ok", "Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            startActivity(new Intent(context, ProfileActivity.class));
                        }
                    }, null);
                }
            }

            @Override
            public void failure(String responseData) {

            }
        });
    }

    private void redeem() {
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        String request = "";
        Content content = contentArrayList.get(binding.spinnerAccount.getSelectedItemPosition() - 1);
        try {
            if (content != null) {
                jsonObject.put("bank_id", content.getBankId());
                jsonObject.put("bank_name", content.getBankName());
                jsonObject.put("account_no", content.getAccountNo());
                jsonObject.put("ifsc_code", content.getIfscCode());
                jsonObject.put("amount",binding. edtAmount.getText().toString().trim());
                request = jsonObject.toString();
                request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, getString(R.string.crypt_pass));
                data = request.getBytes("UTF-8");
                request = Base64.encodeToString(data, Base64.DEFAULT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Call<ResponseBody> call = APIClient.
                getInstance().redeeem(sessionUtil.getToken(), sessionUtil.getId(), request);
        newApiCall.makeApiCall(context, true, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Log.d(TAG, "success: "+responseData.toString());
                DecimalFormat format = new DecimalFormat("0.##");
                Gson gson = new Gson();
                RedeemModel rm = gson.fromJson(responseData, RedeemModel.class);
                if (rm.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    sessionUtil.setAmount(String.valueOf(format.format(rm.getContent().getPbAmount())));
                    sessionUtil.setWAmount(String.valueOf(format.format(rm.getContent().getSbAmount())));
                    CustomDialog customDialog = new CustomDialog();
                    customDialog.showDialogOneButton(context, getString(R.string.redeem),
                            "Your redeem request has been processed. You will be notified with an email when the transaction is completed.",
                            "Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    finish();
                                }
                            });

                } else {
                    Utils.showToast(context, rm.getMessage());
                }
            }

            @Override
            public void failure(String responseData) {
                Log.d(TAG, "failure: "+responseData);
                if (!responseData.isEmpty()) {
                    Utils.showToast(context, responseData);
                }

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
