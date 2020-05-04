package com.zjheng.jobseed.jobseed.SearchJobScene;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.zjheng.jobseed.jobseed.CustomObjectClass.Job;
import com.zjheng.jobseed.jobseed.CustomObjectClass.TalentInfo;
import com.zjheng.jobseed.jobseed.HomeScene.ExploreJobs.HomeRecyclerAdapter;
import com.zjheng.jobseed.jobseed.JobDetail;
import com.zjheng.jobseed.jobseed.LocationList;
import com.zjheng.jobseed.jobseed.R;
import com.zjheng.jobseed.jobseed.RemovedJob;
import com.zjheng.jobseed.jobseed.RemovedTalent;
import com.zjheng.jobseed.jobseed.SearchTalentScene.SearchTalent;
import com.zjheng.jobseed.jobseed.TalentCategories.SubTalentCategoryList;
import com.zjheng.jobseed.jobseed.TalentDetails.TalentDetail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.facebook.FacebookSdk.getApplicationContext;

public class SearchTalentBar extends AppCompatActivity {

    private Query mQueryCurrentQuery;
    private FirebaseAuth mAuth;
    private DatabaseReference mTalent, mUserLocation, mTalentTitle;
    private FirebaseApp fbApp;

    private EditText msearchBar;
    private String searchtext,bigsearchtext, city, lastsearchtext;
    private TextView mLocationBar;
    private ImageButton mbackBtn;
    private ImageButton mremoveBtn;
    private NestedScrollView mcategorynestedscroll;
    private RelativeLayout mnoInternetLay;
    private CardView mretryBtn,mLocationCardView;

    private ProgressDialog mProgress;

    private static int PLACE_PICKER_REQUEST = 1;

    private int loadlimit = 20;

    private RecyclerView mTalentList;
    private LinearLayoutManager mLayoutManager;

    private FirebaseRecyclerAdapter<TalentInfo,BlogViewHolder> firebaseRecyclerAdapter;


    final Handler handler = new Handler();
    Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talentsearch_bar);


        Intent intent = getIntent();
        if(intent.hasExtra("city_id")){
            if (getIntent().getExtras().getString("city_id") != null) {
                city = getIntent().getExtras().getString("city_id");
            }
        }
        if(intent.hasExtra("searchtext")){
            if (getIntent().getExtras().getString("searchtext") != null) {
                lastsearchtext = getIntent().getExtras().getString("searchtext");
            }
        }


        mcategorynestedscroll = (NestedScrollView) findViewById(R.id.categorynestedscroll);
        mTalentList = (RecyclerView)findViewById(R.id.searchlist);
        mnoInternetLay = (RelativeLayout)findViewById(R.id.noInternetLay);
        mretryBtn = (CardView)findViewById(R.id.retryBtn);

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                //Connected
                mnoInternetLay.setVisibility(GONE);
                mcategorynestedscroll.setVisibility(VISIBLE);

            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                //Connected
                mnoInternetLay.setVisibility(GONE);
                mcategorynestedscroll.setVisibility(VISIBLE);
            }
        } else {
            //Disconnected
            Toast.makeText(SearchTalentBar.this, "Network Not Available", Toast.LENGTH_LONG).show();
            mnoInternetLay.setVisibility(VISIBLE);
            mcategorynestedscroll.setVisibility(GONE);

            mretryBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                    if (activeNetwork != null) { // connected to the internet
                        if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                            //Connected
                            mnoInternetLay.setVisibility(GONE);
                            mcategorynestedscroll.setVisibility(VISIBLE);

                        } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                            //Connected
                            mnoInternetLay.setVisibility(GONE);
                            mcategorynestedscroll.setVisibility(VISIBLE);
                        }
                    } else {
                        //Disconnected
                        Toast.makeText(SearchTalentBar.this, "Network Not Available", Toast.LENGTH_LONG).show();
                        mnoInternetLay.setVisibility(VISIBLE);
                        mcategorynestedscroll.setVisibility(GONE);
                    }
                }
            });
        }

        mAuth = FirebaseAuth.getInstance();

        mTalent = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("Talent");

        mUserLocation =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserLocation");

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApiKey("AIzaSyAX3s8Y0CA2RwBF5vxhX9tVqV5Gg1S2UHE")
                .setApplicationId("1:1004980108230:android:4ecc908d28953c07")
                .setDatabaseUrl("https://my-gg-app.firebaseio.com")
                .build();

        boolean hasBeenInitialized = false;
        List<FirebaseApp> fbsLcl = FirebaseApp.getApps(SearchTalentBar.this);
        for (FirebaseApp app : fbsLcl) {
            if (app.getName().equals("LanceApp")) {
                hasBeenInitialized = true;
                fbApp = app;
            }
        }

        if (!hasBeenInitialized) {
            fbApp = FirebaseApp.initializeApp(getApplicationContext(), options, "LanceApp"/*""*/);
        }

        FirebaseDatabase fbDB = FirebaseDatabase.getInstance(fbApp);

        mTalentTitle = fbDB.getReferenceFromUrl("https://my-gg-app.firebaseio.com/").child("Talent");

        msearchBar = (EditText) findViewById(R.id.searchBar);

        mLocationBar = (TextView) findViewById(R.id.LocationBar);

        if(city==null){
            mLocationBar.setText("Specify your location");
        }
        else{
            mLocationBar.setText(city);
        }

        mProgress = new ProgressDialog(this);

        mbackBtn = (ImageButton) findViewById(R.id.backBtn);
        mremoveBtn = (ImageButton) findViewById(R.id.removeBtn);
        mLocationCardView = (CardView) findViewById(R.id.LocationCardView);

        mTalentList.setHasFixedSize(false);
        mTalentList.setNestedScrollingEnabled(false);

        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        mTalentList.setLayoutManager(mLayoutManager);

        mbackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mremoveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                msearchBar.setText("");
            }
        });

       /* msearchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch();
                    return true;
                }
                return false;
            }
        });*/

        msearchBar.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(final Editable s) {

                runnable = new Runnable() {
                    @Override
                    public void run() {
                        //do some work with s.toString()
                        if(s.length() != 0) {

                            final FirebaseRecyclerAdapter oldAdapter = (FirebaseRecyclerAdapter) mTalentList.getAdapter();
                            if (oldAdapter != null) {
                                oldAdapter.cleanup();
                            }

                            bigsearchtext = s.toString();
                            searchtext = s.toString().toLowerCase();
                            String location = mLocationBar.getText().toString();
                            mTalentList.setVisibility(View.VISIBLE);

                            mQueryCurrentQuery = mTalentTitle.child(location).orderByChild("lowertitle").startAt(searchtext).endAt(searchtext+"~").limitToFirst(loadlimit);

                            mQueryCurrentQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot postSnaphot : dataSnapshot.getChildren()) {
                                        Log.e("searchtalentbar", "postSnaphot "+ postSnaphot.getKey());
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<TalentInfo, BlogViewHolder>(
                                    TalentInfo.class,
                                    R.layout.searchlist,
                                    BlogViewHolder.class,
                                    mQueryCurrentQuery

                            ) {

                                @Override
                                protected void populateViewHolder(BlogViewHolder viewHolder, TalentInfo model, int position) {

                                    final String postkey = getRef(position).getKey();//all blog post key
                                    String category = model.getcategory();
                                    String categorys[] = category.split(" / ");
                                    final String maincategory = categorys[0];
                                    final String subcategory = categorys[1];

                                    viewHolder.setTitle(model.gettitle());
                                    Log.e("searchtalentbar", model.gettitle());

                                    viewHolder.mcardview.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            final ProgressDialog mProgressDialog;
                                            mProgressDialog = new ProgressDialog(SearchTalentBar.this,R.style.MyTheme);
                                            mProgressDialog.setCancelable(false);
                                            mProgressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
                                            mProgressDialog.show();

                                            new Thread()
                                            {

                                                public void run()
                                                {
                                                    if(city!=null) {

                                                        mTalent.child(city).child(maincategory).child(subcategory).child(postkey).addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                if(dataSnapshot.exists()){
                                                                    Intent detailintent = new Intent(SearchTalentBar.this, TalentDetail.class);
                                                                    detailintent.putExtra("post_id", postkey);
                                                                    detailintent.putExtra("city_id", city);
                                                                    detailintent.putExtra("maincategory", maincategory);
                                                                    detailintent.putExtra("subcategory", subcategory);
                                                                    startActivity(detailintent);
                                                                    mProgressDialog.dismiss();
                                                                }
                                                                else{
                                                                    Intent detailintent = new Intent(SearchTalentBar.this, RemovedTalent.class);
                                                                    startActivity(detailintent);
                                                                    mProgressDialog.dismiss();
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {

                                                            }
                                                        });
                                                    }
                                                }

                                            }.start();

                                        }
                                    });

                                }
                            };

                            mTalentList.setAdapter(firebaseRecyclerAdapter);
                        }
                    }
                };
                handler.postDelayed(runnable, 500);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

                handler.removeCallbacks(runnable);

            }
        });

        mLocationCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(SearchTalentBar.this, LocationList.class);
                intent.putExtra("locationstatus", "");
                intent.putExtra("travellocations", "");
                startActivityForResult(intent, 1111);
            }
        });

        msearchBar.setText(lastsearchtext);

    }

    private void querySearch( Query mQueryCurrentQuery) {


    }

    @Override
    protected void onDestroy() {
        if (firebaseRecyclerAdapter != null) {
            firebaseRecyclerAdapter.cleanup();
        }
        super.onDestroy();
    }

    private void performSearch(){

        if(city!=null) {
            Intent singleblogintent = new Intent(SearchTalentBar.this, SearchResult.class);
            singleblogintent.putExtra("blog_title", bigsearchtext);
            singleblogintent.putExtra("city_id", city);
            startActivity(singleblogintent);
        }
        else{
            Toast.makeText(SearchTalentBar.this, "Please specify a location", Toast.LENGTH_LONG).show();
        }
    }


    public static class BlogViewHolder extends RecyclerView.ViewHolder{

        View mView;
        TextView msearchResult;
        CardView mcardview;
        RelativeLayout mRlay;

        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            msearchResult = (TextView) mView.findViewById(R.id.searchResult);
            mcardview = (CardView)mView.findViewById(R.id.cardview);
            mRlay = (RelativeLayout)mView.findViewById(R.id.Rlay);
        }

        public void setTitle(String title){
            msearchResult.setText(title);
        }
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

            final ProgressDialog mdialog = new ProgressDialog(SearchTalentBar.this,R.style.MyTheme);
            mdialog.setCancelable(false);
            mdialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
            mdialog.show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    mUserLocation.child(mAuth.getCurrentUser().getUid()).child("CurrentCity").setValue(city);
                    mLocationBar.setText(city);
                    mdialog.dismiss();


                        Intent intent = getIntent();
                        intent.putExtra("city_id", city);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        finish();
                        overridePendingTransition(0, 0);

                        startActivity(intent);
                        overridePendingTransition(0, 0);

                }
            }, 500); //time seconds
        }
        else if ((requestCode == 1111) && (resultCode == Activity.RESULT_CANCELED)) {
        }

        if (requestCode == PLACE_PICKER_REQUEST) {
            mProgress.dismiss();
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(SearchTalentBar.this, data);
                city = "";
                String address = place.getAddress().toString();

                Geocoder geocoder = new Geocoder(SearchTalentBar.this, Locale.getDefault());
                List<Address> addresses;
                try {
                    addresses = geocoder.getFromLocation(place.getLatLng().latitude, place.getLatLng().longitude, 1);
                    if (addresses.size() > 0) {
                        String[] addressSlice = place.getAddress().toString().split(", ");
                        //city = addressSlice[addressSlice.length - 2];
                        city = addresses.get(0).getAdminArea();

                        String postCode = addresses.get(0).getPostalCode();
                        if (city.equals(postCode)) {
                            city = addressSlice[addressSlice.length - 3];
                        }

                        if (city == null) {
                            city = addresses.get(0).getCountryName();
                        }

                        if(address.contains("Pulau Pinang") || address.contains("Penang"))
                        {
                            city = "Penang";
                        }

                        else if (address.contains("Kuala Lumpur"))
                        {
                            city = "Kuala Lumpur";
                        }

                        else if (address.contains("Labuan"))
                        {
                            city = "Labuan";
                        }

                        else if (address.contains("Putrajaya"))
                        {
                            city = "Putrajaya";
                        }

                        else if (address.contains("Johor"))
                        {
                            city = "Johor";
                        }

                        else if (address.contains("Kedah"))
                        {
                            city = "Kedah";
                        }

                        else if (address.contains("Kelantan"))
                        {
                            city = "Kelantan";
                        }

                        else if (address.contains("Melaka")|| address.contains("Melacca"))
                        {
                            city = "Melacca";
                        }

                        else if (address.contains("Negeri Sembilan")|| address.contains("Seremban"))
                        {
                            city = "Negeri Sembilan";
                        }
                        //
                        else if (address.contains("Pahang"))
                        {
                            city = "Pahang";
                        }

                        else if (address.contains("Perak")|| address.contains("Ipoh"))
                        {
                            city = "Perak";
                        }

                        else if (address.contains("Perlis"))
                        {
                            city = "Perlis";
                        }

                        else if (address.contains("Sabah"))
                        {
                            city = "Sabah";
                        }

                        else if (address.contains("Sarawak"))
                        {
                            city = "Sarawak";
                        }

                        else if (address.contains("Selangor")|| address.contains("Shah Alam")|| address.contains("Klang"))
                        {
                            city = "Selangor";
                        }

                        else if (address.contains("Terengganu"))
                        {
                            city = "Terengganu";
                        }

                        mUserLocation.child(mAuth.getCurrentUser().getUid()).child("CurrentCity").setValue(city);
                        mLocationBar.setText(city);

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }
    }
}
