package com.tfb.cbit.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.SnapHelper;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.tfb.cbit.R;
import com.tfb.cbit.activities.AnyTimeGameActivity;
import com.tfb.cbit.activities.AnyTimeNumberGameActivity;
import com.tfb.cbit.activities.AnyTimeSpinningActivity;
import com.tfb.cbit.activities.IdsActivity;
import com.tfb.cbit.activities.KYCVerificationActivity;
import com.tfb.cbit.activities.SpiningTicketSelectionActivity;
import com.tfb.cbit.activities.TicketSelectionActivity;
import com.tfb.cbit.adapter.AdSliderAdapter;
import com.tfb.cbit.adapter.SpeacialContestAdapter;
import com.tfb.cbit.api.APIClient;
import com.tfb.cbit.api.ApiCallback;
import com.tfb.cbit.api.NewApiCall;
import com.tfb.cbit.databinding.FragmentAboutBinding;
import com.tfb.cbit.databinding.FragmentDashBoardBinding;
import com.tfb.cbit.event.UpdateSpecialContestEvent;
import com.tfb.cbit.interfaces.OnItemClickListener;
import com.tfb.cbit.models.JAssetsModel;
import com.tfb.cbit.models.JHitsTotalAmountModel;
import com.tfb.cbit.models.NoticountModel;
import com.tfb.cbit.models.ReferralComissionModel;
import com.tfb.cbit.models.advertise.AdvertiseModel;
import com.tfb.cbit.models.gamelist.Contest;
import com.tfb.cbit.models.gamelist.GameContestModel;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.Utils;
import com.tfb.cbit.views.EndlessRecyclerViewScrollListener;
import com.tfb.cbit.views.FixedAspectRatioFrameLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import ru.tinkoff.scrollingpagerindicator.ScrollingPagerIndicator;


public class DashBoardFragment extends Fragment implements OnItemClickListener {

    private static final String TAG = "PlayContestFragment";
    private List<Contest> contest_list = new ArrayList<>();
    private SpeacialContestAdapter speacialcontestadapter;
    private long mLastClickTime = 0;

    EndlessRecyclerViewScrollListener scrollListener;
    /* @BindView(R.id.viewPager)
     MetalRecyclerViewPager viewPager;*/
    private SessionUtil sessionUtil;
    private Context context;
    Handler handler = new Handler();
    Handler handlerSpecial = new Handler();
    private boolean isHandlerPost = false;
    private int totalAdSize = 0;

    public DashBoardFragment() {
        // Required empty public constructor
    }

    public static DashBoardFragment newInstance() {
        DashBoardFragment fragment = new DashBoardFragment();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        EventBus.getDefault().register(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private FragmentDashBoardBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDashBoardBinding.bind(LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_dash_board, container, false));
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TabsPagerAdapter tabsPagerAdapter = new TabsPagerAdapter(getChildFragmentManager());
        binding.dashboardViewPager.setAdapter(tabsPagerAdapter);
        binding.dashboardTabLayout.setupWithViewPager(binding.dashboardViewPager);

        binding.dashboardViewPager.addOnPageChangeListener(
                new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
                    @Override
                    public void onPageSelected(int position) {
                        ViewGroup.LayoutParams params =
                                binding.dashboardViewPager.getLayoutParams();
                        if (position == 0) {
                            // Tab 1 - chhoti height
                            params.height = getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._110sdp);
                        } else if (position == 1) {
                            // Tab 2 - badi height
                            params.height = getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._170sdp);
                        }
                        binding.dashboardViewPager.setLayoutParams(params);
                    }
                    @Override
                    public void onPageScrollStateChanged(int state) {}
                });

        sessionUtil = new SessionUtil(context);
        //   getAds();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        binding.rvSpecialContest.setLayoutManager(layoutManager);
        speacialcontestadapter = new SpeacialContestAdapter(context, contest_list);
        speacialcontestadapter.setOnItemClickListener(this);
        binding.rvSpecialContest.setAdapter(speacialcontestadapter);
        binding.indicatorSpecialContest.attachToRecyclerView(binding.rvSpecialContest.getRecyclerView());
        binding.rvSpecialContest.showProgress();

        binding.linClassicGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("TAG", "=>" + "ClassicGrid Click");
                Intent i = new Intent(getActivity(), AnyTimeGameActivity.class);
                i.putExtra("game_type", "rdb");
                getActivity().startActivity(i);
            }
        });
        binding.tvKycClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), KYCVerificationActivity.class);
                intent.putExtra(KYCVerificationActivity.IS_REDEEM_CLICK, true);
                startActivity(intent);
            }
        });
        binding.linNotiCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getActivity(), LiveHistoryTabFragment.class);
                i.putExtra("game_type", "rdb");
                getActivity().startActivity(i);

            }
        });
        binding.tvTds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, IdsActivity.class);
                startActivity(intent);

            }
        });
        binding.tvEarning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, IdsActivity.class);
                startActivity(intent);

            }
        });
        binding.linSpiningMachine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("TAG", "=>" + "SpiningMachine Click");
                Intent i = new Intent(getActivity(), AnyTimeSpinningActivity.class);
                startActivity(i);
            }
        });
        binding.linNumberGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("TAG", "=>" + "SpiningMachine Click");
                Intent i = new Intent(getActivity(), AnyTimeNumberGameActivity.class);
                i.putExtra("game_type", "0-9");
                getActivity().startActivity(i);
            }
        });
        try {
            double amount = Double.parseDouble(sessionUtil.getAmount());
            double wAmount = Double.parseDouble(sessionUtil.getWAmount());
            binding.tvTotalBalance.setText("Wallet balance " + Utils.getCurrencyFormat(String.valueOf((amount + wAmount))));
        } catch (Exception e) {
            e.printStackTrace();
        }
        AnytimeGameNotificationCount();
        getAllWaitingJTicketList();
        getJhitsTotalAmount();
        getReferralCommitionTotalAmount();
        binding.linJTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MyJTicketFragment.class);
                startActivity(intent);
            }
        });
        getAds();
    }

    private void getJhitsTotalAmount() {
        Call<ResponseBody> call = APIClient.getInstance().getJhitsTotalAmount(sessionUtil.getToken(), sessionUtil.getId());
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Log.d(TAG, "success: " + responseData);
                Gson gson = new Gson();
                JHitsTotalAmountModel jAssetsModel = gson.fromJson(responseData, JHitsTotalAmountModel.class);
                if (jAssetsModel.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    binding.tvJHitsAmount.setText(Utils.INDIAN_RUPEES + (jAssetsModel.getContent().getTotalsum()));
                }

            }

            @Override
            public void failure(String responseData) {
                Log.d(TAG, "success: " + responseData);
            }
        });
    }

    private void AnytimeGameNotificationCount() {
        Call<ResponseBody> call = APIClient.getInstance().AnytimeGameNotificationCount(sessionUtil.getToken(), sessionUtil.getId());
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Log.d(TAG, "success: " + responseData);
                Gson gson = new Gson();

                NoticountModel jAssetsModel = gson.fromJson(responseData, NoticountModel.class);
                binding.tvNotiCount.setText(jAssetsModel.getContents().getCount() + " New Results Declared");

            }

            @Override
            public void failure(String responseData) {
                Log.d(TAG, "success: " + responseData);
            }
        });
    }

    private void getAllWaitingJTicketList() {
        Call<ResponseBody> call = APIClient.getInstance().getJAssets(sessionUtil.getToken(), sessionUtil.getId());
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Log.d(TAG, "success: " + responseData);
                Gson gson = new Gson();
                JAssetsModel jAssetsModel = gson.fromJson(responseData, JAssetsModel.class);
                binding.tvAppliedCc.setText(jAssetsModel.getContents().getRedemedCC()+" Points");
                binding.tvRedemedCc.setText(jAssetsModel.getContents().getAppliedCC()+" Points");

            }

            @Override
            public void failure(String responseData) {
                Log.d(TAG, "success: " + responseData);
            }
        });
    }

    private void getReferralCommitionTotalAmount() {
        Call<ResponseBody> call = APIClient.getInstance().getReferralCommitionTotalAmount(sessionUtil.getToken(), sessionUtil.getId());
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Log.d(TAG, "success: " + responseData);
                Gson gson = new Gson();
                ReferralComissionModel jAssetsModel = gson.fromJson(responseData, ReferralComissionModel.class);
                if (jAssetsModel.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    binding.tvTds.setText(Utils.getCurrencyFormat(jAssetsModel.getContent().getEarning()));
                    binding.tvEarning.setText((jAssetsModel.getContent().getReferralCount()));
                }
            }

            @Override
            public void failure(String responseData) {
                Log.d(TAG, "success: " + responseData);
            }
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
                    fragment = PlayContestFragment.newInstance();
                    break;
                case 1:
                    fragment = MyContestFragment.newInstance();
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
                    title = "Live Games";
                    break;
                case 1:
                    title = "My Games";
                    break;
            }
            return title;
        }
    }

    private void getAds() {
        Call<ResponseBody> call = APIClient.getInstance().getAds(sessionUtil.getToken(), sessionUtil.getId());
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, true, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Gson gson = new Gson();
                Log.e("TAG", "getAds " + responseData);
                AdvertiseModel am = gson.fromJson(responseData, AdvertiseModel.class);
                if (am.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    //   frmAds.setVisibility(View.GONE);
                    DisplayMetrics metrics = getDisplayMetrics();
                    // if (am.getContent().size() > 0) {
                    binding. viewPager.setAdapter(new AdSliderAdapter(context, am.getContent()));
                    //viewPager.setAdapter(new AdSliderAdapterNew(context,metrics,am.getContent()));
                    totalAdSize = am.getContent().size();
                    if (totalAdSize > 1) {
                        isHandlerPost = handler.post(runnable);
                    }
                    // } else {
                    //     frmAds.setVisibility(View.GONE);
                    // }
                }
            }

            @Override
            public void failure(String responseData) {
                Log.e("TAG", "Error " + responseData);
            }
        });
    }

    private DisplayMetrics getDisplayMetrics() {
        DisplayMetrics metrics = new DisplayMetrics();
        if (getActivity() != null) {
            Display display = getActivity().getWindowManager().getDefaultDisplay();
            display.getMetrics(metrics);
        }

        return metrics;
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // Insert custom code here
            int currentItem =  binding.viewPager.getCurrentItem();
            currentItem++;
            if (currentItem == totalAdSize) {
                binding. viewPager.setCurrentItem(0, false);
            } else {
                binding. viewPager.setCurrentItem(currentItem, true);
            }
            // Repeat every 1 seconds
            handler.postDelayed(runnable, 5000);
        }
    };

    final int speedScroll = 3000;

    final Runnable runnableSpecial = new Runnable() {
        int count = 0;

        @Override
        public void run() {
            if (contest_list.size() > 0)
                if (count < contest_list.size()) {
                    try {
                        binding.rvSpecialContest.getRecyclerView().smoothScrollToPosition(count++);
                        handlerSpecial.postDelayed(this, speedScroll);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    count = 0;
                    binding. rvSpecialContest.getRecyclerView().smoothScrollToPosition(count);
                    handlerSpecial.postDelayed(this, speedScroll);
                }


        }
    };

    @Override
    public void onDestroyView() {

        if (isHandlerPost) {
            handler.removeCallbacks(runnable);
        }
        // handlerSpecial.removeCallbacks(runnableSpecial);
        super.onDestroyView();
    }

    private void getAllSpecialContest() {
        Call<ResponseBody> call = APIClient.getInstance().getAllSpecialContest(sessionUtil.getToken(), sessionUtil.getId());
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                binding.  rvSpecialContest.showRecycler();
                Gson gson = new Gson();
                GameContestModel gameContestModel = gson.fromJson(responseData, GameContestModel.class);
                if (gameContestModel.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    contest_list.clear();
                    contest_list.addAll(gameContestModel.getContent().getContest());
                    speacialcontestadapter.setCurrentTime(gameContestModel.getContent().getCurrentTime());
                }

                speacialcontestadapter.notifyDataSetChanged();
                if (contest_list.size() == 0) {
                    binding. rlSpecial.setVisibility(View.GONE);
                    binding.  rvSpecialContest.setVisibility(View.GONE);
                } else {
                    binding. rlSpecial.setVisibility(View.VISIBLE);
                    binding. rvSpecialContest.setVisibility(View.VISIBLE);
                    SnapHelper snapHelper = new PagerSnapHelper();
                    //  snapHelper.attachToRecyclerView(rvSpecialContest.getRecyclerView());
                    //  handlerSpecial.post(runnableSpecial);
                }
            }

            @Override
            public void failure(String responseData) {

            }
        });
    }

    @Subscribe
    public void onUpdateSpecialContestEvent(UpdateSpecialContestEvent updateSpecialContestEvent) {
        getAllSpecialContest();
    }

    @Override
    public void onItemClick(View view, int position) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        Intent intent = null;
        if (view.getId() == R.id.btnPayNow) {
            if (contest_list.get(position).getGame_type().equalsIgnoreCase("spinning-machine")) {
                intent = new Intent(context, SpiningTicketSelectionActivity.class);
                intent.putExtra(TicketSelectionActivity.CONTEST_ID, String.valueOf(contest_list.get(position).getId()));
                intent.putExtra(TicketSelectionActivity.CONTEST_NAME, contest_list.get(position).getName());
                intent.putExtra(TicketSelectionActivity.CONTEST_RTIME, contest_list.get(position).getStartDate());
                intent.putExtra(TicketSelectionActivity.CONTEST_MinRange, contest_list.get(position).getAnsRangeMin());
                intent.putExtra(TicketSelectionActivity.CONTEST_MaxRange, contest_list.get(position).getAnsRangeMax());
                startActivity(intent);

            } else {
                intent = new Intent(context, TicketSelectionActivity.class);
                intent.putExtra(TicketSelectionActivity.CONTEST_ID, String.valueOf(contest_list.get(position).getId()));
                intent.putExtra(TicketSelectionActivity.CONTEST_NAME, contest_list.get(position).getName());
                intent.putExtra(TicketSelectionActivity.CONTEST_RTIME, contest_list.get(position).getStartDate());
                intent.putExtra(TicketSelectionActivity.CONTEST_MinRange, contest_list.get(position).getAnsRangeMin());
                intent.putExtra(TicketSelectionActivity.CONTEST_MaxRange, contest_list.get(position).getAnsRangeMax());
                startActivity(intent);
            }
        }

    }

    @Override
    public void onResume() {
        getAllSpecialContest();
        binding.rvSpecialContest.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //     getAllSpecialContest();
            }
        });
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }
}
