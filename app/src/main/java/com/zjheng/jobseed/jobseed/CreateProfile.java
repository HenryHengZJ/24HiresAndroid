package com.zjheng.jobseed.jobseed;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by zhen on 4/8/2018.
 */

public class CreateProfile extends AppCompatActivity {

    private CardView mgoCardView;
    private Button mskipBtn;
    private FirebaseAuth mAuth;
    private ImageButton mbackBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createprofile1);

        mAuth = FirebaseAuth.getInstance();

        mgoCardView = findViewById(R.id.goCardView);

        mskipBtn = findViewById(R.id.skipBtn);

        mbackBtn = (ImageButton) findViewById(R.id.backBtn);

        mgoCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), IntroProfile1.class);
                startActivity(intent);
              //  finish();
            }
        });

        mskipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                String uid = mAuth.getCurrentUser().getUid();
                intent.putExtra("user_id", uid);
                startActivity(intent);
                finish();
            }
        });

        mbackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.nochange, R.anim.pulldown);
    }

}