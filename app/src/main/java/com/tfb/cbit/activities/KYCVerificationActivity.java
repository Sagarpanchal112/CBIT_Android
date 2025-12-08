package com.tfb.cbit.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tfb.cbit.R;
import com.tfb.cbit.api.APIClient;
import com.tfb.cbit.api.ApiCallback;
import com.tfb.cbit.api.NewApiCall;
import com.tfb.cbit.databases.DatabaseHandler;
import com.tfb.cbit.databinding.ActivityAboutUsBinding;
import com.tfb.cbit.databinding.ActivityKycverificationBinding;
import com.tfb.cbit.event.UnAuthorizedEvent;
import com.tfb.cbit.models.kycverification.KYCVerificationModel;
import com.tfb.cbit.utility.CustomDialog;
import com.tfb.cbit.utility.ImageChooser;
import com.tfb.cbit.utility.MyValidator;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class KYCVerificationActivity extends BaseAppCompactActivity implements ImageChooser.OnImageChoosedListener {


    private Context context;
    private ImageChooser imageChooser;
    private Uri photoUri = null;
    private SessionUtil sessionUtil;
    public static final String IS_REDEEM_CLICK = "redeem_click";
    private boolean isRedeemClick = false;
    private ActivityKycverificationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityKycverificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        imageChooser = new ImageChooser(this, this);
        sessionUtil = new SessionUtil(context);
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isRedeemClick = bundle.getBoolean(IS_REDEEM_CLICK, false);
            if (isRedeemClick) {
                binding.tvSkip.setVisibility(View.GONE);
            }
        }
        binding.ivBack.setOnClickListener(view -> {
            backPressed();
        });
        binding.tvSkip.setOnClickListener(view -> {
            backPressed();
        });
        binding.frmPanPhoto.setOnClickListener(view -> {
            imageChooser.takeAndCropImage(1, 1, false);
        });
        binding.linearDOB.setOnClickListener(view -> {
            linearDOBClick();
        });
        binding.btnSubmit.setOnClickListener(view -> {
            if (isValidForm()) {
                updatekyc();
            }
        });

    }

    public void backPressed() {
        if (isRedeemClick) {
            super.onBackPressed();
        } else {
            Intent intent = new Intent(context, TutorialActivity.class);
            intent.putExtra(TutorialActivity.IS_REGISTER, true);
            startActivity(intent);
            finish();
            /*Intent intent = new Intent(context, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();*/
        }
    }


    protected void linearDOBClick() {
        Calendar newCalendar = Calendar.getInstance();
        DatePickerDialog startDatePickerDialog = new DatePickerDialog(this, R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar userAge = new GregorianCalendar(year, monthOfYear, dayOfMonth);
                Calendar minAdultAge = new GregorianCalendar();
                minAdultAge.add(Calendar.YEAR, -18);
                if (minAdultAge.before(userAge)) {
                    CustomDialog customDialog = new CustomDialog();
                    customDialog.showDialogOneButton(context, getString(R.string.error),
                            "You must be 18 years and above to play this game.",
                            "Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                } else {
                    SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                    binding.edtDOB.setText(dateFormatter.format(userAge.getTime()));
                }


            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startDatePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        }
        startDatePickerDialog.show();
    }


    private boolean isValidForm() {
        if (!MyValidator.isBlankETError(context, binding.edtPanCard, "Enter Name as per Pan Card", 1, 100)) {
            return false;
        } else if (!MyValidator.isBlankETError(context, binding.edtPanCardNo, "Enter Pan Card Number", 1, 100)) {
            return false;
        } else if (!MyValidator.isBlankETError(context, binding.edtDOB, "Enter Date of Birth", 1, 100)) {
            return false;
        } else if (photoUri == null) {
            Utils.showToast(context, "Select Pancard photo");
            return false;
        } else {
            return true;
        }
    }

    private void updatekyc() {
        RequestBody panname = Utils.createPartFromString(binding.edtPanCard.getText().toString().trim());
        RequestBody panno = Utils.createPartFromString(binding.edtPanCardNo.getText().toString().trim());
        RequestBody dob = Utils.createPartFromString(Utils.getyyyyMMddformat(binding.edtDOB.getText().toString().trim()));
        HashMap<String, RequestBody> map = new HashMap<>();
        map.put("pan_name", panname);
        map.put("pan_number", panno);
        map.put("dob", dob);
        if (photoUri != null) {
            File photoFile = new File(photoUri.getPath());
            RequestBody requestPhotoFile = RequestBody.create(MediaType.parse("multipart/form-data"), photoFile);
            MultipartBody.Part photoBody = MultipartBody.Part.createFormData("pan_image", photoFile.getName(), requestPhotoFile);

            Call<ResponseBody> call = APIClient.getInstance().updateKYC(sessionUtil.getToken(), sessionUtil.getId(), map, photoBody);
            NewApiCall newApiCall = new NewApiCall();
            newApiCall.makeApiCall(context, true, call, new ApiCallback() {
                @Override
                public void success(String responseData) {
                    Gson gson = new Gson();
                    KYCVerificationModel kycVerificationModel = gson.fromJson(responseData, KYCVerificationModel.class);
                    if (kycVerificationModel.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                        sessionUtil.setPANVerify(kycVerificationModel.getContent().getVerifyPan());
                        onBackPressed();
                    } else {
                        Utils.showToast(context, kycVerificationModel.getMessage());
                    }
                }

                @Override
                public void failure(String responseData) {

                }
            });
        }

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

    @Override
    public void onImageChoosedAndCropped(Uri croppedImageUri) {
        binding.ivPanImg.setImageURI(croppedImageUri);
        photoUri = croppedImageUri;
    }

    @Override
    public void onImageChooseAndCropCanceled() {

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();

        if (v != null &&
                (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
            float y = ev.getRawY() + v.getTop() - scrcoords[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom())
                Utils.hideKeyboard(this);
        }
        return super.dispatchTouchEvent(ev);
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
