package com.zjheng.jobseed.jobseed.OtherUserScene;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.zjheng.jobseed.jobseed.MessageScene.ChatRoom;
import com.zjheng.jobseed.jobseed.R;
import com.zjheng.jobseed.jobseed.CustomUIClass.TouchImageView;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.glide.transformations.BlurTransformation;
import uk.co.senab.photoview.PhotoViewAttacher;

public class OtherUser extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mUserAccount, mUserInfo, mChatRoom, mUserChatList, mUserLocation, mUserReview;

    private ImageView mpostImage;
    private TextView mpostName, mpostLocation;
    private TextView mchatusertxt;
    private Toolbar mToolbar;
    private CircleImageView mprofilepic;

    private String user_uid, user_name, post_userimage, userimage, CoverImage;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout tabLayout;

    private SharedPreferences prefs;
    private RatingBar mRatingBar;
    private int rateval, ratestar = 0;
    private TextView mratingtxt;
    private long reviewcount5 = 0, reviewcount4 = 0, reviewcount3 = 0, reviewcount2 = 0 , reviewcount1 = 0, totalreviewcount = 0;
    private Long reviewcount;
    private int reviewcounter, reviewlimit = 20;

    private String ReviewCount, reducedReviewCount;
    private ProgressDialog mProgress;

    private static final String TAG = "OtherUser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_user);

        user_uid = getIntent().getStringExtra("user_uid");

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

        mUserAccount = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserAccount");

        mChatRoom =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("ChatRoom");

        mUserChatList =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserChatList");

        mUserLocation =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserLocation");

        mUserReview = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserReview");

        mpostImage = (ImageView) findViewById(R.id.postImage);
        mprofilepic = (CircleImageView) findViewById(R.id.profilepic);
        mpostName = (TextView) findViewById(R.id.postName);
        mpostLocation = (TextView) findViewById(R.id.postLocation);
        mchatusertxt = (TextView) findViewById(R.id.chatusertxt);

        mRatingBar = (RatingBar) findViewById(R.id.userratingbar);
        mratingtxt = (TextView) findViewById(R.id.ratetxt);

        if(user_uid.equals(mAuth.getCurrentUser().getUid())){
            mchatusertxt.setEnabled(false);
        }

        mUserReview.child(user_uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.hasChild("Rate5")){
                        reviewcount5 = (Long)dataSnapshot.child("Rate5").getValue();
                    }
                    if(dataSnapshot.hasChild("Rate4")){
                        reviewcount4 = (Long)dataSnapshot.child("Rate4").getValue();
                    }
                    if(dataSnapshot.hasChild("Rate3")){
                        reviewcount3 = (Long)dataSnapshot.child("Rate3").getValue();
                    }
                    if(dataSnapshot.hasChild("Rate2")){
                        reviewcount2 = (Long)dataSnapshot.child("Rate2").getValue();
                    }
                    if(dataSnapshot.hasChild("Rate1")){
                        reviewcount1 = (Long)dataSnapshot.child("Rate1").getValue();
                    }
                    totalreviewcount = reviewcount5+reviewcount4+reviewcount3+reviewcount2+reviewcount1;
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

        mUserInfo.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild("UserImage")) {
                    userimage = dataSnapshot.child("UserImage").getValue().toString();
                }

                else{
                    mUserAccount.child(mAuth.getCurrentUser().getUid()).child("image").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            userimage = dataSnapshot.getValue().toString();
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
                            Glide.with(getApplicationContext()).load(post_userimage)
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
                                    Glide.with(getApplicationContext()).load(CoverImage)
                                            .thumbnail(0.5f)
                                            .error(R.color.colorPrimaryDark)
                                            .placeholder(R.color.colorPrimaryDark)
                                            .dontAnimate()
                                            .centerCrop()
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .into(mpostImage);
                                }
                            }
                            else{
                                 Glide.with(getApplicationContext()).load(post_userimage)
                                    .thumbnail(0.5f)
                                    .centerCrop()
                                    .bitmapTransform(new BlurTransformation(OtherUser.this, 100))
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
                                    Glide.with(getApplicationContext()).load(post_userimage)
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
                                            Glide.with(getApplicationContext()).load(CoverImage)
                                                    .thumbnail(0.5f)
                                                    .error(R.color.colorPrimaryDark)
                                                    .placeholder(R.color.colorPrimaryDark)
                                                    .dontAnimate()
                                                    .centerCrop()
                                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                    .into(mpostImage);
                                        }
                                    }
                                    else{
                                        Glide.with(getApplicationContext()).load(post_userimage)
                                            .thumbnail(0.5f)
                                            .centerCrop()
                                            .bitmapTransform(new BlurTransformation(OtherUser.this, 100))
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

        mchatusertxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user_uid!=null && post_userimage!=null){

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

                    Intent chatroomintent = new Intent(OtherUser.this, ChatRoom.class);
                    chatroomintent.putExtra("post_uid",user_uid);
                    chatroomintent.putExtra("owner_image",userimage);
                    chatroomintent.putExtra("receiver_image",post_userimage);
                    startActivity(chatroomintent);
                }
            }
        });

        mpostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CoverImage != null) {
                    final Dialog nagDialog = new Dialog(OtherUser.this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
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
                        final Dialog nagDialog = new Dialog(OtherUser.this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
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

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);

        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);

    }

    private void askrateuser(){

        Log.d(TAG, "askrateuser");

        prefs = OtherUser.this.getSharedPreferences("progress", MODE_PRIVATE);
        int reviewUsedCount_otheruser = prefs.getInt("reviewUsedCount_otheruser",0);
        reviewUsedCount_otheruser++;
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("reviewUsedCount_otheruser", reviewUsedCount_otheruser);
        editor.apply();

        Log.d(TAG, "reviewUsedCount_otheruser " + reviewUsedCount_otheruser);

        if (reviewUsedCount_otheruser== 1 || reviewUsedCount_otheruser==10 || reviewUsedCount_otheruser==20 || reviewUsedCount_otheruser==30 || reviewUsedCount_otheruser==40 || reviewUsedCount_otheruser==50){
            final Dialog dialog = new Dialog(OtherUser.this);
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
                    return OtherUserTalents.newInstance(user_uid);
                case 3:
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
            return 4;
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
                    return "JOB";
                case 2:
                    return "TALENT";
                case 3:
                    return "REVIEW";
            }
            return null;
        }
    }
}
