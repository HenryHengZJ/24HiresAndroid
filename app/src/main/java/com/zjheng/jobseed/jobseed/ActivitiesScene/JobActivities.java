package com.zjheng.jobseed.jobseed.ActivitiesScene;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.TabLayout;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
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
import com.zjheng.jobseed.jobseed.HowItWorks.HowJobWorks;
import com.zjheng.jobseed.jobseed.R;

public class JobActivities extends Fragment {

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
        rootView = inflater.inflate(R.layout.activity_job_activities, container, false);

        context = getActivity();

        mAuth = FirebaseAuth.getInstance();

        mUserActivities =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserActivities");

        setHasOptionsMenu(true);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());

        mhowBtn = (ImageButton) rootView.findViewById(R.id.howBtn);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) rootView.findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);

        tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        View tabOne = (View) LayoutInflater.from(getActivity()).inflate(R.layout.badged_tab, null);
        final TextView tv_title = (TextView) tabOne.findViewById(R.id.tv_title);
        tv_title.setTextColor(Color.parseColor("#eeeeee"));
        final TextView tv_count = (TextView) tabOne.findViewById(R.id.tv_count);
        tv_title.setText("POSTED");

        View tabApplied = (View) LayoutInflater.from(getActivity()).inflate(R.layout.badged_tab, null);
        final TextView tv_titleapply = (TextView) tabApplied.findViewById(R.id.tv_title);
        tv_titleapply.setTextColor(Color.parseColor("#eeeeee"));
        final TextView tv_countapply = (TextView) tabApplied.findViewById(R.id.tv_count);
        tv_titleapply.setText("APPLIED");

        //Display top red dot at TAB BAR if NewApplied or NewPosted is true, which means applied job got shortlisted, or got new applicants
        mUserActivities.child(mAuth.getCurrentUser().getUid()).child("NewApplied").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    String NewAppliedval = dataSnapshot.getValue().toString();
                    if(NewAppliedval.equals("true")){
                        tv_countapply.setVisibility(View.VISIBLE);
                    }
                    else{
                        tv_countapply.setVisibility(View.GONE);
                    }
                }
                else{
                    tv_countapply.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mUserActivities.child(mAuth.getCurrentUser().getUid()).child("NewPosted").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    String NewPostedval = dataSnapshot.getValue().toString();
                    if(NewPostedval.equals("true")){
                        tv_count.setVisibility(View.VISIBLE);
                    }
                    else{
                        tv_count.setVisibility(View.GONE);
                    }
                }
                else{
                    tv_count.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        tabLayout.getTabAt(2).setCustomView(tabOne);
        tabLayout.getTabAt(1).setCustomView(tabApplied);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if(position == 1){
                    tv_titleapply.setTextColor(Color.WHITE);
                    tv_title.setTextColor(Color.parseColor("#eeeeee"));
                }
                else if(position == 2){
                    tv_title.setTextColor(Color.WHITE);
                    tv_titleapply.setTextColor(Color.parseColor("#eeeeee"));
                }
                else if(position == 0){
                    tv_title.setTextColor(Color.parseColor("#eeeeee"));
                    tv_titleapply.setTextColor(Color.parseColor("#eeeeee"));
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
                Intent howitworksintent = new Intent(context, HowJobWorks.class);
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


    public static JobActivities newInstance(String bla) {
        return new JobActivities();
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
                    SavedTab tab1 = new SavedTab();
                    return tab1;
                case 1:
                    AppliedTab tab2 = new AppliedTab();
                    return tab2;
                case 2:
                    PostedTab tab3 = new PostedTab();
                    return tab3;
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
                    return "SAVED";
                case 1:
                    return "APPLIED";
                case 2:
                    return "POSTED";
            }
            return null;
        }
    }
}
