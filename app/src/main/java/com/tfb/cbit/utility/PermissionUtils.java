package com.tfb.cbit.utility;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.content.PermissionChecker;

public final class PermissionUtils {

	public static boolean verifyPermissions(int... grantResults) {
		if (grantResults.length == 0) return false;
		for (int result : grantResults)
			if (result != PackageManager.PERMISSION_GRANTED) return false;
		return true;
	}

	public static boolean hasSelfPermissions(Context context, String... permissions) {
		for (String permission : permissions)
			if (permissionExists(permission) && !hasSelfPermission(context, permission))
				return false;
		return true;
	}

	private static boolean permissionExists(String permission) {
		Integer minVersion = Manifest.permission.READ_EXTERNAL_STORAGE.equals(permission) ? 16 : null;
		return minVersion == null || Build.VERSION.SDK_INT >= minVersion;
	}

	private static boolean hasSelfPermission(Context context, String permission) {
		try {
			return PermissionChecker.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
		} catch (RuntimeException t) {
			return false;
		}
	}

}