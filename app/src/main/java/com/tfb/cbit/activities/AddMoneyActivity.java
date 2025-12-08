package com.tfb.cbit.activities;

import static com.cashfree.pg.CFPaymentService.PARAM_APP_ID;
import static com.cashfree.pg.CFPaymentService.PARAM_CUSTOMER_EMAIL;
import static com.cashfree.pg.CFPaymentService.PARAM_CUSTOMER_NAME;
import static com.cashfree.pg.CFPaymentService.PARAM_CUSTOMER_PHONE;
import static com.cashfree.pg.CFPaymentService.PARAM_ORDER_AMOUNT;
import static com.cashfree.pg.CFPaymentService.PARAM_ORDER_CURRENCY;
import static com.cashfree.pg.CFPaymentService.PARAM_ORDER_ID;
import static com.cashfree.pg.CFPaymentService.PARAM_ORDER_NOTE;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cashfree.pg.CFPaymentService;
import com.google.gson.Gson;
import com.tfb.cbit.CBit;
import com.tfb.cbit.R;
import com.tfb.cbit.api.APIClient;
import com.tfb.cbit.api.ApiCallback;
import com.tfb.cbit.api.NewApiCall;
import com.tfb.cbit.databases.DatabaseHandler;
import com.tfb.cbit.databinding.ActivityAboutUsBinding;
import com.tfb.cbit.databinding.ActivityAddMoneyBinding;
import com.tfb.cbit.event.UnAuthorizedEvent;
import com.tfb.cbit.event.UpdateTicketFooterEvent;
import com.tfb.cbit.models.AddMoneyStatus;
import com.tfb.cbit.models.join_contest.JoinContest;
import com.tfb.cbit.utility.LogHelper;
import com.tfb.cbit.utility.MyValidator;
import com.tfb.cbit.utility.PrintLog;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class AddMoneyActivity extends BaseAppCompactActivity {

    private SessionUtil sessionUtil;
    private Context context;
    public static final String AMOUNT_VALUE = "amountvalue";
    private String ticketAmount = "";
    private String merchantIdSandbox = "097003172226272"; // merchant id for sandbox mode
    // private String merchantId = "140686552067082"; // merchant id for Live mode
    private String merchantId = "MERCHANTUAT"; // merchant id for Live mode
    private String accessTokenSandbox = "236D48639BC0FEB1B2F1ECF312ADD15C"; // access token for sandbox
    private String accessToken = "65063B484A2F0DC8F014B82912DA6289"; // access token for Live
    public int tranId;

    String apiEndPoint = "/pg/v1/pay";
    String salt = "a6334ff7-da0e-4d51-a9ce-76b97d518b1e";
    int saltIndex = 1;
    private static int B2B_PG_REQUEST_CODE = 777;
    private ActivityAddMoneyBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddMoneyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        sessionUtil = new SessionUtil(context);

        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            ticketAmount = bundle.getString(AMOUNT_VALUE, "");
            binding.edtAmount.setText(ticketAmount);
        }
        getLatestUpdate();
        binding.tvBalance.setText(String.valueOf(Utils.getCurrencyFormat(sessionUtil.getAmount())));
        binding.tvWinningBalance.setText(String.valueOf(Utils.getCurrencyFormat(sessionUtil.getWAmount())));
        binding.ivBack.setOnClickListener(view -> {
            onBackPressed();
        });

        binding.btnAddMoney.setOnClickListener(view -> {
            if (isValidForm()) {
                startTransaction();
            }
        });
    }

    public void getLatestUpdate() {

        Call<ResponseBody> call = APIClient.getInstance().getAddmoneyStatus(sessionUtil.getToken(), sessionUtil.getId());
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, true, call, new ApiCallback() {
            @Override
            public void success(String responseData) {

                LogHelper.d("TAG", "success: " + responseData);
                try {
                    JSONObject jObj = new JSONObject(responseData);
                    Gson gson = new Gson();
                    AddMoneyStatus nm = gson.fromJson(responseData, AddMoneyStatus.class);
                    if (nm.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                        if (nm.getContent().getContest().equals("Active")) {
                            binding.btnAddMoney.setClickable(true);
                            binding.btnAddMoney.setEnabled(true);
                            binding.btnAddMoney.setAlpha(1f);
                        } else {
                            binding.btnAddMoney.setClickable(false);
                            binding.btnAddMoney.setEnabled(false);
                            binding.btnAddMoney.setAlpha(.5f);

                        }

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


    private boolean isValidForm() {
        return MyValidator.isBlankETError(context, binding.edtAmount, "Enter Amount", 1, 100);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Register this activity to listen to event.
        /*if (!GlobalBus.getBus().isRegistered(this)) {
            GlobalBus.getBus().register(this);
        }*/
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unregister from activity
       // GlobalBus.getBus().unregister(this);
    }

    /*   @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
       public void getResults(Events.PaymentMessage message) {
           if (message.getResults().equalsIgnoreCase(PaykunHelper.MESSAGE_SUCCESS)) {
               if (!TextUtils.isEmpty(message.getTransactionId())) {
                   addMoney(message.getTransactionId());
               }
           } else if (message.getResults().equalsIgnoreCase(PaykunHelper.MESSAGE_FAILED)) {
               // do your stuff here
               endTransaction("0", "", "Fail");
               Toast.makeText(this, "Your Transaction is failed", Toast.LENGTH_SHORT).show();

           } else if (message.getResults().equalsIgnoreCase(PaykunHelper.MESSAGE_SERVER_ISSUE)) {
               // do your stuff here
               endTransaction("0", "", "Server issue");
               Toast.makeText(this, PaykunHelper.MESSAGE_SERVER_ISSUE, Toast.LENGTH_SHORT).show();
           } else if (message.getResults().equalsIgnoreCase(PaykunHelper.MESSAGE_ACCESS_TOKEN_MISSING)) {
               // do your stuff here
               endTransaction("0", "", "ACCESS_TOKEN_MISSING");
               Toast.makeText(this, "Access Token missing", Toast.LENGTH_SHORT).show();
           } else if (message.getResults().equalsIgnoreCase(PaykunHelper.MESSAGE_MERCHANT_ID_MISSING)) {
               // do your stuff here
               endTransaction("0", "", "MERCHANT_ID_MISSING");
               Toast.makeText(this, "Merchant Id is missing", Toast.LENGTH_SHORT).show();
           } else if (message.getResults().equalsIgnoreCase(PaykunHelper.MESSAGE_INVALID_REQUEST)) {
               endTransaction("0", "", "INVALID_REQUEST");
               Toast.makeText(this, "Invalid Request", Toast.LENGTH_SHORT).show();
           } else if (message.getResults().equalsIgnoreCase(PaykunHelper.MESSAGE_NETWORK_NOT_AVAILABLE)) {
               endTransaction("0", "", "NETWORK_NOT_AVAILABLE");
               Toast.makeText(this, "Network is not available", Toast.LENGTH_SHORT).show();
           }
       }
   */
    private void addMoney(String transactionId) {
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        String request = "";
        try {
            jsonObject.put("amount", binding.edtAmount.getText().toString().trim());
            jsonObject.put("transactionId", transactionId);
            request = jsonObject.toString();
            Log.i("request", "=>" + request);
            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, getString(R.string.crypt_pass));
            data = request.getBytes("UTF-8");
            request = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        final Call<ResponseBody> call = APIClient.getInstance()
                .addMoney(sessionUtil.getToken(), sessionUtil.getId(), request);

        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, true, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                DecimalFormat format = new DecimalFormat("0.##");
                Gson gson = new Gson();
                JoinContest joinContest = gson.fromJson(responseData, JoinContest.class);
                if (joinContest.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    sessionUtil.setAmount(String.valueOf(format.format(joinContest.getContent().getPbAmount())));
                    sessionUtil.setWAmount(String.valueOf(format.format(joinContest.getContent().getSbAmount())));
                    endTransaction("1", transactionId, "Success");
                } else {
                    Utils.showToast(context, joinContest.getMessage());
                }
            }

            @Override
            public void failure(String responseData) {

            }
        });
    }

    private void startTransaction() {
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        String request = "";
        try {
            jsonObject.put("amount", binding.edtAmount.getText().toString().trim());
            jsonObject.put("mobileNo", sessionUtil.getMob());
            request = jsonObject.toString();
            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, getString(R.string.crypt_pass));
            data = request.getBytes("UTF-8");
            request = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        final Call<ResponseBody> call = APIClient.getInstance()
                .startTransaction(sessionUtil.getToken(), sessionUtil.getId(), request);

        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, true, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Log.i("sucess :", responseData);

                try {
                    JSONObject jObj = new JSONObject(responseData);
                    if (jObj.getInt("statusCode") == Utils.StandardStatusCodes.SUCCESS) {
                        JSONObject jcontent = jObj.getJSONObject("content");
                        tranId = jcontent.getInt("transID");
                       generateToken();

                       // addMoney(String.valueOf(tranId));

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

    private void generateToken() {
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        String request = "";
        try {
            jsonObject.put("ORDER_ID", tranId + "");
            jsonObject.put("ORDER_AMOUNT", binding.edtAmount.getText().toString().trim());
            jsonObject.put("ENV_MODE", "Production");
            request = jsonObject.toString();
            Log.d("TAG", "getJoinContest: " + request);
            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, getString(R.string.crypt_pass));
            data = request.getBytes("UTF-8");
            request = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        final Call<ResponseBody> call = APIClient.getInstance()
                .generateToken(sessionUtil.getToken(), sessionUtil.getId(), request);

        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, true, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Log.i("sucess :", responseData);

                try {
                    JSONObject jObj = new JSONObject(responseData);
                    if (jObj.getInt("statusCode") == Utils.StandardStatusCodes.SUCCESS) {
                        Log.i("token", "=>" + jObj.getJSONObject("content").getString("cftoken"));
                        doPayment(jObj.getJSONObject("content").getString("cftoken"));
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

    private void doPayment(String token) {
         String stage = "PROD";
       // String stage = "TEST";
        CFPaymentService cfPaymentService = CFPaymentService.getCFPaymentServiceInstance();
        // cfPaymentService.gPayPayment(AddMoneyActivity.this, getInputParams(), token, stage);

        cfPaymentService.setOrientation(0);
        cfPaymentService.doPayment(AddMoneyActivity.this, getInputParams(), token, stage, "#784BD2", "#FFFFFF", true);
    }

    private Map<String, String> getInputParams() {
           String appId = "2423146ae9d1b480ecf5840c65413242";
        // String appId = "19394184a127e65aca15fcbf3f149391";
        String orderNote = "Test Order";
        String customerName = sessionUtil.getName();
        String customerPhone = sessionUtil.getMob();
        String customerEmail = sessionUtil.getEmail();

        Map<String, String> params = new HashMap<>();

        params.put(PARAM_APP_ID, appId);
        params.put(PARAM_ORDER_ID, tranId + "");
        params.put(PARAM_ORDER_AMOUNT, binding.edtAmount.getText().toString());
        params.put(PARAM_ORDER_NOTE, orderNote);
        params.put(PARAM_CUSTOMER_NAME, customerName);
        params.put(PARAM_CUSTOMER_PHONE, customerPhone);
        params.put(PARAM_CUSTOMER_EMAIL, customerEmail);
        params.put(PARAM_ORDER_CURRENCY, "INR");
        return params;
    }

    String refID = "";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CFPaymentService.REQ_CODE && data != null) {
            Bundle bundle = data.getExtras();
            if (bundle != null)
                for (String key : bundle.keySet()) {
                    if (bundle.getString(key) != null) {
                        Log.i("key", "=>" + key);
                        Log.i("key", "=>" + bundle.getString(key));
                        if (key.equals("referenceId")) {
                            refID=bundle.getString(key);
                        }
                        if (bundle.getString(key).equals("SUCCESS")) {
                            addMoney(refID);
                        }

                    }
                }
        }
       /* if (requestCode == B2B_PG_REQUEST_CODE) {
            addMoney(String.valueOf(tranId));

        }*/
    }

    public String transfromBundleToString(Bundle bundle) {
        String response = "";
        for (String key : bundle.keySet()) {
            response = response.concat(String.format("%s:%s\n", key, bundle.getString(key)));

        }
        return response;
    }

    private void endTransaction(String status, String transactionId, String remarks) {
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        String request = "";
        try {
            jsonObject.put("transID", tranId);
            jsonObject.put("status", status);
            jsonObject.put("transaction_id", transactionId);
            jsonObject.put("remarks", remarks);
            request = jsonObject.toString();
            Log.i("request :", request);
            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, getString(R.string.crypt_pass));
            data = request.getBytes("UTF-8");
            request = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        final Call<ResponseBody> call = APIClient.getInstance()
                .endTransaction(sessionUtil.getToken(), sessionUtil.getId(), request);

        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, true, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Log.i("sucess :", responseData);

                try {
                    JSONObject jObj = new JSONObject(responseData);
                    if (jObj.getInt("statusCode") == Utils.StandardStatusCodes.SUCCESS) {
                        if (status.equalsIgnoreCase("1")) {
                            Intent intent = new Intent(context, AddPaymentStatusActivity.class);
                            if (!ticketAmount.isEmpty()) {
                                EventBus.getDefault().post(new UpdateTicketFooterEvent());
                            }
                            startActivity(intent);
                            finish();
                        }
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
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
