package com.zjheng.jobseed.jobseed;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.google.android.gms.nearby.messages.internal.Update;
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
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.iid.FirebaseInstanceId;
import com.zjheng.jobseed.jobseed.ActivitiesScene.JobActivities;
import com.zjheng.jobseed.jobseed.HomeScene.DiscoverTalent.ComingSoon;
import com.zjheng.jobseed.jobseed.HomeScene.HomeFragment;
import com.zjheng.jobseed.jobseed.LoginScene.Login;
import com.zjheng.jobseed.jobseed.LoginScene.NonSwipeableViewPager;
import com.zjheng.jobseed.jobseed.MessageScene.ChatFragment;
import com.zjheng.jobseed.jobseed.NearbyJobScene.NearbyJob;
import com.zjheng.jobseed.jobseed.PostScene.Post;
import com.zjheng.jobseed.jobseed.TalentActivities.TalentActivities;
import com.zjheng.jobseed.jobseed.TalentDetails.TalentDetail;
import com.zjheng.jobseed.jobseed.UserProfileScene.UserProfileFragment2;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import im.delight.android.location.SimpleLocation;
import me.leolin.shortcutbadger.ShortcutBadger;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.zjheng.jobseed.jobseed.R.id.homeicongrey;
import static com.zjheng.jobseed.jobseed.R.id.maintxt;
import static com.zjheng.jobseed.jobseed.R.id.textView;

public class MainActivity extends AppCompatActivity {

    private View mnotifiBadge2, mnotifiBadge3, mnotifiBadge4, mnotifiBadge5;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserAccount, mUserActivities, mUserChatList,mUserLocation, mUserReview,
            mGeoFire, mUserPosted, mAppVersion,mUserPostedPendingApplicants, mUserBookingMade, mJob, mTalent;
    private GeoFire geoFire;

    private String currentuseruid, currentcity = null, cityid;
    private static final String TAG = "MainActivity";

    private NonSwipeableViewPager mViewPager;
    private TabLayout tabLayout;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private SimpleLocation location;
    private static final int MY_PERMISSION_REQUEST_LOCATION = 2;

    private SharedPreferences prefs;

    boolean doubleBackToExitPressedOnce = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
            return;
        }

        //cancel shadow below actionbar
        //getSupportActionBar().setElevation(0);

        //getSupportActionBar().hide();

        Intent intent = getIntent();

        if(intent.hasExtra("user_id")){
            currentuseruid = getIntent().getExtras().getString("user_id");
        }else{
            if (currentuseruid == null) {
                currentuseruid = mAuth.getCurrentUser().getUid();
            }
        }

        checkappsversion();

        mUserAccount =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserAccount");

        mUserActivities =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserActivities");

        mUserChatList =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserChatList");

        mUserLocation =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserLocation");

        mUserReview =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserReview");

        mUserPosted =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserPosted");

        mUserPostedPendingApplicants =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserPostedPendingApplicants");

        mUserBookingMade = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserBookingMade");

        mTalent = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("Talent");

        mJob =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("Job");

        mGeoFire = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("JobsLocation");
        geoFire = new GeoFire(mGeoFire);

        prefs = getSharedPreferences("saved", Context.MODE_PRIVATE);

        //DETECT USER CURRENT LOCATION
        if(ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)){
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String []{Manifest.permission.ACCESS_COARSE_LOCATION},MY_PERMISSION_REQUEST_LOCATION);
            }
            else{
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String []{Manifest.permission.ACCESS_COARSE_LOCATION},MY_PERMISSION_REQUEST_LOCATION);
            }
        }
        else{
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            try{
                mUserLocation.child(mAuth.getCurrentUser().getUid()).child("CurrentCity").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.exists()){
                            hereLocation();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            } catch (Exception e){
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Location Not Found", Toast.LENGTH_SHORT).show();
            }
        }

        //If user is new user, insert all tokens
        if(intent.hasExtra("newuser")){
            String newuserval = getIntent().getExtras().getString("newuser");

            if (newuserval.equals("true")) {
                addTokens();
            }
        }
        //If user is NOT new user, check the existence of tokens and insert accordingly
        else {
            checkToken();
        }

        final DatabaseReference myConnectionsRef = FirebaseDatabase.getInstance().getReferenceFromUrl ("https://jobseed-2cb76.firebaseio.com").child("UserActivities").child(mAuth.getCurrentUser().getUid()).child("Connections");
        // stores the timestamp of my last disconnect (the last time I was seen online)
        final DatabaseReference lastOnlineRef  = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserActivities").child(mAuth.getCurrentUser().getUid()).child("Lastonline");

        final DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReferenceFromUrl ("https://jobseed-2cb76.firebaseio.com/.info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    // add this device to my connections list
                    // this value could contain info about the device or a timestamp too
                    DatabaseReference con = myConnectionsRef.push();
                    con.setValue(Boolean.TRUE);

                    // when this device disconnects, remove it
                    myConnectionsRef.onDisconnect().removeValue();

                    // when I disconnect, update the last time I was seen online
                    lastOnlineRef.onDisconnect().setValue(ServerValue.TIMESTAMP);
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Check which applicant has passed 24 hours
        mUserPostedPendingApplicants.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnaphot : dataSnapshot.getChildren()) {

                    final String postkey = postSnaphot.getKey();

                    for (DataSnapshot userSnaphot : postSnaphot.getChildren()) {

                        final String otheruserid = userSnaphot.getKey();

                        Long time = (Long) userSnaphot.child("time").getValue();
                        String pressedval = (String) userSnaphot.child("pressed").getValue();

                        Date d = new Date(time);
                        Long tsLong = System.currentTimeMillis();
                        Date datenow = new Date(tsLong);

                        Calendar c = Calendar.getInstance();
                        c.setTime(d);
                        c.add(Calendar.HOUR, 24);

                        //IF user applied day has passed 1 day
                        if (c.getTime().compareTo(datenow) < 0) {

                            deleteapplicant(postkey,otheruserid,pressedval);

                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //Check which applied job has passed 24 hours
        mUserActivities.child(mAuth.getCurrentUser().getUid()).child("Applied").orderByChild("status").equalTo("applied").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnaphot : dataSnapshot.getChildren()) {
                    final String postkey = userSnaphot.getKey();

                    if(userSnaphot.hasChild("time")) {

                        Long time = (Long) userSnaphot.child("time").getValue();

                        Date d = new Date(time);
                        Long tsLong = System.currentTimeMillis();
                        Date datenow = new Date(tsLong);

                        Calendar c = Calendar.getInstance();
                        c.setTime(d);
                        c.add(Calendar.HOUR, 24);
                        if (c.getTime().compareTo(datenow) < 0) {
                            //More than 1 day, display REJECTED on the particular APPLIED JOB

                            mUserActivities.child(mAuth.getCurrentUser().getUid()).child("NewMainNotification").setValue("true");
                            mUserActivities.child(mAuth.getCurrentUser().getUid()).child("NewApplied").setValue("true");
                            mUserActivities.child(mAuth.getCurrentUser().getUid()).child("Applied").child(postkey).child("status").setValue("appliedrejected");
                            mUserActivities.child(mAuth.getCurrentUser().getUid()).child("Applied").child(postkey).child("pressed").setValue("false");

                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //Check which job has been accepted hired, and havent review yet
        mUserActivities.child(mAuth.getCurrentUser().getUid()).child("Applied").orderByChild("status").equalTo("acceptedoffer").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnaphot : dataSnapshot.getChildren()) {

                    final String postkey = userSnaphot.getKey();

                    if(!userSnaphot.hasChild("reviewed")) {

                        if(userSnaphot.hasChild("date")) {

                            String dateval = userSnaphot.child("date").getValue().toString();

                            String[] separated;
                            String lastdate;
                            Date enddate;

                            if (!dateval.contains("to")) {

                                separated = dateval.split(" / ");

                                lastdate = separated[separated.length - 1];
                            }
                            else {
                                separated = dateval.split(" to ");

                                lastdate = separated[1];
                            }

                            SimpleDateFormat dates = new SimpleDateFormat("dd MMM yy");

                            try {
                                enddate = dates.parse(lastdate);

                                final long tsLong = System.currentTimeMillis();
                                Date datenow = new Date(tsLong);

                                Calendar c = Calendar.getInstance();
                                c.setTime(enddate);

                                //If end date < time date NOW, show REVIEW
                                if (c.getTime().compareTo(datenow) < 0) {

                                    if (!userSnaphot.hasChild("reviewpressed")) {
                                        mUserActivities.child(mAuth.getCurrentUser().getUid()).child("Applied").child(postkey).child("reviewpressed").setValue("false");
                                        mUserActivities.child(mAuth.getCurrentUser().getUid()).child("NewMainNotification").setValue("true");
                                        mUserActivities.child(mAuth.getCurrentUser().getUid()).child("NewApplied").setValue("true");
                                    }

                                }

                            } catch (Exception exception) {
                                Log.e("DIDN'T WORK", "exception " + exception);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //Check which booked talent has been accpted booking, and havent review yet
        mUserBookingMade.child(mAuth.getCurrentUser().getUid()).orderByChild("status").equalTo("acceptedbooking").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnaphot : dataSnapshot.getChildren()) {

                    final String postkey = userSnaphot.getKey();

                    if(!userSnaphot.hasChild("reviewed")) {

                        if(userSnaphot.hasChild("dates")) {

                            String dateval = userSnaphot.child("dates").getValue().toString();

                            String[] separated;
                            String lastdate;
                            Date enddate;

                            if (!dateval.contains("to")) {

                                separated = dateval.split(" / ");

                                lastdate = separated[separated.length - 1];
                            }
                            else {
                                separated = dateval.split(" to ");

                                lastdate = separated[1];
                            }

                            SimpleDateFormat dates = new SimpleDateFormat("dd MMM yy");

                            try {
                                enddate = dates.parse(lastdate);

                                final long tsLong = System.currentTimeMillis();
                                Date datenow = new Date(tsLong);

                                Calendar c = Calendar.getInstance();
                                c.setTime(enddate);

                                //If end date < time date NOW, show REVIEW
                                if (c.getTime().compareTo(datenow) < 0) {

                                    if (!userSnaphot.hasChild("reviewpressed")) {
                                        mUserBookingMade.child(mAuth.getCurrentUser().getUid()).child(postkey).child("reviewpressed").setValue("false");
                                        mUserActivities.child(mAuth.getCurrentUser().getUid()).child("NewTalentMainNotification").setValue("true");
                                        mUserActivities.child(mAuth.getCurrentUser().getUid()).child("NewBookingsMade").setValue("true");
                                    }

                                }

                            } catch (Exception exception) {
                                Log.e("DIDN'T WORK", "exception " + exception);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        Uri deepLink;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                            Log.d(TAG, "deepLink" + deepLink);
                            if (deepLink != null) {
                                if (deepLink.getBooleanQueryParameter("jobpost", true)) {
                                    String postval = deepLink.getQueryParameter("jobpost");
                                    Log.d(TAG, "postval" + postval);
                                    String postvals[] = postval.split(",");
                                    Log.d(TAG, "postvals.length" + postvals.length);
                                    if (postvals.length <= 2 && postvals.length > 0) {
                                        Log.d(TAG, "postvals" + postvals);
                                        final String postid = postvals[0];
                                        cityid = postvals[1];

                                        if (cityid.contains("%20")){
                                            cityid = cityid.replace("%20", " ");
                                        }
                                        Log.d(TAG, "cityid" + cityid);
                                        mJob.child(cityid).child(postid).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if(dataSnapshot.exists()){
                                                    Intent jobdetailintent = new Intent(MainActivity.this, JobDetail.class);
                                                    jobdetailintent.putExtra("post_id", postid);
                                                    jobdetailintent.putExtra("city_id", cityid);
                                                    startActivity(jobdetailintent);
                                                }
                                                else{
                                                    Intent jobdetailintent = new Intent(MainActivity.this, RemovedJob.class);
                                                    startActivity(jobdetailintent);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }

                                    else if (postvals.length > 2) {
                                        final String postid = postvals[0];
                                        cityid = postvals[1];
                                        if (cityid.contains("%20")){
                                            cityid = cityid.replace("%20", " ");
                                        }
                                        final String maincategoryid = postvals[2];
                                        final String subcategoryid = postvals[3];

                                        mTalent.child(cityid).child(maincategoryid).child(subcategoryid).child(postid).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if(dataSnapshot.exists()){
                                                    Intent detailintent = new Intent(MainActivity.this, TalentDetail.class);
                                                    detailintent.putExtra("post_id", postid);
                                                    detailintent.putExtra("city_id", cityid);
                                                    detailintent.putExtra("maincategory", maincategoryid);
                                                    detailintent.putExtra("subcategory", subcategoryid);
                                                    startActivity(detailintent);
                                                }
                                                else{
                                                    Intent jobdetailintent = new Intent(MainActivity.this, RemovedTalent.class);
                                                    startActivity(jobdetailintent);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }

                                }
                            }
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //...
                    }
                });

        /*Remove push notifications
        mUserAllNotification.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("Applications")){
                    mUserAllNotification.child(mAuth.getCurrentUser().getUid()).child("Applications").removeValue();
                    ShortcutBadger.removeCount(MainActivity.this);
                }
                if(dataSnapshot.hasChild("ShortListedNotification")){
                    mUserAllNotification.child(mAuth.getCurrentUser().getUid()).child("ShortListedNotification").removeValue();
                    ShortcutBadger.removeCount(MainActivity.this);
                }
                if(dataSnapshot.hasChild("Chat")){
                    mUserAllNotification.child(mAuth.getCurrentUser().getUid()).child("Chat").removeValue();
                    ShortcutBadger.removeCount(MainActivity.this);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });*/


        /* Fixing Later Map loading Delay
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MapView mv = new MapView(getApplicationContext());
                    mv.onCreate(null);
                    mv.onPause();
                    mv.onDestroy();
                }catch (Exception ignored){
                }
            }
        }).start();*/

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager = (NonSwipeableViewPager) findViewById(R.id.container);
        mViewPager.setOffscreenPageLimit(4);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager.setPageTransformer(false, new FadePageTransformer());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);

        final View tabOne = (View) LayoutInflater.from(MainActivity.this).inflate(R.layout.homefragicon, null);
        final ImageView tabicon1 = (ImageView) tabOne.findViewById(homeicongrey);
        final TextView mmaintxt1 = (TextView) tabOne.findViewById(maintxt);

        final View tabTwo = (View) LayoutInflater.from(MainActivity.this).inflate(R.layout.homefragicon, null);
        final ImageView tabicon2 = (ImageView) tabTwo.findViewById(homeicongrey);
        final TextView mmaintxt2 = (TextView) tabTwo.findViewById(maintxt);
        mnotifiBadge2 = (View) tabTwo.findViewById(R.id.notifiBadge);

        final View tabThree = (View) LayoutInflater.from(MainActivity.this).inflate(R.layout.homefragicon, null);
        final ImageView tabicon3 = (ImageView) tabThree.findViewById(homeicongrey);
        final TextView mmaintxt3 = (TextView) tabThree.findViewById(maintxt);
        mnotifiBadge3 = (View) tabThree.findViewById(R.id.notifiBadge);

        final View tabFour = (View) LayoutInflater.from(MainActivity.this).inflate(R.layout.homefragicon, null);
        final ImageView tabicon4 = (ImageView) tabFour.findViewById(homeicongrey);
        final TextView mmaintxt4 = (TextView) tabFour.findViewById(maintxt);
        mnotifiBadge4 = (View) tabFour.findViewById(R.id.notifiBadge);

        final View tabFive = (View) LayoutInflater.from(MainActivity.this).inflate(R.layout.homefragicon, null);
        final ImageView tabicon5 = (ImageView) tabFive.findViewById(homeicongrey);
        final TextView mmaintxt5 = (TextView) tabFive.findViewById(maintxt);
        mnotifiBadge5 = (View) tabFive.findViewById(R.id.notifiBadge);

        //Display notification badge
        mUserActivities.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.child("NewMainNotification").exists()){
                    String NewMainNotificationval = dataSnapshot.child("NewMainNotification").getValue().toString();

                    if(NewMainNotificationval.equals("true")){
                        mnotifiBadge2.setVisibility(View.VISIBLE);
                    }
                    else{
                        mnotifiBadge2.setVisibility(GONE);
                    }
                }
                else{
                    mnotifiBadge2.setVisibility(GONE);
                }


                if(dataSnapshot.child("NewTalentMainNotification").exists()){
                    String NewMainNotificationval = dataSnapshot.child("NewTalentMainNotification").getValue().toString();

                    if(NewMainNotificationval.equals("true")){
                        mnotifiBadge3.setVisibility(View.VISIBLE);
                    }
                    else{
                        mnotifiBadge3.setVisibility(GONE);
                    }
                }
                else{
                    mnotifiBadge3.setVisibility(GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        mUserChatList.child(mAuth.getCurrentUser().getUid()).child("Pressed").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    String Pressed = dataSnapshot.getValue().toString();

                    if(Pressed.equals("true")){
                        mnotifiBadge4.setVisibility(GONE);
                    }
                    else{
                        mnotifiBadge4.setVisibility(View.VISIBLE);
                    }
                }
                else{
                    mnotifiBadge4.setVisibility(GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mUserReview.child(mAuth.getCurrentUser().getUid()).child("Notification").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String Notification = dataSnapshot.getValue().toString();

                    if(Notification.equals("true")){
                        mnotifiBadge5.setVisibility(VISIBLE);
                    }
                    else{
                        mnotifiBadge5.setVisibility(GONE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        tabicon1.setImageResource(R.drawable.homebtn_blue);
        tabicon1.setAlpha(1.0f);
        tabOne.setSelected(true);
        mmaintxt1.setText("Home");
        mmaintxt1.setTextColor(Color.parseColor("#67b8ed"));
        tabLayout.getTabAt(0).setCustomView(tabOne);
        // getSupportActionBar().setTitle("Home");

        tabicon2.setImageResource(R.drawable.jobbtn3);
        tabicon2.setAlpha(0.8f);
        mmaintxt2.setText("Job");
        final ColorStateList oldColors =  mmaintxt2.getTextColors();
        tabLayout.getTabAt(1).setCustomView(tabTwo);

        tabicon3.setImageResource(R.drawable.talentbtn1);
        tabicon3.setAlpha(0.8f);
        mmaintxt3.setText("Talent");
        tabLayout.getTabAt(2).setCustomView(tabThree);

        tabicon4.setImageResource(R.drawable.chatbtn2);
        tabicon4.setAlpha(0.8f);
        mmaintxt4.setText("Message");
        tabLayout.getTabAt(3).setCustomView(tabFour);

        tabicon5.setImageResource(R.drawable.profilebtn2);
        tabicon5.setAlpha(0.8f);
        mmaintxt5.setText("Profile");
        tabLayout.getTabAt(4).setCustomView(tabFive);

        //get tab view
        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        //get number of tab
        int tabsCount = vg.getChildCount();
        //loop the tab
        for (int j = 0; j < tabsCount; j++) {
            //get view of selected tab
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);

            if(j==2){
                //disable the selected tab
                //vgTab.setEnabled(false);
            }
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if(position == 0){

                    // getSupportActionBar().hide();

                    tabicon1.setImageResource(R.drawable.homebtn_blue);
                    tabicon1.setAlpha(1.0f);
                    tabLayout.getTabAt(0).setCustomView(tabOne);
                    mmaintxt1.setTextColor(Color.parseColor("#67b8ed"));
                    //   getSupportActionBar().setTitle("Home");

                    tabicon2.setImageResource(R.drawable.jobbtn3);
                    tabicon2.setAlpha(0.8f);
                    tabLayout.getTabAt(1).setCustomView(tabTwo);
                    mmaintxt2.setTextColor(oldColors);

                    tabicon3.setImageResource(R.drawable.talentbtn1);
                    tabicon3.setAlpha(0.8f);
                    tabLayout.getTabAt(2).setCustomView(tabThree);
                    mmaintxt3.setTextColor(oldColors);

                    tabicon4.setImageResource(R.drawable.chatbtn2);
                    tabicon4.setAlpha(0.8f);
                    tabLayout.getTabAt(3).setCustomView(tabFour);
                    mmaintxt4.setTextColor(oldColors);

                    tabicon5.setImageResource(R.drawable.profilebtn2);
                    tabicon5.setAlpha(0.8f);
                    tabLayout.getTabAt(4).setCustomView(tabFive);
                    mmaintxt5.setTextColor(oldColors);

                }
                else if(position == 1){

                    // getSupportActionBar().show();

                    mUserActivities.child(mAuth.getCurrentUser().getUid()).child("NewMainNotification").setValue("false");
                    mnotifiBadge2.setVisibility(GONE);

                    tabicon2.setImageResource(R.drawable.jobbtn_blue);
                    tabicon2.setAlpha(1.0f);
                    tabLayout.getTabAt(1).setCustomView(tabTwo);
                    mmaintxt2.setTextColor(Color.parseColor("#67b8ed"));
                    //   getSupportActionBar().setTitle("Activities");

                    tabicon1.setImageResource(R.drawable.homebtn2);
                    tabicon1.setAlpha(0.8f);
                    tabLayout.getTabAt(0).setCustomView(tabOne);
                    mmaintxt1.setTextColor(oldColors);

                    tabicon3.setImageResource(R.drawable.talentbtn1);
                    tabicon3.setAlpha(0.8f);
                    tabLayout.getTabAt(2).setCustomView(tabThree);
                    mmaintxt3.setTextColor(oldColors);

                    tabicon4.setImageResource(R.drawable.chatbtn2);
                    tabicon4.setAlpha(0.8f);
                    tabLayout.getTabAt(3).setCustomView(tabFour);
                    mmaintxt4.setTextColor(oldColors);

                    tabicon5.setImageResource(R.drawable.profilebtn2);
                    tabicon5.setAlpha(0.8f);
                    tabLayout.getTabAt(4).setCustomView(tabFive);
                    mmaintxt5.setTextColor(oldColors);

                }
                else if(position == 2){

                    mUserActivities.child(mAuth.getCurrentUser().getUid()).child("NewTalentMainNotification").setValue("false");
                    mnotifiBadge3.setVisibility(GONE);

                    //  getSupportActionBar().show();
                    tabicon3.setImageResource(R.drawable.talent_blue);
                    tabicon3.setAlpha(1.0f);
                    tabLayout.getTabAt(2).setCustomView(tabThree);
                    mmaintxt3.setTextColor(Color.parseColor("#67b8ed"));
                    //getSupportActionBar().setTitle("Nearby Jobs");

                    tabicon1.setImageResource(R.drawable.homebtn2);
                    tabicon1.setAlpha(0.8f);
                    tabLayout.getTabAt(0).setCustomView(tabOne);
                    mmaintxt1.setTextColor(oldColors);

                    tabicon2.setImageResource(R.drawable.jobbtn3);
                    tabicon2.setAlpha(0.8f);
                    tabLayout.getTabAt(1).setCustomView(tabTwo);
                    mmaintxt2.setTextColor(oldColors);

                    tabicon4.setImageResource(R.drawable.chatbtn2);
                    tabicon4.setAlpha(0.8f);
                    tabLayout.getTabAt(3).setCustomView(tabFour);
                    mmaintxt4.setTextColor(oldColors);

                    tabicon5.setImageResource(R.drawable.profilebtn2);
                    tabicon5.setAlpha(0.8f);
                    tabLayout.getTabAt(4).setCustomView(tabFive);
                    mmaintxt5.setTextColor(oldColors);

                }
                else if(position == 3){

                    //   getSupportActionBar().show();

                    mUserChatList.child(mAuth.getCurrentUser().getUid()).child("Pressed").setValue("true");
                    mnotifiBadge4.setVisibility(GONE);

                    tabicon4.setImageResource(R.drawable.chatbtn_blue);
                    tabicon4.setAlpha(1.0f);
                    tabLayout.getTabAt(3).setCustomView(tabFour);
                    mmaintxt4.setTextColor(Color.parseColor("#67b8ed"));
                    //   getSupportActionBar().setTitle("Messages");

                    tabicon1.setImageResource(R.drawable.homebtn2);
                    tabicon1.setAlpha(0.8f);
                    tabLayout.getTabAt(0).setCustomView(tabOne);
                    mmaintxt1.setTextColor(oldColors);

                    tabicon2.setImageResource(R.drawable.jobbtn3);
                    tabicon2.setAlpha(0.8f);
                    tabLayout.getTabAt(1).setCustomView(tabTwo);
                    mmaintxt2.setTextColor(oldColors);

                    tabicon3.setImageResource(R.drawable.talentbtn1);
                    tabicon3.setAlpha(0.8f);
                    tabLayout.getTabAt(2).setCustomView(tabThree);
                    mmaintxt3.setTextColor(oldColors);

                    tabicon5.setImageResource(R.drawable.profilebtn2);
                    tabicon5.setAlpha(0.8f);
                    tabLayout.getTabAt(4).setCustomView(tabFive);
                    mmaintxt5.setTextColor(oldColors);

                }
                else if(position == 4){

                    //  getSupportActionBar().show();

                    mnotifiBadge5.setVisibility(GONE);

                    tabicon5.setImageResource(R.drawable.profilebtn_blue);
                    tabicon5.setAlpha(1.0f);
                    tabLayout.getTabAt(4).setCustomView(tabFive);
                    mmaintxt5.setTextColor(Color.parseColor("#67b8ed"));
                    //  getSupportActionBar().setTitle("Profile");

                    tabicon1.setImageResource(R.drawable.homebtn2);
                    tabicon1.setAlpha(0.8f);
                    tabLayout.getTabAt(0).setCustomView(tabOne);
                    mmaintxt1.setTextColor(oldColors);

                    tabicon2.setImageResource(R.drawable.jobbtn3);
                    tabicon2.setAlpha(0.8f);
                    tabLayout.getTabAt(1).setCustomView(tabTwo);
                    mmaintxt2.setTextColor(oldColors);

                    tabicon3.setImageResource(R.drawable.talentbtn1);
                    tabicon3.setAlpha(0.8f);
                    tabLayout.getTabAt(2).setCustomView(tabThree);
                    mmaintxt3.setTextColor(oldColors);

                    tabicon4.setImageResource(R.drawable.chatbtn2);
                    tabicon4.setAlpha(0.8f);
                    tabLayout.getTabAt(3).setCustomView(tabFour);
                    mmaintxt4.setTextColor(oldColors);

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void deleteapplicant(final String postkey,final String otheruserid,final String pressedval) {

        //More than 1 day, remove user
        mUserPostedPendingApplicants.child(mAuth.getCurrentUser().getUid()).child(postkey).child(otheruserid).removeValue();
        //decrement applicant count
        decrementapplicantscount(mAuth.getCurrentUser().getUid(), postkey);
        //decrement new applicant count if applicant is unpressed yet
        if (pressedval.equals("false")) {
            decrementneweapplicantscount(mAuth.getCurrentUser().getUid(), postkey);
        }

        mUserActivities.child(otheruserid).child("Applied").child(postkey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Check if user applied tab still has the job or not
                if(dataSnapshot.exists()){
                    //If user still has the job, check if the job has been rejected or not
                    if(dataSnapshot.hasChild("status")){
                        String statusval = dataSnapshot.child("status").getValue().toString();
                        if(statusval.equals("applied")){

                            //If the job is still applying, and passed 24 hours, notify user job rejected, and delete user from owner's applicant list
                            mUserActivities.child(otheruserid).child("NewMainNotification").setValue("true");
                            mUserActivities.child(otheruserid).child("NewApplied").setValue("true");
                            mUserActivities.child(otheruserid).child("Applied").child(postkey).child("status").setValue("appliedrejected");
                            mUserActivities.child(otheruserid).child("Applied").child(postkey).child("pressed").setValue("false");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //This is set to remember user has been rejected once, so user can only re-apply ONE MORE time
        DatabaseReference newrejected = mUserActivities.child(otheruserid).child("RejectedApplied").child(postkey).push();
        newrejected.setValue("true");
    }

    public void decrementapplicantscount(String ownuserid, String postkey) {

        mUserPosted.child(ownuserid).child(postkey).child("applicantscount").runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData currentData) {
                if (currentData.getValue() != null) {
                    if((Long)currentData.getValue() == 0){
                        currentData.setValue(0);
                    }
                    else {
                        currentData.setValue((Long) currentData.getValue() - 1);
                    }
                }
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });
    }

    public void decrementneweapplicantscount(String ownuserid, String postkey) {

        mUserPosted.child(ownuserid).child(postkey).child("newapplicantscount").runTransaction(new Transaction.Handler() {
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

    private void checkappsversion(){

        mAppVersion =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("AppVersion");

        mAppVersion.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild("AndroidVersion") && dataSnapshot.hasChild("AndroidUpdateType")){

                    String updatetypeval = dataSnapshot.child("AndroidUpdateType").getValue().toString();
                    String realversion = dataSnapshot.child("AndroidVersion").getValue().toString();
                    String userversion = BuildConfig.VERSION_NAME;

                    if(!realversion.equals(userversion)){

                        if (updatetypeval.equals("3")) {
                            Intent intent = new Intent(MainActivity.this, MustUpdateView.class);
                            startActivity(intent);
                        }
                    }
                }
                else {
                    //OLD method of updating
                    if(dataSnapshot.hasChild("MustVersion")){
                        String realversion = dataSnapshot.child("MustVersion").getValue().toString();
                        String userversion = BuildConfig.VERSION_NAME;
                        if(!realversion.equals(userversion)){
                            Intent intent = new Intent(MainActivity.this, MustUpdateView.class);
                            startActivity(intent);
                        }

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public class FadePageTransformer implements ViewPager.PageTransformer {
        public void transformPage(View view, float position) {
            if(position <= -1.0F || position >= 1.0F) {
                view.setTranslationX(view.getWidth() * position);
                view.setAlpha(0.0F);
            } else if( position == 0.0F ) {
                view.setTranslationX(view.getWidth() * position);
                view.setAlpha(1.0F);
            } else {
                // position is between -1.0F & 0.0F OR 0.0F & 1.0F
                view.setTranslationX(view.getWidth() * -position);
                view.setAlpha(1.0F - Math.abs(position));
            }
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch(position){
                case 0:
                    HomeFragment tab0 = new HomeFragment();
                    return tab0;
                case 1:
                    JobActivities tab1 = new JobActivities();
                    return tab1;
                case 2:
                    TalentActivities tab2 = new TalentActivities();
                    return tab2;
                case 3:
                    ChatFragment tab3 = new ChatFragment();
                    return tab3;
                case 4:
                    UserProfileFragment2 tab4 = new UserProfileFragment2();
                    return tab4;
                default:
                    return null;
            }


        }

        @Override
        public int getItemPosition(Object object) {
            // POSITION_NONE makes it possible to reload the PagerAdapter
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            // Show 5 total pages.
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "";
                case 1:
                    return "";
                case 2:
                    return "";
                case 3:
                    return "";
                case 4:
                    return "";
            }
            return null;
        }
    }

    public void hereLocation() {
        // construct a new instance of SimpleLocation
        location = new SimpleLocation(this);
        // if we can't access the location yet
        if (!location.hasLocationEnabled()) {
            // ask the user to enable location access
            SimpleLocation.openSettings(this);
        }

        final double latitude = location.getLatitude();
        final double longitude = location.getLongitude();

        Log.d(TAG, "latitude: " + latitude);
        Log.d(TAG, "longitude: " + longitude);

        geoaddress(latitude, longitude);

    }

    public void geoaddress(final double latitude, final double longitude ) {

        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
        List<Address> addressList;
        try {
            addressList = geocoder.getFromLocation(latitude, longitude, 1);
            if (addressList.size() > 0) {

                String address = addressList.get(0).getAddressLine(0);
                String city = addressList.get(0).getLocality();
                String state = addressList.get(0).getAdminArea();
                String country = addressList.get(0).getCountryName();
                String fulladdress = address+city+state+country;

                if(state!=null){
                    currentcity = state;
                }
                else{
                    currentcity = addressList.get(0).getLocality();
                }

                if(fulladdress.contains("Pulau Pinang") || fulladdress.contains("Penang")) {currentcity = "Penang";}
                else if (fulladdress.contains("Kuala Lumpur")) {currentcity = "Kuala Lumpur";}
                else if (fulladdress.contains("Labuan")) {currentcity = "Labuan";}
                else if (fulladdress.contains("Putrajaya")) {currentcity = "Putrajaya";}
                else if (fulladdress.contains("Johor")) {currentcity = "Johor";}
                else if (fulladdress.contains("Kedah")) {currentcity = "Kedah";}
                else if (fulladdress.contains("Kelantan")) {currentcity = "Kelantan";}
                else if (fulladdress.contains("Melaka")|| fulladdress.contains("Melacca")) {currentcity = "Melacca";}
                else if (fulladdress.contains("Negeri Sembilan")|| fulladdress.contains("Seremban")) {currentcity = "Negeri Sembilan";}
                //
                else if (fulladdress.contains("Pahang")) {currentcity = "Pahang";}
                else if (fulladdress.contains("Perak")|| fulladdress.contains("Ipoh")) {currentcity = "Perak";}
                else if (fulladdress.contains("Perlis")) {currentcity = "Perlis";}
                else if (fulladdress.contains("Sabah")) {currentcity = "Sabah";}
                else if (fulladdress.contains("Sarawak")) {currentcity = "Sarawak";}
                else if (fulladdress.contains("Selangor")|| fulladdress.contains("Shah Alam")|| fulladdress.contains("Klang")) {currentcity = "Selangor";}
                else if (fulladdress.contains("Terengganu")) {currentcity = "Terengganu";}

                Log.d(TAG, "maincurrentcity: " + currentcity);

                mUserLocation.child(mAuth.getCurrentUser().getUid()).child("CurrentCity").setValue(currentcity);

            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {
        switch (requestCode){
            case MY_PERMISSION_REQUEST_LOCATION:{
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){

                        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        try{
                            mUserLocation.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChild("CurrentCity")){
                                    }
                                    else{
                                        hereLocation();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        } catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Location Not Found", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(MainActivity.this, "No Permission Granted", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            finish();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000); //time seconds
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*if ((requestCode == 10001) && (resultCode == Activity.RESULT_OK)) {
            // recreate your fragment here
            mViewPager.getAdapter().notifyDataSetChanged();
        }*/
    }

    private void checkToken() {

        final String token = FirebaseInstanceId.getInstance().getToken();

        mUserActivities.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.child("ChatTokens").exists()){
                    mUserActivities.child(mAuth.getCurrentUser().getUid()).child("ChatTokens").child(token).setValue(true);
                }
                if(dataSnapshot.child("ApplyTokens").exists()){
                    mUserActivities.child(mAuth.getCurrentUser().getUid()).child("ApplyTokens").child(token).setValue(true);
                }
                if(dataSnapshot.child("ShortlistTokens").exists()){
                    mUserActivities.child(mAuth.getCurrentUser().getUid()).child("ShortlistTokens").child(token).setValue(true);
                }
                if(dataSnapshot.child("HireTokens").exists()){
                    mUserActivities.child(mAuth.getCurrentUser().getUid()).child("HireTokens").child(token).setValue(true);
                }
                if(dataSnapshot.child("BookingTokens").exists()){
                    mUserActivities.child(mAuth.getCurrentUser().getUid()).child("BookingTokens").child(token).setValue(true);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void addTokens() {

        String token = FirebaseInstanceId.getInstance().getToken();
        if (token != null) {

            DatabaseReference current_user_db = mUserActivities.child(mAuth.getCurrentUser().getUid());
            current_user_db.child("ChatTokens").child(token).setValue(true);
            current_user_db.child("ApplyTokens").child(token).setValue(true);
            current_user_db.child("ShortlistTokens").child(token).setValue(true);
            current_user_db.child("BookingTokens").child(token).setValue(true);
            current_user_db.child("HireTokens").child(token).setValue(true);

        }
    }


    @Override
    protected void onResume() {
        super.onResume();

       /* mUserLocation.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("CurrentCity")){}
                else{
                    // make the device update its location
                    location.beginUpdates();
                    final double latitude = location.getLatitude();
                    final double longitude = location.getLongitude();
                    geoaddress(latitude, longitude);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });*/
    }

    /*@Override
    protected void onPause() {
        mUserLocation.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("CurrentCity")){}
                else{
                    // stop location updates (saves battery)
                    location.endUpdates();
                    final double latitude = location.getLatitude();
                    final double longitude = location.getLongitude();
                    geoaddress(latitude, longitude);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        super.onPause();
    }*/
}

