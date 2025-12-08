package com.tfb.cbit.fragments;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tfb.cbit.R;
import com.tfb.cbit.databinding.FragmentAboutBinding;
import com.tfb.cbit.databinding.FragmentMycontestPkgBinding;


public class MyContestPkgFragment extends Fragment {


    public MyContestPkgFragment() {
        // Required empty public constructor
    }


    public static MyContestPkgFragment newInstance() {
        MyContestPkgFragment fragment = new MyContestPkgFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    private FragmentMycontestPkgBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMycontestPkgBinding.bind(LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_mycontest_pkg, container, false));
        View view = binding.getRoot();
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TabsPagerAdapter tabsPagerAdapter = new TabsPagerAdapter(getChildFragmentManager());
        binding. mycontestPkgViewPager.setAdapter(tabsPagerAdapter);
        binding. mycontestPkgTabLayout.setupWithViewPager(binding.mycontestPkgViewPager);

    }

    private class TabsPagerAdapter extends FragmentStatePagerAdapter {
        private int NUM_ITEMS = 2;
        public TabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment=null;
            switch (position) {
                case 0:
                    fragment= HostedContestFragment.newInstance();
                    break;
                case 1:
                    fragment= PackagesTabFragment.newInstance(true);
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
                    title = "Hosted Contest";
                    break;
                case 1:
                    title = "Packages";
                    break;
            }
            return title;
        }
    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();
    }
}
