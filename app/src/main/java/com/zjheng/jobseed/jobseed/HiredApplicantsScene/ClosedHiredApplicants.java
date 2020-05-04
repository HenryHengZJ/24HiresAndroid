package com.zjheng.jobseed.jobseed.HiredApplicantsScene;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zjheng.jobseed.jobseed.ApplicantsScene.HiredApplicants;
import com.zjheng.jobseed.jobseed.R;

public class ClosedHiredApplicants extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mUserActivities, mUserHireNotification;

    private static final String TAG = "Applicants";

    private String userid, post_key, city, post_title, post_desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hiredapplicants_list);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        userid = mAuth.getCurrentUser().getUid();

        post_key = getIntent().getStringExtra("post_id");
        city = getIntent().getStringExtra("city");
        post_title = getIntent().getStringExtra("post_title");
        post_desc = getIntent().getStringExtra("post_desc");

        mUserActivities =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserActivities");

        mUserHireNotification = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("HireNotification");

        mUserActivities.child(mAuth.getCurrentUser().getUid()).child("HiredNotification").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnaphot : dataSnapshot.getChildren()) {
                    final String notificationkey = userSnaphot.getKey();
                    if(notificationkey!=null) {
                        mUserHireNotification.child("Hire").child(notificationkey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                mUserActivities.child(mAuth.getCurrentUser().getUid()).child("HiredNotification").child(notificationkey).removeValue();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        getSupportFragmentManager().findFragmentById(R.id.frame_container);
        //above part is to determine which fragment is in your frame_container
        setFragment(new HiredApplicants());

    }

    protected void setFragment(Fragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putString("post_id", post_key );
        bundle.putString("city", city );
        bundle.putString("post_title", post_title );
        bundle.putString("post_desc", post_desc );

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
        fragment.setArguments(bundle);
        fragmentTransaction.replace(android.R.id.content, fragment);
        fragmentTransaction.commit();
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
