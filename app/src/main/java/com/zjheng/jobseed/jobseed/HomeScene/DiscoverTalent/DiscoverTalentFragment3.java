package com.zjheng.jobseed.jobseed.HomeScene.DiscoverTalent;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wang.avi.AVLoadingIndicatorView;
import com.zjheng.jobseed.jobseed.CustomUIClass.ClickableViewPager;
import com.zjheng.jobseed.jobseed.CustomObjectClass.TalentInfo;
import com.zjheng.jobseed.jobseed.HomeScene.BannerDetails;
import com.zjheng.jobseed.jobseed.HomeScene.TalentBannerViewPagerAdapter;
import com.zjheng.jobseed.jobseed.R;
import com.zjheng.jobseed.jobseed.TalentCategories.SubTalentCategoryList;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.facebook.FacebookSdk.getApplicationContext;

public class DiscoverTalentFragment3 extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference mBanner, mCategoryImg, mRecommendedTalent, mTalent, mNewRisingTalent, mPopularTalent;
    private FirebaseApp fbApp;

    private TextView mjobnotfoundtxt;
    private RelativeLayout mnoInternetLay;
    private CardView mretryBtn, meventCardView, mhomeCardView, mlifestyleCardView, msportCardView, mtechCardView, mtutorCardView;
    private NestedScrollView mnestedscroll;
    private ImageView meventimg, mhomeimg, mlifestyleimg, msportimg, mtechimg, mtutorimg ;

    private ProgressDialog HomeProgress;
    private ProgressDialog mProgress;

    private static final String TAG = "DiscoverTalent";
    private static int PLACE_PICKER_REQUEST = 1;

    private String city, maintalenttype;

    private ClickableViewPager mAdsViewPager;

    private ArrayList<String> bannerlist;
    private ArrayList<String> titlelist;
    private ArrayList<String> descriplist;
    int currentPage = -1;
    Timer timer;
    final long DELAY_MS = 5000;//delay in milliseconds before task is to be executed
    final long PERIOD_MS = 5000; // time in milliseconds between successive task executions.

    private RecyclerView mrecommendedlist1, mrecommendedlist2, mrecommendedlist3;
    private LinearLayoutManager mLayoutManager1, mLayoutManager2, mLayoutManager3;
    private ArrayList <TalentInfo> recommendedlist1, recommendedlist2, recommendedlist3;
    private RecommendedTalentRecyclerAdapter recommendedAdapter1, recommendedAdapter2, recommendedAdapter3;


    Activity context;
    View rootView;


    private AVLoadingIndicatorView mavi;
    private RelativeLayout mloadingLay;


    public static DiscoverTalentFragment3 newInstance(String city) {
        DiscoverTalentFragment3 result = new DiscoverTalentFragment3();
        Bundle bundle = new Bundle();
        bundle.putString("city", city);
        result.setArguments(bundle);
        return result;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        city = bundle.getString("city");
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.activity_home_discovertalents2, container, false);

        context = getActivity();

        setHasOptionsMenu(true);

        mavi = (AVLoadingIndicatorView)rootView.findViewById(R.id.avi);
        mloadingLay= (RelativeLayout)rootView.findViewById(R.id.loadingLay);

        mnoInternetLay = (RelativeLayout)rootView.findViewById(R.id.noInternetLay);
        mretryBtn = (CardView)rootView.findViewById(R.id.retryBtn);

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                Log.d(TAG, "connected to wifi");
                // connected to wifi
                mnoInternetLay.setVisibility(GONE);

            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                Log.d(TAG, "connected to data");
                // connected to the mobile provider's data plan
                mnoInternetLay.setVisibility(GONE);
            }
        } else {
            Log.d(TAG, "not connected");
            // not connected to the internet
            mnoInternetLay.setVisibility(VISIBLE);

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

                        } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                            Log.d(TAG, "connected to data");
                            // connected to the mobile provider's data plan
                            mnoInternetLay.setVisibility(GONE);
                        }
                    } else {
                        Log.d(TAG, "not connected");
                        // not connected to the internet
                        mnoInternetLay.setVisibility(VISIBLE);
                    }
                }
            });
        }

        mAuth = FirebaseAuth.getInstance();

        mProgress = new ProgressDialog(context);

        mjobnotfoundtxt= (TextView) rootView.findViewById(R.id.jobnotfoundtxt);

        mnestedscroll = (NestedScrollView) rootView.findViewById(R.id.nestedscroll);

        meventimg = (ImageView) rootView.findViewById(R.id.eventimg);
        msportimg = (ImageView) rootView.findViewById(R.id.sportimg);
        mhomeimg = (ImageView) rootView.findViewById(R.id.homeimg);
        mlifestyleimg = (ImageView) rootView.findViewById(R.id.lifestyleimg);
        mtechimg = (ImageView) rootView.findViewById(R.id.techimg);
        mtutorimg = (ImageView) rootView.findViewById(R.id.tutorimg);

        meventCardView = (CardView) rootView.findViewById(R.id.eventCardView);
        msportCardView = (CardView) rootView.findViewById(R.id.sportCardView);
        mhomeCardView = (CardView) rootView.findViewById(R.id.homeCardView);
        mlifestyleCardView = (CardView) rootView.findViewById(R.id.lifestyleCardView);
        mtechCardView = (CardView) rootView.findViewById(R.id.techCardView);
        mtutorCardView = (CardView) rootView.findViewById(R.id.tutorCardView);

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApiKey("AIzaSyAX3s8Y0CA2RwBF5vxhX9tVqV5Gg1S2UHE")
                .setApplicationId("1:1004980108230:android:4ecc908d28953c07")
                .setDatabaseUrl("https://my-gg-app.firebaseio.com")
                .build();

        boolean hasBeenInitialized = false;
        List<FirebaseApp> fbsLcl = FirebaseApp.getApps(context);
        for (FirebaseApp app : fbsLcl) {
            Log.d(TAG, "all app.getName() =" + app.getName());
            if (app.getName().equals("LanceApp")) {
                Log.d(TAG, "app.getName() =" + app.getName());
                hasBeenInitialized = true;
                fbApp = app;
            }
        }

        if (!hasBeenInitialized) {
            fbApp = FirebaseApp.initializeApp(getApplicationContext(), options, "LanceApp"/*""*/);
        }

        FirebaseDatabase fbDB = FirebaseDatabase.getInstance(fbApp);

        mBanner = fbDB.getReferenceFromUrl("https://my-gg-app.firebaseio.com/").child("Banner");
        mRecommendedTalent = fbDB.getReferenceFromUrl("https://my-gg-app.firebaseio.com/").child("RecommendedTalent");
        mNewRisingTalent = fbDB.getReferenceFromUrl("https://my-gg-app.firebaseio.com/").child("NewRisingTalent");
        mPopularTalent = fbDB.getReferenceFromUrl("https://my-gg-app.firebaseio.com/").child("PopularTalent");
        mCategoryImg = fbDB.getReferenceFromUrl("https://my-gg-app.firebaseio.com/").child("CategoryImage");
        mTalent = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("Talent");

        bannerlist = new ArrayList<String>();
        titlelist = new ArrayList<String>();
        descriplist = new ArrayList<String>();

        mAdsViewPager = (ClickableViewPager) rootView.findViewById(R.id.adscontainer);
        final TalentBannerViewPagerAdapter mviewPagerAdapter = new TalentBannerViewPagerAdapter(titlelist,descriplist,bannerlist,context);
        mAdsViewPager.setAdapter(mviewPagerAdapter);

        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(mAdsViewPager, true);

        mrecommendedlist1 = (RecyclerView) rootView.findViewById(R.id.talent_list);
        mrecommendedlist2 = (RecyclerView) rootView.findViewById(R.id.talent_list2);
        mrecommendedlist3 = (RecyclerView) rootView.findViewById(R.id.talent_list3);

        mLayoutManager1 = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        mLayoutManager2 = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        mLayoutManager3 = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);

        recommendedlist1 = new ArrayList<TalentInfo>();
        recommendedlist2 = new ArrayList<TalentInfo>();
        recommendedlist3 = new ArrayList<TalentInfo>();

        recommendedAdapter1 = new RecommendedTalentRecyclerAdapter(mTalent, recommendedlist1, context);
        recommendedAdapter2 = new RecommendedTalentRecyclerAdapter(mTalent, recommendedlist2, context);
        recommendedAdapter3 = new RecommendedTalentRecyclerAdapter(mTalent, recommendedlist3, context);

        mrecommendedlist1.setLayoutManager(mLayoutManager1);
        mrecommendedlist1.setAdapter(recommendedAdapter1);

        mrecommendedlist2.setLayoutManager(mLayoutManager2);
        mrecommendedlist2.setAdapter(recommendedAdapter2);

        mrecommendedlist3.setLayoutManager(mLayoutManager3);
        mrecommendedlist3.setAdapter(recommendedAdapter3);

        mRecommendedTalent.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnaphot : dataSnapshot.getChildren()) {

                    TalentInfo recommendtalentinfo = new TalentInfo();

                    if (childSnaphot.hasChild("name") && childSnaphot.hasChild("descrip")
                            && childSnaphot.hasChild("image")
                            && childSnaphot.hasChild("ratenum")
                            && childSnaphot.hasChild("ratestar")
                            && childSnaphot.hasChild("price")
                            && childSnaphot.hasChild("postimage")
                            && childSnaphot.hasChild("category")
                            && childSnaphot.hasChild("city")
                            && childSnaphot.hasChild("postkey")
                            ) {

                        String name = childSnaphot.child("name").getValue().toString();
                        String descrip = childSnaphot.child("descrip").getValue().toString();
                        String image = childSnaphot.child("image").getValue().toString();
                        String ratenum = childSnaphot.child("ratenum").getValue().toString();
                        String ratestar = childSnaphot.child("ratestar").getValue().toString();
                        String price = childSnaphot.child("price").getValue().toString();
                        String postimage = childSnaphot.child("postimage").getValue().toString();
                        String category = childSnaphot.child("category").getValue().toString();
                        String city = childSnaphot.child("city").getValue().toString();
                        String postkey = childSnaphot.child("postkey").getValue().toString();

                        recommendtalentinfo.setname(name);
                        recommendtalentinfo.setimage(image);
                        recommendtalentinfo.setdesc(descrip);
                        recommendtalentinfo.setstring_reviewstar(ratestar);
                        recommendtalentinfo.setstring_reviewcount(ratenum);
                        recommendtalentinfo.setrates(price);
                        recommendtalentinfo.setpostimage(postimage);
                        recommendtalentinfo.setcity(city);
                        recommendtalentinfo.setcategory(category);
                        recommendtalentinfo.setpostkey(postkey);

                        recommendedlist1.add(recommendtalentinfo);

                    }
                }

                recommendedAdapter1.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mNewRisingTalent.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnaphot : dataSnapshot.getChildren()) {

                    TalentInfo recommendtalentinfo = new TalentInfo();

                    if (childSnaphot.hasChild("name") && childSnaphot.hasChild("descrip")
                            && childSnaphot.hasChild("image")
                            && childSnaphot.hasChild("ratenum")
                            && childSnaphot.hasChild("ratestar")
                            && childSnaphot.hasChild("price")
                            && childSnaphot.hasChild("postimage")
                            && childSnaphot.hasChild("category")
                            && childSnaphot.hasChild("city")
                            && childSnaphot.hasChild("postkey")
                            ) {

                        String name = childSnaphot.child("name").getValue().toString();
                        String descrip = childSnaphot.child("descrip").getValue().toString();
                        String image = childSnaphot.child("image").getValue().toString();
                        String ratenum = childSnaphot.child("ratenum").getValue().toString();
                        String ratestar = childSnaphot.child("ratestar").getValue().toString();
                        String price = childSnaphot.child("price").getValue().toString();
                        String postimage = childSnaphot.child("postimage").getValue().toString();
                        String category = childSnaphot.child("category").getValue().toString();
                        String city = childSnaphot.child("city").getValue().toString();
                        String postkey = childSnaphot.child("postkey").getValue().toString();

                        recommendtalentinfo.setname(name);
                        recommendtalentinfo.setimage(image);
                        recommendtalentinfo.setdesc(descrip);
                        recommendtalentinfo.setstring_reviewstar(ratestar);
                        recommendtalentinfo.setstring_reviewcount(ratenum);
                        recommendtalentinfo.setrates(price);
                        recommendtalentinfo.setpostimage(postimage);
                        recommendtalentinfo.setcity(city);
                        recommendtalentinfo.setcategory(category);
                        recommendtalentinfo.setpostkey(postkey);

                        recommendedlist2.add(recommendtalentinfo);

                    }
                }

                recommendedAdapter2.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mPopularTalent.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnaphot : dataSnapshot.getChildren()) {

                    TalentInfo recommendtalentinfo = new TalentInfo();

                    if (childSnaphot.hasChild("name") && childSnaphot.hasChild("descrip")
                            && childSnaphot.hasChild("image")
                            && childSnaphot.hasChild("ratenum")
                            && childSnaphot.hasChild("ratestar")
                            && childSnaphot.hasChild("price")
                            && childSnaphot.hasChild("postimage")
                            && childSnaphot.hasChild("category")
                            && childSnaphot.hasChild("city")
                            && childSnaphot.hasChild("postkey")
                            ) {

                        String name = childSnaphot.child("name").getValue().toString();
                        String descrip = childSnaphot.child("descrip").getValue().toString();
                        String image = childSnaphot.child("image").getValue().toString();
                        String ratenum = childSnaphot.child("ratenum").getValue().toString();
                        String ratestar = childSnaphot.child("ratestar").getValue().toString();
                        String price = childSnaphot.child("price").getValue().toString();
                        String postimage = childSnaphot.child("postimage").getValue().toString();
                        String category = childSnaphot.child("category").getValue().toString();
                        String city = childSnaphot.child("city").getValue().toString();
                        String postkey = childSnaphot.child("postkey").getValue().toString();

                        recommendtalentinfo.setname(name);
                        recommendtalentinfo.setimage(image);
                        recommendtalentinfo.setdesc(descrip);
                        recommendtalentinfo.setstring_reviewstar(ratestar);
                        recommendtalentinfo.setstring_reviewcount(ratenum);
                        recommendtalentinfo.setrates(price);
                        recommendtalentinfo.setpostimage(postimage);
                        recommendtalentinfo.setcity(city);
                        recommendtalentinfo.setcategory(category);
                        recommendtalentinfo.setpostkey(postkey);

                        recommendedlist3.add(recommendtalentinfo);

                    }
                }

                recommendedAdapter3.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mCategoryImg.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int count = 0;
                for (DataSnapshot categorySnaphot : dataSnapshot.getChildren()) {
                    count++;
                    String image =(String)categorySnaphot.getValue();
                    Log.d(TAG, "image "+dataSnapshot.getKey());
                    loadcategoryimg(count,image);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mBanner.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild("BannerPicture")) {
                    for (DataSnapshot bannerSnaphot : dataSnapshot.child("BannerPicture").getChildren()) {
                        String image =(String)bannerSnaphot.getValue();
                        bannerlist.add(image);
                    }
                }

                if (dataSnapshot.hasChild("BannerTitle")) {
                    for (DataSnapshot bannerSnaphot : dataSnapshot.child("BannerTitle").getChildren()) {
                        String title =(String)bannerSnaphot.getValue();
                        titlelist.add(title);
                    }
                }

                if (dataSnapshot.hasChild("BannerDescrip")) {
                    for (DataSnapshot bannerSnaphot : dataSnapshot.child("BannerDescrip").getChildren()) {
                        String descrip =(String)bannerSnaphot.getValue();
                        descriplist.add(descrip);
                    }
                }

                mviewPagerAdapter.notifyDataSetChanged();

                fadeOut(mloadingLay);

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

                openSubTalentCategoryList(position, city);

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


        meventCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SubTalentCategoryList.class);
                intent.putExtra("talenttype", "Event");
                intent.putExtra("city", city);
                startActivity(intent);
            }
        });

        msportCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SubTalentCategoryList.class);
                intent.putExtra("talenttype", "Sport");
                intent.putExtra("city", city);
                startActivity(intent);
            }
        });

        mhomeCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SubTalentCategoryList.class);
                intent.putExtra("talenttype", "Home");
                intent.putExtra("city", city);
                startActivity(intent);
            }
        });

        mlifestyleCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SubTalentCategoryList.class);
                intent.putExtra("talenttype", "Lifestyle");
                intent.putExtra("city", city);
                startActivity(intent);
            }
        });

        mtechCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SubTalentCategoryList.class);
                intent.putExtra("talenttype", "Technology");
                intent.putExtra("city", city);
                startActivity(intent);
            }
        });

        mtutorCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SubTalentCategoryList.class);
                intent.putExtra("talenttype", "Tutor");
                intent.putExtra("city", city);
                startActivity(intent);
            }
        });

        return rootView;
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

    private void loadcategoryimg(int categoryImgInt, String imgurl) {
        ImageView categoryImgView = null;

        switch (categoryImgInt) {
            case 1:
                categoryImgView = meventimg;
                break;
            case 2:
                categoryImgView = mhomeimg;
                break;
            case 3:
                categoryImgView = mlifestyleimg;
                break;
            case 4:
                categoryImgView = msportimg;
                break;
            case 5:
                categoryImgView = mtechimg;
                break;
            case 6:
                categoryImgView = mtutorimg;
                break;
        }
        if (categoryImgView != null) {
            Log.d(TAG, "imgurl "+imgurl);
            Glide.with(context)
                    .load(imgurl)
                    .centerCrop()
                    .dontAnimate()
                    .into(categoryImgView);
        }
    }

    private void openSubTalentCategoryList(final int  position, final String city){

        switch (position) {
            case 0:
                maintalenttype = "Event";
                break;
            case 1:
                maintalenttype = "Home";
                break;
            case 2:
                maintalenttype = "Lifestyle";
                break;
            case 3:
                maintalenttype = "Sport";
                break;
            case 4:
                maintalenttype = "Technology";
                break;
            case 5:
                maintalenttype = "Tutor";
                break;
        }

        Intent intent = new Intent(context, SubTalentCategoryList.class);
        intent.putExtra("talenttype", maintalenttype);
        intent.putExtra("city", city);
        startActivity(intent);
    }

}
