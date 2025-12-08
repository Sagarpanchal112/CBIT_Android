package com.tfb.cbit.fragments;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.tfb.cbit.CBit;
import com.tfb.cbit.R;
import com.tfb.cbit.activities.MyJTicketsWaitingRoomActivity;
import com.tfb.cbit.adapter.RedeemJTicktAdapter;
import com.tfb.cbit.api.APIClient;
import com.tfb.cbit.api.ApiCallback;
import com.tfb.cbit.api.NewApiCall;
import com.tfb.cbit.databinding.FragmentAboutBinding;
import com.tfb.cbit.databinding.FragmentRedeemjtcktBinding;
import com.tfb.cbit.interfaces.OnItemClickJTicket;
import com.tfb.cbit.models.AddReddem.AddRedeemModel;
import com.tfb.cbit.models.RedeemJTicket.Contest;
import com.tfb.cbit.models.RedeemJTicket.RedeemJModel;
import com.tfb.cbit.utility.CustomDialog;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class RedeemJTicketFragment extends Fragment implements OnItemClickJTicket {

    private static final String TAG = "RedeemJTicketFragment";

    private Context context;
    private SessionUtil sessionUtil;
    private NewApiCall newApiCall;
    private List<Contest> ReddemJTcktList = new ArrayList<>();
    private RedeemJTicktAdapter redeemJTicktAdapter;
    Float price;
    Float qty;
    Float totalCC;
    Float avilCC;
    private ArrayAdapter<String> QTYAdapter;
    private List<String> QTYList = new ArrayList<>();
    RedeemJModel redeemJModel;

    public RedeemJTicketFragment() {
        // Required empty public constructor
    }


    public static RedeemJTicketFragment newInstance() {
        RedeemJTicketFragment fragment = new RedeemJTicketFragment();
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
    private FragmentRedeemjtcktBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRedeemjtcktBinding.bind(LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_redeemjtckt, container, false));
        View view = binding.getRoot();
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionUtil = new SessionUtil(context);
        newApiCall = new NewApiCall();


        binding. tvMyCCValue.setText("Total Crendentia Currency : " + Utils.getwithoutCurrencyFormat(sessionUtil.getCredentiaCurrency())+" Points");

        LinearLayoutManager llm = new LinearLayoutManager(context);
        binding.  rvRedeemJTicketList.setLayoutManager(llm);
        redeemJTicktAdapter = new RedeemJTicktAdapter(context, ReddemJTcktList);
        redeemJTicktAdapter.setOnItemClickListener(this);
        binding. rvRedeemJTicketList.setAdapter(redeemJTicktAdapter);

        ReddemJTcktList.clear();
        binding. rvRedeemJTicketList.showProgress();
        getAllJTicketList();

        binding.rvRedeemJTicketList.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllJTicketList();
            }
        });

    }

    private void openFilterDialog(String Price, String ID) {
        binding. tvtotalEntryFee.setText("Entry Fee Metre\n " + Utils.getCurrencyFormat(redeemJModel.getContent().getTotalEntry()));
        binding. tvTotalEarning.setText("Earning Metre\n" + Utils.getCurrencyFormat(redeemJModel.getContent().getTotalEarning()));
        Float AllAmount = Float.valueOf(redeemJModel.getContent().getTotalEntry());
        Float WinningAmount = Float.valueOf(redeemJModel.getContent().getTotalEarning());
        if (AllAmount > WinningAmount) {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.dialog_redeem_j_ticket);

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


            TextView tvNoOfredeem = dialog.findViewById(R.id.tv_noofredeem);
            TextView tvAvblcc = dialog.findViewById(R.id.tv_avblcc);
            TextView tv_errBlcc = dialog.findViewById(R.id.tv_errBlcc);
            EditText edtAmount = dialog.findViewById(R.id.edtAmount);

            Button btnCancel = dialog.findViewById(R.id.btnCancel);
            Button btnApply = dialog.findViewById(R.id.btnApply);
            avilCC = Float.valueOf(sessionUtil.getCredentiaCurrency());
            tvAvblcc.setText("Available : " + Utils.getwithoutCurrencyFormat(sessionUtil.getCredentiaCurrency())+" Points");

            QTYList.add("Select QTY");
            QTYAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, QTYList);
            QTYAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            price = Float.valueOf(Price);
            tv_errBlcc.setText("Total Pay : " + Price+" Points");

            qty = Float.valueOf(String.valueOf("1"));
            totalCC = qty * price;
            if (totalCC > avilCC) {
                btnApply.setEnabled(false);
                btnApply.setAlpha(0.5f);
                tv_errBlcc.setTextColor(getResources().getColor(R.color.color_red));
            } else {
                btnApply.setEnabled(true);
                btnApply.setAlpha(1.0f);
                tv_errBlcc.setTextColor(getResources().getColor(R.color.color_green));
            }
            tv_errBlcc.setText("Redeem quantity value : " + String.format("%.02f", totalCC)+" Points");

            edtAmount.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!TextUtils.isEmpty(s)) {
                        qty = Float.valueOf(String.valueOf(s));
                        totalCC = qty * price;

                        if (totalCC > avilCC) {
                            btnApply.setEnabled(false);
                            btnApply.setAlpha(0.5f);
                            tv_errBlcc.setTextColor(getResources().getColor(R.color.color_red));
                        } else {

                            btnApply.setEnabled(true);
                            btnApply.setAlpha(1.0f);
                            tv_errBlcc.setTextColor(getResources().getColor(R.color.color_green));
                        }

                        tv_errBlcc.setText("Redeem quantity value  : " + String.format("%.02f", totalCC)+" Points");
                    } else {
                        tv_errBlcc.setText("Redeem quantity value : 00"+" Points");
                    }

                }
            });

            // getAccounts();
            btnApply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TextUtils.isEmpty(edtAmount.getText().toString())) {
                        edtAmount.requestFocus();
                        edtAmount.setError("Please enter Quantity");
                    } else if (edtAmount.getText().toString().equals("0")) {
                        edtAmount.requestFocus();
                        edtAmount.setError("Please enter min 1 Quantity");
                    } else {
                        AddJRedeem(Price, ID, edtAmount.getText().toString());
                        dialog.dismiss();
                    }
                }
            });
            btnCancel.setOnClickListener(view -> dialog.dismiss());

            dialog.show();
        } else {
            showalert("You can redeem J tickets only when your entry fee metre is more than earning metre. Refer T & C under J rewards for further clearity");
        }


    }

    private void getAllJTicketList() {
        Call<ResponseBody> call = APIClient.getInstance().getAllJTicket(sessionUtil.getToken(), sessionUtil.getId());
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Log.d(TAG, "success: " + responseData);
                binding. rvRedeemJTicketList.showRecycler();

                Gson gson = new Gson();
                redeemJModel = gson.fromJson(responseData, RedeemJModel.class);
                if (redeemJModel.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    ReddemJTcktList.clear();
                    ReddemJTcktList.addAll(redeemJModel.getContent().getContest());
                }

                binding.  tvtotalEntryFee.setText("Entry Fee Metre\n " + Utils.getCurrencyFormat(redeemJModel.getContent().getTotalEntry()));
                binding.  tvTotalEarning.setText("Earning Metre\n" + Utils.getCurrencyFormat(redeemJModel.getContent().getTotalEarning()));


                Log.d(TAG, "List Size: " + ReddemJTcktList.size());
                redeemJTicktAdapter.notifyDataSetChanged();


            }

            @Override
            public void failure(String responseData) {
                Log.d(TAG, "success: " + responseData);
            }
        });
    }

    private void AddJRedeem(String price, String ID, String qty) {
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        String request = "";
        try {
            Log.i("qty","==>"+qty);
            jsonObject.put("price", price);
            jsonObject.put("id", ID);
            jsonObject.put("qty", qty);
            Log.d(TAG, "AddJRedeem: " + price + ">>>" + ID);


            request = jsonObject.toString();
            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, getString(R.string.crypt_pass));
            data = request.getBytes("UTF-8");
            request = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        final Call<ResponseBody> call = APIClient.getInstance()
                .AddJRedeem(sessionUtil.getToken(), sessionUtil.getId(), request);

        newApiCall.makeApiCall(context, true, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                DecimalFormat format = new DecimalFormat("0.##");

                Log.d(TAG, "success>>: " + responseData);
                Gson gson = new Gson();
                AddRedeemModel addRedeemModel = gson.fromJson(responseData, AddRedeemModel.class);
                if (addRedeemModel.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    // Utils.showToast(context, addRedeemModel.getMessage());
                    showalert(addRedeemModel.getMessage());
                    Log.d(TAG, "success: " + addRedeemModel.getContent().getWallate().getCcAmount());
                    sessionUtil.setCredentiaCurrency(addRedeemModel.getContent().getWallate().getCcAmount());
                    binding. tvMyCCValue.setText("Total Crendentia Currency : " + Utils.getComaFormat(addRedeemModel.getContent().getWallate().getCcAmount()));
                } else if (addRedeemModel.getStatusCode() == Utils.StandardStatusCodes.NO_DATA_FOUND) {
                    Utils.showToast(context, addRedeemModel.getMessage());
                }
            }


            @Override
            public void failure(String responseData) {
                if (!responseData.isEmpty()) {
                    Utils.showToast(context, responseData);
                }
            }
        });

    }

    private void showalert(String msg) {
        final CustomDialog customDialog = new CustomDialog();
        customDialog.showDialogTwoButton(context, "", msg,
                getString(R.string.ok), null, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        customDialog.dismissonedialog();
                    }
                }, null);
    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();

    }


    @Override
    public void onItemClick(String Price, String ID, String TYPE, int waiting_no) {
        if (TYPE.equalsIgnoreCase("REDEEM")) {
            openFilterDialog(Price, ID);

        } else {
            Intent intent = null;
            intent = new Intent(context, MyJTicketsWaitingRoomActivity.class);
            intent.putExtra("Ticket_Id", ID);
            intent.putExtra("status", "Redeem");
            startActivity(intent);
        }
    }

    /* @Override
     public void onClick() {
         openOfferDailog();
     }*/
    LineChart chart;

    private void openOfferDailog() {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.popup_jticket_chart);

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
        chart = dialog.findViewById(R.id.lineChart);
        // setData();
        dialog.show();
    }

    private void setData() {
        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setTouchEnabled(true);
        chart.setMaxHighlightDistance(50f);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);

        chart.setMaxVisibleValueCount(200);
        chart.setPinchZoom(true);


        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXOffset(5f);

        YAxis yl = chart.getAxisLeft();
        yl.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        yl.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        chart.getAxisRight().setEnabled(false);
        String xAxisLabel[] = {"", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};


        XAxis xl = chart.getXAxis();
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setDrawGridLines(false);
        xl.setValueFormatter(new IndexAxisValueFormatter(xAxisLabel));
        ArrayList<Entry> values1 = new ArrayList<>();
        try {
            for (int i = 0; i < xAxisLabel.length; i++) {
                //   values1.add(new Entry(i, (i), context.getDrawable(R.drawable.rec)));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        ScatterDataSet set1 = new ScatterDataSet(values1, "DS 1");
        set1.setScatterShape(ScatterChart.ScatterShape.SQUARE);
        set1.setColor(ColorTemplate.COLORFUL_COLORS[0]);
        set1.setScatterShapeSize(8f);

        ArrayList<IScatterDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1); // add the data sets
        ScatterData data = new ScatterData(dataSets);

        chart.invalidate();

    }


}
