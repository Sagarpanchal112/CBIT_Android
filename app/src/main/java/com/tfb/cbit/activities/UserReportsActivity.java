package com.tfb.cbit.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devlomi.record_view.OnBasketAnimationEnd;
import com.devlomi.record_view.OnRecordClickListener;
import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordPermissionHandler;
import com.devlomi.record_view.RecordView;
import com.google.gson.Gson;
import com.tfb.cbit.CBit;
import com.tfb.cbit.R;
import com.tfb.cbit.adapter.MessageAdapter;
import com.tfb.cbit.adapter.ReportIssueAdapter;
import com.tfb.cbit.api.APIClient;
import com.tfb.cbit.api.ApiCallback;
import com.tfb.cbit.api.NewApiCall;
import com.tfb.cbit.databinding.ActivityAboutUsBinding;
import com.tfb.cbit.databinding.ActivityUserReportsBinding;
import com.tfb.cbit.interfaces.OnItemClickListener;
import com.tfb.cbit.models.ReportIssueModel;
import com.tfb.cbit.models.UserReportModel;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.Utils;
import com.tfb.cbit.views.AudioRecorder;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;

import com.canhub.cropper.CropImageView;

import com.canhub.cropper.CropImage;

import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;

public class UserReportsActivity extends AppCompatActivity {
    private SessionUtil sessionUtil;
    private Context context;

    ReportIssueAdapter reportIssueAdapter;
    MessageAdapter messageAdapter;
    public ArrayList<ReportIssueModel.Content> reportIssueModelArrayList = new ArrayList<>();

    public ArrayList<UserReportModel.Content> messageArrayList = new ArrayList<>();
    public static final int GALLERY = 3;
    private AudioRecorder audioRecorder;
    private File recordFile;
    private ActivityUserReportsBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserReportsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        sessionUtil = new SessionUtil(context);
        LinearLayoutManager llm = new LinearLayoutManager(context, RecyclerView.VERTICAL, true);
        llm.setStackFromEnd(true);
        binding.  rvReportIssue.setLayoutManager(llm);
        messageAdapter = new MessageAdapter(context, messageArrayList);
        binding. rvReportIssue.setAdapter(messageAdapter);
        binding. scrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.scrollView.fullScroll(View.FOCUS_DOWN);
            }
        }, 500);
        LinearLayoutManager llm1 = new LinearLayoutManager(context);
        binding. rvDefaultReport.setLayoutManager(llm1);
        reportIssueAdapter = new ReportIssueAdapter(context, reportIssueModelArrayList);
        binding. rvDefaultReport.setAdapter(reportIssueAdapter);

        reportsListing();
        getUsersIssueList();
        binding.  cInputMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    binding.  cSendMessageBTN.setVisibility(View.VISIBLE);
                    binding.  recordButton.setVisibility(View.GONE);
                } else {
                    binding. cSendMessageBTN.setVisibility(View.GONE);
                    binding. recordButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.ivBack.setOnClickListener(view -> {
            onBackPressed();
        });
        audioRecorder = new AudioRecorder();
        binding. recordButton.setRecordView(binding.recordView);
        binding.  recordButton.setOnRecordClickListener(new OnRecordClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UserReportsActivity.this, "RECORD BUTTON CLICKED", Toast.LENGTH_SHORT).show();
                Log.d("RecordButton", "RECORD BUTTON CLICKED");
            }
        });
        binding.  recordView.setCancelBounds(8);
        binding.recordView.setSmallMicColor(Color.parseColor("#c2185b"));

        //prevent recording under one Second
        binding. recordView.setLessThanSecondAllowed(false);


        binding.  recordView.setSlideToCancelText("Slide To Cancel");


       // binding. recordView.setCustomSounds(R.raw.record_start, R.raw.record_finished, 0);
        // record_button.setListenForRecord(false);

        //ListenForRecord must be false ,otherwise onClick will not be called

        binding. recordView.setOnBasketAnimationEndListener(new OnBasketAnimationEnd() {
            @Override
            public void onAnimationEnd() {
                Log.d("RecordView", "Basket Animation Finished");
            }
        });
        binding. recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {
                //Start Recording..
                binding.  cInputMessage.setVisibility(View.GONE);
                binding.  recordView.setVisibility(View.VISIBLE);
                recordFile = new File(getFilesDir(),  ".mp3");
                try {
                    audioRecorder.start(recordFile.getPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d("RecordView", "onStart");
            }

            @Override
            public void onCancel() {
                //On Swipe To Cancel
                binding. cInputMessage.setVisibility(View.VISIBLE);
                binding.  recordView.setVisibility(View.GONE);
                stopRecording(true);
                Log.d("RecordView", "onCancel");

            }

            @Override
            public void onFinish(long recordTime, boolean limitReached) {
                binding.  cInputMessage.setVisibility(View.VISIBLE);
                binding.  recordView.setVisibility(View.GONE);
                stopRecording(false);
                sendAudioFile(recordFile.getPath());
                Log.d("RecordView", "onFinish");

            }


            @Override
            public void onLessThanSecond() {
                binding.  cInputMessage.setVisibility(View.VISIBLE);
                binding.   recordView.setVisibility(View.GONE);
                stopRecording(true);
                //When the record time is less than One Second
                Log.d("RecordView", "onLessThanSecond");
            }
        });


        reportIssueAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                setUserIssues(reportIssueModelArrayList.get(position).getReport_title());
            }
        });
        binding.  recordView.setRecordPermissionHandler(new RecordPermissionHandler() {
            @Override
            public boolean isPermissionGranted() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    return true;
                }

                boolean recordPermissionAvailable = ContextCompat.checkSelfPermission(UserReportsActivity.this, Manifest.permission.RECORD_AUDIO) == PERMISSION_GRANTED;
                if (recordPermissionAvailable) {
                    return true;
                }


                ActivityCompat.
                        requestPermissions(UserReportsActivity.this,
                                new String[]{Manifest.permission.RECORD_AUDIO},
                                0);

                return false;

            }
        });

        binding. cSendMessageBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!binding.cInputMessage.getText().toString().isEmpty())
                    setUserIssues(binding.cInputMessage.getText().toString());
                //openPopup();
            }
        });

    }
    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(3);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

    private void sendAudioFile(String path) {
        if (path != null) {
            File photoFile = new File(path);
            RequestBody requestPhotoFile = RequestBody.create(MediaType.parse("multipart/form-data"), photoFile);
            MultipartBody.Part photoBody = MultipartBody.Part.createFormData("audio_file", photoFile.getName(), requestPhotoFile);
            Log.i("photoFile :", path);

            Call<ResponseBody> call = APIClient.getInstance().setUserVoiceIssues(sessionUtil.getToken(), sessionUtil.getId(), photoBody);
            NewApiCall newApiCall = new NewApiCall();
            newApiCall.makeApiCall(context, true, call, new ApiCallback() {
                @Override
                public void success(String responseData) {
                    Gson gson = new Gson();
                    Log.i("sucess :", responseData);
                    binding. cInputMessage.setText("");
                    //  Utils.showToast(UserReportsActivity.this, "Report successfully.");
                    getUsersIssueList();

                }

                @Override
                public void failure(String responseData) {

                }
            });
        }
    }

    private void stopRecording(boolean deleteFile) {
        audioRecorder.stop();
        if (recordFile != null && deleteFile) {
            recordFile.delete();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       /* if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(context, data);
            Log.i("profImagePath", "==>" + imageUri.getPath());
            if (CropImage.isReadExternalStoragePermissionsRequired(context, imageUri)) {
                sendImage(imageUri);
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            } else {
                startCropImageActivity(imageUri);
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                sendImage(result.getUri());
                Log.i("profImagePath", "==>" + result.getUri());
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Log.i("profImagePath", "==> error");
            }
        }*/


    }



    private void startCropImageActivity(Uri imageUri) {
      /*  CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setShowCropOverlay(false)
                .setFixAspectRatio(true)
                .setMultiTouchEnabled(true)
                .start(UserReportsActivity.this);
  */
    }

    private void sendImage(Uri imageUri) {
        RequestBody panname = Utils.createPartFromString("Blank");
        HashMap<String, RequestBody> map = new HashMap<>();
        map.put("title ", panname);
        if (imageUri != null) {
            File photoFile = new File(imageUri.getPath().toString());
            RequestBody requestPhotoFile = RequestBody.create(MediaType.parse("multipart/form-data"), photoFile);
            MultipartBody.Part photoBody = MultipartBody.Part.createFormData("report_image", photoFile.getName(), requestPhotoFile);

            Call<ResponseBody> call = APIClient.getInstance().setUserIssues(sessionUtil.getToken(), sessionUtil.getId(), map, photoBody);
            NewApiCall newApiCall = new NewApiCall();
            newApiCall.makeApiCall(context, true, call, new ApiCallback() {
                @Override
                public void success(String responseData) {
                    Gson gson = new Gson();
                    Log.i("sucess :", responseData);
                    binding. cInputMessage.setText("");
                    //  Utils.showToast(UserReportsActivity.this, "Report successfully.");
                    getUsersIssueList();

                }

                @Override
                public void failure(String responseData) {

                }
            });
        }

    }


    private void setUserIssues(String report_title) {
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        String request = "";
        try {
            jsonObject.put("title", report_title);
            request = jsonObject.toString();
            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, getString(R.string.crypt_pass));
            data = request.getBytes("UTF-8");
            request = Base64.encodeToString(data, Base64.DEFAULT);

        } catch (Exception e) {
            e.printStackTrace();
        }
        Call<ResponseBody> call = APIClient
                .getInstance()
                .setUserIssues(sessionUtil.getToken(), sessionUtil.getId(), request);
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Gson gson = new Gson();
                Log.i("sucess :", responseData);
                binding. cInputMessage.setText("");
                //  Utils.showToast(UserReportsActivity.this, "Report successfully.");
                getUsersIssueList();
            }

            @Override
            public void failure(String responseData) {

            }
        });
    }

    private void getUsersIssueList() {
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        String request = "";
        try {
            jsonObject.put("start", "0");
            jsonObject.put("limit", "1000");
            request = jsonObject.toString();
            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, getString(R.string.crypt_pass));
            data = request.getBytes("UTF-8");
            request = Base64.encodeToString(data, Base64.DEFAULT);

        } catch (Exception e) {
            e.printStackTrace();
        }
        Call<ResponseBody> call = APIClient
                .getInstance()
                .getUsersIssueList(sessionUtil.getToken(), sessionUtil.getId(), request);
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Gson gson = new Gson();
                Log.i("Us sucess :", responseData);
                UserReportModel nm = gson.fromJson(responseData, UserReportModel.class);
                if (nm.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    messageArrayList.clear();
                    messageArrayList.addAll(nm.getContent());
                    messageAdapter.notifyDataSetChanged();
                    binding. scrollView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            binding.scrollView.fullScroll(View.FOCUS_DOWN);
                        }
                    }, 500);
                }
            }

            @Override
            public void failure(String responseData) {

            }
        });
    }

    private void reportsListing() {
        Call<ResponseBody> call = APIClient
                .getInstance()
                .reportsListing(sessionUtil.getToken(), sessionUtil.getId());
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Gson gson = new Gson();
                Log.i("sucess :", responseData);
                ReportIssueModel nm = gson.fromJson(responseData, ReportIssueModel.class);
                if (nm.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    reportIssueModelArrayList.clear();
                    reportIssueModelArrayList.addAll(nm.getContent());
                    reportIssueAdapter.notifyDataSetChanged();

                }

            }

            @Override
            public void failure(String responseData) {

            }
        });
    }


    public void attachButton(View view) {

      //  CropImage.startPickImageActivity(UserReportsActivity.this);
     /*   Intent gallery = new Intent(Intent.ACTION_PICK);
        gallery.setType("image/*");
        startActivityForResult(gallery, GALLERY);
   */
    }
}