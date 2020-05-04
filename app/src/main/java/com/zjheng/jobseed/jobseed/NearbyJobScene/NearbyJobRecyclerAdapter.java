package com.zjheng.jobseed.jobseed.NearbyJobScene;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zjheng.jobseed.jobseed.CustomObjectClass.Job;
import com.zjheng.jobseed.jobseed.JobDetail;
import com.zjheng.jobseed.jobseed.R;
import com.zjheng.jobseed.jobseed.RemovedJob;

import java.util.List;

/**
 * Created by zhen on 7/10/2017.
 */

public class NearbyJobRecyclerAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<Job> list;
    private DatabaseReference mJob;

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private static final String TAG = "NearbyJobRecyclerAdapter";
    private boolean isLoading;

    public NearbyJobRecyclerAdapter(DatabaseReference mJob, List<Job> list, Context context){
        this.context = context;
        this.list = list;
        this.mJob = mJob;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(context).inflate(R.layout.nearbyjobrow, parent, false);
            return new MyJobHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_loading, parent, false);
            return new LoadingJobViewHolder(view);
        }
        return null;

        /*View view = LayoutInflater.from(context).inflate(R.layout.job_row, parent, false);
        MyHolder myHolder = new MyHolder(view);

        // Get the TextView reference from RecyclerView current item
        return myHolder;*/
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position){

        if (holder instanceof MyJobHolder) {

            final MyJobHolder jobViewHolder = (MyJobHolder) holder;
            // Get the current item from the data set
            Job mylist = list.get(position);
            final String title = mylist.getTitle();
            final String fulladdress = mylist.getFulladdress();
            final String postimage = mylist.getpostImage();
            final String city = mylist.getCity();
            final int count = mylist.getcount();
            final String postkey = mylist.getpostkey();

            if(title!=null && fulladdress!=null ) {

                jobViewHolder.post_title.setText(title);
                jobViewHolder.post_location.setText(fulladdress);
                jobViewHolder.post_count.setText(String.valueOf(count));
            }

            if (postimage != null) {

                Glide.with(context).load(postimage)
                        .thumbnail(0.5f)
                        .centerCrop()
                        .error(R.drawable.error1)
                        .placeholder(R.drawable.loading_spinner)
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(jobViewHolder.post_image);

            }

            jobViewHolder.cardview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mJob.child(city).child(postkey).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                Intent jobdetailintent = new Intent(context, JobDetail.class);
                                jobdetailintent.putExtra("post_id", postkey);
                                jobdetailintent.putExtra("city_id", city);
                                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity)context , jobViewHolder.post_image, "jobimage");
                                context.startActivity(jobdetailintent, optionsCompat.toBundle());
                                //HomeProgress.dismiss();
                            }
                            else{
                                Intent jobdetailintent = new Intent(context, RemovedJob.class);
                                context.startActivity(jobdetailintent);
                                //HomeProgress.dismiss();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
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
        return list.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public int getItemCount(){
        // Count the items
        return list == null ? 0 : list.size();
    }

    public void setLoaded(boolean isLoading) {
        this.isLoading = isLoading;
        //isLoading = false;
    }

    // "Loading item" ViewHolder
    private class LoadingJobViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar jobprogressBar;

        public LoadingJobViewHolder(View view) {
            super(view);
            jobprogressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
        }
    }

    private class MyJobHolder extends RecyclerView.ViewHolder{

        View mView;
        ImageView post_image;
        RelativeLayout mRlayout;
        CardView cardview;
        TextView post_title;
        TextView post_count;
        TextView post_location;

        public MyJobHolder(View itemView){
            super(itemView);

            mView = itemView;
            mRlayout = (RelativeLayout) mView.findViewById(R.id.Rlayout);
            post_image = (ImageView) mView.findViewById(R.id.postImage);
            cardview = (CardView) mView.findViewById(R.id.cardview);
            post_title= (TextView)mView.findViewById(R.id.postName);
            post_location = (TextView)mView.findViewById(R.id.postAddress);
            post_count = (TextView)mView.findViewById(R.id.textView25);
        }
    }
}
