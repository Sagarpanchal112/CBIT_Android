package com.tfb.cbit.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.tfb.cbit.CBit;
import com.tfb.cbit.R;
import com.tfb.cbit.adapter.AccountAdapter;
import com.tfb.cbit.api.APIClient;
import com.tfb.cbit.api.ApiCallback;
import com.tfb.cbit.api.NewApiCall;
import com.tfb.cbit.databases.DatabaseHandler;
import com.tfb.cbit.databinding.ActivityAboutUsBinding;
import com.tfb.cbit.databinding.ActivityProfileBinding;
import com.tfb.cbit.event.AddBankEvent;
import com.tfb.cbit.event.UnAuthorizedEvent;
import com.tfb.cbit.event.UpdateProfileEvent;
import com.tfb.cbit.models.ReferalCriteriaChart;
import com.tfb.cbit.models.profile.ProfileModel;
import com.tfb.cbit.models.profile_upload.ProfileImageModel;
import com.tfb.cbit.utility.ImageChooser;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class ProfileActivity extends BaseAppCompactActivity implements ImageChooser.OnImageChoosedListener {
    private Context context;
    private SessionUtil sessionUtil;
    private ImageChooser imageChooser;
    private NewApiCall newApiCall;

    private ActivityProfileBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        sessionUtil = new SessionUtil(context);
        newApiCall = new NewApiCall();
        imageChooser = new ImageChooser(this, this);
        binding.rvAccountList.setLayoutManager(new LinearLayoutManager(context));
        binding.rvAccountList.setNestedScrollingEnabled(false);
        binding.nsScrollview.setVisibility(View.GONE);
        binding.pbProgress.setVisibility(View.VISIBLE);
        getProfile();
        getUserQrCode();
        getreferalCriteriachart("T");
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);


        binding.ivQrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, QrCodeActivity.class));

            }
        });
        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        binding.frameProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  imageChooser.takeAndCropImage(1,1,true);
            }
        });
        binding.tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        binding.linearPan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearPanClick();
            }
        });
        binding.tvLinkbank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, LinkBankAccountActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    protected void linearPanClick() {
        if (binding.ivEdit.getVisibility() == View.VISIBLE) {
            Intent intent = new Intent(context, KYCVerificationActivity.class);
            intent.putExtra(KYCVerificationActivity.IS_REDEEM_CLICK, true);
            startActivity(intent);
        }
    }

    private void getreferalCriteriachart(String sortType) {
        JSONObject jsonObject = new JSONObject();
        String request = "";
        try {
            byte[] data;
            jsonObject.put("sortType", sortType);
            jsonObject.put("StartDate", "");
            jsonObject.put("EndDate", "");
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
        Log.i("Token", "==>" + sessionUtil.getToken());
        Log.i("Id", "==>" + sessionUtil.getId());
        Log.i("request", "==>" + request);
        Call<ResponseBody> call = APIClient.getInstance().referalCriteriaChart(sessionUtil.getToken(), sessionUtil.getId(), request);
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, true, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Gson gson = new Gson();
                ReferalCriteriaChart nm = gson.fromJson(responseData, ReferalCriteriaChart.class);
                if (nm.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    Glide.with(context).load(nm.getContents().getUserReferalImage()).apply(Utils.getUserAvatarReques()).into(binding.ivProfilePic);
                 /*   if (nm.getContents().getUserCriteriaID().equals("Master")) {
                        Glide.with(context).load(R.drawable.master).apply(Utils.getUserAvatarRequestOptionHome()).into(ivProfilePic);
                    } else if (nm.getContents().getUserCriteriaID().equals("Super Master")) {
                        Glide.with(context).load(R.drawable.sm_new).apply(Utils.getUserAvatarRequestOptionHome()).into(ivProfilePic);
                    } else if (nm.getContents().getUserCriteriaID().equals("Top Master")) {
                        Glide.with(context).load(R.drawable.tm_new).apply(Utils.getUserAvatarRequestOptionHome()).into(ivProfilePic);
                    } else if (nm.getContents().getUserCriteriaID().equals("VIP")) {
                        Glide.with(context).load(R.drawable.vip_new).apply(Utils.getUserAvatarRequestOptionHome()).into(ivProfilePic);
                    } else if (nm.getContents().getUserCriteriaID().equals("RD")) {
                        Glide.with(context).load(R.drawable.rd_new).apply(Utils.getUserAvatarRequestOptionHome()).into(ivProfilePic);
                    } else {
                        Glide.with(context).load(sessionUtil.getPhoto()).apply(Utils.getUserAvatarRequestOptionHome()).into(ivProfilePic);
                    }*/

                }


            }

            @Override
            public void failure(String responseData) {

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        imageChooser.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        imageChooser.onRequestPermissionsResult(requestCode, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void getProfile() {
        Call<ResponseBody> call = APIClient
                .getInstance()
                .profile(sessionUtil.getToken(), sessionUtil.getId());
        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Gson gson = new Gson();
                ProfileModel pm = gson.fromJson(responseData, ProfileModel.class);
                if (pm.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    binding.nsScrollview.setVisibility(View.VISIBLE);
                    binding.pbProgress.setVisibility(View.GONE);
                    //   Glide.with(context).load(pm.getContent().getProfile_image()).apply(Utils.getUserAvatarRequestOption()).into(ivProfilePic);
                    binding.tvUserName.setText(pm.getContent().getUserName());
                    binding.tvFullName.setText(pm.getContent().getFirstName() + " " + pm.getContent().getMiddelName() + " " + pm.getContent().getLastName());
                    binding.tvEmail.setText(pm.getContent().getEmail());
                    binding.tvMob.setText(pm.getContent().getMobileNo());
                    try {
                        if (pm.getContent().getPanNumber().isEmpty()) {
                            binding.tvPan.setText("XXXXXXX");
                        } else {
                            binding.tvPan.setText(pm.getContent().getPanNumber());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    if (pm.getContent().getBankAccount().size() == 0) {
                        binding.tvLinkbank.setText(getString(R.string.linkbankaccount));
                    } else {
                        binding.tvLinkbank.setText(getString(R.string.addanotheraccount));
                    }

                    binding.rvAccountList.setAdapter(new AccountAdapter(context, pm.getContent().getBankAccount()));

                    sessionUtil.setEmailVerify(pm.getContent().getVerify_email());

                    if (sessionUtil.getEmailVerify() == 0) {
                        binding.ivEmailValidate.setVisibility(View.GONE);
                        binding.tvEmailMsg.setText("Email Verification Pending");
                        binding.tvEmailMsg.setVisibility(View.VISIBLE);
                    } else {
                        binding.ivEmailValidate.setVisibility(View.VISIBLE);
                        binding.tvEmailMsg.setVisibility(View.GONE);
                    }

                    sessionUtil.setPANVerify(pm.getContent().getVerify_pan());
                    if (sessionUtil.getPANVerify() == Utils.PAN_REJECTED) {
                        binding.ivEdit.setVisibility(View.VISIBLE);
                        binding.ivValidate.setVisibility(View.GONE);
                        binding.tvPanMsg.setVisibility(View.VISIBLE);
                        binding.tvPanMsg.setText("KYC Verification Rejected");
                    } else if (sessionUtil.getPANVerify() == Utils.PAN_PENDING) {
                        binding.ivEdit.setVisibility(View.GONE);
                        binding.ivValidate.setVisibility(View.GONE);
                        binding.tvPanMsg.setVisibility(View.VISIBLE);
                        binding.tvPanMsg.setText("KYC Verification Pending");
                    } else if (sessionUtil.getPANVerify() == Utils.PAN_VERIFIED) {
                        binding.ivEdit.setVisibility(View.GONE);
                        binding.ivValidate.setVisibility(View.VISIBLE);
                        binding.tvPanMsg.setVisibility(View.GONE);
                    } else {
                        binding.ivEdit.setVisibility(View.VISIBLE);
                        binding.ivValidate.setVisibility(View.GONE);
                        binding.tvPanMsg.setVisibility(View.GONE);
                    }


                }
            }

            @Override
            public void failure(String responseData) {

            }
        });
    }

    private void getUserQrCode() {
        Call<ResponseBody> call = APIClient
                .getInstance()
                .getUserQrCode(sessionUtil.getToken(), sessionUtil.getId());
        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Gson gson = new Gson();
                Log.i("sucess :", responseData);

                try {
                    JSONObject jObj = new JSONObject(responseData);
                    if (jObj.getInt("statusCode") == Utils.StandardStatusCodes.SUCCESS) {
                        JSONObject jcontent = jObj.getJSONObject("content");
                        Glide.with(context).load(jcontent.getString("path")).apply(Utils.getUserAvatarReques()).into(binding.ivQrcode);
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

    @Subscribe
    public void onAddBankEvent(AddBankEvent addBankEvent) {
        getProfile();
    }

    @Override
    public void onImageChoosedAndCropped(Uri croppedImageUri) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.RESOURCE);
        requestOptions.transform(new CircleCrop());
        Glide.with(context)
                .load(croppedImageUri)
                .apply(requestOptions)
                .into(binding.ivProfilePic);
        if (croppedImageUri != null) {
            File photoFile = new File(croppedImageUri.getPath());
            RequestBody requestPhotoFile = RequestBody.create(MediaType.parse("multipart/form-data"), photoFile);
            MultipartBody.Part photoBody = MultipartBody.Part.createFormData("profile_image", photoFile.getName(), requestPhotoFile);

            Call<ResponseBody> call = APIClient.getInstance().uploadProfileImage(sessionUtil.getToken(), sessionUtil.getId(), photoBody);

            newApiCall.makeApiCall(context, true, call, new ApiCallback() {
                @Override
                public void success(String responseData) {
                    Gson gson = new Gson();
                    ProfileImageModel pim = gson.fromJson(responseData, ProfileImageModel.class);
                    if (pim.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                        sessionUtil.setPhoto(pim.getContent().getProfileImage());
                        EventBus.getDefault().post(new UpdateProfileEvent());
                    } else {
                        Utils.showToast(context, pim.getMessage());
                    }
                }

                @Override
                public void failure(String responseData) {

                }
            });
        }

    }

    @Override
    public void onImageChooseAndCropCanceled() {

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
}
