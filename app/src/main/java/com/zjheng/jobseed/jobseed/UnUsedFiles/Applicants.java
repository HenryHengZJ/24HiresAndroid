package com.zjheng.jobseed.jobseed.UnUsedFiles;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.zjheng.jobseed.jobseed.ApplicantsScene.ApplicantUserProfile;
import com.zjheng.jobseed.jobseed.CustomObjectClass.ApplicantsInfo;
import com.zjheng.jobseed.jobseed.R;

import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.zjheng.jobseed.jobseed.R.id.cardview;
import static com.zjheng.jobseed.jobseed.R.id.postLocation;
import static com.zjheng.jobseed.jobseed.R.id.postTime;
import static com.zjheng.jobseed.jobseed.R.id.workcompany1;
import static com.zjheng.jobseed.jobseed.R.id.workcompany2;

public class Applicants extends AppCompatActivity {

    private RecyclerView mApplicantList;
    private LinearLayoutManager mLayoutManager;
    private RelativeLayout mstartapplyLay;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserActivities , mJob, mUserChatList, mChatRoom, mApplyNotification;

    private static final String TAG = "Applicants";

    private String userid, post_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applicants_list);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        userid = mAuth.getCurrentUser().getUid();

        post_key = getIntent().getStringExtra("post_id");

        mUserActivities =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserActivities");
        mUserActivities.keepSynced(true);

        mUserChatList =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserChatList");
        mUserChatList.keepSynced(true);

        mChatRoom =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("ChatRoom");
        mChatRoom.keepSynced(true);

        mApplyNotification =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("ApplyNotification");
        mApplyNotification.keepSynced(true);

        mJob =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("Job");
        mJob.keepSynced(true);

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
                mUserActivities.child(mAuth.getCurrentUser().getUid()).child("Posted").child(post_key).child("applicants").orderByChild("negatedtime")

        ) {

            @Override
            protected void populateViewHolder(final BlogViewHolder viewHolder, ApplicantsInfo model, int position) {

                final String otheruserid = getRef(position).getKey();
                final String otherusername = model.getName();
               // final String shortlistedval = model.getshortlisted();

                viewHolder.setNameandLocation(model.getName(), otheruserid);
                viewHolder.setWorkTitleandCompany(otheruserid);
                viewHolder.setpostImage(getApplicationContext(), model.getImage(), otheruserid);
                viewHolder.setNewStatus(model.getpressed());
              //  viewHolder.setApplicationStatus(model.getshortlisted(),model.getrejected(),model.gettime(),otherusername, otheruserid, post_key,model.getnewapplicantscount());

               /* viewHolder.mcardview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        viewHolder.mprofileLay.setBackgroundResource(R.drawable.applicant_grey_round);
                        viewHolder.mpostTime.setTextColor(Color.parseColor("#c0000000"));

                        mUserActivities.child(mAuth.getCurrentUser().getUid()).child("Posted").child(post_key).child("applicants").child(otheruserid).child("pressed").setValue("true");

                        if(shortlistedval.equals("true")){
                            Intent otheruserintent = new Intent(Applicants.this, ShortListedApplicantUserProfile.class);
                            otheruserintent.putExtra("user_uid",otheruserid);
                            startActivity(otheruserintent);
                        }
                        else{
                            Intent otheruserintent = new Intent(Applicants.this, ApplicantUserProfile.class);
                            otheruserintent.putExtra("user_uid",otheruserid);
                            otheruserintent.putExtra("post_key",post_key);
                            startActivity(otheruserintent);
                        }
                    }
                });*/
            }
        };

        mApplicantList.setAdapter(firebaseRecyclerAdapter);

        mUserActivities.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("Posted")){
                    if(dataSnapshot.child("Posted").hasChild(post_key)){
                        if(dataSnapshot.child("Posted").child(post_key).hasChild("applicants")){
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

            mpostTime = (TextView)mView.findViewById(postTime);
            mpostName = (TextView)mView.findViewById(R.id.postName);
            mpostLocation = (TextView)mView.findViewById(postLocation);
            mworktitle1 = (TextView)mView.findViewById(R.id.worktitle1);
            mworkcompany1 = (TextView)mView.findViewById(workcompany1);
            mworktitle2 = (TextView)mView.findViewById(R.id.worktitle2);
            mworkcompany2 = (TextView)mView.findViewById(workcompany2);
            mshortlistedLay = (LinearLayout)mView.findViewById(R.id.shortlistedLay);
        }

        public void decrementapplicantscount(String ownuserid, String postkey) {

            mUserActivities.child(ownuserid).child("Posted").child(postkey).child("applicantscount").runTransaction(new Transaction.Handler() {
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

        private void decrementneweapplicantscount(String ownuserid, String postkey) {

            mUserActivities.child(ownuserid).child("Posted").child(postkey).child("newapplicantscount").runTransaction(new Transaction.Handler() {
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

        public void setNewStatus(String pressed) {

            if (pressed!=null) {

                if(pressed.equals("true")){
                    mprofileLay.setBackgroundResource(R.drawable.applicant_grey_round);
                    mpostTime.setTextColor(Color.parseColor("#c0000000"));
                }
                else{
                    mprofileLay.setBackgroundResource(R.drawable.applicant_red_round);
                    mpostTime.setTextColor(Color.parseColor("#FF0466DF"));
                }
            }
        }



        public void setApplicationStatus(final String shortlisted, final String rejected, final Long time, final String otherusername,
                                         final String otheruserid, final String postkey, final Long newapplicantscount){

            if(shortlisted!=null && rejected!=null && time!=null){

                if(shortlisted.equals("true")){
                    mpostTime.setVisibility(GONE);
                    mshortlistedLay.setVisibility(VISIBLE);
                   // mshortlisttxt.setText("You've shortlisted "+otherusername );
                }
                else if(rejected.equals("true")){
                    mcardview.setVisibility(GONE);
                }
                else{
                    mpostTime.setVisibility(VISIBLE);
                    mshortlistedLay.setVisibility(GONE);

                    Date d = new Date(time);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(d);

                    final long endtimeinmilliseconds = cal.getTimeInMillis()+(24*60*60*1000);
                    final long tsLong = System.currentTimeMillis();

                    Date datenow = new Date(tsLong);

                    final long timeleft = endtimeinmilliseconds - tsLong;

                    Calendar c= Calendar.getInstance();
                    c.setTime(d);
                    c.add(Calendar.HOUR,24);

                    if(c.getTime().compareTo(datenow)<0){
                        //if applied date is over 1 day
                        mcardview.setVisibility(GONE);
                    }
                    else{

                        if (mCountDownTimer!= null) {
                            mCountDownTimer.cancel();
                        }

                        mCountDownTimer = new CountDownTimer(timeleft, 1000) {

                            @Override
                            public void onTick(long millisUntilFinished) {

                                long seconds = millisUntilFinished / 1000;
                                long minutes = seconds / 60;
                                long hours = minutes / 60;

                                String time = String.format("%02d:%02d:%02d", hours % 24, minutes % 60 , seconds % 60);

                                mpostTime.setText(time);
                            }

                            @Override
                            public void onFinish() {
                                mcardview.setVisibility(GONE);

                                mUserActivities.child(mAuth.getCurrentUser().getUid()).child("Posted").child(postkey).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.hasChild("newapplicants")){
                                            if(dataSnapshot.child("newapplicants").hasChild(otheruserid)){
                                                //If user is still in new applicants list, decrement new applicant count
                                                decrementneweapplicantscount(mAuth.getCurrentUser().getUid(), postkey);
                                                mUserActivities.child(mAuth.getCurrentUser().getUid()).child("Posted").child(postkey).child("newapplicants").child(otheruserid).removeValue();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                                mUserActivities.child(mAuth.getCurrentUser().getUid()).child("Posted").child(postkey).child("applicants").child(otheruserid).removeValue();
                                //decrement applicant count
                                decrementapplicantscount(mAuth.getCurrentUser().getUid(), postkey);

                                mUserActivities.child(otheruserid).child("Applied").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        //Check if user applied tab still has the job or not
                                        if(dataSnapshot.hasChild(postkey)){
                                            //If user still has the job, check if the job has been rejected or not
                                            if(dataSnapshot.child(postkey).hasChild("rejected")){
                                                String rejectedval = dataSnapshot.child(postkey).child("rejected").getValue().toString();
                                                if(rejectedval.equals("false")){
                                                    Log.d(TAG, "applicants rejectd false");
                                                    //If the job has not been rejected, and passed 24 hours, notify user job rejected, and delete user from owner's applicant list
                                                    mUserActivities.child(otheruserid).child("NewMainNotification").setValue("true");
                                                    mUserActivities.child(otheruserid).child("NewApplied").setValue("true");
                                                    mUserActivities.child(otheruserid).child("Applied").child(postkey).child("rejected").setValue("true");
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        };
                        mCountDownTimer.start();
                    }
                }

            }
        }


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
                                        mpostLocation.setText("No Location");
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
                            mworkcompany1.setText(" at "+workcompany);
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
                            mworkcompany2.setText(" at "+workcompany);
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
