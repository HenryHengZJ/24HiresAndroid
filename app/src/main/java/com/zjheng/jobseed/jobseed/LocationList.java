package com.zjheng.jobseed.jobseed;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zjheng.jobseed.jobseed.SearchTalentScene.SearchTalent;

import java.util.ArrayList;

/**
 * Created by zhen on 5/14/2017.
 *
 */

public class LocationList extends AppCompatActivity implements AvailableLocationRecyclerAdapter.ItemClickListener {

    private RecyclerView mlocationlist;
    private LinearLayoutManager mLayoutManager;

    private FirebaseAuth mAuth;
    private DatabaseReference mLocationList, mUserLocation;

    private static final String TAG = "LocationList";

    private ArrayList <String> savedlocationlist;
    private ArrayList <String> locationlist;
    private ArrayList <String> selectedlocationlist;
    private AvailableLocationRecyclerAdapter locationAdapter;

    private String selectedCity, locationstatus, travellocations;
    private boolean itemClick = false;

    private ImageButton mselectBtn;
    private Toolbar mToolbar;
    private String maincategory, subcategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.locationtab);


        travellocations = getIntent().getStringExtra("travellocations");
        locationstatus = getIntent().getStringExtra("locationstatus");

        if (locationstatus.equals("SearchTalent")) {
            maincategory = getIntent().getStringExtra("maincategory");
            subcategory = getIntent().getStringExtra("subcategory");
        }

        mAuth = FirebaseAuth.getInstance();
        mLocationList =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("AvailableLocation").child("Malaysia");
        mUserLocation =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserLocation");

        mToolbar = findViewById(R.id.toolbar_other);
        mselectBtn = findViewById(R.id.selectBtn);

        mlocationlist = findViewById(R.id.locationlist);
        mlocationlist.setHasFixedSize(false);

        mLayoutManager = new LinearLayoutManager(getApplicationContext());

        selectedlocationlist = new ArrayList<String>();
        locationlist = new ArrayList<String>();
        savedlocationlist = new ArrayList<String>();

        if (locationstatus.equals("All")) {
            mselectBtn.setVisibility(View.VISIBLE);

            if (travellocations!= null ) {
                String[] separatedcities = travellocations.split(" / ");
                for (int x = 0; x< separatedcities.length; x++){
                    savedlocationlist.add(separatedcities[x]);
                    selectedlocationlist.add(separatedcities[x]);
                }
            }
        }
        else {
            mselectBtn.setVisibility(View.GONE);
        }

        locationAdapter = new AvailableLocationRecyclerAdapter(savedlocationlist,locationstatus,locationlist,LocationList.this);
        mlocationlist.setLayoutManager(mLayoutManager);
        locationAdapter.setClickListener(this);
        mlocationlist.setAdapter(locationAdapter);


        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Select Location");
        mToolbar.setTitleTextColor(android.graphics.Color.WHITE);

        mToolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white_24dp);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClick = false;
                onBackPressed();
            }
        });

        mLocationList.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot locationdataSnapshot : dataSnapshot.getChildren()) {
                    String locationval = locationdataSnapshot.getValue().toString();
                    locationlist.add(locationval);
                }

                if (locationstatus.equals("All")) {
                    locationlist.add(0, "All Around the World");
                    locationlist.add(1, "All Malaysia");
                }
                locationlist.add("County Limerick");
                locationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mselectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!selectedlocationlist.isEmpty()) {

                    travellocations = TextUtils.join(" / ", selectedlocationlist);
                    Log.d(TAG, "selectedlocationlist travellocations " + travellocations);
                    itemClick = true;
                    onBackPressed();

                }
                else {
                    Toast.makeText(getApplicationContext(),"Please select at least one location", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onItemClick(View view, final int position) {
        //Toast.makeText(this, "You clicked " + locationlist.get(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
        selectedCity = locationlist.get(position);

        if (locationstatus.equals("All")) {

            if (selectedlocationlist.contains(selectedCity)) {

                for (int x = 0; x< selectedlocationlist.size();x++){
                    String selectedval = selectedlocationlist.get(x);
                    if(selectedval.equals(selectedCity)) {
                        selectedlocationlist.remove(x);
                    }
                }
                View viewx = mlocationlist.findViewHolderForAdapterPosition(position).itemView;
                ImageButton mtickBtn = viewx.findViewById(R.id.tickBtn);
                mtickBtn.setVisibility(View.GONE);
                TextView myTextView = view.findViewById(R.id.searchResult);
                myTextView.setTextColor(Color.parseColor("#de000000"));

            }
            else {
                Log.d(TAG, "selectedlocationlist ADD");
                View viewx = mlocationlist.findViewHolderForAdapterPosition(position).itemView;
                ImageButton mtickBtn = viewx.findViewById(R.id.tickBtn);
                mtickBtn.setVisibility(View.VISIBLE);
                TextView myTextView = view.findViewById(R.id.searchResult);
                myTextView.setTextColor(Color.parseColor("#1a8dfb"));
                selectedlocationlist.add(selectedCity);
            }
        }
        else if (locationstatus.equals("SearchTalent")) {
            Intent searchintent = new Intent(LocationList.this, SearchTalent.class);
            searchintent.putExtra("sortby", "reviewcount_negatedtime");
            searchintent.putExtra("city_id", selectedCity);
            searchintent.putExtra("maincategory", maincategory);
            searchintent.putExtra("subcategory", subcategory);
            startActivity(searchintent);
        }
        else {
            itemClick = true;
            onBackPressed();
        }
    }


    /*@Override
    public void finish() {
        Intent data = new Intent();
        setResult(1111, data);

        super.finish();
    }*/

    private void checkitemClick(String chosencities) {
        if (itemClick) {
            Bundle bundle = new Bundle();
            bundle.putString("city", chosencities);
            Intent mIntent = new Intent();
            mIntent.putExtras(bundle);
            setResult(Activity.RESULT_OK,mIntent);
        }
        else {
            Bundle bundle = new Bundle();
            bundle.putString("city", chosencities);
            Intent mIntent = new Intent();
            mIntent.putExtras(bundle);
            setResult(Activity.RESULT_CANCELED,mIntent);
        }
    }


    @Override
    public void onBackPressed() {

        if (locationstatus.equals("All")) {
            checkitemClick(travellocations);
        }
        else {
            checkitemClick(selectedCity);
        }
        super.onBackPressed();
    }
}