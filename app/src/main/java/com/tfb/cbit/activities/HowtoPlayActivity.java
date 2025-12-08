package com.tfb.cbit.activities;

import android.os.Bundle;

import com.tfb.cbit.R;
import com.tfb.cbit.databinding.ActivityAboutUsBinding;
import com.tfb.cbit.databinding.ActivityHowtoPlayBinding;
import com.tfb.cbit.fragments.HowtoPlayFragment;


public class HowtoPlayActivity extends BaseAppCompactActivity {
    private ActivityHowtoPlayBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHowtoPlayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.frameContent, HowtoPlayFragment.newInstance())
                .commit();

        binding.ivBack.setOnClickListener(view -> {
            onBackPressed();
        });
    }


    @Override
    protected void onDestroy() {

        super.onDestroy();
    }
}
