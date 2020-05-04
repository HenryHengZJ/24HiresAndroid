package com.zjheng.jobseed.jobseed.TalentDetails;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zjheng.jobseed.jobseed.ApplicantsScene.HiredApplicants;
import com.zjheng.jobseed.jobseed.CustomObjectClass.TalentInfo;
import com.zjheng.jobseed.jobseed.CustomObjectClass.UserReview;
import com.zjheng.jobseed.jobseed.R;
import com.zjheng.jobseed.jobseed.SearchTalentScene.SearchTalent;
import com.zjheng.jobseed.jobseed.SearchTalentScene.SearchTalentRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.zjheng.jobseed.jobseed.R.id.reviewcount;

public class SeeMoreReview extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mUserTalentReview;

    private RecyclerView mReviewList;
    private LinearLayoutManager mLayoutManager;

    private List<UserReview> reviewlist;
    private ReviewRecyclerAdapter recyclerAdapter;

    private static final String TAG = "SeeMoreReview";

    private String userid, postkey, talentowner_uid;

    private int loadlimit = 8;

    private int count = 0;

    private Long negatedtime;

    private boolean loading = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seemorereview);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        userid = mAuth.getCurrentUser().getUid();

        talentowner_uid = getIntent().getStringExtra("talentowner_uid");
        postkey = getIntent().getStringExtra("postkey");

        mUserTalentReview = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserTalentReview");

        mReviewList = (RecyclerView) findViewById(R.id.morereviewlist);
        mReviewList.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setReverseLayout(false);
        mLayoutManager.setStackFromEnd(false);

        reviewlist = new ArrayList<UserReview>();
        recyclerAdapter = new ReviewRecyclerAdapter(reviewlist,SeeMoreReview.this);
        mReviewList.setLayoutManager(mLayoutManager);
        mReviewList.setAdapter(recyclerAdapter);

        recyclerviewlistener();

        Log.e(TAG,"talentowner_uid" + talentowner_uid);
        Log.e(TAG,"postkey" + postkey);

        mUserTalentReview.child(talentowner_uid).child(postkey).child("Review").orderByChild("negatedtime").limitToFirst(loadlimit).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot usersdataSnapshot : dataSnapshot.getChildren()){

                    UserReview reviews = new UserReview();

                    String username = usersdataSnapshot.child("username").getValue().toString();
                    String postkey = usersdataSnapshot.getKey();
                    String reviewmessage = usersdataSnapshot.child("reviewmessage").getValue().toString();
                    String userimage = usersdataSnapshot.child("userimage").getValue().toString();
                    Long rating = (Long)usersdataSnapshot.child("rating").getValue();
                    Long time = (Long)usersdataSnapshot.child("time").getValue();
                    negatedtime = (Long)usersdataSnapshot.child("negatedtime").getValue();


                    reviews.setusername(username);
                    reviews.setreviewmessage(reviewmessage);
                    reviews.setuserimage(userimage);
                    reviews.settime(time);
                    reviews.setrating(Integer.valueOf(rating.intValue()));

                    reviewlist.add(reviews);

                    count++;

                    if (count == loadlimit) {
                        reviewlist.add(null);
                        recyclerAdapter.notifyItemInserted(reviewlist.size() - 1);
                    }

                }

                if (count < loadlimit){
                    loading = false;
                }
                else{
                    loading = true;
                }

                if (reviewlist.isEmpty()) {
                    loading = false;
                } else {
                    recyclerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void recyclerviewlistener() {
        mReviewList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                //super.onScrolled(recyclerView, dx, dy);

                if(dy < 0) {

                    int visibleItemCount = mLayoutManager.getChildCount();

                    int totalItemCount = mLayoutManager.getItemCount();

                    int pastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();

                    if ((pastVisibleItems + visibleItemCount) >= totalItemCount) {
                        Log.d(TAG, "ntg");
                    }
                    else if (pastVisibleItems == 0){

                        Log.d(TAG, "dunoapa");

                        /*if (loading)
                        {
                            Log.d(TAG, "loading");
                            //End of list
                            loading = false;

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    reviewlist.remove(0);
                                    recyclerAdapter.notifyItemRemoved(0);
                                    loadMore(loadlimit);
                                }
                            }, 750); //time seconds
                        }*/
                    }
                }

                if(dy > 0) {
                    int lastVisibleItems = mLayoutManager.findLastVisibleItemPosition();

                    if (lastVisibleItems == reviewlist.size() - 1) {
                        Log.d(TAG, "at btm");

                        if (loading)
                        {
                            Log.d(TAG, "loading");
                            //End of list
                            loading = false;

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    reviewlist.remove(reviewlist.size()-1);
                                    recyclerAdapter.notifyItemRemoved(reviewlist.size());
                                    loadMore(loadlimit);

                                }
                            }, 750); //time seconds
                        }
                    }
                }
            }
        });
    }

    protected void loadMore(final int loadlimit){

        count = 0;

        mUserTalentReview.child(talentowner_uid).child(postkey).orderByChild("negatedtime").limitToFirst(loadlimit+1).startAt(negatedtime).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Boolean firstkey = true;
                for(DataSnapshot usersdataSnapshot1 : dataSnapshot.getChildren()){

                    UserReview reviews = new UserReview();

                    String username = usersdataSnapshot1.child("username").getValue().toString();
                    String postkey = usersdataSnapshot1.getKey();
                    String reviewmessage = usersdataSnapshot1.child("reviewmessage").getValue().toString();
                    String userimage = usersdataSnapshot1.child("userimage").getValue().toString();
                    Long rating = (Long)usersdataSnapshot1.child("rating").getValue();
                    Long time = (Long)usersdataSnapshot1.child("time").getValue();
                    negatedtime = (Long)usersdataSnapshot1.child("negatedtime").getValue();


                    reviews.setusername(username);
                    reviews.setreviewmessage(reviewmessage);
                    reviews.setuserimage(userimage);
                    reviews.settime(time);
                    reviews.setrating(Integer.valueOf(rating.intValue()));

                    count++;
                    if (firstkey) {
                        firstkey = false;
                    } else {

                        reviewlist.add(reviews);

                        if (count == loadlimit+1) {
                            reviewlist.add(null);
                            recyclerAdapter.notifyItemInserted(reviewlist.size() - 1);
                        }
                    }
                }


                if (count < loadlimit + 1) {
                    Log.d(TAG, "end loading");
                    recyclerAdapter.notifyDataSetChanged();
                    loading = false;
                }

                else {
                    Log.d(TAG, "cont loading");
                    recyclerAdapter.notifyDataSetChanged();
                    loading = true;
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
}
