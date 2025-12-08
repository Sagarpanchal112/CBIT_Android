package com.tfb.cbit.fragments;



import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import com.tfb.cbit.R;
import com.tfb.cbit.databinding.FragmentAboutBinding;
import com.tfb.cbit.databinding.FragmentHistoryBinding;

public class HistoryFragment extends Fragment {



    public HistoryFragment() {
        // Required empty public constructor
    }

    public static HistoryFragment newInstance() {
        HistoryFragment fragment = new HistoryFragment();
        return fragment;
    }





    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       }
    private FragmentHistoryBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHistoryBinding.bind(LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_history, container, false));
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TabsPagerAdapter tabsPagerAdapter = new TabsPagerAdapter(getChildFragmentManager());
        binding. dashboardViewPager.setAdapter(tabsPagerAdapter);
        binding. dashboardTabLayout.setupWithViewPager( binding.dashboardViewPager);
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
                    fragment = HistoryTabFragment.newInstance("1");
                    break;
                case 1:
                  //  fragment = LiveHistoryTabFragment.newInstance("0");
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
                    title = "AnyTime Game";
                    break;
                case 1:
                    title = "Live Game ";
                    break;
            }
            return title;
        }
    }

}
