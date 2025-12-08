package com.tfb.cbit.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.tfb.cbit.CBit;
import com.tfb.cbit.R;
import com.tfb.cbit.adapter.ApprochListAdapter;
import com.tfb.cbit.adapter.JTicktWaitingRoomAdapter;
import com.tfb.cbit.adapter.JfilterNameAdapter;
import com.tfb.cbit.api.APIClient;
import com.tfb.cbit.api.ApiCallback;
import com.tfb.cbit.api.NewApiCall;
import com.tfb.cbit.databases.DatabaseHandler;
import com.tfb.cbit.databinding.ActivityAboutUsBinding;
import com.tfb.cbit.databinding.WaitingRoomJticketsBinding;
import com.tfb.cbit.event.UnAuthorizedEvent;
import com.tfb.cbit.interfaces.OnItemOfferApproch;
import com.tfb.cbit.models.CommonRes;
import com.tfb.cbit.models.JWaitingRoom.Contest;
import com.tfb.cbit.models.JWaitingRoom.JWaitingRoomModel;
import com.tfb.cbit.models.MyJTicket.ApproachList;
import com.tfb.cbit.models.approch.UserWaitingModel;
import com.tfb.cbit.models.contestdetails.ContestDetailsModel;
import com.tfb.cbit.utility.LogHelper;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class MyJTicketsWaitingRoomActivity extends BaseAppCompactActivity implements OnItemOfferApproch, JTicktWaitingRoomAdapter.OnLoadMoreListener {
    private String TAG = "MyJTicketsWaitingRoomActivity";

    private JTicktWaitingRoomAdapter jTicktWaitingRoomAdapter;
    private Context context;
    public static final String CONTEST_ID = "contestid";
    public static final String CONTEST_NAME = "contestname";
    public static final String CONTEST_RTIME = "RTime";
    private String TicketId = "";
    private List<Contest> WaitingJticketList = new ArrayList<>();
    private SessionUtil sessionUtil;
    private ContestDetailsModel gtm;
    Bundle bundle;
    private NewApiCall newApiCall;
    RequestOptions requestOptions;
     public String status;

    public BottomSheetDialog mBottomSheetFilterDialogCall;
    private WaitingRoomJticketsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = WaitingRoomJticketsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        requestOptions = new RequestOptions();
        binding.ivBack.setOnClickListener(view -> {
            onBackPressed();
        });
      /*  face = Typeface.createFromAsset(getAssets(),
                "fonts/montserrat_medium.otf");
*/

        bundle = getIntent().getExtras();
        if (bundle == null) {
            finish();
            return;
        }
        sessionUtil = new SessionUtil(context);
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        newApiCall = new NewApiCall();
        binding. toolbarTitle.setText("J Tickets Waiting List");
        TicketId = bundle.getString("Ticket_Id", "");
        status = bundle.getString("status", "");
        binding. rvJWaitingRoom.setLayoutManager(new LinearLayoutManager(context));
        if (binding.rvJWaitingRoom.getRecyclerView().getItemAnimator() != null)
            ((SimpleItemAnimator) binding.rvJWaitingRoom.getRecyclerView().getItemAnimator()).setSupportsChangeAnimations(false);
        jTicktWaitingRoomAdapter = new JTicktWaitingRoomAdapter(context);
        binding. rvJWaitingRoom.setAdapter(jTicktWaitingRoomAdapter);


        binding. rvJWaitingRoom.showProgress();
        binding. rvJWaitingRoom.getRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 &&binding. scroll.getVisibility() == View.GONE || dy < 0 &&
                        binding. scroll.getVisibility() == View.GONE) {
                    binding. scroll.show();
                    binding.  scroll.setEnabled(true);
                }
                LinearLayoutManager llManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (dy > 0 && llManager.findLastCompletelyVisibleItemPosition() == (jTicktWaitingRoomAdapter.getItemCount() - 2)) {
                    jTicktWaitingRoomAdapter.showLoading();
                }

            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // If cannot scroll up anymore (top of the recyclerview) - FAB hides immediately
                    if (!recyclerView.canScrollVertically(-1) &&binding. scroll.getVisibility() == View.VISIBLE) {
                        binding.  scroll.setEnabled(false);
                        binding.  scroll.hide();
                    } else if (!recyclerView.canScrollVertically(1)) {
                        // If cannot scroll down anymore (bottom of the recyclerview)
                        binding.scroll.setEnabled(true);
                        binding. scroll.show();
                        // setEndVisibility(endVisibility);
                    } else {
                        //setDelay(delay);
                    }
                } super.onScrollStateChanged(recyclerView, newState);

            }
        });
        jTicktWaitingRoomAdapter.setOnItemApprochListClickListener(new JTicktWaitingRoomAdapter.OnApprochListClickListener() {
            @Override
            public void onItemClick(List<ApproachList> approachList) {
                openDailog(approachList);

            }
        });
        getWaitingRoomtDetails(false);

        binding.   imgFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                my_jticket=false;
                offer_accept=false;
                bottomFilter();
            }
        });
    }

    public boolean my_jticket=false;
    public boolean offer_accept=false;
    public void scrollTop(View view) {
        binding.rvJWaitingRoom.getRecyclerView().smoothScrollToPosition(0);
    }


    public void bottomFilter() {
        View view = getLayoutInflater().inflate(R.layout.bottom_waiting_filter, null);
        mBottomSheetFilterDialogCall = new BottomSheetDialog(this, R.style.CustomBottomSheetDialogTheme);
        mBottomSheetFilterDialogCall.setContentView(view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBottomSheetFilterDialogCall.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
         AppCompatCheckBox tvmy_jticket = mBottomSheetFilterDialogCall.findViewById(R.id.tvmy_jticket);
        AppCompatCheckBox tv_offer_approch = mBottomSheetFilterDialogCall.findViewById(R.id.tv_offer_approch);
        tvmy_jticket.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    my_jticket = true;

                } else {
                    my_jticket = false;
                }
            }
        });
        tv_offer_approch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    offer_accept = true;

                } else {
                    offer_accept = false;
                }
            }
        });


        AppCompatTextView txtClear = mBottomSheetFilterDialogCall.findViewById(R.id.txtClear);
        txtClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                my_jticket = false;
                offer_accept=false;
                mBottomSheetFilterDialogCall.dismiss();

            }
        });
        AppCompatTextView txtApplay = mBottomSheetFilterDialogCall.findViewById(R.id.txtApplay);
        txtApplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                   getWaitingRoomtDetails(false);


                mBottomSheetFilterDialogCall.dismiss();
            }
        });

        mBottomSheetFilterDialogCall.show();
    }

    private void openDailog(List<ApproachList> approachList) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dailog_offer_negotiate);
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
        ImageView img_close=dialog.findViewById(R.id.img_close);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        RecyclerView rvApproch = dialog.findViewById(R.id.rvApproch);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        rvApproch.setLayoutManager(layoutManager);

        ApprochListAdapter productAdapter = new ApprochListAdapter(context, approachList);
        rvApproch.setAdapter(productAdapter);
        productAdapter.setOnItemClickListener(new ApprochListAdapter.OnApprochItemClickListener() {
            @Override
            public void onItemClick(int position, String value) {
                approachNegotiateUser(value, position);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void approachNegotiateUser(String s, int position) {
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        String request = "";
        try {
            jsonObject.put("j_ticket_user_approach_id", position);
            jsonObject.put("negotiate", s);
            request = jsonObject.toString();
            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, getString(R.string.crypt_pass));
            data = request.getBytes("UTF-8");
            request = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Call<ResponseBody> call = APIClient
                .getInstance().approachNegotiate(sessionUtil.getToken(), sessionUtil.getId(),
                        request);
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, true, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Log.i(TAG, "responseData :-> " + responseData);
                Gson gson = new Gson();
                CommonRes commonRes = gson.fromJson(responseData, CommonRes.class);
                if (commonRes.getStatus() == Utils.StandardStatusCodes.SUCCESS) {
                    getWaitingRoomtDetails(false);
                } else {
                    Utils.showToast(context, commonRes.getMessage());
                }
            }

            @Override
            public void failure(String responseData) {
                Log.i(TAG, "responseData :-> " + responseData);
            }
        });
    }

    private void getWaitingRoomtTickets() {
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        String request1 = null;
        try {
            jsonObject.put("id", TicketId);
            request1 = jsonObject.toString();
            request1 = CBit.getCryptLib().encryptPlainTextWithRandomIV(request1, getString(R.string.crypt_pass));
            data = request1.getBytes("UTF-8");
            request1 = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i("Api call", "==>" + "getWaitingRoomtTickets");
        Call<ResponseBody> call = APIClient.getInstance()
                .getUserWaitingList(sessionUtil.getToken(), sessionUtil.getId(), request1);
        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Log.i(TAG, "responseData :-> " + responseData);
                Gson gson = new Gson();
                UserWaitingModel jWaitingRoomModel = gson.fromJson(responseData, UserWaitingModel.class);
                if (jWaitingRoomModel.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    jTicktWaitingRoomAdapter.addUserWating(jWaitingRoomModel.getContent().getContest());
                    Log.i("Size", "==>" + jWaitingRoomModel.getContent().getContest().size());

                }  //  jTicktWaitingRoomAdapter.notifyDataSetChanged();
            }

            @Override
            public void failure(String responseData) {
                Log.i("Api call", "==>" + "getWaitingRoomtTickets");
                try {
                    jTicktWaitingRoomAdapter.dismissLoading();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }


    public String request = "";

    private void getWaitingRoomtDetails(boolean isLoadMore) {

        if (!isLoadMore) {
            JSONObject jsonObject = new JSONObject();
            byte[] data;
            try {
                jsonObject.put("id", TicketId);
                jsonObject.put("start", "0");
                jsonObject.put("limit", "10");
                jsonObject.put("my_jticket", my_jticket);
                jsonObject.put("offer_accept", offer_accept);
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
        }
        Log.i("User id", "==>" + sessionUtil.getId());

        Call<ResponseBody> call = APIClient.getInstance()
                .getWaitingList(sessionUtil.getToken(), sessionUtil.getId(), request);
        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                binding. rvJWaitingRoom.showRecycler();
                Gson gson = new Gson();
                JWaitingRoomModel jWaitingRoomModel = gson.fromJson(responseData, JWaitingRoomModel.class);
                if (jWaitingRoomModel.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    LogHelper.d(TAG, responseData);
                    // WaitingJticketList.clear();
                    WaitingJticketList.addAll(jWaitingRoomModel.getContent().getContest());
                    if (!isLoadMore) {
                         jTicktWaitingRoomAdapter.addAllClass(jWaitingRoomModel.getContent().getContest());
                        jTicktWaitingRoomAdapter.notifyDataSetChanged();
                        if (WaitingJticketList.size() != 0) {
                            binding.  imgBanner.setVisibility(View.VISIBLE);
                            Glide.with(context)
                                    .load(jWaitingRoomModel.getContent().getContest().get(0).getImage())
                                    .apply(requestOptions)
                                    .into(binding.imgBanner);

                        } else {
                            binding. imgBanner.setVisibility(View.GONE);
                        }
                        binding.  currentWaitingPeriod.setText(!TextUtils.isEmpty(jWaitingRoomModel.getContent().getCurrentWaitingPeriod()) ? jWaitingRoomModel.getContent().getCurrentWaitingPeriod() : "");
                    } else {
                        Log.i("TAG", "isLoadMore ");
                        jTicktWaitingRoomAdapter.dismissLoading();
                        jTicktWaitingRoomAdapter.addItemMore(jWaitingRoomModel.getContent().getContest());
                        jTicktWaitingRoomAdapter.setMore(true);
                    }

                } else {
                    jTicktWaitingRoomAdapter.dismissLoading();

                }
                getWaitingRoomtTickets();


                //  jTicktWaitingRoomAdapter.notifyDataSetChanged();
            }

            @Override
            public void failure(String responseData) {
                try {
                    jTicktWaitingRoomAdapter.dismissLoading();
                } catch (Exception e) {
                    e.printStackTrace();
                }

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

    private void openOfferDailog(int position, String Type) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dailog_offer_approch);

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
        ImageView img_close=dialog.findViewById(R.id.img_close);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });  Button btnCancel = dialog.findViewById(R.id.btnCancel);
        EditText edtOffer = dialog.findViewById(R.id.edtOffer);
        Button btnApproach = dialog.findViewById(R.id.btnApproach);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnApproach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int approch = Integer.parseInt(edtOffer.getText().toString());
                    if (approch > 20) {
                        Utils.showToast(context, "You can make maxmimum 20% approch.");

                    } /*else if (waiting_no < waitingNo) {
                    Utils.showToast(context, "You can make maxmimum 20% approch.");

                }*/ else {
                        applyJtciketApproach(edtOffer.getText().toString(), position, Type);
                        dialog.dismiss();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        dialog.show();
    }

    private void openNegoDailog(int posApprochList, int pos, int id) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dailog_nego_approch);

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
        ImageView img_close=dialog.findViewById(R.id.img_close);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        TextView tv_user = dialog.findViewById(R.id.tv_user);
        tv_user.setText(WaitingJticketList.get(pos).getApproachList().get(posApprochList).getUserName() + "'s Offer " + WaitingJticketList.get(pos).getApproachList().get(posApprochList).getNegotiate() + " %");
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        EditText edtOffer = dialog.findViewById(R.id.edtOffer);
        Button btnApproach = dialog.findViewById(R.id.btnApproach);
        Button btnDeal = dialog.findViewById(R.id.btnDeal);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnApproach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int approch = Integer.parseInt(edtOffer.getText().toString());
                if (approch > 20) {
                    Utils.showToast(context, "You can make maxmimum 20% approch.");

                } /*else if (waiting_no < waitingNo) {
                    Utils.showToast(context, "You can make maxmimum 20% approch.");

                }*/ else {
                    approachNegotiate(edtOffer.getText().toString(), id);
                    dialog.dismiss();

                }
            }
        });
        btnDeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                approachComfirm(id);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void applyJtciketApproach(String s, int position, String type) {
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        String request = "";
        try {
            jsonObject.put("ticketId", WaitingJticketList.get(position).getId());
            jsonObject.put("ticketIdApproach", type);
            jsonObject.put("Approach", s);
            request = jsonObject.toString();
            Log.i("Approch request", "==>" + request);
            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, getString(R.string.crypt_pass));
            data = request.getBytes("UTF-8");
            request = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Call<ResponseBody> call = APIClient
                .getInstance().applyJticketApproach(sessionUtil.getToken(), sessionUtil.getId(),
                        request);
        newApiCall.makeApiCall(MyJTicketsWaitingRoomActivity.this, true, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Log.i(TAG, "responseData :-> " + responseData);
                Gson gson = new Gson();
                CommonRes commonRes = gson.fromJson(responseData, CommonRes.class);
                if (commonRes.getStatus() == Utils.StandardStatusCodes.SUCCESS) {
                    Utils.showToast(context, commonRes.getMessage());
                    jTicktWaitingRoomAdapter.packageList.get(position).setIsApproach("Done");
                    jTicktWaitingRoomAdapter.notifyItemChanged(position);
                    // getWaitingRoomtDetails(false);
                } else {
                    Utils.showToast(context, commonRes.getMessage());
                }
            }

            @Override
            public void failure(String responseData) {
                Log.i(TAG, "responseData :-> " + responseData);
            }
        });
    }

    private void approachComfirm(int position) {
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        String request = "";
        try {
            jsonObject.put("j_ticket_user_approach_id", position);
            request = jsonObject.toString();
            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, getString(R.string.crypt_pass));
            data = request.getBytes("UTF-8");
            request = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Call<ResponseBody> call = APIClient
                .getInstance().approachComfirm(sessionUtil.getToken(), sessionUtil.getId(),
                        request);
        newApiCall.makeApiCall(context, true, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Log.i(TAG, "responseData :-> " + responseData);
                Gson gson = new Gson();
                CommonRes commonRes = gson.fromJson(responseData, CommonRes.class);
                if (commonRes.getStatus() == Utils.StandardStatusCodes.SUCCESS) {
                    Utils.showToast(context, commonRes.getMessage());
                    getWaitingRoomtDetails(false);
                } else {
                    Utils.showToast(context, commonRes.getMessage());
                }
            }

            @Override
            public void failure(String responseData) {
                Log.i(TAG, "responseData :-> " + responseData);
            }
        });
    }

    private void approachNegotiate(String s, int position) {
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        String request = "";
        try {
            jsonObject.put("j_ticket_user_approach_id", position);
            jsonObject.put("negotiate", s);
            request = jsonObject.toString();
            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, getString(R.string.crypt_pass));
            data = request.getBytes("UTF-8");
            request = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Call<ResponseBody> call = APIClient
                .getInstance().UserApproachNegotiate(sessionUtil.getToken(), sessionUtil.getId(),
                        request);
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, true, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Gson gson = new Gson();
                CommonRes commonRes = gson.fromJson(responseData, CommonRes.class);
                if (commonRes.getStatus() == Utils.StandardStatusCodes.SUCCESS) {
                    jTicktWaitingRoomAdapter.packageList.get(position).setIsApproach("Done");
                    Utils.showToast(context, commonRes.getMessage());

                } else {
                    Utils.showToast(context, commonRes.getMessage());
                }
            }

            @Override
            public void failure(String responseData) {
            }
        });
    }

    @Override
    public void onItemClick(int posApprochList, int pos, int id, String Type) {
        if (Type.equalsIgnoreCase("Approch")) {
            openNegoDailog(posApprochList, pos, id);
        } else {
            openOfferDailog(pos, Type);
        }
    }

    @Override
    public void onLoadMore() {
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        try {
            jsonObject.put("id", TicketId);
            jsonObject.put("start", jTicktWaitingRoomAdapter.getItemCount());
            jsonObject.put("limit", "10");
            jsonObject.put("my_jticket", my_jticket);
            jsonObject.put("offer_accept", offer_accept);
            request = jsonObject.toString();
            Log.i("NoLoadMore Request", "==>" + request);
            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, getString(R.string.crypt_pass));
            data = request.getBytes("UTF-8");
            request = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        getWaitingRoomtDetails(true);

       /* Log.i("Waiting list size","==>"+WaitingJticketList.size());
        Log.i("getItemCount","==>"+jTicktWaitingRoomAdapter.getItemCount());
        if (WaitingJticketList.size() >= jTicktWaitingRoomAdapter.getItemCount()) {
            getWaitingRoomtDetails(true);
        } else {
            jTicktWaitingRoomAdapter.dismissLoading();
        }*/
    }
}
