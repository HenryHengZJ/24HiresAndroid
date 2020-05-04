package com.zjheng.jobseed.jobseed.UnUsedFiles;

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
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.zjheng.jobseed.jobseed.MessageScene.ChatRoom;
import com.zjheng.jobseed.jobseed.OtherUserScene.OtherUserDetails;
import com.zjheng.jobseed.jobseed.OtherUserScene.OtherUserPosted;
import com.zjheng.jobseed.jobseed.R;
import com.zjheng.jobseed.jobseed.CustomUIClass.TouchImageView;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.glide.transformations.BlurTransformation;
import uk.co.senab.photoview.PhotoViewAttacher;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ShortListedApplicantUserProfile extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mUserAccount, mUserInfo, mChatRoom, mUserChatList, mUserLocation, mUserActivities, mUserReview;

    private ImageView mpostImage;
    private TextView mpostName, mpostLocation;
    private TextView mchatusertxt;
    private Toolbar mToolbar;
    private CircleImageView mprofilepic;
    private LinearLayout mshortlistedLay;

    private String user_uid, post_userimage, user_name, CoverImage, city, post_title, post_desc;

    private CardView mrejectcardview, mshortlistcardview;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout tabLayout;

    private SharedPreferences prefs;
    public static RatingBar mRatingBar;
    private int rateval, ratestar = 0;
    public static TextView mratingtxt;
    private long reviewcount5 = 0, reviewcount4 = 0, reviewcount3 = 0, reviewcount2 = 0 , reviewcount1 = 0, totalreviewcount = 0;
    private Long reviewcount;
    private int reviewcounter, reviewlimit = 20;

    private String ReviewCount, reducedReviewCount;
    private ProgressDialog mProgress;
    private static final String TAG = "ShortlistedApplicant";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applicantprofile);

        user_uid = getIntent().getStringExtra("user_uid");
        city = getIntent().getStringExtra("city");
        post_title = getIntent().getStringExtra("post_title");
        post_desc = getIntent().getStringExtra("post_desc");

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mToolbar = (Toolbar)findViewById(R.id.toolbar_other);
        setSupportActionBar(mToolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeButtonEnabled(true);
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
                    if(user_name!=null) {
                        collapsingToolbarLayout.setTitle(user_name);
                        isShow = true;
                    }
                } else if(isShow) {
                    collapsingToolbarLayout.setTitle(" ");//carefull there should a space between double quote otherwise it wont work
                    isShow = false;
                }
            }
        });

        mAuth = FirebaseAuth.getInstance();

        mUserInfo =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserInfo");
        mUserInfo.keepSynced(true);

        mUserAccount = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserAccount");
        mUserAccount.keepSynced(true);

        mChatRoom =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("ChatRoom");
        mChatRoom.keepSynced(true);

        mUserChatList =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserChatList");
        mUserChatList.keepSynced(true);

        mUserLocation =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserLocation");
        mUserLocation.keepSynced(true);

        mUserActivities =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserActivities");
        mUserActivities.keepSynced(true);

        mUserReview = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserReview");
        mUserReview.keepSynced(true);

        mpostImage = (ImageView) findViewById(R.id.postImage);
        mprofilepic = (CircleImageView) findViewById(R.id.profilepic);

        mpostName = (TextView) findViewById(R.id.postName);
        mpostLocation = (TextView) findViewById(R.id.postLocation);

        mrejectcardview = (CardView) findViewById(R.id.rejectcardview);
        mrejectcardview.setVisibility(GONE);
        mshortlistcardview = (CardView) findViewById(R.id.shortlistcardview);
        mshortlistcardview.setVisibility(GONE);

        mshortlistedLay = (LinearLayout) findViewById(R.id.shortlistedLay);
        mshortlistedLay.setVisibility(VISIBLE);

        mchatusertxt = (TextView) findViewById(R.id.chatusertxt);

        mRatingBar = (RatingBar) findViewById(R.id.userratingbar);
        mratingtxt = (TextView) findViewById(R.id.ratetxt);

        if(user_uid.equals(mAuth.getCurrentUser().getUid())){
            mchatusertxt.setEnabled(false);
        }

        mUserReview.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(user_uid)){
                    if(dataSnapshot.child(user_uid).hasChild("Rate5")){
                        reviewcount5 = (Long)dataSnapshot.child(user_uid).child("Rate5").getValue();
                    }
                    if(dataSnapshot.child(user_uid).hasChild("Rate4")){
                        reviewcount4 = (Long)dataSnapshot.child(user_uid).child("Rate4").getValue();
                    }
                    if(dataSnapshot.child(user_uid).hasChild("Rate3")){
                        reviewcount3 = (Long)dataSnapshot.child(user_uid).child("Rate3").getValue();
                    }
                    if(dataSnapshot.child(user_uid).hasChild("Rate2")){
                        reviewcount2 = (Long)dataSnapshot.child(user_uid).child("Rate2").getValue();
                    }
                    if(dataSnapshot.child(user_uid).hasChild("Rate1")){
                        reviewcount1 = (Long)dataSnapshot.child(user_uid).child("Rate1").getValue();
                    }
                    if(dataSnapshot.child(user_uid).hasChild("Review")){
                        if(!dataSnapshot.child(user_uid).child("Review").hasChild(mAuth.getCurrentUser().getUid())){
                            askrateuser();
                        }
                    }
                    totalreviewcount = reviewcount5+reviewcount4+reviewcount3+reviewcount2+reviewcount1;
                }
                else{
                    askrateuser();
                }
                mratingtxt.setText(totalreviewcount+" Reviews");

                if (totalreviewcount !=0) {
                    long starcount = ((5*reviewcount5)+(4*reviewcount4)+(3*reviewcount3)+(2*reviewcount2)+(1*reviewcount1))/(totalreviewcount);
                    rateval = Math.round(starcount);
                }
                mRatingBar.setRating(rateval);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mUserInfo.child(user_uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild("Name")) {

                    user_name = dataSnapshot.child("Name").getValue().toString();
                    mpostName.setText(user_name);
                }
                else{
                    mUserAccount.child(user_uid).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            user_name = dataSnapshot.getValue().toString();
                            mpostName.setText(user_name);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

                if(dataSnapshot.hasChild("UserImage")) {

                    post_userimage = dataSnapshot.child("UserImage").getValue().toString();
                    if (post_userimage != null) {
                        if (post_userimage.equals("default")) {
                            mprofilepic.setImageResource(R.drawable.defaultprofile_pic);
                            mpostImage.setBackgroundColor(Color.parseColor("#67b8ed"));
                        } else {
                            Glide.with(ShortListedApplicantUserProfile.this).load(post_userimage)
                                    .thumbnail(0.5f)
                                    .centerCrop()
                                    .error(R.drawable.defaultprofile_pic)
                                    .placeholder(R.drawable.defaultprofile_pic)
                                    .dontAnimate()
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(mprofilepic);

                            if(dataSnapshot.hasChild("CoverImage")) {
                                CoverImage = dataSnapshot.child("CoverImage").getValue().toString();
                                if (CoverImage != null) {
                                    Glide.with(ShortListedApplicantUserProfile.this).load(CoverImage)
                                            .thumbnail(0.5f)
                                            .error(R.drawable.profilebg3)
                                            .placeholder(R.drawable.profilebg3)
                                            .dontAnimate()
                                            .centerCrop()
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .into(mpostImage);
                                }
                            }
                            else{
                                Glide.with(ShortListedApplicantUserProfile.this).load(post_userimage)
                                        .thumbnail(0.5f)
                                        .centerCrop()
                                        .bitmapTransform(new BlurTransformation(ShortListedApplicantUserProfile.this, 100))
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .into(mpostImage);
                            }
                        }
                    }
                }

                else{

                    mUserAccount.child(user_uid).child("image").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot1) {

                            post_userimage = dataSnapshot1.getValue().toString();
                            if (post_userimage != null) {
                                if (post_userimage.equals("default")) {
                                    mprofilepic.setImageResource(R.drawable.defaultprofile_pic);
                                } else {
                                    Glide.with(ShortListedApplicantUserProfile.this).load(post_userimage)
                                            .thumbnail(0.5f)
                                            .centerCrop()
                                            .error(R.drawable.defaultprofile_pic)
                                            .placeholder(R.drawable.defaultprofile_pic)
                                            .dontAnimate()
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .into(mprofilepic);

                                    if(dataSnapshot.hasChild("CoverImage")) {
                                       CoverImage = dataSnapshot.child("CoverImage").getValue().toString();
                                        if (CoverImage != null) {
                                            Glide.with(ShortListedApplicantUserProfile.this).load(CoverImage)
                                                    .thumbnail(0.5f)
                                                    .error(R.drawable.profilebg3)
                                                    .placeholder(R.drawable.profilebg3)
                                                    .dontAnimate()
                                                    .centerCrop()
                                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                    .into(mpostImage);
                                        }
                                    }
                                    else{
                                        Glide.with(ShortListedApplicantUserProfile.this).load(post_userimage)
                                                .thumbnail(0.5f)
                                                .centerCrop()
                                                .bitmapTransform(new BlurTransformation(ShortListedApplicantUserProfile.this, 100))
                                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                .into(mpostImage);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }


                if(dataSnapshot.hasChild("Address")) {

                    String Address = dataSnapshot.child("Address").getValue().toString();
                    mpostLocation.setText(Address);
                }
                else{

                    mUserLocation.child(user_uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild("CurrentCity")){
                                String Address = dataSnapshot.child("CurrentCity").getValue().toString();
                                mpostLocation.setText(Address);
                            }
                            else{
                                mpostLocation.setText("");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);

        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);


        mchatusertxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user_uid!=null){

                    //set notification badge at MainActivity
                    mUserChatList.child(mAuth.getCurrentUser().getUid()).child("Pressed").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                mUserChatList.child(mAuth.getCurrentUser().getUid()).child("Pressed").setValue("true");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    //Clear all unread messages
                    mChatRoom.child(mAuth.getCurrentUser().getUid()).child(mAuth.getCurrentUser().getUid() + "_" + user_uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                mChatRoom.child(mAuth.getCurrentUser().getUid()).child(mAuth.getCurrentUser().getUid() + "_" + user_uid).child("UnreadMessagePressed").setValue("true");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    Intent chatroomintent = new Intent(ShortListedApplicantUserProfile.this, ChatRoom.class);
                    chatroomintent.putExtra("post_uid",user_uid);
                    startActivity(chatroomintent);
                }
            }
        });

        mpostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(CoverImage != null) {

                    final Dialog nagDialog = new Dialog(ShortListedApplicantUserProfile.this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
                    nagDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                    nagDialog.setContentView(R.layout.preview_image);
                    RelativeLayout mRlay = (RelativeLayout) nagDialog.findViewById(R.id.Rlay);
                    final TouchImageView ivPreview = (TouchImageView) nagDialog.findViewById(R.id.iv_preview_image);

                    Glide.with(getApplicationContext()).load(CoverImage)
                            .thumbnail(0.5f)
                            .fitCenter()
                            .error(R.drawable.loadingerror3)
                            .placeholder(R.drawable.loading_spinner)
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(ivPreview);

                    //Picasso.with(getApplicationContext()).load(post_image).into(ivPreview);

                    mRlay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            nagDialog.dismiss();
                        }
                    });
                    nagDialog.show();

                }
            }
        });

        mprofilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(post_userimage != null) {
                    if (post_userimage.equals("default")) {

                    } else {
                        PhotoViewAttacher pAttacher;
                        final Dialog nagDialog = new Dialog(ShortListedApplicantUserProfile.this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
                        nagDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                        nagDialog.setContentView(R.layout.preview_image);
                        RelativeLayout mRlay = (RelativeLayout) nagDialog.findViewById(R.id.Rlay);
                        final TouchImageView ivPreview = (TouchImageView) nagDialog.findViewById(R.id.iv_preview_image);

                        Glide.with(getApplicationContext()).load(post_userimage)
                                .thumbnail(0.5f)
                                .fitCenter()
                                .error(R.drawable.loadingerror3)
                                .placeholder(R.drawable.loading_spinner)
                                .dontAnimate()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(ivPreview);

                        //Picasso.with(getApplicationContext()).load(post_image).into(ivPreview);

                        mRlay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                nagDialog.dismiss();
                            }
                        });
                        nagDialog.show();
                    }
                }
            }
        });
    }



    private void askrateuser(){

        Log.d(TAG, "askrateuser");

        prefs = ShortListedApplicantUserProfile.this.getSharedPreferences("progress", MODE_PRIVATE);
        int reviewUsedCount_shortlisted = prefs.getInt("reviewUsedCount_shortlisted",0);
        reviewUsedCount_shortlisted++;
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("reviewUsedCount_shortlisted", reviewUsedCount_shortlisted);
        editor.apply();

        Log.d(TAG, "reviewUsedCount_shortlisted " + reviewUsedCount_shortlisted);

        if (reviewUsedCount_shortlisted == 1 || reviewUsedCount_shortlisted==10 || reviewUsedCount_shortlisted==20 || reviewUsedCount_shortlisted==30 || reviewUsedCount_shortlisted==40 || reviewUsedCount_shortlisted==50){
            final Dialog dialog = new Dialog(ShortListedApplicantUserProfile.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setContentView(R.layout.applicantsdialog);

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.CENTER;

            dialog.getWindow().setAttributes(lp);

            Button cancelbtn = (Button) dialog.findViewById(R.id.cancelBtn);
            cancelbtn.setVisibility(View.GONE);
            TextView mdialogtxt = (TextView) dialog.findViewById(R.id.dialogtxt);
            Button hirebtn = (Button) dialog.findViewById(R.id.hireBtn);

            hirebtn.setText("Got It");
            hirebtn.setTextColor(Color.parseColor("#ff669900"));
            mdialogtxt.setText("You are now able to leave feedback and rating for each other at 'REVIEW' tab");

            dialog.show();

            hirebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    dialog.dismiss();
                }
            });
        }
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
                    return OtherUserDetails.newInstance(user_uid);
                case 1:
                    return OtherUserPosted.newInstance(user_uid);
                case 2:
                    return com.zjheng.jobseed.jobseed.UserProfileScene.UserRatingTab.newInstance(user_uid);
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
            // Show 3 total pages.
            return 3;
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            try{
                super.finishUpdate(container);
            } catch (NullPointerException nullPointerException){
                System.out.println("Catch the NullPointerException in FragmentPagerAdapter.finishUpdate");
            }
        }


        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "PROFILE";
                case 1:
                    return "POST";
                case 2:
                    return "REVIEW";
            }
            return null;
        }
    }
}
