package com.tfb.cbit.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import com.tfb.cbit.CBit;
import com.tfb.cbit.R;
import com.tfb.cbit.databinding.ActivityAboutUsBinding;
import com.tfb.cbit.databinding.ActivityLoginSignUpBinding;
import com.tfb.cbit.fragments.LoginWithMobileFragment;
import com.tfb.cbit.utility.Utils;


public class LoginSignUpActivity extends BaseAppCompactActivity {

    /* @BindView(R.id.tvLogin)
     TextView tvLogin;
     @BindView(R.id.tvSignup)
     TextView tvSignup;*/
    //private Unbinder unbinder = null;
    public static String code = "";
    private boolean isComeSeepLink = false;
    private static final String TAG = "LoginSignUpActivity";
    String[] PERMISSIONS = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    int PERMISSION_ALL = 1;
    private ActivityLoginSignUpBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginSignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // tvLogin.setSelected(true);
        //tvSignup.setSelected(false);
        // checkDeepLink();
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
        Intent intent = getIntent();
        Uri uri = intent.getData();
        Log.d(TAG, "URI>>> " + uri);
        if (uri != null) {
            //  if ("http".equals(uri.getScheme()) && getString(R.string.host_deep_link3).equals(uri.getHost())) {
            code = uri.getQueryParameter("code");
            CBit.referealCode = code;
            Log.d(TAG, "Code>>> " + code);
            //  }
            isComeSeepLink = true;

          /*  getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.frameContainer, SignUpFragment.newInstance(code))
                    .commit();*/
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.frameContainer, LoginWithMobileFragment.newInstance())
                    .commit();

        } else {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                code = bundle.getString("Referral", "");
            }
           /* getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.frameContainer, LoginWithMobileFragment.newInstance())
                    //.add(R.id.frameContainer, LoginFragment.newInstance())
                    .commit();
*/
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.frameContainer, LoginWithMobileFragment.newInstance())
                    //.add(R.id.frameContainer, LoginFragment.newInstance())
                    .commit();
        }


        // EventBus.getDefault().register(this);
    }

    @Override
    public void onBackPressed() {
        FragmentManager manager = getSupportFragmentManager();

        if (manager.getBackStackEntryCount() >= 1) {
            manager.popBackStack();
        } else {
            // Otherwise, ask user if he wants to leave :)
            finish();

        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void checkDeepLink() {
        if (getIntent() != null && getIntent().getData() != null) {
            Uri data = getIntent().getData();
            String scheme = data.getScheme();
            String host = data.getHost();
            String param = data.getQuery();
            Log.d("DeepLink", "Schema : " + scheme);
            Log.d("DeepLink", "Host : " + host);
            Log.d("DeepLink", "param : " + host);

           /* if (host.equals("page_details")){
                Intent intent = new Intent(this,DatadetailAcvity.class);
                intent.putExtra("detail_id",Long.valueOf(data.getQueryParameter("detail_id")));  // URL query values as string, you need to parse string to long.
                startActivity(intent);
            }else{
                // ... other logic
            }*/
        }
    }

   /* @OnClick(R.id.tvLogin)
    protected void tvLoginClick(){
        if(!tvLogin.isSelected()){
            tvLogin.setSelected(true);
            tvSignup.setSelected(false);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameContainer, LoginFragment.newInstance())
                    .commit();
        }
    }*/

   /* @OnClick(R.id.tvSignup)
    protected void tvSignupClick(){
        if(!tvSignup.isSelected()){
            tvLogin.setSelected(false);
            tvSignup.setSelected(true);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameContainer, SignUpFragment.newInstance())
                    .commit();
        }
    }*/

   /* @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(FbRegister fbRegister){
        if(!tvSignup.isSelected()){
            tvLogin.setSelected(false);
            tvSignup.setSelected(true);
            Bundle bundle = new Bundle();
            bundle.putString("fname",fbRegister.getFname());
            bundle.putString("email",fbRegister.getEmail());
            bundle.putString("socialid",fbRegister.getSocialid());
            bundle.putString("socialtype",fbRegister.getSocialtype());
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameContainer, SignUpFragment.newInstance(bundle))
                    .commit();
        }
    }*/

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

    @Override
    protected void onDestroy() {
        // EventBus.getDefault().unregister(this);

        super.onDestroy();
    }
}
