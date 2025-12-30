package com.tfb.cbit.activities;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.tfb.cbit.R;
import com.tfb.cbit.databinding.ActivityAboutUsBinding;
import com.tfb.cbit.databinding.ActivityHoroscopeBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class HoroscopeActivity extends AppCompatActivity {


    long sec = 0;
    long rse = 0;

    private ActivityHoroscopeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHoroscopeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        CountDownTimer Count = new CountDownTimer(30000, 100) {
            public void onTick(long millisUntilFinished) {
                int seconds = (int) ((millisUntilFinished / 1000));
                binding.tvRemainingText.setText(seconds + ":" + millisUntilFinished % 1000);
                sec = millisUntilFinished;
            }

            public void onFinish() {
                binding.tvRemainingText.setText("Kitty Games");
            }
        };
        Count.start();
        binding.tvNineLockNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rse = 30000 - sec;
                int seconds = (int) ((rse / 1000));
              //  tvLockTime.setText("Locked at: " +seconds + ":" + rse % 1000);
                binding. tvLockTime.setText("Locked at: " + binding.tvRemainingText.getText().toString() + "");
                Log.i("lock_time", getDate(1618916009440L,"yyyy-MM-dd HH:mm:ss.SSSS"));
            }
        });

    }
    public static String getDate(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
}