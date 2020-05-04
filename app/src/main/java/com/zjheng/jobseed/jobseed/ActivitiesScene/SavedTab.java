package com.zjheng.jobseed.jobseed.ActivitiesScene;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.zjheng.jobseed.jobseed.RemovedJob;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by zhen on 5/5/2017.
 */

public class SavedTab extends Fragment {

    private RecyclerView mSavedList;
    private LinearLayoutManager mLayoutManager;

    private RelativeLayout mstartsavedLay, mnoInternetLay;
    private FirebaseRecyclerAdapter<Job, BlogViewHolder> firebaseRecyclerAdapter;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserActivities , mJob;

    private CardView mretryBtn;

    private static final String TAG = "SavedTab";

    Activity context;
    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.savedtab, container, false);

        context = getActivity();

        Log.d(TAG, "saved");

        mstartsavedLay = (RelativeLayout) rootView.findViewById(R.id.startsavedLay);
        mnoInternetLay = (RelativeLayout)rootView.findViewById(R.id.noInternetLay);
        mretryBtn = (CardView)rootView.findViewById(R.id.retryBtn);
        mSavedList = (RecyclerView)rootView.findViewById(R.id.savedlist);

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                Log.d(TAG, "connected to wifi");
                //Connected
                mnoInternetLay.setVisibility(GONE);

                if(mstartsavedLay.getVisibility() == View.GONE){
                    mSavedList.setVisibility(VISIBLE);
                }
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                Log.d(TAG, "connected to data");
                //Connected
                mnoInternetLay.setVisibility(GONE);

                if(mstartsavedLay.getVisibility() == View.GONE){
                    mSavedList.setVisibility(VISIBLE);
                }
            }
        } else {
            //Disconnected
            mnoInternetLay.setVisibility(VISIBLE);

            if(mstartsavedLay.getVisibility() == View.GONE){
                mSavedList.setVisibility(GONE);
            }

            mretryBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                    if (activeNetwork != null) { // connected to the internet
                        if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                            Log.d(TAG, "connected to wifi");
                            //Connected
                            mnoInternetLay.setVisibility(GONE);

                            if(mstartsavedLay.getVisibility() == View.GONE){
                                mSavedList.setVisibility(VISIBLE);
                            }
                        } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                            Log.d(TAG, "connected to data");
                            //Connected
                            mnoInternetLay.setVisibility(GONE);

                            if(mstartsavedLay.getVisibility() == View.GONE){
                                mSavedList.setVisibility(VISIBLE);
                            }
                        }
                    } else {
                        //Disconnected
                        mnoInternetLay.setVisibility(VISIBLE);

                        if(mstartsavedLay.getVisibility() == View.GONE){
                            mSavedList.setVisibility(GONE);
                        }
                    }
                }
            });
        }

        mAuth = FirebaseAuth.getInstance();

        mUserActivities =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserActivities");

        mJob =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("Job");

        mSavedList.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(context.getApplicationContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        mSavedList.setLayoutManager(mLayoutManager);


        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Job, BlogViewHolder>(
                Job.class,
                R.layout.activitiesrow,
                BlogViewHolder.class,
                mUserActivities.child(mAuth.getCurrentUser().getUid()).child("Saved").orderByChild("time")

        ) {

            @Override
            protected void populateViewHolder(BlogViewHolder viewHolder, Job model, final int position) {

                final String postkey = getRef(position).getKey();
                final String city = model.getCity();

                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setCompany(model.getCompany());
                viewHolder.setpostImage(context.getApplicationContext(), model.getpostImage());
                viewHolder.setPostStatus(mJob, city, postkey);

                viewHolder.mremoveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        // custom dialog
                        final Dialog dialog = new Dialog(context);
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

                        hirebtn.setText("DELETE");
                        hirebtn.setTextColor(Color.parseColor("#ff669900"));
                        mdialogtxt.setText("Are you sure you to delete this saved job post? ");

                        dialog.show();

                        hirebtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                mUserActivities.child(mAuth.getCurrentUser().getUid()).child("Saved").child(postkey).removeValue();

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

                                mJob.child(city).child(postkey).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists()){
                                            Intent jobdetailintent = new Intent(context, JobDetail.class);
                                            jobdetailintent.putExtra("post_id", postkey);
                                            jobdetailintent.putExtra("city_id", city);
                                            startActivity(jobdetailintent);
                                            mProgressDialog.dismiss();
                                        }
                                        else{
                                            Intent jobdetailintent = new Intent(context, RemovedJob.class);
                                            startActivity(jobdetailintent);
                                            mProgressDialog.dismiss();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }

                        }.start();

                    }
                });

            }
        };

        mSavedList.setAdapter(firebaseRecyclerAdapter);

        mUserActivities.child(mAuth.getCurrentUser().getUid()).child("Saved").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    mstartsavedLay.setVisibility(GONE);
                }
                else{
                    mstartsavedLay.setVisibility(VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return rootView;
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder{

        View mView, mclosedview;
        CircleImageView post_image;
        RelativeLayout mRlayout, mremovedRlay;
        TextView post_desc, mclosedtext;
        CardView cardview;
        ImageButton mremoveBtn;

        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            mRlayout = (RelativeLayout) mView.findViewById(R.id.Rlayout);
            mremovedRlay = (RelativeLayout) mView.findViewById(R.id.removedRlay);
            post_image = (CircleImageView) mView.findViewById(R.id.postImage);
            cardview = (CardView) mView.findViewById(R.id.cardview);
            mclosedtext = (TextView) mView.findViewById(R.id.closedtext);
            mclosedview = (View)mView.findViewById(R.id.closedview);
            mremoveBtn = (ImageButton)mView.findViewById(R.id.removeBtn);
            mremoveBtn.setVisibility(VISIBLE);
        }

        public void setTitle(String title){
            TextView post_title = (TextView)mView.findViewById(R.id.postName);
            post_title.setText(title);
        }
        public void setDesc(String desc){
            post_desc = (TextView)mView.findViewById(R.id.postDescrip);
            post_desc.setText(desc);
        }
        public void setCompany(String company){
            TextView post_company = (TextView)mView.findViewById(R.id.postCompany);
            post_company.setText(company);
        }
        public void setpostImage(Context ctx, String postimage){
            if (postimage != null) {

                Glide.with(ctx).load(postimage)
                        .thumbnail(0.5f)
                        .centerCrop()
                        .error(R.drawable.error3)
                        .placeholder(R.drawable.loading_spinner)
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(post_image);

            }
        }

        public void setPostStatus(DatabaseReference mJob, String city, final String postkey) {

            mclosedtext.setVisibility(GONE);
            mclosedview.setVisibility(GONE);

            if (city !=null && postkey != null){
                mJob.child(city).child(postkey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            mremovedRlay.setVisibility(GONE);
                            if (dataSnapshot.hasChild("closed")) {
                                String closedval = dataSnapshot.child("closed").getValue().toString();
                                if(closedval.equals("true")) {
                                    mremoveBtn.setVisibility(VISIBLE);
                                    mclosedtext.setVisibility(VISIBLE);
                                    mclosedview.setVisibility(VISIBLE);
                                }
                                else{
                                    mremoveBtn.setVisibility(GONE);
                                    mclosedtext.setVisibility(GONE);
                                    mclosedview.setVisibility(GONE);
                                }
                            }
                            else{
                                mremoveBtn.setVisibility(GONE);
                                mclosedtext.setVisibility(GONE);
                                mclosedview.setVisibility(GONE);
                            }
                        } else {
                            mremoveBtn.setVisibility(VISIBLE);
                            post_image.setImageResource(R.drawable.error3);
                            mremovedRlay.setVisibility(VISIBLE);
                            mclosedtext.setVisibility(GONE);
                            mclosedview.setVisibility(GONE);
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

