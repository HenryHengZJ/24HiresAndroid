package com.zjheng.jobseed.jobseed.SearchJobScene;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zjheng.jobseed.jobseed.R;

public class SearchJobCategoryBar extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mJob;

    private String searchtext,bigsearchtext, city;
    private NestedScrollView mcategorynestedscroll;

    private LinearLayout mcategorylist;

    private CardView mBaristaCardView, mBeautyCardView , mChefCardView, mEventCardView, mEmceeCardView;
    private CardView mEducationCardView, mFitnessCardView, mModellingCardView, mMascotCardView;
    private CardView mOfficeCardView, mPromoterCardView, mRoadshowCardView, mRovingCardView;
    private CardView mRetailCardView, mServingCardView, mUsherCardView, mWaiterCardView, mOtherCardView;

    private ProgressDialog mProgress;

    private static int PLACE_PICKER_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_bar_category);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //supportPostponeEnterTransition();

        city = getIntent().getStringExtra("city_id");

        mcategorynestedscroll = (NestedScrollView) findViewById(R.id.categorynestedscroll);
        mcategorylist = (LinearLayout) findViewById(R.id.categorylist);

        mAuth = FirebaseAuth.getInstance();

        mJob =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("Job");

        mProgress = new ProgressDialog(this);

        mBaristaCardView = (CardView) findViewById(R.id.BaristaCardView);
        mBeautyCardView = (CardView) findViewById(R.id.BeautyCardView);
        mChefCardView = (CardView) findViewById(R.id.ChefCardView);
        mEventCardView = (CardView) findViewById(R.id.EventCardView);
        mEmceeCardView = (CardView) findViewById(R.id.EmceeCardView);

        mEducationCardView = (CardView) findViewById(R.id.EducationCardView);
        mFitnessCardView = (CardView) findViewById(R.id.FitnessCardView);
        mModellingCardView = (CardView) findViewById(R.id.ModellingCardView);
        mMascotCardView = (CardView) findViewById(R.id.MascotCardView);

        mOfficeCardView = (CardView) findViewById(R.id.OfficeCardView);
        mPromoterCardView = (CardView) findViewById(R.id.PromoterCardView);
        mRoadshowCardView = (CardView) findViewById(R.id.RoadshowCardView);
        mRovingCardView = (CardView) findViewById(R.id.RovingCardView);

        mRetailCardView = (CardView) findViewById(R.id.RetailCardView);
        mServingCardView = (CardView) findViewById(R.id.ServingCardView);
        mUsherCardView = (CardView) findViewById(R.id.UsherCardView);
        mWaiterCardView = (CardView) findViewById(R.id.WaiterCardView);
        mOtherCardView = (CardView) findViewById(R.id.OtherCardView);

        mBaristaCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(city!=null) {
                    Intent singleblogintent = new Intent(SearchJobCategoryBar.this, SearchCategory.class);
                    singleblogintent.putExtra("category_title", "Barista / Bartender");
                    singleblogintent.putExtra("city_id", city);
                    startActivity(singleblogintent);
                }
                else{
                    Toast.makeText(SearchJobCategoryBar.this, "Please specify a location", Toast.LENGTH_LONG).show();
                }
            }
        });

        mBeautyCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(city!=null) {
                    Intent singleblogintent = new Intent(SearchJobCategoryBar.this, SearchCategory.class);
                    singleblogintent.putExtra("category_title", "Beauty / Wellness");
                    singleblogintent.putExtra("city_id", city);
                    startActivity(singleblogintent);
                }
                else{
                    Toast.makeText(SearchJobCategoryBar.this, "Please specify a location", Toast.LENGTH_LONG).show();
                }
            }
        });

        mChefCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(city!=null) {
                    Intent singleblogintent = new Intent(SearchJobCategoryBar.this, SearchCategory.class);
                    singleblogintent.putExtra("category_title", "Chef / Kitchen Helper");
                    singleblogintent.putExtra("city_id", city);
                    startActivity(singleblogintent);
                }
                else{
                    Toast.makeText(SearchJobCategoryBar.this, "Please specify a location", Toast.LENGTH_LONG).show();
                }
            }
        });

        mEventCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(city!=null) {
                    Intent singleblogintent = new Intent(SearchJobCategoryBar.this, SearchCategory.class);
                    singleblogintent.putExtra("category_title", "Event Crew");
                    singleblogintent.putExtra("city_id", city);
                    startActivity(singleblogintent);
                }
                else{
                    Toast.makeText(SearchJobCategoryBar.this, "Please specify a location", Toast.LENGTH_LONG).show();
                }
            }
        });



        mEmceeCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(city!=null) {
                    Intent singleblogintent = new Intent(SearchJobCategoryBar.this, SearchCategory.class);
                    singleblogintent.putExtra("category_title", "Emcee");
                    singleblogintent.putExtra("city_id", city);
                    startActivity(singleblogintent);
                }
                else{
                    Toast.makeText(SearchJobCategoryBar.this, "Please specify a location", Toast.LENGTH_LONG).show();
                }
            }
        });

        mEducationCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(city!=null) {
                    Intent singleblogintent = new Intent(SearchJobCategoryBar.this, SearchCategory.class);
                    singleblogintent.putExtra("category_title", "Education");
                    singleblogintent.putExtra("city_id", city);
                    startActivity(singleblogintent);
                }
                else{
                    Toast.makeText(SearchJobCategoryBar.this, "Please specify a location", Toast.LENGTH_LONG).show();
                }
            }
        });

        mFitnessCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(city!=null) {
                    Intent singleblogintent = new Intent(SearchJobCategoryBar.this, SearchCategory.class);
                    singleblogintent.putExtra("category_title", "Fitness / Gym");
                    singleblogintent.putExtra("city_id", city);
                    startActivity(singleblogintent);
                }
                else{
                    Toast.makeText(SearchJobCategoryBar.this, "Please specify a location", Toast.LENGTH_LONG).show();
                }
            }
        });

        mModellingCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(city!=null) {
                    Intent singleblogintent = new Intent(SearchJobCategoryBar.this, SearchCategory.class);
                    singleblogintent.putExtra("category_title", "Modelling / Shooting");
                    singleblogintent.putExtra("city_id", city);
                    startActivity(singleblogintent);
                }
                else{
                    Toast.makeText(SearchJobCategoryBar.this, "Please specify a location", Toast.LENGTH_LONG).show();
                }
            }
        });

        mMascotCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(city!=null) {
                    Intent singleblogintent = new Intent(SearchJobCategoryBar.this, SearchCategory.class);
                    singleblogintent.putExtra("category_title", "Mascot");
                    singleblogintent.putExtra("city_id", city);
                    startActivity(singleblogintent);
                }
                else{
                    Toast.makeText(SearchJobCategoryBar.this, "Please specify a location", Toast.LENGTH_LONG).show();
                }
            }
        });

        mOfficeCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(city!=null) {
                    Intent singleblogintent = new Intent(SearchJobCategoryBar.this, SearchCategory.class);
                    singleblogintent.putExtra("category_title", "Office / Admin");
                    singleblogintent.putExtra("city_id", city);
                    startActivity(singleblogintent);
                }
                else{
                    Toast.makeText(SearchJobCategoryBar.this, "Please specify a location", Toast.LENGTH_LONG).show();
                }
            }
        });

        mPromoterCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(city!=null) {
                    Intent singleblogintent = new Intent(SearchJobCategoryBar.this, SearchCategory.class);
                    singleblogintent.putExtra("category_title", "Promoter / Sampling");
                    singleblogintent.putExtra("city_id", city);
                    startActivity(singleblogintent);
                }
                else{
                    Toast.makeText(SearchJobCategoryBar.this, "Please specify a location", Toast.LENGTH_LONG).show();
                }
            }
        });

        mRoadshowCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(city!=null) {
                    Intent singleblogintent = new Intent(SearchJobCategoryBar.this, SearchCategory.class);
                    singleblogintent.putExtra("category_title", "Roadshow");
                    singleblogintent.putExtra("city_id", city);
                    startActivity(singleblogintent);
                }
                else{
                    Toast.makeText(SearchJobCategoryBar.this, "Please specify a location", Toast.LENGTH_LONG).show();
                }
            }
        });

        mRovingCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(city!=null) {
                    Intent singleblogintent = new Intent(SearchJobCategoryBar.this, SearchCategory.class);
                    singleblogintent.putExtra("category_title", "Roving Team");
                    singleblogintent.putExtra("city_id", city);
                    startActivity(singleblogintent);
                }
                else{
                    Toast.makeText(SearchJobCategoryBar.this, "Please specify a location", Toast.LENGTH_LONG).show();
                }
            }
        });

        mRetailCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(city!=null) {
                    Intent singleblogintent = new Intent(SearchJobCategoryBar.this, SearchCategory.class);
                    singleblogintent.putExtra("category_title", "Retail / Consumer");
                    singleblogintent.putExtra("city_id", city);
                    startActivity(singleblogintent);
                }
                else{
                    Toast.makeText(SearchJobCategoryBar.this, "Please specify a location", Toast.LENGTH_LONG).show();
                }
            }
        });

        mServingCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(city!=null) {
                    Intent singleblogintent = new Intent(SearchJobCategoryBar.this, SearchCategory.class);
                    singleblogintent.putExtra("category_title", "Serving");
                    singleblogintent.putExtra("city_id", city);
                    startActivity(singleblogintent);
                }
                else{
                    Toast.makeText(SearchJobCategoryBar.this, "Please specify a location", Toast.LENGTH_LONG).show();
                }
            }
        });

        mUsherCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(city!=null) {
                    Intent singleblogintent = new Intent(SearchJobCategoryBar.this, SearchCategory.class);
                    singleblogintent.putExtra("category_title", "Usher / Ambassador");
                    singleblogintent.putExtra("city_id", city);
                    startActivity(singleblogintent);
                }
                else{
                    Toast.makeText(SearchJobCategoryBar.this, "Please specify a location", Toast.LENGTH_LONG).show();
                }
            }
        });

        mWaiterCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(city!=null) {
                    Intent singleblogintent = new Intent(SearchJobCategoryBar.this, SearchCategory.class);
                    singleblogintent.putExtra("category_title", "Waiter / Waitress");
                    singleblogintent.putExtra("city_id", city);
                    startActivity(singleblogintent);
                }
                else{
                    Toast.makeText(SearchJobCategoryBar.this, "Please specify a location", Toast.LENGTH_LONG).show();
                }
            }
        });

        mOtherCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(city!=null) {
                    Intent singleblogintent = new Intent(SearchJobCategoryBar.this, SearchCategory.class);
                    singleblogintent.putExtra("category_title", "Other");
                    singleblogintent.putExtra("city_id", city);
                    startActivity(singleblogintent);
                }
                else{
                    Toast.makeText(SearchJobCategoryBar.this, "Please specify a location", Toast.LENGTH_LONG).show();
                }
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
