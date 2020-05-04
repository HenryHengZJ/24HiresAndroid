package com.zjheng.jobseed.jobseed.SearchTalentScene;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.zjheng.jobseed.jobseed.CustomObjectClass.TalentInfo;
import com.zjheng.jobseed.jobseed.R;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.zjheng.jobseed.jobseed.R.id.nestedscroll;

public class SearchTalent extends AppCompatActivity {

    private String maincategory, subcategory, city, sortby;
    private Long nextnegatedtime, reviewcount_nextnegatedtime;

    private RecyclerView mJobList;
    private LinearLayoutManager mLayoutManager;
    private SearchTalentRecyclerAdapter recyclerAdapter;
    private NestedScrollView mnestedscroll;

    private TextView mfilterbycitytxt, mfilterbycategorytxt, mfilterbyreviewtxt, mjobnotfoundtxt, mfilterbycitytxt2, mfilterbycategorytxt2, mfilterbyreviewtxt2, mfiltercitytxt, mfiltercitytxt2;

    private RelativeLayout mnocategoryLay, mnoInternetLay;
    private LinearLayout mfilterLay, msortLay, msortLay2;
    private CardView msortCardView;

    private FirebaseAuth mAuth;

    private DatabaseReference mTalent;
    private Query mQuerySearch, mQuerySearchMore;
    private CardView mretryBtn;

    private List<TalentInfo> talentlist;

    private static final String TAG = "SearchTalent";

    private int loadlimit = 8;
    private int count = 0;

    private boolean loading = true;

    private ProgressDialog mdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        sortby = getIntent().getExtras().getString("sortby");
        city = getIntent().getExtras().getString("city_id");
        maincategory = getIntent().getExtras().getString("maincategory");
        subcategory = getIntent().getExtras().getString("subcategory");

        Toolbar mToolbar = (Toolbar)findViewById(R.id.toolbar_other);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(maincategory + " / " + subcategory);
        mToolbar.setTitleTextColor(Color.WHITE);

        mdialog = new ProgressDialog(SearchTalent.this, R.style.MyTheme);

        mToolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white_24dp);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mAuth = FirebaseAuth.getInstance();

        mTalent =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("Talent");

        mjobnotfoundtxt = (TextView) findViewById(R.id.jobnotfoundtxt);
        mnocategoryLay = (RelativeLayout) findViewById(R.id.nocategoryLay);
        mnoInternetLay = (RelativeLayout) findViewById(R.id.noInternetLay);
        mretryBtn = (CardView)findViewById(R.id.retryBtn);

        mnestedscroll = (NestedScrollView) findViewById(nestedscroll);

        ConnectivityManager cm = (ConnectivityManager) SearchTalent.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                Log.d(TAG, "connected to wifi");
                // connected to wifi
                mnoInternetLay.setVisibility(GONE);
                mnestedscroll.setVisibility(VISIBLE);

            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                Log.d(TAG, "connected to data");
                // connected to the mobile provider's data plan
                mnoInternetLay.setVisibility(GONE);
                mnestedscroll.setVisibility(VISIBLE);
            }
        } else {
            Log.d(TAG, "not connected");
            // not connected to the internet
            Toast.makeText(SearchTalent.this, "Network Not Available", Toast.LENGTH_LONG).show();
            mnoInternetLay.setVisibility(VISIBLE);
            mnestedscroll.setVisibility(GONE);

            mretryBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ConnectivityManager cm = (ConnectivityManager) SearchTalent.this.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                    if (activeNetwork != null) { // connected to the internet
                        if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                            Log.d(TAG, "connected to wifi");
                            // connected to wifi
                            mnoInternetLay.setVisibility(GONE);
                            mnestedscroll.setVisibility(VISIBLE);

                        } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                            Log.d(TAG, "connected to data");
                            // connected to the mobile provider's data plan
                            mnoInternetLay.setVisibility(GONE);
                            mnestedscroll.setVisibility(VISIBLE);
                        }
                    } else {
                        Log.d(TAG, "not connected");
                        // not connected to the internet
                        Toast.makeText(SearchTalent.this, "Network Not Available", Toast.LENGTH_LONG).show();
                        mnoInternetLay.setVisibility(VISIBLE);
                        mnestedscroll.setVisibility(GONE);
                    }
                }
            });
        }

        mJobList = (RecyclerView)findViewById(R.id.blog_list);
        mJobList.setHasFixedSize(false);
        mJobList.setNestedScrollingEnabled(false);

        mfilterLay = (LinearLayout) findViewById(R.id.filterLay);
        msortLay = (LinearLayout) findViewById(R.id.sortLay);
        msortLay.setVisibility(VISIBLE);
        msortLay2 = (LinearLayout) findViewById(R.id.sortLay2);
        msortLay2.setVisibility(VISIBLE);

        mfilterbycitytxt = (TextView)findViewById(R.id.filterbywagestxt);
        mfilterbycitytxt.setText(city);
        mfilterbycategorytxt = (TextView)findViewById(R.id.filterbydatetxt);
        mfilterbycategorytxt.setText(maincategory + " / " + subcategory);
        mfilterbyreviewtxt = (TextView)findViewById(R.id.filterbyreviewtxt);
        mfilterbyreviewtxt.setVisibility(VISIBLE);
        mfiltercitytxt = (TextView)findViewById(R.id.filtercitytxt);
        mfiltercitytxt.setVisibility(GONE);
        mfiltercitytxt2 = (TextView)findViewById(R.id.filtercitytxt2);
        mfiltercitytxt2.setVisibility(GONE);

        mfilterbycitytxt2 = (TextView)findViewById(R.id.filterbywagestxt2);
        mfilterbycitytxt2.setText(city);
        mfilterbycategorytxt2 = (TextView)findViewById(R.id.filterbydatetxt2);
        mfilterbycategorytxt2.setText(maincategory + " / " + subcategory);
        mfilterbyreviewtxt2 = (TextView)findViewById(R.id.filterbyreviewtxt2);
        mfilterbyreviewtxt2.setVisibility(VISIBLE);

        if (sortby.equals("reviewcount_negatedtime")) {
            mfilterbyreviewtxt.setText("Sort by Reviews");
            mfilterbyreviewtxt2.setText("Sort by Reviews");
        }
        else {
            mfilterbyreviewtxt.setText("Sort by Recent");
            mfilterbyreviewtxt2.setText("Sort by Recent");
        }

        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setReverseLayout(false);
        mLayoutManager.setStackFromEnd(false);
        talentlist = new ArrayList<TalentInfo>();
        recyclerAdapter = new SearchTalentRecyclerAdapter(mTalent,maincategory,subcategory,talentlist,SearchTalent.this);
        mJobList.setLayoutManager(mLayoutManager);
        mJobList.setAdapter(recyclerAdapter);

        msortCardView = (CardView)findViewById(R.id.sortCardView);

        nestedscrollListener();
        checktalentLocation();

        msortCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showsortfilter();
            }
        });
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
                    if (loading) {
                        loading = false;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                talentlist.remove(talentlist.size()-1);
                                recyclerAdapter.notifyItemRemoved(talentlist.size());
                                loadMoreOperation(city, loadlimit, sortby);

                            }
                        }, 750); //time seconds
                    }
                }
            }
        });
    }

    private void checktalentLocation(){

        if(city != null) {
            mTalent.child(city).child(maincategory).child(subcategory).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {
                        if (sortby.equals("reviewcount_negatedtime")) {
                            Log.d(TAG, "here1");
                            presentsearchjobpost("reviewcount_negatedtime");
                        }
                        else if (sortby.equals("negatedtime")) {
                            presentsearchjobpost("negatedtime");
                        }

                    }
                    else{
                        mnocategoryLay.setVisibility(View.VISIBLE);
                        mjobnotfoundtxt.setText("Sorry, it looks like there aren't any talents related to " + maincategory + " / " + subcategory + " available in " + city);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else {
            mnocategoryLay.setVisibility(View.VISIBLE);
            mjobnotfoundtxt.setText("Sorry, it looks like there aren't any jobs related to " + maincategory + " / " + subcategory + " available in " + city);
        }

    }

    private void presentsearchjobpost(String sortby){

        count = 0;

        mQuerySearch = mTalent.child(city).child(maincategory).child(subcategory).orderByChild(sortby);

        Log.d(TAG, "sortby " + sortby);

        mQuerySearch.limitToFirst(loadlimit).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot usersdataSnapshot : dataSnapshot.getChildren()){

                    TalentInfo talents = new TalentInfo();

                    String title = usersdataSnapshot.child("title").getValue().toString();
                    String postkey = usersdataSnapshot.getKey();
                    String city = usersdataSnapshot.child("city").getValue().toString();
                    String postimage = usersdataSnapshot.child("postimage").getValue().toString();
                    String reviewcount = usersdataSnapshot.child("reviewcount").getValue().toString();
                    String reviewstar = usersdataSnapshot.child("reviewstar").getValue().toString();
                    String verified = usersdataSnapshot.child("verified").getValue().toString();
                    nextnegatedtime = (Long)usersdataSnapshot.child("negatedtime").getValue();
                    reviewcount_nextnegatedtime = (Long)usersdataSnapshot.child("reviewcount_negatedtime").getValue();

                    if (usersdataSnapshot.hasChild("rates")) {
                        String rates = usersdataSnapshot.child("rates").getValue().toString();
                        talents.setrates(rates);
                    }

                    if (usersdataSnapshot.hasChild("dates")) {
                        String dates = usersdataSnapshot.child("dates").getValue().toString();
                        talents.setdates(dates);
                    }


                    talents.settitle(title);
                    talents.setcity(city);
                    talents.setpostkey(postkey);
                    talents.setpostimage(postimage);
                    talents.setstring_reviewcount(reviewcount);
                    talents.setstring_reviewstar(reviewstar);

                    count++;

                    if (verified.equals("true")) {
                        Log.d(TAG, "title: " + title);
                        talentlist.add(talents);
                    }

                    if (count == loadlimit) {
                        talentlist.add(null);
                        recyclerAdapter.notifyItemInserted(talentlist.size() - 1);
                    }

                }

                if (count < loadlimit){
                    loading = false;
                }
                else{
                    loading = true;
                }

                if (talentlist.isEmpty()) {
                    loading = false;
                    mnocategoryLay.setVisibility(View.VISIBLE);
                    mjobnotfoundtxt.setText("Sorry, it looks like there aren't any jobs related to " + maincategory + " / " + subcategory + " available in " + city);
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


    protected void loadMoreOperation(final String city, final int loadlimit, final String sortby){

        count = 0;

        if (sortby.equals("reviewcount_negatedtime")) {
            mQuerySearchMore = mTalent.child(city).child(maincategory).child(subcategory).orderByChild(sortby).limitToFirst(loadlimit+1).startAt(reviewcount_nextnegatedtime);
        }
        else if (sortby.equals("negatedtime")) {
            mQuerySearchMore = mTalent.child(city).child(maincategory).child(subcategory).orderByChild(sortby).limitToFirst(loadlimit+1).startAt(nextnegatedtime);
        }
        else{
            return;
        }

        mQuerySearchMore.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Boolean firstkey = true;
                for(DataSnapshot usersdataSnapshot1 : dataSnapshot.getChildren()){

                    TalentInfo talents = new TalentInfo();

                    String title = usersdataSnapshot1.child("title").getValue().toString();
                    String city = usersdataSnapshot1.child("city").getValue().toString();
                    String postkey = usersdataSnapshot1.getKey();
                    String postimage = usersdataSnapshot1.child("postimage").getValue().toString();
                    String reviewcount = usersdataSnapshot1.child("reviewcount").getValue().toString();
                    String reviewstar = usersdataSnapshot1.child("reviewstar").getValue().toString();
                    String verified = usersdataSnapshot1.child("verified").getValue().toString();
                    nextnegatedtime = (Long)usersdataSnapshot1.child("negatedtime").getValue();
                    reviewcount_nextnegatedtime = (Long)usersdataSnapshot1.child("reviewcount_negatedtime").getValue();

                    if (usersdataSnapshot1.hasChild("rates")) {
                        String rates = usersdataSnapshot1.child("rates").getValue().toString();
                        talents.setrates(rates);
                    }

                    if (usersdataSnapshot1.hasChild("dates")) {
                        String dates = usersdataSnapshot1.child("dates").getValue().toString();
                        talents.setdates(dates);
                    }

                    talents.settitle(title);
                    talents.setcity(city);
                    talents.setpostkey(postkey);
                    talents.setpostimage(postimage);
                    talents.setstring_reviewcount(reviewcount);
                    talents.setstring_reviewstar(reviewstar);

                    count++;
                    if (firstkey) {
                        firstkey = false;
                    } else {

                        if (verified.equals("true")) {
                            Log.d(TAG, "moretitle: " + title);
                            talentlist.add(talents);
                        }
                        if (count == loadlimit+1) {
                            talentlist.add(null);
                            recyclerAdapter.notifyItemInserted(talentlist.size() - 1);
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

    private void showsortfilter() {
        Intent filterintent = new Intent(SearchTalent.this, FilterTalent.class);
        filterintent.putExtra("sortby", sortby);
        filterintent.putExtra("maincategory", maincategory);
        filterintent.putExtra("subcategory", subcategory);
        filterintent.putExtra("city", city);
        startActivityForResult(filterintent, 1111);
        overridePendingTransition(R.anim.pullup,R.anim.nochange);
    }

    @Override
    protected void onDestroy() {
        if (mdialog != null && mdialog.isShowing()) {
            mdialog.dismiss();
        }

        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == 1111) && (resultCode == Activity.RESULT_OK)) {
            // recreate your fragment here
            maincategory = data.getStringExtra("maincategory");
            subcategory = data.getStringExtra("subcategory");
            sortby = data.getStringExtra("sortby");
            city = data.getStringExtra("city");

            mdialog = new ProgressDialog(SearchTalent.this, R.style.MyTheme);
            mdialog.setCancelable(false);
            mdialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
            mdialog.show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (!talentlist.isEmpty()) {talentlist.clear();}

                    Intent searchintent = new Intent(SearchTalent.this, SearchTalent.class);
                    searchintent.putExtra("city_id",city);
                    searchintent.putExtra("maincategory",maincategory);
                    searchintent.putExtra("subcategory",subcategory);
                    searchintent.putExtra("sortby",sortby);
                    startActivity(searchintent);
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    mdialog.dismiss();
                }
            }, 500); //time seconds
        }
        else if ((requestCode == 1111) && (resultCode == Activity.RESULT_CANCELED)) {

        }
    }

}
