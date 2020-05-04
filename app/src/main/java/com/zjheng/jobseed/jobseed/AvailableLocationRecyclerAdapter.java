package com.zjheng.jobseed.jobseed;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
import com.zjheng.jobseed.jobseed.CustomObjectClass.UserChat;
import com.zjheng.jobseed.jobseed.MessageScene.ChatRecyclerAdapter;

import java.util.Collections;
import java.util.List;

import static android.R.id.list;
import static com.zjheng.jobseed.jobseed.R.id.calendar_view;

/**
 * Created by zhen on 7/10/2017.
 */

public class AvailableLocationRecyclerAdapter extends RecyclerView.Adapter {

    private List<String> locationlist = Collections.emptyList();
    private List<String> savedlocationlist = Collections.emptyList();
    private List<String> selectedtravellocationlist  = Collections.emptyList();
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private String locationstatus;

    private final int VIEW_TYPE_NORMAL = 0;
    private final int VIEW_TYPE_ALLMALAYSIA = 1;

    private static final String TAG = "AvailableLocationRecyclerAdapter";

    public AvailableLocationRecyclerAdapter(List<String> savedlocationlist, String locationstatus,List<String> locationlist, Context context){
        this.mInflater = LayoutInflater.from(context);
        this.locationlist = locationlist;
        this.locationstatus = locationstatus;
        this.savedlocationlist = savedlocationlist;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == VIEW_TYPE_NORMAL) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.locationlist, parent, false);
            return new NormalViewHolder(view);
        }
        else if (viewType == VIEW_TYPE_ALLMALAYSIA) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.locationlist, parent, false);
            return new AllMalaysiaViewHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder,  final int position){

        if (holder instanceof NormalViewHolder) {

            NormalViewHolder normalViewHolder = (NormalViewHolder) holder;
            String locationval = locationlist.get(position);
            normalViewHolder.myTextView.setText(locationval);
        }
        else  if (holder instanceof AllMalaysiaViewHolder) {
            final AllMalaysiaViewHolder allmsiaViewHolder = (AllMalaysiaViewHolder) holder;
            final String locationval = locationlist.get(position);
            allmsiaViewHolder.myTextView.setText(locationval);

            if (!savedlocationlist.isEmpty()) {

               for (int x = 0; x< savedlocationlist.size();x++){
                   if (savedlocationlist.get(x).equals(locationval)) {
                       allmsiaViewHolder.mtickBtn.setVisibility(View.VISIBLE);
                       allmsiaViewHolder.myTextView.setTextColor(Color.parseColor("#1a8dfb"));
                   }
               }
            }
        }

    }

    @Override
    public int getItemCount(){
        // Count the items
        return locationlist.size();
    }

    @Override
    public int getItemViewType(int position) {

        int returnval = 0;

        if (locationstatus.equals("All")) {
            returnval = VIEW_TYPE_ALLMALAYSIA;
        }
        else {
            returnval = VIEW_TYPE_NORMAL;
        }

        return returnval;
    }

    private class NormalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView myTextView;
        CardView mcardview;

        public NormalViewHolder(View view) {
            super(view);
            myTextView = (TextView) view.findViewById(R.id.searchResult);
            mcardview = (CardView) view.findViewById(R.id.cardview);
            mcardview.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    private class AllMalaysiaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView myTextView;
        ImageButton mtickBtn;
        CardView mcardview;

        public AllMalaysiaViewHolder(View view) {
            super(view);
            myTextView = (TextView) view.findViewById(R.id.searchResult);
            mcardview = (CardView) view.findViewById(R.id.cardview);
            mtickBtn =  view.findViewById(R.id.tickBtn);
            mcardview.setOnClickListener(this);
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
