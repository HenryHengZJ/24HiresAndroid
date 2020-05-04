package com.zjheng.jobseed.jobseed.ActivitiesScene.EditPostScene;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.MenuItem;
import android.view.View;

import com.zjheng.jobseed.jobseed.R;

public class EditCategorySelect extends AppCompatActivity {

    private CardView mBaristaBtn, mBeautyBtn, mChefBtn, mEventBtn, mEmceeBtn;
    private CardView mEducationBtn, mFitnessBtn, mModellingBtn, mMascotBtn;
    private CardView mOfficeBtn, mPromoterBtn, mRoadshowBtn, mRovingBtn;
    private CardView mRetailBtn, mServingBtn, mUsherBtn, mWaiterBtn, mOtherBtn;
    private String category;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_select);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mBaristaBtn = (CardView) findViewById(R.id.BaristaBtn);
        mBeautyBtn = (CardView) findViewById(R.id.BeautyBtn);
        mChefBtn = (CardView) findViewById(R.id.ChefBtn);
        mEventBtn = (CardView) findViewById(R.id.EventBtn);
        mEmceeBtn = (CardView) findViewById(R.id.EmceeBtn);

        mEducationBtn = (CardView) findViewById(R.id.EducationBtn);
        mFitnessBtn = (CardView) findViewById(R.id.FitnessBtn);
        mModellingBtn = (CardView) findViewById(R.id.ModellingBtn);
        mMascotBtn = (CardView) findViewById(R.id.MascotBtn);

        mOfficeBtn = (CardView) findViewById(R.id.OfficeBtn);
        mPromoterBtn = (CardView) findViewById(R.id.PromoterBtn);
        mRoadshowBtn = (CardView) findViewById(R.id.RoadshowBtn);
        mRovingBtn = (CardView) findViewById(R.id.RovingBtn);

        mRetailBtn = (CardView) findViewById(R.id.RetailBtn);
        mServingBtn = (CardView) findViewById(R.id.ServingBtn);
        mUsherBtn = (CardView) findViewById(R.id.UsherBtn);
        mWaiterBtn = (CardView) findViewById(R.id.WaiterBtn);
        mOtherBtn = (CardView) findViewById(R.id.OtherBtn);

        mBaristaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category = "Barista / Bartender";
                EditPost.meditpostCategory.setText(category);
                finish();
            }
        });

        mBeautyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category = "Beauty / Wellness";
                EditPost.meditpostCategory.setText(category);
                finish();
            }
        });

        mChefBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category = "Chef / Kitchen Helper";
                EditPost.meditpostCategory.setText(category);
                finish();
            }
        });

        mEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category = "Event Crew";
                EditPost.meditpostCategory.setText(category);
                finish();
            }
        });

        mEmceeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category = "Emcee";
                EditPost.meditpostCategory.setText(category);
                finish();
            }
        });

        ///////////////////////////////////////////////////////////////

        mEducationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category = "Education";
                EditPost.meditpostCategory.setText(category);
                finish();
            }
        });

        mFitnessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category = "Fitness / Gym";
                EditPost.meditpostCategory.setText(category);
                finish();
            }
        });

        mModellingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category = "Modelling / Shooting";
                EditPost.meditpostCategory.setText(category);
                finish();
            }
        });

        mMascotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category = "Mascot";
                EditPost.meditpostCategory.setText(category);
                finish();
            }
        });

        ////////////////////////////////////////////////////////////////////

        mOfficeBtn .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category = "Office / Admin";
                EditPost.meditpostCategory.setText(category);
                finish();
            }
        });

        mPromoterBtn .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category = "Promoter / Sampling";
                EditPost.meditpostCategory.setText(category);
                finish();
            }
        });

        mRoadshowBtn .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category = "Roadshow";
                EditPost.meditpostCategory.setText(category);
                finish();
            }
        });

        mRovingBtn .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category = "Roving Team";
                EditPost.meditpostCategory.setText(category);
                finish();
            }
        });

        ////////////////////////////////////////////////////////////////////

        mRetailBtn  .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category = "Retail / Consumer";
                EditPost.meditpostCategory.setText(category);
                finish();
            }
        });

        mServingBtn  .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category = "Serving";
                EditPost.meditpostCategory.setText(category);
                finish();
            }
        });

        mUsherBtn  .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category = "Usher / Ambassador";
                EditPost.meditpostCategory.setText(category);
                finish();
            }
        });

        mWaiterBtn  .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category = "Waiter / Waitress";
                EditPost.meditpostCategory.setText(category);
                finish();
            }
        });

        mOtherBtn   .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category = "Other";
                EditPost.meditpostCategory.setText(category);
                finish();
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
