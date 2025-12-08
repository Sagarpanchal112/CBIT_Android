package com.tfb.cbit.fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.tfb.cbit.CBit;
import com.tfb.cbit.R;
import com.tfb.cbit.api.APIClient;
import com.tfb.cbit.api.ApiCallback;
import com.tfb.cbit.api.NewApiCall;
import com.tfb.cbit.databinding.ActivityAboutUsBinding;
import com.tfb.cbit.databinding.FragmentMyJTicketBinding;
import com.tfb.cbit.interfaces.OnItemClickJTicket;
import com.tfb.cbit.models.ApplyJticketModel;
import com.tfb.cbit.models.MyJTicket.MyJTcktModel;
import com.tfb.cbit.utility.LogHelper;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class MyJTicketFragment extends AppCompatActivity implements OnItemClickJTicket {

    private static final String TAG = "RedeemJTicketFragment";

    private Context context;
    private SessionUtil sessionUtil;
    private NewApiCall newApiCall;
    public static boolean isFirstTimeRedeemd = true;
    public static boolean isFirstTimeApplid = true;
    public static boolean isHit = true;


    public MyJTicketFragment() {
        // Required empty public constructor
    }


    public static MyJTicketFragment newInstance() {
        MyJTicketFragment fragment = new MyJTicketFragment();
        return fragment;
    }

    private FragmentMyJTicketBinding binding;


    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentMyJTicketBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;

        sessionUtil = new SessionUtil(context);
        newApiCall = new NewApiCall();
        TabsPagerAdapter tabsPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        binding.dashboardViewPager.setAdapter(tabsPagerAdapter);
        binding.dashboardTabLayout.setupWithViewPager(binding.dashboardViewPager);
        getUserJTicket();

        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        isFirstTimeRedeemd = true;

    }

    private class TabsPagerAdapter extends FragmentStatePagerAdapter {
        private int NUM_ITEMS = 3;
        private FragmentManager mFragmentManager;

        public TabsPagerAdapter(FragmentManager fm) {
            super(fm);
            mFragmentManager = fm;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = MyJTicketTabFragment.newInstance("0");
                    break;
                case 1:
                    fragment = MyJTicketTabFragment.newInstance("1");
                    break;
                case 2:
                    fragment = MyJTicketTabFragment.newInstance("2");
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            String title = "";
            switch (position) {
                case 0:
                    title = "Redeemed";
                    break;
                case 1:
                    title = "Applied ";
                    break;
                case 2:
                    title = "Received";
                    break;
            }
            return title;
        }
    }

    private void getUserJTicket() {
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        String request = "";
        try {
            jsonObject.put("status", "2");
            jsonObject.put("start", "0");
            jsonObject.put("limit", "10");
            jsonObject.put("filterAscDesc", "ASC");
            jsonObject.put("filterTicketName", "");
            jsonObject.put("filterByDate", "");
            jsonObject.put("sortByApproch", 0);


            request = jsonObject.toString();
            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, getString(R.string.crypt_pass));
            data = request.getBytes("UTF-8");
            request = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Call<ResponseBody> call = APIClient.getInstance().getUserJTicket(sessionUtil.getToken(), sessionUtil.getId(), request);
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                LogHelper.d(TAG, "success: " + responseData);

                Gson gson = new Gson();
                MyJTcktModel myjtcktmodel = gson.fromJson(responseData, MyJTcktModel.class);
                if (myjtcktmodel.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                }

                binding.tvAPDValue.setText((myjtcktmodel.getContent().getADP()));
                if (myjtcktmodel.getContent().getTAP() == null)
                    binding.tvTAPValue.setText(" 0.00"+" Points");
                else binding.tvTAPValue.setText("" + myjtcktmodel.getContent().getTAP() + " Points");
                if (myjtcktmodel.getContent().getBAP() == null)
                    binding.tvBAPValue.setText("0.00 Points");
                else binding.tvBAPValue.setText("" + myjtcktmodel.getContent().getBAP() + " Points");

                binding.DayOfJoin.setText("Your APD Cycle refreshes on " + myjtcktmodel.getContent().getDayOfJoin() + "th Of every month");

            }

            @Override
            public void failure(String responseData) {
                Log.d(TAG, "success: " + responseData);
            }
        });
    }

    private void ApplyNow(String ID) {
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        String request = "";
        try {
            jsonObject.put("id", ID);
            Log.d(TAG, "AddJRedeem: " + ID);
            request = jsonObject.toString();
            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, getString(R.string.crypt_pass));
            data = request.getBytes("UTF-8");
            request = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        final Call<ResponseBody> call = APIClient.getInstance().ApplyJtciket(sessionUtil.getToken(), sessionUtil.getId(), request);

        newApiCall.makeApiCall(context, true, call, new ApiCallback() {
            @Override
            public void success(String responseData) {

                Log.d(TAG, "success: " + responseData);
                Gson gson = new Gson();
                ApplyJticketModel loginRegisterModel = gson.fromJson(responseData, ApplyJticketModel.class);
                if (loginRegisterModel.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    Utils.showToast(MyJTicketFragment.this, loginRegisterModel.getMessage());

                    //  getUserJTicket();

                } else {
                    Log.d(TAG, "fail: " + responseData);
                    Utils.showToast(MyJTicketFragment.this, loginRegisterModel.getMessage());
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


    @Override
    public void onItemClick(String Price, String ID, String Type, int wait_no) {
        ApplyNow(ID);
    }
}
