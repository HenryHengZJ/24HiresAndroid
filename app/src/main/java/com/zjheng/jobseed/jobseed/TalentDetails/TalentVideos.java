package com.zjheng.jobseed.jobseed.TalentDetails;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.zjheng.jobseed.jobseed.CustomUIClass.ClickableViewPager;
import com.zjheng.jobseed.jobseed.R;

import java.util.ArrayList;

/**
 * Created by zhen on 3/3/2018.
 */

public class TalentVideos extends AppCompatActivity implements  YouTubePlayer.OnInitializedListener {

    private ImageButton mbackBtn;
    private String post_image, videoid = "";

    private YouTubePlayerFragment playerFragment;
    private YouTubePlayer mPlayer;
    private String YouTubeKey = "AIzaSyAuHZRT30kj63bROo7mN0otYr_Kdp0L9fM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_video);

        mbackBtn = (ImageButton) findViewById(R.id.backBtn);

        post_image = getIntent().getStringExtra("post_image");
        videoid = extractYoutubeId(post_image);

        playerFragment = (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.youtube_player_fragment);
        playerFragment.initialize(YouTubeKey, this);

        mbackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    public String extractYoutubeId(String url) {

        try {
           // String query = new URL(url).getQuery();
            String[] param = url.split("/vi/");
            Log.e("talentimg", "param[1] " +param[1]);
            String id = null;
            String[] param1 = param[1].split("/");
            Log.e("talentimg", "param1[0] " +param1[0]);
            id = param1[0];
            return id;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (playerFragment != null) {
            getFragmentManager().beginTransaction()
                    .remove(getFragmentManager().findFragmentById(R.id.youtube_player_fragment))
                    .commit();

        }
        if (mPlayer != null) {
            mPlayer.release();
        }
    }


    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player,
                                        boolean wasRestored) {
        mPlayer = player;

        //Enables automatic control of orientation
        mPlayer.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION);

        //Show full screen in landscape mode always
        mPlayer.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE);

        //System controls will appear automatically
        mPlayer.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_SYSTEM_UI);

        Log.e("talenimage", "videoid " + videoid);

        if (!wasRestored) {
            //mPlayer.cueVideo("0KhnAnbwNK4");
            mPlayer.loadVideo(videoid);
        }
        else
        {
            mPlayer.play();
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                        YouTubeInitializationResult errorReason) {
        mPlayer = null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.nochange, R.anim.pulldown);
    }
}
