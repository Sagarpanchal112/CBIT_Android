package com.tfb.cbit.utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.Html;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.tfb.cbit.BuildConfig;
import com.tfb.cbit.R;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class Utils {

    //public static final String BASE_URL = "http://192.168.0.189:3500/api/";
    // public static final String BASE_URL = "http://192.168.0.12:3500/api/";
    //  public static final String BASE_URL = "http://68.183.144.102:3500/api/"; //Live API URL - commented
    // public static final String BASE_URL = "http://68.183.144.102:3600/api/"; //Local API URL
    // public static final String BASE_URL = "http://207.154.223.43:3500/api/"; //Live API URL
    //  public static final String SOCKET_URI = "http://207.154.223.43:3500";// live
    //    public static final String BASE_URL = "https://admin.cbitoriginal.com/api/";
    // public static final String BASE_URL = "http://13.127.63.200:3500"; //Live API URL

    //  public static final String BASE_URL = "http://103.86.176.147:3500"; //NEW SERVER API URL
    // public static final String BASE_URL = "http://103.35.165.112:3500"; //Live API URL
    // public static final String BASE_URL = "http://207.154.223.43:3500"; //Live API URL
  // public static final String BASE_URL = "http://207.154.223.43:3600"; //Test API URL
   // public static final String BASE_URL = "http://3.111.215.20:3600"; //Test API URL
    public static final String BASE_URL = "http://13.201.77.144:3600"; //Test API URL // public static final String BASE_URL = "http://207.154.223.43:3700"; //Test API URL
    public static final String API_URL = BASE_URL + "/api/";
    public static final String SOCKET_URI = BASE_URL;
    public static final String INDIAN_RUPEES = "\u00A3";
    //Game
    public static final int EASY = 1; // (4*5)
    public static final int MODERATE = 2; // (4*8)Level
    public static final int PRO = 3; // (4*10)
    //Game Type
    public static final int FLEXIBAR = 0;
    public static final int FIXEDSLOT = 1;
    //Game Status
    public static final String GAME_NOT_START = "notStart";
    public static final String GAME_START = "start";
    public static final String GAME_END = "gameEnd";
    //PassBookType
    public static final String SUBSTRACT = "subtract";
    public static final String ADD = "add";
    //PAN STATUS
    public static final int PAN_NOT_ADD = 0;
    public static final int PAN_VERIFIED = 1;
    public static final int PAN_REJECTED = 2;
    public static final int PAN_PENDING = 3;
    private static RequestOptions userAvatarRequestOptionHome, userAvatarRequestOption;
    public static final String USER_AGENT = BuildConfig.APPLICATION_ID + "/v" + BuildConfig.VERSION_NAME + "(" + BuildConfig.VERSION_CODE + "); "
            + Build.MANUFACTURER + " " + Build.MODEL + "; "
            + "Android " + Build.VERSION.RELEASE + " (API" + Build.VERSION.SDK_INT + ")";
    public static final int NETWORK_TIMEOUT = 10000;

    public static float getProgress(int totalAns, int rightAns) {
        try {
            return (rightAns * 100 / totalAns);
        } catch (ArithmeticException e) {
            e.printStackTrace();
            return 1;
        }
    }

    public static void appendLog(String text) {
        File logFile = new File("sdcard/log.txt");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static RequestOptions getUserAvatarRequestOptionHome() {
        if (userAvatarRequestOptionHome == null) {
            userAvatarRequestOptionHome = new RequestOptions();
            userAvatarRequestOptionHome.diskCacheStrategy(DiskCacheStrategy.RESOURCE);
            userAvatarRequestOptionHome.placeholder(R.drawable.temp_profile_pic_old);
            userAvatarRequestOptionHome.error(R.drawable.temp_profile_pic_old);
            userAvatarRequestOptionHome.transform(new CircleCrop());
        }
        return userAvatarRequestOptionHome;
    }

    public static RequestOptions getUserAvatarReques() {
        if (userAvatarRequestOptionHome == null) {
            userAvatarRequestOptionHome = new RequestOptions();
            userAvatarRequestOptionHome.diskCacheStrategy(DiskCacheStrategy.RESOURCE);
            userAvatarRequestOptionHome.placeholder(R.drawable.ddd);
            userAvatarRequestOptionHome.error(R.drawable.ddd);

        }
        return userAvatarRequestOptionHome;
    }

    public static RequestOptions getUserAvatarRequestOption() {
        if (userAvatarRequestOption == null) {
            userAvatarRequestOption = new RequestOptions();
            userAvatarRequestOption.diskCacheStrategy(DiskCacheStrategy.RESOURCE);
            userAvatarRequestOption.placeholder(R.drawable.temp_profile_pic);
            userAvatarRequestOption.error(R.drawable.temp_profile_pic);
            userAvatarRequestOption.transform(new CircleCrop());
        }
        return userAvatarRequestOption;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void hideKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    @NonNull
    public static RequestBody createPartFromString(String descriptionString) {
        if (descriptionString == null)
            return RequestBody.create(MultipartBody.FORM, "");
        return RequestBody.create(
                MultipartBody.FORM, descriptionString);

    }

    public static int roundFloat(float value) {
        return (int) (value > 0 ? value + 0.5f : value - 0.5f);
    }

    /**
     * Lightweight choice to {@link Math#round(double)}
     */
    public static long roundDouble(double value) {
        return (long) (value > 0 ? value + 0.5 : value - 0.5);
    }

    public interface StandardStatusCodes {
        int SUCCESS = 200;
        int IN_PROGRESS = 201;
        int No_More_Records = 204;
        int BAD_REQUEST = 400;
        int POLICY_NOT_FULL_FILLED = 420;
        int INTERNAL_SERVER_ERROR = 500;
        int NO_DATA_FOUND = 404;
        int CONFLICT = 409;
        int UNAUTHORISE = 401;
        int NOTACCEPTABLE = 406;
        int DUPLICATE_ERROR = 208;
        int BLOCK_USER = 423;
        int Update_USER = 410;
    }

    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public static String getHHMM(String dateStr) {
        String dateFormate = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = sdf.parse(dateStr);
            sdf.applyPattern("hh : mm a");
            dateFormate = sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dateFormate;
    }

    public static String getHHMMStr(String dateStr) {
        String dateFormate = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = sdf.parse(dateStr);
            sdf.applyPattern("HH:mm:ss");
            dateFormate = sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dateFormate;
    }

    public static String getyyyyMMddformat(String dateStr) {
        String dateFormate = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            Date date = sdf.parse(dateStr);
            sdf.applyPattern("yyyy-MM-dd");
            dateFormate = sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dateFormate;
    }

    public static String convertNodeFormat(String date) {
        if (!TextUtils.isEmpty(date)) {
            String parsedDate = date, formattedDate = "";
            Date initDate = null;
            try {

                initDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(date);
                SimpleDateFormat df = new SimpleDateFormat("hh:mm a dd-MM-yyyy");
                parsedDate = df.format(initDate);
                //String dateStr = "Jul 16, 2013 12:08:59 AM";
                // SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss a", Locale.ENGLISH);
                df.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date newdate = df.parse(parsedDate);
                df.setTimeZone(TimeZone.getDefault());
                formattedDate = df.format(newdate);
                System.out.println(formattedDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return formattedDate;

        }
        return "";
    }

    public static String convertNode2Format(String date) {
        if (!TextUtils.isEmpty(date)) {
            String parsedDate = date, formattedDate = "";
            Date initDate = null;
            try {

                initDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(date);
                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm a ");
                parsedDate = df.format(initDate);
                //String dateStr = "Jul 16, 2013 12:08:59 AM";
                // SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss a", Locale.ENGLISH);
                df.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date newdate = df.parse(parsedDate);
                df.setTimeZone(TimeZone.getDefault());
                formattedDate = df.format(newdate);
                System.out.println(formattedDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return formattedDate;

        }
        return "";
    }

    public static String getDate(String inputDateStr) {
        if (!TextUtils.isEmpty(inputDateStr)) {
            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date date = null;
            try {
                date = inputFormat.parse(inputDateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return outputFormat.format(date);
        }
        return "";
    }

    public static String getDateTime(String inputDateStr) {
        if (!TextUtils.isEmpty(inputDateStr)) {
            // DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            DateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss aa");
            DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date date = null;
            try {
                date = inputFormat.parse(inputDateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return outputFormat.format(date);
        }
        return "";
    }

    public static String getOnlyDate(String inputDateStr) {
        if (!TextUtils.isEmpty(inputDateStr)) {
            // DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            DateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss aa");
            DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date date = null;
            try {
                date = inputFormat.parse(inputDateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return outputFormat.format(date);
        }
        return "";
    }

    public static Date getDateTimeFilter(String inputDateStr) {
        if (!TextUtils.isEmpty(inputDateStr)) {
            // DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            DateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss aa");
            DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Date date = null;
            try {
                date = inputFormat.parse(inputDateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return date;
        }
        return null;
    }

    public static String getddMMyyyyformat(String dateStr) {
        String dateFormate = "";
        try {
//             2019-12-23T14: 11: 47.000Z
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = sdf.parse(dateStr);
            sdf.applyPattern("dd-MM-yyyy");
            dateFormate = sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dateFormate;
    }

    public static String getddMMyyyyhhmmaformat(String dateStr) {
        String dateFormate = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = sdf.parse(dateStr);
            sdf.applyPattern("dd-MM-yyyy hh:mm a");
            dateFormate = sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dateFormate;
    }

    public static String get24HoursTime(String timeStr) {
        String dateFormate = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            Date date = sdf.parse(timeStr);
            sdf.applyPattern("HH:mm");
            dateFormate = sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dateFormate;
    }

    public static long convertMillSeconds(String dateTime, String currentTime) {
        long mill = 0;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = sdf.parse(dateTime);
            mill = date.getTime();

            // SimpleDateFormat csdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a", Locale.getDefault());
            //Current Time
            Date cur = sdf.parse(currentTime);
            //Calendar calendar = Calendar.getInstance();
            //mill = mill - calendar.getTime().getTime();
            mill = mill - cur.getTime();
            //Log.e("TAG",mill+"");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mill;
    }

    public static long convertMillSecondsReminder(String dateTime) {
        long mill = 0;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = sdf.parse(dateTime);
            mill = date.getTime();
            mill = mill - (60000); //(12 Sec * 1000 )

            //Date cur = sdf.parse(currentTime);
            // mill = mill - cur.getTime();
            Log.e("TAG_Alarm", mill + "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mill;
    }

    public static long getMillSeconds(String dateTime) {
        long mill = 0;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = sdf.parse(dateTime);
            mill = date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mill;
    }

    public static void expand(final View v) {
        v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LinearLayout.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    // Get SHAKey For FB Login
    public static String getSHAKey(Context context) {
        String SHA_KEY = "";
        PackageInfo info;
        try {

            info = context.getPackageManager().getPackageInfo(
                    context.getApplicationContext().getPackageName(), PackageManager.GET_SIGNATURES);

            Log.i("TAG", context.getApplicationContext().getPackageName());

            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.e("Hash key ", something);
                SHA_KEY = something;
                System.out.println("Hash key " + something);
            }

        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }
        return SHA_KEY;
    }

    public static boolean isToday(int year, int month, int day) {
        //noinspection UnnecessaryLocalVariable
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // Today in milliseconds
        long today = calendar.getTime().getTime();

        // Given day in milliseconds
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);

        long calendarMillis = calendar.getTime().getTime();

        return today == calendarMillis;
    }

    public static boolean isInThePast(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 1);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long now = calendar.getTimeInMillis();

        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);

        long then = calendar.getTimeInMillis();
        return now > then;
    }

    public static int getImageForSlider(int range, int rangeMaxValue) {
        PrintLog.d("TAG", "Range " + range + " max Value " + rangeMaxValue);
        try {
            int rangeGap = (100 / (rangeMaxValue / range));

            if (rangeGap <= 2) {
                return R.drawable.ic_bar25;
            } else if (rangeGap <= 4) {
                return R.drawable.ic_bar30;
            } else if (rangeGap <= 6) {
                return R.drawable.ic_bar35;
            } else if (rangeGap <= 8) {
                return R.drawable.ic_bar40;
            } else if (rangeGap <= 10) {
                return R.drawable.ic_bar45;
            } else if (rangeGap <= 12) {
                return R.drawable.ic_bar50;
            } else if (rangeGap <= 14) {
                return R.drawable.ic_bar55;
            } else if (rangeGap <= 16) {
                return R.drawable.ic_bar60;
            } else if (rangeGap <= 18) {
                return R.drawable.ic_bar65;
            } else if (rangeGap <= 20) {
                return R.drawable.ic_bar70;
            } else if (rangeGap <= 24) {
                return R.drawable.ic_bar80;
            } else if (rangeGap <= 28) {
                return R.drawable.ic_bar90;
            } else if (rangeGap <= 32) {
                return R.drawable.ic_bar100;
            } else if (rangeGap <= 36) {
                return R.drawable.ic_bar110;
            } else if (rangeGap <= 40) {
                return R.drawable.ic_bar120;
            } else if (rangeGap <= 44) {
                return R.drawable.ic_bar130;
            } else if (rangeGap <= 48) {
                return R.drawable.ic_bar140;
            } else if (rangeGap <= 52) {
                return R.drawable.ic_bar150;
            } else if (rangeGap <= 56) {
                return R.drawable.ic_bar175;
            } else if (rangeGap <= 60) {
                return R.drawable.ic_bar200;
            } else {
                return R.drawable.ic_bar250;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return R.drawable.ic_bar25;
        }


    }

    /*public static String priceWithDecimal (Double price) {
        DecimalFormat formatter = new DecimalFormat("###,###,###.00");
        return Utils.INDIAN_RUPEES+formatter.format(price);
    }

    public static String priceWithoutDecimal (Double price) {
        DecimalFormat formatter = new DecimalFormat("###,###,###.##");
        return Utils.INDIAN_RUPEES+formatter.format(price);
    }

    public static String priceToString(String price) {
        try {
            double amount = Double.parseDouble(price);
            String toShow = priceWithoutDecimal(amount);
            if (toShow.indexOf(".") > 0) {
                return priceWithDecimal(amount);
            } else {
                return priceWithoutDecimal(amount);
            }
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }*/

    public static String getCurrencyFormat(String number) {
        try {
            double amount = Double.parseDouble(number);
            if (amount < 1) {
                return Utils.INDIAN_RUPEES + "0.00";
            }
            //DecimalFormat formatter = new DecimalFormat("##,##,###.00");
            DecimalFormat formatter = new DecimalFormat("##,##,###.##");
            return Utils.INDIAN_RUPEES + formatter.format(amount);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    public static String getCurrencySingleFormat(String number) {
        try {
            double amount = Double.parseDouble(number);
            if (amount < 1) {
                return Utils.INDIAN_RUPEES + "0";
            }
            //DecimalFormat formatter = new DecimalFormat("##,##,###.00");
            DecimalFormat formatter = new DecimalFormat("##,##,###");
            return Utils.INDIAN_RUPEES + formatter.format(amount);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    public static String getTodayDate() {
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c);
        return formattedDate;
    }

    public static String getwithoutCurrencyFormat(String number) {
        try {
            double amount = Double.parseDouble(number);
            if (amount < 1) {
                return "0.00";
            }
            // DecimalFormat formatter = new DecimalFormat("##,##,###.00");
            DecimalFormat formatter = new DecimalFormat("##,##,###.##");
            return formatter.format(amount);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    public static String getTwoDecimalFormat(float number) {
        String abc = String.format("%.02f", number);
        return Utils.INDIAN_RUPEES + abc;
    }

    public static String getCurrencyFormatNumber(String number) {
        double amount = Double.parseDouble(number);
        DecimalFormat formatter = new DecimalFormat("##,##,###");
        return Utils.INDIAN_RUPEES + formatter.format(amount);

    }

    public static String getComaFormat(String number) {
        double amount = Double.parseDouble(number);
        DecimalFormat formatter = new DecimalFormat("##,##,###");
        return formatter.format(amount);

    }

    public static void shareJoinCode(Context context, String code) {
        SessionUtil sessionUtil = new SessionUtil(context);
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, sessionUtil.getName() + " invited you to join the private room \n" + "https://" + context.getString(R.string.host_deep_link) +
                context.getString(R.string.pathprefix) + "?url=ashvh.com&code=" + code);

        context.startActivity(Intent.createChooser(sharingIntent, context.getResources().getString(R.string.share)));
//        context.getString(R.string.port)+
    }

    public static void shareReferralCodeOld(Context context) {
        SessionUtil sessionUtil = new SessionUtil(context);
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Hey there , Start Money Trading in the Form of Gaming . Install the ‘Kitty Games‘ Gaming App with my " +
                "referral code - " + "' " + Html.fromHtml("<font color=#dd4b39>" + sessionUtil.getReferralcode() + "</font>") + "' " + "\n # Win-win Gaming # World’s most shortest and easiest game # Responsible Gaming # Digital Money Market # contests starting @ ₹5/- only . Click on this link " + "https://" + context.getString(R.string.host_deep_link2) + " to download the app . Join Now !");
        context.startActivity(Intent.createChooser(sharingIntent, context.getResources().getString(R.string.share)));
    }

    public static void shareReferralCode(Context context) {
        Uri imageUri = null;
        try {
            imageUri = Uri.parse(MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    BitmapFactory.decodeResource(context.getResources(), R.drawable.cbit_image), null, null));
        } catch (NullPointerException e) {
        }

        SessionUtil sessionUtil = new SessionUtil(context);
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("image/*");
        // sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml("Hey there ,<br /> <br />Install the ‘Kitty Games‘ Gaming App with my " +
                "referral code - ") + "" + Html.fromHtml("<font color=#dd4b39><b>*" + sessionUtil.getReferralcode() + "*</b></font>") + "" + Html.fromHtml("<br /><br />• Shortests and easiest games to Win cash prizes \uD83C\uDFC6<br />• Cashback \uD83D\uDCB5 Upto 180% on loosing. <br />• Refer and earn \uD83D\uDCB0commission upto ₹15 lakhs / year *<br /><br />Terms & conditions apply  <br /><br />Hurry up! Download now!- " + context.getString(R.string.host_deep_link2))); /*+ sessionUtil.getReferralcode() + ". <br /><br />Hurry! Download it now!*/;
        context.startActivity(Intent.createChooser(sharingIntent, context.getResources().getString(R.string.share)));
    }

    public static String getVersionName(Context context) {
//        String version = "";
//        try {
//            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
//            version = pInfo.versionName;
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
        Log.d("TAG", "getVersionName: " + BuildConfig.VERSION_NAME);
        return BuildConfig.VERSION_NAME;
    }

}
