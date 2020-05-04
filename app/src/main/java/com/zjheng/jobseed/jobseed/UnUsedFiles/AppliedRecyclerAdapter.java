package com.zjheng.jobseed.jobseed.UnUsedFiles;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.zjheng.jobseed.jobseed.CustomObjectClass.AppliedClass;
import com.zjheng.jobseed.jobseed.JobDetail;
import com.zjheng.jobseed.jobseed.R;
import com.zjheng.jobseed.jobseed.RemovedJob;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.zjheng.jobseed.jobseed.R.id.cardview;
import static com.zjheng.jobseed.jobseed.R.id.postImage;

/**
 * Created by zhen on 7/10/2017.
 */

public class AppliedRecyclerAdapter extends RecyclerView.Adapter {

    private List<AppliedClass> appliedlist = Collections.emptyList();
    private DatabaseReference mUserActivities, mJob, mUserShortlistNotification;
    private String ownuserid;
    private Context context;

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private static final String TAG = "AppliedRecyclerAdapter";

    public AppliedRecyclerAdapter(String ownuserid, DatabaseReference mUserActivities, DatabaseReference mJob, DatabaseReference mUserShortlistNotification, List<AppliedClass> appliedlist, Context context){
        this.context = context;
        this.appliedlist = appliedlist;
        this.mUserActivities = mUserActivities;
        this.mJob = mJob;
        this.mUserShortlistNotification = mUserShortlistNotification;
        this.ownuserid = ownuserid;
        Log.d(TAG,"AppliedRecyclerAdapter 1");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(context).inflate(R.layout.activitiesrow, parent, false);
            return new AppliedRecyclerAdapter.MyJobHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_loading, parent, false);
            return new AppliedRecyclerAdapter.LoadingJobViewHolder(view);
        }
        return null;


        //View view = LayoutInflater.from(context).inflate(R.layout.activitiesrow, parent, false);
        //return new ViewHolder(view);

        /*View view = LayoutInflater.from(context).inflate(R.layout.job_row, parent, false);
        MyHolder myHolder = new MyHolder(view);

        // Get the TextView reference from RecyclerView current item
        return myHolder;*/
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position){

        if (holder instanceof MyJobHolder) {

            final MyJobHolder jobViewHolder = (MyJobHolder) holder;

            AppliedClass mylist = appliedlist.get(position);

            final String title = mylist.getTitle();
            final String descrip = mylist.getDesc();
            final String company = mylist.getCompany();
            final String postimage = mylist.getpostImage();
            final String pressedval = mylist.getpressed();
            final String closedval = mylist.getclosed();
            final String postkey = mylist.getpostkey();
            final String city = mylist.getCity();
            final String userid = mylist.getUid();
            final Long time = mylist.gettime();

            // Set title, desc, company, image
            if( title!=null && descrip !=null && company!=null && postimage!=null ) {
                jobViewHolder.mpost_title.setText(title);
                jobViewHolder.post_desc.setText(descrip);
                jobViewHolder.mpost_company.setText(company);

                if (postimage.equals("0")) {
                    Glide.with(context).load(R.drawable.job_bg1)
                            .thumbnail(0.5f)
                            .fitCenter()
                            .error(R.drawable.error3)
                            .placeholder(R.drawable.loading_spinner)
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(jobViewHolder.post_image);
                } else if (postimage.equals("1")) {
                    Glide.with(context).load(R.drawable.job_bg2)
                            .thumbnail(0.5f)
                            .fitCenter()
                            .error(R.drawable.error3)
                            .placeholder(R.drawable.loading_spinner)
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(jobViewHolder.post_image);
                } else if (postimage.equals("2")) {
                    Glide.with(context).load(R.drawable.job_bg3)
                            .thumbnail(0.5f)
                            .fitCenter()
                            .error(R.drawable.error3)
                            .placeholder(R.drawable.loading_spinner)
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(jobViewHolder.post_image);
                } else {
                    Glide.with(context).load(postimage)
                            .thumbnail(0.5f)
                            .fitCenter()
                            .error(R.drawable.error3)
                            .placeholder(R.drawable.loading_spinner)
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(jobViewHolder.post_image);
                }
            }

            //Set pressed red dot
            if( pressedval!=null ) {
                if (pressedval.equals("false")) {
                    jobViewHolder.mchatnotifiBadge.setVisibility(VISIBLE);
                    jobViewHolder.mchatnotifiBadge1.setVisibility(VISIBLE);
                }
                else{
                    jobViewHolder.mchatnotifiBadge.setVisibility(GONE);
                    jobViewHolder.mchatnotifiBadge1.setVisibility(GONE);
                }
            }



            jobViewHolder.mremoveBtn.setOnClickListener(new View.OnClickListener() {
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
                    mdialogtxt.setText("Delete this applied job post?");

                    dialog.show();

                    hirebtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            mUserActivities.child(ownuserid).child("Applied").child(postkey).removeValue();

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


            jobViewHolder.mcardview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //Disappear the red dot
                    jobViewHolder.mchatnotifiBadge.setVisibility(GONE);

                    mUserActivities.child(ownuserid).child("NewApplied").setValue("false");

                    //Retrieve notifications key stored inside mUserActivities/uid/ShortlistedNotification, and delete the keys at mUserShortListNotification, also delete itself  location later
                    mUserActivities.child(ownuserid).child("ShortListedNotification").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot userSnaphot : dataSnapshot.getChildren()) {
                                final String notificationkey = userSnaphot.getKey();
                                if(notificationkey!=null) {
                                    mUserShortlistNotification.child("ShortListed").child(notificationkey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            mUserActivities.child(ownuserid).child("ShortListedNotification").child(notificationkey).removeValue();
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                    new Thread()
                    {

                        public void run()
                        {
                            mJob.child(city).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChild(postkey)){
                                        mUserActivities.child(ownuserid).child("Applied").child(postkey).child("pressed").setValue("true");
                                        Intent jobdetailintent = new Intent(context, JobDetail.class);
                                        jobdetailintent.putExtra("post_id", postkey);
                                        jobdetailintent.putExtra("city_id", city);
                                        context.startActivity(jobdetailintent);
                                    }
                                    else{
                                        Intent jobdetailintent = new Intent(context, RemovedJob.class);
                                        context.startActivity(jobdetailintent);
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

        else if (holder instanceof LoadingJobViewHolder) {
            LoadingJobViewHolder loadingViewHolder = (LoadingJobViewHolder) holder;
            loadingViewHolder.jobprogressBar.setIndeterminate(true);
        }


    }

    @Override
    public int getItemViewType(int position) {

        Log.d(TAG,"AppliedRecyclerAdapter getItemViewType");

        int returnval = 0;

        AppliedClass mylist = appliedlist.get(position);

       // final String shortlistedval = mylist.getshortlisted();

       /* if (shortlistedval.equals("true")){
            returnval = 1;
        }
        else {
            returnval = 0;
        }*/

        return returnval;
    }

    @Override
    public int getItemCount(){
        // Count the items
        return appliedlist.size();
    }

    // "Loading item" ViewHolder
    private class LoadingJobViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar jobprogressBar;

        public LoadingJobViewHolder(View view) {
            super(view);
            jobprogressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
        }
    }


    // "Loading item" ViewHolder
    public class MyJobHolder extends RecyclerView.ViewHolder {

        CircleImageView post_image;
        RelativeLayout mRlayout, mwaitingRlay, mshortlistedRlay, mrejectedRlay, mremovedRlay, mprofileLay;
        TextView post_desc, mtimetxt, mclosedtext, mpost_title, mpost_company;
        ProgressBar mprogressbar;
        View mchatnotifiBadge,mchatnotifiBadge1, mclosedview;
        CountDownTimer mCountDownTimer;
        ImageButton mremoveBtn;
        CardView mcardview;
        View mView;

        public MyJobHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mRlayout = (RelativeLayout) mView.findViewById(R.id.Rlayout);
            mprofileLay= (RelativeLayout) mView.findViewById(R.id.profileLay);
            mremovedRlay = (RelativeLayout) mView.findViewById(R.id.removedRlay);
            mwaitingRlay = (RelativeLayout) mView.findViewById(R.id.waitingRlay);
            mshortlistedRlay = (RelativeLayout) mView.findViewById(R.id.shortlistedRlay);
            mrejectedRlay = (RelativeLayout) mView.findViewById(R.id.rejectedRlay);

            post_image = (CircleImageView) mView.findViewById(postImage);

            mcardview = (CardView) mView.findViewById(cardview);

            mprogressbar = (ProgressBar)mView.findViewById(R.id.progressbar);

            mtimetxt = (TextView) mView.findViewById(R.id.timetxt);
            mpost_title = (TextView) mView.findViewById(R.id.postName);
            mpost_company = (TextView) mView.findViewById(R.id.postCompany);
            post_desc = (TextView) mView.findViewById(R.id.postDescrip);
            mclosedtext = (TextView) mView.findViewById(R.id.closedtext);

            mchatnotifiBadge = (View)mView.findViewById(R.id.chatnotifiBadge);
            mchatnotifiBadge1 = (View)mView.findViewById(R.id.chatnotifiBadge1);
            mclosedview = (View)mView.findViewById(R.id.closedview);

            mremoveBtn = (ImageButton)mView.findViewById(R.id.removeBtn);


        }


    }


}
