package com.tfb.cbit.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.tfb.cbit.R;
import com.tfb.cbit.activities.PrivateGameListingActivity;
import com.tfb.cbit.databinding.FragmentAboutBinding;
import com.tfb.cbit.databinding.FragmentJoinByCodeBinding;
import com.tfb.cbit.utility.MyValidator;
import com.tfb.cbit.utility.SessionUtil;



public class JoinByCodeFragment extends Fragment {


    private Context context;
    private SessionUtil sessionUtil;
    public JoinByCodeFragment() {
        // Required empty public constructor
    }

    public static JoinByCodeFragment newInstance() {
        JoinByCodeFragment fragment = new JoinByCodeFragment();
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
    private FragmentJoinByCodeBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentJoinByCodeBinding.bind(LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_join_by_code, container, false));
        View view = binding.getRoot();
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionUtil = new SessionUtil(context);

        binding.btnJoin.setOnClickListener(view1 -> {
            btnJoinClick();

        });
    }

    protected void btnJoinClick(){
        if(MyValidator.isBlankETError(context, binding.edtJoinByCode,"Enter Contest Code",1,100)){
            Intent intent =new Intent(context, PrivateGameListingActivity.class);
            intent.putExtra(PrivateGameListingActivity.JOINBYCODE, binding.edtJoinByCode.getText().toString().trim());
            startActivity(intent);
        }
    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();
    }
}
