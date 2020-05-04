package com.zjheng.jobseed.jobseed.ApplicantsScene;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.zjheng.jobseed.jobseed.MessageScene.ChatRoom;
import com.zjheng.jobseed.jobseed.OtherUserScene.OtherUserDetails;
import com.zjheng.jobseed.jobseed.OtherUserScene.OtherUserPosted;
import com.zjheng.jobseed.jobseed.OtherUserScene.OtherUserTalents;
import com.zjheng.jobseed.jobseed.R;
import com.zjheng.jobseed.jobseed.CustomUIClass.TouchImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.glide.transformations.BlurTransformation;
import uk.co.senab.photoview.PhotoViewAttacher;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ApplicantUserProfile extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mUserAccount, mUserInfo, mChatRoom, mUserChatList, mUserLocation, mUserActivities
            ,mUserShortlistNotification, mUserReview, mUserPostedShortlistedApplicants, mUserPosted, mUserPostedPendingApplicants;

    private ImageView mpostImage;
    private TextView mpostName, mpostLocation;
    private TextView mchatusertxt;
    private Toolbar mToolbar;
    private CircleImageView mprofilepic;
    private LinearLayout mrejectedLay, mshortlistedLay, mchoicesLay, mhiredLay;

    private String user_uid, post_userimage, post_key, user_name, userimage, currentusername, currentuser_uid, CoverImage;
    private String city, post_title, post_desc, showApplicantStatus;

    private CardView mrejectcardview, mshortlistcardview, mrejectshortlistcardview, mhireshortlistcardview;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout tabLayout;

    private RatingBar mRatingBar;
    private SharedPreferences prefs;
    private int rateval;
    private TextView mratingtxt;
    private long reviewcount5 = 0, reviewcount4 = 0, reviewcount3 = 0, reviewcount2 = 0 , reviewcount1 = 0, totalreviewcount = 0;
    private int rateable = 1;

    private static final String TAG = "ApplicantUserProfile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applicantprofile);

        user_uid = getIntent().getStringExtra("user_uid");
        post_key = getIntent().getStringExtra("post_key");
        city = getIntent().getStringExtra("city");
        post_title = getIntent().getStringExtra("post_title");
        post_desc = getIntent().getStringExtra("post_desc");
        showApplicantStatus = getIntent().getStringExtra("applicant_status");

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
        currentuser_uid = mAuth.getCurrentUser().getUid();

        mUserInfo =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserInfo");

        mUserAccount = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserAccount");

        mChatRoom =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("ChatRoom");

        mUserChatList =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserChatList");

        mUserPosted =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserPosted");

        mUserLocation =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserLocation");

        mUserActivities =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserActivities");

        mUserPostedShortlistedApplicants =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserPostedShortlistedApplicants");

        mUserPostedPendingApplicants =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserPostedPendingApplicants");

        mUserShortlistNotification = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("ShortlistedNotification");

        mUserReview = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserReview");

        mpostImage = (ImageView) findViewById(R.id.postImage);
        mprofilepic = (CircleImageView) findViewById(R.id.profilepic);

        mpostName = (TextView) findViewById(R.id.postName);
        mpostLocation = (TextView) findViewById(R.id.postLocation);

        mrejectcardview = (CardView) findViewById(R.id.rejectcardview);
        mshortlistcardview = (CardView) findViewById(R.id.shortlistcardview);
        mrejectshortlistcardview = (CardView) findViewById(R.id.rejectshortlistcardview);
        mhireshortlistcardview = (CardView) findViewById(R.id.hireshortlistcardview);

        mrejectedLay = (LinearLayout) findViewById(R.id.rejectedLay);
        mshortlistedLay = (LinearLayout) findViewById(R.id.shortlistedLay);
        mhiredLay = (LinearLayout) findViewById(R.id.hiredLay);
        mchoicesLay = (LinearLayout) findViewById(R.id.choicesLay);

        mratingtxt = (TextView) findViewById(R.id.ratetxt);
        mRatingBar = (RatingBar) findViewById(R.id.userratingbar);

        mchatusertxt = (TextView) findViewById(R.id.chatusertxt);

        if(user_uid.equals(mAuth.getCurrentUser().getUid())){
            mchatusertxt.setEnabled(false);
        }
        if (showApplicantStatus.equals("1")) {
            mchoicesLay.setVisibility(VISIBLE);
            mshortlistedLay.setVisibility(GONE);
            mhiredLay.setVisibility(GONE);
        }
        else if (showApplicantStatus.equals("2")) {
            mchoicesLay.setVisibility(GONE);
            mshortlistedLay.setVisibility(VISIBLE);
            mhiredLay.setVisibility(GONE);
        }
        else if (showApplicantStatus.equals("3")) {
            mhiredLay.setVisibility(VISIBLE);
            mchoicesLay.setVisibility(GONE);
            mshortlistedLay.setVisibility(GONE);
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
                if(dataSnapshot.hasChild("Name")) {
                    currentusername = dataSnapshot.child("Name").getValue().toString();
                }

                else{
                    mUserAccount.child(mAuth.getCurrentUser().getUid()).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            currentusername = dataSnapshot.getValue().toString();
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


        /*mUserActivities.child(mAuth.getCurrentUser().getUid()).child("Posted").child(post_key).child("applicants").child(user_uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild("shortlisted") && dataSnapshot.hasChild("rejected")){

                    String shortlistedval = dataSnapshot.child("shortlisted").getValue().toString();
                    String rejectedval = dataSnapshot.child("rejected").getValue().toString();

                    if(shortlistedval!=null && rejectedval!=null) {
                        if (shortlistedval.equals("true")) {
                            mchoicesLay.setVisibility(GONE);
                            mshortlistedLay.setVisibility(VISIBLE);
                            mrejectedLay.setVisibility(GONE);
                        } else if (rejectedval.equals("true")) {
                            mchoicesLay.setVisibility(GONE);
                            mshortlistedLay.setVisibility(GONE);
                            mrejectedLay.setVisibility(VISIBLE);
                        } else {
                            mchoicesLay.setVisibility(VISIBLE);
                            mshortlistedLay.setVisibility(GONE);
                            mrejectedLay.setVisibility(GONE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/

        mUserInfo.child(user_uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild("Name")) {

                    user_name = dataSnapshot.child("Name").getValue().toString();
                    mpostName.setText(user_name);
                }
                else{
                    Log.d(TAG, "user_uid: " + user_uid);
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
                                            .error(R.drawable.profilebg3)
                                            .placeholder(R.drawable.profilebg3)
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
                                        .bitmapTransform(new BlurTransformation(ApplicantUserProfile.this, 100))
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
                                                    .error(R.drawable.profilebg3)
                                                    .placeholder(R.drawable.profilebg3)
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
                                                .bitmapTransform(new BlurTransformation(ApplicantUserProfile.this, 100))
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

                    Intent chatroomintent = new Intent(ApplicantUserProfile.this, ChatRoom.class);
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
                    final Dialog nagDialog = new Dialog(ApplicantUserProfile.this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
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
                        final Dialog nagDialog = new Dialog(ApplicantUserProfile.this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
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

        mshortlistcardview.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // custom dialog
                final Dialog dialog = new Dialog(ApplicantUserProfile.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.applicantsdialog);

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                lp.gravity = Gravity.CENTER;

                dialog.getWindow().setAttributes(lp);

                Button cancelbtn = (Button) dialog.findViewById(R.id.cancelBtn);
                TextView mdialogtxt = (TextView) dialog.findViewById(R.id.dialogtxt);
                Button hirebtn = (Button) dialog.findViewById(R.id.hireBtn);

                hirebtn.setText("SHORTLIST");
                hirebtn.setTextColor(Color.parseColor("#FF008FEE"));
                mdialogtxt.setText("Are you sure you want to shortlist "+user_name);

                dialog.show();

                hirebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mUserActivities.child(user_uid).child("Applied").child(post_key).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                //Check if user applied tab still has the job or not
                                if(dataSnapshot.exists()){
                                    mUserActivities.child(user_uid).child("NewMainNotification").setValue("true");
                                    mUserActivities.child(user_uid).child("NewApplied").setValue("true");
                                    mUserActivities.child(user_uid).child("Applied").child(post_key).child("pressed").setValue("false");
                                    mUserActivities.child(user_uid).child("Applied").child(post_key).child("status").setValue("shortlisted");

                                    DatabaseReference newShortlistNotification = mUserShortlistNotification.child("ShortListed").push();
                                    String shortlistnotificationKey = newShortlistNotification.getKey();

                                    Map<String, Object> notificationData = new HashMap<>();
                                    notificationData.put("ownerUid", mAuth.getCurrentUser().getUid());
                                    notificationData.put("receiverUid", user_uid);
                                    notificationData.put("ownerName", currentusername);
                                    newShortlistNotification.setValue(notificationData);

                                   // mUserAllNotification.child(user_uid).child("ShortListedNotification").child(shortlistnotificationKey).setValue(shortlistnotificationKey);
                                    mUserActivities.child(user_uid).child("ShortListedNotification").child(shortlistnotificationKey).setValue(shortlistnotificationKey).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            askrateuser();
                                            rateable = 1;
                                            mSectionsPagerAdapter.notifyDataSetChanged();
                                        }
                                    });

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        //Add user to UserPostedShortlisted List
                        final Map<String, Object> shortlistedData = new HashMap<>();
                        Long tsLong = System.currentTimeMillis();
                        shortlistedData.put("negatedtime", (-1*tsLong));
                        shortlistedData.put("time", ServerValue.TIMESTAMP);
                        shortlistedData.put("image", post_userimage);
                        shortlistedData.put("name", user_name);
                        mUserPostedShortlistedApplicants.child(mAuth.getCurrentUser().getUid()).child(post_key).child(user_uid).setValue(shortlistedData);

                        //Remove user from UserPendingShortlist List
                        mUserPostedPendingApplicants.child(mAuth.getCurrentUser().getUid()).child(post_key).child(user_uid).removeValue();

                        //Add to user's chat
                        final DatabaseReference OwnerChat = mChatRoom.child(currentuser_uid);
                        final DatabaseReference ReceiverChat = mChatRoom.child(user_uid);
                        final DatabaseReference newReceiverChat = ReceiverChat.child(user_uid+"_"+currentuser_uid).child("ChatList").push();
                        final String newChatListkey = newReceiverChat.getKey();
                        final DatabaseReference newOwnerChat = OwnerChat.child(currentuser_uid+"_"+user_uid).child("ChatList").child(newChatListkey);

                        final Map<String, Object> actionchatData = new HashMap<>();
                        actionchatData.put("negatedtime", (-1*tsLong));
                        actionchatData.put("time", ServerValue.TIMESTAMP);
                        actionchatData.put("actiontitle", "shortlisted");
                        actionchatData.put("ownerid", mAuth.getCurrentUser().getUid());
                        actionchatData.put("jobtitle", post_title);
                        actionchatData.put("jobdescrip", post_desc);
                        actionchatData.put("city", city);
                        actionchatData.put("postkey", post_key);

                        mUserChatList.child(mAuth.getCurrentUser().getUid()).child("UserList").child(user_uid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()) {
                                    actionchatData.put("oldtime", dataSnapshot.child("UserList").child(user_uid).child("time").getValue());
                                    newOwnerChat.setValue(actionchatData);
                                    newReceiverChat.setValue(actionchatData);
                                }
                                else {
                                    actionchatData.put("oldtime", 0);
                                    newOwnerChat.setValue(actionchatData);
                                    newReceiverChat.setValue(actionchatData);
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        mchoicesLay.setVisibility(GONE);
                        mshortlistedLay.setVisibility(VISIBLE);
                        mhiredLay.setVisibility(GONE);
                        mrejectedLay.setVisibility(GONE);

                        dialog.dismiss();
                    }
                });

                cancelbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });

        mrejectcardview.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                rejectuser();
            }
        });

        mrejectshortlistcardview.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                rejectuser();
            }
        });

        mhireshortlistcardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent hireintent = new Intent(ApplicantUserProfile.this, HireForm.class);
                hireintent.putExtra("user_uid",user_uid);
                hireintent.putExtra("post_key",post_key);
                hireintent.putExtra("city",city);
                hireintent.putExtra("update","false");
                startActivityForResult(hireintent, 100);
            }
        });
    }

    private void rejectuser() {

        final Dialog dialog = new Dialog(ApplicantUserProfile.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.applicantsdialog);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(lp);

        Button cancelbtn = (Button) dialog.findViewById(R.id.cancelBtn);
        TextView mdialogtxt = (TextView) dialog.findViewById(R.id.dialogtxt);
        Button reject = (Button) dialog.findViewById(R.id.hireBtn);

        reject.setText("REJECT");
        reject.setTextColor(Color.parseColor("#ffff4444"));
        mdialogtxt.setText("Are you sure you want to reject "+user_name);

        dialog.show();

        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mUserActivities.child(user_uid).child("Applied").child(post_key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Check if user applied tab still has the job or not
                        if(dataSnapshot.exists()){
                            mUserActivities.child(user_uid).child("NewMainNotification").setValue("true");
                            mUserActivities.child(user_uid).child("NewApplied").setValue("true");
                            mUserActivities.child(user_uid).child("Applied").child(post_key).child("pressed").setValue("false");
                            mUserActivities.child(user_uid).child("Applied").child(post_key).child("status").setValue("appliedrejected");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                //This is set to remember user has been rejected once, so user can only re-apply ONE MORE time
                DatabaseReference newrejected = mUserActivities.child(user_uid).child("RejectedApplied").child(post_key).push();
                newrejected.setValue("true");

                //Delete user at UserPostedPendingApplicants List, and UserPostedShortlistedApplicants
                mUserPostedPendingApplicants.child(mAuth.getCurrentUser().getUid()).child(post_key).child(user_uid).removeValue();
                mUserPostedShortlistedApplicants.child(mAuth.getCurrentUser().getUid()).child(post_key).child(user_uid).removeValue();

                //Decrement applicants count
                decrementapplicants();

                //Add to user's chat
                final DatabaseReference OwnerChat = mChatRoom.child(currentuser_uid);
                final DatabaseReference ReceiverChat = mChatRoom.child(user_uid);
                final DatabaseReference newReceiverChat = ReceiverChat.child(user_uid+"_"+currentuser_uid).child("ChatList").push();
                final String newChatListkey = newReceiverChat.getKey();
                final DatabaseReference newOwnerChat = OwnerChat.child(currentuser_uid+"_"+user_uid).child("ChatList").child(newChatListkey);

                final Map<String, Object> actionchatData = new HashMap<>();
                Long tsLong = System.currentTimeMillis();
                actionchatData.put("negatedtime", (-1*tsLong));
                actionchatData.put("time", ServerValue.TIMESTAMP);
                actionchatData.put("actiontitle", "rejected");
                actionchatData.put("ownerid", mAuth.getCurrentUser().getUid());
                actionchatData.put("jobtitle", post_title);
                actionchatData.put("jobdescrip", post_desc);
                actionchatData.put("city", city);
                actionchatData.put("postkey", post_key);

                mUserChatList.child(mAuth.getCurrentUser().getUid()).child("UserList").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            if(dataSnapshot.hasChild(user_uid)){

                                actionchatData.put("oldtime", dataSnapshot.child(user_uid).child("time").getValue());

                                newOwnerChat.setValue(actionchatData);
                                newReceiverChat.setValue(actionchatData);
                            }
                            else{
                                actionchatData.put("oldtime", 0);
                                newOwnerChat.setValue(actionchatData);
                                newReceiverChat.setValue(actionchatData);

                            }
                        }
                        else{
                            newOwnerChat.setValue(actionchatData);
                            newReceiverChat.setValue(actionchatData);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                mchoicesLay.setVisibility(GONE);
                mhiredLay.setVisibility(GONE);
                mshortlistedLay.setVisibility(GONE);
                mrejectedLay.setVisibility(VISIBLE);

                dialog.dismiss();
            }
        });

        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    public void decrementapplicants() {

        mUserPosted.child(mAuth.getCurrentUser().getUid()).child(post_key).child("applicantscount").runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData currentData2) {
                if (currentData2.getValue() != null) {
                    if((Long)currentData2.getValue() == 0){
                        currentData2.setValue(0);
                    }
                    else {
                        currentData2.setValue((Long) currentData2.getValue() - 1);
                    }
                }
                return Transaction.success(currentData2);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });
    }

    private void askrateuser(){

        Log.d(TAG, "askrateuser");

        prefs = ApplicantUserProfile.this.getSharedPreferences("progress", MODE_PRIVATE);
        int reviewUsedCount_applicant = prefs.getInt("reviewUsedCount_applicant",0);
        reviewUsedCount_applicant++;
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("reviewUsedCount_applicant", reviewUsedCount_applicant);
        editor.apply();

        if (reviewUsedCount_applicant==1 || reviewUsedCount_applicant==10 || reviewUsedCount_applicant==20 || reviewUsedCount_applicant==30 || reviewUsedCount_applicant==40 || reviewUsedCount_applicant==50){
            final Dialog dialog = new Dialog(ApplicantUserProfile.this);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == 100) && (resultCode == Activity.RESULT_OK)) {
            mhiredLay.setVisibility(VISIBLE);
            mchoicesLay.setVisibility(GONE);
            mshortlistedLay.setVisibility(GONE);
            mrejectedLay.setVisibility(GONE);

        } else if ((requestCode == 100) && (resultCode == Activity.RESULT_CANCELED)) {
            Log.d(TAG, "nothing hired");
        }
    }
}
