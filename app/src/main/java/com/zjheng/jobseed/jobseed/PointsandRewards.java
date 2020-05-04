package com.zjheng.jobseed.jobseed;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zjheng.jobseed.jobseed.ActivitiesScene.AppliedTab;
import com.zjheng.jobseed.jobseed.ActivitiesScene.SavedTab;
import com.zjheng.jobseed.jobseed.CustomUIClass.TouchImageView;
import com.zjheng.jobseed.jobseed.MessageScene.ChatRoom;
import com.zjheng.jobseed.jobseed.OtherUserScene.OtherUserDetails;
import com.zjheng.jobseed.jobseed.OtherUserScene.OtherUserPosted;
import com.zjheng.jobseed.jobseed.OtherUserScene.OtherUserTalents;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.glide.transformations.BlurTransformation;
import uk.co.senab.photoview.PhotoViewAttacher;

import static com.zjheng.jobseed.jobseed.R.id.userimage;
import static com.zjheng.jobseed.jobseed.UnUsedFiles.ShortListedApplicantUserProfile.mratingtxt;

public class PointsandRewards extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mUserAccount, mUserInfo, mChatRoom, mUserChatList, mUserLocation, mUserReview;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private Toolbar mToolbar;

    private static final String TAG = "PointsandRewards";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pointsrewards);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mToolbar = (Toolbar)findViewById(R.id.toolbar_other);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle(" ");
        mToolbar.setSubtitle(" ");

        mToolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white_24dp);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_other);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar_toolbar_other);
        collapsingToolbarLayout.setTitle(" ");

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {

                    collapsingToolbarLayout.setTitle("Points and Rewards");
                    isShow = true;

                } else if(isShow) {
                    collapsingToolbarLayout.setTitle(" ");//carefull there should a space between double quote otherwise it wont work
                    isShow = false;
                }
            }
        });

        mAuth = FirebaseAuth.getInstance();

        mUserInfo =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserInfo");

        mUserAccount = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserAccount");

        mChatRoom =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("ChatRoom");

        mUserChatList =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserChatList");

        mUserLocation =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserLocation");

        mUserReview = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserReview");

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);

        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);

    }

    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch(position){
                case 0:
                    //OtherUserDetails tab1 = new OtherUserDetails();
                   // return OtherUserDetails.newInstance(user_uid);
                    RedemeedTab tab1 = new RedemeedTab();
                    return tab1;
                case 1:
                    //return OtherUserPosted.newInstance(user_uid);
                    RewardsOfferTab tab2 = new RewardsOfferTab();
                    return tab2;
                default:
                    return null;
            }


        }

        @Override
        public int getItemPosition(Object object){
            return PagerAdapter.POSITION_NONE;
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "REDEMEED";
                case 1:
                    return "OFFERS";
            }
            return null;
        }
    }
}
