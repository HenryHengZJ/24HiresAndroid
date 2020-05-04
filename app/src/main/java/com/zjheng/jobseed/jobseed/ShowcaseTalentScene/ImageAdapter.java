package com.zjheng.jobseed.jobseed.ShowcaseTalentScene;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.squareup.picasso.Picasso;
import com.zjheng.jobseed.jobseed.AvailableLocationRecyclerAdapter;
import com.zjheng.jobseed.jobseed.JobDetail;
import com.zjheng.jobseed.jobseed.R;

import java.util.ArrayList;

import static com.zjheng.jobseed.jobseed.R.id.imageView;

/**
 * Created by zhen on 2/25/2018.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    Context context;
    ArrayList<Uri> imagePaths;
    private ItemClickListener mClickListener;
    private static final String TAG = "ImageAdapter";

    public ImageAdapter(Context context, ArrayList<Uri> imagePaths) {
        this.context = context;
        this.imagePaths = imagePaths;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.addtalentpic_row, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (imagePaths.size() != 0) {
            final Uri imagePath = imagePaths.get(position);

            Glide.with(context).load(imagePath)
                    .centerCrop()
                    .error(R.drawable.loadingerror3)
                    .placeholder(R.drawable.loading_spinner)
                    .dontAnimate()
                    .into(holder.mimageView);
        }

    }

    public void changePath(ArrayList<Uri> imagePaths) {

        if (imagePaths.size() != 0) {
            this.imagePaths = imagePaths;
            notifyDataSetChanged();
        }

    }

    @Override
    public int getItemCount() {
        int size;
        if (imagePaths.size() == 0) {
            size = 1;
        }
        else {
            size = imagePaths.size();
        }
        return size;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView mimageView;
        CardView maddpicCardView;

        public ViewHolder(View itemView) {
            super(itemView);
            mimageView = (ImageView) itemView.findViewById(R.id.imageView);
            maddpicCardView = (CardView) itemView.findViewById(R.id.addpicCardView);

            maddpicCardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}