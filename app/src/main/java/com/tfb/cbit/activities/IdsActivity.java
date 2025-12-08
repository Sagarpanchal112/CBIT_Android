package com.tfb.cbit.activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.tfb.cbit.CBit;
import com.tfb.cbit.R;
import com.tfb.cbit.adapter.AllRequestAdapter;
import com.tfb.cbit.adapter.ReferalChartAdapter;
import com.tfb.cbit.adapter.ReferalCriteriaAdapter;
import com.tfb.cbit.api.APIClient;
import com.tfb.cbit.api.ApiCallback;
import com.tfb.cbit.api.NewApiCall;
import com.tfb.cbit.databinding.ActivityAboutUsBinding;
import com.tfb.cbit.databinding.ActivityIdsBinding;
import com.tfb.cbit.models.GroupUserModel;
import com.tfb.cbit.models.ReferalCriteria;
import com.tfb.cbit.models.ReferalCriteriaChart;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class IdsActivity extends AppCompatActivity {
    public String TAG = "IdsActivity";

    private Context context;
    private SessionUtil sessionUtil;
    AllRequestAdapter allRequestAdapter;
    private List<GroupUserModel.Content> contentList = new ArrayList<>();

    ReferalChartAdapter referalChartAdapter;
    private List<ReferalCriteriaChart.ReferralList> referralLists = new ArrayList<>();
    private List<ReferalCriteria.Content> referralLists1 = new ArrayList<>();
    public int totalReferal = 0;
    public float totalWinning = 0;
    public float totalRefCom = 0;
    public float totalTDS = 0;
    Calendar myCalendar;
    public String fromDate, toDate;
    private ActivityIdsBinding binding;


    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIdsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        sessionUtil = new SessionUtil(context);
        myCalendar = Calendar.getInstance();
        LinearLayoutManager llm = new LinearLayoutManager(context);
        binding.rvReferalChart.setLayoutManager(llm);
        referalChartAdapter = new ReferalChartAdapter(context, referralLists);
        binding.rvReferalChart.setAdapter(referalChartAdapter);
        binding.btnInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ReferralActivity.class);
                startActivity(intent);
            }
        });
        binding.imgRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getreferalCriteriachart(sortType);
            }
        });
        //  getreferalCriteriachart(sortType);
        binding.spinnerSortBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    sortType = "M";
                    fromDate = "";
                    toDate = "";
                    getreferalCriteriachart(sortType);
                } else if (position == 1) {
                    sortType = "D";
                    fromDate = "";
                    toDate = "";
                    getreferalCriteriachart(sortType);
                } else if (position == 2) {
                    sortType = "W";
                    fromDate = "";
                    toDate = "";
                    getreferalCriteriachart(sortType);
                } else if (position == 3) {
                    sortType = "M";
                    fromDate = "";
                    toDate = "";
                    getreferalCriteriachart(sortType);
                } else if (position == 4) {
                    sortType = "C";
                    fromDate = "";
                    toDate = "";
                    binding.relDate.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        DatePickerDialog.OnDateSetListener dateFrom = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                view.setMaxDate(myCalendar.getTimeInMillis());
                updateFromLabel();
            }

        };
        binding.tvFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(IdsActivity.this, dateFrom, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });
        DatePickerDialog.OnDateSetListener dateTo = new DatePickerDialog.OnDateSetListener() {

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                view.setMaxDate(myCalendar.getTimeInMillis());
                updateToLabel();
            }

        };
        binding.tvToDate.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                new DatePickerDialog(IdsActivity.this, dateTo, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });
        /*    TextView textView = (TextView) findViewById(R.id.text);
        Shader shader = new LinearGradient(0, 0, 0, textView.getTextSize(), Color.BLACK, Color.BLACK,
                Shader.TileMode.CLAMP);
        textView.getPaint().setShader(shader); */
        binding.tvCriteria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCriteriaDailog();
            }
        });

        binding.ivBack.setOnClickListener(view -> {
            onBackPressed();
        });
    }

    @SuppressLint("NewApi")
    private void updateToLabel() {
        String myFormat = "dd-MM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        binding.tvToDate.setText(sdf.format(myCalendar.getTime()));
        toDate = sdf.format(myCalendar.getTime());
        getreferalCriteriachart("C");
    }

    @SuppressLint("NewApi")
    private void updateFromLabel() {
        String myFormat = "dd-MM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        binding.tvFromDate.setText(sdf.format(myCalendar.getTime()));
        fromDate = sdf.format(myCalendar.getTime());
        getreferalCriteriachart("C");
    }

    ReferalCriteriaAdapter referalCriteriaAdapter;

    private void openCriteriaDailog() {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dailog_criteria);
        if (dialog.getWindow() != null) {
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            Window window = dialog.getWindow();
            lp.copyFrom(window.getAttributes());
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            lp.width = (int) (metrics.widthPixels * 0.90);
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);
        }
        ImageView img_close = dialog.findViewById(R.id.img_close);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        getreferalCriteria();
        RecyclerView rv_ids = dialog.findViewById(R.id.rv_ids);
        referalCriteriaAdapter =
                new ReferalCriteriaAdapter(context, referralLists1);
        rv_ids.setHasFixedSize(true);
        rv_ids.setLayoutManager(new LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL, false));
        rv_ids.setAdapter(referalCriteriaAdapter);


        dialog.show();
    }


    public String request = "";
    public String sortType = "T";

    private void getreferalCriteriachart(String sortType) {
        JSONObject jsonObject = new JSONObject();
        try {
            byte[] data;
            jsonObject.put("sortType", sortType);
            jsonObject.put("StartDate", fromDate);
            jsonObject.put("EndDate", toDate);
            request = jsonObject.toString();
            Log.i("isLoadMore Request", "==>" + request);
            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, getString(R.string.crypt_pass));
            data = request.getBytes("UTF-8");
            request = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Call<ResponseBody> call = APIClient.getInstance().referalCriteriaChart(sessionUtil.getToken(), sessionUtil.getId(), request);
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, true, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Gson gson = new Gson();
                ReferalCriteriaChart nm = gson.fromJson(responseData, ReferalCriteriaChart.class);
                if (nm.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    referralLists.clear();
                    totalRefCom = 0;
                    totalWinning = 0;
                    totalRefCom = 0;
                    totalTDS = 0;
                    totalReferal = 0;

                    referralLists.addAll(nm.getContents().getReferralList());
                    if (nm.getContents().getReferralList().size() > 0) {
                        binding.tvNoDataTitle.setVisibility(View.GONE);
                        binding.linData.setVisibility(View.VISIBLE);
                        binding.tvYourId.setText(nm.getContents().getUserCriteriaID());
                        binding.tvPlayers.setText(nm.getContents().getUserRefferalAllNetwork() + " Players");
                        for (int i = 0; i < referralLists.size(); i++) {
                            totalReferal = totalReferal + referralLists.get(i).getRefferel();
                            totalWinning = totalWinning + referralLists.get(i).getEM();
                            totalRefCom = totalRefCom + referralLists.get(i).getRefComm();
                            totalTDS = totalTDS + referralLists.get(i).getTDS();
                        }
                        binding.tvTotalRef.setText(totalReferal + "");
                        binding.tvTotalRefCom.setText(Utils.getTwoDecimalFormat(Float.parseFloat(String.valueOf(totalRefCom))));
                        binding.tvTotalTds.setText(Utils.getTwoDecimalFormat(Float.parseFloat(String.valueOf(totalTDS))));
                        try {
                            binding.tvTotalWinning.setText(numDifferentiation(totalWinning));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        referalChartAdapter.notifyDataSetChanged();
                    } else {
                        binding.tvNoDataTitle.setVisibility(View.VISIBLE);
                        binding.linData.setVisibility(View.GONE);

                    }

                }

            }

            @Override
            public void failure(String responseData) {
                binding.tvNoDataTitle.setVisibility(View.VISIBLE);
                binding.linData.setVisibility(View.GONE);
            }
        });
    }

    public String numDifferentiation(float amount) {
        String val = "";
        float mAmount = 0;
        if (amount >= 10000000) {
            val = "Cr";
            mAmount = amount / 10000000;
        } else if (amount >= 100000) {
            val = "Lac";
            mAmount = amount / 100000;
        } else if (amount >= 1000) {
            val = "K";
            mAmount = amount / 1000;
        } else {
            mAmount = amount;
        }

        String strAmount = new DecimalFormat("##.##").format(mAmount);
        strAmount += val;
        Log.i("amount", "==>" + strAmount);

        return (strAmount);
    }

    private void getreferalCriteria() {
        Call<ResponseBody> call = APIClient.getInstance().referalCriteria(sessionUtil.getToken(), sessionUtil.getId());
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, true, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Gson gson = new Gson();
                ReferalCriteria nm = gson.fromJson(responseData, ReferalCriteria.class);
                if (nm.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    referralLists1.clear();
                    referralLists1.addAll(nm.getContent());
                }
                referalCriteriaAdapter.notifyDataSetChanged();


            }

            @Override
            public void failure(String responseData) {

            }
        });
    }
}