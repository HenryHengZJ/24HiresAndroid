package com.zjheng.jobseed.jobseed.TalentDetails;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.zjheng.jobseed.jobseed.CustomObjectClass.TalentInfo;
import com.zjheng.jobseed.jobseed.CustomObjectClass.UserReview;
import com.zjheng.jobseed.jobseed.R;
import com.zjheng.jobseed.jobseed.RemovedTalent;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.zjheng.jobseed.jobseed.R.id.cardview;
import static com.zjheng.jobseed.jobseed.R.id.ratingval;
import static com.zjheng.jobseed.jobseed.R.id.reviewcount;
import static com.zjheng.jobseed.jobseed.R.id.txtTime;
import static com.zjheng.jobseed.jobseed.R.id.txtuserComment;

/**
 * Created by zhen on 7/10/2017.
 */

public class ReviewRecyclerAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<UserReview> list;

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private static final String TAG = "ReviewRecyclerAdapter";
    private boolean isLoading;

    public ReviewRecyclerAdapter(List<UserReview> list, Context context){
        this.context = context;
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(context).inflate(R.layout.review_row, parent, false);
            return new MyReviewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position){

        if (holder instanceof MyReviewHolder) {

            final MyReviewHolder talentViewHolder = (MyReviewHolder) holder;
            // Get the current item from the data set
            UserReview mylist = list.get(position);

            final String username = mylist.getusername();
            final String reviewmessage = mylist.getreviewmessage();
            final String userimage = mylist.getuserimage();
            final Long time = mylist.gettime();
            final int rating = mylist.getrating();


            if (username != null && reviewmessage != null && userimage != null && time != null) {

                talentViewHolder.postName.setText(username);

                if (reviewmessage.equals("none")) {
                    talentViewHolder.txtuserComment.setVisibility(GONE);
                    talentViewHolder.txtuserComment.setText("");
                } else {
                    talentViewHolder.txtuserComment.setVisibility(VISIBLE);
                    talentViewHolder.txtuserComment.setText(reviewmessage);
                }

                talentViewHolder.muserratingbar.setRating(rating);

                Long tsLong = System.currentTimeMillis();
                CharSequence result = DateUtils.getRelativeTimeSpanString(time, tsLong, DateUtils.SECOND_IN_MILLIS);
                talentViewHolder.txtTime.setText(result);

                if (!userimage.equals("default")) {
                    Glide.with(context).load(userimage)
                            .thumbnail(0.5f)
                            .centerCrop()
                            .error(R.drawable.defaultprofile_pic)
                            .placeholder(R.drawable.loading_spinner)
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(talentViewHolder.userimagepic);
                }

            } else if (holder instanceof LoadingViewHolder) {
                LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
                loadingViewHolder.jobprogressBar.setIndeterminate(true);
            }
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

    private class MyReviewHolder extends RecyclerView.ViewHolder{

        View mView;
        CircleImageView userimagepic;
        TextView txtTime, txtuserComment, postName;
        RatingBar muserratingbar;

        public MyReviewHolder(View itemView){
            super(itemView);

            mView = itemView;

            userimagepic = (CircleImageView) mView.findViewById(R.id.userimage);
            txtTime = (TextView) mView.findViewById(R.id.txtTime);
            postName = (TextView) mView.findViewById(R.id.postName);
            txtuserComment = (TextView) mView.findViewById(R.id.txtuserComment);
            muserratingbar = (RatingBar) mView.findViewById(R.id.userratingbar);
          
        }
    }
}
