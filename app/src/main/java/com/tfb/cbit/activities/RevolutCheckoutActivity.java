package com.tfb.cbit.activities;

import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class RevolutCheckoutActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        webView = new WebView(this);
        setContentView(webView);

        webView.clearCache(true);
        webView.clearHistory();

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

                String url = request.getUrl().toString();

                if (url.contains("revolut-success")) {
                    setResult(RESULT_OK);
                    finish();
                    return true;
                }

                if (url.contains("revolut-failed")) {
                    setResult(RESULT_CANCELED);
                    finish();
                    return true;
                }
                return false;
            }
        });

        webView.loadUrl(getIntent().getStringExtra("URL"));
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            setResult(RESULT_CANCELED);
            super.onBackPressed();
        }
    }
}
