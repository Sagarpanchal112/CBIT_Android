package com.tfb.cbit.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.canhub.cropper.CropImageView;
import com.tfb.cbit.R;
import com.tfb.cbit.databinding.ActivityAboutUsBinding;
import com.tfb.cbit.databinding.ActivityCropPhotoBinding;
import com.tfb.cbit.utility.PrintLog;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;


public class CropPhotoActivity extends BaseAppCompactActivity {

    /*
    *  Image Cropping Screen
    * #TheFreeBird
    */
    public static final String ASPECTRATIOX = "aspectratiox";
    public static final String ASPECTRATIOY = "aspectratioy";
    public static final String ISFIXEDASPECTRATIO = "isFixedAspactRatio";
    private ActivityCropPhotoBinding binding;

    private static final String TAG = CropPhotoActivity.class.getName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCropPhotoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Uri imageUri = getIntent().getData();
        if (imageUri == null) {
            finish();
            return;
        }

        Bundle bundle = getIntent().getExtras();
        if(bundle == null){
            finish();
            return;
        }

        binding.cropImageView.setGuidelines(CropImageView.Guidelines.ON);
        binding. cropImageView.setCropShape(CropImageView.CropShape.RECTANGLE);
        binding. cropImageView.setScaleType(CropImageView.ScaleType.FIT_CENTER);
        binding. cropImageView.setAspectRatio(bundle.getInt(ASPECTRATIOX,1), bundle.getInt(ASPECTRATIOY,1));
        binding.cropImageView.setFixedAspectRatio(bundle.getBoolean(ISFIXEDASPECTRATIO,false));
        binding. cropImageView.setAutoZoomEnabled(true);
        binding.  cropImageView.setShowProgressBar(true);
        binding.  cropImageView.setBackgroundColor(Color.BLACK);
        binding.  cropImageView.setImageUriAsync(imageUri);
        binding. cropImageView.setOnCropImageCompleteListener(new CropImageView.OnCropImageCompleteListener() {
            @Override
            public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
                OutputStream os = null;
                Bitmap bitmap = result.getBitmap();
                try {
                    String uuid = UUID.randomUUID().toString();
                    File file = File.createTempFile(uuid, ".jpg", getCacheDir());
                    PrintLog.v(TAG, "Image file: " + file.getAbsolutePath());

                    os = new BufferedOutputStream(new FileOutputStream(file));
                    if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 90, os))
                        throw new IOException("Can't compress photo");
                    os.flush();

                    Intent resultIntent = new Intent();
                    resultIntent.setData(Uri.fromFile(file));
                    setResult(RESULT_OK, resultIntent);

                } catch (IOException e) {
                    onImageCropException(e);
                } finally {
                    try {
                        if (os != null) os.close();
                    } catch (IOException ignored) {
                    }
                }
                finish();
            }
        });

        binding.ivBack.setOnClickListener(view -> {
            finish();
        });
        binding.ivRotate.setOnClickListener(view -> {
            binding.cropImageView.rotateImage(90);
        });
        binding.ivDone.setOnClickListener(view -> {
            binding.cropImageView.getCroppedImage();
        });

    }


    private void onImageCropException(Throwable throwable) {
        throwable.printStackTrace();
        Toast.makeText(this, R.string.error_cropping_photo, Toast.LENGTH_SHORT).show();
        finish();
    }


    @Override
    protected void onDestroy() {

        super.onDestroy();
    }
}
