package com.tfb.cbit.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.tfb.cbit.databases.DatabaseHandler;
import com.tfb.cbit.databinding.ActivityAboutUsBinding;
import com.tfb.cbit.event.UnAuthorizedEvent;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class AboutUsActivity extends BaseAppCompactActivity {

    public static final String NAME = "name";
    public static final String LINK = "link";

    private SessionUtil sessionUtil;
    private ActivityAboutUsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAboutUsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        sessionUtil = new SessionUtil(this);

        // -------- Get Data --------
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            finish();
            return;
        }

        String title = bundle.getString(NAME, "");
        String url   = bundle.getString(LINK, "");

        if (url == null || url.isEmpty()) {
            finish();
            return;
        }

        binding.toolbarTitle.setText(title);

        // -------- WebView Settings (COMMON) --------
        WebSettings settings = binding.webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(binding.webView, true);

        // -------- WebView Client --------
        binding.webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                binding.pbProgress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                binding.pbProgress.setVisibility(View.GONE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(
                    WebView view,
                    WebResourceRequest request
            ) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }
        });

        // -------- Chrome Client --------
        binding.webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                binding.pbProgress.setProgress(newProgress);
            }
        });

        // -------- Load URL (ONLY ONCE) --------
        binding.webView.loadUrl(url);

        binding.ivBack.setOnClickListener(v -> onBackPressed());
    }

    @Override
    protected void onDestroy() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        super.onDestroy();
    }

    // -------- Unauthorized Handling --------
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUnAuthorizedEvent(UnAuthorizedEvent event) {
        Utils.showToast(this, event.getMessage());

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
