package com.tfb.cbit.fragments;

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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.tfb.cbit.CBit;
import com.tfb.cbit.R;
import com.tfb.cbit.activities.AddMoneyActivity;
import com.tfb.cbit.activities.HomeActivity;
import com.tfb.cbit.adapter.EasyContestAdapter;
import com.tfb.cbit.adapter.EasyContestPriceAdapter;
import com.tfb.cbit.api.APIClient;
import com.tfb.cbit.api.ApiCallback;
import com.tfb.cbit.api.NewApiCall;
import com.tfb.cbit.databinding.FragmentAboutBinding;
import com.tfb.cbit.databinding.FragmentEasyJoinBinding;
import com.tfb.cbit.interfaces.OnItemClickListener;
import com.tfb.cbit.interfaces.OnItemSwitchClickListener;
import com.tfb.cbit.models.EasyJoinModel;
import com.tfb.cbit.models.SendAutoRenewRequest;
import com.tfb.cbit.models.SendEasyJoinRequest;
import com.tfb.cbit.utility.LogHelper;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EasyJoinFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EasyJoinFragment extends Fragment {

    private SessionUtil sessionUtil;
    private Context context;

    EasyContestAdapter easyContestAdapter;
    EasyContestPriceAdapter easyContestPriceAdapter;
    private List<EasyJoinModel.Contest> referralLists = new ArrayList<>();
    private List<EasyJoinModel.PrinceData> princeDataArrayList = new ArrayList<>();

    public String sortType = "all";
    double primaryPrice = 0;
    double SecondaryPrice = 0;
    double price = 0;
    double finlPrice = 0;
    int count = 0;

    public EasyJoinFragment() {
        // Required empty public constructor
    }

    public static EasyJoinFragment newInstance() {
        EasyJoinFragment fragment = new EasyJoinFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private FragmentEasyJoinBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentEasyJoinBinding.bind(LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_easy_join, container, false));
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionUtil = new SessionUtil(context);
        LinearLayoutManager llm = new LinearLayoutManager(context);
        LinearLayoutManager llm1 = new LinearLayoutManager(context);
        binding.rvContest.setLayoutManager(llm);
        easyContestAdapter = new EasyContestAdapter(EasyJoinFragment.this, referralLists);
        binding.rvContest.setAdapter(easyContestAdapter);
        binding.tvWallet.setText(Utils.getCurrencyFormat(String.valueOf(Double.parseDouble(sessionUtil.getAmount()) + Double.parseDouble(sessionUtil.getWAmount()))));
        getAutoRenewEasyJoin();
        easyContestAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (binding.chkall.isChecked()) {
                    binding.chkall.setChecked(false);
                } else {

                }
                referralLists.get(position).setSelected(!referralLists.get(position).isSelected());
                easyContestAdapter.notifyItemChanged(position);
                priceCalcaulation();
            }
        });
        easyContestAdapter.setOnItemSwitchClickListener(new OnItemSwitchClickListener() {
            @Override
            public void onItemClick(View view, int position, int status) {
                referralLists.get(position).setSwitchSelected(!referralLists.get(position).isSwitchSelected());
                easyContestAdapter.notifyItemChanged(position);
            }
        });

        binding.rvContestPrice.setLayoutManager(llm1);
        easyContestPriceAdapter = new EasyContestPriceAdapter(EasyJoinFragment.this, princeDataArrayList);
        binding.rvContestPrice.setAdapter(easyContestPriceAdapter);
        easyContestPriceAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (binding.chkContsestAll.isChecked()) {
                    binding.chkContsestAll.setChecked(false);
                } else {

                }
                princeDataArrayList.get(position).setSelected(!princeDataArrayList.get(position).isSelected());
                easyContestPriceAdapter.notifyItemChanged(position);
                priceCalcaulation();
            }
        });
        binding.spinnerSortBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    sortType = "All";
                    easyJoinContest(sortType);
                } else if (position == 1) {
                    sortType = "Hourly";
                    easyJoinContest(sortType);
                } else if (position == 2) {
                    sortType = "Half hourly";
                    easyJoinContest(sortType);
                } else if (position == 3) {
                    sortType = "Quarterly";
                    easyJoinContest(sortType);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        binding.linearPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isContest = false;
                boolean isTicket = false;
                for (EasyJoinModel.PrinceData princeData : princeDataArrayList) {
                    if (princeData.isSelected()) {
                        isTicket = true;
                    }
                }
                for (EasyJoinModel.Contest ticket : referralLists) {
                    if (ticket.isSelected()) {
                        isContest = true;
                    }
                }
                if (!isContest) {
                    Utils.showToast(context, "Please choose game");
                } else if (!isTicket) {
                    Utils.showToast(context, "Please choose contests");
                } else {
                    if (binding.tvPayTitle.getText().toString().equals(getString(R.string.confirmpay))) {
                        //Popup Open
                        openConfirmationPopup();
                    } else {
                        priceCalcaulation();
                        finlPrice = finlPrice - (Double.parseDouble(sessionUtil.getAmount()) + Double.parseDouble(sessionUtil.getWAmount()));
                        Intent intent = new Intent(context, AddMoneyActivity.class);
                        intent.putExtra(AddMoneyActivity.AMOUNT_VALUE, String.valueOf(Math.ceil(finlPrice)));
                        startActivity(intent);
                    }

                }
            }
        });

        binding.chkall.setOnClickListener(view1 -> {
            chkallClick();
        });
        binding.chkContsestAll.setOnClickListener(view1 -> {
            chkContsestAll();
        });
    }

    protected void chkallClick() {
        for (int i = 0; i < referralLists.size(); i++) {
            referralLists.get(i).setSelected(binding.chkall.isChecked());

        }
        easyContestAdapter.notifyDataSetChanged();
        priceCalcaulation();
    }

    protected void chkContsestAll() {
        for (int i = 0; i < princeDataArrayList.size(); i++) {
            princeDataArrayList.get(i).setSelected(binding.chkContsestAll.isChecked());
            easyContestPriceAdapter.notifyItemChanged(i);
        }
        priceCalcaulation();
    }

    private void openConfirmationPopup() {
        final Dialog dialog = new Dialog(context);
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
        double finlPrice = 0;
        int count = 0;

        for (EasyJoinModel.PrinceData princeData : princeDataArrayList) {
            if (princeData.isSelected()) {
                price += Double.parseDouble(princeData.getAmount());
            }
        }
        for (EasyJoinModel.Contest ticket : referralLists) {
            if (ticket.isSelected()) {
                count++;
            }
        }
        finlPrice = count * price;
        if (finlPrice <= primaryPrice) {
            tvPrimaryAmount.setText(Utils.getTwoDecimalFormat(Float.parseFloat(String.valueOf(finlPrice))));
            tvSecondaryAmount.setText(Utils.INDIAN_RUPEES + "0.00");
            tvTotalAmount.setText(Utils.getTwoDecimalFormat(Float.parseFloat(String.valueOf(finlPrice))));
        } else {
            double remainingPrice = finlPrice - primaryPrice;
            tvPrimaryAmount.setText(Utils.getTwoDecimalFormat(Float.parseFloat(String.valueOf(primaryPrice))));
            tvSecondaryAmount.setText(Utils.getTwoDecimalFormat(Float.parseFloat(String.valueOf(remainingPrice))));
            tvTotalAmount.setText(Utils.getTwoDecimalFormat(Float.parseFloat(String.valueOf(finlPrice))));
        }

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                ArrayList<SendEasyJoinRequest> snArray = new ArrayList<>();
                ArrayList<SendAutoRenewRequest> anArray = new ArrayList<>();
                ArrayList<String> amountArray = new ArrayList<>();
                for (EasyJoinModel.PrinceData princeData : princeDataArrayList) {
                    if (princeData.isSelected()) {
                        amountArray.add(princeData.getAmount());
                    }
                }

                for (EasyJoinModel.Contest ticket : referralLists) {
                    SendEasyJoinRequest sn = new SendEasyJoinRequest();
                    SendAutoRenewRequest ar = new SendAutoRenewRequest();
                    StringBuilder ticketIds = new StringBuilder();
                    StringBuilder priceIds = new StringBuilder();
                    if (ticket.isSelected()) {
                        sn.setContest_id(String.valueOf(ticket.getId()));
                        ar.setContest_time(Utils.getHHMMStr(ticket.getStartDate()));
                        for (int i = 0; i < ticket.getPrinceData().size(); i++) {
                            for (int j = 0; j < amountArray.size(); j++) {
                                if (ticket.getPrinceData().get(i).getAmount().equals(amountArray.get(j))) {
                                    priceIds.append(ticket.getPrinceData().get(i).getAmount()).append(",");
                                    ticketIds.append(ticket.getPrinceData().get(i).getId()).append(",");
                                    sn.setContestPriceID(ticketIds.toString());
                                    ar.setPrice(priceIds.toString());
                                }
                            }
                        }
                        ar.setStatus(0);
                        sn.setContestPriceID(sn.getContestPriceID().substring(0, sn.getContestPriceID().length() - 1));
                        ar.setPrice(ar.getPrice().substring(0, ar.getPrice().length() - 1));
                        snArray.add(sn);
                        anArray.add(ar);

                    }
                }


                getJoinContest(snArray, anArray);
            }
        });

        dialog.show();
    }

    public void priceCalcaulation() {
        primaryPrice = Double.parseDouble(sessionUtil.getAmount());
        SecondaryPrice = Double.parseDouble(sessionUtil.getWAmount());
        price = 0;
        finlPrice = 0;
        count = 0;


        for (EasyJoinModel.PrinceData princeData : princeDataArrayList) {
            if (princeData.isSelected()) {
                price += Double.parseDouble(princeData.getAmount());
            }
        }
        for (EasyJoinModel.Contest ticket : referralLists) {
            if (ticket.isSelected()) {
                count++;
            }
        }
        finlPrice = count * price;
        LogHelper.e("TAG", "PRICE : " + finlPrice);
        binding.tvPay.setText(Utils.getTwoDecimalFormat(Float.parseFloat(String.valueOf(finlPrice))));
        if (finlPrice > (Double.parseDouble(sessionUtil.getAmount()) + Double.parseDouble(sessionUtil.getWAmount()))) {
            LogHelper.e("TAG", "IF PRICE : " + finlPrice);
            binding.tvPayTitle.setText(getString(R.string.addtowallet));
            binding.tvWallet.setTextColor(ContextCompat.getColor(context, R.color.color_red));
        } else {
            LogHelper.e("TAG", "ElSE PRICE : " + finlPrice);
            binding.tvPayTitle.setText(getString(R.string.confirmpay));
            binding.tvWallet.setTextColor(ContextCompat.getColor(context, R.color.color_green));
        }
    }

    private void getJoinContest(ArrayList<SendEasyJoinRequest> ticketsIds, ArrayList<SendAutoRenewRequest> anArray) {
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        String request = "";
        Gson gson = new Gson();
        try {
            JSONArray jsonArray = new JSONArray(gson.toJson(ticketsIds));
            jsonObject.put("contestData", jsonArray);
            Log.d("TAG", "getJoinContest: >>" + jsonArray);
            request = jsonObject.toString();
            Log.d("TAG", "request: >>" + request);
            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, getString(R.string.crypt_pass));
            data = request.getBytes(StandardCharsets.UTF_8);
            request = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Call<ResponseBody> call = APIClient.getInstance().easyjoinContestPrice(sessionUtil.getToken(), sessionUtil.getId(), request);
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, true, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                //    setAutoRenewEasyJoin(anArray);
                Intent intent = new Intent(context, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().finish();
            }

            @Override
            public void failure(String responseData) {

            }
        });

    }

    private void setAutoRenewEasyJoin(ArrayList<SendAutoRenewRequest> anArray) {
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        String request = "";
        Gson gson = new Gson();
        try {
            JSONArray jsonArray = new JSONArray(gson.toJson(anArray));
            jsonObject.put("contestData", jsonArray);
            Log.d("TAG", "getJoinContest: >>" + jsonArray);
            request = jsonObject.toString();
            Log.d("TAG", "request: >>" + request);
            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, getString(R.string.crypt_pass));
            data = request.getBytes(StandardCharsets.UTF_8);
            request = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Call<ResponseBody> call = APIClient.getInstance().setAutoRenewEasyJoin(sessionUtil.getToken(), sessionUtil.getId(), request);
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, true, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Intent intent = new Intent(context, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().finish();
            }

            @Override
            public void failure(String responseData) {

            }
        });
    }


    private void getAutoRenewEasyJoin() {
        Call<ResponseBody> call = APIClient.getInstance().getAutoRenewEasyJoin(sessionUtil.getToken(), sessionUtil.getId());
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                //  longLog("sucess :",responseData);
                Log.i("sucess :", responseData);
                Gson gson = new Gson();


            }

            @Override
            public void failure(String responseData) {

                Log.d("TAG", "success: " + responseData);
            }
        });
    }

    private void easyJoinContest(String sortType) {
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        String request = "";
        try {
            jsonObject.put("filters", sortType);
            request = jsonObject.toString();
            Log.d("TAG", "request: >>" + request);
            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, getString(R.string.crypt_pass));
            data = request.getBytes(StandardCharsets.UTF_8);
            request = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Call<ResponseBody> call = APIClient.getInstance().easyJoinContest(sessionUtil.getToken(), sessionUtil.getId(), request);
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                //  longLog("sucess :",responseData);
                Log.i("sucess :", responseData);
                Gson gson = new Gson();
                EasyJoinModel nm = gson.fromJson(responseData, EasyJoinModel.class);
                if (nm.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    referralLists.clear();
                    referralLists.addAll(nm.getContent().getContest());
                    princeDataArrayList.clear();
                    princeDataArrayList.addAll(nm.getContent().getContest().get(0).getPrinceData());
                    easyContestAdapter.notifyDataSetChanged();
                    easyContestPriceAdapter.notifyDataSetChanged();

                }

            }

            @Override
            public void failure(String responseData) {

                Log.d("TAG", "success: " + responseData);
            }
        });
    }
}