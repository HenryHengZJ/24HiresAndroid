package com.zjheng.jobseed.jobseed.BookingScene;

import android.graphics.Color;
import android.graphics.Typeface;
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
import android.view.View;
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
import com.zjheng.jobseed.jobseed.ApplicantsScene.HiredApplicants;
import com.zjheng.jobseed.jobseed.ApplicantsScene.PendingApplicants;
import com.zjheng.jobseed.jobseed.ApplicantsScene.ShortlistedApplicants;
import com.zjheng.jobseed.jobseed.R;

public class Bookings extends AppCompatActivity {

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
    private DatabaseReference mUserActivities, mUserBookingNotification;
    private FirebaseAuth mAuth;

    private ImageButton mbackBtn;
    private TextView mtitletxt;

    private static final String TAG = "Bookings";
    private String post_key, post_title, post_desc, city, maincategory, subcategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applicants);

        mAuth = FirebaseAuth.getInstance();

        post_key = getIntent().getStringExtra("post_id");
        post_title = getIntent().getStringExtra("post_title");
        post_desc = getIntent().getStringExtra("post_desc");
        city = getIntent().getStringExtra("city");
        maincategory = getIntent().getStringExtra("maincategory");
        subcategory = getIntent().getStringExtra("subcategory");

        mUserActivities =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserActivities");

        mUserBookingNotification = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("BookingNotification");

        mUserActivities.child(mAuth.getCurrentUser().getUid()).child("BookingNotification").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnaphot : dataSnapshot.getChildren()) {
                    final String notificationkey = userSnaphot.getKey();
                    if(notificationkey!=null) {
                        mUserBookingNotification.child("Booking").child(notificationkey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                mUserActivities.child(mAuth.getCurrentUser().getUid()).child("BookingNotification").child(notificationkey).removeValue();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mtitletxt = findViewById(R.id.titletxt);
        mtitletxt.setText("Bookings");


        mbackBtn = (ImageButton) findViewById(R.id.backBtn);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);

        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);
        tabLayout.setupWithViewPager(mViewPager);

        View tabAcceptedBooking = (View) LayoutInflater.from(Bookings.this).inflate(R.layout.badged_tab, null);
        final TextView tv_titleaccepted = (TextView) tabAcceptedBooking.findViewById(R.id.tv_title);
        tv_titleaccepted.setTextColor(Color.parseColor("#eeeeee"));
        tv_titleaccepted.setText("ACCEPTED BOOKINGS");
        final TextView tv_countaccepted = (TextView) tabAcceptedBooking.findViewById(R.id.tv_count);

        tabLayout.getTabAt(1).setCustomView(tabAcceptedBooking);

        //Display top red dot at TAB BAR if NewApplied or NewPosted is true, which means applied job got shortlisted, or got new applicants
        mUserActivities.child(mAuth.getCurrentUser().getUid()).child("NewAcceptedBooking").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    String NewAcceptedBookingVal = dataSnapshot.getValue().toString();

                    if(NewAcceptedBookingVal.equals("true")){
                        tv_countaccepted.setVisibility(View.VISIBLE);
                    }
                    else{
                        tv_countaccepted.setVisibility(View.GONE);
                    }
                }
                else{
                    tv_countaccepted.setVisibility(View.GONE);
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
                    tv_titleaccepted.setTextColor(Color.WHITE);
                    tv_countaccepted.setVisibility(View.GONE);
                }
                else if(position == 0){
                    tv_titleaccepted.setTextColor(Color.parseColor("#eeeeee"));
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

    public static Bookings newInstance(String bla) {
        return new Bookings();
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
                    return PendingBooking.newInstance(post_key, post_title, post_desc, city, maincategory, subcategory);
                case 1:
                    return AcceptedBooking.newInstance(post_key);
                default:
                    return null;
            }

        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "PENDING BOOKINGS";
                case 1:
                    return "ACCEPTED BOOKINGS";
            }
            return null;
        }
    }
}
