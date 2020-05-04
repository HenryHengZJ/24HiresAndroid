package com.zjheng.jobseed.jobseed.PostScene;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jaredrummler.android.device.DeviceName;
import com.squareup.timessquare.CalendarPickerView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.zjheng.jobseed.jobseed.BuildConfig;
import com.zjheng.jobseed.jobseed.LoginScene.Login;
import com.zjheng.jobseed.jobseed.LoginScene.TnCDetails;
import com.zjheng.jobseed.jobseed.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static android.R.attr.data;
import static android.media.CamcorderProfile.get;
import static android.os.Build.VERSION_CODES.O;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class Post extends AppCompatActivity {

    private MenuItem itemPublish;

    private FirebaseAuth mAuth;
    private DatabaseReference mJob, mUserAccount, mUserActivities, mGeoFire, mCategory, mUserSortFilter, mUserPosted, mDefaultJobPhotos, mUserPhone;
    private GeoFire geoFire;

    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;


    private EditText mpostDescrip, mpostCompany, mpostLocation, mpostTitle, mpostWages, mpostDate;
    private ImageButton maddphotoBtn, mchgPostImage, mcleardateBtn, mclearwagesBtn;
    private ImageView mPostImage;
    private TextView mPostImagetxt;

    private int textChange;
    private String mCurrentPhotoPath, city, mjobbg1, mjobbg2, mjobbg3;
    private Bitmap mImageBitmap;
    private Uri mImageUri= null;
    private double latitude;
    private double longitude;
    private Toolbar mToolbar;
    private Spinner mspinnercurrency, mspinnerrate;
    private long categorynum;
    private int i;

    private ProgressDialog mProgress;
    private SharedPreferences prefs;

    private static int PLACE_PICKER_REQUEST = 1;
    private static final int MY_PERMISSION_REQUEST_LOCATION = 2;
    private static final int GALLERY_INTENT = 3;
    private static final int CAMERA_REQUEST_CODE = 4;
    private static final String TAG = "Post";

    public static EditText mpostCategory;

    private String prefstitle,prefsdesc,prefscompany,prefscategory,prefslocation, startingdate = "";

    boolean b = false;
    private boolean mImgClick = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_post);

        mAuth = FirebaseAuth.getInstance();

        mJob =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("Job");

        mCategory =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("Category");

        mUserAccount =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserAccount");

        mUserActivities =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserActivities");

        mUserPosted =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserPosted");

        mUserSortFilter =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("SortFilter");

        mDefaultJobPhotos =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("DefaultJobPhotos");

        mUserPhone =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserPhone");

        prefs = getSharedPreferences("saved", Context.MODE_PRIVATE);

        mProgress = new ProgressDialog(this);

        mToolbar = (Toolbar)findViewById(R.id.toolbar_other);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Post");
        mToolbar.setTitleTextColor(android.graphics.Color.WHITE);

        mToolbar.setNavigationIcon(R.mipmap.ic_close_white_24dp);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mpostDescrip = (EditText) findViewById(R.id.postDescrip);
        mpostCompany = (EditText) findViewById(R.id.postCompany);
        mpostCategory = (EditText) findViewById(R.id.postCategoryx);
        mpostLocation = (EditText) findViewById(R.id.postLocation);
        mpostTitle = (EditText) findViewById(R.id.postName);
        mPostImage = (ImageView) findViewById(R.id.PostImage);
        maddphotoBtn = (ImageButton) findViewById(R.id.addphotoBtn);
        mchgPostImage = (ImageButton) findViewById(R.id.chgPostImage);
        mPostImagetxt = (TextView) findViewById(R.id.textView4);
        mpostWages = (EditText) findViewById(R.id.postWages);
        mpostDate = (EditText) findViewById(R.id.postDate);
        mspinnerrate = (Spinner) findViewById(R.id.spinnerrate);
        mspinnercurrency = (Spinner) findViewById(R.id.spinnercurrency);
        mcleardateBtn = (ImageButton) findViewById(R.id.cleardateBtn);
        mclearwagesBtn = (ImageButton) findViewById(R.id.clearwagesBtn);

        String[] items = new String[]{"per hour", "per day", "per month"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Post.this, android.R.layout.simple_spinner_dropdown_item, items);
        mspinnerrate.setAdapter(adapter);

        String[] items2 = new String[]{"MYR", "SGD", "CHY", "USD", "GBP", "EUR","NTD","HKD", "INR", "IDR"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(Post.this, android.R.layout.simple_spinner_dropdown_item, items2);
        mspinnercurrency.setAdapter(adapter2);

        if(prefs.contains("titleval")){
            prefstitle = prefs.getString("titleval", "");
            mpostTitle.setText(prefs.getString("titleval", ""));
        }
        if(prefs.contains("descval")){
            prefsdesc = prefs.getString("descval", "");
            mpostDescrip.setText(prefs.getString("descval", ""));
        }
        if(prefs.contains("companyval")){
            prefscompany = prefs.getString("companyval", "");
            mpostCompany.setText(prefs.getString("companyval", ""));
        }
        if(prefs.contains("categoryval")){
            prefscategory = prefs.getString("categoryval", "");
            mpostCategory.setText(prefs.getString("categoryval", ""));
        }
        if(prefs.contains("locationval")){
            prefslocation = prefs.getString("locationval", "");
            mpostLocation.setText(prefs.getString("locationval", ""));
        }
        if(prefs.contains("wagesval")){
            mpostWages.setText(prefs.getString("wagesval", ""));
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
        if(prefs.contains("startingdateval")){
            startingdate = prefs.getString("startingdateval", "");
        }

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

        callback_verificvation();               ///function initialization

        mUserPhone.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    if (dataSnapshot.hasChild("verification")) {
                        String verified_val = dataSnapshot.child("verification").getValue().toString();

                        if (verified_val.equals("pending")) {
                            if (dataSnapshot.hasChild("verificationId") && dataSnapshot.hasChild("phonenum")) {

                                String verificationId_val = dataSnapshot.child("verificationId").getValue().toString();
                                String phonenum_val = dataSnapshot.child("phonenum").getValue().toString();

                                mVerificationId = verificationId_val;
                                verification2("false", phonenum_val);
                            }
                            else {
                                verification1();
                            }
                        }
                    }

                }
                else {
                    verification1();
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
                mpostDate.setHint("Tap to select dates of job");
            }
        });

        mclearwagesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mpostWages.setText("");
                mpostWages.setHint("0.00");
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

        mpostCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent categoryselect = new Intent(Post.this, CategorySelect.class);
                startActivity(categoryselect);
            }
        });

        mpostLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgress.setMessage("Loading..");
                mProgress.setCanceledOnTouchOutside(false);
                mProgress.setCancelable(false);
                mProgress.show();
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                Intent intent;
                try {
                    intent = builder.build(Post.this);
                    startActivityForResult(intent, PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }

        });

        mpostDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showdatepickerdialog();
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
                if(s.length() != 0) {
                    updatePublishButton();
                    invalidateOptionsMenu();

                }
                else{
                    updatePublishButton();
                    invalidateOptionsMenu();
                }
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
                if(s.length() != 0) {
                    updatePublishButton();
                    invalidateOptionsMenu();

                }
                else{
                    updatePublishButton();
                    invalidateOptionsMenu();
                }
            }
        });

        mpostCompany.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0) {
                    updatePublishButton();
                    invalidateOptionsMenu();

                }
                else{
                    updatePublishButton();
                    invalidateOptionsMenu();
                }
            }
        });

        mpostCategory.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0) {
                    updatePublishButton();
                    invalidateOptionsMenu();

                }
                else{
                    updatePublishButton();
                    invalidateOptionsMenu();
                }
            }
        });

        mpostLocation.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0) {
                    updatePublishButton();
                    invalidateOptionsMenu();

                }
                else{
                    updatePublishButton();
                    invalidateOptionsMenu();
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


    private void updatePublishButton(){

        final String title_val = mpostTitle.getText().toString().trim();
        final String desc_val = mpostDescrip.getText().toString().trim();
        final String company_val = mpostCompany.getText().toString().trim();
        final String category_val = mpostCategory.getText().toString().trim();
        final String location_val = mpostLocation.getText().toString().trim();

        if(!TextUtils.isEmpty(title_val)&&!TextUtils.isEmpty(desc_val)&&!TextUtils.isEmpty(company_val)&&!TextUtils.isEmpty(category_val)&&!TextUtils.isEmpty(location_val)) {
            textChange = 1;
            styleMenuButton(textChange);
            b = true;
        }
        else{
            textChange = 0;
            styleMenuButton(textChange);
            b = false;
        }

    }

    private void styleMenuButton(int txtChange) {
        // Find the menu item you want to style
        View view = findViewById(R.id.menuPublish);

        if(txtChange == 1){
            // Cast to a TextView instance if the menu item was found
            if (view != null && view instanceof TextView) {
                ((TextView) view).setTextColor( Color.WHITE ); // Make text colour white
                ((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_SP, 14); // Increase font size
            }
        }

        else if (txtChange == 0){
            // Cast to a TextView instance if the menu item was found
            if (view != null && view instanceof TextView) {
                ((TextView) view).setTextColor( Color.parseColor("#37000000") ); // Make text colour grey
                ((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_SP, 14); // Increase font size
            }
        }


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        itemPublish = menu.findItem(R.id.menuPublish);

        MenuItem itemSettings = menu.findItem(R.id.menuSettings);
        itemSettings.setVisible(false);

        MenuItem itemSearch = menu.findItem(R.id.menuSearch);
        itemSearch.setVisible(false);

        MenuItem item = menu.findItem(R.id.menuSearch2);
        item.setVisible(false);

        MenuItem itemSave = menu.findItem(R.id.menuSave);
        itemSave.setVisible(false);

        itemPublish.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                startPosting();
                return false;
            }
        });

        return true;
    }

    private void showdatepickerdialog(){

        i = 1;

        final Dialog dialog = new Dialog(Post.this);
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

        final CardView mmultiCardView = (CardView) dialog.findViewById(R.id.multiCardView);
        final CardView mRangeCardView = (CardView) dialog.findViewById(R.id.RangeCardView);

        //getting current
        final Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);

        final Date today = new Date();

        //add one year to calendar from todays date
        calendar_view.init(today, nextYear.getTime())
                .inMode(CalendarPickerView.SelectionMode.MULTIPLE);


        dialog.show();

        mmultiCardView.setOnClickListener(new View.OnClickListener() {
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

        mRangeCardView.setOnClickListener(new View.OnClickListener() {
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

                        if(x == calendar_view.getSelectedDates().size()-1){
                            //reach end of dates
                            String myFormat = " dd MMM yy"; //In which you need put here
                            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                            newdate = sdf.format(calenderdate);
                            dates.add(newdate);
                            Log.d(TAG, "multiple endnewdate "+newdate);

                            if(calendar_view.getSelectedDates().size() ==  1){
                                String myFormat2 = "yyMMdd"; //In which you need put here
                                SimpleDateFormat sdf2 = new SimpleDateFormat(myFormat2, Locale.US);
                                startingdate = sdf2.format(calenderdate);
                                Log.d(TAG, "yystartingdatexx "+startingdate);
                            }
                        }
                        else{
                            String myFormat = " dd MMM "; //In which you need put here
                            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                            newdate = sdf.format(calenderdate);
                            dates.add(newdate);
                            Log.d(TAG, "multiple newdate "+newdate);

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
                            String myFormat = " dd MMM yy"; //In which you need put here
                            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                            startdate = sdf.format(calenderdate);
                            Log.d(TAG, "range startdate "+startdate);

                            String myFormat2 = "yyMMdd"; //In which you need put here
                            SimpleDateFormat sdf2 = new SimpleDateFormat(myFormat2, Locale.US);
                            startingdate = sdf2.format(calenderdate);
                            Log.d(TAG, "startingdatexx "+startingdate);
                        }
                    }
                    mpostDate.setText(startdate + " to" + enddate);
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


    private void startPosting(){

        final String title_val = mpostTitle.getText().toString().trim();
        final String desc_val = mpostDescrip.getText().toString().trim();
        final String company_val = mpostCompany.getText().toString().trim();
        final String category_val = mpostCategory.getText().toString().trim();
        final String wages_val = mpostWages.getText().toString().trim();
        String[] separatedwages = wages_val.split("\\.");
        final String wages_whole = separatedwages[0];
        final String fullwages = wages_val.replace(".","");
        Log.d(TAG, "fullwages " + fullwages);
        final String date_val = mpostDate.getText().toString().trim();
        final String spinnerratetext = mspinnerrate.getSelectedItem().toString();
        final String spinnercurrencytext = mspinnercurrency.getSelectedItem().toString();

        switch (category_val) {
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

        final String location_val = mpostLocation.getText().toString().trim();


        if(!TextUtils.isEmpty(wages_val)) {
            if (Integer.parseInt(fullwages) < 100) {

                new  AlertDialog.Builder(Post.this)
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

            mProgress.setMessage("Uploading..");
            mProgress.setCancelable(false);
            mProgress.show();

            if (mImageUri!=null && city !=null) {

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReferenceFromUrl("gs://jobseed-2cb76.appspot.com");
                StorageReference filepath = storageRef.child("JobPhotos").child(mImageUri.getLastPathSegment());

                /*InputStream imageStream = null;
                try {
                    imageStream = getContentResolver().openInputStream(
                            mImageUri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                Bitmap bmp = BitmapFactory.decodeStream(imageStream);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 50, stream);
                byte[] byteArray = stream.toByteArray();

                UploadTask uploadTask = filepath.putBytes(byteArray);*/

                filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        final Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        final DatabaseReference newLocation = mJob.child(city);
                        final DatabaseReference newPost = newLocation.push();
                        final String keyval = newPost.getKey();

                        final DatabaseReference newPosted = mUserPosted.child(mAuth.getCurrentUser().getUid()).child(keyval);

                        final Map<String, Object> postData = new HashMap<>();
                        final Long tsLong = System.currentTimeMillis()/1000;
                        postData.put("negatedtime", (-1*tsLong));
                        postData.put("time", ServerValue.TIMESTAMP);

                        mUserAccount.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                //Save to Posted database
                                newPosted.child("title").setValue(title_val);
                                newPosted.child("desc").setValue(desc_val);
                                newPosted.child("company").setValue(company_val);
                                newPosted.child("city").setValue(city);
                                newPosted.child("pressed").setValue("true");
                                newPosted.child("closed").setValue("false");
                                newPosted.child("totalhiredcount").setValue(0);
                                newPosted.child("applicantscount").setValue(0);
                                newPosted.child("newapplicantscount").setValue(0);
                                newPosted.child("postimage").setValue(downloadUrl.toString());

                                mGeoFire = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("JobsLocation").child(city);

                                geoFire = new GeoFire(mGeoFire);

                                geoFire.setLocation(keyval, new GeoLocation(latitude, longitude), new GeoFire.CompletionListener() {
                                    @Override
                                    public void onComplete(String key, DatabaseError error) {
                                        if (error != null) {
                                            System.err.println("There was an error saving the location to GeoFire: " + error);
                                        } else {
                                            System.out.println("Location saved on server successfully!");
                                        }
                                    }
                                });

                                //Save to Job database
                                long categorylong = categorynum * 10000000000L;
                                postData.put("category_negatedtime", -1*(tsLong+categorylong));

                                //If wages are set
                                if(!TextUtils.isEmpty(wages_val)&& !TextUtils.isEmpty(spinnercurrencytext) && !TextUtils.isEmpty(spinnerratetext)){


                                    postData.put("wages",spinnercurrencytext + " " + wages_val + " " + spinnerratetext);


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
                                    postData.put("mostrecent_wagesrange",-1*mostrecent_wagesrange);

                                    long categorywageslong = categorynum * 100000000000000L;
                                    postData.put("category_mostrecent_wagesrange",-1*(categorywageslong + mostrecent_wagesrange));

                                    //If wages and dates are set
                                    if(!startingdate.equals("")){

                                        long truncatedlongtime = tsLong%100000000;
                                        Log.d(TAG, "trun " + truncatedlongtime);

                                        long mostrecent_wagesrange_startdate =  -1*((truncatedlongtime)+((Long.valueOf(startingdate))* 100000000L)+
                                                (wagescategory * 100000000000000L)+((mspinnercurrency.getSelectedItemPosition()+11) * 10000000000000000L));
                                        postData.put("mostrecent_wagesrange_startdate",mostrecent_wagesrange_startdate);

                                        long category_mostrecent_wagesrange_startdate = -1*((truncatedlongtime)+((Long.valueOf(startingdate))* 100000000L)+
                                                (wagescategory * 100000000000000L)+(mspinnercurrency.getSelectedItemPosition() * 10000000000000000L)+(categorynum * 100000000000000000L));
                                        postData.put("category_mostrecent_wagesrange_startdate",category_mostrecent_wagesrange_startdate);
                                    }
                                    else{
                                        postData.put("mostrecent_wagesrange_startdate",0);
                                        postData.put("category_mostrecent_wagesrange_startdate",0);
                                    }
                                }
                                else{
                                    postData.put("wages","none");
                                    postData.put("mostrecent_wagesrange",0);
                                    postData.put("category_mostrecent_wagesrange",0);
                                    postData.put("mostrecent_wagesrange_startdate",0);
                                    postData.put("category_mostrecent_wagesrange_startdate",0);
                                }

                                //If dates are set
                                if(!TextUtils.isEmpty(date_val)){
                                    long myLong = (-1*tsLong);
                                    postData.put("date",date_val);

                                    if(!startingdate.equals("")){
                                        long startdate =  Long.valueOf(startingdate);
                                        long mostrecent_startdate = (startdate*10000000000L)+tsLong;
                                        postData.put("mostrecent_startdate",-1*mostrecent_startdate);

                                        long categorywageslong = categorynum * 10000000000000000L;
                                        long category_mostrecent_startdate = (startdate*10000000000L)+tsLong;
                                        postData.put("category_mostrecent_startdate",-1*(categorywageslong + category_mostrecent_startdate));
                                    }
                                    else{
                                        postData.put("mostrecent_startdate",0);
                                        postData.put("category_mostrecent_startdate",0);
                                    }
                                }
                                else{
                                    postData.put("date","none");
                                    postData.put("mostrecent_startdate",0);
                                    postData.put("category_mostrecent_startdate",0);
                                }

                                postData.put("title",title_val);
                                postData.put("lowertitle",title_val.toLowerCase());
                                postData.put("desc",desc_val);
                                postData.put("category",category_val);
                                postData.put("company",company_val);
                                postData.put("fulladdress",location_val);
                                postData.put("city",city);
                                postData.put("latitude",latitude);
                                postData.put("longitude",longitude);
                                postData.put("uid",dataSnapshot.child("id").getValue());
                                postData.put("postimage",downloadUrl.toString());
                                postData.put("userimage",dataSnapshot.child("image").getValue());
                                postData.put("postkey",keyval);
                                postData.put("closed","false");
                                postData.put("username",dataSnapshot.child("name").getValue());

                                newPost.setValue(postData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            prefs.edit().clear().apply();

                                            mUserSortFilter.child(mAuth.getCurrentUser().getUid()).removeValue();

                                            Toast.makeText(Post.this, "Post Successfully Published!", Toast.LENGTH_LONG).show();
                                            mProgress.dismiss();

                                            // Go to MainActivity
                                            setResult(Activity.RESULT_OK);
                                            finish();
                                        }
                                        else{
                                            Toast.makeText(Post.this, "Post Failed", Toast.LENGTH_LONG).show();
                                            mProgress.dismiss();
                                        }
                                    }
                                });

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                });

                filepath.putFile(mImageUri).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getApplicationContext(), "Upload failed", Toast.LENGTH_SHORT).show();
                        mProgress.dismiss();
                    }
                });
            }

            else if (mImageUri==null && city !=null){

                final DatabaseReference newLocation = mJob.child(city);
                final DatabaseReference newPost = newLocation.push();
                final String keyval = newPost.getKey();

                final DatabaseReference newPosted = mUserPosted.child(mAuth.getCurrentUser().getUid()).child(keyval);

                final Map<String, Object> postData = new HashMap<>();
                final Long tsLong = System.currentTimeMillis()/1000;
                postData.put("negatedtime", (-1*tsLong));
                postData.put("time", ServerValue.TIMESTAMP);

                mUserAccount.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String imageurlstring = "";

                        Random random = new Random();
                        int value = random.nextInt(3);

                        if (value == 0) {
                            imageurlstring = mjobbg1;
                        }
                        else if (value == 1) {
                            imageurlstring = mjobbg2;
                        }
                        else if (value == 2) {
                            imageurlstring = mjobbg3;
                        }

                        Log.d(TAG, "imageurlstring = "+ imageurlstring);

                        //Save to Posted database
                        newPosted.child("title").setValue(title_val);
                        newPosted.child("desc").setValue(desc_val);
                        newPosted.child("company").setValue(company_val);
                        newPosted.child("city").setValue(city);
                        newPosted.child("pressed").setValue("true");
                        newPosted.child("closed").setValue("false");
                        newPosted.child("totalhiredcount").setValue(0);
                        newPosted.child("applicantscount").setValue(0);
                        newPosted.child("newapplicantscount").setValue(0);
                        newPosted.child("postimage").setValue(imageurlstring);

                        mGeoFire = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("JobsLocation").child(city);

                        geoFire = new GeoFire(mGeoFire);

                        geoFire.setLocation(keyval, new GeoLocation(latitude, longitude), new GeoFire.CompletionListener() {
                            @Override
                            public void onComplete(String key, DatabaseError error) {
                                if (error != null) {
                                    System.err.println("There was an error saving the location to GeoFire: " + error);
                                } else {
                                    System.out.println("Location saved on server successfully!");
                                }
                            }
                        });

                        //Save to Job database
                        long categorylong = categorynum * 10000000000L;
                        postData.put("category_negatedtime", -1*(tsLong+categorylong));


                        //If wages are set
                        if(!TextUtils.isEmpty(wages_val)&& !TextUtils.isEmpty(spinnercurrencytext) && !TextUtils.isEmpty(spinnerratetext)){

                            postData.put("wages",spinnercurrencytext + " " + wages_val + " " + spinnerratetext);

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
                            postData.put("mostrecent_wagesrange",-1*mostrecent_wagesrange);

                            long categorywageslong = categorynum * 100000000000000L;
                            postData.put("category_mostrecent_wagesrange",-1*(categorywageslong + mostrecent_wagesrange));

                            //If wages and dates are set
                            if(!startingdate.equals("")){

                                long truncatedlongtime = tsLong%100000000;
                                Log.d(TAG, "trun " + truncatedlongtime);

                                long mostrecent_wagesrange_startdate =  -1*((truncatedlongtime)+((Long.valueOf(startingdate))* 100000000L)+
                                        (wagescategory * 100000000000000L)+((mspinnercurrency.getSelectedItemPosition()+11) * 10000000000000000L));
                                postData.put("mostrecent_wagesrange_startdate",mostrecent_wagesrange_startdate);

                                long category_mostrecent_wagesrange_startdate = -1*((truncatedlongtime)+((Long.valueOf(startingdate))* 100000000L)+
                                        (wagescategory * 100000000000000L)+(mspinnercurrency.getSelectedItemPosition() * 10000000000000000L)+(categorynum * 100000000000000000L));
                                postData.put("category_mostrecent_wagesrange_startdate",category_mostrecent_wagesrange_startdate);
                            }
                            else{
                                postData.put("mostrecent_wagesrange_startdate",0);
                                postData.put("category_mostrecent_wagesrange_startdate",0);
                            }
                        }
                        else{
                            postData.put("wages","none");
                            postData.put("mostrecent_wagesrange",0);
                            postData.put("category_mostrecent_wagesrange",0);
                            postData.put("mostrecent_wagesrange_startdate",0);
                            postData.put("category_mostrecent_wagesrange_startdate",0);
                        }

                        //If dates are set
                        if(!TextUtils.isEmpty(date_val)){
                            long myLong = (-1*tsLong);
                            postData.put("date",date_val);

                            if(!startingdate.equals("")){
                                long startdate =  Long.valueOf(startingdate);
                                long mostrecent_startdate = (startdate*10000000000L)+tsLong;
                                postData.put("mostrecent_startdate",-1*mostrecent_startdate);

                                long categorywageslong = categorynum * 10000000000000000L;
                                long category_mostrecent_startdate = (startdate*10000000000L)+tsLong;
                                postData.put("category_mostrecent_startdate",-1*(categorywageslong + category_mostrecent_startdate));
                            }
                            else{
                                postData.put("mostrecent_startdate",0);
                                postData.put("category_mostrecent_startdate",0);
                            }
                        }
                        else{
                            postData.put("date","none");
                            postData.put("mostrecent_startdate",0);
                            postData.put("category_mostrecent_startdate",0);
                        }

                        postData.put("title",title_val);
                        postData.put("lowertitle",title_val.toLowerCase());
                        postData.put("desc",desc_val);
                        postData.put("category",category_val);
                        postData.put("company",company_val);
                        postData.put("fulladdress",location_val);
                        postData.put("city",city);
                        postData.put("latitude",latitude);
                        postData.put("longitude",longitude);
                        postData.put("uid",dataSnapshot.child("id").getValue());
                        postData.put("postimage",imageurlstring);
                        postData.put("userimage",dataSnapshot.child("image").getValue());
                        postData.put("postkey",keyval);
                        postData.put("closed","false");
                        postData.put("username",dataSnapshot.child("name").getValue());

                        newPost.setValue(postData).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    prefs.edit().clear().apply();

                                    mUserSortFilter.child(mAuth.getCurrentUser().getUid()).removeValue();

                                    Toast.makeText(Post.this, "Post Successfully Published!", Toast.LENGTH_LONG).show();
                                    mProgress.dismiss();

                                    // Go to MainActivity
                                    setResult(Activity.RESULT_OK);
                                    finish();
                                }
                                else{
                                    Toast.makeText(Post.this, "Post Failed", Toast.LENGTH_LONG).show();
                                    mProgress.dismiss();
                                }
                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        itemPublish.setEnabled(b);
        super.onPrepareOptionsMenu(menu);
        return true;
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

        final String title_val = mpostTitle.getText().toString().trim();
        final String desc_val = mpostDescrip.getText().toString().trim();
        final String company_val = mpostCompany.getText().toString().trim();
        final String category_val = mpostCategory.getText().toString().trim();
        final String location_val = mpostLocation.getText().toString().trim();
        final String wages_val = mpostWages.getText().toString().trim();
        final String date_val = mpostDate.getText().toString().trim();
        final int spinnerrateint = mspinnerrate.getSelectedItemPosition();
        final int spinnercurrencyint = mspinnercurrency.getSelectedItemPosition();

        if(!TextUtils.isEmpty(title_val) || !TextUtils.isEmpty(desc_val) || !TextUtils.isEmpty(company_val)
                || !TextUtils.isEmpty(category_val) || !TextUtils.isEmpty(location_val)
                || !TextUtils.isEmpty(wages_val) || !TextUtils.isEmpty(date_val)) {

            // custom dialog
            final Dialog dialog = new Dialog(Post.this);
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
                    prefs.edit().putString("companyval", company_val).apply();
                    prefs.edit().putString("categoryval", category_val).apply();
                    prefs.edit().putString("locationval", location_val).apply();
                    prefs.edit().putString("wagesval", wages_val).apply();
                    prefs.edit().putString("dateval", date_val).apply();
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

    public void makeLinks(TextView textView, String[] links, ClickableSpan[] clickableSpans) {
        SpannableString spannableString = new SpannableString(textView.getText());
        for (int i = 0; i < links.length; i++) {
            ClickableSpan clickableSpan = clickableSpans[i];
            String link = links[i];

            int startIndexOfLink = textView.getText().toString().indexOf(link);
            spannableString.setSpan(clickableSpan, startIndexOfLink, startIndexOfLink + link.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setText(spannableString, TextView.BufferType.SPANNABLE);
    }


    private void verification2(String starttimer, final String wholephonenum) {
        final Dialog dialog = new Dialog(Post.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.verification2_dialog);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(lp);
        dialog.setCancelable(false);

        final TextView mtimeoutxt =  dialog.findViewById(R.id.timeoutxt);
        final TextView meditphonetxt =  dialog.findViewById(R.id.editphonetxt);
        final TextView msupporttxt =  dialog.findViewById(R.id.supporttxt);

        final Button mverifyBtn =  dialog.findViewById(R.id.verifyBtn);
        final Button mresendBtn =  dialog.findViewById(R.id.resendBtn);
        final ImageButton mcloseBtn = dialog.findViewById(R.id.closeBtn);

        final EditText mdigittxt1 =  dialog.findViewById(R.id.digittxt1);
        mdigittxt1.requestFocus();
        final EditText mdigittxt2 =  dialog.findViewById(R.id.digit1txt2);
        final EditText mdigittxt3 =  dialog.findViewById(R.id.digit1txt3);

        final EditText mdigittxt4 =  dialog.findViewById(R.id.digit1txt4);
        final EditText mdigittxt5 =  dialog.findViewById(R.id.digit1txt5);
        final EditText mdigittxt6 =  dialog.findViewById(R.id.digit1txt6);

        ClickableSpan editphoneClick = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplicationContext(), "Privacy Policy Clicked", Toast.LENGTH_SHORT).show();
                verification1();
                dialog.dismiss();
            }
        };

        ClickableSpan supportClick = new ClickableSpan() {
            @Override
            public void onClick(View view) {

                String verName = BuildConfig.VERSION_NAME;
                int verCode = BuildConfig.VERSION_CODE;

                String deviceName = DeviceName.getDeviceName();

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "support@24hires.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Phone Authentication Problem");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "\n\n\n\n*Please do not delete the following information:\nUserID: " + mAuth.getCurrentUser().getUid() + " App version: " + verName
                        + " Device: " + deviceName);
                startActivity(Intent.createChooser(emailIntent, "Send Report"));
            }
        };

        makeLinks(meditphonetxt, new String[] { "Edit Phone Number"}, new ClickableSpan[] {
                editphoneClick
        });

        makeLinks(msupporttxt, new String[] { "Contact Support"}, new ClickableSpan[] {
                supportClick
        });


        mcloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                onBackPressed();
            }
        });

        if (starttimer.equals("true")) {
            CountDownTimer mCountDownTimer = new CountDownTimer(60*1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                    long seconds = millisUntilFinished / 1000;

                    mtimeoutxt.setText(String.valueOf(seconds));
                    mresendBtn.setAlpha(0.5f);
                    mresendBtn.setEnabled(false);
                }

                @Override
                public void onFinish() {
                    mtimeoutxt.setText("0");
                    mresendBtn.setAlpha(1f);
                    mresendBtn.setEnabled(true);
                }
            };

            mCountDownTimer.start();
        }
        else {
            mtimeoutxt.setText("0");
            mresendBtn.setAlpha(1f);
            mresendBtn.setEnabled(true);
        }

        mdigittxt1.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start,int before, int count)
            {
                // TODO Auto-generated method stub
                if (s.length() == 0) {
                    mdigittxt1.setBackgroundResource(R.drawable.applicant_grey_round);
                }
                else {
                    mdigittxt1.setBackgroundResource(R.drawable.applicant_red_round);
                    if(s.length()==1)     //size as per your requirement
                    {
                        mdigittxt2.requestFocus();
                        mdigittxt2.setBackgroundResource(R.drawable.applicant_red_round);
                    }
                }

            }
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

        });

        mdigittxt2.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start,int before, int count)
            {
                // TODO Auto-generated method stub
                if (s.length() == 0) {
                    mdigittxt2.setBackgroundResource(R.drawable.applicant_grey_round);
                }
                else {
                    mdigittxt2.setBackgroundResource(R.drawable.applicant_red_round);
                    if(s.length()==1)     //size as per your requirement
                    {
                        mdigittxt3.requestFocus();
                        mdigittxt3.setBackgroundResource(R.drawable.applicant_red_round);
                    }
                }

            }
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

        });

        mdigittxt3.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start,int before, int count)
            {
                // TODO Auto-generated method stub
                if (s.length() == 0) {
                    mdigittxt3.setBackgroundResource(R.drawable.applicant_grey_round);
                }
                else {
                    mdigittxt3.setBackgroundResource(R.drawable.applicant_red_round);
                    if(s.length()==1)     //size as per your requirement
                    {
                        mdigittxt4.requestFocus();
                        mdigittxt4.setBackgroundResource(R.drawable.applicant_red_round);
                    }
                }

            }
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

        });

        mdigittxt4.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start,int before, int count)
            {
                // TODO Auto-generated method stub
                if (s.length() == 0) {
                    mdigittxt4.setBackgroundResource(R.drawable.applicant_grey_round);
                }
                else {
                    mdigittxt4.setBackgroundResource(R.drawable.applicant_red_round);
                    if(s.length()==1)     //size as per your requirement
                    {
                        mdigittxt5.requestFocus();
                        mdigittxt5.setBackgroundResource(R.drawable.applicant_red_round);
                    }
                }

            }
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

        });

        mdigittxt5.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start,int before, int count)
            {
                // TODO Auto-generated method stub
                if (s.length() == 0) {
                    mdigittxt5.setBackgroundResource(R.drawable.applicant_grey_round);
                }
                else {
                    mdigittxt5.setBackgroundResource(R.drawable.applicant_red_round);
                    if(s.length()==1)     //size as per your requirement
                    {
                        mdigittxt6.requestFocus();
                        mdigittxt6.setBackgroundResource(R.drawable.applicant_red_round);
                    }
                }

            }
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

        });

        mdigittxt6.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start,int before, int count)
            {
                // TODO Auto-generated method stub
                if (s.length() == 0) {
                    mdigittxt6.setBackgroundResource(R.drawable.applicant_grey_round);
                }
                else {
                    mdigittxt6.setBackgroundResource(R.drawable.applicant_red_round);

                }

            }
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

        });

        Log.e(TAG, "mVerificationId "+ mVerificationId);



        dialog.show();

        mverifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                String digit1 = mdigittxt1.getText().toString().trim();
                String digit2 = mdigittxt2.getText().toString().trim();
                String digit3 = mdigittxt3.getText().toString().trim();
                String digit4 = mdigittxt4.getText().toString().trim();
                String digit5 = mdigittxt5.getText().toString().trim();
                String digit6 = mdigittxt6.getText().toString().trim();

                String finaldigits = digit1 + digit2 + digit3 + digit4 + digit5 + digit6;

                if (!TextUtils.isEmpty(digit1) && !TextUtils.isEmpty(digit2) && !TextUtils.isEmpty(digit3)
                        && !TextUtils.isEmpty(digit4) && !TextUtils.isEmpty(digit5) && !TextUtils.isEmpty(digit6)) {

                    if (mVerificationId != null) {
                        verifyPhoneNumberWithCode(mVerificationId, finaldigits, dialog);
                    }
                    else {
                        verification1();
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Please Resend Code", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        mresendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                if (mVerificationId != null && mResendToken != null) {
                    Log.e(TAG, "resendVerificationCode");
                    resendVerificationCode(wholephonenum, mResendToken);
                }
                else {
                    Log.e(TAG, "startPhoneNumberVerification");
                    startPhoneNumberVerification(wholephonenum);
                }

            }
        });
    }


    private void verification1() {
        final Dialog dialog = new Dialog(Post.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.verification_dialog);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(lp);
        dialog.setCancelable(false);

        final ImageView mlogo = dialog.findViewById(R.id.logo);
        final Button mnextBtn =  dialog.findViewById(R.id.nextBtn);
        final Button mcancelBtn =  dialog.findViewById(R.id.cancelBtn);
        final Spinner mspinnercountrycode =  dialog.findViewById(R.id.spinnercountrycode);
        final EditText mphonenumtxt =  dialog.findViewById(R.id.phonenumtxt);
        String[] countrycodes = new String[]{"MYR +60", "SGD +65"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(Post.this,android.R.layout.simple_spinner_dropdown_item, countrycodes);
        mspinnercountrycode.setAdapter(adapter2);

        dialog.show();

        mnextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String phonenum_val = mphonenumtxt.getText().toString().trim();
                String countrycode_val = mspinnercountrycode.getSelectedItem().toString().trim();
                String country_codes[] = countrycode_val.split(" ");
                String wholephonenum = country_codes[1] + phonenum_val ;

                if (!TextUtils.isEmpty(phonenum_val)) {
                    startPhoneNumberVerification(wholephonenum);

                    Log.e(TAG, "wholephonenum " + wholephonenum);

                    verification2("true", wholephonenum);

                    mUserPhone.child(mAuth.getCurrentUser().getUid()).child("phonenum").setValue(wholephonenum);

                    dialog.dismiss();
                }

                else {
                    mphonenumtxt.setError("Empty phone number");
                }

            }
        });

        mcancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();

                onBackPressed();
            }
        });
    }

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]
    }

    private void verifyCredential(PhoneAuthCredential credential, final Dialog mdialog) {

        final ProgressDialog mProgress = new ProgressDialog(this);
        mProgress.setMessage("Verifying..");
        mProgress.setCancelable(false);
        mProgress.show();

        FirebaseUser mUser= mAuth.getCurrentUser();

        if (mUser != null) {
            mAuth.getCurrentUser().updatePhoneNumber(credential).addOnCompleteListener(this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.e(TAG, "signInWithCredential:success");

                        mUserPhone.child(mAuth.getCurrentUser().getUid()).child("verification").setValue("verified");

                        Snackbar.make(findViewById(android.R.id.content), "Mobile Verified Successfully.",
                                Snackbar.LENGTH_SHORT).show();

                        mdialog.dismiss();
                        mProgress.dismiss();

                    } else {
                        mProgress.dismiss();
                        Log.e(TAG, "signInWithCredential:failure", task.getException());
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            //mVerificationField.setError("Invalid code.");
                            //Snackbar.make(findViewById(android.R.id.content), "Invalid Code.",Snackbar.LENGTH_SHORT).show();
                            Toast.makeText(getApplicationContext(), "Please resend code", Toast.LENGTH_SHORT).show();
                        } else {

                            Toast.makeText(getApplicationContext(), "Phone has been used by other account. Try using another phone", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

        }

    }

    private void verifyPhoneNumberWithCode(String verificationId, String code, final Dialog mdialog) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
        verifyCredential(credential, mdialog);
    }


    private void callback_verificvation() {

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verificaiton without
                //     user action.

                Log.e(TAG, "onVerificationCompleted");
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.


                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Log.e(TAG, "FirebaseAuthInvalidCredentialsException");

                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Log.e(TAG, "FirebaseTooManyRequestsException");

                }
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.


                // Save verification ID and resending token so we can use them later
                Log.e(TAG, "onCodeSent");
                Log.e(TAG, "mVerificationId " + verificationId);
                Log.e(TAG, "mResendToken " + token);

                Toast.makeText(getApplicationContext(), "Verification Code Sent!", Toast.LENGTH_SHORT).show();

                mUserPhone.child(mAuth.getCurrentUser().getUid()).child("verification").setValue("pending");

                if (verificationId != null) {
                    mVerificationId = verificationId;
                    mUserPhone.child(mAuth.getCurrentUser().getUid()).child("verificationId").setValue(mVerificationId);
                }
                if (token != null) {
                    mResendToken = token;
                }
            }
        };
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.nochange, R.anim.pulldown);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PLACE_PICKER_REQUEST)
        {
            mProgress.dismiss();
            if(resultCode==RESULT_OK){
                Place place = PlacePicker.getPlace(this,data);
                city = "";
                String address = place.getAddress().toString();
                String addressName = place.getName().toString();
                latitude = place.getLatLng().latitude;
                longitude = place.getLatLng().longitude;

                Geocoder geocoder = new Geocoder(Post.this, Locale.getDefault());
                List<Address> addresses ;
                try {
                    addresses = geocoder.getFromLocation(place.getLatLng().latitude, place.getLatLng().longitude, 1);
                    if (addresses.size() > 0) {
                        String[] addressSlice = place.getAddress().toString().split(", ");
                        //city = addressSlice[addressSlice.length - 2];
                        city = addresses.get(0).getAdminArea();

                        String postCode = addresses.get(0).getPostalCode();
                        if(city.equals(postCode)){
                            city = addressSlice[addressSlice.length - 3];
                        }

                        if(city==null){
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

                        Log.d(TAG, "addressName and address: " + addressName+", "+address);

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
                        .setAspectRatio(2,1)
                        //.setMaxCropResultSize(2500,1500)
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
                //Picasso.with(Post.this).load(mImageUri).fit().centerInside().into(mPostImage);

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
