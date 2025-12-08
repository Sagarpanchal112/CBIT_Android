package com.tfb.cbit.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.tfb.cbit.CBit;
import com.tfb.cbit.R;
import com.tfb.cbit.activities.AddMoneyActivity;
import com.tfb.cbit.activities.AnySpinningMmachineGameViewActivity;
import com.tfb.cbit.activities.AnyTimeGameActivity;
import com.tfb.cbit.activities.AnyTimeGameHistoryActivity;
import com.tfb.cbit.activities.AnyTimeGameViewActivity;
import com.tfb.cbit.activities.GameViewActivity;
import com.tfb.cbit.adapter.AnySpinerTicketSelectionAdapter;
import com.tfb.cbit.adapter.AnySpiningOptionsAdapter;
import com.tfb.cbit.adapter.ViewFliperItemAdapter;
import com.tfb.cbit.api.APIClient;
import com.tfb.cbit.api.ApiCallback;
import com.tfb.cbit.api.NewApiCall;
import com.tfb.cbit.interfaces.OnItemClickListener;
import com.tfb.cbit.interfaces.OnItemClickStringListener;
import com.tfb.cbit.interfaces.OnItemLongClickListener;
import com.tfb.cbit.models.AnyTimeSpinningCatList;
import com.tfb.cbit.models.SendSpinnerItem;
import com.tfb.cbit.models.anytimegame.AnyTimeGameResponse;
import com.tfb.cbit.models.anytimegame.Content;
import com.tfb.cbit.models.contestdetails.ContestDetailsModel;
import com.tfb.cbit.models.contestdetails.Ticket;
import com.tfb.cbit.utility.LogHelper;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.ResponseBody;
import retrofit2.Call;


public class AnyTimeSpinningGameFragment extends Fragment implements OnItemClickListener, OnItemLongClickListener, OnItemClickStringListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static AnyTimeSpinningGameFragment fragment;

    public int pageNumber = 0;
    int questionNumber;
    Activity activity;
    SuperRecyclerView rvTicketSelection;
    RecyclerView rvOprions;
    private AnySpinerTicketSelectionAdapter ticketSelectionAdapter;
    private NewApiCall newApiCall;
    private SessionUtil sessionUtil;
    private Context context;
    private List<Ticket> ticketList = new ArrayList<>();
    public int no_of_players = 0, pending = 0, played = 0;
    private String contestId = "";
    private String gameNo = "", contestPrizeId = "";
    private List<Content> anyticketList = new ArrayList<>();
    ContestDetailsModel gtm;
    CheckBox chkall;
    ImageView img_one;
    ImageView img_two;
    ImageView img_shuffle;
    TextView tvSelected, tv_playall;
    LinearLayout linearPay, linear3Options;
    TextView tvPayTitle;
    AnyTimeSpinningCatList.Lst content;
    RecyclerView rv_I;
    RecyclerView rv_IV;
    RecyclerView rv_VII;
    RecyclerView rv_X;
    RecyclerView rv_XIII;
    public List<AnyTimeSpinningCatList.Items> selecteditems = new ArrayList<>();

    public AnyTimeSpinningGameFragment() {
        // Required empty public constructor
    }

    private static final String ARG_PAGE_NUMBER = "page_number";


    // TODO: Rename and change types and number of parameters
    public static AnyTimeSpinningGameFragment newInstance(int page) {
        fragment = new AnyTimeSpinningGameFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE_NUMBER, page);
        fragment.setArguments(args);
        fragment.setHasOptionsMenu(true);
        return fragment;
    }

    public AnyTimeSpinningGameFragment(AnyTimeSpinningCatList.Lst content, int questionNumber, Activity activity) {
        this.pageNumber = questionNumber - 1;
        this.questionNumber = questionNumber;
        this.activity = activity;
        this.content = content;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getAnyTimeDetails();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_exam, container, false);
        newApiCall = new NewApiCall();
        context = getActivity();
        sessionUtil = new SessionUtil(context);
        //  getFromSdcard();

        linear3Options = view.findViewById(R.id.linear3Options);
        rvTicketSelection = view.findViewById(R.id.rvTicketSelection);
        tv_playall = view.findViewById(R.id.tv_playall);
        rvOprions = view.findViewById(R.id.rvOprions);
        rv_I = view.findViewById(R.id.rv_I);
        rv_IV = view.findViewById(R.id.rv_IV);
        rv_VII = view.findViewById(R.id.rv_VII);
        rv_X = view.findViewById(R.id.rv_X);
        rv_XIII = view.findViewById(R.id.rv_XIII);
        chkall = view.findViewById(R.id.chkall);
        tvSelected = view.findViewById(R.id.tvSelected);
        linearPay = view.findViewById(R.id.linearPay);
        img_one = view.findViewById(R.id.img_one);
        img_two = view.findViewById(R.id.img_two);
        img_shuffle = view.findViewById(R.id.img_shuffle);
        tvPayTitle = view.findViewById(R.id.tvPayTitle);

        if (rvTicketSelection.getRecyclerView().getItemAnimator() != null)
            ((SimpleItemAnimator) rvTicketSelection.getRecyclerView().getItemAnimator()).setSupportsChangeAnimations(false);

        rvTicketSelection.setLayoutManager(new LinearLayoutManager(context));
        ticketSelectionAdapter = new AnySpinerTicketSelectionAdapter(context, anyticketList, seletedItems);
        rvTicketSelection.setAdapter(ticketSelectionAdapter);
        ticketSelectionAdapter.setOnItemClickListener(this);
        ticketSelectionAdapter.setOnItemLongClickListener(this);
        ticketSelectionAdapter.setOnItemClickStringListener(this);

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

        tv_playall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chkallClick();

      /*          StringBuilder ticketIds = new StringBuilder();
                StringBuilder contestId = new StringBuilder();
                StringBuilder gameNo = new StringBuilder();
                CBit.selectedTicketList.clear();
                for (Content ticket : anyticketList) {
                    // CBit.selectedTicketList.add(ticket);
                    ticketIds.append(ticket.getContestpriceID()).append(",");
                    contestId.append(ticket.getContestID()).append(",");
                    gameNo.append(ticket.getGame_played()).append(",");

                }
                Gson gson = new Gson();
                String lst = gson.toJson(sendImageItemArray);
                String random_lst = "";
                if (anyticketList.get(0).getSlotes().equals("wdw")) {
                    getNotRandomImageArray();
                    random_lst = gson.toJson(notsendImageItemArray);
                }

                ticketIds = ticketIds.deleteCharAt(ticketIds.length() - 1);
                contestId = contestId.deleteCharAt(contestId.length() - 1);
                gameNo = gameNo.deleteCharAt(gameNo.length() - 1);
                Intent intent = new Intent(getActivity(), AnySpinningMmachineGameViewActivity.class);
                intent.putExtra("contest_id", String.valueOf(contestId));
                intent.putExtra("gameNo", gameNo + "");
                intent.putExtra("lst", lst);
                intent.putExtra("random_lst", random_lst);
                intent.putExtra(AnyTimeGameHistoryActivity.CONTEST_PRIZE_ID, contestId + "");
                //  intent.putExtra(GameViewActivity.CONTESTTITLE, String.valueOf(toolbar_title.getText().toString()));
                intent.putExtra(GameViewActivity.CONTESTTYPE, String.valueOf(anyticketList.get(0).getGame_type()));
                intent.putExtra("Tickets", ticketIds.toString());
                intent.putExtra("Content", content);
                startActivity(intent);
                getActivity().finish();*/
            }
        });

        img_shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (anyticketList.get(0).getSlotes().equals("wdw")) {
                    setupSlot(2);

                } else {
                    setupSlot(Integer.parseInt(anyticketList.get(0).getSlotes()));

                }
            }
        });
        return view;
    }

    ArrayList<String> iamgesList;

    public void FadinAnimaiton(RecyclerView img) {
        Animation aniFade = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
        img.startAnimation(aniFade);
    }

    public void setupSlot(int i) {
        slot3By5(i);
    }

    ArrayList<String> seletedItems = new ArrayList<>();
   // ArrayList<String> notseletedItems = new ArrayList<>();

    public void slot3By5(int i) {
        seletedItems.clear();
        seletedItems = getRandomImageArray(i);
        if (i == 2) {
            linear3Options.setVisibility(View.VISIBLE);
            rvOprions.setVisibility(View.GONE);
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.loading_logo)
                    .error(R.drawable.loading_logo)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .priority(Priority.HIGH)
                    .dontAnimate()
                    .dontTransform();
            Glide.with(context).load(seletedItems.get(0)).apply(options).into(img_one);
            Glide.with(context).load(seletedItems.get(1)).apply(options).into(img_two);
        } else {
            linear3Options.setVisibility(View.GONE);
            rvOprions.setVisibility(View.VISIBLE);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            rvOprions.setLayoutManager(linearLayoutManager);
            rvOprions.setAdapter(new AnySpiningOptionsAdapter(context, seletedItems, Utils.GAME_NOT_START));
        }
        ticketSelectionAdapter.setArrayData(seletedItems);


        setUpRecyclr(rv_I, 0, 2);
        setUpRecyclr(rv_IV, 3, 5);
        setUpRecyclr(rv_VII, 6, 8);
        setUpRecyclr(rv_X, 9, 11);
        setUpRecyclr(rv_XIII, 12, 14); //your method
        Log.e("TAG", "seletedItems - " + selecteditems.size());
       /* if (i == 2) {
            for (int j = 0; j <= 14; j++) {
                int randomId = new Random().nextInt(content.getItems().size());
                String SDCardPath = getActivity().getFilesDir().getAbsolutePath() + "/" + content.getItems().get(randomId).getImage();

                for (int k = 0; k < seletedItems.size(); k++) {
                    if (!SDCardPath.equalsIgnoreCase(selecteditems.get(k).getImage())) {
                        SendSpinnerItem obj = new SendSpinnerItem();
                        obj.setId(String.valueOf(selecteditems.get(k).getId()));
                        obj.setImage(selecteditems.get(k).getImage());
                        obj.setItem(selecteditems.get(k).getName());
                        notsendImageItemArray.add(obj);
                    }
                }
            }
        }*/


        Log.e("TAG", "seletedItems - ");
        //   Log.e("TAG", "notsendImageItemArray - " + new Gson().toJson(notsendImageItemArray).toString());

    }

    public void setUpRecyclr(RecyclerView rv, int startPos, int endPos) {
        ArrayList<String> bricksItems = new ArrayList<>();
        // this is dynamic image load from local doenloaded logic
        String SDCardPath = getActivity().getFilesDir().getAbsolutePath() + "/";

        for (int i = startPos; i <= endPos; i++) {
            int randomId = new Random().nextInt(content.getItems().size());
            bricksItems.add(SDCardPath + content.getItems().get(randomId).getImage());

        }

        rv.setLayoutManager(new LinearLayoutManager(context));
        ViewFliperItemAdapter ticketAdapter = new ViewFliperItemAdapter(context, bricksItems);
        rv.setAdapter(ticketAdapter);

    }

    public void getFromSdcard() {
        iamgesList = new ArrayList<String>();// list of file paths
        File[] listFile;

        File file = new File(getActivity().getFilesDir().getAbsolutePath());

        if (file.isDirectory()) {
            listFile = file.listFiles();

            for (int i = 0; i < listFile.length; i++) {

                iamgesList.add(listFile[i].getAbsolutePath());
                LogHelper.d("Total Download ::: ", listFile[i].getAbsolutePath() + "");

            }
        }
    }

   /* protected void chkallClick() {
        int counter = 0;
        double price = 0;

        for (int i = 0; i < ticketList.size(); i++) {
                ticketList.get(i).setSelected(chkall.isChecked());


        }

        for (Ticket ticket : ticketList) {
            if (ticket.isSelected()) {
                counter++;
                price = price + ticket.getAmount();
            }
        }

        tvSelected.setText(counter+" Contest Selected");
        if (tvSelected.getVisibility() != View.VISIBLE)
            Utils.expand(tvSelected);

         if (price > (Double.parseDouble(sessionUtil.getAmount()) + Double.parseDouble(sessionUtil.getWAmount()))) {
            tvPayTitle.setText(getString(R.string.addtowallet));
        } else {
            tvPayTitle.setText("Play");
        }
        if (counter == 0) {
            if (tvSelected.getVisibility() == View.VISIBLE) {
                Utils.collapse(tvSelected);
            }
        }
        ticketSelectionAdapter.notifyDataSetChanged();

    }
*/
   protected void chkallClick() {
        int counter = 0;
        double price = 0;
        for (Content ticket : anyticketList) {
            ticket.setSelected(true);

        }
      /*  for (int i = 0; i < anyticketList.size(); i++) {
            if (anyticketList.get(i).getIsPurchased() == 0) {
                anyticketList.get(i).setSelected(chkall.isChecked());
            }

            ticketSelectionAdapter.notifyItemChanged(i);
        }
*/
        for (Content ticket : anyticketList) {
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
                //  linearPay.setVisibility(View.GONE);
            }
        }
        ticketSelectionAdapter.notifyDataSetChanged();
        //openConfirmationPopup();

    }

    protected void linearPayClick() {
        if (tvPayTitle.getText().toString().equals("Play")) {
            //Popup Open
            int counter = 0;
            for (Content ticket : anyticketList) {
                if (ticket.isSelected()) {
                    counter++;
                }
            }
            if (counter == 0) {
                Utils.showToast(context, "Please select contest");

            }else{
                openConfirmationPopup();
            }

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
    private void openConfirmationPopup() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.ticket_confirmation_popup);

        if (dialog.getWindow() != null) {
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            Window window = dialog.getWindow();
            lp.copyFrom(window.getAttributes());
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            lp.width = (int) (metrics.widthPixels * 0.90);
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);
        }

        TextView tvPrimaryAmount = dialog.findViewById(R.id.tvPrimaryAmount);
        TextView tvSecondaryAmount = dialog.findViewById(R.id.tvSecondaryAmount);
        TextView tvTotalAmount = dialog.findViewById(R.id.tvTotalAmount);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        Button btnOk = dialog.findViewById(R.id.btnOk);
        double primaryPrice = Double.parseDouble(sessionUtil.getAmount());
        double SecondaryPrice = Double.parseDouble(sessionUtil.getWAmount());
        double price = 0;
        for (Content ticket : anyticketList) {
            if (ticket.isSelected()) {
                price = price + ticket.getAmount();
            }
        }

        if (price <= primaryPrice) {
            tvPrimaryAmount.setText(Utils.getTwoDecimalFormat(Float.parseFloat(String.valueOf(price))));
            tvSecondaryAmount.setText(Utils.INDIAN_RUPEES + "0.00");
            tvTotalAmount.setText(Utils.getTwoDecimalFormat(Float.parseFloat(String.valueOf(price))));
        } else {
            double remainingPrice = price - primaryPrice;
            tvPrimaryAmount.setText(Utils.getTwoDecimalFormat(Float.parseFloat(String.valueOf(primaryPrice))));
            tvSecondaryAmount.setText(Utils.getTwoDecimalFormat(Float.parseFloat(String.valueOf(remainingPrice))));
            tvTotalAmount.setText(Utils.getTwoDecimalFormat(Float.parseFloat(String.valueOf(price))));
        }

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        double finalPrice = price;

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (finalPrice <= (Double.parseDouble(sessionUtil.getAmount()) + Double.parseDouble(sessionUtil.getWAmount()))) {
                    StringBuilder ticketIds = new StringBuilder();
                    StringBuilder contestId = new StringBuilder();
                    StringBuilder gameNo = new StringBuilder();
                    CBit.selectedTicketList.clear();
                    for (Content ticket : anyticketList) {
                        if (ticket.isSelected()) {
                            // CBit.selectedTicketList.add(ticket);
                            ticketIds.append(ticket.getContestpriceID()).append(",");
                            contestId.append(ticket.getContestID()).append(",");
                            gameNo.append(ticket.getGame_played()).append(",");
                        }
                    }
                    Gson gson = new Gson();
                    String lst = gson.toJson(sendImageItemArray);
                    String random_lst = "";

                    ticketIds = ticketIds.deleteCharAt(ticketIds.length() - 1);
                    contestId = contestId.deleteCharAt(contestId.length() - 1);
                    gameNo = gameNo.deleteCharAt(gameNo.length() - 1);
                    if (anyticketList.get(0).getSlotes().equals("wdw")) {
                        getNotRandomImageArray();
                        random_lst = gson.toJson(notsendImageItemArray);
                    }
                    Intent intent = new Intent(getActivity(), AnySpinningMmachineGameViewActivity.class);
                    intent.putExtra("contest_id", String.valueOf(contestId));
                    intent.putExtra("gameNo", gameNo + "");
                    intent.putExtra("lst", lst);
                    intent.putExtra("random_lst", random_lst);
                    intent.putExtra(AnyTimeGameHistoryActivity.CONTEST_PRIZE_ID, contestId + "");
                    //  intent.putExtra(GameViewActivity.CONTESTTITLE, String.valueOf(toolbar_title.getText().toString()));
                    intent.putExtra(GameViewActivity.CONTESTTYPE, String.valueOf(anyticketList.get(0).getGame_type()));
                    intent.putExtra("Tickets", ticketIds.toString());
                    intent.putExtra("Content", content);
                    startActivity(intent);
                    getActivity().finish();
                }else{
                    double price = 0;
                    for (Content ticket : anyticketList) {
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
        });

        dialog.show();
    }

    private void getAnyTimeDetails() {
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        String request = "";
        try {
            jsonObject.put("game_type", "spinning-machine");
            jsonObject.put("isSpinningMachine", "1");
            request = jsonObject.toString();
            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, getString(R.string.crypt_pass));
            data = request.getBytes(StandardCharsets.UTF_8);
            request = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Call<ResponseBody> call = APIClient
                .getInstance()
                .getAnyTimeGameList(sessionUtil.getToken(), sessionUtil.getId(), request);
        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                //  rvTicketSelection.showRecycler();
                Gson gson = new Gson();
                AnyTimeGameResponse anyTimeGameResponse = gson.fromJson(responseData, AnyTimeGameResponse.class);
                anyticketList.clear();
                no_of_players = anyTimeGameResponse.getContent().get(0).getNo_of_players();
                pending = anyTimeGameResponse.getContent().get(0).getPendingTickets();
                played = anyTimeGameResponse.getContent().get(0).getPlayers_played();
                anyticketList.addAll(anyTimeGameResponse.getContent());
                contestId = "";
                gameNo = "";
                contestPrizeId = "";

                for (int i = 0; i < anyticketList.size(); i++) {
                    contestId = String.valueOf(anyTimeGameResponse.getContent().get(i).getContestID());
                    gameNo = String.valueOf(anyTimeGameResponse.getContent().get(i).getGame_played());
                    contestPrizeId = String.valueOf(anyTimeGameResponse.getContent().get(i).getContestpriceID());

                }
                try {
                    if (anyTimeGameResponse.getContent().get(0).getSlotes().equals("wdw")) {
                        setupSlot(2);

                    } else {
                        setupSlot(Integer.parseInt(anyTimeGameResponse.getContent().get(0).getSlotes()));

                    }
                    FadinAnimaiton(rv_I);
                    FadinAnimaiton(rv_IV);
                    FadinAnimaiton(rv_VII);
                    FadinAnimaiton(rv_X);
                    FadinAnimaiton(rv_XIII);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(String responseData) {
            }
        });
    }


    ArrayList<SendSpinnerItem> sendImageItemArray = new ArrayList<>();
    ArrayList<SendSpinnerItem> notsendImageItemArray = new ArrayList<>();

    public ArrayList<String> getRandomImageArray(int count) {
        ArrayList<String> seletedItems = new ArrayList<>();
        ArrayList<Integer> pickedInt = new ArrayList<>();
        String SDCardPath = getActivity().getFilesDir().getAbsolutePath() + "/";
        int randomId;
        sendImageItemArray.clear();
        for (int i = 0; i < count; i++) {
            do {
                randomId = new Random().nextInt(content.getItems().size());
            } while (pickedInt.contains(randomId));
            pickedInt.add(randomId);

            seletedItems.add(SDCardPath + content.getItems().get(randomId).getImage());
            SendSpinnerItem itemSend = new SendSpinnerItem();
            itemSend.setId(String.valueOf(content.getItems().get(randomId).getId()));
            itemSend.setItem(String.valueOf(content.getItems().get(randomId).getName()));
            itemSend.setImage(String.valueOf(content.getItems().get(randomId).getImage()));
            if (i == 1) {
                SendSpinnerItem itemSends = new SendSpinnerItem();
                itemSends.setId("0");
                itemSends.setItem("Draw");
                itemSends.setImage("draw.jpg");
                sendImageItemArray.add(itemSends);
            }
            sendImageItemArray.add(itemSend);

        }
        return seletedItems;

    }

    public ArrayList<String> getNotRandomImageArray() {
        ArrayList<String> seletedItems = new ArrayList<>();
        String SDCardPath = getActivity().getFilesDir().getAbsolutePath() + "/";
        int randomId;
        notsendImageItemArray.clear();
        AnyTimeSpinningCatList.Items itemsnew = new AnyTimeSpinningCatList.Items();
        Log.i("TAG","=>notsendImageItemArray"+new Gson().toJson(content.getItems()));
        Log.i("TAG","=>notsendImageItemArray"+new Gson().toJson(sendImageItemArray));

        outerloop:
        for (AnyTimeSpinningCatList.Items items : content.getItems()) {
            for (SendSpinnerItem obj : sendImageItemArray) {
                if (Integer.parseInt(obj.getId())==items.getId()) {
                    itemsnew = new AnyTimeSpinningCatList.Items();
                    break ;
                }else{
                    Log.i("TAG","=>In IF getNotRandomImageArray");
                    itemsnew = items;
                }
            }
        }
        seletedItems.add(SDCardPath + itemsnew.getImage());
        SendSpinnerItem itemSend = new SendSpinnerItem();
        itemSend.setId(String.valueOf(itemsnew.getId()));
        itemSend.setItem(String.valueOf(itemsnew.getName()));
        itemSend.setImage(String.valueOf(itemsnew.getImage()));
        notsendImageItemArray.add(itemSend);
        Log.i("TAG","=>notsendImageItemArray"+new Gson().toJson(notsendImageItemArray));

       /* for (int i = 0; i < 1; i++) {


            do {
                randomId = new Random().nextInt(content.getItems().size());
            } while (sendImageItemID.contains(content.getItems().get(randomId).getId()));
            pickedInt.add(randomId);
            seletedItems.add(SDCardPath + content.getItems().get(randomId).getImage());
            SendSpinnerItem itemSend = new SendSpinnerItem();
            itemSend.setId(String.valueOf(content.getItems().get(randomId).getId()));
            itemSend.setItem(String.valueOf(content.getItems().get(randomId).getName()));
            itemSend.setImage(String.valueOf(content.getItems().get(randomId).getImage()));
            notsendImageItemArray.add(itemSend);
        }*/
        return seletedItems;

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
            anyticketList.get(position).setSelected(!anyticketList.get(position).isSelected());
            ticketSelectionAdapter.notifyItemChanged(position);
            for (Content ticket : anyticketList) {
                if (ticket.isSelected()) {
                    counter++;
                    price = price + ticket.getAmount();
                }
            }

            tvSelected.setText(counter + " Contest Selected ");
            if (tvSelected.getVisibility() != View.VISIBLE)
                Utils.expand(tvSelected);

            if (price > (Double.parseDouble(sessionUtil.getAmount()) + Double.parseDouble(sessionUtil.getWAmount()))) {
                tvPayTitle.setText(getString(R.string.addtowallet));
            } else {
                tvPayTitle.setText("Play");
            }
            if (counter == 0) {
                if (tvSelected.getVisibility() == View.VISIBLE) {
                    Utils.collapse(tvSelected);
                }
            }
        }
    }

    @Override
    public void onItemLongClick(View view, int position) {

    }

    @Override
    public void onItemStringClick(View view, int position) {
        if (anyticketList.get(0).getSlotes().equals("wdw")) {
            setupSlot(2);

        } else {
            setupSlot(Integer.parseInt(anyticketList.get(0).getSlotes()));

        }
    }
}