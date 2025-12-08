package com.tfb.cbit.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.tfb.cbit.api.APIClient;
import com.tfb.cbit.api.ApiCallback;
import com.tfb.cbit.api.NewApiCall;
import com.tfb.cbit.models.AppMaintenceStatus;
import com.tfb.cbit.utility.PrintLog;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.Utils;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;


public class BaseAppCompactActivity extends AppCompatActivity {
    private SessionUtil sessionUtils;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy =
                new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        adjustFontScale(getResources().getConfiguration());
        context=this;
        sessionUtils = new SessionUtil(context);

      //  getAppMaintenanceStatus();
    }

    public String request = "";

    private void getAppMaintenanceStatus() {

        Call<ResponseBody> call = APIClient.getInstance().getAppMaintenanceStatus(sessionUtils.getToken(), sessionUtils.getId());
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(this, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                //  longLog("sucess :",responseData);
                Log.i("sucess :", responseData);
                try {
                    JSONObject jObj = new JSONObject(responseData);
                    Gson gson = new Gson();
                    AppMaintenceStatus nm = gson.fromJson(responseData, AppMaintenceStatus.class);
                    if (nm.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                        if (nm.getContent().getValue() == 1) {
                            Dialog dialog=new AlertDialog.Builder(context).setTitle("CBIT").
                                    setMessage(nm.getMessage()).
                                    setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
finish();
                                }
                            }).setCancelable(false).create();
                            dialog.show();
                        }

                    }
                } catch (Exception e) {

                }

            }

            @Override
            public void failure(String responseData) {


            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void adjustFontScale(Configuration configuration) {
        if (configuration.fontScale > 1) {
            PrintLog.e("TAG", "fontScale=" + configuration.fontScale); //Custom Log class, you can use Log.w
            PrintLog.e("TAG", "font too big. scale down..."); //Custom Log class, you can use Log.w
            configuration.fontScale = (float) 1;
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
            wm.getDefaultDisplay().getMetrics(metrics);
            metrics.scaledDensity = configuration.fontScale * metrics.density;
            getBaseContext().getResources().updateConfiguration(configuration, metrics);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}