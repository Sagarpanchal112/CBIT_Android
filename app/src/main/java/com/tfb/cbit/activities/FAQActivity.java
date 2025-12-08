package com.tfb.cbit.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tfb.cbit.R;
import com.tfb.cbit.databases.DatabaseHandler;
import com.tfb.cbit.databinding.ActivityAboutUsBinding;
import com.tfb.cbit.event.UnAuthorizedEvent;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class FAQActivity extends BaseAppCompactActivity {


    public static final String NAME = "name";
    public static final String LINK = "link";
    private SessionUtil sessionUtil;
    private ActivityAboutUsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutUsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sessionUtil = new SessionUtil(this);
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            finish();
            return;
        }

        binding.toolbarTitle.setText(bundle.getString(NAME, ""));
        binding.webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                binding.pbProgress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });
        binding.webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                binding.pbProgress.setProgress(newProgress);
                if (newProgress == 100) {
                    binding.pbProgress.setVisibility(View.GONE);
                }
            }
        });
        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.loadUrl(bundle.getString(LINK, ""));

        binding.ivBack.setOnClickListener(view -> {
            onBackPressed();
        });
    }


    @Override
    protected void onDestroy() {
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);

        super.onDestroy();
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
}
