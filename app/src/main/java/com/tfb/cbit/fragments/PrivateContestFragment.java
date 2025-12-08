package com.tfb.cbit.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.tfb.cbit.CBit;
import com.tfb.cbit.R;
import com.tfb.cbit.activities.TicketSelectionActivity;
import com.tfb.cbit.adapter.GameContestAdapter;
import com.tfb.cbit.api.APIClient;
import com.tfb.cbit.api.ApiCallback;
import com.tfb.cbit.api.NewApiCall;
import com.tfb.cbit.databinding.FragmentAboutBinding;
import com.tfb.cbit.databinding.FragmentPlayContestBinding;
import com.tfb.cbit.interfaces.OnItemClickListener;
import com.tfb.cbit.models.gamelist.Contest;
import com.tfb.cbit.models.gamelist.GameContestModel;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;


public class PrivateContestFragment extends Fragment implements OnItemClickListener {

    private Context context;
    private List<Contest> contest_list=new ArrayList<>();
    private GameContestAdapter gameContestAdapter;
    SessionUtil sessionUtil;
    private long mLastClickTime = 0;
    String code = "";
    public PrivateContestFragment() {
        // Required empty public constructor
    }

    public static PrivateContestFragment newInstance(String code) {
        PrivateContestFragment fragment = new PrivateContestFragment();
        Bundle bundle = new Bundle();
        bundle.putString("code",code);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            code = getArguments().getString("code","");
        }
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
        sessionUtil = new SessionUtil(context);
        LinearLayoutManager llm = new LinearLayoutManager(context);
        binding. rvPlayGameList.setLayoutManager(llm);
        gameContestAdapter = new GameContestAdapter(PrivateContestFragment.this,contest_list);
        gameContestAdapter.setOnItemClickListener(this);
        binding. rvPlayGameList.setAdapter(gameContestAdapter);


        binding. rvPlayGameList.showProgress();
        getJoinCode();

    }

    private void getJoinCode(){
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        String request="";
        try {
            jsonObject.put("contestCode",code);
            request = jsonObject.toString();
            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request,getString(R.string.crypt_pass));
            data = request.getBytes("UTF-8");
            request = Base64.encodeToString(data,Base64.DEFAULT);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Call<ResponseBody> call = APIClient.getInstance()
                .getByCode(sessionUtil.getToken(),sessionUtil.getId(),request);
                //.getByCode(sessionUtil.getToken(),sessionUtil.getName(),code);

        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, true, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                binding. rvPlayGameList.showRecycler();
                Gson gson = new Gson();
                GameContestModel gameContestModel = gson.fromJson(responseData,GameContestModel.class);
                if(gameContestModel.getStatusCode() == Utils.StandardStatusCodes.SUCCESS){
                    contest_list.clear();
                    contest_list.addAll(gameContestModel.getContent().getContest());
                    gameContestAdapter.setCurrentTime(gameContestModel.getContent().getCurrentTime());
                }

                if(!code.isEmpty()){
                    TextView tvMsg = binding. rvPlayGameList.getEmptyView().findViewById(R.id.tvMsg);
                    tvMsg.setText("This contest has expired.");
                }
                gameContestAdapter.notifyDataSetChanged();
            }

            @Override
            public void failure(String responseData) {

            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        Intent intent = null;
        if (view.getId() == R.id.btnPayNow) {
            intent = new Intent(context, TicketSelectionActivity.class);
            intent.putExtra(TicketSelectionActivity.CONTEST_ID, String.valueOf(contest_list.get(position).getId()));
            intent.putExtra(TicketSelectionActivity.CONTEST_NAME, contest_list.get(position).getName());
            intent.putExtra(TicketSelectionActivity.CONTEST_RTIME, contest_list.get(position).getStartDate());
            intent.putExtra(TicketSelectionActivity.CONTEST_MinRange, contest_list.get(position).getAnsRangeMin());
            intent.putExtra(TicketSelectionActivity.CONTEST_MaxRange, contest_list.get(position).getAnsRangeMax());
            startActivity(intent);
        }
    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();
    }


}
