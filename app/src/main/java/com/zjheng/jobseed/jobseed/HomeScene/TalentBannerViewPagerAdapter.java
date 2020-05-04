package com.zjheng.jobseed.jobseed.HomeScene;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zjheng.jobseed.jobseed.R;

import java.util.ArrayList;

/**
 * Created by zhen on 11/4/2017.
 */

public class TalentBannerViewPagerAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<String> imagelist;
    private ArrayList<String> titlelist;
    private ArrayList<String> descriplist;
    private static final String TAG = "BannerViewPagerAdapter";

    public TalentBannerViewPagerAdapter(ArrayList<String> titlelist, ArrayList<String> descriplist, ArrayList<String> imagelist, Context context)
    {
        this.context = context;
        this.imagelist = imagelist;
        this.titlelist = titlelist;
        this.descriplist = descriplist;

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

        View view = layoutInflater.inflate(R.layout.talentbanner_layout, container, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.bannerImgView);
        TextView mtitletxt = (TextView) view.findViewById(R.id.titletxt);
        TextView mdescriptxt = (TextView) view.findViewById(R.id.descriptxt);

        mtitletxt.setText(titlelist.get(position));

        mdescriptxt.setText(descriplist.get(position));

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
