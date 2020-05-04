package com.zjheng.jobseed.jobseed.ApplicantsScene;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.zjheng.jobseed.jobseed.CustomObjectClass.ApplicantsInfo;
import com.zjheng.jobseed.jobseed.R;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.zjheng.jobseed.jobseed.R.id.cardview;
import static com.zjheng.jobseed.jobseed.R.id.postLocation;
import static com.zjheng.jobseed.jobseed.R.id.workcompany1;
import static com.zjheng.jobseed.jobseed.R.id.workcompany2;

public class ShortlistedApplicants extends Fragment {

    private RecyclerView mApplicantList;
    private LinearLayoutManager mLayoutManager;
    private RelativeLayout mstartapplyLay;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserActivities , mJob, mUserChatList, mChatRoom,
            mApplyNotification, mUserPostedShortlistedApplicants, mUserInfo, mUserLocation;
    private Query mQuerySyok;
    private ChildEventListener mChild;

    private static final String TAG = "Applicants";

    private String ownuserid, post_key, city, post_title, post_desc;

    private FirebaseRecyclerAdapter<ApplicantsInfo, BlogViewHolder> firebaseRecyclerAdapter;

    private Boolean isStarted = false;
    private Boolean isVisible = false;
    private Boolean firsttime = false;

    private ProgressDialog myLoadingDialog;

    Activity context;
    View rootView;

    public static ShortlistedApplicants newInstance(String post_id, String city, String post_title, String post_desc) {
        ShortlistedApplicants result = new ShortlistedApplicants();
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

        Log.d(TAG, "SHORTLIST onCreateView");

        firsttime = true;

        loadShortlistData();

        return rootView;

    }

    /*@Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.d(TAG, "SHORTLIST setUserVisibleHint");
        isVisible = isVisibleToUser;
        if (isStarted && isVisible) {
            viewDidAppear();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "SHORTLIST on start");
        isStarted = true;
        if (isVisible && isStarted){
            viewDidAppear();
        }
    }

    public void viewDidAppear() {
        // your logic

        Log.d(TAG, "SHORTLIST view did appear ");

        if (firsttime) {
            loadShortlistData();
            firsttime = false;
        }

    }*/

    private void loadShortlistData() {

        Log.d(TAG, "SHORTLIST loadShortlistData ");

        mAuth = FirebaseAuth.getInstance();

        ownuserid = mAuth.getCurrentUser().getUid();

        mUserInfo =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserInfo");

        mUserLocation =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserLocation");

        mUserActivities =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserActivities");

        mUserChatList =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserChatList");

        mUserPostedShortlistedApplicants =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserPostedShortlistedApplicants");

        mChatRoom =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("ChatRoom");

        mApplyNotification =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("ApplyNotification");

        mJob =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("Job");

        mstartapplyLay = (RelativeLayout) rootView.findViewById(R.id.startapplyLay);

        mApplicantList = (RecyclerView)rootView.findViewById(R.id.applicantlist);
        mApplicantList.setHasFixedSize(false);

        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setReverseLayout(false);
        mLayoutManager.setStackFromEnd(false);

        mApplicantList.setLayoutManager(mLayoutManager);

        mUserPostedShortlistedApplicants.child(mAuth.getCurrentUser().getUid()).child(post_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "shortlisted dataSnapshot.getChildrenCount " + dataSnapshot.getChildrenCount());
                for (DataSnapshot usersdataSnapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "shortlisted single dataSnapshot.key " + usersdataSnapshot.getKey());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ApplicantsInfo, BlogViewHolder>(
                ApplicantsInfo.class,
                R.layout.applicantsrow,
                BlogViewHolder.class,
                mUserPostedShortlistedApplicants.child(mAuth.getCurrentUser().getUid()).child(post_key).orderByChild("negatedtime")

        ) {

            @Override
            protected void populateViewHolder(final BlogViewHolder viewHolder, ApplicantsInfo model, int position) {

                final String otheruserid = getRef(position).getKey();
                Log.d(TAG, "shortlisted otheruserid " + otheruserid);

                viewHolder.setNameandLocation(mUserInfo, mUserLocation, model.getName(), otheruserid);
                viewHolder.setWorkTitleandCompany(mUserInfo, otheruserid);
                viewHolder.setpostImage(mUserInfo, getApplicationContext(), model.getImage(), otheruserid);

                viewHolder.mcardview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent otheruserintent = new Intent(context, ApplicantUserProfile.class);
                        otheruserintent.putExtra("user_uid",otheruserid);
                        otheruserintent.putExtra("post_key",post_key);
                        otheruserintent.putExtra("city",city);
                        otheruserintent.putExtra("post_title",post_title);
                        otheruserintent.putExtra("post_desc",post_desc);
                        otheruserintent.putExtra("applicant_status","2");
                        startActivity(otheruserintent);

                    }
                });
            }
        };

        mApplicantList.setAdapter(firebaseRecyclerAdapter);


        mUserPostedShortlistedApplicants.child(mAuth.getCurrentUser().getUid()).child(post_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Log.d(TAG, "mUserPostedShortlistedApplicants changed " + dataSnapshot.getChildren());
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
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if ( mQuerySyok != null && mChild != null) {
            mQuerySyok.removeEventListener(mChild);
        }
        Log.d(TAG, "shortlist stopped");
        firebaseRecyclerAdapter.cleanup();
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder{

        View mView;
        RelativeLayout mprofileLay;
        CircleImageView mprofilepic;
        CardView mcardview;
        TextView mpostName, mpostLocation, mworktitle1, mworkcompany1, mworktitle2, mworkcompany2, mworktitle3, mworkcompany3 ;
        TextView mshortlisttxt, mmessagetxt;
        CountDownTimer mCountDownTimer;

        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            mprofilepic = (CircleImageView) mView.findViewById(R.id.profilepic);
            mcardview = (CardView) mView.findViewById(cardview);
            mprofileLay = (RelativeLayout) mView.findViewById(R.id.profileLay);

            mpostName = (TextView)mView.findViewById(R.id.postName);
            mpostLocation = (TextView)mView.findViewById(postLocation);
            mworktitle1 = (TextView)mView.findViewById(R.id.worktitle1);
            mworkcompany1 = (TextView)mView.findViewById(workcompany1);
            mworktitle2 = (TextView)mView.findViewById(R.id.worktitle2);
            mworkcompany2 = (TextView)mView.findViewById(workcompany2);
        }


        public void setNameandLocation(final DatabaseReference mUserInfo, final DatabaseReference mUserLocation,final String name, final String otheruserid){
            if(name!=null && otheruserid!= null) {
                mUserInfo.child(otheruserid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild("Name")) {
                            String user_name = dataSnapshot.child("Name").getValue().toString();
                            mpostName.setText(user_name);
                            Log.d(TAG, "shortlisted user_name " + user_name);
                        }
                        else{
                            mpostName.setText(name);
                            Log.d(TAG, "shortlisted name " + name);
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
        public void setWorkTitleandCompany(final DatabaseReference mUserInfo, final String otheruserid){
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
