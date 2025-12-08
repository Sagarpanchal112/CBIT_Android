package com.tfb.cbit.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.tfb.cbit.R;
import com.tfb.cbit.activities.CreateContestActivity;
import com.tfb.cbit.activities.PrivateContestDetailsActivity;
import com.tfb.cbit.adapter.HostedContestAdapter;
import com.tfb.cbit.api.APIClient;
import com.tfb.cbit.api.ApiCallback;
import com.tfb.cbit.api.NewApiCall;
import com.tfb.cbit.databinding.FragmentAboutBinding;
import com.tfb.cbit.databinding.FragmentHostedContestBinding;
import com.tfb.cbit.interfaces.OnItemClickListener;
import com.tfb.cbit.models.hostedcontest.Content;
import com.tfb.cbit.models.hostedcontest.HostedContestModel;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.Utils;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;


public class HostedContestFragment extends Fragment implements OnItemClickListener {


    private HostedContestAdapter hostedContestAdapter;
    private Context context;
    private NewApiCall newApiCall;
    private SessionUtil sessionUtil;
    private List<Content> hostedList = new ArrayList<>();
    public HostedContestFragment() {
        // Required empty public constructor
    }


    public static HostedContestFragment newInstance() {
        HostedContestFragment fragment = new HostedContestFragment();
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
    private FragmentHostedContestBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHostedContestBinding.bind(LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_hosted_contest, container, false));
        View view = binding.getRoot();
        return view;

     }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newApiCall = new NewApiCall();
        sessionUtil = new SessionUtil(context);
        LinearLayoutManager llm = new LinearLayoutManager(context);
        binding. rvHostedContestList.setLayoutManager(llm);
        hostedContestAdapter = new HostedContestAdapter(context,hostedList);
        hostedContestAdapter.setOnItemClickListener(this);
        binding. rvHostedContestList.setAdapter(hostedContestAdapter);

        binding. rvHostedContestList.showProgress();

        binding.fabCreateContest.setOnClickListener(view1 -> {
            fabCreateContestClick();
        });
    }

    @Override
    public void onResume() {
        getHostedContest();

        binding. rvHostedContestList.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getHostedContest();
            }
        });
        super.onResume();
    }

    protected void fabCreateContestClick(){
        startActivity(new Intent(context, CreateContestActivity.class));
    }


    @Override
    public void onItemClick(View view, int position) {
        if (view.getId() == R.id.ivShare) {/* Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                //sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, String.valueOf("Join contest\nContest Code : "+hostedList.get(position).getContestCode()));
                startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share)));*/
            Utils.shareJoinCode(context, hostedList.get(position).getContestCode());
        } else {
            Intent intent = new Intent(context, PrivateContestDetailsActivity.class);
            intent.putExtra(PrivateContestDetailsActivity.CONTESTID, String.valueOf(hostedList.get(position).getId()));
            intent.putExtra(PrivateContestDetailsActivity.CONTESTNAME, String.valueOf(hostedList.get(position).getName()));
            startActivity(intent);
        }
    }

    private void getHostedContest(){
        Call<ResponseBody> call = APIClient.getInstance().getPrivateContest(sessionUtil.getToken(),sessionUtil.getId());
        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                binding. rvHostedContestList.showRecycler();
                Gson gson = new Gson();
                HostedContestModel hostedContestModel = gson.fromJson(responseData,HostedContestModel.class);
                if(hostedContestModel.getStatusCode() == Utils.StandardStatusCodes.SUCCESS){
                    hostedList.clear();
                    hostedList.addAll(hostedContestModel.getContent());
                }

                hostedContestAdapter.notifyDataSetChanged();
            }

            @Override
            public void failure(String responseData) {

            }
        });
    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();
    }
}
