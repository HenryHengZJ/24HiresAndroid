package com.zjheng.jobseed.jobseed;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zjheng.jobseed.jobseed.CustomObjectClass.Job;
import com.zjheng.jobseed.jobseed.CustomObjectClass.Rewards;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by zhen on 5/5/2017.
 */

public class RedemeedTab extends Fragment {

    private GridView mRedemeedList;
    private LinearLayoutManager mLayoutManager;

    private RelativeLayout mstartredeemdLay, mnoInternetLay;
    private List<Rewards> rewardslist;
    private RewardsAdapter adapter;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserRedeemed ;

    private CardView mretryBtn;

    private static final String TAG = "RedemeedTab";

    Activity context;
    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.rewards_redeemedtab, container, false);

        context = getActivity();

        Log.d(TAG, "Redemeed");

        mstartredeemdLay = (RelativeLayout) rootView.findViewById(R.id.startredeemedLay);
        mnoInternetLay = (RelativeLayout)rootView.findViewById(R.id.noInternetLay);
        mretryBtn = (CardView)rootView.findViewById(R.id.retryBtn);
        mRedemeedList = (GridView) rootView.findViewById(R.id.redeemedlist);

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                Log.d(TAG, "connected to wifi");
                //Connected
                mnoInternetLay.setVisibility(GONE);

                if(mstartredeemdLay.getVisibility() == View.GONE){
                    mRedemeedList.setVisibility(VISIBLE);
                }
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                Log.d(TAG, "connected to data");
                //Connected
                mnoInternetLay.setVisibility(GONE);

                if(mstartredeemdLay.getVisibility() == View.GONE){
                    mRedemeedList.setVisibility(VISIBLE);
                }
            }
        } else {
            //Disconnected
            mnoInternetLay.setVisibility(VISIBLE);

            if(mstartredeemdLay.getVisibility() == View.GONE){
                mRedemeedList.setVisibility(GONE);
            }

            mretryBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                    if (activeNetwork != null) { // connected to the internet
                        if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                            Log.d(TAG, "connected to wifi");
                            //Connected
                            mnoInternetLay.setVisibility(GONE);

                            if(mstartredeemdLay.getVisibility() == View.GONE){
                                mRedemeedList.setVisibility(VISIBLE);
                            }
                        } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                            Log.d(TAG, "connected to data");
                            //Connected
                            mnoInternetLay.setVisibility(GONE);

                            if(mstartredeemdLay.getVisibility() == View.GONE){
                                mRedemeedList.setVisibility(VISIBLE);
                            }
                        }
                    } else {
                        //Disconnected
                        mnoInternetLay.setVisibility(VISIBLE);

                        if(mstartredeemdLay.getVisibility() == View.GONE){
                            mRedemeedList.setVisibility(GONE);
                        }
                    }
                }
            });
        }

        mAuth = FirebaseAuth.getInstance();

        mUserRedeemed =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserRedeemed");

        rewardslist = new ArrayList<Rewards>();

        mRedemeedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

            }
        });

        mUserRedeemed.child(mAuth.getCurrentUser().getUid()).orderByChild("negatedtime").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot usersdataSnapshot1 : dataSnapshot.getChildren()){

                    Rewards rewards = new Rewards();

                    if (usersdataSnapshot1.hasChild("title")&& usersdataSnapshot1.hasChild("points")&& usersdataSnapshot1.hasChild("image")) {

                        String title = usersdataSnapshot1.child("title").getValue().toString();
                        String points = usersdataSnapshot1.child("points").getValue().toString();
                        String image = usersdataSnapshot1.child("image").getValue().toString();

                        rewards.settitle(title);
                        rewards.setpoints(points);
                        rewards.setimage(image);

                        rewardslist.add(rewards);

                    }
                }

                adapter = new RewardsAdapter(context,rewardslist);
                mRedemeedList.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

       /* mLayoutManager = new LinearLayoutManager(context.getApplicationContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        mSavedList.setLayoutManager(mLayoutManager);*/


        mUserRedeemed.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    mstartredeemdLay.setVisibility(GONE);
                }
                else{
                    mstartredeemdLay.setVisibility(VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return rootView;
    }
}

