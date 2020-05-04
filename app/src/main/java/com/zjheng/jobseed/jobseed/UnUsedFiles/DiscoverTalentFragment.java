package com.zjheng.jobseed.jobseed.UnUsedFiles;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.zjheng.jobseed.jobseed.CustomUIClass.ClickableViewPager;
import com.zjheng.jobseed.jobseed.HomeScene.BannerDetails;
import com.zjheng.jobseed.jobseed.HomeScene.TalentBannerViewPagerAdapter;
import com.zjheng.jobseed.jobseed.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.facebook.FacebookSdk.getApplicationContext;

public class DiscoverTalentFragment extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference mBanner, mCategoryImg;
    private FirebaseApp fbApp;

    private TextView mjobnotfoundtxt;
    private RelativeLayout mnoInternetLay;
    private CardView mretryBtn;
    private NestedScrollView mnestedscroll;
    private ImageView mmusicimg, msportimg, mchessimg, mphotoimg, mculinaryimg, mtechimg, mhandymanimg, mtourguideimg;

    private ProgressDialog HomeProgress;
    private ProgressDialog mProgress;

    private static final String TAG = "DiscoverTalent";
    private static int PLACE_PICKER_REQUEST = 1;

    private String city;

    private ClickableViewPager mAdsViewPager;

    private ArrayList<String> bannerlist;
    private ArrayList<String> titlelist;
    private ArrayList<String> descriplist;
    int currentPage = -1;
    Timer timer;
    final long DELAY_MS = 5000;//delay in milliseconds before task is to be executed
    final long PERIOD_MS = 5000; // time in milliseconds between successive task executions.


    Activity context;
    View rootView;


    public static DiscoverTalentFragment newInstance(String city) {
        DiscoverTalentFragment result = new DiscoverTalentFragment();
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

        rootView = inflater.inflate(R.layout.activity_home_discovertalents, container, false);

        context = getActivity();

        setHasOptionsMenu(true);

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

        mmusicimg= (ImageView) rootView.findViewById(R.id.musicimg);
        msportimg= (ImageView) rootView.findViewById(R.id.sportimg);
        mchessimg= (ImageView) rootView.findViewById(R.id.chessimg);
        mphotoimg= (ImageView) rootView.findViewById(R.id.photoimg);
        mculinaryimg= (ImageView) rootView.findViewById(R.id.culinaryimg);
        mtechimg= (ImageView) rootView.findViewById(R.id.techimg);
        mhandymanimg= (ImageView) rootView.findViewById(R.id.handymanimg);
        mtourguideimg= (ImageView) rootView.findViewById(R.id.tourguideimg);

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

        fbDB.getReferenceFromUrl("https://my-gg-app.firebaseio.com/").child("Sohaui").push().setValue("aa");

        mBanner = fbDB.getReferenceFromUrl("https://my-gg-app.firebaseio.com/").child("Banner");
        mCategoryImg = fbDB.getReferenceFromUrl("https://my-gg-app.firebaseio.com/").child("CategoryImage");

        bannerlist = new ArrayList<String>();
        titlelist = new ArrayList<String>();
        descriplist = new ArrayList<String>();

        mAdsViewPager = (ClickableViewPager) rootView.findViewById(R.id.adscontainer);
        final TalentBannerViewPagerAdapter mviewPagerAdapter = new TalentBannerViewPagerAdapter(titlelist,descriplist,bannerlist,context);
        mAdsViewPager.setAdapter(mviewPagerAdapter);

        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(mAdsViewPager, true);

        mCategoryImg.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int count = 0;
                for (DataSnapshot categorySnaphot : dataSnapshot.getChildren()) {
                    count++;
                    String image =(String)categorySnaphot.getValue();
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
                        Log.d(TAG, "image banner =" + image);
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

        return rootView;
    }

    private void loadcategoryimg(int categoryImgInt, String imgurl) {
        ImageView categoryImgView = null;
        switch (categoryImgInt) {
            case 1:
                categoryImgView = mchessimg;
                break;
            case 2:
                categoryImgView = mculinaryimg;
                break;
            case 3:
                categoryImgView = mhandymanimg;
                break;
            case 4:
                categoryImgView = mmusicimg;
                break;
            case 5:
                categoryImgView = mphotoimg;
                break;
            case 6:
                categoryImgView = msportimg;
                break;
            case 7:
                categoryImgView = mtechimg;
                break;
            case 8:
                categoryImgView = mtourguideimg;
                break;
        }
        if (categoryImgView != null) {
            Glide.with(context)
                    .load(imgurl)
                    .centerCrop()
                    .dontAnimate()
                    .into(categoryImgView);
        }
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

}
