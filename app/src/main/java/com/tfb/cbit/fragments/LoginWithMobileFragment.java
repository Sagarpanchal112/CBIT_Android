package com.tfb.cbit.fragments;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.gson.Gson;
import com.onesignal.OneSignal;
import com.tfb.cbit.CBit;
import com.tfb.cbit.R;
import com.tfb.cbit.activities.ForgotPasswordActivity;
import com.tfb.cbit.activities.HomeActivity;
import com.tfb.cbit.activities.OTPVerificationActivity;
import com.tfb.cbit.activities.TutorialActivity;
import com.tfb.cbit.api.APIClient;
import com.tfb.cbit.api.ApiCallback;
import com.tfb.cbit.api.NewApiCall;
import com.tfb.cbit.api.NewApiLoginCall;
import com.tfb.cbit.databinding.FragmentAboutBinding;
import com.tfb.cbit.databinding.FragmentLoginWithMobBinding;
import com.tfb.cbit.models.CheckUpdateModel;
import com.tfb.cbit.models.CommonRes;
import com.tfb.cbit.models.LoginMobileModel;
import com.tfb.cbit.models.login_register.LoginRegisterModel;
import com.tfb.cbit.models.wallet_transfer_otp.OTPModel;
import com.tfb.cbit.utility.CustomDialog;
import com.tfb.cbit.utility.LogHelper;
import com.tfb.cbit.utility.MyValidator;
import com.tfb.cbit.utility.PrintLog;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.Utils;

import net.mftd313.updatelibrary.UpdateLibrary;
import net.mftd313.updatelibrary.listeners.UpdateDownloadStartedListener;
import net.mftd313.updatelibrary.listeners.UpdateInstallStartedListener;
import net.mftd313.updatelibrary.listeners.UpdateReadyToDownloadListener;
import net.mftd313.updatelibrary.listeners.UpdateReadyToInstallListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Arrays;

import okhttp3.ResponseBody;
import retrofit2.Call;


public class LoginWithMobileFragment extends Fragment {

    private static final String TAG = "LoginFragment";


    private NewApiCall newApiCall;
    private NewApiLoginCall newApiLoginCall;
    private Context context;
     DecimalFormat format = new DecimalFormat("0.##");
    CallbackManager callbackManager;
    private SessionUtil sessionUtil;
   // String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
   // int PERMISSION_ALL = 1;
    private ProgressDialog progressDialog;

    public LoginWithMobileFragment() {
        // Required empty public constructor
    }

    public static LoginWithMobileFragment newInstance() {
        LoginWithMobileFragment fragment = new LoginWithMobileFragment();
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
    private FragmentLoginWithMobBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentLoginWithMobBinding.bind(LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_login_with_mob, container, false));
        View view = binding.getRoot();
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
         sessionUtil = new SessionUtil(context);
        newApiCall = new NewApiCall();
        newApiLoginCall = new NewApiLoginCall();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        callbackManager = CallbackManager.Factory.create();
        checkVersion();
        facebookLoginManagerCallback();

        binding.ivFacebook.setOnClickListener(view1 -> {
            ivFacebookClick();
        });
        binding.btnLogin.setOnClickListener(view1 -> {
            btnLoginClick();
        });
        binding.tvForgotPass.setOnClickListener(view1 -> {
            tvForgotPassClick();
        });
        binding.linearRegister.setOnClickListener(view1 -> {
            linearRegisterClick();
        });
        binding.linearloginwithmob.setOnClickListener(view1 -> {
            linearloginwithmobClick();
        });
        binding.tvHowtoPlay.setOnClickListener(view1 -> {
            tvHowtoPlayClick();
        });
    }

    protected void ivFacebookClick() {
        if (Utils.isNetworkAvailable(context)) {
            LoginManager.getInstance().logInWithReadPermissions(this,
                    Arrays.asList("public_profile", "email"));
        } else {
            Utils.showToast(context, "No Internet Available");
        }
    }


    protected void btnLoginClick() {
        if (isValidForm()) {
            getOtp();
        }
    }


    protected void tvForgotPassClick() {
        startActivity(new Intent(context, ForgotPasswordActivity.class));
    }

    protected void linearRegisterClick() {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameContainer, SignUpFragment.newInstance("", ""))
                    .commit();
        }
    }


    protected void linearloginwithmobClick() {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameContainer, LoginFragment.newInstance())
                    .commit();
        }
    }



    protected void tvHowtoPlayClick() {
        startActivity(new Intent(context, TutorialActivity.class));
        //startActivity(new Intent(context, HowtoPlayActivity.class));
    }
    private void checkVersion() {
        File file = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + getActivity().getPackageName() + "/files/cbitoriginal.apk");
        LogHelper.e("TAG", "Path : " + file.getAbsolutePath());
        if (file.exists()) {
            file.delete();
            LogHelper.e("TAG", " IF Path : Delete");
        }
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        String request = "";
        try {
            jsonObject.put("version", Utils.getVersionName(context));
            jsonObject.put("plateform", "Android");
            request = jsonObject.toString();
            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, getString(R.string.crypt_pass));
            data = request.getBytes("UTF-8");
            request = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Call<ResponseBody> call = APIClient.getInstance().CheckVersion(request);
        NewApiLoginCall newApiCall = new NewApiLoginCall();
        newApiCall.makeApiCall(context, true, call, new ApiCallback() {
            @Override
            public void success(String responseData) {

                Log.d(TAG, "success: " + responseData);
//                OTPModel otpModel = gson.fromJson(responseData,OTPModel.class);
//                if(otpModel.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
////                    edtOTP.setText(otp);
//                }else{
//                    Utils.showToast(context,otpModel.getMessage());
//                }
                Gson gson = new Gson();
                CommonRes commonRes = gson.fromJson(responseData, CommonRes.class);
                if (commonRes.getStatus() == Utils.StandardStatusCodes.SUCCESS) {
                   /* if (!hasPermissions(getActivity(), PERMISSIONS)) {
                        ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, PERMISSION_ALL);
                    }*/

                } else if (commonRes.getStatus() == Utils.StandardStatusCodes.Update_USER) {
                    CheckUpdateModel loginMobileModel = gson.fromJson(responseData, CheckUpdateModel.class);
                    CustomDialog customDialog = new CustomDialog();
                    customDialog.showDialogOneButton(context, "Update Alert...!", commonRes.getMessage(),
                            "OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    if (CBit.getSocketUtils().getmSocket().connected()) {
                                        CBit.getSocketUtils().disConnect();
                                    }
                                    sessionUtil.setAutoStart(false);
                                    String url = loginMobileModel.getAppurl();
                                    //  DownloadApk downloadApk = new DownloadApk(context);
                                    //  downloadApk.startDownloadingApk(url);

                                    UpdateLibrary.with(getActivity())
                                            .setDownloadingNotificationTitle(getString(R.string.app_name))
                                            .setDownloadingNotificationText(getString(R.string.downloading_new_version))
                                            .setDownloadedNotificationTitle(getString(R.string.app_name))
                                            .setDownloadedNotificationText(getString(R.string.download_completed))
                                            .setDownloadedNotificationSmallIconResource(R.mipmap.app_green_icon)
                                            .setDownloadedNotificationLargeIconResource(R.mipmap.app_green_icon)
                                            .setUpdateReadyToDownloadListener(new UpdateReadyToDownloadListener() {
                                                @Override
                                                public void onReadyToDownload(final Context context, Uri uri) {
                                                    progressDialog.hide();
                                                    UpdateLibrary.getUpdateManager().download(context);

                                                }
                                            })
                                            .setUpdateDownloadStartedListener(new UpdateDownloadStartedListener() {
                                                @Override
                                                public void onDownloadStarted(Context context, Uri uri) {
                                                    progressDialog.setMessage(getString(R.string.downloading_new_version));
                                                    progressDialog.show();
                                                }
                                            })
                                            .setUpdateReadyToInstallListener(new UpdateReadyToInstallListener() {
                                                @Override
                                                public void onReadyToInstall(final Context context, Uri uri) {
                                                    progressDialog.hide();
                                                    UpdateLibrary.getUpdateManager().install(context);

                                                }
                                            })
                                            .setUpdateInstallStartedListener(new UpdateInstallStartedListener() {
                                                @Override
                                                public void onInstallStarted(Context context, Uri uri) {
                                                    progressDialog.setMessage(getString(R.string.installing_new_version));
                                                    progressDialog.show();
                                                }
                                            })

                                            .init(Uri.parse(url));
                                }
                            });
                }


            }

            @Override
            public void failure(String responseData) {
                PrintLog.e("TAG", "Failure");
            }
        });
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void facebookLoginManagerCallback() {
        LoginManager.getInstance().logOut();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        Log.d(TAG, "loginresult: " + loginResult.toString());
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(
                                            JSONObject object,
                                            GraphResponse response) {
                                        fbSignIn(object, loginResult.getAccessToken().getToken());
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,first_name,last_name,email,picture");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        Utils.showToast(context, "Login Cancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.e("TAG", exception.getMessage() + "");
                    }
                });
    }

    // getting detail of facebook user profile
    private void fbSignIn(JSONObject user, String accessToken) {
        //System.out.println("FB Login : " + user);
        String facebookfirstName = user.optString("first_name");
        String facebooklastname = user.optString("last_name");
        String facebookemail = user.optString("email");
        //  JSONObject picObj = user.optJSONObject("picture");
        // JSONObject data = picObj.optJSONObject("data");
        // facebookpicture = data.optString("url");
        //Log.d(TAG,"FB Pic "+facebookpicture);
        String facebookid = user.optString("id");
        PrintLog.e("TAG", "Facebook " + facebookid);
        if (LoginManager.getInstance() != null)
            LoginManager.getInstance().logOut();

        // String fullName = facebookfirstName + " " +facebooklastname;
        getSocialLogin(facebookid, facebookfirstName, facebooklastname, facebookemail, "Facebook");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    private boolean isValidForm() {
        if (!MyValidator.isBlankETError(context,  binding.edtMobile, "Enter Mobile", 10, 10)) {
            return false;
        } else {
            return true;
        }
    }

    private void getOtp() {
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        String request = "";
        try {
            jsonObject.put("mobile_no", binding. edtMobile.getText().toString().trim());
            jsonObject.put("version", Utils.getVersionName(context));
            jsonObject.put("plateform", "Android");
            request = jsonObject.toString();
            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, getString(R.string.crypt_pass));
            data = request.getBytes("UTF-8");
            request = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String UUID = OneSignal.getDeviceState().getUserId();

        Call<ResponseBody> call = APIClient.getInstance().loginwithmob(request);
        newApiLoginCall.makeApiCall(context, true, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Gson gson = new Gson();
                Log.d(TAG, "otp>>>>: " + responseData);
                OTPModel otpModel = gson.fromJson(responseData, OTPModel.class);
                if (otpModel.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    Bundle bundle = new Bundle();
                    bundle.putString("mobile_no",  binding.edtMobile.getText().toString().trim());
                    bundle.putString("deviceId", UUID);
                    bundle.putString("deviceType", "android");
                    bundle.putString("otpId", String.valueOf(otpModel.getContent().getOtpId()));
                    bundle.putString("otp", String.valueOf(otpModel.getContent().getOtp()));
                    bundle.putString(OTPVerificationActivity.SCREEN_TYPE, "loginwithmobile");
                    Intent intent = new Intent(context, OTPVerificationActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else if (otpModel.getStatusCode() == Utils.StandardStatusCodes.Update_USER) {
                    LoginMobileModel loginMobileModel = gson.fromJson(responseData, LoginMobileModel.class);
                    if (loginMobileModel.getContent().getStatuss().equalsIgnoreCase("Update")) {
                        checkVersion();
                    }else if (loginMobileModel.getContent().getStatuss().equalsIgnoreCase("MobileNo")) {
                        CustomDialog customDialog = new CustomDialog();
                        customDialog.showDialogOneButton(context, "Welcome to Kitty Games", "Please enter your details to register with Kitty Games .",
                                "OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        getActivity().getSupportFragmentManager()
                                                .beginTransaction()
                                                .replace(R.id.frameContainer, SignUpFragment.newInstance("",  binding.edtMobile.getText().toString()))
                                                .addToBackStack(null)
                                                .commit();
                                    }
                                });
                    } else {
                       checkReferal();
                    }
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

    private void checkReferal() {
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        String request = "";
        try {
            jsonObject.put("mobile",  binding.edtMobile.getText().toString().trim());
            request = jsonObject.toString();
            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, getString(R.string.crypt_pass));
            data = request.getBytes("UTF-8");
            request = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Call<ResponseBody> call = APIClient.getInstance().checkForReferral(request);
        newApiCall.makeApiCall(context, true, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                CustomDialog customDialog = new CustomDialog();
                customDialog.showDialogOneButton(context, "Welcome to Kitty Games", "Please enter your details to register with Kitty Games .",
                        "OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                getActivity().getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.frameContainer, SignUpFragment.newInstance("",  binding.edtMobile.getText().toString()))
                                        .addToBackStack(null)
                                        .commit();
                            }
                        });


            }

            @Override
            public void failure(String responseData) {
                PrintLog.e("TAG", "Failure");
            }
        });
    }




    private void getSocialLogin(final String social_Id, final String fname, final String lname, final String email, final String social_type) {
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        String request = "";
        try {
            jsonObject.put("social_Id", social_Id);
            jsonObject.put("social_Type", social_type);
            jsonObject.put("deviceId", sessionUtil.getFcmtoken());
            jsonObject.put("deviceType", "android");
            jsonObject.put("OneSignalID",OneSignal.getDeviceState().getUserId());// OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId());
            jsonObject.put("version", Utils.getVersionName(context));
            jsonObject.put("plateform", "Android");

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
                .signIn(request);
        //.signIn(edtEmail.getText().toString().trim(),edtPassword.getText().toString().trim(),sessionUtil.getFcmtoken(),"android");
        newApiCall.makeApiCall(context, true, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Log.d(TAG, "logindata: " + responseData);
                Gson gson = new Gson();
                LoginRegisterModel loginRegisterModel = gson.fromJson(responseData, LoginRegisterModel.class);
                if (loginRegisterModel.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    SessionUtil sessionUtil = new SessionUtil(context);
                    sessionUtil.setData(loginRegisterModel.getContest().getToken(),
                            loginRegisterModel.getContest().getFirstName(), loginRegisterModel.getContest().getMiddelName(),
                            loginRegisterModel.getContest().getLastName(),
                            loginRegisterModel.getContest().getEmail(),
                            loginRegisterModel.getContest().getMobileNo(),
                            binding.   edtPassword.getText().toString().trim(),
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
                            social_Id, social_type, loginRegisterModel.getContest().getReferralCode(),
                            String.valueOf(format.format(loginRegisterModel.getContest().getWallateDetails().getCcAmount())),
                            loginRegisterModel.getContest().getWallateDetails().getWalletAuth(), false,
                            loginRegisterModel.getContest().getAutoPilot(),
                            loginRegisterModel.getContest().getIsRedeem());
                    startActivity(new Intent(context, HomeActivity.class));
                    if (getActivity() != null)
                        getActivity().finish();
                } else if (loginRegisterModel.getStatusCode() == Utils.StandardStatusCodes.NO_DATA_FOUND) {
                    Bundle bundle = new Bundle();
                    bundle.putString("fname", fname);
                    bundle.putString("lname", lname);
                    bundle.putString("email", email);
                    bundle.putString("socialid", social_Id);
                    bundle.putString("socialtype", social_type);
                    if (getActivity() != null) {
                        CustomDialog customDialog = new CustomDialog();
                        customDialog.showDialogOneButton(context, "Welcome to Kitty Games", "Please enter your details to register with Kitty Games .",
                                "OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        getActivity().getSupportFragmentManager()
                                                .beginTransaction()
                                                .replace(R.id.frameContainer, SignUpFragment.newInstance(bundle))
                                                .commit();
                                    }
                                });

                    }
                    //EventBus.getDefault().post(new FbRegister(social_Id,social_type,email,fname));
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
    public void onDestroyView() {

        super.onDestroyView();

    }
}
