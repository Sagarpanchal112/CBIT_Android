package com.tfb.cbit.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tfb.cbit.CBit;
import com.tfb.cbit.R;
import com.tfb.cbit.adapter.SpinerTicketResultAdapter;
import com.tfb.cbit.adapter.ViewFliperItemAdapter;
import com.tfb.cbit.adapter.WinningOptionsAdapter;
import com.tfb.cbit.api.APIClient;
import com.tfb.cbit.api.ApiCallback;
import com.tfb.cbit.api.NewApiCall;
import com.tfb.cbit.databases.DatabaseHandler;
import com.tfb.cbit.databinding.ActivityAboutUsBinding;
import com.tfb.cbit.databinding.ActivityHistorySpinningResultBinding;
import com.tfb.cbit.event.UnAuthorizedEvent;
import com.tfb.cbit.interfaces.OnItemClickListener;
import com.tfb.cbit.models.contestdetails.BoxJson;
import com.tfb.cbit.models.contestdetails.ContestDetailsModel;
import com.tfb.cbit.models.contestdetails.Ticket;
import com.tfb.cbit.models.contestdetails.WinningOptions;
import com.tfb.cbit.utility.LogHelper;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class HistorySpinningResultActivity extends BaseAppCompactActivity implements OnItemClickListener {
    private static final String TAG = "GameResultActivity";

    private Context context;
    public static final String CONTEST_ID = "contestId";
    public static final String IS_REMINDER = "isReminder";
    private String contestId = "";
    private SessionUtil sessionUtil = null;
    private SpinerTicketResultAdapter ticketResultAdapter;
    private List<Ticket> ticketList = new ArrayList<>();
    private boolean isReminderScreen = false;
    private String constest_type = "";
    //   protected PowerManager.WakeLock mWakeLock;

    public List<BoxJson> boxJson = new ArrayList<>();
    public List<WinningOptions> winningOptionsList = new ArrayList<>();
    private ActivityHistorySpinningResultBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHistorySpinningResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        sessionUtil = new SessionUtil(context);
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            finish();
            return;
        }
        isReminderScreen = bundle.getBoolean(IS_REMINDER, false);
        binding. rvTickets.setLayoutManager(new LinearLayoutManager(context));
        ticketResultAdapter = new SpinerTicketResultAdapter(context, ticketList, constest_type);
        ticketResultAdapter.setOnItemClickListener(this);
        binding. rvTickets.setAdapter(ticketResultAdapter);
        contestId = bundle.getString(CONTEST_ID, "");
        binding.pbProgress.setVisibility(View.VISIBLE);
        binding. linearContent.setVisibility(View.GONE);

        getContestDetails();
        binding.ivBack.setOnClickListener(view -> {
            onBackPressed();
        });
    }



    private void getContestDetails() {
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        String request = "";
        try {
            jsonObject.put("contest_id", contestId);
            request = jsonObject.toString();
            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, getString(R.string.crypt_pass));
            data = request.getBytes(StandardCharsets.UTF_8);
            request = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Call<ResponseBody> call = APIClient.getInstance()
                .contestDetails(sessionUtil.getToken(), sessionUtil.getId(), request);
        //.contestDetails(sessionUtil.getToken(),sessionUtil.getName(),contestId);
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                LogHelper.d(TAG, responseData);
                Gson gson = new Gson();
                ContestDetailsModel gtm = gson.fromJson(responseData, ContestDetailsModel.class);
                if (gtm.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    binding.  pbProgress.setVisibility(View.GONE);
                    binding.  linearContent.setVisibility(View.VISIBLE);
                    constest_type = gtm.getContent().getGame_type();
                    binding. tvWinnings.setText("Win Amount : " + Utils.INDIAN_RUPEES + gtm.getContent().getTotalWinAmount());
                    binding.  tvCCWinnings.setText("Reclamation : " + gtm.getContent().getTotalCCWinAmount()+" Points");
                    binding. gameNote.setText(gtm.getContent().getTitle());

                    for(int i=0;i<gtm.getContent().getWinningOptions().size();i++){
                        if(gtm.getContent().getAnswer().equalsIgnoreCase(String.valueOf(gtm.getContent().getWinningOptions().get(i).getId()))){
                            binding. tvAns.setText("Win = " + gtm.getContent().getWinningOptions().get(i).getItem());
                        }
                    }

                    binding. tvnowin.setText("Refund : " + Utils.INDIAN_RUPEES + gtm.getContent().getNowin());
                    binding. toolbarTitle.setText(gtm.getContent().getName());
                    boxJson.addAll(gtm.getContent().getBoxJson());
                    winningOptionsList.addAll(gtm.getContent().getWinningOptions());
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                    linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                    binding. rvWinningOption.setLayoutManager(linearLayoutManager);
                    WinningOptionsAdapter optionsAdapter = new WinningOptionsAdapter(context,winningOptionsList,gtm.getContent().getAnswer());
                    binding. rvWinningOption.setAdapter(optionsAdapter);
                    setupSlot(gtm);
                    Log.d(TAG, "Game type: " + gtm.getContent().getGame_type());

                    for (Ticket ticket : gtm.getContent().getTickets()) {
                        if (ticket.getIsPurchased() != 0) {
                            ticketList.add(ticket);
                        }
                    }
                    ticketResultAdapter.setGameType(constest_type);
                    ticketResultAdapter.notifyDataSetChanged();
                } else {
                    Utils.showToast(context, gtm.getMessage());
                }
            }

            @Override
            public void failure(String responseData) {

            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        if (view.getId() == R.id.tvViewWinner) {
            Intent intent = new Intent(context, ContestWinnerActivity.class);
            intent.putExtra(ContestWinnerActivity.CONTESTPRICEID, String.valueOf(ticketList.get(position).getContestPriceId()));
            intent.putExtra(ContestWinnerActivity.CONTEST_NAME, binding.toolbarTitle.getText().toString());
            intent.putExtra(ContestWinnerActivity.ISPRIVATE, false);
            startActivity(intent);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUnAuthorizedEvent(UnAuthorizedEvent unAuthorizedEvent) {
        Utils.showToast(this, unAuthorizedEvent.getMessage());
        DatabaseHandler databaseHandler = new DatabaseHandler(this);
        databaseHandler.deleteTable();
        String fcmToken = sessionUtil.getFcmtoken();
        sessionUtil.logOut();
        sessionUtil.setFCMToken(fcmToken);
        Intent intent = new Intent(this, LoginSignUpActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        //  this.mWakeLock.release();

        super.onDestroy();
    }

    public void setupSlot(ContestDetailsModel gtm) {
        if (gtm.getContent().getRows() == 3)
            slot3By5();
        if (gtm.getContent().getRows() == 4)
            slot4By5();
        if (gtm.getContent().getRows() == 5)
            slot5By5();
    }

    public void slot3By5() {
        setUpRecyclr(binding.rvI, 0, 2);
        setUpRecyclr(binding.rvIV, 3, 5);
        setUpRecyclr(binding.rvVII, 6, 8);
        setUpRecyclr(binding.rvX, 9, 11);
        setUpRecyclr(binding.rvXIII, 12, 14);

    }

    public void slot4By5() {
        setUpRecyclr(binding.rvI, 0, 3);
        setUpRecyclr(binding.rvIV, 4, 7);
        setUpRecyclr(binding.rvVII, 8, 11);
        setUpRecyclr(binding.rvX, 12, 15);
        setUpRecyclr(binding.rvXIII, 16, 19);

    }

    public void slot5By5() {
        setUpRecyclr(binding.rvI, 0, 4);
        setUpRecyclr(binding.rvIV, 5, 9);
        setUpRecyclr(binding.rvVII, 10, 14);
        setUpRecyclr(binding.rvX, 15, 19);
        setUpRecyclr(binding.rvXIII, 20, 24);

    }

    public void setUpRecyclr(RecyclerView rv, int startPos, int endPos) {
        ArrayList<String> bricksItems = new ArrayList<>();
        // this is dynamic image load from local doenloaded logic
        String SDCardPath = getFilesDir().getAbsolutePath() + "/";
       /* for (int j = 0; j < winningOptionsList.size(); j++) {
            for (int i = startPos; i <= endPos; i++) {
                if (winningOptionsList.get(j).getObjectNo() == Integer.parseInt(boxJson.get(i).getNumber())) {
                    bricksItems.add(SDCardPath + winningOptionsList.get(j).getImage());
                }
            }
        }*/
        for (int i = startPos; i <= endPos; i++) {
            bricksItems.add(SDCardPath + boxJson.get(i).getImage());
        }
        rv.setLayoutManager(new LinearLayoutManager(context));
        ViewFliperItemAdapter ticketAdapter = new ViewFliperItemAdapter(context, bricksItems);
        rv.setAdapter(ticketAdapter);

    }

}