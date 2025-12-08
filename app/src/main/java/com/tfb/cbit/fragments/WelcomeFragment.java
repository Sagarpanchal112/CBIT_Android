package com.tfb.cbit.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.tfb.cbit.R;
import com.tfb.cbit.animation.MyBounceInterpolator;
import com.tfb.cbit.databinding.FragmentAboutBinding;
import com.tfb.cbit.databinding.FragmentWelcomeBinding;


public class WelcomeFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String code;


    public WelcomeFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static WelcomeFragment newInstance(String param1, String param2) {
        WelcomeFragment fragment = new WelcomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static WelcomeFragment newInstance(String code) {
        WelcomeFragment fragment = new WelcomeFragment();
        Bundle bundle = new Bundle();
        bundle.putString("code", code);
        fragment.setArguments(bundle);
        return fragment;
    }


    public static WelcomeFragment newInstance() {
        WelcomeFragment fragment = new WelcomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            code = getArguments().getString("code", "");

        }
    }

    private FragmentWelcomeBinding binding;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentWelcomeBinding.bind(LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_welcome, container, false));
        View view = binding.getRoot();

        Animation myAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.bounce);

        // Use bounce interpolator with amplitude 0.2 and frequency 20
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.5, 5);
        myAnim.setInterpolator(interpolator);

        Animation animBlinkRgister = AnimationUtils.loadAnimation(getActivity(),
                R.anim.bounce);
        animBlinkRgister.setInterpolator(interpolator);
        binding.lblRegister.startAnimation(myAnim);
        myAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.lblLogin.startAnimation(animBlinkRgister);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        binding.btnLogin.setOnClickListener(view1 -> {
            btnLoginClick();
        });
        binding.btnRagister.setOnClickListener(view1 -> {
            btnRegisterClick();
        });

        return view;
    }

    protected void btnLoginClick() {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.frameContainer, LoginWithMobileFragment.newInstance())
                //.add(R.id.frameContainer, LoginFragment.newInstance())
                .commit();

    }

    protected void btnRegisterClick() {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.frameContainer, SignUpFragment.newInstance(code, ""))
                .commit();
    }
}