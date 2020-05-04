package com.zjheng.jobseed.jobseed.SearchTalentScene;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.google.firebase.database.ValueEventListener;
import com.zjheng.jobseed.jobseed.CustomObjectClass.Job;
import com.zjheng.jobseed.jobseed.CustomObjectClass.TalentInfo;
import com.zjheng.jobseed.jobseed.R;
import com.zjheng.jobseed.jobseed.RemovedTalent;
import com.zjheng.jobseed.jobseed.TalentDetails.TalentDetail;

import java.util.List;

import static android.R.attr.category;
import static com.zjheng.jobseed.jobseed.R.drawable.reviewstar;
import static com.zjheng.jobseed.jobseed.R.drawable.wages;

/**
 * Created by zhen on 7/10/2017.
 */

public class SearchTalentRecyclerAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<TalentInfo> list;
    private DatabaseReference mTalent;
    private String maincategory,subcategory;

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private static final String TAG = "TalentRecyclerAdapter";
    private boolean isLoading;

    public SearchTalentRecyclerAdapter(DatabaseReference mTalent,String maincategory,String subcategory, List<TalentInfo> list, Context context){
        this.context = context;
        this.list = list;
        this.mTalent = mTalent;
        this.maincategory = maincategory;
        this.subcategory = subcategory;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(context).inflate(R.layout.talentsrow, parent, false);
            return new MyTalentHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;

        /*View view = LayoutInflater.from(context).inflate(R.layout.job_row, parent, false);
        MyHolder myHolder = new MyHolder(view);

        // Get the TextView reference from RecyclerView current item
        return myHolder;*/
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position){

        if (holder instanceof MyTalentHolder) {

            final MyTalentHolder talentViewHolder = (MyTalentHolder) holder;
            // Get the current item from the data set
            TalentInfo mylist = list.get(position);
            final String postkey = mylist.getpostkey();
            final String title = mylist.gettitle();
            final String city = mylist.getcity();
            final String postimage = mylist.getpostimage();
            final String rates = mylist.getrates();
            final String date = mylist.getdates();
            final String reviewcount = mylist.getstring_reviewcount();
            final String reviewstar = mylist.getstring_reviewstar();

            if(title!=null && reviewcount!=null && reviewstar!=null ) {

                talentViewHolder.post_title.setText(title);
                talentViewHolder.mreviewcount.setText("(" + reviewcount + ")");
                talentViewHolder.mreviewstar.setText(reviewstar);

                if(rates!=null){
                    talentViewHolder.post_rates.setText(rates);
                }
                else{
                    talentViewHolder.post_rates.setText("Negotiatable");
                }

                if(date!=null){

                    final String[] dates = date.split("\\(");
                    talentViewHolder.post_date1.setText(dates[0]);
                    talentViewHolder.post_date2.setText("("+dates[1]);
                }
                else{
                    talentViewHolder.post_date1.setText("No Specified Availability");
                    talentViewHolder.post_date2.setText("");
                }
            }

            if (postimage != null) {

                final String postimage0;

                if (postimage.contains(" , ")) {
                    String postimages[] = postimage.split(" , ");
                    postimage0 = postimages[0];
                } else {
                    postimage0 = postimage;
                }

                Glide.with(context).load(postimage0)
                        .thumbnail(0.5f)
                        .centerCrop()
                        .error(R.drawable.error1)
                        .placeholder(R.drawable.loading_spinner)
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(talentViewHolder.post_image);

            }

            talentViewHolder.cardview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mTalent.child(city).child(maincategory).child(subcategory).child(postkey).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                Intent detailintent = new Intent(context, TalentDetail.class);
                                detailintent.putExtra("post_id", postkey);
                                detailintent.putExtra("city_id", city);
                                detailintent.putExtra("maincategory", maincategory);
                                detailintent.putExtra("subcategory", subcategory);
                                context.startActivity(detailintent);
                            }
                            else{
                                Intent jobdetailintent = new Intent(context, RemovedTalent.class);
                                context.startActivity(jobdetailintent);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

            });
        }
        else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
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
    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar jobprogressBar;

        public LoadingViewHolder(View view) {
            super(view);
            jobprogressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
        }
    }

    private class MyTalentHolder extends RecyclerView.ViewHolder{

        View mView;
        ImageView post_image;
        RelativeLayout mRlayout;
        CardView cardview;
        TextView post_title;
        TextView mreviewcount;
        TextView mreviewstar;
        TextView post_rates;
        TextView post_date1;
        TextView post_date2;

        public MyTalentHolder(View itemView){
            super(itemView);

            mView = itemView;
            mRlayout = (RelativeLayout) mView.findViewById(R.id.Rlayout);
            post_image = (ImageView) mView.findViewById(R.id.postImage);
            cardview = (CardView) mView.findViewById(R.id.cardview);
            post_title= (TextView)mView.findViewById(R.id.postTitle);
            post_rates = (TextView)mView.findViewById(R.id.ratesval);
            post_date1 = (TextView)mView.findViewById(R.id.datesval1);
            post_date2 = (TextView)mView.findViewById(R.id.datesval2);
            mreviewstar = (TextView)mView.findViewById(R.id.reviewstar);
            mreviewcount = (TextView)mView.findViewById(R.id.reviewcount);
          
        }
    }
}
