package com.zjheng.jobseed.jobseed.TalentActivities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zjheng.jobseed.jobseed.ActivitiesScene.AppliedTab;
import com.zjheng.jobseed.jobseed.ActivitiesScene.PostedTab;
import com.zjheng.jobseed.jobseed.ActivitiesScene.SavedTab;
import com.zjheng.jobseed.jobseed.HowItWorks.HowJobWorks;
import com.zjheng.jobseed.jobseed.HowItWorks.HowTalentWorks;
import com.zjheng.jobseed.jobseed.R;

import static com.zjheng.jobseed.jobseed.R.id.tv_count;
import static com.zjheng.jobseed.jobseed.R.id.tv_title;

public class TalentActivities extends Fragment {

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
    private DatabaseReference mUserActivities;
    private FirebaseAuth mAuth;
    private ImageButton mhowBtn;

    Activity context;
    View rootView;

    private static final String TAG = "JobActivities";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_talent_activities, container, false);

        context = getActivity();

        mAuth = FirebaseAuth.getInstance();

        mUserActivities =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserActivities");

        setHasOptionsMenu(true);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());

        mhowBtn = (ImageButton) rootView.findViewById(R.id.howBtn);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) rootView.findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        View tabOne = (View) LayoutInflater.from(getActivity()).inflate(R.layout.badged_tab, null);
        final TextView tv_titleOne = (TextView) tabOne.findViewById(tv_title);
        tv_titleOne.setTextColor(Color.parseColor("#FFFFFFFF"));
        tv_titleOne.setText("BOOKINGS MADE");
        final TextView tv_One = (TextView) tabOne.findViewById(tv_count);


        View tabTwo = (View) LayoutInflater.from(getActivity()).inflate(R.layout.badged_tab, null);
        final TextView tv_titleTwo = (TextView) tabTwo.findViewById(tv_title);
        tv_titleTwo.setTextColor(Color.parseColor("#eeeeee"));
        tv_titleTwo.setText("MY TALENTS");
        final TextView tv_Two = (TextView) tabTwo.findViewById(tv_count);


        //Display top red dot at TAB BAR if NewApplied or NewPosted is true, which means applied job got shortlisted, or got new applicants
        mUserActivities.child(mAuth.getCurrentUser().getUid()).child("NewBookingsMade").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    String val = dataSnapshot.getValue().toString();
                    if(val.equals("true")){
                        tv_One.setVisibility(View.VISIBLE);
                    }
                    else{
                        tv_One.setVisibility(View.GONE);
                    }
                }
                else{
                    tv_One.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mUserActivities.child(mAuth.getCurrentUser().getUid()).child("NewMyTalents").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    String val = dataSnapshot.getValue().toString();
                    if(val.equals("true")){
                        tv_Two.setVisibility(View.VISIBLE);
                    }
                    else{
                        tv_Two.setVisibility(View.GONE);
                    }
                }
                else{
                    tv_Two.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        tabLayout.getTabAt(0).setCustomView(tabOne);
        tabLayout.getTabAt(1).setCustomView(tabTwo);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if(position == 0){
                    tv_titleOne.setTextColor(Color.WHITE);
                    tv_titleTwo.setTextColor(Color.parseColor("#eeeeee"));
                }
                else if(position == 1){
                    tv_titleTwo.setTextColor(Color.WHITE);
                    tv_titleOne.setTextColor(Color.parseColor("#eeeeee"));
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        mhowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent howitworksintent = new Intent(context, HowTalentWorks.class);
                startActivity(howitworksintent);
            }
        });

        return rootView;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem itemSettings = menu.findItem(R.id.menuSettings);
        itemSettings.setVisible(false);

        MenuItem itemPublish = menu.findItem(R.id.menuPublish);
        itemPublish.setVisible(false);

        MenuItem item = menu.findItem(R.id.menuSearch);
        item.setVisible(false);

        MenuItem itemSave = menu.findItem(R.id.menuSave);
        itemSave.setVisible(false);

        MenuItem itemSearch = menu.findItem(R.id.menuSearch2);
        itemSearch.setVisible(false);
    }


    public static TalentActivities newInstance(String bla) {
        return new TalentActivities();
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
                    BookingsMadeTab tab1 = new BookingsMadeTab();
                    return tab1;
                case 1:
                    MyTalentsTab tab2 = new MyTalentsTab();
                    return tab2;
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
                    return "BOOKINGS MADE";
                case 1:
                    return "MY TALENTS";
            }
            return null;
        }
    }
}
