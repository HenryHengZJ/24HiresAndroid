package com.zjheng.jobseed.jobseed.UnUsedFiles;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.zjheng.jobseed.jobseed.CustomObjectClass.Job;
import com.zjheng.jobseed.jobseed.R;

import java.util.List;

/**
 * Created by zhen on 8/15/2017.
 */

public class SwipeStackAdapter extends ArrayAdapter {

    private Context context;
    private List<Job> list;
    private int resource;
    private LayoutInflater inflater;

    private static final String TAG = "ArrayAdapter";

    public SwipeStackAdapter(List<Job> list, Context context) {
        super(context, R.layout.swiperow, list);
        this.list = list;
        this.context = context;
        this.resource = R.layout.swiperow;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Job getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        Log.d(TAG, "position " + position);

        convertView = (LinearLayout) inflater.inflate(resource, null);

        Job item = (Job) getItem(position);

        //TextView textViewCard = (TextView) convertView.findViewById(R.id.textViewCard);
        //textViewCard.setText(mData.get(position));
        //RelativeLayout mRlayout = (RelativeLayout) convertView.findViewById(R.id.Rlayout);

        if (item != null){
            TextView post_title = (TextView) convertView.findViewById(R.id.postName);
            post_title.setText(item.getTitle());
            TextView post_desc = (TextView) convertView.findViewById(R.id.postDescrip);
            post_desc.setText(item.getDesc());
            TextView post_company = (TextView) convertView.findViewById(R.id.postCompany);
            post_company.setText(item.getCompany());
            ImageView post_image = (ImageView) convertView.findViewById(R.id.postImage);
            final String postimage = item.getpostImage();
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

        return convertView;
    }
}