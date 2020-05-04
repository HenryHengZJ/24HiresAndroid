package com.zjheng.jobseed.jobseed.UserProfileScene;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
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
import com.michael.easydialog.EasyDialog;
import com.zjheng.jobseed.jobseed.MainActivity;
import com.zjheng.jobseed.jobseed.PointsRewards;
import com.zjheng.jobseed.jobseed.PointsandRewards;
import com.zjheng.jobseed.jobseed.PostScene.Post;
import com.zjheng.jobseed.jobseed.R;
import com.zjheng.jobseed.jobseed.Settings;
import com.zjheng.jobseed.jobseed.ShowcaseTalentScene.ShowcaseTalent;
import com.zjheng.jobseed.jobseed.TalentDetails.TalentDetail;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class UserProfileFragment2 extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference mUserAccount, mUserInfo;
    private CircleImageView mprofilepic;
    private NestedScrollView mcategorynestedscroll;

    private CardView mretryBtn;
    private RelativeLayout mnoInternetLay;

    private TextView muserNametxt, muserprofilecomplete;

    private static final String TAG = "UserProfile";

    private String user_uid;

    private int scoremarks = 0;

    private ProgressBar muserprogressbar;

    private CardView mprofileCardView, msettingsCardView, mJobCardView, mfaqCardView, mtalentCardView, mpointsCardView;

    Activity context;
    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.activity_profile_fragment, container, false);

        context=getActivity();

        mcategorynestedscroll = (NestedScrollView) rootView.findViewById(R.id.categorynestedscroll);
        mnoInternetLay = (RelativeLayout) rootView.findViewById(R.id.noInternetLay);
        mretryBtn = rootView.findViewById(R.id.retryBtn);

        Log.d(TAG, "UserProfileFragment2");

        setHasOptionsMenu(true);

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                Log.d(TAG, "connected to wifi");
                // connected to wifi
                mnoInternetLay.setVisibility(GONE);
                mcategorynestedscroll.setVisibility(VISIBLE);

            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                Log.d(TAG, "connected to data");
                // connected to the mobile provider's data plan
                mnoInternetLay.setVisibility(GONE);
                mcategorynestedscroll.setVisibility(VISIBLE);
            }
        } else {
            Log.d(TAG, "not connected");
            // not connected to the internet
            mnoInternetLay.setVisibility(VISIBLE);
            mcategorynestedscroll.setVisibility(GONE);

            mretryBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                    if (activeNetwork != null) { // connected to the internet
                        if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                            Log.d(TAG, "connected to wifi");
                            // connected to wifi
                            mnoInternetLay.setVisibility(GONE);
                            mcategorynestedscroll.setVisibility(VISIBLE);

                        } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                            Log.d(TAG, "connected to data");
                            // connected to the mobile provider's data plan
                            mnoInternetLay.setVisibility(GONE);
                            mcategorynestedscroll.setVisibility(VISIBLE);
                        }
                    } else {
                        Log.d(TAG, "not connected");
                        // not connected to the internet
                        mnoInternetLay.setVisibility(VISIBLE);
                        mcategorynestedscroll.setVisibility(GONE);
                    }
                }
            });
        }

        loadData();

        return rootView;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        MenuItem itemSettings = menu.findItem(R.id.menuSettings);
        itemSettings.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                Intent settingintent = new Intent(context, Settings.class);
                startActivity(settingintent);

                return false;
            }
        });

        MenuItem itemSearch = menu.findItem(R.id.menuSearch2);
        itemSearch.setVisible(false);

        MenuItem item = menu.findItem(R.id.menuSearch);
        item.setVisible(false);

        MenuItem itemPublish = menu.findItem(R.id.menuPublish);
        itemPublish.setVisible(false);

        MenuItem itemSave = menu.findItem(R.id.menuSave);
        itemSave.setVisible(false);
    }


    private void loadData() {

        mAuth = FirebaseAuth.getInstance();

        user_uid = mAuth.getCurrentUser().getUid();

        mUserAccount =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserAccount");

        mUserInfo =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserInfo");

        muserNametxt = (TextView) rootView.findViewById(R.id.userNametxt);
        muserprofilecomplete = (TextView) rootView.findViewById(R.id.userprofilecomplete);
        mprofilepic = (CircleImageView) rootView.findViewById(R.id.profilepic);

        mprofileCardView = (CardView)rootView.findViewById(R.id.profileCardView);
        msettingsCardView = (CardView)rootView.findViewById(R.id.settingsCardView);
        mJobCardView = (CardView)rootView.findViewById(R.id.JobCardView);
        mfaqCardView = (CardView)rootView.findViewById(R.id.faqCardView);
        mtalentCardView = (CardView)rootView.findViewById(R.id.talentCardView);
        mpointsCardView = (CardView)rootView.findViewById(R.id.pointsCardView);

        muserprogressbar = rootView.findViewById(R.id.userprogressbar);

        mpointsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        mpointsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent settingintent = new Intent(context, PointsRewards.class);
                startActivity(settingintent);
            }
        });

        msettingsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent settingintent = new Intent(context, Settings.class);
                startActivity(settingintent);
            }
        });

        mtalentCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent settingintent = new Intent(context, ShowcaseTalent.class);
                startActivity(settingintent);
                context.overridePendingTransition(R.anim.pullup,R.anim.nochange);
            }
        });

        mJobCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Post.class);
                startActivityForResult(intent, 10001);
                context.overridePendingTransition(R.anim.pullup,R.anim.nochange);
            }
        });

        mfaqCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, HowItWorks.class);
                startActivity(intent);
            }
        });


        mprofileCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent settingintent = new Intent(context, OwnUserProfile.class);
                startActivity(settingintent);
            }
        });

        mUserInfo.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild("Name")) {

                    String Name = dataSnapshot.child("Name").getValue().toString();
                    muserNametxt.setText(Name);

                } else {
                    mUserAccount.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot.hasChild("name")) {
                                String name = dataSnapshot.child("name").getValue().toString();
                                muserNametxt.setText(name);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

                if (dataSnapshot.hasChild("WorkExp1")) {
                    scoremarks += 10;
                }
                if (dataSnapshot.hasChild("Gender")) {
                    scoremarks += 10;
                }
                if (dataSnapshot.hasChild("Age")) {
                    scoremarks += 10;
                }
                if (dataSnapshot.hasChild("Email")) {
                    scoremarks += 10;
                }
                if (dataSnapshot.hasChild("Education")) {
                    scoremarks += 10;
                }
                if (dataSnapshot.hasChild("Language")) {
                    scoremarks += 10;
                }
                if (dataSnapshot.hasChild("About")) {
                    scoremarks += 10;
                }

                computescore(scoremarks);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mUserInfo.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("UserImage")) {

                    Log.d(TAG, "got UserImage");

                    String post_userimage = dataSnapshot.child("UserImage").getValue().toString();
                    if (post_userimage != null) {
                        if (post_userimage.equals("default")) {
                            mprofilepic.setImageResource(R.drawable.defaultprofile_pic);
                        } else {

                            Log.d(TAG, "load UserImage");

                            Glide.with(getActivity()).load(post_userimage)
                                    .thumbnail(0.5f)
                                    .centerCrop()
                                    .error(R.drawable.defaultprofile_pic)
                                    .placeholder(R.drawable.defaultprofile_pic)
                                    .dontAnimate()
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(mprofilepic);
                        }
                    }
                }

                else{

                    mUserAccount.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot1) {

                            if(dataSnapshot1.hasChild("image")){
                                Log.d(TAG, "gt post_userimage");

                                String post_userimage = dataSnapshot1.child("image").getValue().toString();
                                if (post_userimage != null) {
                                    if (post_userimage.equals("default")) {
                                        mprofilepic.setImageResource(R.drawable.defaultprofile_pic);
                                    } else {
                                        Glide.with(getActivity()).load(post_userimage)
                                                .thumbnail(0.5f)
                                                .centerCrop()
                                                .error(R.drawable.defaultprofile_pic)
                                                .placeholder(R.drawable.defaultprofile_pic)
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

    private void computescore(int scoremarks){

        double totalscore1 = (double)scoremarks / (double)70;
        double totalscore2 = (totalscore1 * 100);
        int finalscore = (int) totalscore2;
        muserprofilecomplete.setText(String.valueOf(finalscore) + "%");
        muserprogressbar.setProgress(finalscore);

        if (finalscore <25) {

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                Drawable progressDrawable = muserprogressbar.getProgressDrawable().mutate();
                progressDrawable.setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
                muserprogressbar.setProgressDrawable(progressDrawable);
                muserprofilecomplete.setTextColor(Color.parseColor("#FFF23215"));

            }else {
                muserprogressbar.setProgressTintList(ColorStateList.valueOf(Color.RED));
                muserprofilecomplete.setTextColor(Color.parseColor("#FFF23215"));
            }
        }
        else if (finalscore >25 && finalscore <75) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                Drawable progressDrawable = muserprogressbar.getProgressDrawable().mutate();
                progressDrawable.setColorFilter(Color.parseColor("#f7b51a"), android.graphics.PorterDuff.Mode.SRC_IN);
                muserprogressbar.setProgressDrawable(progressDrawable);
                muserprofilecomplete.setTextColor(Color.parseColor("#f7b51a"));

            }else {
                muserprogressbar.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#f7b51a")));
                muserprofilecomplete.setTextColor(Color.parseColor("#f7b51a"));
            }
        }
        else if (finalscore >75) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                Drawable progressDrawable = muserprogressbar.getProgressDrawable().mutate();
                progressDrawable.setColorFilter(Color.parseColor("#6dda61"), android.graphics.PorterDuff.Mode.SRC_IN);
                muserprogressbar.setProgressDrawable(progressDrawable);
                muserprofilecomplete.setTextColor(Color.parseColor("#109502"));

            }else {
                muserprogressbar.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#6dda61")));
                muserprofilecomplete.setTextColor(Color.parseColor("#109502"));
            }
        }
    }

}
