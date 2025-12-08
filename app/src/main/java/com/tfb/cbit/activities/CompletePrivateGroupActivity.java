package com.tfb.cbit.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.tfb.cbit.R;
import com.tfb.cbit.databinding.ActivityAboutUsBinding;
import com.tfb.cbit.databinding.ActivityCompletePrivateGroupBinding;


public class CompletePrivateGroupActivity extends AppCompatActivity {


    private ActivityCompletePrivateGroupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCompletePrivateGroupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.tvHostGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CompletePrivateGroupActivity.this, HostGameActivity.class);
                startActivity(i);
            }
        });
    }
}