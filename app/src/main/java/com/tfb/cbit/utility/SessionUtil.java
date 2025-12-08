package com.tfb.cbit.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SessionUtil {

    private SharedPreferences preferences;
    private Context context;

    private static final String TOKEN = "token";
    private static final String ISLOGIN = "isLogin";
    private static final String FCMTOKEN = "fcmtoken";
    private static final String ID = "id";
    private static final String LAST_UPDATED_DATE = "last_updated_date";
    private static final String FNAME = "fname";
    private static final String MNAME = "mname";
    private static final String LNAME = "lname";
    private static final String NAME = "name";
    private static final String EMAIL = "email";
    private static final String MOB = "mob";
    private static final String PASS = "pass";
    private static final String PHOTO = "photo";
    private static final String MYCODE = "mycode";
    private static final String AMOUNT = "amount";
    private static final String REFERRALCODE = "ReferralCode";
    private static final String WINNINGAMOUNT = "wamount";
    private static final String SETNOTIFICATION = "setNotification";
    private static final String USERNAME = "username";
    private static final String IS_PAN_VERIFY = "verify_pan";
    private static final String IS_BANK_VERIFY = "verify_bank";
    private static final String IS_EMAIL_VERIFY = "verify_email";
    private static final String SOCIAL_ID = "social_id";
    private static final String SOCIAL_TYPE = "social_type";
    private static final String CREDENTIA_CURRENCY = "ccAmount";
    private static final String WALLET_AUTH = "WalletAuth";
    private static final String MobileLogin = "MobileLogin";
    private static final String ISAutoPilot = "AutoPilot";
    private static final String ISRedeem = "isRedeem";
    private static final String ISAutoStart = "ISAutoStart";
    private static final String ISAD = "Isad";


    public SessionUtil(Context context) {
        this.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setFCMToken(String token) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(FCMTOKEN, token);
        editor.apply();
    }

    public String getFcmtoken() {
        return preferences.getString(FCMTOKEN, "");
    }

    public boolean getAutoStart() {
        return preferences.getBoolean(ISAutoStart, false);
    }

    public void setAutoStart(boolean token) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(ISAutoStart, token);
        editor.apply();
    }

    public void setData(String token, String fname, String mname, String lname, String email, String mob, String pass,
                        String photo, String myCode, String id, String amount, String wamonut,
                        int notification, String username, int pan_verify, int bank_verify, int verify_email, String socialId,
                        String socialType, String referralcode, String ccAmount, String WalletAuth, boolean mobilelogin, int AutoPilot, int isRedeem) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(TOKEN, "Bearer " + token);
        editor.putBoolean(ISLOGIN, true);
        editor.putString(FNAME, fname);
        editor.putString(MNAME, mname);
        editor.putString(LNAME, lname);
        editor.putString(NAME, fname + " " + lname);
        editor.putString(EMAIL, email);
        editor.putString(MOB, mob);
        editor.putString(PASS, pass);
        editor.putString(PHOTO, photo);
        editor.putString(MYCODE, myCode);
        editor.putString(ID, id);
        editor.putString(AMOUNT, amount);
        editor.putString(WINNINGAMOUNT, wamonut);
        editor.putString(USERNAME, username);
        editor.putInt(SETNOTIFICATION, notification);
        editor.putInt(IS_PAN_VERIFY, pan_verify);
        editor.putInt(IS_BANK_VERIFY, bank_verify);
        editor.putInt(IS_EMAIL_VERIFY, verify_email);
        editor.putString(SOCIAL_ID, socialId);
        editor.putString(SOCIAL_TYPE, socialType);
        editor.putString(REFERRALCODE, referralcode);
        editor.putString(CREDENTIA_CURRENCY, ccAmount);
        editor.putString(WALLET_AUTH, WalletAuth);
        editor.putBoolean(MobileLogin, mobilelogin);
        editor.putInt(ISAutoPilot, AutoPilot);
        editor.putInt(ISRedeem, isRedeem);
        editor.apply();
    }


    public boolean isLogin() {
        return preferences.getBoolean(ISLOGIN, false);
    }

    public boolean MobileLogin() {
        return preferences.getBoolean(MobileLogin, false);
    }

    public String getToken() {
        return preferences.getString(TOKEN, "");
    }

    public String getId() {
        return preferences.getString(ID, "");
    }

    public void setLastUpdatedDate(String date) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(LAST_UPDATED_DATE, date);
        editor.apply();
    }

    public int getISAd() {
        return preferences.getInt(ISAD, 0);
    }

    public void setISAd(int isautopilot) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(ISAD, isautopilot);
        editor.apply();
    }

    public String getLastUpdatedDate() {
        return preferences.getString(LAST_UPDATED_DATE, "");
    }

    public int getISAutoPilot() {
        return preferences.getInt(ISAutoPilot, 0);
    }

    public int getISRedeem() {
        return preferences.getInt(ISRedeem, 0);
    }

    public String getName() {
        return preferences.getString(NAME, "");
    }

    public void setName(String name) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(NAME, name);
        editor.apply();
    }

    public void setISAutoPilot(int isautopilot) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(ISAutoPilot, isautopilot);
        editor.apply();
    }

    public void setISRedeem(int isredeem) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(ISRedeem, isredeem);
        editor.apply();
    }

    public String getEmail() {
        return preferences.getString(EMAIL, "");
    }

    public String getReferralcode() {
        return preferences.getString(REFERRALCODE, "");
    }


    public String getMob() {
        return preferences.getString(MOB, "");
    }

    public String getPhoto() {
        return preferences.getString(PHOTO, "");
    }

    public String getMycode() {
        return preferences.getString(MYCODE, "");
    }

    public String getAmount() {
        return preferences.getString(AMOUNT, "0");
    }

    public String getWAmount() {
        return preferences.getString(WINNINGAMOUNT, "0");
    }

    public void setAmount(String amount) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(AMOUNT, amount);
        editor.apply();
    }

    public String getCredentiaCurrency() {
        return preferences.getString(CREDENTIA_CURRENCY, "0");
    }

    public void setCredentiaCurrency(String ccamount) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(CREDENTIA_CURRENCY, ccamount);
        editor.apply();
    }

    public String getWalletAuth() {
        return preferences.getString(WALLET_AUTH, "0");
    }

    public void setWalletAuth(String WalletAuth) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(WALLET_AUTH, WalletAuth);
        editor.apply();
    }

    public void setWAmount(String wamount) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(WINNINGAMOUNT, wamount);
        editor.apply();
    }

    public void setPhoto(String photo) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PHOTO, photo);
        editor.apply();
    }

    public String getPass() {
        return preferences.getString(PASS, "");
    }

    public void setPass(String pass) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PASS, pass);
        editor.apply();
    }

    public void setNotification(int notification) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(SETNOTIFICATION, notification);
        editor.apply();
    }

    public int getNotification() {
        return preferences.getInt(SETNOTIFICATION, 1);
    }

    public String getUserName() {
        return preferences.getString(USERNAME, "");
    }

    public int getPANVerify() {
        return preferences.getInt(IS_PAN_VERIFY, 0);
    }

    public void setPANVerify(int panVerify) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(IS_PAN_VERIFY, panVerify);
        editor.apply();
    }

    public int getBANKVerify() {
        return preferences.getInt(IS_BANK_VERIFY, 0);
    }

    public String getSocialId() {
        return preferences.getString(SOCIAL_ID, "");
    }

    public String getSocialType() {
        return preferences.getString(SOCIAL_TYPE, "");
    }

    public void setEmailVerify(int emailVerify) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(IS_EMAIL_VERIFY, emailVerify);
        editor.apply();
    }

    public int getEmailVerify() {
        return preferences.getInt(IS_EMAIL_VERIFY, 0);
    }

    public void logOut() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

}
