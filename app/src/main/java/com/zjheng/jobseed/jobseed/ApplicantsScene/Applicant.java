package com.zjheng.jobseed.jobseed.ApplicantsScene;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zjheng.jobseed.jobseed.ActivitiesScene.AppliedTab;
import com.zjheng.jobseed.jobseed.ActivitiesScene.PostedTab;
import com.zjheng.jobseed.jobseed.ActivitiesScene.SavedTab;
import com.zjheng.jobseed.jobseed.OtherUserScene.OtherUserDetails;
import com.zjheng.jobseed.jobseed.R;

import static com.zjheng.jobseed.jobseed.R.id.tv_count;
import static com.zjheng.jobseed.jobseed.R.id.tv_title;

public class Applicant extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private DatabaseReference mUserActivities, mApplyNotification;
    private FirebaseAuth mAuth;

    private ImageButton mbackBtn;

    private static final String TAG = "Applicant";
    private String post_key, city,post_title, post_desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applicants);

        mAuth = FirebaseAuth.getInstance();

        post_key = getIntent().getStringExtra("post_id");
        city = getIntent().getStringExtra("city");
        post_title = getIntent().getStringExtra("post_title");
        post_desc = getIntent().getStringExtra("post_desc");

        mUserActivities =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserActivities");

        mApplyNotification =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("ApplyNotification");

        mUserActivities.child(mAuth.getCurrentUser().getUid()).child("ApplyNotification").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnaphot : dataSnapshot.getChildren()) {
                    final String notificationkey = userSnaphot.getKey();
                    if(notificationkey!=null) {
                        mApplyNotification.child("Applications").child(notificationkey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                mUserActivities.child(mAuth.getCurrentUser().getUid()).child("ApplyNotification").child(notificationkey).removeValue();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mbackBtn = (ImageButton) findViewById(R.id.backBtn);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);

        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);
        tabLayout.setupWithViewPager(mViewPager);

        View tabHired = (View) LayoutInflater.from(Applicant.this).inflate(R.layout.badged_tab, null);
        final TextView tv_titlehired = (TextView) tabHired.findViewById(R.id.tv_title);
        tv_titlehired.setTextColor(Color.parseColor("#eeeeee"));
        tv_titlehired.setText("HIRED");
        final TextView tv_counthired = (TextView) tabHired.findViewById(R.id.tv_count);

        tabLayout.getTabAt(2).setCustomView(tabHired);

        //Display top red dot at TAB BAR if NewApplied or NewPosted is true, which means applied job got shortlisted, or got new applicants
        mUserActivities.child(mAuth.getCurrentUser().getUid()).child("NewApplicant").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    String NewApplicantval = dataSnapshot.getValue().toString();
                    Log.d(TAG,"newapplicanval = " + NewApplicantval);
                    if(NewApplicantval.equals("true")){
                        tv_counthired.setVisibility(View.VISIBLE);
                    }
                    else{
                        tv_counthired.setVisibility(View.GONE);
                    }
                }
                else{
                    tv_counthired.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if(position == 1){
                    tv_titlehired.setTextColor(Color.parseColor("#eeeeee"));
                }
                else if(position == 2){
                    tv_titlehired.setTextColor(Color.WHITE);
                    tv_counthired.setVisibility(View.GONE);
                }
                else if(position == 0){
                    tv_titlehired.setTextColor(Color.parseColor("#eeeeee"));
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mbackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public static Applicant newInstance(String bla) {
        return new Applicant();
    }

    /**
     * A placeholder fragment containing a simple view.
     */


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch(position){
                case 0:
                    return PendingApplicants.newInstance(post_key, city, post_title, post_desc);
                case 1:
                    return ShortlistedApplicants.newInstance(post_key, city, post_title, post_desc);
                case 2:
                    return HiredApplicants.newInstance(post_key, city, post_title, post_desc);
                default:
                    return null;
            }


        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "PENDING";
                case 1:
                    return "SHORTLISTED";
                case 2:
                    return "HIRED";
            }
            return null;
        }
    }
}
