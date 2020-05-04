package com.zjheng.jobseed.jobseed.ActivitiesScene.EditPostScene;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.nearby.messages.internal.Update;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.squareup.timessquare.CalendarPickerView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.zjheng.jobseed.jobseed.R;

import java.io.File;
import java.io.IOException;
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

import static android.view.View.VISIBLE;
import static com.zjheng.jobseed.jobseed.R.id.delete;
import static com.zjheng.jobseed.jobseed.R.id.spinnercurrency;
import static com.zjheng.jobseed.jobseed.R.id.spinnerrate;

public class EditPost extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private DatabaseReference mJob, mUserAccount, mUserActivities, newEditPost, mUserPostedShortlistedApplicants,
            newPosted, mCategory, mGeoFire, mUserPosted, mUserPostedPendingApplicants, mUserPostedHiredApplicants, mDefaultJobPhotos;

    private EditText mpostDescrip, mpostCompany, mpostLocation, mpostTitle, mpostWages, mpostDate;
    private ImageButton maddphotoBtn, mchgPostImage, mcleardateBtn, mclearwagesBtn;
    private ImageView mPostImage;
    private TextView mPostImagetxt;
    private Spinner mspinnerrate, mspinnercurrency;

    private String mCurrentPhotoPath, city, newcity;
    private Bitmap mImageBitmap;
    private Uri mImageUri= null;
    private double latitude;
    private double longitude;
    private Toolbar mToolbar;

    private ProgressDialog mProgress;

    private static int PLACE_PICKER_REQUEST = 1;
    private static final int MY_PERMISSION_REQUEST_LOCATION = 2;
    private static final int GALLERY_INTENT = 3;
    private static final int CAMERA_REQUEST_CODE = 4;
    private static final String TAG = "EditPost";

    public static EditText meditpostCategory;

    private boolean mImgClick = false;
    private long categorynum;
    private String postkey, postimage, ownuserid, mjobbg1 = "", mjobbg2 = "", mjobbg3 = "" ;
    private Long category_negatedtime,mostrecent_startdate, mostrecent_wagesrange, mostrecent_wagesrange_startdate, negatedtime;
    private String startingdate = "";
    private int i;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_post);

        postkey = getIntent().getStringExtra("post_id");
        city = getIntent().getStringExtra("city_id");

        mAuth = FirebaseAuth.getInstance();
        ownuserid = mAuth.getCurrentUser().getUid();

        mJob =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("Job");

        mCategory =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("Category");

        mUserAccount =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserAccount");

        mUserActivities =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserActivities");

        mUserPosted =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserPosted");

        mUserPostedPendingApplicants =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserPostedPendingApplicants");

        mUserPostedShortlistedApplicants =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserPostedShortlistedApplicants");

        mUserPostedHiredApplicants =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserPostedHiredApplicants");

        mDefaultJobPhotos =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("DefaultJobPhotos");

        mProgress = new ProgressDialog(this);

        mpostDescrip = (EditText) findViewById(R.id.postDescrip);
        mpostCompany = (EditText) findViewById(R.id.postCompany);
        meditpostCategory = (EditText) findViewById(R.id.postCategoryx);
        mpostLocation = (EditText) findViewById(R.id.postLocation);
        mpostTitle = (EditText) findViewById(R.id.postName);
        mPostImage = (ImageView) findViewById(R.id.PostImage);
        maddphotoBtn = (ImageButton) findViewById(R.id.addphotoBtn);
        mchgPostImage = (ImageButton) findViewById(R.id.chgPostImage);
        mPostImagetxt = (TextView) findViewById(R.id.textView4);

        mpostWages = (EditText) findViewById(R.id.postWages);
        mpostDate = (EditText) findViewById(R.id.postDate);
        mspinnerrate = (Spinner) findViewById(spinnerrate);
        mspinnercurrency = (Spinner) findViewById(spinnercurrency);
        mcleardateBtn = (ImageButton) findViewById(R.id.cleardateBtn);
        mclearwagesBtn = (ImageButton) findViewById(R.id.clearwagesBtn);

        String[] items = new String[]{"per hour", "per day", "per month"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(EditPost.this, android.R.layout.simple_spinner_dropdown_item, items);
        mspinnerrate.setAdapter(adapter);

        String[] items2 = new String[]{"MYR", "SGD", "CHY", "USD", "GBP", "EUR","NTD","HKD", "INR", "IDR"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(EditPost.this, android.R.layout.simple_spinner_dropdown_item, items2);
        mspinnercurrency.setAdapter(adapter2);

        mToolbar = (Toolbar)findViewById(R.id.toolbar_other);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Edit Post");
        mToolbar.setTitleTextColor(android.graphics.Color.WHITE);

        mToolbar.setNavigationIcon(R.mipmap.ic_close_white_24dp);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mDefaultJobPhotos.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("jobbg1")) {
                    mjobbg1 = dataSnapshot.child("jobbg1").getValue().toString();
                }
                if (dataSnapshot.hasChild("jobbg2")) {
                    mjobbg2 = dataSnapshot.child("jobbg2").getValue().toString();
                }
                if (dataSnapshot.hasChild("jobbg3")) {
                    mjobbg3 = dataSnapshot.child("jobbg3").getValue().toString();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mJob.child(city).child(postkey).addListenerForSingleValueEvent(new ValueEventListener() {
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
                    meditpostCategory.setText(category);
                }
                if(dataSnapshot.hasChild("company")){
                    String company = dataSnapshot.child("company").getValue().toString();
                    mpostCompany.setText(company);
                }
                if(dataSnapshot.hasChild("fulladdress")){
                    String fulladdress = dataSnapshot.child("fulladdress").getValue().toString();
                    mpostLocation.setText(fulladdress);
                }
                if(dataSnapshot.hasChild("longitude")){
                    longitude = (Double) dataSnapshot.child("longitude").getValue();
                }
                if(dataSnapshot.hasChild("latitude")){
                    latitude = (Double) dataSnapshot.child("latitude").getValue();
                }
                if(dataSnapshot.hasChild("postimage")){
                    postimage = dataSnapshot.child("postimage").getValue().toString();

                    Glide.with(getApplicationContext()).load(postimage)
                            .thumbnail(0.5f)
                            .centerCrop()
                            .error(R.drawable.profilebg3)
                            .placeholder(R.drawable.loading_spinner)
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(mPostImage);

                    maddphotoBtn.setVisibility(View.GONE);
                    mPostImagetxt.setVisibility(View.GONE);
                    mchgPostImage.setVisibility(View.VISIBLE);

                }
                if(dataSnapshot.hasChild("date")){
                    String dateval = dataSnapshot.child("date").getValue().toString();
                    if(!dateval.equals("none")){
                        mpostDate.setText(dateval);
                    }
                }
                if(dataSnapshot.hasChild("mostrecent_startdate")){
                    Long mostrecent_startdatelong = (Long) dataSnapshot.child("mostrecent_startdate").getValue();
                    if(mostrecent_startdatelong!=0){
                        String mostrecent_startdatestring = Long.toString(mostrecent_startdatelong);
                        startingdate = mostrecent_startdatestring.substring(1, 7);
                        Log.d(TAG, "startingdate " + startingdate);
                    }
                }
                if(dataSnapshot.hasChild("wages")){
                    String wagesval = dataSnapshot.child("wages").getValue().toString();
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

                        mpostWages.setText(fullwages);
                        mspinnercurrency.setSelection(currency);
                        mspinnerrate.setSelection(ratespinner);
                    }
                }

                if(dataSnapshot.hasChild("negatedtime")){
                    long oldnegatedtime = (Long) dataSnapshot.child("negatedtime").getValue();
                    negatedtime = -1*oldnegatedtime;
                }
                if(dataSnapshot.hasChild("category_negatedtime")){
                    category_negatedtime = (Long) dataSnapshot.child("category_negatedtime").getValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mcleardateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mpostDate.setText("");
                startingdate = "";
                mpostDate.setHint("(Optional) Pick your job's starting date");
            }
        });

        mclearwagesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mpostWages.setText("");
                mpostWages.setHint("0.00");
            }
        });

        mpostDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showdatepickerdialog();
            }
        });

        maddphotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAction();
            }
        });

        mchgPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAction();
            }
        });

        meditpostCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent categoryselect = new Intent(EditPost.this, EditCategorySelect.class);
                startActivity(categoryselect);
            }
        });

        mpostLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgress.setMessage("Loading..");
                mProgress.show();
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                Intent intent;
                try {
                    intent = builder.build(EditPost.this);
                    startActivityForResult(intent, PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }

        });

        mpostWages.addTextChangedListener(new MoneyTextWatcher());

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

    private void showAction() {
        // custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.addphoto_dialog);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(lp);

        RelativeLayout mcameraActionbtn = (RelativeLayout) dialog.findViewById(R.id.cameraAction);

        RelativeLayout mgalleryActionbtn = (RelativeLayout) dialog.findViewById(R.id.galleryAction);

        dialog.show();

        mcameraActionbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        Log.i(TAG, "IOException");
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                    }
                }

                dialog.dismiss();

                /*Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, CAMERA_REQUEST_CODE);//zero can be replaced with any action code
                dialog.dismiss();*/
        }
        });

        mgalleryActionbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                galleryIntent.setType("image/* ");
                startActivityForResult(galleryIntent,GALLERY_INTENT );
                dialog.dismiss();
            }
        });
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                storageDir      // directory
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
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

    private void showdatepickerdialog(){

        i = 1;

        final Dialog dialog = new Dialog(EditPost.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.datetimepicker_dialog);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(lp);

        Button cancelbtn = (Button) dialog.findViewById(R.id.cancelBtn);
        Button okBtn = (Button) dialog.findViewById(R.id.okBtn);
        final TextView mmultipledatetxt = (TextView) dialog.findViewById(R.id.multipledatetxt);
        mmultipledatetxt.setTextColor(Color.parseColor("#008fee"));
        final ImageView mmultipledates_img = (ImageView) dialog.findViewById(R.id.multipledates_img);
        mmultipledates_img.setVisibility(VISIBLE);
        final TextView mrangedatetxt = (TextView) dialog.findViewById(R.id.rangedatetxt);
        final ImageView mrangedates_img = (ImageView) dialog.findViewById(R.id.rangedates_img);
        final CalendarPickerView calendar_view = (CalendarPickerView) dialog.findViewById(R.id.calendar_view);
        //getting current
        final Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);
        final Date today = new Date();

        //add one year to calendar from todays date
        calendar_view.init(today, nextYear.getTime())
                .inMode(CalendarPickerView.SelectionMode.MULTIPLE);

        dialog.show();

        mmultipledatetxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add one year to calendar from todays date
                calendar_view.init(today, nextYear.getTime())
                        .inMode(CalendarPickerView.SelectionMode.MULTIPLE);

                mmultipledatetxt.setTextColor(Color.parseColor("#008fee"));
                mmultipledates_img.setVisibility(VISIBLE);
                mrangedatetxt.setTextColor(Color.parseColor("#ff778088"));
                mrangedates_img.setVisibility(View.INVISIBLE);

                i = 1;
            }
        });

        mrangedatetxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add one year to calendar from todays date
                calendar_view.init(today, nextYear.getTime())
                        .inMode(CalendarPickerView.SelectionMode.RANGE);

                mrangedatetxt.setTextColor(Color.parseColor("#008fee"));
                mrangedates_img.setVisibility(VISIBLE);
                mmultipledatetxt.setTextColor(Color.parseColor("#ff778088"));
                mmultipledates_img.setVisibility(View.INVISIBLE);

                i = 2;
            }
        });

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(i == 1){
                    //MULTIPLE DATES
                    String newdate = "";

                    List<String> dates = new ArrayList<String>();

                    for (int x = 0; x< calendar_view.getSelectedDates().size();x++){

                        //here you can fetch all dates

                        Date calenderdate = calendar_view.getSelectedDates().get(x);

                        //reach end of dates
                        if(x == calendar_view.getSelectedDates().size()-1){
                           
                            String myFormat = " dd MMM yy"; 
                            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                            newdate = sdf.format(calenderdate);
                            dates.add(newdate);
                            Log.d(TAG, "multiple endnewdate "+newdate);

                            //Get the starting date if only 1 date is selected
                            if(calendar_view.getSelectedDates().size() ==  1){
                                String myFormat2 = "yyMMdd"; 
                                SimpleDateFormat sdf2 = new SimpleDateFormat(myFormat2, Locale.US);
                                startingdate = sdf2.format(calenderdate);
                                Log.d(TAG, "yystartingdatexx "+startingdate);
                            }
                        }
                        //Not reach end of date, keep looping for multiple dates
                        else{
                            String myFormat = " dd MMM "; //In which you need put here
                            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                            newdate = sdf.format(calenderdate);
                            dates.add(newdate);
                            Log.d(TAG, "multiple newdate "+newdate);

                            //Get the starting date for multiple dates selected
                            if( x == 0){
                                String myFormat2 = "yyMMdd"; //In which you need put here
                                SimpleDateFormat sdf2 = new SimpleDateFormat(myFormat2, Locale.US);
                                startingdate = sdf2.format(calenderdate);
                                Log.d(TAG, "startingdatexx "+startingdate);
                            }
                        }
                    }

                    String finaldates = TextUtils.join(" / ", dates);
                    mpostDate.setText(finaldates);
                }
                else if (i == 2){
                    //RANGE DATES
                    String startdate = "";
                    String enddate = "";
                    for (int x = 0; x< calendar_view.getSelectedDates().size();x++){

                        //here you can fetch all dates

                        Date calenderdate = calendar_view.getSelectedDates().get(x);

                        if(x == calendar_view.getSelectedDates().size()-1){
                            //reach end of dates
                            String myFormat = " dd MMM yy"; //In which you need put here
                            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                            enddate = sdf.format(calenderdate);
                            Log.d(TAG, "range endnewdate "+enddate);

                            if(calendar_view.getSelectedDates().size() ==  1){
                                String myFormat2 = "yyMMdd"; //In which you need put here
                                SimpleDateFormat sdf2 = new SimpleDateFormat(myFormat2, Locale.US);
                                startingdate = sdf2.format(calenderdate);
                                Log.d(TAG, "yystartingdatexx "+startingdate);
                            }
                        }
                        else if (x == 0){
                            String myFormat = " dd MMM "; //In which you need put here
                            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                            startdate = sdf.format(calenderdate);
                            Log.d(TAG, "range startdate "+startdate);

                            String myFormat2 = "yyMMdd"; //In which you need put here
                            SimpleDateFormat sdf2 = new SimpleDateFormat(myFormat2, Locale.US);
                            startingdate = sdf2.format(calenderdate);
                            Log.d(TAG, "startingdatexx "+startingdate);
                        }
                    }
                    mpostDate.setText(startdate + "to" + enddate);
                }

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
    

    private void findApplicants( final Map<String, Object> newAppliedData ) {

        //Notify all PENDING applicants who has applied to the job about the job has changed
        mUserPostedPendingApplicants.child(ownuserid).child(postkey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnaphot : dataSnapshot.getChildren()) {
                    final String applicantuserid = userSnaphot.getKey();
                    mUserActivities.child(applicantuserid).child("Applied").child(postkey).updateChildren(newAppliedData);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Notify all SHORTLISTED applicants who has applied to the job about the job has changed
        mUserPostedShortlistedApplicants.child(ownuserid).child(postkey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnaphot : dataSnapshot.getChildren()) {
                    final String applicantuserid = userSnaphot.getKey();
                    mUserActivities.child(applicantuserid).child("Applied").child(postkey).updateChildren(newAppliedData);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Notify all HIRED applicants who has applied to the job about the job has changed
        mUserPostedHiredApplicants.child(ownuserid).child(postkey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnaphot : dataSnapshot.getChildren()) {
                    final String applicantuserid = userSnaphot.getKey();
                    mUserActivities.child(applicantuserid).child("Applied").child(postkey).updateChildren(newAppliedData);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void just_update( final DatabaseReference newEditPost, final DatabaseReference newPosted, final Map<String, Object> newJobData, final Map<String, Object> newPostedData, final Map<String, Object> newAppliedData) {

        newEditPost.updateChildren(newJobData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                findApplicants(newAppliedData);
            }
        });

        newPosted.updateChildren(newPostedData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mProgress.dismiss();
                    Toast.makeText(getApplicationContext(), "Post successfully updated!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }


    private void deleteold_createnew( final DatabaseReference newEditPost, final DatabaseReference newPosted, final Map<String, Object> newJobData, final Map<String, Object> newPostedData, final Map<String, Object> newAppliedData) {
        
        mJob.child(city).child(postkey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                newEditPost.setValue(dataSnapshot.getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        newEditPost.updateChildren(newJobData).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                mJob.child(city).child(postkey).removeValue();
                                findApplicants(newAppliedData);
                            }
                        });
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        newPosted.updateChildren(newPostedData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mProgress.dismiss();
                    Toast.makeText(getApplicationContext(), "Post successfully updated!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }
    
    private void getOtherData( final DatabaseReference newEditPost, final DatabaseReference newPosted, final String delete, final Map<String, Object> newJobData, final Map<String, Object> newPostedData, final Map<String, Object> newAppliedData,
                                 final Long tsLong, final String wages_val, final String spinnercurrencytext, final String spinnerratetext, final String date_val, final String location_val, final String wages_whole) {
        long categorylong = categorynum * 10000000000L;
        newJobData.put("category_negatedtime", (-1*(tsLong+categorylong)));

        //If wages are set
        if(!TextUtils.isEmpty(wages_val)&& !TextUtils.isEmpty(spinnercurrencytext) && !TextUtils.isEmpty(spinnerratetext)){

            newJobData.put("wages", spinnercurrencytext + " " + wages_val + " " + spinnerratetext);

            long wholeamount =  (Long.valueOf(wages_whole));
            long  wagescategory = 0;

            if(spinnerratetext.equals("per hour")){
                if(wholeamount<5){wagescategory = 11;}
                else if (wholeamount>=5 && wholeamount<=10){wagescategory = 12;}
                else if (wholeamount>=11 && wholeamount<=20){wagescategory = 13;}
                else if (wholeamount>=21 && wholeamount<=50){wagescategory = 14;}
                else if (wholeamount>50){wagescategory = 15;}
            }
            else if (spinnerratetext.equals("per day")){
                if(wholeamount<70){wagescategory = 21;}
                else if (wholeamount>=70 && wholeamount<=100){wagescategory = 22;}
                else if (wholeamount>=101 && wholeamount<=200){wagescategory = 23;}
                else if (wholeamount>=201 && wholeamount<=500){wagescategory = 24;}
                else if (wholeamount>500){wagescategory = 25;}
            }
            else if (spinnerratetext.equals("per month")){
                if(wholeamount<1000){wagescategory = 31;}
                else if (wholeamount>=1000 && wholeamount<=1500){wagescategory = 32;}
                else if (wholeamount>=1501 && wholeamount<=2000){wagescategory = 33;}
                else if (wholeamount>=2001 && wholeamount<=5000){wagescategory = 34;}
                else if (wholeamount>5000){wagescategory = 35;}
            }

            long mostrecent_wagesrange = tsLong +(wagescategory* 10000000000L)+((mspinnercurrency.getSelectedItemPosition()+11)*1000000000000L);
            newJobData.put("mostrecent_wagesrange", (-1*mostrecent_wagesrange));

            long categorywageslong = categorynum * 100000000000000L;
            newJobData.put("category_mostrecent_wagesrange", (-1*(categorywageslong + mostrecent_wagesrange)));

            //If wages and dates are set
            if(!startingdate.equals("")){

                long truncatedlongtime = tsLong%100000000;

                long mostrecent_wagesrange_startdate =  -1*((truncatedlongtime)+((Long.valueOf(startingdate))* 100000000L)+
                        (wagescategory * 100000000000000L)+((mspinnercurrency.getSelectedItemPosition()+11) * 10000000000000000L));
                newJobData.put("mostrecent_wagesrange_startdate", mostrecent_wagesrange_startdate);

                long category_mostrecent_wagesrange_startdate = -1*((truncatedlongtime)+((Long.valueOf(startingdate))* 100000000L)+
                        (wagescategory * 100000000000000L)+(mspinnercurrency.getSelectedItemPosition() * 10000000000000000L)+(categorynum * 100000000000000000L));
                newJobData.put("category_mostrecent_wagesrange_startdate", category_mostrecent_wagesrange_startdate);

            }
            else{

                newJobData.put("mostrecent_wagesrange_startdate", 0);

                newJobData.put("category_mostrecent_wagesrange_startdate", 0);
            }
        }
        else{
            newJobData.put("wages", "none");

            newJobData.put("mostrecent_wagesrange", 0);

            newJobData.put("category_mostrecent_wagesrange", 0);

            newJobData.put("mostrecent_wagesrange_startdate", 0);

            newJobData.put("category_mostrecent_wagesrange_startdate", 0);
        }

        //If dates are set
        if(!TextUtils.isEmpty(date_val)){

            newJobData.put("date", date_val);

            if(!startingdate.equals("")){
                long startdate =  Long.valueOf(startingdate);
                long mostrecent_startdate = (startdate*10000000000L)+tsLong;
                newJobData.put("mostrecent_startdate", (-1*mostrecent_startdate));

                long categorywageslong = categorynum * 10000000000000000L;
                long category_mostrecent_startdate = (startdate*10000000000L)+tsLong;
                newJobData.put("category_mostrecent_startdate", (-1*(categorywageslong + category_mostrecent_startdate)));
            }
            else{

                newJobData.put("mostrecent_startdate",0);

                newJobData.put("category_mostrecent_startdate",0);
            }
        }
        else{

            newJobData.put("date","none");

            newJobData.put("mostrecent_startdate",0);

            newJobData.put("category_mostrecent_startdate",0);
        }

        newJobData.put("longitude",longitude);
        newJobData.put("latitude",latitude);
        newJobData.put("fulladdress",location_val);

        
        if (delete.equals("true")) {
            deleteold_createnew(newEditPost, newPosted, newJobData, newPostedData, newAppliedData );
        }
        else {
            just_update(newEditPost, newPosted, newJobData, newPostedData, newAppliedData );
        }
    }

    private void startPosting(){

        final String title_val = mpostTitle.getText().toString().trim();
        final String desc_val = mpostDescrip.getText().toString().trim();
        final String company_val = mpostCompany.getText().toString().trim();
        final String category_val = meditpostCategory.getText().toString().trim();
        final String wages_val = mpostWages.getText().toString().trim();
        String[] separatedwages = wages_val.split("\\.");
        final String wages_whole = separatedwages[0];
        final String fullwages = wages_val.replace(".","");
        Log.d(TAG, "fullwages " + fullwages);
        final String date_val = mpostDate.getText().toString().trim();
        final String spinnerratetext = mspinnerrate.getSelectedItem().toString();
        final String spinnercurrencytext = mspinnercurrency.getSelectedItem().toString();

        switch (category_val) {
            case "Barista / Bartender": categorynum = 11; break;
            case "Beauty / Wellness": categorynum = 12; break;
            case "Chef / Kitchen Helper": categorynum = 13; break;
            case "Event Crew": categorynum = 14; break;
            case "Emcee": categorynum = 15; break;
            case "Education": categorynum = 16; break;
            case "Fitness / Gym": categorynum = 17; break;
            case "Modelling / Shooting": categorynum = 18; break;
            case "Mascot": categorynum = 19; break;
            case "Office / Admin": categorynum = 20; break;
            case "Promoter / Sampling": categorynum = 21; break;
            case "Roadshow": categorynum = 22; break;
            case "Roving Team": categorynum = 23; break;
            case "Retail / Consumer": categorynum = 24; break;
            case "Serving": categorynum = 25; break;
            case "Usher / Ambassador": categorynum = 26; break;
            case "Waiter / Waitress": categorynum = 27; break;
            case "Other": categorynum = 28; break;
        }

        final String location_val = mpostLocation.getText().toString().trim();

        if(!TextUtils.isEmpty(wages_val)) {
            if (Integer.parseInt(fullwages) < 100) {

                new  AlertDialog.Builder(EditPost.this)
                        .setTitle("Invalid Wages")
                        .setMessage("The smallest wages amount is 1.00")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {}
                        }).show();
                return;
            }
        }

        if(!TextUtils.isEmpty(title_val)&&!TextUtils.isEmpty(desc_val)&&!TextUtils.isEmpty(company_val)&&!TextUtils.isEmpty(category_val)&&!TextUtils.isEmpty(location_val)) {

            mProgress.setMessage("Saving..");
            mProgress.setCancelable(false);
            mProgress.show();

            final Long tsLong = System.currentTimeMillis() / 1000;
            
            //Save to myTalent database
            final Map<String, Object> newPostedData = new HashMap<>();
            newPostedData.put("title", title_val);
            newPostedData.put("desc", desc_val);
            newPostedData.put("category", category_val);
            newPostedData.put("company", company_val);

            final Map<String,Object> newJobData=new HashMap<>();
            newJobData.put("title", title_val);
            newJobData.put("lowertitle", title_val.toLowerCase());
            newJobData.put("desc", desc_val);
            newJobData.put("category", category_val);
            newJobData.put("company", company_val);


            final Map<String,Object> newAppliedData=new HashMap<>();
            newAppliedData.put("title", title_val);
            newAppliedData.put("desc", desc_val);
            newAppliedData.put("company", company_val);
            
            
            if (mImageUri!=null) {

                if(postimage !=null) {

                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReferenceFromUrl("gs://jobseed-2cb76.appspot.com");
                    StorageReference filepath = storageRef.child("JobPhotos").child(mImageUri.getLastPathSegment());


                    if(!postimage.equals(mjobbg1) && !postimage.equals(mjobbg2) && !postimage.equals(mjobbg3)){
                        StorageReference oldpath = storage.getReferenceFromUrl(postimage);
                        oldpath.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // File deleted successfully
                                Log.d(TAG, "onSuccess: deleted file = " + postimage);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Uh-oh, an error occurred!
                                Log.d(TAG, "onFailure: did not delete file");
                            }
                        });
                    }

                    filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            final Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            Log.d(TAG, "onSuccess: updated file = " + downloadUrl);

                            //If city has changed
                            if (newcity!=null && !newcity.equals(city)){

                                newEditPost =  mJob.child(newcity).child(postkey);
                                newPosted = mUserPosted.child(mAuth.getCurrentUser().getUid()).child(postkey);

                                newAppliedData.put("postimage", downloadUrl.toString());
                                newAppliedData.put("city", newcity);

                                newPostedData.put("city", newcity);
                                newPostedData.put("postimage", downloadUrl.toString());

                                newJobData.put("city", newcity);
                                newJobData.put("postimage", downloadUrl.toString());
                                newJobData.put("negatedtime", (-1*tsLong));
                                newJobData.put("time", ServerValue.TIMESTAMP);

                                mGeoFire = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("JobsLocation").child(newcity);
                                GeoHash geoHash = new GeoHash(new GeoLocation(latitude, longitude));
                                Map<String, Object> updates = new HashMap<>();
                                updates.put(postkey+"/g", geoHash.getGeoHashString());
                                updates.put(postkey+ "/l", Arrays.asList(latitude, longitude));
                                mGeoFire.updateChildren(updates);

                                DatabaseReference mOldGeoFire = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("JobsLocation").child(city);
                                mOldGeoFire.child(postkey).removeValue();

                                getOtherData(newEditPost, newPosted, "true", newJobData, newPostedData, newAppliedData, tsLong, wages_val, spinnercurrencytext, spinnerratetext, date_val, location_val, wages_whole);
                            }

                            //Else, city still same
                            else{
                                
                                newEditPost =  mJob.child(city).child(postkey);
                                newPosted = mUserPosted.child(mAuth.getCurrentUser().getUid()).child(postkey);

                                newAppliedData.put("postimage", downloadUrl.toString());
                                
                                newPostedData.put("postimage", downloadUrl.toString());

                                newJobData.put("postimage", downloadUrl.toString());
                                
                                //Update geofire location
                                mGeoFire = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("JobsLocation").child(city);
                                GeoHash geoHash = new GeoHash(new GeoLocation(latitude, longitude));
                                Map<String, Object> updates = new HashMap<>();
                                updates.put(postkey + "/g", geoHash.getGeoHashString());
                                updates.put(postkey + "/l", Arrays.asList(latitude, longitude));
                                mGeoFire.updateChildren(updates);

                                getOtherData(newEditPost, newPosted, "false", newJobData, newPostedData, newAppliedData, tsLong, wages_val, spinnercurrencytext, spinnerratetext, date_val, location_val, wages_whole);
                                
                            }
                        }
                    });

                    filepath.putFile(mImageUri).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(getApplicationContext(), "Upload failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            
            //No new Image changed
            else{
                //If city has changed
                if (newcity!=null && !newcity.equals(city)){

                    newEditPost =  mJob.child(newcity).child(postkey);
                    newPosted = mUserPosted.child(mAuth.getCurrentUser().getUid()).child(postkey);

                    newAppliedData.put("city", newcity);

                    newPostedData.put("city", newcity);

                    newJobData.put("city", newcity);
                    newJobData.put("negatedtime", (-1*tsLong));
                    newJobData.put("time", ServerValue.TIMESTAMP);

                    //Update geofire
                    mGeoFire = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("JobsLocation").child(newcity);
                    GeoHash geoHash = new GeoHash(new GeoLocation(latitude, longitude));
                    Map<String, Object> updates = new HashMap<>();
                    updates.put(postkey+"/g", geoHash.getGeoHashString());
                    updates.put(postkey+ "/l", Arrays.asList(latitude, longitude));
                    mGeoFire.updateChildren(updates);

                    DatabaseReference mOldGeoFire = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("JobsLocation").child(city);
                    mOldGeoFire.child(postkey).removeValue();

                    getOtherData(newEditPost, newPosted, "true", newJobData, newPostedData, newAppliedData, tsLong, wages_val, spinnercurrencytext, spinnerratetext, date_val, location_val, wages_whole);
                
                }
                else{
                    //City still same
                    newEditPost =  mJob.child(city).child(postkey);
                    newPosted = mUserPosted.child(mAuth.getCurrentUser().getUid()).child(postkey);
                    
                    //Update geofire
                    mGeoFire = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("JobsLocation").child(city);
                    GeoHash geoHash = new GeoHash(new GeoLocation(latitude, longitude));
                    Map<String, Object> updates = new HashMap<>();
                    updates.put(postkey + "/g", geoHash.getGeoHashString());
                    updates.put(postkey + "/l", Arrays.asList(latitude, longitude));
                    mGeoFire.updateChildren(updates);

                    getOtherData(newEditPost, newPosted, "false", newJobData, newPostedData, newAppliedData, tsLong, wages_val, spinnercurrencytext, spinnerratetext, date_val, location_val, wages_whole);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PLACE_PICKER_REQUEST)
        {
            mProgress.dismiss();
            if(resultCode==RESULT_OK){
                Place place = PlacePicker.getPlace(this,data);
                String address = place.getAddress().toString();
                String addressName = place.getName().toString();
                latitude = place.getLatLng().latitude;
                longitude = place.getLatLng().longitude;

                Geocoder geocoder = new Geocoder(EditPost.this, Locale.getDefault());
                List<Address> addresses ;
                try {
                    addresses = geocoder.getFromLocation(place.getLatLng().latitude, place.getLatLng().longitude, 1);
                    if (addresses.size() > 0) {
                        String[] addressSlice = place.getAddress().toString().split(", ");
                        //city = addressSlice[addressSlice.length - 2];
                        newcity = addresses.get(0).getAdminArea();

                        String postCode = addresses.get(0).getPostalCode();
                        if(newcity.equals(postCode)){
                            newcity = addressSlice[addressSlice.length - 3];
                        }

                        if(newcity==null){
                            newcity = addresses.get(0).getCountryName();
                        }

                        if(address.contains("Pulau Pinang") || address.contains("Penang")) {newcity = "Penang";}
                        else if (address.contains("Kuala Lumpur")) {newcity = "Kuala Lumpur";}
                        else if (address.contains("Labuan")) {newcity = "Labuan";}
                        else if (address.contains("Putrajaya")) {newcity = "Putrajaya";}
                        else if (address.contains("Johor")) {newcity = "Johor";}
                        else if (address.contains("Kedah")) {newcity = "Kedah";}
                        else if (address.contains("Kelantan")) {newcity = "Kelantan";}
                        else if (address.contains("Melaka")|| address.contains("Melacca")) {newcity = "Melacca";}
                        else if (address.contains("Negeri Sembilan")|| address.contains("Seremban")) {newcity = "Negeri Sembilan";}
                        //
                        else if (address.contains("Pahang")) {newcity = "Pahang";}
                        else if (address.contains("Perak")|| address.contains("Ipoh")) {newcity = "Perak";}
                        else if (address.contains("Perlis")) {newcity = "Perlis";}
                        else if (address.contains("Sabah")) {newcity = "Sabah";}
                        else if (address.contains("Sarawak")) {newcity = "Sarawak";}
                        else if (address.contains("Selangor")|| address.contains("Shah Alam")|| address.contains("Klang")) {newcity = "Selangor";}
                        else if (address.contains("Terengganu")) {newcity = "Terengganu";}

                        mpostLocation.setText(addressName+", "+address);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK){

            Uri imageuri = data.getData();

            CropImage.activity(imageuri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    //.setMaxCropResultSize(2500,1500)
                    .setAspectRatio(2,1)
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .start(this);
        }

        else if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            try {
                Uri imageuri = Uri.parse(mCurrentPhotoPath);

                mImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(mCurrentPhotoPath));

                CropImage.activity(imageuri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        //.setMaxCropResultSize(2500,1500)
                        .setAspectRatio(2,1)
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .start(this);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //set the image into imageview
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();
                Glide.with(getApplicationContext()).load(mImageUri)
                        .thumbnail(0.5f)
                        .centerCrop()
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(mPostImage);

                maddphotoBtn.setVisibility(View.GONE);
                mPostImagetxt.setVisibility(View.GONE);
                mchgPostImage.setVisibility(View.VISIBLE);
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = result.getError();
            }
            else if (resultCode == RESULT_CANCELED){
                mImageUri = null;
            }
            else{
                mImageUri = null;
            }
        }
    }
}
