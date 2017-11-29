package com.prts.pickccustomer.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.prts.pickccustomer.R;
import com.prts.pickccustomer.constants.ProgressDialog;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class WebViewActivity extends AppCompatActivity {

    public static final String URL  = "url";
    private static final String TAG = "WebViewAct";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        String url = intent.getStringExtra(URL);
        loadWebViewWithSettings(url);
    }
    WebView webview;
    private void loadWebViewWithSettings(String url){
        webview = (WebView) findViewById(R.id.webView);
        Log.d(getLocalClassName(), "loadWebViewWithSettings: "+url);
        CustomWebViewClient customWebViewClient = new CustomWebViewClient();
        webview.setWebViewClient(customWebViewClient);

        WebSettings settings=webview.getSettings();
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
            progressDialog = new ProgressDialog(WebViewActivity.this,R.style.AppDialogTheme1);
            progressDialog.setTitle("Loading");
            progressDialog.setMessage("Please wait...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            new ParseURLAsync().execute(url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (progressDialog != null && progressDialog.isShowing()){
                progressDialog.dismiss();
            }
        }

        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            //do whatever you want with the url that is clicked inside the webview.
            //for example tell the webview to load that url.
            view.loadUrl(url);
            //return true if this method handled the link event
            //or false otherwise
            return true;
        }
    }


    private class ParseURLAsync extends AsyncTask<String, Void, String> {
        String title2;

        @Override
        protected String doInBackground(String... urls) {
            StringBuffer buffer = new StringBuffer();
            String completeUrl = urls[0];
            try {
                Log.d("JSwa", "Connecting to [" + completeUrl + "]");
                Document doc = Jsoup.connect(completeUrl).get();
                Log.d("JSwa", "Connected to [" + completeUrl + "]");
// Get document (HTML page) title
                String title = doc.title();

                Log.d("JSwA", "Title [" + title + "]");
                title2 = title;
                buffer.append("Title: " + title + "\n");

// Get meta info
                Elements metaElems = doc.select("h3");

                Log.d(TAG, "doInBackground: " + metaElems);
                buffer.append("META DATA\n");
                if (metaElems != null && !metaElems.isEmpty()) {
                    title2 = metaElems.get(0).html();
                }
               /* for (Element metaElem : metaElems) {
                    String name = metaElem.html();
                    String content = metaElem.attr("content");
                    buffer.append("name [" + name + "] - content [" + content + "] \n");
                }

                Elements topicList = doc.select("h2.topic");
                buffer.append("Topic listrn");
                for (Element topic : topicList) {
                    String data = topic.text();

                    buffer.append("Data [" + data + "] \n");
                }
*/
            } catch (Throwable t) {
                t.printStackTrace();
            }

            return buffer.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //editText.setText(s);
            Log.d(TAG, "onPostExecute: "+title2);
//            if (title2 != null) {
                getSupportActionBar().setTitle("Help");
//            }
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (webview.canGoBack()) {
                        webview.goBack();
                    }
                    else {
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }
}
