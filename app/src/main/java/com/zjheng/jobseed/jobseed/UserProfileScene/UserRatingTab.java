package com.zjheng.jobseed.jobseed.UserProfileScene;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.zjheng.jobseed.jobseed.OtherUserScene.OtherUser;
import com.zjheng.jobseed.jobseed.R;
import com.zjheng.jobseed.jobseed.CustomObjectClass.UserReview;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by zhen on 5/5/2017.
 */

public class UserRatingTab extends Fragment {

    private RecyclerView mreviewlist;
    private LinearLayoutManager mLayoutManager;
    private FirebaseRecyclerAdapter<UserReview, BlogViewHolder> firebaseRecyclerAdapter;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserReview, mUserAccount;
    private Query mQuery;

    private RelativeLayout mnoreviewLay;

    private CardView mreviewcardview;
    private LinearLayout mreviewLay, mshortlistedLay;

    private static final String TAG = "UserRating";

    private String userid , ReviewCount;
    private ProgressDialog mProgress;
    private long reviewcount5 = 0, reviewcount4 = 0, reviewcount3 = 0, reviewcount2 = 0 , reviewcount1 = 0, totalreviewcount = 0;
    private int removeCount;
    private int reviewlimit = 20;

    Activity context;
    View rootView;

    public static UserRatingTab newInstance(String userid) {
        UserRatingTab result = new UserRatingTab();
        Bundle bundle = new Bundle();
        bundle.putString("useruid", userid);
        result.setArguments(bundle);
        return result;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        userid = bundle.getString("useruid");
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.activity_review, container, false);

        context = getActivity();

        Log.d(TAG, "UserRating");

        mreviewlist = (RecyclerView)rootView.findViewById(R.id.reviewlist);
        mreviewcardview = (CardView)rootView.findViewById(R.id.reviewcardview);
        mreviewcardview.setVisibility(GONE);
        mshortlistedLay = (LinearLayout)rootView.findViewById(R.id.shortlistedLay);
        mreviewLay = (LinearLayout)rootView.findViewById(R.id.reviewLay);
        mreviewLay.setVisibility(GONE);
        mnoreviewLay = (RelativeLayout) rootView.findViewById(R.id.noreviewLay);

        mAuth = FirebaseAuth.getInstance();

        mUserReview = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserReview");

        mUserAccount =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserAccount");

        mreviewlist.setHasFixedSize(false);
        mLayoutManager = new LinearLayoutManager(context.getApplicationContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        mreviewlist.setLayoutManager(mLayoutManager);
        mreviewlist.setAdapter(new SampleRecycler());

        mUserReview.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("Review")){

                    mnoreviewLay.setVisibility(GONE);

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

                    if(totalreviewcount>reviewlimit){
                        mQuery = mUserReview.child(userid).child("Review").limitToFirst(reviewlimit);
                        setupview(mQuery);
                        mreviewlist.setAdapter(firebaseRecyclerAdapter);
                    }
                    else{
                        mQuery = mUserReview.child(userid).child("Review");
                        setupview(mQuery);
                        mreviewlist.setAdapter(firebaseRecyclerAdapter);
                    }
                }
                else{
                    Log.d(TAG, "mnoreviewLay");

                    mnoreviewLay.setVisibility(VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        return rootView;
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
                viewHolder.setuserimage(context, model.getuserimage());
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
}

