package com.zjheng.jobseed.jobseed.HomeScene;

import android.content.Context;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.zjheng.jobseed.jobseed.R;

import java.util.ArrayList;
import java.util.List;

import static android.R.id.list;

/**
 * Created by zhen on 11/4/2017.
 */

public class BannerViewPagerAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<String> imagelist;
    private static final String TAG = "BannerViewPagerAdapter";

    public BannerViewPagerAdapter(ArrayList<String> imagelist, Context context)
    {
        this.context = context;
        this.imagelist = imagelist;

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if(imagelist != null){
            return imagelist.size();
        }
        return 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {

        View view = layoutInflater.inflate(R.layout.banner_layout, container, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.bannerImgView);

        Glide.with(context)
                .load(imagelist.get(position))
                .centerCrop()
                .dontAnimate()
                .into(imageView);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }


}
