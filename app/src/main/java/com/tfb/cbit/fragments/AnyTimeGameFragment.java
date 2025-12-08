package com.tfb.cbit.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.google.gson.Gson;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.tfb.cbit.CBit;
import com.tfb.cbit.R;
import com.tfb.cbit.activities.AddMoneyActivity;
import com.tfb.cbit.activities.AnyTimeGameHistoryActivity;
import com.tfb.cbit.activities.AnyTimeGameViewActivity;
import com.tfb.cbit.activities.GameViewActivity;
import com.tfb.cbit.activities.JoinUserListActivity;
import com.tfb.cbit.adapter.AnyTicketSelectionAdapter;
import com.tfb.cbit.api.APIClient;
import com.tfb.cbit.api.ApiCallback;
import com.tfb.cbit.api.NewApiCall;
import com.tfb.cbit.interfaces.OnItemClickListener;
import com.tfb.cbit.interfaces.OnItemLongClickListener;
import com.tfb.cbit.models.AnyTimeGameContestList;
import com.tfb.cbit.models.anytimegame.AnyTimeGameResponse;
import com.tfb.cbit.models.anytimegame.Content;
import com.tfb.cbit.models.contestdetails.ContestDetailsModel;
import com.tfb.cbit.models.contestdetails.Ticket;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.Utils;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AnyTimeGameFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AnyTimeGameFragment extends Fragment implements OnItemClickListener, OnItemLongClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static AnyTimeGameFragment fragment;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public int pageNumber = 0;
    int questionNumber;
    Activity activity;
    SuperRecyclerView rvTicketSelection;
    private AnyTicketSelectionAdapter ticketSelectionAdapter;
    private NewApiCall newApiCall;
    private SessionUtil sessionUtil;
    private Context context;
    private List<Ticket> ticketList = new ArrayList<>();
    public int no_of_players = 0, pending = 0, played = 0;
    private String contestId = "";
    private String gameNo="",contestPrizeId="";
    private List<Content> anyticketList = new ArrayList<>();
    ContestDetailsModel gtm;
    CheckBox chkall;
    TextView tvSelected;
    LinearLayout linearPay;
    TextView tvPayTitle;
    AnyTimeGameContestList.Content content;

    public AnyTimeGameFragment() {
        // Required empty public constructor
    }

    private static final String ARG_PAGE_NUMBER = "page_number";

    // TODO: Rename and change types and number of parameters
    public static AnyTimeGameFragment newInstance(int page) {
        fragment = new AnyTimeGameFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE_NUMBER, page);
        fragment.setArguments(args);
        fragment.setHasOptionsMenu(true);
        return fragment;
    }

    public AnyTimeGameFragment(AnyTimeGameContestList.Content content, int questionNumber, Activity activity) {
        this.pageNumber = questionNumber - 1;
        this.questionNumber = questionNumber;
        this.activity = activity;
        this.content=content;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_exam, container, false);
        newApiCall = new NewApiCall();
        context = getActivity();
        sessionUtil = new SessionUtil(context);
        rvTicketSelection = view.findViewById(R.id.rvTicketSelection);
        chkall = view.findViewById(R.id.chkall);
        tvSelected = view.findViewById(R.id.tvSelected);
        linearPay = view.findViewById(R.id.linearPay);
        tvPayTitle = view.findViewById(R.id.tvPayTitle);

        if (rvTicketSelection.getRecyclerView().getItemAnimator() != null)
            ((SimpleItemAnimator) rvTicketSelection.getRecyclerView().getItemAnimator()).setSupportsChangeAnimations(false);
        rvTicketSelection.setLayoutManager(new LinearLayoutManager(context));
       // ticketSelectionAdapter = new AnyTicketSelectionAdapter(context, ticketList);
        rvTicketSelection.setAdapter(ticketSelectionAdapter);
        ticketSelectionAdapter.setOnItemClickListener(this);
        ticketSelectionAdapter.setOnItemLongClickListener(this);
        getAnyTimeDetails();

        chkall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chkallClick();
            }
        });
        linearPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearPayClick();
            }
        });
        return view;
    }

    protected void chkallClick() {
        int counter = 0;
        double price = 0;

        for (int i = 0; i < ticketList.size(); i++) {
            if (ticketList.get(i).getIsPurchased() == 0) {
                ticketList.get(i).setSelected(chkall.isChecked());
            }

            ticketSelectionAdapter.notifyItemChanged(i);
        }

        for (Ticket ticket : ticketList) {
            if (ticket.isSelected()) {
                counter++;
                price = price + ticket.getAmount();
            }
        }

        tvSelected.setText("Selected " + counter);
        if (tvSelected.getVisibility() != View.VISIBLE)
            Utils.expand(tvSelected);

        linearPay.setVisibility(View.VISIBLE);
        if (price > (Double.parseDouble(sessionUtil.getAmount()) + Double.parseDouble(sessionUtil.getWAmount()))) {
            tvPayTitle.setText(getString(R.string.addtowallet));
        } else {
            tvPayTitle.setText("Play");
        }
        if (counter == 0) {
            if (tvSelected.getVisibility() == View.VISIBLE) {
                Utils.collapse(tvSelected);
                linearPay.setVisibility(View.GONE);
            }
        }
    }

    protected void linearPayClick() {
        if (tvPayTitle.getText().toString().equals("Play")) {
            //Popup Open

            StringBuilder ticketIds = new StringBuilder();
            CBit.selectedTicketList.clear();
            for (Ticket ticket : ticketList) {
                if (ticket.isSelected()) {
                   // CBit.selectedTicketList.add(ticket);
                    ticketIds.append(ticket.getContestPriceId()).append(",");
                }
            }
            ticketIds = ticketIds.deleteCharAt(ticketIds.length() - 1);
            Intent intent = new Intent(getActivity(), AnyTimeGameViewActivity.class);
            intent.putExtra("contest_id", String.valueOf(contestId));
            intent.putExtra("gameNo", gameNo + "");
            intent.putExtra(AnyTimeGameHistoryActivity.CONTEST_PRIZE_ID, contestPrizeId + "");
            //  intent.putExtra(GameViewActivity.CONTESTTITLE, String.valueOf(toolbar_title.getText().toString()));
            intent.putExtra(GameViewActivity.CONTESTTYPE, String.valueOf(anyticketList.get(0).getGame_type()));
            intent.putExtra("Tickets", ticketIds.toString());
            startActivity(intent);
            getActivity().finish();
        } else {
            double price = 0;
            for (Ticket ticket : ticketList) {
                if (ticket.isSelected()) {
                    price = price + ticket.getAmount();
                }
            }
            price = price - (Double.parseDouble(sessionUtil.getAmount()) + Double.parseDouble(sessionUtil.getWAmount()));
            Intent intent = new Intent(context, AddMoneyActivity.class);
            intent.putExtra(AddMoneyActivity.AMOUNT_VALUE, String.valueOf(Math.ceil(price)));
            startActivity(intent);
        }
    }

    private void getAnyTimeDetails() {
        Call<ResponseBody> call = APIClient
                .getInstance()
                .getAnyTimeGameList(sessionUtil.getToken(), sessionUtil.getId(), "0");

        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                //  rvTicketSelection.showRecycler();
                Gson gson = new Gson();
                AnyTimeGameResponse anyTimeGameResponse = gson.fromJson(responseData, AnyTimeGameResponse.class);

                ticketList.clear();
                no_of_players = anyTimeGameResponse.getContent().get(0).getNo_of_players();
                pending = anyTimeGameResponse.getContent().get(0).getPendingTickets();
                played = anyTimeGameResponse.getContent().get(0).getPlayers_played();
                anyticketList.addAll(anyTimeGameResponse.getContent());
                for (int i = 0; i < anyticketList.size(); i++) {
                    if(content.getContestID()==anyticketList.get(i).getContestID()){
                        contestId = String.valueOf(anyTimeGameResponse.getContent().get(i).getContestID());
                        contestPrizeId = String.valueOf(anyTimeGameResponse.getContent().get(i).getContestpriceID());
                        gameNo = String.valueOf(anyTimeGameResponse.getContent().get(i).getGame_played());

                    }
                }
                getTicketDetails();


            }

            @Override
            public void failure(String responseData) {

            }
        });
    }

    private void getTicketDetails() {
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        String request = "";
        try {
            jsonObject.put("contest_id", contestId);
            jsonObject.put("GameNo", gameNo);
            jsonObject.put("contest_price_id", contestPrizeId);
            request = jsonObject.toString();
            Log.i("Send request","==>"+request);
            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, getString(R.string.crypt_pass));
            data = request.getBytes(StandardCharsets.UTF_8);
            request = Base64.encodeToString(data, Base64.DEFAULT);

        } catch (Exception e) {
            e.printStackTrace();
        }


        Call<ResponseBody> call = APIClient.getInstance().contestDetailsAnyTimeGame(sessionUtil.getToken(), sessionUtil.getId(), request);
        //.contestDetails(sessionUtil.getToken(),sessionUtil.getName(),contestId);
        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                rvTicketSelection.showRecycler();
                Gson gson = new Gson();
                gtm = gson.fromJson(responseData, ContestDetailsModel.class);

                ticketSelectionAdapter.setViewType(gtm.getContent().getType());
                ticketSelectionAdapter.setMinAns(gtm.getContent().getAnsRangeMin());
                ticketSelectionAdapter.setMaxAns(gtm.getContent().getAnsRangeMax());
                ticketSelectionAdapter.setNoPlayer(no_of_players, pending, played);
                if (gtm.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    ticketList.clear();
                    System.out.println(gtm.getContent().getTickets());
                    ticketList.addAll(gtm.getContent().getTickets());
                } else {
                    Utils.showToast(context, gtm.getMessage());
                }
                ticketSelectionAdapter.notifyDataSetChanged();
            }

            @Override
            public void failure(String responseData) {

            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        if (view.getId() == R.id.chkSelect) {
            int counter = 0;
            double price = 0;
            if (chkall.isChecked()) {
                chkall.setChecked(false);
            } else {

            }
            ticketList.get(position).setSelected(!ticketList.get(position).isSelected());
            ticketSelectionAdapter.notifyItemChanged(position);
            for (Ticket ticket : ticketList) {
                if (ticket.isSelected()) {
                    counter++;
                    price = price + ticket.getAmount();
                }
            }

            tvSelected.setText(counter + " Contest Selected ");
            if (tvSelected.getVisibility() != View.VISIBLE)
                Utils.expand(tvSelected);

            linearPay.setVisibility(View.VISIBLE);
            if (price > (Double.parseDouble(sessionUtil.getAmount()) + Double.parseDouble(sessionUtil.getWAmount()))) {
                tvPayTitle.setText(getString(R.string.addtowallet));
            } else {
                tvPayTitle.setText("Play");
            }
            if (counter == 0) {
                if (tvSelected.getVisibility() == View.VISIBLE) {
                    Utils.collapse(tvSelected);
                    linearPay.setVisibility(View.GONE);
                }
            }
        } else {
            Intent intent = new Intent(context, JoinUserListActivity.class);
            intent.putExtra(JoinUserListActivity.CONTEST_NAME, gtm.getContent().getName());
            intent.putExtra(JoinUserListActivity.CONTEST_PRICE_ID, ticketList.get(position).getContestPriceId() + "");
            intent.putExtra(JoinUserListActivity.CONTEST_GAME_NO, ticketList.get(position).getGame_no());
            startActivity(intent);
        }
    }

    @Override
    public void onItemLongClick(View view, int position) {

    }
}