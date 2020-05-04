package com.zjheng.jobseed.jobseed.ShowcaseTalentScene;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sangcomz.fishbun.FishBun;
import com.sangcomz.fishbun.adapter.image.impl.PicassoAdapter;
import com.sangcomz.fishbun.define.Define;
import com.zjheng.jobseed.jobseed.LocationList;
import com.zjheng.jobseed.jobseed.PostScene.Post;
import com.zjheng.jobseed.jobseed.R;
import com.zjheng.jobseed.jobseed.TalentCategories.TalentCategoryList;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by zhen on 2/25/2018.
 */

public class ShowcaseTalent extends AppCompatActivity implements ImageAdapter.ItemClickListener {

    private Toolbar mToolbar;
    private CardView maddpicCardView1, maddpicCardView2, maddpicCardView3, maddpicCardView4, maddpicCardView5, mseemorecardview;
    private RecyclerView maddpicRecyclerView;
    private ArrayList<Uri> path = new ArrayList<>();
    private ArrayList<String> pathString = new ArrayList<>();
    private HorizontalScrollView mhorizontalScrollView;
    private LinearLayoutManager linearLayoutManager;
    private ImageAdapter imageAdapter;

    private ImageButton mcleardateBtn, mclearwagesBtn, mpostTalentBtn;
    private EditText mpostLink1, mpostLink2, mpostTitle,mpostDescrip, mpostCategoryx, mpostBaseLocation, mpostTravelLocation, mpostRates, mpostDate;
    private Spinner mspinnerrate, mspinnercurrency;
    private ExpandableRelativeLayout mexpandableLayout1;

    private ImageView myoutubeimg, myoutubeimg2;

    private SharedPreferences prefs;
    private ProgressDialog mProgress;
    private FirebaseApp fbApp;
    private FirebaseStorage fbStorage;

    final android.os.Handler handler = new android.os.Handler();
    Runnable runnable;

   // private YouTubePlayerFragment playerFragment;
  //  private YouTubePlayer mPlayer;
    private String YouTubeKey = "AIzaSyAuHZRT30kj63bROo7mN0otYr_Kdp0L9fM", travellocations, finalimageuris, ownuserid, maincategory, subcategory;
    private int x, imagecount = 0;

    private static final String TAG = "ShowcaseTalent";

    private FirebaseAuth mAuth;
    private DatabaseReference mTalent, mUserMyTalent, mTalentVerify, mTalentTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talent_post);

        mAuth = FirebaseAuth.getInstance();
        ownuserid = mAuth.getCurrentUser().getUid();
        Log.d(TAG, "ownuserid " + ownuserid);


        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApiKey("AIzaSyAX3s8Y0CA2RwBF5vxhX9tVqV5Gg1S2UHE")
                .setApplicationId("1:1004980108230:android:4ecc908d28953c07")
                .setDatabaseUrl("https://my-gg-app.firebaseio.com")
                .build();

        boolean hasBeenInitialized = false;
        List<FirebaseApp> fbsLcl = FirebaseApp.getApps(ShowcaseTalent.this);
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
        fbStorage = FirebaseStorage.getInstance(fbApp);


        mTalent =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("Talent");
        mUserMyTalent =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("MyTalent");
        mTalentVerify =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("TalentVerify");

        prefs = getSharedPreferences("saved", Context.MODE_PRIVATE);

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
        mpostTalentBtn = findViewById(R.id.postTalentBtn);

        myoutubeimg = findViewById(R.id.youtubeimg);
        myoutubeimg2 = findViewById(R.id.youtubeimg2);

        mpostTalentBtn.setEnabled(false);
        mpostTalentBtn.setAlpha(0.5f);

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

        //playerFragment = (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.youtube_player_fragment);

        //playerFragment.initialize(YouTubeKey, this);

        maddpicRecyclerView = findViewById(R.id.addpicRecyclerView);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        imageAdapter = new ImageAdapter(this, path);
        maddpicRecyclerView.setLayoutManager(linearLayoutManager);
        imageAdapter.setClickListener(this);
        maddpicRecyclerView.setAdapter(imageAdapter);

        String[] items = new String[]{"per hour", "per day", "per month"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ShowcaseTalent.this, android.R.layout.simple_spinner_dropdown_item, items);
        mspinnerrate.setAdapter(adapter);

        String[] items2 = new String[]{"MYR", "SGD", "CHY", "USD", "GBP", "EUR","NTD","HKD", "INR", "IDR"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(ShowcaseTalent.this, android.R.layout.simple_spinner_dropdown_item, items2);
        mspinnercurrency.setAdapter(adapter2);

        mpostRates.addTextChangedListener(new MoneyTextWatcher());

        if(prefs.contains("titleval")){
            mpostTitle.setText(prefs.getString("titleval", ""));
        }
        if(prefs.contains("descval")){
            mpostDescrip.setText(prefs.getString("descval", ""));
        }
        if(prefs.contains("categoryval")){
            mpostCategoryx.setText(prefs.getString("categoryval", ""));
        }
        if(prefs.contains("baselocationval")){
            mpostBaseLocation.setText(prefs.getString("baselocationval", ""));
        }
        if(prefs.contains("travellocation")){
            mpostTravelLocation.setText(prefs.getString("travellocation", ""));
        }
        if(prefs.contains("ratesval")){
            mpostRates.setText(prefs.getString("ratesval", ""));
        }
        if(prefs.contains("dateval")){
            mpostDate.setText(prefs.getString("dateval", ""));
        }
        if(prefs.contains("spinnerrateintval")){
            mspinnerrate.setSelection(prefs.getInt("spinnerrateintval", 0));
        }
        if(prefs.contains("spinnercurrencyintval")){
            mspinnercurrency.setSelection(prefs.getInt("spinnercurrencyintval", 0));
        }
        if(prefs.contains("link1")){
            mpostLink1.setText(prefs.getString("link1", ""));
        }
        if(prefs.contains("link2")){
            mpostLink2.setText(prefs.getString("link2", ""));
        }

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
                Intent intent = new Intent(ShowcaseTalent.this, TalentCategoryList.class);
                startActivityForResult(intent, 1111);
            }
        });

        mpostBaseLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShowcaseTalent.this, LocationList.class);
                intent.putExtra("locationstatus", "");
                intent.putExtra("travellocations", "");
                startActivityForResult(intent, 1000);
            }
        });

        mpostTravelLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(prefs.contains("travellocation")){
                    travellocations = prefs.getString("travellocation", "");
                }
                Intent intent = new Intent(ShowcaseTalent.this, LocationList.class);
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
                openMultipleImagePicker();
            }
        });

        maddpicCardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMultipleImagePicker();
            }
        });

        maddpicCardView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMultipleImagePicker();
            }
        });

        maddpicCardView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMultipleImagePicker();
            }
        });

        maddpicCardView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

        mpostTalentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startPosting();
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

        mpostTitle.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                updatePublishButton();
            }
        });

        mpostDescrip.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                updatePublishButton();
            }
        });

        mpostCategoryx.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                updatePublishButton();
            }
        });

        mpostBaseLocation.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                updatePublishButton();
            }
        });

        mpostTravelLocation.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                updatePublishButton();
            }
        });

    }

    /*@Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player,
                                        boolean wasRestored) {
        mPlayer = player;

        //Enables automatic control of orientation
        mPlayer.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION);

        //Show full screen in landscape mode always
        mPlayer.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE);

        //System controls will appear automatically
        mPlayer.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_SYSTEM_UI);

        if (!wasRestored) {
            mPlayer.cueVideo("0KhnAnbwNK4");
            //mPlayer.loadVideo("9rLZYyMbJic");
        }
        else
        {
            mPlayer.play();
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                        YouTubeInitializationResult errorReason) {
        mPlayer = null;
    }*/

    private void showavailabitydialog() {
        final Dialog dialog = new Dialog(ShowcaseTalent.this);
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
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ShowcaseTalent.this, android.R.layout.simple_spinner_dropdown_item, items);
        mspinner1.setAdapter(adapter);

        String[] items2 = new String[]{"All Day All Night", "Day Only", "Night Only"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(ShowcaseTalent.this, android.R.layout.simple_spinner_dropdown_item, items2);
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


    private void updatePublishButton(){

        final String title_val = mpostTitle.getText().toString().trim();
        final String desc_val = mpostDescrip.getText().toString().trim();
        final String category_val = mpostCategoryx.getText().toString().trim();
        final String baselocation_val = mpostBaseLocation.getText().toString().trim();
        final String travellocation_val = mpostTravelLocation.getText().toString().trim();

        if(!TextUtils.isEmpty(title_val)&&!TextUtils.isEmpty(desc_val)&&!TextUtils.isEmpty(baselocation_val)&&!TextUtils.isEmpty(category_val)&&!TextUtils.isEmpty(travellocation_val)) {
            if (mhorizontalScrollView.getVisibility() == View.GONE) {
                mpostTalentBtn.setEnabled(true);
                mpostTalentBtn.setAlpha(1f);
            }
            else {
                mpostTalentBtn.setEnabled(false);
                mpostTalentBtn.setAlpha(0.5f);
            }
        }
        else {
            mpostTalentBtn.setEnabled(false);
            mpostTalentBtn.setAlpha(0.5f);
        }

    }

    private void openMultipleImagePicker() {
        FishBun.with(ShowcaseTalent.this)
                .setImageAdapter(new PicassoAdapter())
                .setMaxCount(5)
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

    private void startPosting() {

        imagecount = 0;

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

            mProgress.setMessage("Uploading..");
            mProgress.setCancelable(false);
            mProgress.show();

            if (!path.isEmpty()) {


                StorageReference storageRef = fbStorage.getReferenceFromUrl("gs://my-gg-app.appspot.com");

                final DatabaseReference newPost = mTalent.child(baselocation_val).child(maincategory).child(subcategory).push();
                final String keyval = newPost.getKey();
                final DatabaseReference newMyTalent = mUserMyTalent.child(ownuserid).child(keyval);


                final Long tsLong = System.currentTimeMillis()/1000;

                //Save to Search Titles at another DB
                final Map<String, Object> TalentTitleData = new HashMap<>();
                TalentTitleData.put("lowertitle", title_val.toLowerCase());
                TalentTitleData.put("title",title_val);
                TalentTitleData.put("category",category_val);

                //Save to myTalent database
                final Map<String, Object> myTalentData = new HashMap<>();
                myTalentData.put("title", title_val);
                myTalentData.put("desc", desc_val);
                myTalentData.put("city", baselocation_val);
                myTalentData.put("category", category_val);
                myTalentData.put("pressed", "true");
                myTalentData.put("verified", "false");
                myTalentData.put("reviewcount", 0);
                myTalentData.put("reviewstar", 0);
                myTalentData.put("totalbookingcount", 0);
                myTalentData.put("newbookingcount", 0);

                //Save to Talent database
                final Map<String, Object> newPostData = new HashMap<>();
                newPostData.put("negatedtime", (-1*tsLong));
                newPostData.put("time", ServerValue.TIMESTAMP);
                newPostData.put("title", title_val);
                newPostData.put("desc", desc_val);
                newPostData.put("uid", ownuserid);
                newPostData.put("verified", "false");
                newPostData.put("travelcity", travellocation_val);
                newPostData.put("city", baselocation_val);
                newPostData.put("reviewcount", 0);
                newPostData.put("reviewcount_negatedtime", 0);
                newPostData.put("reviewstar", 0);
                newPostData.put("category", category_val);

                if (!TextUtils.isEmpty(rates_val)) {
                    if (!rates_val.equals("0.00")) {
                        newPostData.put("rates",spinnercurrencytext + " " + rates_val + " " + spinnerratetext);
                    }
                }

                if (!TextUtils.isEmpty(availability_val)) {
                    newPostData.put("dates",availability_val);
                }

                for (x = 0; x< path.size(); x++){

                    Uri imageuri = path.get(x);
                    StorageReference filepath = storageRef.child("TalentPhotos").child(ownuserid).child(imageuri.getLastPathSegment());

                    filepath.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            imagecount++;

                            final Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            pathString.add(downloadUrl.toString());

                            //REACH LAST ONE
                            if (imagecount == path.size()) {

                                if (!TextUtils.isEmpty(postLink1_val) && postLink1_val.contains("youtube")) {
                                    String id1 = extractYoutubeId(postLink1_val);
                                    String url1 = "https://img.youtube.com/vi/"+id1+"/0.jpg";
                                    newPostData.put("link1",postLink1_val);
                                    pathString.add(url1);
                                }

                                if (!TextUtils.isEmpty(postLink2_val) && postLink2_val.contains("youtube")) {
                                    String id2 = extractYoutubeId(postLink1_val);
                                    String url2 = "https://img.youtube.com/vi/"+id2+"/0.jpg";
                                    newPostData.put("link2",postLink2_val);
                                    pathString.add(url2);
                                }

                                if (!pathString.isEmpty()) {

                                    if (pathString.size() > 1) {
                                        //Got Link images OR Multiple images
                                        finalimageuris = TextUtils.join(" , ", pathString);
                                        newPostData.put("postimage", finalimageuris);
                                        myTalentData.put("postimage", finalimageuris);

                                    }
                                    else {
                                        //Only ONE image
                                        myTalentData.put("postimage", downloadUrl.toString());
                                        newPostData.put("postimage", downloadUrl.toString());

                                    }

                                    mTalentTitle.child(baselocation_val).child(keyval).setValue(TalentTitleData);

                                    newMyTalent.setValue(myTalentData);

                                    newPost.setValue(newPostData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                                mTalentVerify.child(ownuserid).child(keyval).setValue("pending");

                                                showSubmitDialog();
                                            }
                                            else{
                                                Toast.makeText(ShowcaseTalent.this, "Post Failed", Toast.LENGTH_LONG).show();
                                                mProgress.dismiss();
                                            }
                                        }
                                    });
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

    private void showSubmitDialog(){

        final Dialog dialog = new Dialog(ShowcaseTalent.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.passwordreset_dialog);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(lp);
        dialog.setCancelable(false);

        TextView mdialogtxt =  dialog.findViewById(R.id.dialogtxt);

        ImageView memaillogo = (ImageView) dialog.findViewById(R.id.emaillogo);
        memaillogo.setImageResource(R.drawable.talent_postwait);
        memaillogo.setVisibility(VISIBLE);

        Button okbtn = (Button) dialog.findViewById(R.id.hireBtn);
        Button cancelbtn = (Button) dialog.findViewById(R.id.cancelBtn);
        cancelbtn.setVisibility(GONE);

        okbtn.setText("OK");
        okbtn.setTextColor(Color.parseColor("#0e52a5"));
        mdialogtxt.setText("Your Talent has been submitted to us. Please allow 24 - 48 hours for our team to verify it. If you would like to cancel your submittion, please contact our support team via email or phone");

        dialog.show();

        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();

                prefs.edit().clear().apply();

                Toast.makeText(ShowcaseTalent.this, "Talent Successfully Submitted!", Toast.LENGTH_LONG).show();
                mProgress.dismiss();

                // Go to MainActivity
                setResult(Activity.RESULT_OK);
                finish();

            }
        });

    }

    @Override
    public void onBackPressed() {

        final String title_val = mpostTitle.getText().toString().trim();
        final String desc_val = mpostDescrip.getText().toString().trim();
        final String category_val = mpostCategoryx.getText().toString().trim();
        final String baselocation_val = mpostBaseLocation.getText().toString().trim();
        final String travellocation_val = mpostTravelLocation.getText().toString().trim();
        final String rates_val = mpostRates.getText().toString().trim();
        final String date_val = mpostDate.getText().toString().trim();
        final String link1_val = mpostLink1.getText().toString().trim();
        final String link2_val = mpostLink2.getText().toString().trim();
        final int spinnerrateint = mspinnerrate.getSelectedItemPosition();
        final int spinnercurrencyint = mspinnercurrency.getSelectedItemPosition();

        if(!TextUtils.isEmpty(title_val) || !TextUtils.isEmpty(desc_val) || !TextUtils.isEmpty(travellocation_val)
                || !TextUtils.isEmpty(category_val) || !TextUtils.isEmpty(baselocation_val) || !TextUtils.isEmpty(link1_val)
                || !TextUtils.isEmpty(rates_val) || !TextUtils.isEmpty(date_val)|| !TextUtils.isEmpty(link2_val)) {

            // custom dialog
            final Dialog dialog = new Dialog(ShowcaseTalent.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.applicantsdialog);

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.CENTER;

            dialog.getWindow().setAttributes(lp);

            Button keepbtn = (Button) dialog.findViewById(R.id.hireBtn);
            TextView mdialogtxt = (TextView) dialog.findViewById(R.id.dialogtxt);
            Button discardbtn = (Button) dialog.findViewById(R.id.cancelBtn);

            discardbtn.setText("DISCARD");
            discardbtn.setTextColor(Color.parseColor("#FFDF2C04"));
            mdialogtxt.setText("Your post will be deleted once you leave, do you want to save as draft? ");
            keepbtn.setText("KEEP");
            keepbtn.setTextColor(Color.parseColor("#ff669900"));

            dialog.show();

            discardbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    prefs.edit().clear().apply();

                    dialog.dismiss();
                    finish();
                }
            });

            keepbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    prefs.edit().putString("titleval", title_val).apply();
                    prefs.edit().putString("descval", desc_val).apply();
                    prefs.edit().putString("categoryval", category_val).apply();
                    prefs.edit().putString("baselocationval", baselocation_val).apply();
                    prefs.edit().putString("travellocation", travellocation_val).apply();
                    prefs.edit().putString("ratesval", rates_val).apply();
                    prefs.edit().putString("dateval", date_val).apply();
                    prefs.edit().putString("link1", link1_val).apply();
                    prefs.edit().putString("link2", link2_val).apply();
                    prefs.edit().putInt("spinnerrateintval",spinnerrateint).apply();
                    prefs.edit().putInt("spinnercurrencyintval",spinnercurrencyint).apply();

                    dialog.dismiss();
                    finish();
                }
            });
        }
        else{
            finish();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.nochange, R.anim.pulldown);
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

            String baselocation = data.getStringExtra("city");
            mpostBaseLocation.setText(baselocation);

        }
        else if ((requestCode == 1000) && (resultCode == Activity.RESULT_CANCELED)) {
            Log.d(TAG, "cancel here");
        }

        if ((requestCode == 2000) && (resultCode == Activity.RESULT_OK)) {
            // recreate your fragment here

            String travellocation = data.getStringExtra("city");
            prefs.edit().putString("travellocation", travellocation).apply();
            mpostTravelLocation.setText(travellocation);

        }
        else if ((requestCode == 2000) && (resultCode == Activity.RESULT_CANCELED)) {
            Log.d(TAG, "cancel here");
        }

        if ((requestCode == 1111) && (resultCode == Activity.RESULT_OK)) {
            // recreate your fragment here
            maincategory = data.getStringExtra("maincategory");
            subcategory = data.getStringExtra("subcategory");
            String strCategoryText = data.getStringExtra("category");
            mpostCategoryx.setText(strCategoryText);

        }
        else if ((requestCode == 1111) && (resultCode == Activity.RESULT_CANCELED)) {
            Log.d(TAG, "cancel here");
        }

        if ((requestCode == Define.ALBUM_REQUEST_CODE) && (resultCode == Activity.RESULT_OK)) {
            // recreate your fragment here

            path = data.getParcelableArrayListExtra(Define.INTENT_PATH);
            imageAdapter.changePath(path);
            Log.e("showcast talent", path.toString());
            maddpicRecyclerView.setVisibility(View.VISIBLE);
            mhorizontalScrollView.setVisibility(View.GONE);
            updatePublishButton();
        }
    }
}
