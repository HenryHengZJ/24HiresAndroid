package com.zjheng.jobseed.jobseed.SearchJobScene;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.zjheng.jobseed.jobseed.CustomObjectClass.Job;
import com.zjheng.jobseed.jobseed.HomeScene.ExploreJobs.FilterJob;
import com.zjheng.jobseed.jobseed.R;
import com.zjheng.jobseed.jobseed.SearchTalentScene.SearchTalent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.zjheng.jobseed.jobseed.R.id.nestedscroll;

public class SearchCategory extends AppCompatActivity {

    private String category_title, city;

    private RecyclerView mJobList;
    private LinearLayoutManager mLayoutManager;
    private SearchRecyclerAdapter recyclerAdapter;
    private NestedScrollView mnestedscroll;

    private TextView mfilterbywagestxt, mfilterbydatetxt, mjobnotfoundtxt, mfilterbywagestxt2, mfilterbydatetxt2, mfiltercitytxt, mfiltercitytxt2 ;

    private RelativeLayout mnocategoryLay, mnoInternetLay;
    private LinearLayout mfilterLay;
    private CardView msortCardView;

    private FirebaseAuth mAuth;

    private DatabaseReference mJob, mUserSortFilter;
    private Query mQuerySearch, mQuerySearchMore;
    private CardView mretryBtn;

    private List<Job> joblist;

    private Long firstvariablechildcount;
    private String startingdatestring, endingdatestring,filterbystart = "", filterbyend = "", oldfilterbywages;
    private Long  category_firstposttime, variablechildcount,  category_mostrecent_startdate,  category_mostrecent_wagesrange,  category_mostrecent_wagesrange_startdate;
    private long startdate = 0 , enddate = 0, wagescategory = 0, filterbywages = 0;
    private long startwagesfilter, starttime;

    private static final String TAG = "SearchCategory";

    private int loadlimit = 8;
    private int count = 0;
    private int categorynum, scenario;

    private boolean loading = true;
    private Calendar myCalendar;

    private DatePickerDialog startdatePickerDialog;
    private DatePickerDialog enddatePickerDialog;

    private ProgressDialog mdialog;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        Log.d(TAG, "Search Results2");

        Intent intent = getIntent();
        if(intent.hasExtra("city_id")){
            if (getIntent().getExtras().getString("city_id") != null) {
                city = getIntent().getExtras().getString("city_id");
            }
        }
        if(intent.hasExtra("category_title")){
            if (getIntent().getExtras().getString("category_title") != null) {
                category_title = getIntent().getExtras().getString("category_title");
            }
        }

        Toolbar mToolbar = (Toolbar)findViewById(R.id.toolbar_other);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(category_title);
        mToolbar.setTitleTextColor(android.graphics.Color.WHITE);

        mdialog = new ProgressDialog(SearchCategory.this, R.style.MyTheme);
        mProgress = new ProgressDialog(this);

        mToolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white_24dp);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        switch (category_title) {
            case "Barista / Bartender":
                categorynum = 11;
                break;
            case "Beauty / Wellness":
                categorynum = 12;
                break;
            case "Chef / Kitchen Helper":
                categorynum = 13;
                break;
            case "Event Crew":
                categorynum = 14;
                break;
            case "Emcee":
                categorynum = 15;
                break;
            case "Education":
                categorynum = 16;
                break;
            case "Fitness / Gym":
                categorynum = 17;
                break;
            case "Modelling / Shooting":
                categorynum = 18;
                break;
            case "Mascot":
                categorynum = 19;
                break;
            case "Office / Admin":
                categorynum = 20;
                break;
            case "Promoter / Sampling":
                categorynum = 21;
                break;
            case "Roadshow":
                categorynum = 22;
                break;
            case "Roving Team":
                categorynum = 23;
                break;
            case "Retail / Consumer":
                categorynum = 24;
                break;
            case "Serving":
                categorynum = 25;
                break;
            case "Usher / Ambassador":
                categorynum = 26;
                break;
            case "Waiter / Waitress":
                categorynum = 27;
                break;
            case "Other":
                categorynum = 28;
                break;
        }

        mAuth = FirebaseAuth.getInstance();

        mJob =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("Job");

        mUserSortFilter =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("SortFilter");

        mjobnotfoundtxt = (TextView) findViewById(R.id.jobnotfoundtxt);
        mnocategoryLay = (RelativeLayout) findViewById(R.id.nocategoryLay);
        mnoInternetLay = (RelativeLayout) findViewById(R.id.noInternetLay);
        mretryBtn = (CardView)findViewById(R.id.retryBtn);
        mnestedscroll = (NestedScrollView) findViewById(nestedscroll);

        ConnectivityManager cm = (ConnectivityManager) SearchCategory.this.getSystemService(Context.CONNECTIVITY_SERVICE);
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
            Toast.makeText(SearchCategory.this, "Network Not Available", Toast.LENGTH_LONG).show();
            mnoInternetLay.setVisibility(VISIBLE);
            mnestedscroll.setVisibility(GONE);

            mretryBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ConnectivityManager cm = (ConnectivityManager) SearchCategory.this.getSystemService(Context.CONNECTIVITY_SERVICE);
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
                        Toast.makeText(SearchCategory.this, "Network Not Available", Toast.LENGTH_LONG).show();
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
        mfilterbywagestxt = (TextView)findViewById(R.id.filterbywagestxt);
        mfilterbydatetxt = (TextView)findViewById(R.id.filterbydatetxt);
        mfilterbywagestxt2 = (TextView)findViewById(R.id.filterbywagestxt2);
        mfilterbydatetxt2 = (TextView)findViewById(R.id.filterbydatetxt2);
        mfiltercitytxt = (TextView)findViewById(R.id.filtercitytxt);
        mfiltercitytxt2 = (TextView)findViewById(R.id.filtercitytxt2);
        mfiltercitytxt.setText(city);
        mfiltercitytxt2.setText(city);

        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setReverseLayout(false);
        mLayoutManager.setStackFromEnd(false);
        joblist = new ArrayList<Job>();
        recyclerAdapter = new SearchRecyclerAdapter(mJob,joblist,SearchCategory.this);
        mJobList.setLayoutManager(mLayoutManager);
        mJobList.setAdapter(recyclerAdapter);

        msortCardView = (CardView)findViewById(R.id.sortCardView);

        nestedscrollListener();

        mUserSortFilter.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    //Got start no end
                    if(dataSnapshot.hasChild("StartDate")
                            && !dataSnapshot.hasChild("EndDate")) {
                        filterbystart = dataSnapshot.child("StartDate").getValue().toString();
                        filterbyend = "";

                        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd", Locale.US);
                        Date dateFromString = null;
                        try {
                            dateFromString = sdf.parse(filterbystart);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        SimpleDateFormat sdf1 = new SimpleDateFormat("dd MMM yy", Locale.US);
                        String dateAsString = sdf1.format(dateFromString);

                        mfilterbydatetxt.setText(dateAsString);
                        mfilterbydatetxt.setVisibility(VISIBLE);
                        mfilterbydatetxt2.setText(dateAsString);
                        mfilterbydatetxt2.setVisibility(VISIBLE);

                        if(dataSnapshot.hasChild("OldWagesFilter")) {
                            //If user selected Show All
                            scenario = 11;
                            oldfilterbywages = "true";
                            filterbywages = (Long) dataSnapshot.child("OldWagesFilter").getValue();
                            checkuserLocation(filterbystart, filterbyend, filterbywages, scenario, categorynum);

                            mfilterbywagestxt.setVisibility(GONE);
                            mfilterbywagestxt2.setVisibility(GONE);
                        }
                        else if(dataSnapshot.hasChild("WagesFilter")){
                            //If user selected Wages Filter
                            scenario = 1;
                            oldfilterbywages = "false";
                            filterbywages = (Long)dataSnapshot.child("WagesFilter").getValue();
                            checkuserLocation(filterbystart,filterbyend, filterbywages, scenario, categorynum);

                            String finalwagestext = filterbywagestext(filterbywages);

                            mfilterbywagestxt.setText(finalwagestext);
                            mfilterbywagestxt.setVisibility(VISIBLE);
                            mfilterbywagestxt2.setText(finalwagestext);
                            mfilterbywagestxt2.setVisibility(VISIBLE);
                        }
                        else{
                            scenario = 11;
                            filterbywages = 1111; //defauly value = MYR + per hour + Less Than 5
                            oldfilterbywages = "true";
                            checkuserLocation(filterbystart,filterbyend, filterbywages, scenario, categorynum);

                            mfilterbywagestxt.setVisibility(GONE);
                            mfilterbywagestxt2.setVisibility(GONE);
                        }
                    }
                    //Got start got end
                    else if(dataSnapshot.hasChild("StartDate")
                            && dataSnapshot.hasChild("EndDate")) {
                        filterbystart = dataSnapshot.child("StartDate").getValue().toString();
                        filterbyend = dataSnapshot.child("EndDate").getValue().toString();

                        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd", Locale.US);
                        SimpleDateFormat sdf1 = new SimpleDateFormat("dd MMM yy", Locale.US);
                        Date startdateFromString = null;
                        try {
                            startdateFromString = sdf.parse(filterbystart);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        String startdateAsString = sdf1.format(startdateFromString);

                        Date enddateFromString = null;
                        try {
                            enddateFromString = sdf.parse(filterbyend);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        String enddateAsString = sdf1.format(enddateFromString);

                        mfilterbydatetxt.setText(startdateAsString + " to " + enddateAsString);
                        mfilterbydatetxt.setVisibility(VISIBLE);
                        mfilterbydatetxt2.setText(startdateAsString + " to " + enddateAsString);
                        mfilterbydatetxt2.setVisibility(VISIBLE);

                        if(dataSnapshot.hasChild("OldWagesFilter")){
                            //If user selected Show All Wages
                            scenario = 22;
                            oldfilterbywages = "true";
                            filterbywages = (Long)dataSnapshot.child("OldWagesFilter").getValue();
                            checkuserLocation(filterbystart,filterbyend, filterbywages, scenario, categorynum);

                            mfilterbywagestxt.setVisibility(GONE);
                            mfilterbywagestxt2.setVisibility(GONE);
                        }

                        else if(dataSnapshot.hasChild("WagesFilter")){
                            //If user selected Wages Filter
                            scenario = 2;
                            oldfilterbywages = "false";
                            filterbywages = (Long)dataSnapshot.child("WagesFilter").getValue();
                            checkuserLocation(filterbystart,filterbyend, filterbywages, scenario, categorynum);

                            String finalwagestext = filterbywagestext(filterbywages);

                            mfilterbywagestxt.setText(finalwagestext);
                            mfilterbywagestxt.setVisibility(VISIBLE);
                            mfilterbywagestxt2.setText(finalwagestext);
                            mfilterbywagestxt2.setVisibility(VISIBLE);

                        }
                        else{
                            scenario = 22;
                            filterbywages = 1111; //defauly value = MYR + per hour + Less Than 5
                            oldfilterbywages = "true";
                            checkuserLocation(filterbystart,filterbyend, filterbywages, scenario, categorynum);

                            mfilterbywagestxt.setVisibility(GONE);
                            mfilterbywagestxt2.setVisibility(GONE);
                        }
                    }
                    //No start no end
                    else{
                        filterbystart = "";
                        filterbyend = "";

                        mfilterbydatetxt.setVisibility(GONE);
                        mfilterbydatetxt2.setVisibility(GONE);

                        if(dataSnapshot.hasChild("OldWagesFilter")){
                            //If user selected Show All Wages
                            scenario = 33;
                            oldfilterbywages = "true";
                            filterbywages = (Long)dataSnapshot.child("OldWagesFilter").getValue();
                            checkuserLocation(filterbystart,filterbyend, filterbywages, scenario, categorynum);
                            mfilterbywagestxt.setText("None");
                            mfilterbywagestxt.setVisibility(VISIBLE);
                            mfilterbywagestxt2.setText("None");
                            mfilterbywagestxt2.setVisibility(VISIBLE);
                        }

                        else if(dataSnapshot.hasChild("WagesFilter")){
                            //If user selected Wages Filter
                            scenario = 3;
                            oldfilterbywages = "false";
                            filterbywages = (Long)dataSnapshot.child("WagesFilter").getValue();
                            checkuserLocation(filterbystart,filterbyend, filterbywages, scenario, categorynum);

                            String finalwagestext = filterbywagestext(filterbywages);
                            mfilterbywagestxt.setText(finalwagestext);
                            mfilterbywagestxt.setVisibility(VISIBLE);
                            mfilterbywagestxt2.setText(finalwagestext);
                            mfilterbywagestxt2.setVisibility(VISIBLE);
                        }

                        else{
                            scenario = 33;
                            oldfilterbywages = "true";
                            filterbywages = 1111; //defauly value = MYR + per hour + Less Than 5
                            checkuserLocation(filterbystart,filterbyend, filterbywages, scenario, categorynum);
                            mfilterbywagestxt.setText("None");
                            mfilterbywagestxt.setVisibility(VISIBLE);
                            mfilterbywagestxt2.setText("None");
                            mfilterbywagestxt2.setVisibility(VISIBLE);
                        }
                    }
                }
                else{
                    scenario = 33;
                    filterbystart = "";
                    filterbyend = "";
                    oldfilterbywages = "true";
                    filterbywages = 1111; //defauly value = MYR + per hour + Less Than 5
                    checkuserLocation(filterbystart,filterbyend, filterbywages, scenario, categorynum);

                    mfilterbywagestxt.setText("None");
                    mfilterbywagestxt.setVisibility(VISIBLE);
                    mfilterbywagestxt2.setText("None");
                    mfilterbywagestxt2.setVisibility(VISIBLE);

                    mfilterbydatetxt.setVisibility(GONE);
                    mfilterbydatetxt2.setVisibility(GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /*mJobList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                //super.onScrolled(recyclerView, dx, dy);

                if(dy > 0) {

                    if(firstvariablechildcount!= null){
                        if(firstvariablechildcount>loadlimit){
                            //Scroll up
                            if(mfilterLay.getVisibility() == VISIBLE) {
                                Animation slide_up = AnimationUtils.loadAnimation(SearchCategory.this,
                                        R.anim.slide_up);

                                // Start animation
                                mfilterLay.startAnimation(slide_up);
                                mfilterLay.setVisibility(GONE);
                            }

                            int lastVisibleItems = mLayoutManager.findLastVisibleItemPosition();

                            if (lastVisibleItems == joblist.size() - 1) {
                                Log.d(TAG, "at btm");
                                if(firstposttime!=null) {

                                    if(variablechildcount!=null){


                                    }
                                }
                            }
                        }


                    }
                }
                else if(dy < 0){
                    //Scroll down

                    //Load animation
                    mfilterLay.setVisibility(VISIBLE);
                    Animation slide_down = AnimationUtils.loadAnimation(SearchCategory.this,
                            R.anim.slide_down);

                    // Start animation
                    mfilterLay.startAnimation(slide_down);

                }
            }
        });*/

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
                    if(category_firstposttime!=null) {
                        Log.d(TAG, "loading null: " + loading);
                        if (loading) {
                            loading = false;
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    joblist.remove(joblist.size()-1);
                                    recyclerAdapter.notifyItemRemoved(joblist.size());
                                    loadMoreOperation(city, loadlimit, scenario);

                                }
                            }, 750); //time seconds
                        }
                    }
                }
            }
        });
    }

    private void checkuserLocation(final String filterbystart,final String filterbyend,final long filterbywages,final int scenario, final int categorynum){

        if(city != null) {
            mJob.child(city).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {
                        presentsearchjobpost(filterbystart, filterbyend, filterbywages, scenario, categorynum);
                    }
                    else{
                        mnocategoryLay.setVisibility(View.VISIBLE);
                        mjobnotfoundtxt.setText("Sorry, it looks like there aren't any jobs related to " + category_title + " available in " + city);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else {
            mnocategoryLay.setVisibility(View.VISIBLE);
            mjobnotfoundtxt.setText("Sorry, it looks like there aren't any jobs related to " + category_title + " available in " + city);
        }

    }

    private void presentsearchjobpost(final String filterbystart,final String filterbyend, final long filterbywages, final int scenario, final int categorynum){

        count = 0;

        int intwages = 0, currencyint = 0, wagescategoryint = 0;

        if(filterbywages!=0){
            //get currency number
            intwages = (int) (filterbywages % 100000);
            currencyint = Integer.parseInt(Integer.toString(intwages).substring(0, 2));

            //get wagescateogry number
            wagescategoryint = intwages % 100;
        }

        Long tsLong = System.currentTimeMillis()/1000;
        if(!filterbystart.equals("")){
            startdate =  Long.valueOf(filterbystart);
        }
        if(!filterbyend.equals("")){
            enddate =  Long.valueOf(filterbyend);
        }

        //Got start no end
        if(scenario == 1){
            //Got WagesFilter

            starttime = -1*((startdate* 100000000L)+(wagescategoryint * 100000000000000L)+((currencyint-11) * 10000000000000000L)+(categorynum * 100000000000000000L));
            long endtime = -1*((tsLong)+(999999* 100000000L)+(wagescategoryint * 100000000000000L)+((currencyint-11) * 10000000000000000L)+(categorynum * 100000000000000000L));

            Log.d(TAG,"starttime1 " + starttime );
            Log.d(TAG,"endtime1 " + endtime );

            mQuerySearch = mJob.child(city).orderByChild("category_mostrecent_wagesrange_startdate").startAt(endtime).endAt(starttime);

        }

        else if (scenario == 11){
            //No WagesFilter

            starttime = -1*((startdate* 10000000000L)+(categorynum * 10000000000000000L));
            long endtime = -1*((tsLong)+(999999* 10000000000L)+(categorynum * 10000000000000000L));

            Log.d(TAG,"starttime11 " + starttime );
            Log.d(TAG,"endtime11 " + endtime );

            mQuerySearch = mJob.child(city).orderByChild("category_mostrecent_startdate").startAt(endtime).endAt(starttime);
        }

        //Got start got end
        else if (scenario == 2){
            //Got WagesFilter

            long endtime = -1*((tsLong%100000000)+(enddate* 100000000L)+(wagescategoryint * 100000000000000L)+((currencyint-11) * 10000000000000000L)+(categorynum * 100000000000000000L));
            starttime = -1*((startdate* 100000000L)+(wagescategoryint * 100000000000000L)+((currencyint-11) * 10000000000000000L)+(categorynum * 100000000000000000L));

            Log.d(TAG,"currencyint2 " + currencyint );
            Log.d(TAG,"intwages2 " + intwages );
            Log.d(TAG,"endtime2 " + endtime );
            Log.d(TAG,"starttime2 " + starttime );
            Log.d(TAG,"categorynum2 " + categorynum );

            mQuerySearch = mJob.child(city).orderByChild("category_mostrecent_wagesrange_startdate").startAt(endtime).endAt(starttime);
        }

        else if (scenario == 22){
            //No WagesFilter

            starttime = -1*((startdate* 10000000000L)+(categorynum * 10000000000000000L));
            long endtime = -1*((tsLong)+(enddate* 10000000000L)+(categorynum * 10000000000000000L));

            Log.d(TAG,"endtime22 " + endtime );
            Log.d(TAG,"starttime22 " + starttime );

            mQuerySearch = mJob.child(city).orderByChild("category_mostrecent_startdate").startAt(endtime).endAt(starttime);
        }

        //No start no end
        else if (scenario == 3){
            //Got WagesFilter

            long currencylong = Long.valueOf(currencyint)*1000000000000L;

            long wagescategorylong = Long.valueOf(wagescategoryint)*10000000000L;

            long categorylong = categorynum * 100000000000000L;

            long endwagesfilter = -1*(tsLong+wagescategorylong+currencylong+categorylong);
            startwagesfilter = -1*(wagescategorylong+currencylong+categorylong);

            Log.d(TAG,"currencylong " + currencylong );
            Log.d(TAG,"wagescategorylong " + wagescategorylong );
            Log.d(TAG,"endwagesfilter " + endwagesfilter );
            Log.d(TAG,"startwagesfilter " + startwagesfilter );

            mQuerySearch = mJob.child(city).orderByChild("category_mostrecent_wagesrange").startAt(endwagesfilter).endAt(startwagesfilter);
        }

        else if (scenario == 33 || scenario == 0){
            //No WagesFilter

            long categorylong = categorynum * 10000000000L;

            long starttime = -1*(tsLong+categorylong);
            long endtime = -1*(categorylong);

            mQuerySearch = mJob.child(city).orderByChild("category_negatedtime").startAt(starttime).endAt(endtime);
        }
        else{
            return;
        }

        mQuerySearch.limitToFirst(loadlimit).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot usersdataSnapshot : dataSnapshot.getChildren()){

                    Job jobs = new Job();

                    String title = usersdataSnapshot.child("title").getValue().toString();
                    String desc = usersdataSnapshot.child("desc").getValue().toString();
                    String category = usersdataSnapshot.child("category").getValue().toString();
                    String company = usersdataSnapshot.child("company").getValue().toString();
                    String fulladdress = usersdataSnapshot.child("fulladdress").getValue().toString();
                    String postimage = usersdataSnapshot.child("postimage").getValue().toString();
                    String postkey = usersdataSnapshot.child("postkey").getValue().toString();
                    String city = usersdataSnapshot.child("city").getValue().toString();
                    String closed = usersdataSnapshot.child("closed").getValue().toString();
                    category_firstposttime = (Long) usersdataSnapshot.child("category_negatedtime").getValue();
                    category_mostrecent_startdate = (Long) usersdataSnapshot.child("category_mostrecent_startdate").getValue();
                    category_mostrecent_wagesrange = (Long) usersdataSnapshot.child("category_mostrecent_wagesrange").getValue();
                    category_mostrecent_wagesrange_startdate = (Long) usersdataSnapshot.child("category_mostrecent_wagesrange_startdate").getValue();

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

                    if (category.equals(category_title)) {
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
                    mjobnotfoundtxt.setText("Sorry, it looks like there aren't any jobs related to " + category_title + " available in " + city);
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

    private String filterbywagestext(long filterbywages){

        long newwagescategory = filterbywages % 100;
        String wagescat = null, currency = null;

        if(newwagescategory == 11){ wagescat = "Less than 5";}
        else if(newwagescategory == 12){ wagescat =  "5 to 10";}
        else if(newwagescategory == 13){ wagescat = "11 to 20";}
        else if(newwagescategory == 14){ wagescat = "21 to 50";}
        else if(newwagescategory == 15){ wagescat = "More than 50";}
        else if(newwagescategory == 21){ wagescat ="Less than 70";}
        else if(newwagescategory == 22){ wagescat ="70 to 100";}
        else if(newwagescategory == 23){ wagescat ="101 to 200";}
        else if(newwagescategory == 24){ wagescat ="201 to 500";}
        else if(newwagescategory == 25){ wagescat ="More than 500";}
        else if(newwagescategory == 31){ wagescat ="Less than 1000";}
        else if(newwagescategory == 32){ wagescat ="1000 to 1500";}
        else if(newwagescategory == 33){ wagescat ="1500 to 2000"; }
        else if(newwagescategory == 34){ wagescat ="2000 to 5000";}
        else if(newwagescategory == 35){ wagescat ="More than 5000";}
        else{ wagescat ="None";}

        //get currency number
        int intwages = (int) (filterbywages % 100000);
        int currencyint = Integer.parseInt(Integer.toString(intwages).substring(0, 2));
        if(currencyint == 11){ currency = "MYR";}
        else if(currencyint == 12){ currency =  "SGD";}
        else if(currencyint == 13){ currency = "CHY";}
        else if(currencyint == 14){ currency = "USD";}
        else if(currencyint == 15){ currency = "GBP";}
        else if(currencyint == 16){ currency ="EUR";}
        else if(currencyint == 17){ currency ="NTD";}
        else if(currencyint == 18){ currency ="HKD";}
        else if(currencyint == 19){ currency ="INR";}
        else if(currencyint == 20){ currency ="IDR";}

        String finalwages = "(" + currency + ") " + wagescat;

        return finalwages;
    }

    protected void loadMoreOperation(final String city, final int loadlimit, final int scenario){

        count = 0;

        //Got start no end
        if(scenario == 1){
            //Got WagesFilter
            Log.d(TAG,"category_mostrecent_wagesrange_startdate1 " + category_mostrecent_wagesrange_startdate );

            mQuerySearchMore = mJob.child(city).orderByChild("category_mostrecent_wagesrange_startdate").limitToFirst(loadlimit+1).startAt(category_mostrecent_wagesrange_startdate).endAt(starttime);
        }
        else if (scenario == 11){
            //No WagesFilter
            Log.d(TAG,"category_mostrecent_startdate1 " + category_mostrecent_startdate );

            mQuerySearchMore = mJob.child(city).orderByChild("category_mostrecent_startdate").limitToFirst(loadlimit+1).startAt(category_mostrecent_startdate).endAt(starttime);
        }

        //Got start got end
        else if (scenario == 2){
            //Got WagesFilter
            Log.d(TAG,"category_mostrecent_wagesrange_startdate2 " + category_mostrecent_wagesrange_startdate );

            mQuerySearchMore = mJob.child(city).orderByChild("category_mostrecent_wagesrange_startdate").limitToFirst(loadlimit+1).startAt(category_mostrecent_wagesrange_startdate).endAt(starttime);
        }

        else if (scenario == 22){
            //No WagesFilter
            Log.d(TAG,"category_mostrecent_startdate22 " + category_mostrecent_startdate );

            mQuerySearchMore = mJob.child(city).orderByChild("category_mostrecent_startdate").limitToFirst(loadlimit+1).startAt(category_mostrecent_startdate).endAt(starttime);
        }

        //No start no end
        else if (scenario == 3){
            //Got WagesFilter
            Log.d(TAG,"category_mostrecent_wagesrange3 " + category_mostrecent_wagesrange );

            mQuerySearchMore = mJob.child(city).orderByChild("category_mostrecent_wagesrange").limitToFirst(loadlimit+1).startAt(category_mostrecent_wagesrange).endAt(startwagesfilter);
        }

        else if (scenario == 33 || scenario == 0){
            //No WagesFilter
            Log.d(TAG,"category_negatedtime33 " + category_firstposttime );

            mQuerySearchMore = mJob.child(city).orderByChild("category_negatedtime").limitToFirst(loadlimit+1).startAt(category_firstposttime);
        }
        else{
            return;
        }

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
                            && usersdataSnapshot1.hasChild("category_negatedtime")) {

                        String title = usersdataSnapshot1.child("title").getValue().toString();
                        Log.d(TAG, "loadmoretitle: " + title );
                        String desc = usersdataSnapshot1.child("desc").getValue().toString();
                        String category = usersdataSnapshot1.child("category").getValue().toString();
                        String company = usersdataSnapshot1.child("company").getValue().toString();
                        String fulladdress = usersdataSnapshot1.child("fulladdress").getValue().toString();
                        String postimage = usersdataSnapshot1.child("postimage").getValue().toString();
                        String postkey = usersdataSnapshot1.child("postkey").getValue().toString();
                        String city = usersdataSnapshot1.child("city").getValue().toString();
                        String closed = usersdataSnapshot1.child("closed").getValue().toString();
                        category_firstposttime = (Long) usersdataSnapshot1.child("category_negatedtime").getValue();
                        Log.d(TAG, "more category_firstposttime: " + category_firstposttime);
                        category_mostrecent_startdate = (Long) usersdataSnapshot1.child("category_mostrecent_startdate").getValue();
                        category_mostrecent_wagesrange = (Long) usersdataSnapshot1.child("category_mostrecent_wagesrange").getValue();
                        category_mostrecent_wagesrange_startdate = (Long) usersdataSnapshot1.child("category_mostrecent_wagesrange_startdate").getValue();

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

                        if (category.equals(category_title)){
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

        Intent filterintent = new Intent(SearchCategory.this, FilterJob.class);
        filterintent.putExtra("oldfilterbywages", oldfilterbywages);
        filterintent.putExtra("filterbywages", filterbywages);
        filterintent.putExtra("filterbystart", filterbystart);
        filterintent.putExtra("filterbyend", filterbyend);
        filterintent.putExtra("city", city);
        startActivityForResult(filterintent, 8888);
        overridePendingTransition(R.anim.pullup,R.anim.nochange);

        /*final DatabaseReference newSortFilter = mUserSortFilter.child(mAuth.getCurrentUser().getUid());
        // custom dialog

        final Dialog dialog = new Dialog(SearchCategory.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.sortdialog);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(lp);

        final CardView mallrangeCardView = (CardView) dialog.findViewById(R.id.allrangeCardView);
        final CardView mspecificrangeCardView = (CardView) dialog.findViewById(R.id.specificrangeCardView);
        final ImageView mallrangetick = (ImageView) dialog.findViewById(R.id.allrangetick);
        final ImageView mwagesrangetick = (ImageView) dialog.findViewById(R.id.wagesrangetick);
        final RelativeLayout mblockLay = (RelativeLayout) dialog.findViewById(R.id.blockLay);

        final CardView mfilterbystartdate_cardview = (CardView) dialog.findViewById(R.id.sortbystartdate_cardview);
        final ImageView mstartingdate_tickimg = (ImageView) dialog.findViewById(R.id.startingdate_tickimg);
        final TextView mstartdatetxt = (TextView) dialog.findViewById(R.id.startdatetxt);

        final CardView mfilterbyenddate_cardview = (CardView) dialog.findViewById(R.id.sortbyenddate_cardview);
        final ImageView mendingdate_tickimg = (ImageView) dialog.findViewById(R.id.endingdate_tickimg);
        final TextView menddatetxt = (TextView) dialog.findViewById(R.id.enddatetxt);

        final EditText mratetxt = (EditText) dialog.findViewById(R.id.ratetxt);
        final Spinner mspinnerrate = (Spinner) dialog.findViewById(R.id.spinnerrate);
        final Spinner mspinnercurrency = (Spinner) dialog.findViewById(R.id.spinnercurrency);
        final SeekBar mpriceBar = (SeekBar)dialog.findViewById(R.id.priceBar);
        mpriceBar.setMax(4);

        final Button mclearBtn = (Button) dialog.findViewById(R.id.clearBtn);
        final Button mapplyBtn = (Button) dialog.findViewById(R.id.applyBtn);

        String[] items = new String[]{"per hour", "per day", "per month"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SearchCategory.this, android.R.layout.simple_spinner_dropdown_item, items);
        mspinnerrate.setAdapter(adapter);

        String[] items2 = new String[]{"MYR", "SGD", "CHY", "USD", "GBP", "EUR", "NTD","HKD","INR","IDR"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(SearchCategory.this, android.R.layout.simple_spinner_dropdown_item, items2);
        mspinnercurrency.setAdapter(adapter2);

        if(oldfilterbywages.equals("true")){
            mblockLay.setVisibility(VISIBLE);
            mallrangetick.setImageResource(R.drawable.single_tick);
            mwagesrangetick.setImageResource(R.color.buttonTextColor);
        }
        else{
            mwagesrangetick.setImageResource(R.drawable.single_tick);
            mallrangetick.setImageResource(R.color.buttonTextColor);
            mblockLay.setVisibility(GONE);
        }

        if(filterbywages!=0){

            long newwagescategory = filterbywages % 100;

            if(newwagescategory == 11){ mspinnerrate.setSelection(0); mratetxt.setText("Less than 5"); mpriceBar.setProgress(0); wagescategory = 11;}
            else if(newwagescategory == 12){ mspinnerrate.setSelection(0); mratetxt.setText("5 to 10"); mpriceBar.setProgress(1); wagescategory = 12;}
            else if(newwagescategory == 13){ mspinnerrate.setSelection(0); mratetxt.setText("11 to 20"); mpriceBar.setProgress(2); wagescategory = 13; }
            else if(newwagescategory == 14){ mspinnerrate.setSelection(0); mratetxt.setText("21 to 50"); mpriceBar.setProgress(3); wagescategory = 14;}
            else if(newwagescategory == 15){ mspinnerrate.setSelection(0); mratetxt.setText("More than 50"); mpriceBar.setProgress(4); wagescategory = 15;}
            else if(newwagescategory == 21){ mspinnerrate.setSelection(1); mratetxt.setText("Less than 70"); mpriceBar.setProgress(0); wagescategory = 21;}
            else if(newwagescategory == 22){ mspinnerrate.setSelection(1); mratetxt.setText("70 to 100"); mpriceBar.setProgress(1); wagescategory = 22;}
            else if(newwagescategory == 23){ mspinnerrate.setSelection(1); mratetxt.setText("101 to 200"); mpriceBar.setProgress(2); wagescategory = 23;}
            else if(newwagescategory == 24){ mspinnerrate.setSelection(1); mratetxt.setText("201 to 500"); mpriceBar.setProgress(3); wagescategory = 24;}
            else if(newwagescategory == 25){ mspinnerrate.setSelection(1); mratetxt.setText("More than 500"); mpriceBar.setProgress(4); wagescategory = 25;}
            else if(newwagescategory == 31){ mspinnerrate.setSelection(2); mratetxt.setText("Less than 1000"); mpriceBar.setProgress(0); wagescategory = 31;}
            else if(newwagescategory == 32){ mspinnerrate.setSelection(2); mratetxt.setText("1000 to 1500"); mpriceBar.setProgress(1); wagescategory = 32;}
            else if(newwagescategory == 33){ mspinnerrate.setSelection(2); mratetxt.setText("1500 to 2000"); mpriceBar.setProgress(2); wagescategory = 33;}
            else if(newwagescategory == 34){ mspinnerrate.setSelection(2); mratetxt.setText("2000 to 5000"); mpriceBar.setProgress(3); wagescategory = 34;}
            else if(newwagescategory == 35){ mspinnerrate.setSelection(2); mratetxt.setText("More than 5000"); mpriceBar.setProgress(4); wagescategory = 35;}

            //get currency number
            int intwages = (int) (filterbywages % 100000);
            int currencyint = Integer.parseInt(Integer.toString(intwages).substring(0, 2)) - 11;
            mspinnercurrency.setSelection(currencyint);
        }

        if(!filterbystart.equals("")){
            startingdatestring = filterbystart;
            SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd", Locale.US);
            Date dateFromString = null;
            try {
                dateFromString = sdf.parse(filterbystart);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            SimpleDateFormat sdf1 = new SimpleDateFormat("dd MMMM yy", Locale.US);
            String dateAsString = sdf1.format(dateFromString);
            mstartdatetxt.setText(dateAsString);
            mstartingdate_tickimg.setVisibility(VISIBLE);
            mstartdatetxt.setTextColor(Color.parseColor("#008fee"));
        }
        else{
            mstartdatetxt.setText("");
            mstartdatetxt.setHint("Start Date");
            mstartingdate_tickimg.setVisibility(GONE);
            mstartdatetxt.setTextColor(Color.BLACK);
        }

        if(!filterbyend.equals("")){
            endingdatestring = filterbyend;
            SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd", Locale.US);
            Date dateFromString = null;
            try {
                dateFromString = sdf.parse(filterbyend);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            SimpleDateFormat sdf1 = new SimpleDateFormat("dd MMMM yy", Locale.US);
            String dateAsString = sdf1.format(dateFromString);
            menddatetxt.setText(dateAsString);
            mendingdate_tickimg.setVisibility(VISIBLE);
            menddatetxt.setTextColor(Color.parseColor("#008fee"));
        }
        else{
            menddatetxt.setText("");
            menddatetxt.setHint("End Date");
            mendingdate_tickimg.setVisibility(GONE);
            menddatetxt.setTextColor(Color.BLACK);
        }

        mallrangeCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mallrangetick.setImageResource(R.drawable.single_tick);
                mwagesrangetick.setImageResource(R.color.buttonTextColor);
                mblockLay.setVisibility(VISIBLE);

                oldfilterbywages = "true";
            }
        });

        mspecificrangeCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mwagesrangetick.setImageResource(R.drawable.single_tick);
                mallrangetick.setImageResource(R.color.buttonTextColor);
                mblockLay.setVisibility(GONE);

                oldfilterbywages = "false";
            }
        });

        mpriceBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (mspinnerrate.getSelectedItemPosition() == 0){
                    if(progress==0){mratetxt.setText("Less than 5"); wagescategory = 11;}
                    else if(progress==1){mratetxt.setText("5 to 10");wagescategory = 12;}
                    else if(progress==2){mratetxt.setText("11 to 20");wagescategory = 13;}
                    else if(progress==3){mratetxt.setText("21 to 50");wagescategory = 14;}
                    else if(progress==4){mratetxt.setText("More than 50");wagescategory = 15;}
                }
                else if (mspinnerrate.getSelectedItemPosition() == 1){
                    if(progress==0){mratetxt.setText("Less than 70");wagescategory = 21;}
                    else if(progress==1){mratetxt.setText("70 to 100");wagescategory = 22;}
                    else if(progress==2){mratetxt.setText("101 to 200");wagescategory = 23;}
                    else if(progress==3){mratetxt.setText("201 to 500");wagescategory = 24;}
                    else if(progress==4){mratetxt.setText("More than 500");wagescategory = 25;}
                }
                else if (mspinnerrate.getSelectedItemPosition() == 2){
                    if(progress==0){mratetxt.setText("Less than 1000");wagescategory = 31;}
                    else if(progress==1){mratetxt.setText("1000 to 1500");wagescategory = 32;}
                    else if(progress==2){mratetxt.setText("1500 to 2000");wagescategory = 33;}
                    else if(progress==3){mratetxt.setText("2000 to 5000");wagescategory = 34;}
                    else if(progress==4){mratetxt.setText("More than 5000");wagescategory = 35;}
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mspinnerrate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                if(position == 0){
                    int value = mpriceBar.getProgress();
                    if(value == 0){ mratetxt.setText("Less than 5"); wagescategory = 11; }
                    else if(value == 1){ mratetxt.setText("5 to 10"); wagescategory = 12; }
                    else if(value == 2){ mratetxt.setText("11 to 20"); wagescategory = 13; }
                    else if(value == 3){ mratetxt.setText("21 to 50"); wagescategory = 14; }
                    else if(value == 4){ mratetxt.setText("More than 50"); wagescategory = 15; }
                }
                else if(position == 1){
                    int value = mpriceBar.getProgress();
                    if(value == 0){ mratetxt.setText("Less than 70"); wagescategory = 21; }
                    else if(value == 1){ mratetxt.setText("70 to 100"); wagescategory = 22; }
                    else if(value == 2){ mratetxt.setText("101 to 200"); wagescategory = 23; }
                    else if(value == 3){ mratetxt.setText("201 to 500"); wagescategory = 24; }
                    else if(value == 4){ mratetxt.setText("More than 500"); wagescategory = 25; }
                }
                else if(position == 2){
                    int value = mpriceBar.getProgress();
                    if(value == 0){ mratetxt.setText("Less than 1000"); wagescategory = 31; }
                    else if(value == 1){ mratetxt.setText("1000 to 1500"); wagescategory = 32; }
                    else if(value == 2){ mratetxt.setText("1500 to 2000"); wagescategory = 33; }
                    else if(value == 3){ mratetxt.setText("2000 to 5000"); wagescategory = 34; }
                    else if(value == 4){ mratetxt.setText("More than 5000"); wagescategory = 35; }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        myCalendar = Calendar.getInstance();
        final String myFormat = "dd MMMM yy"; //In which you need put here
        final SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                mstartdatetxt.setText(sdf.format(myCalendar.getTime()));
                mstartingdate_tickimg.setVisibility(VISIBLE);
                mstartdatetxt.setTextColor(Color.parseColor("#008fee"));

                String myFormat2 = "yyMMdd"; //In which you need put here
                SimpleDateFormat sdf2 = new SimpleDateFormat(myFormat2, Locale.US);
                startingdatestring = sdf2.format(myCalendar.getTime());
            }

        };

        final DatePickerDialog.OnDateSetListener enddate = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                menddatetxt.setText(sdf.format(myCalendar.getTime()));
                mendingdate_tickimg.setVisibility(VISIBLE);
                menddatetxt.setTextColor(Color.parseColor("#008fee"));

                String myFormat2 = "yyMMdd"; //In which you need put here
                SimpleDateFormat sdf2 = new SimpleDateFormat(myFormat2, Locale.US);
                endingdatestring = sdf2.format(myCalendar.getTime());
            }

        };

        mfilterbystartdate_cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startdatePickerDialog = new DatePickerDialog(SearchCategory.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                startdatePickerDialog.getDatePicker().setMinDate(myCalendar.getTimeInMillis());

                if(!filterbystart.equals("")){

                    SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd", Locale.US);

                    Date dateFromString = null;
                    try {
                        dateFromString = sdf.parse(filterbystart);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    SimpleDateFormat sdf1 = new SimpleDateFormat("ddMMyyyy", Locale.US);
                    String dateAsString = sdf1.format(dateFromString);

                    String year = dateAsString.substring(4, 8);
                    int startyear = Integer.parseInt(year);

                    String month = dateAsString.substring(2, 4);
                    int startmonth = Integer.parseInt(month) - 1;
                    Log.d(TAG, "startmonth "+startmonth);

                    String day = dateAsString.substring(0, 2);
                    int startday = Integer.parseInt(day);

                    startdatePickerDialog.updateDate(startyear,startmonth,startday);
                    startdatePickerDialog.getDatePicker().setMinDate(myCalendar.getTimeInMillis());
                }
                startdatePickerDialog.show();
            }
        });

        mfilterbyenddate_cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                enddatePickerDialog = new DatePickerDialog(SearchCategory.this, enddate, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                enddatePickerDialog.getDatePicker().setMinDate(myCalendar.getTimeInMillis());

                if(!filterbyend.equals("")){
                    SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd", Locale.US);

                    Date dateFromString = null;
                    try {
                        dateFromString = sdf.parse(filterbyend);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    SimpleDateFormat sdf1 = new SimpleDateFormat("ddMMyyyy", Locale.US);
                    String dateAsString = sdf1.format(dateFromString);

                    String year = dateAsString.substring(4, 8);
                    int endyear = Integer.parseInt(year);

                    String month = dateAsString.substring(2, 4);
                    int endmonth = Integer.parseInt(month) - 1;
                    Log.d(TAG, "endmonth "+endmonth);

                    String day = dateAsString.substring(0, 2);
                    int endday = Integer.parseInt(day);

                    enddatePickerDialog.updateDate(endyear,endmonth,endday);
                    enddatePickerDialog.getDatePicker().setMinDate(myCalendar.getTimeInMillis());
                }
                enddatePickerDialog.show();
            }
        });

        mstartingdate_tickimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mstartdatetxt.setText("");
                mstartdatetxt.setHint("Start Date");
                mstartingdate_tickimg.setVisibility(GONE);
                mstartdatetxt.setTextColor(Color.BLACK);
                startingdatestring = "";
            }
        });

        mendingdate_tickimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menddatetxt.setText("");
                menddatetxt.setHint("End Date");
                mendingdate_tickimg.setVisibility(GONE);
                menddatetxt.setTextColor(Color.BLACK);
                endingdatestring = "";
            }
        });

        mapplyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mProgress.setMessage("Applying Filter");
                mProgress.setCancelable(false);
                mProgress.show();

                String startdateval = mstartdatetxt.getText().toString();
                String enddateval = menddatetxt.getText().toString();
                Log.d(TAG, "startdateval " + startdateval);
                Log.d(TAG, "enddateval " + enddateval);
                if (!startdateval.equals("")) {
                    if (!enddateval.equals("")) {

                        long startingdatelong = Long.valueOf(startingdatestring);
                        long endingdatelong = Long.valueOf(endingdatestring);

                        if (startingdatelong > endingdatelong) {
                            new AlertDialog.Builder(SearchCategory.this)
                                    .setTitle("Invalid Start Date")
                                    .setMessage("Start Date has to be earlier than End Date")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    }).show();
                            return;
                        } else {
                            newSortFilter.child("StartDate").setValue(startingdatestring);
                            newSortFilter.child("EndDate").setValue(endingdatestring);
                        }
                    } else {
                        newSortFilter.child("StartDate").setValue(startingdatestring);
                        newSortFilter.child("EndDate").removeValue();
                    }
                } else {
                    if (!enddateval.equals("")) {
                        new AlertDialog.Builder(SearchCategory.this)
                                .setTitle("Empty Start Date")
                                .setMessage("Please select a start date")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                }).show();
                        return;
                    } else {
                        newSortFilter.child("StartDate").removeValue();
                        newSortFilter.child("EndDate").removeValue();
                    }
                }

                if (oldfilterbywages.equals("true")) {
                    //No WagesFilter = OldWagesFilter

                    long spinnercurrency = (mspinnercurrency.getSelectedItemPosition() + 11) * 100;
                    final long WagesFilter = spinnercurrency + wagescategory;
                    newSortFilter.child("WagesFilter").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            newSortFilter.child("OldWagesFilter").setValue(WagesFilter);

                            mProgress.dismiss();
                            dialog.dismiss();

                            mdialog.setCancelable(false);
                            mdialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
                            mdialog.show();

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    if (!joblist.isEmpty()) {joblist.clear();}

                                    Intent searchintent = new Intent(SearchCategory.this, SearchCategory.class);
                                    searchintent.putExtra("city_id",city);
                                    searchintent.putExtra("category_title",category_title);
                                    startActivity(searchintent);
                                    finish();
                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                    mdialog.dismiss();
                                }
                            }, 500); //time seconds
                        }
                    });}
                else if (oldfilterbywages.equals("false")) {
                    //Got WagesFilter

                    long spinnercurrency = (mspinnercurrency.getSelectedItemPosition() + 11) * 100;
                    long WagesFilter = spinnercurrency + wagescategory;
                    newSortFilter.child("WagesFilter").setValue(WagesFilter).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            newSortFilter.child("OldWagesFilter").removeValue();

                            mProgress.dismiss();
                            dialog.dismiss();

                            mdialog.setCancelable(false);
                            mdialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
                            mdialog.show();

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    if (!joblist.isEmpty()) {joblist.clear();}

                                    Intent searchintent = new Intent(SearchCategory.this, SearchCategory.class);
                                    searchintent.putExtra("city_id",city);
                                    searchintent.putExtra("category_title",category_title);
                                    startActivity(searchintent);
                                    finish();
                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                    mdialog.dismiss();
                                }
                            }, 500); //time seconds
                        }
                    });
                }
            }
        });

        mclearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mratetxt.setText("Less Than 5");
                mpriceBar.setProgress(0);
                mspinnerrate.setSelection(0);
                mspinnercurrency.setSelection(0);

                mstartdatetxt.setText("");
                mstartdatetxt.setHint("Start Date");
                mstartingdate_tickimg.setVisibility(GONE);
                mstartdatetxt.setTextColor(Color.BLACK);

                menddatetxt.setText("");
                menddatetxt.setHint("End Date");
                mendingdate_tickimg.setVisibility(GONE);
                menddatetxt.setTextColor(Color.BLACK);

                mallrangetick.setImageResource(R.drawable.single_tick);
                mwagesrangetick.setImageResource(R.color.buttonTextColor);
                mblockLay.setVisibility(VISIBLE);

                oldfilterbywages = "true";
                wagescategory = 11;
            }
        });

        dialog.show();*/
    }

    @Override
    protected void onDestroy() {
        if (mdialog != null && mdialog.isShowing()) {
            mdialog.dismiss();
        }
        if (mProgress != null && mProgress.isShowing()) {
            mProgress.dismiss();
        }

        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == 8888) && (resultCode == Activity.RESULT_OK)) {
            // recreate your fragment here

            city = data.getStringExtra("city");
            Log.d(TAG, "city " +city );

            final ProgressDialog mdialog = new ProgressDialog(SearchCategory.this, R.style.MyTheme);
            mdialog.setCancelable(false);
            mdialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
            mdialog.show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (!joblist.isEmpty()) {joblist.clear();}

                    Intent searchintent = new Intent(SearchCategory.this, SearchCategory.class);
                    searchintent.putExtra("city_id",city);
                    searchintent.putExtra("category_title",category_title);
                    startActivity(searchintent);
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    mdialog.dismiss();
                }
            }, 500); //time seconds
        }

    }

}
