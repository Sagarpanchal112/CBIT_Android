package com.tfb.cbit;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.multidex.MultiDexApplication;

import com.tfb.cbit.views.ThemeHelper;


public class DarkThemeApplication extends MultiDexApplication {
    public static boolean isInfo = false;

    public void onCreate() {
        super.onCreate();
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        String themePref = sharedPreferences.getString("themePref", ThemeHelper.LIGHT_MODE);
        ThemeHelper.applyTheme(themePref);
    }
}