package com.tfb.cbit.fragments;


import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.tfb.cbit.CBit;
import com.tfb.cbit.R;
import com.tfb.cbit.adapter.DefaultJoinAdapter;
import com.tfb.cbit.api.APIClient;
import com.tfb.cbit.api.ApiCallback;
import com.tfb.cbit.api.NewApiCall;
import com.tfb.cbit.databinding.FragmentAboutBinding;
import com.tfb.cbit.databinding.FragmentSettingsBinding;
import com.tfb.cbit.interfaces.OnItemClickListener;
import com.tfb.cbit.models.DefaultJoinTicket;
import com.tfb.cbit.models.notification.setnotification.SetNotificationModel;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;


public class SettingsFragment extends Fragment {


    private SessionUtil sessionUtil;
    private Context context;

    public DefaultJoinAdapter defaultJoinAdapter;
    private List<DefaultJoinTicket.Contest> ticketList = new ArrayList<>();

    String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    int PERMISSION_ALL = 1;

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
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
    private FragmentSettingsBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.bind(LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_settings, container, false));
        View view = binding.getRoot();
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionUtil = new SessionUtil(context);
        binding.  switchNotification.setChecked(sessionUtil.getNotification() == 1);
        binding.  tvVersionName.setText("Version : " + Utils.getVersionName(context));
        LinearLayoutManager llm = new LinearLayoutManager(context);
        binding. rvDefaultJoin.setLayoutManager(llm);
        defaultJoinAdapter = new DefaultJoinAdapter(context, ticketList);
        binding. rvDefaultJoin.setAdapter(defaultJoinAdapter);
        defaultJoinAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if ( binding.chkall.isChecked()) {
                    binding. chkall.setChecked(false);
                } else {

                }
                ticketList.get(position).setSelected(!ticketList.get(position).isSelected());
                defaultJoinAdapter.notifyItemChanged(position);

            }
        });
        getdefaultJoinTicket();

        binding. tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder ticketIds = new StringBuilder();
                for (DefaultJoinTicket.Contest ticket : ticketList) {
                    if (ticket.isSelected()) {
                        ticketIds.append(ticket.getPrice()).append(",");
                    }
                }
                if(ticketIds.length()!=0) {
                    ticketIds = ticketIds.deleteCharAt(ticketIds.length() - 1);
                    getJoinContest(ticketIds.toString());
                }else{
                    getJoinContest("");
                }

            }
        });

        binding.chkall.setOnClickListener(view1 -> {
            chkallClick();
        });
        binding.switchNotification.setOnClickListener(view1 -> {
            switchNotificationClick();
        });

    }
   protected void chkallClick() {
        for (int i = 0; i < ticketList.size(); i++) {
            ticketList.get(i).setSelected( binding.chkall.isChecked());
            defaultJoinAdapter.notifyItemChanged(i);
        }
    }
    protected void switchNotificationClick() {
        setNotification( binding.switchNotification.isChecked() ? 1 : 0);
    }
    private void getJoinContest(String ticketsIds) {
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        String request = "";
        try {
            jsonObject.put("priceId", ticketsIds);
            Log.d("TAG", "getJoinContest: >>" + ticketsIds);
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
        Call<ResponseBody> call = APIClient.getInstance().setUserDefaultTicketPrice(sessionUtil.getToken(), sessionUtil.getId(), request);
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, true, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                getActivity().onBackPressed();
            }

            @Override
            public void failure(String responseData) {

            }
        });
    }


    private void getdefaultJoinTicket() {
        Call<ResponseBody> call = APIClient.getInstance().getdefaultJoinTicket(sessionUtil.getToken(), sessionUtil.getId());
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Log.i("sucess :", responseData);
                Gson gson = new Gson();
                DefaultJoinTicket nm = gson.fromJson(responseData, DefaultJoinTicket.class);
                if (nm.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    ticketList.clear();
                    ticketList.addAll(nm.getContents().getContest());
                    defaultJoinAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void failure(String responseData) {

                Log.d("TAG", "success: " + responseData);
            }
        });
    }



    private void setNotification(int notification) {
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        String request = "";
        try {
            jsonObject.put("setNotification", notification);
            request = jsonObject.toString();
            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, getString(R.string.crypt_pass));
            data = request.getBytes("UTF-8");
            request = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Call<ResponseBody> call = APIClient.getInstance()
                .setNotification(sessionUtil.getToken(), sessionUtil.getId(), request);
        //.setNotification(sessionUtil.getToken(),sessionUtil.getName(),notification);
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, true, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Gson gson = new Gson();
                SetNotificationModel setNotificationModel = gson.fromJson(responseData, SetNotificationModel.class);
                if (setNotificationModel.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    sessionUtil.setNotification(setNotificationModel.getContent().getSetNotification());
                    binding. switchNotification.setChecked(sessionUtil.getNotification() == 1);
                } else {
                    Utils.showToast(context, setNotificationModel.getMessage());
                    binding.  switchNotification.setChecked(sessionUtil.getNotification() == 1);
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
