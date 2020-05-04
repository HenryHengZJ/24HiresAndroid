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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.zjheng.jobseed.jobseed.CustomObjectClass.Job;
import com.zjheng.jobseed.jobseed.JobDetail;
import com.zjheng.jobseed.jobseed.LocationList;
import com.zjheng.jobseed.jobseed.R;
import com.zjheng.jobseed.jobseed.RemovedJob;
import com.zjheng.jobseed.jobseed.TalentCategories.SubTalentCategoryList;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class SearchBar extends AppCompatActivity {

    private Query mQueryCurrentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference mJob, mUserLocation;
    private RecyclerView mJobList;
    private LinearLayoutManager mLayoutManager;
    private EditText msearchBar;
    private String searchtext,bigsearchtext, city;
    private TextView mLocationBar, mjobCategoryTxt;
    private ImageButton mbackBtn;
    private ImageButton mremoveBtn;
    private NestedScrollView mcategorynestedscroll;
    private RelativeLayout mnoInternetLay;
    private CardView mretryBtn, mtalentCardView;

    private FirebaseRecyclerAdapter<Job,BlogViewHolder> firebaseRecyclerAdapter;

    private LinearLayout mcategorylist;
    private CardView  mLocationCardView;

    private CardView mBaristaCardView, mWaiterCardView , mEventCardView, mOfficeCardView, mPromoterCardView, mRoadshowCardView;

    private CardView mTalentEventCardView, mTalentHomeCardView , mTalentLifestyleCardView, mTalentSportCardView, mTalentTechCardView, mTalentTutorCardView;

    private ProgressDialog mProgress;

    private static int PLACE_PICKER_REQUEST = 1;

    final android.os.Handler handler = new android.os.Handler();
    Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_bar2);

        city = getIntent().getStringExtra("city_id");

        mcategorynestedscroll = (NestedScrollView) findViewById(R.id.categorynestedscroll);
        mJobList = (RecyclerView)findViewById(R.id.searchlist);
        mcategorylist = (LinearLayout) findViewById(R.id.categorylist);
        mnoInternetLay = (RelativeLayout)findViewById(R.id.noInternetLay);
        mretryBtn = (CardView)findViewById(R.id.retryBtn);
        mtalentCardView = (CardView)findViewById(R.id.talentCardView);

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                //Connected
                mnoInternetLay.setVisibility(GONE);
                mcategorynestedscroll.setVisibility(VISIBLE);
                mJobList.setVisibility(VISIBLE);
                mcategorylist.setVisibility(VISIBLE);

            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                //Connected
                mnoInternetLay.setVisibility(GONE);
                mcategorynestedscroll.setVisibility(VISIBLE);
                mJobList.setVisibility(VISIBLE);
                mcategorylist.setVisibility(VISIBLE);
            }
        } else {
            //Disconnected
            Toast.makeText(SearchBar.this, "Network Not Available", Toast.LENGTH_LONG).show();
            mnoInternetLay.setVisibility(VISIBLE);
            mcategorynestedscroll.setVisibility(GONE);
            mJobList.setVisibility(GONE);
            mcategorylist.setVisibility(GONE);

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
                            mJobList.setVisibility(VISIBLE);
                            mcategorylist.setVisibility(VISIBLE);

                        } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                            //Connected
                            mnoInternetLay.setVisibility(GONE);
                            mcategorynestedscroll.setVisibility(VISIBLE);
                            mJobList.setVisibility(VISIBLE);
                            mcategorylist.setVisibility(VISIBLE);
                        }
                    } else {
                        //Disconnected
                        Toast.makeText(SearchBar.this, "Network Not Available", Toast.LENGTH_LONG).show();
                        mnoInternetLay.setVisibility(VISIBLE);
                        mcategorynestedscroll.setVisibility(GONE);
                        mJobList.setVisibility(GONE);
                        mcategorylist.setVisibility(GONE);
                    }
                }
            });
        }

        mAuth = FirebaseAuth.getInstance();

        mJob =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("Job");

        mUserLocation =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserLocation");

        msearchBar = (EditText) findViewById(R.id.searchBar);
        mLocationBar = (TextView) findViewById(R.id.LocationBar);
        mjobCategoryTxt = (TextView) findViewById(R.id.jobCategoryTxt);

        if(city==null){
            mLocationBar.setText("Specify your location");
        }
        else{
            mLocationBar.setText(city);
        }

        mProgress = new ProgressDialog(this);

        mbackBtn = (ImageButton) findViewById(R.id.backBtn);
        mremoveBtn = (ImageButton) findViewById(R.id.removeBtn);

        mBaristaCardView = (CardView) findViewById(R.id.BaristaCardView);
        mEventCardView = (CardView) findViewById(R.id.EventCardView);
        mOfficeCardView = (CardView) findViewById(R.id.OfficeCardView);
        mPromoterCardView = (CardView) findViewById(R.id.PromoterCardView);
        mRoadshowCardView = (CardView) findViewById(R.id.RoadshowCardView);
        mWaiterCardView = (CardView) findViewById(R.id.WaiterCardView);

        mTalentEventCardView = (CardView) findViewById(R.id.TalentEventCardView);
        mTalentHomeCardView = (CardView) findViewById(R.id.TalentHomeCardView);
        mTalentLifestyleCardView = (CardView) findViewById(R.id.TalentLifestyleCardView);
        mTalentSportCardView = (CardView) findViewById(R.id.TalentSportCardView);
        mTalentTechCardView = (CardView) findViewById(R.id.TalentTechCardView);
        mTalentTutorCardView = (CardView) findViewById(R.id.TalentTutorCardView);

        mLocationCardView = (CardView) findViewById(R.id.LocationCardView);

        mJobList.setHasFixedSize(false);
        //mJobList.setFocusable(false);
        mJobList.setNestedScrollingEnabled(false);

        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        mJobList.setLayoutManager(mLayoutManager);

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

        mjobCategoryTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent singleblogintent = new Intent(SearchBar.this, SearchJobCategoryBar.class);
                singleblogintent.putExtra("city_id", city);
                startActivity(singleblogintent);
            }
        });

        mtalentCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(msearchBar.getText().toString())) {

                    String searchtext = msearchBar.getText().toString();
                    String city = mLocationBar.getText().toString();

                    Intent singleblogintent = new Intent(SearchBar.this, SearchTalentBar.class);
                    singleblogintent.putExtra("city_id", city);
                    singleblogintent.putExtra("searchtext", searchtext);
                    startActivity(singleblogintent);
                }
            }
        });

        msearchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch();
                    return true;
                }
                return false;
            }
        });

        msearchBar.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(final Editable s) {

                runnable = new Runnable() {
                    @Override
                    public void run() {
                        //do some work with s.toString()
                        if(s.length() != 0) {

                            final FirebaseRecyclerAdapter oldAdapter = (FirebaseRecyclerAdapter) mJobList.getAdapter();
                            if (oldAdapter != null) {
                                oldAdapter.cleanup();
                            }

                            bigsearchtext = s.toString();
                            searchtext = s.toString().toLowerCase();
                            String location = mLocationBar.getText().toString();

                            mcategorylist.setVisibility(View.GONE);
                            mtalentCardView.setVisibility(View.VISIBLE);
                            mJobList.setVisibility(View.VISIBLE);

                            mQueryCurrentUser = mJob.child(location).orderByChild("lowertitle").startAt(searchtext).endAt(searchtext+"~").limitToFirst(20);

                            firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Job, BlogViewHolder>(
                                    Job.class,
                                    R.layout.searchlist,
                                    BlogViewHolder.class,
                                    mQueryCurrentUser

                            ) {

                                @Override
                                protected void populateViewHolder(BlogViewHolder viewHolder, Job model, int position) {

                                    final String post_key = getRef(position).getKey();//all blog post key

                                    viewHolder.setTitle(model.getTitle());

                                    viewHolder.mcardview.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            final ProgressDialog mProgressDialog;
                                            mProgressDialog = new ProgressDialog(SearchBar.this,R.style.MyTheme);
                                            mProgressDialog.setCancelable(false);
                                            mProgressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
                                            mProgressDialog.show();

                                            new Thread()
                                            {

                                                public void run()
                                                {
                                                    if(city!=null) {

                                                        mJob.child(city).child(post_key).addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                if(dataSnapshot.exists()){
                                                                    Intent jobdetailintent = new Intent(SearchBar.this, JobDetail.class);
                                                                    jobdetailintent.putExtra("post_id", post_key);
                                                                    jobdetailintent.putExtra("city_id", city);
                                                                    startActivity(jobdetailintent);
                                                                    mProgressDialog.dismiss();
                                                                }
                                                                else{
                                                                    Intent jobdetailintent = new Intent(SearchBar.this, RemovedJob.class);
                                                                    startActivity(jobdetailintent);
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

                            mJobList.setAdapter(firebaseRecyclerAdapter);
                        }
                        else{
                            mJobList.setVisibility(View.GONE);
                            mtalentCardView.setVisibility(View.GONE);
                            mcategorylist.setVisibility(View.VISIBLE);
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

                Intent intent = new Intent(SearchBar.this, LocationList.class);
                intent.putExtra("locationstatus", "");
                intent.putExtra("travellocations", "");
                startActivityForResult(intent, 1111);

               /* mProgress.setMessage("Loading");
                mProgress.setCancelable(false);
                mProgress.show();

                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                Intent intent;
                try {
                    intent = builder.build(SearchBar.this);
                    startActivityForResult(intent, PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }*/
            }
        });

        mTalentEventCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchBar.this, SubTalentCategoryList.class);
                intent.putExtra("talenttype", "Event");
                intent.putExtra("city", city);
                startActivity(intent);
            }
        });

        mTalentSportCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchBar.this, SubTalentCategoryList.class);
                intent.putExtra("talenttype", "Sport");
                intent.putExtra("city", city);
                startActivity(intent);
            }
        });

        mTalentHomeCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchBar.this, SubTalentCategoryList.class);
                intent.putExtra("talenttype", "Home");
                intent.putExtra("city", city);
                startActivity(intent);
            }
        });

        mTalentLifestyleCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchBar.this, SubTalentCategoryList.class);
                intent.putExtra("talenttype", "Lifestyle");
                intent.putExtra("city", city);
                startActivity(intent);
            }
        });

        mTalentTechCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchBar.this, SubTalentCategoryList.class);
                intent.putExtra("talenttype", "Technology");
                intent.putExtra("city", city);
                startActivity(intent);
            }
        });

        mTalentTutorCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchBar.this, SubTalentCategoryList.class);
                intent.putExtra("talenttype", "Tutor");
                intent.putExtra("city", city);
                startActivity(intent);
            }
        });

        mBaristaCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(city!=null) {
                    Intent singleblogintent = new Intent(SearchBar.this, SearchCategory.class);
                    singleblogintent.putExtra("category_title", "Barista / Bartender");
                    singleblogintent.putExtra("city_id", city);
                    startActivity(singleblogintent);
                }
                else{
                    Toast.makeText(SearchBar.this, "Please specify a location", Toast.LENGTH_LONG).show();
                }
            }
        });

        mEventCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(city!=null) {
                    Intent singleblogintent = new Intent(SearchBar.this, SearchCategory.class);
                    singleblogintent.putExtra("category_title", "Event Crew");
                    singleblogintent.putExtra("city_id", city);
                    startActivity(singleblogintent);
                }
                else{
                    Toast.makeText(SearchBar.this, "Please specify a location", Toast.LENGTH_LONG).show();
                }
            }
        });



        mOfficeCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(city!=null) {
                    Intent singleblogintent = new Intent(SearchBar.this, SearchCategory.class);
                    singleblogintent.putExtra("category_title", "Office / Admin");
                    singleblogintent.putExtra("city_id", city);
                    startActivity(singleblogintent);
                }
                else{
                    Toast.makeText(SearchBar.this, "Please specify a location", Toast.LENGTH_LONG).show();
                }
            }
        });

        mPromoterCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(city!=null) {
                    Intent singleblogintent = new Intent(SearchBar.this, SearchCategory.class);
                    singleblogintent.putExtra("category_title", "Promoter / Sampling");
                    singleblogintent.putExtra("city_id", city);
                    startActivity(singleblogintent);
                }
                else{
                    Toast.makeText(SearchBar.this, "Please specify a location", Toast.LENGTH_LONG).show();
                }
            }
        });

        mRoadshowCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(city!=null) {
                    Intent singleblogintent = new Intent(SearchBar.this, SearchCategory.class);
                    singleblogintent.putExtra("category_title", "Roadshow");
                    singleblogintent.putExtra("city_id", city);
                    startActivity(singleblogintent);
                }
                else{
                    Toast.makeText(SearchBar.this, "Please specify a location", Toast.LENGTH_LONG).show();
                }
            }
        });

        mWaiterCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(city!=null) {
                    Intent singleblogintent = new Intent(SearchBar.this, SearchCategory.class);
                    singleblogintent.putExtra("category_title", "Waiter / Waitress");
                    singleblogintent.putExtra("city_id", city);
                    startActivity(singleblogintent);
                }
                else{
                    Toast.makeText(SearchBar.this, "Please specify a location", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (firebaseRecyclerAdapter != null) {
            firebaseRecyclerAdapter.cleanup();
        }
        super.onDestroy();
    }

    private void performSearch(){

        if(city!=null && bigsearchtext != null) {
            Intent singleblogintent = new Intent(SearchBar.this, SearchResult.class);
            singleblogintent.putExtra("blog_title", bigsearchtext);
            singleblogintent.putExtra("city_id", city);
            startActivity(singleblogintent);
        }
        else{
            Toast.makeText(SearchBar.this, "Please specify a location", Toast.LENGTH_LONG).show();
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

            final ProgressDialog mdialog = new ProgressDialog(SearchBar.this,R.style.MyTheme);
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
                Place place = PlacePicker.getPlace(SearchBar.this, data);
                city = "";
                String address = place.getAddress().toString();

                Geocoder geocoder = new Geocoder(SearchBar.this, Locale.getDefault());
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
