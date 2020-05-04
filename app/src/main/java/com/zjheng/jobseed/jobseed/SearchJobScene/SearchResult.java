package com.zjheng.jobseed.jobseed.SearchJobScene;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.zjheng.jobseed.jobseed.CustomObjectClass.Job;
import com.zjheng.jobseed.jobseed.R;

import java.util.ArrayList;
import java.util.List;

import static com.zjheng.jobseed.jobseed.R.id.nestedscroll;


public class SearchResult extends AppCompatActivity {

    private String blog_title, city;

    private RecyclerView mJobList;
    private LinearLayoutManager mLayoutManager;
    private SearchRecyclerAdapter recyclerAdapter;
    private NestedScrollView mnestedscroll;

    private TextView mjobnotfoundtxt ;

    private RelativeLayout mnocategoryLay, mnoInternetLay, mblankLay;
    private LinearLayout mfilterLay, mfilterLay2;
    private CardView msortCardView;;

    private FirebaseAuth mAuth;

    private DatabaseReference mJob;
    private Query mCurrentQuery, mQuerySearchMore;
    private CardView mretryBtn;

    private List<Job> joblist;

    private String firsttitle;

    private static final String TAG = "SearchResult";

    private int loadlimit = 8;
    private int count = 0;

    private boolean loading = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        Toolbar mToolbar = (Toolbar)findViewById(R.id.toolbar_other);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Search Results");
        mToolbar.setTitleTextColor(android.graphics.Color.WHITE);

        mToolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white_24dp);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Intent intent = getIntent();
        if(intent.hasExtra("blog_title")){
            if (getIntent().getExtras().getString("blog_title") != null) {
                blog_title = getIntent().getExtras().getString("blog_title");
            }
        }
        if(intent.hasExtra("city_id")){
            if (getIntent().getExtras().getString("city_id") != null) {
                city = getIntent().getExtras().getString("city_id");
            }
        }

        mAuth = FirebaseAuth.getInstance();

        mJob =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("Job");

        mCurrentQuery = mJob.child(city).orderByChild("lowertitle").startAt(blog_title.toLowerCase()).endAt(blog_title.toLowerCase()+"~");

        mjobnotfoundtxt = (TextView) findViewById(R.id.jobnotfoundtxt);
        mnoInternetLay = (RelativeLayout) findViewById(R.id.noInternetLay);
        mnocategoryLay = (RelativeLayout) findViewById(R.id.nocategoryLay);
        mretryBtn = (CardView)findViewById(R.id.retryBtn);
        mnestedscroll = (NestedScrollView) findViewById(nestedscroll);


        ConnectivityManager cm = (ConnectivityManager) SearchResult.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                Log.d(TAG, "connected to wifi");
                // connected to wifi
                mnoInternetLay.setVisibility(View.GONE);
                mnestedscroll.setVisibility(View.VISIBLE);

            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                Log.d(TAG, "connected to data");
                // connected to the mobile provider's data plan
                mnoInternetLay.setVisibility(View.GONE);
                mnestedscroll.setVisibility(View.VISIBLE);
            }
        } else {
            Log.d(TAG, "not connected");
            // not connected to the internet
            Toast.makeText(SearchResult.this, "Network Not Available", Toast.LENGTH_LONG).show();
            mnoInternetLay.setVisibility(View.VISIBLE);
            mnestedscroll.setVisibility(View.GONE);

            mretryBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ConnectivityManager cm = (ConnectivityManager) SearchResult.this.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                    if (activeNetwork != null) { // connected to the internet
                        if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                            Log.d(TAG, "connected to wifi");
                            // connected to wifi
                            mnoInternetLay.setVisibility(View.GONE);
                            mnestedscroll.setVisibility(View.VISIBLE);

                        } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                            Log.d(TAG, "connected to data");
                            // connected to the mobile provider's data plan
                            mnoInternetLay.setVisibility(View.GONE);
                            mnestedscroll.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Log.d(TAG, "not connected");
                        // not connected to the internet
                        Toast.makeText(SearchResult.this, "Network Not Available", Toast.LENGTH_LONG).show();
                        mnoInternetLay.setVisibility(View.VISIBLE);
                        mnestedscroll.setVisibility(View.GONE);
                    }
                }
            });
        }

        mblankLay = findViewById(R.id.blankLay);
        mblankLay.setVisibility(View.VISIBLE);

        mJobList = (RecyclerView)findViewById(R.id.blog_list);
        mJobList.setNestedScrollingEnabled(false);
        mJobList.setHasFixedSize(false);

        mfilterLay = (LinearLayout) findViewById(R.id.filterLay);
        mfilterLay.setVisibility(View.GONE);
        mfilterLay2 = (LinearLayout) findViewById(R.id.filterLay2);
        mfilterLay2.setVisibility(View.GONE);

        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setReverseLayout(false);
        mLayoutManager.setStackFromEnd(false);

        joblist = new ArrayList<Job>();
        recyclerAdapter = new SearchRecyclerAdapter(mJob,joblist,SearchResult.this);
        mJobList.setLayoutManager(mLayoutManager);
        mJobList.setAdapter(recyclerAdapter);

        msortCardView = (CardView)findViewById(R.id.sortCardView);
        msortCardView.setVisibility(View.GONE);

        nestedscrollListener();

        checkuserLocation();

    }

    private void nestedscrollListener(){
        mnestedscroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if (scrollY == 0) {
                    Log.d(TAG, "REACHED TOP");
                }

                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                    Log.d(TAG, "REACHED BOTTOM");
                    if(firsttitle!=null) {
                        Log.d(TAG, "loading null: " + loading);
                        if (loading) {
                            loading = false;
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    joblist.remove(joblist.size()-1);
                                    recyclerAdapter.notifyItemRemoved(joblist.size());
                                    loadMoreOperation(city, loadlimit);

                                }
                            }, 750); //time seconds
                        }
                    }
                }
            }
        });
    }

    private void checkuserLocation(){

        if(city != null) {
            mJob.child(city).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {
                        presentsearchjobpost();
                    }
                    else{
                        mnocategoryLay.setVisibility(View.VISIBLE);
                        mjobnotfoundtxt.setText("Sorry, it looks like there aren't any jobs related to " + blog_title + " available in " + city);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else {
            mnocategoryLay.setVisibility(View.VISIBLE);
            mjobnotfoundtxt.setText("Sorry, it looks like there aren't any jobs related to " + blog_title + " available in " + city);
        }

    }

    private void presentsearchjobpost(){

        count = 0;

        mCurrentQuery.limitToFirst(loadlimit).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot usersdataSnapshot : dataSnapshot.getChildren()){

                    Job jobs = new Job();

                    if (usersdataSnapshot.hasChild("title")&& usersdataSnapshot.hasChild("desc")&& usersdataSnapshot.hasChild("category")
                            && usersdataSnapshot.hasChild("company")&& usersdataSnapshot.hasChild("fulladdress")
                            && usersdataSnapshot.hasChild("postimage")&& usersdataSnapshot.hasChild("postkey")
                            && usersdataSnapshot.hasChild("city")&& usersdataSnapshot.hasChild("closed")
                            && usersdataSnapshot.hasChild("lowertitle")) {

                        String title = usersdataSnapshot.child("title").getValue().toString();
                        String desc = usersdataSnapshot.child("desc").getValue().toString();
                        String category = usersdataSnapshot.child("category").getValue().toString();
                        String company = usersdataSnapshot.child("company").getValue().toString();
                        String fulladdress = usersdataSnapshot.child("fulladdress").getValue().toString();
                        String postimage = usersdataSnapshot.child("postimage").getValue().toString();
                        firsttitle = usersdataSnapshot.child("lowertitle").getValue().toString();
                        String postkey = usersdataSnapshot.child("postkey").getValue().toString();
                        String city = usersdataSnapshot.child("city").getValue().toString();
                        String closed = usersdataSnapshot.child("closed").getValue().toString();

                        if (usersdataSnapshot.hasChild("wages")){
                            String wages = usersdataSnapshot.child("wages").getValue().toString();
                            jobs.setWages(wages);
                        }
                        if (usersdataSnapshot.hasChild("date")){
                            String date = usersdataSnapshot.child("date").getValue().toString();
                            jobs.setDate(date);
                        }

                        jobs.setTitle(title);
                        jobs.setDesc(desc);
                        jobs.setCategory(category);
                        jobs.setCompany(company);
                        jobs.setFulladdress(fulladdress);
                        jobs.setpostImage(postimage);
                        jobs.setpostkey(postkey);
                        jobs.setCity(city);
                        jobs.setclosed(closed);

                        count++;
                        if (closed.equals("false")) {
                            Log.d(TAG, "title: " + title);
                            joblist.add(jobs);
                        }
                        if (count == loadlimit) {
                            joblist.add(null);
                            recyclerAdapter.notifyItemInserted(joblist.size() - 1);
                        }
                    }
                }

                if (count < loadlimit){
                    loading = false;
                }
                else{
                    loading = true;
                }

                if (joblist.isEmpty()) {
                    loading = false;
                    mnocategoryLay.setVisibility(View.VISIBLE);
                    mjobnotfoundtxt.setText("Sorry, it looks like there aren't any jobs related to " + blog_title + " available in " + city);
                } else {
                    mnocategoryLay.setVisibility(View.GONE);
                    recyclerAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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

    protected void loadMoreOperation(final String city, final int loadlimit){
        Log.d(TAG, "aafirstpostkey: " + firsttitle);

        mQuerySearchMore = mJob.child(city).orderByChild("lowertitle").limitToFirst(loadlimit+1).startAt(firsttitle).endAt(firsttitle.toLowerCase()+"~");

        mQuerySearchMore.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Boolean firstkey = true;

                for(DataSnapshot usersdataSnapshot1 : dataSnapshot.getChildren()){

                    Job jobs = new Job();

                    if (usersdataSnapshot1.hasChild("title")&& usersdataSnapshot1.hasChild("desc")&& usersdataSnapshot1.hasChild("category")
                            && usersdataSnapshot1.hasChild("company")&& usersdataSnapshot1.hasChild("fulladdress")
                            && usersdataSnapshot1.hasChild("postimage")&& usersdataSnapshot1.hasChild("postkey")
                            && usersdataSnapshot1.hasChild("city")&& usersdataSnapshot1.hasChild("closed")
                            && usersdataSnapshot1.hasChild("lowertitle")) {

                        String title = usersdataSnapshot1.child("title").getValue().toString();
                        String desc = usersdataSnapshot1.child("desc").getValue().toString();
                        String category = usersdataSnapshot1.child("category").getValue().toString();
                        String company = usersdataSnapshot1.child("company").getValue().toString();
                        String fulladdress = usersdataSnapshot1.child("fulladdress").getValue().toString();
                        String postimage = usersdataSnapshot1.child("postimage").getValue().toString();
                        firsttitle = usersdataSnapshot1.child("lowertitle").getValue().toString();
                        String postkey = usersdataSnapshot1.child("postkey").getValue().toString();
                        String city = usersdataSnapshot1.child("city").getValue().toString();
                        String closed = usersdataSnapshot1.child("closed").getValue().toString();

                        if (usersdataSnapshot1.hasChild("wages")){
                            String wages = usersdataSnapshot1.child("wages").getValue().toString();
                            jobs.setWages(wages);
                        }
                        if (usersdataSnapshot1.hasChild("date")){
                            String date = usersdataSnapshot1.child("date").getValue().toString();
                            jobs.setDate(date);
                        }

                        jobs.setTitle(title);
                        jobs.setDesc(desc);
                        jobs.setCategory(category);
                        jobs.setCompany(company);
                        jobs.setFulladdress(fulladdress);
                        jobs.setpostImage(postimage);
                        jobs.setpostkey(postkey);
                        jobs.setCity(city);
                        jobs.setclosed(closed);

                        count++;
                        if (firstkey) {
                            firstkey = false;
                        } else {
                            if (closed.equals("false")) {
                                Log.d(TAG, "moretitle: " + title);
                                joblist.add(jobs);
                            }
                            if (count == loadlimit+1) {
                                joblist.add(null);
                                recyclerAdapter.notifyItemInserted(joblist.size() - 1);
                            }
                        }
                    }
                }

                if (count < loadlimit + 1) {
                    Log.d(TAG, "end loading");
                    recyclerAdapter.notifyDataSetChanged();
                    loading = false;
                }

                else {
                    Log.d(TAG, "cont loading");
                    recyclerAdapter.notifyDataSetChanged();
                    loading = true;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}


