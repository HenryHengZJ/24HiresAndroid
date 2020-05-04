package com.zjheng.jobseed.jobseed.TalentDetails;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.zjheng.jobseed.jobseed.R;

import java.util.ArrayList;

/**
 * Created by zhen on 11/4/2017.
 */

public class TalentImageViewPagerAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<String> imagelist;
    private static final String TAG = "BannerViewPagerAdapter";
    private String detailsimage;

    public TalentImageViewPagerAdapter(String detailsimage, ArrayList<String> imagelist, Context context)
    {
        this.context = context;
        this.imagelist = imagelist;
        this.detailsimage = detailsimage;

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

        View finalview = null;

        finalview = layoutInflater.inflate(R.layout.preview_talentimage, container, false);
        ImageView imageView = (ImageView) finalview.findViewById(R.id.iv_preview_image);
        ImageView mplayimage = (ImageView) finalview.findViewById(R.id.playimage);

        if (imagelist.get(position).contains("youtube")) {
            mplayimage.setVisibility(View.VISIBLE);
        }
        else {
            mplayimage.setVisibility(View.GONE);
        }

        if (detailsimage.equals("true")) {
            Glide.with(context)
                    .load(imagelist.get(position))
                    .thumbnail(0.5f)
                    .fitCenter()
                    .error(R.drawable.loadingerror3)
                    .placeholder(R.drawable.loading_spinner)
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);
        }
        else {
            Glide.with(context)
                    .load(imagelist.get(position))
                    .thumbnail(0.5f)
                    .centerCrop()
                    .error(R.drawable.loadingerror3)
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);
        }

        container.addView(finalview);

        return finalview;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }


}
