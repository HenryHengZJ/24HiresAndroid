package com.zjheng.jobseed.jobseed.HomeScene.DiscoverTalent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zjheng.jobseed.jobseed.HowItWorks.HowTalentWorks;
import com.zjheng.jobseed.jobseed.R;

/**
 * Created by zhen on 5/5/2017.
 */

public class ComingSoon extends Fragment {

    private ImageButton mhowBtn;

    View rootView;
    Activity context;
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.activity_talent_activities, container, false);

        context=getActivity();

        mhowBtn = (ImageButton) rootView.findViewById(R.id.howBtn);

        mhowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent howitworksintent = new Intent(context, HowTalentWorks.class);
                startActivity(howitworksintent);
            }
        });


        return rootView;
    }
}

