package com.zjheng.jobseed.jobseed.OtherUserScene;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.google.firebase.database.ValueEventListener;
import com.zjheng.jobseed.jobseed.CustomObjectClass.Job;
import com.zjheng.jobseed.jobseed.JobDetail;
import com.zjheng.jobseed.jobseed.R;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by zhen on 5/5/2017.
 */

public class OtherUserPosted extends Fragment {

    private RecyclerView mPostedList;
    private RelativeLayout mstartpostedLay;
    private LinearLayoutManager mLayoutManager;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserActivities , mJob, mUserPosted;

    private static final String TAG = "OtherUserPosted";

    private String userid;

    Activity context;
    View rootView;

    public static OtherUserPosted newInstance(String userid) {
        OtherUserPosted result = new OtherUserPosted();
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

        rootView = inflater.inflate(R.layout.userpostedtab, container, false);

        context = getActivity();

        mAuth = FirebaseAuth.getInstance();

        mUserActivities =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserActivities");

        mUserPosted =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserPosted");

        mJob =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("Job");

        mstartpostedLay = (RelativeLayout) rootView.findViewById(R.id.startpostedLay);
        mPostedList = (RecyclerView)rootView.findViewById(R.id.postedlist);
        mPostedList.setHasFixedSize(false);

        mLayoutManager = new LinearLayoutManager(context.getApplicationContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        mPostedList.setLayoutManager(mLayoutManager);

        FirebaseRecyclerAdapter<Job, BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Job, BlogViewHolder>(
                Job.class,
                R.layout.postedrow,
                BlogViewHolder.class,
                mUserPosted.child(userid)

        ) {

            @Override
            protected void populateViewHolder(final BlogViewHolder viewHolder, Job model, int position) {

                final String postkey = getRef(position).getKey();
                final String city = model.getCity();

                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setCompany(model.getCompany());
                viewHolder.setpostImage(context.getApplicationContext(), model.getpostImage());
                viewHolder.setJobStatus(model.getclosed());

                viewHolder.cardview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        final ProgressDialog mProgressDialog;
                        mProgressDialog = new ProgressDialog(context,R.style.MyTheme);
                        mProgressDialog.setCancelable(false);
                        mProgressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
                        mProgressDialog.show();

                        new Thread()
                        {

                            public void run()
                            {
                                Log.e(TAG, "pressed");
                                Intent jobdetailintent = new Intent(context, JobDetail.class);
                                jobdetailintent.putExtra("post_id",postkey);
                                jobdetailintent.putExtra("city_id",city);
                                startActivity(jobdetailintent);
                                mProgressDialog.dismiss();
                            }

                        }.start();

                    }
                });
            }
        };

        mPostedList.setAdapter(firebaseRecyclerAdapter);

        if(userid!=null) {

            mUserPosted.child(userid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        mstartpostedLay.setVisibility(GONE);
                    } else {
                        mstartpostedLay.setVisibility(VISIBLE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


        return rootView;
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder{

        View mView, mclosedview;
        ImageView post_image;
        RelativeLayout mRlayout;
        TextView post_desc,mclosedtext;
        CardView cardview;

        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            mRlayout = (RelativeLayout) mView.findViewById(R.id.Rlayout);
            post_image = (ImageView) mView.findViewById(R.id.postImage);
            cardview = (CardView) mView.findViewById(R.id.cardview);
            mclosedtext = (TextView) mView.findViewById(R.id.closedtext);
            mclosedview = (View)mView.findViewById(R.id.closedview);

        }

        public void setJobStatus(final String closed){
            if(closed!=null){
                if(closed.equals("true")) {
                    mclosedtext.setVisibility(VISIBLE);
                    mclosedview.setVisibility(VISIBLE);
                }
                else{
                    mclosedtext.setVisibility(GONE);
                    mclosedview.setVisibility(GONE);
                }
            }
        }

        public void setTitle(String title){
            if(title!=null) {
                TextView post_title = (TextView) mView.findViewById(R.id.postName);
                post_title.setText(title);
            }
        }
        public void setDesc(String desc){
            if(desc!=null) {
                post_desc = (TextView) mView.findViewById(R.id.postDescrip);
                post_desc.setText(desc);
            }
        }
        public void setCompany(String company){
            if(company!=null) {
                TextView post_company = (TextView) mView.findViewById(R.id.postCompany);
                post_company.setText(company);
            }
        }
        public void setpostImage(Context ctx, String postimage){
            if (postimage != null) {

                Glide.with(ctx).load(postimage)
                        .thumbnail(0.5f)
                        .fitCenter()
                        .error(R.drawable.error3)
                        .placeholder(R.drawable.loading_spinner)
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(post_image);

            }
        }
    }
}
