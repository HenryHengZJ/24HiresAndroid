package com.zjheng.jobseed.jobseed.HomeScene;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.zjheng.jobseed.jobseed.MainActivity;
import com.zjheng.jobseed.jobseed.R;

/**
 * Created by zhen on 11/28/2017.
 */


public class BannerDetails extends AppCompatActivity {

    private WebView myWebView;
    private String linkurl;
    private ProgressDialog mProgress;

    private static final String TAG = "BannerDetail";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Log.d(TAG, "banner view " + linkurl);

        linkurl = getIntent().getStringExtra("linkurl");

        mProgress = new ProgressDialog(BannerDetails.this);

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
