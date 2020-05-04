package com.zjheng.jobseed.jobseed.TalentDetails;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.games.Player;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.zjheng.jobseed.jobseed.CustomUIClass.ClickableViewPager;
import com.zjheng.jobseed.jobseed.R;
import com.zjheng.jobseed.jobseed.TalentCategories.SubTalentCategoryRecyclerAdapter;
import com.zjheng.jobseed.jobseed.TalentCategories.TalentCategoryList;

import java.net.URL;
import java.util.ArrayList;

import static android.os.Build.VERSION_CODES.O;
//import static com.google.android.gms.internal.zzti.On;

/**
 * Created by zhen on 3/3/2018.
 */

public class TalentImages extends AppCompatActivity  {

    private Toolbar mToolbar;
    private TextView mtitletxt;
    private ImageButton mbackBtn;
    private String post_image;
    private ArrayList<String> postimagelist;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talentimages);

        mToolbar =  findViewById(R.id.toolbar);
        mbackBtn =  findViewById(R.id.backBtn);
        mtitletxt =  findViewById(R.id.titletxt);

        postimagelist = new ArrayList<String>();

        post_image = getIntent().getStringExtra("post_image");
        position = getIntent().getIntExtra("position", 0);

        if (post_image.contains(" , ")) {
            String[] separatedimages = post_image.split(" , ");
            for (int x = 0; x < separatedimages.length; x++) {
                postimagelist.add(separatedimages[x]);
            }
        } else {
            postimagelist.add(post_image);
        }

        mtitletxt.setText("Images (1 / " + postimagelist.size() + ")");

        ClickableViewPager mAdsViewPager = (ClickableViewPager) findViewById(R.id.adscontainer);
        final TalentImageViewPagerAdapter mviewPagerAdapter = new TalentImageViewPagerAdapter("true", postimagelist, TalentImages.this);
        mAdsViewPager.setAdapter(mviewPagerAdapter);

        mAdsViewPager.setCurrentItem(position);

        mAdsViewPager.setOnItemClickListener(new ClickableViewPager.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                if (postimagelist.get(position).contains("youtube")) {
                    Intent intent = new Intent(TalentImages.this, TalentVideos.class);
                    intent.putExtra("post_image",post_image);
                    startActivity(intent);
                    TalentImages.this.overridePendingTransition(R.anim.pullup,R.anim.nochange);
                }

            }
        });

        mAdsViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                mtitletxt.setText("Images (" + (position + 1) + " / " + postimagelist.size() + ")");

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mbackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

}
