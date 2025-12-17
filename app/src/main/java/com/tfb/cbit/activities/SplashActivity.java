package com.tfb.cbit.activities;

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
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.VideoView;

import androidx.core.app.ActivityCompat;

import com.google.gson.Gson;
import com.onesignal.OneSignal;
import com.tfb.cbit.CBit;
import com.tfb.cbit.R;
import com.tfb.cbit.api.APIClient;
import com.tfb.cbit.api.ApiCallback;
import com.tfb.cbit.api.NewApiCall;
import com.tfb.cbit.api.NewApiLoginCall;
import com.tfb.cbit.models.CheckUpdateModel;
import com.tfb.cbit.models.CommonRes;
import com.tfb.cbit.models.login_register.LoginRegisterModel;
import com.tfb.cbit.utility.CustomDialog;
import com.tfb.cbit.utility.LogHelper;
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

import okhttp3.ResponseBody;
import retrofit2.Call;

public class SplashActivity extends BaseAppCompactActivity {

    private static final String TAG = "SplashActivity";
    private SessionUtil sessionUtil;
    private Context context;
    DecimalFormat format = new DecimalFormat("0.##");
   // public RetryPolicy retryPolicy;
   // String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
  //  int PERMISSION_ALL = 1;
    private NewApiLoginCall newApiLoginCall;
    private ProgressDialog progressDialog;
    VideoView videoView;
    Button btnSkip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        context = this;
        sessionUtil = new SessionUtil(context);
        newApiLoginCall = new NewApiLoginCall();
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);

        //retryPolicy = new DefaultRetryPolicy();
      /*  if (!hasPermissions(this, PERMISSIONS)) {
            //ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }*/
        videoView = findViewById(R.id.videoView);
        btnSkip = findViewById(R.id.btnSkip);

        // Video path
        Uri videoUri = Uri.parse("android.resource://"
                + getPackageName() + "/" + R.raw.splash);

        videoView.setVideoURI(videoUri);
        videoView.setOnPreparedListener(mp -> {
            float videoRatio = mp.getVideoWidth() / (float) mp.getVideoHeight();
            float screenRatio = videoView.getWidth() / (float) videoView.getHeight();

            float scale = videoRatio / screenRatio;

            if (scale >= 1f) {
                videoView.setScaleX(scale);
            } else {
                videoView.setScaleY(1f / scale);
            }
        });
        videoView.start();
        videoView.setOnCompletionListener(mp -> openNextScreen());
        btnSkip.setOnClickListener(v -> openNextScreen());
    }
    private void openNextScreen() {
        if (sessionUtil.isLogin()) {
            if (!sessionUtil.getSocialId().isEmpty()) {
                getSocialLogin(sessionUtil.getSocialId(), sessionUtil.getSocialType());
            } else if (sessionUtil.MobileLogin()) {
                checkVersion();
            } else {
                getSignIn();
            }
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(context, LoginSignUpActivity.class));
                    finish();
                }
            }, 2000);
        }
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
    private void getSignIn() {
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        String request = "";
        try {
            jsonObject.put("email", sessionUtil.getEmail());
            jsonObject.put("password", sessionUtil.getPass());
            jsonObject.put("deviceId", sessionUtil.getFcmtoken());
            jsonObject.put("deviceType", "android");
            jsonObject.put("OneSignalID", OneSignal.getDeviceState().getUserId());// OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId());
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
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Log.d(TAG, "success: " + responseData);
                // Toast.makeText(context, "Splash Log OneSignalId ==> "+OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId(), Toast.LENGTH_SHORT).show();
                Gson gson = new Gson();
                LoginRegisterModel loginRegisterModel = gson.fromJson(responseData, LoginRegisterModel.class);
                if (loginRegisterModel.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    SessionUtil sessionUtil = new SessionUtil(context);
                    sessionUtil.setData(loginRegisterModel.getContest().getToken(),
                            loginRegisterModel.getContest().getFirstName(), loginRegisterModel.getContest().getMiddelName(), loginRegisterModel.getContest().getLastName(),
                            loginRegisterModel.getContest().getEmail(),
                            loginRegisterModel.getContest().getMobileNo(), sessionUtil.getPass(), loginRegisterModel.getContest().getProfileImage(),
                            loginRegisterModel.getContest().getMyCode(), String.valueOf(loginRegisterModel.getContest().getId()),
                            String.valueOf(format.format(loginRegisterModel.getContest().getWallateDetails().getPbAmount())),
                            String.valueOf(format.format(loginRegisterModel.getContest().getWallateDetails().getSbAmount())),
                            loginRegisterModel.getContest().getSetNotification(), loginRegisterModel.getContest().getUserName(),
                            loginRegisterModel.getContest().getVerify_pan(), loginRegisterModel.getContest().getVerify_bank(),
                            loginRegisterModel.getContest().getVerify_email(), "", "", loginRegisterModel.getContest().getReferralCode(),
                            String.valueOf(format.format(loginRegisterModel.getContest().getWallateDetails().getCcAmount())), loginRegisterModel.getContest().getWallateDetails().getWalletAuth(), false,
                            loginRegisterModel.getContest().getAutoPilot(),
                            loginRegisterModel.getContest().getIsRedeem());
                   /* if (!hasPermissions(SplashActivity.this, PERMISSIONS)) {
                        // ActivityCompat.requestPermissions(SplashActivity.this, PERMISSIONS, PERMISSION_ALL);
                    } else {*/
                        // downloadImages();
                        startActivity(new Intent(context, HomeActivity.class));
                        finish();
                   // }

                } else {
                    String fcmToken = sessionUtil.getFcmtoken();
                    sessionUtil.logOut();
                    sessionUtil.setFCMToken(fcmToken);
                    startActivity(new Intent(context, LoginSignUpActivity.class));
                    finish();
                }
            }

            @Override
            public void failure(String responseData) {
                String fcmToken = sessionUtil.getFcmtoken();
                sessionUtil.logOut();
                sessionUtil.setFCMToken(fcmToken);
                startActivity(new Intent(context, LoginSignUpActivity.class));
                finish();
            }
        });

    }
    private void checkVersion() {
        File file = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + getPackageName() + "/files/cbitoriginal.apk");
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
        NewApiCall newApiCall = new NewApiCall();
        newApiLoginCall.makeApiCall(context, true, call, new ApiCallback() {
            @Override
            public void success(String responseData) {

                Log.d(TAG, "success: " + responseData);

                Gson gson = new Gson();
                CommonRes commonRes = gson.fromJson(responseData, CommonRes.class);
                if (commonRes.getStatus() == Utils.StandardStatusCodes.SUCCESS) {
                  /*  if (!hasPermissions(SplashActivity.this, PERMISSIONS)) {
                        ActivityCompat.requestPermissions(SplashActivity.this, PERMISSIONS, PERMISSION_ALL);
                    } else {*/

                        startActivity(new Intent(context, HomeActivity.class));
                        finish();
                        //downloadImages();
                   // }

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
                                    Log.i("Url", "==>" + url);
                                    //  DownloadApk downloadApk = new DownloadApk(context);
                                    //  downloadApk.startDownloadingApk(url);
                                    UpdateLibrary.with(SplashActivity.this)
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
                                                    //progressDialog.show();
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
    private void getSocialLogin(final String social_Id, final String social_type) {
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        String request = "";
        try {
            jsonObject.put("social_Id", social_Id);
            jsonObject.put("social_Type", social_type);
            jsonObject.put("deviceId", sessionUtil.getFcmtoken());
            jsonObject.put("deviceType", "android");
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
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {

                Gson gson = new Gson();
                LoginRegisterModel loginRegisterModel = gson.fromJson(responseData, LoginRegisterModel.class);
                if (loginRegisterModel.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    SessionUtil sessionUtil = new SessionUtil(context);
                    sessionUtil.setData(loginRegisterModel.getContest().getToken(),
                            loginRegisterModel.getContest().getFirstName(), loginRegisterModel.getContest().getMiddelName(), loginRegisterModel.getContest().getLastName(),
                            loginRegisterModel.getContest().getEmail(),
                            loginRegisterModel.getContest().getMobileNo(), "", loginRegisterModel.getContest().getProfileImage(),
                            loginRegisterModel.getContest().getMyCode(), String.valueOf(loginRegisterModel.getContest().getId()),
                            String.valueOf(format.format(loginRegisterModel.getContest().getWallateDetails().getPbAmount())),
                            String.valueOf(format.format(loginRegisterModel.getContest().getWallateDetails().getSbAmount())),
                            loginRegisterModel.getContest().getSetNotification(), loginRegisterModel.getContest().getUserName(),
                            loginRegisterModel.getContest().getVerify_pan(), loginRegisterModel.getContest().getVerify_bank(),
                            loginRegisterModel.getContest().getVerify_email(), social_Id, social_type, loginRegisterModel.getContest().getReferralCode(),
                            String.valueOf(format.format(loginRegisterModel.getContest().getWallateDetails().getCcAmount())), loginRegisterModel.getContest().getWallateDetails().getWalletAuth(), false,
                            loginRegisterModel.getContest().getAutoPilot(),
                            loginRegisterModel.getContest().getIsRedeem());
                    startActivity(new Intent(context, HomeActivity.class));
                    finish();
                } else {
                    String fcmToken = sessionUtil.getFcmtoken();
                    sessionUtil.logOut();
                    sessionUtil.setFCMToken(fcmToken);
                    startActivity(new Intent(context, LoginSignUpActivity.class));
                    finish();
                }
            }

            @Override
            public void failure(String responseData) {
                String fcmToken = sessionUtil.getFcmtoken();
                sessionUtil.logOut();
                sessionUtil.setFCMToken(fcmToken);
                startActivity(new Intent(context, LoginSignUpActivity.class));
                finish();
            }
        });

    }
}
