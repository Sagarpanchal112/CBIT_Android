package com.tfb.cbit.activities;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.tfb.cbit.R;

public class LuckyGameActivity extends AppCompatActivity {
    Animation anim1,anim2,anim3,anim4;
    ImageView img_lucky, img_unlucky1, img_unlucky, img_lucky1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lucky_game);
        img_lucky = findViewById(R.id.img_lucky);
        img_unlucky1 = findViewById(R.id.img_unlucky1);
        img_unlucky = findViewById(R.id.img_unlucky);
        img_lucky1 = findViewById(R.id.img_lucky1);
        anim1 = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.up_from_bottom);
        anim3 = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.up_from_bottom);
        anim4 = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.up_from_bottom);
        img_lucky.startAnimation(anim1);
        anim2 = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.up_from_bottom);
        img_lucky1.startAnimation(anim2);
        img_unlucky.startAnimation(anim4);
        img_unlucky1.startAnimation(anim3);

        // start the animation

    }
}