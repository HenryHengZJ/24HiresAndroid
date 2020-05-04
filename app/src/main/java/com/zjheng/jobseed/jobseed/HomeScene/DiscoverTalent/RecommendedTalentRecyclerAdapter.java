package com.zjheng.jobseed.jobseed.HomeScene.DiscoverTalent;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.zjheng.jobseed.jobseed.CustomObjectClass.TalentInfo;
import com.zjheng.jobseed.jobseed.R;
import com.zjheng.jobseed.jobseed.RemovedTalent;
import com.zjheng.jobseed.jobseed.TalentDetails.TalentDetail;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by zhen on 7/10/2017.
 */

public class RecommendedTalentRecyclerAdapter extends RecyclerView.Adapter <RecommendedTalentRecyclerAdapter.ViewHolder> {

    //private List<String> locationlist = Collections.emptyList();
    private LayoutInflater mInflater;
    private List<TalentInfo> list;
    private Context context;
    private DatabaseReference mTalent;

    private static final String TAG = "RecommendedAdapter";

    public RecommendedTalentRecyclerAdapter( DatabaseReference mTalent,List<TalentInfo> list, Context context){
        this.mInflater = LayoutInflater.from(context);
        this.list = list;
        this.context = context;
        this.mTalent = mTalent;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = mInflater.inflate(R.layout.recommended_talent_row, parent, false);
        return new ViewHolder(view);

        /*View view = LayoutInflater.from(context).inflate(R.layout.job_row, parent, false);
        MyHolder myHolder = new MyHolder(view);

        // Get the TextView reference from RecyclerView current item
        return myHolder;*/
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){

        TalentInfo mylist = list.get(position);
        final String postkey = mylist.getpostkey();
        final String city = mylist.getcity();
        final String category = mylist.getcategory();
        final String userName = mylist.getname();
        final String descrip = mylist.getdesc();
        final String ratestar = mylist.getstring_reviewstar();
        final String ratenum = mylist.getstring_reviewcount();
        final String userimage = mylist.getimage();
        final String postimage = mylist.getpostimage();

        String categorys[] = category.split(" / ");
        final String maincategory = categorys[0];
        final String subcategory = categorys[1];

        holder.muserName.setText(userName);
        holder.mpostDescrip.setText(descrip);
        holder.mratingval.setText(ratestar);
        holder.mratingnum.setText(ratenum);

        final String price = mylist.getrates().replace("per", "/");
        holder.mpriceval.setText(price);

        Glide.with(context).load(postimage)
                .thumbnail(0.5f)
                .centerCrop()
                .error(R.drawable.profilebg3)
                .placeholder(R.drawable.loading_spinner)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.mpostImage);

        if (userimage.equals("default")) {
            holder.mprofilepic.setImageResource(R.drawable.defaultprofile_pic);
        }
        else{
            Glide.with(context).load(userimage)
                    .thumbnail(0.5f)
                    .centerCrop()
                    .error(R.drawable.defaultprofile_pic)
                    .placeholder(R.drawable.loading_spinner)
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.mprofilepic);
        }

        holder.mcardview.setOnClickListener(new View.OnClickListener() {
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

    @Override
    public int getItemCount(){
        // Count the items
        return list.size();
    }


    // "Loading item" ViewHolder
    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView mcardview;
        TextView muserName, mpostDescrip, mratingval, mratingnum, mpriceval;
        ImageView mpostImage;
        CircleImageView mprofilepic;

        public ViewHolder(View view) {
            super(view);

            mcardview = (CardView) view.findViewById(R.id.cardview);
            muserName = (TextView) view.findViewById(R.id.userName);
            mpostDescrip = (TextView) view.findViewById(R.id.postDescrip);
            mratingval = (TextView) view.findViewById(R.id.ratingval);
            mratingnum = (TextView) view.findViewById(R.id.ratingnum);
            mpriceval = (TextView) view.findViewById(R.id.priceval);
            mpostImage = (ImageView) view.findViewById(R.id.postImage);
            mprofilepic = (CircleImageView) view.findViewById(R.id.profilepic);
        }


    }
}
