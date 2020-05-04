package com.zjheng.jobseed.jobseed;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
import com.zjheng.jobseed.jobseed.CustomObjectClass.Rewards;

import java.util.List;

/**
 * Created by zhen on 7/10/2017.
 */

public class RewardsAdapter extends BaseAdapter {

    private Context context;
    private List<Rewards> list;
    private DatabaseReference mJob;

    private static final String TAG = "RewardsAdapter";

    public RewardsAdapter(Context context, List<Rewards> list){
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount(){
        // Count the items
        return list == null ? 0 : list.size();
    }
    @Override
    public Object getItem(int position)
    {
        return position;
    }
    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup
            parent)
    {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View gridView;
        if (convertView == null)
        {
            gridView = new View(context);
            // get layout from mobile.xml
            gridView = inflater.inflate(R.layout.rewards_row, null);

            ImageView post_image = (ImageView) gridView.findViewById(R.id.postImage);
            CardView cardview = (CardView) gridView.findViewById(R.id.cardview);
            TextView post_title= (TextView)gridView.findViewById(R.id.posttitle);
            TextView post_points = (TextView)gridView.findViewById(R.id.postpoints);

            Rewards mylist = list.get(position);
            final String title = mylist.gettitle();
            final String points = mylist.getpoints();
            final String postimage = mylist.getimage();

            if(title !=null && points!=null ) {

                post_title.setText(title);
                post_points.setText(points);
            }

            if (postimage != null) {

                Glide.with(context).load(postimage)
                        .thumbnail(0.5f)
                        .centerCrop()
                        .error(R.drawable.error1)
                        .placeholder(R.drawable.loading_spinner)
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(post_image);

            }
        }
        else
        {
            gridView = (View) convertView;
        }
        return gridView;
    }



}
