package com.tfb.cbit.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.tfb.cbit.R;
import com.tfb.cbit.activities.UserListActivity;
import com.tfb.cbit.adapter.ParticipantAdapter;
import com.tfb.cbit.databinding.FragmentAboutBinding;
import com.tfb.cbit.databinding.FragmentParticipantBinding;
import com.tfb.cbit.interfaces.OnItemClickListener;
import com.tfb.cbit.models.private_contest_detail.Content;
import com.tfb.cbit.models.private_contest_detail.Ticket;

import java.util.ArrayList;
import java.util.List;


public class ParticipantFragment extends Fragment implements OnItemClickListener {

     private List<Ticket> ticketList = new ArrayList<>();
    private ParticipantAdapter participantAdapter;
    private Context context;
    private Content content = null;
    public ParticipantFragment() {
        // Required empty public constructor
    }

    public static ParticipantFragment newInstance(String s) {
        ParticipantFragment fragment = new ParticipantFragment();
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
    private FragmentParticipantBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentParticipantBinding.bind(LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_participant, container, false));
        View view = binding.getRoot();
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding. rvParticipantList.setLayoutManager(new LinearLayoutManager(context));
        participantAdapter = new ParticipantAdapter(context,ticketList);
        participantAdapter.setOnItemClickListener(this);
        binding. rvParticipantList.setAdapter(participantAdapter);
        if(getArguments()!=null){
            content = new Gson().fromJson(getArguments().getString("content",""),Content.class);
            ticketList.addAll(content.getTicket());
        }
        participantAdapter.notifyDataSetChanged();


    }

    @Override
    public void onItemClick(View view, int position) {
        if (view.getId() == R.id.btnView) {
            if (content != null) {
                Intent intent = new Intent(context, UserListActivity.class);
                intent.putExtra(UserListActivity.CONTESTNAME, content.getName());
                intent.putExtra(UserListActivity.TICKET, new Gson().toJson(ticketList.get(position)));
                startActivity(intent);
            }
        }
    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();
    }


}
