package com.zjheng.jobseed.jobseed;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaView;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.zjheng.jobseed.jobseed.ApplicantsScene.ApplicantUserProfile;
import com.zjheng.jobseed.jobseed.CustomUIClass.TouchImageView;
import com.zjheng.jobseed.jobseed.MessageScene.ChatRoom;
import com.zjheng.jobseed.jobseed.OtherUserScene.OtherUser;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.senab.photoview.PhotoViewAttacher;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.zjheng.jobseed.jobseed.R.id.rejectedRlay;

public class JobDetail extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mJob, mUserActivities, mChatRoom, mUserChatList, mUserPostedPendingApplicants,
            mUserInfo, mUserAccount, mUserApplyNotification, mUserReport, mUserPosted;

    private String city, postkey, post_location, post_image, post_title, post_desc, post_company, jobowner_uid, post_category;
    private String current_username, current_userimage, closed, appliedkey, receiver_userimage, current_useruid;
    private String shortlistedval,rejectedval, statusval;
    private Double latitude, longitude;

    private TextView mpostTitle;
    private TextView mpostDescrip;
    private TextView mpostCategoryx;
    private TextView mpostCompany;
    private TextView mtimetxt;
    private TextView musernametxt;
    private TextView mlocationtxt;
    private TextView mreporttxt;
    private TextView mchatusertxt;
    private ImageView mmapimg;
    private TextView mdatetxt, mwagesstxt;

    private RelativeLayout mappliedRlay;
    private RelativeLayout mshortlistedRlay;
    private RelativeLayout mrejectedRlay;
    private LinearLayout msaveapplyLay;
    private RelativeLayout mhiredRlay;
    private RelativeLayout mrejectedOfferRlay;

    private CardView msaveBtn;
    private CardView mchatuserCardView;
    private CardView mapplyBtn;
    private CardView mrejectedBtn;
    private TextView msavetxt,mapplytxt ;

    private ImageView mpostImage, mclosedimage, mstreetviewimg;
    private CircleImageView mprofilepic;
    private TextView mclosedBtn;
    private ImageButton mshareBtn;

    private Toolbar mToolbar;
    private View mclosedview;

    private Uri mShareUrl;

    private ProgressDialog mProgress;

    private FirebaseAnalytics mFirebaseAnalytics;

    private static final String TAG = "JobDetail";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_detail);
        supportPostponeEnterTransition();

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        mToolbar = (Toolbar)findViewById(R.id.toolbar_other);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setTitle(" ");
        mToolbar.setSubtitle(" ");

        mProgress = new ProgressDialog(this);

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
                    collapsingToolbarLayout.setTitle("Job Details");
                    isShow = true;
                } else if(isShow) {
                    collapsingToolbarLayout.setTitle(" ");//carefull there should a space between double quote otherwise it wont work
                    isShow = false;
                }
            }
        });

        city = getIntent().getStringExtra("city_id");
        postkey = getIntent().getStringExtra("post_id");

        mAuth = FirebaseAuth.getInstance();
        current_useruid = mAuth.getCurrentUser().getUid();

        mJob =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("Job");


        mUserActivities =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserActivities");
        mUserActivities.child(current_useruid).child("Applied").child(postkey).keepSynced(true);

        mUserPostedPendingApplicants =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserPostedPendingApplicants");

        mChatRoom =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("ChatRoom");


        mUserChatList =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserChatList");

        mUserInfo =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserInfo");


        mUserAccount = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserAccount");


        mUserApplyNotification = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("ApplyNotification");

        mUserPosted =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserPosted");

        mUserReport =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserReport");


        mpostTitle = (TextView) findViewById(R.id.postName);
        mpostDescrip = (TextView) findViewById(R.id.postDescrip);
        mpostCategoryx = (TextView) findViewById(R.id.postCategoryx);
        mpostCompany = (TextView) findViewById(R.id.postCompany);
        mtimetxt = (TextView) findViewById(R.id.timetxt);
        musernametxt = (TextView) findViewById(R.id.usernametxt);
        mlocationtxt = (TextView) findViewById(R.id.locationtxt);
        mreporttxt = (TextView) findViewById(R.id.reporttxt);
        mchatusertxt =(TextView) findViewById(R.id.chatusertxt);
        msavetxt =(TextView) findViewById(R.id.savetxt);
        mapplytxt =(TextView) findViewById(R.id.applytxt);
        mdatetxt =(TextView) findViewById(R.id.datetxt);
        mwagesstxt =(TextView) findViewById(R.id.wagesstxt);

        mshareBtn = findViewById(R.id.shareBtn);

        mappliedRlay = (RelativeLayout) findViewById(R.id.appliedRlay);
        mrejectedRlay = (RelativeLayout) findViewById(rejectedRlay);
        mshortlistedRlay = (RelativeLayout) findViewById(R.id.shortlistedRlay);
        msaveapplyLay = (LinearLayout) findViewById(R.id.saveapplyLay);
        mrejectedOfferRlay = (RelativeLayout) findViewById(R.id.rejectedOfferRlay);
        mhiredRlay = (RelativeLayout) findViewById(R.id.hiredRlay);

        msaveBtn = (CardView) findViewById(R.id.saveBtn);
        mapplyBtn = (CardView) findViewById(R.id.applyBtn);
        mrejectedBtn = (CardView) findViewById(R.id.rejectedBtn);
        mchatuserCardView = (CardView) findViewById(R.id.chatuserCardView);

        mpostImage = (ImageView) findViewById(R.id.postImage);
        mclosedimage = (ImageView) findViewById(R.id.closedimage);
        mprofilepic = (CircleImageView) findViewById(R.id.profilepic);
        mclosedBtn = (TextView) findViewById(R.id.closedBtn);
        mclosedview = (View) findViewById(R.id.closedview);

        mmapimg = (ImageView) findViewById(R.id.mapimg);

        mstreetviewimg = (ImageView) findViewById(R.id.streetviewimg);

        mJob.child(city).child(postkey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                post_title = (String) dataSnapshot.child("title").getValue();
                post_desc = (String) dataSnapshot.child("desc").getValue();
                post_category = (String) dataSnapshot.child("category").getValue();
                post_company = (String) dataSnapshot.child("company").getValue();
                post_image = (String) dataSnapshot.child("postimage").getValue();
                post_location = (String) dataSnapshot.child("fulladdress").getValue();
                jobowner_uid = (String) dataSnapshot.child("uid").getValue();
                latitude = (Double) dataSnapshot.child("latitude").getValue();
                longitude = (Double) dataSnapshot.child("longitude").getValue();
                closed = (String) dataSnapshot.child("closed").getValue();

                if(dataSnapshot.hasChild("wages")){
                    String wages = (String) dataSnapshot.child("wages").getValue();
                    if(!wages.equals("none")){
                        mwagesstxt.setText(wages);
                    }
                    else{
                        mwagesstxt.setText("Wages are not disclosed");
                    }
                }
                if(dataSnapshot.hasChild("date")){
                    String date = (String) dataSnapshot.child("date").getValue();
                    if(!date.equals("none")){
                        mdatetxt.setText(date);
                    }
                    else{
                        mdatetxt.setText("No Specified Date");
                    }

                }

                if(dataSnapshot.hasChild("time")){
                    Long time = (Long) dataSnapshot.child("time").getValue();
                    Long tsLong = System.currentTimeMillis();
                    CharSequence result = DateUtils.getRelativeTimeSpanString(time, tsLong, 0);
                    mtimetxt.setText("Created: "+result+" by");}

                mpostTitle.setText(post_title);
                mpostDescrip.setText(post_desc);
                mpostCategoryx.setText(post_category);
                mpostCompany.setText(post_company);
                mlocationtxt.setText(post_location);

                if(latitude!= null && longitude!= null) {
                    String streetURL = "https://maps.googleapis.com/maps/api/streetview?size=400x200&location="+latitude+","+longitude+"&fov=90&heading=235&pitch=10";

                    String mapURL = "http://maps.google.com/maps/api/staticmap?center=" + latitude + "," + longitude + "&markers=color:0xff0000%7Clabel:%7C"+latitude+","+longitude+"&zoom=14&size=400x200";

                    if(!JobDetail.this.isFinishing()){
                        Glide.with(getApplicationContext()).load(mapURL)
                                .centerCrop()
                                .error(R.drawable.loadingerror3)
                                .placeholder(R.drawable.loading_spinner)
                                .dontAnimate()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(mmapimg);

                        Glide.with(getApplicationContext()).load(streetURL)
                                .centerCrop()
                                .error(R.drawable.loadingerror3)
                                .placeholder(R.drawable.loading_spinner)
                                .dontAnimate()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(mstreetviewimg);
                    }
                }

                if(post_image != null){
                    if(!JobDetail.this.isFinishing()) {
                        Glide.with(getApplicationContext()).load(post_image)
                                .centerCrop()
                                .dontAnimate()
                                .listener(new RequestListener<String, GlideDrawable>() {
                                    @Override
                                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                        supportStartPostponedEnterTransition();
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                        supportStartPostponedEnterTransition();
                                        return false;
                                    }
                                })
                                .into(mpostImage);

                    }
                }

                if(jobowner_uid!=null){
                    mUserInfo.child(jobowner_uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild("Name")) {
                                String receiver_username = (String) dataSnapshot.child("Name").getValue();
                                musernametxt.setText(receiver_username);
                            }
                            else{
                                mUserAccount.child(jobowner_uid).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            String receiver_username = (String) dataSnapshot.getValue();
                                            musernametxt.setText(receiver_username);
                                        }
                                        else {
                                            musernametxt.setText("");
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }

                            if(dataSnapshot.hasChild("UserImage")) {
                                receiver_userimage = (String) dataSnapshot.child("UserImage").getValue();
                                if(receiver_userimage != null) {
                                    if (receiver_userimage.equals("default")) {
                                        mprofilepic.setImageResource(R.drawable.defaultprofile_pic);
                                    } else {

                                        if(!JobDetail.this.isFinishing()){
                                            Glide.with(getApplicationContext()).load(receiver_userimage)
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
                            }

                            else{
                                mUserAccount.child(jobowner_uid).child("image").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        receiver_userimage = dataSnapshot.getValue().toString();
                                        if(receiver_userimage != null) {
                                            if (receiver_userimage.equals("default")) {
                                                mprofilepic.setImageResource(R.drawable.defaultprofile_pic);
                                            } else {
                                                if(!JobDetail.this.isFinishing()) {
                                                    Glide.with(getApplicationContext()).load(receiver_userimage)
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

                if (jobowner_uid.equals(mAuth.getCurrentUser().getUid())){

                    msaveapplyLay.setVisibility(GONE);
                    mchatuserCardView.setVisibility(View.INVISIBLE);

                    if(closed!=null && closed.equals("true")){
                        //Display closed img
                        mclosedimage.setVisibility(VISIBLE);
                        mclosedBtn.setVisibility(VISIBLE);
                        mclosedview.setVisibility(VISIBLE);
                    }
                }
                else {
                    if(closed!=null && closed.equals("true")){
                        //Display closed img
                        mclosedimage.setVisibility(VISIBLE);
                        mclosedBtn.setVisibility(VISIBLE);
                        mclosedview.setVisibility(VISIBLE);
                        mappliedRlay.setVisibility(GONE);
                        msaveapplyLay.setVisibility(GONE);
                        mrejectedRlay.setVisibility(GONE);
                    }
                    else {
                        //Remove job closed img
                        mclosedimage.setVisibility(GONE);
                        mclosedBtn.setVisibility(GONE);
                        mclosedview.setVisibility(GONE);

                        mUserActivities.child(mAuth.getCurrentUser().getUid()).child("Applied").child(postkey).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                //Check if user has applied this job
                                if(dataSnapshot.exists()){

                                    if (dataSnapshot.hasChild("status")) {
                                        statusval = dataSnapshot.child("status").getValue().toString();

                                        if (statusval.equals("applied")) {
                                            mappliedRlay.setVisibility(VISIBLE);
                                        }
                                        else if (statusval.equals("appliedrejected")) {
                                            checkrejectedJob();
                                        }
                                        else if (statusval.equals("shortlisted")) {
                                            mshortlistedRlay.setVisibility(VISIBLE);
                                        }
                                        else if (statusval.equals("acceptedoffer")) {
                                            mhiredRlay.setVisibility(VISIBLE);
                                        }
                                        else if (statusval.equals("rejectedoffer")) {
                                            mrejectedOfferRlay.setVisibility(VISIBLE);
                                        }
                                        else if (statusval.equals("pendingoffer")) {
                                            mshortlistedRlay.setVisibility(VISIBLE);
                                        }
                                        else if (statusval.equals("changedoffer")) {
                                            mshortlistedRlay.setVisibility(VISIBLE);
                                        }
                                        else {
                                            mshortlistedRlay.setVisibility(VISIBLE);
                                        }
                                    }
                                }

                                //If user has not applied to this job, display save and apply btn
                                else{

                                    msaveapplyLay.setVisibility(VISIBLE);

                                    checkrejectedJob();

                                    //Check if user has saved this job or not
                                    mUserActivities.child(mAuth.getCurrentUser().getUid()).child("Saved").child(postkey).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            if(dataSnapshot.exists()){
                                                msavetxt.setText("SAVED");
                                            }
                                            else{
                                                msavetxt.setText("SAVE");
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                    //Check if user has been shortlisted before he/she deleted this applied job post via removeBtn
                                    mUserActivities.child(mAuth.getCurrentUser().getUid()).child("Shortlisted").child(postkey).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            if(dataSnapshot.exists()){
                                                mshortlistedRlay.setVisibility(VISIBLE);
                                                mappliedRlay.setVisibility(GONE);
                                                msaveapplyLay.setVisibility(GONE);
                                                mrejectedRlay.setVisibility(GONE);
                                                mrejectedOfferRlay.setVisibility(GONE);
                                                mhiredRlay.setVisibility(GONE);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                    //Check if user has been Hired before he/she deleted this applied job post via removeBtn
                                    mUserActivities.child(mAuth.getCurrentUser().getUid()).child("Hired").child(postkey).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            if(dataSnapshot.exists()){
                                                mhiredRlay.setVisibility(VISIBLE);
                                                mrejectedOfferRlay.setVisibility(GONE);
                                                mshortlistedRlay.setVisibility(GONE);
                                                mappliedRlay.setVisibility(GONE);
                                                msaveapplyLay.setVisibility(GONE);
                                                mrejectedRlay.setVisibility(GONE);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                    //Check if user has REJECT the hiring offer before he/she deleted this applied job post via removeBtn
                                    mUserActivities.child(mAuth.getCurrentUser().getUid()).child("RejectedOffer").child(postkey).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            if(dataSnapshot.exists()){
                                                mrejectedOfferRlay.setVisibility(VISIBLE);
                                                mhiredRlay.setVisibility(GONE);
                                                mshortlistedRlay.setVisibility(GONE);
                                                mappliedRlay.setVisibility(GONE);
                                                msaveapplyLay.setVisibility(GONE);
                                                mrejectedRlay.setVisibility(GONE);
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

                if(dataSnapshot.hasChild("UserImage")) {
                    current_userimage = dataSnapshot.child("UserImage").getValue().toString();
                }

                else{
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

                String link = "https://24hires.com/?jobpost=" + postkey + "," + city;
                FirebaseDynamicLinks.getInstance().createDynamicLink()
                        .setLink(Uri.parse(link))
                        .setDynamicLinkDomain("vh87a.app.goo.gl")
                        .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                        .setIosParameters(new DynamicLink.IosParameters.Builder("com.jobin24.JobIn24").build())
                        .setSocialMetaTagParameters(
                                new DynamicLink.SocialMetaTagParameters.Builder()
                                        .setTitle(post_title)
                                        .setDescription(post_desc)
                                        .setImageUrl(Uri.parse(post_image))
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

                                mShareUrl = shortDynamicLink.getShortLink();

                                String subject = String.format("%s wants you to check out this job!", current_username);
                                String invitationLink = mShareUrl.toString();
                                String msg = "Hey, Check out this job on 24Hires! "
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


        mreporttxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(JobDetail.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.report_dialog);

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                lp.gravity = Gravity.CENTER;

                dialog.getWindow().setAttributes(lp);

                CardView mspamcardview = (CardView)  dialog.findViewById(R.id.spamcardview);
                CardView mirrelavantcardview = (CardView)  dialog.findViewById(R.id.irrelavantcardview);
                CardView minappropriatecardview = (CardView)  dialog.findViewById(R.id.inappropriatecardview);

                dialog.show();

                mspamcardview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mUserReport.child(mAuth.getCurrentUser().getUid()).child("Post").child("Spam").child(city).setValue(postkey);
                        showreportdialog();

                        dialog.dismiss();
                    }
                });

                mirrelavantcardview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mUserReport.child(mAuth.getCurrentUser().getUid()).child("Post").child("Irrelavant").child(city).setValue(postkey);
                        showreportdialog();

                        dialog.dismiss();
                    }
                });

                minappropriatecardview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mUserReport.child(mAuth.getCurrentUser().getUid()).child("Post").child("Inappropriate").child(city).setValue(postkey);
                        showreportdialog();

                        dialog.dismiss();
                    }
                });
            }
        });


        mprofilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "postuid " + jobowner_uid);
                Log.d(TAG, "shortlistedval " + shortlistedval);
                if(jobowner_uid!=null) {

                    Intent otheruserintent = new Intent(JobDetail.this, OtherUser.class);
                    otheruserintent.putExtra("user_uid",jobowner_uid);
                    startActivity(otheruserintent);

                }
            }
        });


        mchatuserCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(jobowner_uid!=null && receiver_userimage!=null){
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
                    mChatRoom.child(mAuth.getCurrentUser().getUid()).child(mAuth.getCurrentUser().getUid() + "_" + jobowner_uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                mChatRoom.child(mAuth.getCurrentUser().getUid()).child(mAuth.getCurrentUser().getUid() + "_" + jobowner_uid).child("UnreadMessagePressed").setValue("true");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    Intent chatroomintent = new Intent(JobDetail.this, ChatRoom.class);
                    chatroomintent.putExtra("post_uid",jobowner_uid);
                    chatroomintent.putExtra("owner_image",current_userimage);
                    chatroomintent.putExtra("receiver_image",receiver_userimage);
                    startActivity(chatroomintent);
                }
            }
        });

        msaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String savestatus = msavetxt.getText().toString();

                //If this job has not been saved, save it now
                if(savestatus.equals("SAVE")){
                    if(postkey!=null && post_title != null && post_desc != null && post_company!= null && city!= null && post_image!= null){
                        final DatabaseReference newSave = mUserActivities.child(mAuth.getCurrentUser().getUid()).child("Saved").child(postkey);

                        Long tsLong = System.currentTimeMillis()/1000;
                        Map<String, Object> checkoutData = new HashMap<>();
                        checkoutData.put("time", ServerValue.TIMESTAMP);
                        checkoutData.put("negatedtime", (-1*tsLong));
                        checkoutData.put("title", post_title);
                        checkoutData.put("desc", post_desc);
                        checkoutData.put("company", post_company);
                        checkoutData.put("city", city);
                        checkoutData.put("postimage", post_image);

                        newSave.setValue(checkoutData);

                        msavetxt.setText("SAVED");
                        Toast.makeText(getApplicationContext(),"You saved this job post!", Toast.LENGTH_SHORT).show();
                    }
                }

                //If this job has been saved, unsaved it now
                else if (savestatus.equals("SAVED")){

                    mUserActivities.child(mAuth.getCurrentUser().getUid()).child("Saved").child(postkey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            msavetxt.setText("SAVE");
                            Toast.makeText(getApplicationContext(),"You have un-saved this job post!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        mapplyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(postkey!=null && jobowner_uid!=null && post_title != null && post_desc != null && post_company!= null && city!= null && post_image!= null){

                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Job Applied");
                    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Job Applied Already");
                    mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                    mappliedRlay.setVisibility(VISIBLE);
                    msaveapplyLay.setVisibility(GONE);
                    mrejectedRlay.setVisibility(GONE);
                    mshortlistedRlay.setVisibility(GONE);

                    //Remove saved post
                    mUserActivities.child(mAuth.getCurrentUser().getUid()).child("Saved").child(postkey).removeValue();
                    msavetxt.setText("SAVE");

                    //Notification trigger
                    final DatabaseReference newApplyNotification = mUserApplyNotification.child("Applications").push();
                    final String applynotificationKey = newApplyNotification.getKey();
                    final Map<String, Object> notificationData = new HashMap<>();
                    notificationData.put("ownerUid", mAuth.getCurrentUser().getUid());
                    notificationData.put("receiverUid", jobowner_uid);
                    notificationData.put("ownerName", current_username);
                    newApplyNotification.setValue(notificationData);

                    //  mUserAllNotification.child(jobowner_uid).child("Applications").child(applynotificationKey).setValue(applynotificationKey);
                    mUserActivities.child(jobowner_uid).child("ApplyNotification").child(applynotificationKey).setValue(applynotificationKey);

                    //Add as applicant to owner's applicant list
                    final DatabaseReference newUser = mUserPostedPendingApplicants.child(jobowner_uid).child(postkey).child(mAuth.getCurrentUser().getUid());
                    Long tsLong = System.currentTimeMillis()/1000;
                    Map<String, Object> checkoutData = new HashMap<>();
                    checkoutData.put("time", ServerValue.TIMESTAMP);
                    checkoutData.put("negatedtime", (-1*tsLong));
                    checkoutData.put("name", current_username);
                    checkoutData.put("image", current_userimage);
                    checkoutData.put("pressed", "false");

                    newUser.setValue(checkoutData).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            //Add notification to owner
                            //  mUserPosted.child(jobowner_uid).child(postkey).child("pressed").setValue("false");
                            mUserActivities.child(jobowner_uid).child("NewMainNotification").setValue("true");
                            mUserActivities.child(jobowner_uid).child("NewPosted").setValue("true");
                            incrementapplicants();
                        }
                    });

                    //Add to own applied tab
                    final DatabaseReference newApply = mUserActivities.child(mAuth.getCurrentUser().getUid()).child("Applied").child(postkey);
                    Long tsLong1 = System.currentTimeMillis()/1000;
                    Map<String, Object> checkoutData1 = new HashMap<>();
                    checkoutData1.put("time", ServerValue.TIMESTAMP);
                    checkoutData1.put("negatedtime", (-1*tsLong1));
                    checkoutData1.put("title", post_title);
                    checkoutData1.put("uid", jobowner_uid);
                    checkoutData1.put("desc", post_desc);
                    checkoutData1.put("company", post_company);
                    checkoutData1.put("city", city);
                    checkoutData1.put("postimage", post_image);
                    checkoutData1.put("status", "applied");
                    checkoutData1.put("pressed", "true");
                    checkoutData1.put("closed", "false");

                    newApply.setValue(checkoutData1);

                    //Add to user's chat
                    final DatabaseReference OwnerChat = mChatRoom.child(current_useruid);
                    final DatabaseReference ReceiverChat = mChatRoom.child(jobowner_uid);
                    final DatabaseReference newReceiverChat = ReceiverChat.child(jobowner_uid+"_"+current_useruid).child("ChatList").push();
                    final String newChatListkey = newReceiverChat.getKey();
                    final DatabaseReference newOwnerChat = OwnerChat.child(current_useruid+"_"+jobowner_uid).child("ChatList").child(newChatListkey);

                    final Map<String, Object> actionchatData = new HashMap<>();
                    actionchatData.put("negatedtime", (-1*tsLong));
                    actionchatData.put("time", ServerValue.TIMESTAMP);
                    actionchatData.put("actiontitle", "applied");
                    actionchatData.put("ownerid", mAuth.getCurrentUser().getUid());
                    actionchatData.put("jobtitle", post_title);
                    actionchatData.put("jobdescrip", post_desc);
                    actionchatData.put("city", city);
                    actionchatData.put("postkey", postkey);

                    mUserChatList.child(mAuth.getCurrentUser().getUid()).child("UserList").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                if(dataSnapshot.child(jobowner_uid).exists()){

                                    actionchatData.put("oldtime", dataSnapshot.child(jobowner_uid).child("time").getValue());

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
                                actionchatData.put("oldtime", 0);
                                newOwnerChat.setValue(actionchatData);
                                newReceiverChat.setValue(actionchatData);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    Toast.makeText(getApplicationContext(),"You applied to this job post! Good Luck!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mrejectedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // custom dialog
                final Dialog dialog = new Dialog(JobDetail.this);
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

                hirebtn.setText("RE-APPLY");
                cancelbtn.setText("CANCEL");
                hirebtn.setTextColor(Color.parseColor("#ff669900"));
                mdialogtxt.setText("You may only re-apply once. Are you sure you want to re-apply this job?");

                dialog.show();

                hirebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if( postkey!=null && post_title != null && post_desc != null && post_company!= null && city!= null && post_image!= null && jobowner_uid!=null){

                            mappliedRlay.setVisibility(VISIBLE);
                            msaveapplyLay.setVisibility(GONE);
                            mrejectedRlay.setVisibility(GONE);
                            mshortlistedRlay.setVisibility(GONE);

                            //Remove saved post
                            mUserActivities.child(mAuth.getCurrentUser().getUid()).child("Saved").child(postkey).removeValue();
                            msavetxt.setText("SAVE");

                            //Notification trigger
                            final DatabaseReference newreApplyNotification = mUserApplyNotification.child("Applications").push();
                            final String reapplynotificationKey = newreApplyNotification.getKey();
                            final Map<String, Object> notificationData2 = new HashMap<>();
                            notificationData2.put("ownerUid", mAuth.getCurrentUser().getUid());
                            notificationData2.put("receiverUid", jobowner_uid);
                            notificationData2.put("ownerName", current_username);
                            newreApplyNotification.setValue(notificationData2);

                            //   mUserAllNotification.child(jobowner_uid).child("Applications").child(reapplynotificationKey).setValue(reapplynotificationKey);
                            mUserActivities.child(jobowner_uid).child("ApplyNotification").child(reapplynotificationKey).setValue(reapplynotificationKey);

                            //Add as applicant to owner's applicant list
                            final DatabaseReference newUser = mUserPostedPendingApplicants.child(jobowner_uid).child(postkey).child(mAuth.getCurrentUser().getUid());
                            Long tsLong = System.currentTimeMillis()/1000;
                            Map<String, Object> checkoutData = new HashMap<>();
                            checkoutData.put("time", ServerValue.TIMESTAMP);
                            checkoutData.put("negatedtime", (-1*tsLong));
                            checkoutData.put("name", current_username);
                            checkoutData.put("image", current_userimage);
                            checkoutData.put("pressed", "false");

                            newUser.setValue(checkoutData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    //Add notification to owner
                                    // mUserPosted.child(jobowner_uid).child(postkey).child("pressed").setValue("false");
                                    mUserActivities.child(jobowner_uid).child("NewMainNotification").setValue("true");
                                    mUserActivities.child(jobowner_uid).child("NewPosted").setValue("true");
                                    incrementapplicants();
                                }
                            });

                            //Add to own applied tab
                            final DatabaseReference newApply = mUserActivities.child(mAuth.getCurrentUser().getUid()).child("Applied").child(postkey);
                            Long tsLong1 = System.currentTimeMillis()/1000;
                            Map<String, Object> checkoutData1 = new HashMap<>();
                            checkoutData1.put("time", ServerValue.TIMESTAMP);
                            checkoutData1.put("negatedtime", (-1*tsLong1));
                            checkoutData1.put("title", post_title);
                            checkoutData1.put("uid", jobowner_uid);
                            checkoutData1.put("desc", post_desc);
                            checkoutData1.put("company", post_company);
                            checkoutData1.put("city", city);
                            checkoutData1.put("postimage", post_image);
                            checkoutData1.put("status", "applied");
                            checkoutData1.put("pressed", "true");
                            checkoutData1.put("closed", "false");

                            newApply.setValue(checkoutData1);

                            //Add to user's chat
                            final DatabaseReference OwnerChat = mChatRoom.child(current_useruid);
                            final DatabaseReference ReceiverChat = mChatRoom.child(jobowner_uid);
                            final DatabaseReference newReceiverChat = ReceiverChat.child(jobowner_uid+"_"+current_useruid).child("ChatList").push();
                            final String newChatListkey = newReceiverChat.getKey();
                            final DatabaseReference newOwnerChat = OwnerChat.child(current_useruid+"_"+jobowner_uid).child("ChatList").child(newChatListkey);

                            final Map<String, Object> actionchatData = new HashMap<>();
                            actionchatData.put("negatedtime", (-1*tsLong));
                            actionchatData.put("time", ServerValue.TIMESTAMP);
                            actionchatData.put("actiontitle", "applied");
                            actionchatData.put("ownerid", mAuth.getCurrentUser().getUid());
                            actionchatData.put("jobtitle", post_title);
                            actionchatData.put("jobdescrip", post_desc);
                            actionchatData.put("city", city);
                            actionchatData.put("postkey", postkey);

                            mUserChatList.child(mAuth.getCurrentUser().getUid()).child("UserList").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        if(dataSnapshot.child(jobowner_uid).exists()){

                                            actionchatData.put("oldtime", dataSnapshot.child(jobowner_uid).child("time").getValue());

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
                                        actionchatData.put("oldtime", 0);
                                        newOwnerChat.setValue(actionchatData);
                                        newReceiverChat.setValue(actionchatData);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            Toast.makeText(getApplicationContext(), "You re-applied to this job post! Good Luck!", Toast.LENGTH_SHORT).show();
                        }

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

        mpostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(post_image != null && post_image.equals("default")){

                }
                else {
                    PhotoViewAttacher pAttacher;
                    final Dialog nagDialog = new Dialog(JobDetail.this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
                    nagDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                    nagDialog.setContentView(R.layout.preview_image);
                    RelativeLayout mRlay = (RelativeLayout) nagDialog.findViewById(R.id.Rlay);
                    final TouchImageView ivPreview = (TouchImageView) nagDialog.findViewById(R.id.iv_preview_image);

                    Glide.with(getApplicationContext()).load(post_image)
                            .thumbnail(0.5f)
                            .fitCenter()
                            .error(R.drawable.loadingerror3)
                            .placeholder(R.drawable.loading_spinner)
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(ivPreview);

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

        mstreetviewimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(latitude!= null && longitude!= null) {
                    /*Uri getmap = Uri.parse("google.streetview:cbll="+latitude+","+longitude+"&cbp=0,30,0,0,-15");
                    Intent mapintent = new Intent(Intent.ACTION_VIEW, getmap);
                    mapintent.setPackage("com.google.android.apps.maps");
                    startActivity(mapintent);*/

                    final Dialog dialog = new Dialog(JobDetail.this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                    dialog.setContentView(R.layout.streetviewdialog);

                    StreetViewPanoramaView mStreetViewPanoramaView = (StreetViewPanoramaView) dialog.findViewById(R.id.steet_view_panorama);


                    mStreetViewPanoramaView.onCreate(dialog.onSaveInstanceState());
                    mStreetViewPanoramaView.onResume();

                    mStreetViewPanoramaView.getStreetViewPanoramaAsync(new OnStreetViewPanoramaReadyCallback() {
                        @Override
                        public void onStreetViewPanoramaReady(StreetViewPanorama panorama) {
                            panorama.setPosition(new LatLng(latitude, longitude));
                        }
                    });

                    dialog.show();

                }
            }
        });

        mmapimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(JobDetail.this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                dialog.setContentView(R.layout.mapviewdialog);

                MapView mMapView = (MapView) dialog.findViewById(R.id.map);

                MapsInitializer.initialize(JobDetail.this);

                mMapView.onCreate(dialog.onSaveInstanceState());
                mMapView.onResume();

                mMapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(final GoogleMap googleMap) {
                        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                        if(latitude!= null && longitude!= null){


                            googleMap.getUiSettings().setZoomControlsEnabled(true);
                            googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);

                            googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude)).title(post_location));
                            CameraPosition markposition = CameraPosition.builder().target(new LatLng(latitude,longitude)).zoom(16).bearing(0).tilt(45).build();
                            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(markposition));

                            googleMap.getUiSettings().setMapToolbarEnabled(true);

                        }

                        Log.d(TAG, "newlat " +  googleMap.getCameraPosition().target.latitude);
                        Log.d(TAG, "newlong " +  googleMap.getCameraPosition().target.longitude);

                        googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                            @Override
                            public void onCameraIdle() {
                                // Cleaning all the markers.

                                Log.d(TAG, "newlatxx " +  googleMap.getCameraPosition().target.latitude);
                                Log.d(TAG, "newlongxx " +  googleMap.getCameraPosition().target.longitude);


                            }
                        });

                    }


                });

                dialog.show();
            }
        });

        /*if(mMapView != null){
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(JobDetail.this);
        }*/
    }

    /*@Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(JobDetail.this);
        mGoogleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if(latitude!= null && longitude!= null){
            googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude)).title(post_location));
            CameraPosition markposition = CameraPosition.builder().target(new LatLng(latitude,longitude)).zoom(16).bearing(0).tilt(45).build();
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(markposition));
        }
    }*/

    private void checkrejectedJob() {
        //Check If user has been rejected twice, if yes, disable re-apply btn to allow user only re-apply once
        mUserActivities.child(mAuth.getCurrentUser().getUid()).child("RejectedApplied").child(postkey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    mhiredRlay.setVisibility(GONE);
                    mrejectedOfferRlay.setVisibility(GONE);
                    mshortlistedRlay.setVisibility(GONE);
                    mappliedRlay.setVisibility(GONE);
                    msaveapplyLay.setVisibility(GONE);
                    mrejectedRlay.setVisibility(VISIBLE);

                    Long rejectedcount = dataSnapshot.getChildrenCount();

                    Log.d(TAG, "rejected twice " + rejectedcount);

                    if (rejectedcount >= 2) {
                        mrejectedBtn.setVisibility(GONE);
                    } else if (rejectedcount < 2) {
                        mrejectedBtn.setVisibility(VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void incrementapplicants() {

        mUserPosted.child(jobowner_uid).child(postkey).child("newapplicantscount").runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData currentData) {
                if (currentData.getValue() == null) {
                    currentData.setValue(1);
                } else {
                    currentData.setValue((Long) currentData.getValue() + 1);
                }
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });

        mUserPosted.child(jobowner_uid).child(postkey).child("applicantscount").runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData currentData2) {
                Log.d(TAG, "currentData2 " +currentData2.getValue());
                if (currentData2.getValue() == null) {
                    currentData2.setValue(1);
                } else {
                    currentData2.setValue((Long) currentData2.getValue() + 1);
                }
                return Transaction.success(currentData2);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

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
        final Dialog dialog = new Dialog(JobDetail.this);
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
}

