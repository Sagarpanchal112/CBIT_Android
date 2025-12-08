package com.tfb.cbit.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.widget.AppCompatSpinner;

import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tfb.cbit.CBit;
import com.tfb.cbit.R;
import com.tfb.cbit.api.APIClient;
import com.tfb.cbit.api.ApiCallback;
import com.tfb.cbit.api.NewApiCall;
import com.tfb.cbit.databases.DatabaseHandler;
import com.tfb.cbit.databinding.ActivityAboutUsBinding;
import com.tfb.cbit.databinding.ActivityTicketDetailsBinding;
import com.tfb.cbit.event.UnAuthorizedEvent;
import com.tfb.cbit.models.SlotDataModel;
import com.tfb.cbit.models.add_contest.AddContestModel;
import com.tfb.cbit.models.ticketjson.FixTicket;
import com.tfb.cbit.models.ticketjson.FlexiTicket;
import com.tfb.cbit.models.ticketjson.Slots;
import com.tfb.cbit.utility.MyValidator;
import com.tfb.cbit.utility.PrintLog;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class TicketDetailsActivity extends BaseAppCompactActivity {


    private Context context;
    Bundle bundle = null;
    private SessionUtil sessionUtil;
    private SlotDataModel slotDataModel;
    private ActivityTicketDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTicketDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        sessionUtil = new SessionUtil(context);
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        bundle = getIntent().getExtras();
        if(bundle==null){
            finish();
            return;
        }
        binding. scrollView.setVisibility(View.INVISIBLE);
        binding. btnSave.setVisibility(View.INVISIBLE);
        binding. pbProgress.setVisibility(View.VISIBLE);

        int ansType = 0;
        if( bundle.getString("rangeMin","").equals("-100")){
            ansType =0;
        }else if( bundle.getString("rangeMin","").equals("-10")){
            ansType =1;
        }else if( bundle.getString("rangeMin","").equals("0")){
            ansType =2;
        }

        if(bundle.getInt("type")== Utils.FLEXIBAR){
            for(int i=0;i<bundle.getInt("nooftickets");i++){
                View v = getLayoutInflater().inflate(R.layout.layout_flexibar, binding.linearContent, false);
                binding.  linearContent.addView(v,i);
                LinearLayout linearBracketSize = v.findViewById(R.id.linearBracketSize);


                final AppCompatSpinner spinnerBracketSize = v.findViewById(R.id.spinnerBracketSize);
                String[] braketValues;
                if(ansType == 0){
                    braketValues = getResources().getStringArray(R.array.bracketsize_type0);
                }else if(ansType == 1){
                    braketValues = getResources().getStringArray(R.array.bracketsize_type1);
                }else {
                    braketValues = getResources().getStringArray(R.array.bracketsize_type2);
                }
               ArrayAdapter sa = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item,
                       braketValues);
                sa.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);

                spinnerBracketSize.setAdapter(sa);
                linearBracketSize.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        spinnerBracketSize.performClick();
                    }
                });


            }
        }else{
            slotDataModel = new SlotDataModel(ansType);
            for(int i=0;i<bundle.getInt("nooftickets");i++){
                View v = getLayoutInflater().inflate(R.layout.layout_fixbar,binding. linearContent, false);
                binding. linearContent.addView(v,i);
                LinearLayout linearSlot = v.findViewById(R.id.linearSlot);
                final LinearLayout linearSlotContent = v.findViewById(R.id.linearSlotContent);
                final Spinner spinnerSlot = v.findViewById(R.id.spinnerSlot);
                linearSlot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        spinnerSlot.performClick();
                    }
                });

              spinnerSlot.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                  @Override
                  public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    int count = 0;
                    count = Integer.parseInt(String.valueOf(spinnerSlot.getSelectedItem()));
                    List<String> slots = null;
                    switch (count){
                        case 2:
                            slots = slotDataModel.get2Slot();
                            break;
                        case 3:
                            slots = slotDataModel.get3Slot();
                            break;
                        case 4:
                            slots = slotDataModel.get4Slot();
                            break;
                        case 5:
                            slots = slotDataModel.get5Slot();
                            break;
                        case 6:
                            slots = slotDataModel.get6Slot();
                            break;
                        case 7:
                            slots = slotDataModel.get7Slot();
                            break;
                        case 8:
                            slots = slotDataModel.get8Slot();
                            break;
                        case 9:
                            slots = slotDataModel.get9Slot();
                            break;
                        case 10:
                            slots = slotDataModel.get10Slot();
                            break;
                    }
                    linearSlotContent.removeAllViews();
                    for(int j=0;j<count;j++){
                        View v = getLayoutInflater().inflate(R.layout.layout_slot, linearSlotContent, false);
                        linearSlotContent.addView(v);
                        TextView tvStartValue = v.findViewById(R.id.tvStartValue);
                        TextView tvEndValue = v.findViewById(R.id.tvEndValue);
                        if(slots!=null) {
                            tvStartValue.setText(slots.get(j).split("to")[0].trim());
                            tvEndValue.setText(slots.get(j).split("to")[1].trim());
                        }
                    }
                  }

                  @Override
                  public void onNothingSelected(AdapterView<?> adapterView) {

                  }
              });
            }
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding. scrollView.setVisibility(View.VISIBLE);
                        binding. btnSave.setVisibility(View.VISIBLE);
                        binding. pbProgress.setVisibility(View.GONE);
                    }
                });
            }
        },1000);

        binding.ivBack.setOnClickListener(view -> {
            onBackPressed();
        });

        binding.btnSave.setOnClickListener(view -> {
            btnSaveClick();
        });

    }


   protected void btnSaveClick(){
        if(isValidForm()){
            if(bundle!=null){
                int count =binding. linearContent.getChildCount();
                String ticketJson = "";
                List<FlexiTicket> flexiTickets = new ArrayList<>();
                List<FixTicket> fixTickets = new ArrayList<>();
                for(int i=0;i<count;i++){
                    View view =binding. linearContent.getChildAt(i);
                    EditText edtTicketPrice = view.findViewById(R.id.edtTicketPrice);
                    if(bundle.getInt("type")== Utils.FLEXIBAR) {
                        Spinner spinnerBracketSize = view.findViewById(R.id.spinnerBracketSize);
                        flexiTickets.add(new FlexiTicket(edtTicketPrice.getText().toString().trim(), String.valueOf(spinnerBracketSize.getSelectedItem())));
                    }else{
                        LinearLayout linearSlotContent = view.findViewById(R.id.linearSlotContent);
                        List<Slots> slots = new ArrayList<>();
                        for(int j=0;j<linearSlotContent.getChildCount();j++){
                            TextView tvStartValue = linearSlotContent.getChildAt(j).findViewById(R.id.tvStartValue);
                            TextView tvEndValue = linearSlotContent.getChildAt(j).findViewById(R.id.tvEndValue);

                            if(Integer.parseInt(tvStartValue.getText().toString())==
                            Integer.parseInt(tvEndValue.getText().toString())){
                                slots.add(new Slots(tvStartValue.getText().toString(),
                                        tvEndValue.getText().toString(),
                                      tvStartValue.getText().toString()));
                            }else {
                                slots.add(new Slots(tvStartValue.getText().toString(),
                                        tvEndValue.getText().toString(),
                                        String.valueOf(tvStartValue.getText().toString() + " to " + tvEndValue.getText().toString())));
                            }
                        }
                        fixTickets.add(new FixTicket(edtTicketPrice.getText().toString().trim(),slots));
                    }
                }
                Gson gson = new Gson();
                if(bundle.getInt("type")== Utils.FLEXIBAR) {
                    ticketJson = gson.toJson(flexiTickets);
                }else{
                    ticketJson = gson.toJson(fixTickets);
                }
                PrintLog.i("TAG",ticketJson);
                JSONObject jsonObject = new JSONObject();
                byte[] data;
                String request="";
                try {
                    jsonObject.put("level", bundle.getInt("level"));
                    jsonObject.put("type",bundle.getInt("type"));
                    jsonObject.put("name",bundle.getString("name"));
                    jsonObject.put("startDate",bundle.getString("startDate"));
                    jsonObject.put("startTime",Utils.get24HoursTime(bundle.getString("startTime")));
                    jsonObject.put("ticketJson",ticketJson);
                    jsonObject.put("maxWinner",bundle.getString("maxWinner"));
                    jsonObject.put("isNotify",bundle.getBoolean("isNotify",false)?"true":"false");
                   // jsonObject.put("isNotify",bundle.getBoolean("isNotify",false));
                    jsonObject.put("rangeMin", bundle.getString("rangeMin",""));
                    jsonObject.put("rangeMax", bundle.getString("rangeMax",""));
                    request = jsonObject.toString();
                    request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request,getString(R.string.crypt_pass));
                    data = request.getBytes("UTF-8");
                    request = Base64.encodeToString(data,Base64.DEFAULT);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Call<ResponseBody> call = APIClient.getInstance()
                        .addPrivateContest(sessionUtil.getToken(),sessionUtil.getId(),request);

                NewApiCall newApiCall = new NewApiCall();
                newApiCall.makeApiCall(context, true, call, new ApiCallback() {
                    @Override
                    public void success(String responseData) {
                        Gson gson = new Gson();
                        final AddContestModel addContestModel = gson.fromJson(responseData,AddContestModel.class);
                        if(addContestModel.getStatusCode() == Utils.StandardStatusCodes.SUCCESS){
                            Intent intent = new Intent(context,ShareContestCodeActivity.class);
                            intent.putExtra(ShareContestCodeActivity.CONTEST_CODE,addContestModel.getContent().getContestId());
                            startActivity(intent);
                            setResult(RESULT_OK);
                            finish();
                           /* final CustomDialog customDialog = new CustomDialog();
                            customDialog.showDialogTwoButton(context, getString(R.string.app_name), "Contest Code : " + addContestModel.getContent().getContestId(),
                                    "Share", "Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                                            sharingIntent.setType("text/plain");
                                            //sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                                            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, String.valueOf("Join contest\nContest Code : "+addContestModel.getContent().getContestId()));
                                            startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share)));

                                            dialogInterface.dismiss();
                                            setResult(RESULT_OK);
                                            finish();
                                        }
                                    }, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                            setResult(RESULT_OK);
                                            finish();
                                        }
                                    });*/
                        }else{
                            Utils.showToast(context,addContestModel.getMessage());
                        }
                    }

                    @Override
                    public void failure(String responseData) {
                    }
                });


            }

        }
    }

    private boolean isValidForm(){
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        int count = binding.linearContent.getChildCount();
        for(int i=0;i<count;i++){
            View view =binding. linearContent.getChildAt(i);
            EditText edtTicketPrice = view.findViewById(R.id.edtTicketPrice);
            if(!MyValidator.isBlankETError(context,edtTicketPrice,"Enter Ticket Price",1,100)){
                return false;
            }/*else if(Integer.parseInt(edtTicketPrice.getText().toString().trim())<1){
                edtTicketPrice.setError("Minimum 1 rupee of Ticket Price");
                edtTicketPrice.requestFocus();
                return false;
            }*/

            Double d = Double.parseDouble(edtTicketPrice.getText().toString().trim());

            if(d % 1 != 0){
                edtTicketPrice.setText(decimalFormat.format(Math.floor(d * 100) / 100));
            }
        }

        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();
        if (v != null &&
                (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
            float y = ev.getRawY() + v.getTop() - scrcoords[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom())
                Utils.hideKeyboard(this);
        }
        return super.dispatchTouchEvent(ev);
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
        if(EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);

        super.onDestroy();
    }
}
