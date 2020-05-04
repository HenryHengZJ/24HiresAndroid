package com.zjheng.jobseed.jobseed.LoginScene;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.zjheng.jobseed.jobseed.R;

/**
 * Created by zhen on 11/28/2017.
 */


public class TnCDetails extends AppCompatActivity {

    private WebView myWebView;
    private String title;
    private String linkurl;
    private ProgressDialog mProgress;

    private static final String TAG = "TnCDetails";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Log.d(TAG, "TnCDetails link " + linkurl);

        linkurl = getIntent().getStringExtra("linkurl");
        title = getIntent().getStringExtra("title");

        if (title.equals("Terms of Use")) {
            setTitle("Terms of Use");
        }
        else {
            setTitle("Privacy Policy");
        }

        mProgress = new ProgressDialog(TnCDetails.this);

        mProgress.setMessage("Loading..");
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.show();

        myWebView = (WebView)findViewById(R.id.myWebView);
        myWebView.setInitialScale(1);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.getSettings().setLoadWithOverviewMode(true);
        myWebView.getSettings().setUseWideViewPort(true);
        myWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        myWebView.setScrollbarFadingEnabled(false);
        myWebView.getSettings().setBuiltInZoomControls(true);
        myWebView.getSettings().setSupportZoom(true);
        myWebView.getSettings().setDisplayZoomControls(true);
        myWebView.loadUrl(linkurl);

        myWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, final String url) {
                mProgress.dismiss();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
