package com.zjheng.jobseed.jobseed;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zjheng.jobseed.jobseed.UserProfileScene.UserProfileInfo;

/**
 * Created by zhen on 4/8/2018.
 */

public class IntroProfile3 extends AppCompatActivity {

    private CardView mnextCardView;
    private Button mskipBtn;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserInfo;
    private EditText meducationtxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introprofile3);

        Toolbar mToolbar = (Toolbar)findViewById(R.id.toolbar_other);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Step 3 of 4");
        mToolbar.setTitleTextColor(android.graphics.Color.WHITE);

        mToolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white_24dp);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mAuth = FirebaseAuth.getInstance();

        mUserInfo =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserInfo");

        meducationtxt = (EditText)findViewById(R.id.educationtxt);

        mnextCardView = findViewById(R.id.nextCardView);

        mskipBtn = findViewById(R.id.skipBtn);

        mskipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IntroProfile3.this, IntroProfile4.class);
                startActivity(intent);
            }
        });

        mnextCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String educationtxt = meducationtxt.getText().toString().trim();

                final DatabaseReference newEducation = mUserInfo.child(mAuth.getCurrentUser().getUid()).child("Education");

                if(TextUtils.isEmpty(educationtxt)){
                    meducationtxt.setError("Empty Education. Press SKIP above to skip this step");
                }

                if(!TextUtils.isEmpty(educationtxt)){
                    newEducation.setValue(educationtxt);
                    Intent intent = new Intent(IntroProfile3.this, IntroProfile4.class);
                    startActivity(intent);
                }


            }
        });

    }

}
