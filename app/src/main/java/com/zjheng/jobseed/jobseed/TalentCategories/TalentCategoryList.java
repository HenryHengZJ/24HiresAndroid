package com.zjheng.jobseed.jobseed.TalentCategories;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zjheng.jobseed.jobseed.AvailableLocationRecyclerAdapter;
import com.zjheng.jobseed.jobseed.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhen on 5/14/2017.
 *
 */

public class TalentCategoryList extends AppCompatActivity implements AvailableLocationRecyclerAdapter.ItemClickListener, SubTalentCategoryRecyclerAdapter.ItemClickListener {

    private RecyclerView mcategorylist;
    private LinearLayoutManager mLayoutManager;

    private Dialog dialog;
    private ArrayList <String> subcategorylist;
    private SubTalentCategoryRecyclerAdapter subcategoryAdapter;

    private DatabaseReference mCategory;
    private FirebaseApp fbApp;
    private FirebaseDatabase fbDB;

    private static final String TAG = "TalentCategoryList";

    private ArrayList <String> categorylist;
    private ArrayList <String> emptylist;
    private AvailableLocationRecyclerAdapter categoryAdapter;

    private String maincategory, subcategory;
    private boolean itemClick = false;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.locationtab);

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApiKey("AIzaSyAX3s8Y0CA2RwBF5vxhX9tVqV5Gg1S2UHE")
                .setApplicationId("1:1004980108230:android:4ecc908d28953c07")
                .setDatabaseUrl("https://my-gg-app.firebaseio.com")
                .build();

        boolean hasBeenInitialized = false;
        List<FirebaseApp> fbsLcl = FirebaseApp.getApps(TalentCategoryList.this);
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
        getSupportActionBar().setTitle("Select Talent Category");
        mToolbar.setTitleTextColor(android.graphics.Color.WHITE);

        mToolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white_24dp);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClick = false;
                onBackPressed();
            }
        });

        mcategorylist = findViewById(R.id.locationlist);
        mcategorylist.setHasFixedSize(false);

        mLayoutManager = new LinearLayoutManager(getApplicationContext());

        categorylist = new ArrayList<String>();
        emptylist = new ArrayList<String>();
        categoryAdapter = new AvailableLocationRecyclerAdapter(emptylist,"",categorylist,TalentCategoryList.this);
        mcategorylist.setLayoutManager(mLayoutManager);
        categoryAdapter.setClickListener(this);
        mcategorylist.setAdapter(categoryAdapter);

        mCategory.addListenerForSingleValueEvent(new ValueEventListener() {
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

    private void showSubCategory(String maincategory) {

        dialog = new Dialog(TalentCategoryList.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.talentcategorytab);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(lp);

        dialog.show();

        final RecyclerView msubcategorylist;
        final LinearLayoutManager msubLayoutManager;

        msubcategorylist = dialog.findViewById(R.id.categorylist);
        msubcategorylist.setHasFixedSize(false);

        msubLayoutManager = new LinearLayoutManager(TalentCategoryList.this);

        subcategorylist = new ArrayList<String>();
        subcategoryAdapter = new SubTalentCategoryRecyclerAdapter(subcategorylist,TalentCategoryList.this);
        msubcategorylist.setLayoutManager(msubLayoutManager);
        subcategoryAdapter.setClickListener(this);
        msubcategorylist.setAdapter(new SampleRecycler());

        Log.d(TAG, "maincategory " + maincategory);

        mCategory.child(maincategory).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot categorydataSnapshot : dataSnapshot.getChildren()) {
                    String categoryval = categorydataSnapshot.getKey();
                    Log.d(TAG, "categoryval " + categoryval);
                    subcategorylist.add(categoryval);
                }
                subcategoryAdapter.notifyDataSetChanged();
                msubcategorylist.setAdapter(subcategoryAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // SampleHolder.java
    public class SampleHolder extends RecyclerView.ViewHolder {
        public SampleHolder(View itemView) {
            super(itemView);
        }
    }

    // SampleRecycler.java
    public class SampleRecycler extends RecyclerView.Adapter<SampleHolder> {
        @Override
        public SampleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(SampleHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }


    @Override
    public void onSubCatClick(View view, final int position) {
        subcategory = subcategorylist.get(position);
        itemClick = true;
        dialog.dismiss();
        onBackPressed();
    }

    @Override
    public void onItemClick(View view, final int position) {
        //Toast.makeText(this, "You clicked " + locationlist.get(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
        maincategory = categorylist.get(position);
        showSubCategory(maincategory);

    }


    @Override
    public void onBackPressed() {
        if (itemClick) {
            Bundle bundle = new Bundle();
            bundle.putString("category", maincategory+" / "+subcategory);
            bundle.putString("maincategory", maincategory);
            bundle.putString("subcategory", subcategory);
            Intent mIntent = new Intent();
            mIntent.putExtras(bundle);
            setResult(Activity.RESULT_OK,mIntent);
        }
        else {
            Bundle bundle = new Bundle();
            bundle.putString("category", "");
            bundle.putString("maincategory", "");
            bundle.putString("subcategory", "");
            Intent mIntent = new Intent();
            mIntent.putExtras(bundle);
            setResult(Activity.RESULT_CANCELED,mIntent);
        }

        super.onBackPressed();
    }


}