package com.tfb.cbit.activities;

import android.os.Build;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.tfb.cbit.R;

public class EMerchantCheckoutActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        webView = new WebView(this);
        setContentView(webView);

        String paymentUrl = getIntent().getStringExtra("URL");

        WebSettings settings = webView.getSettings();

        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setLoadsImagesAutomatically(true);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setSupportMultipleWindows(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);

        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setDatabaseEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }

//        webView.setWebChromeClient(new WebChromeClient() {
//
//            @Override
//            public void onProgressChanged(WebView view, int newProgress) {
//                if (newProgress == 100) {
//                    s
//                } else {
//                    // Show Loader
//                }
//            }
//        });

        webView.setWebChromeClient(new WebChromeClient());

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view,
                                                    WebResourceRequest request) {

                String url = request.getUrl().toString();

                return handleUrl(url);

            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view,
                                                    String url) {

                return handleUrl(url);

            }

        });

        webView.loadUrl(paymentUrl);

    }

    private boolean handleUrl(String url) {

        if (url.contains("/payment/success")) {

            setResult(RESULT_OK);

            finish();

            return true;

        }

        if (url.contains("/payment/failure")) {

            setResult(RESULT_CANCELED);

            finish();

            return true;

        }

        if (url.contains("/payment/cancel")) {

            setResult(RESULT_CANCELED);

            finish();

            return true;

        }

        return false;

    }

}