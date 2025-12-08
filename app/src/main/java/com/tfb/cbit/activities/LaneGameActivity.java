/*
package com.tfb.cbit.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.format.DateFormat;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tfb.cbit.R;
import com.tfb.cbit.adapter.LaneBricksAdapter;
import com.tfb.cbit.adapter.SpinningBricksAdapter;
import com.tfb.cbit.adapter.SpinningTicketAdapter;


public class LaneGameActivity extends AppCompatActivity {

    private Context context;
    Handler handlerSpecial = new Handler();
    Handler handlerLaneTwo = new Handler();

    public LaneBricksAdapter bricksAdapter;
    public SpinningTicketAdapter ticketAdapter;
    private Handler handler = new Handler();
    private boolean isHandlerPost = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);

        setContentView(R.layout.activity_lane_game);
        context = this;
        ButterKnife.bind(this);

        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        rvLanTwo.setLayoutManager(staggeredGridLayoutManager);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        rvLanTwo.setAdapter(bricksAdapter);
         rvLanOne.setLayoutManager(staggeredGridLayoutManager);
        rvLanOne.setAdapter(bricksAdapter);


        new CountDownTimer(60000, 500) {
            int count = 0,countTwo=0;
            public void onTick(long millisUntilFinished) {
                tvRemainingText.setText("Game Starts in : 00:00" + DateFormat.format(":ss", millisUntilFinished).toString());
                //here you can have your logic to set text to edittext

            }

            public void onFinish() {

                tvText.setVisibility(View.VISIBLE);
                tvRemainingText.setVisibility(View.GONE);
                gemeStratTimer();
                setTickets();
            }

        }.start();

    }
    public void setLaneOne(int count) {


    }


    public void gemeStratTimer() {
        new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                tvText.setText(DateFormat.format("ss", millisUntilFinished).toString());
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                finish();
            }

        }.start();
    }
    public void setTickets() {
        rvTickets.setLayoutManager(new LinearLayoutManager(context));
        if (rvTickets.getItemAnimator() != null)
            ((SimpleItemAnimator) rvTickets.getItemAnimator()).setSupportsChangeAnimations(false);
        ticketAdapter = new SpinningTicketAdapter(context);
        rvTickets.setAdapter(ticketAdapter);
    }

}*/
