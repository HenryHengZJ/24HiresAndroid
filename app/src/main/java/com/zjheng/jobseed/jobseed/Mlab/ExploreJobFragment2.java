package com.zjheng.jobseed.jobseed.Mlab;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wang.avi.AVLoadingIndicatorView;
import com.zjheng.jobseed.jobseed.CustomObjectClass.Job;
import com.zjheng.jobseed.jobseed.CustomUIClass.ClickableViewPager;
import com.zjheng.jobseed.jobseed.HomeScene.BannerDetails;
import com.zjheng.jobseed.jobseed.HomeScene.BannerViewPagerAdapter;
import com.zjheng.jobseed.jobseed.HomeScene.ExploreJobs.FilterJob;
import com.zjheng.jobseed.jobseed.HomeScene.ExploreJobs.HomeRecyclerAdapter;
import com.zjheng.jobseed.jobseed.LocationList;
import com.zjheng.jobseed.jobseed.NearbyJobScene.NearbyJob;
import com.zjheng.jobseed.jobseed.R;
import com.zjheng.jobseed.jobseed.SearchJobScene.SearchCategory;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ExploreJobFragment2 extends Fragment {

    private RecyclerView mJobList;
    private LinearLayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private HomeRecyclerAdapter homeRecyclerAdapter;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserAccount , mJob, mUserLocation, mUserActivities, mUserSortFilter, mBanner;
    private Query mQueryLoadMore, mQueryJob;
    private ValueEventListener mListener;

    private TextView mjobnotfoundtxt, mcurrentLocationtxt;
    private RelativeLayout mnojobLay, mnoInternetLay;
    private TextView msortfilter;
    private NestedScrollView mnestedscroll;
    private LinearLayout mBaristaBtn, mBeautyBtn, mChefBtn, mEventBtn, mEmceeBtn;
    private LinearLayout mEducationBtn, mFitnessBtn, mModellingBtn, mMascotBtn;
    private LinearLayout mOfficeBtn, mPromoterBtn, mRoadshowBtn, mRovingBtn;
    private LinearLayout mRetailBtn, mServingBtn, mUsherBtn, mWaiterBtn, mOtherBtn;
    private CardView mretryBtn, mnearbyCardView, mSortCardView, mfilternearbyCardView, mchgLocatioLay, mcurrentLocationCardView;

    private ProgressDialog HomeProgress;
    private ProgressDialog mProgress;
    private Calendar myCalendar;

    private static final String TAG = "ExploreJobFragment2";
    private static int PLACE_PICKER_REQUEST = 1;

    private List <Job> joblist = new ArrayList<Job>();

    private String city, filterbystart = "", filterbyend = "", oldfilterbywages;
    private String startingdatestring, endingdatestring;
    private Long firstposttime, variablechildcount, mostrecent_startdate, mostrecent_wagesrange, mostrecent_wagesrange_startdate;

    private boolean loading = true;
    private int count, scenario;
    private int loadlimit = 8;
    private long startdate = 0 , enddate = 0, wagescategory = 0, filterbywages = 0;
    private long startwagesfilter, starttime;

    private DatePickerDialog startdatePickerDialog;
    private DatePickerDialog enddatePickerDialog;

    private ClickableViewPager mAdsViewPager;

    private ArrayList<String> bannerlist;
    int currentPage = -1;
    Timer timer;
    private long DELAY_MS = 5000;//delay in milliseconds before task is to be executed
    private long PERIOD_MS = 5000; // time in milliseconds between successive task executions.
    private AVLoadingIndicatorView mavi;


    private RelativeLayout mloadingLay;
    Activity context;
    View rootView;

    class GetData extends AsyncTask<String,Void,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            String stream = null;
            String urlString = params[0];
            Log.e("urlString", "urlString line " + urlString);
            HTTPDataHandler http = new HTTPDataHandler();
            stream = http.GetHTTPData(urlString );
            return stream;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Gson gson = new Gson();
            Type listType = new TypeToken<List <Job>>(){}.getType();
            joblist = gson.fromJson(s,listType);
            Log.e("joblist", "joblist line " + joblist);
            homeRecyclerAdapter = new HomeRecyclerAdapter(mJob,joblist,getActivity());
            mJobList.setAdapter(homeRecyclerAdapter);

            fadeOut(mloadingLay);

            mnestedscroll.setVisibility(VISIBLE);
            mnojobLay.setVisibility(GONE);
        }
    }

    public static ExploreJobFragment2 newInstance(String city) {
        ExploreJobFragment2 result = new ExploreJobFragment2();
        Bundle bundle = new Bundle();
        bundle.putString("city", city);
        result.setArguments(bundle);
        return result;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        String cityfromhome = bundle.getString("city");
        city = filtercity(cityfromhome);
    }

    private String filtercity(String address) {

        if(address.contains("Pulau Pinang") || address.contains("Penang")) {city = "Penang";}
        else if (address.contains("Kuala Lumpur")) {city = "Kuala Lumpur";}
        else if (address.contains("Labuan")) {city = "Labuan";}
        else if (address.contains("Putrajaya")) {city = "Putrajaya";}
        else if (address.contains("Johor")) {city = "Johor";}
        else if (address.contains("Kedah")) {city = "Kedah";}
        else if (address.contains("Kelantan")) {city = "Kelantan";}
        else if (address.contains("Melaka")|| address.contains("Melacca")) {city = "Melacca";}
        else if (address.contains("Negeri Sembilan")|| address.contains("Seremban")) {city = "Negeri Sembilan";}
        //
        else if (address.contains("Pahang")) {city = "Pahang";}
        else if (address.contains("Perak")|| address.contains("Ipoh")) {city = "Perak";}
        else if (address.contains("Perlis")) {city = "Perlis";}
        else if (address.contains("Sabah")) {city = "Sabah";}
        else if (address.contains("Sarawak")) {city = "Sarawak";}
        else if (address.contains("Selangor")|| address.contains("Shah Alam")|| address.contains("Klang")) {city = "Selangor";}
        else if (address.contains("Terengganu")) {city = "Terengganu";}

        return address;

    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.activity_home_explorejobs2, container, false);

        context = getActivity();

        setHasOptionsMenu(true);

        mavi = (AVLoadingIndicatorView)rootView.findViewById(R.id.avi);
        mloadingLay= (RelativeLayout)rootView.findViewById(R.id.loadingLay);

        mnoInternetLay = (RelativeLayout)rootView.findViewById(R.id.noInternetLay);
        mretryBtn = (CardView)rootView.findViewById(R.id.retryBtn);
        mSwipeRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.swipe_container);
        mfilternearbyCardView = (CardView) rootView.findViewById(R.id.filternearbyCardView);
        mfilternearbyCardView.bringToFront();
        mfilternearbyCardView.invalidate();

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                Log.d(TAG, "connected to wifi");
                // connected to wifi
                mnoInternetLay.setVisibility(GONE);
                mSwipeRefreshLayout.setVisibility(VISIBLE);
                mfilternearbyCardView.setVisibility(VISIBLE);

            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                Log.d(TAG, "connected to data");
                // connected to the mobile provider's data plan
                mnoInternetLay.setVisibility(GONE);
                mSwipeRefreshLayout.setVisibility(VISIBLE);
                mfilternearbyCardView.setVisibility(VISIBLE);
            }
        } else {
            Log.d(TAG, "not connected");
            // not connected to the internet
            mnoInternetLay.setVisibility(VISIBLE);
            mSwipeRefreshLayout.setVisibility(GONE);
            mfilternearbyCardView.setVisibility(GONE);

            mretryBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                    if (activeNetwork != null) { // connected to the internet
                        if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                            Log.d(TAG, "connected to wifi");
                            // connected to wifi
                            mnoInternetLay.setVisibility(GONE);
                            mSwipeRefreshLayout.setVisibility(VISIBLE);
                            mfilternearbyCardView.setVisibility(VISIBLE);

                        } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                            Log.d(TAG, "connected to data");
                            // connected to the mobile provider's data plan
                            mnoInternetLay.setVisibility(GONE);
                            mSwipeRefreshLayout.setVisibility(VISIBLE);
                            mfilternearbyCardView.setVisibility(VISIBLE);
                        }
                    } else {
                        Log.d(TAG, "not connected");
                        // not connected to the internet
                        mnoInternetLay.setVisibility(VISIBLE);
                        mSwipeRefreshLayout.setVisibility(GONE);
                        mfilternearbyCardView.setVisibility(GONE);
                    }
                }
            });
        }

        mAuth = FirebaseAuth.getInstance();

        mUserAccount =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserAccount");

        mUserActivities =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserActivities");

        mUserLocation =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserLocation");

        mJob =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("Job");

        mUserSortFilter =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("SortFilter");
        mUserSortFilter.child(mAuth.getCurrentUser().getUid()).keepSynced(true);

        mBanner=  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("Banner");

        mProgress = new ProgressDialog(context);

        msortfilter =(TextView) rootView.findViewById(R.id.sortfilter);

        mjobnotfoundtxt= (TextView) rootView.findViewById(R.id.jobnotfoundtxt);
        mcurrentLocationtxt= (TextView) rootView.findViewById(R.id.currentLocationtxt);
        mcurrentLocationtxt.setText(city);
        mnojobLay = (RelativeLayout) rootView.findViewById(R.id.nojobLay);
        mSortCardView = (CardView) rootView.findViewById(R.id.SortCardView);
        mnearbyCardView = (CardView) rootView.findViewById(R.id.nearbyCardView);
        mcurrentLocationCardView = (CardView) rootView.findViewById(R.id.currentLocationCardView);

        mchgLocatioLay = (CardView) rootView.findViewById(R.id.chgLocatioLay);
        mnestedscroll = (NestedScrollView) rootView.findViewById(R.id.nestedscroll);

        mBaristaBtn = (LinearLayout) rootView.findViewById(R.id.BaristaBtn);
        mBeautyBtn = (LinearLayout) rootView.findViewById(R.id.BeautyBtn);
        mChefBtn = (LinearLayout) rootView.findViewById(R.id.ChefBtn);
        mEventBtn = (LinearLayout) rootView.findViewById(R.id.EventBtn);
        mEmceeBtn = (LinearLayout) rootView.findViewById(R.id.EmceeBtn);

        mEducationBtn = (LinearLayout) rootView.findViewById(R.id.EducationBtn);
        mFitnessBtn = (LinearLayout) rootView.findViewById(R.id.FitnessBtn);
        mModellingBtn = (LinearLayout) rootView.findViewById(R.id.ModellingBtn);
        mMascotBtn = (LinearLayout) rootView.findViewById(R.id.MascotBtn);

        mOfficeBtn = (LinearLayout) rootView.findViewById(R.id.OfficeBtn);
        mPromoterBtn = (LinearLayout) rootView.findViewById(R.id.PromoterBtn);
        mRoadshowBtn = (LinearLayout) rootView.findViewById(R.id.RoadshowBtn);
        mRovingBtn = (LinearLayout) rootView.findViewById(R.id.RovingBtn);

        mRetailBtn = (LinearLayout) rootView.findViewById(R.id.RetailBtn);
        mServingBtn = (LinearLayout) rootView.findViewById(R.id.ServingBtn);
        mUsherBtn = (LinearLayout) rootView.findViewById(R.id.UsherBtn);
        mWaiterBtn = (LinearLayout) rootView.findViewById(R.id.WaiterBtn);
        mOtherBtn = (LinearLayout) rootView.findViewById(R.id.OtherBtn);

        bannerlist = new ArrayList<String>();
        mAdsViewPager = (ClickableViewPager) rootView.findViewById(R.id.adscontainer);
        final BannerViewPagerAdapter mviewPagerAdapter = new BannerViewPagerAdapter(bannerlist,context);
        mAdsViewPager.setAdapter(mviewPagerAdapter);

        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(mAdsViewPager, true);

        mBanner.child("BannerPicture").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot bannerSnaphot : dataSnapshot.getChildren()) {
                    String image =(String)bannerSnaphot.getValue();
                    bannerlist.add(image);
                }
                mviewPagerAdapter.notifyDataSetChanged();

                Log.d(TAG, "bannerlist.size() "+ bannerlist.size());

                //This will scroll page-by-page so that you can view scroll happening
                /*After setting the adapter use the timer */
                final Handler handler = new Handler();
                final Runnable Update = new Runnable() {
                    public void run() {
                        mAdsViewPager.setCurrentItem(++currentPage, true);
                        // go to initial page i.e. position 0
                        if (currentPage == bannerlist.size() -1) {
                            currentPage = -1;
                            // ++currentPage will make currentPage = 0
                        }
                    }
                };

                timer = new Timer(); // This will create a new Thread
                timer .schedule(new TimerTask() { // task to be scheduled

                    @Override
                    public void run() {
                        handler.post(Update);
                    }
                }, DELAY_MS, PERIOD_MS);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        mAdsViewPager.setOnItemClickListener(new ClickableViewPager.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                Log.d(TAG, "position: "+position);

                retrieveURLlink(position);

            }
        });

        mAdsViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                currentPage = position;

                if(position == bannerlist.size() -1){
                    currentPage = -1;
                }

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mJobList = (RecyclerView)rootView.findViewById(R.id.blog_list);
        mJobList.setHasFixedSize(false);
        //mJobList.setFocusable(false);
        mJobList.setNestedScrollingEnabled(false);
        mLayoutManager = new LinearLayoutManager(context.getApplicationContext());
        mLayoutManager.setReverseLayout(false);
        mLayoutManager.setStackFromEnd(false);
        homeRecyclerAdapter = new HomeRecyclerAdapter(mJob,joblist,getActivity());
        mJobList.setLayoutManager(mLayoutManager);
        mJobList.setAdapter(homeRecyclerAdapter);

        loading = true;
        loadlimit = 8;
        startdate = 0 ; enddate = 0; wagescategory = 0; filterbywages = 0;
        filterbystart = ""; filterbyend = "";

       // new GetData().execute("http://mongodb.24hires.com:3001/api/status?lowertitle__regex=/^du/i");

        checkSortFilter();
        nestedscrollListener();

        // Set a Refresh Listener for the SwipeRefreshLayout
        mSwipeRefreshLayout.setColorSchemeColors(Color.parseColor("#47ded6"), Color.parseColor("#DE47A7"), Color.parseColor("#6D47DE"));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh the data
                if(city!=null && filterbystart!=null && filterbyend!=null) {

                    loading = true;
                    loadlimit = 8;
                    startdate = 0 ; enddate = 0; wagescategory = 0; filterbywages = 0;
                    filterbystart = ""; filterbyend = "";

                    if (!joblist.isEmpty()) {joblist.clear();}

                    checkSortFilter();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }, 600); //time seconds
                }
                else{
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        mnearbyCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nearbyintent = new Intent(context, NearbyJob.class);
                startActivity(nearbyintent);
                context.overridePendingTransition(R.anim.pullup,R.anim.nochange);
            }
        });

        mSortCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showsortfilter();
            }
        });

        mchgLocatioLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, LocationList.class);
                intent.putExtra("locationstatus", "");
                intent.putExtra("travellocations", "");
                startActivityForResult(intent, 1111);
            }

        });

        mcurrentLocationCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, LocationList.class);
                intent.putExtra("locationstatus", "");
                intent.putExtra("travellocations", "");
                startActivityForResult(intent, 1111);
            }
        });

        mBaristaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(city!=null) {
                    Intent singleblogintent = new Intent(context, SearchCategory.class);
                    singleblogintent.putExtra("category_title", "Barista / Bartender");
                    singleblogintent.putExtra("city_id", city);
                    startActivity(singleblogintent);
                }
            }
        });

        mBeautyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(city!=null) {
                    Intent singleblogintent = new Intent(context, SearchCategory.class);
                    singleblogintent.putExtra("category_title", "Beauty / Wellness");
                    singleblogintent.putExtra("city_id", city);
                    startActivity(singleblogintent);
                }
            }
        });

        mChefBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(city!=null) {
                    Intent singleblogintent = new Intent(context, SearchCategory.class);
                    singleblogintent.putExtra("category_title", "Chef / Kitchen Helper");
                    singleblogintent.putExtra("city_id", city);
                    startActivity(singleblogintent);
                }
            }
        });

        mEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(city!=null) {
                    Intent singleblogintent = new Intent(context, SearchCategory.class);
                    singleblogintent.putExtra("category_title", "Event Crew");
                    singleblogintent.putExtra("city_id", city);
                    startActivity(singleblogintent);
                }
            }
        });

        mEmceeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(city!=null) {
                    Intent singleblogintent = new Intent(context, SearchCategory.class);
                    singleblogintent.putExtra("category_title", "Emcee");
                    singleblogintent.putExtra("city_id", city);
                    startActivity(singleblogintent);
                }
            }
        });

        /////////////////////////////////////////////////////////////////////////////////////////////

        mEducationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(city!=null) {
                    Intent singleblogintent = new Intent(context, SearchCategory.class);
                    singleblogintent.putExtra("category_title", "Education");
                    singleblogintent.putExtra("city_id", city);
                    startActivity(singleblogintent);
                }
            }
        });


        mFitnessBtn .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(city!=null) {
                    Intent singleblogintent = new Intent(context, SearchCategory.class);
                    singleblogintent.putExtra("category_title", "Fitness / Gym");
                    singleblogintent.putExtra("city_id", city);
                    startActivity(singleblogintent);
                }
            }
        });

        mModellingBtn .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(city!=null) {
                    Intent singleblogintent = new Intent(context, SearchCategory.class);
                    singleblogintent.putExtra("category_title", "Modelling / Shooting");
                    singleblogintent.putExtra("city_id", city);
                    startActivity(singleblogintent);
                }
            }
        });

        mModellingBtn .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(city!=null) {
                    Intent singleblogintent = new Intent(context, SearchCategory.class);
                    singleblogintent.putExtra("category_title", "Modelling / Shooting");
                    singleblogintent.putExtra("city_id", city);
                    startActivity(singleblogintent);
                }
            }
        });

        mMascotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(city!=null) {
                    Intent singleblogintent = new Intent(context, SearchCategory.class);
                    singleblogintent.putExtra("category_title", "Mascot");
                    singleblogintent.putExtra("city_id", city);
                    startActivity(singleblogintent);
                }
            }
        });

        ////////////////////////////////////////////////////////////////////////////////////////////

        mOfficeBtn .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(city!=null) {
                    Intent singleblogintent = new Intent(context, SearchCategory.class);
                    singleblogintent.putExtra("category_title", "Office / Admin");
                    singleblogintent.putExtra("city_id", city);
                    startActivity(singleblogintent);
                }
            }
        });

        mPromoterBtn .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(city!=null) {
                    Intent singleblogintent = new Intent(context, SearchCategory.class);
                    singleblogintent.putExtra("category_title", "Promoter / Sampling");
                    singleblogintent.putExtra("city_id", city);
                    startActivity(singleblogintent);
                }
            }
        });

        mRoadshowBtn .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(city!=null) {
                    Intent singleblogintent = new Intent(context, SearchCategory.class);
                    singleblogintent.putExtra("category_title", "Roadshow");
                    singleblogintent.putExtra("city_id", city);
                    startActivity(singleblogintent);
                }
            }
        });

        mRovingBtn .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(city!=null) {
                    Intent singleblogintent = new Intent(context, SearchCategory.class);
                    singleblogintent.putExtra("category_title", "Roving Team");
                    singleblogintent.putExtra("city_id", city);
                    startActivity(singleblogintent);
                }
            }
        });

        /////////////////////////////////////////////////////////////////////////////////////////////


        mRetailBtn  .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(city!=null) {
                    Intent singleblogintent = new Intent(context, SearchCategory.class);
                    singleblogintent.putExtra("category_title", "Retail / Consumer");
                    singleblogintent.putExtra("city_id", city);
                    startActivity(singleblogintent);
                }
            }
        });

        mServingBtn  .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(city!=null) {
                    Intent singleblogintent = new Intent(context, SearchCategory.class);
                    singleblogintent.putExtra("category_title", "Serving");
                    singleblogintent.putExtra("city_id", city);
                    startActivity(singleblogintent);
                }
            }
        });

        mUsherBtn  .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(city!=null) {
                    Intent singleblogintent = new Intent(context, SearchCategory.class);
                    singleblogintent.putExtra("category_title", "Usher / Ambassador");
                    singleblogintent.putExtra("city_id", city);
                    startActivity(singleblogintent);
                }
            }
        });

        mWaiterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(city!=null) {
                    Intent singleblogintent = new Intent(context, SearchCategory.class);
                    singleblogintent.putExtra("category_title", "Waiter / Waitress");
                    singleblogintent.putExtra("city_id", city);
                    startActivity(singleblogintent);
                }
            }
        });

        mOtherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(city!=null) {
                    Intent singleblogintent = new Intent(context, SearchCategory.class);
                    singleblogintent.putExtra("category_title", "Other");
                    singleblogintent.putExtra("city_id", city);
                    startActivity(singleblogintent);
                }
            }
        });

        return rootView;
    }

    private void retrieveURLlink(final int urlposition){

        final String intposition = "banner" + Integer.toString(urlposition+1);
        Log.d(TAG, "intposition "+intposition);

        mBanner.child("BannerLink").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild(intposition)){
                    String urllink =(String)dataSnapshot.child(intposition).getValue();
                    Log.d(TAG, "urllink "+urllink);
                    Intent bannerdetailintent = new Intent(context, BannerDetails.class);
                    bannerdetailintent.putExtra("linkurl", urllink);
                    context.startActivity(bannerdetailintent);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }



    private void nestedscrollListener(){
        mnestedscroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY) {

                }
                else if (scrollY < oldScrollY) {

                }

                if (scrollY == 0) {
                    Log.d(TAG, "REACHED TOP");
                }

                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                    Log.d(TAG, "REACHED BOTTOM");
                    if(firstposttime!=null) {
                        Log.d(TAG, "loading null: " + loading);
                        if (loading) {

                            loading = false;

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (joblist.size() > 0) {
                                        joblist.remove(joblist.size() - 1);
                                        homeRecyclerAdapter.notifyItemRemoved(joblist.size());
                                        loadMoreNormalJob(city, loadlimit, scenario);
                                    }
                                }
                            }, 750); //time seconds
                        }
                    }
                }
            }
        });
    }

    private void showsortfilter() {
        Intent filterintent = new Intent(context, FilterJob.class);
        filterintent.putExtra("oldfilterbywages", oldfilterbywages);
        filterintent.putExtra("filterbywages", filterbywages);
        filterintent.putExtra("filterbystart", filterbystart);
        filterintent.putExtra("filterbyend", filterbyend);
        filterintent.putExtra("city", city);
        startActivityForResult(filterintent, 8888);
        context.overridePendingTransition(R.anim.pullup,R.anim.nochange);
    }

    private void checkSortFilter(){

        mUserSortFilter.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    //Got start no end
                    if(dataSnapshot.hasChild("StartDate")
                            && !dataSnapshot.hasChild("EndDate")) {
                        filterbystart = dataSnapshot.child("StartDate").getValue().toString();
                        filterbyend = "";

                        if(dataSnapshot.hasChild("OldWagesFilter")){
                            //If user selected Show All Wages
                            scenario = 11;
                            oldfilterbywages = "true";
                            filterbywages = (Long)dataSnapshot.child("OldWagesFilter").getValue();
                            checkuserLocation(filterbystart,filterbyend, filterbywages, scenario);

                        }
                        else if (dataSnapshot.hasChild("WagesFilter")){
                            //If user selected Specific Range
                            scenario = 1;
                            oldfilterbywages = "false";
                            filterbywages = (Long)dataSnapshot.child("WagesFilter").getValue();
                            checkuserLocation(filterbystart,filterbyend, filterbywages, scenario);
                        }
                        else{
                            scenario = 11;
                            filterbywages = 1111; //defauly value = MYR + per hour + Less Than 5
                            oldfilterbywages = "true";
                            checkuserLocation(filterbystart,filterbyend, filterbywages, scenario);
                        }
                    }
                    //Got start got end
                    else if(dataSnapshot.hasChild("StartDate")
                            && dataSnapshot.hasChild("EndDate")) {
                        filterbystart = dataSnapshot.child("StartDate").getValue().toString();
                        filterbyend = dataSnapshot.child("EndDate").getValue().toString();

                        if(dataSnapshot.hasChild("OldWagesFilter")){
                            //If user selected Show All Wages
                            scenario = 22;
                            oldfilterbywages = "true";
                            filterbywages = (Long)dataSnapshot.child("OldWagesFilter").getValue();
                            checkuserLocation(filterbystart,filterbyend, filterbywages, scenario);

                        }
                        else if (dataSnapshot.hasChild("WagesFilter")){
                            //If user selected Specific Range
                            scenario = 2;
                            oldfilterbywages = "false";
                            filterbywages = (Long)dataSnapshot.child("WagesFilter").getValue();
                            checkuserLocation(filterbystart,filterbyend, filterbywages, scenario);
                        }
                        else{
                            scenario = 22;
                            filterbywages = 1111; //defauly value = MYR + per hour + Less Than 5
                            oldfilterbywages = "true";
                            checkuserLocation(filterbystart,filterbyend, filterbywages, scenario);
                        }
                    }
                    //No start no end
                    else{
                        filterbystart = "";
                        filterbyend = "";

                        if(dataSnapshot.hasChild("OldWagesFilter")){
                            //If user selected Show All Wages
                            scenario = 33;
                            oldfilterbywages = "true";
                            filterbywages = (Long)dataSnapshot.child("OldWagesFilter").getValue();
                            checkuserLocation(filterbystart,filterbyend, filterbywages, scenario);

                        }
                        else if (dataSnapshot.hasChild("WagesFilter")){
                            //If user selected Specific Range
                            scenario = 3;
                            oldfilterbywages = "false";
                            filterbywages = (Long)dataSnapshot.child("WagesFilter").getValue();
                            checkuserLocation(filterbystart,filterbyend, filterbywages, scenario);
                        }
                        else{
                            scenario = 33;
                            filterbywages = 1111; //defauly value = MYR + per hour + Less Than 5
                            oldfilterbywages = "true";
                            checkuserLocation(filterbystart,filterbyend, filterbywages, scenario);
                        }
                    }
                }
                else{
                    filterbystart = "";
                    filterbyend = "";
                    filterbywages = 1111; //defauly value = MYR + per hour + Less Than 5
                    scenario = 33;
                    oldfilterbywages = "true";
                    checkuserLocation(filterbystart,filterbyend, filterbywages, scenario);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void checkuserLocation(final String filterbystart,final String filterbyend,final long filterbywages,final int scenario){


        if (city != null) {

            if(city.equals("none")) {

                Log.d(TAG, "citits2 " + city);


                //mloadingLay.setVisibility(GONE);
                fadeOut(mloadingLay);

                mnojobLay.setVisibility(VISIBLE);
                mnestedscroll.setVisibility(GONE);

                mjobnotfoundtxt.setText("Please specify your location in order to see more jobs.");

                if (scenario == 1 || (scenario == 22)) {
                    msortfilter.setText("2");
                } else if (scenario == 2) {
                    msortfilter.setText("3");
                } else if (scenario == 33 || scenario == 0) {
                    msortfilter.setText("0");
                } else {
                    msortfilter.setText("1");
                }

            }
            else {

                Log.d(TAG,"citits1 " + city );

                //mloadingLay.setVisibility(GONE);
                fadeOut(mloadingLay);

                mnestedscroll.setVisibility(VISIBLE);
                mnojobLay.setVisibility(GONE);

                if(scenario == 1 || (scenario == 22)){ msortfilter.setText("2"); }
                else if(scenario == 2){ msortfilter.setText("3"); }
                else if(scenario == 33 || scenario == 0){ msortfilter.setText("0"); }
                else { msortfilter.setText("1"); }

                presentjobpost(city, filterbystart,filterbyend, filterbywages, scenario);

            }
        }

        //If user first time login, no saved location yet
        else {

            Log.d(TAG, "citits3 " + city);

          //  mloadingLay.setVisibility(GONE);
            fadeOut(mloadingLay);

            mnojobLay.setVisibility(VISIBLE);
            mnestedscroll.setVisibility(GONE);

            mjobnotfoundtxt.setText("Please specify your location in order to see more jobs.");

        }
    }

    private void fadeOut(final RelativeLayout view) {
        final AlphaAnimation fadeOutAnimation = new AlphaAnimation(1.0F,  0.0F);
        fadeOutAnimation.setDuration(1000);
        fadeOutAnimation.setInterpolator(new AccelerateInterpolator());
        fadeOutAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
                mavi.smoothToHide();
            }
        });
        view.startAnimation(fadeOutAnimation);
    }


    private void presentjobpost(final String city, final String filterbystart,final String filterbyend,final long filterbywages,final int scenario) {

        count = 0;

        Log.d(TAG,"scenario " + scenario );
        int intwages = 0, currencyint = 0, wagescategoryint = 0;

        if(filterbywages!=0){
            //get currency number
            intwages = (int) (filterbywages % 100000);
            currencyint = Integer.parseInt(Integer.toString(intwages).substring(0, 2));

            Log.d(TAG,"intwages " + intwages );
            Log.d(TAG,"currencyint " + currencyint );
            //get wagescateogry number
            wagescategoryint = intwages % 100;
        }

        Long tsLong = System.currentTimeMillis()/1000;
        if(!filterbystart.equals("")){
            Log.d(TAG,"filterbystart11 " + filterbystart );
            startdate =  Long.valueOf(filterbystart);
            int length = (int) Math.log10(startdate) + 1;
            if(length<6){

            }
            Log.d(TAG,"startdate11 " + startdate );

        }
        if(!filterbyend.equals("")){
            enddate =  Long.valueOf(filterbyend);
        }

        //Got start no end
        if(scenario == 1){
            //Got WagesFilter

            starttime = -1*((startdate* 100000000L)+(wagescategoryint * 100000000000000L)+(currencyint * 10000000000000000L));
            long endtime = -1*((tsLong)+(999999* 100000000L)+(wagescategoryint * 100000000000000L)+(currencyint * 10000000000000000L));

            Log.d(TAG,"starttime1 " + starttime );
            Log.d(TAG,"endtime1 " + endtime );

            mQueryJob = mJob.child(city).orderByChild("mostrecent_wagesrange_startdate").startAt(endtime).endAt(starttime);
        }

        else if (scenario == 11){
            //No WagesFilter = OldWagesFilter

            long endtime = -1*(tsLong+(999999* 10000000000L));
            String startstring = filterbystart+"0000000000";
            starttime =  -1*(Long.valueOf(startstring));
            //long starttime = -1*((startdate* 10000000000L));

            Log.d(TAG,"starttime11 " + starttime );
            Log.d(TAG,"endtime11 " + endtime );

            mQueryJob = mJob.child(city).orderByChild("mostrecent_startdate").startAt(endtime).endAt(starttime);
        }

        //Got start got end
        else if (scenario == 2){
            //Got WagesFilter

            long endtime = -1*((tsLong%100000000)+(enddate* 100000000L)+(wagescategoryint * 100000000000000L)+(currencyint * 10000000000000000L));
            starttime = -1*((startdate* 100000000L)+(wagescategoryint * 100000000000000L)+(currencyint * 10000000000000000L));

            Log.d(TAG,"currencyint2 " + currencyint );
            Log.d(TAG,"intwages2 " + intwages );
            Log.d(TAG,"endtime2 " + endtime );
            Log.d(TAG,"starttime2 " + starttime );

            mQueryJob = mJob.child(city).orderByChild("mostrecent_wagesrange_startdate").startAt(endtime).endAt(starttime);

        }
        else if (scenario == 22){
            //No WagesFilter = OldWagesFilter

            long endtime = -1*(tsLong+(enddate* 10000000000L));
            starttime = -1*((startdate* 10000000000L));

            Log.d(TAG,"endtime22 " + endtime );
            Log.d(TAG,"starttime22 " + starttime );

            mQueryJob = mJob.child(city).orderByChild("mostrecent_startdate").startAt(endtime).endAt(starttime);
        }

        //No start no end
        else if (scenario == 3){
            //Got WagesFilter

            long currencylong = Long.valueOf(currencyint)*1000000000000L;
            Log.d(TAG,"currencylong3 " + currencylong );

            long wagescategorylong = Long.valueOf(wagescategoryint)*10000000000L;
            Log.d(TAG,"wagescategorylong3 " + wagescategorylong );

            long endwagesfilter = -1*(tsLong+wagescategorylong+currencylong);
            startwagesfilter = -1*(wagescategorylong+currencylong);

            Log.d(TAG,"endwagesfilter " + endwagesfilter );
            Log.d(TAG,"startwagesfilter " + startwagesfilter );

            mQueryJob = mJob.child(city).orderByChild("mostrecent_wagesrange").startAt(endwagesfilter).endAt(startwagesfilter);
        }

        else if (scenario == 33 || scenario == 0){
            //No WagesFilter = OldWagesFilter
            mQueryJob = mJob.child(city).orderByChild("negatedtime");
        }
        else{
            return;
        }

        mJob.child(city).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    mQueryJob.limitToFirst(loadlimit).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            for (DataSnapshot usersdataSnapshot : dataSnapshot.getChildren()) {

                                Job jobs = new Job();

                                if (usersdataSnapshot.hasChild("title") && usersdataSnapshot.hasChild("desc") && usersdataSnapshot.hasChild("category")
                                        && usersdataSnapshot.hasChild("company") && usersdataSnapshot.hasChild("fulladdress")
                                        && usersdataSnapshot.hasChild("postimage") && usersdataSnapshot.hasChild("postkey")
                                        && usersdataSnapshot.hasChild("city") && usersdataSnapshot.hasChild("closed")) {

                                    String title = usersdataSnapshot.child("title").getValue().toString();
                                    Log.d(TAG, "firstloadtitle: " + title);
                                    String desc = usersdataSnapshot.child("desc").getValue().toString();
                                    String category = usersdataSnapshot.child("category").getValue().toString();
                                    String company = usersdataSnapshot.child("company").getValue().toString();
                                    String fulladdress = usersdataSnapshot.child("fulladdress").getValue().toString();
                                    String postimage = usersdataSnapshot.child("postimage").getValue().toString();
                                    String postkey = usersdataSnapshot.child("postkey").getValue().toString();
                                    String city = usersdataSnapshot.child("city").getValue().toString();
                                    String closed = usersdataSnapshot.child("closed").getValue().toString();
                                    firstposttime = (Long) usersdataSnapshot.child("negatedtime").getValue();
                                    firstposttime = (Long) usersdataSnapshot.child("negatedtime").getValue();
                                    mostrecent_startdate = (Long) usersdataSnapshot.child("mostrecent_startdate").getValue();
                                    mostrecent_wagesrange = (Long) usersdataSnapshot.child("mostrecent_wagesrange").getValue();
                                    mostrecent_wagesrange_startdate = (Long) usersdataSnapshot.child("mostrecent_wagesrange_startdate").getValue();


                                    if (usersdataSnapshot.hasChild("wages")) {
                                        String wages = usersdataSnapshot.child("wages").getValue().toString();
                                        jobs.setWages(wages);
                                    }
                                    if (usersdataSnapshot.hasChild("date")) {
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
                                        homeRecyclerAdapter.notifyItemInserted(joblist.size() - 1);
                                    }
                                }
                            }

                            if (count < loadlimit){
                                loading = false;
                            }
                            else{
                                loading = true;
                            }

                            Log.d(TAG, "firstposttimead: " + firstposttime);

                            //In case all the jobs are closed, so empty list
                            if (joblist.isEmpty()) {
                                loading = false;
                               // mSwipeRefreshLayout.setEnabled(false);
                                mnestedscroll.setVisibility(View.GONE);
                                mnojobLay.setVisibility(VISIBLE);
                                mjobnotfoundtxt.setText("Sorry, it looks like there aren't any jobs available in " + city);
                            } else {
                              //  mSwipeRefreshLayout.setEnabled(true);
                                mnojobLay.setVisibility(GONE);
                                mnestedscroll.setVisibility(VISIBLE);
                                homeRecyclerAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                } else {
                  //  mSwipeRefreshLayout.setEnabled(false);
                    mnestedscroll.setVisibility(View.GONE);
                    mnojobLay.setVisibility(VISIBLE);
                    mjobnotfoundtxt.setText("Sorry, it looks like there aren't any jobs available in " + city);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    protected void loadMoreNormalJob(final String city, final int loadlimit, final int scenario){

        count = 0;

        //Got start no end
        if(scenario == 1){
            //Got WagesFilter
            Log.d(TAG,"mostrecent_wagesrange_startdate1 " + mostrecent_wagesrange_startdate );

            mQueryLoadMore = mJob.child(city).orderByChild("mostrecent_wagesrange_startdate").limitToFirst(loadlimit+1).startAt(mostrecent_wagesrange_startdate).endAt(starttime);
        }
        else if (scenario == 11){
            //No WagesFilter
            Log.d(TAG,"mostrecent_startdate1 " + mostrecent_startdate );

            mQueryLoadMore = mJob.child(city).orderByChild("mostrecent_startdate").limitToFirst(loadlimit+1).startAt(mostrecent_startdate).endAt(starttime);;
        }

        //Got start got end
        else if (scenario == 2){
            //Got WagesFilter
            Log.d(TAG,"mostrecent_wagesrange_startdate2 " + mostrecent_wagesrange_startdate );

            mQueryLoadMore = mJob.child(city).orderByChild("mostrecent_wagesrange_startdate").limitToFirst(loadlimit+1).startAt(mostrecent_wagesrange_startdate).endAt(starttime);;
        }

        else if (scenario == 22){
            //No WagesFilter
            Log.d(TAG,"mostrecent_startdate22 " + mostrecent_startdate );

            mQueryLoadMore = mJob.child(city).orderByChild("mostrecent_startdate").limitToFirst(loadlimit+1).startAt(mostrecent_startdate).endAt(starttime);;
        }

        //No start no end
        else if (scenario == 3){
            //Got WagesFilter
            Log.d(TAG,"mostrecent_wagesrange3 " + mostrecent_wagesrange );

            mQueryLoadMore = mJob.child(city).orderByChild("mostrecent_wagesrange").limitToFirst(loadlimit+1).startAt(mostrecent_wagesrange).endAt(startwagesfilter);;
        }

        else if (scenario == 33 || scenario == 0){
            //No WagesFilter
            Log.d(TAG,"negatedtime33 " + firstposttime );

            mQueryLoadMore = mJob.child(city).orderByChild("negatedtime").limitToFirst(loadlimit+1).startAt(firstposttime);
        }
        else{
            return;
        }

        mQueryLoadMore.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Boolean firstkey = true;
                for(DataSnapshot usersdataSnapshot1 : dataSnapshot.getChildren()){

                    Job jobs = new Job();

                    if (usersdataSnapshot1.hasChild("title")&& usersdataSnapshot1.hasChild("desc")&& usersdataSnapshot1.hasChild("category")
                            && usersdataSnapshot1.hasChild("company")&& usersdataSnapshot1.hasChild("fulladdress")
                            && usersdataSnapshot1.hasChild("postimage")&& usersdataSnapshot1.hasChild("postkey")
                            && usersdataSnapshot1.hasChild("city")&& usersdataSnapshot1.hasChild("closed")
                            && usersdataSnapshot1.hasChild("negatedtime")) {

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
                        firstposttime = (Long) usersdataSnapshot1.child("negatedtime").getValue();
                        mostrecent_startdate = (Long) usersdataSnapshot1.child("mostrecent_startdate").getValue();
                        mostrecent_wagesrange = (Long) usersdataSnapshot1.child("mostrecent_wagesrange").getValue();
                        mostrecent_wagesrange_startdate = (Long) usersdataSnapshot1.child("mostrecent_wagesrange_startdate").getValue();

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
                            Log.d(TAG, "firstkey: " + firstkey);
                        } else {
                            if (closed.equals("false")) {
                                Log.d(TAG, "moretitle: " + title);
                                joblist.add(jobs);
                            }

                            if (count == loadlimit+1) {
                                joblist.add(null);
                                homeRecyclerAdapter.notifyItemInserted(joblist.size() - 1);
                            }
                        }

                    }
                }

                if (count < loadlimit + 1) {
                    Log.d(TAG, "end loading");
                    homeRecyclerAdapter.notifyDataSetChanged();
                    loading = false;
                }

                else {
                    Log.d(TAG, "cont loading");
                    homeRecyclerAdapter.notifyDataSetChanged();
                    loading = true;
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == 1111) && (resultCode == Activity.RESULT_OK)) {
            // recreate your fragment here

            Bundle bundle = data.getExtras();
            final String selectedcity = bundle.getString("city");
            city = selectedcity;
            mUserLocation.child(mAuth.getCurrentUser().getUid()).child("CurrentCity").setValue(selectedcity);

            final ProgressDialog mdialog = new ProgressDialog(context,R.style.MyTheme);
            mdialog.setCancelable(false);
            mdialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
            mdialog.show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    //FragmentTransaction ft = getFragmentManager().beginTransaction();
                    //ft.detach(ExploreJobFragment.this).attach(ExploreJobFragment.this).commit();
                    loading = true;
                    loadlimit = 8;
                    //startdate = 0 ; enddate = 0; wagescategory = 0; filterbywages = 0;
                    //filterbystart = ""; filterbyend = "";
                    if (!joblist.isEmpty()) {joblist.clear();}
                    presentjobpost(city, filterbystart,filterbyend,filterbywages,scenario);
                    mcurrentLocationtxt.setText(selectedcity);

                    mdialog.dismiss();
                }
            }, 500); //time seconds
        }
        else if ((requestCode == 1111) && (resultCode == Activity.RESULT_CANCELED)) {
            Log.d(TAG, "cancel here");
        }


        if ((requestCode == 8888) && (resultCode == Activity.RESULT_OK)) {
            // recreate your fragment here

            city = data.getStringExtra("city");
            mcurrentLocationtxt.setText(city);

            final ProgressDialog mdialog = new ProgressDialog(context, R.style.MyTheme);
            mdialog.setCancelable(false);
            mdialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
            mdialog.show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (!joblist.isEmpty()) {joblist.clear();}

                    //FragmentTransaction ft = getFragmentManager().beginTransaction();
                    //ft.detach(ExploreJobFragment.this).attach(ExploreJobFragment.this).commit();

                    loading = true;
                    loadlimit = 8;
                    if (!joblist.isEmpty()) {joblist.clear();}
                    checkSortFilter();

                    mdialog.dismiss();
                }
            }, 500); //time seconds
        }


        /*

        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {

                HomeProgress.dismiss();

                Place place = PlacePicker.getPlace(context.getApplicationContext(), data);
                city = "";
                String address = place.getAddress().toString();
                Log.d(TAG, "address: " + address);

                Geocoder geocoder = new Geocoder(context.getApplicationContext(), Locale.getDefault());
                List<Address> addresses;
                try {
                    addresses = geocoder.getFromLocation(place.getLatLng().latitude, place.getLatLng().longitude, 1);
                    if (addresses.size() > 0) {
                        String[] addressSlice = place.getAddress().toString().split(", ");
                        //city = addressSlice[addressSlice.length - 2];
                        city = addresses.get(0).getAdminArea();

                        String postCode = addresses.get(0).getPostalCode();
                        if (city.equals(postCode)) {city = addressSlice[addressSlice.length - 3];}

                        if (city == null) {city = addresses.get(0).getCountryName();}

                        if(address.contains("Pulau Pinang") || address.contains("Penang")) {city = "Penang";}
                        else if (address.contains("Kuala Lumpur")) {city = "Kuala Lumpur";}
                        else if (address.contains("Labuan")) {city = "Labuan";}
                        else if (address.contains("Putrajaya")) {city = "Putrajaya";}
                        else if (address.contains("Johor")) {city = "Johor";}
                        else if (address.contains("Kedah")) {city = "Kedah";}
                        else if (address.contains("Kelantan")) {city = "Kelantan";}
                        else if (address.contains("Melaka")|| address.contains("Melacca")) {city = "Melacca";}
                        else if (address.contains("Negeri Sembilan")|| address.contains("Seremban")) {city = "Negeri Sembilan";}
                        //
                        else if (address.contains("Pahang")) {city = "Pahang";}
                        else if (address.contains("Perak")|| address.contains("Ipoh")) {city = "Perak";}
                        else if (address.contains("Perlis")) {city = "Perlis";}
                        else if (address.contains("Sabah")) {city = "Sabah";}
                        else if (address.contains("Sarawak")) {city = "Sarawak";}
                        else if (address.contains("Selangor")|| address.contains("Shah Alam")|| address.contains("Klang")) {city = "Selangor";}
                        else if (address.contains("Terengganu")) {city = "Terengganu";}

                        Log.d(TAG, "cityhome: " + city);

                        mUserLocation.child(mAuth.getCurrentUser().getUid()).child("CurrentCity").setValue(city);

                        final ProgressDialog mdialog = new ProgressDialog(context,R.style.MyTheme);
                        mdialog.setCancelable(false);
                        mdialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
                        mdialog.show();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                               // HomeFragment.mcurrentLocation.setText(city);

                                FragmentTransaction ft = getFragmentManager().beginTransaction();
                                ft.detach(ExploreJobFragment.this).attach(ExploreJobFragment.this).commit();

                                mdialog.dismiss();
                            }
                        }, 500); //time seconds
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            else if (resultCode == RESULT_CANCELED) {
                HomeProgress.dismiss();
            }
        }*/
    }
}
