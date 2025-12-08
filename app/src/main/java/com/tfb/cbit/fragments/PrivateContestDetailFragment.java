package com.tfb.cbit.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tfb.cbit.R;
import com.tfb.cbit.activities.ContestWinnerActivity;
import com.tfb.cbit.adapter.BricksAdapter;
import com.tfb.cbit.adapter.ParticipantAndWinnerAdapter;
import com.tfb.cbit.databinding.FragmentAboutBinding;
import com.tfb.cbit.databinding.FragmentPrivateContestDetailBinding;
import com.tfb.cbit.interfaces.OnItemClickListener;
import com.tfb.cbit.models.private_contest_detail.Content;
import com.tfb.cbit.utility.Utils;



public class PrivateContestDetailFragment extends Fragment {


    private Context context;
    private Content content = null;
    public PrivateContestDetailFragment() {
        // Required empty public constructor
    }

    public static PrivateContestDetailFragment newInstance(String s) {
        PrivateContestDetailFragment fragment = new PrivateContestDetailFragment();
        Bundle args = new Bundle();
        args.putString("content",s);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }
    private FragmentPrivateContestDetailBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPrivateContestDetailBinding.bind(LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_private_contest_detail, container, false));
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(getArguments()!=null){
            content = new Gson().fromJson(getArguments().getString("content",""),Content.class);
            binding. rvBricks.setLayoutManager(new GridLayoutManager(context,4));
            BricksAdapter bricksAdapter = new BricksAdapter(context,content.getBoxJson(),true);
            binding. rvBricks.setAdapter(bricksAdapter);

            binding. tvTotalAmount.setText(String.valueOf(Utils.INDIAN_RUPEES+content.getTotalAmount()));
            try {
                if(Double.parseDouble(content.getTotalCommision())<1){
                    binding. tvTotalCommission.setVisibility(View.GONE);
                }else{
                    binding. tvTotalCommission.setVisibility(View.VISIBLE);
                    binding. tvTotalCommission.setText(String.valueOf("Your commission "+Utils.INDIAN_RUPEES+content.getTotalCommision()));
                }
            }catch (Exception e){
                e.printStackTrace();
                binding. tvTotalCommission.setVisibility(View.GONE);
            }


            binding. rvTicketList.setLayoutManager(new LinearLayoutManager(context));
            ParticipantAndWinnerAdapter pwa = new ParticipantAndWinnerAdapter(context,content.getTicket());
            pwa.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    if (view.getId() == R.id.btnView) {
                        Intent intent = new Intent(context, ContestWinnerActivity.class);
                        intent.putExtra(ContestWinnerActivity.CONTESTPRICEID, String.valueOf(content.getTicket().get(position).getId()));
                        intent.putExtra(ContestWinnerActivity.CONTEST_NAME, content.getName());
                        intent.putExtra(ContestWinnerActivity.ISPRIVATE, true);
                        startActivity(intent);
                    }
                }
            });
            binding. rvTicketList.setAdapter(pwa);
        }
    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();
    }
}
