package com.tfb.cbit.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.tfb.cbit.CBit;
import com.tfb.cbit.R;
import com.tfb.cbit.adapter.AnySpinerTicketResultAdapter;
import com.tfb.cbit.adapter.ViewFliperItemAdapter;
import com.tfb.cbit.adapter.WinningOptionsAdapter;
import com.tfb.cbit.api.APIClient;
import com.tfb.cbit.api.ApiCallback;
import com.tfb.cbit.api.NewApiCall;
import com.tfb.cbit.databases.DatabaseHandler;
import com.tfb.cbit.databinding.ActivityAboutUsBinding;
import com.tfb.cbit.databinding.ActivityAnySpinerGameResultBinding;
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

public class AnySpinerGameHistoryActivity extends BaseAppCompactActivity implements OnItemClickListener {
    private static final String TAG = "GameResultActivity";
    /*
     * Game Result Screen
     * (Screen Open with GamePlay and History Item)
     * #Brineweb
     */


    private Context context;
    public static final String CONTEST_ID = "contestId";
    public static final String GAME_NO = "gameNo";
    public static final String CONTEST_PRIZE_ID = "contestPrizeId";
    public static final String IS_REMINDER = "isReminder";
    public static final String IS_BLINK = "isBlink";
    private String contestId = "", gameNo = "", contestPrizeId = "";
    private SessionUtil sessionUtil = null;
    private AnySpinerTicketResultAdapter ticketResultAdapter;
    private List<Ticket> ticketList = new ArrayList<>();
    private boolean isReminderScreen = false;
    private boolean isBlink = false;
    private String constest_type = "";

    public List<BoxJson> boxJson = new ArrayList<>();
    public List<WinningOptions> winningOptionsList = new ArrayList<>();
    String SDCardPath = Environment.getExternalStorageDirectory() + "/.cbit/";

    public String contest_price_game_list = "";

    private void blink() {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int timeToBlink = 1000;    //in milissegunds
                try {
                    Thread.sleep(timeToBlink);
                } catch (Exception e) {
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (binding.tvTextBlink.getVisibility() == View.VISIBLE) {
                            binding.tvTextBlink.setVisibility(View.INVISIBLE);
                        } else {
                            binding.tvTextBlink.setVisibility(View.VISIBLE);
                        }
                        blink();
                    }
                });
            }
        }).start();
    }

    Animation animation;
    private ActivityAnySpinerGameResultBinding binding;

    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
        binding = ActivityAnySpinerGameResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        sessionUtil = new SessionUtil(context);
        animation = AnimationUtils.loadAnimation(context, R.anim.bounce);
        animation.setRepeatCount(Animation.INFINITE);
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            finish();
            return;
        }
        isReminderScreen = bundle.getBoolean(IS_REMINDER, false);
        isBlink = bundle.getBoolean(IS_BLINK, false);
        binding.rvTickets.setLayoutManager(new LinearLayoutManager(context));
        ticketResultAdapter = new AnySpinerTicketResultAdapter(context, ticketList, constest_type);
        ticketResultAdapter.setOnItemClickListener(this);
        contest_price_game_list = bundle.getString("contest_price_game_list", "");
        ticketResultAdapter.isPlayer(isBlink);
        binding.rvTickets.setAdapter(ticketResultAdapter);
        contestId = bundle.getString(CONTEST_ID, "");
        gameNo = bundle.getString(GAME_NO, "");
        contestPrizeId = bundle.getString(CONTEST_PRIZE_ID, "");
        binding.pbProgress.setVisibility(View.VISIBLE);
        binding.linearContent.setVisibility(View.GONE);
        Log.i("isBlink", "==>" + isBlink);
        if (isBlink) {
            binding.tvTextBlink.setVisibility(View.VISIBLE);
            blink();
            // tv_text_blink.startAnimation(animation);


        } else {
            binding.tvTextBlink.setVisibility(View.GONE);

        }
        setCountDown();
        binding.ivBack.setOnClickListener(view -> {
            if (isReminderScreen) {
                Intent intent = new Intent(context, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                super.onBackPressed();
            }
        });

    }

    CountDownTimer yourCountDownTimer;

    public void setCountDown() {
        getContestDetails();
    /*  waitingPopup();
    yourCountDownTimer = new CountDownTimer(15000, 1000) {
            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                if (calculatingDialog != null && calculatingDialog.isShowing()) {
                    calculatingDialog.dismiss();
                }
            }
        }.start();*/

    }

    private Dialog calculatingDialog = null;

    private void waitingPopup() {
        if (calculatingDialog == null) {
            calculatingDialog = new Dialog(this);
            calculatingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            calculatingDialog.setCancelable(false);
            calculatingDialog.setContentView(R.layout.dialog_calculating_result);

            if (calculatingDialog.getWindow() != null) {
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                Window window = calculatingDialog.getWindow();
                lp.copyFrom(window.getAttributes());
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                DisplayMetrics metrics = getResources().getDisplayMetrics();
                lp.width = (int) (metrics.widthPixels * 0.90);
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                window.setAttributes(lp);
            }
        }

        calculatingDialog.show();
    }


    private void getContestDetails() {
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        String request = "";
        try {
            jsonObject.put("contest_id", contestId);
            jsonObject.put("GameNo", gameNo);
            jsonObject.put("contest_price_id", contestPrizeId);
            jsonObject.put("contest_price_game_list", contest_price_game_list);
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
                .contestDetailsAnyTimeGame(sessionUtil.getToken(), sessionUtil.getId(), request);
        //.contestDetails(sessionUtil.getToken(),sessionUtil.getName(),contestId);
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                LogHelper.d(TAG, responseData);
                Gson gson = new Gson();
                ContestDetailsModel gtm = gson.fromJson(responseData, ContestDetailsModel.class);
                if (gtm.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    binding.pbProgress.setVisibility(View.GONE);
                    binding.linearContent.setVisibility(View.VISIBLE);
                    constest_type = gtm.getContent().getGame_type();
                    binding.tvWinnings.setText("You win : " + Utils.INDIAN_RUPEES + gtm.getContent().getTotalWinAmount());
                    binding.tvCCWinnings.setText("You win : " + gtm.getContent().getTotalCCWinAmount()+" Points");

                    for (int i = 0; i < gtm.getContent().getWinningOptions().size(); i++) {
                        if (gtm.getContent().getAnswer().equalsIgnoreCase(String.valueOf(gtm.getContent().getWinningOptions().get(i).getId()))) {

                            // tvAns.setText("Win = " + gtm.getContent().getWinningOptions().get(i).getItem());
                        }
                    }
                    binding.tvnowin.setText("No win : " + Utils.INDIAN_RUPEES + gtm.getContent().getNowin());

                    binding.toolbarTitle.setText("What's Most?");
                    boxJson.addAll(gtm.getContent().getBoxJson());
                    winningOptionsList.addAll(gtm.getContent().getWinningOptions());
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                    linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                    binding.rvWinningOption.setLayoutManager(linearLayoutManager);
                    WinningOptionsAdapter optionsAdapter = new WinningOptionsAdapter(context, winningOptionsList, gtm.getContent().getAnswer());
                    binding.rvWinningOption.setAdapter(optionsAdapter);
                    setupSlot(gtm);
                    Log.d(TAG, "Game type: " + gtm.getContent().getGame_type());

                    ticketList.clear();
                    ticketList.addAll(gtm.getContent().getTickets());

                    if (isWin()) {
                        binding.tvAnsText.setText("You chose right answer in " + gtm.getContent().getTickets().get(0).getLockTime() + "");
                        //  tvAnsText.setText("You chose right answer in " + gtm.getContent().getTickets().get(0).getLockTime() + "");
                    } else {
                        binding.tvAnsText.setText("You chose wrong answer in " + gtm.getContent().getTickets().get(0).getLockTime() + "");
                        //  tvAnsText.setText("You chose wrong answer in " + gtm.getContent().getTickets().get(0).getLockTime() + "");
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

    public boolean isWin() {
        for (int i = 0; i < ticketList.size(); i++) {
            if ((ticketList.get(i).isWin())) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onItemClick(View view, int position) {
        if (view.getId() == R.id.tvViewWinner) {
            Intent intent = new Intent(context, AnyContestWinnerActivity.class);
            intent.putExtra("game_no", String.valueOf(ticketList.get(position).getGame_no()));
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
        for (int j = 0; j < winningOptionsList.size(); j++) {
            for (int i = startPos; i <= endPos; i++) {
                if (winningOptionsList.get(j).getObjectNo() == Integer.parseInt(boxJson.get(i).getNumber())) {
                    bricksItems.add(SDCardPath + winningOptionsList.get(j).getImage());

                }
            }
        }

        rv.setLayoutManager(new LinearLayoutManager(context));
        ViewFliperItemAdapter ticketAdapter = new ViewFliperItemAdapter(context, bricksItems);
        rv.setAdapter(ticketAdapter);

    }

}
