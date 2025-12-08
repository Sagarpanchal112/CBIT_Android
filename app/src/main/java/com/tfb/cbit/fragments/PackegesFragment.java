package com.tfb.cbit.fragments;


import android.content.Context;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.tfb.cbit.CBit;
import com.tfb.cbit.R;
import com.tfb.cbit.adapter.MyJTicktAdapter;
import com.tfb.cbit.api.APIClient;
import com.tfb.cbit.api.ApiCallback;
import com.tfb.cbit.api.NewApiCall;
import com.tfb.cbit.databinding.FragmentAboutBinding;
import com.tfb.cbit.databinding.FragmentMyJTicketBinding;
import com.tfb.cbit.interfaces.OnItemClickJTicket;
import com.tfb.cbit.models.ApplyJticketModel;
import com.tfb.cbit.models.MyJTicket.Contest;
import com.tfb.cbit.models.MyJTicket.MyJTcktModel;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PackegesFragment extends Fragment {

    private static final String TAG = "PackegesFragment";

    private Context context;
    private SessionUtil sessionUtil;
    private NewApiCall newApiCall;
    private List<Contest> MyJTcktList = new ArrayList<>();
    private MyJTicktAdapter myJTicktAdapter;


    public PackegesFragment() {
        // Required empty public constructor
    }


    public static PackegesFragment newInstance() {
        PackegesFragment fragment = new PackegesFragment();
        return fragment;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    private FragmentMyJTicketBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMyJTicketBinding.bind(LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_about, container, false));
        View view = binding.getRoot();
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding. linDetail.setVisibility(View.GONE);
        sessionUtil = new SessionUtil(context);
        newApiCall = new NewApiCall();
        TabsPagerAdapter tabsPagerAdapter = new TabsPagerAdapter(getChildFragmentManager());
        binding.dashboardViewPager.setAdapter(tabsPagerAdapter);
        binding. dashboardTabLayout.setupWithViewPager(binding.dashboardViewPager);

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
                    fragment = PackagesTabFragment.newInstance(false);
                    break;
                case 1:
                    fragment = PackagesTabFragment.newInstance(true);
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
                    title = "Packages";
                    break;
                case 1:
                    title = "MyPackages";
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
