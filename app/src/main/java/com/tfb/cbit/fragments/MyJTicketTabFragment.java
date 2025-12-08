package com.tfb.cbit.fragments;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.tfb.cbit.CBit;
import com.tfb.cbit.R;
import com.tfb.cbit.adapter.ApprochListAdapter;
import com.tfb.cbit.adapter.JfilterNameAdapter;
import com.tfb.cbit.adapter.MyJTicktAdapter;
import com.tfb.cbit.api.APIClient;
import com.tfb.cbit.api.ApiCallback;
import com.tfb.cbit.api.NewApiCall;
import com.tfb.cbit.databinding.FragmentAboutBinding;
import com.tfb.cbit.databinding.FragmentTabMyJticketBinding;
import com.tfb.cbit.interfaces.OnItemClickJTicket;
import com.tfb.cbit.models.ApplyJticketModel;
import com.tfb.cbit.models.CommonRes;
import com.tfb.cbit.models.JticketFilter.JTicketName;
import com.tfb.cbit.models.JticketFilter.Names;
import com.tfb.cbit.models.MyJTicket.ApproachList;
import com.tfb.cbit.models.MyJTicket.Contest;
import com.tfb.cbit.models.MyJTicket.MyJTcktModel;
import com.tfb.cbit.utility.CustomDialog;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;

import static com.tfb.cbit.fragments.MyJTicketFragment.isFirstTimeApplid;
import static com.tfb.cbit.fragments.MyJTicketFragment.isFirstTimeRedeemd;
import static com.tfb.cbit.fragments.MyJTicketFragment.isHit;

public class MyJTicketTabFragment extends Fragment implements OnItemClickJTicket, MyJTicktAdapter.OnLoadMoreListener {

    private static final String TAG = "MyJTicketTabFragment";

    public BottomSheetDialog mBottomSheetFilterDialogCall;
    private Context context;
    private SessionUtil sessionUtil;
    private NewApiCall newApiCall;
    private List<Contest> MyJTcktList = new ArrayList<>();
    private MyJTicktAdapter myJTicktAdapter;
    public List<Names> jNameFilter = new ArrayList<>();
    public String jType = "";
    public String selectedId = "", sortByApproch = "0";
    public String ASC_DESC = "ASC";
    private Handler mHandler;

    public MyJTicketTabFragment() {
        // Required empty public constructor
    }

    public static MyJTicketTabFragment newInstance(String type) {
        MyJTicketTabFragment fragment = new MyJTicketTabFragment();
        Bundle args = new Bundle();
        args.putString("jType", type);
        fragment.setArguments(args);
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
    private FragmentTabMyJticketBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentTabMyJticketBinding.bind(LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_tab_my_jticket, container, false));
        View view = binding.getRoot();
        return view;
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            jType = getArguments().getString("jType");
        }
        sessionUtil = new SessionUtil(context);
        newApiCall = new NewApiCall();
        binding. tvAPDValue.setVisibility(View.GONE);
        mHandler = new Handler(Looper.getMainLooper());

        LinearLayoutManager llm = new LinearLayoutManager(context);
        binding. rvPackagesList.setLayoutManager(llm);

        myJTicktAdapter = new MyJTicktAdapter(context, this, binding.rvPackagesList);
        binding. rvPackagesList.setAdapter(myJTicktAdapter);
        myJTicktAdapter.setOnItemClickListener(MyJTicketTabFragment.this);
        myJTicktAdapter.setOnItemApprochClickListener(new MyJTicktAdapter.OnApprochItemClickListener() {
            @Override
            public void onItemClick(int position, String value) {
                approachNegotiate(value, position);
            }
        });
        myJTicktAdapter.setOnItemApprochListClickListener(new MyJTicktAdapter.OnApprochListClickListener() {
            @Override
            public void onItemClick(List<ApproachList> approachList) {
                openDailog(approachList);
            }
        });
        binding. rvPackagesList.getRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager llManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (dy > 0 && llManager.findLastCompletelyVisibleItemPosition() == (myJTicktAdapter.getItemCount() - 2)) {
                    myJTicktAdapter.showLoading();
                }

            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }
        });

        binding. rvPackagesList.showProgress();
        if (jType.equalsIgnoreCase("0") && isFirstTimeRedeemd) {
            isFirstTimeRedeemd = false;
            final LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.layout_animation_fall_down);
            binding. rvPackagesList.getRecyclerView().setLayoutAnimation(controller);
        } else if (jType.equalsIgnoreCase("0") && !isFirstTimeRedeemd) {
            binding. rvPackagesList.getRecyclerView().setLayoutAnimation(null);
        }
        if (jType.equalsIgnoreCase("1") && isFirstTimeApplid) {
            isFirstTimeApplid = false;
            final LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.layout_animation_fall_down);
            binding. rvPackagesList.getRecyclerView().setLayoutAnimation(controller);
        } else if (jType.equalsIgnoreCase("1") && !isFirstTimeApplid) {
            binding.rvPackagesList.getRecyclerView().setLayoutAnimation(null);
        }
        if (jType.equalsIgnoreCase("2") && isHit) {
            isHit = false;
            final LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.layout_animation_fall_down);
            binding.  rvPackagesList.getRecyclerView().setLayoutAnimation(controller);
        } else if (jType.equalsIgnoreCase("2") && !isHit) {
            binding.  rvPackagesList.getRecyclerView().setLayoutAnimation(null);
        }

        binding. imgFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomFilter();
            }
        });
        binding. rvPackagesList.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (jType.equalsIgnoreCase("0")) {
                    getUserJTicket(false, "0", ASC_DESC, selectedId,sortByApproch);
                } else if (jType.equalsIgnoreCase("1")) {
                    getUserJTicket(false, "1", ASC_DESC, selectedId,sortByApproch);
                } else if (jType.equalsIgnoreCase("2")) {
                    getUserJTicket(false, "2", ASC_DESC, selectedId,sortByApproch);
                }
            }
        });

    }

    public String request = "";

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
        });  RecyclerView rvApproch = dialog.findViewById(R.id.rvApproch);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        rvApproch.setLayoutManager(layoutManager);

        ApprochListAdapter productAdapter = new ApprochListAdapter(context, approachList);
        rvApproch.setAdapter(productAdapter);
        productAdapter.setOnItemClickListener(new ApprochListAdapter.OnApprochItemClickListener() {
            @Override
            public void onItemClick(int position, String value) {
                approachNegotiate(value, position);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void getUserJTicket(boolean isLoadMore, String status, String asc, String ticketName, String sortByApproch) {
        if (!isLoadMore) {
            JSONObject jsonObject = new JSONObject();
            byte[] data;
            try {
                jsonObject.put("status", status);
                jsonObject.put("start", "0");
                jsonObject.put("limit", "10");
                jsonObject.put("filterAscDesc", asc);
                jsonObject.put("filterTicketName", ticketName);
                jsonObject.put("filterByDate", "");
                jsonObject.put("sortByApproch", sortByApproch);
                request = jsonObject.toString();
                Log.i("send request", "==>" + request);
                request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, getString(R.string.crypt_pass));
                data = request.getBytes("UTF-8");
                request = Base64.encodeToString(data, Base64.DEFAULT);

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        Call<ResponseBody> call = APIClient.getInstance().getUserJTicket(sessionUtil.getToken(), sessionUtil.getId(), request);
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                binding.  rvPackagesList.showRecycler();
                Gson gson = new Gson();
                MyJTcktModel myjtcktmodel = gson.fromJson(responseData, MyJTcktModel.class);
                if (myjtcktmodel.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    if (!isLoadMore) {
                        myJTicktAdapter.addAllClass(myjtcktmodel.getContent().getContest());
                        myJTicktAdapter.notifyDataSetChanged();
                    } else {
                        myJTicktAdapter.dismissLoading();
                        myJTicktAdapter.addItemMore(myjtcktmodel.getContent().getContest());
                        myJTicktAdapter.setMore(true);
                    }

                }
                gson = new Gson();
                String json = gson.toJson(myjtcktmodel.getContent().getContest());
             //   tvAPDValue.setText("Average Play Per Day (APD) : " + Utils.getCurrencyFormat(myjtcktmodel.getContent().getADP()));
                binding.  tvAPDValue.setText("Average Play Per Day (APD) : " +Utils.INDIAN_RUPEES+(myjtcktmodel.getContent().getADP()));
                binding.  DayOfJoin.setText("Your APD Cycle refreshes on " + myjtcktmodel.getContent().getDayOfJoin() + "th Of every month");

            }

            @Override
            public void failure(String responseData) {
                Log.d(TAG, "success: " + responseData);
            }
        });
    }

    private void getUserJTicketName(String status) {

        JSONObject jsonObject = new JSONObject();
        byte[] data;
        try {
            jsonObject.put("status", status);
            request = jsonObject.toString();
            Log.i("send request", "==>" + request);
            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, getString(R.string.crypt_pass));
            data = request.getBytes("UTF-8");
            request = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


        Call<ResponseBody> call = APIClient.getInstance().getUserJTicketName(sessionUtil.getToken(), sessionUtil.getId(), request);
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Log.d(TAG, "success: " + responseData);
                Gson gson = new Gson();
                JTicketName jTicketName = gson.fromJson(responseData, JTicketName.class);
                if (jTicketName.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    jNameFilter.addAll(jTicketName.getContent().getNames());
                }
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

        final Call<ResponseBody> call = APIClient.getInstance()
                .ApplyJtciket(sessionUtil.getToken(), sessionUtil.getId(), request);

        newApiCall.makeApiCall(context, true, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Gson gson = new Gson();
                ApplyJticketModel loginRegisterModel = gson.fromJson(responseData, ApplyJticketModel.class);
                if (loginRegisterModel.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    // Utils.showToast(context, loginRegisterModel.getMessage());
                    if (jType.equalsIgnoreCase("0")) {
                        getUserJTicket(false, "0", ASC_DESC, selectedId,sortByApproch);
                    } else if (jType.equalsIgnoreCase("1")) {
                        getUserJTicket(false, "1", ASC_DESC, selectedId,sortByApproch);
                    } else if (jType.equalsIgnoreCase("2")) {
                        getUserJTicket(false, "2", ASC_DESC, selectedId,sortByApproch);
                    }
                    showalert(loginRegisterModel.getMessage());
                    //    getUserJTicket("0");

                } else {
                    Utils.showToast(context, loginRegisterModel.getMessage());
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
    public void onResume() {
        super.onResume();
        if (jType.equalsIgnoreCase("0")) {
            getUserJTicket(false, "0", ASC_DESC, selectedId, sortByApproch);
            getUserJTicketName("0");
        } else if (jType.equalsIgnoreCase("1")) {
            getUserJTicket(false, "1", ASC_DESC, selectedId, sortByApproch);
            getUserJTicketName("1");
        } else if (jType.equalsIgnoreCase("2")) {
            getUserJTicket(false, "2", ASC_DESC, selectedId, sortByApproch);
            getUserJTicketName("2");
        }
    }

    public void bottomFilter() {
        View view = getLayoutInflater().inflate(R.layout.bottom_filter, null);
        mBottomSheetFilterDialogCall = new BottomSheetDialog(getActivity(), R.style.CustomBottomSheetDialogTheme);
        mBottomSheetFilterDialogCall.setContentView(view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBottomSheetFilterDialogCall.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        ImageView imgShort = mBottomSheetFilterDialogCall.findViewById(R.id.imgShort);
        AppCompatCheckBox tvEO = mBottomSheetFilterDialogCall.findViewById(R.id.tvEO);
        tvEO.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sortByApproch = "1";

                } else {
                    sortByApproch = "0";
                }
            }
        });

        if (ASC_DESC.equalsIgnoreCase("ASC")) {
            imgShort.setImageResource(R.drawable.ic_arrow_up);
        } else {

            imgShort.setImageResource(R.drawable.ic_arrow_down);
        }

        LinearLayout linName = mBottomSheetFilterDialogCall.findViewById(R.id.linName);
        linName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ASC_DESC.equalsIgnoreCase("ASC")) {
                    ASC_DESC = "DESC";
                    imgShort.setImageResource(R.drawable.ic_arrow_down);
                } else {
                    imgShort.setImageResource(R.drawable.ic_arrow_up);
                    ASC_DESC = "ASC";
                }

            }
        });
        AppCompatTextView txtClear = mBottomSheetFilterDialogCall.findViewById(R.id.txtClear);
        txtClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ASC_DESC = "ASC";
                selectedId = "";
                if (jType.equalsIgnoreCase("0")) {
                    getUserJTicket(false, "0", ASC_DESC, selectedId,sortByApproch);
                    myJTicktAdapter.notifyDataSetChanged();
                    mBottomSheetFilterDialogCall.dismiss();
                } else if (jType.equalsIgnoreCase("1")) {
                    getUserJTicket(false, "1", ASC_DESC, selectedId,sortByApproch);
                    myJTicktAdapter.notifyDataSetChanged();
                    mBottomSheetFilterDialogCall.dismiss();
                } else if (jType.equalsIgnoreCase("2")) {
                    getUserJTicket(false, "2", ASC_DESC, selectedId,sortByApproch);
                    myJTicktAdapter.notifyDataSetChanged();
                    mBottomSheetFilterDialogCall.dismiss();
                }
            }
        });
        AppCompatTextView txtApplay = mBottomSheetFilterDialogCall.findViewById(R.id.txtApplay);
        txtApplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (jType.equalsIgnoreCase("0")) {
                    getUserJTicket(false, "0", ASC_DESC, selectedId,sortByApproch);
                } else if (jType.equalsIgnoreCase("1")) {
                    getUserJTicket(false, "1", ASC_DESC, selectedId,sortByApproch);
                } else if (jType.equalsIgnoreCase("2")) {
                    getUserJTicket(false, "2", ASC_DESC, selectedId,sortByApproch);
                }

                mBottomSheetFilterDialogCall.dismiss();
            }
        });
        RecyclerView rvCultures = mBottomSheetFilterDialogCall.findViewById(R.id.rvName);
        rvCultures.setLayoutManager(new

                LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        //getLocation();
        JfilterNameAdapter adapter = new JfilterNameAdapter(getContext(), jNameFilter);
        rvCultures.setAdapter(adapter);
        adapter.setOnItemClickListener(new JfilterNameAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                selectedId = String.valueOf(jNameFilter.get(position).getId());
            }
        });


        mBottomSheetFilterDialogCall.show();
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

    @Override
    public void onItemClick(String Price, String ID, String Type, int wait_no) {
        ApplyNow(ID);
    }

    @Override
    public void onLoadMore() {
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        try {
            if (jType.equalsIgnoreCase("0")) {
                jsonObject.put("status", "0");
            } else if (jType.equalsIgnoreCase("1")) {
                jsonObject.put("status", "1");
            } else if (jType.equalsIgnoreCase("2")) {
                jsonObject.put("status", "2");
            }
            jsonObject.put("start", myJTicktAdapter.getItemCount());
            jsonObject.put("limit", "10");
            jsonObject.put("filterAscDesc", ASC_DESC);
            jsonObject.put("filterTicketName", selectedId);
            jsonObject.put("filterByDate", "");


            request = jsonObject.toString();
            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, getString(R.string.crypt_pass));
            data = request.getBytes("UTF-8");
            request = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        getUserJTicket(true, "", ASC_DESC, selectedId,sortByApproch);

    }

}
