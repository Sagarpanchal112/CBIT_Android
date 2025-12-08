package com.tfb.cbit.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.tfb.cbit.R;
import com.tfb.cbit.activities.GameViewActivity;
import com.tfb.cbit.activities.MySpinningTicketsActivity;
import com.tfb.cbit.activities.MyTicketsActivity;
import com.tfb.cbit.activities.SpiningTicketSelectionActivity;
import com.tfb.cbit.activities.SpinningMmachineGameViewActivity;
import com.tfb.cbit.activities.TicketSelectionActivity;
import com.tfb.cbit.adapter.MyContestAdapter;
import com.tfb.cbit.api.APIClient;
import com.tfb.cbit.api.ApiCallback;
import com.tfb.cbit.api.NewApiCall;
import com.tfb.cbit.databinding.FragmentAboutBinding;
import com.tfb.cbit.databinding.FragmentMyContestBinding;
import com.tfb.cbit.event.UpdateMyContestEvent;
import com.tfb.cbit.interfaces.OnItemClickListener;
import com.tfb.cbit.models.gamelist.Contest;
import com.tfb.cbit.models.gamelist.GameContestModel;
import com.tfb.cbit.services.TimerService;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import ru.tinkoff.scrollingpagerindicator.ScrollingPagerIndicator;


public class MyContestFragment extends Fragment implements OnItemClickListener {

    private static final String TAG = "MyContestFragment";

    private NewApiCall newApiCall;
    private Context context;
    private List<Contest> contest_list = new ArrayList<>();
    private MyContestAdapter myContestAdapter;
    SessionUtil sessionUtil;
    private long mLastClickTime = 0;
    private TimerService timerService;
    private boolean serviceBound;

    public MyContestFragment() {
        // Required empty public constructor
    }

    public static MyContestFragment newInstance() {
        MyContestFragment fragment = new MyContestFragment();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    private FragmentMyContestBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMyContestBinding.bind(LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_my_contest, container, false));
        View view = binding.getRoot();
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newApiCall = new NewApiCall();
        sessionUtil = new SessionUtil(context);
        LinearLayoutManager llm = new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false);

        binding.  rvPlayGameList.setLayoutManager(llm);
        myContestAdapter = new MyContestAdapter(MyContestFragment.this, contest_list);
        myContestAdapter.setOnItemClickListener(this);
        binding. rvPlayGameList.setAdapter(myContestAdapter);
        binding. indicatorSpecialContest.attachToRecyclerView(binding.rvPlayGameList.getRecyclerView());

       /* rvPlayGameList.showProgress();
        getMyContest();

        rvPlayGameList.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMyContest();
            }
        });*/
        binding. rvPlayGameList.showProgress();
    }

    @Override
    public void onResume() {

        getMyContest();

        binding. rvPlayGameList.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMyContest();
            }
        });
        super.onResume();
    }

    private void getMyContest() {
        Call<ResponseBody> call = APIClient.getInstance().getMyContest(sessionUtil.getToken(), sessionUtil.getId());
        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                binding. rvPlayGameList.showRecycler();
                Gson gson = new Gson();
                GameContestModel gameContestModel = gson.fromJson(responseData, GameContestModel.class);
                if (gameContestModel.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    contest_list.clear();
                    contest_list.addAll(gameContestModel.getContent().getContest());
                    myContestAdapter.setCurrentTime(gameContestModel.getContent().getCurrentTime());

                }
                myContestAdapter.notifyDataSetChanged();
            }

            @Override
            public void failure(String responseData) {

            }
        });
    }

    @Subscribe
    public void onUpdateMyContestEvent(UpdateMyContestEvent updateMyContestEvent) {
        getMyContest();
    }

    @Override
    public void onItemClick(View view, int position) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        Intent intent = null;
        int id = view.getId();
        if (id == R.id.btnMyTickets) {
            if (contest_list.get(position).getGame_type().equalsIgnoreCase("spinning-machine")) {
                intent = new Intent(context, SpiningTicketSelectionActivity.class);
                intent.putExtra(MyTicketsActivity.CONTEST_ID, String.valueOf(contest_list.get(position).getId()));
                intent.putExtra(MyTicketsActivity.CONTEST_NAME, contest_list.get(position).getName());
                intent.putExtra(TicketSelectionActivity.CONTEST_RTIME, contest_list.get(position).getStartDate());
                intent.putExtra(SpiningTicketSelectionActivity.CONTEST_TYPE, "MyGames");
                Log.d(TAG, "Mycontest: " + contest_list.get(position).getStartDate());
                startActivity(intent);

            } else {
                intent = new Intent(context, TicketSelectionActivity.class);
                intent.putExtra(MyTicketsActivity.CONTEST_ID, String.valueOf(contest_list.get(position).getId()));
                intent.putExtra(MyTicketsActivity.CONTEST_NAME, contest_list.get(position).getName());
                intent.putExtra(TicketSelectionActivity.CONTEST_RTIME, contest_list.get(position).getStartDate());
                intent.putExtra(TicketSelectionActivity.CONTEST_TYPE, "MyGames");

                Log.d(TAG, "Mycontest: " + contest_list.get(position).getStartDate());
                startActivity(intent);
            }
        } else if (id == R.id.btnEnter) {
            if (contest_list.get(position).getGame_type().equalsIgnoreCase("spinning-machine")) {
                intent = new Intent(context, SpinningMmachineGameViewActivity.class);
                intent.putExtra(GameViewActivity.CONTESTID, String.valueOf(contest_list.get(position).getId()));
                intent.putExtra(GameViewActivity.CONTESTTITLE, String.valueOf(contest_list.get(position).getName()));
                intent.putExtra(GameViewActivity.CONTESTTYPE, String.valueOf(contest_list.get(position).getGame_type()));
                startActivity(intent);
            } else {
                intent = new Intent(context, GameViewActivity.class);
                intent.putExtra(GameViewActivity.CONTESTID, String.valueOf(contest_list.get(position).getId()));
                intent.putExtra(GameViewActivity.CONTESTTITLE, String.valueOf(contest_list.get(position).getName()));
                intent.putExtra(GameViewActivity.CONTESTTYPE, String.valueOf(contest_list.get(position).getGame_type()));
                startActivity(intent);
            }
        }

    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();
    }


}
