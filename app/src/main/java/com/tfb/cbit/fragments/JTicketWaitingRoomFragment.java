package com.tfb.cbit.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.tfb.cbit.activities.MyJTicketsWaitingRoomActivity;
import com.tfb.cbit.adapter.WaitingAllJTicktAdapter;
import com.tfb.cbit.api.APIClient;
import com.tfb.cbit.api.ApiCallback;
import com.tfb.cbit.api.NewApiCall;
import com.tfb.cbit.databinding.FragmentAboutBinding;
import com.tfb.cbit.databinding.FragmentWaitingRoomBinding;
import com.tfb.cbit.interfaces.OnItemClickJTicket;
import com.tfb.cbit.models.RedeemJTicket.Contest;
import com.tfb.cbit.models.RedeemJTicket.RedeemJModel;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.Utils;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class JTicketWaitingRoomFragment extends Fragment implements OnItemClickJTicket {

    private static final String TAG = "RedeemJTicketFragment";

    private Context context;
   private SessionUtil sessionUtil;
    private NewApiCall newApiCall;
    private List<Contest> ReddemJTcktList = new ArrayList<>();
    private WaitingAllJTicktAdapter waitingAllJTicktAdapter;

    public JTicketWaitingRoomFragment() {
        // Required empty public constructor
    }


    public static JTicketWaitingRoomFragment newInstance() {
        JTicketWaitingRoomFragment fragment = new JTicketWaitingRoomFragment();
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

    private FragmentWaitingRoomBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentWaitingRoomBinding.bind(LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_waiting_room, container, false));
        View view = binding.getRoot();
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionUtil = new SessionUtil(context);
        newApiCall = new NewApiCall();


        LinearLayoutManager llm = new LinearLayoutManager(context);
        binding. rvPackagesList.setLayoutManager(llm);
        waitingAllJTicktAdapter = new WaitingAllJTicktAdapter(context, ReddemJTcktList);
        waitingAllJTicktAdapter.setOnItemClickListener(this);
        binding. rvPackagesList.setAdapter(waitingAllJTicktAdapter);

        ReddemJTcktList.clear();
        binding.rvPackagesList.showProgress();
        getAllWaitingJTicketList();

        binding.  rvPackagesList.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllWaitingJTicketList();
            }
        });
    }


    private void getAllWaitingJTicketList() {
        Call<ResponseBody> call = APIClient.getInstance().getAllJTicket(sessionUtil.getToken(), sessionUtil.getId());
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Log.d(TAG, "success: " + responseData);
                binding.  rvPackagesList.showRecycler();

                Gson gson = new Gson();
                RedeemJModel redeemJModel = gson.fromJson(responseData, RedeemJModel.class);
                if (redeemJModel.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    ReddemJTcktList.clear();
                    ReddemJTcktList.addAll(redeemJModel.getContent().getContest());
                }

                Log.d(TAG, "List Size: " + ReddemJTcktList.size());
                waitingAllJTicktAdapter.notifyDataSetChanged();

            }

            @Override
            public void failure(String responseData) {
                Log.d(TAG, "success: " + responseData);
            }
        });
    }


    @Override
    public void onDestroyView() {

        super.onDestroyView();
    }


    @Override
    public void onItemClick(String Price, String ID, String Type,int wait_no) {
        Intent intent = null;
        intent = new Intent(context, MyJTicketsWaitingRoomActivity.class);
        intent.putExtra("Ticket_Id", ID);
        intent.putExtra("status", "JTicket");
        startActivity(intent);

    }
}
