package com.prts.pickccustomer.ui;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.prts.pickccustomer.R;
import com.prts.pickccustomer.constants.ProgressDialog;

public class RateCardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_card);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String url = "http://ragsarma-001-site6.htempurl.com/ratecard/mobile";
        WebView webview = (WebView) findViewById(R.id.webview);

        Log.d(getLocalClassName(), "onCreateView: " + url);
        CustomWebViewClient customWebViewClient = new CustomWebViewClient();
        webview.setWebViewClient(customWebViewClient);

        WebSettings settings = webview.getSettings();
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setSupportZoom(true);
        settings.setJavaScriptEnabled(true);
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);

        if (Build.VERSION.SDK_INT >= 19) {
            webview.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        else {
            webview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        customWebViewClient.shouldOverrideUrlLoading(webview, url);
    }

    private class CustomWebViewClient extends WebViewClient {
        ProgressDialog progressDialog;

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            progressDialog = new ProgressDialog(RateCardActivity.this,R.style.AppDialogTheme1);
            progressDialog.setTitle("Loading");
            progressDialog.setMessage("Please wait...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //do whatever you want with the url that is clicked inside the webview.
            //for example tell the webview to load that url.
            view.loadUrl(url);
            //return true if this method handled the link event
            //or false otherwise
            return true;
        }

    }
}
