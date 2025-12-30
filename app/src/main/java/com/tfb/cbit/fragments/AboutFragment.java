package com.tfb.cbit.fragments;


import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tfb.cbit.R;
import com.tfb.cbit.databinding.FragmentAboutBinding;



public class AboutFragment extends Fragment {

    private String htmlFormat="<p>Kitty Games is a product of Credentia games llp . Kitty Games ( choose bracket in time ) sees a vision , where people from all over the country , except from the states of Assam , Orissa and telengana, come together to join a contest with an entry fee . Kitty Games serves a platform , where the users can join a contest , play a live game , and get a chance to take back the winning amount . The winning amount will be divided amongst all the tickets with the right answer . User can also buy more than one ticket for a particular contest .</p>\n" +
            "<p>Kitty Games serves 2 platforms to join the contest and play the game . I.e public platform and private platform . Users can join the contests appearing on the dashboard to play along with users all over the country . User can also host a contest , in which , he or she can forward the contest code to his friends and can compete the game with whom so ever they want to .</p>\n" +
            "<p>P.S - the trend of vc in India is quite trending between group of individuals . In vc, all pay a particular amount periodically and every time there&rsquo;s someone who takes the entire collected amount .<br />Kitty Games stands a bit different here . This platform stands no compulsion over joining contests repeatedly / periodically. And also , there can be more than one winner as well for the contest .</p>";
    public AboutFragment() {
        // Required empty public constructor
    }

    public static AboutFragment newInstance() {
        AboutFragment fragment = new AboutFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    private FragmentAboutBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAboutBinding.bind(LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_about, container, false));
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            binding.  tvAbout.setText(Html.fromHtml(htmlFormat, Html.FROM_HTML_MODE_COMPACT));
        } else {
            binding.  tvAbout.setText(Html.fromHtml(htmlFormat));
        }
    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();
    }
}
