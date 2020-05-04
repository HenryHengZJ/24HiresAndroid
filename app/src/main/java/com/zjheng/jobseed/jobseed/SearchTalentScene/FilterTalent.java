package com.zjheng.jobseed.jobseed.SearchTalentScene;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zjheng.jobseed.jobseed.LocationList;
import com.zjheng.jobseed.jobseed.R;
import com.zjheng.jobseed.jobseed.TalentCategories.TalentCategoryList;

public class FilterTalent extends AppCompatActivity {

    private ImageView mreviewtickimg, mrecenttickimg;

    private CardView mreviewCardView, mrecentCardView, mcategoryCardView, mlocationCardView, mapplyCardView;

    private String sortby, maincategory, subcategory, city;

    private TextView mpostCategoryx, mpostBaseLocation;

    private Boolean mapplyPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_talent);

        sortby = getIntent().getExtras().getString("sortby");
        maincategory = getIntent().getExtras().getString("maincategory");
        subcategory = getIntent().getExtras().getString("subcategory");
        city = getIntent().getExtras().getString("city");

        Toolbar mToolbar = (Toolbar)findViewById(R.id.toolbar_other);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Refine");
        mToolbar.setTitleTextColor(android.graphics.Color.WHITE);

        mToolbar.setNavigationIcon(R.mipmap.ic_close_white_24dp);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mreviewtickimg = findViewById(R.id.reviewtickimg);
        mrecenttickimg = findViewById(R.id.recenttickimg);

        mreviewCardView = findViewById(R.id.reviewCardView);
        mrecentCardView = findViewById(R.id.recentCardView);
        mcategoryCardView = findViewById(R.id.categoryCardView);
        mlocationCardView = findViewById(R.id.locationCardView);
        mapplyCardView = findViewById(R.id.applyCardView);

        mpostCategoryx = findViewById(R.id.postCategoryx);
        mpostBaseLocation = findViewById(R.id.postBaseLocation);

        mpostCategoryx.setText(maincategory + " / " + subcategory);
        mpostBaseLocation.setText(city);

        if (sortby.equals("reviewcount_negatedtime")) {
            mreviewtickimg.setImageResource(R.drawable.single_tick);
            mrecenttickimg.setImageResource(R.color.buttonTextColor);
            sortby = "reviewcount_negatedtime";
        }
        else {
            mrecenttickimg.setImageResource(R.drawable.single_tick);
            mreviewtickimg.setImageResource(R.color.buttonTextColor);
            sortby = "negatedtime";
        }

        mcategoryCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FilterTalent.this, TalentCategoryList.class);
                startActivityForResult(intent, 1111);
            }
        });

        mlocationCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FilterTalent.this, LocationList.class);
                intent.putExtra("locationstatus", "");
                intent.putExtra("travellocations", "");
                startActivityForResult(intent, 1000);
            }
        });

        mreviewCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mreviewtickimg.setImageResource(R.drawable.single_tick);
                mrecenttickimg.setImageResource(R.color.buttonTextColor);

                sortby = "reviewcount_negatedtime";
            }
        });

        mrecentCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mrecenttickimg.setImageResource(R.drawable.single_tick);
                mreviewtickimg.setImageResource(R.color.buttonTextColor);

                sortby = "negatedtime";
            }
        });

        mapplyCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mapplyPressed = true;
                onBackPressed();
            }
        });

    }


    @Override
    public void onBackPressed() {

        if (mapplyPressed) {
            Bundle bundle = new Bundle();
            bundle.putString("city", city);
            bundle.putString("maincategory", maincategory);
            bundle.putString("subcategory", subcategory);
            bundle.putString("sortby", sortby);
            Intent mIntent = new Intent();
            mIntent.putExtras(bundle);
            setResult(Activity.RESULT_OK,mIntent);
        }
        else {
            setResult(Activity.RESULT_CANCELED);
        }

        super.onBackPressed();
        overridePendingTransition(R.anim.nochange, R.anim.pulldown);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == 1000) && (resultCode == Activity.RESULT_OK)) {
            // recreate your fragment here

            String baselocation = data.getStringExtra("city");
            mpostBaseLocation.setText(baselocation);
            city = baselocation;
        }
        else if ((requestCode == 1000) && (resultCode == Activity.RESULT_CANCELED)) {

        }

        if ((requestCode == 1111) && (resultCode == Activity.RESULT_OK)) {
            // recreate your fragment here
            maincategory = data.getStringExtra("maincategory");
            subcategory = data.getStringExtra("subcategory");
            String strCategoryText = data.getStringExtra("category");
            mpostCategoryx.setText(strCategoryText);


        }
        else if ((requestCode == 1111) && (resultCode == Activity.RESULT_CANCELED)) {

        }
    }
}
