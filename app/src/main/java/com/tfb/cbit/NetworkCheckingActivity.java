package com.tfb.cbit;

import android.content.Context;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.tfb.cbit.utility.Utils;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class NetworkCheckingActivity extends AppCompatActivity  {

    Context context;
    private Timer mTimer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_checking);
        context = this;

        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new CheckForConnection(), 0, 5 * 1000);
    }

    class CheckForConnection extends TimerTask {
        @Override
        public void run() {
            new CheckNetwork().execute();

        }
    }

    class CheckNetwork extends AsyncTask<Void,Void,Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            return isNetworkAvailable();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(aBoolean){
                hasInternetConnection();
            }else {
                hasNoInternetConnection();
            }
        }
    }

    private boolean isNetworkAvailable(){
        try {
            HttpURLConnection urlc = (HttpURLConnection) (new URL("http://68.183.144.102:3500/api/checkNetwork").openConnection());
            urlc.setRequestProperty("User-Agent", "Android");
            urlc.setRequestProperty("Connection", "close");
            urlc.setConnectTimeout(1500);
            urlc.setReadTimeout(1500);
            urlc.connect();
            if ((urlc.getResponseCode() == 200)) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    public void hasInternetConnection() {
        Utils.showToast(context,"Connected");
    }


    public void hasNoInternetConnection() {
        Utils.showToast(context,"No Connected");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
