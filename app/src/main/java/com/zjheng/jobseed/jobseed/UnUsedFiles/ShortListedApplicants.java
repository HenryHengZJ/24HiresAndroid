package com.zjheng.jobseed.jobseed.UnUsedFiles;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zjheng.jobseed.jobseed.CustomObjectClass.ApplicantsInfo;
import com.zjheng.jobseed.jobseed.R;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.zjheng.jobseed.jobseed.R.id.cardview;
import static com.zjheng.jobseed.jobseed.R.id.postLocation;
import static com.zjheng.jobseed.jobseed.R.id.postTime;
import static com.zjheng.jobseed.jobseed.R.id.workcompany1;
import static com.zjheng.jobseed.jobseed.R.id.workcompany2;

public class ShortListedApplicants extends AppCompatActivity {

    private RecyclerView mApplicantList;
    private LinearLayoutManager mLayoutManager;
    private RelativeLayout mstartapplyLay;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserActivities , mJob, mUserChatList, mChatRoom, mApplyNotification;

    private static final String TAG = "Applicants";

    private String userid, post_key, city, post_title, post_desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applicants_list);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        userid = mAuth.getCurrentUser().getUid();

        post_key = getIntent().getStringExtra("post_id");
        city = getIntent().getStringExtra("city");
        post_title = getIntent().getStringExtra("post_title");
        post_desc = getIntent().getStringExtra("post_desc");

        mUserActivities =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserActivities");

        mUserChatList =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserChatList");

        mChatRoom =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("ChatRoom");

        mJob =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("Job");

        mApplyNotification =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("ApplyNotification");

        mUserActivities.child(mAuth.getCurrentUser().getUid()).child("ApplyNotification").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnaphot : dataSnapshot.getChildren()) {
                    final String notificationkey = userSnaphot.getKey();
                    if(notificationkey!=null) {
                        mApplyNotification.child("Applications").child(notificationkey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                mUserActivities.child(mAuth.getCurrentUser().getUid()).child("ApplyNotification").child(notificationkey).removeValue();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mstartapplyLay = (RelativeLayout) findViewById(R.id.startapplyLay);

        mApplicantList = (RecyclerView)findViewById(R.id.applicantlist);
        mApplicantList.setHasFixedSize(false);

        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setReverseLayout(false);
        mLayoutManager.setStackFromEnd(false);

        mApplicantList.setLayoutManager(mLayoutManager);

        FirebaseRecyclerAdapter<ApplicantsInfo, BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ApplicantsInfo, BlogViewHolder>(
                ApplicantsInfo.class,
                R.layout.applicantsrow,
                BlogViewHolder.class,
                mUserActivities.child(mAuth.getCurrentUser().getUid()).child("Posted").child(post_key).child("shortlistedapplicants").orderByChild("negatedtime")

        ) {

            @Override
            protected void populateViewHolder(final BlogViewHolder viewHolder, ApplicantsInfo model, int position) {

                final String otheruserid = model.getuserid();
                final String otherusername = model.getName();

                viewHolder.setNameandLocation(model.getName(), otheruserid);
                viewHolder.setWorkTitleandCompany(otheruserid);
                viewHolder.setpostImage(getApplicationContext(), model.getImage(), otheruserid);
               // viewHolder.setApplicationStatus(otherusername);

                viewHolder.mcardview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        viewHolder.mprofileLay.setBackgroundResource(R.drawable.applicant_grey_round);
                        viewHolder.mpostTime.setTextColor(Color.BLACK);

                        Intent otheruserintent = new Intent(ShortListedApplicants.this, ShortListedApplicantUserProfile.class);
                        otheruserintent.putExtra("user_uid",otheruserid);
                        otheruserintent.putExtra("city",city);
                        otheruserintent.putExtra("post_title",post_title);
                        otheruserintent.putExtra("post_desc",post_desc);
                        startActivity(otheruserintent);
                    }
                });
            }
        };

        mApplicantList.setAdapter(firebaseRecyclerAdapter);

        mUserActivities.child(mAuth.getCurrentUser().getUid()).child("Posted").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.child("Posted").hasChild(post_key)){
                        if(dataSnapshot.child("Posted").child(post_key).hasChild("shortlistedapplicants")){
                            mstartapplyLay.setVisibility(GONE);
                        }
                        else{
                            mstartapplyLay.setVisibility(VISIBLE);
                        }
                    }
                    else{
                        mstartapplyLay.setVisibility(VISIBLE);
                    }
                }
                else{
                    mstartapplyLay.setVisibility(VISIBLE);
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

    public static class BlogViewHolder extends RecyclerView.ViewHolder{

        View mView;
        LinearLayout mshortlistedLay;
        RelativeLayout mprofileLay;
        CircleImageView mprofilepic;
        CardView mcardview;
        TextView mpostName, mpostTime, mpostLocation, mworktitle1, mworkcompany1, mworktitle2, mworkcompany2, mworktitle3, mworkcompany3 ;
        CountDownTimer mCountDownTimer;

        FirebaseAuth mAuth;
        DatabaseReference mUserActivities, mUserInfo, mUserLocation;

        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            mAuth = FirebaseAuth.getInstance();

            mUserActivities =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserActivities");
            mUserActivities.keepSynced(true);

            mUserInfo =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserInfo");
            mUserInfo.keepSynced(true);

            mUserLocation =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserLocation");
            mUserLocation.keepSynced(true);

            mprofilepic = (CircleImageView) mView.findViewById(R.id.profilepic);
            mcardview = (CardView) mView.findViewById(cardview);
            mprofileLay = (RelativeLayout) mView.findViewById(R.id.profileLay);
            mprofileLay.setBackgroundResource(R.drawable.applicant_grey_round);

            mpostTime = (TextView)mView.findViewById(postTime);
            mpostTime.setVisibility(GONE);
            mpostName = (TextView)mView.findViewById(R.id.postName);
            mpostLocation = (TextView)mView.findViewById(postLocation);
            mworktitle1 = (TextView)mView.findViewById(R.id.worktitle1);
            mworkcompany1 = (TextView)mView.findViewById(workcompany1);
            mworktitle2 = (TextView)mView.findViewById(R.id.worktitle2);
            mworkcompany2 = (TextView)mView.findViewById(workcompany2);
            mshortlistedLay = (LinearLayout)mView.findViewById(R.id.shortlistedLay);
        }


        /*public void setApplicationStatus(final String otherusername){

            if(otherusername!=null ){
                mpostTime.setVisibility(GONE);
                mshortlistedLay.setVisibility(VISIBLE);
                mshortlisttxt.setText("You've shortlisted "+otherusername );
            }
        }*/


        public void setNameandLocation(final String name, final String otheruserid){
            if(name!=null && otheruserid!= null) {
                mUserInfo.child(otheruserid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild("Name")) {
                            String user_name = dataSnapshot.child("Name").getValue().toString();
                            mpostName.setText(user_name);
                        }
                        else{
                            mpostName.setText(name);
                        }

                        if (dataSnapshot.hasChild("Address")) {
                            String Address = dataSnapshot.child("Address").getValue().toString();
                            mpostLocation.setText(Address);
                            mpostLocation.setVisibility(VISIBLE);
                        } else {
                            mUserLocation.child(otheruserid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild("CurrentCity")) {
                                        String Address = dataSnapshot.child("CurrentCity").getValue().toString();
                                        mpostLocation.setText(Address);
                                        mpostLocation.setVisibility(VISIBLE);
                                    } else {
                                        mpostLocation.setText("");
                                        mpostLocation.setVisibility(GONE);
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
        public void setWorkTitleandCompany(final String otheruserid){
            if(otheruserid!= null) {

                mUserInfo.child(otheruserid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild("WorkExp1")) {

                            String worktitle = dataSnapshot.child("WorkExp1").child("worktitle").getValue().toString();
                            mworktitle1.setText(worktitle);

                            String workcompany = dataSnapshot.child("WorkExp1").child("workcompany").getValue().toString();
                            mworkcompany1.setText(workcompany);
                        }
                        else{
                            mworktitle1.setText("No Work Experiences");
                            mworkcompany1.setVisibility(GONE);
                        }

                        if(dataSnapshot.hasChild("WorkExp2")) {

                            String worktitle = dataSnapshot.child("WorkExp2").child("worktitle").getValue().toString();
                            mworktitle2.setVisibility(VISIBLE);
                            mworktitle2.setText(worktitle);

                            String workcompany = dataSnapshot.child("WorkExp2").child("workcompany").getValue().toString();
                            mworkcompany2.setVisibility(VISIBLE);
                            mworkcompany2.setText(workcompany);
                        }
                        else{
                            mworktitle2.setVisibility(GONE);
                            mworkcompany2.setVisibility(GONE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }


        public void setpostImage(final Context ctx, final String postimage, final String otheruserid){

            if(otheruserid!= null && postimage!=null) {

                mUserInfo.child(otheruserid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("UserImage")) {
                            String post_userimage = dataSnapshot.child("UserImage").getValue().toString();
                            if (post_userimage != null){
                                if(post_userimage.equals("default")){
                                    mprofilepic.setImageResource(R.drawable.defaultprofile_pic);
                                } else {
                                    Glide.with(ctx).load(post_userimage)
                                            .thumbnail(0.5f)
                                            .centerCrop()
                                            .error(R.drawable.error3)
                                            .placeholder(R.drawable.loading_spinner)
                                            .dontAnimate()
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .into(mprofilepic);
                                }
                            }
                        } else {
                            if (postimage.equals("default")) {
                                    Glide.with(ctx).load(R.drawable.defaultprofile_pic)
                                            .thumbnail(0.5f)
                                            .centerCrop()
                                            .error(R.drawable.error3)
                                            .placeholder(R.drawable.loading_spinner)
                                            .dontAnimate()
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .into(mprofilepic);
                                } else {
                                    Glide.with(ctx).load(postimage)
                                            .thumbnail(0.5f)
                                            .centerCrop()
                                            .error(R.drawable.error3)
                                            .placeholder(R.drawable.loading_spinner)
                                            .dontAnimate()
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .into(mprofilepic);
                            }

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
