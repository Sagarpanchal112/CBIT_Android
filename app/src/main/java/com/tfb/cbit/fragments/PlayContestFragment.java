package com.tfb.cbit.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.AlarmClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.malinskiy.superrecyclerview.OnMoreListener;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.tfb.cbit.R;
import com.tfb.cbit.activities.SpiningTicketSelectionActivity;
import com.tfb.cbit.activities.TicketSelectionActivity;
import com.tfb.cbit.adapter.GameContestAdapter;
import com.tfb.cbit.api.APIClient;
import com.tfb.cbit.api.ApiCallback;
import com.tfb.cbit.api.NewApiCall;
import com.tfb.cbit.databinding.FragmentAboutBinding;
import com.tfb.cbit.databinding.FragmentPlayContestBinding;
import com.tfb.cbit.event.UpdateUpcomingContestEvent;
import com.tfb.cbit.interfaces.OnItemClickListener;
import com.tfb.cbit.models.gamelist.Contest;
import com.tfb.cbit.models.gamelist.GameContestModel;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import ru.tinkoff.scrollingpagerindicator.ScrollingPagerIndicator;


public class PlayContestFragment extends Fragment implements OnItemClickListener, GameContestAdapter.OnLoadMoreListener {

    private static final String TAG = "PlayContestFragment";

    private NewApiCall newApiCall;
    private Context context;
    private List<Contest> contest_list = new ArrayList<>();
    private GameContestAdapter gameContestAdapter;
    SessionUtil sessionUtil;
    private long mLastClickTime = 0;

    public PlayContestFragment() {
        // Required empty public constructor
    }

    public static PlayContestFragment newInstance() {
        PlayContestFragment fragment = new PlayContestFragment();
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
    private FragmentPlayContestBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPlayContestBinding.bind(LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_play_contest, container, false));
        View view = binding.getRoot();
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newApiCall = new NewApiCall();
        sessionUtil = new SessionUtil(context);

        binding. mSetAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM)
                        .putExtra(AlarmClock.EXTRA_MESSAGE, "Cbit Alarm")
                        .putExtra(AlarmClock.EXTRA_HOUR, 18)
                        .putExtra(AlarmClock.EXTRA_MINUTES, 50);
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        LinearLayoutManager llm = new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false);
        binding. rvPlayGameList.setLayoutManager(llm);
        gameContestAdapter = new GameContestAdapter(PlayContestFragment.this,contest_list);
        gameContestAdapter.setOnItemClickListener(this);
        binding. rvPlayGameList.setAdapter(gameContestAdapter);
        binding. indicatorSpecialContest.attachToRecyclerView(binding.rvPlayGameList.getRecyclerView());
      /*  rvPlayGameList.setupMoreListener(new OnMoreListener() {
            @Override
            public void onMoreAsked(int numberOfItems, int numberBeforeMore, int currentItemPos) {
                // Fetch more from Api or DB
                startCount=numberOfItems;
                getAllContest(false,numberOfItems);
            }
        }, 10);*/
      /*   rvPlayGameList.getRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager llManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (dy > 0 && llManager.findLastCompletelyVisibleItemPosition() == (gameContestAdapter.getItemCount() - 2)) {
                    gameContestAdapter.showLoading();
                }

            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // If cannot scroll up anymore (top of the recyclerview) - FAB hides immediately

                }
                super.onScrollStateChanged(recyclerView, newState);

            }
        });
*/
      /*  rvPlayGameList.showProgress();
        getAllContest();

        rvPlayGameList.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllContest();
            }
        });*/

        binding. rvPlayGameList.showProgress();

    }

    @Override
    public void onResume() {
        getAllContest(false,0);
        binding. rvPlayGameList.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllContest(false,0);
            }
        });
        super.onResume();
    }

    public int startCount = 0;

    private void getAllContest(boolean isLoadMore,int count) {
      /*  if (!isLoadMore) {
            startCount = 0;
            gameContestAdapter = new GameContestAdapter(PlayContestFragment.this, contest_list);
            rvPlayGameList.setAdapter(gameContestAdapter);
            gameContestAdapter.setOnItemClickListener(this);

        } else {
            startCount = gameContestAdapter.getItemCount();
        }*/
        Call<ResponseBody> call = APIClient.getInstance().getAllContest(sessionUtil.getToken(), sessionUtil.getId(), count, "50");
        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                binding.  rvPlayGameList.showRecycler();
                Gson gson = new Gson();
                GameContestModel gameContestModel = gson.fromJson(responseData, GameContestModel.class);
                if (gameContestModel.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    contest_list.clear();
                    contest_list.addAll(gameContestModel.getContent().getContest());
                    Log.i("Current time","==>"+gameContestModel.getContent().getCurrentTime());
                    gameContestAdapter.setCurrentTime(gameContestModel.getContent().getCurrentTime());
                  /*  if (!isLoadMore) {
                        gameContestAdapter.addAllClass(gameContestModel.getContent().getContest());
                        gameContestAdapter.setCurrentTime(gameContestModel.getContent().getCurrentTime());
                        gameContestAdapter.notifyDataSetChanged();
                    } else {
                        gameContestAdapter.dismissLoading();
                        gameContestAdapter.addItemMore(gameContestModel.getContent().getContest());
                        gameContestAdapter.setCurrentTime(gameContestModel.getContent().getCurrentTime());
                        gameContestAdapter.setMore(true);
                    }*/
                }
                gameContestAdapter.notifyDataSetChanged();

            }

            @Override
            public void failure(String responseData) {
                try {
                    gameContestAdapter.dismissLoading();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void AnytimeGameNotificationCount(boolean isLoadMore,int count) {

        Call<ResponseBody> call = APIClient.getInstance().getAllContest(sessionUtil.getToken(), sessionUtil.getId(), count, "50");
        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                binding.  rvPlayGameList.showRecycler();
                Gson gson = new Gson();
                GameContestModel gameContestModel = gson.fromJson(responseData, GameContestModel.class);
                if (gameContestModel.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    contest_list.clear();
                    contest_list.addAll(gameContestModel.getContent().getContest());
                    Log.i("Current time","==>"+gameContestModel.getContent().getCurrentTime());
                    gameContestAdapter.setCurrentTime(gameContestModel.getContent().getCurrentTime());
                  /*  if (!isLoadMore) {
                        gameContestAdapter.addAllClass(gameContestModel.getContent().getContest());
                        gameContestAdapter.setCurrentTime(gameContestModel.getContent().getCurrentTime());
                        gameContestAdapter.notifyDataSetChanged();
                    } else {
                        gameContestAdapter.dismissLoading();
                        gameContestAdapter.addItemMore(gameContestModel.getContent().getContest());
                        gameContestAdapter.setCurrentTime(gameContestModel.getContent().getCurrentTime());
                        gameContestAdapter.setMore(true);
                    }*/
                }
                gameContestAdapter.notifyDataSetChanged();

            }

            @Override
            public void failure(String responseData) {
                try {
                    gameContestAdapter.dismissLoading();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Subscribe
    public void onUpdateUpcomingContestEvent(UpdateUpcomingContestEvent updateUpcomingContestEvent) {
        getAllContest(false,startCount);
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
    public void onDestroyView() {

        super.onDestroyView();
    }

    @Override
    public void onLoadMore() {
        getAllContest(true,0);

    }
}
