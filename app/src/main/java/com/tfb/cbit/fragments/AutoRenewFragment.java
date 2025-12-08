package com.tfb.cbit.fragments;

import android.app.Dialog;
import android.content.Context;
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
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.tfb.cbit.CBit;
import com.tfb.cbit.R;
import com.tfb.cbit.adapter.AutoJoinAdapter;
import com.tfb.cbit.adapter.AutoJoinPriceAdapter;
import com.tfb.cbit.api.APIClient;
import com.tfb.cbit.api.ApiCallback;
import com.tfb.cbit.api.NewApiCall;
import com.tfb.cbit.databinding.FragmentAboutBinding;
import com.tfb.cbit.databinding.FragmentAutoRenewBinding;
import com.tfb.cbit.interfaces.OnItemClickListener;
import com.tfb.cbit.interfaces.OnItemSwitchClickListener;
import com.tfb.cbit.models.AutoRenewModel;
import com.tfb.cbit.models.SendAutoRenewRequest;
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
 * Use the {@link AutoRenewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class AutoRenewFragment extends Fragment {

    private SessionUtil sessionUtil;
    private Context context;

    AutoJoinAdapter adapter;
    private List<AutoRenewModel.AutorenewTable> referralLists = new ArrayList<>();
    private List<AutoRenewModel.Price> priceLists = new ArrayList<>();

    public AutoRenewFragment() {
        // Required empty public constructor
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

    // TODO: Rename and change types and number of parameters
    public static AutoRenewFragment newInstance() {
        AutoRenewFragment fragment = new AutoRenewFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    private FragmentAutoRenewBinding binding;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAutoRenewBinding.bind(LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_auto_renew, container, false));
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionUtil = new SessionUtil(context);
        LinearLayoutManager llm = new LinearLayoutManager(context);
        binding. rvContest.setLayoutManager(llm);
        getAutoRenewEasyJoin();
        adapter = new AutoJoinAdapter(AutoRenewFragment.this, referralLists);
        binding. rvContest.setAdapter(adapter);
        adapter.setOnItemSwitchClickListener(new OnItemSwitchClickListener() {
            @Override
            public void onItemClick(View view, int position, int status) {
                if (referralLists.get(position).getContest_status() == 0) {
                    setAutoRenewEasyJoin(referralLists.get(position).getContest_Id(), 1);
                } else {
                    setAutoRenewEasyJoin(referralLists.get(position).getContest_Id(), 0);
                }

            }
        });

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                openUserTicketDailog(position);
            }
        });

    }

    private void openUserTicketDailog(int position) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dailog_price);

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

        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        Button btnSet = dialog.findViewById(R.id.btnSet);
        RecyclerView rvContestPrice = dialog.findViewById(R.id.rvContestPrice);

        //  ArrayList<AutoRenewModel.Price> tempPriceList = new ArrayList<>();
        //  tempPriceList.addAll(priceLists);
        for (int i = 0; i < referralLists.get(position).getContest_price().size(); i++) {
            for (int j = 0; j < priceLists.size(); j++) {
                if (referralLists.get(position).getContest_price().get(i) == priceLists.get(j).getPrice()) {
                    priceLists.get(j).setSelected(true);
                }
            }
        }
        AutoJoinPriceAdapter itemListDataAdapter =
                new AutoJoinPriceAdapter(AutoRenewFragment.this, priceLists);
        rvContestPrice.setHasFixedSize(true);
        rvContestPrice.setLayoutManager(new GridLayoutManager(context,
                2));
        rvContestPrice.setAdapter(itemListDataAdapter);
        itemListDataAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                priceLists.get(position).setSelected(!priceLists.get(position).isSelected());
                itemListDataAdapter.notifyItemChanged(position);
            }
        });
        rvContestPrice.setNestedScrollingEnabled(false);

        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* ArrayList<SendAutoRenewRequest> anArray = new ArrayList<>();
                ArrayList<String> amountArray = new ArrayList<>();
                StringBuilder priceIds = new StringBuilder();
                for (AutoRenewModel.Price princeData : tempPriceList) {
                    SendAutoRenewRequest ar = new SendAutoRenewRequest();
                    if (princeData.isSelected()) {
                        priceIds.append(princeData.getPrice()).append(",");
                        ar.setPrice(priceIds.toString());
                        amountArray.add(String.valueOf(princeData.getPrice()));
                        ar.setStatus(0);
                        ar.setPrice(ar.getPrice().substring(0, ar.getPrice().length() - 1));
                        ar.setContest_time(referralLists.get(position).getAutorenew_time());
                        anArray.add(ar);
                    }
                }
*/
                ArrayList<SendAutoRenewRequest> anArray = new ArrayList<>();
                ArrayList<Integer> amountArray = new ArrayList<>();
                ArrayList<Integer> ids = new ArrayList<>();
                StringBuilder priceIds = new StringBuilder();
                for (AutoRenewModel.Price princeData : priceLists) {
                    if (princeData.isSelected()) {
                        amountArray.add(princeData.getPrice());
                        priceIds.append(princeData.getPrice()).append(",");
                    }
                }

                SendAutoRenewRequest ar = new SendAutoRenewRequest();
                if (ar.getPrice().length() > 0) {
                    dialog.dismiss();
                    ar.setPrice(priceIds.toString());
                    ar.setContest_time(referralLists.get(position).getAutorenew_time());
                    ar.setStatus(0);
                    ar.setPrice(ar.getPrice().substring(0, ar.getPrice().length() - 1));
                    anArray.add(ar);
                    setAutoRenewEasyJoin(anArray);
                    getAutoRenewEasyJoin();
                } else {
                    Utils.showToast(context, "please choose contests");
                }

               /*

                for (int i = 0; i < priceLists.size(); i++) {
                    if (priceLists.get(i).isSelected()) {
                        for (int j = 0; j < amountArray.size(); j++) {
                            if (priceLists.get(i).getPrice() == (amountArray.get(j))) {
                                priceIds.append(priceLists.get(i).getPrice()).append(",");
                                ar.setPrice(priceIds.toString());
                            }
                        }
                        ar.setContest_time(referralLists.get(position).getAutorenew_time());
                        ar.setStatus(0);
                        ar.setPrice(ar.getPrice().substring(0, ar.getPrice().length() - 1));
                        anArray.add(ar);
                    }

                }
*/

            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                getAutoRenewEasyJoin();
            }
        });
        dialog.show();
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
                getAutoRenewEasyJoin();
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
                LogHelper.d("sucess :", responseData);
                Gson gson = new Gson();
                AutoRenewModel nm = gson.fromJson(responseData, AutoRenewModel.class);
                if (nm.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    referralLists.clear();
                    referralLists.addAll(nm.getAutorenewTable());
                    priceLists.clear();
                    priceLists.addAll(nm.getPrice());
                    adapter.notifyDataSetChanged();

                }

            }

            @Override
            public void failure(String responseData) {

                Log.d("TAG", "success: " + responseData);
            }
        });
    }

    private void setAutoRenewEasyJoin(int id, int status) {
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        String request = "";
        Gson gson = new Gson();
        try {
            jsonObject.put("id", id + "");
            jsonObject.put("status", status + "");
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
        Call<ResponseBody> call = APIClient.getInstance().setAutoRenewEasyJoinStatus(sessionUtil.getToken(), sessionUtil.getId(), request);
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, true, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                getAutoRenewEasyJoin();
            }

            @Override
            public void failure(String responseData) {

            }
        });
    }

}