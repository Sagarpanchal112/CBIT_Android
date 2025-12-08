package com.tfb.cbit.fragments;


import static com.tfb.cbit.utility.Utils.SOCKET_URI;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.tfb.cbit.CBit;
import com.tfb.cbit.R;
import com.tfb.cbit.activities.AddMoneyActivity;
import com.tfb.cbit.activities.CCPassbookActivity;
import com.tfb.cbit.activities.KYCVerificationActivity;
import com.tfb.cbit.activities.PassbookActivity;
import com.tfb.cbit.activities.RedeemActivity;
import com.tfb.cbit.activities.TransferWalletActivity;
import com.tfb.cbit.adapter.JAssestAdapter;
import com.tfb.cbit.api.APIClient;
import com.tfb.cbit.api.ApiCallback;
import com.tfb.cbit.api.NewApiCall;
import com.tfb.cbit.databinding.FragmentAboutBinding;
import com.tfb.cbit.databinding.FragmentWalletBinding;
import com.tfb.cbit.event.UpdateMyContestEvent;
import com.tfb.cbit.event.UpdateWallet;
import com.tfb.cbit.models.CheckPanStatus;
import com.tfb.cbit.models.JAssetsModel;
import com.tfb.cbit.models.JHitsTotalAmountModel;
import com.tfb.cbit.models.ReferralComissionModel;
import com.tfb.cbit.utility.CustomDialog;
import com.tfb.cbit.utility.PrintLog;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.SocketUtils;
import com.tfb.cbit.utility.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;


public class WalletFragment extends Fragment {

    private static final String TAG = "WalletFragment";

    private SessionUtil sessionUtil;
    private Context context;
    private List<JAssetsModel.RedemedList> allRequestArrayList = new ArrayList<>();

    public WalletFragment() {
        // Required empty public constructor
    }

    public static WalletFragment newInstance() {
        WalletFragment fragment = new WalletFragment();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private static final String SOCKET_PATH = "/socket.io";
    public static final String EVENT_KYC_UPDATE = "onKycUpdate";

    private FragmentWalletBinding binding;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentWalletBinding.bind(LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_wallet, container, false));
        View view = binding.getRoot();
        sessionUtil = new SessionUtil(context);

        binding.tvAppliedCc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUserTicketDailog();
            }
        });
        binding.tvRedemedCc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUserTicketDailog();
            }
        });
        //  checkPanStatus();
        binding.tvKycClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, KYCVerificationActivity.class);
                intent.putExtra(KYCVerificationActivity.IS_REDEEM_CLICK, true);
                startActivity(intent);
            }
        });
        CBit.getSocketUtils().connect();

        return view;
    }

    private void getAllWaitingJTicketList() {
        Call<ResponseBody> call = APIClient.getInstance().getJAssets(sessionUtil.getToken(), sessionUtil.getId());
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Log.d(TAG, "success: " + responseData);
                Gson gson = new Gson();
                allRequestArrayList.clear();
                JAssetsModel jAssetsModel = gson.fromJson(responseData, JAssetsModel.class);
                binding.tvAppliedCc.setText(jAssetsModel.getContents().getAppliedCC()+" Points");
                binding.tvRedemedCc.setText(jAssetsModel.getContents().getRedemedCC()+" Points");
                allRequestArrayList.addAll(jAssetsModel.getContents().getRedemedLists());

            }

            @Override
            public void failure(String responseData) {
                Log.d(TAG, "success: " + responseData);
            }
        });
    }

    private void checkPanStatus() {
        Call<ResponseBody> call = APIClient
                .getInstance()
                .checkPanStatus(sessionUtil.getToken(), sessionUtil.getId());
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Gson gson = new Gson();
                Log.i("sucess :", responseData);
                CheckPanStatus jAssetsModel = gson.fromJson(responseData, CheckPanStatus.class);

                try {
                    JSONObject jObj = new JSONObject(responseData);
                    if (jObj.getInt("statusCode") == Utils.StandardStatusCodes.SUCCESS) {
                        JSONObject jcontent = jObj.getJSONObject("content");
                        sessionUtil.setPANVerify(jcontent.getInt("verify_pan"));
                        // sessionUtil.setCredentiaCurrency(jAssetsModel.getContent().getUserWalletWithSckoet().get(0).getCcAmount() + "");
                        //  tvCredetiaCC.setText("CC " + Utils.getwithoutCurrencyFormat(sessionUtil.getCredentiaCurrency()));
                        binding. tvCredetiaCC.setText(Utils.getwithoutCurrencyFormat(sessionUtil.getCredentiaCurrency())+" Points");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(String responseData) {

            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionUtil = new SessionUtil(context);

        binding.btnAddMoney.setOnClickListener(view1 -> {
            startActivity(new Intent(context, AddMoneyActivity.class));
        });
        binding.btnRedeem.setOnClickListener(view1 -> {
            btnRedeemClick();
        });
        binding.btnPassbook.setOnClickListener(view1 -> {
            btnPassbookClick();
        });
        binding.btnCCPassBook.setOnClickListener(view1 -> {
            btnCCPassbookClick();
        });
        binding.btnUBTWallet.setOnClickListener(view1 -> {
            btnUBTWalletClick();
        });
        binding.btnWBTWallet.setOnClickListener(view1 -> {
            btnWBTWalletClick();
        });
    }

    protected void btnRedeemClick() {
        Log.i("click", "==>btnRedeem");
        CustomDialog customDialog = new CustomDialog();
        if (sessionUtil.getPANVerify() == Utils.PAN_NOT_ADD) {
            customDialog.showDialogTwoButton(context, "", "You need to complete your KYC for redeem process.", "Ok", "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    Intent intent = new Intent(context, KYCVerificationActivity.class);
                    intent.putExtra(KYCVerificationActivity.IS_REDEEM_CLICK, true);
                    startActivity(intent);
                }
            }, null);
        } else if (sessionUtil.getPANVerify() == Utils.PAN_PENDING) {
            customDialog.showDialogOneButton(context, "", "KYC Verification Pending.", "Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        } else if (sessionUtil.getPANVerify() == Utils.PAN_REJECTED) {
            customDialog.showDialogOneButton(context, "", "KYC Verification Rejected, Add New Details.", "Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    Intent intent = new Intent(context, KYCVerificationActivity.class);
                    intent.putExtra(KYCVerificationActivity.IS_REDEEM_CLICK, true);
                    startActivity(intent);
                }
            });
        } else {
            startActivity(new Intent(context, RedeemActivity.class));
        }
    }

    protected void btnPassbookClick() {
        startActivity(new Intent(context, PassbookActivity.class));
    }

     protected void btnCCPassbookClick() {
        startActivity(new Intent(context, CCPassbookActivity.class));
    }

    protected void btnUBTWalletClick() {
        Intent intent = new Intent(context, TransferWalletActivity.class);
        intent.putExtra(TransferWalletActivity.TRANSFER_TYPE, "0");
        startActivity(intent);
    }

    protected void btnWBTWalletClick() {
        Intent intent = new Intent(context, TransferWalletActivity.class);
        intent.putExtra(TransferWalletActivity.TRANSFER_TYPE, "1");
        // intent.putExtra(TransferWalletActivity.TRANSFER_TYPE, "0");
        startActivity(intent);
    }

    private void openUserTicketDailog() {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dailog_jasset);

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
        RecyclerView rvUserList = dialog.findViewById(R.id.rv_Jasset);
        JAssestAdapter itemListDataAdapter =
                new JAssestAdapter(context, allRequestArrayList);
        rvUserList.setHasFixedSize(true);
        rvUserList.setLayoutManager(new LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL, false));
        rvUserList.setAdapter(itemListDataAdapter);

        rvUserList.setNestedScrollingEnabled(false);

        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        binding. tvBalance.setText(String.valueOf(Utils.getCurrencyFormat(sessionUtil.getAmount())));
        binding. tvWinningBalance.setText(String.valueOf(Utils.getCurrencyFormat(sessionUtil.getWAmount())));
        Log.d(TAG, "wallertauth: " + sessionUtil.getWalletAuth());
        if (sessionUtil.getWalletAuth().equalsIgnoreCase("1")) {
            binding. btnUBTWallet.setVisibility(View.VISIBLE);
            binding. btnWBTWallet.setVisibility(View.VISIBLE);
        } else {
            binding. btnUBTWallet.setVisibility(View.GONE);
            binding. btnWBTWallet.setVisibility(View.GONE);
        }
        try {
            double amount = Double.parseDouble(sessionUtil.getAmount());
            double wAmount = Double.parseDouble(sessionUtil.getWAmount());
            binding. tvTotalBalance.setText(Utils.getCurrencyFormat(String.valueOf((amount + wAmount))));
        } catch (Exception e) {
            e.printStackTrace();
        }
        getAllWaitingJTicketList();
        getJhitsTotalAmount();
        getReferralCommitionTotalAmount();
    }

    private void getJhitsTotalAmount() {
        Call<ResponseBody> call = APIClient.getInstance().getJhitsTotalAmount(sessionUtil.getToken(), sessionUtil.getId());
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Log.d(TAG, "success: " + responseData);
                Gson gson = new Gson();
                JHitsTotalAmountModel jAssetsModel = gson.fromJson(responseData, JHitsTotalAmountModel.class);
                if (jAssetsModel.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    binding.  tvJHitsAmount.setText(Utils.INDIAN_RUPEES + (jAssetsModel.getContent().getTotalsum()));
                }

            }

            @Override
            public void failure(String responseData) {
                Log.d(TAG, "success: " + responseData);
            }
        });
    }

    private void getReferralCommitionTotalAmount() {
        Call<ResponseBody> call = APIClient.getInstance().getReferralCommitionTotalAmount(sessionUtil.getToken(), sessionUtil.getId());
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Log.d(TAG, "success: " + responseData);
                Gson gson = new Gson();
                ReferralComissionModel jAssetsModel = gson.fromJson(responseData, ReferralComissionModel.class);
                if (jAssetsModel.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    binding. tvTds.setText((jAssetsModel.getContent().getTds()));
                    binding.  tvEarning.setText(Utils.getCurrencyFormat(jAssetsModel.getContent().getEarning()));
                }
            }

            @Override
            public void failure(String responseData) {
                Log.d(TAG, "success: " + responseData);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(UpdateWallet updateWallet) {
        binding. tvBalance.setText(String.valueOf(Utils.getCurrencyFormat(sessionUtil.getAmount())));
        binding. tvWinningBalance.setText(String.valueOf(Utils.getCurrencyFormat(sessionUtil.getWAmount())));
        try {
            double amount = Double.parseDouble(sessionUtil.getAmount());
            double wAmount = Double.parseDouble(sessionUtil.getWAmount());
            binding.  tvTotalBalance.setText(Utils.getCurrencyFormat(String.valueOf((amount + wAmount))));
        } catch (Exception e) {
            e.printStackTrace();
        }
        checkPanStatus();
    }



    @Subscribe
    public void onUpdateMyContestEvent(UpdateMyContestEvent updateMyContestEvent) {
        getAllWaitingJTicketList();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onDestroyView() {

        super.onDestroyView();

    }

}
