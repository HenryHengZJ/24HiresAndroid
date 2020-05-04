package com.zjheng.jobseed.jobseed.TalentDetails;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.sangcomz.fishbun.define.Define;
import com.zjheng.jobseed.jobseed.CustomObjectClass.UserReview;
import com.zjheng.jobseed.jobseed.CustomUIClass.ClickableViewPager;
import com.zjheng.jobseed.jobseed.MessageScene.ChatRoom;
import com.zjheng.jobseed.jobseed.OtherUserScene.OtherUser;
import com.zjheng.jobseed.jobseed.R;
import com.zjheng.jobseed.jobseed.UserProfileScene.UserRatingTab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.R.attr.category;
import static android.R.attr.path;
import static android.R.attr.x;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.zjheng.jobseed.jobseed.R.id.pendingRlay;
import static com.zjheng.jobseed.jobseed.R.id.reviewcount;
import static com.zjheng.jobseed.jobseed.R.id.reviewnumtxt;
import static com.zjheng.jobseed.jobseed.R.id.travellocationtxt;

public class TalentDetail extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mChatRoom, mUserChatList,mUserBookingMade,
            mUserInfo, mUserAccount, mUserTalentReport, mTalent, mUserTalentReview;
    private Query mQuery;

    private String city, postkey, post_basecity, post_travelcity, post_image, post_title, post_desc, talentowner_uid, post_category;
    private String talent_username, talent_userimage, current_userimage, current_username, current_useruid, category;
    private String maincategory, subcategory;
    private Long reviewcount, reviewstar;

    private RecyclerView mreviewRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private FirebaseRecyclerAdapter<UserReview, BlogViewHolder> firebaseRecyclerAdapter;

    private long reviewcount5 = 0, reviewcount4 = 0, reviewcount3 = 0, reviewcount2 = 0 , reviewcount1 = 0, totalreviewcount = 0;
    private int reviewlimit = 8;

    private TextView mpostTitle;
    private TextView mpostDescrip;
    private TextView mpostCategoryx;
    private TextView musernametxt;
    private TextView mbaselocationtxt;
    private TextView mtravellocationtxt;
    private TextView mreporttxt;
    private TextView mchatusertxt;
    private TextView mdatetxt, mratestxt;
    private TextView mreviewnumtxt;
    private TextView mreviewstartxt;

    private RatingBar muserratingbar;

    private RelativeLayout mpendingRlay;

    private CardView mchatuserCardView, mbookCardView;
    private CardView mseemoreCardView;

    private CircleImageView mprofilepic;

    private ClickableViewPager mAdsViewPager;

    private ArrayList<String> postimagelist;

    private Toolbar mToolbar;

    private ImageButton mshareBtn;

    private ProgressDialog mProgress;

    private static final String TAG = "TalentDetail";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talent_detail);

        mProgress = new ProgressDialog(this);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_other);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setTitle(" ");
        mToolbar.setSubtitle(" ");

        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_other);
        final AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar_toolbar_other);
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
                    collapsingToolbarLayout.setTitle("Talent Details");
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbarLayout.setTitle(" ");//carefull there should a space between double quote otherwise it wont work
                    isShow = false;
                }
            }
        });

        city = getIntent().getStringExtra("city_id");
        postkey = getIntent().getStringExtra("post_id");
        maincategory = getIntent().getStringExtra("maincategory");
        subcategory = getIntent().getStringExtra("subcategory");

        mAuth = FirebaseAuth.getInstance();
        current_useruid = mAuth.getCurrentUser().getUid();

        mTalent = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("Talent");

        mChatRoom = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("ChatRoom");

        mUserChatList = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserChatList");

        mUserInfo = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserInfo");

        mUserAccount = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserAccount");

        mUserTalentReport = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserTalentReport");

        mUserBookingMade = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserBookingMade");

        mUserTalentReview = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserTalentReview");

        mpostTitle = (TextView) findViewById(R.id.postTitle);
        mpostDescrip = (TextView) findViewById(R.id.postDescrip);
        mpostCategoryx = (TextView) findViewById(R.id.postCategoryx);
        musernametxt = (TextView) findViewById(R.id.usernametxt);
        mbaselocationtxt = (TextView) findViewById(R.id.baselocationtxt);
        mtravellocationtxt = (TextView) findViewById(travellocationtxt);
        mreporttxt = (TextView) findViewById(R.id.reporttxt);
        mchatusertxt = (TextView) findViewById(R.id.chatusertxt);
        mdatetxt = (TextView) findViewById(R.id.datetxt);
        mratestxt = (TextView) findViewById(R.id.ratestxt);
        mreviewnumtxt = (TextView) findViewById(R.id.reviewnumtxt);
        mreviewstartxt = (TextView) findViewById(R.id.reviewstartxt);

        mshareBtn = findViewById(R.id.shareBtn);

        mreviewRecyclerView = findViewById(R.id.reviewRecyclerView);
        mreviewRecyclerView.setHasFixedSize(false);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        mreviewRecyclerView.setLayoutManager(mLayoutManager);
        mreviewRecyclerView.setAdapter(new SampleRecycler());

        muserratingbar = findViewById(R.id.userratingbar);

        mseemoreCardView = findViewById(R.id.seemoreCardView);

        mpendingRlay = (RelativeLayout) findViewById(pendingRlay);

        mchatuserCardView = (CardView) findViewById(R.id.chatuserCardView);
        mbookCardView = (CardView) findViewById(R.id.bookCardView);

        mprofilepic = (CircleImageView) findViewById(R.id.profilepic);

        postimagelist = new ArrayList<String>();
        mAdsViewPager = (ClickableViewPager) findViewById(R.id.adscontainer);
        final TalentImageViewPagerAdapter mviewPagerAdapter = new TalentImageViewPagerAdapter("false", postimagelist, TalentDetail.this);
        mAdsViewPager.setAdapter(mviewPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(mAdsViewPager, true);

        mAdsViewPager.setOnItemClickListener(new ClickableViewPager.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                if (post_image != null) {
                    Intent intent = new Intent(TalentDetail.this, TalentImages.class);
                    intent.putExtra("post_image",post_image);
                    intent.putExtra("position",position);
                    startActivity(intent);
                }
            }
        });


        mTalent.child(city).child(maincategory).child(subcategory).child(postkey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                post_title = (String) dataSnapshot.child("title").getValue();
                post_desc = (String) dataSnapshot.child("desc").getValue();
                post_category = (String) dataSnapshot.child("category").getValue();
                post_basecity = (String) dataSnapshot.child("city").getValue();
                post_image = (String) dataSnapshot.child("postimage").getValue();
                post_travelcity = (String) dataSnapshot.child("travelcity").getValue();
                talentowner_uid = (String) dataSnapshot.child("uid").getValue();
                post_image = (String) dataSnapshot.child("postimage").getValue();
                reviewcount = (Long) dataSnapshot.child("reviewcount").getValue();
                reviewstar = (Long) dataSnapshot.child("reviewstar").getValue();

                if (dataSnapshot.hasChild("rates")) {
                    String wages = (String) dataSnapshot.child("rates").getValue();
                    mratestxt.setText(wages);
                } else {
                    mratestxt.setText("Negotiatable");
                }

                if (dataSnapshot.hasChild("dates")) {
                    String date = (String) dataSnapshot.child("dates").getValue();
                    mdatetxt.setText(date);
                } else {
                    mdatetxt.setText("No Specified Availability Dates");
                }

                mpostTitle.setText(post_title);
                mpostDescrip.setText(post_desc);
                mpostCategoryx.setText(post_category);
                mbaselocationtxt.setText(post_basecity);
                mtravellocationtxt.setText(post_travelcity);

                if (reviewcount == 0) {
                    mreviewnumtxt.setText("No Reviews Yet");
                    mreviewstartxt.setVisibility(GONE);
                    muserratingbar.setRating(0);
                    mreviewRecyclerView.setVisibility(GONE);
                }
                else {
                    mreviewnumtxt.setText("(" + String.valueOf(reviewcount) + ") Reviews");
                    mreviewstartxt.setVisibility(VISIBLE);
                    String string_reviewstarint = String.format("%.1f", Double.parseDouble(reviewstar + ""));
                    mreviewstartxt.setText(String.valueOf(string_reviewstarint));
                    muserratingbar.setRating(reviewstar);
                    mreviewRecyclerView.setVisibility(View.VISIBLE);
                }

                if (post_image.contains(" , ")) {
                    String[] separatedimages = post_image.split(" , ");
                    for (int x = 0; x < separatedimages.length; x++) {
                        postimagelist.add(separatedimages[x]);
                        Log.e(TAG, "postimagelist " + separatedimages[x]);
                    }

                    mviewPagerAdapter.notifyDataSetChanged();
                } else {
                    postimagelist.add(post_image);
                    Log.e(TAG, "postimagelist " + post_image);
                    mviewPagerAdapter.notifyDataSetChanged();
                }

                if (talentowner_uid != null) {

                    loadReview(talentowner_uid);

                    mUserInfo.child(talentowner_uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild("Name")) {
                                talent_username = dataSnapshot.child("Name").getValue().toString();
                                musernametxt.setText(talent_username);
                            } else {
                                mUserAccount.child(talentowner_uid).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        talent_username = dataSnapshot.getValue().toString();
                                        musernametxt.setText(talent_username);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }

                            if (dataSnapshot.hasChild("UserImage")) {
                                talent_userimage = dataSnapshot.child("UserImage").getValue().toString();
                                if (talent_userimage != null) {
                                    if (talent_userimage.equals("default")) {
                                        mprofilepic.setImageResource(R.drawable.defaultprofile_pic);
                                    } else {

                                        if (!TalentDetail.this.isFinishing()) {
                                            Glide.with(getApplicationContext()).load(talent_userimage)
                                                    .thumbnail(0.5f)
                                                    .centerCrop()
                                                    .error(R.drawable.defaultprofile_pic)
                                                    .placeholder(R.drawable.loading_spinner)
                                                    .dontAnimate()
                                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                    .into(mprofilepic);
                                        }
                                    }
                                }
                            } else {
                                mUserAccount.child(talentowner_uid).child("image").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        talent_userimage = dataSnapshot.getValue().toString();
                                        if (talent_userimage != null) {
                                            if (talent_userimage.equals("default")) {
                                                mprofilepic.setImageResource(R.drawable.defaultprofile_pic);
                                            } else {
                                                if (!TalentDetail.this.isFinishing()) {
                                                    Glide.with(getApplicationContext()).load(talent_userimage)
                                                            .thumbnail(0.5f)
                                                            .centerCrop()
                                                            .error(R.drawable.loadingerror3)
                                                            .placeholder(R.drawable.loading_spinner)
                                                            .dontAnimate()
                                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                            .into(mprofilepic);
                                                }
                                            }
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
                }

                if (talentowner_uid.equals(mAuth.getCurrentUser().getUid())) {

                    mbookCardView.setVisibility(GONE);
                    mchatuserCardView.setVisibility(View.INVISIBLE);
                }
                else {

                    mUserBookingMade.child(mAuth.getCurrentUser().getUid()).child(postkey).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                String statusval = dataSnapshot.child("status").getValue().toString();
                                if (statusval.equals("booked")) {
                                    Log.e(TAG, "booked");
                                    mpendingRlay.setVisibility(View.VISIBLE);
                                    mbookCardView.setVisibility(View.GONE);
                                }
                            }
                            else {
                                mpendingRlay.setVisibility(View.GONE);
                                mbookCardView.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    mchatuserCardView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mUserInfo.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild("Name")) {
                    current_username = dataSnapshot.child("Name").getValue().toString();
                }
                else{
                    mUserAccount.child(mAuth.getCurrentUser().getUid()).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                current_username = dataSnapshot.getValue().toString();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

                if (dataSnapshot.hasChild("UserImage")) {
                    current_userimage = dataSnapshot.child("UserImage").getValue().toString();
                } else {
                    mUserAccount.child(mAuth.getCurrentUser().getUid()).child("image").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                current_userimage = dataSnapshot.getValue().toString();
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

        mshareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProgress.setMessage("Waiting..");
                mProgress.setCanceledOnTouchOutside(false);
                mProgress.setCancelable(false);
                mProgress.show();

                String link = "https://24hires.com/?jobpost=" + postkey + "," + city + "," + maincategory + "," + subcategory;
                FirebaseDynamicLinks.getInstance().createDynamicLink()
                        .setLink(Uri.parse(link))
                        .setDynamicLinkDomain("vh87a.app.goo.gl")
                        .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                        .setIosParameters(new DynamicLink.IosParameters.Builder("com.example.ios").build())
                        .setSocialMetaTagParameters(
                                new DynamicLink.SocialMetaTagParameters.Builder()
                                        .setTitle(post_title)
                                        .setDescription(post_desc)
                                        .setImageUrl(Uri.parse(postimagelist.get(0)))
                                        .build())
                        .buildShortDynamicLink()
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                mProgress.dismiss();
                                Toast.makeText(getApplicationContext(),"Sharing failed. Please try again later", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnSuccessListener(new OnSuccessListener<ShortDynamicLink>() {
                            @Override
                            public void onSuccess(ShortDynamicLink shortDynamicLink) {

                                mProgress.dismiss();

                                Uri mShareUrl = shortDynamicLink.getShortLink();

                                String subject = String.format("%s wants you to check out this talent", current_username);
                                String invitationLink = mShareUrl.toString();
                                String msg = "Hey, Check out this talent on 24Hires! "
                                        + invitationLink;

                                Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.setType("text/plain");
                                intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                                intent.putExtra(Intent.EXTRA_TEXT, msg);
                                if (intent.resolveActivity(getPackageManager()) != null) {
                                    startActivity(Intent.createChooser(intent, "Share using"));
                                }
                            }
                        });

            }
        });

        mseemoreCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (talentowner_uid != null ) {
                    Intent reviewintent = new Intent(TalentDetail.this, SeeMoreReview.class);
                    reviewintent.putExtra("talentowner_uid", talentowner_uid);
                    reviewintent.putExtra("postkey", postkey);
                    startActivity(reviewintent);
                }
            }
        });


        mreporttxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(TalentDetail.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.report_dialog);

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                lp.gravity = Gravity.CENTER;

                dialog.getWindow().setAttributes(lp);

                CardView mspamcardview = (CardView) dialog.findViewById(R.id.spamcardview);
                CardView mirrelavantcardview = (CardView) dialog.findViewById(R.id.irrelavantcardview);
                CardView minappropriatecardview = (CardView) dialog.findViewById(R.id.inappropriatecardview);

                dialog.show();

                mspamcardview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mUserTalentReport.child(mAuth.getCurrentUser().getUid()).child("Post").child("Spam").child(city).setValue(postkey);
                        showreportdialog();

                        dialog.dismiss();
                    }
                });

                mirrelavantcardview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mUserTalentReport.child(mAuth.getCurrentUser().getUid()).child("Post").child("Irrelavant").child(city).setValue(postkey);
                        showreportdialog();

                        dialog.dismiss();
                    }
                });

                minappropriatecardview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mUserTalentReport.child(mAuth.getCurrentUser().getUid()).child("Post").child("Inappropriate").child(city).setValue(postkey);
                        showreportdialog();

                        dialog.dismiss();
                    }
                });
            }
        });


        mprofilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (talentowner_uid != null) {

                    Intent otheruserintent = new Intent(TalentDetail.this, OtherUser.class);
                    otheruserintent.putExtra("user_uid", talentowner_uid);
                    startActivity(otheruserintent);

                }
            }
        });


        mchatuserCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (talentowner_uid != null && talent_userimage != null) {

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
                    mChatRoom.child(mAuth.getCurrentUser().getUid()).child(mAuth.getCurrentUser().getUid() + "_" + talentowner_uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                mChatRoom.child(mAuth.getCurrentUser().getUid()).child(mAuth.getCurrentUser().getUid() + "_" + talentowner_uid).child("UnreadMessagePressed").setValue("true");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    Intent chatroomintent = new Intent(TalentDetail.this, ChatRoom.class);
                    chatroomintent.putExtra("post_uid", talentowner_uid);
                    chatroomintent.putExtra("owner_image", current_userimage);
                    chatroomintent.putExtra("receiver_image", talent_userimage);
                    startActivity(chatroomintent);
                }
            }


        });

        mbookCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent bookintent = new Intent(TalentDetail.this, BookingForm.class);
                bookintent.putExtra("talentowner_uid", talentowner_uid);
                bookintent.putExtra("post_key", postkey);
                bookintent.putExtra("city", city);
                bookintent.putExtra("maincategory", maincategory);
                bookintent.putExtra("subcategory", subcategory);
                bookintent.putExtra("update", "false");
                startActivityForResult(bookintent,1111);
            }
        });
    }

    private void loadReview(final String talentowner_uid) {
        mUserTalentReview.child(talentowner_uid).child(postkey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("Review")){

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
                    Log.e(TAG, "totalreviewcount" + totalreviewcount);

                    if(totalreviewcount>reviewlimit){
                        mseemoreCardView.setVisibility(VISIBLE);
                        mQuery = mUserTalentReview.child(talentowner_uid).child(postkey).child("Review").limitToFirst(reviewlimit);
                        setupview(mQuery);
                        mreviewRecyclerView.setAdapter(firebaseRecyclerAdapter);
                    }
                    else{
                        mseemoreCardView.setVisibility(VISIBLE);
                        mQuery = mUserTalentReview.child(talentowner_uid).child(postkey).child("Review");
                        setupview(mQuery);
                        mreviewRecyclerView.setAdapter(firebaseRecyclerAdapter);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);

        MenuItem itemSettings = menu.findItem(R.id.menuSettings);
        itemSettings.setVisible(false);

        MenuItem itemSearch = menu.findItem(R.id.menuSearch);
        itemSearch.setVisible(false);

        MenuItem itemPublish = menu.findItem(R.id.menuPublish);
        itemPublish.setVisible(false);

        MenuItem itemSave = menu.findItem(R.id.menuSave);
        itemSave.setVisible(false);

        MenuItem item = menu.findItem(R.id.menuSearch2);
        item.setVisible(false);

        return true;
    }

    private void showreportdialog(){
        final Dialog dialog = new Dialog(TalentDetail.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.applicantsdialog);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(lp);

        Button cancelbtn = (Button) dialog.findViewById(R.id.cancelBtn);
        cancelbtn.setVisibility(GONE);
        TextView mdialogtxt = (TextView) dialog.findViewById(R.id.dialogtxt);
        Button okbtn = (Button) dialog.findViewById(R.id.hireBtn);

        okbtn.setText("OK");
        okbtn.setTextColor(Color.parseColor("#0e52a5"));
        mdialogtxt.setText("Thanks for making our community better with this feedback. We will take appropriate action against abuse.");

        dialog.show();

        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });
    }

    // SampleHolder.java
    public class SampleHolder extends RecyclerView.ViewHolder {
        public SampleHolder(View itemView) {
            super(itemView);
        }
    }

    // SampleRecycler.java
    public class SampleRecycler extends RecyclerView.Adapter<SampleHolder> {
        @Override
        public SampleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(SampleHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }


    private void setupview(Query mQuery){
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<UserReview, BlogViewHolder>(
                UserReview.class,
                R.layout.review_row,
                BlogViewHolder.class,
                mQuery
        ) {

            @Override
            protected void populateViewHolder(BlogViewHolder viewHolder, UserReview model, final int position) {

                final String userid = getRef(position).getKey();

                viewHolder.setusername(model.getusername());
                viewHolder.setreviewmessage(model.getreviewmessage());
                viewHolder.settime(model.gettime());
                viewHolder.setuserimage(TalentDetail.this, model.getuserimage());
                viewHolder.setratingbar(model.getrating());

                /*viewHolder.userimagepic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent otheruserintent = new Intent(context, OtherUser.class);
                        otheruserintent.putExtra("user_uid",userid);
                        startActivity(otheruserintent);
                    }
                });*/
            }
        };
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder{

        View mView;
        CircleImageView userimagepic;
        TextView txtTime, txtuserComment, postName;
        RatingBar muserratingbar;

        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            userimagepic = (CircleImageView) mView.findViewById(R.id.userimage);
            txtTime = (TextView) mView.findViewById(R.id.txtTime);
            postName = (TextView) mView.findViewById(R.id.postName);
            txtuserComment = (TextView) mView.findViewById(R.id.txtuserComment);
            muserratingbar = (RatingBar) mView.findViewById(R.id.userratingbar);

        }

        public void setusername(String username){
            postName.setText(username);
        }
        public void setreviewmessage(String reviewmessage){
            if(reviewmessage.equals("none")){
                txtuserComment.setVisibility(GONE);
                txtuserComment.setText("");
            }
            else{
                txtuserComment.setVisibility(VISIBLE);
                txtuserComment.setText(reviewmessage);
            }
        }
        public void setratingbar(int ratingval){
            muserratingbar.setRating(ratingval);
        }
        public void settime(Long time){
            Long tsLong = System.currentTimeMillis();
            CharSequence result = DateUtils.getRelativeTimeSpanString(time, tsLong, DateUtils.SECOND_IN_MILLIS);
            txtTime.setText(result);
        }
        public void setuserimage(Context ctx, String userimage){
            if (userimage != null) {

                Log.e(TAG, "model.userimage()" +userimage);

                if (!userimage.equals("default")) {
                    Glide.with(ctx).load(userimage)
                            .thumbnail(0.5f)
                            .centerCrop()
                            .error(R.drawable.defaultprofile_pic)
                            .placeholder(R.drawable.loading_spinner)
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(userimagepic);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == 1111) && (resultCode == Activity.RESULT_OK)) {
            
            mpendingRlay.setVisibility(View.VISIBLE);
            mbookCardView.setVisibility(GONE);
            Toast.makeText(getApplicationContext(),"You have successfully booked " + talent_username + "'s talent!", Toast.LENGTH_SHORT).show();

        }
        else if ((requestCode == 1111) && (resultCode == Activity.RESULT_CANCELED)) {
            Log.d(TAG, "cancel here");
        }
    }
}

