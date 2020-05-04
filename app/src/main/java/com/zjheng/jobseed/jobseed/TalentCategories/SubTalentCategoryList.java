package com.zjheng.jobseed.jobseed.TalentCategories;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zjheng.jobseed.jobseed.AvailableLocationRecyclerAdapter;
import com.zjheng.jobseed.jobseed.LocationList;
import com.zjheng.jobseed.jobseed.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhen on 5/14/2017.
 *
 */

public class SubTalentCategoryList extends AppCompatActivity implements AvailableLocationRecyclerAdapter.ItemClickListener {

    private RecyclerView mcategorylist;
    private LinearLayoutManager mLayoutManager;

    private Dialog dialog;
    private ArrayList <String> subcategorylist;
    private SubTalentCategoryRecyclerAdapter subcategoryAdapter;

    private DatabaseReference mCategory;
    private FirebaseApp fbApp;
    private FirebaseDatabase fbDB;

    private static final String TAG = "SubTalentCategoryList";

    private ArrayList <String> categorylist;
    private ArrayList <String> emptylist;
    private AvailableLocationRecyclerAdapter categoryAdapter;

    private String subcategory, talenttype, city;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.locationtab);

        talenttype = getIntent().getStringExtra("talenttype");
        city = getIntent().getStringExtra("city");

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApiKey("AIzaSyAX3s8Y0CA2RwBF5vxhX9tVqV5Gg1S2UHE")
                .setApplicationId("1:1004980108230:android:4ecc908d28953c07")
                .setDatabaseUrl("https://my-gg-app.firebaseio.com")
                .build();

        boolean hasBeenInitialized = false;
        List<FirebaseApp> fbsLcl = FirebaseApp.getApps(SubTalentCategoryList.this);
        for (FirebaseApp app : fbsLcl) {
            if (app.getName().equals("LanceApp")) {
                hasBeenInitialized = true;
                fbApp = app;
            }
        }

        if (!hasBeenInitialized) {
            fbApp = FirebaseApp.initializeApp(getApplicationContext(), options, "LanceApp"/*""*/);
        }

        fbDB = FirebaseDatabase.getInstance(fbApp);

        mCategory = fbDB.getReferenceFromUrl("https://my-gg-app.firebaseio.com/").child("Category");

        mToolbar = findViewById(R.id.toolbar_other);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Select Talent SubCategory");
        mToolbar.setTitleTextColor(android.graphics.Color.WHITE);

        mToolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white_24dp);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mcategorylist = findViewById(R.id.locationlist);
        mcategorylist.setHasFixedSize(false);

        mLayoutManager = new LinearLayoutManager(getApplicationContext());

        categorylist = new ArrayList<String>();
        emptylist = new ArrayList<String>();
        categoryAdapter = new AvailableLocationRecyclerAdapter(emptylist,"",categorylist,SubTalentCategoryList.this);
        mcategorylist.setLayoutManager(mLayoutManager);
        categoryAdapter.setClickListener(this);
        mcategorylist.setAdapter(categoryAdapter);

        mCategory.child(talenttype).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot categorydataSnapshot : dataSnapshot.getChildren()) {
                    String categoryval = categorydataSnapshot.getKey();
                    categorylist.add(categoryval);
                }
                categoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    @Override
    public void onItemClick(View view, final int position) {
        //Toast.makeText(this, "You clicked " + locationlist.get(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
        subcategory = categorylist.get(position);

        Intent intent = new Intent(SubTalentCategoryList.this, LocationList.class);
        intent.putExtra("locationstatus", "SearchTalent");
        intent.putExtra("travellocations", "");
        intent.putExtra("maincategory",talenttype);
        intent.putExtra("subcategory",subcategory);
        startActivity(intent);

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


}