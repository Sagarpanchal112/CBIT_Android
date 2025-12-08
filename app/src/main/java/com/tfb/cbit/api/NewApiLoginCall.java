package com.tfb.cbit.api;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tfb.cbit.CBit;
import com.tfb.cbit.R;
import com.tfb.cbit.activities.LoginSignUpActivity;
import com.tfb.cbit.models.CommonRes;
import com.tfb.cbit.utility.CustomDialog;
import com.tfb.cbit.utility.PrintLog;
import com.tfb.cbit.utility.Utils;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewApiLoginCall {

    private CustomDialog customDialog;
    private String TAG = "api";
    private Dialog dialog;
    private String msg = "";
    private SharedPreferences preferences;

    public void makeApiCall(final Context context, final boolean isLoadingNeeded, Call<ResponseBody> call, final ApiCallback ApiCallback) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        customDialog = new CustomDialog();
        if (isConnectingToInternet(context)) {
            //Todo isLoadingNeed
            if (isLoadingNeeded)
                customDialog.displayProgress(context, context.getString(R.string.loading));
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    System.err.println(call.request());
                    if (isLoadingNeeded)
                        customDialog.dismissProgress(context);
                    Gson gson = new Gson();
                    String bodyString = null;
                    if (response.isSuccessful()) {
                        try {
                            bodyString = new String(response.body().bytes());
                            // Log.d(TAG, "onResponse>>: "+response.);
                            PrintLog.e(TAG, "WS call success res encrypt:=> " + bodyString);
                            bodyString = CBit.getCryptLib().decryptCipherTextWithRandomIV(bodyString, context.getString(R.string.crypt_pass));
                            PrintLog.e(TAG, "WS call success res :=> " + bodyString);
                            CommonRes commonRes = gson.fromJson(bodyString, CommonRes.class);
                            switch (commonRes.getStatus()) {
                                case Utils.StandardStatusCodes.SUCCESS:
                                    msg = commonRes.getMessage();
                                    if (msg != null && !msg.isEmpty()) {
                                        ApiCallback.success(bodyString);
                                    } else {
                                        ApiCallback.success(bodyString);
                                    }
                                    break;
                                case Utils.StandardStatusCodes.NO_DATA_FOUND:
                                    msg = commonRes.getMessage();
                                    if (msg != null && !msg.isEmpty()) {
                                        ApiCallback.success(bodyString);
                                    } else {
                                        ApiCallback.success(bodyString);
                                    }
                                    break;
                                case Utils.StandardStatusCodes.BLOCK_USER:
                                    ApiCallback.success(bodyString);
                                    break;
                                case Utils.StandardStatusCodes.Update_USER:
                                    ApiCallback.success(bodyString);
                                    break;
                                case Utils.StandardStatusCodes.BAD_REQUEST:
                                case Utils.StandardStatusCodes.DUPLICATE_ERROR:
                                case Utils.StandardStatusCodes.CONFLICT:
                                case Utils.StandardStatusCodes.NOTACCEPTABLE:
                                    msg = commonRes.getMessage();
                                    Utils.showToast(context, msg);
                                    break;
                                case Utils.StandardStatusCodes.UNAUTHORISE:
                                    ApiCallback.failure(bodyString);
                                    Intent intent = new Intent(context, LoginSignUpActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    context.startActivity(intent);

                                    break;
                                default:
                                    break;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        Log.d(TAG, "onResponse:call " + call.toString());
                        Log.d(TAG, "onResponse: " + new Gson().toJson(response));
                        if (response.code() == Utils.StandardStatusCodes.UNAUTHORISE) {
                            try {
                                bodyString = new String(response.errorBody().bytes());
                                ApiCallback.failure(bodyString);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        } else {
                            Toast.makeText(context, "network error", Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, t.toString());
                    customDialog.dismissProgress(context);
                }
            });
        } else {
            Utils.showToast(context, "No Internet Connection");
        }
    }

    private boolean isConnectingToInternet(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivity != null;
        NetworkInfo activeNetwork = connectivity.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }
}





