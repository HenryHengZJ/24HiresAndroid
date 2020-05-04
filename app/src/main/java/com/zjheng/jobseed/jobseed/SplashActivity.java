package com.zjheng.jobseed.jobseed;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.zjheng.jobseed.jobseed.LoginScene.Login;

/**
 * Created by zhen on 7/7/2017.
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
    }
}