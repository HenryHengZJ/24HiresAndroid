package com.zjheng.jobseed.jobseed.MessageScene;

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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.zjheng.jobseed.jobseed.JobDetail;
import com.zjheng.jobseed.jobseed.R;
import com.zjheng.jobseed.jobseed.CustomObjectClass.UserChat;
import com.zjheng.jobseed.jobseed.RemovedJob;
import com.zjheng.jobseed.jobseed.RemovedTalent;
import com.zjheng.jobseed.jobseed.TalentDetails.TalentDetail;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.R.id.message;

/**
 * Created by zhen on 7/10/2017.
 */

public class ChatRecyclerAdapter extends RecyclerView.Adapter {

    private List<UserChat> list;
    private String receiver_image, userimage, ownuserid;
    private Context context;

    private DatabaseReference mUserInfo, mUserAccount, mJob, mTalent;

    private static final String TAG = "ChatRecyclerAdapter";

    private final int VIEW_TYPE_LOADING = 0;
    private final int VIEW_TYPE_ITEM_OWNER = 1;
    private final int VIEW_TYPE_ITEM_OTHERUSER = 2;
    private final int VIEW_TYPE_ACTION_OWNER = 3;
    private final int VIEW_TYPE_ACTION_OTHERUSER = 4;

    private int lastVisibleItem, totalItemCount;
    private int visibleThreshold = 5;
    private boolean isLoading;

    public ChatRecyclerAdapter(String ownuserid, DatabaseReference mTalent, DatabaseReference mJob, DatabaseReference mUserInfo,DatabaseReference mUserAccount, List<UserChat> list, String receiver_image, String userimage, Context context){

        this.list = list;
        this.context = context;
        this.receiver_image = receiver_image;
        this.userimage = userimage;
        this.mUserInfo = mUserInfo;
        this.mUserAccount = mUserAccount;
        this.ownuserid = ownuserid;
        this.mJob = mJob;
        this.mTalent = mTalent;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == VIEW_TYPE_ITEM_OWNER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ownuser_chatroomrow, parent, false);
            return new OwnMyHolder(view);
        }
        else if (viewType == VIEW_TYPE_ITEM_OTHERUSER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.otheruser_chatroomrow, parent, false);
            return new OtherMyHolder(view);
        }
        else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
        else if (viewType == VIEW_TYPE_ACTION_OWNER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ownuser_chatroom_actionrow, parent, false);
            return new OwnActionMyHolder(view);
        }
        else if (viewType == VIEW_TYPE_ACTION_OTHERUSER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.otheruser_chatroom_actionrow, parent, false);
            return new OtherActionMyHolder(view);
        }
        return null;

        //View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ownuser_chatroomrow, parent, false);
        //MyHolder myHolder = new MyHolder(view);

        // Get the TextView reference from RecyclerView current item
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position){

        if (holder instanceof OwnMyHolder) {

            OwnMyHolder ownuserViewHolder = (OwnMyHolder) holder;
            // Get the current item from the data set
            UserChat mylist = list.get(position);

            final String message = mylist.getmessage();
            final Long time = mylist.gettime();
            final Long oldtime = mylist.getoldtime();

            if(message !=null && time!=null && receiver_image!=null ) {

                Date d = new Date(time );
                SimpleDateFormat fmt = new SimpleDateFormat("HH:mm");

                ownuserViewHolder.mownusertxt.setText(message);
                ownuserViewHolder.mownusertime.setText(fmt.format(time));

                if (userimage != null) {
                    if (userimage.equals("default")) {
                        ownuserViewHolder.mownuserimg.setImageResource(R.drawable.defaultprofile_pic);
                    }
                    else{
                        Glide.with(context).load(userimage)
                                .thumbnail(0.5f)
                                .centerCrop()
                                .error(R.drawable.defaultprofile_pic)
                                .placeholder(R.drawable.loading_spinner)
                                .dontAnimate()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(ownuserViewHolder.mownuserimg);
                    }
                }
                else {
                    ownuserViewHolder.mownuserimg.setImageResource(R.drawable.defaultprofile_pic);
                }

            }

            if(time!=null && oldtime !=null) {

                Date d = new Date(time );
                Date oldd = new Date(oldtime );

                SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");

                if(fmt.format(d).equals(fmt.format(oldd))){
                    ownuserViewHolder.post_time.setVisibility(View.GONE);
                }
                else{
                    ownuserViewHolder.post_time.setVisibility(View.VISIBLE);
                    SimpleDateFormat fmtnew = new SimpleDateFormat("dd MMM HH:mm");
                    ownuserViewHolder.post_time.setText(fmtnew.format(time));
                }
            }

        }

        else if (holder instanceof OwnActionMyHolder) {

            OwnActionMyHolder ownactionViewHolder = (OwnActionMyHolder) holder;
            // Get the current item from the data set
            UserChat mylist = list.get(position);

            final String actiontitle = mylist.getactiontitle();
            final String jobtitle = mylist.getjobtitle();
            final String jobdescrip = mylist.getjobdescrip();
            final String city = mylist.getcity();
            final String postkey = mylist.getpostkey();
            final Long time = mylist.gettime();
            final Long oldtime = mylist.getoldtime();
            final String maincategory = mylist.getmaincategory();
            final String subcategory = mylist.getsubcategory();

            if( actiontitle !=null && jobtitle!=null && jobdescrip!=null && city!=null && postkey!=null && time!=null && receiver_image!=null ) {

                Date d = new Date(time);
                SimpleDateFormat fmt = new SimpleDateFormat("HH:mm");

                if (actiontitle.equals("applied")) {
                    ownactionViewHolder.mtitleLay.setBackgroundResource(R.drawable.applied_rounded_corner1);
                }
                else if (actiontitle.equals("shortlisted")) {
                    ownactionViewHolder.mtitleLay.setBackgroundResource(R.drawable.shortlisted_rounded_corner1);
                }
                else if (actiontitle.equals("hired")) {
                    ownactionViewHolder.mtitleLay.setBackgroundResource(R.drawable.hired_rounded_corner1);
                }
                else if (actiontitle.equals("rejected")) {
                    ownactionViewHolder.mtitleLay.setBackgroundResource(R.drawable.rejected_rounded_corner1);
                }
                else if (actiontitle.equals("booked talent")) {
                    ownactionViewHolder.mtitleLay.setBackgroundResource(R.drawable.pendingbooking_rounded_corner1);
                }
                else if (actiontitle.equals("booking accepted")) {
                    ownactionViewHolder.mtitleLay.setBackgroundResource(R.drawable.hired_rounded_corner1);
                }
                else if (actiontitle.equals("booking rejected")) {
                    ownactionViewHolder.mtitleLay.setBackgroundResource(R.drawable.rejected_rounded_corner1);
                }

                ownactionViewHolder.mactiontxt.setText(actiontitle);
                ownactionViewHolder.mtitletxt.setText(jobtitle);
                ownactionViewHolder.mdescriptxt.setText(jobdescrip);
                ownactionViewHolder.mownusertime.setText(fmt.format(time));

                if (userimage != null) {
                    if (userimage.equals("default")) {
                        ownactionViewHolder.mownuserimg.setImageResource(R.drawable.defaultprofile_pic);
                    }
                    else{
                        Glide.with(context).load(userimage)
                                .thumbnail(0.5f)
                                .centerCrop()
                                .error(R.drawable.defaultprofile_pic)
                                .placeholder(R.drawable.loading_spinner)
                                .dontAnimate()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(ownactionViewHolder.mownuserimg);
                    }
                }
                else {
                    ownactionViewHolder.mownuserimg.setImageResource(R.drawable.defaultprofile_pic);
                }

                ownactionViewHolder.mopenJobCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (maincategory == null || subcategory == null) {
                            mJob.child(city).child(postkey).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
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
                        else {
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

                    }
                });

            }

            if(time!=null && oldtime !=null) {

                Date d = new Date(time );
                Date oldd = new Date(oldtime );

                SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");

                if(fmt.format(d).equals(fmt.format(oldd))){
                    ownactionViewHolder.post_time.setVisibility(View.GONE);
                }
                else{
                    ownactionViewHolder.post_time.setVisibility(View.VISIBLE);
                    SimpleDateFormat fmtnew = new SimpleDateFormat("dd MMM HH:mm");
                    ownactionViewHolder.post_time.setText(fmtnew.format(time));
                }
            }
        }

        else if (holder instanceof OtherMyHolder) {

            OtherMyHolder otheruserViewHolder = (OtherMyHolder) holder;
            // Get the current item from the data set
            UserChat mylist = list.get(position);

            final String message = mylist.getmessage();
            final Long time = mylist.gettime();
            final Long oldtime = mylist.getoldtime();

            if( message !=null && time!=null && receiver_image!=null ) {

                Date d = new Date(time);
                SimpleDateFormat fmt = new SimpleDateFormat("HH:mm");

                otheruserViewHolder.motherusertxt.setText(message);
                otheruserViewHolder.motherusertime.setText(fmt.format(time));

                if (receiver_image.equals("default")) {
                    otheruserViewHolder.motheruserimg.setImageResource(R.drawable.defaultprofile_pic);
                }
                else{
                    Glide.with(context).load(receiver_image)
                            .thumbnail(0.5f)
                            .centerCrop()
                            .error(R.drawable.defaultprofile_pic)
                            .placeholder(R.drawable.loading_spinner)
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(otheruserViewHolder.motheruserimg);
                }

            }

            if(time!=null && oldtime !=null) {

                Date d = new Date(time );
                Date oldd = new Date(oldtime );

                SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");

                if(fmt.format(d).equals(fmt.format(oldd))){
                    otheruserViewHolder.post_time.setVisibility(View.GONE);
                }
                else{
                    otheruserViewHolder.post_time.setVisibility(View.VISIBLE);
                    SimpleDateFormat fmtnew = new SimpleDateFormat("dd MMM HH:mm");
                    otheruserViewHolder.post_time.setText(fmtnew.format(time));
                }
            }
        }

        else if (holder instanceof OtherActionMyHolder) {

            OtherActionMyHolder otheractionViewHolder = (OtherActionMyHolder) holder;
            // Get the current item from the data set
            UserChat mylist = list.get(position);

            final String actiontitle = mylist.getactiontitle();
            final String jobtitle = mylist.getjobtitle();
            final String jobdescrip = mylist.getjobdescrip();
            final String city = mylist.getcity();
            final String postkey = mylist.getpostkey();
            final Long time = mylist.gettime();
            final Long oldtime = mylist.getoldtime();
            final String maincategory = mylist.getmaincategory();
            final String subcategory = mylist.getsubcategory();


            if( actiontitle !=null && jobtitle!=null && jobdescrip!=null && city!=null && postkey!=null && time!=null && receiver_image!=null ) {

                Date d = new Date(time);
                SimpleDateFormat fmt = new SimpleDateFormat("HH:mm");

                if (actiontitle.equals("applied")) {
                    otheractionViewHolder.mtitleLay.setBackgroundResource(R.drawable.applied_rounded_corner1);
                }
                else if (actiontitle.equals("shortlisted")) {
                    otheractionViewHolder.mtitleLay.setBackgroundResource(R.drawable.shortlisted_rounded_corner1);
                }
                else if (actiontitle.equals("hired")) {
                    otheractionViewHolder.mtitleLay.setBackgroundResource(R.drawable.hired_rounded_corner1);
                }
                else if (actiontitle.equals("rejected")) {
                    otheractionViewHolder.mtitleLay.setBackgroundResource(R.drawable.rejected_rounded_corner1);
                }
                else if (actiontitle.equals("booked talent")) {
                    otheractionViewHolder.mtitleLay.setBackgroundResource(R.drawable.pendingbooking_rounded_corner1);
                }
                else if (actiontitle.equals("booked talent")) {
                    otheractionViewHolder.mtitleLay.setBackgroundResource(R.drawable.pendingbooking_rounded_corner1);
                }
                else if (actiontitle.equals("booking accepted")) {
                    otheractionViewHolder.mtitleLay.setBackgroundResource(R.drawable.hired_rounded_corner1);
                }
                else if (actiontitle.equals("booking rejected")) {
                    otheractionViewHolder.mtitleLay.setBackgroundResource(R.drawable.rejected_rounded_corner1);
                }
                otheractionViewHolder.mactiontxt.setText(actiontitle);
                otheractionViewHolder.mtitletxt.setText(jobtitle);
                otheractionViewHolder.mdescriptxt.setText(jobdescrip);
                otheractionViewHolder.motherusertime.setText(fmt.format(time));

                if (receiver_image.equals("default")) {
                    otheractionViewHolder.motheruserimg.setImageResource(R.drawable.defaultprofile_pic);
                }
                else{
                    Glide.with(context).load(receiver_image)
                            .thumbnail(0.5f)
                            .centerCrop()
                            .error(R.drawable.defaultprofile_pic)
                            .placeholder(R.drawable.loading_spinner)
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(otheractionViewHolder.motheruserimg);
                }

                otheractionViewHolder.mopenJobCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (maincategory == null || subcategory == null) {
                            mJob.child(city).child(postkey).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
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
                        else {
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

                    }
                });

            }

            if(time!=null && oldtime !=null) {

                Date d = new Date(time );
                Date oldd = new Date(oldtime );

                SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");

                if(fmt.format(d).equals(fmt.format(oldd))){
                    otheractionViewHolder.post_time.setVisibility(View.GONE);
                }
                else{
                    otheractionViewHolder.post_time.setVisibility(View.VISIBLE);
                    SimpleDateFormat fmtnew = new SimpleDateFormat("dd MMM HH:mm");
                    otheractionViewHolder.post_time.setText(fmtnew.format(time));
                }
            }
        }

        else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }


    }

    @Override
    public int getItemViewType(int position) {

        int returnval = 0;
        UserChat mylist = list.get(position);

        if (list.get(position) == null) {
            returnval = VIEW_TYPE_LOADING;
        }
        else {
            String owneruid = mylist.getownerid();
            String actiontitle = mylist.getactiontitle();
            if (owneruid.equals(ownuserid)) {
                if (actiontitle != null) {
                    returnval = VIEW_TYPE_ACTION_OWNER;
                }
                else {
                    returnval = VIEW_TYPE_ITEM_OWNER;
                }
            }
            else {
                if (actiontitle != null) {
                    returnval = VIEW_TYPE_ACTION_OTHERUSER;
                }
                else {
                    returnval = VIEW_TYPE_ITEM_OTHERUSER;
                }
            }

        }

        return returnval;
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public void setLoaded(boolean isLoading) {
        this.isLoading = isLoading;
        //isLoading = false;
    }

    private class OwnActionMyHolder extends RecyclerView.ViewHolder{

        View mView;
        TextView mactiontxt, mownusertime, mtitletxt, mdescriptxt;
        RelativeLayout mownusertxtLay, mtitleLay;
        CircleImageView mownuserimg;
        CardView mopenJobCardView;

        TextView post_time;

        public OwnActionMyHolder(View itemView){
            super(itemView);

            mView = itemView;

            mactiontxt = (TextView)mView.findViewById(R.id.actiontxt);
            mownusertime = (TextView)mView.findViewById(R.id.ownusertime);
            post_time = (TextView) mView.findViewById(R.id.postTime);
            mtitletxt = (TextView)mView.findViewById(R.id.titletxt);
            mdescriptxt = (TextView)mView.findViewById(R.id.descriptxt);

            mownusertxtLay = (RelativeLayout) mView.findViewById(R.id.ownusertxtLay);
            mtitleLay = (RelativeLayout) mView.findViewById(R.id.titleLay);

            mownuserimg = (CircleImageView) mView.findViewById(R.id.ownuserimg);

            mopenJobCardView = (CardView) mView.findViewById(R.id.openJobCardView);
        }
    }

    private class OtherActionMyHolder extends RecyclerView.ViewHolder{

        View mView;
        TextView mactiontxt, motherusertime, mtitletxt, mdescriptxt;
        RelativeLayout motherusertxtLay, mtitleLay;
        CircleImageView motheruserimg;
        CardView mopenJobCardView;

        TextView post_time;

        public OtherActionMyHolder(View itemView){
            super(itemView);

            mView = itemView;

            mactiontxt = (TextView)mView.findViewById(R.id.actiontxt);
            motherusertime = (TextView)mView.findViewById(R.id.otherusertime);
            post_time = (TextView) mView.findViewById(R.id.postTime);
            mtitletxt = (TextView)mView.findViewById(R.id.titletxt);
            mdescriptxt = (TextView)mView.findViewById(R.id.descriptxt);

            motherusertxtLay = (RelativeLayout) mView.findViewById(R.id.otherusertxtLay);
            mtitleLay = (RelativeLayout) mView.findViewById(R.id.titleLay);

            motheruserimg = (CircleImageView) mView.findViewById(R.id.otheruserimg);

            mopenJobCardView = (CardView) mView.findViewById(R.id.openJobCardView);
        }
    }

    private class OtherMyHolder extends RecyclerView.ViewHolder{

        View mView;
        TextView motherusertxt, motherusertime;
        RelativeLayout motherusertxtLay;
        CircleImageView motheruserimg;

        TextView post_time;

        public OtherMyHolder(View itemView){
            super(itemView);

            mView = itemView;

            motherusertxt = (TextView)mView.findViewById(R.id.otherusertxt);
            motherusertime = (TextView)mView.findViewById(R.id.otherusertime);
            post_time = (TextView) mView.findViewById(R.id.postTime);
            motherusertxtLay = (RelativeLayout) mView.findViewById(R.id.otherusertxtLay);
            motheruserimg = (CircleImageView) mView.findViewById(R.id.otheruserimg);
        }
    }

    private class OwnMyHolder extends RecyclerView.ViewHolder{

        View mView;
        TextView mownusertxt, mownusertime;
        RelativeLayout mownusertxtLay;
        CircleImageView mownuserimg;

        TextView post_time;

        public OwnMyHolder(View itemView){
            super(itemView);

            mView = itemView;

            mownusertxt = (TextView)mView.findViewById(R.id.ownusertxt);
            mownusertime = (TextView)mView.findViewById(R.id.ownusertime);
            post_time = (TextView) mView.findViewById(R.id.postTime);
            mownusertxtLay = (RelativeLayout) mView.findViewById(R.id.ownusertxtLay);
            mownuserimg = (CircleImageView) mView.findViewById(R.id.ownuserimg);
        }
    }

    // "Loading item" ViewHolder
    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
        }
    }

}
