package com.zjheng.jobseed.jobseed.ApplicantsScene;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.squareup.timessquare.CalendarPickerView;
import com.zjheng.jobseed.jobseed.ActivitiesScene.EditPostScene.EditPost;
import com.zjheng.jobseed.jobseed.JobDetail;
import com.zjheng.jobseed.jobseed.MainActivity;
import com.zjheng.jobseed.jobseed.PostScene.Post;
import com.zjheng.jobseed.jobseed.R;


import java.io.IOException;
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

import de.hdodenhof.circleimageview.CircleImageView;

import static android.R.attr.editable;
import static android.R.attr.value;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.zjheng.jobseed.jobseed.PostScene.Post.mpostCategory;
import static com.zjheng.jobseed.jobseed.R.drawable.post_category;
import static com.zjheng.jobseed.jobseed.R.drawable.post_wages;
import static com.zjheng.jobseed.jobseed.R.id.editBtn;
import static com.zjheng.jobseed.jobseed.R.id.paymentBtn1;
import static com.zjheng.jobseed.jobseed.R.id.postNumDates;
import static com.zjheng.jobseed.jobseed.R.id.postTipsPay;
import static com.zjheng.jobseed.jobseed.R.id.spinnerrate;
import static com.zjheng.jobseed.jobseed.R.id.userimage;

public class HireForm extends AppCompatActivity {

    private String category;
    private CircleImageView mpostImage;
    private TextView mpostTitle, mpostCompany, mpostDescrip, mnumoftxt, mbasicpaytxt;
    private EditText mpostDate, mpostLocation, mpostNumDates, mpostBasicPay, mpostTotalBasicPay, mpostTipsPay, mpostTotalAllPay, mpostPaymentDate, mpostAddNote, mpostHours;
    private CardView mhireCardView, mjobcardview, mupdateCardView;
    private Spinner mspinnerrate, mspinnercurrency;
    private ImageButton mcleardateBtn;
    private RelativeLayout mhoursLay;

    private String user_uid, city, post_key, post_title, post_desc, current_useruid, current_userimage, current_username;
    private String post_company, post_date, post_location, post_image, user_name, user_image, post_wages, updateval ;
    private int i, numDays, spinneritem;

    private DatabaseReference mJob, mUserChatList, mUserPostedShortlistedApplicants, mUserPostedHiredApplicants,
            mUserActivities, mChatRoom, mUserHireNotification, mUserAccount, mUserInfo, mUserPosted;
    private FirebaseAuth mAuth;

    private ProgressDialog mProgress;

    private double latitude;
    private double longitude;

    private boolean hiredClick = false;
    private ArrayList<Date> chosendates = new ArrayList<Date>();

    private static final String TAG = "HireForm";
    private static int PLACE_PICKER_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hireform);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mJob =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("Job");

        mUserChatList =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserChatList");

        mUserPosted =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserPosted");

        mUserPostedShortlistedApplicants =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserPostedShortlistedApplicants");

        mUserPostedHiredApplicants =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserPostedHiredApplicants");

        mUserActivities =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserActivities");

        mChatRoom =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("ChatRoom");

        mUserHireNotification = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("HireNotification");

        mUserAccount = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserAccount");

        mUserInfo =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserInfo");

        mAuth = FirebaseAuth.getInstance();
        current_useruid = mAuth.getCurrentUser().getUid();

        user_uid = getIntent().getStringExtra("user_uid");
        post_key = getIntent().getStringExtra("post_key");
        city = getIntent().getStringExtra("city");
        updateval = getIntent().getStringExtra("update");

        mProgress = new ProgressDialog(this);

        mpostImage = (CircleImageView) findViewById(R.id.postImage);
        mhireCardView = (CardView) findViewById(R.id.hireCardView);
        mjobcardview = (CardView) findViewById(R.id.jobcardview);
        mupdateCardView = (CardView) findViewById(R.id.updateCardView);

        mhoursLay = findViewById(R.id.hoursLay);

        if (updateval.equals("true")) {
            mupdateCardView.setVisibility(VISIBLE);
            mhireCardView.setVisibility(GONE);
        }
        else {
            mupdateCardView.setVisibility(GONE);
            mhireCardView.setVisibility(VISIBLE);
        }

        mpostTitle = (TextView) findViewById(R.id.postTitle);
        mpostCompany = (TextView) findViewById(R.id.postCompany);
        mpostDescrip = (TextView) findViewById(R.id.postDescrip);
        mnumoftxt = (TextView) findViewById(R.id.numoftxt);
        mbasicpaytxt = (TextView) findViewById(R.id.basicpaytxt);

        mpostDate = (EditText) findViewById(R.id.postDate);
        mpostLocation = (EditText) findViewById(R.id.postLocation);
        mpostNumDates = (EditText) findViewById(R.id.postNumDates);
        mpostBasicPay = (EditText) findViewById(R.id.postBasicPay);
        mpostTotalBasicPay = (EditText) findViewById(R.id.postTotalBasicPay);
        mpostTipsPay = (EditText) findViewById(postTipsPay);
        mpostTotalAllPay = (EditText) findViewById(R.id.postTotalAllPay);
        mpostPaymentDate = (EditText) findViewById(R.id.postPaymentDate);
        mpostAddNote = (EditText) findViewById(R.id.postAddNote);
        mpostHours = (EditText) findViewById(R.id.postHours);

        mcleardateBtn = (ImageButton) findViewById(R.id.cleardateBtn);

        mspinnerrate = (Spinner) findViewById(R.id.spinnerrate);
        mspinnercurrency = (Spinner) findViewById(R.id.spinnercurrency);

        String[] items = new String[]{"per hour", "per day", "per month"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(HireForm.this, android.R.layout.simple_spinner_dropdown_item, items);
        mspinnerrate.setAdapter(adapter);

        String[] items2 = new String[]{"MYR", "SGD", "CHY", "USD", "GBP", "EUR","NTD","HKD", "INR", "IDR"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(HireForm.this, android.R.layout.simple_spinner_dropdown_item, items2);
        mspinnercurrency.setAdapter(adapter2);

        loadData();

        mspinnerrate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here

                if(position == 0){

                    mnumoftxt.setText("Num of Days");
                    mhoursLay.setAlpha(1f);
                    if (updateval.equals("false")) {
                        mpostHours.setEnabled(true);
                    }

                    //If previous spinner rate was PER DAY
                    if (spinneritem == 1) {

                        if (!TextUtils.isEmpty(mpostBasicPay.getText().toString()) && !TextUtils.isEmpty(mpostNumDates.getText().toString()) && !TextUtils.isEmpty(mpostHours.getText().toString())) {

                            String numhoursString = mpostHours.getText().toString();
                            String basicPayString = mpostBasicPay.getText().toString();
                            String numDatesString = mpostNumDates.getText().toString();

                            Double totalbasicpayint = Double.parseDouble(basicPayString) * Double.parseDouble(numDatesString) * Double.parseDouble(numhoursString) ;
                            String string_totalbasicpayint = String.format("%.2f", totalbasicpayint);
                            mpostTotalBasicPay.setText(String.valueOf(string_totalbasicpayint));

                            String tipsPay = "0";

                            if (!TextUtils.isEmpty(mpostTipsPay.getText().toString())) {
                                tipsPay = mpostTipsPay.getText().toString();
                            }

                            Double totalallpayint = totalbasicpayint + Integer.parseInt(tipsPay);
                            String string_totalallpayint = String.format("%.2f", totalallpayint);
                            mpostTotalAllPay.setText(String.valueOf(string_totalallpayint));
                        }
                        else {
                            mpostTotalBasicPay.setText("");
                            mpostTotalAllPay.setText("");
                        }
                    }
                    //If previous spinner rate was PER MONTH
                    else if (spinneritem == 2) {
                        mpostNumDates.setText("");
                        mpostHours.setText("");
                    }

                    spinneritem = 0;
                }

                else if(position == 1){

                    mnumoftxt.setText("Num of Days");
                    mhoursLay.setAlpha(0.5f);
                    if (updateval.equals("false")) {
                        mpostHours.setEnabled(false);
                    }

                    if (spinneritem == 0) {

                        if (!TextUtils.isEmpty(mpostBasicPay.getText().toString()) && !TextUtils.isEmpty(mpostNumDates.getText().toString())) {

                            String basicPayString = mpostBasicPay.getText().toString();
                            String numDatesString = mpostNumDates.getText().toString();

                            Double totalbasicpayint = Double.parseDouble(basicPayString) * Double.parseDouble(numDatesString);
                            String string_totalbasicpayint = String.format("%.2f", totalbasicpayint);
                            mpostTotalBasicPay.setText(String.valueOf(string_totalbasicpayint));

                            String tipsPay = "0";

                            if (!TextUtils.isEmpty(mpostTipsPay.getText().toString())) {
                                tipsPay = mpostTipsPay.getText().toString();
                            }

                            Double totalallpayint = totalbasicpayint + Integer.parseInt(tipsPay);
                            String string_totalallpayint = String.format("%.2f", totalallpayint);
                            mpostTotalAllPay.setText(String.valueOf(string_totalallpayint));
                        }

                    }
                    else if (spinneritem == 2) {
                        mpostNumDates.setText("");
                    }

                    spinneritem = 1;
                }
                else if(position == 2){

                    mnumoftxt.setText("Num of Months");
                    mhoursLay.setAlpha(0.5f);

                    if (updateval.equals("false")) {
                        mpostHours.setEnabled(false);
                    }

                    if (spinneritem != 2) {
                        mpostNumDates.setText("");
                    }
                    spinneritem = 2;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        mUserInfo.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild("UserImage")) {
                    current_userimage = dataSnapshot.child("UserImage").getValue().toString();
                }

                else{
                    mUserAccount.child(mAuth.getCurrentUser().getUid()).child("image").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            current_userimage = dataSnapshot.getValue().toString();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                if(dataSnapshot.hasChild("Name")) {
                    current_username = dataSnapshot.child("Name").getValue().toString();
                }

                else{
                    mUserAccount.child(mAuth.getCurrentUser().getUid()).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            current_username = dataSnapshot.getValue().toString();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mUserInfo.child(user_uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild("UserImage")) {
                    user_image = dataSnapshot.child("UserImage").getValue().toString();
                }

                else{
                    mUserAccount.child(user_uid).child("image").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            user_image = dataSnapshot.getValue().toString();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                if(dataSnapshot.hasChild("Name")) {
                    user_name = dataSnapshot.child("Name").getValue().toString();
                }

                else{
                    mUserAccount.child(user_uid).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            user_name = dataSnapshot.getValue().toString();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mpostHours.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

                if(s.length() == 0) {
                    mpostTotalBasicPay.setText("");
                    mpostTotalAllPay.setText("");
                }
                else {

                    if (!TextUtils.isEmpty(mpostBasicPay.getText().toString()) && !TextUtils.isEmpty(mpostNumDates.getText().toString())) {

                        String numhoursString = s.toString();
                        String basicPayString = mpostBasicPay.getText().toString();
                        String numDatesString = mpostNumDates.getText().toString();

                        Double totalbasicpayint = Double.parseDouble(basicPayString) * Double.parseDouble(numDatesString) * Double.parseDouble(numhoursString) ;
                        String string_totalbasicpayint = String.format("%.2f", totalbasicpayint);
                        mpostTotalBasicPay.setText(String.valueOf(string_totalbasicpayint));

                        String tipsPay = "0";

                        if (!TextUtils.isEmpty(mpostTipsPay.getText().toString())) {
                            tipsPay = mpostTipsPay.getText().toString();
                        }

                        Double totalallpayint = totalbasicpayint + Integer.parseInt(tipsPay);
                        String string_totalallpayint = String.format("%.2f", totalallpayint);
                        mpostTotalAllPay.setText(String.valueOf(string_totalallpayint));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mpostNumDates.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() == 0) {
                    mpostTotalBasicPay.setText("");
                    mpostTotalAllPay.setText("");
                }
                else {
                    if (mspinnerrate.getSelectedItemPosition() == 0) {
                        if ( !TextUtils.isEmpty(mpostHours.getText().toString()) && !TextUtils.isEmpty(mpostBasicPay.getText().toString())) {
                            //If hours is NOT empty
                            String numhoursString = mpostHours.getText().toString();
                            String basicPayString = mpostBasicPay.getText().toString();
                            String numDatesString = s.toString();

                            Double totalbasicpayint = Double.parseDouble(basicPayString) * Double.parseDouble(numDatesString) * Double.parseDouble(numhoursString);
                            String string_totalbasicpayint = String.format("%.2f", totalbasicpayint);
                            mpostTotalBasicPay.setText(String.valueOf(string_totalbasicpayint));

                            String tipsPay = "0";

                            if (!TextUtils.isEmpty(mpostTipsPay.getText().toString()))  {
                                tipsPay = mpostTipsPay.getText().toString();
                            }

                            Double totalallpayint = totalbasicpayint + Integer.parseInt(tipsPay);
                            String string_totalallpayint = String.format("%.2f", totalallpayint);
                            mpostTotalAllPay.setText(String.valueOf(string_totalallpayint));
                        }
                    }
                    else {
                        if (!TextUtils.isEmpty(mpostBasicPay.getText().toString())) {

                            String basicPayString = mpostBasicPay.getText().toString();
                            String numDatesString = s.toString();

                            Double totalbasicpayint = Double.parseDouble(basicPayString) * Double.parseDouble(numDatesString);
                            String string_totalbasicpayint = String.format("%.2f", totalbasicpayint);
                            mpostTotalBasicPay.setText(String.valueOf(string_totalbasicpayint));

                            String tipsPay = "0";

                            if (!TextUtils.isEmpty(mpostTipsPay.getText().toString())) {
                                tipsPay = mpostTipsPay.getText().toString();
                            }

                            Double totalallpayint = totalbasicpayint + Integer.parseInt(tipsPay);
                            String string_totalallpayint = String.format("%.2f", totalallpayint);
                            mpostTotalAllPay.setText(String.valueOf(string_totalallpayint));
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mpostBasicPay.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0) {
                    String basicPayString = s.toString();
                    Double basicPayInt = Double.parseDouble(basicPayString);
                   // int basicPayInt = Integer.parseInt(basicPayString);

                    if (mspinnerrate.getSelectedItemPosition() == 0) {
                        if ( !TextUtils.isEmpty(mpostHours.getText().toString()) && !TextUtils.isEmpty(mpostNumDates.getText().toString())) {

                            String numDatesString = mpostNumDates.getText().toString();
                            String numhoursString = mpostHours.getText().toString();

                            Double totalbasicpayint = basicPayInt * Double.parseDouble(numDatesString) * Double.parseDouble(numhoursString);
                            String string_totalbasicpayint = String.format("%.2f", totalbasicpayint);
                            mpostTotalBasicPay.setText(String.valueOf(string_totalbasicpayint));

                            String tipsPay = "0";

                            if (!TextUtils.isEmpty(mpostTipsPay.getText().toString()))  {
                                tipsPay = mpostTipsPay.getText().toString();
                            }

                            Double totalallpayint = totalbasicpayint + Integer.parseInt(tipsPay);
                            String string_totalallpayint = String.format("%.2f", totalallpayint);
                            mpostTotalAllPay.setText(String.valueOf(string_totalallpayint));
                        }
                    }
                    else {
                        if (!TextUtils.isEmpty(mpostNumDates.getText().toString())) {

                            String numDatesString = mpostNumDates.getText().toString();

                            Double totalbasicpayint = basicPayInt * Double.parseDouble(numDatesString);
                            String string_totalbasicpayint = String.format("%.2f", totalbasicpayint);
                            mpostTotalBasicPay.setText(String.valueOf(string_totalbasicpayint));

                            String tipsPay = "0";

                            if (!TextUtils.isEmpty(mpostTipsPay.getText().toString()))  {
                                tipsPay = mpostTipsPay.getText().toString();
                            }

                            Double totalallpayint = totalbasicpayint + Integer.parseInt(tipsPay);
                            String string_totalallpayint = String.format("%.2f", totalallpayint);
                            mpostTotalAllPay.setText(String.valueOf(string_totalallpayint));
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mpostTipsPay.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

                int tipsPayInt = 0;

                if(s.length() != 0) {
                    String tipsPayString = s.toString();
                    tipsPayInt = Integer.parseInt(tipsPayString);
                }

                if (!TextUtils.isEmpty(mpostNumDates.getText().toString()) && !TextUtils.isEmpty(mpostTotalBasicPay.getText().toString())) {

                    Double totalallpayint = tipsPayInt +  Double.parseDouble(mpostTotalBasicPay.getText().toString());
                    String string_totalallpayint = String.format("%.2f", totalallpayint);
                    mpostTotalAllPay.setText(String.valueOf(string_totalallpayint));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mpostDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showdatepickerdialog();
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
                    intent = builder.build(HireForm.this);
                    startActivityForResult(intent, PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        mpostPaymentDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showpaymentdatedialog();
            }
        });

        mcleardateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mpostDate.setText("");
                mpostDate.setHint("Tap to select dates of hiring");
                mpostNumDates.setText("");
                numDays = 0;
                if (!chosendates.isEmpty()) {
                    chosendates.clear();
                }
            }
        });

        mjobcardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent jobdetailintent = new Intent(HireForm.this, JobDetail.class);
                jobdetailintent.putExtra("post_id", post_key);
                jobdetailintent.putExtra("city_id", city);
                startActivity(jobdetailintent);
            }
        });

        mupdateCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean boolupdate = true;
                hireapplicants(boolupdate);
            }
        });

        mhireCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(HireForm.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setContentView(R.layout.applicantsdialog);

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                lp.gravity = Gravity.CENTER;

                dialog.getWindow().setAttributes(lp);

                Button cancelbtn = (Button) dialog.findViewById(R.id.cancelBtn);
                TextView mdialogtxt = (TextView) dialog.findViewById(R.id.dialogtxt);
                Button mhirebtn = (Button) dialog.findViewById(R.id.hireBtn);

                mhirebtn.setText("HIRE");
                cancelbtn.setText("CANCEL");
                mhirebtn.setTextColor(Color.parseColor("#ff669900"));
                mdialogtxt.setText("Are you sure you want to hire " + user_name);

                dialog.show();

                mhirebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Boolean boolupdate = false;
                        hireapplicants(boolupdate);
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
        });

        mpostBasicPay.addTextChangedListener(new MoneyTextWatcher());

        if (updateval.equals("true")) {
            mpostNumDates.setEnabled(false);
            mpostNumDates.setAlpha(0.5f);
            mnumoftxt.setAlpha(0.5f);

            mpostBasicPay.setEnabled(false);
            mpostBasicPay.setAlpha(0.5f);
            mbasicpaytxt.setAlpha(0.5f);

            mpostHours.setEnabled(false);

            mspinnerrate.setEnabled(false);
            mspinnerrate.setAlpha(0.5f);

            mspinnercurrency.setEnabled(false);
            mspinnercurrency.setAlpha(0.5f);

            mpostPaymentDate.setEnabled(false);
            mpostPaymentDate.setAlpha(0.5f);
        }

    }

    private void updateData() {

        mUserPostedHiredApplicants.child(mAuth.getCurrentUser().getUid()).child(post_key).child(user_uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("basicpay")) {
                        mpostBasicPay.setText(dataSnapshot.child("basicpay").getValue().toString());
                    }
                    if (dataSnapshot.hasChild("numhours")) {
                        mpostHours.setText(dataSnapshot.child("numhours").getValue().toString());
                    }
                    if (dataSnapshot.hasChild("location")) {
                        mpostLocation.setText(dataSnapshot.child("location").getValue().toString());
                    }
                    if (dataSnapshot.hasChild("paymentdate")) {
                        mpostPaymentDate.setText(dataSnapshot.child("paymentdate").getValue().toString());
                    }
                    if (dataSnapshot.hasChild("tipspay")) {
                        mpostTipsPay.setText(dataSnapshot.child("tipspay").getValue().toString());
                    }
                    if (dataSnapshot.hasChild("additionalnote")) {
                        mpostAddNote.setText(dataSnapshot.child("additionalnote").getValue().toString());
                    }
                    if (dataSnapshot.hasChild("date")) {
                        post_date = dataSnapshot.child("date").getValue().toString();
                        mpostDate.setText(dataSnapshot.child("date").getValue().toString());
                    }
                    if (dataSnapshot.hasChild("numdates")) {
                        mpostNumDates.setText(dataSnapshot.child("numdates").getValue().toString());
                    }
                    if (dataSnapshot.hasChild("basictotalpay")) {
                        mpostTotalBasicPay.setText(dataSnapshot.child("basictotalpay").getValue().toString());
                    }
                    if (dataSnapshot.hasChild("totalallpay")) {
                        mpostTotalAllPay.setText(dataSnapshot.child("totalallpay").getValue().toString());
                    }
                    if (dataSnapshot.hasChild("spinnerrate") && dataSnapshot.hasChild("spinnercurrency")) {

                        String spinnerrate_val = dataSnapshot.child("spinnerrate").getValue().toString();
                        String spinnercurrency_val = dataSnapshot.child("spinnercurrency").getValue().toString();

                        display_Rate(spinnerrate_val);
                        display_Currency(spinnercurrency_val);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadData() {

        mJob.child(city).child(post_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                post_title = (String) dataSnapshot.child("title").getValue();
                mpostTitle.setText(post_title);

                post_desc = (String) dataSnapshot.child("desc").getValue();
                mpostDescrip.setText(post_desc);

                post_company = (String) dataSnapshot.child("company").getValue();
                mpostCompany.setText(post_company);

                post_image = (String) dataSnapshot.child("postimage").getValue();

                if (post_image!=null) {
                    {
                        if(!HireForm.this.isFinishing()) {
                            Glide.with(getApplicationContext()).load(post_image)
                                    .centerCrop()
                                    .error(R.drawable.profilebg3)
                                    .placeholder(R.drawable.profilebg3)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .dontAnimate()
                                    .into(mpostImage);
                        }
                    }
                }

                if (updateval.equals("true")) {
                    updateData();
                }
                else {
                    post_date = (String) dataSnapshot.child("date").getValue();

                    post_wages = (String) dataSnapshot.child("wages").getValue();

                    if (!post_wages.equals("none")) {
                        displayRate_Currency(post_wages);
                    }

                    if (!post_date.equals("none")) {
                        mpostDate.setText(post_date);
                        countDates(post_date);
                    }

                    post_location = (String) dataSnapshot.child("fulladdress").getValue();
                    mpostLocation.setText(post_location);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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

    private void display_Rate(String spinnerrate_val) {

        int ratespinner;

        if(spinnerrate_val.equals("per hour")){ratespinner = 0;}
        else if(spinnerrate_val.equals("per day")){ratespinner = 1;}
        else{ratespinner = 2;}

        mspinnerrate.setSelection(ratespinner);
        spinneritem = ratespinner;
    }

    private void displayRate_Currency(String post_wages) {

        String fullwages, stringcurrency, rateper;
        int ratespinner;

        String[] separated1 = post_wages.split(" per ");
        String[] separated2 = separated1[0].split(" ");
        fullwages = separated2[1];
        stringcurrency = separated2[0];
        rateper = separated1[1];

        if(rateper.equals("hour")){ratespinner = 0;}
        else if(rateper.equals("day")){ratespinner = 1;}
        else{ratespinner = 2;}

        display_Currency(stringcurrency);

        mpostBasicPay.setText(fullwages);

        mspinnerrate.setSelection(ratespinner);

        if (ratespinner == 0) {
            mpostTotalBasicPay.setText("");
            mpostTotalAllPay.setText("");
        }

        spinneritem = ratespinner;
    }
    
    private void display_Currency (String stringcurrency) {

        int currency = 0;

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

        mspinnercurrency.setSelection(currency);
    }


    private void countDates(String post_date) {
        if (!post_date.contains("to")) {
            String[] separated = post_date.split(" / ");

            numDays = separated.length;
            mpostNumDates.setText(String.valueOf(numDays));

        }
        else {
            String[] separated = post_date.split(" to ");

            Date startdate;
            Date enddate;

            SimpleDateFormat dates = new SimpleDateFormat("dd MMM yy");

            try {
                //Dates to compare
                //Setting dates
                startdate = dates.parse(separated[0]);
                enddate = dates.parse(separated[1]);

                //Comparing dates
                long difference = Math.abs(startdate.getTime() - enddate.getTime());
                long differenceDates = difference / (24 * 60 * 60 * 1000);

                //Convert long to String
                if (mspinnerrate.getSelectedItemPosition() != 2) {
                    numDays = (int)(differenceDates +1);
                    mpostNumDates.setText(String.valueOf(numDays));
                }

            } catch (Exception exception) {
                Log.e("DIDN'T WORK", "exception " + exception);
            }

        }
    }

    private void showCUSTOMpaymentdatedialog(final Dialog mdialog) {
        final Dialog dialog = new Dialog(HireForm.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custompaymentdate_dialog);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(lp);

        final EditText mcustomPaymentDate = (EditText) dialog.findViewById(R.id.customPaymentDate);
        final Button mokBtn = (Button) dialog.findViewById(R.id.okBtn);
        final Button mcancelBtn = (Button) dialog.findViewById(R.id.cancelBtn);

        dialog.show();

        mokBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String custompayment = mcustomPaymentDate.getText().toString().trim();;

                if (!TextUtils.isEmpty(custompayment)) {
                    mpostPaymentDate.setText(custompayment + " After Event Finishes");
                    dialog.dismiss();
                    mdialog.dismiss();
                }
                else {
                    Toast.makeText(HireForm.this, "Blank field", Toast.LENGTH_SHORT).show();
                }

            }
        });

        mcancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                mdialog.dismiss();
            }
        });
    }

    private void showpaymentdatedialog() {
        final Dialog dialog = new Dialog(HireForm.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.paymentdate_dialog);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(lp);

        final Button mpaymentBtn1 = (Button) dialog.findViewById(R.id.paymentBtn1);
        final Button mpaymentBtn2 = (Button) dialog.findViewById(R.id.paymentBtn2);
        final Button mpaymentBtn3 = (Button) dialog.findViewById(R.id.paymentBtn3);
        final Button mpaymentBtn4 = (Button) dialog.findViewById(R.id.paymentBtn4);
        final Button mpaymentBtn5 = (Button) dialog.findViewById(R.id.paymentBtn5);
        final Button mpaymentBtn6 = (Button) dialog.findViewById(R.id.paymentBtn6);
        final Button mpaymentBtn7 = (Button) dialog.findViewById(R.id.paymentBtn7);
        final Button mpaymentBtn8 = (Button) dialog.findViewById(R.id.paymentBtn8);

        dialog.show();

        mpaymentBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mpostPaymentDate.setText(mpaymentBtn1.getText());
                dialog.dismiss();
            }
        });

        mpaymentBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mpostPaymentDate.setText(mpaymentBtn2.getText());
                dialog.dismiss();
            }
        });

        mpaymentBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mpostPaymentDate.setText(mpaymentBtn3.getText());
                dialog.dismiss();
            }
        });

        mpaymentBtn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mpostPaymentDate.setText(mpaymentBtn4.getText());
                dialog.dismiss();
            }
        });

        mpaymentBtn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mpostPaymentDate.setText(mpaymentBtn5.getText());
                dialog.dismiss();
            }
        });

        mpaymentBtn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mpostPaymentDate.setText(mpaymentBtn6.getText());
                dialog.dismiss();
            }
        });

        mpaymentBtn7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mpostPaymentDate.setText(mpaymentBtn7.getText());
                dialog.dismiss();
            }
        });

        mpaymentBtn8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCUSTOMpaymentdatedialog(dialog);
            }
        });

    }

    private void showdatepickerdialog(){

        i = 1;

        final Dialog dialog = new Dialog(HireForm.this);
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

        if (!chosendates.isEmpty()) {

            //add one year to calendar from todays date
            calendar_view.init(today, nextYear.getTime())
                    .inMode(CalendarPickerView.SelectionMode.MULTIPLE)
                    .withSelectedDates(chosendates);

        }
        else {
            //add one year to calendar from todays date
            calendar_view.init(today, nextYear.getTime())
                    .inMode(CalendarPickerView.SelectionMode.MULTIPLE);
        }

        dialog.show();

        mmultiCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add one year to calendar from todays date
                chosendates.clear();

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
                chosendates.clear();

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

                    chosendates.clear();

                    for (int x = 0; x< calendar_view.getSelectedDates().size();x++){

                        //here you can fetch all dates

                        Date calenderdate = calendar_view.getSelectedDates().get(x);

                        chosendates.add(calenderdate);

                        Log.d(TAG, "multiple calenderdate "+calenderdate);

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
                               // startingdate = sdf2.format(calenderdate);
                               // Log.d(TAG, "yystartingdatexx "+startingdate);
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
                               // startingdate = sdf2.format(calenderdate);
                              //  Log.d(TAG, "startingdatexx "+startingdate);
                            }
                        }
                    }

                    String finaldates = TextUtils.join(" / ", dates);
                    mpostDate.setText(finaldates);
                    countDates(finaldates);
                }
                else if (i == 2){
                    //RANGE DATES

                    String startdate = "";
                    String enddate = "";

                    chosendates.clear();

                    for (int x = 0; x< calendar_view.getSelectedDates().size();x++){

                        //here you can fetch all dates

                        Date calenderdate = calendar_view.getSelectedDates().get(x);

                        chosendates.add(calenderdate);

                        if(x == calendar_view.getSelectedDates().size()-1){
                            //reach end of dates
                            String myFormat = " dd MMM yy"; //In which you need put here
                            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                            enddate = sdf.format(calenderdate);
                            Log.d(TAG, "range endnewdate "+enddate);

                            if(calendar_view.getSelectedDates().size() ==  1){
                                String myFormat2 = "yyMMdd"; //In which you need put here
                                SimpleDateFormat sdf2 = new SimpleDateFormat(myFormat2, Locale.US);
                               // startingdate = sdf2.format(calenderdate);
                               // Log.d(TAG, "yystartingdatexx "+startingdate);
                            }
                        }
                        else if (x == 0){
                            String myFormat = " dd MMM yy"; //In which you need put here
                            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                            startdate = sdf.format(calenderdate);
                            Log.d(TAG, "range startdate "+startdate);

                            String myFormat2 = "yyMMdd"; //In which you need put here
                            SimpleDateFormat sdf2 = new SimpleDateFormat(myFormat2, Locale.US);
                         //   startingdate = sdf2.format(calenderdate);
                           // Log.d(TAG, "startingdatexx "+startingdate);
                        }
                    }
                    mpostDate.setText(startdate + " to" + enddate);
                    countDates(startdate + " to" + enddate);
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

    private void hireapplicants(final Boolean boolupdate) {

        if (boolupdate) {
            mProgress.setMessage("Updating..");
        }
        else {
            mProgress.setMessage("Hiring..");
        }

        mProgress.setCancelable(false);
        mProgress.show();

        final String location_val = mpostLocation.getText().toString().trim();
        final String additionalnote_val = mpostAddNote.getText().toString().trim();
        final String paymentdate_val = mpostPaymentDate.getText().toString().trim();

        final String totalallpay_val = mpostTotalAllPay.getText().toString().trim();
        final String tipspay_val = mpostTipsPay.getText().toString().trim();
        final String basictotalpay_val = mpostTotalBasicPay.getText().toString().trim();
        final String basicpay_val = mpostBasicPay.getText().toString().trim();
        final String numdates_val = mpostNumDates.getText().toString().trim();
        final String numhours_val = mpostHours.getText().toString().trim();

        final String date_val = mpostDate.getText().toString().trim();
        final String spinnerratetext = mspinnerrate.getSelectedItem().toString();
        final String spinnercurrencytext = mspinnercurrency.getSelectedItem().toString();

        if (!TextUtils.isEmpty(location_val) && !TextUtils.isEmpty(paymentdate_val) && !TextUtils.isEmpty(totalallpay_val) && !TextUtils.isEmpty(basictotalpay_val)
                && !TextUtils.isEmpty(basicpay_val) && !TextUtils.isEmpty(numdates_val) && !TextUtils.isEmpty(date_val)
                && !TextUtils.isEmpty(spinnerratetext) && !TextUtils.isEmpty(spinnercurrencytext) ) {


            String[] separated;
            String lastdate;
            Date enddate;

            if (!date_val.contains("to")) {

                separated = date_val.split(" / ");

                lastdate = separated[separated.length - 1];
            }
            else {
                separated = date_val.split(" to ");

                lastdate = separated[1];
            }

            SimpleDateFormat dates = new SimpleDateFormat("dd MMM yy");

            try {
                enddate = dates.parse(lastdate);

                final long tsLong = System.currentTimeMillis();
                Date datenow = new Date(tsLong);

                Calendar c = Calendar.getInstance();
                c.setTime(enddate);

                //If end date < time date NOW, show REVIEW
                if (c.getTime().compareTo(datenow) < 0) {

                    Toast.makeText(HireForm.this, "Hiring Dates must be from today onwards.", Toast.LENGTH_SHORT).show();
                    mpostDate.setError("Invalid Dates");
                    mProgress.dismiss();
                    return;

                }

            } catch (Exception exception) {
                Log.e("DIDN'T WORK", "exception " + exception);
                Toast.makeText(HireForm.this, "Unable to hire, please try again", Toast.LENGTH_SHORT).show();
                mProgress.dismiss();
                return;
            }

            mUserActivities.child(user_uid).child("Applied").child(post_key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //Check if user applied tab still has the job or not
                    if(dataSnapshot.exists()){
                        mUserActivities.child(user_uid).child("NewMainNotification").setValue("true");
                        mUserActivities.child(user_uid).child("NewApplied").setValue("true");
                        mUserActivities.child(user_uid).child("Applied").child(post_key).child("pressed").setValue("false");
                        mUserActivities.child(user_uid).child("Applied").child(post_key).child("date").setValue(date_val);

                        if (boolupdate) {
                            mUserActivities.child(user_uid).child("Applied").child(post_key).child("status").setValue("changedoffer");
                        }
                        else {
                            mUserActivities.child(user_uid).child("Applied").child(post_key).child("status").setValue("pendingoffer");
                            DatabaseReference newHireNotification = mUserHireNotification.child("Hire").push();
                            String hirenotificationKey = newHireNotification.getKey();

                            Map<String, Object> notificationData = new HashMap<>();
                            notificationData.put("ownerUid", mAuth.getCurrentUser().getUid());
                            notificationData.put("receiverUid", user_uid);
                            notificationData.put("ownerName", current_username);
                            newHireNotification.setValue(notificationData);

                            // mUserAllNotification.child(user_uid).child("ShortListedNotification").child(shortlistnotificationKey).setValue(shortlistnotificationKey);
                            mUserActivities.child(user_uid).child("HiredNotification").child(hirenotificationKey).setValue(hirenotificationKey);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            //Add user to UserPostedHiredApplicants List
            final Map<String, Object> hiredData = new HashMap<>();

            hiredData.put("image", user_image);
            hiredData.put("name", user_name);
            hiredData.put("location", location_val);
            hiredData.put("paymentdate", paymentdate_val);

            hiredData.put("totalallpay", totalallpay_val);
            hiredData.put("basictotalpay", basictotalpay_val);
            hiredData.put("basicpay", basicpay_val);
            hiredData.put("numdates", numdates_val);

            hiredData.put("date", date_val);
            hiredData.put("spinnerrate", spinnerratetext);
            hiredData.put("spinnercurrency", spinnercurrencytext);
            hiredData.put("offerstatus", "pending");

            if (spinnerratetext.equals("per hour")) {
                hiredData.put("numhours", numhours_val);
            }
            else {
                mUserPostedHiredApplicants.child(mAuth.getCurrentUser().getUid()).child(post_key).child(user_uid).child("numhours").removeValue();
            }
            if (!TextUtils.isEmpty(additionalnote_val)) {
                hiredData.put("additionalnote", additionalnote_val);
            }
            if (!TextUtils.isEmpty(tipspay_val)) {
                hiredData.put("tipspay", tipspay_val);
            }

            if (boolupdate) {
                mUserPostedHiredApplicants.child(mAuth.getCurrentUser().getUid()).child(post_key).child(user_uid).updateChildren(hiredData).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //Remove user from UserPostedShortlistedApplicants List
                        mProgress.dismiss();
                        hiredClick = true;
                        onBackPressed();
                    }
                });
            }
            else {
                Long tsLong = System.currentTimeMillis();
                hiredData.put("negatedtime", (-1*tsLong));
                hiredData.put("time", ServerValue.TIMESTAMP);

                incrementhiredapplicants();

                mUserPostedHiredApplicants.child(mAuth.getCurrentUser().getUid()).child(post_key).child(user_uid).setValue(hiredData).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //Remove user from UserPostedShortlistedApplicants List
                        mUserPostedShortlistedApplicants.child(mAuth.getCurrentUser().getUid()).child(post_key).child(user_uid).removeValue();
                        mProgress.dismiss();
                        hiredClick = true;
                        onBackPressed();
                    }
                });

                //Add to user's chat
                final DatabaseReference OwnerChat = mChatRoom.child(current_useruid);
                final DatabaseReference ReceiverChat = mChatRoom.child(user_uid);
                final DatabaseReference newReceiverChat = ReceiverChat.child(user_uid+"_"+current_useruid).child("ChatList").push();
                final String newChatListkey = newReceiverChat.getKey();
                final DatabaseReference newOwnerChat = OwnerChat.child(current_useruid+"_"+user_uid).child("ChatList").child(newChatListkey);

                final Map<String, Object> actionchatData = new HashMap<>();
                actionchatData.put("negatedtime", (-1*tsLong));
                actionchatData.put("time", ServerValue.TIMESTAMP);
                actionchatData.put("actiontitle", "hired");
                actionchatData.put("ownerid", mAuth.getCurrentUser().getUid());
                actionchatData.put("jobtitle", post_title);
                actionchatData.put("jobdescrip", post_desc);
                actionchatData.put("city", city);
                actionchatData.put("postkey", post_key);

                mUserChatList.child(mAuth.getCurrentUser().getUid()).child("UserList").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            if(dataSnapshot.hasChild(user_uid)){

                                actionchatData.put("oldtime", dataSnapshot.child(user_uid).child("time").getValue());

                                newOwnerChat.setValue(actionchatData);
                                newReceiverChat.setValue(actionchatData);
                            }
                            else{
                                actionchatData.put("oldtime", 0);
                                newOwnerChat.setValue(actionchatData);
                                newReceiverChat.setValue(actionchatData);

                            }
                        }
                        else{
                            newOwnerChat.setValue(actionchatData);
                            newReceiverChat.setValue(actionchatData);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
        else {

            Toast.makeText(HireForm.this, "Please fill in empty fields", Toast.LENGTH_LONG).show();

            if (TextUtils.isEmpty(location_val)) {
                mpostLocation.setError("Empty Location");
            }
            if (TextUtils.isEmpty(paymentdate_val)) {
                mpostPaymentDate.setError("Empty Payment Date");
            }
            if (TextUtils.isEmpty(date_val)) {
                mpostDate.setError("Empty Dates");
            }
            if (TextUtils.isEmpty(basictotalpay_val)) {
                if (spinnerratetext.contains("per hour") && TextUtils.isEmpty(numhours_val)) {
                    mpostHours.setError("Empty Hours");
                }

                if (TextUtils.isEmpty(basicpay_val)) {
                    mpostBasicPay.setError("Empty Basic Pay");
                }
                if (TextUtils.isEmpty(numdates_val)) {
                    mpostNumDates.setError("Empty Number of Days");
                }

            }

            mProgress.dismiss();

        }
    }

    public void incrementhiredapplicants() {

        mUserPosted.child(mAuth.getCurrentUser().getUid()).child(post_key).child("totalhiredcount").runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData currentData) {
                if (currentData.getValue() == null) {
                    currentData.setValue(1);
                } else {
                    currentData.setValue((Long) currentData.getValue() + 1);
                }
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

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

    @Override
    public void onBackPressed() {
        if (hiredClick) {
            setResult(Activity.RESULT_OK);
        }
        else {
            setResult(Activity.RESULT_CANCELED);
        }

        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PLACE_PICKER_REQUEST) {
            mProgress.dismiss();
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                city = "";
                String address = place.getAddress().toString();
                String addressName = place.getName().toString();
                latitude = place.getLatLng().latitude;
                longitude = place.getLatLng().longitude;

                Geocoder geocoder = new Geocoder(HireForm.this, Locale.getDefault());
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

                        if (address.contains("Pulau Pinang") || address.contains("Penang")) {
                            city = "Penang";
                        } else if (address.contains("Kuala Lumpur")) {
                            city = "Kuala Lumpur";
                        } else if (address.contains("Labuan")) {
                            city = "Labuan";
                        } else if (address.contains("Putrajaya")) {
                            city = "Putrajaya";
                        } else if (address.contains("Johor")) {
                            city = "Johor";
                        } else if (address.contains("Kedah")) {
                            city = "Kedah";
                        } else if (address.contains("Kelantan")) {
                            city = "Kelantan";
                        } else if (address.contains("Melaka") || address.contains("Melacca")) {
                            city = "Melacca";
                        } else if (address.contains("Negeri Sembilan") || address.contains("Seremban")) {
                            city = "Negeri Sembilan";
                        }
                        //
                        else if (address.contains("Pahang")) {
                            city = "Pahang";
                        } else if (address.contains("Perak") || address.contains("Ipoh")) {
                            city = "Perak";
                        } else if (address.contains("Perlis")) {
                            city = "Perlis";
                        } else if (address.contains("Sabah")) {
                            city = "Sabah";
                        } else if (address.contains("Sarawak")) {
                            city = "Sarawak";
                        } else if (address.contains("Selangor") || address.contains("Shah Alam") || address.contains("Klang")) {
                            city = "Selangor";
                        } else if (address.contains("Terengganu")) {
                            city = "Terengganu";
                        }

                        Log.d(TAG, "addressName and address: " + addressName + ", " + address);

                        mpostLocation.setText(addressName + ", " + address);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
