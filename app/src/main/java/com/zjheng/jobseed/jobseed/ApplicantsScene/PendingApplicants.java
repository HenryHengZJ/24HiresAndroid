package com.zjheng.jobseed.jobseed.ApplicantsScene;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.support.v4.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.zjheng.jobseed.jobseed.CustomObjectClass.ApplicantsInfo;
import com.zjheng.jobseed.jobseed.R;

import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.zjheng.jobseed.jobseed.R.id.cardview;
import static com.zjheng.jobseed.jobseed.R.id.postLocation;
import static com.zjheng.jobseed.jobseed.R.id.postTime;
import static com.zjheng.jobseed.jobseed.R.id.workcompany1;
import static com.zjheng.jobseed.jobseed.R.id.workcompany2;

public class PendingApplicants extends Fragment {

    private RecyclerView mApplicantList;
    private LinearLayoutManager mLayoutManager;
    private RelativeLayout mstartapplyLay;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserActivities , mJob, mUserChatList, mChatRoom,
            mApplyNotification, mUserPostedPendingApplicants, mUserPosted,
            mUserLocation, mUserInfo;

    private static final String TAG = "Applicants";

    private String ownuserid, post_key, city, post_title, post_desc;

    Activity context;
    View rootView;

    public static PendingApplicants newInstance(String post_id, String city, String post_title, String post_desc) {
        PendingApplicants result = new PendingApplicants();
        Bundle bundle = new Bundle();
        bundle.putString("post_id", post_id);
        bundle.putString("city", city);
        bundle.putString("post_title", post_title);
        bundle.putString("post_desc", post_desc);
        result.setArguments(bundle);
        return result;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        post_key = bundle.getString("post_id");
        city = bundle.getString("city");
        post_title = bundle.getString("post_title");
        post_desc = bundle.getString("post_desc");
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.activity_applicants_list, container, false);

        context = getActivity();

        mAuth = FirebaseAuth.getInstance();

        ownuserid = mAuth.getCurrentUser().getUid();

        mUserInfo =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserInfo");

        mUserLocation =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserLocation");

        mUserActivities =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserActivities");

        mUserPostedPendingApplicants =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserPostedPendingApplicants");

        mUserPosted =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserPosted");

        mUserChatList =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserChatList");

        mChatRoom =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("ChatRoom");

        mJob =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("Job");

        mstartapplyLay = (RelativeLayout) rootView.findViewById(R.id.startapplyLay);

        mApplicantList = (RecyclerView)rootView.findViewById(R.id.applicantlist);
        mApplicantList.setHasFixedSize(false);

        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setReverseLayout(false);
        mLayoutManager.setStackFromEnd(false);

        mApplicantList.setLayoutManager(mLayoutManager);

        mUserPostedPendingApplicants.child(mAuth.getCurrentUser().getUid()).child(post_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "pending dataSnapshot.getChildrenCount " + dataSnapshot.getChildrenCount());
                for (DataSnapshot usersdataSnapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "pending single dataSnapshot.key " + usersdataSnapshot.getKey());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        FirebaseRecyclerAdapter<ApplicantsInfo, BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ApplicantsInfo, BlogViewHolder>(
                ApplicantsInfo.class,
                R.layout.applicantsrow,
                BlogViewHolder.class,
                mUserPostedPendingApplicants.child(mAuth.getCurrentUser().getUid()).child(post_key).orderByChild("negatedtime")

        ) {

            @Override
            protected void populateViewHolder(final BlogViewHolder viewHolder, ApplicantsInfo model, int position) {

                final String otheruserid = getRef(position).getKey();

                viewHolder.setNameandLocation(mUserInfo, mUserLocation, model.getName(), otheruserid);
                viewHolder.setWorkTitleandCompany(mUserInfo, otheruserid);
                viewHolder.setpostImage(mUserInfo, getApplicationContext(), model.getImage(), otheruserid);
                viewHolder.setNewStatus(model.getpressed());
                viewHolder.setApplicationStatus(mUserPosted,mUserPostedPendingApplicants, mUserActivities, ownuserid, model.gettime(), otheruserid, post_key, model.getpressed());

                viewHolder.mcardview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        viewHolder.mprofileLay.setBackgroundResource(R.drawable.applicant_grey_round);
                        viewHolder.mpostTime.setTextColor(Color.parseColor("#c0000000"));

                        mUserPostedPendingApplicants.child(mAuth.getCurrentUser().getUid()).child(post_key).child(otheruserid).child("pressed").setValue("true");

                            Intent otheruserintent = new Intent(context, ApplicantUserProfile.class);
                            otheruserintent.putExtra("user_uid",otheruserid);
                            otheruserintent.putExtra("post_key",post_key);
                            otheruserintent.putExtra("city",city);
                            otheruserintent.putExtra("post_title",post_title);
                            otheruserintent.putExtra("post_desc",post_desc);
                            otheruserintent.putExtra("applicant_status","1");
                            startActivity(otheruserintent);
                            //startActivity(otheruserintent);

                    }
                });
            }
        };

        mApplicantList.setAdapter(firebaseRecyclerAdapter);

        mUserPostedPendingApplicants.child(mAuth.getCurrentUser().getUid()).child(post_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    mstartapplyLay.setVisibility(GONE);
                }
                else{
                    mstartapplyLay.setVisibility(VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return rootView;

    }


    public static class BlogViewHolder extends RecyclerView.ViewHolder{

        View mView;
        RelativeLayout mprofileLay;
        CircleImageView mprofilepic;
        CardView mcardview;
        TextView mpostName, mpostTime, mpostLocation, mworktitle1, mworkcompany1, mworktitle2, mworkcompany2, mworktitle3, mworkcompany3 ;
        CountDownTimer mCountDownTimer;

        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

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
        }

        public void decrementapplicantscount(DatabaseReference mUserPosted, String ownuserid, String postkey) {

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

        private void decrementneweapplicantscount(DatabaseReference mUserPosted, String ownuserid, String postkey) {

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



        public void setApplicationStatus(
                final DatabaseReference mUserPosted, final DatabaseReference mUserPostedPendingApplicants, final DatabaseReference mUserActivities,
                final String ownuserid, final Long time, final String otheruserid, final String postkey, final String pressed){

            if(ownuserid!=null && otheruserid!=null && postkey!=null && time!=null && pressed!= null){

                    mpostTime.setVisibility(VISIBLE);

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

                                if (pressed.equals("false")) {
                                    decrementneweapplicantscount(mUserPosted,ownuserid, postkey);
                                }

                                mUserPostedPendingApplicants.child(ownuserid).child(postkey).child(otheruserid).removeValue();
                                //decrement applicant count
                                decrementapplicantscount(mUserPosted,ownuserid, postkey);

                                mUserActivities.child(otheruserid).child("Applied").child(postkey).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        //Check if user applied tab still has the job or not
                                        if(dataSnapshot.exists()){
                                            //If user still has the job, check if the job has been rejected or not
                                            if(dataSnapshot.hasChild("status")){
                                                String statusval = dataSnapshot.child("status").getValue().toString();
                                                if(statusval.equals("applied")){
                                                    Log.d(TAG, "applicants rejectd false");
                                                    //If the job has not been rejected, and passed 24 hours, notify user job rejected, and delete user from owner's applicant list
                                                    mUserActivities.child(otheruserid).child("NewMainNotification").setValue("true");
                                                    mUserActivities.child(otheruserid).child("NewApplied").setValue("true");
                                                    mUserActivities.child(otheruserid).child("Applied").child(postkey).child("status").setValue("appliedrejected");
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


        public void setNameandLocation(final DatabaseReference mUserInfo, final DatabaseReference mUserLocation, final String name, final String otheruserid){
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
        public void setWorkTitleandCompany( final DatabaseReference mUserInfo, final String otheruserid){
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
                            mworkcompany1.setVisibility(View.INVISIBLE);
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


        public void setpostImage(final DatabaseReference mUserInfo, final Context ctx, final String postimage, final String otheruserid){

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
                                            .error(R.drawable.defaultprofile_pic)
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
                                            .error(R.drawable.defaultprofile_pic)
                                            .placeholder(R.drawable.loading_spinner)
                                            .dontAnimate()
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .into(mprofilepic);
                                } else {
                                    Glide.with(ctx).load(postimage)
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

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
    }
}
