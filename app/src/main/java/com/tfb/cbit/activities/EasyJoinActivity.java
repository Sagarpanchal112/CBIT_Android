package com.tfb.cbit.activities;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.tfb.cbit.R;
import com.tfb.cbit.databinding.ActivityAboutUsBinding;
import com.tfb.cbit.databinding.ActivityEasyJoinBinding;
import com.tfb.cbit.fragments.AutoRenewFragment;
import com.tfb.cbit.fragments.EasyJoinFragment;
import com.tfb.cbit.fragments.PlayContestFragment;
import com.tfb.cbit.utility.SessionUtil;


public class EasyJoinActivity extends AppCompatActivity {
    private SessionUtil sessionUtil;
    private Context context;
    private ActivityEasyJoinBinding binding;

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEasyJoinBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
         TabsPagerAdapter tabsPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        binding.dashboardViewPager.setAdapter(tabsPagerAdapter);
        binding.dashboardTabLayout.setupWithViewPager(binding.dashboardViewPager);
        binding.ivBack.setOnClickListener(view -> {
            onBackPressed();
        });
    }

    private class TabsPagerAdapter extends FragmentStatePagerAdapter {
        private int NUM_ITEMS = 2;
        private FragmentManager mFragmentManager;

        public TabsPagerAdapter(FragmentManager fm) {
            super(fm);
            mFragmentManager = fm;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = EasyJoinFragment.newInstance();
                    break;
                case 1:
                    fragment = AutoRenewFragment.newInstance();
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            String title = "";
            switch (position) {
                case 0:
                    title = "Easy Join";
                    break;
                case 1:
                    title = "Auto Renew";
                    break;
            }
            return title;
        }
    }


}