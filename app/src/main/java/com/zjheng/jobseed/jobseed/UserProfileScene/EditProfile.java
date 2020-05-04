package com.zjheng.jobseed.jobseed.UserProfileScene;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.zjheng.jobseed.jobseed.R;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.zjheng.jobseed.jobseed.UserProfileScene.OwnUserProfile.mchangeprofilepic;

public class EditProfile extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mUserInfo, mUserAccount;

    private EditText mnametxt, mabouttxt, magetxt, mweighttxt, mheighttxt;

    private Spinner mgenderspinner;

    private ImageButton mcleardatesBtn;

    private Calendar myCalendar;
    private String birthdates;

    private static final String TAG = "EditProfile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile2);

        mAuth = FirebaseAuth.getInstance();

        mUserInfo =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserInfo");

        mUserAccount = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserAccount");

        Toolbar mToolbar = (Toolbar)findViewById(R.id.toolbar_other);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Edit Profile");
        mToolbar.setTitleTextColor(android.graphics.Color.WHITE);

        mToolbar.setNavigationIcon(R.mipmap.ic_close_white_24dp);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mnametxt = (EditText)findViewById(R.id.nametxt);
        mabouttxt = (EditText)findViewById(R.id.abouttxt);
        magetxt = (EditText)findViewById(R.id.agetxt);
        mweighttxt = (EditText)findViewById(R.id.weighttxt);
        mheighttxt = (EditText)findViewById(R.id.heighttxt);
        mcleardatesBtn  = findViewById(R.id.cleardatesBtn);
        mgenderspinner = findViewById(R.id.genderspinner);

        String[] items = new String[]{"", "Male", "Female"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(EditProfile.this, android.R.layout.simple_spinner_dropdown_item, items);
        mgenderspinner.setAdapter(adapter);

        mnametxt.requestFocus();

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

                magetxt.setText(sdf.format(myCalendar.getTime()));
                birthdates = sdf.format(myCalendar.getTime());
            }

        };

        mcleardatesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                magetxt.setText("");
                birthdates = "";
            }
        });

        magetxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog birthdatePickerDialog = new DatePickerDialog(EditProfile.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));

                String age_val = magetxt.getText().toString().trim();

                Log.d(TAG, "age_val = " + age_val);

                if(!age_val.equals("")){

                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yy", Locale.US);

                    Date dateFromString = null;
                    try {
                        dateFromString = sdf.parse(age_val);
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

                    birthdatePickerDialog.updateDate(startyear,startmonth,startday);
                }

                birthdatePickerDialog.show();
            }
        });


        mUserInfo.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("Name")) {

                    String Name = (String) dataSnapshot.child("Name").getValue();
                    mnametxt.setText(Name);
                }
                else{
                    mUserAccount.child(mAuth.getCurrentUser().getUid()).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                String name = (String)dataSnapshot.getValue();
                                mnametxt.setText(name);
                            }
                            else {
                                mnametxt.setText("");
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

                if(dataSnapshot.hasChild("About")){

                    String about = dataSnapshot.child("About").getValue().toString();

                    mabouttxt.setText(about);
                }

                if(dataSnapshot.hasChild("Gender")){

                    String Gender = dataSnapshot.child("Gender").getValue().toString();
                    if (Gender.equals("Male")) {
                        mgenderspinner.setSelection(1);
                    }
                    else {
                        mgenderspinner.setSelection(2);
                    }
                }

                if(dataSnapshot.hasChild("Age")){

                    String Age = dataSnapshot.child("Age").getValue().toString();

                    magetxt.setText(Age);
                }

                if(dataSnapshot.hasChild("Weight")){

                    String Weight = dataSnapshot.child("Weight").getValue().toString();

                    mweighttxt.setText(Weight);
                }

                if(dataSnapshot.hasChild("Height")){

                    String Height = dataSnapshot.child("Height").getValue().toString();

                    mheighttxt.setText(Height);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public int getAge(int DOByear, int DOBmonth, int DOBday) {

        int age;

        final Calendar calenderToday = Calendar.getInstance();
        int currentYear = calenderToday.get(Calendar.YEAR);
        int currentMonth = 1 + calenderToday.get(Calendar.MONTH);
        int todayDay = calenderToday.get(Calendar.DAY_OF_MONTH);

        age = currentYear - DOByear;

        if(DOBmonth > currentMonth){
            --age;
        }
        else if(DOBmonth == currentMonth){
            if(DOBday > todayDay){
                --age;
            }
        }
        return age;
    }


    private void startPosting() {

        final ProgressDialog mProgress = new ProgressDialog(this);
        mProgress.setMessage("Uploading..");
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.setCancelable(false);
        mProgress.show();

        final String nametxt = mnametxt.getText().toString().trim();
        final String abouttxt = mabouttxt.getText().toString().trim();
        final String agetxt = magetxt.getText().toString().trim();
        final String weighttxt = mweighttxt.getText().toString().trim();
        final String heighttxt = mheighttxt.getText().toString().trim();
        final String genderval = mgenderspinner.getSelectedItem().toString().trim();

        final DatabaseReference newName = mUserInfo.child(mAuth.getCurrentUser().getUid()).child("Name");
        final DatabaseReference newAccount = mUserAccount.child(mAuth.getCurrentUser().getUid());
        final DatabaseReference newAbout = mUserInfo.child(mAuth.getCurrentUser().getUid()).child("About");
        final DatabaseReference newAge = mUserInfo.child(mAuth.getCurrentUser().getUid()).child("Age");
        final DatabaseReference newWeight = mUserInfo.child(mAuth.getCurrentUser().getUid()).child("Weight");
        final DatabaseReference newHeight = mUserInfo.child(mAuth.getCurrentUser().getUid()).child("Height");
        final DatabaseReference newGender = mUserInfo.child(mAuth.getCurrentUser().getUid()).child("Gender");

        if(TextUtils.isEmpty(nametxt)){
            mnametxt.requestFocus();
            mnametxt.setError("Name must not be empty");
            Toast.makeText(EditProfile.this, "Name must not be empty", Toast.LENGTH_LONG).show();
            mProgress.dismiss();
        }

        if(!TextUtils.isEmpty(nametxt)){

            Map< String, Object> nameData = new HashMap<>();
            nameData.put("name", nametxt);
            newAccount.updateChildren(nameData);

            newName.setValue(nametxt).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if(TextUtils.isEmpty(abouttxt)){
                        newAbout.removeValue();
                        UserProfileInfo.mAbouttxt.setText("");
                        UserProfileInfo.mAbouttxt.setHint("Add desciption about yourself");
                    }
                    else if(!TextUtils.isEmpty(abouttxt)){
                        newAbout.setValue(abouttxt);
                        UserProfileInfo.mAbouttxt.setText(abouttxt);
                    }

                    if(TextUtils.isEmpty(genderval)){
                        newGender.removeValue();
                        UserProfileInfo.mGender.setVisibility(GONE);
                    }
                    else if(!TextUtils.isEmpty(genderval)){
                        newGender.setValue(genderval);
                        UserProfileInfo.mGender.setVisibility(VISIBLE);
                        UserProfileInfo.mgendertxt.setText(genderval);
                    }


                    if(TextUtils.isEmpty(agetxt)){
                        newAge.removeValue();
                        UserProfileInfo.mAge.setVisibility(GONE);
                    }
                    else if(!TextUtils.isEmpty(agetxt)){
                        newAge.setValue(birthdates);
                        UserProfileInfo.mAge.setVisibility(VISIBLE);
                        UserProfileInfo.magetxt.setText(birthdates);
                    }


                    if(TextUtils.isEmpty(weighttxt)){
                        newWeight.removeValue();
                        UserProfileInfo.mWeight.setVisibility(GONE);
                    }
                    else if(!TextUtils.isEmpty(weighttxt)){
                        newWeight.setValue(weighttxt);
                        UserProfileInfo.mWeight.setVisibility(VISIBLE);
                        UserProfileInfo.mweighttxt.setText(weighttxt);
                    }


                    if(TextUtils.isEmpty(heighttxt)){
                        newHeight.removeValue();
                        UserProfileInfo.mHeight.setVisibility(GONE);
                    }
                    else if(!TextUtils.isEmpty(heighttxt)){
                        newHeight.setValue(heighttxt);
                        UserProfileInfo.mHeight.setVisibility(VISIBLE);
                        UserProfileInfo.mheighttxt.setText(heighttxt);
                    }

                    OwnUserProfile.muserNametxt.setText(nametxt);
                    mProgress.dismiss();
                    onBackPressed();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        MenuItem itemSave = menu.findItem(R.id.menuSave);

        MenuItem itemSettings = menu.findItem(R.id.menuSettings);
        itemSettings.setVisible(false);

        MenuItem itemSearch = menu.findItem(R.id.menuSearch);
        itemSearch.setVisible(false);

        MenuItem item = menu.findItem(R.id.menuSearch2);
        item.setVisible(false);

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



}
