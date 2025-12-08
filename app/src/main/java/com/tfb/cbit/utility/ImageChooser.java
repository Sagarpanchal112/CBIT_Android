package com.tfb.cbit.utility;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import android.widget.Toast;

import com.canhub.cropper.CropImage;
import com.tfb.cbit.R;
import com.tfb.cbit.activities.CropPhotoActivity;
//import com.theartofdev.edmodo.cropper.CropImage;

public class ImageChooser {

	private static final int REQ_PERMISSION_CAMERA = 0;
	private static final int REQ_PERMISSION_READ_EXTERNAL_STORAGE = 1;

	private static final int REQ_CODE_CROP_PHOTO = 5049;

	private static final String[] PERMISSION_CAMERA = new String[]{Manifest.permission.CAMERA};

	@SuppressLint("InlinedApi")
	private static final String[] PERMISSION_READ_EXTERNAL_STORAGE = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};

	private final Activity activity;
	private final Fragment fragment;
	private final OnImageChoosedListener onImageChoosedListener;
	private int aspectRatioX = 1;
	private int aspectRatioY = 1;
	private boolean isFixedRatio = false;
	private Uri pendingImageUri;

	public interface OnImageChoosedListener {
		void onImageChoosedAndCropped(Uri croppedImageUri);

		void onImageChooseAndCropCanceled();
	}

	public ImageChooser(Activity activity, OnImageChoosedListener onImageChoosedListener) {
		this(activity, null, onImageChoosedListener);
	}

	public ImageChooser(Activity activity, Fragment fragment, OnImageChoosedListener onImageChoosedListener) {
		this.activity = activity;
		this.fragment = fragment;
		this.onImageChoosedListener = onImageChoosedListener;
	}

	public void takeAndCropImage(int aspectRatioX,int aspectRatioY,boolean isFixedRatio) {
		this.aspectRatioX = aspectRatioX;
		this.aspectRatioY = aspectRatioY;
		this.isFixedRatio = isFixedRatio;
//		if (CropImage.isExplicitCameraPermissionRequired(activity)
//				&& !PermissionUtils.hasSelfPermissions(activity, PERMISSION_CAMERA)) {
//			ActivityCompat.requestPermissions(activity, PERMISSION_CAMERA, REQ_PERMISSION_CAMERA);
//			return;
//		}
		showTakeImageChooser();
	}

	private void showTakeImageChooser() {
//		Intent pickImageChooserIntent = CropImage.getPickImageChooserIntent(activity, activity.getString(R.string.choose_image_source), false,true);
//		if (fragment == null) {
//			activity.startActivityForResult(pickImageChooserIntent, CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE);
//		} else {
//			fragment.startActivityForResult(pickImageChooserIntent, CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE);
//		}
	}

	private void checkReadPermission(Uri imageUri) {
//		if (CropImage.isReadExternalStoragePermissionsRequired(activity, imageUri)
//				&& !PermissionUtils.hasSelfPermissions(activity, PERMISSION_READ_EXTERNAL_STORAGE)) {
//			pendingImageUri = imageUri;
//			ActivityCompat.requestPermissions(activity, PERMISSION_READ_EXTERNAL_STORAGE, REQ_PERMISSION_READ_EXTERNAL_STORAGE);
//			return;
//		}
//		showCropImageActivity(imageUri);
	}

	public void showCropImageActivity(Uri imageUri) {
		Intent cropImageIntent = new Intent(null, imageUri, activity, CropPhotoActivity.class);
		cropImageIntent.putExtra(CropPhotoActivity.ASPECTRATIOX,aspectRatioX);
		cropImageIntent.putExtra(CropPhotoActivity.ASPECTRATIOY,aspectRatioY);
		cropImageIntent.putExtra(CropPhotoActivity.ISFIXEDASPECTRATIO,isFixedRatio);
		if (fragment == null) {
			activity.startActivityForResult(cropImageIntent, REQ_CODE_CROP_PHOTO);
		} else {
			fragment.startActivityForResult(cropImageIntent, REQ_CODE_CROP_PHOTO);
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
//			case CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE:
//				if (resultCode == Activity.RESULT_OK) {
//					checkReadPermission(CropImage.getPickImageResultUri(activity, data));
//				} else {
//					onImageChoosedListener.onImageChooseAndCropCanceled();
//				}
//				break;
			case REQ_CODE_CROP_PHOTO:
				if (resultCode == Activity.RESULT_OK) {
					onImageChoosedListener.onImageChoosedAndCropped(data.getData());
				} else {
					onImageChoosedListener.onImageChooseAndCropCanceled();
				}
				break;
		}
	}

	public void onRequestPermissionsResult(int requestCode, @NonNull int[] grantResults) {
		switch (requestCode) {
			case REQ_PERMISSION_CAMERA:
				if (PermissionUtils.verifyPermissions(grantResults)) {
					showTakeImageChooser();
				} else {
					Toast.makeText(activity, R.string.permission_denied_camera, Toast.LENGTH_SHORT).show();
					onImageChoosedListener.onImageChoosedAndCropped(null);
				}
				break;
			case REQ_PERMISSION_READ_EXTERNAL_STORAGE:
				if (PermissionUtils.verifyPermissions(grantResults)) {
					showCropImageActivity(pendingImageUri);
				} else {
					Toast.makeText(activity, R.string.permission_denied_read_storage, Toast.LENGTH_SHORT).show();
					onImageChoosedListener.onImageChoosedAndCropped(null);
				}
				pendingImageUri = null;
				break;
		}
	}

}