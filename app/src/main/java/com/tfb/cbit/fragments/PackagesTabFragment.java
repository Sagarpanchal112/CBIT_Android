package com.tfb.cbit.fragments;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.tfb.cbit.CBit;
import com.tfb.cbit.R;
import com.tfb.cbit.activities.AddMoneyActivity;
import com.tfb.cbit.adapter.PackagesAdapter;
import com.tfb.cbit.adapter.SelectedPackagesAdapter;
import com.tfb.cbit.api.APIClient;
import com.tfb.cbit.api.ApiCallback;
import com.tfb.cbit.api.NewApiCall;
import com.tfb.cbit.databinding.FragmentAboutBinding;
import com.tfb.cbit.databinding.FragmentPackagesBinding;
import com.tfb.cbit.interfaces.OnItemClickListener;
import com.tfb.cbit.interfaces.PackageInterface;
import com.tfb.cbit.models.contest_pkg.Content;
import com.tfb.cbit.models.contest_pkg.ContestPrice;
import com.tfb.cbit.models.contest_pkg.PackageModel;
import com.tfb.cbit.models.pkg_buy.PkgBuyModel;
import com.tfb.cbit.utility.CustomDialog;
import com.tfb.cbit.utility.PrintLog;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class PackagesTabFragment extends Fragment implements PackageInterface {
    private Context context;
    private SessionUtil sessionUtil;
    private NewApiCall newApiCall;
    private List<Content> packageList = new ArrayList<>();
    private PackagesAdapter packagesAdapter;
    private boolean isMyPkg = false;
    public List<String> packegTimeList = new ArrayList<>();
    public List<String> packegValidtyList = new ArrayList<>();
    public String selectedTime = "", selectedValidity = "";

    public PackagesTabFragment() {
        // Required empty public constructor
    }

    public static PackagesTabFragment newInstance() {
        PackagesTabFragment fragment = new PackagesTabFragment();
        return fragment;
    }

    public static PackagesTabFragment newInstance(boolean isMyPkg) {
        PackagesTabFragment fragment = new PackagesTabFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("MyPkg", isMyPkg);
        fragment.setArguments(bundle);
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

    private FragmentPackagesBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPackagesBinding.bind(LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_packages, container, false));
        View view = binding.getRoot();
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionUtil = new SessionUtil(context);
        newApiCall = new NewApiCall();
        binding.tvAPDValue.setVisibility(View.GONE);
        binding.DayOfJoin.setVisibility(View.GONE);
        if (getArguments() != null) {
            isMyPkg = getArguments().getBoolean("MyPkg", false);
        }
        if (isMyPkg)
            binding.linearSelection.setVisibility(View.GONE);
        else
            binding.linearSelection.setVisibility(View.VISIBLE);
        binding.tvWallet.setText(Utils.getCurrencyFormat(String.valueOf(Double.parseDouble(sessionUtil.getAmount()) + Double.parseDouble(sessionUtil.getWAmount()))));

        LinearLayoutManager llm = new LinearLayoutManager(context);
        binding.rvPackagesList.setLayoutManager(llm);
        packagesAdapter = new PackagesAdapter(context, packageList, isMyPkg);
        packagesAdapter.setOnItemClickListener(this);
        binding.rvPackagesList.setAdapter(packagesAdapter);

        packageList.clear();
        binding.rvPackagesList.showProgress();
        getContestTime();
        getContestDays();
        if (isMyPkg) {
            getPackageList();
        }

        binding.rvPackagesList.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPackageList();
            }
        });

        String bodyString = null, contestTime = "";
        try {
            bodyString = CBit.getCryptLib().decryptCipherTextWithRandomIV("IZl+Da9j21fw8qD2c69C8FVspAUhtB+wiebkMQzPBIHgW1ucZZqL3sOzSPGc71Ju0yZKjz5PeeJhx/FrJ3sbuJwWYl/Xusrt3FZopyOn0mV3lyf8rVePsjdWq5hGfV2trWyCWeJ+e5T7ZQ72xp5+uA==", context.getString(R.string.crypt_pass));
            PrintLog.d("PackageTab", "WS call success res :=> " + bodyString);
            contestTime = CBit.getCryptLib().decryptCipherTextWithRandomIV("E0FjuvKSsoKFqZs0MYAHUkgVKCdLhVaCIadcpNKpUJmXjWtBzCW/an9N7OhoD67smrfGjZ0a7Hu27KPeuqraYJgZC1LLobdyaBQLfYhedtad0uw/S+VbXAm7btdP3xryesZDqn3aCHHKqlKbWHIMpfrqeEVQHJ9e5rYowKCCnYL0YaOozLGqxTN13daPAjd12GgFKF2FHpRB+pS8+QnkXmqR884oIHZjhIROqzwo0mdxcYFlDXVuf3ejvBDEMowLlzKpjbytElvgdElelg7utw==", context.getString(R.string.crypt_pass));
            PrintLog.d("PackageTab", "contestTime :=> " + contestTime);
            contestTime = CBit.getCryptLib().decryptCipherTextWithRandomIV("VKVIb5mohnANdW/8virQqPWsr0BriO4cxG89pJ8rsNpkuxuA/TLjFrr1IbIZqZfFlpdlZOLsCiAYBxnK6d8TBL2jt5tcGmS+g2AYr1FLyW4sSJvSsI673VgY2mGJK+i2Qrm7dpbHd/WNVRN/q9HLIQ==", context.getString(R.string.crypt_pass));
            PrintLog.d("PackageTab", "Packeges :=> " + contestTime);

        } catch (Exception e) {
            e.printStackTrace();
        }

        binding.linearPay.setOnClickListener(view1 -> {
            buyTicket();
        });
        binding.chkall.setOnClickListener(view1 -> {
            chkallClick();
        });


    }

    public void buyTicket() {
        if (binding.tvPayTitle.getText().toString().equals(getString(R.string.pay))) {
            //Popup Open
            buyPkgPopup();
        } else {
            double price = 0;
            for (Content ticket : packageList) {
                if (ticket.isSelected()) {
                    price = price + ticket.getAmount();
                }
            }
            price = price - (Double.parseDouble(sessionUtil.getAmount()) + Double.parseDouble(sessionUtil.getWAmount()));
            Intent intent = new Intent(context, AddMoneyActivity.class);
            intent.putExtra(AddMoneyActivity.AMOUNT_VALUE, String.valueOf(Math.ceil(price)));
            startActivity(intent);
        }

//        buyPkgPopup(1);
    }

    protected void chkallClick() {
        int counter = 0;
        double price = 0;

        for (int i = 0; i < packageList.size(); i++) {

            packageList.get(i).getPackages().setSelected(binding.chkall.isChecked());
            for (int j = 0; j < packageList.get(i).getContestPriceList().size(); j++) {
                packageList.get(i).getContestPriceList().get(j).setSelected(binding.chkall.isChecked());
            }
            packagesAdapter.notifyItemChanged(i);
        }

        for (Content ticket : packageList) {
            for (ContestPrice cObj : ticket.getContestPriceList()) {
                if (cObj.isSelected()) {
                    counter++;
                    price = price + (cObj.getAmount() * ticket.getPackages().getValidity());
                }
            }
        }

        binding.tvSelected.setText("Selected " + counter);
        binding.tvPay.setText(Utils.getTwoDecimalFormat(Float.parseFloat(String.valueOf(price))));
        if (binding.tvSelected.getVisibility() != View.VISIBLE)
            Utils.expand(binding.tvSelected);
        binding.linearPay.setVisibility(View.VISIBLE);
        if (price > (Double.parseDouble(sessionUtil.getAmount()) + Double.parseDouble(sessionUtil.getWAmount()))) {
            binding.tvPayTitle.setText(getString(R.string.addtowallet));
            binding.tvWallet.setTextColor(ContextCompat.getColor(context, R.color.color_red));
        } else {
            binding.tvPayTitle.setText(getString(R.string.pay));
            binding.tvWallet.setTextColor(ContextCompat.getColor(context, R.color.color_green));
        }
        if (counter == 0) {
            if (binding.tvSelected.getVisibility() == View.VISIBLE) {
                Utils.collapse(binding.tvSelected);
                binding.linearPay.setVisibility(View.GONE);
            }
        }
    }


    private void getPackageList() {
        Call<ResponseBody> call;

        if (isMyPkg) {
            call = APIClient.getInstance().myPackage(sessionUtil.getToken(), sessionUtil.getId());
        } else {

            call = APIClient.getInstance().getPackages(sessionUtil.getToken(), sessionUtil.getId(), selectedTime, selectedValidity);

        }
        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                binding.rvPackagesList.showRecycler();
                Gson gson = new Gson();
                PackageModel pm = gson.fromJson(responseData, PackageModel.class);
                if (pm.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    packageList.clear();
                    if (isMyPkg) {
                        packageList.addAll(pm.getContent());
                    } else {
                        for (Content ticket : pm.getContent()) {
                            if (ticket.getContestPriceList().size() > 0) {
                                packageList.add(ticket);
                            }
                        }
                    }
                }
                packagesAdapter.notifyDataSetChanged();

            }

            @Override
            public void failure(String responseData) {

            }
        });
    }

    public void getContestTime() {
        Call<ResponseBody> call;
        call = APIClient.getInstance().getContestTime(sessionUtil.getToken(), sessionUtil.getId());

        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                try {
                    JSONObject jObj = new JSONObject(responseData);
                    JSONArray jArray = jObj.getJSONArray("content");
                    for (int i = 0; i < jArray.length(); i++) {
                        packegTimeList.add(jArray.getJSONObject(i).getString("StartTime"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (packegTimeList.size() > 0) {
                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, packegTimeList);
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerTime.setAdapter(spinnerAdapter);
                    binding.spinnerValidity.setSelection(0);
                    selectedTime = packegTimeList.get(0);
                    spinnerAdapter.notifyDataSetChanged();
                    binding.spinnerTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                            // your code here
                            selectedTime = packegTimeList.get(position);
                            getPackageList();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parentView) {
                            // your code here
                        }

                    });
                    getPackageList();
                } else {
                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, packegTimeList);
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerTime.setAdapter(spinnerAdapter);
                    spinnerAdapter.add("No Data");
                    binding.spinnerValidity.setSelection(0);
                    spinnerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void failure(String responseData) {

            }
        });
    }

    public void getContestDays() {
        Call<ResponseBody> call;
        call = APIClient.getInstance().getContestDays(sessionUtil.getToken(), sessionUtil.getId());
        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                try {
                    JSONObject jObj = new JSONObject(responseData);
                    JSONArray jArray = jObj.getJSONArray("content");
                    for (int i = 0; i < jArray.length(); i++) {
                        packegValidtyList.add(jArray.getJSONObject(i).getString("validity"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (packegValidtyList.size() > 0) {
                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, packegValidtyList);
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerValidity.setAdapter(spinnerAdapter);
                    spinnerAdapter.notifyDataSetChanged();
                    selectedValidity = packegValidtyList.get(0);
                    binding.spinnerValidity.setSelection(0);
                    binding.spinnerValidity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                            // your code here
                            selectedValidity = packegValidtyList.get(position);
                            getPackageList();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parentView) {
                            // your code here
                        }

                    });
                } else {
                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, packegValidtyList);
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerValidity.setAdapter(spinnerAdapter);
                    spinnerAdapter.add("No Data");
                    binding.spinnerValidity.setSelection(0);
                    spinnerAdapter.notifyDataSetChanged();
                }


            }

            @Override
            public void failure(String responseData) {

            }
        });
    }

    @Override
    public void onItemClick(View view, int position, boolean isChild, int childPosition) {
      /*  switch (view.getId()) {
            case R.id.btnBuy:
                buyPkgPopup(position);
                break;
        }*/

        if (view.getId() == R.id.chkSelect) {
            int counter = 0;
            double price = 0;
            //if(counter>0){
            if (isChild) {
                packageList.get(position).getContestPriceList().get(childPosition).setSelected(!packageList.get(position).getContestPriceList().get(childPosition).isSelected());

                packagesAdapter.notifyItemChanged(position);
                for (Content ticket : packageList) {
                    for (ContestPrice cObj : ticket.getContestPriceList()) {
                        if (cObj.isSelected()) {
                            counter++;
                            price = price + (cObj.getAmount() * ticket.getPackages().getValidity());
                        }
                    }
                }
            } else {
                packageList.get(position).getPackages().setSelected(!packageList.get(position).getPackages().isSelected());
                for (int j = 0; j < packageList.get(position).getContestPriceList().size(); j++) {
                    packageList.get(position).getContestPriceList().get(j).setSelected(packageList.get(position).getPackages().isSelected());
                }
                packagesAdapter.notifyItemChanged(position);
                for (Content ticket : packageList) {
                    for (ContestPrice cObj : ticket.getContestPriceList()) {
                        if (cObj.isSelected()) {
                            counter++;
                            price = price + (cObj.getAmount() * ticket.getPackages().getValidity());
                        }
                    }
                }

            }

            binding.tvSelected.setText(counter + " Contest Selected ");
            binding.tvPay.setText(Utils.getTwoDecimalFormat(Float.parseFloat(String.valueOf(price))));
            if (binding.tvSelected.getVisibility() != View.VISIBLE)
                Utils.expand(binding.tvSelected);
            binding.linearPay.setVisibility(View.VISIBLE);
            if (price > (Double.parseDouble(sessionUtil.getAmount()) + Double.parseDouble(sessionUtil.getWAmount()))) {
                binding.tvPayTitle.setText(getString(R.string.addtowallet));
                binding.tvWallet.setTextColor(ContextCompat.getColor(context, R.color.color_red));
            } else {
                binding.tvPayTitle.setText(getString(R.string.pay));
                binding.tvWallet.setTextColor(ContextCompat.getColor(context, R.color.color_green));
            }
            if (counter == 0) {
                if (binding.tvSelected.getVisibility() == View.VISIBLE) {
                    Utils.collapse(binding.tvSelected);
                    binding.linearPay.setVisibility(View.GONE);
                }
            }
        }
    }

    private void buyPkgPopup() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.package_confirmation_popup);

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
        TextView txtTime = dialog.findViewById(R.id.txtTime);
        TextView txtValidity = dialog.findViewById(R.id.txtValidity);

        RecyclerView rvPackagesPurchesedList = dialog.findViewById(R.id.rvPackagesPurchesedList);
        TextView tvSecondaryAmount = dialog.findViewById(R.id.tvSecondaryAmount);
        TextView tvTotalAmount = dialog.findViewById(R.id.tvTotalAmount);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        Button btnOk = dialog.findViewById(R.id.btnOk);
        double primaryPrice = Double.parseDouble(sessionUtil.getAmount());
        double SecondaryPrice = Double.parseDouble(sessionUtil.getWAmount());
        double price = 0;
        List<ContestPrice> selectedPackageList = new ArrayList<>();
        for (Content ticket : packageList) {
            for (ContestPrice cObj : ticket.getContestPriceList()) {
                if (cObj.isSelected()) {
                    selectedPackageList.add(cObj);
                    price = price + (cObj.getAmount() * ticket.getPackages().getValidity());
                }
            }

        }

        txtTime.setText("Game time : " + selectedTime);
        txtValidity.setText("Validity : " + selectedValidity + " days");
        LinearLayoutManager llm = new LinearLayoutManager(context);
        rvPackagesPurchesedList.setLayoutManager(llm);
        SelectedPackagesAdapter selectedPackagesAdapter = new SelectedPackagesAdapter(context, selectedPackageList, isMyPkg, Integer.parseInt(selectedValidity));
        rvPackagesPurchesedList.setAdapter(selectedPackagesAdapter);


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

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                buyPkg();
            }
        });

        dialog.show();
    }

    private void buyPkgPopup(final int position) {
        CustomDialog customDialog = new CustomDialog();
        customDialog.showDialogTwoButton(context, "Purchase Package", "Are you sure to want purchase this package?",
                "Buy", "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        double pkgPrice = 0;
                        for (Content ticket : packageList) {
                            if (ticket.isSelected()) {

                                pkgPrice = pkgPrice + ticket.getAmount();
                            }
                        }
                        double wallet = (Double.parseDouble(sessionUtil.getAmount()) + Double.parseDouble(sessionUtil.getWAmount()));
                        if (pkgPrice <= wallet) {
                            buyPkg();
                        } else {
                            Intent intent = new Intent(context, AddMoneyActivity.class);
                            intent.putExtra(AddMoneyActivity.AMOUNT_VALUE, String.valueOf(pkgPrice));
                            startActivity(intent);
                        }
                    }
                }, null);
    }

    private void buyPkg() {
        JSONArray jsonArray = new JSONArray();
        try {
            StringBuilder ticketIds = new StringBuilder();

            for (Content ticket : packageList) {
                JSONObject jsonObject = new JSONObject();

                jsonObject.put("id", ticket.getPackages().getId());
                JSONArray jsonIDArray = new JSONArray();

                for (ContestPrice cObj : ticket.getContestPriceList()) {
                    if (cObj.isSelected()) {
                        ticketIds.append(cObj.getContestPriceID()).append(",");
                        jsonIDArray.put(cObj.getContestPriceID());
                    }
                }
                ticketIds = ticketIds.deleteCharAt(ticketIds.length() - 1);
                if (jsonIDArray != null) {
                    jsonObject.put("contestPriceID", jsonIDArray);
                    jsonArray.put(jsonObject);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        byte[] data;
        String request = "";
        try {
            request = jsonArray.toString();
            Log.i("TAG", "request :->" + request);
            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, getString(R.string.crypt_pass));
            data = request.getBytes("UTF-8");
            request = Base64.encodeToString(data, Base64.DEFAULT);


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Call<ResponseBody> call = APIClient.getInstance()
                .buyPackage(sessionUtil.getToken(), sessionUtil.getId(), jsonArray.toString());
        //.buyPackage(sessionUtil.getToken(),sessionUtil.getName(),pkgID);
        newApiCall.makeApiCall(context, true, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Gson gson = new Gson();
                PkgBuyModel pkgBuyModel = gson.fromJson(responseData, PkgBuyModel.class);
                if (pkgBuyModel.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    Utils.showToast(context, "Congratulations, Commission is now active under your account.");
                    sessionUtil.setAmount(pkgBuyModel.getContent().getPbAmount());
                    sessionUtil.setWAmount(pkgBuyModel.getContent().getSbAmount());
                } else {
                    Utils.showToast(context, pkgBuyModel.getMessage());
                }
            }

            @Override
            public void failure(String responseData) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


}
