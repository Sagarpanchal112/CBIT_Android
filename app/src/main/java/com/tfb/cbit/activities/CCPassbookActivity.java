package com.tfb.cbit.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.tfb.cbit.CBit;
import com.tfb.cbit.R;
import com.tfb.cbit.adapter.PassbookAdapter;
import com.tfb.cbit.adapter.PassbookFilterAdapter;
import com.tfb.cbit.api.APIClient;
import com.tfb.cbit.api.ApiCallback;
import com.tfb.cbit.api.NewApiCall;
import com.tfb.cbit.databases.DatabaseHandler;
import com.tfb.cbit.databinding.ActivityAboutUsBinding;
import com.tfb.cbit.databinding.ActivityPassbookBinding;
import com.tfb.cbit.event.UnAuthorizedEvent;
import com.tfb.cbit.models.passbook.Content;
import com.tfb.cbit.models.passbook.PassBookModel;
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

public class CCPassbookActivity extends BaseAppCompactActivity implements PassbookAdapter.OnLoadMoreListener {

    private static final String TAG = "CCPassbookActivity";

    private Context context;
    private List<Content> passBookList = new ArrayList<>();
    private PassbookAdapter passbookAdapter;
    private SessionUtil sessionUtil;
    public BottomSheetDialog mBottomSheetFilterDialogCall;
    public ArrayList<String> dropdownArray = new ArrayList<>();
    public RecyclerView recy_pass;
    public PassbookFilterAdapter passbookFilterAdapter;
    private ActivityPassbookBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPassbookBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        sessionUtil = new SessionUtil(context);
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        LinearLayoutManager llm = new LinearLayoutManager(context);
        binding. toolbarTitle.setText("Points PassBook");

        binding. rvPassbookList.setLayoutManager(llm);
        passbookAdapter = new PassbookAdapter(context, passBookList);
        binding. rvPassbookList.setAdapter(passbookAdapter);
        binding. linCCAmt.setVisibility(View.VISIBLE);
        binding. tvCCBalance.setText(Utils.getComaFormat(sessionUtil.getCredentiaCurrency())+" Points");
        Log.d(TAG, "CC>>>: " + sessionUtil.getCredentiaCurrency());

        binding. linWinblc.setVisibility(View.GONE);
        binding. linUnutilizeblc.setVisibility(View.GONE);
        // tvWinningBalance.setText(Utils.getCurrencyFormat(sessionUtil.getWAmount()));
        binding. tvTotalBalance.setText(Utils.getCurrencyFormat(String.valueOf(Double.parseDouble(sessionUtil.getAmount()) +
                Double.parseDouble(sessionUtil.getWAmount()))));

        binding.  rvPassbookList.showProgress();
        getCCPassBookDetails(false);
        binding.  rvPassbookList.getRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager llManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (dy > 0 && llManager.findLastCompletelyVisibleItemPosition() == (passbookAdapter.getItemCount() - 2)) {
                    passbookAdapter.showLoading();
                }
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // If cannot scroll up anymore (top of the recyclerview) - FAB hides immediately

                }
                super.onScrollStateChanged(recyclerView, newState);

            }
        });
        binding. imgFilter.setVisibility(View.GONE);
        binding.  imgFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomFilter();
            }
        });
        binding.ivBack.setOnClickListener(view -> {
            onBackPressed();
        });
        binding. rvPassbookList.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getCCPassBookDetails(false);
            }
        });
    }

    public void bottomFilter() {
        View view = getLayoutInflater().inflate(R.layout.bottom_passbook_filter, null);
        mBottomSheetFilterDialogCall = new BottomSheetDialog(this, R.style.CustomBottomSheetDialogTheme);
        mBottomSheetFilterDialogCall.setContentView(view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBottomSheetFilterDialogCall.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        recy_pass = view.findViewById(R.id.recy_pass);
        LinearLayoutManager llm = new LinearLayoutManager(context);
        recy_pass.setLayoutManager(llm);
      //  passbookFilterAdapter = new PassbookFilterAdapter(context, dropdownArray,);
        recy_pass.setAdapter(passbookFilterAdapter);
        TextView txtApplay = view.findViewById(R.id.txtApplay);
        txtApplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetFilterDialogCall.dismiss();
                getCCPassBookDetails(false);
            }
        });
        mBottomSheetFilterDialogCall.show();
    }

    public String request = "";

    private void getCCPassBookDetails(boolean isLoadMore) {
        if (!isLoadMore) {
            JSONObject jsonObject = new JSONObject();
            byte[] data;
            try {
                jsonObject.put("start", "0");
                jsonObject.put("limit", "10");
                request = jsonObject.toString();
                LogHelper.d("Request Data", request);
                request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, getString(R.string.crypt_pass));
                data = request.getBytes(StandardCharsets.UTF_8);
                request = Base64.encodeToString(data, Base64.DEFAULT);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Call<ResponseBody> call = APIClient.getInstance().getCCPassbook(sessionUtil.getToken(), sessionUtil.getId(), request);
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                binding.  tvCCBalance.setText( Utils.getwithoutCurrencyFormat(sessionUtil.getCredentiaCurrency())+" Points");
//                tvBalance.setText("CC " + sessionUtil.getCredentiaCurrency());
//                tvWinningBalance.setText(Utils.getCurrencyFormat(sessionUtil.getWAmount()));
                binding.   tvTotalBalance.setText(Utils.getCurrencyFormat(String.valueOf(Double.parseDouble(sessionUtil.getAmount()) +
                        Double.parseDouble(sessionUtil.getWAmount()))));
                binding.   rvPassbookList.showRecycler();
                Gson gson = new Gson();
                PassBookModel pm = gson.fromJson(responseData, PassBookModel.class);
                if (pm.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    passBookList.clear();
                    passBookList.addAll(pm.getContent());
                    if (!isLoadMore) {
                        passbookAdapter.addAllClass(pm.getContent());
                        passbookAdapter.notifyDataSetChanged();
                    } else {
                        Log.i("TAG", "isLoadMore ");
                        passbookAdapter.dismissLoading();
                        passbookAdapter.addItemMore(pm.getContent());
                        passbookAdapter.setMore(true);
                    }
                }
                passbookAdapter.notifyDataSetChanged();
            }

            @Override
            public void failure(String responseData) {

            }
        });
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

    @Override
    public void onLoadMore() {
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        try {
            jsonObject.put("start", passbookAdapter.getItemCount() + 1);
            jsonObject.put("limit", "10");
            request = jsonObject.toString();
            LogHelper.d("Request Data", request);
            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, getString(R.string.crypt_pass));
            data = request.getBytes(StandardCharsets.UTF_8);
            request = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        getCCPassBookDetails(true);
    }
}
