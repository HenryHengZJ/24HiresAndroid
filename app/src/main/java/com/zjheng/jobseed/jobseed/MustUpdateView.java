package com.zjheng.jobseed.jobseed;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MustUpdateView extends AppCompatActivity {

    private CardView mupdateCardView;
    private DatabaseReference mUserLocation, mAppVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_must_update_view);

        mAppVersion =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("AppVersion");

        mupdateCardView = (CardView) findViewById(R.id.updateCardView);

        mupdateCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkappsversion();
            }
        });
    }

    private void checkappsversion(){

        mAppVersion.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("MustVersion")){
                    String realversion = dataSnapshot.child("MustVersion").getValue().toString();
                    String userversion = BuildConfig.VERSION_NAME;
                    mustUpdate(realversion,userversion);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void mustUpdate(String realversion,String userversion){

        if(!realversion.equals(userversion)){
            final String appPackageName = "com.zjheng.jobseed.jobseed"; // package name of the app
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Do Here what ever you want do on back press;
    }
}
