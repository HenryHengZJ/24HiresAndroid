package com.zjheng.jobseed.jobseed.TalentActivities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.core.GeoHash;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sangcomz.fishbun.FishBun;
import com.sangcomz.fishbun.adapter.image.impl.PicassoAdapter;
import com.sangcomz.fishbun.define.Define;
import com.squareup.timessquare.CalendarPickerView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.zjheng.jobseed.jobseed.ActivitiesScene.EditPostScene.EditCategorySelect;
import com.zjheng.jobseed.jobseed.LocationList;
import com.zjheng.jobseed.jobseed.R;
import com.zjheng.jobseed.jobseed.ShowcaseTalentScene.ImageAdapter;
import com.zjheng.jobseed.jobseed.ShowcaseTalentScene.ShowcaseTalent;
import com.zjheng.jobseed.jobseed.TalentCategories.TalentCategoryList;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.R.attr.category;
import static android.R.attr.data;
import static android.R.attr.y;
import static android.view.View.VISIBLE;
import static com.zjheng.jobseed.jobseed.ActivitiesScene.EditPostScene.EditPost.meditpostCategory;
import static com.zjheng.jobseed.jobseed.R.id.postTalentBtn;
import static com.zjheng.jobseed.jobseed.R.id.spinnercurrency;
import static com.zjheng.jobseed.jobseed.R.id.spinnerrate;

public class EditTalent extends AppCompatActivity implements ImageAdapter.ItemClickListener {


    private Toolbar mToolbar;
    private CardView maddpicCardView1, maddpicCardView2, maddpicCardView3, maddpicCardView4, maddpicCardView5, mseemorecardview;
    private RecyclerView maddpicRecyclerView;
    private ArrayList<Uri> path = new ArrayList<>();
    private ArrayList<Uri> oldpath = new ArrayList<>();
    private ArrayList<String> oldpathString = new ArrayList<>();
    private ArrayList<String> newpathString = new ArrayList<>();
    private HorizontalScrollView mhorizontalScrollView;
    private LinearLayoutManager linearLayoutManager;
    private ImageAdapter imageAdapter;

    private ImageButton mcleardateBtn, mclearwagesBtn, mpostTalentBtn;
    private ImageButton mimgbtn1, mimgbtn2, mimgbtn3, mimgbtn4, mimgbtn5;
    private ImageView mimg1,mimg2,mimg3,mimg4,mimg5;
    private EditText mpostLink1, mpostLink2, mpostTitle,mpostDescrip, mpostCategoryx, mpostBaseLocation, mpostTravelLocation, mpostRates, mpostDate;
    private Spinner mspinnerrate, mspinnercurrency;
    private ExpandableRelativeLayout mexpandableLayout1;

    private ImageView myoutubeimg, myoutubeimg2;

    private ProgressDialog mProgress;
    private FirebaseApp fbApp;
    private FirebaseStorage fbStorage;

    private String imgPressed;

    final android.os.Handler handler = new android.os.Handler();
    Runnable runnable;


    private String finalimageuris, ownuserid, maincategory, subcategory;
    private String travellocations, postkey, city, newmaincategory, newsubcategory, newcity, oldpostimage;
    private int x, imagecount = 0, deleteimagecount = 0;
    private Uri mImgUri1, mImgUri2, mImgUri3, mImgUri4, mImgUri5;
    private String mImgOldString1, mImgOldString2, mImgOldString3, mImgOldString4, mImgOldString5;

    private static final String TAG = "EditTalent";

    private FirebaseAuth mAuth;
    private DatabaseReference mTalent, mUserMyTalent, mTalentVerify, mUserMyTalentAcceptedBookings, mUserMyTalentPendingBookings, mUserBookingMade, mTalentTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talent_post);

        postkey = getIntent().getStringExtra("post_id");
        city = getIntent().getStringExtra("city_id");
        maincategory = getIntent().getStringExtra("maincategory");
        subcategory = getIntent().getStringExtra("subcategory");

        mAuth = FirebaseAuth.getInstance();
        ownuserid = mAuth.getCurrentUser().getUid();

        mTalent =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("Talent");
        mUserMyTalent =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("MyTalent");
        mTalentVerify =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("TalentVerify");

        mUserMyTalentAcceptedBookings = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserMyTalentAcceptedBookings");

        mUserMyTalentPendingBookings = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserMyTalentPendingBookings");

        mUserBookingMade = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserBookingMade");

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApiKey("AIzaSyAX3s8Y0CA2RwBF5vxhX9tVqV5Gg1S2UHE")
                .setApplicationId("1:1004980108230:android:4ecc908d28953c07")
                .setDatabaseUrl("https://my-gg-app.firebaseio.com")
                .build();

        boolean hasBeenInitialized = false;
        List<FirebaseApp> fbsLcl = FirebaseApp.getApps(EditTalent.this);
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
        fbStorage = FirebaseStorage.getInstance(fbApp);

        mTalentTitle = fbDB.getReferenceFromUrl("https://my-gg-app.firebaseio.com/").child("Talent");


        mToolbar = (Toolbar)findViewById(R.id.toolbar_other);

        mProgress = new ProgressDialog(this);

        mhorizontalScrollView = (HorizontalScrollView)findViewById(R.id.horizontalScrollView);
        mcleardateBtn = findViewById(R.id.cleardateBtn);
        mclearwagesBtn = findViewById(R.id.clearwagesBtn);

        mpostLink1 = findViewById(R.id.postLink1);
        mpostLink2 = findViewById(R.id.postLink2);
        mpostTitle = findViewById(R.id.postTitle);
        mpostDescrip = findViewById(R.id.postDescrip);
        mpostCategoryx = findViewById(R.id.postCategoryx);
        mpostBaseLocation = findViewById(R.id.postBaseLocation);
        mpostTravelLocation = findViewById(R.id.postTravelLocation);
        mpostRates = findViewById(R.id.postRates);
        mpostDate = findViewById(R.id.postDate);

        mspinnerrate = findViewById(R.id.spinnerrate);
        mspinnercurrency = findViewById(R.id.spinnercurrency);

        mseemorecardview = findViewById(R.id.seemorecardview);
        mexpandableLayout1 = findViewById(R.id.expandableLayout1);
        mpostTalentBtn = findViewById(postTalentBtn);
        mpostTalentBtn.setEnabled(false);
        mpostTalentBtn.setVisibility(View.GONE);

        myoutubeimg = findViewById(R.id.youtubeimg);
        myoutubeimg2 = findViewById(R.id.youtubeimg2);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Showcase Talent");
        mToolbar.setTitleTextColor(android.graphics.Color.WHITE);

        mToolbar.setNavigationIcon(R.mipmap.ic_close_white_24dp);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        maddpicCardView1 = findViewById(R.id.addpicCardView1);
        maddpicCardView2 = findViewById(R.id.addpicCardView2);
        maddpicCardView3 = findViewById(R.id.addpicCardView3);
        maddpicCardView4 = findViewById(R.id.addpicCardView4);
        maddpicCardView5 = findViewById(R.id.addpicCardView5);

        mimg1 = findViewById(R.id.img1);
        mimg2 = findViewById(R.id.img2);
        mimg3 = findViewById(R.id.img3);
        mimg4 = findViewById(R.id.img4);
        mimg5 = findViewById(R.id.img5);

        mimgbtn1 = findViewById(R.id.imgbtn1);
        mimgbtn2 = findViewById(R.id.imgbtn2);
        mimgbtn3 = findViewById(R.id.imgbtn3);
        mimgbtn4 = findViewById(R.id.imgbtn4);
        mimgbtn5 = findViewById(R.id.imgbtn5);

        maddpicRecyclerView = findViewById(R.id.addpicRecyclerView);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        imageAdapter = new ImageAdapter(this, path);
        maddpicRecyclerView.setLayoutManager(linearLayoutManager);
        imageAdapter.setClickListener(this);
        maddpicRecyclerView.setAdapter(imageAdapter);

        String[] items = new String[]{"per hour", "per day", "per month"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(EditTalent.this, android.R.layout.simple_spinner_dropdown_item, items);
        mspinnerrate.setAdapter(adapter);

        String[] items2 = new String[]{"MYR", "SGD", "CHY", "USD", "GBP", "EUR","NTD","HKD", "INR", "IDR"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(EditTalent.this, android.R.layout.simple_spinner_dropdown_item, items2);
        mspinnercurrency.setAdapter(adapter2);

        maddpicRecyclerView.setVisibility(View.GONE);
        mhorizontalScrollView.setVisibility(View.VISIBLE);

        mpostRates.addTextChangedListener(new MoneyTextWatcher());

        mTalent.child(city).child(maincategory).child(subcategory).child(postkey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("title")){
                    String title = dataSnapshot.child("title").getValue().toString();
                    mpostTitle.setText(title);
                }
                if(dataSnapshot.hasChild("desc")){
                    String desc = dataSnapshot.child("desc").getValue().toString();
                    mpostDescrip.setText(desc);
                }
                if(dataSnapshot.hasChild("category")){
                    String category = dataSnapshot.child("category").getValue().toString();
                    mpostCategoryx.setText(category);
                }
                if(dataSnapshot.hasChild("link1")){
                    String link1 = dataSnapshot.child("link1").getValue().toString();
                    mpostLink1.setText(link1);
                }
                if(dataSnapshot.hasChild("link2")){
                    String link2 = dataSnapshot.child("link2").getValue().toString();
                    mpostLink2.setText(link2);
                }
                if(dataSnapshot.hasChild("city")){
                    String city = dataSnapshot.child("city").getValue().toString();
                    mpostBaseLocation.setText(city);
                }
                if(dataSnapshot.hasChild("travelcity")){
                    travellocations = dataSnapshot.child("travelcity").getValue().toString();
                    mpostTravelLocation.setText(travellocations);
                }

                if(dataSnapshot.hasChild("postimage")){
                    String oldpostimage = dataSnapshot.child("postimage").getValue().toString();

                    if (oldpostimage.contains(" , ")) {
                        String[] separatedimages = oldpostimage.split(" , ");
                        for (int x = 0; x < separatedimages.length; x++) {

                            if (!separatedimages[x].contains("youtube")) {
                                oldpathString.add(separatedimages[x]);
                            }
                        }
                        for (int y = 0; y < oldpathString.size(); y++) {

                            ImageView mimgview = null;

                            if (y == 0) {
                                mimgview = mimg1;
                                mimgbtn1.setVisibility(View.GONE);
                                mImgOldString1 = oldpathString.get(y);
                            }
                            else if (y == 1) {
                                mimgview = mimg2;
                                mimgbtn2.setVisibility(View.GONE);
                                mImgOldString2 = oldpathString.get(y);
                            }
                            else if (y == 2) {
                                mimgview = mimg3;
                                mimgbtn3.setVisibility(View.GONE);
                                mImgOldString3 = oldpathString.get(y);
                            }
                            else if (y == 3) {
                                mimgview = mimg4;
                                mimgbtn4.setVisibility(View.GONE);
                                mImgOldString4 = oldpathString.get(y);
                            }
                            else if (y == 4) {
                                mimgview = mimg5;
                                mimgbtn5.setVisibility(View.GONE);
                                mImgOldString5 = oldpathString.get(y);
                            }

                            Glide.with(getApplicationContext()).load(oldpathString.get(y))
                                    .thumbnail(0.5f)
                                    .fitCenter()
                                    .error(R.drawable.error3)
                                    .placeholder(R.drawable.loading_spinner)
                                    .dontAnimate()
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(mimgview);
                        }

                    } else {
                        Glide.with(getApplicationContext()).load(oldpostimage)
                                .thumbnail(0.5f)
                                .fitCenter()
                                .error(R.drawable.error3)
                                .placeholder(R.drawable.loading_spinner)
                                .dontAnimate()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(mimg1);

                    }
                }
                if(dataSnapshot.hasChild("dates")){
                    String dateval = dataSnapshot.child("dates").getValue().toString();
                    if(!dateval.equals("none")){
                        mpostDate.setText(dateval);
                    }
                }
                if(dataSnapshot.hasChild("rates")){
                    String wagesval = dataSnapshot.child("rates").getValue().toString();
                    if(!wagesval.equals("none")){
                        String moneydigit = null, rateper, fullwages, stringcurrency;
                        int currency = 0, ratespinner = 0;

                        String[] separated1 = wagesval.split(" per ");
                        String[] separated2 = separated1[0].split(" ");

                        fullwages = separated2[1];
                        stringcurrency = separated2[0];
                        rateper = separated1[1];

                        if(rateper.equals("hour")){ratespinner = 0;}
                        else if(rateper.equals("day")){ratespinner = 1;}
                        else{ratespinner = 2;}

                        if(stringcurrency.contains("MYR")){
                            currency = 0;
                        }
                        else if(stringcurrency.contains("SGD")){
                            currency = 1;
                        }
                        else if(stringcurrency.contains("CHY")){
                            currency = 2;
                        }
                        else if(stringcurrency.contains("USD")){
                            currency = 3;
                        }
                        else if(stringcurrency.contains("GBP")){
                            currency = 4;
                        }
                        else if(stringcurrency.contains("EUR")){
                            currency = 5;
                        }
                        else if(stringcurrency.contains("NTD")){
                            currency = 6;
                        }
                        else if(stringcurrency.contains("HKD")){
                            currency = 7;
                        }
                        else if(stringcurrency.contains("INR")){
                            currency = 9;
                        }
                        else if(stringcurrency.contains("IDR")){
                            currency = 11;
                        }

                        mpostRates.setText(fullwages);
                        mspinnercurrency.setSelection(currency);
                        mspinnerrate.setSelection(ratespinner);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mseemorecardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mseemorecardview.setVisibility(View.GONE);
                mexpandableLayout1.toggle();
            }
        });

        mpostCategoryx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditTalent.this, TalentCategoryList.class);
                startActivityForResult(intent, 1111);
            }
        });

        mpostBaseLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditTalent.this, LocationList.class);
                intent.putExtra("locationstatus", "");
                intent.putExtra("travellocations", "");
                startActivityForResult(intent, 1000);
            }
        });

        mpostTravelLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditTalent.this, LocationList.class);
                intent.putExtra("locationstatus", "All");
                intent.putExtra("travellocations", travellocations);
                startActivityForResult(intent, 2000);
            }
        });

        mpostDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showavailabitydialog();
            }
        });


        maddpicCardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgPressed = "1";
                openMultipleImagePicker();
            }
        });

        maddpicCardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgPressed = "2";
                openMultipleImagePicker();
            }
        });

        maddpicCardView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgPressed = "3";
                openMultipleImagePicker();
            }
        });

        maddpicCardView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgPressed = "4";
                openMultipleImagePicker();
            }
        });

        maddpicCardView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgPressed = "5";
                openMultipleImagePicker();
            }
        });

        mcleardateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mpostDate.setText("");
                mpostDate.setHint("Tap to select available dates");
            }
        });

        mclearwagesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mpostRates.setText("");
                mpostRates.setHint("0.00");
            }
        });

        mpostLink2.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(final Editable s) {

                runnable = new Runnable() {
                    @Override
                    public void run() {
                        //do some work with s.toString()
                        String ur12 = extractYoutubeId(s.toString());

                        String url = "https://img.youtube.com/vi/"+ur12+"/1.jpg";
                        Glide.with(getApplicationContext()).load(url)
                                .thumbnail(0.5f)
                                .fitCenter()
                                .error(R.drawable.youtube2)
                                .placeholder(R.drawable.loading_spinner)
                                .dontAnimate()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(myoutubeimg2);

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

        mpostLink1.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(final Editable s) {

                runnable = new Runnable() {
                    @Override
                    public void run() {
                        //do some work with s.toString()
                        String ur1l = extractYoutubeId(s.toString());
                        Log.d(TAG, "Editable ur1l " + ur1l);

                        String url = "https://img.youtube.com/vi/"+ur1l+"/1.jpg";
                        Glide.with(getApplicationContext()).load(url)
                                .thumbnail(0.5f)
                                .fitCenter()
                                .error(R.drawable.youtube2)
                                .placeholder(R.drawable.loading_spinner)
                                .dontAnimate()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(myoutubeimg);

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

    }

    public String extractYoutubeId(String url) {

        try {
            String query = new URL(url).getQuery();
            String[] param = query.split("&");
            String id = null;
            for (String row : param) {
                String[] param1 = row.split("=");
                if (param1[0].equals("v")) {
                    id = param1[1];
                }
            }
            return id;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    public class MoneyTextWatcher implements TextWatcher {
        boolean mEditing;

        public MoneyTextWatcher() {
            mEditing = false;
        }

        public synchronized void afterTextChanged(Editable s) {
            if(!mEditing) {
                mEditing = true;

                String digits = s.toString().replaceAll("\\D", "");

                NumberFormat nf = NumberFormat.getCurrencyInstance();
                DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) nf).getDecimalFormatSymbols();
                decimalFormatSymbols.setCurrencySymbol("");
                ((DecimalFormat) nf).setDecimalFormatSymbols(decimalFormatSymbols);

                try{
                    String formatted = nf.format(Double.parseDouble(digits)/100);
                    s.replace(0, s.length(), formatted);
                } catch (NumberFormatException nfe) {
                    s.clear();
                }

                mEditing = false;
            }
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        public void onTextChanged(CharSequence s, int start, int before, int count) { }
    }


    private void openMultipleImagePicker() {
        FishBun.with(EditTalent.this)
                .setImageAdapter(new PicassoAdapter())
                .setMaxCount(1)
                .setMinCount(1)
                .setPickerSpanCount(3)
                .setActionBarColor(Color.parseColor("#67b8ed"), Color.parseColor("#67b8ed"), false)
                .setActionBarTitleColor(Color.parseColor("#FFFFFFFF"))
                .setSelectedImages(path)
                .setAlbumSpanCount(1, 2)
                .setButtonInAlbumActivity(false)
                .setCamera(true)
                .exceptGif(true)
                .textOnNothingSelected("Please select one or more")
                .startAlbum();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        MenuItem itemSave = menu.findItem(R.id.menuSave);

        MenuItem itemSettings = menu.findItem(R.id.menuSettings);
        itemSettings.setVisible(false);

        MenuItem item = menu.findItem(R.id.menuSearch2);
        item.setVisible(false);

        MenuItem itemSearch = menu.findItem(R.id.menuSearch);
        itemSearch.setVisible(false);

        MenuItem itemPublish = menu.findItem(R.id.menuPublish);
        itemPublish.setVisible(false);

        itemSave.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                startPosting();
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
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

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.nochange, R.anim.pulldown);
    }

    private void showavailabitydialog() {
        final Dialog dialog = new Dialog(EditTalent.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.availabilitydate_dialog);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(lp);

        final Spinner mspinner1 = dialog.findViewById(R.id.spinner1);
        final Spinner mspinner2 = dialog.findViewById(R.id.spinner2);
        Button cancelbtn = dialog.findViewById(R.id.cancelBtn);
        Button okBtn = dialog.findViewById(R.id.okBtn);

        String[] items = new String[]{"All Week", "Weekends Only", "Weekdays Only"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(EditTalent.this, android.R.layout.simple_spinner_dropdown_item, items);
        mspinner1.setAdapter(adapter);

        String[] items2 = new String[]{"All Day All Night", "Day Only", "Night Only"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(EditTalent.this, android.R.layout.simple_spinner_dropdown_item, items2);
        mspinner2.setAdapter(adapter2);

        dialog.show();

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mpostDate.setText(mspinner1.getSelectedItem().toString() + " (" + mspinner2.getSelectedItem().toString() + ")");
                dialog.dismiss();
            }
        });

        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }


    private void findApplicants( final Map<String, Object> newBookingData ) {
        
        //Notify all SHORTLISTED bookings customer about the job has changed
        mUserMyTalentAcceptedBookings.child(ownuserid).child(postkey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnaphot : dataSnapshot.getChildren()) {
                    final String applicantuserid = userSnaphot.getKey();
                    mUserBookingMade.child(applicantuserid).child(postkey).updateChildren(newBookingData);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Notify all PENDING bookings customer about the job has changed
        mUserMyTalentPendingBookings.child(ownuserid).child(postkey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnaphot : dataSnapshot.getChildren()) {
                    final String applicantuserid = userSnaphot.getKey();
                    mUserBookingMade.child(applicantuserid).child(postkey).updateChildren(newBookingData);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void just_update(final DatabaseReference newPost, final DatabaseReference newMyTalent, final Map<String, Object> newPostData, final Map<String, Object> myTalentData, final Map<String, Object> newBookingData) {

        newPost.updateChildren(newPostData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                findApplicants(newBookingData);
            }
        });

        newMyTalent.updateChildren(myTalentData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mProgress.dismiss();
                Toast.makeText(getApplicationContext(), "Talent successfully updated!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }


    private void deleteold_createnew(final DatabaseReference newPost, final DatabaseReference newMyTalent, final Map<String, Object> newPostData, final Map<String, Object> myTalentData, final Map<String, Object> newBookingData) {

        mTalent.child(city).child(maincategory).child(subcategory).child(postkey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                newPost.setValue(dataSnapshot.getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        newPost.updateChildren(newPostData).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                mTalent.child(city).child(maincategory).child(subcategory).child(postkey).removeValue();
                                findApplicants(newBookingData);
                            }
                        });
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        newMyTalent.updateChildren(myTalentData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mProgress.dismiss();
                Toast.makeText(getApplicationContext(), "Talent successfully updated!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }
    
    
    private void checkCityandCategories (final Long tsLong, final Map<String, Object> newPostData, final Map<String, Object> myTalentData, final Map<String, Object> newBookingData, final Map<String, Object> newTalentTitleData) {

        final DatabaseReference newPost;
       // final String keyval;
        final DatabaseReference newMyTalent = mUserMyTalent.child(ownuserid).child(postkey);
        
        //If city has changed
        if ((newcity!=null) && (!newcity.equals(city))) {

            newPostData.put("negatedtime", (-1 * tsLong));
            newPostData.put("time", ServerValue.TIMESTAMP);

            //If categories has changed
            if (((newmaincategory!=null) && (newsubcategory!=null)) && ((!newmaincategory.equals(maincategory)) || (!newsubcategory.equals(subcategory)))) {

                newPost = mTalent.child(newcity).child(newmaincategory).child(newsubcategory).child(postkey);
               // keyval = newPost.getKey();
               // newMyTalent = mUserMyTalent.child(ownuserid).child(keyval);

                deleteold_createnew(newPost, newMyTalent, newPostData, myTalentData, newBookingData);
            }

            //If categories remains same
            else {
                newPost = mTalent.child(newcity).child(maincategory).child(subcategory).child(postkey);
               // keyval = newPost.getKey();
                //newMyTalent = mUserMyTalent.child(ownuserid).child(keyval);

                deleteold_createnew(newPost, newMyTalent, newPostData, myTalentData, newBookingData);
            }

            mTalentTitle.child(city).child(postkey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mTalentTitle.child(newcity).child(postkey).setValue(dataSnapshot.getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mTalentTitle.child(newcity).child(postkey).updateChildren(newTalentTitleData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    mTalentTitle.child(city).child(postkey).removeValue();
                                }
                            });
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        //If city remains same
        else {

            //If categories has changed
            if (((newmaincategory!=null) && (newsubcategory!=null)) && ((!newmaincategory.equals(maincategory)) || (!newsubcategory.equals(subcategory)))) {

                newPostData.put("negatedtime", (-1 * tsLong));
                newPostData.put("time", ServerValue.TIMESTAMP);

                newPost = mTalent.child(city).child(newmaincategory).child(newsubcategory).child(postkey);
               // newMyTalent = mUserMyTalent.child(ownuserid).child(postkey);

                deleteold_createnew(newPost, newMyTalent, newPostData, myTalentData, newBookingData);
            }

            //If categories remains same
            else {
                newPost = mTalent.child(city).child(maincategory).child(subcategory).child(postkey);
               // newMyTalent = mUserMyTalent.child(ownuserid).child(postkey);

                just_update(newPost, newMyTalent, newPostData, myTalentData, newBookingData);
            }

            mTalentTitle.child(city).child(postkey).updateChildren(newTalentTitleData);
        }
    }

    private void startPosting(){

        imagecount = 0;
        deleteimagecount = 0;

        final String title_val = mpostTitle.getText().toString().trim();
        final String desc_val = mpostDescrip.getText().toString().trim();
        final String baselocation_val = mpostBaseLocation.getText().toString().trim();
        final String travellocation_val = mpostTravelLocation.getText().toString().trim();
        final String category_val = mpostCategoryx.getText().toString().trim();
        final String rates_val = mpostRates.getText().toString().trim();
        final String availability_val = mpostDate.getText().toString().trim();
        final String spinnerratetext = mspinnerrate.getSelectedItem().toString();
        final String spinnercurrencytext = mspinnercurrency.getSelectedItem().toString();
        final String postLink1_val = mpostLink1.getText().toString().trim();
        final String postLink2_val = mpostLink2.getText().toString().trim();

        if(!TextUtils.isEmpty(title_val)&&!TextUtils.isEmpty(desc_val)&&!TextUtils.isEmpty(baselocation_val)&&!TextUtils.isEmpty(travellocation_val)
                &&!TextUtils.isEmpty(category_val)) {

            mProgress.setMessage("Saving..");
            mProgress.setCancelable(false);
            mProgress.show();

            //If at least one image is being selected
            if (mimgbtn1.getVisibility() == View.GONE || mimgbtn2.getVisibility() == View.GONE || mimgbtn3.getVisibility() == View.GONE
                    || mimgbtn4.getVisibility() == View.GONE || mimgbtn5.getVisibility() == View.GONE) {

                final Long tsLong = System.currentTimeMillis() / 1000;

                //Save to Search Titles at another DB
                final Map<String, Object> newTalentTitleData = new HashMap<>();
                newTalentTitleData.put("lowertitle", title_val.toLowerCase());
                newTalentTitleData.put("title",title_val);
                newTalentTitleData.put("category",category_val);


                //Save to myTalent database
                final Map<String, Object> myTalentData = new HashMap<>();
                myTalentData.put("title", title_val);
                myTalentData.put("desc", desc_val);
                myTalentData.put("city", baselocation_val);
                myTalentData.put("category", category_val);
                

                //Save to mTalent database
                final Map<String, Object> newPostData = new HashMap<>();
                newPostData.put("title", title_val);
                newPostData.put("desc", desc_val);
                newPostData.put("uid", ownuserid);
                newPostData.put("travelcity", travellocation_val);
                newPostData.put("city", baselocation_val);
                newPostData.put("category", category_val);

                if (!TextUtils.isEmpty(rates_val)) {
                    newPostData.put("rates", spinnercurrencytext + " " + rates_val + " " + spinnerratetext);
                }

                if (!TextUtils.isEmpty(availability_val)) {
                    newPostData.put("dates", availability_val);
                }

                if (!TextUtils.isEmpty(postLink1_val)) {
                    newPostData.put("link1", postLink1_val);
                }

                if (!TextUtils.isEmpty(postLink2_val)) {
                    newPostData.put("link2", postLink2_val);
                }

                
                final Map<String, Object> newBookingData = new HashMap<>();
                newBookingData.put("title", title_val);
                newBookingData.put("desc", desc_val);
                newBookingData.put("city", baselocation_val);
                newBookingData.put("category", category_val);
                

                if (mImgUri1 == null && mImgUri2 == null && mImgUri3 == null && mImgUri4 == null && mImgUri5 == null) {
                    Log.e(TAG, "same paths");
                    //don't need delete old images, just update other data
                    checkCityandCategories(tsLong, newPostData, myTalentData, newBookingData, newTalentTitleData);
                }

                else {
                    Log.e(TAG, "NOT same paths");

                    final ArrayList<String> deleteImgStrings = new ArrayList<>();
                    final ArrayList<Uri> newImgUris = new ArrayList<>();
                    final ArrayList<String> newImgStrings = new ArrayList<>();

                    if (mImgUri1 != null) {
                        newImgUris.add(mImgUri1);
                        if( mImgOldString1 != null) {
                            deleteImgStrings.add(mImgOldString1);
                        }
                    }
                    if (mImgUri2 != null) {
                        newImgUris.add(mImgUri2);
                        if( mImgOldString2 != null) {
                            deleteImgStrings.add(mImgOldString2);
                        }
                    }
                    if (mImgUri3 != null) {
                        newImgUris.add(mImgUri3);
                        if( mImgOldString3 != null) {
                            deleteImgStrings.add(mImgOldString3);
                        }
                    }
                    if (mImgUri4 != null) {
                        newImgUris.add(mImgUri4);
                        if( mImgOldString4 != null) {
                            deleteImgStrings.add(mImgOldString4);
                        }
                    }
                    if (mImgUri5 != null) {
                        newImgUris.add(mImgUri5);
                        if( mImgOldString5 != null) {
                            deleteImgStrings.add(mImgOldString5);
                        }
                    }

                    //Delete all old images
                    if (!deleteImgStrings.isEmpty()) {

                        for (int x = 0; x < deleteImgStrings.size(); x++) {

                            Log.e(TAG, "deleteImgStrings.get(x) " + deleteImgStrings.get(x));

                            StorageReference oldstorageRef = fbStorage.getReferenceFromUrl(deleteImgStrings.get(x));
                            oldstorageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: deleted file");

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Uh-oh, an error occurred!
                                    Log.d(TAG, "onFailure: did not delete file");
                                }
                            });
                        }

                    }



                    StorageReference newstorageRef = fbStorage.getReferenceFromUrl("gs://my-gg-app.appspot.com");

                    Log.e(TAG, "newImgUris.size() " + newImgUris.size());
                    //Upload all new images
                    for (int x = 0; x < newImgUris.size(); x++) {

                        Uri imageuri = newImgUris.get(x);
                        Log.e(TAG, "imageuri " + imageuri);
                        StorageReference filepath = newstorageRef.child("TalentPhotos").child(ownuserid).child(imageuri.getLastPathSegment());

                        Log.e(TAG, "filepath " + filepath);

                        filepath.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                imagecount++;
                                final Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                newImgStrings.add(downloadUrl.toString());

                                //REACH LAST ONE
                                if (imagecount == newImgUris.size()) {

                                    if (mImgUri1 == null &&  mImgOldString1 != null) {
                                        newImgStrings.add(0, mImgOldString1);
                                    }
                                    if (mImgUri2 == null &&  mImgOldString2 != null) {
                                        newImgStrings.add(1, mImgOldString2);
                                    }
                                    if (mImgUri3 == null &&  mImgOldString3 != null) {
                                        newImgStrings.add(2, mImgOldString3);
                                    }
                                    if (mImgUri4 == null &&  mImgOldString4 != null) {
                                        newImgStrings.add(3, mImgOldString4);
                                    }
                                    if (mImgUri5 == null &&  mImgOldString5 != null) {
                                        newImgStrings.add(4, mImgOldString5);
                                    }

                                    if (!TextUtils.isEmpty(postLink1_val) && postLink1_val.contains("youtube")) {
                                        String id1 = extractYoutubeId(postLink1_val);
                                        String url1 = "https://img.youtube.com/vi/" + id1 + "/0.jpg";
                                        newImgStrings.add(url1);
                                    }

                                    if (!TextUtils.isEmpty(postLink2_val) && postLink2_val.contains("youtube")) {
                                        String id2 = extractYoutubeId(postLink1_val);
                                        String url2 = "https://img.youtube.com/vi/" + id2 + "/0.jpg";
                                        newImgStrings.add(url2);
                                    }


                                    if (!newImgStrings.isEmpty()) {

                                        Log.e(TAG, "newImgStrings.size() " + newImgStrings.size());

                                        if (newImgStrings.size() > 1) {
                                            //Got Link images OR Multiple images
                                            finalimageuris = TextUtils.join(" , ", newImgStrings);
                                            newPostData.put("postimage", finalimageuris);
                                            myTalentData.put("postimage", finalimageuris);

                                        } else {
                                            //Only ONE image
                                            myTalentData.put("postimage", downloadUrl.toString());
                                            newPostData.put("postimage", downloadUrl.toString());

                                        }

                                        checkCityandCategories(tsLong, newPostData, myTalentData, newBookingData, newTalentTitleData);
                                    }
                                }
                            }
                        });

                        filepath.putFile(imageuri).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Toast.makeText(getApplicationContext(), "Upload failed", Toast.LENGTH_SHORT).show();
                                mProgress.dismiss();
                            }
                        });

                    }
                }

            }
        }
    }

    @Override
    public void onItemClick(View view, final int position) {
        openMultipleImagePicker();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == 1000) && (resultCode == Activity.RESULT_OK)) {
            // recreate your fragment here

            newcity = data.getStringExtra("city");
            mpostBaseLocation.setText(newcity);

        }
        else if ((requestCode == 1000) && (resultCode == Activity.RESULT_CANCELED)) {
            Log.d(TAG, "cancel here");
        }

        if ((requestCode == 2000) && (resultCode == Activity.RESULT_OK)) {
            // recreate your fragment here

            String travellocation = data.getStringExtra("city");
            mpostTravelLocation.setText(travellocation);

        }
        else if ((requestCode == 2000) && (resultCode == Activity.RESULT_CANCELED)) {
            Log.d(TAG, "cancel here");
        }

        if ((requestCode == 1111) && (resultCode == Activity.RESULT_OK)) {
            // recreate your fragment here
            newmaincategory = data.getStringExtra("maincategory");
            newsubcategory = data.getStringExtra("subcategory");
            String strCategoryText = data.getStringExtra("category");
            mpostCategoryx.setText(strCategoryText);

        }
        else if ((requestCode == 1111) && (resultCode == Activity.RESULT_CANCELED)) {
            Log.d(TAG, "cancel here");
        }

        if ((requestCode == Define.ALBUM_REQUEST_CODE) && (resultCode == Activity.RESULT_OK)) {
            // recreate your fragment here
          //  Log.e(TAG, "path here 1 " + path.size());
           // path.clear();
            path = data.getParcelableArrayListExtra(Define.INTENT_PATH);

            //imageAdapter.changePath(path);

            if (imgPressed != null) {

                ImageView mimgview = null;

                if (imgPressed.equals("1")) {
                    mimgview = mimg1;
                    mImgUri1 = path.get(0);
                    mimgbtn1.setVisibility(View.GONE);
                }
                else if (imgPressed.equals("2")) {
                    mimgview = mimg2;
                    mImgUri2 = path.get(0);
                    mimgbtn2.setVisibility(View.GONE);
                }
                else if (imgPressed.equals("3")) {
                    mimgview = mimg3;
                    mImgUri3 = path.get(0);
                    mimgbtn3.setVisibility(View.GONE);
                }
                else if (imgPressed.equals("4")) {
                    mimgview = mimg4;
                    mImgUri4 = path.get(0);
                    mimgbtn4.setVisibility(View.GONE);
                }
                else if (imgPressed.equals("5")) {
                    mimgview = mimg5;
                    mImgUri5 = path.get(0);
                    mimgbtn5.setVisibility(View.GONE);
                }

                Glide.with(getApplicationContext()).load(path.get(path.size()-1))
                        .thumbnail(0.5f)
                        .fitCenter()
                        .error(R.drawable.error3)
                        .placeholder(R.drawable.loading_spinner)
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(mimgview);

                path.clear();

            }

            for (int y = 0; y < path.size(); y++) {
                Log.e(TAG, "path here 2 " + path.get(y));
            }



            for (int y = 0; y < oldpath.size(); y++) {
                Log.e(TAG, "oldpath here 2 " + oldpath.get(y));
            }

        }
    }
}
