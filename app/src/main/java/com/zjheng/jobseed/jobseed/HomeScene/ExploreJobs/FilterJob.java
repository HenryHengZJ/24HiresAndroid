package com.zjheng.jobseed.jobseed.HomeScene.ExploreJobs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zjheng.jobseed.jobseed.LocationList;
import com.zjheng.jobseed.jobseed.MainActivity;
import com.zjheng.jobseed.jobseed.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class FilterJob extends AppCompatActivity {



    private String oldfilterbywages = "", filterbystart = "", startingdatestring, filterbyend = "", endingdatestring, city;
    private Long filterbywages;
    private long wagescategory = 0;

    private Boolean mapplyPressed = false;

    private CardView mallrangeCardView, mspecificrangeCardView, mfilterbystartdate_cardview, mfilterbyenddate_cardview, mapplyBtn, mlocationCardView;
    private ImageView mallrangetick, mwagesrangetick, mstartingdate_tickimg, mendingdate_tickimg;
    private RelativeLayout mblockLay;
    private TextView mstartdatetxt, menddatetxt, mpostBaseLocation;
    private EditText mratetxt;
    private Spinner mspinnerrate, mspinnercurrency;
    private SeekBar mpriceBar;
    private Button mclearBtn;

    private Calendar myCalendar;
    private DatePickerDialog startdatePickerDialog;
    private DatePickerDialog enddatePickerDialog;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserSortFilter, newSortFilter, mUserLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_job);

        mAuth = FirebaseAuth.getInstance();

        mUserLocation =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserLocation");

        mUserSortFilter =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("SortFilter");
        mUserSortFilter.child(mAuth.getCurrentUser().getUid()).keepSynced(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            oldfilterbywages = extras.getString("oldfilterbywages");
            filterbywages = extras.getLong("filterbywages");
            filterbystart = extras.getString("filterbystart");
            filterbyend = extras.getString("filterbyend");
            city = extras.getString("city");
        }

        newSortFilter = mUserSortFilter.child(mAuth.getCurrentUser().getUid());

        Toolbar mToolbar = (Toolbar)findViewById(R.id.toolbar_other);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Refine");
        mToolbar.setTitleTextColor(android.graphics.Color.WHITE);

        mToolbar.setNavigationIcon(R.mipmap.ic_close_white_24dp);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mallrangeCardView = (CardView) findViewById(R.id.allrangeCardView);
        mspecificrangeCardView = (CardView) findViewById(R.id.specificrangeCardView);
        mlocationCardView = findViewById(R.id.locationCardView);
        mallrangetick = (ImageView) findViewById(R.id.allrangetick);
        mwagesrangetick = (ImageView) findViewById(R.id.wagesrangetick);
        mblockLay = (RelativeLayout) findViewById(R.id.blockLay);

        mpostBaseLocation = findViewById(R.id.postBaseLocation);
        mpostBaseLocation.setText(city);

        mfilterbystartdate_cardview = (CardView) findViewById(R.id.sortbystartdate_cardview);
        mstartingdate_tickimg = (ImageView) findViewById(R.id.startingdate_tickimg);
        mstartdatetxt = (TextView) findViewById(R.id.startdatetxt);

        mfilterbyenddate_cardview = (CardView) findViewById(R.id.sortbyenddate_cardview);
        mendingdate_tickimg = (ImageView) findViewById(R.id.endingdate_tickimg);
        menddatetxt = (TextView) findViewById(R.id.enddatetxt);

        mratetxt = (EditText) findViewById(R.id.ratetxt);
        mspinnerrate = (Spinner) findViewById(R.id.spinnerrate);
        mspinnercurrency = (Spinner) findViewById(R.id.spinnercurrency);
        mpriceBar = (SeekBar)findViewById(R.id.priceBar);
        mpriceBar.setMax(4);

        mclearBtn = (Button) findViewById(R.id.clearBtn);
        mapplyBtn = (CardView) findViewById(R.id.applyBtn);

        String[] items = new String[]{"per hour", "per day", "per month"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(FilterJob.this, android.R.layout.simple_spinner_dropdown_item, items);
        mspinnerrate.setAdapter(adapter);

        String[] items2 = new String[]{"MYR", "SGD", "CHY", "USD", "GBP", "EUR", "NTD","HKD","INR","IDR"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(FilterJob.this, android.R.layout.simple_spinner_dropdown_item, items2);
        mspinnercurrency.setAdapter(adapter2);

        if (oldfilterbywages != null) {
            if(oldfilterbywages.equals("true")){
                mblockLay.setVisibility(VISIBLE);
                mallrangetick.setImageResource(R.drawable.single_tick);
                mwagesrangetick.setImageResource(R.color.buttonTextColor);
            }
            else{
                mwagesrangetick.setImageResource(R.drawable.single_tick);
                mallrangetick.setImageResource(R.color.buttonTextColor);
                mblockLay.setVisibility(GONE);
            }
        }
        else {
            mwagesrangetick.setImageResource(R.drawable.single_tick);
            mallrangetick.setImageResource(R.color.buttonTextColor);
            mblockLay.setVisibility(GONE);
        }

        if(filterbywages!=0){

            long newwagescategory = filterbywages % 100;

            if(newwagescategory == 11){ mspinnerrate.setSelection(0); mratetxt.setText("Less than 5"); mpriceBar.setProgress(0); wagescategory = 11;}
            else if(newwagescategory == 12){ mspinnerrate.setSelection(0); mratetxt.setText("5 to 10"); mpriceBar.setProgress(1); wagescategory = 12;}
            else if(newwagescategory == 13){ mspinnerrate.setSelection(0); mratetxt.setText("11 to 20"); mpriceBar.setProgress(2); wagescategory = 13; }
            else if(newwagescategory == 14){ mspinnerrate.setSelection(0); mratetxt.setText("21 to 50"); mpriceBar.setProgress(3); wagescategory = 14;}
            else if(newwagescategory == 15){ mspinnerrate.setSelection(0); mratetxt.setText("More than 50"); mpriceBar.setProgress(4); wagescategory = 15;}
            else if(newwagescategory == 21){ mspinnerrate.setSelection(1); mratetxt.setText("Less than 70"); mpriceBar.setProgress(0); wagescategory = 21;}
            else if(newwagescategory == 22){ mspinnerrate.setSelection(1); mratetxt.setText("70 to 100"); mpriceBar.setProgress(1); wagescategory = 22;}
            else if(newwagescategory == 23){ mspinnerrate.setSelection(1); mratetxt.setText("101 to 200"); mpriceBar.setProgress(2); wagescategory = 23;}
            else if(newwagescategory == 24){ mspinnerrate.setSelection(1); mratetxt.setText("201 to 500"); mpriceBar.setProgress(3); wagescategory = 24;}
            else if(newwagescategory == 25){ mspinnerrate.setSelection(1); mratetxt.setText("More than 500"); mpriceBar.setProgress(4); wagescategory = 25;}
            else if(newwagescategory == 31){ mspinnerrate.setSelection(2); mratetxt.setText("Less than 1000"); mpriceBar.setProgress(0); wagescategory = 31;}
            else if(newwagescategory == 32){ mspinnerrate.setSelection(2); mratetxt.setText("1000 to 1500"); mpriceBar.setProgress(1); wagescategory = 32;}
            else if(newwagescategory == 33){ mspinnerrate.setSelection(2); mratetxt.setText("1500 to 2000"); mpriceBar.setProgress(2); wagescategory = 33;}
            else if(newwagescategory == 34){ mspinnerrate.setSelection(2); mratetxt.setText("2000 to 5000"); mpriceBar.setProgress(3); wagescategory = 34;}
            else if(newwagescategory == 35){ mspinnerrate.setSelection(2); mratetxt.setText("More than 5000"); mpriceBar.setProgress(4); wagescategory = 35;}

            //get currency number
            int intwages = (int) (filterbywages % 100000);
            int currencyint = Integer.parseInt(Integer.toString(intwages).substring(0, 2)) - 11;
            mspinnercurrency.setSelection(currencyint);
        }

        if(!filterbystart.equals("")){
            startingdatestring = filterbystart;
            SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd", Locale.US);
            Date dateFromString = null;
            try {
                dateFromString = sdf.parse(filterbystart);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            SimpleDateFormat sdf1 = new SimpleDateFormat("dd MMMM yy", Locale.US);
            String dateAsString = sdf1.format(dateFromString);
            mstartdatetxt.setText(dateAsString);
            mstartingdate_tickimg.setVisibility(VISIBLE);
            mstartdatetxt.setTextColor(Color.parseColor("#008fee"));
        }
        else{
            mstartdatetxt.setText("");
            mstartdatetxt.setHint("Start Date");
            mstartingdate_tickimg.setVisibility(GONE);
            mstartdatetxt.setTextColor(Color.BLACK);
        }

        if(!filterbyend.equals("")){
            endingdatestring = filterbyend;
            SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd", Locale.US);
            Date dateFromString = null;
            try {
                dateFromString = sdf.parse(filterbyend);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            SimpleDateFormat sdf1 = new SimpleDateFormat("dd MMMM yy", Locale.US);
            String dateAsString = sdf1.format(dateFromString);
            menddatetxt.setText(dateAsString);
            mendingdate_tickimg.setVisibility(VISIBLE);
            menddatetxt.setTextColor(Color.parseColor("#008fee"));
        }
        else{
            menddatetxt.setText("");
            menddatetxt.setHint("End Date");
            mendingdate_tickimg.setVisibility(GONE);
            menddatetxt.setTextColor(Color.BLACK);
        }

        mallrangeCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mallrangetick.setImageResource(R.drawable.single_tick);
                mwagesrangetick.setImageResource(R.color.buttonTextColor);
                mblockLay.setVisibility(VISIBLE);

                oldfilterbywages = "true";
            }
        });

        mspecificrangeCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mwagesrangetick.setImageResource(R.drawable.single_tick);
                mallrangetick.setImageResource(R.color.buttonTextColor);
                mblockLay.setVisibility(GONE);

                oldfilterbywages = "false";
            }
        });

        mpriceBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (mspinnerrate.getSelectedItemPosition() == 0){
                    if(progress==0){mratetxt.setText("Less than 5"); wagescategory = 11;}
                    else if(progress==1){mratetxt.setText("5 to 10");wagescategory = 12;}
                    else if(progress==2){mratetxt.setText("11 to 20");wagescategory = 13;}
                    else if(progress==3){mratetxt.setText("21 to 50");wagescategory = 14;}
                    else if(progress==4){mratetxt.setText("More than 50");wagescategory = 15;}
                }
                else if (mspinnerrate.getSelectedItemPosition() == 1){
                    if(progress==0){mratetxt.setText("Less than 70");wagescategory = 21;}
                    else if(progress==1){mratetxt.setText("70 to 100");wagescategory = 22;}
                    else if(progress==2){mratetxt.setText("101 to 200");wagescategory = 23;}
                    else if(progress==3){mratetxt.setText("201 to 500");wagescategory = 24;}
                    else if(progress==4){mratetxt.setText("More than 500");wagescategory = 25;}
                }
                else if (mspinnerrate.getSelectedItemPosition() == 2){
                    if(progress==0){mratetxt.setText("Less than 1000");wagescategory = 31;}
                    else if(progress==1){mratetxt.setText("1000 to 1500");wagescategory = 32;}
                    else if(progress==2){mratetxt.setText("1500 to 2000");wagescategory = 33;}
                    else if(progress==3){mratetxt.setText("2000 to 5000");wagescategory = 34;}
                    else if(progress==4){mratetxt.setText("More than 5000");wagescategory = 35;}
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mspinnerrate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                if(position == 0){
                    int value = mpriceBar.getProgress();
                    if(value == 0){ mratetxt.setText("Less than 5"); wagescategory = 11; }
                    else if(value == 1){ mratetxt.setText("5 to 10"); wagescategory = 12; }
                    else if(value == 2){ mratetxt.setText("11 to 20"); wagescategory = 13; }
                    else if(value == 3){ mratetxt.setText("21 to 50"); wagescategory = 14; }
                    else if(value == 4){ mratetxt.setText("More than 50"); wagescategory = 15; }
                }
                else if(position == 1){
                    int value = mpriceBar.getProgress();
                    if(value == 0){ mratetxt.setText("Less than 70"); wagescategory = 21; }
                    else if(value == 1){ mratetxt.setText("70 to 100"); wagescategory = 22; }
                    else if(value == 2){ mratetxt.setText("101 to 200"); wagescategory = 23; }
                    else if(value == 3){ mratetxt.setText("201 to 500"); wagescategory = 24; }
                    else if(value == 4){ mratetxt.setText("More than 500"); wagescategory = 25; }
                }
                else if(position == 2){
                    int value = mpriceBar.getProgress();
                    if(value == 0){ mratetxt.setText("Less than 1000"); wagescategory = 31; }
                    else if(value == 1){ mratetxt.setText("1000 to 1500"); wagescategory = 32; }
                    else if(value == 2){ mratetxt.setText("1500 to 2000"); wagescategory = 33; }
                    else if(value == 3){ mratetxt.setText("2000 to 5000"); wagescategory = 34; }
                    else if(value == 4){ mratetxt.setText("More than 5000"); wagescategory = 35; }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        myCalendar = Calendar.getInstance();
        final String myFormat = "dd MMMM yy"; //In which you need put here
        final SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                mstartdatetxt.setText(sdf.format(myCalendar.getTime()));
                mstartingdate_tickimg.setVisibility(VISIBLE);
                mstartdatetxt.setTextColor(Color.parseColor("#008fee"));

                String myFormat2 = "yyMMdd"; //In which you need put here
                SimpleDateFormat sdf2 = new SimpleDateFormat(myFormat2, Locale.US);
                startingdatestring = sdf2.format(myCalendar.getTime());
            }

        };

        final DatePickerDialog.OnDateSetListener enddate = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                menddatetxt.setText(sdf.format(myCalendar.getTime()));
                mendingdate_tickimg.setVisibility(VISIBLE);
                menddatetxt.setTextColor(Color.parseColor("#008fee"));

                String myFormat2 = "yyMMdd"; //In which you need put here
                SimpleDateFormat sdf2 = new SimpleDateFormat(myFormat2, Locale.US);
                endingdatestring = sdf2.format(myCalendar.getTime());
            }

        };

        mfilterbystartdate_cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startdatePickerDialog = new DatePickerDialog(FilterJob.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                startdatePickerDialog.getDatePicker().setMinDate(myCalendar.getTimeInMillis());

                if(!filterbystart.equals("")){

                    SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd", Locale.US);

                    Date dateFromString = null;
                    try {
                        dateFromString = sdf.parse(filterbystart);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    SimpleDateFormat sdf1 = new SimpleDateFormat("ddMMyyyy", Locale.US);
                    String dateAsString = sdf1.format(dateFromString);

                    String year = dateAsString.substring(4, 8);
                    int startyear = Integer.parseInt(year);

                    String month = dateAsString.substring(2, 4);
                    int startmonth = Integer.parseInt(month) - 1;

                    String day = dateAsString.substring(0, 2);
                    int startday = Integer.parseInt(day);

                    startdatePickerDialog.updateDate(startyear,startmonth,startday);
                    startdatePickerDialog.getDatePicker().setMinDate(myCalendar.getTimeInMillis());
                }
                startdatePickerDialog.show();
            }
        });

        mfilterbyenddate_cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                enddatePickerDialog = new DatePickerDialog(FilterJob.this, enddate, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                enddatePickerDialog.getDatePicker().setMinDate(myCalendar.getTimeInMillis());

                if(!filterbyend.equals("")){
                    SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd", Locale.US);

                    Date dateFromString = null;
                    try {
                        dateFromString = sdf.parse(filterbyend);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    SimpleDateFormat sdf1 = new SimpleDateFormat("ddMMyyyy", Locale.US);
                    String dateAsString = sdf1.format(dateFromString);

                    String year = dateAsString.substring(4, 8);
                    int endyear = Integer.parseInt(year);

                    String month = dateAsString.substring(2, 4);
                    int endmonth = Integer.parseInt(month) - 1;

                    String day = dateAsString.substring(0, 2);
                    int endday = Integer.parseInt(day);

                    enddatePickerDialog.updateDate(endyear,endmonth,endday);
                    enddatePickerDialog.getDatePicker().setMinDate(myCalendar.getTimeInMillis());
                }
                enddatePickerDialog.show();
            }
        });

        mstartingdate_tickimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mstartdatetxt.setText("");
                mstartdatetxt.setHint("Start Date");
                mstartingdate_tickimg.setVisibility(GONE);
                mstartdatetxt.setTextColor(Color.BLACK);
                startingdatestring = "";
            }
        });

        mendingdate_tickimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menddatetxt.setText("");
                menddatetxt.setHint("End Date");
                mendingdate_tickimg.setVisibility(GONE);
                menddatetxt.setTextColor(Color.BLACK);
                endingdatestring = "";
            }
        });

        mapplyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String startdateval = mstartdatetxt.getText().toString();
                String enddateval = menddatetxt.getText().toString();
                String usercity = mpostBaseLocation.getText().toString();

                if (TextUtils.isEmpty(usercity)) {
                    Toast.makeText(FilterJob.this, "Location Cannot be Empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!startdateval.equals("")) {
                    if (!enddateval.equals("")) {

                        long startingdatelong = Long.valueOf(startingdatestring);
                        long endingdatelong = Long.valueOf(endingdatestring);

                        if (startingdatelong > endingdatelong) {
                            new AlertDialog.Builder(FilterJob.this)
                                    .setTitle("Invalid Start Date")
                                    .setMessage("Start Date has to be earlier than End Date")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    }).show();
                            return;
                        } else {
                            newSortFilter.child("StartDate").setValue(startingdatestring);
                            newSortFilter.child("EndDate").setValue(endingdatestring);
                        }
                    } else {
                        newSortFilter.child("StartDate").setValue(startingdatestring);
                        newSortFilter.child("EndDate").removeValue();
                    }
                } else {
                    if (!enddateval.equals("")) {
                        new AlertDialog.Builder(FilterJob.this)
                                .setTitle("Empty Start Date")
                                .setMessage("Please select a start date")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                }).show();
                        return;
                    } else {
                        newSortFilter.child("StartDate").removeValue();
                        newSortFilter.child("EndDate").removeValue();
                    }
                }

                if (oldfilterbywages.equals("true")) {
                    //No WagesFilter = OldWagesFilter

                    long spinnercurrency = (mspinnercurrency.getSelectedItemPosition() + 11) * 100;
                    final long WagesFilter = spinnercurrency + wagescategory;
                    newSortFilter.child("WagesFilter").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            newSortFilter.child("OldWagesFilter").setValue(WagesFilter);
                            mUserLocation.child(mAuth.getCurrentUser().getUid()).child("CurrentCity").setValue(mpostBaseLocation.getText().toString());
                            city = mpostBaseLocation.getText().toString();
                            mapplyPressed = true;
                            onBackPressed();

                        }
                    });}
                else if (oldfilterbywages.equals("false")) {
                    //Got WagesFilter

                    long spinnercurrency = (mspinnercurrency.getSelectedItemPosition() + 11) * 100;
                    long WagesFilter = spinnercurrency + wagescategory;
                    newSortFilter.child("WagesFilter").setValue(WagesFilter).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            newSortFilter.child("OldWagesFilter").removeValue();
                            mUserLocation.child(mAuth.getCurrentUser().getUid()).child("CurrentCity").setValue(mpostBaseLocation.getText().toString());
                            city = mpostBaseLocation.getText().toString();
                            mapplyPressed = true;
                            onBackPressed();
                        }
                    });
                }
            }
        });

        mclearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mratetxt.setText("Less Than 5");
                mpriceBar.setProgress(0);
                mspinnerrate.setSelection(0);
                mspinnercurrency.setSelection(0);

                mstartdatetxt.setText("");
                mstartdatetxt.setHint("Start Date");
                mstartingdate_tickimg.setVisibility(GONE);
                mstartdatetxt.setTextColor(Color.BLACK);

                menddatetxt.setText("");
                menddatetxt.setHint("End Date");
                mendingdate_tickimg.setVisibility(GONE);
                menddatetxt.setTextColor(Color.BLACK);

                mallrangetick.setImageResource(R.drawable.single_tick);
                mwagesrangetick.setImageResource(R.color.buttonTextColor);
                mblockLay.setVisibility(VISIBLE);

                oldfilterbywages = "true";
                wagescategory = 11;

                mapplyPressed = false;
            }
        });

        mlocationCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FilterJob.this, LocationList.class);
                intent.putExtra("locationstatus", "");
                intent.putExtra("travellocations", "");
                startActivityForResult(intent, 1111);
            }
        });

    }


    @Override
    public void onBackPressed() {

        if (mapplyPressed) {
            Bundle bundle = new Bundle();
            bundle.putString("city", city);
            Intent mIntent = new Intent();
            mIntent.putExtras(bundle);
            setResult(Activity.RESULT_OK,mIntent);
        }
        else {
            setResult(Activity.RESULT_CANCELED);
        }

        super.onBackPressed();
        overridePendingTransition(R.anim.nochange, R.anim.pulldown);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == 1111) && (resultCode == Activity.RESULT_OK)) {
            // recreate your fragment here

            String baselocation = data.getStringExtra("city");
            mpostBaseLocation.setText(baselocation);
        }
        else if ((requestCode == 1111) && (resultCode == Activity.RESULT_CANCELED)) {

        }
    }
}
