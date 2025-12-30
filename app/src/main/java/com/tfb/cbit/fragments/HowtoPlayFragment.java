package com.tfb.cbit.fragments;


import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tfb.cbit.R;
import com.tfb.cbit.databinding.FragmentAboutBinding;
import com.tfb.cbit.databinding.FragmentHowtoPlayBinding;


public class HowtoPlayFragment extends Fragment {


    private String htmlFormat="<p><strong>HOW TO PLAY</strong><br />Users can join the contest by paying the entry fees mentioned at the dashboard . Numbers and colours randomly rotating freezes down at the time of the live contest . User has to add the value in the blue box , subtract the value in the red box and ignore / nullify the value in the green box . Users have to place their judgement over the calculation of the total by adding , subtracting and ignoring the values in the boxes . A user will have 30 secs to place their answer . Though the winnings are strictly based on speed . Maximum numbers of winners will be showcased before the contest goes live . For e,g , in a contest with 1000 tickets , the system has announced maximum winners as 300 . So , in this case , fastest 300 tickets with the right answer will take back the winnnings .</p>\n" +
            "<p>For e.g [green 700] [ [blue (850 ]. [ red 300 ]</p>\n" +
            "<p>= (0 x 700 ) + 850 - 300<br />= 550</p>\n" +
            "<p>So the answer is 550.</p>\n" +
            "<p>This is an example of just 3 grids . If there would be 4th grid as [ red 950 ] . Then the answer would be</p>\n" +
            "<p>(0 x 700 ) + 850 - 300 - 950<br />= 0 + 850 - 300 - 950<br />= -400</p>\n" +
            "<p><strong>Note</strong> :- the sum of the calculation can be in positive negative or 0 as well .</p>\n" +
            "<p>Contests can be either if easy mode (20 grids ) / moderate mode (32 grids ) / pro mode (40<br />grids )</p>\n" +
            "<p><strong>Note</strong> :- this info will be placed on dashboard before u join the contests.</p>\n" +
            "<p><strong>Abbreviations</strong> :-</p>\n" +
            "<p><strong>E</strong> - easy mode .<br /><strong>M</strong> - moderate mode<br /><strong>P</strong> - pro mode .<br /><strong>Flexi</strong> - flexi bar ( game mode 1 )<br /><strong>Fix</strong> - fix slots ( game mode 2 )</p>";


    private String faqFormate = "<p><span style=\"font-weight: 400;\">FAQ</span></p>\n" +
            "<p>&nbsp;</p>\n" +
            "<ul>\n" +
            "<li style=\"font-weight: 400;\"><span style=\"font-weight: 400;\">What is Kitty Games app about ?</span></li>\n" +
            "</ul>\n" +
            "<p>&nbsp;</p>\n" +
            "<p><span style=\"font-weight: 400;\">Kitty Games is a mobile gaming app based on real money winnings . It&rsquo;s a game of skill based on calculation .</span></p>\n" +
            "<p>&nbsp;</p>\n" +
            "<ul>\n" +
            "<li style=\"font-weight: 400;\"><span style=\"font-weight: 400;\">How to Play Red Win / Draw / Blue win.?</span></li>\n" +
            "</ul>\n" +
            "<p>&nbsp;</p>\n" +
            "<p><span style=\"font-weight: 400;\">Rotating grids freeze at the time of the contest. Users have  to quickly judge if red total seems more or the total of digits in blue seems more or does that look like a tie ? </span></p>\n" +
            "<p><span style=\"font-weight: 400;\">(Please refer side bar &mdash;&gt; how to play .</span></p>\n" +
            "<p><span style=\"font-weight: 400;\">Or click on &lsquo;i&rsquo; at the right top on your screen for an illustration )</span></p>\n" +
            "<p>&nbsp;</p>\n" +
            "<ul>\n" +
            "<li style=\"font-weight: 400;\"><span style=\"font-weight: 400;\">What is Credentia currency ( Points ) & J ticket  and how to use them ?</span></li>\n" +
            "</ul>\n" +
            "<p>&nbsp;</p>\n" +
            "<p><span style=\"font-weight: 400;\">Credentia Games LLp intends to provide a finance rolling platform which opts to be something way more than just gaming . We introduce A win win situation , where in you receive INR when you win in a particular contest. And when you loose, you receive Credentia currency ( Points ) . Users can use Points to redeem J tickets .</span></p>\n" +
            "<p>&nbsp;</p>\n" +
            "<p><span style=\"font-weight: 400;\">J ticket is a voucher that can be redeemed with credentia currency (under redeem j tickets ) . The redemption price of J ticket keeps updating live as per the traffic of applications of J tickets in the waiting room.  After redeeming J ticket , users can then apply for cashback . Users can check their current waiting number in waiting room & also know about their  j ticket status and cashbacks under ‘ My J tickets ‘</span></p>\n" +
            "<p>&nbsp;</p>\n" +
            "<p>&nbsp;</p>\n" +
            "<ul>\n" +
            "<li style=\"font-weight: 400;\"><span style=\"font-weight: 400;\">How to play the game ‘3 slots’ ?</span></li>\n" +
            "</ul>\n" +
            "<p>&nbsp;</p>\n" +
            "<p><span style=\"font-weight: 400;\">Rotating grids freeze at the time of the contest .Participants have to add the value in the blue box , ignore the value in the green box and subtract the value in the red box to calculate the sum . And then place their judgement by choosing your slot from the given answer Range .</span></p>\n" +
            "<p><span style=\"font-weight: 400;\">(Please refer side bar &mdash;&gt; how to play.</span></p>\n" +
            "<p><span style=\"font-weight: 400;\">Or click on &lsquo;i&rsquo; at the right top on your screen for an illustration )</span></p>\n" +
            "<p>&nbsp;</p>\n" +
            "<ul>\n" +
            "<li style=\"font-weight: 400;\"><span style=\"font-weight: 400;\">How to add money into App&rsquo;s wallet ?</span></li>\n" +
            "</ul>\n" +
            "<p>&nbsp;</p>\n" +
            "<p><span style=\"font-weight: 400;\">Click on sidebar &mdash;&gt; wallet &mdash;-&gt; add money . Users can add money via cards , net banking and various wallets like paytm , mobikwick , Phone pay , google pay , BHIM , what&rsquo;s app money , jio money , etc ...</span></p>\n" +
            "<p>&nbsp;</p>\n" +
          /*  "<ul>\n" +
            "<li style=\"font-weight: 400;\"><span style=\"font-weight: 400;\">Can I transfer Wallet balance to a friend ?</span></li>\n" +
            "</ul>\n" +
            "<p>&nbsp;</p>\n" +
            "<p><span style=\"font-weight: 400;\">Yes you can transfer wallet balance to your friends by click on side bar &mdash;&gt; wallet &mdash;&gt; transfer to wallet .</span></p>\n" +
            "<p>&nbsp;</p>\n" +


            "<ul>\n" +
            "<li style=\"font-weight: 400;\"><span style=\"font-weight: 400;\">How can I play a trial game ?</span></li>\n" +
            "</ul>\n" +
            "<p>&nbsp;</p>\n" +
            "<p><span style=\"font-weight: 400;\">You can host a contest in private room and input ticket price as ₹0 , share the link or the code with your friend and you can play the game .</span></p>\n" +
            "<p>&nbsp;</p>\n" +
            "<ul>\n" +
            "<li style=\"font-weight: 400;\"><span style=\"font-weight: 400;\">Can I play this game anytime ?</span></li>\n" +
            "</ul>\n" +
            "<p>&nbsp;</p>\n" +
            "<p><span style=\"font-weight: 400;\">User can anytime host a contest in private room and play with his friends . But in public room , there will be contest launched for a specific time .</span></p>\n" +
            "<p>&nbsp;</p>\n" +*/

            "<ul>\n" +
            "<li style=\"font-weight: 400;\"><span style=\"font-weight: 400;\">Abbreviations :</span></li>\n" +
            "</ul>\n" +
            "<p>&nbsp;</p>\n" +
            "<ul>\n" +
            "<li style=\"font-weight: 400;\"><span style=\"font-weight: 400;\">&lsquo; E &lsquo; stands for easy mode , &lsquo;m&rsquo; stands for moderate mode and &lsquo;p&rsquo; stands for pro mode</span></li>\n" +
          /*  "<li style=\"font-weight: 400;\"><span style=\"font-weight: 400;\">&lsquo;Fix&rsquo; stand for fix slots , &lsquo;Flexi&rsquo; stands for flexi bar</span></li>\n" +*/
            "<li style=\"font-weight: 400;\"><span style=\"font-weight: 400;\">&lsquo;UL&rsquo; stands for updating live</span></li>\n" +
            "<li style=\"font-weight: 400;\"><span style=\"font-weight: 400;\">N/A stands for not applicable</span></li>\n" +
            "</ul>\n" +
            "<p>&nbsp;</p>\n" +

            "<ul>\n" +
            "<li style=\"font-weight: 400;\"><span style=\"font-weight: 400;\">how can I redeem my withdrawable balance into bank account ?</span></li>\n" +
            "</ul>\n" +
            "<p>&nbsp;</p>\n" +
            "<p><span style=\"font-weight: 400;\">Click on side bar &mdash;&gt; wallet &mdash;-&gt; transfer to bank account . Input your bank details and click on redeem . You will receive a notification via email when amount is deposited into your bank account.</span></p>\n" +
            "<p>&nbsp;</p>\n" +
            "<ul>\n" +
            "<li style=\"font-weight: 400;\"><span style=\"font-weight: 400;\">Will I immediately receive money into my bank account after I redeem it from the apps wallet ?</span></li>\n" +
            "</ul>\n" +
            "<p>&nbsp;</p>\n" +
            "<p><span style=\"font-weight: 400;\">Online transactions mostly take upto 4-5 business days to process the request.</span></p>\n" +
            "<p>&nbsp;</p>\n" +
            "<ul>\n" +
            "<li style=\"font-weight: 400;\"><span style=\"font-weight: 400;\">Can I earn without participating for the contest ?</span></li>\n" +
            "</ul>\n" +
            "<p>&nbsp;</p>\n" +
            "<p><span style=\"font-weight: 400;\">Yes . You can earn simply by referring the app to your friends. Users receive INR handouts when their referral wins and also when their referral’s J tickets hit.</span></p>\n" +
            "<p>&nbsp;</p>\n" +
            "<ul>\n" +
            "<li style=\"font-weight: 400;\"><span style=\"font-weight: 400;\">Is tds deducted in any case ?</span></li>\n" +
            "</ul>\n" +
            "<p>&nbsp;</p>\n" +
            "<p><span style=\"font-weight: 400;\">31.2% Tds is deducted if a participant wins 10,000 or more in each contest . 5% TDS will be deducted on referral earnings above ₹15,000 per financial year. You will receive tds certificate via email at your registered email id .</span></p>\n" +
            "<p>&nbsp;</p>\n" +
            "<ul>\n" +
            "<li style=\"font-weight: 400;\"><span style=\"font-weight: 400;\">Will any amount be deducted on withdrawal ?</span></li>\n" +
            "</ul>\n" +
            "<p>&nbsp;</p>\n" +
            "<p><span style=\"font-weight: 400;\">No . We don’t charge any amount on withdrawal . We transfer the entire amount into your linked bank account .</span></p>\n" +
            "<p>&nbsp;</p>\n" +
            "<ul>\n" +
            "<li style=\"font-weight: 400;\"><span style=\"font-weight: 400;\">What happens if only one participant joined the contest and the registration time has expired ?</span></li>\n" +
            "</ul>\n" +
            "<p>&nbsp;</p>\n" +
            "<p><span style=\"font-weight: 400;\">In that case the contest is cancelled and entry fee is reversed to the participant. Minimum 2 participants should register for a contest to take place .</span></p>\n" +
            "<p><br /><br /><br /><span style=\"font-weight: 400;\">For further queries , email us anytime at&nbsp;</span><a href=\"mailto:info@cbitoriginal.com\"><span style=\"font-weight: 400;\">info@cbitoriginal.com</span></a></p>";
    public HowtoPlayFragment() {
        // Required empty public constructor
    }

    public static HowtoPlayFragment newInstance() {
        HowtoPlayFragment fragment = new HowtoPlayFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    private FragmentHowtoPlayBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHowtoPlayBinding.bind(LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_howto_play, container, false));
        View view = binding.getRoot();
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding. tvHowToPlay.setMovementMethod(LinkMovementMethod.getInstance());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            binding.   tvHowToPlay.setText(Html.fromHtml(faqFormate, Html.FROM_HTML_MODE_COMPACT));
        } else {
            binding.  tvHowToPlay.setText(Html.fromHtml(faqFormate));
        }
    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();
    }
}
