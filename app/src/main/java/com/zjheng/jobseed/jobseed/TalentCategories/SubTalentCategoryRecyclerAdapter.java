package com.zjheng.jobseed.jobseed.TalentCategories;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zjheng.jobseed.jobseed.R;

import java.util.Collections;
import java.util.List;

/**
 * Created by zhen on 7/10/2017.
 */

public class SubTalentCategoryRecyclerAdapter extends RecyclerView.Adapter <SubTalentCategoryRecyclerAdapter.ViewHolder> {

    private List<String> subcategorylist = Collections.emptyList();
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    private static final String TAG = "SubTalentCategor";

    public SubTalentCategoryRecyclerAdapter(List<String> subcategorylist, Context context){
        this.mInflater = LayoutInflater.from(context);
        this.subcategorylist = subcategorylist;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = mInflater.inflate(R.layout.locationlist, parent, false);
        return new ViewHolder(view);

        /*View view = LayoutInflater.from(context).inflate(R.layout.job_row, parent, false);
        MyHolder myHolder = new MyHolder(view);

        // Get the TextView reference from RecyclerView current item
        return myHolder;*/
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        String subcategoryval = subcategorylist.get(position);
        Log.d(TAG, "subcategoryval " + subcategoryval);
        holder.myTextView.setText(subcategoryval);
    }

    @Override
    public int getItemCount(){
        // Count the items
        return subcategorylist.size();
    }


    // "Loading item" ViewHolder
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView myTextView;
        CardView mcardview;

        public ViewHolder(View view) {
            super(view);
            myTextView = (TextView) view.findViewById(R.id.searchResult);
            mcardview = (CardView) view.findViewById(R.id.cardview);
            mcardview.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onSubCatClick(view, getAdapterPosition());
        }
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onSubCatClick(View view, int position);
    }


}
