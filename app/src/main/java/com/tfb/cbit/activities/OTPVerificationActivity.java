package com.tfb.cbit.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.onesignal.OneSignal;
import com.tfb.cbit.CBit;
import com.tfb.cbit.R;
import com.tfb.cbit.api.APIClient;
import com.tfb.cbit.api.ApiCallback;
import com.tfb.cbit.api.NewApiCall;
import com.tfb.cbit.databases.DatabaseHandler;
import com.tfb.cbit.databinding.ActivityAboutUsBinding;
import com.tfb.cbit.databinding.ActivityOtpverificationBinding;
import com.tfb.cbit.event.UnAuthorizedEvent;
import com.tfb.cbit.models.login_register.LoginRegisterModel;
import com.tfb.cbit.models.wallet_transfer.WalletTransferModel;
import com.tfb.cbit.models.wallet_transfer_otp.OTPModel;
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

import okhttp3.ResponseBody;
import retrofit2.Call;

public class OTPVerificationActivity extends BaseAppCompactActivity {

    private static final String TAG = "OTPVerificationActivity";

    private Context context;
    private Bundle bundle;
    private SessionUtil sessionUtil;
    private String screentype = "";
    public static final String SCREEN_TYPE = "type";
    private String otpId = "", otp = "";
    String OneSignalID = "";
    private ActivityOtpverificationBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtpverificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        sessionUtil = new SessionUtil(context);
        bundle = getIntent().getExtras();
       /* OneSignal.idsAvailable((userId, registrationId) -> {
            Log.d(TAG, "onCreate: User:" + userId);
            this.OneSignalID = userId;
        });*/
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        if (bundle == null) {
            finish();
            return;
        }
        PrintLog.e("TAG", "OTP Screen " + bundle.toString());
//        edtOTP.setText(bundle.getString("otp",""));
        screentype = bundle.getString(SCREEN_TYPE, "");
        otpId = bundle.getString("otpId", "");
        otp = bundle.getString("otp", "");
        if (screentype.equals("wallet")) {
            binding.edtOTP.setText(bundle.getString("otp", ""));
            binding.tvOtpTitle.setText("Please enter Verification code send\nto +91 " + sessionUtil.getMob());
        } else if (screentype.equals("register")) {
            binding. tvOtpTitle.setText("Please enter Verification code send\nto +91 " + bundle.getString("mobile_no", ""));
        } else if (screentype.equals("loginwithmobile")) {
            binding. tvOtpTitle.setText("Please enter Verification code send\nto +91 " + bundle.getString("mobile_no", ""));
        }
        binding.ivBack.setOnClickListener(view -> {
            onBackPressed();
        });
        binding.btnVerify.setOnClickListener(view -> {
            if (isValidForm()) {
                if (bundle != null)
                    if (screentype.equals("wallet")) {
                        addWallet();
                    } else if (screentype.equals("register")) {
                        getSignUp();
                    } else if (screentype.equals("loginwithmobile")) {
                        getLoginWithMob();
                    }
            }
        });
        binding.linearResend.setOnClickListener(view -> {
            if (bundle != null) {
                if (screentype.equals("wallet")) {
                    sendOtp();
                } else {
                    sendOtpAuth();
                }
            }
        });

    }


    private boolean isValidForm() {
        return MyValidator.isBlankETError(context,binding. edtOTP, "Enter OTP", 1, 100);
    }

    private void sendOtpAuth() {
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        String request = "";
        try {
            jsonObject.put("mobile_no", bundle.getString("mobile_no", ""));
            jsonObject.put("version", Utils.getVersionName(context));
            jsonObject.put("plateform", "Android");
            //  jsonObject.put("email",bundle.getString("email",""));
            request = jsonObject.toString();
            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, getString(R.string.crypt_pass));
            data = request.getBytes("UTF-8");
            request = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Call<ResponseBody> call = APIClient.getInstance().loginwithmob(request);
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, true, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Gson gson = new Gson();
                OTPModel otpModel = gson.fromJson(responseData, OTPModel.class);
                if (otpModel.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    otpId = String.valueOf(otpModel.getContent().getOtpId());
                    otp = String.valueOf(otpModel.getContent().getOtp());

//                    edtOTP.setText(otp);
                } else {
                    Utils.showToast(context, otpModel.getMessage());
                }
            }

            @Override
            public void failure(String responseData) {
                PrintLog.e("TAG", "Failure");
            }
        });
    }

    private void sendOtp() {
        Call<ResponseBody> call = APIClient.getInstance().sendOtp(sessionUtil.getToken(), sessionUtil.getId());
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, true, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Gson gson = new Gson();
                OTPModel otpModel = gson.fromJson(responseData, OTPModel.class);
                if (otpModel.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    Bundle bundle = new Bundle();
                    otpId = String.valueOf(otpModel.getContent().getOtpId());
                    otp = String.valueOf(otpModel.getContent().getOtp());
                    binding. edtOTP.setText(otp);
                } else {
                    Utils.showToast(context, otpModel.getMessage());
                }
            }

            @Override
            public void failure(String responseData) {

            }
        });
    }

    private void addWallet() {
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        String request = "";
        try {
            jsonObject.put("mobile", bundle.getString("mobile", ""));
            jsonObject.put("amount", bundle.getString("amount", ""));
            jsonObject.put("type", bundle.getString(TransferWalletActivity.TRANSFER_TYPE, ""));
            jsonObject.put("otpId", otpId);
            jsonObject.put("otp", otp);
            request = jsonObject.toString();
            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, getString(R.string.crypt_pass));
            data = request.getBytes("UTF-8");
            request = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Call<ResponseBody> call = APIClient.getInstance()
                .addWallate(sessionUtil.getToken(), sessionUtil.getId(), request);
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, true, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Gson gson = new Gson();
                WalletTransferModel wtf = gson.fromJson(responseData, WalletTransferModel.class);
                if (wtf.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    DecimalFormat format = new DecimalFormat("0.##");
                    sessionUtil.setAmount(String.valueOf(format.format(wtf.getContent().getPbAmount())));
                    sessionUtil.setWAmount(String.valueOf(format.format(wtf.getContent().getSbAmount())));
                    finish();
                } else {
                    Utils.showToast(context, wtf.getMessage());
                }
            }

            @Override
            public void failure(String responseData) {

            }
        });
    }

    private void getSignUp() {
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        String request = "";
        try {
            jsonObject.put("firstName", bundle.getString("fname", ""));
            jsonObject.put("middelName", bundle.getString("mname", ""));
            jsonObject.put("lastName", bundle.getString("lname", ""));
            jsonObject.put("email", bundle.getString("email", ""));
            jsonObject.put("password", bundle.getString("password", ""));
            jsonObject.put("ReferralCode", bundle.getString("ReferralCode", ""));
            jsonObject.put("mobile_no", bundle.getString("mobile_no", ""));
            jsonObject.put("deviceId", bundle.getString("deviceId", ""));
            jsonObject.put("deviceType", bundle.getString("deviceType", ""));
            jsonObject.put("OneSignalID", OneSignal.getDeviceState().getUserId());
            jsonObject.put("otpId", otpId);
            jsonObject.put("otp", binding.edtOTP.getText().toString());
            jsonObject.put("userName", bundle.getString("userName", ""));
            jsonObject.put("social_Id", bundle.getString("socialid", ""));
            jsonObject.put("social_Type", bundle.getString("socialtype", ""));
            jsonObject.put("StateID", bundle.getInt("StateId", 0));
            jsonObject.put("CityID", bundle.getInt("CityId", 0));
            request = jsonObject.toString();
            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, getString(R.string.crypt_pass));
            data = request.getBytes("UTF-8");
            request = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Call<ResponseBody> call = APIClient.getInstance().signUp(request);
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, true, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                //   Toast.makeText(context, "Reg OneSignalId ==> "+OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId(), Toast.LENGTH_SHORT).show();
                DecimalFormat format = new DecimalFormat("0.##");
                Gson gson = new Gson();
                LoginRegisterModel loginRegisterModel = gson.fromJson(responseData, LoginRegisterModel.class);
                if (loginRegisterModel.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    SessionUtil sessionUtil = new SessionUtil(context);
                    sessionUtil.setData(loginRegisterModel.getContest().getToken(),
                            loginRegisterModel.getContest().getFirstName(), loginRegisterModel.getContest().getMiddelName(), loginRegisterModel.getContest().getLastName(), loginRegisterModel.getContest().getEmail(),
                            loginRegisterModel.getContest().getMobileNo(), bundle.getString("password", ""), loginRegisterModel.getContest().getProfileImage(),
                            loginRegisterModel.getContest().getMyCode(), String.valueOf(loginRegisterModel.getContest().getId()),
                            String.valueOf(format.format(loginRegisterModel.getContest().getWallateDetails().getPbAmount())),
                            String.valueOf(format.format(loginRegisterModel.getContest().getWallateDetails().getSbAmount())),
                            loginRegisterModel.getContest().getSetNotification(), loginRegisterModel.getContest().getUserName(),
                            loginRegisterModel.getContest().getVerify_pan(), loginRegisterModel.getContest().getVerify_bank(),
                            loginRegisterModel.getContest().getVerify_email(),
                            bundle.getString("socialid", ""), bundle.getString("socialtype", ""),
                            loginRegisterModel.getContest().getReferralCode(),
                            String.valueOf(format.format(loginRegisterModel.getContest().getWallateDetails().getCcAmount())),
                            loginRegisterModel.getContest().getWallateDetails().getWalletAuth(), false,
                            loginRegisterModel.getContest().getAutoPilot(),
                            loginRegisterModel.getContest().getIsRedeem());
                    //  Intent intent = new Intent(context,KYCVerificationActivity.class);
                    Intent intent = new Intent(context, HomeActivity.class);
                    CBit.ImageDownload = loginRegisterModel.getContest().getImageDownload();
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Utils.showToast(context, loginRegisterModel.getMessage());
                }
            }

            @Override
            public void failure(String responseData) {
                if (!responseData.isEmpty()) {
                    Utils.showToast(context, responseData);
                }
            }
        });
    }

    private void getLoginWithMob() {
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        String request = "";
        try {
            jsonObject.put("mobile_no", bundle.getString("mobile_no", ""));
            jsonObject.put("deviceId", bundle.getString("deviceId", ""));
            jsonObject.put("deviceType", bundle.getString("deviceType", ""));
            jsonObject.put("OneSignalID", OneSignal.getDeviceState().getUserId());
            jsonObject.put("otpId", otpId);
            jsonObject.put("otp",binding. edtOTP.getText().toString());
            request = jsonObject.toString();
            Log.d(TAG, "request>>>>: " + request);
            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, getString(R.string.crypt_pass));
            data = request.getBytes("UTF-8");
            request = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Call<ResponseBody> call = APIClient.getInstance().signUpwithMobile(request);
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, true, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                //    Toast.makeText(context, "Reg OneSignalId ==> "+OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId(), Toast.LENGTH_SHORT).show();
                DecimalFormat format = new DecimalFormat("0.##");
                Gson gson = new Gson();
                LoginRegisterModel loginRegisterModel = gson.fromJson(responseData, LoginRegisterModel.class);
                if (loginRegisterModel.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    SessionUtil sessionUtil = new SessionUtil(context);
                    sessionUtil.setData(loginRegisterModel.getContest().getToken(),
                            loginRegisterModel.getContest().getFirstName(),
                            loginRegisterModel.getContest().getMiddelName(),
                            loginRegisterModel.getContest().getLastName(),
                            loginRegisterModel.getContest().getEmail(),
                            loginRegisterModel.getContest().getMobileNo(),
                            bundle.getString("password", ""),
                            loginRegisterModel.getContest().getProfileImage(),
                            loginRegisterModel.getContest().getMyCode(),
                            String.valueOf(loginRegisterModel.getContest().getId()),
                            String.valueOf(format.format(loginRegisterModel.getContest().getWallateDetails().getPbAmount())),
                            String.valueOf(format.format(loginRegisterModel.getContest().getWallateDetails().getSbAmount())),
                            loginRegisterModel.getContest().getSetNotification(),
                            loginRegisterModel.getContest().getUserName(),
                            loginRegisterModel.getContest().getVerify_pan(),
                            loginRegisterModel.getContest().getVerify_bank(),
                            loginRegisterModel.getContest().getVerify_email(),
                            bundle.getString("socialid", ""),
                            bundle.getString("socialtype", ""),
                            loginRegisterModel.getContest().getReferralCode(),
                            String.valueOf(format.format(loginRegisterModel.getContest().getWallateDetails().getCcAmount())),
                            loginRegisterModel.getContest().getWallateDetails().getWalletAuth(), true,
                            loginRegisterModel.getContest().getAutoPilot(),
                            loginRegisterModel.getContest().getIsRedeem());
                    Intent intent = new Intent(context, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Utils.showToast(context, loginRegisterModel.getMessage());
                }
            }

            @Override
            public void failure(String responseData) {
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
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
